<div id="apprTrainCourseWindow" class="hide">
	<div>${uiLabelMap.ApprovalTrainingCourse}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span3 text-algin-right">
					<label class="asterisk">${uiLabelMap.HRApprove}</label>
				</div>
				<div class="span9">
					<div class="row-fluid">
						<div class="span12">
							<div class="span4">
								<div id="acceptTrainingPpsl">${uiLabelMap.CommonApprove}</div>		
							</div>
							<div class="span4">
								<div id="rejectTrainingPpsl">${uiLabelMap.HRReject}</div>
							</div>
							<div class="span3">
								<div id="cancelTrainingPpsl">${uiLabelMap.HRCommonCancel}</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span3 text-algin-right">
					<label class="">${uiLabelMap.HRNotes}</label>
				</div>
				<div class="span9">
					<textarea id="changeReason"></textarea>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelAppr" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAppr">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/training/ApprovalTrainingCourse.js"></script>