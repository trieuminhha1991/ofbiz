<script>

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.CompanyIdDuplicated = "${StringUtil.wrapString(uiLabelMap.CompanyIdDuplicated)}";
	uiLabelMap.CreateSuccess = "${StringUtil.wrapString(uiLabelMap.CreateSuccess)}";
</script>
<div id="alterpopupWindow" class="hide popup-bound">
    <div>${uiLabelMap.AddNewShipCompany}</div>
   	<div class='form-window-container'>
		<div class='form-window-content'>
	    	<div class="row-fluid margin-top10">
	 			<div class="span4 align-right asterisk" >${uiLabelMap.CompanyId}</div>
	 			<div class="span8"><input type='text' id="txtCompanyId" /></div>
	 		</div>
			<div class="row-fluid margin-top10">
	 			<div class="span4 align-right asterisk" >${uiLabelMap.ShipCompanyName}</div>
	 			<div class="span8"><input type='text' id="txtShipCompanyName"></input></div>
			</div>
			<div class="row-fluid margin-top10">
	 			<div class="span4 align-right" >${uiLabelMap.CompanyDesciption}</div>
    	 		<div class="span8"><textarea id="txtDescription" ></textarea></div>
			</div>
		 	<div class="form-action popup-footer">
	 			<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    		<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	         </div>
         </div>
    </div>
</div>
<script type="text/javascript" src="/imexresources/js/import/addShipCompany.js"></script>