package com.jsonengine.test
{
	import com.adobe.serialization.json.JSON;
	import com.jsonengine.net.NetManager;
	
	import flash.net.URLVariables;
	
	import org.libspark.as3unit.assert.assertEquals;
	import org.libspark.as3unit.assert.async;
	import org.libspark.as3unit.test;
	
	use namespace org.libspark.as3unit.test;

	/**
	 * Tests updating the stored users via POST method.
	 */
	public class JEUpdateTest
	{		
		
		private static const NEWAGE:int = 26;
		
		test function testJsonStylePost():void {
			
			// update the age
			AllTests.resultUser1.age = NEWAGE;
			
			// encode it into JSON
			var params:URLVariables = new URLVariables();
			params._doc = JSON.encode(AllTests.resultUser1);
			
			// put it to server
			NetManager.i.sendReq("/_je/test", params, "POST", async(function(result:Object):void {
				var resultObj:Object = JSON.decode(String(result));
				assertEquals(AllTests.resultUser1.age, resultObj.age, "age should be updated");
			}));
		}
		
		test function testFormStylePost():void {
			
			// update the age
			var params:URLVariables = AllTests.getBettyAsParams();
			params.age = NEWAGE;
			params._docId = AllTests.resultUser2._docId;
			
			// put it to server
			NetManager.i.sendReq("/_je/test", params, "POST", async(function(result:Object):void {
				var resultObj:Object = JSON.decode(String(result));
				assertEquals(NEWAGE, resultObj.age, "age should be updated");
			}));
		}
	}
}