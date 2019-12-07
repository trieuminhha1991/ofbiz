<div id="alterpopupWindowAddSupplierProduct" style="display:none;">
	<div style="font-size:18px!important;">${uiLabelMap.POAddNewProductSupplier}</div>
	<div style="overflow: hidden;">

		<div class="row-fluid">
			<div class="row-fluid span6" style="margin-top:10px;">
				<div class="span5">
					<label class="asterisk" style="margin-top: 3px; float: right;">${uiLabelMap.ProductSupplier}:</label>
				</div>
				<div class="span7">
					<div id="txtSupplier" style="width: 100%" class="">
						<div id="jqxgridSupplier">
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid span6" style="margin-top:10px;">
				<div class="span5 div-inline-block">
					<label class="asterisk" style="margin-top: 3px; float: right;">${uiLabelMap.ProductCurrencyUomId}:</label>
				</div>
				<div class="span7 div-inline-block">
					<div id="productCurrencyUomId"></div>
				</div>
			</div>
		</div>

		<div class="row-fluid">
			<div class="row-fluid span6" style="margin-top:10px;">
				<div class="span5">
					<label class="asterisk" style="margin-top: 3px; float: right;">${uiLabelMap.POProduct}:</label>
				</div>
				<div class="span7">
					<div id="productId" style="width: 100%" class="">
						<div id="jqxgridProduct">
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid span6" style="margin-top:10px;">
				<div class="span5 div-inline-block">
					<label class="asterisk" style="margin-top: 3px; float: right;">${uiLabelMap.BSProductPackingUomId}:</label>
				</div>
				<div class="span7 div-inline-block">
					<div id="quantityUomId"></div>
				</div>
			</div>
		</div>

		<div class="row-fluid">
			<div class="row-fluid span6" style="margin-top:10px;">
				<div class="span5 div-inline-block">
					<label class="asterisk" style="margin-top: 3px; float: right;">${uiLabelMap.BSPurchasePrice} (${uiLabelMap.BSBeforeTax}):</label>
				</div>
				<div class="span7 div-inline-block">
					<div id="lastPrice"></div>
				</div>
			</div>
			<div class="row-fluid span6" style="margin-top:10px;">
				<div class="span5">
					<label class="asterisk" style="margin-top: 3px; float: right;">${uiLabelMap.BSSupplierProductId}:</label>
				</div>
				<div class="span7">
					<input type="text" id="supplierProductId" style="width: 96%;" />
				</div>
			</div>
		</div>

		<div class="row-fluid">
			<div class="row-fluid span6" style="margin-top:10px;">
				<div class="span5">
					<label class="asterisk" style="margin-top: 3px; float: right;">${uiLabelMap.CommonFromDate}:</label>
				</div>
				<div class="span7">
					<div id="availableFromDate"></div>
				</div>
			</div>
			<div class="row-fluid span6" style="margin-top:10px;">
				<div class="span5 div-inline-block">
					<label class=""  style="margin-top: 3px; float: right;">${uiLabelMap.CommonThruDate}:</label>
				</div>
				<div class="span7 div-inline-block">
					<div id="availableThruDate"></div>
				</div>
			</div>
		</div>

		<div class="row-fluid">
			<div class="row-fluid span6" style="margin-top:10px;">
				<div class="span5">
					<label class="asterisk" style="margin-top: 3px; float: right;">${uiLabelMap.FormFieldTitle_minimumOrderQuantity}:</label>
				</div>
				<div class="span7">
					<div id="minimumOrderQuantity"></div>
				</div>
			</div>
			<div class="hidden" style="margin-top:10px;">
				<div class="hidden">
					<label class="" style="margin-top: 3px; float: right;">${uiLabelMap.FormFieldTitle_shippingPrice}:</label>
				</div>
				<div class="hidden">
					<div id="shippingPrice"></div>
				</div>
			</div>
			<div class="row-fluid span6" style="margin-top:10px;">
				<div class="span5 div-inline-block">
					<label class=""  style="margin-top: 3px; float: right;">${uiLabelMap.FormFieldTitle_canDropShip}:</label>
				</div>
				<div class="span7 div-inline-block">
					<div id="canDropShip"></div>
				</div>
			</div>
		</div>

		<hr style="margin: 10px; border-top: 1px solid grey;opacity: 0.4;">
		<div class="row-fluid">
			<button id="alterCancel" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
			<button id="createAndContinue" class="btn btn-success form-action-button pull-right"><i class="fa-plus"></i> ${uiLabelMap.SaveAndContinue}</button>
			<button id="alterSave" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<script type="text/javascript" src="/poresources/js/productSupplier/productSupplierNewForPopup.js?v=0.0.7"></script>
<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
	uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
	uiLabelMap.wgcreatesuccess = "${StringUtil.wrapString(uiLabelMap.wgcreatesuccess)}";
	uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
</script>
