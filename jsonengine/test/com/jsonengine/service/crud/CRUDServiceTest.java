package com.jsonengine.service.crud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import net.arnx.jsonic.JSON;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;

import com.jsonengine.common.JEAccessDeniedException;
import com.jsonengine.common.JEConflictException;
import com.jsonengine.common.JENotFoundException;
import com.jsonengine.common.JETestUtils;
import com.jsonengine.model.JEDoc;
import com.jsonengine.service.crud.CRUDRequest;
import com.jsonengine.service.crud.CRUDService;

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
    
}
