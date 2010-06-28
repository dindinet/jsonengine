package com.jsonengine.service.query;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.arnx.jsonic.JSON;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;

import com.jsonengine.common.JEAccessDeniedException;
import com.jsonengine.common.JEConflictException;
import com.jsonengine.common.JENotFoundException;
import com.jsonengine.common.JETestUtils;
import com.jsonengine.service.query.QueryFilter;
import com.jsonengine.service.query.QueryRequest;
import com.jsonengine.service.query.QueryService;

/**
 * Tests query operations of {@link QueryService}.
 * 
 * @author kazunori_279
 */
public class QueryServiceTest extends AppEngineTestCase {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    @SuppressWarnings("unchecked")
    @Test
    public void queryAllUsers() throws JEConflictException,
            JENotFoundException, JEAccessDeniedException {

        // setup test users
        JETestUtils.i.storeTestUsers();

        // find all users
        final QueryRequest qr = JETestUtils.i.createTestQueryRequest();
        final String resultJson = QueryService.i.query(qr);

        // validate the result
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        assertEquals(4, resultMaps.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryFirstTwoUsers() throws JEConflictException,
            JENotFoundException, JEAccessDeniedException {

        // setup test users
        JETestUtils.i.storeTestUsers();

        // find all users with a limit
        final QueryRequest qr = JETestUtils.i.createTestQueryRequest();
        final QueryFilter qf = new QueryFilter.LimitFilter(qr.getDocType(), 2);
        qr.addQueryFilter(qf);
        final String resultJson = QueryService.i.query(qr);

        // validate the result size
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        assertEquals(2, resultMaps.size());
    }

    @Test
    public void queryWithEQ() throws JEConflictException, JENotFoundException,
            JEAccessDeniedException {

        // who's weight is equal to 123.45?
        final List<Map<String, Object>> resultMaps =
            queryOnAProp("weight", QueryFilter.Comparator.EQ, 123.45);

        // compare the result with Betty
        assertEquals(1, resultMaps.size());
        assertTrue(JETestUtils.i.areMapsEqual(
            JETestUtils.i.getDaniel(),
            resultMaps.get(0)));
    }

    @Test
    public void queryWithLT() throws JEConflictException, JENotFoundException,
            JEAccessDeniedException {

        // who's weight is less than 123.45?
        final List<Map<String, Object>> resultMaps =
            queryOnAProp("weight", QueryFilter.Comparator.LT, 123.45);

        // validate the result
        assertEquals(2, resultMaps.size());
        assertTrue(JETestUtils.i.areMapsEqual(
            JETestUtils.i.getAmanda(),
            resultMaps.get(0)));
        assertTrue(JETestUtils.i.areMapsEqual(
            JETestUtils.i.getMarc(),
            resultMaps.get(1)));
    }

    @Test
    public void queryWithLE() throws JEConflictException, JENotFoundException,
            JEAccessDeniedException {

        // who's weight is less than or equal to 123.45?
        final List<Map<String, Object>> resultMaps =
            queryOnAProp("weight", QueryFilter.Comparator.LE, 123.45);

        // validate the result
        assertEquals(3, resultMaps.size());
        assertTrue(JETestUtils.i.areMapsEqual(
            JETestUtils.i.getAmanda(),
            resultMaps.get(0)));
        assertTrue(JETestUtils.i.areMapsEqual(
            JETestUtils.i.getMarc(),
            resultMaps.get(1)));
        assertTrue(JETestUtils.i.areMapsEqual(
            JETestUtils.i.getDaniel(),
            resultMaps.get(2)));
    }

    @Test
    public void queryWithGT() throws JEConflictException, JENotFoundException,
            JEAccessDeniedException {

        // who's weight is greater than 123.45?
        final List<Map<String, Object>> resultMaps =
            queryOnAProp("weight", QueryFilter.Comparator.GT, 123.45);

        // validate the result
        assertEquals(1, resultMaps.size());
        assertTrue(JETestUtils.i.areMapsEqual(
            JETestUtils.i.getBetty(),
            resultMaps.get(0)));
    }

    @Test
    public void queryWithGE() throws JEConflictException, JENotFoundException,
            JEAccessDeniedException {

        // who's weight is greater than or equal to 123.45?
        final List<Map<String, Object>> resultMaps =
            queryOnAProp("weight", QueryFilter.Comparator.GE, 123.45);

        // validate the result
        assertEquals(2, resultMaps.size());
        assertTrue(JETestUtils.i.areMapsEqual(
            JETestUtils.i.getDaniel(),
            resultMaps.get(0)));
        assertTrue(JETestUtils.i.areMapsEqual(
            JETestUtils.i.getBetty(),
            resultMaps.get(1)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryWithGEAndLE() throws JEConflictException,
            JENotFoundException, JEAccessDeniedException {

        // setup test users
        JETestUtils.i.storeTestUsers();

        // build query filters
        final QueryRequest qr = JETestUtils.i.createTestQueryRequest();
        final QueryFilter condFilter =
            new QueryFilter.CondFilter(
                qr.getDocType(),
                "weight",
                QueryFilter.Comparator.GE,
                12.345);
        qr.addQueryFilter(condFilter);
        final QueryFilter condFilter2 =
            new QueryFilter.CondFilter(
                qr.getDocType(),
                "weight",
                QueryFilter.Comparator.LE,
                123.45);
        qr.addQueryFilter(condFilter2);

        // execute query
        final String resultJson = QueryService.i.query(qr);
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        log.info("Result: " + resultJson);

        // validate the result
        assertEquals(2, resultMaps.size());
        assertTrue(JETestUtils.i.areMapsEqual(
            JETestUtils.i.getMarc(),
            resultMaps.get(0)));
        assertTrue(JETestUtils.i.areMapsEqual(
            JETestUtils.i.getDaniel(),
            resultMaps.get(1)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryWith3EQs() throws JEConflictException,
            JENotFoundException, JEAccessDeniedException {

        // setup test users
        JETestUtils.i.storeTestUsers();

        // build query filters
        final QueryRequest qr = JETestUtils.i.createTestQueryRequest();
        final QueryFilter condFilter =
            new QueryFilter.CondFilter(
                qr.getDocType(),
                "weight",
                QueryFilter.Comparator.EQ,
                123.45);
        qr.addQueryFilter(condFilter);
        final QueryFilter condFilter2 =
            new QueryFilter.CondFilter(
                qr.getDocType(),
                "isMale",
                QueryFilter.Comparator.EQ,
                true);
        qr.addQueryFilter(condFilter2);
        final QueryFilter condFilter3 =
            new QueryFilter.CondFilter(
                qr.getDocType(),
                "id",
                QueryFilter.Comparator.EQ,
                "002");
        qr.addQueryFilter(condFilter3);

        // execute query
        final String resultJson = QueryService.i.query(qr);
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        log.info("Result: " + resultJson);

        // validate the result
        assertEquals(1, resultMaps.size());
        assertTrue(JETestUtils.i.areMapsEqual(
            JETestUtils.i.getDaniel(),
            resultMaps.get(0)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryWithSortAsc() throws JEConflictException,
            JENotFoundException, JEAccessDeniedException {

        // setup test users
        JETestUtils.i.storeTestUsers();

        // find all users
        final QueryRequest qr = JETestUtils.i.createTestQueryRequest();
        final QueryFilter sortFilter =
            new QueryFilter.SortFilter(
                qr.getDocType(),
                "id",
                QueryFilter.SortOrder.ASC);
        qr.addQueryFilter(sortFilter);

        // execute query
        final String resultJson = QueryService.i.query(qr);

        // validate the result
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        assertEquals(4, resultMaps.size());
        assertEquals("001", resultMaps.get(0).get("id"));
        assertEquals("002", resultMaps.get(1).get("id"));
        assertEquals("003", resultMaps.get(2).get("id"));
        assertEquals("004", resultMaps.get(3).get("id"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryWithSortDesc() throws JEConflictException,
            JENotFoundException, JEAccessDeniedException {

        // setup test users
        JETestUtils.i.storeTestUsers();

        // find all users
        final QueryRequest qr = JETestUtils.i.createTestQueryRequest();
        final QueryFilter sortFilter =
            new QueryFilter.SortFilter(
                qr.getDocType(),
                "weight",
                QueryFilter.SortOrder.DESC);
        qr.addQueryFilter(sortFilter);

        // execute query
        final String resultJson = QueryService.i.query(qr);

        // validate the result
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        assertEquals(4, resultMaps.size());
        assertEquals("001", resultMaps.get(0).get("id"));
        assertEquals("002", resultMaps.get(1).get("id"));
        assertEquals("003", resultMaps.get(2).get("id"));
        assertEquals("004", resultMaps.get(3).get("id"));
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> queryOnAProp(String propName,
            QueryFilter.Comparator cp, Object propValue)
            throws JEConflictException, JENotFoundException,
            JEAccessDeniedException {

        // setup test users
        JETestUtils.i.storeTestUsers();

        // build query filters
        final QueryRequest qr = JETestUtils.i.createTestQueryRequest();
        final QueryFilter condFilter =
            new QueryFilter.CondFilter(qr.getDocType(), propName, cp, propValue);
        qr.addQueryFilter(condFilter);
        final QueryFilter.SortOrder sortOrder =
            true ? QueryFilter.SortOrder.ASC : QueryFilter.SortOrder.DESC;
        final QueryFilter sortFilter =
            new QueryFilter.SortFilter(qr.getDocType(), propName, sortOrder);
        qr.addQueryFilter(sortFilter);

        // execute query
        final String resultJson = QueryService.i.query(qr);
        log.info("Result: " + resultJson);
        return (List<Map<String, Object>>) JSON.decode(resultJson);
    }
}
