<script type="text/javascript">
	var dataInvoiceType = [<#if listInvoiceType?exists><#list listInvoiceType as type>{invoiceTypeId : "${type.invoiceTypeId}",description : "${StringUtil.wrapString(type.description)}"},</#list></#if>];
	var dataStatusType = [<#if listStatusItem?exists><#list listStatusItem as type>{statusId : "${type.statusId}",description : "${StringUtil.wrapString(type.description)}",},</#list></#if>];
</script>