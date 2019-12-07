<@jqOlbCoreLib hasGrid=false hasDropDownButton=false hasDropDownList=true hasComboBox=false hasValidator=true/>
<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
</script>
<script type="text/javascript" src="/poresources/js/common/AddressProcessor.js?v=0.0.3"></script>

<div id="alterpopupWindowAddressProcessor" style="display:none;">	
<div id="addressProcessorTitle">${uiLabelMap.DmsAddAddress}</div>
<div style="overflow-x: hidden;">
	<div class="row-fluid form-window-content-custom">
		<div class="span12">
			<div class="row-fluid">
				<div class="span4"><label class="text-right asterisk">${uiLabelMap.BPOCountry}</label></div>
				<div class="span8"><div id="txtCountry" tabindex="5"/></div></div>
			</div>
			<div class="row-fluid">
				<div class="span4"><label class="text-right asterisk">${uiLabelMap.BPOStateProvince}</label></div>
				<div class="span8"><div id="txtProvince" tabindex="6"/></div></div>
			</div>
			<div class="row-fluid">
				<div class="span4"><label class="text-right">${uiLabelMap.BPOCounty}</label></div>
				<div class="span8"><div id="txtCounty" tabindex="7"/></div></div>
			</div>
			<div class="row-fluid">
				<div class="span4"><label class="text-right">${uiLabelMap.BPOWard}</label></div>
				<div class="span8"><div id="txtWard" tabindex="8"></div></div>
			</div>
			<div class="row-fluid">
				<div class="span4"><label class="text-right asterisk">${uiLabelMap.BPOAddress1}</label></div>
				<div class="span8"><input type="text" id="txtAddress1" tabindex="9"/></div>
			</div>
			<div class="row-fluid">
				<div class="span4"><label class="text-right">${uiLabelMap.BPOZipCode}&nbsp;&nbsp;&nbsp;</label></div>
				<div class="span8"><input type="text" id="txtZipCode" tabindex="10"/></div>
			</div>
		</div>
	</div>
	<div class="form-action">
		<div class="row-fluid">
			<button id="alterCancelAddressProcessor" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
			<button id="alterSaveAddressProcessor" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
</div>

<#assign organizationId = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
<script>
	const organizationId = "${organizationId?if_exists}";
</script>