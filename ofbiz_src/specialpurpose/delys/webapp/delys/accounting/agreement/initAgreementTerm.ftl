<script>
	<#assign invItemTypeList = delegator.findList("InvoiceItemType", null, null, null, null, false) />
	var iitData = [
		<#list invItemTypeList as item>
		{
			invoiceItemTypeId : '${item.invoiceItemTypeId?if_exists}',
			description : '${item.description?if_exists}'
		},
		</#list>
	];
	<#assign termTypeList = delegator.findList("TermType", null, null, null, null, false) />
	var ttData = [
		<#list termTypeList as item >
		{
			<#assign description = StringUtil.wrapString(item.get("description", locale)) />
			termTypeId : '${item.termTypeId?if_exists}',
			description : '${description}'
		},
		</#list>
	];
</script>