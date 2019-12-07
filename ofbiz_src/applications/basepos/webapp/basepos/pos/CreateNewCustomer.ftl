<#include "component://widget/templates/jqwLocalization.ftl" />
<style>
.labelCreateCustomer{
	font-size: 13px;
	padding-top: 5px;
}

.jqx-validator-hint{
	height: 25px;
}

#city .jqx-combobox-input {
    padding-left: 5px !important;
}

.inputCreateCustomer{
	padding-left: 5px;
}
</style>
<div id="alterpopupWindowCreateNewCustomer" style="display:none;">
    <div style="background-color: #438EB9; border-color: #0077BC;">${uiLabelMap.BPOSCreateCustomer}</div>
    <div>
    	<div class='row-fluid form-window-content'>
    		<div class='span12'>
    			<div class= 'row-fluid margin-bottom10'>
	    			<div class='span4 align-right labelCreateCustomer asterisk'>
						${uiLabelMap.BPOSCustomerName}
					</div>
					<div class="span8">
						<input type="text" class="inputCreateCustomer" id="customerFullName" name="customerFullName" style="width: 99%">
			   		</div>
			   	</div>
    			<div class= 'row-fluid margin-bottom10'>
	    			<div class='span4 align-right labelCreateCustomer asterisk'>
						${uiLabelMap.BPOSAddress}
					</div>
					<div class="span8">
						<input type="text" class="inputCreateCustomer" id="address" name="address" style="width: 99%">
			   		</div>
			   	</div>
    			<div class= 'row-fluid margin-bottom10'>
	    			<div class='span4 align-right labelCreateCustomer asterisk'>
						${uiLabelMap.BPOSCity}
					</div>
					<div class="span8">
						<div id="city">
						</div>
			   		</div>
			   	</div>
    			<div class= 'row-fluid margin-bottom10'>
	    			<div class='span4 align-right labelCreateCustomer'>
						${uiLabelMap.BPOSMobile}
					</div>
					<div class="span8">
						<input type="text" class="inputCreateCustomer" id="phone" name="phone" style="width: 99%">
			   		</div>
			   	</div>
    		</div>
    	</div>
    	<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancelCustomer" class='btn btn-danger form-action-button pull-right'><i class='icon-remove'></i> ${uiLabelMap.BPOSCancel}</button>
					<button id="alterSaveCustomer" class='btn btn-primary form-action-button pull-right'><i class='icon-ok'></i> ${uiLabelMap.BPOSCreate}</button>
				</div>
			</div>
		</div>
    </div>
</div>
<@jqOlbCoreLib hasComboBox=true hasValidator=true/>
<script type="text/javascript">
	var BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
	var BPOSAreYouCertainlyCreated = "${StringUtil.wrapString(uiLabelMap.BPOSAreYouCertainlyCreated)}";
	var validFieldRequire = "${StringUtil.wrapString(uiLabelMap.validFieldRequire)}";
	var BPOSPhoneNumberIncorrect = "${StringUtil.wrapString(uiLabelMap.BPOSPhoneNumberIncorrect)}";
	<#if postalAddress?exists>
		var countryGeoId = '${postalAddress.countryGeoId?if_exists}';
		var stateProvinceGeoId = '${postalAddress.stateProvinceGeoId?if_exists}';
	<#else>
		var countryGeoId = '';
		var stateProvinceGeoId = '';
	</#if>
	
</script>
<script type="text/javascript" src="/posresources/js/CreateNewCustomer.js"></script>