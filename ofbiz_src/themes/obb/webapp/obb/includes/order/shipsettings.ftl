<#assign cart = sessionAttributes.shoppingCart?if_exists>
<div id="jm-container" class="jm-col1-layout wrap not-breadcrumbs clearfix">
	<div class="main clearfix">
		<div id="jm-mainbody" class="clearfix">
			<div id="jm-main">
				<div class="inner clearfix">
					<div id="jm-current-content" class="clearfix">
						<div class="cart">
						    <div class="page-title title-buttons">
							  <h1>${uiLabelMap.OrderShippingInformation}</h1>
						    </div>
						      <style type="text/css">
							#editShippingContact label{
								width:120px;
								display:inline-block;
							}
						      </style>
							  <div id="shippingFormServerError" class="errorMessage"></div>
							  <form id="editShippingContact" method="post" action="<@ofbizUrl>processShipSettings</@ofbizUrl>" name="${parameters.formNameValue}">
							    <fieldset><legend>${uiLabelMap.OrderShippingInformation}</legend>
							      <input type="hidden" name="shippingContactMechId" value="${parameters.shippingContactMechId?if_exists}"/>
							      <input type="hidden" name="partyId" value="${cart.getPartyId()?default("_NA_")}"/>
							      <div>
							        <label for="address1">${uiLabelMap.PartyAddressLine1}*</label>
							        <input id="address1" name="address1" class="input-text required" type="text" value="${address1?if_exists}"/>
							        <span id="advice-required-address1" class="custom-advice errorMessage" style="display:none"> (${uiLabelMap.CommonRequired})</span>
							      </div>
							      <div>
							        <label for="address2">${uiLabelMap.PartyAddressLine2}</label>
							        <input id="address2" name="address2" class="input-text" type="text" value="${address2?if_exists}"/>
							      </div>
							      <div>
							        <label for="city">${uiLabelMap.CommonCity}*</label>
							        <input id="city" name="city" class="required input-text" type="text" value="${city?if_exists}"/>
							        <span id="advice-required-city" class="custom-advice errorMessage input-text" style="display:none"> (${uiLabelMap.CommonRequired})</span>
							      </div>
							      <div>
							        <label for="postalCode">${uiLabelMap.PartyZipCode}*</label>
							        <input id="postalCode" name="postalCode" class="required input-text" type="text" value="${postalCode?if_exists}" size="12" maxlength="10"/>
							        <span id="advice-required-postalCode" class="custom-advice errorMessage" style="display:none"> (${uiLabelMap.CommonRequired})</span>
							      </div>
							      <div>
							        <label for="countryGeoId">${uiLabelMap.CommonCountry}*</label>
							        <select name="countryGeoId" id="countryGeoId">
							          <#if countryGeoId??>
							            <option value="${countryGeoId!}">${countryProvinceGeo!(countryGeoId!)}</option>
							          </#if>
							          ${screens.render("component://common/widget/CommonScreens.xml#countries")}
							        </select>
							        <span id="advice-required-countryGeoId" style="display:none" class="errorMessage"> (${uiLabelMap.CommonRequired})</span>
							      </div>
							      <div>
							        <label for="state">${uiLabelMap.CommonState}*</label>
							        <select id="stateProvinceGeoId" name="stateProvinceGeoId">
							          <#if stateProvinceGeoId?has_content>
							            <option value='${stateProvinceGeoId!}'>${stateProvinceGeo!(stateProvinceGeoId!)}</option>
							          <#else>
							            <option value="_NA_">${uiLabelMap.PartyNoState}</option>
							          </#if>
							          ${screens.render("component://common/widget/CommonScreens.xml#states")}
							        </select>
							        <span id="advice-required-stateProvinceGeoId" style="display:none" class="errorMessage">(${uiLabelMap.CommonRequired})</span>
							      </div>
							      <div class="buttons">
							        <button type="submit" value="empty_cart" title="${uiLabelMap.CommonContinue}" class="button btn-empty" id="empty_cart_button">
								<span><span>${uiLabelMap.CommonContinue}</span></span>
				                    </button>
							      </div>
							    </fieldset>
							  </form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>