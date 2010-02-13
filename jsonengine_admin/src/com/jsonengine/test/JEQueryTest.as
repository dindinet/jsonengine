package com.jsonengine.test
{
	import com.adobe.serialization.json.JSON;
	import com.jsonengine.net.NetManager;
	
	import flash.net.URLVariables;
	
	import org.libspark.as3unit.assert.async;
	import org.libspark.as3unit.test;
	
	use namespace org.libspark.as3unit.test;

	/**
	 * Tests query.
	 */
	public class JEQueryTest
	{		
		test function testQuery():void {
			var params:URLVariables = new URLVariables();
			var uri:String = "/_q/myDoc/cond.age.eq.10/sort.age.asc/limit.10";
			NetManager.i.sendReq(uri, params, "GET", async(function(result:Object):void {
				var resultObjs:Array = JSON.decode(String(result));
			}));
		}
	}
}