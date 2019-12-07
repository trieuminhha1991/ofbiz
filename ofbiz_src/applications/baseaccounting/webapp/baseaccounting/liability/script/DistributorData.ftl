<@jqGridMinimumLib />
<script>
	var dataInvoiceTypeP = [];
	<#if listInvoiceTypeP?exists>
		<#list listInvoiceTypeP as item>
		    <#assign description = item.get("description", locale)/>
		    var tmpOb = new Object();
		    tmpOb.invoiceTypeId = "${item.invoiceTypeId}";
		    tmpOb.description = "${description}";
		    dataInvoiceTypeP[${item_index}] = tmpOb;
		</#list>
	</#if>	
	var dataInvoiceTypeS = [];
	<#if listInvoiceTypeS?exists>
		<#list listInvoiceTypeS as item>
		    <#assign description = item.get("description", locale)/>
		    var tmpOb = new Object();
		    tmpOb.invoiceTypeId = "${item.invoiceTypeId}";
		    tmpOb.description = "${description}";
		    dataInvoiceTypeS[${item_index}] = tmpOb;
		</#list>
	</#if>	
	var dataStatusType = [];
	<#if listStatusItem?exists>
		<#list listStatusItem as item>
		    <#assign description = item.get("description", locale)/>
		    var tmpOb = new Object();
		    tmpOb.statusId = "${item.statusId}";
		    tmpOb.description = "${description}";
		    dataStatusType[${item_index}] = tmpOb;
		</#list>
	</#if>
	<#assign typeLiabilityS="SALES"/>
	<#assign typeLiabilityP="PURCHASE"/>
</script>

