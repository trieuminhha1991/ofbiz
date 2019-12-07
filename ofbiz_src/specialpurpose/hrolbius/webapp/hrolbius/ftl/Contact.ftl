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

  <div id="partyContactInfo" class="widget-box transparent no-bottom-border">
  	<div class="widget-box collapsed">
  <div class="widget-header widget-header-small header-color-blue2">
   <h6>${uiLabelMap.PartyContactInformation}</h6>
   <div class="widget-toolbar">
   		<a href="#" data-action="collapse">
   			<i class="icon-chevron-up"></i>
   		</a>
   </div>
  </div>
    <div class="widget-body">
	  <#if security.hasEntityPermission("PARTYMGR", "_CREATE", session) || userLogin.partyId == partyId>
        <span style="float:right;">
          <a style="float:right;margin-top: 10px; margin-bottom: 10px; margin-right: 5px; text-decoration: none; font-size: 14px;" class="open-sans icon-plus-sign" href="<@ofbizUrl>editcontactmech?partyId=${partyId}</@ofbizUrl>">&nbsp${uiLabelMap.CommonCreateNew}</a>
          </span>
        </#if>
      <#if contactMeches?has_content>
        <table class="table table-hover table-striped table-bordered dataTable" cellspacing="0">
          <tr>
            <th style="-moz-border-radius:0px;border-radius:0px;-webkit-border-radius:0px;">${uiLabelMap.PartyContactType}</th>
            <th>${uiLabelMap.PartyContactInformation}</th>
            <th>${uiLabelMap.HrolbiusContactPublic}</th>
            <th>&nbsp;</th>
          </tr>
          <#list contactMeches as contactMechMap>
            <#assign contactMech = contactMechMap.contactMech>
            <#assign partyContactMech = contactMechMap.partyContactMech>
            <tr>
              <td class="align-top" style="width:105;">${contactMechMap.contactMechType.get("description",locale)}</td>
              <td>
                <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
                  <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType", true)>
                  <div>
                    <#if contactMechPurposeType?has_content>
                      <b>${contactMechPurposeType.get("description",locale)}</b>
                    <#else>
                      <b>${uiLabelMap.PartyMechPurposeTypeNotFound}: "${partyContactMechPurpose.contactMechPurposeTypeId}"</b>
                    </#if>
                    <#if partyContactMechPurpose.thruDate?has_content>
                      (${uiLabelMap.CommonExpire}: ${partyContactMechPurpose.thruDate})
                    </#if>
                  </div>
                </#list>
                <#if "POSTAL_ADDRESS" = contactMech.contactMechTypeId>
                  <#if contactMechMap.postalAddress?has_content>
                    <#assign postalAddress = contactMechMap.postalAddress>
                    ${setContextField("postalAddress", postalAddress)}
                    ${screens.render("component://party/widget/partymgr/PartyScreens.xml#postalAddressHtmlFormatter")}
                    <#if postalAddress.geoPointId?has_content>
                      <#if contactMechPurposeType?has_content>
                        <#assign popUptitle = contactMechPurposeType.get("description", locale) + uiLabelMap.CommonGeoLocation>
                      </#if>
                      <a class="btn btn-mini btn-primary" href="javascript:popUp('<@ofbizUrl>PartyGeoLocation?geoPointId=${postalAddress.geoPointId}&partyId=${partyId}</@ofbizUrl>', '${popUptitle?if_exists}', '450', '550')">${uiLabelMap.CommonGeoLocation}</a>
                    </#if>
                  </#if>
                <#elseif "TELECOM_NUMBER" = contactMech.contactMechTypeId>
                  <#if contactMechMap.telecomNumber?has_content>
                    <#assign telecomNumber = contactMechMap.telecomNumber>
                    <div>
                      ${telecomNumber.countryCode?if_exists}
                      <#if telecomNumber.areaCode?has_content>${telecomNumber.areaCode?default("000")}-</#if><#if telecomNumber.contactNumber?has_content>${telecomNumber.contactNumber?default("000-0000")}</#if>
                      <#if partyContactMech.extension?has_content>${uiLabelMap.PartyContactExt}&nbsp;${partyContactMech.extension}</#if>
                        <#if !telecomNumber.countryCode?has_content || telecomNumber.countryCode = "011">
                          <a class="btn btn-mini btn-primary" target="_blank" href="${uiLabelMap.CommonLookupAnywhoLink}" >${uiLabelMap.CommonLookupAnywho}</a>
                          <a class="btn btn-mini btn-primary" target="_blank" href="${uiLabelMap.CommonLookupWhitepagesTelNumberLink}" >${uiLabelMap.CommonLookupWhitepages}</a>
                        </#if>
                    </div>
                  </#if>
                <#elseif "EMAIL_ADDRESS" = contactMech.contactMechTypeId>
                  <div>
                    ${contactMech.infoString?if_exists}
                    <form method="post" action="<@ofbizUrl>NewDraftCommunicationEvent</@ofbizUrl>" onsubmit="javascript:submitFormDisableSubmits(this)" name="createEmail${contactMech.infoString?replace("&#64;","")?replace(".","")}">
                      <#if userLogin.partyId?has_content>
                      <input name="partyIdFrom" value="${userLogin.partyId}" type="hidden"/>
                      </#if>
                      <input name="partyIdTo" value="${partyId}" type="hidden"/>
                      <input name="contactMechIdTo" value="${contactMech.contactMechId}" type="hidden"/>
                      <input name="my" value="My" type="hidden"/>
                      <input name="statusId" value="COM_PENDING" type="hidden"/>
                      <input name="communicationEventTypeId" value="EMAIL_COMMUNICATION" type="hidden"/>
                    </form><a class="btn btn-mini btn-primary" href="javascript:document.createEmail${contactMech.infoString?replace("&#64;","")?replace(".","")}.submit()"><i class="icon-envelope"></i>${uiLabelMap.CommonSendEmail}</a>
                  </div>
                <#elseif "WEB_ADDRESS" = contactMech.contactMechTypeId>
                  <div>
                    ${contactMech.infoString?if_exists}
                    <#assign openAddress = contactMech.infoString?default("")>
                    <#if !openAddress?starts_with("http") && !openAddress?starts_with("HTTP")><#assign openAddress = "http://" + openAddress></#if>
                    <a target="_blank" href="${openAddress}" class="btn btn-mini btn-primary">${uiLabelMap.CommonOpenPageNewWindow}</a>
                  </div>
                <#else>
                  <div>${contactMech.infoString?if_exists}</div>
                </#if>
                <div>(${uiLabelMap.CommonUpdated}:&nbsp;${partyContactMech.fromDate})</div>
                <#if partyContactMech.thruDate?has_content><div><b>${uiLabelMap.PartyContactEffectiveThru}:&nbsp;${partyContactMech.thruDate}</b></div></#if>
                <#-- create cust request -->
                <#if custRequestTypes?exists>
                  <form name="createCustRequestForm" action="<@ofbizUrl>createCustRequest</@ofbizUrl>" method="post" onsubmit="javascript:submitFormDisableSubmits(this)">
                    <input type="hidden" name="partyId" value="${partyId}"/>
                    <input type="hidden" name="fromPartyId" value="${partyId}"/>
                    <input type="hidden" name="fulfillContactMechId" value="${contactMech.contactMechId}"/>
                    <select name="custRequestTypeId">
                      <#list custRequestTypes as type>
                        <option value="${type.custRequestTypeId}">${type.get("description", locale)}</option>
                      </#list>
                    </select>
                    <button type="submit" class="btn btn-warning btn-small">
                    	<i class="icon-ok"></i>
                    	${uiLabelMap.PartyCreateNewCustRequest}
                    </button>
                  </form>
                </#if>
              </td>
              <td valign="top" style="width:10%;"><b>(${partyContactMech.allowSolicitation?if_exists})</b></td>
              <td class="button-col">
                <#if security.hasEntityPermission("PARTYMGR", "_UPDATE", session) || userLogin.partyId == partyId>
                  <a title="${uiLabelMap.CommonUpdate}" style="margin-left: 0px;float:left;margin-top:5px;" class="btn btn-mini btn-primary" href="<@ofbizUrl>editcontactmech?partyId=${partyId}&amp;contactMechId=${contactMech.contactMechId}</@ofbizUrl>"><i class="icon-edit"></i></a>
                </#if>
                <#if security.hasEntityPermission("PARTYMGR", "_DELETE", session) || userLogin.partyId == partyId>
                  <form style="float:left;" name="partyDeleteContact" method="post" action="<@ofbizUrl>deleteContactMech</@ofbizUrl>" onsubmit="javascript:submitFormDisableSubmits(this)">
                    <input name="partyId" value="${partyId}" type="hidden"/>
                    <input name="contactMechId" value="${contactMech.contactMechId}" type="hidden"/>
                    <button type="submit" class="btn btn-warning btn-mini margin-top5" title="${uiLabelMap.CommonExpire}" style="margin-left:1px;">
                    <i class="icon-trash"></i>
                    </button>
                  </form>
                </#if>
              </td>
            </tr>
          </#list>
        </table>
      <#else>
	 	<div style="margin-bottom:10px; margin-top:5px;" class="padding-top8">
        <span class="alert alert-warning font-size13"> ${uiLabelMap.PartyNoContactInformation}</span>
		</div>
      </#if>
    </div>
  </div>
  </div>