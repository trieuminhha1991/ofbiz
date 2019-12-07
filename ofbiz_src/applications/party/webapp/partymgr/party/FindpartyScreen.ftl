
<#assign extInfo = parameters.extInfo?default("N")>
<#assign inventoryItemId = parameters.inventoryItemId?default("")>
<#assign serialNumber = parameters.serialNumber?default("")>
<#assign softIdentifier = parameters.softIdentifier?default("")>
<#assign sortField = parameters.sortField?if_exists/>
<#-- Only allow the search fields to be hidden when we have some results -->
<!--<#if partyList?has_content>
  <#assign hideFields = parameters.hideFields?default("N")>
<#else>
  <#assign hideFields = "N">
</#if>-->
<div class="olbius-extra">
  <div >	
  </div>
  	<div >
  	<div >
  	<div class="widget-main">
  <!-- 	<#if partyList?has_content>
  	<#if hideFields == "Y">
      	<a class="icon-chevron-down" href="<@ofbizUrl>findparty?hideFields=N&sortField=${sortField?if_exists}${paramList}</@ofbizUrl>" title="${uiLabelMap.CommonShowLookupFields}">&nbsp;</a>
  	<#else>
     	<a class="icon-chevron-up" href="<@ofbizUrl>findparty?hideFields=Y&sortField=${sortField?if_exists}${paramList}</@ofbizUrl>" title="${uiLabelMap.CommonHideFields}">&nbsp;</a>
  	</#if>
	</#if>-->
    <div id="findPartyParameters"> <!--<#if hideFields != "N"> style="display:none" </#if> -->
      <#-- NOTE: this form is setup to allow a search by partial partyId or userLoginId; to change it to go directly to
          the viewprofile page when these are entered add the follow attribute to the form element:

           onsubmit="javascript:lookupParty('<@ofbizUrl>viewprofile</@ofbizUrl>');"
       -->
      <form method="post" name="lookupparty" action="<@ofbizUrl>findparty</@ofbizUrl>" class="basic-form form-padding">
        <input type="hidden" name="lookupFlag" value="Y"/>
        <input type="hidden" name="hideFields" value="Y"/>
        <div class="row-fluid">
        <div class="span12">
        <div class="span6">
        <table class="basic-table" cellspacing="0">
          <tr>
            <td>${uiLabelMap.PartyContactInformation}</td>
            <td>
              <input type="radio" name="extInfo" style="opacity: 0.5; position: relative;" class="margin-top-nav-1"  value="N" onclick="javascript:refreshInfo();" <#if extInfo == "N">checked="checked"</#if>/>&nbsp;${uiLabelMap.CommonNone}&nbsp;
              <input type="radio" name="extInfo" style="opacity: 0.5; position: relative;" class="margin-top-nav-1" value="P" onclick="javascript:refreshInfo();" <#if extInfo == "P">checked="checked"</#if>/>&nbsp;${uiLabelMap.PartyPostal}&nbsp;
              <input type="radio" name="extInfo" style="opacity: 0.5; position: relative;" class="margin-top-nav-1" value="T" onclick="javascript:refreshInfo();" <#if extInfo == "T">checked="checked"</#if>/>&nbsp;${uiLabelMap.PartyTelecom}&nbsp;
              <input type="radio" name="extInfo" style="opacity: 0.5; position: relative;" class="margin-top-nav-1" value="O" onclick="javascript:refreshInfo();" <#if extInfo == "O">checked="checked"</#if>/>&nbsp;${uiLabelMap.CommonOther}&nbsp;
            </td>
          </tr>
          <tr>
            <td >${uiLabelMap.PartyPartyId}</td>
            <td><input type="text" name="partyId" value="${parameters.partyId?if_exists}"/></td>
          </tr>
          <tr>
            <td >${uiLabelMap.PartyUserLogin}</td>
            <td><input type="text" name="userLoginId" value="${parameters.userLoginId?if_exists}"/></td>
          </tr>
          <tr>
            <td >${uiLabelMap.PartyLastName}</td>
            <td><input type="text" name="lastName" value="${parameters.lastName?if_exists}"/></td>
          </tr>
          <tr>
            <td >${uiLabelMap.PartyFirstName}</td>
            <td><input type="text" name="firstName" value="${parameters.firstName?if_exists}"/></td>
          </tr>
          <tr>
            <td >${uiLabelMap.PartyPartyGroupName}</td>
            <td><input type="text" name="groupName" value="${parameters.groupName?if_exists}"/></td>
          </tr>
      </table>
      </div>
      <div class="span6" style="margin-top: 23px;">
      <table class="basic-table" cellspacing="0">
          <tr>
            <td >${uiLabelMap.PartyRoleType}</td>
            <td>
              <select name="roleTypeId">
