<cfcomponent extends="org.lucee.cfml.test.LuceeTestCase" labels="s3" skip="true">
	<cfscript>
		// skip closure
		function isNotSupported() {
			variables.s3Details=getCredentials();
			if(structIsEmpty(s3Details)) return true;
			if(!isNull(variables.s3Details.ACCESS_KEY_ID) && !isNull(variables.s3Details.SECRET_KEY)) {
				variables.supported = true;
			}
			else
				variables.supported = false;
			return !variables.supported;
		}

		function beforeAll() skip="isNotSupported"{
			if(isNotSupported()) return;
			s3Details = getCredentials();
			bucketName = lcase( s3Details.bucket_prefix & "1489-#hash(CreateGUID())#");
			base = "s3://#s3Details.ACCESS_KEY_ID#:#s3Details.SECRET_KEY#@";
			variables.baseWithBucketName = "s3://#s3Details.ACCESS_KEY_ID#:#s3Details.SECRET_KEY#@/#bucketName#";
			// for skipping rest of the cases, if error occurred.
			hasError = false;
			// for replacing s3 access keys from error msgs
			regEx = "\[[a-zA-Z0-9\:\/\@]+\]";
		}

		function afterAll() skip="isNotSupported"{
			if(isNotSupported()) return;
			 if( directoryExists(baseWithBucketName) )
			 	directoryDelete(baseWithBucketName, true);
		}

		public function run( testResults , testBox ) {
			describe( title="Test suite for LDEV-1489 ( checking s3 file operations )", body=function() {
				it(title="Creating a new s3 bucket", skip=isNotSupported(), body=function( currentSpec ) {
					if(isNotSupported()) return;
					if( directoryExists(baseWithBucketName))
						directoryDelete(baseWithBucketName, true);
					directoryCreate(baseWithBucketName);
				});

				it(title="checking ACL permission, default set in application.cfc", skip=isNotSupported(), body=function( currentSpec ){
					uri = createURI('LDEV1489')
					local.result = _InternalRequest(
						template:"#uri#/test.cfm",
						url: {
							bucketName: bucketName
						}
					);
					expect(listSort(local.result.filecontent,"textnocase","asc","|")).toBe('READ|WRITE');
				});

				it(title="checking cffile, with attribute storeAcl = 'private' ", skip=isNotSupported(), body=function( currentSpec ){
					cffile (action="write", file=baseWithBucketName & "/teskt.txt", output="Sample s3 text", storeAcl="private");
					var acl = StoreGetACL( baseWithBucketName & "/teskt.txt" );
					removeFullControl(acl);
					expect(arrayisEmpty(acl)).toBe(true);
				});

				it(title="checking cffile, with attribute storeAcl = 'public-read' ", skip=isNotSupported(), body=function( currentSpec ){
					cffile (action="write", file=baseWithBucketName & "/test2.txt", output="Sample s3 text", storeAcl="public-read");
					var acl = StoreGetACL( baseWithBucketName & "/test2.txt" );
					removeFullControl(acl);
					expect(acl[1].permission).toBe('read');
				});

				it(title="checking cffile, with attribute storeAcl = 'public-read-write' ", skip=isNotSupported(), body=function( currentSpec ){
					cffile (action="write", file=baseWithBucketName & "/test3.txt", output="Sample s3 text", storeAcl="public-read-write");
					var acl = StoreGetACL( baseWithBucketName & "/test3.txt" );
					removeFullControl(acl);
					var result = acl[1].permission & "|" & acl[2].permission;
					expect(listSort(result,"textnocase","asc","|")).toBe('READ|WRITE');
				});

				it(title="checking cffile, with attribute storeAcl value as aclObject (an array of struct where struct represents an ACL grant)", skip=isNotSupported(), body=function( currentSpec ){
					arr=[{'group':"all",'permission':"read"}];
					cffile (action="write", file=baseWithBucketName & "/test5.txt", output="Sample s3 text", storeAcl="#arr#");
					var acl = StoreGetACL( baseWithBucketName & "/test5.txt" );
					removeFullControl(acl);
					expect(acl[1].permission).toBe("read");
				});

				it(title="checking ACL default permission, without 'storeAcl' attribute", skip=isNotSupported(), body=function( currentSpec ){
					cffile (action="write", file=baseWithBucketName & "/test6.txt", output="Sample s3 text");
					var acl = StoreGetACL( baseWithBucketName & "/test6.txt" );
					removeFullControl(acl);

					if(isNewS3())expect(len(acl)).toBe(0);
					else expect(acl[1].permission).toBe('READ');
				});


			});
		}

		private function removeFullControl(acl) {
			local.index=0;
			loop array=acl index="local.i" item="local.el" {
				if(el.permission=="FULL_CONTROL")
					local.index=i;
			}
			if(index gt 0) ArrayDeleteAt( acl, index );
		}

		// Private functions
		private struct function getCredentials() {
			return server.getTestService("s3");
		}


		private string function createURI(string calledName){
			var baseURI="/test/#listLast(getDirectoryFromPath(getCurrenttemplatepath()),"\/")#/";
			return baseURI&""&calledName;
		}

		private function isNewS3(){
			qry=  extensionlist(false);
			loop query=qry {
				if(qry.id=="17AB52DE-B300-A94B-E058BD978511E39E") {
					if(left(qry.version,1)>=2) return true;
				}
			}
			return false;
		}
	</cfscript>
</cfcomponent>

