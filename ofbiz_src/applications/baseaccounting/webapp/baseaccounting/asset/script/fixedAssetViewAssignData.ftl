<script>
	<#assign listFixedAssetTypes = delegator.findByAnd("FixedAssetType", null, null, false)>
	var fixedAssetTypeData = [
      <#if listFixedAssetTypes?exists>
      	<#list listFixedAssetTypes as fixedAssetType>
      		{
      			fixedAssetTypeId : "${fixedAssetType.fixedAssetTypeId}",
      			description : "${StringUtil.wrapString(fixedAssetType.get('description'))}",
  			},
  		</#list>
  	  </#if>
	];
	
	<#assign listStatusItems = delegator.findByAnd("StatusItem", {"statusTypeId" : "FA_ASGN_STATUS"}, Static["org.ofbiz.base.util.UtilMisc"].toList("statusTypeId DESC"), false)>
	var statusData = [
      <#if listStatusItems?exists>
      	<#list listStatusItems as item>
      		{
      			statusId : "${item.statusId}",
      			description : "${StringUtil.wrapString(item.get('description', locale))}",
  			},
  		</#list>
  	  </#if>
	];
	
	<#assign listRoleTypes = delegator.findByAnd("RoleType", null, null, false)>
	var roleTypeData = [
      <#if listRoleTypes?exists>
      	<#list listRoleTypes as role>
      		{
      			roleTypeId : "${role.roleTypeId}",
      			description : "${StringUtil.wrapString(role.get('description', locale))}",
  			},
  		</#list>
  	  </#if>
	];
	
	function getRoleType(roleTypeId){
		var roleType = {};
		for(var i = 0; i < roleTypeData.length; i++){
			if(roleTypeData[i].roleTypeId == roleTypeId){
				roleType['roleTypeId'] = roleTypeData[i].roleTypeId;
				roleType['description'] = roleTypeData[i].description;
			}
		}
		return roleType;
	}
	
	
	<#if !rootOrgList?exists>
		<#assign rootOrgList = [Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)]/>
	</#if>
	
	var globalObject = (function(){
		<#assign defaultSuffix = ""/>
		${setContextField("defaultSuffix", defaultSuffix)}
		<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
		return{
			createJqxTreeDropDownBtn:createJqxTreeDropDownBtn${defaultSuffix?if_exists}
		}
	}());
	var globalVar = {};
	globalVar.rootPartyArr = [
		<#if rootOrgList?has_content>
			<#list rootOrgList as rootOrgId>
			<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
			{
				partyId: "${rootOrgId}",
				partyName: "${StringUtil.wrapString(rootOrg.groupName)}"
			},
			</#list>
		</#if>
	]
</script>