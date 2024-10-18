component extends="org.lucee.cfml.test.LuceeTestCase"  labels="mysql" {
	function isMySqlNotSupported() {
		var mySql = mySqlCredentials();
		if(isNull(mysql) || structCount(mySql)==0){
			return true;
		} else{
			return false;
		}
	}

	function run( testResults , testBox ) {
		describe( title="Test suite for LDEV-1953", body=function() {
			it( title='Checking mysql with zeroDateTimeBehavior as convertToNull',skip=isMySqlNotSupported(),body=function( currentSpec ) {
				var uri = createURI("LDEV1953");
				var result = _InternalRequest(
					template:"#uri#/ldev1953.cfm"
				);
				expect(result.fileContent.trim()).toBe("123");
			});
		});
	}

	// Private functions
	private string function createURI(string calledName){
		var baseURI = "/test/#listLast(getDirectoryFromPath(getCurrenttemplatepath()),"\/")#/";
		return baseURI & "" & calledName;
	}

	private struct function mySqlCredentials() {
		return server.getDatasource("mysql");
	}

}