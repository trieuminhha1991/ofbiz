<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxloader.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>

<script type="text/javascript">
<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
var uiLabelMap = {};
var globalVar = {};

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

<#if security.hasEntityPermission("RECRUITOFFICEPLAN", "_APPROVE", session)>
	globalVar.hasPermissionAppr = true;
</#if>

globalVar.userLoginPartyId = "${userLogin.partyId}";
globalVar.monthNames = ["${StringUtil.wrapString(uiLabelMap.CommonJanuary)}", 
                    "${StringUtil.wrapString(uiLabelMap.CommonFebruary)}", 
                    "${StringUtil.wrapString(uiLabelMap.CommonMarch)}", 
                    "${StringUtil.wrapString(uiLabelMap.CommonApril)}", 
                    "${StringUtil.wrapString(uiLabelMap.CommonMay)}", 
                    "${StringUtil.wrapString(uiLabelMap.CommonJune)}",
              		"${StringUtil.wrapString(uiLabelMap.CommonJuly)}", 
              		"${StringUtil.wrapString(uiLabelMap.CommonAugust)}", 
              		"${StringUtil.wrapString(uiLabelMap.CommonSeptember)}", 
              		"${StringUtil.wrapString(uiLabelMap.CommonOctober)}", 
              		"${StringUtil.wrapString(uiLabelMap.CommonNovember)}", 
              		"${StringUtil.wrapString(uiLabelMap.CommonDecember)}"];
globalVar.statusArr = [
	<#if statusList?has_content>
		<#list statusList as status>
		{
			statusId: "${status.statusId}",
			description: "${StringUtil.wrapString(status.description?if_exists)}",
			statusCode: "${StringUtil.wrapString(status.statusCode?if_exists)}"
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

<#assign customerTimeYears = delegator.findList("CustomTimePeriod", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("periodTypeId", "YEARLY"), null, null, null, false) />
var yearData = new Array();
<#list customerTimeYears as item>
	var row = {};
	row['customTimePeriodId'] = "${item.customTimePeriodId}";
	row['fromDate'] = "${item.fromDate?if_exists}";
	row['thruDate'] = "${item.thruDate?if_exists}";
	yearData.push(row);
</#list>

<#if selectYearCustomTimePeriodId?has_content>
	globalVar.selectYearCustomTimePeriodId = "${selectYearCustomTimePeriodId}";
</#if>
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
uiLabelMap.HRCommonTime = "${StringUtil.wrapString(uiLabelMap.HRCommonTime)}";
uiLabelMap.HRCommonQuantity = "${StringUtil.wrapString(uiLabelMap.HRCommonQuantity)}";
uiLabelMap.CommonStatus = "${StringUtil.wrapString(uiLabelMap.CommonStatus)}";
uiLabelMap.HRNotes = "${StringUtil.wrapString(uiLabelMap.HRNotes)}";
uiLabelMap.HRCommonAccept = "${StringUtil.wrapString(uiLabelMap.HRCommonAccept)}";
uiLabelMap.HRCommonReject = "${StringUtil.wrapString(uiLabelMap.HRCommonReject)}";
uiLabelMap.RecruitmentAnticipate = "${StringUtil.wrapString(uiLabelMap.RecruitmentAnticipate)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.NoRowSelected = "${StringUtil.wrapString(uiLabelMap.NoRowSelected)}";
uiLabelMap.HRApprovalConfirm = "${StringUtil.wrapString(uiLabelMap.HRApprovalConfirm)}";
uiLabelMap.RecruitmentAnticipateItemAccepted = "${StringUtil.wrapString(uiLabelMap.RecruitmentAnticipateItemAccepted)}";
uiLabelMap.RecruitmentAnticipateItemRejected = "${StringUtil.wrapString(uiLabelMap.RecruitmentAnticipateItemRejected)}";
uiLabelMap.HRNotApproval = "${StringUtil.wrapString(uiLabelMap.HRNotApproval)}";
uiLabelMap.ApprovalReason = "${StringUtil.wrapString(uiLabelMap.ApprovalReason)}";
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
uiLabelMap.UpdateRecruitmentAnticipateConfirm = "${StringUtil.wrapString(uiLabelMap.UpdateRecruitmentAnticipateConfirm)}";
</script>