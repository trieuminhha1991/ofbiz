<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
var globalVar = {};
var uiLableMap = {};
<#if !rootOrgList?exists>
	<#if Static["com.olbius.basehr.util.PartyUtil"].isFullPermissionView(delegator, userLogin.userLoginId)>
		<#assign rootOrgList = Static["com.olbius.basehr.util.SecurityUtil"].getPartiesByRolesWithCurrentOrg(userLogin, Static["com.olbius.basehr.util.PropertiesUtil"].SALES_DEPT_ROLE, delegator)/>
	<#else>
		<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
	</#if>
</#if>

globalVar.customTimePeriodArr = [
	<#if customTimePeriodList?has_content>
		<#list customTimePeriodList as customTimePeriod>
		{
			customTimePeriodId: '${customTimePeriod.customTimePeriodId}',
			periodName: '${StringUtil.wrapString(customTimePeriod.periodName)}',
			fromDate: ${customTimePeriod.fromDate.getTime()},
			thruDate: ${customTimePeriod.thruDate.getTime()}
		},
		</#list>
	</#if>
];
globalVar.recruitmentTypeEnumArr = [
	<#if recruitmentTypeEnumList?has_content>
		<#list recruitmentTypeEnumList as recruitmentTypeEnum>
		{
			enumId: "${recruitmentTypeEnum.enumId}",
			description: '${StringUtil.wrapString(recruitmentTypeEnum.description?if_exists)}'
		},
		</#list>
	</#if>
];
globalVar.genderList = [
     	<#if genderList?has_content>                   
	  		<#list genderList as gender1>
	  		{
	  			genderId : "${gender1.genderId}",
	  			description : "${StringUtil.wrapString(gender1.description)}"
	  		},
	  		</#list>	
	  	</#if>
];
globalVar.rootPartyArr =  [
	<#if rootOrgList?has_content>
		<#list rootOrgList as rootOrgId>
		<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
		{
			partyId: "${rootOrgId}",
			partyName: "${rootOrg.groupName}"
		},
		</#list>
	</#if>
];
var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());
</script>