package com.jsonengine.service.query;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slim3.datastore.ModelQuery;
import org.slim3.datastore.StringCollectionAttributeMeta;

import com.jsonengine.common.JEUtils;
import com.jsonengine.meta.JEDocMeta;
import com.jsonengine.model.JEDoc;

/**
 * Represents a filter (condFilter, sortFilter and limitFilter) for a query.
 * 
 * @author @kazunori_279
 */
public abstract class QueryFilter {

    /**
     * Comparators that can be specified with a condFilter.
     */
    public enum Comparator {
        EQ, GE, GT, LE, LT
    };

    /**
     * Represents a confFilter.
     * 
     * @author @kazunori_279
     */
    public static class CondFilter extends QueryFilter {

        // the comparator
        protected final Comparator comparator;

        // the upper limit value for the property
        private final String condMax;

        // the lower limit value for the property
        private final String condMin;

        // user specified value for the filtering
        protected final String condParam;

        /**
         * Creates a confFilter.
         * 
         * @param docType
         *            docType of this filter
         * @param propName
         *            property name for the filtering
         * @param comparator
         *            comparator for the filtering
         * @param propValue
         *            property value for the filtering
         */
        public CondFilter(String docType, String propName,
                Comparator comparator, Object propValue) {
            super(docType);
            this.comparator = comparator;
            final String condPrefix = docType + ":" + propName + ":";
            this.condParam = condPrefix + JEUtils.i.encodePropValue(propValue);
            this.condMin = condPrefix;
            this.condMax = condPrefix + "\uffff";
        }

        @Override
        public ModelQuery<JEDoc> applyFilterToModelQuery(ModelQuery<JEDoc> mq,
                boolean isSingleCond) {
            final JEDocMeta jeDocMeta = JEDocMeta.get();
            final StringCollectionAttributeMeta<JEDoc, Set<String>> ie =
                jeDocMeta.indexEntries;
            switch (comparator) {
            case LT:
                mq = mq.filter(ie.lessThan(condParam));
                if (isSingleCond) {
                    mq = mq.filter(ie.greaterThan(condMin));
                }
                return mq;
            case LE:
                mq = mq.filter(ie.lessThanOrEqual(condParam));
                if (isSingleCond) {
                    mq = mq.filter(ie.greaterThan(condMin));
                }
                return mq;
            case GT:
                mq = mq.filter(ie.greaterThan(condParam));
                if (isSingleCond) {
                    mq = mq.filter(ie.lessThan(condMax));
                }
                return mq;
            case GE:
                mq = mq.filter(ie.greaterThanOrEqual(condParam));
                if (isSingleCond) {
                    mq = mq.filter(ie.lessThan(condMax));
                }
                return mq;
            case EQ:
                return mq.filter(ie.equal(condParam));
            }
            throw new IllegalArgumentException();
        }

        @Override
        public String toString() {
            return "CondFilter(docType="
                + docType
                + ", cp="
                + comparator
                + ", condParam="
                + condParam
                + ", condMin="
                + condMin
                + ", condMax="
                + condMax
                + ") ";
        }
    };

    /**
     * Represents a limitFilter.
     * 
     * @author @kazunori_279
     */
    public static class LimitFilter extends QueryFilter {

        private final int limitCount;

        /**
         * Creates a limitFilter.
         * 
         * @param docType
         *            docType of this filter.
         * @param limitCount
         *            maximum number of resuls for this limit filter.
         */
        public LimitFilter(String docType, int limitCount) {
            super(docType);
            this.limitCount = limitCount;
        }

        @Override
        public ModelQuery<JEDoc> applyFilterToModelQuery(ModelQuery<JEDoc> mq,
                boolean isSingleCond) {
            return mq.limit(limitCount);
        }

        @Override
        public String toString() {
            return "LimitFilter(docType="
                + docType
                + ", limitCount="
                + limitCount
                + ")";
        }
    }

    /**
     * Represents a sortFilter.
     * 
     * @author @kazunori_279
     */
    public static class SortFilter extends QueryFilter {

