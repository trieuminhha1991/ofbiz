<script src="/administrationresources/js/security/addUserGroup.js"></script>

<div id="addUserGroup" style="display:none;">
	<div>${uiLabelMap.ADAddUserGroup}</div>
	<div>
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.ADUserGroupId}</label></div>
			<div class="span8"><input type="text" id="txtUserGroupId" tabindex="5" /></div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.BSDescription}</label></div>
			<div class="span8"><input type="text" id="txtDescription" tabindex="6" /></div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span12">
				<button id="btnCancelUserGroup" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="btnSaveUserGroup" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>