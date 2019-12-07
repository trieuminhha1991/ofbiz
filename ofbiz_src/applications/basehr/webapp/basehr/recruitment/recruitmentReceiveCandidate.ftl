<div id="recruitmentReceiveCandidateWindow" class="hide">
	<div>${uiLabelMap.RecruitmentReceivedCandidate}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position: relative;">
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="asterisk">${uiLabelMap.EmployeeId}</label>
							</div>
							<div class="span7">
								<input type="text" id="partyCodeReceiveCandidate">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="asterisk">${uiLabelMap.PartyIdWork}</label>
							</div>
							<div class="span7">
								<div id="dropDownButtonReceiveCandidate">
									<div style="border: none;" id="jqxTreeReceiveCandidate">
									</div>
								</div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="asterisk">${uiLabelMap.RecruitmentPositionPassed}</label>
							</div>
							<div class="span7">
								<div id="emplPositionTypeIdReceiveCandidate"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.ProbationaryDeadline}</label>
							</div>
							<div class="span7">
								<div id="probationaryDeadLineReceiveCandidate"></div>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.HRFullName}</label>
							</div>
							<div class="span7">
								<input type="text" id="fullNameReceiveCandidate">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="asterisk">${uiLabelMap.RecruitmentDateReceiveCandidate}</label>
							</div>
							<div class="span7">
								<div id="dateJoinCompanyReceiveCandidate"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="asterisk">${uiLabelMap.SalaryBaseFlat}</label>
							</div>
							<div class="span7">
								<div class="row-fluid">
									<div id="salaryBaseFlatReceiveCandidate"></div>
								</div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="asterisk">${uiLabelMap.PeriodTypePayroll}</label>
							</div>
							<div class="span7">
								<div id="periodTypeReceiveCandidate"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<hr style="margin: 0 0 10px"/>
			<div class="row-fluid" style="">
				<div class="span12">
					<div class="span4" style="text-align: center;">
						<a href="javascript:recruitmentReceiveCandidateObj.showCandidateSetting('information')"><label><i class="fa-male"></i>${uiLabelMap.PartyPersonalInformation}</label></a>
					</div>
					<div class="span3" style="margin: 0">
						<a href="javascript:recruitmentReceiveCandidateObj.showCandidateSetting('contact')"><label><i class="fa-phone"></i>${uiLabelMap.PartyContactMechs}</label></a>
					</div>
					<div class="span3" style="margin: 0">
						<a href="javascript:recruitmentReceiveCandidateObj.showCandidateSetting('userLogin')"><label><i class="fa-user"></i>${uiLabelMap.HRCommonUserLogin}</label></a>
					</div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="ajaxLoadingReceiveCandidate" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAjaxReceiveCandidate"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelReceiveCandidate" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveReceiveCandidate" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="createUserLoginCanidateWindow" class="hide">
	<div>${uiLabelMap.HRCommonUserLogin}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position: relative;">
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="">${uiLabelMap.HRFullName}</label>
				</div>
				<div class="span7">
					<input type="text" id="fullNameCandidateCreateUserLogin">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${uiLabelMap.userLoginId}</label>
				</div>
				<div class="span7">
					<input type="text" id="userLoginIdCanidate">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${uiLabelMap.CommonPassword}</label>
				</div>
				<div class="span7">
					<input type="password" id="passwordCandidate">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${uiLabelMap.HRConfirmPassword}</label>
				</div>
				<div class="span7">
					<input type="password" id="confirmPasswordCandidate">
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="ajaxLoadingUserLoginCandidate" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAjaxUserLogin"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelCreateuserLoginCandidate" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveCreateuserLoginCandidate" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/recruitment/recruitmentReceiveCandidate.js"></script>