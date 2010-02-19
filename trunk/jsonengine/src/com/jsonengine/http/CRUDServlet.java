package com.jsonengine.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jsonengine.common.JEConflictException;
import com.jsonengine.common.JENotFoundException;
import com.jsonengine.common.JEUtils;
import com.jsonengine.crud.CRUDRequest;
import com.jsonengine.crud.CRUDService;

/**
 * Provides REST API for jsonengine CRUD operations.
 * 
 * @author @kazunori_279
 */
public class CRUDServlet extends HttpServlet {

    public static final String PARAM_NAME_DOC = "doc";

    public static final String PARAM_NAME_CHECK_CONFLICT = "cc";

    public static final String PARAM_NAME_DELETE = "delete";

    private static final long serialVersionUID = 1L;

    private static final String CHARSET = "UTF-8";

    private static final String RESP_CONTENT_TYPE =
        "application/json; charset=" + CHARSET;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // do get
        final CRUDRequest jeReq = createJERequest(req);
        final String resultJson;
        try {
            resultJson = CRUDService.i.get(jeReq);
        } catch (JENotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        } catch (JEConflictException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        }

        // return the result
        resp.setContentType(RESP_CONTENT_TYPE);
        final PrintWriter pw = resp.getWriter();
        pw.append(resultJson);
        pw.close();
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
            resultJson = CRUDService.i.put(jeReq);
        } catch (JEConflictException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
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
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // do delete
        final CRUDRequest jeReq = createJERequest(req);
        try {
            CRUDService.i.delete(jeReq);
        } catch (JENotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        } catch (JEConflictException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        }
    }

    private CRUDRequest createJERequest(HttpServletRequest req)
            throws UnsupportedEncodingException {

        // set charset for reading parameters
        req.setCharacterEncoding(CHARSET);

        // parse URI
        final String[] tokens = req.getRequestURI().split("/");
        if (tokens.length < 3) {
            throw new IllegalArgumentException("No docType found");
        }

        // put docType and docId into jeReq
        final CRUDRequest jeReq = new CRUDRequest(req.getParameter(PARAM_NAME_DOC));
        if (tokens.length >= 3) {
            jeReq.setDocType(tokens[2]);
        }
        if (tokens.length >= 4) {
            jeReq.setDocId(tokens[3]);
        }

        // set Google account info, timestamp, and checkConflict flag
        if (req.getUserPrincipal() != null) {
            jeReq.setRequestedBy(req.getUserPrincipal().getName());
        }
        jeReq.setRequestedAt(JEUtils.i.getGlobalTimestamp());
        jeReq.setCheckConflict("true".equals(req
            .getParameter(PARAM_NAME_CHECK_CONFLICT)));

        return jeReq;
    }

}
