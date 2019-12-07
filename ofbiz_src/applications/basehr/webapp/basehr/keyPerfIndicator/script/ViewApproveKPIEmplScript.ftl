<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>


<script type="text/javascript">
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>

if(typeof(globalVar) == 'undefined'){
	var globalVar = {};
};

if(typeof(globalVar.monthStart) == 'undefined'){
	globalVar.monthStart = ${monthStart.getTime()};
};

if(typeof(globalVar.monthEnd) == 'undefined'){
	globalVar.monthEnd = ${monthEnd.getTime()};
};

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

<#if expandedList?has_content>
	<#assign expandTreeId=expandedList[0]>
	if(typeof(globalVar.expandTreeId) == 'undefined'){
		globalVar.expandTreeId = "${expandTreeId}";		
	}
<#else>
	<#assign expandTreeId="">
</#if>

var uiLabelMap = {};
uiLabelMap.ListKPIAssignForEmpl = '${StringUtil.wrapString(uiLabelMap.ListKPIAssignForEmpl)}';
uiLabelMap.AssignKPIForEmpl = '${StringUtil.wrapString(uiLabelMap.AssignKPIForEmpl)}';
uiLabelMap.CommonFromDate = '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}';
uiLabelMap.CommonThruDate = '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}';
uiLabelMap.CommonStatus = '${StringUtil.wrapString(uiLabelMap.CommonStatus)}';
uiLabelMap.CommonEmployee = '${StringUtil.wrapString(uiLabelMap.CommonEmployee)}';
uiLabelMap.DateJoinCompany = "${StringUtil.wrapString(uiLabelMap.DateJoinCompany)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
uiLabelMap.ExpireDateMustGreaterOrEqualThanEffectiveDate = "${StringUtil.wrapString(uiLabelMap.ExpireDateMustGreaterOrEqualThanEffectiveDate)}";
uiLabelMap.AssignKPIConfirm = "${StringUtil.wrapString(uiLabelMap.AssignKPIConfirm)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.CommonDepartment = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.YouHaveNtApprKPI = "${StringUtil.wrapString(uiLabelMap.YouHaveNtApprKPI)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.ChooseTimeRangeAppKPI = "${StringUtil.wrapString(uiLabelMap.ChooseTimeRangeAppKPI)}";
uiLabelMap.PleaseSelectOption = "${StringUtil.wrapString(uiLabelMap.PleaseSelectOption)}";
uiLabelMap.HRNoDataChoosedToApprove = "${StringUtil.wrapString(uiLabelMap.HRNoDataChoosedToApprove)}";
</script>
