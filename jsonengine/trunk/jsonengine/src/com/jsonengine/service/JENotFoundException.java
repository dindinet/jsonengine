package com.jsonengine.service;

/**
 * Notifies that the specified JSON document has not found.
 * 
 * @author @kazunori_279
 */
public class JENotFoundException extends Exception {

    private static final long serialVersionUID = 1L;
    
    public JENotFoundException(Throwable th) {
        super(th);
    }
    
    public JENotFoundException(String msg) {
        super(msg);
    }   
}