        // the property name to be sorted
        private final String propName;

        // sort order
        private final SortOrder sortOrder;

        /**
         * Creates a sortFilter.
         * 
         * @param docType
         *            docType of this filter.
         * @param propName
         *            property name for the sorting.
         * @param sortOrder
         *            sort order for the sorting.
         */
        public SortFilter(String docType, String propName, SortOrder sortOrder) {
            super(docType);
            this.propName = propName;
            this.sortOrder = sortOrder;
        }

        @Override
        public ModelQuery<JEDoc> applyFilterToModelQuery(ModelQuery<JEDoc> mq,
                boolean isSingleCond) {
            return mq; // do nothing
        }

        @Override
        public Collection<Object> applyFilterToResultList(
                Collection<Object> resultList) {

            // build a Comparator for in-memory sorting
            final java.util.Comparator<Object> cp =
                new java.util.Comparator<Object>() {
                    @SuppressWarnings("unchecked")
                    public int compare(Object o1, Object o2) {
                        final Object v1 =
                            ((Map<String, Object>) o1).get(propName);
                        final Object v2 =
                            ((Map<String, Object>) o2).get(propName);
                        if (sortOrder == SortOrder.ASC) {
                            return ((Comparable<Object>) v1).compareTo(v2);
                        } else {
                            return ((Comparable<Object>) v2).compareTo(v1);
                        }
                    }
                };

            // sort the results
            final SortedSet<Object> sortedResults = new TreeSet<Object>(cp);
            sortedResults.addAll(resultList);
            return sortedResults;
        }

        @Override
        public String toString() {
            return "SoftFilter(docType="
                + docType
                + ", protName="
                + propName
                + ", sortOrder="
                + sortOrder
                + ")";
        }
    }

    /**
     * Sort order that can be specified with a sortFilter.
     */
    public enum SortOrder {
        ASC, DESC
    }

    /**
     * parses a token String and convert it to a Comparator.
     * 
     * @param token
     * @return a Comparator
     */
    public static Comparator parseComparator(String token) {
        if ("lt".equals(token)) {
            return Comparator.LT;
        } else if ("le".equals(token)) {
            return Comparator.LE;
        } else if ("gt".equals(token)) {
            return Comparator.GT;
        } else if ("ge".equals(token)) {
            return Comparator.GE;
        } else if ("eq".equals(token)) {
            return Comparator.EQ;
        } else {
            throw new IllegalArgumentException(
                "Illegal comparator for a confFilter: " + token);
        }
    }

    /**
     * Parses a token String and convert it to a SortOrder;
     * 
     * @param token
     * @return a SortOrder
     */
    public static SortOrder parseSortOrder(String token) {
        if ("desc".equals(token)) {
            return SortOrder.DESC;
        } else if ("asc".equals(token)) {
            return SortOrder.ASC;
        } else {
            throw new IllegalArgumentException(
                "Illegal sortOrder for a sortFilter: " + token);
        }
    }

    /**
     * docType for this filter.
     */
    public final String docType;

    /**
     * Creates an instance of QueryFilter for a docType;
     * 
     * @param docType
     */
    public QueryFilter(String docType) {
        this.docType = docType;
    }

    /**
     * Applies this filter to the specified ModelQuery. Subclasses may
     * implements this.
     * 
     * @param curMq
     *            a ModelQuery to apply this filter
     * @param isSingleCond
     *            a flag to indicate if this query contains only single
     *            condition (GT/GE, or LT/LE)
     * @return a ModelQuery
     */
    public ModelQuery<JEDoc> applyFilterToModelQuery(ModelQuery<JEDoc> curMq,
            boolean isSingleCond) {
        return curMq; // do nothing by default
    }

    /**
     * Applies this filter to the specified result list. Subclasses may override
     * this.
     * 
     * @param resultList
     * @return
     */
    public Collection<Object> applyFilterToResultList(
            Collection<Object> resultList) {
        return resultList; // do nothing by default
    }
}
