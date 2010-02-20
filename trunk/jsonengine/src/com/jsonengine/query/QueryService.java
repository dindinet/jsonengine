package com.jsonengine.query;

import java.util.LinkedList;
import java.util.List;

import net.arnx.jsonic.JSON;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelQuery;

import com.jsonengine.meta.JEDocMeta;
import com.jsonengine.model.JEDoc;

/**
 * Provides query service for jsonengine.
 * 
 * @author @kazunori_279
 */
public class QueryService {

    /**
     * Singleton instance.
     */
    public static final QueryService i = new QueryService();

    private final JEDocMeta jeDocMeta = JEDocMeta.get();

    private QueryService() {
    }

    public String query(QueryRequest queryReq) {
        final List<JEDoc> resultJeDocs =
            queryReq.applyFilter(Datastore.query(jeDocMeta)).asList();
        final List<Object> results = new LinkedList<Object>();
        for (JEDoc jeDoc : resultJeDocs) {
            results.add(jeDoc.getDocValues());
        }
        return JSON.encode(results);
    }

}
