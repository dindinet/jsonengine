package com.jsonengine.service;

/**
 * Holds various request parameters required for processing jsonengine
 * operations.
 * 
 * @author @kazunori_279
 */
public abstract class JERequest {

    private String requestedBy;
    
    private long requestedAt;
    
    private String docType;

    public JERequest() {
        super();
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public long getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(long requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docName) {
        this.docType = docName;
    }

}