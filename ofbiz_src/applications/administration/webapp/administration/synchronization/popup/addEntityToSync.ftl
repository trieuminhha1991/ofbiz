<script src="/administrationresources/js/synchronization/addEntityToSync.js"></script>

<div id="addEntityToSync" style="display:none;">
	<div>${uiLabelMap.ADAddEntityToSync}</div>
	<div class="form-window-content-custom">
		<div class="row-fluid">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.entityGroupId}</label></div>
			<div class="span8"><div id="txtEntityGroupId" tabindex="5" ></div></div>
		</div>
		<div class="row-fluid">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.entityOrPackage}</label></div>
			<div class="span8"><div id="txtEntityOrPackage" tabindex="6" ></div></div>
		</div>
		<div class="row-fluid form-action">
			<div class="span12">
				<button id="btnCancelEntityToSync" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="btnSaveEntityToSync" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>