<script src="/crmresources/js/crmsetting/addCommunicationReason.js"></script>

<div id="addCommunicationReason" style="display:none;">
	<div>${uiLabelMap.CommonAdd}</div>
	<div class="form-window-content-custom">
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.KReasonClaimId}</label></div>
			<div class="span8"><input type="text" id="ReasonCode" tabindex="5" /></div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.KReasonType}</label></div>
			<div class="span8"><div id="ReasonType" tabindex="6" ></div></div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.KSequenceId}</label></div>
			<div class="span8"><input type="number" id="SequenceId" tabindex="7" /></div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.BSDescription}</label></div>
			<div class="span8"><input type="text" id="Description" tabindex="8" /></div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span12">
				<button id="btnCancel" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="btnSave" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
