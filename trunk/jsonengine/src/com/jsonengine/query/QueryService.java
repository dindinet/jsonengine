package com.jsonengine.query;

import java.util.LinkedList;
import java.util.List;

import net.arnx.jsonic.JSON;

import org.slim3.datastore.Datastore;

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

    private QueryService() {
    }

    /**
     * Executes query for the specified {@link QueryRequest}.
     * 
     * @param queryReq
     *            {@link QueryRequest} which includes the filters for the query.
     * @return JSON document of the results.
     */
    public String query(QueryRequest queryReq) {

        // execute query
        final JEDocMeta jeDocMeta = JEDocMeta.get();
        final List<JEDoc> resultJeDocs =
            queryReq.applyFilter(Datastore.query(jeDocMeta), jeDocMeta).asList();

        // extract docValues from the result JEDocs
        final List<Object> resultValues = new LinkedList<Object>();
        for (JEDoc jeDoc : resultJeDocs) {
            resultValues.add(jeDoc.getDocValues());
        }

        // return the results by JSON
        return JSON.encode(resultValues);
    }
}
