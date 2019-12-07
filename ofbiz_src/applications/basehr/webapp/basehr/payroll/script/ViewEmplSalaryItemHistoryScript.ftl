<@jqGridMinimumLib/>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign startDate = Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp) />
<script type="text/javascript">
var yearCustomTimePeriod = [
	<#if customTimePeriodYear?has_content>
		<#list customTimePeriodYear as customTimePeriod>
			{
				customTimePeriodId: "${customTimePeriod.customTimePeriodId}",
				periodTypeId: "${customTimePeriod.periodTypeId}",
				periodName: "${StringUtil.wrapString(customTimePeriod.periodName)}",
				fromDate: ${customTimePeriod.fromDate.getTime()},
				thruDate: ${customTimePeriod.thruDate.getTime()}
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
		<#if selectYearCustomTimePeriodId?exists>
			selectYearCustomTimePeriodId: "${selectYearCustomTimePeriodId}",
		</#if>
		startDate: ${startDate.getTime()}
};

var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

var uiLabelMap = {
		NoEmployeeChoose_createInvoice: "${StringUtil.wrapString(uiLabelMap.NoEmployeeChoose_createInvoice)}?",
		CommonClose: "${uiLabelMap.CommonClose}",
		CreateInvoiceForPartyConfirm: "${StringUtil.wrapString(uiLabelMap.CreateInvoiceForPartyConfirm)}",
		CommonSubmit: "${uiLabelMap.CommonSubmit}",
		CommonCancel: "${uiLabelMap.CommonCancel}"
}
</script>