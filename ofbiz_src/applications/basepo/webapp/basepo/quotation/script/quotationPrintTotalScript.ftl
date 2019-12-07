<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<@jqOlbCoreLib />
<script type="text/javascript">
	<#assign uomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, false)/>
	var dataSelected = new Array();
	
	var uomData = new Array();
	<#list uomList as uomItem>
		<#assign description = StringUtil.wrapString(uomItem.get("description", locale)) />
		var row = {};
		row['uomId'] = '${uomItem.uomId}';
		row['description'] = "${description?default('')}";
		uomData[${uomItem_index}] = row;
	</#list>
	
	if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
	uiLabelMap.BSConfirmation = "${uiLabelMap.BSConfirmation}?";
	uiLabelMap.BSYouNotYetChooseRow = "${uiLabelMap.BSYouNotYetChooseRow}!";
	
	var dataField = ${dataField};
	var quotationSelected = {"productQuotationId" : '${productQuotation.productQuotationId}'};
</script>
<script type="text/javascript" src="/poresources/js/quotation/quotationPrintTotal.js?v=0.0.1"></script>