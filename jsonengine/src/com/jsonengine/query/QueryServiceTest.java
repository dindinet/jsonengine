package com.jsonengine.query;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;

import com.jsonengine.common.JEConflictException;
import com.jsonengine.common.JETestUtils;

/**
 * Tests query operations of {@link QueryService}.
 * 
 * @author kazunori_279
 */
public class QueryServiceTest extends AppEngineTestCase {

    @Test
    public void testQuery() throws JEConflictException {

        // save a test users
        JETestUtils.i.storeTestUsers();
        
        // get all users
        final QueryRequest qr = JETestUtils.i.createTestQueryRequest();
        final String resultJson = QueryService.i.query(qr);
        System.out.println(resultJson);
    }
}
