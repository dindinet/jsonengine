package com.jsonengine.query;

import java.util.HashSet;
import java.util.Set;

import org.slim3.datastore.ModelQuery;

import com.jsonengine.common.JERequest;
import com.jsonengine.model.JEDoc;

/**
 * Holds various request parameters required for processing jsonengine query
 * operations.
 * 
 * @author @kazunori_279
 */
public class QueryRequest extends JERequest {

    private final Set<QueryFilter> queryFilters =
        new HashSet<QueryFilter>();

    public void addQueryFilter(QueryFilter qf) {
        queryFilters.add(qf);
    }

    public ModelQuery<JEDoc> applyFilter(ModelQuery<JEDoc> mq) {
        ModelQuery<JEDoc> curMq = mq;
        for (QueryFilter qf : queryFilters) {
            curMq = qf.applyFilter(curMq);
        }
        return curMq;
    }
}
