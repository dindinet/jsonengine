package com.jsonengine.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.jsonengine.common.JEAccessDeniedException;
import com.jsonengine.common.JEConflictException;
import com.jsonengine.common.JENotFoundException;
import com.jsonengine.common.JEUtils;
import com.jsonengine.service.crud.CRUDRequest;
import com.jsonengine.service.crud.CRUDService;

/**
 * Provides REST API for jsonengine CRUD operations.
 * 
 * @author @kazunori_279
 */
public class CRUDServlet extends HttpServlet {

    public static final String CHARSET = "UTF-8";

    public static final String PARAM_NAME_CHECK_UPDATES_AFTER =
        "_checkUpdatesAfter";

    public static final String PARAM_NAME_DELETE = "_delete";

    public static final String PARAM_NAME_DOC = "_doc";

    public static final String PARAM_NAME_DOCID = "_docId";

    public static final String RESP_CONTENT_TYPE =
        "application/json; charset=" + CHARSET;
    
    private static final long serialVersionUID = 1L;

    private static final UserService userService = UserServiceFactory.getUserService();

    private CRUDRequest createJERequest(HttpServletRequest req)
            throws UnsupportedEncodingException {

        // set charset for reading parameters
        req.setCharacterEncoding(CHARSET);

        // parse JSON doc
        final String jsonDocParam = req.getParameter(PARAM_NAME_DOC);
        final CRUDRequest jeReq;
        if (jsonDocParam != null) {
            // JSON style params
            jeReq = new CRUDRequest(jsonDocParam);
        } else {
            // FORM style params
            jeReq = new CRUDRequest(decodeFormStyleParams(req));
        }
         

        // parse URI and put docType and docId into jeReq
        final String[] tokens = req.getRequestURI().split("/");
        if (tokens.length < 3) {
            throw new IllegalArgumentException("No docType found");
        }
        if (tokens.length >= 3) {
            jeReq.setDocType(tokens[2]);
        }
        if (tokens.length >= 4) {
            jeReq.setDocId(tokens[3]);
        }

        // set Google account info
        if (req.getUserPrincipal() != null) {
            jeReq.setRequestedBy(req.getUserPrincipal().getName());
            jeReq.setAdmin(userService.isUserAdmin());
        }
        
        // set timestamp
        jeReq.setRequestedAt((new JEUtils()).getGlobalTimestamp());
        
        // set checkConflict flag
        try {
            jeReq.setCheckUpdatesAfter(Long.parseLong(req
                .getParameter(PARAM_NAME_CHECK_UPDATES_AFTER)));
        } catch (Exception e) {
            // NPE or NumberFormatException
        }

        return jeReq;
    }

    @SuppressWarnings("unchecked")
    private String decodeFormStyleParams(HttpServletRequest req) {

        // convert all the parameters into Map
        final Enumeration<String> paramNames =
            (Enumeration<String>) req.getParameterNames();
        final Map<String, Object> jsonMap = new HashMap<String, Object>();
        while (paramNames.hasMoreElements()) {
            final String paramName = paramNames.nextElement();
            final Object paramValue = decodeOneParam(req, paramName);
            jsonMap.put(paramName, paramValue);
        }

        // convert the Map into JSON
        return JSON.encode(jsonMap);
    }

    private Object decodeOneParam(HttpServletRequest req, String paramName) {
        final String[] paramValues = req.getParameterValues(paramName);
        final Object paramValue;
        if (paramValues.length == 1) {
            // if there's only one param value, use it
            paramValue = decodeOneParamValue(paramValues[0]);
        } else {
            // if there're multiple param values, put them into a List
            final List<Object> ls = new LinkedList<Object>();
            for (String s : paramValues) {
                ls.add(decodeOneParamValue(s));
            }
            paramValue = ls;
        }
        return paramValue;
    }

    private Object decodeOneParamValue(String valueStr) {

        // try to decode it as a Long
        try {
            return Long.parseLong(valueStr);
        } catch (NumberFormatException e) {
            // if failed, try next
        }

        // try to decode it as a Double
        try {
            return Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            // if failed, try next
        }

        // try to decode it as a Boolean
        if ("true".equals(valueStr)) {
            return Boolean.TRUE;
        } else if ("false".equals(valueStr)) {
            return Boolean.FALSE;
        }

        // use the value as is
        return valueStr;
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // do delete
        final CRUDRequest jeReq = createJERequest(req);
        try {
            (new CRUDService()).delete(jeReq);
        } catch (JENotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        } catch (JEConflictException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        } catch (JEAccessDeniedException e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // do get
        final CRUDRequest jeReq = createJERequest(req);
        final String resultJson;
        try {
            resultJson = (new CRUDService()).get(jeReq);
        } catch (JENotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        } catch (JEConflictException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        } catch (JEAccessDeniedException e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // return the result
        resp.setContentType(RESP_CONTENT_TYPE);
        final PrintWriter pw = resp.getWriter();
        pw.append(resultJson);
        pw.close();
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        throw new IllegalArgumentException("Operation not supported");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // if "delete" condParam is set true, doDelete. Otherwise, doPut
        if ("true".equals(req.getParameter(PARAM_NAME_DELETE))) {
            doDelete(req, resp);
        } else {
            doPut(req, resp);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // do put
        final CRUDRequest jeReq = createJERequest(req);
        final String resultJson;
        try {
            resultJson = (new CRUDService()).put(jeReq);
        } catch (JEConflictException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        } catch (JEAccessDeniedException e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // return the result
        resp.setContentType(RESP_CONTENT_TYPE);
        final PrintWriter pw = resp.getWriter();
        pw.append(resultJson);
        pw.close();
    }

}
