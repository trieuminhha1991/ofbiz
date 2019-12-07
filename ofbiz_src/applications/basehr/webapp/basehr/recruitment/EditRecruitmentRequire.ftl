<style>
#commentPopoverEdit {
    z-index: 9999999;
}
.jqx-popover-modal-background { z-index: 20000; }
</style>
<div id="editRecruitmentRequireWindow" class="hide">
	<div>${uiLabelMap.EditRecruitmentRequirement}</div>
	<div class='form-window-container' style="position: relative;">
		<div class="row-fluid" style="position: absolute; width: calc(100% - 10px); top: 5px; right: 5px">
			<div class="alert alert-success">
				${uiLabelMap.CommonStatus}: <span id="recruitmentRequireStatusEdit"></span>
			</div>
		</div>
		<div class='form-window-content' style="margin-top: 40px; height: calc(100% - 80px)">
			<div class='row-fluid margin-bottom10'>
   				<div class="span3 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.OrganizationalUnit)}:</label>
				</div>
				<div class="span9">
					<label id="partyIdEdit"></label>
				</div>
   			</div>
			<div class='row-fluid margin-bottom10'>
   				<div class="span3 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.HrCommonPositionRequire)}:</label>
				</div>
				<div class="span9">
					<label id="emplPositionTypeIdEdit"></label>
				</div>
   			</div>
   			<div class='row-fluid margin-bottom10'>
   				<div class="span3 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.TimeNeedEmployee)}:</label>
				</div>
				<div class="span9">
					<label id="monthYearEdit" style="font-size: 14px"></label>
				</div>
   			</div>
   			<div class='row-fluid margin-bottom10'>
				<div class="span3 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.AccordingApprovedPlan)}</label>
				</div>
				<div class="span9">
					<div class="row-fluid">
						<div class="span12">
							<div class="span4">
								<div class="row-fluid">
									<div class="span12">
										<div class="span6">
											<div style='padding-top: 6px; margin-left: 0px !important' id='plannedRadioBtnEdit'><span style="font-size: 14px">${uiLabelMap.CommonYes}</span></div>
										</div>
										<div class="span6">
											<div style='padding-top: 6px; margin-left: 0px !important' id='unplannedRadioBtnEdit'><span style="font-size: 14px">${uiLabelMap.CommonNo}</span></div>
										</div>
									</div>
								</div>
							</div>
							<div class="span8">
								<div class='row-fluid'>
					   				<div class="span7 text-algin-right">
										<label class="">${StringUtil.wrapString(uiLabelMap.RecruitmentQuantityApproved)}:</label>
									</div>
									<div class="span5">
										<span id="recruitQtyApprovedEdit" style="font-size: 14px"></span>
									</div>
					   			</div>
							</div>
						</div>						
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
   				<div class="span3 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.PlannedRecruitment)}</label>
				</div>
				<div class="span9">
					<div class="row-fluid">
						<div class="span12">
							<div class="span4">
								<div id="quantityPlannedEdit"></div>
							</div>
							<div class="span8">
								<div class='row-fluid'>
					   				<div class="span7 text-algin-right">
										<label class="">${StringUtil.wrapString(uiLabelMap.QuantityUnplannedShort)}</label>
									</div>
									<div class="span5">
										<div id="quantityUnplannedEdit"></div>
									</div>
					   			</div>							
							</div>
						</div>
					</div>
				</div>
   			</div>
   			<div class='row-fluid margin-bottom10'>
				<div class='span3 align-right'>
					<label class="">${uiLabelMap.RecruitmentFormType}</label>
				</div>
				<div class="span9">
					<div id="recruitmentFormTypeEdit"></div>
				</div>
			</div>
   			<div class='row-fluid margin-bottom10'>
				<div class='span3 align-right'>
					<label class="">${uiLabelMap.RecruitmentReason}</label>
				</div>
				<div class="span9">
					<label id="recruitmentReasonEdit" style="display: inline-block;"></label>
					<a href="javascript:void(0)" style="display: none;" id="seeMoreCommentEdit">${uiLabelMap.HRSeeMore}</a>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span3 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.RequirePositionRecruited)}</label>
				</div>
				<div class="span9">
					<div id="recruitmentReqCondGridEdit"></div>
				</div>
			</div>
			<#--<!-- <div class='row-fluid margin-bottom10'>
				<div class='span3 align-right'>
					<label class="">${uiLabelMap.HRNotes}</label>
				</div>
				<div class="span9">
					<textarea id="changeReasonEdit"></textarea>
				</div>
			</div> -->
			<div class="row-fluid no-left-margin">
				<div id="loadingRecruitReqEdit" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerRecruitReqEdit"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelEditRecruitReq" class='btn btn-danger form-action-button pull-right'><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>				
					<button id="saveEditRecruitReq" class='btn btn-primary form-action-button pull-right' style="display: none;"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>				
				</div>
			</div>
		</div>
	</div>
</div>

<div id="commentPopoverEdit">
	<div id="commentPopoverEditContent" style="word-break: break-all;"></div>
</div>

<script type="text/javascript" src="/hrresources/js/recruitment/EditRecruitmentRequire.js"></script>