package com.jsonengine.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import net.arnx.jsonic.JSON;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;

import com.jsonengine.model.JEDoc;

/**
 * Tests CRUD operations of {@link CRUDService}.
 * 
 * @author @kazunori_279
 */
//public class CRUDServiceTest extends TestCase {
public class CRUDServiceTest extends AppEngineTestCase {

    private static final String TEST_USERNAME = "tester";
    private static final String TEST_DOCTYPE = "test";

    @SuppressWarnings("unchecked")
    @Test
    public void testCRUD() throws JEConflictException, JENotFoundException {

        // save a test data
        final Map<String, String> testMap = createTestMap();
        final CRUDRequest jeReq =
            createTestJERequestContext(JSON.encode(testMap));
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
        assertNotNull("_updatedAt must exists", resultMap
            .remove(JEDoc.PROP_NAME_UPDATED_AT));
        assertEquals(docId, resultMap.remove(JEDoc.PROP_NAME_DOCID));
        assertEquals(testMap, resultMap);

        // update the test data
        testMap.put("001", "foo2");
        testMap.put("004", "hoge");
        testMap.remove("002");
        final CRUDRequest jeReq2 =
            createTestJERequestContext(JSON.encode(testMap));
        jeReq2.setCheckConflict(false);
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
        assertEquals(testMap, resultMap2);

        // try saving the old data again and check if the conflict detection is
        // working
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

    private Map<String, String> createTestMap() {
        final Map<String, String> testData = new HashMap<String, String>();
        testData.put("001", "foo");
        testData.put("002", "bar");
        testData.put("003", "baz");
        testData.put("bigPropValue1", JEUtils.i.generateRandomAlnums(400));
        testData.put("bigPropValue2", JEUtils.i.generateRandomAlnums(400));
        testData.put("bigPropValue3", JEUtils.i.generateRandomAlnums(400));
        return testData;
    }

    private CRUDRequest createTestJERequestContext(String json) {
        final CRUDRequest jeReq = new CRUDRequest(json);
        jeReq.setCheckConflict(true);
        jeReq.setDocType(TEST_DOCTYPE);
        jeReq.setRequestedAt(JEUtils.i.getGlobalTimestamp());
        jeReq.setRequestedBy(TEST_USERNAME);
        return jeReq;
    }
}
