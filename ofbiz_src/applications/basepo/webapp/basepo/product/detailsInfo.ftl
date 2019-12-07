<script type="text/javascript" src="/poresources/js/product/detailsInfo.js"></script>

<div class="row-fluid">
	<div class="span3"><label class="text-right asterisk">${uiLabelMap.DmsProductWeight}</label></div>
	<div class="span3"><div id="txtWeight"></div></div>
	<div class="span3"><label class="text-right asterisk">${uiLabelMap.DmsNetWeight}</label></div>
	<div class="span3"><div id="txtProductWeight"></div></div>
</div>
<div class="row-fluid margin-top20">
	<div class="span3"><label class="text-right asterisk">${uiLabelMap.DmsProductWeightUomId}</label></div>
	<div class="span3"><div id="txtWeightUomId"></div></div>
	<div class="span3"><label class="text-right asterisk">${uiLabelMap.DmsQuantityUomId}</label></div>
	<div class="span3">
		<div id="txtQuantityUomId"></div>
		<li id="addNewConfig" class="green icon-plus" title="${uiLabelMap.addNewConfigPacking}" style="margin-left: 225px;margin-top: -22px;position: absolute; cursor: pointer; display: none;"></li>
	</div>
</div>
<div class="row-fluid margin-top20">
	<div class="span3"><label class="text-right asterisk">${uiLabelMap.DACurrencyUomId}</label></div>
	<div class="span3"><div id="txtCurrencyUomId"></div></div>
	<div class="span3"><label class="text-right">${uiLabelMap.DmsProductDefaultPrice}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span3"><div id="txtProductDefaultPrice"></div></div>
</div>
<div class="row-fluid margin-top20">
	<div class="span3"><label class="text-right">${uiLabelMap.DmsProductListPrice}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span3"><div id="txtProductListPrice"></div></div>
	<div class="span3 hide"><label class="text-right asterisk">${uiLabelMap.TaxInPrice}</label></div>
	<div class="span3 hide"><div id="txtTaxInPrice"></div></div>
	<div class="span3 hide"><label class="text-right">${uiLabelMap.DmsDayN}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span3 hide"><div id="txtDayN"></div></div>
</div>
<div class="row-fluid margin-top20">
	<div class="span3 hide"><label class="text-right">${uiLabelMap.DmsShelflife}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span3 hide"><div id="txtShelflife"></div></div>
</div>

<div id="popupConfigPacking" style="display:none;">
	<div>${uiLabelMap.addNewConfigPacking}</div>
	<div style="overflow-y: hidden;">
		<div class="row-fluid">
			<div class="row-fluid" style="margin-top: 10px;">
				<div class="span5" style="text-align: right;"><label class="" style="margin-top: 4px;">${uiLabelMap.uomFromId}:</label></div>
				<div class="span7"><div id="uomFromId1"></div></div>
			</div>
			<div class="row-fluid" style="margin-top: 10px;">
				<div class="span5" style="text-align: right;"><label class="" style="margin-top: 4px;">${uiLabelMap.uomToId}:</label></div>
				<div class="span7"><label id="uomFromIdBaseProduct"></label></div>
			</div>
			<div class="row-fluid" style="margin-top: 10px;">
				<div class="span5" style="text-align: right;"><label class="" style="margin-top: 4px;">${uiLabelMap.QuantityConvert}:</label></div>
				<div class="span7"><div id="quantityConvert1"></div></div>
			</div>
			<div class="row-fluid" style="margin-top: 10px;">
				<div class="span5" style="text-align: right;"><label class="" style="margin-top: 4px;">${uiLabelMap.AvailableFromDate}:</label></div>
				<div class="span7"><div id="fromDate1"></div></div>
			</div>
			<div class="row-fluid" style="margin-top: 10px;">
				<div class="span5" style="text-align: right;"><label class="" style="margin-top: 4px;">${uiLabelMap.AvailableThruDate}:</label></div>
				<div class="span7"><div id="thruDate1"></div></div>
			</div>
		</div>
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id="configPackingCancel" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
				<button id="configPackingSave" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.Save}</button>
			</div>
		</div>
	</div>
</div>
