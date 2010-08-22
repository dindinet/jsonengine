package com.jsonengine.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;
import org.slim3.tester.TestEnvironment;
import org.slim3.util.AppEngineUtil;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.apphosting.api.ApiProxy;
import com.jsonengine.common.JEAccessDeniedException;
import com.jsonengine.common.JEConflictException;
import com.jsonengine.common.JEUtils;
import com.jsonengine.meta.JEDocMeta;
import com.jsonengine.model.JEDoc;
import com.jsonengine.service.crud.CRUDRequest;
import com.jsonengine.service.crud.CRUDService;

public class FrontControllerTest extends ControllerTestCase {

    JEDocMeta meta = JEDocMeta.get();

    @Test
    public void POST_canInsertADoc() throws Exception {
        tester.request.setMethod("post");
        tester.param("name", "Foo");
        tester.param("age", "20");
        tester.start("/_je/myDoc");
        FrontController controller = tester.getController();
        assertThat(controller, is(notNullValue()));

        JEDoc jeDoc =
            Datastore
                .query(meta)
                .filter(meta.docType.equal("myDoc"))
                .asSingle();
        assertThat(jeDoc, is(notNullValue()));
        assertThat(jeDoc.getDocType(), is("myDoc"));
        Map<String, Object> values = jeDoc.getDocValues();
        assertThat(values.get("name").toString(), is("Foo"));
        assertThat(values.get("age").toString(), is("20"));
    }

    @Test
    public void PUT_canNotInsertADoc() throws Exception {
        tester.request.setMethod("PUT");
        tester.param("name", "Foo");
        tester.param("age", "20");
        tester.start("/_je/myDoc");
        FrontController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(
            tester.response.getStatus(),
            is(HttpServletResponse.SC_CONFLICT));

        JEDoc jeDoc =
            Datastore
                .query(meta)
                .filter(meta.docType.equal("myDoc"))
                .asSingle();

        assertThat(jeDoc, is(nullValue()));
    }

    @Test
    public void DELETE_canDeleteADoc() throws Exception {
        String docId = createTestData();

        assertThat(Datastore
            .query(meta)
            .filter(meta.docType.equal("myDoc"))
            .count(), is(1));

        tester.request.setMethod("delete");
        tester.start("/_je/myDoc/" + docId);

        FrontController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.isRedirect(), is(false));
        int count =
            Datastore.query(meta).filter(meta.docType.equal("myDoc")).count();
        assertThat(count, is(0));
    }

    @Test
    public void POST_canDeleteADoc() throws Exception {
        String docId = createTestData();

        assertThat(Datastore
            .query(meta)
            .filter(meta.docType.equal("myDoc"))
            .count(), is(1));

        tester.request.setMethod("post");
        tester.param("_method", "delete");
        tester.start("/_je/myDoc/" + docId);

        FrontController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.isRedirect(), is(false));
        int count =
            Datastore.query(meta).filter(meta.docType.equal("myDoc")).count();
        assertThat(count, is(0));
    }

    @Test
    public void GET_canGetADocByDocId() throws Exception {
        String docId = createTestData();

        tester.start("/_je/myDoc/" + docId);

        FrontController controller = tester.getController();
        assertThat(controller, is(notNullValue()));

        assertThat(tester.response.getStatus(), is(200));
        String jsonString = tester.response.getOutputAsString();
        assertThat(jsonString, is(notNullValue()));
    }

    @Test
    public void GET_shouldReturn404IfDocNotFoundForADocId() throws Exception {
        tester.start("/_je/myDoc/notfoundDocId");

        FrontController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(404));
    }

    @Test
    public void emailShouldBeSavedIfUserHasLoggedIn() throws Exception {
        if (AppEngineUtil.isProduction()) {
            return; // kotoriではログインユーザを変更できないのでテストしない
        }

        TestEnvironment env = new TestEnvironment("unitTest@gmail.com", false);
        ApiProxy.setEnvironmentForCurrentThread(env);
        tester.request.setMethod("post");
        tester.start("/_je/myDoc");

        FrontController controller = tester.getController();
        assertThat(controller, is(notNullValue()));

        JEDoc jeDoc =
            Datastore
                .query(meta)
                .filter(meta.docType.equal("myDoc"))
                .asSingle();
        assertThat(jeDoc, is(notNullValue()));
        assertThat(jeDoc.getDocType(), is("myDoc"));

        Map<String, Object> values = jeDoc.getDocValues();
        String createdBy = values.get(JEDoc.PROP_NAME_CREATED_BY).toString();
        assertThat(createdBy, is("unitTest@gmail.com"));
    }

    @SuppressWarnings("unchecked")
    private String createTestData() throws JEConflictException,
            JEAccessDeniedException {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("age", "10");
        CRUDRequest jeReq = new CRUDRequest(jsonMap);
        jeReq.setDocType("myDoc");
        jeReq.setRequestedAt((new JEUtils()).getGlobalTimestamp());
        jeReq.setRequestedBy("unitTest@gmail.com");
        String json = new CRUDService().put(jeReq, false);
        Map jeDoc = (Map) JSON.decode(json);
        String docId = (String) jeDoc.get(JEDoc.PROP_NAME_DOCID);
        return docId;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void partialUpdate() throws Exception {

        // insert a doc
        tester.request.setMethod("post");
        tester.param("name", "Foo");
        tester.param("age", "20");
        tester.start("/_je/myDoc");

        // validate the result
        final String resultJson = tester.response.getOutputAsString();
        final Map<String, Object> resultMap =
            (Map<String, Object>) JSON.decode(resultJson);
        assertThat(resultMap.get("age").toString(), is("20"));
        assertThat(resultMap.get("name").toString(), is("Foo"));

        // update the doc partially (age -> 40)
        final String docId = (String) resultMap.get("_docId");
        tester.request.setMethod("post");
        tester.param("age", "40");
        tester.param("name", "Foo");
        tester.param("_docId", docId);
        tester.start("/_je/myDoc");

        // validate the result
        final Key key =
            KeyFactory.createKey(JEDoc.class.getSimpleName(), docId);
        final JEDoc jeDoc =
            Datastore.query(meta).filter(meta.key.equal(key)).asSingle();
        assertThat(jeDoc, is(notNullValue()));
        final Map<String, Object> jeMap = jeDoc.getDocValues();
        assertThat(jeMap.get("age").toString(), is("40"));
        assertThat(jeMap.get("name").toString(), is("Foo"));
        assertThat(jeMap.get("_docId").toString(), is(docId));
        
    }

}
