<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<div  class="cartdiv">
  <h2>${uiLabelMap.EcommerceMyAccount}</h2>
  <div class="screenlet-body clearfix">
    <div>
      <a class="button" href="<@ofbizUrl>editProfile</@ofbizUrl>">${uiLabelMap.EcommerceEditProfile}</a>
      <b>${uiLabelMap.PartyContactInformation}:</b>
      <label>${firstName?if_exists} ${lastName?if_exists}</label>
      <input type="hidden" id="updatedEmailContactMechId" name="emailContactMechId" value="${emailContactMechId?if_exists}" />
      <input type="hidden" id="updatedEmailAddress" name="updatedEmailAddress" value="${emailAddress?if_exists}" />
      <#if emailAddress?exists>
        <label>${emailAddress?if_exists}</label>
        <a href="mailto:${emailAddress?if_exists}" class="linkcolor">(${uiLabelMap.PartySendEmail})</a>
      </#if>
      <div id="serverError_${emailContactMechId?if_exists}" class="errorMessage"></div>
    </div>
    <br />
    <#-- Manage Addresses -->
    <div>
      <h2>${uiLabelMap.EcommerceAddressBook}</h2>
      <a class="button" href="<@ofbizUrl>manageAddress</@ofbizUrl>">${uiLabelMap.EcommerceManageAddresses}</a>
      <div class="left center">
          <table style="margin-top:5px;">
		<thead>
			<tr>
				<td colspan="3">${uiLabelMap.EcommercePrimaryShippingAddress}</td>
			</tr>
			<tr>
				<td>
					${uiLabelMap.PartyAddressLine1}/${uiLabelMap.PartyAddressLine2}
				</td>
				<td>
					${uiLabelMap.CommonCity}, ${uiLabelMap.CommonState}, ${uiLabelMap.PartyZipCode}, ${uiLabelMap.CommonCountry}
				</td>
				<td>${uiLabelMap.PartyPhoneNumber}</td>
			</tr>
		</thead>
		<tr>
		          <#if shipToContactMechId?exists>
		            <td>${shipToAddress1?if_exists}</td>
		            <#if shipToAddress2?has_content><td>${shipToAddress2?if_exists}</td></#if>
		            <td>
		              <table>
				<tr>
			                <td>
			                  <#if shipToStateProvinceGeoId?has_content && shipToStateProvinceGeoId != "_NA_">
			                    ${shipToStateProvinceGeoId}
			                  </#if>
			                  ${shipToCity?if_exists},
			                  ${shipToPostalCode?if_exists}
			                </td>
			                <td>${shipToCountryGeoId?if_exists}</td>
		                </tr>
		              </table>
		            </td>
		            <#if shipToTelecomNumber?has_content>
		            <td>
		              ${shipToTelecomNumber.countryCode?if_exists}-
		              ${shipToTelecomNumber.areaCode?if_exists}-
		              ${shipToTelecomNumber.contactNumber?if_exists}
		              <#if shipToExtension?exists>-${shipToExtension?if_exists}</#if>
		            </td>
		            </#if>
		          <#else>
		            <td>${uiLabelMap.PartyPostalInformationNotFound}</td>
		          </#if>
	          </tr>
          </table>
      </div>
      <br />
      <div class="right center">
        <h2>${uiLabelMap.EcommercePrimaryBillingAddress}</h2>
          <table>
		<thead>
			<tr>
				<td>
					${uiLabelMap.PartyAddressLine1}/${uiLabelMap.PartyAddressLine2}
				</td>
				<td>
					${uiLabelMap.CommonCity}, ${uiLabelMap.CommonState}, ${uiLabelMap.PartyZipCode}, ${uiLabelMap.CommonCountry}
				</td>
				<td>${uiLabelMap.PartyPhoneNumber}</td>
			</tr>
		</thead>
		<tr>
		          <#if billToContactMechId?exists>
		            <td>${billToAddress1?if_exists}</td>
		            <#if billToAddress2?has_content><td>${billToAddress2?if_exists}</td></#if>
		            <td>
		              <table>
				<tr>
			                <td>
			                  <#if billToStateProvinceGeoId?has_content && billToStateProvinceGeoId != "_NA_">
			                    ${billToStateProvinceGeoId}
			                  </#if>
			                  ${billToCity?if_exists},
			                  ${billToPostalCode?if_exists}
			                </td>
			                <td>${billToCountryGeoId?if_exists}</td>
		                </tr>
		              </table>
		            </td>
		            <#if billToTelecomNumber?has_content>
		            <td>
		              ${billToTelecomNumber.countryCode?if_exists}-
		              ${billToTelecomNumber.areaCode?if_exists}-
		              ${billToTelecomNumber.contactNumber?if_exists}
		              <#if billToExtension?exists>-${billToExtension?if_exists}</#if>
		            </td>
		            </#if>
		          <#else>
		            <td>${uiLabelMap.PartyPostalInformationNotFound}</td>
		          </#if>
	          </tr>
          </table>
      </div>
    </div>
  </div>
</div>