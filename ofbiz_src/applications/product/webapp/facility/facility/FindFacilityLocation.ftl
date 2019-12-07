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
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
	<h4>${uiLabelMap.PageTitleFindFacilityLocation}</h4>
   <span class="widget-toolbar none-content">
      <a class="icon-plus-sign open-sans" href="<@ofbizUrl>EditFacility</@ofbizUrl>">${uiLabelMap.ProductNewFacility}</a>
      <a href="<@ofbizUrl>EditFacilityLocation?facilityId=${facilityId?if_exists}</@ofbizUrl>" class="icon-plus-sign open-sans">${uiLabelMap.ProductNewFacilityLocation}</a>
    </span>
    </div>

    <form action="<@ofbizUrl>FindFacilityLocation</@ofbizUrl>" method="get" name="findFacilityLocation" class="basic-form">
        <#if (facilityId?exists)>
            <input type="hidden" name="facilityId" value="${facilityId}" />
        </#if>        
        <table class="basic-table margin-left16" cellspacing="0">
        <#if !(facilityId?exists)>
            <tr>
                <td >${uiLabelMap.ProductFacility}</td>
                <td><input type="text" value="" size="19" maxlength="20" /></td>
            </tr>
        </#if>
        <tr>
            <td >${uiLabelMap.ProductLocationSeqId}</td>
            <td>
                <#if parameters.facilityId?exists>
                    <#assign LookupFacilityLocationView="LookupFacilityLocation?facilityId=${facilityId}">
                <#else>
                    <#assign LookupFacilityLocationView="LookupFacilityLocation">
                </#if>
                <@htmlTemplate.lookupField formName="findFacilityLocation" name="locationSeqId" id="locationSeqId" fieldFormName="${LookupFacilityLocationView}"/>
            </td>
        </tr>
        <tr>
            <td >${uiLabelMap.CommonArea}</td>
            <td><input type="text" name="areaId" value="" size="19" maxlength="20" /></td>
        </tr>
        <tr>
            <td >${uiLabelMap.ProductAisle}</td>
            <td><input type="text" name="aisleId" value="" size="19" maxlength="20" /></td>
        </tr>
        <tr>
            <td >${uiLabelMap.ProductSection}</td>
            <td><input type="text" name="sectionId" value="" size="19" maxlength="20" /></td>
        </tr>
        <tr>
            <td >${uiLabelMap.ProductLevel}</td>
            <td><input type="text" name="levelId" value="" size="19" maxlength="20" /></td>
        </tr>
        <tr>
            <td >${uiLabelMap.ProductPosition}</td>
            <td><input type="text" name="positionId" value="" size="19" maxlength="20" /></td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td><button class="btn btn-small btn-primary" type="submit" name="look_up">
            	<i class="icon-search"></i>
            	${uiLabelMap.CommonFind}
            </button>
            </td>
        </tr>
        </table>
    </form>

    <#if foundLocations?exists>
        <#-- TODO: Put this in a screenlet - make it look more like the party find screen -->
        <br />
        <h3>${uiLabelMap.CommonFound}:&nbsp;${foundLocations.size()}&nbsp;${uiLabelMap.ProductLocationsFor}&nbsp;<#if facility?exists>${(facility.facilityName)?if_exists}</#if> [ID:${facilityId?if_exists}]</h3>
        <br />
        <table class="basic-table hover-bar" cellspacing="0">
        <tr class="header-row-2">
            <td>${uiLabelMap.ProductFacility}</td>
            <td>${uiLabelMap.ProductLocationSeqId}</td>
            <td>${uiLabelMap.ProductType}</td>
            <td>${uiLabelMap.CommonArea}</td>
            <td>${uiLabelMap.ProductAisle}</td>
            <td>${uiLabelMap.ProductSection}</td>
            <td>${uiLabelMap.ProductLevel}</td>
            <td>${uiLabelMap.ProductPosition}</td>
            <td>&nbsp;</td>
        </tr>
        <#assign rowClass = "2">
        <#list foundLocations as location>
        <#assign locationTypeEnum = location.getRelatedOne("TypeEnumeration", true)?if_exists>
        <tr valign="middle"<#if rowClass == "1"> class="alternate-row"</#if>>
            <td><a class="btn btn-info btn-mini" href="<@ofbizUrl>EditFacility?facilityId=${(location.facilityId)?if_exists}</@ofbizUrl>">${(location.facilityId)?if_exists}</a></td>
            <td><a class="btn btn-info btn-mini" href="<@ofbizUrl>EditFacilityLocation?facilityId=${facilityId}&locationSeqId=${(location.locationSeqId)?if_exists}</@ofbizUrl>">${(location.locationSeqId)?if_exists}</a></td>
            <td>${(locationTypeEnum.get("description",locale))?default(location.locationTypeEnumId?if_exists)}</td>
            <td>${(location.areaId)?if_exists}</td>
            <td>${(location.aisleId)?if_exists}</td>
            <td>${(location.sectionId)?if_exists}</td>
            <td>${(location.levelId)?if_exists}</td>
            <td>${(location.positionId)?if_exists}</td>
            <td class="button-col">
              <a class="btn btn-info btn-mini" href="<@ofbizUrl>EditInventoryItem?facilityId=${(location.facilityId)?if_exists}&locationSeqId=${(location.locationSeqId)?if_exists}</@ofbizUrl>">${uiLabelMap.ProductNewInventoryItem}</a>
              <#if itemId?exists>
                <a class="btn btn-info btn-mini" href="<@ofbizUrl>UpdateInventoryItem?inventoryItemId=${itemId}&facilityId=${facilityId}&locationSeqId=${(location.locationSeqId)?if_exists}</@ofbizUrl>">${uiLabelMap.ProductSetItem} ${itemId}</a>
              </#if>
              <a class="btn btn-info btn-mini" href="<@ofbizUrl>EditFacilityLocation?facilityId=${(location.facilityId)?if_exists}&locationSeqId=${(location.locationSeqId)?if_exists}</@ofbizUrl>">${uiLabelMap.CommonEdit}</a>
            </td>
        </tr>
        <#-- toggle the row color -->
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
        </#list>
        </table>
    </#if>
</div>