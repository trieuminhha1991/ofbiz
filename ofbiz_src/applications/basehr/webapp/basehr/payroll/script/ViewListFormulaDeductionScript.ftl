<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxeditor.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxvalidator.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
var uiLabelMap = {};
uiLabelMap.CommonRequired = "${StringUtil.wrapString(uiLabelMap.CommonRequired)}";
uiLabelMap.ConfirmCreateFormula = "${StringUtil.wrapString(uiLabelMap.ConfirmCreateFormula)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.ConfirmCreateNewFormulaConfrim = "${StringUtil.wrapString(uiLabelMap.ConfirmCreateNewFormulaConfrim)}";
uiLabelMap.OnlyContainInvalidChar = "${StringUtil.wrapString(uiLabelMap.OnlyContainInvalidChar)}";
var characteristicArr = new Array();
<#if payrollCharacteristic?has_content>
	<#list payrollCharacteristic as characteristic>
		var row = {};
		row["payrollCharacteristicId"] = "${characteristic.payrollCharacteristicId}";
		row["description"] = "${characteristic.description}";
		characteristicArr[${characteristic_index}] = row;
	</#list>
</#if>

var characDeductionArr = [
	<#list payrollCharacteristic as characteristic>
		<#if characteristic.payrollCharacteristicId == "DEDUCTION" || characteristic.payrollCharacteristicId == "TAX_DEDUCTION">
			{
				payrollCharacteristicId: "${characteristic.payrollCharacteristicId}",
				description: "${StringUtil.wrapString(characteristic.description)}"
			},
		</#if>
	</#list>
];

var periodTypes = [
	   	<#list periodTypes as period>
	   		{
	   			periodTypeId : "${period.periodTypeId}",
	   			description : "${StringUtil.wrapString(period.description?default(''))}"
	   		},
	   	</#list>	
   	];
   	
var allParameterType = [
		<#if allParameterType?exists>
			<#list allParameterType as parameterType>
				{
					type : "${parameterType.code}",
					name : "${StringUtil.wrapString(parameterType.name?default(''))}",
					description : "${StringUtil.wrapString(parameterType.description?default(''))}"
				},
			</#list>
		</#if>          
];   	

var paramCharacteristicArr = [
	<#if paramCharacteristicList?has_content>
		<#list paramCharacteristicList as characteristic>
			{
				paramCharacteristicId: '${StringUtil.wrapString(characteristic.paramCharacteristicId)}',
				description: '${StringUtil.wrapString(characteristic.description?if_exists)}'
			},
		</#list>
	</#if>
];
</script>