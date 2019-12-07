<div id="recruitCalendarScheduleWindow" class="hide">
	<div>${uiLabelMap.CandidateInterviewList}</div>
	<div class='form-window-container'>
		<div id="containerrecruitCalListGrid"></div>
		<div id="jqxNotificationrecruitCalListGrid">
	        <div id="notificationContentrecruitCalListGrid">
	        </div>
	    </div>
		<div id="recruitCalListGrid"></div>
	</div>
</div>

<div id="scheduleInterviewCandidateWindow" class="hide">
	<div>${uiLabelMap.HRCalendarScheduleInterview}</div>
	<div class='form-window-container'>
		<div class="row-fluid">
			<div id="wizardCalendarSchedule" class="row-fluid hide" data-target="#stepContainerCalendarSchedule">
		        <ul class="wizard-steps wizard-steps-square">
		                <li data-target="#orderingInterviewCal" class="active">
		                    <span class="step">1. ${uiLabelMap.OrderingInterview}</span>
		                </li>
		                <li data-target="#scheduleInterviewCal">
		                    <span class="step">2. ${uiLabelMap.HRCalendarScheduleInterview}</span>
		                </li>
		    	</ul>
		    </div><!--#fuelux-wizard-->
		     <div class="step-content row-fluid position-relative" id="stepContainerCalendarSchedule">
		     	<div class="step-pane active" id="orderingInterviewCal">
		     		<div id="containerorderInterviewCandidateGrid"></div>
					<div id="jqxNotificationorderInterviewCandidateGrid">
				        <div id="notificationContentorderInterviewCandidateGrid">
				        </div>
				    </div>
		     		<div id="orderInterviewCandidateGrid"></div>
		     	</div>
		     	<div class="step-pane" id="scheduleInterviewCal" style="margin-top: 15px">
		     		<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>
							<label class="asterisk">${uiLabelMap.StartInterviewTime}</label>
						</div>
						<div class="span7">
							<div id="startInterviewTime"></div>
						</div>
					</div>
		     		<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>
							<label class=""></label>
						</div>
						<div class="span7">
							<div id="isInterviewMorning" style="margin-left: -5px !important; margin-top: 5px">${uiLabelMap.RecruitmentInterviewInMorning}</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>
							<label class="">${uiLabelMap.RecruimentInterviewMorningFrom}</label>
						</div>
						<div class="span7">
							<div class="row-fluid">
								<div class="span12">
									<div class="span5">
										<div id="interviewMorningFrom"></div>
									</div>
									<div class="span7">
										<div class="row-fluid">
											<div class="span3 align-center">
												<label>${uiLabelMap.HRCommonToUppercase}</label>
											</div>
											<div class="span9">
												<div id="interviewMorningTo"></div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>
							<label class=""></label>
						</div>
						<div class="span7">
							<div id="isInterviewAfternoon" style="margin-left: -5px !important; margin-top: 5px">${uiLabelMap.RecruitmentInterviewInAfternoon}</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>
							<label class="">${uiLabelMap.RecruimentInterviewAfternoonFrom}</label>
						</div>
						<div class="span7">
							<div class="row-fluid">
								<div class="span12">
									<div class="span5">
										<div id="interviewAfternoonFrom"></div>
									</div>
									<div class="span7">
										<div class="row-fluid">
											<div class="span3 align-center">
												<label>${uiLabelMap.HRCommonToUppercase}</label>
											</div>
											<div class="span9">
												<div id="interviewAfternoonTo"></div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>
							<label class="">${uiLabelMap.RecruitmentNbrApplicantForInterview}</label>
						</div>
						<div class="span7">
							<div id="nbrApplicantForInterview"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>
							<label class="">${uiLabelMap.RecruitmentTimeForAnInterview}</label>
						</div>
						<div class="span7">
							<div class="row-fluid">
								<div class="span12">
									<div class="span11">
										<div id="timeForInterview"></div>
									</div>
									<div class="span1" style="margin: 0">
										<label>${uiLabelMap.HRMinute}</label>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>
							<label class="">${uiLabelMap.RecruitmentOverlapTime}</label>
						</div>
						<div class="span7">
							<div class="row-fluid">
								<div class="span12">
									<div class="span11">
										<div id="overlapTimeInterview"></div>
									</div>
									<div class="span1" style="margin: 0">
										<label>${uiLabelMap.HRMinute}</label>
									</div>
								</div>
							</div>
						</div>
					</div>
		     	</div>
		     </div>
		     <div class="form-action wizard-actions">
					<button class="btn btn-next btn-success form-action-button pull-right" data-last="${uiLabelMap.CommonSave}" id="btnNextScheduleCandidate">
						${uiLabelMap.CommonNext}
						<i class="icon-arrow-right icon-on-right"></i>
					</button>
					<button class="btn btn-prev form-action-button pull-right" id="btnPrevScheduleCandidate">
						<i class="icon-arrow-left"></i>
						${uiLabelMap.CommonPrevious}
					</button>
				</div>
				<div class="row-fluid no-left-margin">
					<div id="ajaxLoadingScheduleCandidate" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerAjaxScheduleCandidate"></div>
					</div>
				</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="/hrresources/js/recruitment/RecruitmentCalendarSchedule.js"></script>