<#if currentRole?has_content>
                <option value="${currentRole.roleTypeId}">${currentRole.get("description",locale)}</option>
                <option value="${currentRole.roleTypeId}">---</option>
</#if>
                <option value="ANY">${uiLabelMap.CommonAnyRoleType}</option>
<#list roleTypes as roleType>
                <option value="${roleType.roleTypeId}">${roleType.get("description",locale)}</option>
</#list>
              </select>
            </td>
          </tr>
          <tr>
            <td >${uiLabelMap.PartyType}</td>
            <td>
              <select name="partyTypeId">
<#if currentPartyType?has_content>
                <option value="${currentPartyType.partyTypeId}">${currentPartyType.get("description",locale)}</option>
                <option value="${currentPartyType.partyTypeId}">---</option>
</#if>
                <option value="ANY">${uiLabelMap.CommonAny}</option>
<#list partyTypes as partyType>
                <option value="${partyType.partyTypeId}">${partyType.get("description",locale)}</option>
</#list>
              </select>
            </td>
          </tr>
          <tr>
            <td >${uiLabelMap.ProductInventoryItemId}</td>
            <td><input type="text" name="inventoryItemId" value="${parameters.inventoryItemId?if_exists}"/></td>
          </tr>
          <tr>
            <td >${uiLabelMap.ProductSerialNumber}</td>
            <td><input type="text" name="serialNumber" value="${parameters.serialNumber?if_exists}"/></td>
          </tr>
          <tr>
            <td >${uiLabelMap.ProductSoftIdentifier}</td>
            <td><input type="text" name="softIdentifier" value="${parameters.softIdentifier?if_exists}"/></td>
          </tr>
<#if extInfo == "P">
          <tr><td colspan="3"><hr /></td></tr>
          <tr>
            <td >${uiLabelMap.CommonAddress1}</td>
            <td><input type="text" name="address1" value="${parameters.address1?if_exists}"/></td>
          </tr>
          <tr>
            <td >${uiLabelMap.CommonAddress2}</td>
            <td><input type="text" name="address2" value="${parameters.address2?if_exists}"/></td>
          </tr>
          <tr>
            <td >${uiLabelMap.CommonCity}</td>
            <td><input type="text" name="city" value="${parameters.city?if_exists}"/></td>
          </tr>
          <tr>
            <td >${uiLabelMap.CommonStateProvince}</td>
            <td>
              <select name="stateProvinceGeoId">
  <#if currentStateGeo?has_content>
                <option value="${currentStateGeo.geoId}">${currentStateGeo.geoName?default(currentStateGeo.geoId)}</option>
                <option value="${currentStateGeo.geoId}">---</option>
  </#if>
                <option value="ANY">${uiLabelMap.CommonAnyStateProvince}</option>
                ${screens.render("component://common/widget/CommonScreens.xml#states")}
              </select>
            </td>
          </tr>
          <tr>
            <td >${uiLabelMap.PartyPostalCode}</td>
            <td><input type="text" name="postalCode" value="${parameters.postalCode?if_exists}"/></td>
          </tr>
</#if>
<#if extInfo == "T">
          <tr><td colspan="3"><hr /></td></tr>
          <tr>
            <td >${uiLabelMap.CommonCountryCode}</td>
            <td><input type="text" name="countryCode" value="${parameters.countryCode?if_exists}"/></td>
          </tr>
          <tr>
            <td >${uiLabelMap.PartyAreaCode}</td>
            <td><input type="text" name="areaCode" value="${parameters.areaCode?if_exists}"/></td>
          </tr>
          <tr>
            <td >${uiLabelMap.PartyContactNumber}</td>
            <td><input type="text" name="contactNumber" value="${parameters.contactNumber?if_exists}"/></td>
          </tr>
</#if>
<#if extInfo == "O">
          <tr><td colspan="3"><hr /></td></tr>
          <tr>
            <td >${uiLabelMap.PartyContactInformation}</td>
            <td><input type="text" name="infoString" value="${parameters.infoString?if_exists}"/></td>
          </tr>
</#if>
        </table>
        </div>
        </div>
        <div class="span12 align-center" style="margin-top: 10px;">
           <button type="submit" class="btn btn-small btn-primary" onclick="javascript:document.lookupparty.submit();">
           <i class="icon-search"></i>
            ${uiLabelMap.CommonFind}
          </button>
        </div>
        </div>
      </form>
    </div>
    <script language="JavaScript" type="text/javascript">
      document.lookupparty.partyId.focus();
    </script>
</div>
</div>
</div>
</div>