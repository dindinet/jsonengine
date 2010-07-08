package com.jsonengine.test
{
	import com.jsonengine.net.NetManager;
	
	import flash.net.URLVariables;
	
	import org.libspark.as3unit.assert.async;
	import org.libspark.as3unit.test;
	
	use namespace org.libspark.as3unit.test;

	/**
	 * Cleaning up all the test data.
	 */
	public class JECleanUpTest
	{		
		test function testCleanUp():void {
			cleanUp("testQuery", function():void {
				cleanUp("test", function():void {});
			});
		}
		
		private function cleanUp(docType:String, handler:Function):void {
			var params:URLVariables = new URLVariables();
			params["_delete"] = "true";
			var uri:String = "/_je/" + docType + "/";
			NetManager.i.sendReq(uri, params, "POST", async(handler));			
		}
	}

}