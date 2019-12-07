 <div id="jm-main">
	<div class="inner clearfix">
		<div id="jm-current-content" class="clearfix">
			<div class="my-account account-create">
				<div class="dashboard">
				    <div class="page-title">
				        <h1 class='account-title'>${uiLabelMap.PartyContactInformation}</h1>
				    </div>
					<div class="screenlet account-form">
					  <div class="boxlink">
					    <a href="<@ofbizUrl>editcontactmech</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonCreateNew}</a>
					  </div>
					  <div class="screenlet-body">
					  <#if partyContactMechValueMaps?has_content>
						<style type="text/css">
							table td{
								padding-left:5px;
								padding-top:10px;
								vertical-align: middle;
							}
							table td:first-child{
								width:150px;
							}
							table th{
								padding-left:5px;
							}
							table input[type=text]{
								width:200px;
							}
							table select{
								width:222px;
							}
							table tr:first-child{
							}
							table tr{
								background:url('/obbresources/skin/frontend/default/jm_megamall/images/bkg_divider1.gif') 0 100% repeat-x;
							}
							table {
							    border-collapse: collapse;
							}
						</style>
					    <table width="100%" border="0" cellpadding="0">
					      <tr valign="bottom">
					        <th>${uiLabelMap.PartyContactType}</th>
					        <th></th>
					        <th>${uiLabelMap.CommonInformation}</th>
					        <th colspan="2">${uiLabelMap.PartySolicitingOk}?</th>
					        <th></th>
					        <th></th>
					      </tr>
					      <#list partyContactMechValueMaps as partyContactMechValueMap>
					        <#assign contactMech = partyContactMechValueMap.contactMech?if_exists />
					        <#assign contactMechType = partyContactMechValueMap.contactMechType?if_exists />
					        <#assign partyContactMech = partyContactMechValueMap.partyContactMech?if_exists />
					          <tr>
					            <td align="right" valign="top">
					              ${contactMechType.get("description",locale)}
					            </td>
					            <td>&nbsp;</td>
					            <td valign="top">
					              <#list partyContactMechValueMap.partyContactMechPurposes?if_exists as partyContactMechPurpose>
					                <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType", true) />
					                <div>
					                  <#if contactMechPurposeType?exists>
					                    ${contactMechPurposeType.get("description",locale)}
					                    <#if contactMechPurposeType.contactMechPurposeTypeId == "SHIPPING_LOCATION" && (profiledefs.defaultShipAddr)?default("") == contactMech.contactMechId>
					                      <span class="buttontextdisabled">${uiLabelMap.ObbIsDefault}</span>
					                    <#elseif contactMechPurposeType.contactMechPurposeTypeId == "SHIPPING_LOCATION">
					                      <form name="defaultShippingAddressForm" method="post" action="<@ofbizUrl>setprofiledefault/dashboard</@ofbizUrl>">
					                        <input type="hidden" name="productStoreId" value="${productStoreId}" />
					                        <input type="hidden" name="defaultShipAddr" value="${contactMech.contactMechId}" />
					                        <input type="hidden" name="partyId" value="${party.partyId}" />
					                        <input type="submit" value="Set as shipping default" class="button" />
					                      </form>

					                    </#if>
					                    <#if contactMechPurposeType.contactMechPurposeTypeId == "BILLING_LOCATION" && (profiledefs.defaultBillAddr)?default("") == contactMech.contactMechId>
					                      <span class="buttontextdisabled">${uiLabelMap.ObbIsDefault}</span>
					                    <#elseif contactMechPurposeType.contactMechPurposeTypeId == "BILLING_LOCATION">
					                      <form name="defaultBillingAddressForm" method="post" action="<@ofbizUrl>setprofiledefault/dashboard</@ofbizUrl>">
					                        <input type="hidden" name="productStoreId" value="${productStoreId}" />
					                        <input type="hidden" name="defaultBillAddr" value="${contactMech.contactMechId}" />
					                        <input type="hidden" name="partyId" value="${party.partyId}" />
					                        <input type="submit" value="Set as billing default" class="button" />
					                      </form>
					                    </#if>
					                  <#else>
					                    ${uiLabelMap.PartyPurposeTypeNotFound}: "${partyContactMechPurpose.contactMechPurposeTypeId}"
					                  </#if>
					                  <#if partyContactMechPurpose.thruDate?exists>(${uiLabelMap.CommonExpire}:${partyContactMechPurpose.thruDate.toString()})</#if>
					                </div>
					              </#list>
					              <#if contactMech.contactMechTypeId?if_exists = "POSTAL_ADDRESS">
					                <#assign postalAddress = partyContactMechValueMap.postalAddress?if_exists />
					                <div>
					                  <#if postalAddress?exists>
					                    <#if postalAddress.toName?has_content>${uiLabelMap.CommonTo}: ${postalAddress.toName}<br /></#if>
					                    <#if postalAddress.attnName?has_content>${uiLabelMap.PartyAddrAttnName}: ${postalAddress.attnName}<br /></#if>
					                    ${postalAddress.address1}<br />
					                    <#if postalAddress.address2?has_content>${postalAddress.address2}<br /></#if>
					                    ${postalAddress.city}<#if postalAddress.stateProvinceGeoId?has_content>,&nbsp;${postalAddress.stateProvinceGeoId}</#if>&nbsp;${postalAddress.postalCode?if_exists}
					                    <#if postalAddress.countryGeoId?has_content><br />${postalAddress.countryGeoId}</#if>
					                    <#if (!postalAddress.countryGeoId?has_content || postalAddress.countryGeoId?if_exists = "USA")>
					                      <#assign addr1 = postalAddress.address1?if_exists />
					                      <#if (addr1.indexOf(" ") > 0)>
					                        <#assign addressNum = addr1.substring(0, addr1.indexOf(" ")) />
					                        <#assign addressOther = addr1.substring(addr1.indexOf(" ")+1) />
					                        <a target="_blank" href="${uiLabelMap.CommonLookupWhitepagesAddressLink}" class="linktext">(${uiLabelMap.CommonLookupWhitepages})</a>
					                      </#if>
					                    </#if>
					                  <#else>
					                    ${uiLabelMap.PartyPostalInformationNotFound}.
					                  </#if>
					                  </div>
					              <#elseif contactMech.contactMechTypeId?if_exists = "TELECOM_NUMBER">
					                <#assign telecomNumber = partyContactMechValueMap.telecomNumber?if_exists>
					                <div>
					                <#if telecomNumber?exists>
					                  ${telecomNumber.countryCode?if_exists}
					                  <#if telecomNumber.areaCode?has_content>${telecomNumber.areaCode}-</#if>${telecomNumber.contactNumber?if_exists}
					                  <#if partyContactMech.extension?has_content>ext&nbsp;${partyContactMech.extension}</#if>
					                  <#if (!telecomNumber.countryCode?has_content || telecomNumber.countryCode = "011")>
					                    <a target="_blank" href="${uiLabelMap.CommonLookupAnywhoLink}" class="linktext">${uiLabelMap.CommonLookupAnywho}</a>
					                    <a target="_blank" href="${uiLabelMap.CommonLookupWhitepagesTelNumberLink}" class="linktext">${uiLabelMap.CommonLookupWhitepages}</a>
					                  </#if>
					                <#else>
					                  ${uiLabelMap.PartyPhoneNumberInfoNotFound}.
					                </#if>
					                </div>
					              <#elseif contactMech.contactMechTypeId?if_exists = "EMAIL_ADDRESS">
					                  ${contactMech.infoString}
					                  <a href="mailto:${contactMech.infoString}" class="linktext">(${uiLabelMap.PartySendEmail})</a>
					              <#elseif contactMech.contactMechTypeId?if_exists = "WEB_ADDRESS">
					                <div>
					                  ${contactMech.infoString}
					                  <#assign openAddress = contactMech.infoString?if_exists />
					                  <#if !openAddress.startsWith("http") && !openAddress.startsWith("HTTP")><#assign openAddress = "http://" + openAddress /></#if>
					                  <a target="_blank" href="${openAddress}" class="linktext">(${uiLabelMap.CommonOpenNewWindow})</a>
					                </div>
					              <#else>
					                ${contactMech.infoString?if_exists}
					              </#if>
					              <div>(${uiLabelMap.CommonUpdated}:&nbsp;${partyContactMech.fromDate.toString()})</div>
					              <#if partyContactMech.thruDate?exists><div>${uiLabelMap.CommonDelete}:&nbsp;${partyContactMech.thruDate.toString()}</div></#if>
					            </td>
					            <td align="center" valign="top"><div>(${partyContactMech.allowSolicitation?if_exists})</div></td>
					            <td>&nbsp;</td>
					            <td align="right" valign="top">
					              <a href="<@ofbizUrl>editcontactmech?contactMechId=${contactMech.contactMechId}</@ofbizUrl>" class="button">${uiLabelMap.CommonUpdate}</a>
					            </td>
					            <td align="right" valign="top">
					              <form name= "deleteContactMech_${contactMech.contactMechId}" method= "post" action= "<@ofbizUrl>deleteContactMech</@ofbizUrl>">
					                <div>
					                <input type= "hidden" name= "contactMechId" value= "${contactMech.contactMechId}"/>
					                <a href='javascript:document.deleteContactMech_${contactMech.contactMechId}.submit()' class='button'>${uiLabelMap.CommonExpire}</a>
					              </div>
					              </form>
					            </td>
					          </tr>
					      </#list>
					    </table>
					  <#else>
					    <label>${uiLabelMap.PartyNoContactInformation}.</label><br />
					  </#if>
					  </div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>