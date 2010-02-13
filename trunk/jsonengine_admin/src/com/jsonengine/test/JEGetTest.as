package com.jsonengine.test
{
	import com.adobe.serialization.json.JSON;
	import com.jsonengine.net.NetManager;
	
	import flash.net.URLVariables;
	
	import org.libspark.as3unit.assert.assertEquals;
	import org.libspark.as3unit.assert.assertTrue;
	import org.libspark.as3unit.assert.async;
	import org.libspark.as3unit.test;
	
	use namespace org.libspark.as3unit.test;

	/**
	 * Tests GET method.
	 */
	public class JEGetTest
	{		
		test function testGet():void {
			var params:URLVariables = new URLVariables();
			var uri:String = "/_je/test/" + AllTests.testObj._docId;
			NetManager.i.sendReq(uri, params, "GET", async(function(result:Object):void {
				var resultObj:Object = JSON.decode(String(result));
				assertEquals(AllTests.testObj.foo, resultObj.foo);
				assertEquals(AllTests.testObj.bar, resultObj.bar);
				assertEquals(AllTests.testObj._docId, resultObj._docId);
				assertEquals(AllTests.testObj._updatedAt, resultObj._updatedAt);
			}));
		}
	}
}