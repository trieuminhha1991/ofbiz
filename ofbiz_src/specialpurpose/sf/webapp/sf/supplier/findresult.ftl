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
  <div class="widget-header widget-header-small header-color-blue2">
	<h6 style="font-size:20px">${uiLabelMap.CommonSearchResults}</h6>

	</div>
	<div class="widget-body">
	<div class="widget-body-inner">
	<div class="widget-main">
  <#if partyList?has_content>
    <#-- Pagination -->
    <#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
    <#assign commonUrl = "findsupplier?hideFields=" + hideFields + paramList + "&sortField=" + sortField?if_exists + "&"/>
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
        <td>Facebook Id</td>
        <td>
            <a  href="<@ofbizUrl>findsupplier</@ofbizUrl>?<#if sortField?has_content><#if sortField == "createdDate">sortField=-createdDate<#elseif sortField == "-createdDate">sortField=createdDate<#else>sortField=createdDate</#if><#else>sortField=createdDate</#if>${paramList?if_exists}&VIEW_SIZE=${viewSize?if_exists}&VIEW_INDEX=${viewIndex?if_exists}"
                <#if sortField?has_content><#if sortField == "createdDate">class="sort-order-desc"<#elseif sortField == "-createdDate">class="sort-order-asc"<#else>class="sort-order"</#if><#else>class="sort-order"</#if>>${uiLabelMap.FormFieldTitle_createdDate}
            </a>
        </td>
      </tr>
    <#assign alt_row = false>
    <#assign rowCount = 0>
    <#list partyList as partyRow>
      <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
        <td><a  href="<@ofbizUrl>editSupplier?partyId=${partyRow.partyId}</@ofbizUrl>">${partyRow.partyId}</a></td>
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
		${partyRow.firstName}&nbsp;${partyRow.lastName}
        </td>
        <td>
		${partyRow.facebookId}
        </td>
        <#assign partyDate = delegator.findOne("Party", {"partyId":partyRow.partyId}, true)/>
        <td>${partyDate.createdDate?if_exists}</td>
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