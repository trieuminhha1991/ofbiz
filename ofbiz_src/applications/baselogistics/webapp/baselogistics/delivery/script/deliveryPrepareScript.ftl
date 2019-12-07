<script>
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var glOrderId;
	var quickClick = 0;
	var saveClick = 0;
	var hasReturn = false;
	var checkContinue = false;
	var mm = 0;
	var perAdmin = false;
	var inOrderDetail= false;
	var listNoteItems = [];
	var listProductToAdd = [];
	<#if hasOlbPermission("MODULE", "LOG_DELIVERY", "ADMIN")>
		perAdmin = true;
	</#if>
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
	<#assign uomConversions = delegator.findList("UomConversion", null, null, null, null, false) />
	var uomConvertData = new Array();
	<#list uomConversions as item>
		var row = {};
		row['uomId'] = "${item.uomId}";
		row['uomIdTo'] = "${item.uomIdTo}";
		row['conversionFactor'] = "${item.conversionFactor}";
		uomConvertData[${item_index}] = row;
	</#list>
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_STATUS"), null, null, null, false)/>
	var statusData = [];
	<#list statuses as item>
		var row = {};
		<#assign descStatus = StringUtil.wrapString(item.get('description', locale))>
		row['statusId'] = "${item.statusId}";
		row['description'] = "${descStatus?if_exists}";
		statusData[${item_index}] = row;
	</#list>
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var uomData = [];
	<#list uoms as item>
		var row = {};
		<#assign descPackingUom = StringUtil.wrapString(item.description?if_exists)/>
		row['quantityUomId'] = "${item.uomId?if_exists}";
		row['description'] = "${descPackingUom?if_exists}";
		uomData[${item_index}] = row;
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list weightUoms as item>
		var row = {};
		<#assign abbreviation = StringUtil.wrapString(item.get("abbreviation", locale)) />
		row['uomId'] = "${item.uomId}";
		row['description'] = "${abbreviation?if_exists}";
		weightUomData[${item_index}] = row;
	</#list>
	
	var facilityData = [];
	var facilityReturnData = [];
	<#if listFacilityByCustomer?has_content>
		<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", company)), Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("facilityId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, listFacilityByCustomer))), null, null, null, false)>
	<#else>
		<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", company)), null, null, null, false)>
	</#if>
	<#list facilities as item>
		var row = {};
		<#assign descFac = StringUtil.wrapString(item.facilityName?if_exists)/>
		row['facilityId'] = "${item.facilityId?if_exists}";
		row['ownerPartyId']= "${item.ownerPartyId?if_exists}";
		row['description'] = "${descFac?if_exists?replace('\n', ' ')}";
		row['productStoreId'] = "${item.productStoreId?if_exists}";
		facilityData.push(row);
		<#assign listInvInFac = delegator.findList("InventoryItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", item.facilityId)), null, null, null, false)>
		<#if listInvInFac?has_content>
			facilityReturnData.push(row);
		</#if>
	</#list>
	
	// return reason
	<#assign reasons = delegator.findList("ReturnReason", null, null, null, null, false)>
	var returnReasonData = [];
	<#list reasons as item>
		<#assign descReason = StringUtil.wrapString(item.get("description", locale))>
		var row = {};
		row['returnReasonId'] = '${item.returnReasonId}';
		row['description'] = '${descReason?if_exists}';
		returnReasonData.push(row);
	</#list>
	
	// inventory item status
	<#assign invItemStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "INV_NON_SER_STTS"), null, null, null, false)>
	var invStatusData = [];
	var rowTmp = {};
	rowTmp['statusId'] = 'Good';
	rowTmp['description'] = '${uiLabelMap.InventoryGood}';
	invStatusData.push(rowTmp);
	<#list invItemStatus as item>
		<#assign descInvStatus = StringUtil.wrapString(item.get("description", locale))>
		var row = {};
		row['statusId'] = '${item.statusId}';
		row['description'] = '${descInvStatus?if_exists}';
		invStatusData.push(row);
	</#list>
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_ITEM_STATUS"), null, null, null, false)>
	var dlvItemStatusData = [];
	<#list statuses as item>
		var row = {};
		<#assign descDlvItemStatus = StringUtil.wrapString(item.get("description", locale)) />
		row['statusId'] = '${item.statusId}';
		row['description'] = '${descDlvItemStatus?if_exists}';
		dlvItemStatusData[${item_index}] = row;
	</#list>
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var quantityUomData = [];
	<#list uoms as item>
		var row = {};
		<#assign descPackingUom = StringUtil.wrapString(item.get("description", locale)) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${descPackingUom?if_exists}';
		quantityUomData[${item_index}] = row;
	</#list>
	
	function getUomDescription(uomId) {
		for (var i = 0; i < weightUomData.length; i ++) {
			if (weightUomData[i].uomId == uomId) {
				return weightUomData[i].description;
			}
		}
		for (var i = 0; i < quantityUomData.length; i ++) {
			if (quantityUomData[i].uomId == uomId) {
				return quantityUomData[i].description;
			}
		}
	}
