<div id="jm-container" class="jm-col1-layout wrap not-breadcrumbs clearfix">
	<div class="main clearfix">
		<div id="jm-mainbody" class="clearfix">
			<div id="jm-main">
				<div class="inner clearfix">
					<div id="jm-current-content" class="clearfix">
						<div class="cart">
						<div class="page-title title-buttons">
								<#if canNotView>
								  <h3>${uiLabelMap.AccountingEFTNotBelongToYou}.</h3></div>
								&nbsp;<a href="<@ofbizUrl>${donePage}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonGoBack}</a>
								<#else>
								    <#if !eftAccount?exists>
								      <h1>${uiLabelMap.AccountingAddNewEftAccount}</h1>
					        </div>
								      <form method="post" action="<@ofbizUrl>createEftAccount?DONE_PAGE=${donePage}</@ofbizUrl>" name="editeftaccountform" style="margin: 0;">
								    <#else>
								      <h1>${uiLabelMap.PageTitleEditEFTAccount}</h1>
								      <form method="post" action="<@ofbizUrl>updateEftAccount?DONE_PAGE=${donePage}</@ofbizUrl>" name="editeftaccountform" style="margin: 0;">
								        <input type="hidden" name="paymentMethodId" value="${paymentMethodId}" />
								    </#if>
								    <a href="<@ofbizUrl>${donePage}</@ofbizUrl>" class="button">${uiLabelMap.CommonGoBack}</a>
								    <span style="margin-left:10px;margin-right:10px;">|</span><a href="javascript:document.editeftaccountform.submit()" class="button">${uiLabelMap.CommonSave}</a>
								    <p/>
								    <table id="shopping-cart-table" class="data-table cart-table">
								    <tr>
								      <td width="26%" align="right" valign="top"><div>${uiLabelMap.AccountingNameOnAccount}</div></td>
								      <td width="5">&nbsp;</td>
								      <td width="74%">
								        <input type="text" class="input-text" size="30" maxlength="60" name="nameOnAccount" value="${eftAccountData.nameOnAccount?if_exists}" />
								      *</td>
								    </tr>
								    <tr>
								      <td width="26%" align="right" valign="top"><div>${uiLabelMap.AccountingCompanyNameOnAccount}</div></td>
								      <td width="5">&nbsp;</td>
								      <td width="74%">
								        <input type="text" class="input-text" size="30" maxlength="60" name="companyNameOnAccount" value="${eftAccountData.companyNameOnAccount?if_exists}" />
								      </td>
								    </tr>
								    <tr>
								      <td width="26%" align="right" valign="top"><div>${uiLabelMap.AccountingBankName}</div></td>
								      <td width="5">&nbsp;</td>
								      <td width="74%">
								        <input type="text" class="input-text" size="30" maxlength="60" name="bankName" value="${eftAccountData.bankName?if_exists}" />
								      *</td>
								    </tr>
								    <tr>
								      <td width="26%" align="right" valign="top"><div>${uiLabelMap.AccountingRoutingNumber}</div></td>
								      <td width="5">&nbsp;</td>
								      <td width="74%">
								        <input type="text" class="input-text" size="10" maxlength="30" name="routingNumber" value="${eftAccountData.routingNumber?if_exists}" />
								      *</td>
								    </tr>
								    <tr>
								      <td width="26%" align="right" valign="top"><div>${uiLabelMap.AccountingAccountType}</div></td>
								      <td width="5">&nbsp;</td>
								      <td width="74%">
								        <select name="accountType" class="selectBox">
								          <option>${eftAccountData.accountType?if_exists}</option>
								          <option></option>
								          <option>${uiLabelMap.CommonChecking}</option>
								          <option>${uiLabelMap.CommonSavings}</option>
								        </select>
								      *</td>
								    </tr>
								    <tr>
								      <td width="26%" align="right" valign="top"><div>${uiLabelMap.AccountingAccountNumber}</div></td>
								      <td width="5">&nbsp;</td>
								      <td width="74%">
								        <input type="text" class="input-text" size="20" maxlength="40" name="accountNumber" value="${eftAccountData.accountNumber?if_exists}" />
								      *</td>
								    </tr>
								    <tr>
								      <td width="26%" align="right" valign="top"><div>${uiLabelMap.CommonDescription}</div></td>
								      <td width="5">&nbsp;</td>
								      <td width="74%">
								        <input type="text" class="input-text" size="30" maxlength="60" name="description" value="${paymentMethodData.description?if_exists}" />
								      </td>
								    </tr>

								    <tr>
								      <td width="26%" align="right" valign="top"><div>${uiLabelMap.PartyBillingAddress}</div></td>
								      <td width="5">&nbsp;</td>
								      <td width="74%">
								        <#-- Removed because is confusing, can add but would have to come back here with all data populated as before...
								        <a href="<@ofbizUrl>editcontactmech</@ofbizUrl>" class="buttontext">
								          [Create New Address]</a>&nbsp;&nbsp;
								        -->
								        <table width="100%" border="0" cellpadding="1">
								        <#if curPostalAddress?exists>
								          <tr>
								            <td align="right" valign="top" width="1%">
								              <input type="radio" name="contactMechId" value="${curContactMechId}" checked="checked" />
								            </td>
								            <td valign="top" width="80%">
								              <div><b>${uiLabelMap.PartyUseCurrentAddress}:</b></div>
								              <#list curPartyContactMechPurposes as curPartyContactMechPurpose>
								                <#assign curContactMechPurposeType = curPartyContactMechPurpose.getRelatedOne("ContactMechPurposeType", true)>
								                <div>
								                  <b>${curContactMechPurposeType.get("description",locale)?if_exists}</b>
								                  <#if curPartyContactMechPurpose.thruDate?exists>
								                    (${uiLabelMap.CommonExpire}:${curPartyContactMechPurpose.thruDate.toString()})
								                  </#if>
								                </div>
								              </#list>
								              <div>
								                <#if curPostalAddress.toName?exists><b>${uiLabelMap.CommonTo}:</b> ${curPostalAddress.toName}<br /></#if>
								                <#if curPostalAddress.attnName?exists><b>${uiLabelMap.PartyAddrAttnName}:</b> ${curPostalAddress.attnName}<br /></#if>
								                ${curPostalAddress.address1?if_exists}<br />
								                <#if curPostalAddress.address2?exists>${curPostalAddress.address2}<br /></#if>
								                ${curPostalAddress.city}<#if curPostalAddress.stateProvinceGeoId?has_content>,&nbsp;${curPostalAddress.stateProvinceGeoId}</#if>&nbsp;${curPostalAddress.postalCode}
								                <#if curPostalAddress.countryGeoId?exists><br />${curPostalAddress.countryGeoId}</#if>
								              </div>
								              <div>(${uiLabelMap.CommonUpdated}:&nbsp;${(curPartyContactMech.fromDate.toString())?if_exists})</div>
								              <#if curPartyContactMech.thruDate?exists><div><b>${uiLabelMap.CommonDelete}:&nbsp;${curPartyContactMech.thruDate.toString()}</b></div></#if>
								            </td>
								          </tr>
								        <#else>
								           <#-- <tr>
								            <td valign="top" colspan="2">
								              <div>${uiLabelMap.PartyNoBillingAddress}</div>
								            </td>
								          </tr> -->
								        </#if>
								          <#-- is confusing
								          <tr>
								            <td valign="top" colspan="2">
								              <div><b>${uiLabelMap.ObbMessage3}</b></div>
								            </td>
								          </tr>
								          -->
								          <#list postalAddressInfos as postalAddressInfo>
								            <#assign contactMech = postalAddressInfo.contactMech>
								            <#assign partyContactMechPurposes = postalAddressInfo.partyContactMechPurposes>
								            <#assign postalAddress = postalAddressInfo.postalAddress>
								            <#assign partyContactMech = postalAddressInfo.partyContactMech>
								            <tr>
								              <td align="right" valign="top" width="1%">
								                <input type="radio" name="contactMechId" value="${contactMech.contactMechId}" />
								              </td>
								              <td valign="top" width="80%">
								                <#list partyContactMechPurposes as partyContactMechPurpose>
								                    <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType", true)>
								                    <div>
								                      <b>${contactMechPurposeType.get("description",locale)?if_exists}</b>
								                      <#if partyContactMechPurpose.thruDate?exists>(${uiLabelMap.CommonExpire}:${partyContactMechPurpose.thruDate})</#if>
								                    </div>
								                </#list>
								                <div>
								                  <#if postalAddress.toName?exists><b>${uiLabelMap.CommonTo}:</b> ${postalAddress.toName}<br /></#if>
								                  <#if postalAddress.attnName?exists><b>${uiLabelMap.PartyAddrAttnName}:</b> ${postalAddress.attnName}<br /></#if>
								                  ${postalAddress.address1?if_exists}<br />
								                  <#if postalAddress.address2?exists>${postalAddress.address2}<br /></#if>
								                  ${postalAddress.city}<#if postalAddress.stateProvinceGeoId?has_content>,&nbsp;${postalAddress.stateProvinceGeoId}</#if>&nbsp;${postalAddress.postalCode}
								                  <#if postalAddress.countryGeoId?exists><br />${postalAddress.countryGeoId}</#if>
								                </div>
								                <div>(${uiLabelMap.CommonUpdated}:&nbsp;${(partyContactMech.fromDate.toString())?if_exists})</div>
								                <#if partyContactMech.thruDate?exists><div><b>${uiLabelMap.CommonDelete}:&nbsp;${partyContactMech.thruDate.toString()}</b></div></#if>
								              </td>
								            </tr>
								          </#list>
								          <#if !postalAddressInfos?has_content && !curContactMech?exists>
								              <tr><td colspan="2"><div>${uiLabelMap.PartyNoContactInformation}.</div></td></tr>
								          </#if>
								        </table>
								      </td>
								    </tr>
								  </table>
								  </form>
								  <a href="<@ofbizUrl>${donePage}</@ofbizUrl>" class="button">${uiLabelMap.CommonGoBack}</a>
								  <span style="margin-left:10px;margin-right:10px;">|</span><a href="javascript:document.editeftaccountform.submit()" class="button">${uiLabelMap.CommonSave}</a>
								</#if>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
