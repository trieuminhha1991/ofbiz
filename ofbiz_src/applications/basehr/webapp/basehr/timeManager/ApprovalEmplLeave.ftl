<#include "script/ApprovalEmplLeaveScript.ftl"/>
<div class="row-fluid">
	<div id="approvalEmplLeaveWindow" class="hide">
		<div>${StringUtil.wrapString(uiLabelMap.ApprovalEmplLeaveApplGeneral)}</div>
		<div class="form-window-container">
			<div class="form-window-content" id="jqxPanelApproval">
				<div class='row-fluid margin-bottom10' style="margin-top: 15px">
					<div class="span3 text-algin-right">
						<label>${uiLabelMap.HREmplApprovalCurrStatus}</label>
					</div>
					<div class="span9">
						<div id="statusId"></div>
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
									<div id="acceptEmplLeave">${uiLabelMap.CommonApprove}</div>		
								</div>
								<div class="span4">
									<div id="rejectEmplLeave">${uiLabelMap.HRReject}</div>
								</div>
								<div class="span3">
									<div id="cancelEmplLeave">${uiLabelMap.HRCancelLeaveApplication}</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span3 text-algin-right">
						<label class="">${uiLabelMap.HRFullName}</label>
					</div>
					<div class="span9">
						<input type="text" id="employeeName">
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span3 text-algin-right">
						<label>${uiLabelMap.HrCommonPosition}</label>
					</div>
					<div class="span9">
						<input type="text" id="emplPositionType">
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span3 text-algin-right">
						<label>${uiLabelMap.CommonDepartment}</label>
					</div>
					<div class="span9">
						<input type="text" id="partyIdFrom">
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span3 text-algin-right">
						<label class="asterisk">${uiLabelMap.HREmplLeaveReason}</label>
					</div>
					<div class="span9">
						<div class="row-fluid">
							<div class="span12">
								<div class="span7">
									<div id="emplLeaveReason"></div>
								</div>
								<div class="span5">
									<div class="row-fluid">
										<div class="span6" style="margin-left: 2px">
											<label class="">${uiLabelMap.HRPayRate}</label>	
										</div>
										<div class="span5" style="">
											<div id="rateBenefit"></div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span3 text-algin-right">
						<label class="asterisk">${uiLabelMap.HrCommonWorkingShift}</label>
					</div>
					<div class="span9">
						<div id="workingShift"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span3 text-algin-right">
						<label class="asterisk">${uiLabelMap.CommonFromDate}</label>
					</div>
					<div class="span9">
						<div class="row-fluid">
							<div class="span12">
								<div class="span7">
									<div id="fromDate"></div>
								</div>
								<div class="span5">
									<div class="row-fluid">
										<div id="fromDateTime"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span3 text-algin-right">
						<label class="asterisk">${uiLabelMap.CommonThruDate}</label>
					</div>
					<div class="span9">
						<div class="row-fluid">
							<div class="span12">
								<div class="span7">
									<div id="thruDate"></div>
								</div>
								<div class="span5">
									<div class="row-fluid">
										<div id="thruDateTime"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span3 text-algin-right">
						<label>${uiLabelMap.DateApplication}</label>
					</div>
					<div class="span9">
						<div id="dateApplication"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span3 text-algin-right">
						<label>${uiLabelMap.CommonDescription}</label>
					</div>
					<div class="span9">
						<div id="emplLeaveDesc">${uiLabelMap.HRCommonNotSetting}</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span3 text-algin-right">
						<label>${uiLabelMap.NoteOfApprove}</label>
					</div>
					<div class="span9">
						<textarea id="notes"></textarea>
					</div>
				</div>
			</div>
			<div class="form-action">
				<button id="btnCancel" class='btn btn-danger form-action-button pull-right'>
					<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSave">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/timeManager/ApprovalEmplLeave.js"></script>