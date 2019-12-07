<#-- TODO deleted -->

<#if parameters.partyId?exists>
	<#include "component://basesalesmtl/webapp/basesalesmtl/common/listAddress.ftl"/>
	<#else>

<div class="row-fluid form-horizontal form-window-content-custom">
<div class="span6">
<div class="row-fluid margin-top10">
	<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsCountry}</label></div>
	<div class="span7"><div id="txtCountry"></div></div>
</div>
<div class="row-fluid margin-top10">
	<div class="span5"><label class="text-right">${uiLabelMap.DmsCounty}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span7"><div id="txtCounty" tabindex="8"></div></div>
</div>
<div class="row-fluid margin-top10">
	<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsAddress1}</label></div>
	<div class="span7"><input type="text" id="tarAddress" tabindex="10"/></div>
</div>
<div class="row-fluid margin-top10">
	<div class="span5"><label class="text-right">${uiLabelMap.EmailAddress}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span7"><input type="email" id="txtEmailAddress" tabindex="12"/></div>
</div>
</div>
<div class="span6">
<div class="row-fluid margin-top10">
	<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsProvince}</label></div>
	<div class="span7"><div id="txtProvince" tabindex="7"></div></div>
</div>
<div class="row-fluid margin-top10">
	<div class="span5"><label class="text-right">${uiLabelMap.DmsWard}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span7"><div id="txtWard" tabindex="9"></div></div>
</div>
<div class="row-fluid margin-top10">
	<div class="span5"><label class="text-right asterisk">${uiLabelMap.PhoneNumber}</label></div>
	<div class="span7"><input type="tel" id="txtPhoneNumber" tabindex="11"/></div>
</div>
</div>
</div>

</#if>