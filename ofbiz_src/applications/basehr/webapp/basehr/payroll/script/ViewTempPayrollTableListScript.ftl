<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpanel.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>

<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript">
<#assign startDate = Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp) />
var statuses = [
	<#if statuses?has_content>
		<#list statuses as status>
			{
				statusId : "${status.statusId}",
				description : "${status.description}",
			},
		</#list>
	</#if>
];
var periodTypes = [
	<#list periodTypes as period>
		{
			periodTypeId : "${period.periodTypeId}",
			description : "${StringUtil.wrapString(period.description?default(''))}"
		},
	</#list>	
];

var theme = 'olbius';
 
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

var globalObject = (function(){
	var dataFormula = [
		<#if payrollChar?exists>
			<#list payrollChar as characteristic>
				{
					id: "${characteristic.payrollCharacteristicId}_parent",
					parentId: "-1",
					text: "${StringUtil.wrapString(characteristic.description?if_exists)}",
					value: "${characteristic.payrollCharacteristicId}"
				},
			</#list>
		</#if>
	];
	var checkedFormula = new Array();
	<#assign expandItemList = []> 
	<#if payrollFormula?has_content>
		<#list payrollFormula as formula>
			var row = {};
			row["id"]= "${formula.code}_code";
			row["parentId"]= "${formula.payrollCharacteristicId}_parent";
			row["text"]= "${StringUtil.wrapString(formula.name)}";
			row["value"] = "${formula.code}";
			dataFormula.push(row);
			<#if formula.get("includedPayrollTable")?exists>
				<#if formula.get("includedPayrollTable") == "Y">
					checkedFormula.push("${formula.code}_code");
					<#if expandItemList?seq_index_of(formula.payrollCharacteristicId + "_parent") == -1>
						<#assign expandItemList = expandItemList + [formula.payrollCharacteristicId + "_parent"]> 
					</#if>
				</#if>
			<#else>
				<#if formula.payrollCharacteristicId?exists>
					checkedFormula.push("${formula.code}_code");			
					<#if expandItemList?seq_index_of(formula.payrollCharacteristicId + "_parent") == -1>
						<#assign expandItemList = expandItemList + [formula.payrollCharacteristicId + "_parent"]> 
					</#if>
				</#if>
			</#if>
		</#list>
	</#if> 
	var getDataFormula = function(){
		return dataFormula;
	};
	
	var getCheckedFormula = function(){
		return checkedFormula;
	};
	
	var expandItemList = [
		<#list expandItemList as expand>
			"${expand}",
		</#list>
	];
	var getExpandItemList = function(){
		return expandItemList;
	};
	
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	
	return{
		getDataFormula: getDataFormula,
		getExpandItemList: getExpandItemList,
		getCheckedFormula: getCheckedFormula,
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());
<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
var globalVar = {
		<#if selectYearCustomTimePeriodId?exists>
			selectYearCustomTimePeriodId: "${selectYearCustomTimePeriodId}",
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
		startDate: ${startDate.getTime()}
};

var uiLabelMap = {
		ConfirmCreatePayrollTable: "${StringUtil.wrapString(uiLabelMap.ConfirmCreatePayrollTable)}",
		CommonSubmit: "${uiLabelMap.CommonSubmit}",
		CommonClose: "${uiLabelMap.CommonClose}",
		CommonCancel: "${uiLabelMap.CommonCancel}",
		FieldRequired: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}',
		PayrollCalculated_Recalculated: "${uiLabelMap.PayrollCalculated_Recalculated}",
		PayrollTableNotCalculated: "${uiLabelMap.PayrollTableNotCalculated}"
}

</script>
