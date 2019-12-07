<script src="/administrationresources/js/security/addGroupToModule.js"></script>

<div id="jqxwindowAddGroup" class='hide'>
	<div>${uiLabelMap.ADAddPermissionForGroup}</div>
	<div style="overflow-x: hidden;">
		
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right">${uiLabelMap.ADApplicationId}&nbsp;&nbsp;&nbsp;</label></div>
			<div class="span8"><label class="green" id="txtAddGroupApplicationId">${(application.applicationId)?if_exists}</label></div>
		</div>
		
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.ADUserGroup}</label></div>
			<div class="span8">
				<div id="txtAddGroupEmployeeId">
					<div style="border-color: transparent;" id="jqxgridAddGroupEmployeeId" tabindex="5"></div>
				</div>
			</div>
		</div>
		
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.ADPermission}</label></div>
			<div class="span8"><div id="txtAddGroupPermissionId"></div></div>
		</div>

		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.DmsFromDate}</label></div>
			<div class="span8"><div id="txtAddGroupFromDate"></div></div>
		</div>
		
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right">${uiLabelMap.DmsThruDate}&nbsp;&nbsp;&nbsp;</label></div>
			<div class="span8"><div id="txtAddGroupThruDate"></div></div>
		</div>
	    
	    <div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelAddGroup" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveAddGroup" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>