package com.jsonengine.model;

import java.io.Serializable;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;

/**
 * Represents a model that holds meta info for a docType.
 * 
 * @author @kazunori_279
 */
@Model(schemaVersion = 1)
public class JEDocTypeInfo implements Serializable {

    /**
     * An access level where only administrator is allowed to access.
     */
    public static final String ACCESS_LEVEL_ADMIN = "admin";

    /**
     * An access level where only the creator of the doc is allowed to access.
     */
    public static final String ACCESS_LEVEL_PRIVATE = "private";

    /**
     * An access level where only an authenticated user is allowed to access.
     */
    public static final String ACCESS_LEVEL_PROTECTED = "protected";

    /**
     * An access level where anyone is allowed to access.
     */
    public static final String ACCESS_LEVEL_PUBLIC = "public";

    private static final long serialVersionUID = 1L;

    // access level required for read operations (read and query) to this
    // docType
    private String accessLevelForRead = ACCESS_LEVEL_PUBLIC;

    // access level required for write operations (create, update and delete) to
    // this docType
    private String accessLevelForWrite = ACCESS_LEVEL_PUBLIC;

    // the key that includes docType as key name (to assure uniqueness for each
    // docType)
    @Attribute(primaryKey = true)
    private Key key;

    @Attribute(version = true)
    private Long version;

    /**
     * Returns access level for read or write access of this docType.
     * 
     * @param isRead
     *            true if it's read access
     * @return access level String
     */
    public String getAccessLevel(boolean isRead) {
        return isRead ? getAccessLevelForRead() : getAccessLevelForWrite();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        JEDocTypeInfo other = (JEDocTypeInfo) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }

    public String getAccessLevelForRead() {
        return accessLevelForRead;
    }

    public String getAccessLevelForWrite() {
        return accessLevelForWrite;
    }

    /**
     * Returns the key.
     * 
     * @return the key
     */
    public Key getKey() {
        return key;
    }

    /**
     * Returns the version.
     * 
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    public void setAccessLevelForRead(String accessLevelForRead) {
        this.accessLevelForRead = accessLevelForRead;
    }

    public void setAccessLevelForWrite(String accessLevelForWrite) {
        this.accessLevelForWrite = accessLevelForWrite;
    }

    /**
     * Sets the key.
     * 
     * @param key
     *            the key
     */
    public void setKey(Key key) {
        this.key = key;
    }

    /**
     * Sets the version.
     * 
     * @param version
     *            the version
     */
    public void setVersion(Long version) {
        this.version = version;
    }
}
