<div id="editHRPlanningWindow" class="hide">
	<div>${uiLabelMap.CommonEdit}</div>
	<div class='form-window-container'>
		<div class="form-window-content" style="position: relative;">
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}</label>
				</div>
				<div class="span8">
					<input type="text" id="emplPositionTypeIdEdit">
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
								<div id="monthCustomTimeEdit"></div>
							</div>
							<div class="span6">
								<div id="yearCustomTimeEdit"></div>
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
					<div id="quantityEdit"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.CommonStatus)}</label>
				</div>
				<div class="span8">
					<input type="text" id="statusPlanningEdit">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.HRCommonApprover}</label>
				</div>
				<div class="span8">
					<div id="editListDropDownBtn">
						 <div style="border-color: transparent;" id="jqxGridEdit"></div>
					</div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="ajaxLoadingEdit" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAjaxEdit"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancelEdit" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSaveEdit" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.Save}</button>
					<button type="button" class="btn btn-success form-action-button pull-right" id="saveAndContinueEdit"><i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>
				</div>
			</div>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitmentEditHRPlanning.js"></script>