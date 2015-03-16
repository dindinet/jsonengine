## What is jsonengine? ##

**jsonengine** is a simple and ultra-scalable JSON storage that works on Google App Engine. You do not need any server-side Java/Python coding. Just deploy a jsonengine to App Engine cloud as your application, and you can call the REST API from any HTTP client (JavaScript, Flash, iPhone/iPad/Android or etc) to get/put/search any kind of JSON documents you want to use.

  * [Japanese version 日本語ドキュメント](http://jxck.bitbucket.org/jsonengine-doc-ja/build/html/index.html)

## Features ##

  * No server-side Java/Python/SQL coding nor schema definition required.
  * Very easy to setup - just deploy jsonengine to App Engine and that's it.
  * Your client can access to the jsonengine via REST API to get/put/search any kind of JSON documents you use.
  * You can put your HTML contents to jsonengine to host it on App Engine.
  * No schema definition required, and each JSON doc may have a different schema.
  * Provides the various advantages of App Engine including extreme cost efficiency (it's free to start), limitless scalablity, ease of management, and high availability.
  * Supports Google account and OpenID authentication and access control on each JSON document. Provides admin console for setting the access control.
  * Supports detection of update conflict to provide reliable transactions.

  * Supports event-driven/streaming-based features like continuous query and pub/sub messaging (in future)

## Sample clients ##

Try ｔhe sample clients (simple BBS page) running on jsonengine with the URL below. Take a look on the HTML source to learn how simple it is to write a client for jsonengine.

  * [jQuery simple BBS sample](http://jsonengine.appspot.com/samples/bbs.html)
  * [jQuery complex BBS sample](http://jsonengine-sample.appspot.com/jsonengine-bbs/)
  * [jsonengine-note](http://jsonengine.appspot.com/samples/jsonengine-note/)
  * [Android BBS sample](http://code.google.com/p/jsonengine/downloads/detail?name=android_bbs_sample.apk&can=2&q=#makechanges) ([source](http://github.com/itog/JsonEngineSample))

### Production services based on jsonengine ###

  * [PinIt](https://chrome.google.com/extensions/detail/fmkklenggkaimfbahdenjjibgihlndcn?hl=ja)
    * A Chrome extension to put and share post-it memos anywhere on the web.

## Getting Started and other documents ##

  * [How to install jsonengine](HowToInstall.md)
  * [How to use jsonengine](HowToUse.md)
  * [How to use query on jsonengine](HowToUseQuery.md)
  * [How to admin jsonengine](HowToAdmin.md)
  * [Release notes](ReleaseNotes.md)

  * [Japanese version 日本語ドキュメント](http://jxck.bitbucket.org/jsonengine-doc-ja/build/html/index.html)

## Need support? Any feedback? ##

jsonengine developers are exchanging info on Twitter with hashtag #jsonengine . Tweet your feedbacks or questions with it and someone would reply to it.

  * [jsonengine on Twitter](http://twitter.com/#!/search/jsonengine)

And please also feel free to post (or star) your requirements for jsonengine here:

  * [Issues](http://code.google.com/p/jsonengine/issues/list)

## Acknowledgments ##

jsonengine is powered by the following technologies:

  * [Google App Engine](http://code.google.com/intl/en/appengine/)
  * [Slim3](http://www.slim3.org/)
  * [Kotori Web JUnit Runner](http://code.google.com/p/kotori/wiki/KotoriWebJUnitRunner?wl=en)
  * [jsonic](http://jsonic.sourceforge.jp/)
  * [AS3Unit](http://www.libspark.org/wiki/yossy/AS3Unit)
  * and others