<div id="ApprWorkovertimeRegWindow" class="hide">
	<div>${uiLabelMap.ApprovalWorkovertimeRegister}</div>
	<div class='form-window-container' style="position: relative;">
		<div class="form-window-content" id="jqxPanelApproval">
			<div class='row-fluid margin-bottom10' style="margin-top: 15px">
				<div class="span3 text-algin-right">
					<label>${uiLabelMap.HREmplApprovalCurrStatus}</label>
				</div>
				<div class="span9">
					<div id="statusIdAppr"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span3 text-algin-right">
					<label class="asterisk">${uiLabelMap.HRApprove}</label>
				</div>
				<div class="span9">
					<div class="row-fluid">
						<div class="span12">
							<div class="span4">
								<div id="acceptWorkovertime">${uiLabelMap.CommonApprove}</div>		
							</div>
							<div class="span4">
								<div id="rejectWorkovertime">${uiLabelMap.HRReject}</div>
							</div>
							
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span3 text-algin-right'>
					<label class="">${uiLabelMap.CommonEmployee}</label>
				</div>
				<div class="span9">
					<input type="text" id="partyIdAppr">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span3 text-algin-right'>
					<label class="">${uiLabelMap.HrCommonPosition}</label>
				</div>
				<div class="span9">
					<input type="text" id="emplPositionAppr">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span3 text-algin-right'>
					<label class="">${uiLabelMap.CommonDepartment}</label>
				</div>
				<div class="span9">
					<input type="text" id="groupNameAppr">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span3 text-algin-right'>
					<label class="">${uiLabelMap.HREmplOvertimeDateRegis}</label>
				</div>
				<div class="span9">
					<div id="dateRegisteredAppr"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span3 text-algin-right'>
					<label class="">${uiLabelMap.CommonBeginingFromDate}</label>
				</div>
				<div class="span9">
					<div class="row-fluid">
						<div class="span12">
							<div class="span4">
								<div id="fromDateAppr"></div>
							</div>
							<div class="span8">
								<div class='row-fluid'>
									<div class='span5 text-algin-right'>
										<label class="">${uiLabelMap.CommonThruDate}</label>
									</div>
									<div class="span7">
										<div id="thruDateAppr"></div>
									</div>
								</div>							
							</div>				
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span3 text-algin-right'>
					<label class="">${uiLabelMap.WorkingOvertimeStartTime}</label>
				</div>
				<div class="span9">
					<div class="row-fluid">
						<div class="span12">
							<div class="span4">
								<div id="startTimeAppr"></div>
							</div>
							<div class="span8">
								<div class='row-fluid'>
									<div class='span5 text-algin-right'>
										<label class="">${uiLabelMap.WorkingOvertimeEndTime}</label>
									</div>
									<div class="span7">
										<div id="endTimeAppr"></div>
									</div>
								</div>							
							</div>				
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span3 text-algin-right'>
					<label class="">${uiLabelMap.HRCommonApplyFor}</label>
				</div>
				<div class="span9">
					<div id="dayOfWeekApplAppr"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span3 text-algin-right'>
					<label class="">${uiLabelMap.ClaimRejectReason}</label>
				</div>
				<div class="span9">
					<textarea id="reasonApproval"></textarea>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingAppr" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAppr"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAppr">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAppr">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/timesheet/ApprWorkingOvertimeRegister.js"></script>