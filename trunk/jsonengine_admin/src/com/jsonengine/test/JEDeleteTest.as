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
	public class JEDeleteTest
	{		
		test function testDelete():void {
			var params:URLVariables = new URLVariables();
			params["_delete"] = "true";
			var uri:String = "/_je/test/" + AllTests.resultUser1._docId;
			NetManager.i.sendReq(uri, params, "POST", async(function(result:Object):void {
				trace("DELETE success");
			}));
		}
	}
}