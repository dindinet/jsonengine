package com.jsonengine.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.util.StringUtil;

import com.jsonengine.common.JEAccessDeniedException;
import com.jsonengine.common.JEConflictException;
import com.jsonengine.common.JENotFoundException;
import com.jsonengine.common.JEUserUtils;
import com.jsonengine.common.JEUtils;
import com.jsonengine.service.crud.CRUDRequest;
import com.jsonengine.service.crud.CRUDService;

public class CRUDController extends Controller {

    private static final Logger logger =
        Logger.getLogger(CRUDController.class.getName());

    @Override
    public Navigation run() throws Exception {
        logger.info("Call CRUDController#run");

        if (isPost() || isPut()) {
            // if "delete" condParam is set true, doDelete. Otherwise, doPut
            if ("true".equals(asString(PARAM_NAME_DELETE))) {
                doDelete(request, response);
            } else {
                doPut(request, response);
            }
        } else if (isGet()) {
            doGet(request, response);
        } else if (isDelete()) {
            doDelete(request, response);
        }

        return null;
    }

    public static final String PARAM_NAME_CHECK_UPDATES_AFTER =
        "_checkUpdatesAfter";

    public static final String PARAM_NAME_DELETE = "_delete";

    public static final String PARAM_NAME_DOC = "_doc";

    public static final String PARAM_NAME_DOCID = "_docId";

    public static final String RESP_CONTENT_TYPE =
        "application/json; charset=UTF-8";

    private CRUDRequest createJERequest(HttpServletRequest req)
            throws UnsupportedEncodingException {

        // parse JSON doc
        final String jsonDocParam = asString(PARAM_NAME_DOC);
        final CRUDRequest jeReq;
        if (jsonDocParam != null) {
            // JSON style params
            jeReq = new CRUDRequest(jsonDocParam);
        } else {
            // FORM style params
            jeReq = new CRUDRequest(decodeFormStyleParams(req));
        }

        // parse URI and put docType and docId into jeReq
        String docType = asString("docType");
        if (StringUtil.isEmpty(docType)) {
            throw new IllegalArgumentException("No docType found");
        }
        jeReq.setDocType(docType);
        String docId = asString("docId");
        if (!StringUtil.isEmpty(docId)) {
            jeReq.setDocId(docId);
        }

        // set Google account info
        if (JEUserUtils.isLoggedIn()) {
            jeReq.setRequestedBy(JEUserUtils.userEmail());
            jeReq.setAdmin(JEUserUtils.isAdmin());
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
        final Enumeration<String> paramNames = req.getParameterNames();
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

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException{

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

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

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

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {

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