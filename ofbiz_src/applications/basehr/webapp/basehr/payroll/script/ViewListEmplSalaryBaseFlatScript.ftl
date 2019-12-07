<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
var globalVar = {};
var uiLabelMap = {};

globalVar.periodTypeArr = [
	<#if periodTypeList?exists>
		<#list periodTypeList as periodType>
			{
				periodTypeId: "${periodType.periodTypeId}",
				description: "${StringUtil.wrapString(periodType.description?if_exists)}" 		
			},
		</#list>
	</#if>
];

<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>

<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
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
globalVar.nowTimestamp = ${nowTimestamp.getTime()};
globalVar.monthStart = ${monthStart.getTime()},
globalVar.monthEnd = ${monthEnd.getTime()};

var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

uiLabelMap.DateJoinCompany = "${StringUtil.wrapString(uiLabelMap.DateJoinCompany)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.CommonDepartment = "${StringUtil.wrapString(uiLabelMap.CommonDepartment)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.ThruDateMustBeAfterFromDate = "${StringUtil.wrapString(uiLabelMap.ThruDateMustBeAfterFromDate)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonPeriodType = "${StringUtil.wrapString(uiLabelMap.CommonPeriodType)}";
uiLabelMap.HRCommonAmount = "${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}";
uiLabelMap.CommonThruDate = "${StringUtil.wrapString(uiLabelMap.CommonThruDate)}";
uiLabelMap.EffectiveFromDate = "${StringUtil.wrapString(uiLabelMap.EffectiveFromDate)}";
uiLabelMap.EmployeeListSelected = "${StringUtil.wrapString(uiLabelMap.EmployeeListSelected)}";
uiLabelMap.CreateSalaryForEmplConfirm = "${StringUtil.wrapString(uiLabelMap.CreateSalaryForEmplConfirm)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.EmployeeSelected = "${StringUtil.wrapString(uiLabelMap.EmployeeSelected)}";
uiLabelMap.NoPartyChoose = "${StringUtil.wrapString(uiLabelMap.NoPartyChoose)}";
uiLabelMap.CommonMonth = "${StringUtil.wrapString(uiLabelMap.CommonMonth)}";
uiLabelMap.EmployeeSalaryDetails = "${StringUtil.wrapString(uiLabelMap.EmployeeSalaryDetails)}";
uiLabelMap.SalaryBaseFlat = "${StringUtil.wrapString(uiLabelMap.SalaryBaseFlat)}";
uiLabelMap.PeriodTypePayroll = "${StringUtil.wrapString(uiLabelMap.PeriodTypePayroll)}";
uiLabelMap.UpdateEmplSalaryBaseConfirm = "${StringUtil.wrapString(uiLabelMap.UpdateEmplSalaryBaseConfirm)}";
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
uiLabelMap.HRTimeIsNotValid = "${StringUtil.wrapString(uiLabelMap.HRTimeIsNotValid)}";
</script>