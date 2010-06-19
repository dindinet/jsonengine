package com.jsonengine.query;

import java.util.Set;

import org.slim3.datastore.ModelQuery;
import org.slim3.datastore.StringCollectionAttributeMeta;

import com.jsonengine.common.JEUtils;
import com.jsonengine.meta.JEDocMeta;
import com.jsonengine.model.JEDoc;

public abstract class QueryFilter {

    public enum Comparator {
        LT, LE, GT, GE, EQ
    };

    public enum SortOrder {
        ASC, DESC
    };

    private static final JEDocMeta jeDocMeta = JEDocMeta.get();

    public final String docType;

    public QueryFilter(String docType) {
        this.docType = docType;
    }

    public abstract ModelQuery<JEDoc> applyFilter(ModelQuery<JEDoc> curMq);

    public static class CondFilter extends QueryFilter {

        public final Comparator comparator;

        public final String condParam;

        public final String condMin;

        public final String condMax;

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
        public ModelQuery<JEDoc> applyFilter(ModelQuery<JEDoc> mq) {
            final StringCollectionAttributeMeta<JEDoc, Set<String>> ie =
                jeDocMeta.indexEntries;
            switch (comparator) {
            case LT:
                return mq.filter(ie.lessThan(condParam)).filter(
                    ie.greaterThan(condMin));
            case LE:
                return mq.filter(ie.lessThanOrEqual(condParam)).filter(
                    ie.greaterThan(condMin));
            case GT:
                return mq.filter(ie.greaterThan(condParam)).filter(
                    ie.lessThan(condMax));
            case GE:
                return mq.filter(ie.greaterThanOrEqual(condParam)).filter(
                    ie.lessThan(condMax));
            case EQ:
                return mq.filter(ie.equal(condParam));
            }
            throw new IllegalArgumentException();
        }
    }

    public static class LimitFilter extends QueryFilter {

        public final int limitCount;

        public LimitFilter(String docType, int limitCount) {
            super(docType);
            this.limitCount = limitCount;
        }

        @Override
        public ModelQuery<JEDoc> applyFilter(ModelQuery<JEDoc> mq) {
            return mq.limit(limitCount);
        }
    }

    public static class SortFilter extends QueryFilter {

        public final String propName;

        public final SortOrder sortOrder;

        public SortFilter(String docType, String propName, SortOrder sortOrder) {
            super(docType);
            this.propName = propName;
            this.sortOrder = sortOrder;
        }

        @Override
        public ModelQuery<JEDoc> applyFilter(ModelQuery<JEDoc> mq) {
            return mq; // TODO
        }
    }
}
