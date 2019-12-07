<div id="wdwApprove" style="display: none;">
	<div id="wdwHeaderApprove">
		<span>
		   ${uiLabelMap.HRApprove}
		</span>
	</div>
	<div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContentApprove">
		<div class="basic-form form-horizontal" style="margin-top: 10px">
			<div class="row-fluid" >
				<div class="span12">
					<div class="control-group no-left-margin">
						<label class="control-label" style="width: 100px !important">${uiLabelMap.HRApprove}:</label>
						<div class="controls" style="margin-left: 150px !important;">
							<div style='display: inline-block' id='jqxAccepted'><span>${uiLabelMap.HRCommonAccept}</span></div>
							<div style='display: inline-block' id='jqxRejected'><span>${uiLabelMap.HRCommonReject}</span></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label asterisk" style="width: 100px !important">${uiLabelMap.HRComment}:</label>
						<div class="controls" style="margin-left: 150px !important;">
							<textarea id="apprComment"></textarea>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancelAppr" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSaveAppr" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>
