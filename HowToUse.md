## REST API ##

jsonengine provides REST API described below for creating, reading, updating and deleting JSON documents. Client can use the API in a RESTful way (e.g. using GET/POST/PUT/DELETE HTTP methods) but can also use only GET and POST for accessing the all features (this would be useful for Flash client).

  * POST: create or update a doc.
  * GET: read doc(s).
  * PUT (or POST with `_`method=put parameter): update a doc.
  * DELETE (or POST with `_`method=delete parameter): delete doc(s).

## Getting JSON document(s) from the Datastore ##

See [HowToUseQuery](HowToUseQuery.md) for how to get JSON document(s) from Datastore.

## Saving a JSON document to the Datastore ##

### POST Example (create) ###

You can save a doc on the Datastore with POST method. You can use one of the following styles and both of them stores a JSON doc {"name":"Foo",age:20}.

  * FORM parameter style
    * POST /`_`je/myDoc?name=Foo&age=20

  * JSON doc style
    * POST /`_`je/myDoc?`_`doc={"name":"Foo","age":20}

And you will get the following response:

  * Response (for status code 200):
    * {"name":"Foo",age:20,`_`docId:"kzlQxBmFhuOLneBQMdvLR5jjGktR8KG8",`_`updatedAt:1266738290619, ... }

### POST Example (update) ###

To update an existing doc, put its docId on the path:

  * POST /`_`je/myDoc/kzlQxBmFhuOLneBQMdvLR5jjGktR8KG8?age=25
  * POST /`_`je/myDoc/kzlQxBmFhuOLneBQMdvLR5jjGktR8KG8?`_`doc={"age":25}

You can specify only the properties you want to update.

### POST syntax ###

POST method will create or update a doc.

```
/POST /_je/<docType>[/<docId>]
```

  * HTTP Request URI values
    * **docType** (required): docType (document type) of the doc.
      * Similar to table name or class name. For example, "address" docType may be for saving a doc which contains address info.
    * **docId** (optional): docID (document id) of the doc to be updated.

You can pass a doc to jsonengine by either "FORM parameter style parameters" or "JSON doc style parameters".

  * HTTP Request parameters (FORM parameter style)
    * **<any parameters>**: You can put any pairs of parameter name and value. They will be interpreted as properties of a doc.
      * jsonengine will try to parse each parameter value as 1) a long number, 2) a double number, 3) a boolean and then 4) a string. So please be careful that strings like "001" or "true" will not be stored as String. If you want to specify exact types of properties to avoid it, please use JSON doc style.
      * If you provide multiple property values with a same name, the values will be stored in an array in a doc.
      * If you are updating an existing doc, you can provide only the properties you want to update.
      * You can not use property names starting with `_` in this style.
    * **`_`docId** (optional): docId of the doc to be created/updated (see the following docId notes).
    * **`_`checkUpdatesAfter** (optional): set the value of `_`updatedAt property of the client-side copy of the doc to check update conflict. See "Update conflict detection" section below for details.

  * HTTP Request parameters (JSON doc style)
    * **`_`doc** (required): JSON string to be created/updated.
      * You can include `_`docId property in the doc to create/update it with the specified docId (see the following docId notes).
      * The JSON string should be URL encoded.
    * **`_`checkUpdatesAfter** (optional): set the value of `_`updatedAt property of the client-side copy of the doc to check update conflict. See "Update conflict detection" section below for details.

And you will get one of the following HTTP response:

  * HTTP Response
    * Status code 200: Created or updated the doc successfully.
      * Response body: the updated doc includes the generated properties (see below) like `_`docId, `_`createdAt and `_`createdBy.
    * Status code 403: Access not authorized (see [HowToAdmin](HowToAdmin.md) for access control setting).
    * Status code 409: Detected update conflict.
    * Status code 500: Caught an unexpected system error.

  * Specifying docId and how POST method behaves
    * You can provide `_`docId property via `_`docId parameter (in the FORM parameter style) or `_`docId property of a doc (in the JSON doc style).
    * If you provide the `_`docId, POST method will try to update the existing doc. If not found, create a new doc.
    * If you do not provide any `_`docId, jsonengine will create a new doc. An auto-generated `_`docId will be returned via the result doc.

