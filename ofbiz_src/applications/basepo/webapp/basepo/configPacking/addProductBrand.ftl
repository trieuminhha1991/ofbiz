<script src="/poresources/js/configPacking/addProductBrand.js"></script>
<style>
	label {
		margin-top: 5px;
	}
	.row-fluid {
		min-height: 40px;
	}
</style>

<div id="jqxwindowAddBrand" style="display:none;">
	<div>${uiLabelMap.POAddProductBrand}</div>
	<div>
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.POBrandId}</label></div>
			<div class="span8"><input type="text" id="PartyCode" tabindex="5" /></div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.POBrandName}</label></div>
			<div class="span8"><input type="text" id="GroupName" tabindex="6" /></div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span12">
				<button id="btnCancel" tabindex="8" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="btnSave" tabindex="7" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>