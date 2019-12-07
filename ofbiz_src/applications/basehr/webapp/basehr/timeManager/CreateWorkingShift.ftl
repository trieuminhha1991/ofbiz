<div id="popupAddRow" class="hide">
	<div>${uiLabelMap.CreateNewWorkingShift}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.WorkingShiftIdFull}</label>
							</div>
							<div class="span7">
								<input type="text" id="workingShiftIdNew">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="asterisk">${uiLabelMap.WorkingShiftNameFull}</label>
							</div>
							<div class="span7">
								<input type="text" id="workingShiftNameNew">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.MinuteAllowWorkLate}</label>
							</div>
							<div class="span7">
								<div id="allowLateMinuteNew"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.MinimumMinuteCalcOT}</label>
							</div>
							<div class="span7">
								<div id="minMinuteOvertimeNew"></div>
							</div>
						</div>
						
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.AllowWorkingOvertimeAfterShift}</label>
							</div>
							<div class="span7">
								<div id="allowOTAfterShift" style="margin-left: 0 !important"></div>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="asterisk">${uiLabelMap.WorkingShiftStart}</label>
							</div>
							<div class="span5">
								<div id="shiftStartTimeNew"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="">${uiLabelMap.WorkingShiftBreakStart}</label>
							</div>
							<div class="span5">
								<div id="shiftBreakStartTimeNew"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label>${uiLabelMap.WorkingShiftBreakEnd}</label>
							</div>
							<div class="span5">
								<div id="shiftBreakEndTimeNew"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="asterisk">${uiLabelMap.WorkingShiftEnd}</label>
							</div>
							<div class="span5">
								<div id="shiftEndTimeNew"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="">${uiLabelMap.StartOverTimeAfterShift}</label>
							</div>
							<div class="span5">
								<div id="startOverTimeAfterShiftNew"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="">${uiLabelMap.EndOverTimeAfterShift}</label>
							</div>
							<div class="span5">
								<div id="endOverTimeAfterShiftNew"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<hr/>
			</div>
			<div class="row-fluid">
				<div id="jqxgridShiftWorkTypeNew"></div>
			</div>
		</div>
		<div class="form-action">
			<button id="btnCancelNew" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSaveNew">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
		
	</div>
</div>	

<script type="text/javascript" src="/hrresources/js/timeManager/CreateWorkingShift.js"></script>