<div class="span12 boder-all-profile">
	<span class="text-header">${uiLabelMap.PartyContactMechs}</span>
	<div class="row-fluid no-left-margin borderBottom">
		<div class="span3 lineHeight30" style="text-align: right; font-size: 14px">
			<span class="labelColor">
				${uiLabelMap.PermanentResidence}
			</span>
		</div>
		<div class="span9 lineHeight30">
			<div class="row-fluid">
				<#if permanentResidence.contactMechId?has_content>
					<div id="permanentResidenceAfterAdd">
						<span>
						<#assign permanent = delegator.findOne("PostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId", permanentResidence.contactMechId), false)>
						 ${permanent.address1}&#44;
						 <#if permanent.wardGeoId?exists>
							<#assign ward = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", permanent.wardGeoId), false)>
							<#if ward?exists && ward.geoId != "_NA_">
								${ward.geoName?if_exists}&#44;
							</#if>
						 </#if>
						 <#if permanent.countyGeoId?exists>
							<#assign district = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", permanent.countyGeoId), false)>
							<#if district?exists && district.geoId != "_NA_">
								${district.geoName?if_exists}&#44;
							</#if>
						 </#if>
						 <#if permanent.stateProvinceGeoId?exists>
							 <#assign stateProvince = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", permanent.stateProvinceGeoId), false)>
							 ${stateProvince.geoName?if_exists}&#46;
						 </#if>
						</span>	 
						<button class="grid-action-button icon-edit blue" type="button" id="editPermanentResidenceBtn" title="${StringUtil.wrapString(uiLabelMap.CommonEdit)}"></button>
					</div>
				<#else>
					<div id="addPermanentResidence">
						<button class="grid-action-button icon-plus-sign blue" type="button" id="addPermanentResidenceBtn">
							${uiLabelMap.CommonAddNew}
						</button>
					</div>
					<div style="display: none;" id="permanentResidenceAfterAdd">
						<span></span>
						<button class="grid-action-button icon-edit blue" type="button" id="editPermanentResidenceBtn" title="${StringUtil.wrapString(uiLabelMap.CommonEdit)}"></button>
					</div>
				</#if>
			</div>
		</div>
	</div>
	<div class="row-fluid no-left-margin borderBottom">
		<div class="span3 lineHeight30" style="text-align: right; font-size: 14px">
			<span class="labelColor">${uiLabelMap.ContactAddress}</span>
		</div>
		<div class="span9 lineHeight30">
			<#if currentResidence.contactMechId?has_content>
				<div id="currentResidenceAfterAdd">
					<span>
					<#assign residence = delegator.findOne("PostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId", currentResidence.contactMechId), false)>
					 ${residence.address1}&#44;
					 <#if residence.wardGeoId?exists>
						<#assign ward = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", residence.wardGeoId), false)>
						<#if ward?exists && ward.geoId != "_NA_">
							${ward.geoName?if_exists}&#44;
						</#if>
					 </#if>
					 <#if residence.countyGeoId?exists>
						<#assign district = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", residence.countyGeoId), false)>
						<#if district?exists && district.geoId != "_NA_">
							${district.geoName?if_exists}&#44;
						</#if>
					 </#if>
					 <#assign stateProvince = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", residence.stateProvinceGeoId), false)>
					 ${stateProvince.geoName?if_exists}&#46;
					</span>
					 <button class="grid-action-button icon-edit blue" type="button" id="editCurrentResidenceBtn" title="${StringUtil.wrapString(uiLabelMap.CommonEdit)}"></button>
				</div>
			<#else>
				<div id="addCurrentResidence">
					<button class="grid-action-button icon-plus-sign blue" type="button" id="addCurrentResidenceBtn">
						${uiLabelMap.CommonAddNew}
					</button>
				</div>
				<div style="display: none;" id="currentResidenceAfterAdd">
					<span id="currentResidenceText"></span>
					<button class="grid-action-button icon-edit blue" type="button" id="editCurrentResidenceBtn" title="${StringUtil.wrapString(uiLabelMap.CommonEdit)}"></button>
				</div>
			</#if>
		</div>
	</div>
	<div class="row-fluid no-left-margin borderBottom">
		<div class="span3 lineHeight30" style="text-align: right; font-size: 14px">
			<span class="labelColor">${uiLabelMap.CommonEmail}</span>
		</div>
		<div class="span9" style="line-height: 27px">
			<div id="displayEmail">
				<#if partyEmail.emailAddress?exists>
					<span>
						${partyEmail.emailAddress}
					</span>	
					<button class="grid-action-button icon-edit blue" type="button" id="editEmailAddressBtn">
					</button>
				<#else>
					<span>
						<button class="grid-action-button icon-plus-sign blue" type="button" id="addEmailAddressBtn">
							${uiLabelMap.CommonAddNew}
						</button>
					</span>	
					<button class="grid-action-button icon-edit blue" type="button" id="editEmailAddressBtn" style="display: none">
					</button>
				</#if>
				
			</div>
			<div class="row-fluid" id="editEmail" style="display: none">
				<div class="span4">
					<input type="text" id="editEmailInput">
				</div>
				<div class="span8">
					<button class="grid-action-button icon-ok" id="saveUpdateEmail" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonSubmit)}"></button>
					<button class="grid-action-button icon-remove" id="cancelUpdateEmail" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}"></button>
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid no-left-margin borderBottom">
		<div class="span3 lineHeight30" style="text-align: right; font-size: 14px"><span class="labelColor">${uiLabelMap.PhoneNumber}</span></div>
		<div class="span9" style="line-height: 27px">
			<div id="displayPhoneNumber">
				<#if phoneNumber?has_content && phoneNumber.contactMechId?exists>
					<span>
						${phoneNumber.countryCode?if_exists} ${phoneNumber.areaCode?if_exists} ${phoneNumber.contactNumber?if_exists}&nbsp;
					</span>	
					<button class="grid-action-button icon-edit blue" type="button" id="editPhoneNumberBtn"></button>
				<#else>
					<span>
						<button class="grid-action-button icon-plus-sign blue" type="button" id="addPhoneNumberBtn">
							${uiLabelMap.CommonAddNew}
						</button>	
					</span>
					<button class="grid-action-button icon-edit blue" type="button" id="editPhoneNumberBtn" style="display: none;"></button>
				</#if>
				
			</div>
			<div class="row-fluid" id="editPhoneNumber" style="display: none">
				<div class="span5">
					<!-- <input type="text" id="phoneNbrCountryCode">
					<input type="text" id="phoneNbrAreaCode"> -->
					<input type="text" id="phoneNbrContactNbr">
				</div>
				<div class="span7">
					<button class="grid-action-button icon-ok" id="saveUpdatePhoneNbr" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonSubmit)}"></button>
					<button class="grid-action-button icon-remove" id="cancelUpdatePhoneNbr" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}"></button>
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid no-left-margin borderBottom">
		<div class="span3 lineHeight30" style="text-align: right; font-size: 14px">
			<span class="labelColor">${uiLabelMap.PhoneMobile}</span>
		</div>
		<div class="span9" style="line-height: 27px">
			<div id="displayMobileNumber">
				<#if mobileNumber?has_content && mobileNumber.contactMechId?exists>
					<span>
						${mobileNumber.countryCode?if_exists} ${mobileNumber.areaCode?if_exists} ${mobileNumber.contactNumber?if_exists}&nbsp;
					</span>	
					<button class="grid-action-button icon-edit blue" type="button" id="editMobileNumberBtn"></button>
				<#else>
					<span>
						<button class="grid-action-button icon-plus-sign blue" type="button" id="addMobileNumberBtn">
							${uiLabelMap.CommonAddNew}
						</button>
					</span>		
					<button class="grid-action-button icon-edit blue" type="button" id="editMobileNumberBtn" style="display: none;"></button>
				</#if>
				
			</div>
			<div class="row-fluid" id="editMobileNumber" style="display: none">
				<div class="span5">
					<!-- <input type="text" id="mobileNbrCountryCode">
					<input type="text" id="mobileNbrAreaCode"> -->
					<input type="text" id="mobileNbrContactNbr">
				</div>
				<div class="span7">
					<button class="grid-action-button icon-ok" id="saveUpdateMobileNbr" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonSubmit)}"></button>
					<button class="grid-action-button icon-remove" id="cancelUpdateMobileNbr" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}"></button>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="addPermanentResidenceWindow" class="hide">
	<div>
		${StringUtil.wrapString(uiLabelMap.AddPermanentResidence)}
	</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${uiLabelMap.CommonAddress1}</label>
				</div>
				<div class="span8">
					<input type="text" id="address1PermRes">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${uiLabelMap.CommonCountry}</label>
				</div>
				<div class="span8">
					<div id="countryGeoIdPermRes"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${uiLabelMap.CommonCity}</label>
				</div>
				<div class="span8">
					<div id="stateGeoIdPermRes"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${uiLabelMap.PartyDistrictGeoId}</label>
				</div>
				<div class="span8">
					<div id="countyGeoIdPermRes"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="btnPermCancel" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="btnPermSave">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>		
</div>
<div id="addCurrentResidenceWindow" class="hide">
	<div>
		${StringUtil.wrapString(uiLabelMap.AddCurrentResidence)}
	</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${uiLabelMap.CommonAddress1}</label>
				</div>
				<div class="span8">
					<input type="text" id="address1CurrRes">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${uiLabelMap.CommonCountry}</label>
				</div>
				<div class="span8">
					<div id="countryGeoIdCurrRes"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${uiLabelMap.CommonCity}</label>
				</div>
				<div class="span8">
					<div id="stateGeoIdCurrRes"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${uiLabelMap.PartyDistrictGeoId}</label>
				</div>
				<div class="span8">
					<div id="countyGeoIdCurrRes"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="btnCurrCancel" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="btnCurrSave">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>		
</div>
<script type="text/javascript" src="/hrresources/js/profile/contactInfo.js"></script>
