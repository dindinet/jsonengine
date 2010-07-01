package com.jsonengine.test
{
	import com.jsonengine.net.NetManager;
	
	import flash.events.Event;
	import flash.net.URLVariables;
	
	import org.libspark.as3unit.assert.async;
	import org.libspark.as3unit.assert.fail;
	import org.libspark.as3unit.test;
	
	use namespace org.libspark.as3unit.test;

	/**
	 * Tests GET method.
	 */
	public class JEDeleteConfirmTest
	{
		test function testDeleteConfirm():void {
			var params:URLVariables = new URLVariables();
			var uri:String = "/_je/test/" + AllTests.resultUser1._docId;
			NetManager.i.sendReq(uri, params, "GET", function(result:Object):void {
					fail("This GET should be failed");
				}, async(function(event:Event):void {
					trace("DELETE confirmation success.");
				}));
		}
	}
}