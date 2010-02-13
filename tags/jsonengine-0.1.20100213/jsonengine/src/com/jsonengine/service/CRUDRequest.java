package com.jsonengine.service;

import java.math.BigDecimal;
import java.util.Map;

import com.jsonengine.model.JEDoc;

import net.arnx.jsonic.JSON;

/**
 * Holds various request parameters required for processing jsonengine CRUD
 * operations.
 * 
 * @author @kazunori_279
 */
public class CRUDRequest extends JERequest {

    // the original JSON document sent from a client
    private final String jsonDoc;

    // a Map which is decoded from the jsonDoc
    private final Map<String, Object> jsonMap;

    // JSON document ID
    private String docId;

    // a flag to indicate if confliction should be checked or not
    private boolean checkConflict;

    /**
     * Creates a CRUDRequest instance from specified JSON document.
     * 
     * @param jsonDoc
     */
    @SuppressWarnings("unchecked")
    public CRUDRequest(String jsonDoc) {
        this.jsonDoc = jsonDoc;
        if (jsonDoc != null) {
            // decode jsonDoc and fill it into jsonMap
            jsonMap = JSON.decode(jsonDoc, Map.class);
            setDocId((String) jsonMap.get(JEDoc.PROP_NAME_DOCID));
        } else {
            jsonMap = null;
        }
    }

    /**
     * Returns _updatedAt property value of this JSON document. Returns null if
     * it has not.
     * 
     * @return {@link BigDecimal} _updatedAt value.
     */
    public Long getUpdatedAt() {
        return (Long) jsonMap.get(JEDoc.PROP_NAME_UPDATED_AT);
    }

    public String getJsonDoc() {
        return jsonDoc;
    }

    public void setJsonDoc(String jsonDoc) {
        throw new IllegalStateException("Operation not supported");
    }

    public Map<String, Object> getJsonMap() {
        return jsonMap;
    }

    public void setJsonMap(Map<String, Object> jsonMap) {
        throw new IllegalStateException("Operation not supported");
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public void setCheckConflict(boolean checkConflict) {
        this.checkConflict = checkConflict;
    }

    public boolean isCheckConflict() {
        return checkConflict;
    }

}
