<script type="text/javascript" src="/poresources/js/product/supplierInfo.js"></script>

<div id="jqxwindowAddSupplier" style="display:none;">
	<div>${uiLabelMap.CommonAdd} ${uiLabelMap.DmsSupplier}</div>
	<div>
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid">
					<div class="span6"><label class="text-right asterisk">${uiLabelMap.ProductSupplier}</label></div>
					<div class="span6"><div id="supplierAdd"></div></div>
				</div>
				<div class="row-fluid">
					<div class="span6"><label class="text-right asterisk">${uiLabelMap.FormFieldTitle_supplierProductId}</label></div>
					<div class="span6"><input type="text" id="supplierProductId" /></div>
				</div>
				<div class="row-fluid">
					<div class="span6"><label class="text-right asterisk">${uiLabelMap.FormFieldTitle_minimumOrderQuantity}</label></div>
					<div class="span6"><div id="minimumOrderQuantity"></div></div>
				</div>
				<div class="row-fluid">
					<div class="span6"><label class="text-right asterisk">${uiLabelMap.FormFieldTitle_availableFromDate}</label></div>
					<div class="span6"><div id="availableFromDate"></div></div>
				</div>
				<div class="row-fluid">
					<div class="span6"><label class="text-right">${uiLabelMap.FormFieldTitle_shippingPrice}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span6"><div id="shippingPrice"></div></div>
				</div>
			</div>
			<div class="span6">
				<div class="row-fluid">
					<div class="span5"><label class="text-right asterisk">${uiLabelMap.ProductCurrencyUomId}</label></div>
					<div class="span7"><div id="productCurrencyUomId"></div></div>
				</div>
				<div class="row-fluid">
					<div class="span5"><label class="text-right">${uiLabelMap.FormFieldTitle_lastPrice}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span7"><div id="lastPrice"></div></div>
				</div>
				<div class="row-fluid">
					<div class="span5"><label class="text-right">${uiLabelMap.POAccComments}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span7"><input type="text" id="comments"/></div>
				</div>
				<div class="row-fluid">
					<div class="span5"><label class="text-right">${uiLabelMap.FormFieldTitle_availableThruDate}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span7"><div id="availableThruDate"></div></div>
				</div>
				<div class="row-fluid">
					<div class="span5"><label class="text-right">${uiLabelMap.FormFieldTitle_canDropShip}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span7"><div id="canDropShip"></div></div>
				</div>
			</div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span12">
				<button id="btnCancelSupplier" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="btnSaveSupplier" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
