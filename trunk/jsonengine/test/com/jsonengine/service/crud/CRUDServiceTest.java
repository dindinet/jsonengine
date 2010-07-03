package com.jsonengine.service.crud;

import static org.junit.Assert.*;

import java.util.Map;

import net.arnx.jsonic.JSON;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;

import com.jsonengine.common.JEAccessDeniedException;
import com.jsonengine.common.JEConflictException;
import com.jsonengine.common.JENotFoundException;
import com.jsonengine.common.JETestUtils;
import com.jsonengine.model.JEDoc;

/**
 * Tests CRUD operations of {@link CRUDService}.
 * 
 * @author @kazunori_279
 */
// public class CRUDServiceTest extends TestCase {
public class CRUDServiceTest extends AppEngineTestCase {

    @SuppressWarnings("unchecked")
    @Test
    public void testCRUD() throws JEConflictException, JENotFoundException, JEAccessDeniedException {

        // save a test data
        JETestUtils.i.storeTestDocTypeInfo();
        final Map<String, Object> testMap = JETestUtils.i.createTestMap();
        final CRUDRequest jeReq = JETestUtils.i.createTestCRUDRequest(testMap);
        final String savedJson = CRUDService.i.put(jeReq);
        final String docId =
            (String) ((Map<String, Object>) JSON.decode(savedJson))
                .get(JEDoc.PROP_NAME_DOCID);

        // get the stored test data
        jeReq.setDocId(docId);
        final String resultJson = CRUDService.i.get(jeReq);
        System.out.println(resultJson);

        // verify it
        final Map<String, Object> resultMap =
            (Map<String, Object>) JSON.decode(resultJson);
        final Long updatedAt = JETestUtils.i.getUpdatedAtFromTestMap(resultMap);
        assertNotNull("_updatedAt must exists", updatedAt);
        assertEquals(docId, resultMap.remove(JEDoc.PROP_NAME_DOCID));
        assertTrue(JETestUtils.i.areMapsEqual(testMap, resultMap));

        // update the test data
        testMap.put("001", "foo2");
        testMap.put("004", "hoge");
        testMap.remove("002");
        final CRUDRequest jeReq2 = JETestUtils.i.createTestCRUDRequest(testMap);
        jeReq2.setCheckUpdatesAfter(updatedAt);
        jeReq2.setDocId(docId);
        CRUDService.i.put(jeReq2);
        final String resultJson2 = CRUDService.i.get(jeReq2);
        System.out.println(resultJson2);

        // verify it
        final Map<String, Object> resultMap2 =
            (Map<String, Object>) JSON.decode(resultJson2);
        assertNotNull("_updatedAt must exists", resultMap2
            .remove(JEDoc.PROP_NAME_UPDATED_AT));
        assertEquals(docId, resultMap2.remove(JEDoc.PROP_NAME_DOCID));
        assertTrue(JETestUtils.i.areMapsEqual(testMap, resultMap2));

        // try saving the old data again and check if the conflict detection is
        // working
        jeReq.setCheckUpdatesAfter(updatedAt);
        try {
            CRUDService.i.put(jeReq);
            fail("Should throw a JEConflictionException");
        } catch (JEConflictException e) {
            // OK
        }

        // try saving again without the conflict detection
        CRUDService.i.put(jeReq2);

        // try removing the data and check if the conflict detection is
        // working
        try {
            CRUDService.i.delete(jeReq);
            fail("Should throw a JEConflictionException");
        } catch (JEConflictException e) {
            // OK
        }

        // try removing the data again without the conflict detection
        CRUDService.i.delete(jeReq2);

        // make sure the data is removed
        try {
            CRUDService.i.get(jeReq2);
            fail("Should throw a JENotFoundException");
        } catch (JENotFoundException e) {
            // OK
        }
    }
    
//    @SuppressWarnings("unchecked")
    @Test
    public void testDeleteDocType() throws JEConflictException, JENotFoundException, JEAccessDeniedException {
        
        // save test data
        JETestUtils.i.storeTestDocTypeInfo();
        JETestUtils.i.storeTestUsers(JETestUtils.TEST_DOCTYPE);
        
        // delete all
        final CRUDRequest cr = JETestUtils.i.createTestCRUDRequest();
        CRUDService.i.delete(cr);
        
//        // wait a while
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        
//        // check there's no docs under the docType
//        final QueryRequest qr = JETestUtils.i.createTestQueryRequest(JETestUtils.TEST_DOCTYPE);
//        final String resultJson = QueryService.i.query(qr);
//
//        // validate the result
//        final List<Map<String, Object>> resultMaps =
//            (List<Map<String, Object>>) JSON.decode(resultJson);
//        assertEquals(0, resultMaps.size());
    }
    
}
