<script>
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.CreateSuccess = "${StringUtil.wrapString(uiLabelMap.CreateSuccess)}";
	uiLabelMap.BPPleaseSelect = "${StringUtil.wrapString(uiLabelMap.BPPleaseSelect)}";
	uiLabelMap.CannotBeforeNow = "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
	uiLabelMap.CanNotAfterThruDate = "${StringUtil.wrapString(uiLabelMap.CanNotAfterThruDate)}";
	uiLabelMap.CanNotBeforeFromDate = "${StringUtil.wrapString(uiLabelMap.CanNotBeforeFromDate)}";
	uiLabelMap.ValueMustBeGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreaterThanZero)}";
	
	var productId = "${parameters.productId}"
</script>
<div>
<div id="alterpopupWindow" class="hide popup-bound">
    <div>${uiLabelMap.BPAddOtherTax}</div>
   	<div class='form-window-container'>
		<div class='form-window-content'>
	    	<div class="row-fluid margin-top10">
	 			<div class="span4 align-right asterisk" >${uiLabelMap.BPOtherTaxType}</div>
	 			<div class="span8"><div id="taxType"></div></div>
	 		</div>
			<div class="row-fluid margin-top10">
	 			<div class="span4 align-right asterisk" >${uiLabelMap.BPTaxPercentage}</div>
	 			<div class="span8"><div id="taxPercent"></div></div>
			</div>
			<div class="row-fluid margin-top10">
	 			<div class="span4 align-right asterisk" >${uiLabelMap.BPFromDate}</div>
	 			<div class="span8"><div id="fromDate"></div></div>
			</div>
			<div class="row-fluid margin-top10">
	 			<div class="span4 align-right" >${uiLabelMap.BPThruDate}</div>
	 			<div class="span8"><div id="thruDate"></div></div>
			</div>
			<div class="row-fluid margin-top10">
	 			<div class="span4 align-right" >${uiLabelMap.BPDescription}</div>
    	 		<div class="span8"><textarea id="description" ></textarea></div>
			</div>
		 	<div class="form-action popup-footer">
	 			<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    		<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	         </div>
         </div>
    </div>
</div>
</div>
<script type="text/javascript" src="/poresources/js/product/addOtherTax.js"></script>