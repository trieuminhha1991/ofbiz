<#-- TODO deleted -->
<script src="/salesmtlresources/js/common/representative.js"></script>

<div class="row-fluid form-horizontal form-window-content-custom">
<div class="span6">
	<div class="row-fluid">
		<div class="span5"><label class="text-right">${uiLabelMap.FullName}</label></div>
		<div class="span7"><input type="text" id="txtRFullName" tabindex="15"/></div>
	</div>
	<div class="row-fluid">
		<div class="span5"><label class="text-right">${uiLabelMap.DmsPartyBirthDate}&nbsp;&nbsp;&nbsp;</label></div>
		<div class="span7"><div id="txtRBirthDate" tabindex="16"></div></div>
	</div>
	<div class="row-fluid">
		<div class="span5"><label class="text-right">${uiLabelMap.DmsCountry}&nbsp;&nbsp;&nbsp;</label></div>
		<div class="span7"><div id="txtRCountry"></div></div>
	</div>
	<div class="row-fluid">
		<div class="span5"><label class="text-right">${uiLabelMap.DmsCounty}&nbsp;&nbsp;&nbsp;</label></div>
		<div class="span7"><div id="txtRCounty" tabindex="19"></div></div>
	</div>
	<div class="row-fluid">
		<div class="span5"><label class="text-right">${uiLabelMap.DmsAddress1}&nbsp;&nbsp;&nbsp;</label></div>
		<div class="span7"><input type="text" id="tarRAddress" tabindex="21"/></div>
	</div>
</div>

<div class="span6">
	<div class="row-fluid">
		<div class="span5"><label class="text-right">${uiLabelMap.DmsPartyGender}&nbsp;&nbsp;&nbsp;</label></div>
		<div class="span7"><div id="txtRGender"></div></div>
	</div>
	<div class="row-fluid">
		<div class="span5"><label class="text-right">${uiLabelMap.PhoneNumber}&nbsp;&nbsp;&nbsp;</label></div>
		<div class="span7"><input type="tel" id="txtRPhoneNumber" tabindex="17"/></div>
	</div>
	<div class="row-fluid">
		<div class="span5"><label class="text-right">${uiLabelMap.DmsProvince}&nbsp;&nbsp;&nbsp;</label></div>
		<div class="span7"><div id="txtRProvince" tabindex="18"></div></div>
	</div>
	<div class="row-fluid">
		<div class="span5"><label class="text-right">${uiLabelMap.DmsWard}&nbsp;&nbsp;&nbsp;</label></div>
		<div class="span7"><div id="txtRWard" tabindex="20"></div></div>
	</div>
	<div class="row-fluid">
		<div class="span5"><label class="text-right">${uiLabelMap.EmailAddress}&nbsp;&nbsp;&nbsp;</label></div>
		<div class="span7"><input type="email" id="txtREmailAddress" tabindex="22"/></div>
	</div>
</div>
</div>

<script>
var listGender = [{
	value : 'M',
	label : '${StringUtil.wrapString(uiLabelMap.DmsMale)}'
}, {
	value : 'F',
	label : '${StringUtil.wrapString(uiLabelMap.DmsFemale)}'
}];
</script>