<div id="ApprRecruitAnticipateWindow" class="hide">
	<div>${uiLabelMap.ApprovalRecruitAnticipate}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position: relative;">
			<#--<!-- <div class="row-fluid">
				<div class="span12">
					<div class="alert alert-success">
						${uiLabelMap.CommonStatus}: <span id="apprRecruitAnticipateStatus"></span>
					</div>
				</div>
			</div>	 -->
			<!-- <div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span5 align-right'>
								<label class="">${uiLabelMap.RecruitmentAnticipate}:</label>
							</div>
							<div class='span7'>
								<label id="apprYearCustomTime"></label>
							</div>					
						</div>
					</div>
				</div>
			</div> -->
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid'>
							<div class='span4 align-right'>
								<label class=""><b>${uiLabelMap.OrganizationalUnit}</b>:</label>
							</div>
							<div class='span8'>
								<label id="apprPartyId"></label>
							</div>					
						</div>
					</div>
					<div class="span6">
						<div class='row-fluid'>
							<div class='span4 align-right'>
								<label class=""><b>${uiLabelMap.HrCommonPosition}</b>:</label>
							</div>
							<div class='span8'>
								<label id="apprEmplPositionType"></label>
							</div>					
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12">
					<div id="containerapprRecruitAnticipateGrid" style="background-color: transparent; overflow: auto;">
				    </div>
				    <div id="jqxNotificationapprRecruitAnticipateGrid">
				        <div id="notificationContentapprRecruitAnticipateGrid">
				        </div>
				    </div>
					<div id="apprRecruitAnticipateGrid"></div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingApprRecruitAnticipateWindow" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerApprRecruitAnticipateWindow"></div>
				</div>
			</div>
			<#if security.hasEntityPermission("RECRUITOFFICEPLAN", "_APPROVE", session)>
			<div class="row-fluid no-left-margin hide" id="apprBtn">
					<button id="acceptRecruitAnticipateItemSelected" class='grid-action-button'><i class='icon-ok'></i> ${uiLabelMap.ApprovalSelected}</button>
					<button id="acceptAllApprRecruitAnticipate" class='grid-action-button'><i class='fa fa-check-circle'></i> ${uiLabelMap.ApprovalAll}</button>
					<button id="rejectRecruitAnticipateItemSelected" style="color: red !important;"  class='grid-action-button'><i class='fa-remove'></i> ${uiLabelMap.RejectSelectedRows}</button>
					<button id="rejectAllRecruitAnticipateItem" style="color: red !important;" class='grid-action-button'><i class='fa fa-times-circle'></i> ${uiLabelMap.NotApprovalAll}</button>
			</div>
			</#if>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelApprRecruitAnticipate" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
					<#if security.hasEntityPermission("RECRUITOFFICEPLAN", "_APPROVE", session)>
					<button id="saveApprRecruitAnticipate" class='btn btn-primary form-action-button pull-right' style="display: none;"><i class='fa fa-thumbs-o-up'></i> ${uiLabelMap.HRApprove}</button>
					</#if>
					
				</div>
			</div>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/recruitment/ApprovalRecruitmentAnticipate.js"></script>
