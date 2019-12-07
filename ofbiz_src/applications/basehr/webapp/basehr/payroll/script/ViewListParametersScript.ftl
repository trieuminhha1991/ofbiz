<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>
<script type="text/javascript" src="/images/jquery/plugins/validate/jquery.validate.min.js"></script>
<script>
	var periodTypes = [
	<#list periodTypes as period>
		{
			periodTypeId : "${period.periodTypeId}",
			description : "${StringUtil.wrapString(period.description?default(''))}"
		},
	</#list>	
	];
	
	var parameterTypes = [
		<#if parameterTypes?exists>
			<#list parameterTypes as parameterType>
				{
					code : "${parameterType.code}",
					name : "${StringUtil.wrapString(parameterType.name?default(''))}",
					description : "${StringUtil.wrapString(parameterType.description?default(''))}"
				},
			</#list>
		</#if>
	]; 
	var allParameterType=[
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