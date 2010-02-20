package com.jsonengine.common;

import java.util.HashMap;
import java.util.Map;

import net.arnx.jsonic.JSON;

import com.jsonengine.crud.CRUDRequest;
import com.jsonengine.crud.CRUDService;
import com.jsonengine.model.JEDoc;
import com.jsonengine.query.QueryRequest;

/**
 * Provides utility methods for Test cases.
 * 
 * @author @kazunori_279
 */
public class JETestUtils {

    private static final String TEST_USERNAME = "tester";
    private static final String TEST_DOCTYPE = "test";

    /**
     * Singleton instance.
     */
    public static final JETestUtils i = new JETestUtils();

    private JETestUtils() {
    }

    /**
     * Creates a Map with test properties.
     * 
     * @return a Map for testing.
     */
    public Map<String, String> createTestMap() {
        final Map<String, String> testData = new HashMap<String, String>();
        testData.put("name", "Foo");
        testData.put("age", "20");
        testData.put("email", "foo@example.com");
        testData.put("bigPropValue1", JEUtils.i.generateRandomAlnums(400));
        testData.put("bigPropValue2", JEUtils.i.generateRandomAlnums(400));
        testData.put("bigPropValue3", JEUtils.i.generateRandomAlnums(400));
        return testData;
    }

    public void storeTestUsers() throws JEConflictException {
        final Map<String, Object> user1 = new HashMap<String, Object>();
        user1.put("id", "001");
        user1.put("name", "Betty Suarez");
        user1.put("age", 25);
        user1.put("isMale", false);
        user1.put("weight", 1234.5);
        saveJsonMap(user1);

        final Map<String, Object> user2 = new HashMap<String, Object>();
        user2.put("id", "002");
        user2.put("name", "Daniel Meade");
        user2.put("age", 35);
        user2.put("isMale", true);
        user2.put("weight", 123.45);
        saveJsonMap(user2);
        
        final Map<String, Object> user3 = new HashMap<String, Object>();
        user3.put("id", "003");
        user3.put("name", "Marc St. James");
        user3.put("age", 30);
        user3.put("isMale", true);
        user3.put("weight", 12.345);
        saveJsonMap(user3);

        final Map<String, Object> user4 = new HashMap<String, Object>();
        user4.put("id", "004");
        user4.put("name", "Amanda Tannen Sommers");
        user4.put("age", 28);
        user4.put("isMale", false);
        user4.put("weight", 1.2345);
        saveJsonMap(user4);
    }

    @SuppressWarnings("unchecked")
    public String saveJsonMap(final Map<String, Object> map)
            throws JEConflictException {
        final CRUDRequest jeReq =
            JETestUtils.i.createTestCRUDRequest(JSON.encode(map));
        final String savedJson = CRUDService.i.put(jeReq);
        final String docId =
            (String) ((Map<String, Object>) JSON.decode(savedJson))
                .get(JEDoc.PROP_NAME_DOCID);
        return docId;
    }

    /**
     * Creates a test CRUDRequest with a specified JSON document.
     * 
     * @param json
     *            JSON document
     * @return {@link CRUDRequest} for testing.
     */
    public CRUDRequest createTestCRUDRequest(String json) {
        final CRUDRequest jeReq = new CRUDRequest(json);
        jeReq.setCheckConflict(true);
        jeReq.setDocType(TEST_DOCTYPE);
        jeReq.setRequestedAt(JEUtils.i.getGlobalTimestamp());
        jeReq.setRequestedBy(TEST_USERNAME);
        return jeReq;
    }

    /**
     * Creates a test QueryRequest with a specified JSON document.
     * 
     * @param json
     * @return {@link QueryRequest} for testing.
     */
    public QueryRequest createTestQueryRequest() {
        final QueryRequest jeReq = new QueryRequest();
        jeReq.setDocType(TEST_DOCTYPE);
        jeReq.setRequestedAt(JEUtils.i.getGlobalTimestamp());
        jeReq.setRequestedBy(TEST_USERNAME);
        return jeReq;
    }
}
