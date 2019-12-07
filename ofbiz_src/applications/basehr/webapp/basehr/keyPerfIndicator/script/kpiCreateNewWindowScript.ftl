<script type="text/javascript">
	if(typeof(globalVar) == 'undefined'){
		globalVar = {};
	}
	globalVar.newKPIWindow = "${newKPIWindow}";
	globalVar.perfCriDevelopmentTypeArr = [
   		<#if perfCriDevelopmentTypeList?has_content>
   			<#list perfCriDevelopmentTypeList as perfCriDevelopmentType>
   			{
   				perfCriDevelopmetTypeId: "${perfCriDevelopmentType.perfCriDevelopmetTypeId}",
   				perfCriDevelopmetName: "${StringUtil.wrapString(perfCriDevelopmentType.perfCriDevelopmetName)}",
   				description: "${StringUtil.wrapString(perfCriDevelopmentType.description)}",
   				formula: "${perfCriDevelopmentType.formula}",
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
	
</script>