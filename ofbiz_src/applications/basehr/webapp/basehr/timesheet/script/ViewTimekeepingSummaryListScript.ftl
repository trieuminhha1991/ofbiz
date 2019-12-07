<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
var globalVar = {};
var uiLabelMap = {};
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

uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.EmplTimesheetList = "${StringUtil.wrapString(uiLabelMap.EmplTimesheetList)}";
uiLabelMap.CommonFromLowercase = "${StringUtil.wrapString(uiLabelMap.CommonFromLowercase)}";
uiLabelMap.CommonToLowercase = "${StringUtil.wrapString(uiLabelMap.CommonToLowercase)}";
uiLabelMap.TimekeepingDetailName = "${StringUtil.wrapString(uiLabelMap.TimekeepingDetailName)}";
uiLabelMap.SummaryFromTimekeepingDetail = "${StringUtil.wrapString(uiLabelMap.SummaryFromTimekeepingDetail)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.DateNotValid = "${StringUtil.wrapString(uiLabelMap.DateNotValid)}";
uiLabelMap.ValueMustGreaterOrEqualThanFromDate = "${StringUtil.wrapString(uiLabelMap.ValueMustGreaterOrEqualThanFromDate)}";
uiLabelMap.TimekeepingDetailIsNotSelected = "${StringUtil.wrapString(uiLabelMap.TimekeepingDetailIsNotSelected)}";
uiLabelMap.TimekeepingDetailIsNotCreated = "${StringUtil.wrapString(uiLabelMap.TimekeepingDetailIsNotCreated)}";
uiLabelMap.ConfirmCreateEmplTimesheetSummary = "${StringUtil.wrapString(uiLabelMap.ConfirmCreateEmplTimesheetSummary)}";
</script>