<div id="configPartyWorkingShiftWindow" class="hide">
	<div>
		${uiLabelMap.ConfigPartyWorkingShift}
	</div>
	<div>
		<div id="containerNtfEditConfigPartyWS" class="container-noti">
		</div>
		<div id="jqxNotificationEditConfigPartyWS">
			<div id="notificationContentEditConfigPartyWS"></div>
		</div>
		<div id="configPartyShiftTreeGrid"></div>		
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/timeManager/configPartyWorkingShift.js"></script>
<#if security.hasEntityPermission("HR_TIMEMGR", "_ADMIN", session)>
	<div id="editConfigPartyWSWindow" class='hide'>
		<div>${uiLabelMap.EditConfigPartyConfigWorkingShift}</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="">${uiLabelMap.OrgUnitName}</label>
					</div>
					<div class="span7">
						<input type="text" id="configWSPartyName">					
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="">${uiLabelMap.OrgUnitId}</label>
					</div>
					<div class="span7">
						<input type="text" id="configWSPartyId">
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="">${uiLabelMap.HrCommonWorkingShift}</label>
					</div>
					<div class="span7">
						<div id="workingShiftDropdownlist"></div>
					</div>
				</div>
			</div>
			<div class="form-action">
				<button id="cancelConfigPartyWS" class='btn btn-danger form-action-button pull-right'>
					<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="saveConfigPartyWS">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
	<script type="text/javascript" src="/hrresources/js/timeManager/createConfigPartyWorkingShift.js"></script>			
</#if>