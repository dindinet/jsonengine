package com.jsonengine.service.doctype;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;

import com.jsonengine.model.JEDocTypeInfo;

public class DocTypeServiceTest extends AppEngineTestCase {

    public static final String TEST_DOCTYPE = "test";

    public static final String TEST_USER_FOO = "foo";

    public static final String TEST_USER_BAR = "bar";

    private DocTypeService service = new DocTypeService();

    @Test
    public void testAccessWithNoDocTypeInfo() throws Exception {
        assertTrue(isAccessibleByPublic(true)); // TODO
        assertTrue(isAccessibleByPublic(false));
    }

    @Test
    public void testAccessForPublic() throws Exception {
        service.createDocTypeInfo(
            TEST_DOCTYPE,
            JEDocTypeInfo.ACCESS_LEVEL_PUBLIC,
            JEDocTypeInfo.ACCESS_LEVEL_PUBLIC);
        assertTrue(isAccessibleByPublic(true));
        assertTrue(isAccessibleByPublic(false));
    }

    @Test
    public void testAccessForProtected() throws Exception {
        service.createDocTypeInfo(
            TEST_DOCTYPE,
            JEDocTypeInfo.ACCESS_LEVEL_PROTECTED,
            JEDocTypeInfo.ACCESS_LEVEL_PROTECTED);
        assertFalse(isAccessibleByPublic(true));
        assertFalse(isAccessibleByPublic(false));
        assertTrue(isAccessibleByProtected(true));
        assertTrue(isAccessibleByProtected(false));
    }

    @Test
    public void testAccessForPrivate() throws Exception {
        service.createDocTypeInfo(
            TEST_DOCTYPE,
            JEDocTypeInfo.ACCESS_LEVEL_PRIVATE,
            JEDocTypeInfo.ACCESS_LEVEL_PRIVATE);
        assertFalse(isAccessibleByPublic(true));
        assertFalse(isAccessibleByPublic(false));
        assertFalse(isAccessibleByProtected(true));
        assertFalse(isAccessibleByProtected(false));
        assertTrue(isAccessibleByPrivate(true));
        assertTrue(isAccessibleByPrivate(false));
    }
    
    @Test
    public void testAccessForAdmin() throws Exception {
        service.createDocTypeInfo(
            TEST_DOCTYPE,
            JEDocTypeInfo.ACCESS_LEVEL_ADMIN,
            JEDocTypeInfo.ACCESS_LEVEL_ADMIN);
        assertFalse(isAccessibleByPublic(true));
        assertFalse(isAccessibleByPublic(false));
        assertFalse(isAccessibleByProtected(true));
        assertFalse(isAccessibleByProtected(false));
        assertFalse(isAccessibleByPrivate(true));
        assertFalse(isAccessibleByPrivate(false));
        assertTrue(isAccessibleByAdmin(true));
        assertTrue(isAccessibleByAdmin(false));
    }
    
    private boolean isAccessibleByPublic(boolean isRead) {
        return service.isAccessible(
            TEST_DOCTYPE,
            null,
            TEST_USER_BAR,
            isRead,
            false);
    }

    private boolean isAccessibleByProtected(boolean isRead) {
        return service.isAccessible(
            TEST_DOCTYPE,
            TEST_USER_FOO,
            TEST_USER_BAR,
            isRead,
            false);
    }

    private boolean isAccessibleByPrivate(boolean isRead) {
        return service.isAccessible(
            TEST_DOCTYPE,
            TEST_USER_BAR,
            TEST_USER_BAR,
            isRead,
            false);
    }

    private boolean isAccessibleByAdmin(boolean isRead) {
        return service.isAccessible(
            TEST_DOCTYPE,
            TEST_USER_BAR,
            TEST_USER_BAR,
            isRead,
            true);
    }

}
