<#-- TODO deleted -->
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
		<div class="row-fluid">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.DADistributorId}</label></div>
				<div class="span7"><input type="text" id="partyCode" class="no-space" tabindex="2"/></div>
		</div>
		<div class="row-fluid">
			<div class="span5"><label class="text-right">${uiLabelMap.BSCurrencyUomId}&nbsp;&nbsp;&nbsp;</label></div>
			<div class="span7"><div id="currencyUomId"></div></div>
		</div>
		<div class="row-fluid">
			<div class="span5"><label class="text-right asterisk">${uiLabelMap.BSSupervisor}</label></div>
			<div class="span7">
				<div id="divSupervisor">
					<div style="border-color: transparent;" id="jqxgridSupervisor"></div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span5"><label class="text-right">${uiLabelMap.BSDescription}&nbsp;&nbsp;&nbsp;</label></div>
			<div class="span7"><input type="text" id="comments" tabindex="5"/></div>
		</div>
		<#if parameters.partyId?exists>
		<div class="row-fluid">
			<div class="span5"><label class="text-right">${uiLabelMap.EmailAddress}&nbsp;&nbsp;&nbsp;</label></div>
			<div class="span7"><input type="email" id="txtEmailAddress" tabindex="7"/></div>
		</div>
		<#else>
		<div class="row-fluid">
			<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsCatalog}</label></div>
			<div class="span7"><div id="txtCatalog"></div></div>
		</div>
		</#if>
	</div>
	<div class="span5">
		<div class="row-fluid">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.DADistributorName}</label></div>
				<div class="span7"><input type="text" id="groupName" tabindex="1"/></div>
		</div>
		<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.FormFieldTitle_officeSiteName}&nbsp;&nbsp;&nbsp;</label></div>
				<div class="span7"><input type="text" id="officeSiteName" tabindex="3"/></div>
		</div>
		<div class="row-fluid">
			<div class="span5"><label class="text-right">${uiLabelMap.PartyTaxAuthInfos}&nbsp;&nbsp;&nbsp;</label></div>
			<div class="span7"><input type="text" id="taxAuthInfos" tabindex="4"/></div>
		</div>
		<#if !parameters.partyId?exists>
		<div class="row-fluid">
			<div class="span5"><label class="text-right asterisk">${uiLabelMap.BSProductStore}</label></div>
			<div class="span7"><div id="txtProductStore"></div></div>
		</div>
		</#if>
		<#if parameters.partyId?exists>
		<div class="row-fluid">
			<div class="span5"><label class="text-right asterisk">${uiLabelMap.PhoneNumber}</label></div>
			<div class="span7"><input type="number" id="txtPhoneNumber" tabindex="6"/></div>
		</div>
		</#if>
	</div>
</div>