package com.jsonengine.model;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import net.arnx.jsonic.JSON;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;
import com.jsonengine.service.CRUDRequest;
import com.jsonengine.service.JERequest;
import com.jsonengine.service.JEUtils;

/**
 * Represents an entity that stores a JSON document along with metadata like
 * index.
 * 
 * @author @kazunori_279
 */
@Model
public class JEDoc implements Serializable {

    /**
     * A registered property name for docId of each JSON document.
     */
    public static final String PROP_NAME_DOCID = "_docId";

    /**
     * A registered property name for updatedAt of each JSON document.
     */
    public static final String PROP_NAME_UPDATED_AT = "_updatedAt";

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance of JEDoc from specified {@link CRUDRequest}.
     * 
     * @param jeReq
     * @return {@link JEDoc} instance
     */
    public static JEDoc createJEDoc(JERequest jeReq) {
        final JEDoc jeDoc = new JEDoc();
        jeDoc
            .setKey(Datastore.createKey(JEDoc.class, JEUtils.i.generateUUID()));
        jeDoc.setCreatedAt(jeReq.getRequestedAt());
        jeDoc.setCreatedBy(jeReq.getRequestedBy());
        jeDoc.setDocType(jeReq.getDocType());
        return jeDoc;
    }

    // key name = docId
    @Attribute(primaryKey = true)
    private Key key;

    // JSON document type
    private String docType;

    // a Map that holds all the content of the JSON document
    @Attribute(lob = true)
    private Map<String, Object> docValues;

    // all property values will be added here automatically
    // "<docType>:<propName>:<propValue>" e.g. "foo:updatedAt:123"
    private Set<String> indexEntries;

    // User ID of the creator of this document
    private String createdBy;

    // User ID of the updater of this document
    private String updatedBy;

    // a timestamp of document creation
    private long createdAt;

    // a timestamp of document update
    private long updatedAt;

    /**
     * Encodes this document into a JSON document.
     * 
     * @return JSON document
     */
    public String encodeJSON() {
        return JSON.encode(getDocValues());
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getDocId() {
        return this.key.getName();
    }

    public Map<String, Object> getDocValues() {
        return docValues;
    }

    public void setDocValues(Map<String, Object> objValue) {
        this.docValues = objValue;
    }

    public Set<String> getIndexEntries() {
        return indexEntries;
    }

    public void setIndexEntries(Set<String> indexValues) {
        this.indexEntries = indexValues;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDocType(String className) {
        this.docType = className;
    }

    public String getDocType() {
        return docType;
    }

}
