<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script>
		
	var listViewMethod01 = [];
	var listViewMethod02 = [];
	var mt1 = {};
	mt1["methodId"] = "viewByInventoryItem";
	mt1["description"] = "${uiLabelMap.InventoryVariance}";
	var mt2 = {};
	mt2["methodId"] = "viewByProduct";
	mt2["description"] = "${uiLabelMap.InventoryCounted}";
	listViewMethod01.push(mt2);
	listViewMethod01.push(mt1);
	listViewMethod02.push(mt2);
	listViewMethod02.push(mt1);
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.ChooseReasonAndQuantityBeforeSelect = "${StringUtil.wrapString(uiLabelMap.ChooseReasonAndQuantityBeforeSelect)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.ProductManufactureDate = "${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}";
	uiLabelMap.ProductExpireDate = "${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}";
	uiLabelMap.QOH = "${StringUtil.wrapString(uiLabelMap.QOH)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.Batch = "${StringUtil.wrapString(uiLabelMap.Batch)}";
	uiLabelMap.Status = "${StringUtil.wrapString(uiLabelMap.Status)}";
	uiLabelMap.Reason = "${StringUtil.wrapString(uiLabelMap.Reason)}";
	uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.InventoryGood = "${StringUtil.wrapString(uiLabelMap.InventoryGood)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	
	uiLabelMap.InventoryVariance = "${StringUtil.wrapString(uiLabelMap.InventoryVariance)}";
	uiLabelMap.InventoryCounted = "${StringUtil.wrapString(uiLabelMap.InventoryCounted)}";
</script>
<script type="text/javascript" src="/logresources/js/inventory/newPhysicalInventoryConfirm.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>