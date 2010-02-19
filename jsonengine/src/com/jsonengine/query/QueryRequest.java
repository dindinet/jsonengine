package com.jsonengine.query;

import java.util.HashSet;
import java.util.Set;

import org.slim3.datastore.ModelQuery;

import com.jsonengine.common.JERequest;

/**
 * Holds various request parameters required for processing jsonengine query
 * operations.
 * 
 * @author @kazunori_279
 */
public class QueryRequest<T> extends JERequest {
    
    private final Set<QueryFilter<T>> queryFilters = new HashSet<QueryFilter<T>>();

    public void addQueryFilter(QueryFilter<T> qf) {
        queryFilters.add(qf);
    }
    
    public ModelQuery<T> applyFilter(ModelQuery<T> mq) {
        ModelQuery<T> curMq = mq;
        for (QueryFilter<T> qf : queryFilters) {
            curMq = qf.applyFilter(curMq);
        }
        return curMq;
    }
}
