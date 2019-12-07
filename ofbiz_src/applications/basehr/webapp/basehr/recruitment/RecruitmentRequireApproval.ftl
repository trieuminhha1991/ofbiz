 <div id="recruitRequireApprWindow" class="hide">
	<div>${uiLabelMap.RecruitmentRequireApproval}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div id="panelRecruitmentRequireConds">
				<div style="padding: 20px 7px 0">
					<div class="row-fluid">
						<div class="span12">
							<div class="span6">
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${StringUtil.wrapString(uiLabelMap.OrganizationalUnit)}</label>
									</div>
									<div class="span8">
										<input id="partyIdAppr" type="text">
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${StringUtil.wrapString(uiLabelMap.HrCommonPositionRequire)}</label>
									</div>
									<div class="span8">
										<input id="emplPositionTypeIdAppr" type="text">
									</div>
								</div>
								
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${StringUtil.wrapString(uiLabelMap.AccordingApprovedPlan)}</label>
									</div>
									<div class="span8">
										<div class="row-fluid">
											<div class="span4">
												<div style='padding-top: 6px; margin-left: 0px !important' id='plannedRadioBtn'><span style="font-size: 14px">${uiLabelMap.CommonYes}</span></div>
											</div>
											<div class="span4">
												<div style='padding-top: 6px; margin-left: 0px !important' id='unplannedRadioBtn'><span style="font-size: 14px">${uiLabelMap.CommonNo}</span></div>
											</div>
										</div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${StringUtil.wrapString(uiLabelMap.PlannedRecruitmentShort)}</label>
									</div>
									<div class="span8">
										<div id="quantityAppr"></div>
									</div>
								</div>	
							</div>
							<div class="span6">
								<div class='row-fluid margin-bottom10'>
									<div class="span5 text-algin-right">
										<label class="">${StringUtil.wrapString(uiLabelMap.TimeNeedEmployeeShort)}</label>
									</div>
									<div class="span7">
										<input id="monthYearAppr" type="text">
									</div>
								</div>	
								<div class='row-fluid margin-bottom10'>
									<div class="span5 text-algin-right">
										<label class="">${StringUtil.wrapString(uiLabelMap.RecruitmentRequestor)}</label>
									</div>
									<div class="span7">
										<input id="partyIdCreated" type="text">
									</div>
								</div>	
								<div class='row-fluid margin-bottom10'>
									<div class="span5 text-algin-right">
										<label class="">${StringUtil.wrapString(uiLabelMap.RecruitmentFormType)}</label>
									</div>
									<div class="span7">
										<input id="recruitmentFormTypeAppr" type="text">
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span5 text-algin-right">
										<label class="">${StringUtil.wrapString(uiLabelMap.QuantityUnplannedShort)}</label>
									</div>
									<div class="span7">
										<div id="quantityUnplannedAppr"></div>
									</div>
								</div>	
							</div>
						</div>
					</div>	
					<div class="row-fluid">
						<div class="span12">
							<div class='row-fluid margin-bottom10'>
								<div class='span2 align-right'>
									<label class="">${uiLabelMap.RecruitmentReason}</label>
								</div>
								<div class="span10">
									<label id="commentRecruitment">${uiLabelMap.HRCommonNotSetting}</label>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class="span2 text-algin-right">
									<label class="">${StringUtil.wrapString(uiLabelMap.CommonStatus)}</label>
								</div>
								<div class="span10">
									<input id="currStatusId" type="text">
								</div>
							</div>	
								
							<div class='row-fluid margin-bottom10'>
								<div class="span2 text-algin-right">
									<label class="">${StringUtil.wrapString(uiLabelMap.RequirePositionRecruited)}</label>
								</div>
								<div class="span10">
									<div id="recruitmentReqCondGridAppr"></div>
								</div>
							</div>	
							<div class='row-fluid margin-bottom10'>
								<div class="span2 text-algin-right">
									<label class="">${StringUtil.wrapString(uiLabelMap.HRNotes)}</label>
								</div>
								<div class="span10">
									<textarea id="commentApproval"></textarea>
								</div>
							</div>	
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div id="recruitRequireCondGrid"></div>
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
						<button id="alterCancelAppr" class='btn btn-success form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
						<button id="cancelApproval" style="display: none;" class='btn btn-danger form-action-button pull-right'><i class='fa fa-ban'></i>${uiLabelMap.HRCommonCancel}</button>
						<button id="rejectApproval" style="display: none;" class='btn form-action-button pull-right'><i class='fa fa-thumbs-down'></i>${uiLabelMap.CommonReject}</button>
						<button id="acceptApproval" style="display: none;" class='btn btn-primary form-action-button pull-right'><i class='fa fa-thumbs-up'></i>${uiLabelMap.CommonApprove}</button>
					</div>
			</div>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitmentRequireApproval.js"></script>