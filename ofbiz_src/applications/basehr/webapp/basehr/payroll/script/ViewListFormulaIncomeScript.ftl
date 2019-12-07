<@jqGridMinimumLib/>
<script src="/aceadmin/jqw/jqwidgets/jqxeditor.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxvalidator.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
var uiLabelMap = {};
uiLabelMap.CommonRequired = "${StringUtil.wrapString(uiLabelMap.CommonRequired)}";
uiLabelMap.ConfirmCreateNewFormulaConfrim = "${StringUtil.wrapString(uiLabelMap.ConfirmCreateNewFormulaConfrim)}";
uiLabelMap.ConfirmCreateNewFormulaConfrim = "${StringUtil.wrapString(uiLabelMap.ConfirmCreateNewFormulaConfrim)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.InvalidChar = '${StringUtil.wrapString(uiLabelMap.InvalidChar)}';
uiLabelMap.OnlyContainInvalidChar = "${StringUtil.wrapString(uiLabelMap.OnlyContainInvalidChar)}";
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

	var characteristicArr = [
		<#if payrollCharacteristic?has_content>
			<#list payrollCharacteristic as characteristic>
				{
					payrollCharacteristicId: "${characteristic.payrollCharacteristicId}",
					description: "${characteristic.description}"
				},				
			</#list>
		</#if>	                         
	];
	
	var payrollItemTypeArr = [
		<#if payrollItemType?has_content>
			<#list payrollItemType as item>
				{
					payrollItemTypeId: "${item.payrollItemTypeId}",
					description: "${item.description}"
				},
			</#list>
		</#if>
	];

	var taxableTypeArr = [
		<#if taxableType?has_content>
			<#list taxableType as type>
				{
					taxableTypeId: "${type.taxableTypeId}",
					description: "${type.description}"
				},
			</#list>
		</#if>	
	];

	var characIncomeArr = [
		<#list payrollCharacteristic as characteristic>
			<#if characteristic.payrollCharacteristicId == "INCOME">
				{
					payrollCharacteristicId: "${characteristic.payrollCharacteristicId}",
					description: "${StringUtil.wrapString(characteristic.description)}"
				},
			</#if>
		</#list>
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
