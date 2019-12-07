<script>
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.AreYouSureEditShipCompany = "${StringUtil.wrapString(uiLabelMap.AreYouSureEditShipCompany)}";
	uiLabelMap.UpdateSuccess = "${StringUtil.wrapString(uiLabelMap.UpdateSuccess)}";
	uiLabelMap.CompanyIdDuplicated = "${StringUtil.wrapString(uiLabelMap.CompanyIdDuplicated)}";
	
</script>
<div id="editPopupWindow" class="hide popup-bound">
    <div>${uiLabelMap.EditShipCompany}</div>
   	<div class='form-window-container'>
		<div class='form-window-content'>
	    	<div class="row-fluid margin-top10">
	 			<div class="span4 align-right asterisk" >${uiLabelMap.CompanyId}</div>
	 			<div class="span8"><input type='text' id="txtCompanyIdEdit" /></div>
	 		</div>
			<div class="row-fluid margin-top10">
	 			<div class="span4 align-right asterisk" >${uiLabelMap.ShipCompanyName}</div>
	 			<div class="span8"><input type='text' id="txtShipCompanyNameEdit"></input></div>
			</div>
			<div class="row-fluid margin-top10">
	 			<div class="span4 align-right" >${uiLabelMap.CompanyDesciption}</div>
    	 		<div class="span8"><textarea id="txtDescriptionEdit" ></textarea></div>
			</div>
		 	<div class="form-action popup-footer">
	 			<button id="alterCancelEdit" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    		<button id="alterSaveEdit" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	         </div>
         </div>
    </div>
</div>
<script type="text/javascript" src="/imexresources/js/import/editShipCompany.js"></script>