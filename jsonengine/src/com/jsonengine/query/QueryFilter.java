package com.jsonengine.query;

import java.util.Set;

import org.slim3.datastore.ModelQuery;
import org.slim3.datastore.StringCollectionAttributeMeta;

import com.jsonengine.common.JEUtils;
import com.jsonengine.meta.JEDocMeta;
import com.jsonengine.model.JEDoc;

public abstract class QueryFilter<T> {

    enum Comparator {
        LT, LE, GT, GE, EQ
    };

    enum SortOrder {
        ASC, DESC
    };

    private final JEDocMeta jeDocMeta = JEDocMeta.get();

    public final String docType;

    public QueryFilter(String docType) {
        this.docType = docType;
    }

    public abstract ModelQuery<JEDoc> applyFilter(ModelQuery<JEDoc> curMq);

    public class CondFilter extends QueryFilter<JEDoc> {

        public final Comparator comparator;

        public final String condParam;

        public CondFilter(String docType, String propName,
                Comparator comparator, Object propValue) {            
            super(docType);
            this.comparator = comparator;
            this.condParam =
                docType
                    + ":"
                    + propName
                    + ":"
                    + JEUtils.i.encodePropValue(propValue);
        }

        @Override
        public ModelQuery<JEDoc> applyFilter(ModelQuery<JEDoc> mq) {
            final StringCollectionAttributeMeta<JEDoc, Set<String>> ie =
                jeDocMeta.indexEntries;
            switch (comparator) {
            case LT:
                return mq.filter(ie.lessThan(condParam));
            case LE:
                return mq.filter(ie.lessThanOrEqual(condParam));
            case GT:
                return mq.filter(ie.greaterThan(condParam));
            case GE:
                return mq.filter(ie.greaterThanOrEqual(condParam));
            case EQ:
                return mq.filter(ie.equal(condParam));
            }
            throw new IllegalArgumentException();
        }
    }

    public class LimitFilter extends QueryFilter<JEDoc> {

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

    public class SortFilter extends QueryFilter<JEDoc> {

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
