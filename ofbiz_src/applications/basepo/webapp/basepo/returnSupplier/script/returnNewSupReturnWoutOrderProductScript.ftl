<script type="text/javascript">
	<#assign reasons = delegator.findList("ReturnReason", null, null, null, null, false)>
	var returnReasonData = [];
	<#list reasons as item>
		var row = {};
		row['returnReasonId'] = '${item.returnReasonId}';
		row['description'] = '${StringUtil.wrapString(item.get("description", locale)?if_exists)}';
		returnReasonData.push(row);
	</#list>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.SequenceId = '${StringUtil.wrapString(uiLabelMap.SequenceId)}';
	uiLabelMap.ProductId = '${StringUtil.wrapString(uiLabelMap.ProductId)}';
	uiLabelMap.ProductName = '${StringUtil.wrapString(uiLabelMap.ProductName)}';
	uiLabelMap.POReturnQuantity = '${StringUtil.wrapString(uiLabelMap.POReturnQuantity)}';
	uiLabelMap.Quantity = '${StringUtil.wrapString(uiLabelMap.Quantity)}';
	uiLabelMap.BSQuantityMustBeGreaterThanZero = '${StringUtil.wrapString(uiLabelMap.BSQuantityMustBeGreaterThanZero)}';
	uiLabelMap.BPOQuantiyMustBeSmallerThanReturnableQuantity = '${StringUtil.wrapString(uiLabelMap.BPOQuantiyMustBeSmallerThanReturnableQuantity)}';
	uiLabelMap.UnitPrice = '${StringUtil.wrapString(uiLabelMap.UnitPrice)}';
	uiLabelMap.Reason = '${StringUtil.wrapString(uiLabelMap.Reason)}';
	uiLabelMap.PleaseSelectTitle = '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}';
	uiLabelMap.Unit = '${StringUtil.wrapString(uiLabelMap.Unit)}';
	uiLabelMap.Description = '${StringUtil.wrapString(uiLabelMap.Description)}';
	uiLabelMap.ReturnPriceCannotExceedPurchasePrice = '${StringUtil.wrapString(uiLabelMap.ReturnPriceCannotExceedPurchasePrice)}';
	uiLabelMap.BSReturnPrice = '${StringUtil.wrapString(uiLabelMap.BSReturnPriceB)}';
</script>
<script type="text/javascript" src="/poresources/js/returnSupplier/returnNewSupReturnWoutOrderProduct.js?v=1.0.1"></script>