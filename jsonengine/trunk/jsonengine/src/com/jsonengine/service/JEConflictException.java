package com.jsonengine.service;

/**
 * Represents the service has detected a conflict of updates between clients.
 * 
 * @author @kazunori_279
 */
public class JEConflictException extends Exception {

    private static final long serialVersionUID = 1L;
    
    public JEConflictException(Throwable th) {
        super(th);
    }
    
    public JEConflictException(String msg) {
        super(msg);
    }
    

}
