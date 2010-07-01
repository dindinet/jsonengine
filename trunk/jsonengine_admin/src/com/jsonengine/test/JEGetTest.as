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
			var uri:String = "/_je/test/" + AllTests.resultUser1._docId;
			NetManager.i.sendReq(uri, params, "GET", async(function(result:Object):void {
				var resultObj:Object = JSON.decode(String(result));
				assertTrue(AllTests.compareUsers(AllTests.betty, resultObj), "all props should have the same values");				
				trace("GET success");
			}));
		}
		
		test function testGet2():void {
			var params:URLVariables = new URLVariables();
			var uri:String = "/_je/test/" + AllTests.resultUser2._docId;
			NetManager.i.sendReq(uri, params, "GET", async(function(result:Object):void {
				var resultObj:Object = JSON.decode(String(result));
				assertTrue(AllTests.compareUsers(AllTests.betty, resultObj), "all props should have the same values");
				assertEquals(3, resultObj.friends.length, "should handle the multiple prop values as an Array");
				trace("GET success");
			}));
		}
	}
}