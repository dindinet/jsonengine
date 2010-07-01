package com.jsonengine.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.jsonengine.common.JEAccessDeniedException;
import com.jsonengine.common.JEUtils;
import com.jsonengine.service.query.QueryFilter;
import com.jsonengine.service.query.QueryRequest;
import com.jsonengine.service.query.QueryService;

/**
 * Provides REST API for jsonengine query operations.
 * 
 * @author @kazunori_279
 */
public class QueryServlet extends HttpServlet {

    public static final String PARAM_COND = "cond";

    public static final String PARAM_LIMIT = "limit";

    public static final String PARAM_SORT = "sort";

    private static final long serialVersionUID = 1L;

    private static final UserService userService =
        UserServiceFactory.getUserService();

    private static final Pattern condPattern =
        Pattern.compile("^([^\\.]*)\\.(eq|gt|ge|lt|le)\\.(.*)$");

    private static final Pattern quotePattern = Pattern.compile("^\"(.*)\"$");

    private QueryRequest createQueryRequest(HttpServletRequest req)
            throws UnsupportedEncodingException {

        // set charset for reading parameters
        req.setCharacterEncoding(CRUDServlet.CHARSET);

        // parse URI and put docType and docId into jeReq
        final QueryRequest qReq = new QueryRequest();
        final String[] tokens = req.getRequestURI().split("/");
        if (tokens.length < 3) {
            throw new IllegalArgumentException("No docType found");
        }
        if (tokens.length >= 3) {
            qReq.setDocType(tokens[2]);
        }

        // set Google account info, timestamp, and checkConflict flag
        if (req.getUserPrincipal() != null) {
            qReq.setRequestedBy(req.getUserPrincipal().getName());
            qReq.setAdmin(userService.isUserAdmin());
        }
        qReq.setRequestedAt(JEUtils.i.getGlobalTimestamp());
        return qReq;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // add QueryFilters for "cond" parameter
        final QueryRequest qReq = createQueryRequest(req);
        final String[] conds = req.getParameterValues(PARAM_COND);
        if (conds != null) {
            for (String cond : conds) {
                parseCondFilter(qReq, cond);
            }
        }

        // add QueryFilters for "sort"
        final String sortParam = req.getParameter(PARAM_SORT);
        if (sortParam != null) {
            parseSortFilter(qReq, sortParam);
        }

        // add QueryFilters for "limit"
        final String limitParam = req.getParameter(PARAM_LIMIT);
        if (limitParam != null) {
            parseLimitFilter(qReq, limitParam);
        }

        // execute the query
        final String resultJson;
        try {
            resultJson = QueryService.i.query(qReq);
        } catch (JEAccessDeniedException e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // return the result
        resp.setContentType(CRUDServlet.RESP_CONTENT_TYPE);
        final PrintWriter pw = resp.getWriter();
        pw.append(resultJson);
        pw.close();
    }

    private void parseCondFilter(final QueryRequest qReq, final String cond) {

        // try to parse the cond params
        final Matcher m = condPattern.matcher(cond);
        if (!m.find()) {
            throw new IllegalArgumentException("Illegal condFilter: " + cond);
        }
        final String propName = m.group(1);
        final QueryFilter.Comparator cp =
            QueryFilter.parseComparator(m.group(2));
        final String propValue = m.group(3);

        // try to convert propValue
        final Object propValueObj = convertPropValue(propValue);

        // create confFilter
        final QueryFilter condFilter =
            new QueryFilter.CondFilter(
                qReq.getDocType(),
                propName,
                cp,
                propValueObj);
        qReq.addQueryFilter(condFilter);
    }

    private Object convertPropValue(final String propValue) {

        // if it's quoted, treat it as a String
        final Matcher m = quotePattern.matcher(propValue);
        if (m.find()) {
            return m.group(1);
        }

        // if it's not quoted, try to parse it as a BigDecimal
        try {
            return new BigDecimal(propValue);
        } catch (NumberFormatException e) {
            // failed
        }

        // try to parse as a Boolean
        if ("true".equals(propValue)) {
            return true;
        } else if ("false".equals(propValue)) {
            return false;
        }

        // otherwise, treat it as a String
        return propValue;
    }

    private void parseLimitFilter(final QueryRequest qReq,
            final String limitParam) {
        final int limit = Integer.parseInt(limitParam);
        final QueryFilter limitFilter =
            new QueryFilter.LimitFilter(qReq.getDocType(), limit);
        qReq.addQueryFilter(limitFilter);
    }

    private void parseSortFilter(final QueryRequest qReq, final String sortParam) {
        final String[] sortTokens = sortParam.split("\\.");
        final String propName = sortTokens[0];
        final QueryFilter.SortOrder so =
            QueryFilter.parseSortOrder(sortTokens[1]);
        final QueryFilter sortFilter =
            new QueryFilter.SortFilter(qReq.getDocType(), propName, so);
        qReq.addQueryFilter(sortFilter);
    }
}
