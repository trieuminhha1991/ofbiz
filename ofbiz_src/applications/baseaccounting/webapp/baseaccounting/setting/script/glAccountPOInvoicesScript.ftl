<#assign mapconds = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("invoiceTypeId",Static["org.ofbiz.entity.condition.EntityJoinOperator"].EQUALS,"PURCHASE_INVOICE")/>
<#assign listInvoiceItemType = delegator.findList("InvoiceItemType",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(mapconds),null,Static["org.ofbiz.base.util.UtilMisc"].toList("description DESC"),null,false)/>

<script type="text/javascript">
if(typeof(uiLabelMap) == 'undefined') var uiLabelMap = {};
uiLabelMap.BACCaccountName = '${StringUtil.wrapString(uiLabelMap.BACCAccountName)}';
uiLabelMap.BACCaccountCode = '${StringUtil.wrapString(uiLabelMap.BACCAccountCode)}';
	var dataITT = new Array();
	dataITT = [
		<#list listInvoiceItemType as acc>
			{
				'invoiceItemTypeId' : '${acc.invoiceItemTypeId?if_exists}',
				'description' : "<span class='custom-style-word'> ${StringUtil.wrapString(acc.get('description',locale)?default(''))}</span>"
			},
		</#list>	
		]
</script>

