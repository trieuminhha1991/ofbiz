<div id="approveHRPlanningWindow" class="hide">
	<div>${uiLabelMap.ApproveHRPlanning}</div>
	<div class='form-window-container'>
		<div class="form-window-content" style="position: relative;">
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}</label>
				</div>
				<div class="span8">
					<input type="text" id="emplPositionTypeIdApprove">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HRPlanningShort)}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span6">
								<div id="monthCustomTimeApprove"></div>
							</div>
							<div class="span6">
								<div id="yearCustomTimeApprove"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.CommonQty)}</label>
				</div>
				<div class="span8">
					<div id="quantityApprove"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.CommonStatus)}</label>
				</div>
				<div class="span8">
					<input type="text" id="statusPlanningApprove">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HRApprove)}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span4">
								<div id="acceptHRPlanning">${uiLabelMap.HRCommonAccept}</div>		
							</div>
							<div class="span4">
								<div id="rejectHRPlanning">${uiLabelMap.HRReject}</div>
							</div>
							<div class="span4">
								<div id="cancelHRPlanning">${uiLabelMap.HRCommonCancel}</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.CommonReason)}</label>
				</div>
				<div class="span8">
					<input type="text" id="commentApprove">
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="ajaxLoadingApprove" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAjaxApprove"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancelApprove" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSaveApprove" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.Save}</button>
					<button type="button" class="btn btn-success form-action-button pull-right" id="saveAndContinueApprove"><i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>
				</div>
			</div>
		</div>
	</div>	
</div>

<script type="text/javascript" src="/hrresources/js/recruitment/ApproveHRPlanning.js"></script>