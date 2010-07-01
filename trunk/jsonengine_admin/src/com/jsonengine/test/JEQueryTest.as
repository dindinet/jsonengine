package com.jsonengine.test
{
	import com.adobe.serialization.json.JSON;
	import com.jsonengine.net.NetManager;
	
	import flash.net.URLVariables;
	
	import mx.controls.Alert;
	
	import org.libspark.as3unit.assert.async;
	import org.libspark.as3unit.test;
	
	use namespace org.libspark.as3unit.test;

	/**
	 * Tests query.
	 */
	public class JEQueryTest
	{				
		test function postBetty():void {
			var params:URLVariables = new URLVariables();
			params._doc = JSON.encode(AllTests.betty);
			NetManager.i.sendReq("/_je/testQuery", params, "POST", async(postAmanda));
		}
		
		private function postAmanda(result:Object):void {
			var params:URLVariables = new URLVariables();
			params._doc = JSON.encode(AllTests.amanda);
			NetManager.i.sendReq("/_je/testQuery", params, "POST", async(postDaniel));
		}
		
		private function postDaniel(result:Object):void {
			var params:URLVariables = new URLVariables();
			params._doc = JSON.encode(AllTests.daniel);
			NetManager.i.sendReq("/_je/testQuery", params, "POST", async(postMarc));
		}
		
		private function postMarc(result:Object):void {
			var params:URLVariables = new URLVariables();
			params._doc = JSON.encode(AllTests.marc);
			NetManager.i.sendReq("/_je/testQuery", params, "POST", async(testQuery));
		}
		
		private function testQuery(result:Object):void {
			var params:URLVariables = new URLVariables();
			NetManager.i.sendReq("/_q/testQuery", params, "GET", async(veryfyQuery));
		}
		
		private function veryfyQuery(result:Object):void {
			Alert.show(String(result));
			var resultObjs:Array = JSON.decode(String(result));			
		}
	}
}