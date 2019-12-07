<#include "script/profileCreateEmplApplLeaveScript.ftl"/>
<div class="row-fluid">
	<div id="addNewWindow" class="hide">
		<div>${StringUtil.wrapString(uiLabelMap.HRAddLeaveApplication)}</div>
		<div class="form-window-container">
			<div class="form-window-content">
				<div class='row-fluid margin-bottom10'>
					<div class="span3 text-algin-right">
						<label class="">${uiLabelMap.HRFullName}</label>
					</div>
					<div class="span9">
						<div class="row-fluid">
							<div class="span12">
								<div class="span5" style="margin-right: 0">
									<input type="text" id="employeeName">
								</div>
								<div class="span7" style="margin-left: 0">
									<div class='row-fluid'>
										<div class="span3 text-algin-right">
											<label>${uiLabelMap.EmployeeIdShort}</label>
										</div>
										<div class="span9">
											<input type="text" id="employeeId">
										</div>
									</div>									
								</div>
							</div>
						</div>
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
						<label class="asterisk">${uiLabelMap.HRCommonApprover}</label>
					</div>
					<div class="span9">
						<div id="approverListDropDownBtn">
							 <div style="border-color: transparent;" id="jqxGridApprover"></div>
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
						<label>${uiLabelMap.HRNotes}</label>
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
<script type="text/javascript" src="/hrresources/js/profile/profileCreateEmplApplLeave.js"></script>