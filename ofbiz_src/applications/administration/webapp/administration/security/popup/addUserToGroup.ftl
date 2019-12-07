<script src="/administrationresources/js/security/addUserToGroup.js"></script>
<script src="/crmresources/js/generalUtils.js"></script>
<div id="jqxwindowAddUserToGroup" class='hide'>
	<div>${uiLabelMap.ADAddUserToGroup}</div>
	<div style="overflow-x: hidden;">
		
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right">${uiLabelMap.ADUserGroupId}&nbsp;&nbsp;&nbsp;</label></div>
			<div class="span8"><label class="green" id="txtGroupId">${(application.applicationId)?if_exists}</label></div>
		</div>
		
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.CommonEmployee}</label></div>
			<div class="span8">
				<div id="txtAddUserToGroupEmployeeId">
					<div style="border-color: transparent;" id="jqxgridAddUserToGroupEmployeeId" tabindex="5"></div>
				</div>
			</div>
		</div>
	    
	    <div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelAddUserToGroup" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveAddUserToGroup" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>