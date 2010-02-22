package com.jsonengine.crud;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.arnx.jsonic.JSON;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jsonengine.common.JEConflictException;
import com.jsonengine.common.JENotFoundException;
import com.jsonengine.common.JERequest;
import com.jsonengine.common.JEUtils;
import com.jsonengine.model.JEDoc;

/**
 * Implements CRUD operations for jsonengine.
 * 
 * @author @kazunori_279
 */
public class CRUDService {

    /**
     * Singleton instance.
     */
    public static final CRUDService i = new CRUDService();

    private CRUDService() {
    }

    /**
     * Creates or updates specified JSON document into Datastore. If you provide
     * a docId (via {@link CRUDRequest} parameter), it checks if there's existing
     * document with the same docId. If yes, it updates it. If no, it creates
     * new one.
     * 
     * If checkConflict property of specified {@link CRUDRequest} is set true, it
     * checks if anyone has already updated the same document. If yes, it throws
     * a {@link JEConflictException}.
     * 
     * @condParam jeReq
     *            {@link CRUDRequest}
     * @return the saved JSON document with _docId and _updatedAt properties
     * @throws JEConflictException
     *             if it detected a update confliction
     */
    public String put(CRUDRequest jeReq) throws JEConflictException {

        // try to find an existing JEDoc for the docId
        final Transaction tx = Datastore.beginTransaction();
        JEDoc jeDoc = null;
        if (jeReq.getDocId() != null) {
            try {
                jeDoc = getJEDoc(tx, jeReq);
            } catch (JENotFoundException e) {
                // not found
            }
        }

        // if existing JEDoc is not found, create new one
        if (jeDoc == null) {
            jeDoc = JEDoc.createJEDoc(jeReq);
        }

        // update JEDoc content
        jeDoc.setUpdatedAt(jeReq.getRequestedAt());
        jeDoc.setUpdatedBy(jeReq.getRequestedBy());
        jeDoc.setDocValues(jeReq.getJsonMap());
        jeDoc.getDocValues().put(JEDoc.PROP_NAME_DOCID, jeDoc.getDocId());
        jeDoc.getDocValues().put(
            JEDoc.PROP_NAME_UPDATED_AT,
            jeDoc.getUpdatedAt());

        // build index entries of the JEDoc
        jeDoc.setIndexEntries(buildIndexEntries(jeReq, jeDoc.getDocValues()));

        // save JEDoc
        try {
            Datastore.put(tx, jeDoc);
            Datastore.commit(tx);
        } catch (ConcurrentModificationException e) {
            throw new JEConflictException(e);
        }

        // return the saved JSON document
        return JSON.encode(jeDoc.getDocValues());
    }

    private JEDoc getJEDoc(Transaction tx, CRUDRequest jeReq)
            throws JEConflictException, JENotFoundException {

        // try to get specified JEDoc
        final Key jeDocKey = Datastore.createKey(JEDoc.class, jeReq.getDocId());
        JEDoc jeDoc = null;
        try {
            jeDoc = Datastore.get(tx, JEDoc.class, jeDocKey);
        } catch (EntityNotFoundRuntimeException e) {
            throw new JENotFoundException(e);
        }

        // check if it's found
        if (jeDoc == null) {
            throw new JENotFoundException("JEDoc not found");
        }

        // check update confliction by checking updatedAt
        if (jeDoc != null
            && jeReq.getCheckUpdatesAfter() != null
            && isConflicted(jeReq, jeDoc)) {
            throw new JEConflictException("Detedted a conflict by_updatedAt");
        }
        return jeDoc;
    }

    private boolean isConflicted(CRUDRequest jeReq, JEDoc jeDoc) {
        return jeReq.getUpdatedAt() != null
            && jeDoc.getUpdatedAt() > jeReq.getUpdatedAt().longValue();
    }

    // build index entries from the top-level properties
    private Set<String> buildIndexEntries(JERequest jeReq,
            Map<String, Object> docValues) {
        final Set<String> indexEntries = new HashSet<String>();
        for (String propName : docValues.keySet()) {

            // skip any props start with "_" (e.g. _foo, _bar)
            if (propName.startsWith("_")) {
                continue;
            }

            // an index entry will be like: <docType>:<propName>:<propValue>
            final String propValue = JEUtils.i.encodePropValue(docValues.get(propName));
            if (propValue != null) {
                indexEntries.add(jeReq.getDocType()
                    + ":"
                    + propName
                    + ":"
                    + propValue);
            }
        }
        return indexEntries;
    }

    /**
     * Returns a JSON document specified by the CRUDRequest's docId. Throws a
     * {@link JENotFoundException} if there's no such JSON document with the
     * docId. You can also pass a JSON document in the {@link CRUDRequest} to
     * check if it has been updated or not.
     * 
     * @condParam jeReq
     *            a CRUDRequest with docName to be retrieved.
     * @return a JSON document retrieved.
     * @throws JENotFoundException
     *             if it can not find any JSON document with specified docId.
     * @throws JEConflictException
     *             if checkConflict property of {@link CRUDRequest} is set true
     *             and if it detected that the JSON document in
     *             {@link CRUDRequest} is updated.
     */
    public String get(CRUDRequest jeReq) throws JENotFoundException,
            JEConflictException {

        // try to get the JEDoc
        assert jeReq.getDocId() != null;
        final Transaction tx = Datastore.beginTransaction();
        final JEDoc jeDoc;
        try {
            jeDoc = getJEDoc(tx, jeReq);
            Datastore.commit(tx);
        } catch (ConcurrentModificationException e) {
            throw new JEConflictException(e);
        }
        return jeDoc.encodeJSON();
    }

    /**
     * Deletes the specified JSON document from Datastore. You need to provide a
     * docId (via {@link CRUDRequest} parameter) to remove an existing document
     * with the same docId.
     * 
     * If checkConflict property of specified {@link CRUDRequest} is set true, and
     * you provide the original JSON document with _updatedAt property in
     * {@link CRUDRequest}, it checks if anyone has already updated the same
     * document. If yes, it throws a {@link JEConflictException}.
     * 
     * @condParam json
     *            JSON document string to be saved
     * @condParam jeReq
     *            {@link CRUDRequest}
     * @return docId of the saved JSON document.
     * @throws JENotFoundException
     *             if it can not find any JSON document with specified docId.
     * @throws JEConflictException
     *             if it detected a update confliction
     */
    public void delete(CRUDRequest jeReq) throws JENotFoundException,
            JEConflictException {

        // try to find an existing JEDoc for the docId
        assert jeReq.getDocId() != null;
        final Transaction tx = Datastore.beginTransaction();
        final JEDoc jeDoc = getJEDoc(tx, jeReq);

        // remove it
        try {
            Datastore.delete(tx, jeDoc.getKey());
            Datastore.commit(tx);
        } catch (ConcurrentModificationException e) {
            throw new JEConflictException(e);
        }
    }
}
