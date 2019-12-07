<script>
	<#assign listEnums = delegator.findByAnd("Enumeration", {"enumTypeId", "FA_DECREMENT_REASON"}, null, false)>
	var enumData = [
      <#if listEnums?exists>
      	<#list listEnums as enum>
      		{
      			enumId : "${enum.enumId}",
      			description : "${StringUtil.wrapString(enum.get('description'))}",
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