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
	uiLabelMap.OrderId = '${StringUtil.wrapString(uiLabelMap.OrderId)}';
	uiLabelMap.ProductId = '${StringUtil.wrapString(uiLabelMap.ProductId)}';
	uiLabelMap.ProductName = '${StringUtil.wrapString(uiLabelMap.ProductName)}';
	uiLabelMap.OrderNumber = '${StringUtil.wrapString(uiLabelMap.OrderNumber)}';
	uiLabelMap.POReturnQuantity = '${StringUtil.wrapString(uiLabelMap.POReturnQuantity)}';
	uiLabelMap.Quantity = '${StringUtil.wrapString(uiLabelMap.Quantity)}';
	uiLabelMap.BPOQuantiyMustBeSmallerThanReturnableQuantity = '${StringUtil.wrapString(uiLabelMap.BPOQuantiyMustBeSmallerThanReturnableQuantity)}';
	uiLabelMap.UnitPrice = '${StringUtil.wrapString(uiLabelMap.UnitPrice)}';
	uiLabelMap.Reason = '${StringUtil.wrapString(uiLabelMap.Reason)}';
	uiLabelMap.PleaseSelectTitle = '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}';
	uiLabelMap.Unit = '${StringUtil.wrapString(uiLabelMap.Unit)}';
	uiLabelMap.ReturnPriceCannotExceedPurchasePrice = '${StringUtil.wrapString(uiLabelMap.ReturnPriceCannotExceedPurchasePrice)}';

    uiLabelMap.ProductPromoId = '${StringUtil.wrapString(uiLabelMap.ProductPromoId)}';
    uiLabelMap.ProductPromoName = '${StringUtil.wrapString(uiLabelMap.ProductPromoName)}';
    uiLabelMap.orderedPromoAmount = '${StringUtil.wrapString(uiLabelMap.orderedPromoAmount)}';
    uiLabelMap.returnableAmount = '${StringUtil.wrapString(uiLabelMap.returnableAmount)}';
    uiLabelMap.BPOAmountMustBeSmallerThanReturnableAmount = '${StringUtil.wrapString(uiLabelMap.BPOAmountMustBeSmallerThanReturnableAmount)}';
    uiLabelMap.returnAmount = '${StringUtil.wrapString(uiLabelMap.returnAmount)}';
    uiLabelMap.listReturnProductPromoByOrder = '${StringUtil.wrapString(uiLabelMap.listReturnProductPromoByOrder)}';

</script>
<script type="text/javascript" src="/poresources/js/returnSupplier/supReturnNewSupReturnProductPromo.js"></script>