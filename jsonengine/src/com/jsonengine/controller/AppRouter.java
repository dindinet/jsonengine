package com.jsonengine.controller;

import org.slim3.controller.router.RouterImpl;


public class AppRouter extends RouterImpl {

    public AppRouter() {

        // CRUDController
        addRouting("/_je/{doctype}", "/cRUD?docType={doctype}");
        addRouting("/_je/{doctype}/{docId}", "/cRUD?docType={doctype}&docId={docId}");

        // QueryController
        addRouting("/_q/{doctype}", "/query?docType={doctype}");
    }

}