</script>
<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.PleaseChooseCountryBefore = "${StringUtil.wrapString(uiLabelMap.PleaseChooseCountryBefore)}";
	uiLabelMap.PleaseChooseProvinceBefore = "${StringUtil.wrapString(uiLabelMap.PleaseChooseProvinceBefore)}";
	uiLabelMap.PleaseChooseFacilityBefore = "${StringUtil.wrapString(uiLabelMap.PleaseChooseFacilityBefore)}";
	uiLabelMap.MustUploadScanFile = "${StringUtil.wrapString(uiLabelMap.MustUploadScanFile)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.SomeProductHasMissingQuantity = "${StringUtil.wrapString(uiLabelMap.SomeProductHasMissingQuantity)}";
	uiLabelMap.Click = "${StringUtil.wrapString(uiLabelMap.Click)}";
	uiLabelMap.orderTo = "${StringUtil.wrapString(uiLabelMap.orderTo)}";
	uiLabelMap.updateReasonNoteForOrder = "${StringUtil.wrapString(uiLabelMap.updateReasonNoteForOrder)}";
	uiLabelMap.or = "${StringUtil.wrapString(uiLabelMap.or)}";
	uiLabelMap.click = "${StringUtil.wrapString(uiLabelMap.click)}";
	uiLabelMap.backToEditDelivery = "${StringUtil.wrapString(uiLabelMap.backToEditDelivery)}";
	uiLabelMap.orderTo = "${StringUtil.wrapString(uiLabelMap.orderTo)}";
	uiLabelMap.Record = "${StringUtil.wrapString(uiLabelMap.Record)}";
	uiLabelMap.TheManufacturedDateFieldNotYetBeEntered = "${StringUtil.wrapString(uiLabelMap.TheManufacturedDateFieldNotYetBeEntered)}";
	uiLabelMap.TheExpiredDateFieldNotYetBeEntered = "${StringUtil.wrapString(uiLabelMap.TheExpiredDateFieldNotYetBeEntered)}";
	uiLabelMap.TheBatchFieldNotYetBeEntered = "${StringUtil.wrapString(uiLabelMap.TheBatchFieldNotYetBeEntered)}";
	uiLabelMap.PleaseSelectAReason = "${StringUtil.wrapString(uiLabelMap.PleaseSelectAReason)}";
	uiLabelMap.PleaseSelectAStatusOfProduct = "${StringUtil.wrapString(uiLabelMap.PleaseSelectAStatusOfProduct)}";
	uiLabelMap.PleaseChooseReceiveOrNo = "${StringUtil.wrapString(uiLabelMap.PleaseChooseReceiveOrNo)}";
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
	uiLabelMap.FacilityNotEnoughProduct = "${StringUtil.wrapString(uiLabelMap.FacilityNotEnoughProduct)}";
	uiLabelMap.NumberGTZ = "${StringUtil.wrapString(uiLabelMap.NumberGTZ)}";
	uiLabelMap.ExportValueLTZRequireValue = "${StringUtil.wrapString(uiLabelMap.ExportValueLTZRequireValue)}";
	uiLabelMap.CommonNoStatesProvincesExists = "${StringUtil.wrapString(uiLabelMap.CommonNoStatesProvincesExists)}";
	uiLabelMap.NoFacEnoughAllProductRequire = "${StringUtil.wrapString(uiLabelMap.NoFacEnoughAllProductRequire)}";
	uiLabelMap.CannotQuickCreate = "${StringUtil.wrapString(uiLabelMap.CannotQuickCreate)}";
	uiLabelMap.DropFileOrClickToChoose = "${StringUtil.wrapString(uiLabelMap.DropFileOrClickToChoose)}";
	uiLabelMap.NameOfImagesMustBeLessThan100Character = "${StringUtil.wrapString(uiLabelMap.NameOfImagesMustBeLessThan100Character)}";
	uiLabelMap.NameOfImagesMustBeLessThan50Character = "${StringUtil.wrapString(uiLabelMap.NameOfImagesMustBeLessThan50Character)}";
	uiLabelMap.Scan = "${StringUtil.wrapString(uiLabelMap.Scan)}";
	uiLabelMap.AttachFileScan = "${StringUtil.wrapString(uiLabelMap.AttachFileScan)}";
	uiLabelMap.ActualDeliveredDateMustAfterActualExportedDate = "${StringUtil.wrapString(uiLabelMap.ActualDeliveredDateMustAfterActualExportedDate)}";
	uiLabelMap.CannotAfterNow = "${StringUtil.wrapString(uiLabelMap.CannotAfterNow)}";
	uiLabelMap.ArrivalDateEstimatedMustBeAfterExportDateEstimated = "${StringUtil.wrapString(uiLabelMap.ArrivalDateEstimatedMustBeAfterExportDateEstimated)}";
	uiLabelMap.CannotBeforeNow = "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
	uiLabelMap.DLYItemMissingFieldsDlv = "${StringUtil.wrapString(uiLabelMap.DLYItemMissingFieldsDlv)}";
	uiLabelMap.LogCheckActuallyExportedGreaterRealCommunication = "${StringUtil.wrapString(uiLabelMap.LogCheckActuallyExportedGreaterRealCommunication)}";
	uiLabelMap.DLYItemComplete = "${StringUtil.wrapString(uiLabelMap.DLYItemComplete)}";
	uiLabelMap.DItemMissingFieldsExp = "${StringUtil.wrapString(uiLabelMap.DItemMissingFieldsExp)}";
	uiLabelMap.WithExpireDate = "${StringUtil.wrapString(uiLabelMap.WithExpireDate)}";
	uiLabelMap.ConfirmToDelivery = "${StringUtil.wrapString(uiLabelMap.ConfirmToDelivery)}";
	uiLabelMap.LogIs = "${StringUtil.wrapString(uiLabelMap.LogIs)}";
	uiLabelMap.LogCheckShowUnitPrice = "${StringUtil.wrapString(uiLabelMap.LogCheckShowUnitPrice)}";
	uiLabelMap.DeliveryNote = "${StringUtil.wrapString(uiLabelMap.DeliveryNote)}";
	uiLabelMap.UpdateActualDeliveredQuantity = "${StringUtil.wrapString(uiLabelMap.UpdateActualDeliveredQuantity)}";
	uiLabelMap.UpdateActualExportedQuantity = "${StringUtil.wrapString(uiLabelMap.UpdateActualExportedQuantity)}";
	uiLabelMap.Record = "${StringUtil.wrapString(uiLabelMap.Record)}";
	uiLabelMap.DeliveryDoc = "${StringUtil.wrapString(uiLabelMap.DeliveryDoc)}";
	uiLabelMap.DItemMissingFieldsExp = "${StringUtil.wrapString(uiLabelMap.DItemMissingFieldsExp)}";
	uiLabelMap.AreYouSureApprove = "${StringUtil.wrapString(uiLabelMap.AreYouSureApprove)}";
	uiLabelMap.Approve = "${StringUtil.wrapString(uiLabelMap.Approve)}";
	uiLabelMap.AllItemInListMustBeUpdated = "${StringUtil.wrapString(uiLabelMap.AllItemInListMustBeUpdated)}";
	uiLabelMap.ThisFieldMustNotByContainSpecialCharacter = "${StringUtil.wrapString(uiLabelMap.ThisFieldMustNotByContainSpecialCharacter)}";
	uiLabelMap.ExpiredDateSelectedNotEnoughSelectAnother = "${StringUtil.wrapString(uiLabelMap.ExpiredDateSelectedNotEnoughSelectAnother)}";
	uiLabelMap.NotEnough = "${StringUtil.wrapString(uiLabelMap.NotEnough)}";
	uiLabelMap.NotEnoughDetail = "${StringUtil.wrapString(uiLabelMap.NotEnoughDetail)}";
	uiLabelMap.ExpireDateNotEnter = "${StringUtil.wrapString(uiLabelMap.ExpireDateNotEnter)}";
	uiLabelMap.CheckCharacterValidate1To20 = "${StringUtil.wrapString(uiLabelMap.CheckCharacterValidate1To20)}";
	uiLabelMap.ShipmentIdExisted = "${StringUtil.wrapString(uiLabelMap.ShipmentIdExisted)}";
	uiLabelMap.AreYouSureSend = "${StringUtil.wrapString(uiLabelMap.AreYouSureSend)}";
	uiLabelMap.OutOfDate = "${StringUtil.wrapString(uiLabelMap.OutOfDate)}";
	uiLabelMap.AttachExportedScan = "${StringUtil.wrapString(uiLabelMap.AttachExportedScan)}";
	uiLabelMap.AttachDeliveredScan = "${StringUtil.wrapString(uiLabelMap.AttachDeliveredScan)}";
	uiLabelMap.MustUploadScanFileDelivery = "${StringUtil.wrapString(uiLabelMap.MustUploadScanFileDelivery)}";
	uiLabelMap.MustUploadScanFileExpt = "${StringUtil.wrapString(uiLabelMap.MustUploadScanFileExpt)}";
	uiLabelMap.NotManageAnyFacility = "${StringUtil.wrapString(uiLabelMap.NotManageAnyFacility)}";
	uiLabelMap.ApproveDelivery = "${StringUtil.wrapString(uiLabelMap.ApproveDelivery)}";
	uiLabelMap.WaitForApprove = "${StringUtil.wrapString(uiLabelMap.WaitForApprove)}";
	uiLabelMap.DeliveryNoteIdExisted = "${StringUtil.wrapString(uiLabelMap.DeliveryNoteIdExisted)}";
	uiLabelMap.AreYouSureCancel = "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";
	uiLabelMap.OnlySupportFile = "${StringUtil.wrapString(uiLabelMap.OnlySupportFile)}";
	uiLabelMap.ExpiredDateSum = "${StringUtil.wrapString(uiLabelMap.ExpiredDateSum)}";
	uiLabelMap.ManufacturedDateSum = "${StringUtil.wrapString(uiLabelMap.ManufacturedDateSum)}";
	uiLabelMap.ReceivedDateSum = "${StringUtil.wrapString(uiLabelMap.ReceivedDateSum)}";
	uiLabelMap.NotEnoughProduct = "${StringUtil.wrapString(uiLabelMap.NotEnoughProduct)}";
	uiLabelMap.BLCompleted	= "${StringUtil.wrapString(uiLabelMap.BLCompleted)}";
	
	uiLabelMap.For = "${StringUtil.wrapString(uiLabelMap.For)}";
	uiLabelMap.Product = "${StringUtil.wrapString(uiLabelMap.Product)}";
	uiLabelMap.Return = "${StringUtil.wrapString(uiLabelMap.Return)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.BSViewDetail = "${StringUtil.wrapString(uiLabelMap.BSViewDetail)}";
	uiLabelMap.DeliveryDocPooledTax = "${StringUtil.wrapString(uiLabelMap.DeliveryDocPooledTax)}";
	uiLabelMap.DeliveryNote = "${StringUtil.wrapString(uiLabelMap.DeliveryNote)}";
	uiLabelMap.DeliveryDoc = "${StringUtil.wrapString(uiLabelMap.DeliveryDoc)}";
	
	uiLabelMap.MissingManufactureDate = "${StringUtil.wrapString(uiLabelMap.MissingManufactureDate)}";
	
	uiLabelMap.BLExportQuantityLessThanRequiredQuantity = "${StringUtil.wrapString(uiLabelMap.BLExportQuantityLessThanRequiredQuantity)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
	uiLabelMap.BLLocationCodeSum = "${StringUtil.wrapString(uiLabelMap.BLLocationCodeSum)}";
	uiLabelMap.BLSGCTicket = "${StringUtil.wrapString(uiLabelMap.BLSGCTicket)}";
	uiLabelMap.UpdateSuccessfully = "${StringUtil.wrapString(uiLabelMap.UpdateSuccessfully)}";
	uiLabelMap.UpdateError = "${StringUtil.wrapString(uiLabelMap.UpdateError)}";
	uiLabelMap.BLSearchProductInDelivery = "${StringUtil.wrapString(uiLabelMap.BLSearchProductInDelivery)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.LogYes = "${StringUtil.wrapString(uiLabelMap.LogYes)}";
	uiLabelMap.LogNO = "${StringUtil.wrapString(uiLabelMap.LogNO)}";
	uiLabelMap.IsPromo = "${StringUtil.wrapString(uiLabelMap.IsPromo)}";
	
	uiLabelMap.Canceled = "${StringUtil.wrapString(uiLabelMap.Canceled)}";
	uiLabelMap.BLFacilityId = "${StringUtil.wrapString(uiLabelMap.BLFacilityId)}";
	uiLabelMap.FacilityName = "${StringUtil.wrapString(uiLabelMap.FacilityName)}";
	
	uiLabelMap.BLNotFound = "${StringUtil.wrapString(uiLabelMap.BLNotFound)}";
	uiLabelMap.DestAddress = "${StringUtil.wrapString(uiLabelMap.DestAddress)}";
	uiLabelMap.BLQuickView = "${StringUtil.wrapString(uiLabelMap.BLQuickView)}";
	
</script>
<script type="text/javascript" src="/logresources/js/delivery/salesDelivery.js?v=1.1.3"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js?v=1.0.6"></script>
