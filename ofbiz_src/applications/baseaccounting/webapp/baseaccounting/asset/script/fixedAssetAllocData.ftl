<script>
	<#assign listPartyGlAcc = delegator.findByAnd("FAPartyGlAccountView", null, null, false)>
	var partyGlAccountData = [
      <#if listPartyGlAcc?exists>
      	<#list listPartyGlAcc as item>
      		{
      			partyId : "${item.partyId}",
      			glAccountId : "${item.glAccountId}",
      			accountName : "${StringUtil.wrapString(item.get('accountName'))}",
  			},
  		</#list>
  	  </#if>
	];
</script>