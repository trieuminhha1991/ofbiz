<@jqGridMinimumLib/>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>

<script type="text/javascript">
if(typeof(globalVar) == 'undefined'){
	globalVar = {};
}
globalVar.terminationReasonArr = [
    <#if terminationReasonList?exists>
    <#list terminationReasonList as termination>
		{
			terminationReasonId: "${termination.terminationReasonId}",
			description: "${StringUtil.wrapString(termination.description?if_exists)}"
		},
	</#list>
	</#if>
];

<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>

globalVar.statusArr = [
	<#if workingStatusList?exists>
	<#list workingStatusList as status>
		{
			statusId: "${status.statusId}",
			description: "${StringUtil.wrapString(status.description?if_exists)}"
		},
	</#list>
	</#if>             
];
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>
globalVar.monthStart = ${monthStart.getTime()};
globalVar.monthEnd = ${monthEnd.getTime()};
globalVar.rootPartyArr = [
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
		createJqxTreeDropDownBtn:createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

</script>