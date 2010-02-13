package com.jsonengine.test
{
	import com.adobe.serialization.json.JSON;
	import com.jsonengine.net.NetManager;
	
	import flash.net.URLVariables;
	
	import org.libspark.as3unit.assert.assertEquals;
	import org.libspark.as3unit.assert.assertNotNull;
	import org.libspark.as3unit.assert.async;
	import org.libspark.as3unit.test;
	
	use namespace org.libspark.as3unit.test;

	/**
	 * Tests POST method.
	 */
	public class JEPostTest
	{		
		test function testPost():void {
			
			// encode it into JSON
			var params:URLVariables = new URLVariables();
			params.doc = JSON.encode(AllTests.testObj);
			
			// put it to server
			NetManager.i.sendReq("/_je/test", params, "POST", async(function(result:Object):void {
				var resultObj:Object = JSON.decode(String(result));
				assertEquals(AllTests.testObj.foo, resultObj.foo);
				assertEquals(AllTests.testObj.bar, resultObj.bar);
				assertNotNull(resultObj._docId, "_docId should be included");
				assertNotNull(resultObj._updatedAt, "_updatedAt should be included");
				AllTests.testObj = resultObj;
			}));
		}
	}
}