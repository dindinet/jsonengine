## Getting a JSON document from the Datastore ##

Here are some examples of GET method.

### Example 1: Getting one doc for a docId ###

```
/GET /_je/myDoc/kzlQxBmFhuOLneBQMdvLR5jjGktR8KG8
```

This GET request will return a doc for the docId "kzlQxBmFhuOLneBQMdvLR5jjGktR8KG8".

  * Response (for status code 200):
    * {"name":"Foo",age:20,`_`docId:"kzlQxBmFhuOLneBQMdvLR5jjGktR8KG8",`_`updatedAt:1266738290619, ... }

### Example 2: Searching for docs with specified condition ###

```
/GET /_je/myDoc?cond=name.eq.Foo
```

This GET request returns an array of JSON documents for the following condition:

  * docType is "myDoc".
  * "name" property equals to Foo.

### Example 3: Searching for docs with sorting and limit ###

```
/GET /_je/myDoc?cond=age.ge.20&cond=age.lt.60&sort=age.asc&limit=10
```

This GET request returns an array of JSON documents that satisfies:

  * docType is "myDoc".
  * "age" property is "greater than or equal to 20" **and** "less than 60".
  * Sorted by "age" property in ascending order.
  * Number of returned documents will be limited to 10.

## GET syntax ##

GET finds existing JSON documents.

```
/GET /_je/<docType>/<docId>
```

```
/GET /_je/<docType>[?<queryFilter>{&<queryFilter>}]
```

where `[`?`<`queryFilter`>``{`&`<`queryFilter`>``}``]` means you can write 0 or more `<`queryFilter`>` as query string on the URI. `<`queryFilter`>` is defined below.

```
<queryFilter> = <condFilter> | <sortFilter> | <limitFilter>

<condFilter> = 'cond=' <propertyName> '.' <comparator> '.' <propertyValue>
<comparator> = 'eq' | 'lt' | 'le' | 'gt' | 'ge' 
<propertyValue> = "<stringValue>" | '<stringValue>' | <stringValue> | <numericValue> | 'true' | 'false'

<sortFilter> = 'sort=' <propertyName> '.' <sortOrder>
<sortOrder> = 'asc' | 'desc'

<limitFilter> = 'limit=' <integer>
```

  * HTTP Request URI values
    * **docType** (required): docType (document type) of the doc.
    * **docId** (required): docID (document id) of the doc to be retrieved.

  * HTTP Request parameters
    * None.

  * HTTP Response
    * Status code 200: Returned the doc(s) successfully.
      * Response body: retrived doc(s).
    * Status code 403: Access not authorized (see [HowToAdmin](HowToAdmin.md) for access control setting).
    * Status code 404: If there is no such doc stored in jsonengine.
    * Status code 500: Caught an unexpected system error.

## Notes ##

  * Multiple `<`condFilter`>`s will be combined with **logical AND** operation.
  * jsonengine checks if `<`propertyValue`>` can be converted to a numeric value at first. So if you want to pass a numeric value (such as "123") as a String, you need to wrap it with double or single quotations. e.g. cond=name.eq."123"
  * If you use only **"eq" comparator** in `<`condFilter`>`s, you can specify any number of `<`condFilter`>`s. e.g. cond=age.eq.10&cond=name.eq.Foo&cond=id.eq."001"&...
  * If you use either **`<`sortFilter`>`** or **inequality comparators** (lt, le, gt, ge), you can not specify more than one kind of `<`propertyName`>` in a query URI.
    * OK: cond=**age**.ge.20&cond=**age**.lt.30
    * Error: cond=**age**.ge.20&cond=**name**.eq.Foo
    * OK: cond=**age**.ge.20&sort=**age**.asc
    * Error: cond=**age**.ge.20&sort=**name**.asc
  * The parameter values should be URL encoded.