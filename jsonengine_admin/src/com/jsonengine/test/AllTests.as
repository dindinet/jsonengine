package com.jsonengine.test
{
	import flash.net.URLVariables;
	
	import org.libspark.as3unit.runners.Suite;
	
	/**
	 * Provides a list of all test
	 * 
	 * @private
	 */
	public class AllTests
	{
		public static const RunWith:Class = Suite;
		
		public static const SuiteClasses:Array = [
			JEPostTest,
			JEGetTest,
			JEUpdateTest,
			JEDeleteTest,
//			JEDeleteConfirmTest
			JEQueryTest
		];
		
		public static var resultUser1:Object;

		public static var resultUser2:Object;

	    public static var amanda:Object = {
	        "id" : "#004",
	        "name" : "Amanda Tannen Sommers",
	        "age" : 28,
	        "isMale" : false,
	        "weight": 1.2345
	    };
	        
	    public static var marc:Object = {
	        "id" : "#003",
	        "name" : "Marc St. James",
	        "age" : 30,
	        "isMale" : true,
	        "weight" : 12.345
	    };

	    public static var daniel:Object = {
	        "id" : "#002",
	        "name" : "Daniel Meade",
	        "age" : 35,
	        "isMale" : true,
	        "weight" : 123.45
	    };

	    public static var betty:Object = {
	        "id" : "#001",
	        "name" : "Betty Suarez",
	        "age" : 25,
	        "isMale" : false,
	        "weight" : 1234.5
	    };
	    
	    public static function getBettyAsParams():URLVariables {
	    	var params:URLVariables = new URLVariables();
	    	params.id = betty.id;
	    	params.name = betty.name;
	    	params.age = betty.age;
	    	params.isMale = betty.isMale;
	    	params.weight = betty.weight;
	    	params.friends = ["daniel", "marc", "amanda"]; // multiple params test
	    	return params;
	    }
	    
	    public static function compareUsers(u1:Object, u2:Object):Boolean {
	    	return u1.id == u2.id && u1.name == u2.name && u1.age == u2.age && u1.isMale == u2.isMale && u1.weight == u2.weight;
	    }

	}
}