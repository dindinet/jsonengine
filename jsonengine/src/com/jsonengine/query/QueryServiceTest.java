package com.jsonengine.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void testQueryAll() throws JEConflictException, JENotFoundException {

        // save a test users
        JETestUtils.i.storeTestUsers();

        // get all users
        final QueryRequest qr = JETestUtils.i.createTestQueryRequest();
        final String resultJson = QueryService.i.query(qr);
        System.out.println(resultJson);

        // validate the result
        final List<Map<String, Object>> results =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        assertEquals(4, results.size());

        // get Betty by ID
        final QueryRequest qr2 = JETestUtils.i.createTestQueryRequest();
        final QueryFilter eqQf =
            new QueryFilter.CondFilter(
                qr2.getDocType(),
                "id",
                QueryFilter.Comparator.EQ,
                "001");
        qr2.addQueryFilter(eqQf);
        final String resultJson2 = QueryService.i.query(qr2);
        System.out.println(resultJson2);

        // validate the result
        final List<Map<String, Object>> results2 =
            (List<Map<String, Object>>) JSON.decode(resultJson2);
        assertEquals(1, results2.size());
        assertTrue(JETestUtils.i.compareMaps(JETestUtils.i.getBetty(), results2
            .get(0)));
    }
}
