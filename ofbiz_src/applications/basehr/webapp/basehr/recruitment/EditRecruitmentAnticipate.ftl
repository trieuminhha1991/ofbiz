<div id="EditRecruitAnticipateWindow" class="hide">
	<div>${uiLabelMap.EditRecruitAnticipatePlan}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position: relative;">
			<div class="row-fluid">
				<div class="alert alert-success" style="margin-bottom: 10px">
					${uiLabelMap.EditRecruitAnticipatePlanNotes}
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid'>
							<div class='span4 align-right'>
								<label class=""><b>${uiLabelMap.OrganizationalUnit}</b>:</label>
							</div>
							<div class='span8'>
								<label id="editPartyId"></label>
							</div>					
						</div>
					</div>
					<div class="span6">
						<div class='row-fluid'>
							<div class='span4 align-right'>
								<label class=""><b>${uiLabelMap.HrCommonPosition}</b>:</label>
							</div>
							<div class='span8'>
								<label id="editEmplPositionType"></label>
							</div>					
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12">
					<div id="containerapprRecruitAnticipateGrid" style="background-color: transparent; overflow: auto;">
				    </div>
				    <div id="jqxNotificationeditRecruitAnticipateGrid">
				        <div id="notificationContenteditRecruitAnticipateGrid">
				        </div>
				    </div>
					<div id="editRecruitAnticipateGrid"></div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingEditRecruitAnticipateWindow" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerEditRecruitAnticipateWindow"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelEditRecruitAnticipate" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
					<button id="saveEditRecruitAnticipate" class='btn btn-primary form-action-button pull-right'><i class='icon-ok'></i> ${uiLabelMap.CommonUpdate}</button>
				</div>
			</div>
		</div>
	</div>	
</div>

<script type="text/javascript" src="/hrresources/js/recruitment/EditRecruitmentAnticipate.js"></script>