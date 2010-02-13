package com.jsonengine.test
{
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
			JEDeleteTest, 
			JEDeleteConfirmTest
		];
		
		public static var testObj:Object = { "foo" : "fooValue", "bar" : "barValue" };
	}
}