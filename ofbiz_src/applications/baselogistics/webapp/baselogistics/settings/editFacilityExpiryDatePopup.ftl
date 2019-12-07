<script>
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.AreYouSureEdit = "${StringUtil.wrapString(uiLabelMap.AreYouSureEdit)}";
	uiLabelMap.UpdateSuccess = "${StringUtil.wrapString(uiLabelMap.UpdateSuccess)}";
	
</script>
<div id="editPopupWindow" class="hide popup-bound">
    <div>${uiLabelMap.EditFacilityExpiryDate}</div>
   	<div class='form-window-container'>
		<div class='form-window-content'>
	    	<div class="row-fluid margin-top10">
	 			<div class="span4 align-right asterisk" >${uiLabelMap.BLFacilityId}</div>
	 			<div class="span8"><input disabled type='text' id="txtFacilityId" /></div>
	 		</div>
			<div class="row-fluid margin-top10">
	 			<div class="span4 align-right asterisk" >${uiLabelMap.BLFacilityName}</div>
	 			<div class="span8"><input disabled type='text' id="txtFacilityName" ></input></div>
			</div>
			<div class="row-fluid margin-top10">
	 			<div class="span4 align-right" >${uiLabelMap.BLFacilityRequireDate}</div>
    	 		<div class="span8"><div id="txtFacilityRequireDate" ></div></div>
			</div>
		 	<div class="form-action popup-footer">
	 			<button id="alterCancelEdit" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    		<button id="alterSaveEdit" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	         </div>
         </div>
    </div>
</div>
<script type="text/javascript" src="/logresources/js/config/editFacilityExpiryDate.js"></script>