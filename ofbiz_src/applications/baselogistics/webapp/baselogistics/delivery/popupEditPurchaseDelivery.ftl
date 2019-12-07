<script>
	var listProductToAdd = [];
	var glDeliveryId = "${parameters.deliveryId?if_exists}";
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var uomData = [];
	<#list uoms as item>
		var row = {};
		<#assign descPackingUom = StringUtil.wrapString(item.description?if_exists)/>
		row['uomId'] = "${item.uomId?if_exists}";
		row['description'] = "${descPackingUom?if_exists}";
		uomData.push(row);
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	<#list weightUoms as item>
		var row = {};
		<#assign abbreviation = StringUtil.wrapString(item.get("abbreviation", locale)) />
		row['uomId'] = "${item.uomId}";
		row['description'] = "${abbreviation?if_exists}";
		uomData.push(row);
	</#list>
	
	function getUomDesc(uomId) {
		for (var i = 0; i < uomData.length; i ++) {
			if (uomData[i].uomId == uomId) {
				return uomData[i].description;
			}
		}
	}
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BLQuantityGreateThanQuantityAvailable = "${StringUtil.wrapString(uiLabelMap.BLQuantityGreateThanQuantityAvailable)}";
	uiLabelMap.BLQuantityAvailable = "${StringUtil.wrapString(uiLabelMap.BLQuantityAvailable)}";
	uiLabelMap.CreatedNumberSum = "${StringUtil.wrapString(uiLabelMap.CreatedNumberSum)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.AreYouSureUpdate = "${StringUtil.wrapString(uiLabelMap.AreYouSureUpdate)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.UpdateSuccessfully = "${StringUtil.wrapString(uiLabelMap.UpdateSuccessfully)}";
	uiLabelMap.UpdateError = "${StringUtil.wrapString(uiLabelMap.UpdateError)}";
	uiLabelMap.IsPromo = "${StringUtil.wrapString(uiLabelMap.IsPromo)}";
	uiLabelMap.BLQuantityCurrent = "${StringUtil.wrapString(uiLabelMap.BLQuantityCurrent)}";
	uiLabelMap.BLQuantityWant = "${StringUtil.wrapString(uiLabelMap.BLQuantityWant)}";
	uiLabelMap.LogNO = "${StringUtil.wrapString(uiLabelMap.LogNO)}";
	uiLabelMap.LogYes = "${StringUtil.wrapString(uiLabelMap.LogYes)}";
	uiLabelMap.NumberGTOEZ = "${StringUtil.wrapString(uiLabelMap.NumberGTOEZ)}";
</script>

<div id="editPopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.BLDeliveryEdit}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid">
				<a style="float:right;font-size:14px; margin-right: 5px" id="editAddRow" href="javascript:dlvEditObj.editAddNewProduct()" data-rel="tooltip" title="${uiLabelMap.AddRow}" data-placement="bottom"><i class="icon-plus-sign open-sans"></i></a>
				<div><div id="editGridProduct"></div></div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="editCancel" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
			<button id="editSave" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div id="editAddProductWindow" class="hide popup-bound">
	<div>${uiLabelMap.BLAddProducts}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid">
				<div><div id="editAddProductGrid"></div></div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="editAddProductCancel" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
			<button id="editAddProductSave" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<script type="text/javascript" src="/logresources/js/delivery/popupEditPurchaseDelivery.js?v=1.1.1"></script>