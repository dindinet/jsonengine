package com.jsonengine.model;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class JEDocTypeInfoTest extends AppEngineTestCase {

    private JEDocTypeInfo model = new JEDocTypeInfo();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
}