### PUT syntax ###

PUT method will only update an existing doc and will not create a new doc. If it can not find existing doc for specified docId, it will return a status code 404. You can also call this method by putting `_`method=put parameter on POST method.

```
/PUT /_je/<docType>[/<docId>]
```

```
/POST /_je/<docType>[/<docId>]?_method=put
```

The syntax of HTTP request parameters and HTTP response are the same as POST method, except for that PUT can return the following HTTP response:

  * Status code 404: No doc found for specified docId.

## Deleting a JSON document from the Datastore ##

### DELETE example ###

DELETE method (and POST method with `_`method=delete parameter) will delete the specified doc.

  * DELETE /`_`je/myDoc/kzlQxBmFhuOLneBQMdvLR5jjGktR8KG8
  * POST /`_`je/myDoc/kzlQxBmFhuOLneBQMdvLR5jjGktR8KG8?`_`method=delete

  * Response (for status code 200):
    * None.

### DELETE syntax ###

To delete an existing JSON document, you can use DELETE HTTP method, or POST method with `_`method=delete parameter. You can delete all docs under a docType by omitting `<`docId`>`.

```
/DELETE /_je/<docType>/<docId>
```

```
/POST /_je/<docType>/<docId>?_method=delete
```

  * HTTP Request URI values
    * **docType** (required): docType (document type) of the JSON doc.
    * **docId** (optional): docID (document id) of the doc to be deleted. By omitting this, you can delete all the docs under the docType. Please note that this will be processed in background and may take a while if there are many docs to delete.

  * HTTP Request parameters
    * **`_`checkUpdatesAfter** (optional): set the value of `_`updatedAt property of the client-side copy of the doc to check update conflict. See "Update conflict detection" section below for details.

  * HTTP Response
    * Status code 200: Deleted the doc successfully.
      * Response body: empty.
    * Status code 403: Access not authorized (see [HowToAdmin](HowToAdmin.md) for access control setting).
    * Status code 404: If there is no such doc stored in jsonengine.
    * Status code 409: Detected update conflict.
    * Status code 500: Caught an unexpected system error.

## Update conflict detection ##

jsonengine provides an "update conflict detection" as an optional feature. A client may set "`_`checkUpdatesAfter" parameter to the `_`updatedAt value of the client-side copy of the doc on POST/PUT/DELETE methods. Then jsonengine will compare it with the `_`updatedAt property value of the doc stored in jsonengine. If the latter is newer, it means some other client has updated the doc already and the client's doc is not the latest anymore. jsonengine will return Status code 409 (Conflict) without updating it so that client may get the latest doc again and let user retry the update. In this way the web application can maintain data integrity across the service (a.k.a. optimistic concurrency control).

## JSON Document Format ##

jsonengine can handle arbitrary docs with the features below.

  * Schemaless: even though you need to specify the docType when you store a doc on jsonengine, each doc can have different schema. A docType is just a "tag" for grouping docs and it does not represent any fixed schema.
  * Nested: You can let doc having children or grand-children docs. But only the top-level literal properties can be used as filtering/sorting condition on query.

### Restrictions ###

  * A doc's size should be smaller than `<` 1MB: Because of the restriction of App Engine, a doc should be smaller than 1MB.
  * Indexed property size `<` 500 chars: Any properties with literal value (i.e. string, boolean and number) will be added to the jsonengine's internal index automatically, so that the document can be filtered or sorted by the property on queries.
    * The size of the indexed property (= the property name length + property value length) should be less than 500 characters.
    * By adding "`_`" prefix to the property name (e.g. `_`foo), it will be excluded from the index and you can store more than 500 chars to it.

### Generated properties ###

The following properties are generated by jsonengine automatically and client must not modify those values. You can use generated properties as a filtering or sorting condition on query.

  * **`_`docId**: an unique ID of each doc.
  * **`_`createdAt**: a long value of timestamp when the doc has been created.
  * **`_`createdBy**: An user ID of the person created the doc.
  * **`_`updatedAt**: a long value of timestamp when the doc has been updated.
  * **`_`updatedBy**: An user ID of the person updated the doc.