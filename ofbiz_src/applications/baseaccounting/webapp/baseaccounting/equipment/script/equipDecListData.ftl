<script>
	<#assign listDecrementTypes = delegator.findByAnd("DecrementType", null, null, false)>
	var decrementTypeData = [
      <#if listDecrementTypes?exists>
      	<#list listDecrementTypes as decType>
      		{
      			decrementTypeId : "${decType.decrementTypeId}",
      			description : "${StringUtil.wrapString(decType.get('description'))}",
  			},
  		</#list>
  	  </#if>
	];
	
	var booleanData = [
      		{
      			   description: "${uiLabelMap.BACCOpen}",
      			   value: 'N'
      		},
      		{
      			   description: "${uiLabelMap.BACCClose}",
      			   value: 'Y'
      		},
   	]
</script>