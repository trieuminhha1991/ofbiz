<div id="partyDatekeepingDetailWindow" class="hide">
	<div>${uiLabelMap.CommonDate}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span6 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.DateAttendance)}</label>
				</div>
				<div class="span6">
					<label id="dateTimekeepingDetailDate"></label>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span6 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.EmployeeId)}</label>
				</div>
				<div class="span6">
					<label id="partyDatekeepingId"></label>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span6 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.EmployeeName)}</label>
				</div>
				<div class="span6">
					<label id="partyDatekeepingName"></label>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span6 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.ActualWorkday)}</label>
				</div>
				<div class="span6">
					<div class="row-fluid">
						<div class="span12">
							<div class="span10">
								<div id="dateTimekeepingActualWorkday" style="display: inline-block; float: left;"></div>
							</div>
							<div class="span2">
								<div id="checkActualWorkday" style="margin: 5px 0 0 0 !important"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class='row-fluid margin-bottom10'>
				<div class="span6 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.WorkdayLeavePaid)}</label>
				</div>
				<div class="span6">
					<div class="row-fluid">
						<div class="span12">
							<div class="span10">
								<div id="dateTimekeepingLeavePaid" ></div>
							</div>
							<div class="span2">
								<div id="checkLeavePaid" style="margin: 5px 0 0 0 !important"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span6 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.TotalWorkOvertimeHours)}</label>
				</div>
				<div class="span6">
					<div class="row-fluid">
						<div class="span12">
							<div class="span10">
								<div id="dateTimekeepingOvertimeHours" ></div>
							</div>
							<div class="span2">
								<div id="checkOvertimeHours" style="margin: 5px 0 0 0 !important"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingDateTimekeepingDetail" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerDateTimekeepingDetail"></div>
				</div>
			</div>
		</div>
		<div class='form-action'>
			<button type="button" class="btn btn-danger form-action-button pull-right icon-remove open-sans" id="cancelUpdateTimekeepingDetail">${uiLabelMap.CommonClose}</button>
			<button type="button" class="btn btn-primary form-action-button pull-right icon-ok open-sans" id="saveUpdateTimekeepingDetail">${uiLabelMap.CommonSubmit}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/timesheet/ViewTimekeepingDetailPartyDatekeeping.js"></script>