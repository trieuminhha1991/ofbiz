<div id="jqxwindowAddressEditor" style="display:none;">
	<div>${uiLabelMap.DmsAddAddress}</div>
	<div>
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="text-right">${uiLabelMap.DmsCountry}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span7"><div id="txtCountry"></div></div>
				</div>
			</div>
			<div class="span5">
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsProvince}</label></div>
					<div class="span7"><div id="txtProvince"></div></div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="text-right">${uiLabelMap.DmsCounty}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span7"><div id="txtCounty"></div></div>
				</div>
			</div>
			<div class="span5">
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="text-right">${uiLabelMap.DmsWard}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span7"><div id="txtWard"></div></div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsAddress1}</label></div>
					<div class="span7">
						<textarea rows="3" cols="50" id="tarAddress1" style="resize: none;margin: 0px !important;  width: 530px;"></textarea>
					</div>
				</div>
			</div>
		</div>
		<input type="hidden" id="partyIdAvalible" />
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id="cancelEditAddress" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="saveEditAddress" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
