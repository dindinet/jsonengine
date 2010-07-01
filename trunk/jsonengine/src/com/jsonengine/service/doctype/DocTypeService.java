package com.jsonengine.service.doctype;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.jsonengine.common.JEUtils;
import com.jsonengine.meta.JEDocTypeInfoMeta;
import com.jsonengine.model.JEDocTypeInfo;

/**
 * Provices various methods regarding {@link JEDocTypeInfo}, including access
 * control.
 * 
 * @author @kazunori_279
 */
public class DocTypeService {

    public static final String MC_KEY_DOCTYPEINFO =
        "com.jsonengine.service.doctype.DocTypeService#docTypeInfo:";

    /**
     * Checks if the docType is accessible for specified requestor, creator and
     * read/write mode.
     * 
     * @param docType
     *            docType to be checked.
     * @param requestedBy
     *            An user ID of the requestor (null means the user has not
     *            authenticated).
     * @param createdBy
     *            An user ID of the creator of the doc (null means that this is
     *            a create request).
     * @param isRead
     *            true if this is a read access (false if it's a write access)
     * @param isAdmin
     *            true if the requestor is an administrator.
     * @return true if the access is allowed.
     */
    public boolean isAccessible(String docType, String requestedBy,
            String createdBy, boolean isRead, boolean isAdmin) {

        // get docTypeInfo
        final JEDocTypeInfo jdti = getDocTypeInfo(docType);
        
        // it it's admin access, allow it
        if (isAdmin) {
            return true;
        }

        // if there's no docTypeInfo specified, deny all access
        if (jdti == null) {
            return true; // TODO disallow it after preparing the admin console
        }

        // if it's "public", allow all accesses
        if (JEDocTypeInfo.ACCESS_LEVEL_PUBLIC.equals(jdti
            .getAccessLevel(isRead))) {
            return true;
        }

        // if it's "protected", check requestor has an ID
        if (JEDocTypeInfo.ACCESS_LEVEL_PROTECTED.equals(jdti
            .getAccessLevel(isRead))) {
            return requestedBy != null;
        }

        // if it's "private", check if this is a create request, requestor =
        // creator, or an admin access
        if (JEDocTypeInfo.ACCESS_LEVEL_PRIVATE.equals(jdti
            .getAccessLevel(isRead))) {
            final boolean isCreateRequest =
                !isRead && createdBy == null && requestedBy != null;
            final boolean isCreatorAccess =
                requestedBy != null && requestedBy.equals(createdBy);
            return isCreateRequest || isCreatorAccess || isAdmin;
        }

        // otherwise, disallow the access
        return false;
    }

    /**
     * Checks if the docType is able to be queried.
     * 
     * @param docType
     *            docType to be checked.
     * @param requestedBy
     *            An user ID of the requestor (null means the user has not
     *            authenticated).
     * @param isAdmin
     *            true if the requestor is an administrator.
     * @return true if the access is allowed.
     */
    public boolean isAccessibleByQuery(String docType, String requestedBy,
            boolean isAdmin) {

        // get docTypeInfo
        final JEDocTypeInfo jdti = getDocTypeInfo(docType);

        // it it's admin access, allow it
        if (isAdmin) {
            return true;
        }

        // if there's no docTypeInfo specified deny it
        if (jdti == null) {
            return false;
        }
        
        // if there's no docTypeInfo specified, or it's "public", allow all
        // accesses
        if (JEDocTypeInfo.ACCESS_LEVEL_PUBLIC.equals(jdti
                .getAccessLevel(true))) {
            return true;
        }

        // if it's "protected", check requestor has an ID
        if (JEDocTypeInfo.ACCESS_LEVEL_PROTECTED.equals(jdti
            .getAccessLevel(true))) {
            return requestedBy != null;
        }

        // if it's "private", check requestor has an ID
        if (JEDocTypeInfo.ACCESS_LEVEL_PRIVATE
            .equals(jdti.getAccessLevel(true))) {
            return requestedBy != null;
        }

        // otherwise, disallow the access
        return false;
    }

    /**
     * Returns {@link JEDocTypeInfo} for specified docType.
     * 
     * @param docType
     *            docType to get.
     * @return {@link JEDocTypeInfo} for the docType
     */
    public JEDocTypeInfo getDocTypeInfo(String docType) {

        // check docType is available
        assert docType != null;

        // try to get it from Memcache
        JEDocTypeInfo jdti =
            (JEDocTypeInfo) JEUtils.mcService.get(MC_KEY_DOCTYPEINFO + docType);
        if (jdti != null) {
            return jdti;
        }

        // try to get docTypeInfo by docType
        final Key key =
            KeyFactory.createKey(JEDocTypeInfo.class.getSimpleName(), docType);
        final Transaction tx = Datastore.beginTransaction();
        try {
            jdti = Datastore.get(new JEDocTypeInfoMeta(), key);
            JEUtils.mcService.put(MC_KEY_DOCTYPEINFO + docType, jdti);
            tx.commit();
        } catch (EntityNotFoundRuntimeException e) {
            // ignore if it's not found
        }
        return jdti;
    }

    /**
     * Create and save a {@link JEDocTypeInfo} for specified docType.
     * 
     * @param docType
     *            docType for the {@link JEDocTypeInfo}.
     * @param accessLevelForRead
     *            access level specified for this docType on read operations.
     * @param accessLevelForWrite
     *            access level specified for this docType on write operations.
     * @return a {@link JEDocTypeInfo} created.
     */
    public JEDocTypeInfo createDocTypeInfo(String docType,
            String accessLevelForRead, String accessLevelForWrite) {

        // create JEDocTypeInfo
        final JEDocTypeInfo jdti = new JEDocTypeInfo();
        jdti.setKey(KeyFactory.createKey(
            jdti.getClass().getSimpleName(),
            docType));
        jdti.setAccessLevelForRead(accessLevelForRead);
        jdti.setAccessLevelForWrite(accessLevelForWrite);

        // save it
        final Transaction tx = Datastore.beginTransaction();
        Datastore.put(jdti);
        tx.commit();
        clearDocTypeCache(docType);
        return jdti;
    }

    private void clearDocTypeCache(String docType) {
        JEUtils.mcService.delete(MC_KEY_DOCTYPEINFO + docType);
    }
}
