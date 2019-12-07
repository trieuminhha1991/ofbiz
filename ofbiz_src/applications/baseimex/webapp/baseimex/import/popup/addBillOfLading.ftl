<script>

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	
</script>
<div id="alterpopupWindow" class="hide popup-bound">
    <div>${uiLabelMap.AddNewBillOfLading}</div>
   	<div class='form-window-container'>
		<div class='form-window-content'>
	    	<div class="row-fluid margin-top10">
	 			<div class="span4 align-right asterisk" >${uiLabelMap.BillNumber}</div>
	 			<div class="span8"><input type='text' id="txtBillNumber" /></div>
	 		</div>
			<div class="row-fluid margin-top10">
	 			<div class="span4 align-right asterisk" >${uiLabelMap.FromShippingLine}</div>
	 			<div class="span8"><div type='text' id="txtpartyIdFrom"></div></div>
			</div>
			<div class="row-fluid margin-top10">
	 			<div class="span4 align-right asterisk" >${uiLabelMap.departureDate}</div>
    	 		<div class="span8"><div type='text' id="txtdepartureDate"></div></div>
			</div>
			<div class="row-fluid margin-top10">
	 			<div class="span4 align-right asterisk" >${uiLabelMap.arrivalDate}</div>
	 			<div class="span8"><div type='text' id="txtarrivalDate"></div></div>
			</div>
		 	<div class="form-action popup-footer">
	 			<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    		<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	         </div>
         </div>
    </div>
</div>

<script type="text/javascript" src="/imexresources/js/import/addBillOfLading.js"></script>