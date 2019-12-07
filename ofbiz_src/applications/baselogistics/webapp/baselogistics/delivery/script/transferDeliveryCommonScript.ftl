<style type="text/css">
	.bootbox{
	    z-index: 990009 !important;
	}
	.modal-backdrop{
	    z-index: 890009 !important;
	}
	.loading-container{
		z-index: 999999 !important;
	}
</style>
<script>
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	<#assign localeStr = "VI" />
	var localeStr = "VI";
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	
	var perAdmin = false;
	var checkUpdate = false;
	<#if hasOlbPermission("MODULE", "LOG_DELIVERY", "ADMIN")>
		perAdmin = true;
		checkUpdate = true;
	</#if>
	<#if hasOlbPermission("MODULE", "LOG_DELIVERY", "CREATE")>
		checkUpdate = true;
	</#if>
	
	var saveClick = 0; 
	var checkContinue = false;
	var checkOpenPoppup = false;
	var listDeliveryItemData = [];
	var glTransferId;
    var inTransferDetail = false;
    var deliveryDT;
	var listInv = [];
    <#assign itemTypes = delegator.findList("TransferItemType", null, null, null, null, false) >
	var transferItemTypeData = [];
	<#list itemTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale)) />
		row['transferItemTypeId'] = "${item.transferItemTypeId?if_exists}";
		row['description'] = "${description}";
		transferItemTypeData[${item_index}] = row;
	</#list>
	
	<#assign listUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var packingUomData = [];
	<#list listUoms as item>
		var row = {};
		<#assign qtyDesc = StringUtil.wrapString(item.get("description", locale))/>
		row['uomId'] = "${item.uomId?if_exists}";
		row['description'] = "${qtyDesc?if_exists}";
		packingUomData[${item_index}] = row;
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list weightUoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale)) />
		row['uomId'] = "${item.uomId}";
		row['description'] = "${description?if_exists}";
		weightUomData[${item_index}] = row;
	</#list>
	
	<#assign uomConversions = delegator.findList("UomConversion", null, null, null, null, false) />
	var uomConvertData = new Array();
	<#list uomConversions as item>
		var row = {};
		row['uomId'] = "${item.uomId}";
		row['uomIdTo'] = "${item.uomIdTo}";
		row['conversionFactor'] = "${item.conversionFactor}";
		uomConvertData[${item_index}] = row;
	</#list>
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_STATUS"), null, null, null, false) />
	var statusData = [];
	<#list statuses as item>
		var row = {};
		<#assign descStatus = StringUtil.wrapString(item.get("description", locale))>
		row['statusId'] = "${item.statusId}";
		row['description'] = "${descStatus?if_exists}";
		statusData.push(row);
	</#list>
	
	<#assign statusItems = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_ITEM_STATUS"), null, null, null, false)>
	var dlvItemStatusData = [];
	<#list statusItems as item>
		var row = {};
		<#assign descItem= StringUtil.wrapString(item.get("description", locale)) />
		row['statusId'] = '${item.statusId}';
		row['description'] = '${descItem?if_exists}';
		dlvItemStatusData.push(row);
	</#list>
	
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var quantityUomData = [];
	<#list uoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale)) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${description}';
		quantityUomData.push(row);
	</#list>
	
	function getUomDescription(uomId) {
	 	for (x in quantityUomData) {
	 		if (quantityUomData[x].uomId == uomId) {
	 			return quantityUomData[x].description;
	 		}
	 	}
	 	for (x in weightUomData) {
	 		if (weightUomData[x].uomId == uomId) {
	 			return weightUomData[x].description;
	 		}
	 	}
	 }
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.AreYouSureApprove = "${StringUtil.wrapString(uiLabelMap.AreYouSureApprove)}";
	uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.AttachFileScan = "${StringUtil.wrapString(uiLabelMap.AttachFileScan)}";
	uiLabelMap.DropFileOrClickToChoose = "${StringUtil.wrapString(uiLabelMap.DropFileOrClickToChoose)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	
	uiLabelMap.MustUploadScanFile = "${StringUtil.wrapString(uiLabelMap.MustUploadScanFile)}";
	uiLabelMap.FileScan = "${StringUtil.wrapString(uiLabelMap.FileScan)}";
	uiLabelMap.ActualExportedDate = "${StringUtil.wrapString(uiLabelMap.ActualExportedDate)}";
	uiLabelMap.ActualDeliveredDate = "${StringUtil.wrapString(uiLabelMap.ActualDeliveredDate)}";
	uiLabelMap.DLYItemMissingFieldsDlv = "${StringUtil.wrapString(uiLabelMap.DLYItemMissingFieldsDlv)}";
	uiLabelMap.LogCheckActuallyExportedGreaterRealCommunication = "${StringUtil.wrapString(uiLabelMap.LogCheckActuallyExportedGreaterRealCommunication)}";
	uiLabelMap.DLYItemComplete = "${StringUtil.wrapString(uiLabelMap.DLYItemComplete)}";
	uiLabelMap.DItemMissingFieldsExp = "${StringUtil.wrapString(uiLabelMap.DItemMissingFieldsExp)}";
	uiLabelMap.wgpagergotopagestring = "${StringUtil.wrapString(uiLabelMap.wgpagergotopagestring)}";
	uiLabelMap.wgpagershowrowsstring = "${StringUtil.wrapString(uiLabelMap.wgpagershowrowsstring)}";
	uiLabelMap.wgpagerrangestring = "${StringUtil.wrapString(uiLabelMap.wgpagerrangestring)}";
	uiLabelMap.wgpagernextbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagernextbuttonstring)}";
	uiLabelMap.wgpagerpreviousbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagerpreviousbuttonstring)}";
	uiLabelMap.wgsortascendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortascendingstring)}";
	uiLabelMap.wgsortdescendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortdescendingstring)}";
	uiLabelMap.wgsortremovestring = "${StringUtil.wrapString(uiLabelMap.wgsortremovestring)}";
	uiLabelMap.wgemptydatastring = "${StringUtil.wrapString(uiLabelMap.wgemptydatastring)}";
	uiLabelMap.wgfilterselectstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}";
	uiLabelMap.wgfilterselectallstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}";
	uiLabelMap.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
	uiLabelMap.wgdragDropToGroupColumn = "${StringUtil.wrapString(uiLabelMap.wgdragDropToGroupColumn)}";
	uiLabelMap.wgtodaystring = "${StringUtil.wrapString(uiLabelMap.wgtodaystring)}";
	uiLabelMap.wgclearstring = "${StringUtil.wrapString(uiLabelMap.wgclearstring)}";
	uiLabelMap.Confirm = "${StringUtil.wrapString(uiLabelMap.Confirm)}";
	uiLabelMap.ExpireDate = "${StringUtil.wrapString(uiLabelMap.ExpireDate)}";
	uiLabelMap.ApprovedDelivery = "${StringUtil.wrapString(uiLabelMap.ApprovedDelivery)}";
	uiLabelMap.DeliveryNote = "${StringUtil.wrapString(uiLabelMap.DeliveryNote)}";
	uiLabelMap.UpdateActualExportedQuantity = "${StringUtil.wrapString(uiLabelMap.UpdateActualExportedQuantity)}";
	uiLabelMap.UpdateActualDeliveredQuantity = "${StringUtil.wrapString(uiLabelMap.UpdateActualDeliveredQuantity)}";
	uiLabelMap.DeliveryDoc = "${StringUtil.wrapString(uiLabelMap.DeliveryDoc)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.QuantityNeedToTransfer = "${StringUtil.wrapString(uiLabelMap.QuantityNeedToTransfer)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.QuantityToTransfer = "${StringUtil.wrapString(uiLabelMap.QuantityToTransfer)}";
	uiLabelMap.QuantityMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.QuantityMustBeGreateThanZero)}";
	uiLabelMap.QuantityCantNotGreateThanQuantityNeedTransfer = "${StringUtil.wrapString(uiLabelMap.QuantityCantNotGreateThanQuantityNeedTransfer)}";
	uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
	uiLabelMap.RequiredExpireDate = "${StringUtil.wrapString(uiLabelMap.RequiredExpireDate)}";
	uiLabelMap.ActualExportedQuantity = "${StringUtil.wrapString(uiLabelMap.ActualExportedQuantity)}";
	uiLabelMap.ActualDeliveredQuantity = "${StringUtil.wrapString(uiLabelMap.ActualDeliveredQuantity)}";
	uiLabelMap.Status = "${StringUtil.wrapString(uiLabelMap.Status)}";
	uiLabelMap.ExpireDate = "${StringUtil.wrapString(uiLabelMap.ExpireDate)}";
	uiLabelMap.NotEnoughDetail = "${StringUtil.wrapString(uiLabelMap.NotEnoughDetail)}";
	uiLabelMap.NotEnough = "${StringUtil.wrapString(uiLabelMap.NotEnough)}";
	uiLabelMap.FacilityNotEnoughProduct = "${StringUtil.wrapString(uiLabelMap.FacilityNotEnoughProduct)}";
	uiLabelMap.NumberGTZ = "${StringUtil.wrapString(uiLabelMap.NumberGTZ)}";
	uiLabelMap.QuantityGreateThanQuantityCreatedInSalesDelivery = "${StringUtil.wrapString(uiLabelMap.QuantityGreateThanQuantityCreatedInSalesDelivery)}";
	uiLabelMap.ExpireDateNotEnter = "${StringUtil.wrapString(uiLabelMap.ExpireDateNotEnter)}";
	uiLabelMap.ExpiredDateSelectedNotEnoughSelectAnother = "${StringUtil.wrapString(uiLabelMap.ExpiredDateSelectedNotEnoughSelectAnother)}";
	uiLabelMap.PleaseEnterQuantityExported = "${StringUtil.wrapString(uiLabelMap.PleaseEnterQuantityExported)}";
	uiLabelMap.or = "${StringUtil.wrapString(uiLabelMap.or)}";
	uiLabelMap.AreYouSureCancel = "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";
	
	uiLabelMap.ProductMissDatetimeReceived = "${StringUtil.wrapString(uiLabelMap.ProductMissDatetimeReceived)}";
	uiLabelMap.ProductMissDatetimeManufactured = "${StringUtil.wrapString(uiLabelMap.ProductMissDatetimeManufactured)}";
	uiLabelMap.ProductMissExpiredDate = "${StringUtil.wrapString(uiLabelMap.ProductMissExpiredDate)}";
	uiLabelMap.ExpiredDateSum = "${StringUtil.wrapString(uiLabelMap.ExpiredDateSum)}";
	uiLabelMap.ManufacturedDateSum = "${StringUtil.wrapString(uiLabelMap.ManufacturedDateSum)}";
	uiLabelMap.ReceivedDateSum = "${StringUtil.wrapString(uiLabelMap.ReceivedDateSum)}";
	
	uiLabelMap.LogInventoryItem = "${StringUtil.wrapString(uiLabelMap.LogInventoryItem)}";
	uiLabelMap.InventoryItemNotChoose = "${StringUtil.wrapString(uiLabelMap.InventoryItemNotChoose)}";
	uiLabelMap.AnInvItemMissExpiredDate = "${StringUtil.wrapString(uiLabelMap.AnInvItemMissExpiredDate)}";
	uiLabelMap.DeliveryTransferNote = "${StringUtil.wrapString(uiLabelMap.DeliveryTransferNote)}";
	uiLabelMap.Scan = "${StringUtil.wrapString(uiLabelMap.Scan)}";
	uiLabelMap.BLCompleted = "${StringUtil.wrapString(uiLabelMap.BLCompleted)}";
	uiLabelMap.BSViewDetail = "${StringUtil.wrapString(uiLabelMap.BSViewDetail)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.ExportPdf = "${StringUtil.wrapString(uiLabelMap.ExportPdf)}";
	uiLabelMap.BLQuickView = "${StringUtil.wrapString(uiLabelMap.BLQuickView)}";
</script>
<script type="text/javascript" src="/logresources/js/delivery/transferDelivery.js?v=1.0.0"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>