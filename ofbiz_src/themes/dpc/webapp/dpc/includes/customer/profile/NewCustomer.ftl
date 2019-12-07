<div class="container">
	<h1>${uiLabelMap.EcommerceMyAccount}</h1>
	<form id="newUserForm" method="post" action="<@ofbizUrl>createCustomerProfile</@ofbizUrl>">
		<h2>${uiLabelMap.PartyContactInformation}</h2>
		<div class="content">
			<input type="hidden" name="roleTypeId" value="CUSTOMER" />
			<input type="hidden" name="emailContactMechPurposeTypeId" value="PRIMARY_EMAIL" />
			<#assign productStoreId = Static["org.ofbiz.product.store.ProductStoreWorker"].getProductStoreId(request) />
			<input type="hidden" name="productStoreId" value="${productStoreId?if_exists}" />
			<table class="form">
				<tbody>
					<tr>
						<td><span class="required">*</span>${uiLabelMap.PartyFirstName}:<span id="advice-required-firstName" style="display: none" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<input type="text" name="firstName" id="firstName" class="required" value="${parameters.firstName?if_exists}" maxlength="30" />
						</td>
					</tr>
					<tr>
						<td><span class="required">*</span>${uiLabelMap.PartyLastName}:<span id="advice-required-lastName" style="display: none" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<input type="text" name="lastName" id="lastName" class="required" value="${parameters.lastName?if_exists}" maxlength="30" />
						</td>
					</tr>
					<tr>
						<td><span class="required">*</span>${uiLabelMap.CommonEmail}:<span id="advice-required-emailAddress" style="display: none" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<input type="text" class="required validate-email" name="emailAddress" id="emailAddress" value="${parameters.emailAddress?if_exists}" maxlength="255" />
						<span id="advice-validate-email-emailAddress" class="errorMessage" style="display:none">${uiLabelMap.PartyEmailAddressNotFormattedCorrectly}</span></td>
					</tr>
				</tbody>
			</table>
		</div>
		<h2>${uiLabelMap.EcommerceAccountInformation}</h2>
		<div class="content">
			<table class="form">
				<tbody>
					<tr>
						<td><span class="required">*</span>${uiLabelMap.CommonUsername}:<span id="advice-required-username" style="display: none" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<input type="text" name="username" id="username" class="required" value="${parameters.username?if_exists}" maxlength="255" />
						</td>
					</tr>
					<tr>
						<td><span class="required">*</span>${uiLabelMap.CommonPassword}:<span id="advice-required-password" style="display: none" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<input type="password" name="password" id="password" class="required validate-password" value="${parameters.password?if_exists}" maxlength="16" />
						</td>
						<span id="advice-validate-password-password" class="errorMessage" style="display:none">${uiLabelMap["loginservices.password_may_not_equal_username"]}</span>
					</tr>
					<tr>
						<td><span class="required">*</span>${uiLabelMap.PartyRepeatPassword}:<span id="advice-required-passwordVerify" style="display: none" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<input type="password" name="passwordVerify" id="passwordVerify" class="required validate-passwordVerify" value="${parameters.passwordVerify?if_exists}" maxlength="16" />
						<span id="advice-validate-passwordVerify-passwordVerify" class="errorMessage" style="display:none">${uiLabelMap["loginservices.password_did_not_match_verify_password"]}</span></td>
					</tr>
				</tbody>
			</table>
		</div>
		<h2>${uiLabelMap.OrderShippingInformation}</h2>
		<div class="content">
			<table class="form">
				<tbody>
					<tr>
						<td><span class="required">*</span>${uiLabelMap.PartyAddressLine1}:<span id="advice-required-shipToAddress1" style="display: none" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<input type="text" name="shipToAddress1" id="shipToAddress1" class="required" value="${parameters.shipToAddress1?if_exists}" />
						</td>
					</tr>
					<tr>
						<td>${uiLabelMap.PartyAddressLine2}:</td>
						<td>
						<input type="text" name="shipToAddress2" id="shipToAddress2" value="${parameters.shipToAddress2?if_exists}" />
						</td>
					</tr>
					<tr>
						<td><span class="required">*</span>${uiLabelMap.CommonCity}:<span id="advice-required-shipToCity" style="display: none" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<input type="text" name="shipToCity" id="shipToCity" class="required" value="${parameters.shipToCity?if_exists}" />
						</td>
					</tr>
					<tr>
						<td><span class="required">*</span>${uiLabelMap.PartyZipCode}:<span id="advice-required-shipToPostalCode" style="display: none" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<input type="text" name="shipToPostalCode" id="shipToPostalCode" class="required" value="${parameters.shipToPostalCode?if_exists}" maxlength="10" />
						</td>
					</tr>
					<tr>
						<td><span class="required">*</span>${uiLabelMap.CommonCountry}:<span id="advice-required-shipToCountryGeoId" style="display: none" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<select name="shipToCountryGeoId" id="shipToCountryGeoId">
							<#if shipToCountryGeoId??>
							<option value="${shipToCountryGeoId!}">${shipToCountryProvinceGeo!(shipToCountryGeoId!)}</option>
							</#if>
							${screens.render("component://common/widget/CommonScreens.xml#countries")}
						</select></td>
					</tr>
					<tr id='shipToStates'>
						<td><span class="required">*</span>${uiLabelMap.CommonState}:<span id="advice-required-shipToStateProvinceGeoId" style="display: none" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<select id="shipToStateProvinceGeoId" name="shipToStateProvinceGeoId">
							<#if shipToStateProvinceGeoId?has_content>
							<option value='${shipToStateProvinceGeoId!}'>${shipToStateProvinceGeo!(shipToStateProvinceGeoId!)}</option>
							<#else>
							<option value="_NA_">${uiLabelMap.PartyNoState}</option>
							</#if>
						</select></td>
					</tr>
					<tr>
						<td><span class="required">*</span>${uiLabelMap.PartyPhoneNumber}: <span id="advice-required-shipToCountryCode" style="display:none" class="errorMessage"></span><span id="advice-required-shipToAreaCode" style="display:none" class="errorMessage"></span><span id="advice-required-shipToContactNumber" style="display:none" class="errorMessage"></span><span id="shipToPhoneRequired" style="display: none;" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<input type="text" name="shipToCountryCode" id="shipToCountryCode" value="${parameters.shipToCountryCode?if_exists}" size="3" maxlength="3" />
						-
						<input type="text" name="shipToAreaCode" id="shipToAreaCode" value="${parameters.shipToAreaCode?if_exists}" size="3" maxlength="3" />
						-
						<input type="text" name="shipToContactNumber" id="shipToContactNumber" value="${contactNumber?default("${parameters.shipToContactNumber?if_exists}")}" size="6" maxlength="7" />
						-
						<input type="text" name="shipToExtension" id="shipToExtension" value="${extension?default("${parameters.shipToExtension?if_exists}")}" size="3" maxlength="3" />
						</td>
					</tr>
				</tbody>
			</table>
			<input type="checkbox" class="checkbox" name="useShippingAddressForBilling" id="useShippingAddressForBilling" value="Y" <#if parameters.useShippingAddressForBilling?has_content && parameters.useShippingAddressForBilling?default("")=="Y">checked="checked"</#if> />
			${uiLabelMap.FacilityBillingAddressSameShipping}
		</div>
		<h2>${uiLabelMap.PageTitleBillingInformation}</h2>
		<div class="content">
			<table class="form">
				<tbody>
					<tr>
						<td><span class="required">*</span>${uiLabelMap.PartyAddressLine1}:<span id="advice-required-billToAddress1" style="display: none" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<input type="text" name="billToAddress1" id="billToAddress1" class="required" value="${parameters.billToAddress1?if_exists}" />
						</td>
					</tr>
					<tr>
						<td>${uiLabelMap.PartyAddressLine2}</td>
						<td>
						<input type="text" name="billToAddress2" id="billToAddress2" value="${parameters.billToAddress2?if_exists}" />
						</td>
					</tr>
					<tr>
						<td><span class="required">*</span>${uiLabelMap.CommonCity}:<span id="advice-required-billToCity" style="display: none" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<input type="text" name="billToCity" id="billToCity" class="required" value="${parameters.billToCity?if_exists}" />
						</td>
					</tr>
					<tr>
						<td><span class="required">*</span>${uiLabelMap.PartyZipCode}:<span id="advice-required-billToPostalCode" style="display: none" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<input type="text" name="billToPostalCode" id="billToPostalCode" class="required" value="${parameters.billToPostalCode?if_exists}" maxlength="10" />
						</td>
					</tr>
					<tr>
						<td><span class="required">*</span>${uiLabelMap.CommonCountry}:<span id="advice-required-billToCountryGeoId" style="display: none" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<select name="billToCountryGeoId" id="billToCountryGeoId" class='required selectBox'>
							<#if billToCountryGeoId??>
							<option value='${billToCountryGeoId!}'>${billToCountryProvinceGeo!(billToCountryGeoId!)}</option>
							</#if>
							${screens.render("component://common/widget/CommonScreens.xml#countries")}
						</select></td>
					</tr>
					<tr id='billToStates'>
						<td><span class="required">*</span>${uiLabelMap.CommonState}:<span id="advice-required-billToStateProvinceGeoId" style="display: none" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<select id="billToStateProvinceGeoId" name="billToStateProvinceGeoId">
							<#if billToStateProvinceGeoId?has_content>
							<option value='${billToStateProvinceGeoId!}'>${billToStateProvinceGeo!(billToStateProvinceGeoId!)}</option>
							<#else>
							<option value="_NA_">${uiLabelMap.PartyNoState}</option>
							</#if>
						</select></td>
					</tr>
					<tr>
						<td><span class="required">*</span>${uiLabelMap.PartyPhoneNumber}: <span id="advice-required-billToCountryCode" style="display:none" class="errorMessage"></span><span id="advice-required-billToAreaCode" style="display:none" class="errorMessage"></span><span id="advice-required-billToContactNumber" style="display:none" class="errorMessage"></span><span id="billToPhoneRequired" style="display: none;" class="errorMessage">(${uiLabelMap.CommonRequired})</span></td>
						<td>
						<input type="text" name="billToCountryCode" id="billToCountryCode" value="${parameters.billToCountryCode?if_exists}" size="3" maxlength="3"/>
						-
						<input type="text" name="billToAreaCode" id="billToAreaCode" value="${parameters.billToAreaCode?if_exists}" size="3" maxlength="3"/>
						-
						<input type="text" name="billToContactNumber" id="billToContactNumber" value="${contactNumber?default("${parameters.billToContactNumber?if_exists}")}" size="6" maxlength="7"/>
						-
						<input type="text" name="billToExtension" id="billToExtension" value="${extension?default("${parameters.billToExtension?if_exists}")}" size="3" maxlength="3"/>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div>
			<a id="submitNewUserForm" href="javascript:void(0);" class="button">${uiLabelMap.CommonSubmit}</a>
		</div>
	</form>
</div>