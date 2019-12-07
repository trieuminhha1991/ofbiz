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
	var listInv = [];
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	<#assign localeStr = "VI" />
	var localeStr = "VI";
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	<#if hasOlbPermission("MODULE", "LOG_DELIVERY", "ADMIN")>
		perAdmin = true;
	</#if>
	<#assign requirement = delegator.findOne("Requirement", {"requirementId" : parameters.requirementId?if_exists}, false)/>
	var listInv = [];
    var glDeliveryId;
	var requirementId = "${parameters.requirementId?if_exists}";
	
    <#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
    
    <#assign deliveryTypeEnums = delegator.findList("DeliveryTypeEnum", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumId", requirement.reasonEnumId?if_exists), null, null, null, false)/>
    <#if deliveryTypeEnums?has_content>
		<#assign deliveryTypeId = deliveryTypeEnums.get(0).deliveryTypeId?if_exists>
    </#if>
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_STATUS"), null, null, null, false)/>
	var statusData = [];
	<#list statuses as item>
		var row = {};
		<#assign descStatus = StringUtil.wrapString(item.get('description', locale))>
		row['statusId'] = "${item.statusId}";
		row['description'] = "${descStatus?if_exists}";
		statusData[${item_index}] = row;
	</#list>
	
	<#assign requirementStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "REQUIREMENT_STATUS")), null, null, null, false) />
	var reqStatusData2 = [
   	   	<#if requirementStatus?exists>
   	   		<#list requirementStatus as item>
   	   			{
   	   				statusId: "${item.statusId?if_exists}",
   	   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
   	   			},
   	   		</#list>
   	   	</#if>
   	];
	
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
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_ITEM_STATUS"), null, null, null, false)>
	var dlvItemStatusData = [];
	<#list statuses as item>
		var row = {};
		<#assign descDlvItemStatus = StringUtil.wrapString(item.get("description", locale)) />
		row['statusId'] = '${item.statusId}';
		row['description'] = '${descDlvItemStatus?if_exists}';
		dlvItemStatusData[${item_index}] = row;
	</#list>
	
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
</script>
<script type="text/javascript" src="/logresources/js/requirement/requirementDelivery.js"></script>
