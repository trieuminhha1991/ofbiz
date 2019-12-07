<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
			<form name="CreateMgrForOrg" id="CreateMgrForOrg" method="post" 
				action="<@ofbizUrl>CreateMgrForOrg</@ofbizUrl>">
				<div class="step-pane" id="step3">
					<input type="hidden" name="orgId" value="${parameters.orgId?if_exists}">
					<#assign param = ["orgId"]>
						<table>
							<tr>
								<td><label class="padding-bottom5 padding-right15" for="roleTypeId">
									${uiLabelMap.HrolbiusGroupName}</label>
								</td>
   								<td>
   									<#if parameters.orgId?exists>
   										<#assign orgName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, parameters.orgId, false)>
   										 ${orgName} [${parameters.orgId?if_exists}]
   									</#if>
    								
   								</td>
							</tr>
							<tr>
								<td><label class="padding-bottom5 padding-right15" for="roleTypeId">${uiLabelMap.Title}</label></td>
    								<td>
     									<select name="roleTypeId" id="roleTypeId">
     										<#list roleList as role>
     											<option value="${role.roleTypeId}">${role.description}</option>
     										</#list>
     									</select>
    								</td>
							</tr>
							<tr>
								<td><label class="padding-bottom5 padding-right15" for="managerId">
										${uiLabelMap.Manager}</label></td>
     								<td>
     								
     									<@htmlTemplate.lookupField formName="CreateMgrForOrg" name="managerId" 
     										id="managerId" fieldFormName="LookupPerson" targetParameterIter="${parametersIter}"/>
									<span class="tooltipob">${uiLabelMap.required}</span>
     								</td>
							</tr>
							<tr>
								<td><label class="padding-bottom5 padding-right15" for="title">${uiLabelMap.Title}</label></td>
     								<td>
      									<select name="title" id="title">
      										<#list titles as title>
      											<option value="${title.roleTypeId}">${title.description}</option>
      										</#list>
      									</select>
     								</td>
							</tr>
							<tr>
								<td></td>
								<td>
									<button type="submit" name="${uiLabelMap.CommonSubmit}" class="btn btn-primary btn-small" >
										${uiLabelMap.CommonSubmit}
									</button>
								</td>
							</tr>
								
						</table>
					</div> 
			</form>
		</div>
	</div>
</div>		