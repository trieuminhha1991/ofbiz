<#--===================================Prepare Data=====================================================-->
<script>
	<#assign listUoms = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, Static["org.ofbiz.base.util.UtilMisc"].toList("uomId DESC"), false)>
	<#assign listInvoiceTypes = delegator.findByAnd("InvoiceType", null, Static["org.ofbiz.base.util.UtilMisc"].toList("invoiceTypeId DESC"), false)>
	<#assign listStatusItems = delegator.findByAnd("StatusItem", {"statusTypeId" : "INVOICE_STATUS"}, Static["org.ofbiz.base.util.UtilMisc"].toList("statusTypeId DESC"), false)>
	
	var uomData = [
      <#if listUoms?exists>
      	<#list listUoms as uom>
      	<#if uom?exists && uom.uomId == "USD" || uom.uomId == "EUR" || uom.uomId == "VND">
      		{
      			uomId : "${uom.uomId}",
      			description : "${StringUtil.wrapString(uom.get('description'))}",
  			},
		</#if>
  		</#list>
  	  </#if>
	];
	
	var statusData = [
       <#if listStatusItems?exists>
       	<#list listStatusItems as item>
       		{
       			statusId : "${item.statusId}",
       			description : "${StringUtil.wrapString(item.get('description'))}",
   			},
   		</#list>
   	  </#if>
 	];
	
	var invoiceTypeData = [
      <#if listInvoiceTypes?exists>
      	<#list listInvoiceTypes as item>
      		{
      			invoiceTypeId : "${item.invoiceTypeId}",
      			description : "${StringUtil.wrapString(item.get('description',locale))}",
  			},
  		</#list>
  	  </#if>
	];
	
	<#if parameters.invoiceId?exists>
		<#assign invoice = delegator.findOne("Invoice", {"invoiceId" : parameters.invoiceId}, true) >
	</#if>
</script>
<#--===================================/Prepare Data=====================================================-->