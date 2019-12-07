<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>

<script type="text/javascript">
<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>
var uiLabelMap = {};
var globalVar = {};

uiLabelMap.KPIPeriod = "${StringUtil.wrapString(uiLabelMap.KPIPeriod)}";
globalVar.monthStart = ${monthStart.getTime()};
globalVar.monthEnd = ${monthEnd.getTime()};

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

globalVar.periodTypeArr = [
	<#if periodTypeList?has_content>
		<#list periodTypeList as periodType>
		{
			periodTypeId: '${periodType.periodTypeId}',
			description: '${periodType.description?if_exists}'
		},
		</#list>
	</#if>
];

globalVar.perfCriteriaTypeArr = [
	<#if perfCriteriaTypeList?has_content>
		<#list perfCriteriaTypeList as perfCriteriaType>
		{
			perfCriteriaTypeId: '${perfCriteriaType.perfCriteriaTypeId}',
			description: '${StringUtil.wrapString(perfCriteriaType.description?if_exists)}'
		},
		</#list>
	</#if>
];
globalVar.statusArr = [
	<#if KPIStatusList?has_content>
		<#list KPIStatusList as status>
		{
			statusId: '${status.statusId}',
			description: '${StringUtil.wrapString(status.description?if_exists)}'
		},
		</#list> 
	</#if>                       
];
globalVar.uomArr = [
	<#if uomList?has_content>
		<#list uomList as uom>
		{
			uomId: '${uom.uomId}',
			abbreviation: '${StringUtil.wrapString(uom.abbreviation?if_exists)}',
			description: '${StringUtil.wrapString(uom.description?if_exists)}'
		},
		</#list>
	</#if>
];

<#if defaultPeriodTypeId?exists>
	globalVar.defaultPeriodTypeId = "${defaultPeriodTypeId}";
</#if>
var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.accRemoveFilter = '${StringUtil.wrapString(uiLabelMap.accRemoveFilter)}';
uiLabelMap.wgdelete = '${StringUtil.wrapString(uiLabelMap.wgdelete)}';
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.CommonId = '${StringUtil.wrapString(uiLabelMap.CommonId)}';
uiLabelMap.filterselectallstring = '${StringUtil.wrapString(uiLabelMap.filterselectallstring)}';
uiLabelMap.KPIWeigth = '${StringUtil.wrapString(uiLabelMap.KPIWeigth)}';
uiLabelMap.HRTarget = '${StringUtil.wrapString(uiLabelMap.HRTarget)}';
uiLabelMap.HRFrequency = '${StringUtil.wrapString(uiLabelMap.HRFrequency)}';
uiLabelMap.HRCommonKPIName = '${StringUtil.wrapString(uiLabelMap.HRCommonKPIName)}';
uiLabelMap.HRCommonFields = '${StringUtil.wrapString(uiLabelMap.HRCommonFields)}';
uiLabelMap.HRCommonUnit = '${StringUtil.wrapString(uiLabelMap.HRCommonUnit)}';
uiLabelMap.KPIChoosen = '${StringUtil.wrapString(uiLabelMap.KPIChoosen)}';
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.CommonDepartment = "${StringUtil.wrapString(uiLabelMap.CommonDepartment)}";
uiLabelMap.HRCommonToLowercase = "${StringUtil.wrapString(uiLabelMap.HRCommonToLowercase)}";
uiLabelMap.HrCommonFromLowercase = "${StringUtil.wrapString(uiLabelMap.HrCommonFromLowercase)}";
</script>
