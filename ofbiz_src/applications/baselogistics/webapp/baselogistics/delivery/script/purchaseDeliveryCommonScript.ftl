<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/logresources/js/logisticsCommon.js?v=1.0.5"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js?v=1.0.5"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.extend.js"></script>

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
<script type="text/javascript">
	var quickClick = 0;
	var saveClick = 0;
	var checkContinue = false;
	var is = 0;
	var deliveryItemData = [];
	var listItems = [];
	var listEditedItems = [];
	var glDeliveryId;
    var glOriginFacilityId;
    var glDestFacilityId;
    var glDeliveryStatusId;
    var glOrderId;
    var isAdmin = false;
    var inOrderDetail = false;
    var listLocationData = [];
    var locationAdapter = null;
    var listProductToAdd = [];
    var facilitySelected = null;
    var facilityData = [];
	<#assign localeStr = "VI" />
		var localeStr = "VI";
	<#if locale = "en">
	<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	var company = '${company}';
	
	<#assign hasRoles = false>
	<#if hasOlbPermission("MODULE", "LOG_DELIVERY", "ADMIN")>
		<#assign hasRoles = true>
	</#if>
	
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var uomData = [];
	<#list uoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)/>
		row['quantityUomId'] = "${item.uomId?if_exists}";
		row['description'] = "${description?if_exists?replace('\n', ' ')}";
		uomData.push(row);
	</#list> 

	<#assign uomConversions = delegator.findList("UomConversion", null, null, null, null, false) />
	var uomConvertData = new Array();
	<#list uomConversions as item>
		var row = {};
		row['uomId'] = "${item.uomId}";
		row['uomIdTo'] = "${item.uomIdTo}";
		row['conversionFactor'] = "${item.conversionFactor}";
		uomConvertData.push(row);
	</#list>

	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list weightUoms as item>
		var row = {};
		<#assign abbreviation = StringUtil.wrapString(item.get("abbreviation", locale)) />
		row['uomId'] = "${item.uomId}";
		row['description'] = "${abbreviation?if_exists}";
		weightUomData.push(row);
	</#list>
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_STATUS"), null, null, null, false)/>
	var statusData = [];
	<#list statuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get('description', locale))>
		row['statusId'] = "${item.statusId}";
		row['description'] = "${description}";
		statusData.push(row);
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
	
	<#assign wuoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list wuoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("abbreviation", locale)) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${description}';
		weightUomData.push(row);
	</#list>
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_ITEM_STATUS"), null, null, null, false)>
	var dlvItemStatusData = [];
	<#list statuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale)) />
		row['statusId'] = '${item.statusId}';
		row['description'] = '${description}';
		dlvItemStatusData.push(row);
	</#list>
	
	<#if hasOlbPermission("MODULE", "LOG_DELIVERY", "ADMIN")>
		isAdmin = true;
	</#if>
	
	function getUomDescription(uomId) {
		for (var i = 0; i < quantityUomData.length; i ++){
			if (quantityUomData[i].uomId == uomId){
				return '<span style=\"text-align: right\">' + quantityUomData[i].description +'</span>';
			}
		}
		for (var i = 0; i < weightUomData.length; i ++){
			if (weightUomData[i].uomId == uomId){
				return '<span style=\"text-align: right\">' + weightUomData[i].description +'</span>';
			}
		}
	}
	
	var deliveryTypeId = "DELIVERY_PURCHASE";
	
	var url = "jqxGeneralServicer?sname=getListDelivery&deliveryTypeId="+deliveryTypeId;
	var gridResult = '#jqxgridDelivery';
	var listProductSelected = [];
	var searchDescription = "${StringUtil.wrapString(uiLabelMap.BLSearchPurchaseDeliveryByProduct)}";
	
	var extendToolbar = function(container){
		var str = "<div id='productSearch' class='pull-right margin-top5'><div id='jqxgridListProduct' style='margin-top: 4px;'></div></div>";
		container.append(str);
	}
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
	uiLabelMap.CommonCancel	= "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK	= "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.MustUploadScanFile = "${StringUtil.wrapString(uiLabelMap.MustUploadScanFile)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.TheBatchFieldNotYetBeEntered	= "${StringUtil.wrapString(uiLabelMap.TheBatchFieldNotYetBeEntered)}";
	uiLabelMap.wgpagergotopagestring	= "${StringUtil.wrapString(uiLabelMap.wgpagergotopagestring)}";
	uiLabelMap.wgpagershowrowsstring	= "${StringUtil.wrapString(uiLabelMap.wgpagershowrowsstring)}";
	uiLabelMap.wgpagerrangestring	= "${StringUtil.wrapString(uiLabelMap.wgpagerrangestring)}";
	uiLabelMap.wgpagernextbuttonstring	= "${StringUtil.wrapString(uiLabelMap.wgpagernextbuttonstring)}";
	uiLabelMap.wgpagerpreviousbuttonstring	= "${StringUtil.wrapString(uiLabelMap.wgpagerpreviousbuttonstring)}";
	uiLabelMap.wgsortascendingstring	= "${StringUtil.wrapString(uiLabelMap.wgsortascendingstring)}";
	uiLabelMap.wgsortdescendingstring	= "${StringUtil.wrapString(uiLabelMap.wgsortdescendingstring)}";
	uiLabelMap.wgsortremovestring	= "${StringUtil.wrapString(uiLabelMap.wgsortremovestring)}";
	uiLabelMap.wgemptydatastring	= "${StringUtil.wrapString(uiLabelMap.wgemptydatastring)}";
	uiLabelMap.wgfilterselectstring	= "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}";
	uiLabelMap.wgfilterselectallstring	= "${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}";
	uiLabelMap.filterchoosestring	= "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
	uiLabelMap.wgdragDropToGroupColumn	= "${StringUtil.wrapString(uiLabelMap.wgdragDropToGroupColumn)}";
	uiLabelMap.wgtodaystring	= "${StringUtil.wrapString(uiLabelMap.wgtodaystring)}";
	uiLabelMap.wgclearstring	= "${StringUtil.wrapString(uiLabelMap.wgclearstring)}";
	uiLabelMap.NumberGTZ	= "${StringUtil.wrapString(uiLabelMap.NumberGTZ)}";
	uiLabelMap.Scan	= "${StringUtil.wrapString(uiLabelMap.Scan)}";
	uiLabelMap.AttachFileScan	= "${StringUtil.wrapString(uiLabelMap.AttachFileScan)}";
	uiLabelMap.CannotBeforeNow	= "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
	uiLabelMap.DeliveryDoc	= "${StringUtil.wrapString(uiLabelMap.DeliveryDoc)}";
	uiLabelMap.DItemMissingFieldsExp	= "${StringUtil.wrapString(uiLabelMap.DItemMissingFieldsExp)}";
	uiLabelMap.AreYouSureApprove	= "${StringUtil.wrapString(uiLabelMap.AreYouSureApprove)}";
	uiLabelMap.Approve	= "${StringUtil.wrapString(uiLabelMap.Approve)}";
	uiLabelMap.NameOfImagesMustBeLessThan50Character	= "${StringUtil.wrapString(uiLabelMap.NameOfImagesMustBeLessThan50Character)}";
	uiLabelMap.AreYouSureConfirm	= "${StringUtil.wrapString(uiLabelMap.AreYouSureConfirm)}";
	uiLabelMap.AllItemInListMustBeUpdated	= "${StringUtil.wrapString(uiLabelMap.AllItemInListMustBeUpdated)}";
	uiLabelMap.ActualReceivedDateMustAfterActualStartDeliveryDate	= "${StringUtil.wrapString(uiLabelMap.ActualReceivedDateMustAfterActualStartDeliveryDate)}";
	uiLabelMap.CannotAfterNow	= "${StringUtil.wrapString(uiLabelMap.CannotAfterNow)}";
	uiLabelMap.ArrivalDateEstimatedMustBeAfterExportDateEstimated	= "${StringUtil.wrapString(uiLabelMap.ArrivalDateEstimatedMustBeAfterExportDateEstimated)}";
	uiLabelMap.MustBeBeforeRequiredDate	= "${StringUtil.wrapString(uiLabelMap.MustBeBeforeRequiredDate)}";
	uiLabelMap.Shipping	= "${StringUtil.wrapString(uiLabelMap.Shipping)}";
	uiLabelMap.Received	= "${StringUtil.wrapString(uiLabelMap.Received)}";
	uiLabelMap.ReceiveNote	= "${StringUtil.wrapString(uiLabelMap.ReceiveNote)}";
	uiLabelMap.ConfirmDatetimeProviderStartExport	= "${StringUtil.wrapString(uiLabelMap.ConfirmDatetimeProviderStartExport)}";
	uiLabelMap.UpdateActualReceivedQuantity	= "${StringUtil.wrapString(uiLabelMap.UpdateActualReceivedQuantity)}";
	uiLabelMap.MissingActualDeliveredQty	= "${StringUtil.wrapString(uiLabelMap.MissingActualDeliveredQty)}";
	uiLabelMap.MissingExpireDate	= "${StringUtil.wrapString(uiLabelMap.MissingExpireDate)}";
	uiLabelMap.MissingManufactureDate	= "${StringUtil.wrapString(uiLabelMap.MissingManufactureDate)}";
	uiLabelMap.PleaseChooseProductAndFulfillData	= "${StringUtil.wrapString(uiLabelMap.PleaseChooseProductAndFulfillData)}";
	uiLabelMap.DLYItemComplete	= "${StringUtil.wrapString(uiLabelMap.DLYItemComplete)}";
	uiLabelMap.MissingBacth	= "${StringUtil.wrapString(uiLabelMap.MissingBacth)}";
	uiLabelMap.DropFileOrClickToChoose	= "${StringUtil.wrapString(uiLabelMap.DropFileOrClickToChoose)}";
	uiLabelMap.ThisFieldMustNotByContainSpecialCharacter = "${StringUtil.wrapString(uiLabelMap.ThisFieldMustNotByContainSpecialCharacter)}";
	uiLabelMap.HasOverTheRequiredTimeToReceive	= "${StringUtil.wrapString(uiLabelMap.HasOverTheRequiredTimeToReceive)}";
	uiLabelMap.AreYouSureSend = "${StringUtil.wrapString(uiLabelMap.AreYouSureSend)}";
	uiLabelMap.ApproveDelivery	= "${StringUtil.wrapString(uiLabelMap.ApproveDelivery)}";
	uiLabelMap.WaitForApprove	= "${StringUtil.wrapString(uiLabelMap.WaitForApprove)}";
	uiLabelMap.ConfirmAndContinue	= "${StringUtil.wrapString(uiLabelMap.ConfirmAndContinue)}";
	uiLabelMap.MustUploadScanFileDelivery	= "${StringUtil.wrapString(uiLabelMap.MustUploadScanFileDelivery)}";
	uiLabelMap.AreYouSureCancel	= "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";
	uiLabelMap.OnlySupportFile	= "${StringUtil.wrapString(uiLabelMap.OnlySupportFile)}";
	uiLabelMap.For	= "${StringUtil.wrapString(uiLabelMap.For)}";
	uiLabelMap.Product	= "${StringUtil.wrapString(uiLabelMap.Product)}";
	uiLabelMap.ReceiveNoteIdExisted	= "${StringUtil.wrapString(uiLabelMap.ReceiveNoteIdExisted)}";
	uiLabelMap.BLCompleted	= "${StringUtil.wrapString(uiLabelMap.BLCompleted)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.BSViewDetail = "${StringUtil.wrapString(uiLabelMap.BSViewDetail)}";
	uiLabelMap.ExportPdf = "${StringUtil.wrapString(uiLabelMap.ExportPdf)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.BLPackingForm = "${StringUtil.wrapString(uiLabelMap.BLPackingForm)}";
	uiLabelMap.BLQuantityByQCUom = "${StringUtil.wrapString(uiLabelMap.BLQuantityByQCUom)}";
	uiLabelMap.BLQuantityByEAUom = "${StringUtil.wrapString(uiLabelMap.BLQuantityByEAUom)}";
	uiLabelMap.BLQuantityEATotal = "${StringUtil.wrapString(uiLabelMap.BLQuantityEATotal)}";
	uiLabelMap.ManufacturedDateSum = "${StringUtil.wrapString(uiLabelMap.ManufacturedDateSum)}";
	uiLabelMap.ExpiredDateSum = "${StringUtil.wrapString(uiLabelMap.ExpiredDateSum)}";
	uiLabelMap.Batch = "${StringUtil.wrapString(uiLabelMap.Batch)}";
	uiLabelMap.IsPromo = "${StringUtil.wrapString(uiLabelMap.IsPromo)}";
	uiLabelMap.LogYes = "${StringUtil.wrapString(uiLabelMap.LogYes)}";
	uiLabelMap.LogNO = "${StringUtil.wrapString(uiLabelMap.LogNO)}";
	uiLabelMap.BLLocationCode = "${StringUtil.wrapString(uiLabelMap.BLLocationCode)}";
	uiLabelMap.BLSearchPurchaseDeliveryByProduct = "${StringUtil.wrapString(uiLabelMap.BLSearchPurchaseDeliveryByProduct)}";
	
	uiLabelMap.BLReceiptNoteWithPrice = "${StringUtil.wrapString(uiLabelMap.BLReceiptNoteWithPrice)}";
	uiLabelMap.BLLocationCode = "${StringUtil.wrapString(uiLabelMap.BLLocationCode)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.ParentLocation = "${StringUtil.wrapString(uiLabelMap.ParentLocation)}";
	uiLabelMap.Canceled = "${StringUtil.wrapString(uiLabelMap.Canceled)}";
	uiLabelMap.ReceiptNote = "${StringUtil.wrapString(uiLabelMap.ReceiptNote)}";
	uiLabelMap.BLFacilityId = "${StringUtil.wrapString(uiLabelMap.BLFacilityId)}";
	uiLabelMap.FacilityName = "${StringUtil.wrapString(uiLabelMap.FacilityName)}";
	uiLabelMap.BLQuickView = "${StringUtil.wrapString(uiLabelMap.BLQuickView)}";
    uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";

</script>

<script type="text/javascript" src="/logresources/js/delivery/purchaseDelivery.js?v=1.1.6"></script>
<script type="text/javascript" src="/logresources/js/searchProduct.js?v=1.0.5"></script>