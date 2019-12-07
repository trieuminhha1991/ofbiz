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
</style>
<div id="jm-main">
	<div class="inner clearfix">
		<div id="jm-current-content" class="clearfix">
			<div class="my-account">
				<div class="page-title">
				<h1>Thêm mới địa chỉ</h1>
				</div>
				<#include "component://common/webcommon/includes/setDependentDropdownValuesJs.ftl"/>
				<#if canNotView>
				  <h3>${uiLabelMap.PartyContactInfoNotBelongToYou}.</h3>
				  <a href="<@ofbizUrl>${donePage}</@ofbizUrl>" class="button"><small>« </small>${uiLabelMap.CommonBack}</a>
				<#else>
				  <#if !contactMech?exists>
				    <#-- When creating a new contact mech, first select the type, then actually create -->
				    <#if !requestParameters.preContactMechTypeId?exists && !preContactMechTypeId?exists>
				    <h2>${uiLabelMap.PartyCreateNewContactInfo}</h2>
				    <form method="post" action='<@ofbizUrl>editcontactmechnosave</@ofbizUrl>' name="createcontactmechform" id="createcontactmechform">
				      <table width="90%" border="0" cellpadding="2" cellspacing="0">
				        <tr>
				          <td><label for="preContactMechTypeId">${uiLabelMap.PartySelectContactType}</label></td>
				          <td>
				            <select name="preContactMechTypeId" id="preContactMechTypeId" class='selectBox'>
				              <#list contactMechTypes as contactMechType>
				                <option value='${contactMechType.contactMechTypeId}'>${contactMechType.get("description",locale)}</option>
				              </#list>
				            </select>&nbsp;<a href="javascript:document.createcontactmechform.submit()"><p style="padding-left:10px;display: inline-block;">${uiLabelMap.CommonCreate}</p></a>
				          </td>
				        </tr>
				      </table>
				    </form>
				    <#-- <p><h3>ERROR: Contact information with ID "${contactMechId}" not found!</h3></p> -->
				    </#if>
				  </#if>

				  <#if contactMechTypeId?exists>
				    <#if !contactMech?exists>
				      <h2>${uiLabelMap.PartyCreateNewContactInfo}</h2>
				      <a href='<@ofbizUrl>${donePage}</@ofbizUrl>' class="button"><small>« </small>${uiLabelMap.CommonGoBack}</a>
				      <form method="post" action='<@ofbizUrl>${reqName}</@ofbizUrl>' name="editcontactmechform" id="editcontactmechform">
					<table width="90%" border="0">

				          <input type='hidden' name='contactMechTypeId' value='${contactMechTypeId}' />
				          <#if contactMechPurposeType?exists>
				            <div>(${uiLabelMap.PartyNewContactHavePurpose} "${contactMechPurposeType.get("description",locale)?if_exists}")</div>
				          </#if>
				          <#if cmNewPurposeTypeId?has_content><input type='hidden' name='contactMechPurposeTypeId' value='${cmNewPurposeTypeId}' /></#if>
				          <#if preContactMechTypeId?has_content><input type='hidden' name='preContactMechTypeId' value='${preContactMechTypeId}' /></#if>
				          <#if paymentMethodId?has_content><input type='hidden' name='paymentMethodId' value='${paymentMethodId}' /></#if>
				    <#else>
				      <h2>${uiLabelMap.PartyEditContactInfo}</h2>
				      <a href="<@ofbizUrl>${donePage}</@ofbizUrl>" class="button"><small>« </small>${uiLabelMap.CommonGoBack}</a>
				      <a href="javascript:document.editcontactmechform.submit()" class="button">${uiLabelMap.CommonSave}</a>
				      <table width="90%" border="0" cellpadding="2" cellspacing="0">
				        <tr>
				          <td align="right" valign="top">${uiLabelMap.PartyContactPurposes}</td>
				          <td>&nbsp;</td>
				          <td>
				            <table border="0" cellspacing="1">
				              <#list partyContactMechPurposes?if_exists as partyContactMechPurpose>
				                <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType", true) />
				                <tr>
				                  <td>
				                    <#if contactMechPurposeType?exists>
				                      ${contactMechPurposeType.get("description",locale)}
				                    <#else>
				                      ${uiLabelMap.PartyPurposeTypeNotFound}: "${partyContactMechPurpose.contactMechPurposeTypeId}"
				                    </#if>
				                     (${uiLabelMap.CommonSince}:${partyContactMechPurpose.fromDate.toString()})
				                    <#if partyContactMechPurpose.thruDate?exists>(${uiLabelMap.CommonExpires}:${partyContactMechPurpose.thruDate.toString()})</#if>
				                  </td>
				                  <td>
				                      <form name="deletePartyContactMechPurpose_${partyContactMechPurpose.contactMechPurposeTypeId}" method="post" action="<@ofbizUrl>deletePartyContactMechPurpose</@ofbizUrl>">
				                        <div>
				                          <input type="hidden" name="contactMechId" value="${contactMechId}"/>
				                          <input type="hidden" name="contactMechPurposeTypeId" value="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
				                          <input type="hidden" name="fromDate" value="${partyContactMechPurpose.fromDate}"/>
				                          <input type="hidden" name="useValues" value="true"/>
				                          <a href='javascript:document.deletePartyContactMechPurpose_${partyContactMechPurpose.contactMechPurposeTypeId}.submit()' class='button'>&nbsp;${uiLabelMap.CommonDelete}&nbsp;</a>
				                        </div>
				                      </form>
				                  </td>
				                </tr>
				              </#list>
				              <#if purposeTypes?has_content>
				              <tr>
				                <td>
				                  <form method="post" action='<@ofbizUrl>createPartyContactMechPurpose</@ofbizUrl>' name='newpurposeform'>
				                    <input type="hidden" name="contactMechId" value="${contactMechId}"/>
				                    <input type="hidden" name="useValues" value="true"/>
				                      <select name='contactMechPurposeTypeId' class='selectBox'>
				                        <option></option>
				                        <#list purposeTypes as contactMechPurposeType>
				                          <option value='${contactMechPurposeType.contactMechPurposeTypeId}'>${contactMechPurposeType.get("description",locale)}</option>
				                        </#list>
				                      </select>
				                  </form>
				                </td>
				                <td><span style="padding-left:5px;"></span><a href='javascript:document.newpurposeform.submit()' class='button'>${uiLabelMap.PartyAddPurpose}</a></td>
				              </tr>
				              </#if>
				            </table>
				          </td>
				        </tr>
				        <form method="post" action='<@ofbizUrl>${reqName}</@ofbizUrl>' name="editcontactmechform" id="editcontactmechform">
				          <div>
				          <input type="hidden" name="contactMechId" value='${contactMechId}' />
				          <input type="hidden" name="contactMechTypeId" value='${contactMechTypeId}' />
				    </#if>

				    <#if contactMechTypeId = "POSTAL_ADDRESS">
				      <tr>
				        <td align="right" valign="top"><label for="toName">${uiLabelMap.PartyToName}</label></td>
				        <td>&nbsp;</td>
				        <td>
				          <input type="text" class='input-text' size="30" maxlength="60" name="toName" id="toName" value="${postalAddressData.toName?if_exists}" />
				        </td>
				      </tr>
				      <tr>
				        <td align="right" valign="top"><label for="attnName">${uiLabelMap.PartyAttentionName}</label></td>
				        <td>&nbsp;</td>
				        <td>
				          <input type="text" class='input-text' size="30" maxlength="60" name="attnName" value="${postalAddressData.attnName?if_exists}" />
				        </td>
				      </tr>
				      <tr>
				        <td align="right" valign="top"><label class="required" for="address1">${uiLabelMap.PartyAddressLine1}<em>*</em></label></td>
				        <td>&nbsp;</td>
				        <td>
						<div class="input-box">
							<input type="text" id="address1" class='input-text required-entry' size="30" maxlength="30" name="address1" value="${postalAddressData.address1?if_exists}" />
						</div>
				        </td>
				      </tr>
				      <tr>
				        <td align="right" valign="top"><label for="address2">${uiLabelMap.PartyAddressLine2}</label></td>
				        <td>&nbsp;</td>
				        <td>
				            <input type="text" class='input-text' size="30" maxlength="30" name="address2" id="address2" value="${postalAddressData.address2?if_exists}" />
				        </td>
				      </tr>
				      <tr>
				        <td align="right" valign="top"><label class="required" for="city">${uiLabelMap.PartyCity}<em>*</em></label></td>
				        <td>&nbsp;</td>
				        <td>
						<div class="input-box">
						<input type="text" class='input-text required-entry' size="30" maxlength="30" id="city" name="city" value="${postalAddressData.city?if_exists}" />
						</div>
				        </td>
				      </tr>
				      <tr>
				        <td align="right" valign="top"> <label for="editcontactmechform_stateProvinceGeoId">${uiLabelMap.PartyState}</label></td>
				        <td>&nbsp;</td>
				        <td>
				          <select name="stateProvinceGeoId" id="editcontactmechform_stateProvinceGeoId">
				          </select>
				        </td>
				      </tr>
				      <tr>
				        <td align="right" valign="top"><label class="required" for="postalCode">${uiLabelMap.PartyZipCode}<em>*</em></label></td>
				        <td >&nbsp;</td>
				        <td>
				          <input type="text" class='input-text required-entry' size="12" maxlength="10" id="postalCode" name="postalCode" value="${postalAddressData.postalCode?if_exists}" />
				        </td>
				      </tr>
				      <tr>
				        <td align="right" valign="top"><label for="editcontactmechform_countryGeoId">${uiLabelMap.CommonCountry}</label></td>
				        <td>&nbsp;</td>
				        <td>
				          <select name="countryGeoId" id="editcontactmechform_countryGeoId">
				          ${screens.render("component://common/widget/CommonScreens.xml#countries")}
				          <#if (postalAddress?exists) && (postalAddress.countryGeoId?exists)>
				            <#assign defaultCountryGeoId = postalAddress.countryGeoId>
				          <#else>
				            <#assign defaultCountryGeoId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "country.geo.id.default")>
				          </#if>
				          <option selected="selected" value="${defaultCountryGeoId}">
				          <#assign countryGeo = delegator.findOne("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",defaultCountryGeoId), false)>
				            ${countryGeo.get("geoName",locale)}
				          </option>
				          </select>
				        </td>
				      </tr>
				    <#elseif contactMechTypeId = "TELECOM_NUMBER">
				      <tr>
				        <td align="right" valign="top"><label for="editcontactmechform_countryGeoId">${uiLabelMap.PartyPhoneNumber}</label></td>
				        <td>&nbsp;</td>
				        <td>
				          <input type="text" style="width:60px;" class="input-text" size="4" maxlength="10" name="countryCode" value="${telecomNumberData.countryCode?if_exists}" />
				          -&nbsp;<input type="text" style="width:60px;" class='input-text' size="4" maxlength="10" name="areaCode" value="${telecomNumberData.areaCode?if_exists}" />
				          -&nbsp;<input type="text" style="width:60px;" class='input-text' size="15" maxlength="15" name="contactNumber" value="${telecomNumberData.contactNumber?if_exists}" />
				          &nbsp;${uiLabelMap.PartyExtension}&nbsp;<input type="text" style="width:40px;" class='input-text' size="6" maxlength="10" name="extension" value="${partyContactMechData.extension?if_exists}" />
				        </td>
				      </tr>
				      <tr>
				        <td align="right" valign="top"></td>
				        <td>&nbsp;</td>
				        <td>[${uiLabelMap.CommonCountryCode}] [${uiLabelMap.PartyAreaCode}] [${uiLabelMap.PartyContactNumber}] [${uiLabelMap.PartyExtension}]</td>
				      </tr>
				    <#elseif contactMechTypeId = "EMAIL_ADDRESS">
				      <tr>
				        <td align="right" valign="top"><label class="required" for="editcontactmechform_countryGeoId"><em>*</em>${uiLabelMap.PartyEmailAddress}</label></td>
				        <td>&nbsp;</td>
				        <td>
				          <input type="text" class='input-text required-entry' size="60" maxlength="255" name="emailAddress" value="<#if tryEntity>${contactMech.infoString?if_exists}<#else>${requestParameters.emailAddress?if_exists}</#if>" />
				        </td>
				      </tr>
				    <#else>
				      <tr>
				        <td align="right" valign="top"><label for="infoString" class="required">${contactMechType.get("description",locale)?if_exists}<em>*</em></label></td>
				        <td>&nbsp;</td>
				        <td>
				            <input type="text" class='input-text required-entry' size="60" maxlength="255" name="infoString" id="infoString" value="${contactMechData.infoString?if_exists}" />
				        </td>
				      </tr>
				    </#if>
				      <tr>
				        <td align="right" valign="top"><label for="allowSolicitation">${uiLabelMap.PartyAllowSolicitation}?</label></td>
				        <td>&nbsp;</td>
				        <td>
				          <select name="allowSolicitation" id="allowSolicitation" class='selectBox'>
				            <#if (((partyContactMechData.allowSolicitation)!"") == "Y")><option value="Y">${uiLabelMap.CommonY}</option></#if>
				            <#if (((partyContactMechData.allowSolicitation)!"") == "N")><option value="N">${uiLabelMap.CommonN}</option></#if>
				            <option></option>
				            <option value="Y">${uiLabelMap.CommonY}</option>
				            <option value="N">${uiLabelMap.CommonN}</option>
				          </select>
				        </td>
				      </tr>
				      </table>
				      <div class="buttons-set">
				        <p class="required">* Required Fields</p>
				        <p class="back-link"><a href="<@ofbizUrl>${donePage}</@ofbizUrl>"><small>« </small>${uiLabelMap.CommonGoBack}</a></p>
				        <button type="submit" onclick="submithidden()" title="${uiLabelMap.CommonSave}" class="button">
							<span><span>${uiLabelMap.CommonSave}</span></span>
						</button>
				      </div>
				    </form>
				    <script type="text/javascript">
					    //<![CDATA[
					        var dataFormTmp = new VarienForm('editcontactmechform', true);
					    //]]>
					</script>
				  <#else>
				    <a href="<@ofbizUrl>${donePage}</@ofbizUrl>"><p style="padding-top:10px;padding-left:5px;">${uiLabelMap.CommonGoBack}</p></a>
				  </#if>
				</#if>
			</div>
		</div>
	</div>
</div>

<style type="text/css">
	.errorMessage{
		color:red;
		margin-left:40px !important;
	}
</style>