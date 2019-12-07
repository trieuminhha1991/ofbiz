<script src="/administrationresources/js/security/addPartyToAction.js"></script>

<div id="jqxwindowAddUserToAction" class='hide'>
<div>${uiLabelMap.ADAddPermissionForUser}</div>
<div style="overflow-x: hidden;">
	
	<div class="row-fluid margin-top10">
		<div class="span4"><label class="text-right">${uiLabelMap.ADApplicationId}&nbsp;&nbsp;&nbsp;</label></div>
		<div class="span8"><label class="green" id="txtAddUserToActionApplicationId"></label></div>
	</div>
	
	<div class="row-fluid margin-top10">
		<div class="span4"><label class="text-right asterisk">${uiLabelMap.CommonEmployee}</label></div>
		<div class="span8">
			<div id="txtAddUserToActionEmployeeId">
				<div style="border-color: transparent;" id="jqxgridAddUserToActionEmployeeId" tabindex="5"></div>
			</div>
		</div>
	</div>
	
	<div class="row-fluid margin-top10">
		<div class="span4"><label class="text-right asterisk">${uiLabelMap.ADPermission}</label></div>
		<div class="span8"><div id="txtAddUserToActionPermissionId"></div></div>
	</div>

	<div class="row-fluid margin-top10">
		<div class="span4"><label class="text-right asterisk">${uiLabelMap.DmsFromDate}</label></div>
		<div class="span8"><div id="txtAddUserToActionFromDate"></div></div>
	</div>
	
	<div class="row-fluid margin-top10">
		<div class="span4"><label class="text-right">${uiLabelMap.DmsThruDate}&nbsp;&nbsp;&nbsp;</label></div>
		<div class="span8"><div id="txtAddUserToActionThruDate"></div></div>
	</div>
    
    <div class="form-action">
		<div class='row-fluid'>
			<div class="span12 margin-top10">
				<button id="cancelAddUserToAction" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button id="saveAddUserToAction" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
</div>