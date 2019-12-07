<script type="text/javascript">
<#assign personFactory = delegator.findList('PersonFactoryFact', null, null, null, null, false) />
<#assign emplList = Static['com.olbius.basehr.util.PartyUtil'].getEmplInOrgAtPeriod(delegator, userLogin.userLoginId) />

var listPersonFactory = [
     <#if emplList?exists>
	     <#list emplList as pF>
			{
				partyId : "${pF.partyId?if_exists}",
				fullName : "${pF.fullName?if_exists}"
			},
	     </#list>  
     </#if>
 ];

<#if !rootOrgList?exists>
	<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
var globalVar = {
		rootPartyArr: [
   			<#if rootOrgList?has_content>
   				<#list rootOrgList as rootOrgId>
   				<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
   				{
   					partyId: "${rootOrgId}",
   					partyName: "${rootOrg.groupName}"
   				},
   				</#list>
   			</#if>
   		],
};
</script>