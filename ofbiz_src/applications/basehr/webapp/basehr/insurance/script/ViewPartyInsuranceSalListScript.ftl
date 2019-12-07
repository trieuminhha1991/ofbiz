<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>

<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>
<#if expandedList?has_content>
	<#assign expandTreeId=expandedList[0]>
<#else>
	<#assign expandTreeId="">
</#if>
<script type="text/javascript">
var periodTypeArr = [
	<#if periodTypeList?has_content>
		<#list periodTypeList as periodType>
			{
				periodTypeId: '${periodType.periodTypeId}',
				description: '${StringUtil.wrapString(periodType.description?if_exists)}'
			},
		</#list>
	</#if>
];

<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>

var globalVar = {
		nowTimestamp: ${nowTimestamp.getTime()},
		monthStart: ${monthStart.getTime()},
		monthEnd: ${monthEnd.getTime()},
		<#if expandTreeId?has_content>
		expandTreeId: "${expandTreeId}",	
		</#if>
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

var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

var uiLabelMap = {};
uiLabelMap.PayrollParamPositionHighest = "${StringUtil.wrapString(uiLabelMap.PayrollParamPositionHighest)}";
uiLabelMap.PayrollParamPositionLowest = "${StringUtil.wrapString(uiLabelMap.PayrollParamPositionLowest)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId?default(''))}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName?default(''))}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition?default(''))}";
uiLabelMap.CommonDepartment ="${StringUtil.wrapString(uiLabelMap.CommonDepartment?default(''))}";
uiLabelMap.DateJoinCompany ="${StringUtil.wrapString(uiLabelMap.DateJoinCompany?default(''))}";
uiLabelMap.AmountValueGreaterThanZero ="${StringUtil.wrapString(uiLabelMap.AmountValueGreaterThanZero?default(''))}";
uiLabelMap.FieldRequired ="${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}";
uiLabelMap.ThruDateMustGreaterThanFromDate ="${StringUtil.wrapString(uiLabelMap.ThruDateMustGreaterThanFromDate?default(''))}";
uiLabelMap.HrCreateNewConfirm ="${StringUtil.wrapString(uiLabelMap.HrCreateNewConfirm?default(''))}";
uiLabelMap.CommonSubmit ="${StringUtil.wrapString(uiLabelMap.CommonSubmit?default(''))}";
uiLabelMap.CommonCancel ="${StringUtil.wrapString(uiLabelMap.CommonCancel?default(''))}";
uiLabelMap.CommonAddNew ="${StringUtil.wrapString(uiLabelMap.CommonAddNew?default(''))}";
uiLabelMap.EditPartyInsuranceSalary ="${StringUtil.wrapString(uiLabelMap.EditPartyInsuranceSalary?default(''))}";
uiLabelMap.EditPartyInsuranceConfirm ="${StringUtil.wrapString(uiLabelMap.EditPartyInsuranceConfirm?default(''))}";

function jqxTreeEmplListSelect(event){
	var item = $('#jqxTreeEmplList').jqxTree('getItem', event.args.element);
	var partyId = item.value;
	var tmpS = $("#EmplListInOrg").jqxGrid('source');
	tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + partyId;
	$("#EmplListInOrg").jqxGrid('source', tmpS);
}
</script>