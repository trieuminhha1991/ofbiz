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
	var saveClick = 0;
	var perAdmin = false;
	<#assign orderHeader = delegator.findOne("OrderHeader", {"orderId" : parameters.orderId?if_exists}, false)/>
	var listInv = [];
	var glDeliveryId;
	var actualStartDategl;
	var listDeliveryItemData = [];
	var glDeliveryStatusId;
	var orderStatus = "${orderHeader.statusId}"
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list weightUoms as item>
		var row = {};
		<#assign abbreviation = StringUtil.wrapString(item.get("abbreviation", locale)) />
		row['uomId'] = "${item.uomId}";
		row['description'] = "${abbreviation?if_exists}";
		weightUomData[${item_index}] = row;
	</#list>
	
	<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", company)), null, null, null, false)>
	var facilityData = [];
	<#list facilities as item>
		var row = {};
		<#assign descFac = StringUtil.wrapString(item.facilityName?if_exists)/>
		row['facilityId'] = "${item.facilityId?if_exists}";
		row['ownerPartyId']= "${item.ownerPartyId?if_exists}";
		row['description'] = "${descFac?if_exists}";
		row['productStoreId'] = "${item.productStoreId?if_exists}";
		facilityData.push(row);
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
	
	<#assign dlvStatuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_STATUS"), null, null, null, false)>
	var statusData = [];
	<#list dlvStatuses as item>
		var row = {};
		<#assign descDlvStatus = StringUtil.wrapString(item.get("description", locale)) />
		row['statusId'] = '${item.statusId}';
		row['description'] = '${descDlvStatus?if_exists}';
		statusData[${item_index}] = row;
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
	
	var orderId = "${orderId}";
	if (orderId){
		$.ajax({
			type: "POST",
			url: "getOrderRoleAndParty",
			data: {
				orderId: orderId,
			},
			async: false,
			success: function (res){
				listParty = res['listParties'];
			}
		});
	}
	var listPartyFrom = [];
	var listPartyTo = [];
	if (listParty && listParty.length > 0){
		for (var i = 0; i < listParty.length; i ++){
			var party = listParty[i];
			if (party.roleTypeId == "BILL_FROM_VENDOR"){
				listPartyFrom.push(party);
			}
			if (party.roleTypeId == "BILL_TO_CUSTOMER"){
				listPartyTo.push(party);
			}
		}
	}
	
	<#assign localeStr = "VI" />
	var localeStr = "VI";
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
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
	uiLabelMap.QuickCreateDeliveryNoteFromConsignistaFacility = "${StringUtil.wrapString(uiLabelMap.QuickCreateDeliveryNoteFromConsignistaFacility)}";
	
</script>
<script type="text/javascript" src="/salesresources/js/order/fastSalesDelivery.js"></script>