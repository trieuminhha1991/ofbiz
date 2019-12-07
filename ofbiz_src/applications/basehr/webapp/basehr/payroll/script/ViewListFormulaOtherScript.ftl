<@jqGridMinimumLib/>
<script src="/aceadmin/jqw/jqwidgets/jqxeditor.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxvalidator.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
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
 
var characteristicArr = new Array();
<#if payrollCharacteristic?has_content>
	<#list payrollCharacteristic as characteristic>
		var row = {};
		row["payrollCharacteristicId"] = "${characteristic.payrollCharacteristicId}";
		row["description"] = "${characteristic.description}";
		characteristicArr[${characteristic_index}] = row;
	</#list>
</#if>
</script>