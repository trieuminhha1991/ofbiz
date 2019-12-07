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
	<#if facility?exists && facilityId?has_content>
		<h3>${uiLabelMap.PageTitleEditFacilityContent}</h3>
	<#else>
		<h3>${uiLabelMap.ProductNewFacility}</h3>
	</#if>
	<span class="widget-toolbar none-content">
		${screens.render("component://product/widget/facility/FacilityScreens.xml#EditFacilitySubTabBar")}
	</span>
</div>
<div class="widget-body" style="margin-top:15px !important;">
<#if facility?exists && facilityId?has_content>
  <form action="<@ofbizUrl>UpdateFacility</@ofbizUrl>" name="EditFacilityForm" method="post" class="basic-form">
  <input type="hidden" name="facilityId" value="${facilityId?if_exists}" />
  <table class="basic-table" cellspacing='0'>
  <tr>
    <td class="float-right">${uiLabelMap.ProductFacilityId}</td>
    <td style="width:5%"></td>
    <td>
      ${facilityId?if_exists} <span class="tooltip">${uiLabelMap.ProductNotModificationRecrationFacility}</span>
    </td>
  </tr>
<#else>
  <form action="<@ofbizUrl>CreateFacility</@ofbizUrl>" name="EditFacilityForm" method="post" class="basic-form">
  <#if facilityId?exists>
    <h3>${uiLabelMap.ProductCouldNotFindFacilityWithId} "${facilityId?if_exists}".</h3>
  </#if>
  
  <table class="basic-table" cellspacing='0'>
</#if>

  <tr>
    <td class="float-right">${uiLabelMap.ProductFacilityTypeId}</td>
    <td style="width:5%"></td>
    <td>
      <select name="facilityTypeId">
        <option selected="selected" value='${facilityType.facilityTypeId?if_exists}'>${facilityType.get("description",locale)?if_exists}</option>
        <option value='${facilityType.facilityTypeId?if_exists}'>----</option>
        <#list facilityTypes as nextFacilityType>
          <option value='${nextFacilityType.facilityTypeId?if_exists}'>${nextFacilityType.get("description",locale)?if_exists}</option>
        </#list>
      </select>
    </td>
  </tr>
  <tr>
    <td class="float-right">${uiLabelMap.FormFieldTitle_parentFacilityId}</td>
    <td style="width:5%"></td>
    <td>
      <@htmlTemplate.lookupField value="${facility.parentFacilityId?if_exists}" formName="EditFacilityForm" name="parentFacilityId" id="parentFacilityId" fieldFormName="LookupFacility"/>
    </td>
  </tr>
  <tr>
    <td class="float-right">${uiLabelMap.ProductFacilityOwner}</td>
    <td style="width:5%"></td>
    <td>
      <@htmlTemplate.lookupField value="${facility.ownerPartyId?if_exists}" formName="EditFacilityForm" name="ownerPartyId" id="ownerPartyId" fieldFormName="LookupPartyName"/>
      <span class="tooltip">${uiLabelMap.CommonRequired}</span>
    </td>
  </tr>
  <tr>
    <td class="float-right">${uiLabelMap.ProductFacilityDefaultWeightUnit}</td>
    <td style="width:5%"></td>
    <td>
      <select name="defaultWeightUomId">
          <option value=''>${uiLabelMap.CommonNone}</option>
          <#list weightUomList as uom>
            <option value='${uom.uomId}'
               <#if (facility.defaultWeightUomId?has_content) && (uom.uomId == facility.defaultWeightUomId)>
               selected="selected"
               </#if>
             >${uom.get("description",locale)?default(uom.uomId)}</option>
          </#list>
      </select>
    </td>
  </tr>
  <tr>
    <td class="float-right">${uiLabelMap.ProductFacilityDefaultInventoryItemType}</td>
    <td style="width:5%"></td>
    <td>
      <select name="defaultInventoryItemTypeId">
          <#list inventoryItemTypes as nextInventoryItemType>
            <option value='${nextInventoryItemType.inventoryItemTypeId}'
               <#if (facility.defaultInventoryItemTypeId?has_content) && (nextInventoryItemType.inventoryItemTypeId == facility.defaultInventoryItemTypeId)>
               selected="selected"
               </#if>
             >${nextInventoryItemType.get("description",locale)?default(nextInventoryItemType.inventoryItemTypeId)}</option>
          </#list>
      </select>
    </td>
  </tr>
  <tr>
    <td class="float-right">${uiLabelMap.FacilityName}</td>
    <td style="width:5%"></td>
    <td>
      <input type="text" name="facilityName" value="${facility.facilityName?if_exists}" size="30" maxlength="60" />
      <span class="tooltip">${uiLabelMap.CommonRequired}</span>
    </td>
  </tr>
<!--  <tr>
    <td class="float-right">${uiLabelMap.ProductFacilitySize}</td>
    <td style="width:5%"></td>
    <td><input type="text" name="facilitySize" value="${facility.facilitySize?if_exists}" size="10" maxlength="20" /></td>
  </tr>
  -->
  <!-- <tr>
   <td class="float-right">${uiLabelMap.ProductFacilityDefaultAreaUnit}</td>
   <td style="width:5%"></td>
    <td>
      <select name="facilitySizeUomId">
          <option value=''>${uiLabelMap.CommonNone}</option>
          <#list areaUomList as uom>
            <option value='${uom.uomId}'
               <#if (facility.facilitySizeUomId?has_content) && (uom.uomId == facility.facilitySizeUomId)>
               selected="selected"
               </#if>
             >${uom.get("description",locale)?default(uom.uomId)}</option>
          </#list>
      </select>
    </td>
  </tr>  
  -->
  <tr>
    <td class="float-right">${uiLabelMap.CommonDescription}</td>
    <td style="width:5%"></td>
    <td ><input type="text" name="description" value="${facility.description?if_exists}" size="60" maxlength="250" /></td>
  </tr>
<!-- <tr>
    <td class="float-right">${uiLabelMap.ProductDefaultDaysToShip}</td>
    <td style="width:5%"></td>
    <td><input type="text" name="defaultDaysToShip" value="${facility.defaultDaysToShip?if_exists}" size="10" maxlength="20" /></td>
  </tr>
  -->
  <tr>
    <td>&nbsp;</td>
    <td style="width:5%"></td>
    <#if facilityId?has_content>
      <td>
      	<button class="btn btn-small btn-primary" type="submit" name="Update">
      		<i class="icon-ok"></i>    
      		${uiLabelMap.CommonUpdate}
      	</button>
      </td>
    <#else>    
      <td>
      <button class="btn btn-small btn-primary" type="submit" name="Update">
      		<i class="icon-save"></i>    
      		${uiLabelMap.CommonSave}
      	</button>
      </td>
    </#if>
  </tr>
</table>

</form>
</div>
</div>