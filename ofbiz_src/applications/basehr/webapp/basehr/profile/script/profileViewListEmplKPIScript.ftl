<@jqGridMinimumLib/>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
var globalVar = {};
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
var uiLabelMap = {};
uiLabelMap.KPIPeriod = "${StringUtil.wrapString(uiLabelMap.KPIPeriod)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
</script>