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
<#assign extInfo = parameters.extInfo?default("N")>
<#assign inventoryItemId = parameters.inventoryItemId?default("")>
<#assign serialNumber = parameters.serialNumber?default("")>
<#assign softIdentifier = parameters.softIdentifier?default("")>
<#assign sortField = parameters.sortField?if_exists/>
<#-- Only allow the search fields to be hidden when we have some results -->
<#if partyList?has_content>
  <#assign hideFields = parameters.hideFields?default("N")>
<#else>
  <#assign hideFields = "N">
</#if>
<#if (parameters.firstName?has_content || parameters.lastName?has_content)>
  <#assign createUrl = "editperson?create_new=Y&amp;lastName=${parameters.lastName?if_exists}&amp;firstName=${parameters.firstName?if_exists}"/>
<#elseif (parameters.groupName?has_content)>
  <#assign createUrl = "editpartygroup?create_new=Y&amp;groupName=${parameters.groupName?if_exists}"/>
<#else>
  <#assign createUrl = "createnew"/>
</#if>

<#if partyList?exists>
  <#if hideFields != "Y">
  </#if>
  <div class="widget-box transparent no-border-bottom">
  	<div class="widget-body">
  	<div class="widget-body-inner">
  	<div class="widget-main">
  <#if partyList?has_content>
    <#-- Pagination -->
    <#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
    <#assign commonUrl = "findparty?hideFields=" + hideFields + paramList + "&sortField=" + sortField?if_exists + "&"/>
    <#assign viewIndexFirst = 0/>
    <#assign viewIndexPrevious = viewIndex - 1/>
    <#assign viewIndexNext = viewIndex + 1/>
    <#assign viewIndexLast = Static["java.lang.Math"].floor(partyListSize/viewSize)/>
    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount", lowIndex, "highCount", highIndex, "total", partyListSize)/>
    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
    <@nextPrev commonUrl=commonUrl ajaxEnabled=false javaScriptEnabled=false paginateStyle="dataTables_paginate paging_bootstrap pagination" paginateFirstStyle="nav-first" viewIndex=viewIndex highIndex=highIndex listSize=partyListSize viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel="" paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl="" ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel="" paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
    <table class="table table-hover table-striped table-bordered dataTable" cellspacing="0">
      <tr class="header-row-2">
        <td>${uiLabelMap.PartyPartyId}</td>
        <td>${uiLabelMap.PartyUserLogin}</td>
        <td>${uiLabelMap.PartyName}</td>
    <#if extInfo?default("") == "P" >
        <td>${uiLabelMap.PartyCity}</td>
    </#if>
    <#if extInfo?default("") == "P">
        <td>${uiLabelMap.PartyPostalCode}</td>
    </#if>
    <#if extInfo?default("") == "T">
        <td>${uiLabelMap.PartyAreaCode}</td>
    </#if>
    <#if inventoryItemId?default("") != "">
        <td>${uiLabelMap.ProductInventoryItemId}</td>
    </#if>
    <#if serialNumber?default("") != "">
        <td>${uiLabelMap.ProductSerialNumber}</td>
    </#if>
    <#if softIdentifier?default("") != "">
        <td>${uiLabelMap.ProductSoftIdentifier}</td>
    </#if>
        <td>${uiLabelMap.PartyRelatedCompany}</td>
        <td>${uiLabelMap.PartyType}</td>
        <td>${uiLabelMap.PartyMainRole}</td>
        <td>
            <a  href="<@ofbizUrl>findparty</@ofbizUrl>?<#if sortField?has_content><#if sortField == "createdDate">sortField=-createdDate<#elseif sortField == "-createdDate">sortField=createdDate<#else>sortField=createdDate</#if><#else>sortField=createdDate</#if>${paramList?if_exists}&VIEW_SIZE=${viewSize?if_exists}&VIEW_INDEX=${viewIndex?if_exists}" 
                <#if sortField?has_content><#if sortField == "createdDate">class="sort-order-desc"<#elseif sortField == "-createdDate">class="sort-order-asc"<#else>class="sort-order"</#if><#else>class="sort-order"</#if>>${uiLabelMap.FormFieldTitle_createdDate}
            </a>
        </td>
        <td>
            <a href="<@ofbizUrl>findparty</@ofbizUrl>?<#if sortField?has_content><#if sortField == "lastModifiedDate">sortField=-lastModifiedDate<#elseif sortField == "-lastModifiedDate">sortField=lastModifiedDate<#else>sortField=lastModifiedDate</#if><#else>sortField=lastModifiedDate</#if>${paramList?if_exists}&VIEW_SIZE=${viewSize?if_exists}&VIEW_INDEX=${viewIndex?if_exists}" 
                <#if sortField?has_content><#if sortField == "lastModifiedDate">class="sort-order-desc"<#elseif sortField == "-lastModifiedDate">class="sort-order-asc"<#else>class="sort-order"</#if><#else>class="sort-order"</#if>>${uiLabelMap.FormFieldTitle_lastModifiedDate}
            </a>
        </td>
        <td>&nbsp;</td>
      </tr>
    <#assign alt_row = false>
    <#assign rowCount = 0>
    <#list partyList as partyRow>
      <#assign partyType = partyRow.getRelatedOne("PartyType", false)?if_exists>
      <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
        <td><a  href="<@ofbizUrl>viewprofile?partyId=${partyRow.partyId}</@ofbizUrl>">${partyRow.partyId}</a></td>
        <td>
      <#if partyRow.containsKey("userLoginId")>
          ${partyRow.userLoginId?default("N/A")}
      <#else>
        <#assign userLogins = partyRow.getRelated("UserLogin", null, null, false)>
        <#if (userLogins.size() > 0)>
          <#if (userLogins.size() > 1)>
          (${uiLabelMap.CommonMany})
          <#else>
            <#assign userLogin = userLogins.get(0)>
          ${userLogin.userLoginId}
          </#if>
        <#else>
          (${uiLabelMap.CommonNone})
        </#if>
      </#if>
        </td>
        <td>
      <#if partyRow.getModelEntity().isField("lastName") && lastName?has_content>
          ${partyRow.lastName}<#if partyRow.firstName?has_content>, ${partyRow.firstName}</#if>
      <#elseif partyRow.getModelEntity().isField("groupName") && partyRow.groupName?has_content>
          ${partyRow.groupName}
      <#else>
        <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(partyRow, true)>
        <#if partyName?has_content>
          ${partyName}
        <#else>
          (${uiLabelMap.PartyNoNameFound})
        </#if>
      </#if>
        </td>
      <#if extInfo?default("") == "T">
        <td>${partyRow.areaCode?if_exists}</td>
      </#if>
      <#if extInfo?default("") == "P" >
        <td>${partyRow.city?if_exists}, ${partyRow.stateProvinceGeoId?if_exists}</td>
      </#if>
      <#if extInfo?default("") == "P">
        <td>${partyRow.postalCode?if_exists}</td>
      </#if>
      <#if inventoryItemId?default("") != "">
        <td>${partyRow.inventoryItemId?if_exists}</td>
      </#if>
      <#if serialNumber?default("") != "">
        <td>${partyRow.serialNumber?if_exists}</td>
      </#if>
      <#if softIdentifier?default("") != "">
        <td>${partyRow.softIdentifier?if_exists}</td>
      </#if>
      <#if partyType?exists>
        <td>
        <#if partyType.partyTypeId?has_content && partyType.partyTypeId=="PERSON">
          <#assign partyRelateCom = delegator.findByAnd("PartyRelationship", {"partyIdTo", partyRow.partyId,"roleTypeIdFrom","ACCOUNT","roleTypeIdTo","CONTACT"}, null, false)>
          <#if partyRelateCom?has_content>
            <#list partyRelateCom as partyRelationship>
              <#if partyRelationship.partyIdFrom?has_content>
                <#assign companyName=Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyRelationship.partyIdFrom, true)>
          ${companyName?if_exists}
              </#if>
            </#list>
          </#if>
        </#if>
        </td>
        <td><#if partyType.description?exists>${partyType.get("description", locale)}<#else>???</#if></td>
      <#else>
        <td></td><td></td>
      </#if>
        <td>
      <#assign mainRole = dispatcher.runSync("getPartyMainRole", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyRow.partyId, "userLogin", userLogin))/>
              ${mainRole.description?if_exists}
        </td>
        <#assign partyDate = delegator.findOne("Party", {"partyId":partyRow.partyId}, true)/>
        <td>${partyDate.createdDate?if_exists}</td>
        <td>${partyDate.lastModifiedDate?if_exists}</td>
        <td class="button-col align-float">
          <a  href="<@ofbizUrl>viewprofile?partyId=${partyRow.partyId}</@ofbizUrl>">${uiLabelMap.CommonDetails}</a>
      <#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>
          <form name= "searchorders_o_${rowCount}" method= "post" action= "/ordermgr/control/searchorders">
            <input type= "hidden" name= "lookupFlag" value= "Y" />
            <input type= "hidden" name= "hideFields" value= "Y" />
            <input type= "hidden" name= "partyId" value= "${partyRow.partyId}" />
            <input type= "hidden" name= "viewIndex" value= "1" />
            <input type= "hidden" name= "viewSize" value= "20" />
            <a  href="javascript:document.searchorders_o_${rowCount}.submit()">${uiLabelMap.OrderOrders}</a>
          </form>
          <a  href="/ordermgr/control/FindQuote?partyId=${partyRow.partyId + externalKeyParam}">${uiLabelMap.OrderOrderQuotes}</a>
      </#if>
      <#if security.hasEntityPermission("ORDERMGR", "_CREATE", session)>
          <a  href="/ordermgr/control/checkinits?partyId=${partyRow.partyId + externalKeyParam}">${uiLabelMap.OrderNewOrder}</a>
          <a  href="/ordermgr/control/EditQuote?partyId=${partyRow.partyId + externalKeyParam}">${uiLabelMap.OrderNewQuote}</a>
      </#if>
        </td>
      </tr>
      <#assign rowCount = rowCount + 1>
      <#-- toggle the row color -->
      <#assign alt_row = !alt_row>
    </#list>
    </table>
  <#else>
    <div id="findPartyResults_2" class="alert alert-info padding-top8">
      <span>${uiLabelMap.PartyNoPartiesFound}</span>
    </div>
  </#if>
  <#if lookupErrorMessage?exists>
    <h3>${lookupErrorMessage}</h3>
  </#if>
  </div>
  </div>
  </div>
  </div>
</#if>