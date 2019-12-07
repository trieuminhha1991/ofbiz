<script src="/crmresources/js/crmsetting/addCommunicationSubject.js"></script>

<div id="addCommunicationSubject" style="display:none;">
	<div>${uiLabelMap.KCommunicationSubject}</div>
	<div class="form-window-content-custom">
		<div class="row-fluid margin-top10">
			<div class="span5"><label class="text-right asterisk">${uiLabelMap.KCommSubjectCode}</label></div>
			<div class="span7"><input type="text" id="SubjectCode" tabindex="5" /></div>
		</div>
		<div class="row-fluid">
			<div class="span5"><label class="text-right">${uiLabelMap.KSequenceId}&nbsp;&nbsp;&nbsp;</label></div>
			<div class="span7"><div id="SequenceId" tabindex="6"></div></div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span5"><label class="text-right asterisk">${uiLabelMap.BSDescription}</label></div>
			<div class="span7"><input type="text" id="Description" tabindex="7" /></div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span12">
				<button id="btnCancel" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="btnSave" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>