package com.jsonengine.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;

import com.jsonengine.common.JEConflictException;
import com.jsonengine.common.JENotFoundException;
import com.jsonengine.common.JETestUtils;

/**
 * Tests query operations of {@link QueryService}.
 * 
 * @author kazunori_279
 */
public class QueryServiceTest extends AppEngineTestCase {

    @SuppressWarnings("unchecked")
    @Test
    public void queryAllUsers() throws JEConflictException, JENotFoundException {

        // setup test users
        JETestUtils.i.storeTestUsers();

        // find all users
        final QueryRequest qr = JETestUtils.i.createTestQueryRequest();
        final String resultJson = QueryService.i.query(qr);
        System.out.println(resultJson);

        // validate the result
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        assertEquals(4, resultMaps.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryWithEQ() throws JEConflictException, JENotFoundException {

        // setup test users
        JETestUtils.i.storeTestUsers();

        // get Betty by ID
        final String resultJson =
            findTestUsersByCondition("id", QueryFilter.Comparator.EQ, "001");
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);

        // compare the result with Betty
        assertEquals(1, resultMaps.size());
        assertTrue(JETestUtils.i.compareMaps(
            JETestUtils.i.getBetty(),
            resultMaps.get(0)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryWithLT() throws JEConflictException, JENotFoundException {

        // setup test users
        JETestUtils.i.storeTestUsers();

        // get Betty by ID
        final String resultJson =
            findTestUsersByCondition("weight", QueryFilter.Comparator.LT, BigDecimal.valueOf(100.0));
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);

        // TODO 
    }
    
    
    private String findTestUsersByCondition(String propName,
            QueryFilter.Comparator cp, Object propValue) {
        final QueryRequest qr = JETestUtils.i.createTestQueryRequest();
        final QueryFilter eqQf =
            new QueryFilter.CondFilter(qr.getDocType(), propName, cp, propValue);
        qr.addQueryFilter(eqQf);
        final String resultJson = QueryService.i.query(qr);
        System.out.println(resultJson);
        return resultJson;
    }
}
