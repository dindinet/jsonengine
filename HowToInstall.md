# How to install jsonengine #

The requirements for installing jsonengine are:

  * Windows, Mac or Linux PC with internet connection
  * Your Google account

jsonengine can be easily installed by the following steps, and you can then start using it instantly.  No server-side programming or complex configuration is required.

  1. Installing the Java JDK
  1. Installing the App Engine SDK for Java
  1. Downloading jsonengine package
  1. Running jsonengine with local dev server
  1. Running jsonengine on App Engine cloud

## Installing the Java JDK ##

jsonengine runs on Java 6. If you do not have installed Java 6 on your PC yet, download and install the Java SDK 6 from here:

  * [Java SDK for Windows and Linux user](http://java.sun.com/javase/downloads/index.jsp)
  * [Java SDK for Mac user](http://developer.apple.com/java/)

After installing the JDK, please check if the following commands can be run on the command prompt/shell and further, whether they display proper version numbers.

```
> java -version
```

```
> javac -version
```

## Installing the App Engine SDK for Java ##

Download the App Engine SDK for Java. jsonengine supports SDK version 1.3.0 or later.

  * [App Engine SDK for Java](http://code.google.com/intl/en/appengine/downloads.html#Google_App_Engine_SDK_for_Java)

After downloading the file, unpack the archive in a convenient location on your disk. It will create a directory named "appengine-java-sdk-x.x.x". In the following document, the directory will be refered as _appengine-java-sdk-dir_.

## Downloading jsonengine package ##

Download the latest jsonengine package from the download page.

  * [jsonengine downloads](http://code.google.com/p/jsonengine/downloads/list)

Once you download the file, unpack the archive in a convenient location on your disk. It will create a directory named "jsonengine". In the following, the directory will be refered as _jsonengine-dir_.

## Running jsonengine on local dev server ##

Now you can run jsonengine on a local dev server. You can use the dev server for debugging it without deploying it to the App Engine cloud.

  * _jsonengine-dir\war_ is the document root of your application. Copy your HTML contents into this directory. If you just want to test jsonengine sample pages or your client is hosted outside (e.g. iPhone/iPad/Android app), you may skip this step.
  * change current directory to _appengine-java-sdk-dir_/bin
  * run dev\_appserver tool to run a dev server

On Windows command prompt:
```
> cd appengine-java-sdk-dir\bin
> dev_appserver.cmd jsonengine-dir\war
```

On Mac/Linux shell:
```
> cd appengine-java-sdk-dir/bin
> ./dev_appserver.sh jsonengine-dir/war
```

Now your jsonengine is running on the dev server. You can open the following URL to check if your jsonengine is working properly.

  * http://localhost:8080/samples/bbs.html
  * Note: this sample code does not work on the dev server now. Please replace all "messageBody`_`" to "messageBody" in /samples/bbs.html to solve this problem. Or you can just skip this process and test it on the App Engine cloud.

## Running jsonengine on App Engine cloud ##

After you test the jsonengine to determine whether it is working properly on the local server, you may upload it to the App Engine cloud with the following procedure.

### Registering your App Engine application ID ###

jsonengine will be running as an App Engine application that is registered to your Google account. If you have not used App Engine before, click the following link and sign up to App Engine with your Google account.

  * https://appengine.google.com/

Once you have signed up with App Engine, create an application for jsonengine with the follwing steps:

  * Click the "Create an Application" button on "Applications Overview" page
  * Type your favorite Application Identifier (app id) and Application Title for the application. For example, if you name it as "my-appid", your jsonengine will run on "http://my-appid.appspot.com". In the following, the app id will be refered as _my-appid_.
  * Click the "Check Availability" button to check if the app id is unique
  * Click the "Save" button and now your app id is registered

### Uploading jsonengine (and your HTML client) to App Engine ###

Now you can upload the jsonengine to App Engine. You may also upload your HTML content and client code (JS or Flash) to App Engine, too.

  * Open _jsonengine-dir_/war/WEB-INF/appengine-web.xml file with an editor, and replace the existing app id "jsonengine" with your app id "my-appid" in the application element:

```
<application>my-appid</application>
```

  * change current directory to _appengine-java-sdk-dir_/bin
  * run appcfg tool to upload jsonengine

On Windows command prompt:
```
> cd appengine-java-sdk-dir\bin
> appcfg.cmd update jsonengine-dir\war
```

On Mac/Linux shell:
```
> cd appengine-java-sdk-dir/bin
> ./appcfg.sh update jsonengine-dir/war
```

  * Enter your Google username and password at the prompts. Once the updating has finished successfully, it will show a message: "Update completed successfully."

Open the following URL to check if your jsonengine is working on App Engine cloud properly.

  * http://my-appid.appspot.com/samples/bbs.html

And also check the following page to use the admin page for security configuration and unit testing of jsonengine.

  * [How to admin jsonengine](HowToAdmin.md)