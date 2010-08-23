package com.jsonengine.service.doctype;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;

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
        (new DocTypeService()).saveDocTypeInfo(
            TEST_DOCTYPE,
            DocTypeService.ACCESS_LEVEL_PUBLIC,
            DocTypeService.ACCESS_LEVEL_PUBLIC);
        assertTrue(isAccessibleByPublic(true));
        assertTrue(isAccessibleByPublic(false));
    }

    @Test
    public void testAccessForProtected() throws Exception {
        (new DocTypeService()).saveDocTypeInfo(
            TEST_DOCTYPE,
            DocTypeService.ACCESS_LEVEL_PROTECTED,
            DocTypeService.ACCESS_LEVEL_PROTECTED);
        assertFalse(isAccessibleByPublic(true));
        assertFalse(isAccessibleByPublic(false));
        assertTrue(isAccessibleByProtected(true));
        assertTrue(isAccessibleByProtected(false));
    }

    @Test
    public void testAccessForPrivate() throws Exception {
        (new DocTypeService()).saveDocTypeInfo(
            TEST_DOCTYPE,
            DocTypeService.ACCESS_LEVEL_PRIVATE,
            DocTypeService.ACCESS_LEVEL_PRIVATE);
        assertFalse(isAccessibleByPublic(true));
        assertFalse(isAccessibleByPublic(false));
        assertFalse(isAccessibleByProtected(true));
        assertFalse(isAccessibleByProtected(false));
        assertTrue(isAccessibleByPrivate(true));
        assertTrue(isAccessibleByPrivate(false));
    }

    @Test
    public void testAccessForAdmin() throws Exception {
        (new DocTypeService()).saveDocTypeInfo(
            TEST_DOCTYPE,
            DocTypeService.ACCESS_LEVEL_ADMIN,
            DocTypeService.ACCESS_LEVEL_ADMIN);
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
