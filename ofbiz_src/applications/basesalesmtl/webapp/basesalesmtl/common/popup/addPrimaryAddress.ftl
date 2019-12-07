<script src="/salesmtlresources/js/common/addPrimaryAddress.js"></script>

<div id="jqxwindowAddPrimaryAddress" style="display:none;">
	<div id="addAddressTitle">${uiLabelMap.DmsAddAddress}</div>
	<div>
		
		<div class="row-fluid form-horizontal form-window-content-custom">
			<div class="span6">
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsCountry}</label></div>
					<div class="span7"><div id="txtCountry"></div></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="text-right">${uiLabelMap.DmsCounty}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span7"><div id="txtCounty" tabindex="8"></div></div>
				</div>
			</div>
			<div class="span6">
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsProvince}</label></div>
					<div class="span7"><div id="txtProvince" tabindex="7"></div></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="text-right">${uiLabelMap.DmsWard}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span7"><div id="txtWard" tabindex="9"></div></div>
				</div>
			</div>
		</div>	
	
		<input type="hidden" id="txtContactMechId" />
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id="cancelAddAddress" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
				<button id="saveAddAddress" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>