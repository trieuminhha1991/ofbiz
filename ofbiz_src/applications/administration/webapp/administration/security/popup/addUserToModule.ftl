<script src="/administrationresources/js/security/addUserToModule.js"></script>

<div id="jqxwindowAddUser" class='hide'>
	<div>${uiLabelMap.ADAddPermissionForUser}</div>
	<div style="overflow-x: hidden;">
		
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right">${uiLabelMap.ADApplicationId}&nbsp;&nbsp;&nbsp;</label></div>
			<div class="span8"><label class="green" id="txtAddUserApplicationId">${(application.applicationId)?if_exists}</label></div>
		</div>
		
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.CommonEmployee}</label></div>
			<div class="span8">
				<div id="txtAddUserEmployeeId">
					<div style="border-color: transparent;" id="jqxgridAddUserEmployeeId" tabindex="5"></div>
				</div>
			</div>
		</div>
		
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.ADPermission}</label></div>
			<div class="span8"><div id="txtAddUserPermissionId"></div></div>
		</div>

		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.DmsFromDate}</label></div>
			<div class="span8"><div id="txtAddUserFromDate"></div></div>
		</div>
		
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right">${uiLabelMap.DmsThruDate}&nbsp;&nbsp;&nbsp;</label></div>
			<div class="span8"><div id="txtAddUserThruDate"></div></div>
		</div>
	    
	    <div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelAddUser" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveAddUser" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>