<script type="text/javascript" src="/salesresources/js/product/addProductPrice.js"></script>
<style>
	label {
		margin-top: 5px;
	}
	.row-fluid {
		min-height: 40px;
	}
</style>

<div id="addProductPrice" style="display:none;">
	<div>${uiLabelMap.CommonAdd} ${uiLabelMap.ProductPrice}</div>
	<div>
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid margin-top10">
					<div class="span4"><label class="text-right">${uiLabelMap.ProductProductId}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span8">
						<label class="green" id="divProductId"></label>
					</div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span4"><label class="text-right asterisk">${uiLabelMap.ProductPrice}</label></div>
					<div class="span8"><div id="divProductPrice"></div></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span4"><label class="text-right asterisk">${uiLabelMap.DmsQuantityUomId}</label></div>
					<div class="span8"><div id="divQuantityUomId"></div></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span4"><label class="text-right asterisk">${uiLabelMap.DmsFromDate}</label></div>
					<div class="span8"><div id="divFromDate"></div></div>
				</div>
			</div>
			<div class="span6">
				<div class="row-fluid margin-top10">
					<div class="span4"></div>
					<div class="span8"></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span4"><label class="text-right asterisk">${uiLabelMap.ProductPriceType}</label></div>
					<div class="span8"><div id="divProductPriceType"></div></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span4"><label class="text-right asterisk">${uiLabelMap.ProductCurrencyUom}</label></div>
					<div class="span8"><div id="divProductCurrencyUom"></div></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span4"><label class="text-right">${uiLabelMap.DmsThruDate}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span8"><div id="divThruDate"></div></div>
				</div>
			</div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span12">
				<button id="btnCancel" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="btnSave" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>