package com.jsonengine.net
{
	import flash.net.URLVariables;
	
	import mx.controls.Alert;
	import mx.core.Application;
	import mx.managers.CursorManager;
	import mx.rpc.AsyncToken;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.http.HTTPService;
	import mx.utils.URLUtil;
	
	public class NetManager
	{
		private static const URL_HOST_DEV:String = "http://jsonengine.appspot.com";
//		private static const URL_HOST_DEV:String = "http://localhost:8888";
		
		[Bindable]
		public static var i:NetManager = new NetManager();
		
		private var httpService:HTTPService = new HTTPService();
		
		private var urlHost:String;
		
		[Bindable]
		public var isBusy:Boolean = false;
		
		public function init():void {
			
			// init HTTPService
		    httpService.useProxy = false;
			httpService.contentType = HTTPService.CONTENT_TYPE_FORM;
			httpService.resultFormat = HTTPService.RESULT_FORMAT_TEXT;
            httpService.addEventListener(ResultEvent.RESULT, onResult);
            httpService.addEventListener(FaultEvent.FAULT, onFault);
            
            // determine URL host
            var url:String = Application.application.url;
            if (url.match(/file:.*/)) {
            	urlHost = URL_HOST_DEV;
            } else {
            	urlHost = "http://" + URLUtil.getServerNameWithPort(url);
            }
		}
		
		/**
		 * Sends a http request to the server.
		 */
		public function sendReq(uri:String, params:URLVariables, method:String, handler:Function, 
			faultHandler:Function = null):void {
			
			// send query
			httpService.method = method;
			httpService.url = urlHost + uri;
            var token:AsyncToken = httpService.send(params)
            trace("sendReq: sending to: " + httpService.url);
            
            // attach the handler to the result
            token.handler = handler;
            token.faultHandler = faultHandler;
            isBusy = true;
            CursorManager.setBusyCursor();
		}
		
		private function onResult(e:ResultEvent):void {
			
			// log result
			trace("onResult: " + e.statusCode + ": " + e.message.body);
            isBusy = false;
            CursorManager.removeBusyCursor();
			
			// pass the result to the handler
            var handler:Function = e.token.handler;
            handler(e.result);
		}
			
		private function onFault(e:FaultEvent):void {
			trace("onFault: " + e);
            isBusy = false;
            CursorManager.removeBusyCursor();
            
			// pass the result to the handler
            var faultHandler:Function = e.token.faultHandler;
            if (faultHandler != null) faultHandler(e);
		}		

	}
}