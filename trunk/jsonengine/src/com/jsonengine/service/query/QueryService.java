package com.jsonengine.service.query;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.arnx.jsonic.JSON;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelQuery;

import com.jsonengine.common.JEAccessDeniedException;
import com.jsonengine.meta.JEDocMeta;
import com.jsonengine.model.JEDoc;
import com.jsonengine.model.JEDocTypeInfo;
import com.jsonengine.service.doctype.DocTypeService;

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
     * @throws JEAccessDeniedException
     *             if the query is not allowed for this docType.
     */
    public String query(QueryRequest queryReq) throws JEAccessDeniedException {

        // check if accessible
        if (!queryReq.isAccessibleByQuery()) {
            throw new JEAccessDeniedException();
        }

        // check if this is a "private" query
        final DocTypeService docTypeService = new DocTypeService();
        final JEDocTypeInfo jdti =
            docTypeService.getDocTypeInfo(queryReq.getDocType());
        final boolean isPrivate =
            jdti != null
                && JEDocTypeInfo.ACCESS_LEVEL_PRIVATE.equals(jdti
                    .getAccessLevelForRead());

        // build query (add in-memory filtering with createdBy if it's a private query)
        final JEDocMeta jeDocMeta = JEDocMeta.get();
        ModelQuery<JEDoc> mq =
            queryReq.applyFiltersToModelQuery(Datastore.query(jeDocMeta));
        if (isPrivate) {
            mq.filterInMemory(jeDocMeta.createdBy.equal(queryReq.getRequestedBy()));
        }

        // execute query
        final List<JEDoc> resultJeDocs = mq.asList();

        // extract docValues from the result JEDocs
        Collection<Object> results = new LinkedList<Object>();
        for (JEDoc jeDoc : resultJeDocs) {
            results.add(jeDoc.getDocValues());
        }

        // apply post query filters
        results = queryReq.applyFiltersToResultList(results);

        // return the results in JSON
        return JSON.encode(results);
    }
}
