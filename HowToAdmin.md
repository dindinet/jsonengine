# How to admin jsonengine #

Once you have installed your jsonengine, you can access the jsonengine admin page with the following URL:

  * http://my-appid.appspot.com/_admin/

At the first time of accessing to the page, you will see the login page. Check "Sign in as Administrator" checkbox and press "Log In" button. You will see the admin page.

On the admin page, you can use the tabs described below.

## Security tab ##

On the Security tab, you can add, edit or delete an access level setting for each docType. The setting includes both "Access Level for Read" and "Access Level for Write" for each docType. The "Read" means the get and the query access and the "Write" means the put and the delete access.

http://img.f.hatena.ne.jp/images/fotolife/k/kazunori_279/20100708/20100708221407.png?1278594870

For both read and write operations, you can choose one of the following access levels:

| Access Level | Who can read? (get and query) | Who can write? (put and delete) |
|:-------------|:------------------------------|:--------------------------------|
| public |  Anyone | Anyone |
| protected | User | User |
| private | Owner | User can put a new Doc, Owner can update or delete a Doc |
| admin | Administrator | Administrator |

Roles:

  * Anyone: any user who accesses to jsonengine without authentication.
  * User: A user authenticated by either Google account or Open ID.
  * Owner: The owner of the Doc.
  * Administrator: The administrator of your appid.

Notes:

  * A query on "private" docType will return only the Docs owned by the requesting user.
  * The "public" docType can be accessed by anyone on the internet. It is strongly recommended to not use the "public" level on write operations as it would possibly allow anyone to store their data, without any limit, on your jsonengine.

## Test tab ##

On the Test tab, you can start a series of unit tests.

By pressing the "Start Testing on the cloud" button, your browser will show a "Kotori Web JUnit Runner" page below. Click the "Check All" button and then click the "Run" button to start the testing. The script inside the jsonengine on the cloud will issue a series of test requests to itself and check if the results are correct. If a green bar appears, it indicates the jsonengine is working properly.

http://img.f.hatena.ne.jp/images/fotolife/k/kazunori_279/20100708/20100708223045.png?1278595938