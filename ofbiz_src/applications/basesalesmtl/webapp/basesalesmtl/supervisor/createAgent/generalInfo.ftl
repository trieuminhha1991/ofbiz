<div class="row-fluid form-horizontal form-window-content-custom">
<div class="span3">
<div class="row-fluid">
	<div class="span12">
		<div class="logo-company">
			<input type="file" id="logoImageUrl" style="visibility:hidden;" accept="image/*"/>		
			<img src="/salesmtlresources/logo/LOGO_demo.png" id="logoImage"/>
		</div>
	</div>
</div>
</div>
<div class="span4">
<div class="row-fluid margin-top10">
		<div class="span5"><label class="text-right">${uiLabelMap.BSAgentId}&nbsp;&nbsp;&nbsp;</label></div>
		<div class="span7"><input type="text" id="partyCode" class="no-space" tabindex="2"/></div>
</div>
<div class="row-fluid margin-top10">
		<div class="span5"><label class="text-right">${uiLabelMap.FormFieldTitle_officeSiteName}&nbsp;&nbsp;&nbsp;</label></div>
		<div class="span7"><input type="text" id="officeSiteName" tabindex="3"/></div>
</div>
<div class="row-fluid margin-top10">
	<div class="span5"><label class="text-right">${uiLabelMap.BSCurrencyUomId}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span7"><div id="currencyUomId"></div></div>
</div>
<div class="row-fluid margin-top10">
	<div class="span5"><label class="text-right">${uiLabelMap.DADistributor}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span7">
		<div id="divDistributor">
			<div style="border-color: transparent;" id="jqxgridDistributor"></div>
		</div>
	</div>
</div>
<#if !parameters.partyId?exists>
<div class="row-fluid margin-top10">
	<div class="span5"><label class="text-right">${uiLabelMap.BSRoute}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span7">
		<div id="divRoute">
			<div style="border-color: transparent;" id="jqxgridRoute"></div>
		</div>
	</div>
</div>
</#if>
<#if parameters.partyId?exists>
<div class="row-fluid margin-top10">
	<div class="span5"><label class="text-right">${uiLabelMap.EmailAddress}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span7"><input type="email" id="txtEmailAddress" tabindex="6"/></div>
</div>
</#if>
</div>
<div class="span5 no-left-margin">
<div class="row-fluid margin-top10">
		<div class="span5"><label class="text-right asterisk">${uiLabelMap.BSAgentName}</label></div>
		<div class="span7"><input type="text" id="groupName" tabindex="1"/></div>
</div>
<div class="row-fluid margin-top10">
	<div class="span5"><label class="text-right">${uiLabelMap.PartyTaxAuthInfos}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span7"><input type="text" id="taxAuthInfos" tabindex="4"/></div>
</div>
<div class="row-fluid margin-top10">
	<div class="span5"><label class="text-right">${uiLabelMap.BSDescription}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span7"><input type="text" id="comments" tabindex="5"/></div>
</div>
<div class="row-fluid margin-top10">
	<div class="span5"><label class="text-right">${uiLabelMap.BSSalesman}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span7">
		<div id="divSalesman">
			<div style="border-color: transparent;" id="jqxgridSalesman"></div>
		</div>
	</div>
</div>
<#if !parameters.partyId?exists>
<div class="row-fluid margin-top10">
	<div class="span5"><label class="text-right asterisk">${uiLabelMap.BSProductStore}</label></div>
	<div class="span7"><div id="txtProductStore"></div></div>
</div>
</#if>
<#if parameters.partyId?exists>
<div class="row-fluid margin-top10">
	<div class="span5"><label class="text-right asterisk">${uiLabelMap.PhoneNumber}</label></div>
	<div class="span7"><input type="number" id="txtPhoneNumber" tabindex="7"/></div>
</div>
</#if>
</div>
</div>