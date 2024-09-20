<cfset error.message="">
<cfset error.detail="">

<cfadmin 
	action="getSecurity"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="security">

<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="hasAccess"
	secType="setting"
	secValue="yes">
	

<!--- 
Defaults --->
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">

<cfif hasAccess>
	<cftry>
		<cfswitch expression="#form.mainAction#">
		<!--- UPDATE --->
			<cfcase value="#stText.Buttons.Update#">
				
				<cfadmin 
					action="updateSecurity"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#"
					limitEvaluation="#form.limitEvaluation?:false#"
					varUsage="#form.varUsage#"
					remoteClients="#request.getRemoteClients()#">
			
			</cfcase>
			<!--- reset to server setting --->
			<cfcase value="#stText.Buttons.resetServerAdmin#">
				
				<cfadmin 
					action="updateSecurity"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#"
					limitEvaluation=""
					varUsage=""
					remoteClients="#request.getRemoteClients()#">
			
			</cfcase>
		</cfswitch>
		<cfcatch>
			<cfset error.message=cfcatch.message>
			<cfset error.detail=cfcatch.Detail>
			<cfset error.cfcatch=cfcatch>
		</cfcatch>
	</cftry>
</cfif>

<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "">
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>

<!--- 
Error Output --->
<cfset printError(error)>
<cfscript>
	stText.security.desc="All settings that concern security in Lucee.";
	stText.security.varUsage="Variable Usage in Queries";
	stText.security.varUsageDesc="With this setting, you can control how Lucee handles variables used within queries.";

	stText.security.varUsageIgnore="Allow variables within a query";
	stText.security.varUsageWarn="Add a warning to debug output";
	stText.security.varUsageError="Throw an exception";
</cfscript>
<cfoutput>
	<cfif not hasAccess>
		<cfset noAccess(stText.setting.noAccess)>
	</cfif>
	
	<div class="pageintro">#stText.security.desc#</div>
	<cfformClassic onerror="customError" action="#request.self#?action=#url.action#" method="post">
		<table class="maintbl">
			<tbody>
				
				<!--- Variable Usage in Queries --->
				<tr>
					<th scope="row">#stText.security.varUsage#</th>
					<td>
						<cfif hasAccess>
							<select name="varUsage">
								<cfloop list="ignore,warn,error" item="type">
									<option <cfif type EQ security.varusage> selected="selected"</cfif> value="#type#">#stText.security["varUsage"&type]#</option>
								</cfloop>
							</select>
						<cfelse>
							<input type="hidden" name="varUsage" value="#security.varusage#">
							<b>#security.varusage#</b>
						</cfif>
						<div class="comment">#stText.security.varUsageDesc#</div>
						<cfsavecontent variable="codeSample">
							this.security.variableUsage="#security.varusage#";
						</cfsavecontent>
						<cfset renderCodingTip( codeSample)>
					</td>
				</tr>
				<cfscript>

					stText.security.limitEvaluation="Limit variable evaluation in functions/tags";
					stText.security.limitEvaluationDesc="If enable you cannot use expression within ""[ ]"" like this susi[getVariableName()] . 
					This affects the following functions [IsDefined, structGet, empty] and the following tags [savecontent attribute ""variable""].";
				
				</cfscript>
				<!--- limit function isDefined --->
				<tr>
					<th scope="row">#stText.security.limitEvaluation#</th>
					<td>
						<cfif hasAccess>
							<input type="checkbox" class="checkbox" <cfif (security.limitEvaluation?:true)> checked="checked"</cfif> name="limitEvaluation" value="true" />
						<cfelse>
							<input type="hidden" name="limitEvaluation" value="#security.limitEvaluation?:true#">
							<b>#yesNoFormat(security.limitEvaluation)#</b>
						</cfif>
						<div class="comment">#stText.security.limitEvaluationDesc#</div>
						<cfsavecontent variable="codeSample">
							this.security.limitEvaluation=#security.limitEvaluation?:true#;
						</cfsavecontent>
						<cfset renderCodingTip( codeSample)>
						<cfset renderSysPropEnvVar( "lucee.security.limitEvaluation",security.limitEvaluation?:true)>
					</td>
				</tr>

			</tbody>
		
			<cfif hasAccess>
				<tfoot>
					<tr>
						<td colspan="2">
							<input class="bl button submit" type="submit" name="mainAction" value="#stText.Buttons.Update#">
							<input class="br button reset" type="reset" name="cancel" value="#stText.Buttons.Cancel#">
						</td>
					</tr>
				</tfoot>
			</cfif>
		</table>
	</cfformClassic>
</cfoutput>
