<script src="/administrationresources/js/security/moduleAddPermissionToParty.js"></script>

<div id="jqxwindowAddPermission" class='hide'>
	<div>${uiLabelMap.ADAddPermission}</div>
	<div style="overflow-x: hidden;">
		
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right">${uiLabelMap.ADApplicationId}&nbsp;&nbsp;&nbsp;</label></div>
			<div class="span8"><label class="green" id="txtApplicationId"></label></div>
		</div>

		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right" id="lblEmployeeId">${uiLabelMap.EmployeeId}&nbsp;&nbsp;&nbsp;</label></div>
			<div class="span8"><label class="green" id="txtEmployeeId"></label></div>
		</div>

		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.ADPermission}</label></div>
			<div class="span8"><div id="txtPermissionId"></div></div>
		</div>

		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.DmsFromDate}</label></div>
			<div class="span8"><div id="txtFromDate"></div></div>
		</div>

		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right">${uiLabelMap.DmsThruDate}&nbsp;&nbsp;&nbsp;</label></div>
			<div class="span8"><div id="txtThruDate"></div></div>
		</div>

		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelAddPermission" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveAddPermission" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>