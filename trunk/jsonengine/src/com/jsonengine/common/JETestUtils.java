package com.jsonengine.common;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import com.jsonengine.crud.CRUDRequest;
import com.jsonengine.crud.CRUDService;
import com.jsonengine.model.JEDoc;
import com.jsonengine.query.QueryRequest;
import com.jsonengine.query.QueryService;

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
    public Map<String, Object> createTestMap() {
        final Map<String, Object> testData = new HashMap<String, Object>();
        testData.put("name", "Foo");
        testData.put("age", 20);
        testData.put("email", "foo@example.com");
        testData.put("bigPropValue1", JEUtils.i.generateRandomAlnums(400));
        testData.put("bigPropValue2", JEUtils.i.generateRandomAlnums(400));
        testData.put("bigPropValue3", JEUtils.i.generateRandomAlnums(400));
        return testData;
    }

    public void storeTestUsers() throws JEConflictException,
            JENotFoundException {

        removeAllUsers();

        final Map<String, Object> user1 = getBetty();
        saveJsonMap(user1);

        final Map<String, Object> user2 = getDaniel();
        saveJsonMap(user2);

        final Map<String, Object> user3 = getMarc();
        saveJsonMap(user3);

        final Map<String, Object> user4 = getAmanda();
        saveJsonMap(user4);
    }

    public Map<String, Object> getAmanda() {
        final Map<String, Object> user4 = new HashMap<String, Object>();
        user4.put("id", "004");
        user4.put("name", "Amanda Tannen Sommers");
        user4.put("age", 28);
        user4.put("isMale", false);
        user4.put("weight", 1.2345);
        return user4;
    }

    public Map<String, Object> getMarc() {
        final Map<String, Object> user3 = new HashMap<String, Object>();
        user3.put("id", "003");
        user3.put("name", "Marc St. James");
        user3.put("age", 30);
        user3.put("isMale", true);
        user3.put("weight", 12.345);
        return user3;
    }

    public Map<String, Object> getDaniel() {
        final Map<String, Object> user2 = new HashMap<String, Object>();
        user2.put("id", "002");
        user2.put("name", "Daniel Meade");
        user2.put("age", 35);
        user2.put("isMale", true);
        user2.put("weight", 123.45);
        return user2;
    }

    public Map<String, Object> getBetty() {
        final Map<String, Object> user1 = new HashMap<String, Object>();
        user1.put("id", "001");
        user1.put("name", "Betty Suarez");
        user1.put("age", 25);
        user1.put("isMale", false);
        user1.put("weight", 1234.5);
        return user1;
    }

    @SuppressWarnings("unchecked")
    public void removeAllUsers() throws JENotFoundException,
            JEConflictException {

        // get all users
        final QueryRequest qr = JETestUtils.i.createTestQueryRequest();
        final String resultJson = QueryService.i.query(qr);

        // remove them
        final CRUDRequest cr = createTestCRUDRequest();
        final List<Map<String, Object>> results =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        for (Map<String, Object> user : results) {
            cr.setDocId((String) user.get(JEDoc.PROP_NAME_DOCID));
            CRUDService.i.delete(cr);
        }
    }

    @SuppressWarnings("unchecked")
    public String saveJsonMap(final Map<String, Object> map)
            throws JEConflictException {
        final CRUDRequest jeReq = JETestUtils.i.createTestCRUDRequest(map);
        final String savedJson = CRUDService.i.put(jeReq);
        final String docId =
            (String) ((Map<String, Object>) JSON.decode(savedJson))
                .get(JEDoc.PROP_NAME_DOCID);
        return docId;
    }

    /**
     * Creates a test CRUDRequest with a specified JSON document.
     * 
     * @param testMap
     *            a Map to create a JSON document
     * @return {@link CRUDRequest} for testing.
     */
    public CRUDRequest createTestCRUDRequest(Map<String, Object> testMap) {
        final CRUDRequest jeReq = new CRUDRequest(JSON.encode(testMap));
        jeReq.setCheckUpdatesAfter((Long) testMap
            .get(JEDoc.PROP_NAME_UPDATED_AT));
        jeReq.setDocType(TEST_DOCTYPE);
        jeReq.setRequestedAt(JEUtils.i.getGlobalTimestamp());
        jeReq.setRequestedBy(TEST_USERNAME);
        return jeReq;
    }

    /**
     * Creates a test CRUDRequest without a JSON document.
     * 
     * @return {@link CRUDRequest} for testing.
     */
    public CRUDRequest createTestCRUDRequest() {
        final CRUDRequest jeReq = new CRUDRequest();
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

    /**
     * Compares entries of the specified two Maps.
     * 
     * @param _map1
     *            the first Map to be compared
     * @param _map2
     *            the second Map to be compared
     * @return true if two Maps has the same entries.
     */
    public boolean areMapsEqual(Map<String, Object> _map1,
            Map<String, Object> _map2) {

        final Map<String, Object> map1 = removeUnderscoredProperties(_map1);
        final Map<String, Object> map2 = removeUnderscoredProperties(_map2);

        for (String key : map1.keySet()) {
            if (!map2.containsKey(key)) {
                return false;
            }
            final Object obj1 = map1.get(key);
            final Object obj2 = map2.get(key);
            final boolean isEqual =
                (obj1 == null && obj2 == null)
                    || (obj1 != null && obj1.equals(obj2));
            final boolean isEqualInString =
                obj1 != null
                    && obj2 != null
                    && obj1.toString().equals(obj2.toString());
            if (!isEqual && !isEqualInString) {
                return false;
            }
        }
        return map1.size() == map2.size();
    }

    private Map<String, Object> removeUnderscoredProperties(
            Map<String, Object> origMap) {

        final Map<String, Object> newMap = new HashMap<String, Object>();
        for (String key : origMap.keySet()) {
            if (key.startsWith("_"))
                continue;
            newMap.put(key, origMap.get(key));
        }
        return newMap;
    }

    /**
     * Extract the updatedAt value from specified Map and return it as a Long
     * value.
     * 
     * @param testMap
     *            Map for testing which include updatedAt value.
     * @return Long value of updatedAt
     */
    public Long getUpdatedAtFromTestMap(Map<String, Object> testMap) {
        return ((BigDecimal) testMap.get(JEDoc.PROP_NAME_UPDATED_AT))
            .longValue();
    }

}
