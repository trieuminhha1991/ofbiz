<script src="/administrationresources/js/security/addGroupToAction.js"></script>

<div id="jqxwindowAddGroupToAction" class="hide">
<div>${uiLabelMap.ADAddPermissionForGroup}</div>
<div style="overflow-x: hidden;">
	
	<div class="row-fluid margin-top10">
		<div class="span4"><label class="text-right">${uiLabelMap.ADApplicationId}&nbsp;&nbsp;&nbsp;</label></div>
		<div class="span8"><label class="green" id="txtAddGroupToActionApplicationId"></label></div>
	</div>
	
	<div class="row-fluid margin-top10">
		<div class="span4"><label class="text-right asterisk">${uiLabelMap.ADUserGroup}</label></div>
		<div class="span8">
			<div id="txtAddGroupToActionEmployeeId">
				<div style="border-color: transparent;" id="jqxgridAddGroupToActionEmployeeId" tabindex="5"></div>
			</div>
		</div>
	</div>
	
	<div class="row-fluid margin-top10">
		<div class="span4"><label class="text-right asterisk">${uiLabelMap.ADPermission}</label></div>
		<div class="span8"><div id="txtAddGroupToActionPermissionId"></div></div>
	</div>

	<div class="row-fluid margin-top10">
		<div class="span4"><label class="text-right asterisk">${uiLabelMap.DmsFromDate}</label></div>
		<div class="span8"><div id="txtAddGroupToActionFromDate"></div></div>
	</div>
	
	<div class="row-fluid margin-top10">
		<div class="span4"><label class="text-right">${uiLabelMap.DmsThruDate}&nbsp;&nbsp;&nbsp;</label></div>
		<div class="span8"><div id="txtAddGroupToActionThruDate"></div></div>
	</div>
    
    <div class="form-action">
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id="cancelAddGroupToAction" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
				<button id="saveAddGroupToAction" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
</div>