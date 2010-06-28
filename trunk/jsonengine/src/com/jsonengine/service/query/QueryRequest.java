package com.jsonengine.service.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slim3.datastore.ModelQuery;

import com.jsonengine.common.JERequest;
import com.jsonengine.model.JEDoc;
import com.jsonengine.service.query.QueryFilter.Comparator;

/**
 * Holds various request parameters required for processing jsonengine query
 * operations.
 * 
 * @author @kazunori_279
 */
public class QueryRequest extends JERequest {

    boolean hasGtOrGe = false;

    boolean hasLtOrLe = false;

    private final Set<QueryFilter> queryFilters = new HashSet<QueryFilter>();

    /**
     * Adds a QueryFilter to this QueryRequest.
     * 
     * @param qf
     *            {@link QueryFilter} to be added.
     */
    public void addQueryFilter(QueryFilter qf) {
        if (qf instanceof QueryFilter.CondFilter) {
            checkCondDuplication(qf);
        }
        queryFilters.add(qf);
    }

    /**
     * Applies all the filters of this QueryRequest to the specified
     * {@link ModelQuery}.
     * 
     * @param mq
     *            a {@link ModelQuery} for this query.
     * @return a {@link ModelQuery} with all the filters applied.
     */
    public ModelQuery<JEDoc> applyFiltersToModelQuery(ModelQuery<JEDoc> mq) {
        ModelQuery<JEDoc> curMq = mq;
        final boolean isSingleCond = hasGtOrGe != hasLtOrLe;
        for (QueryFilter qf : queryFilters) {
            curMq = qf.applyFilterToModelQuery(curMq, isSingleCond);
        }
        return curMq;
    }

    /**
     * Applies all the filters of this QueryRequest to the specified results.
     * 
     * @param results
     *            a {@link Collection} of results of this query.
     * @return a {@link Collection} with all the filters applied.
     */
    public Collection<Object> applyFiltersToResultList(
            Collection<Object> results) {
        for (QueryFilter qf : queryFilters) {
            results = qf.applyFilterToResultList(results);
        }
        return results;
    }

    private void checkCondDuplication(QueryFilter qf) {
        final QueryFilter.CondFilter cf = (QueryFilter.CondFilter) qf;
        if ((cf.comparator == Comparator.GT || cf.comparator == Comparator.GE)) {
            if (hasGtOrGe) {
                throw new IllegalStateException(
                    "Can not add high pass cond more than one: " + qf);
            } else {
                hasGtOrGe = true;
            }
        }
        if ((cf.comparator == Comparator.LT || cf.comparator == Comparator.LE)) {
            if (hasLtOrLe) {
                throw new IllegalStateException(
                    "Can not add low pass cond more than one: " + qf);
            } else {
                hasLtOrLe = true;
            }
        }
    }
}
