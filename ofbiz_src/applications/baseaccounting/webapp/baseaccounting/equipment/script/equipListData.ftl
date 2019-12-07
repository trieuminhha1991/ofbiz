<script>
	<#assign listEquipmentTypes = delegator.findByAnd("EquipmentType", null, null, false)>
	var equipmentTypeData = [
	  <#if listEquipmentTypes?exists>
	  	<#list listEquipmentTypes as equipmentType>
	  		{
	  			equipmentTypeId : "${equipmentType.equipmentTypeId}",
	  			description : "${StringUtil.wrapString(equipmentType.get('description'))}",
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
	<#assign listUoms = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, Static["org.ofbiz.base.util.UtilMisc"].toList("uomId DESC"), false)>
	var uomData = [
       <#if listUoms?exists>
       	<#list listUoms as uom>
	       	<#if uom.uomId == "EUR" || uom.uomId == "VND" || uom.uomId == "USD">
	       		{
	       			uomId : "${uom.uomId}",
	       			description : "${StringUtil.wrapString(uom.get('description'))}",
	   			},
	   		</#if>	
   		</#list>
   	  </#if>
 	];
	
	<#assign listQuanUoms = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, Static["org.ofbiz.base.util.UtilMisc"].toList("uomId DESC"), false)>
	var quanUomData = [
       <#if listQuanUoms?exists>
       	<#list listQuanUoms as uom>
       		{
       			uomId : "${uom.uomId}",
       			description : "${StringUtil.wrapString(uom.get('description'))}",
   			},
   		</#list>
   	  </#if>
 	];
	
	<#assign listStatusItems = delegator.findByAnd("StatusItem", {"statusTypeId" : "FIXEDASSET_STATUS"}, Static["org.ofbiz.base.util.UtilMisc"].toList("statusTypeId DESC"), false)>
	var statusData = [
      <#if listStatusItems?exists>
      	<#list listStatusItems as item>
      		{
      			statusId : "${item.statusId}",
      			description : "${StringUtil.wrapString(item.get('description'))}",
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