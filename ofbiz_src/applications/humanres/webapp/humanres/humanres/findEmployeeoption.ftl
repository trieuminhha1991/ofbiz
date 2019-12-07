<#assign extInfo = parameters.extInfo?default("N")>
<div id="findEmployee" class="widget-box transparent no-bottom-border">
   <#-- \<div class="widget-header">
    <h4>${uiLabelMap.CommonFind} ${uiLabelMap.HumanResEmployee}</h4>
        <span class="widget-toolbar">
            <#if parameters.hideFields?default("N") == "Y">
                <li><a href="<@ofbizUrl>findEmployees?hideFields=N${paramList}</@ofbizUrl>">${uiLabelMap.CommonShowLookupFields}</a></li>
            <#else>
            <#if partyList?exists><li><a href="<@ofbizUrl>findEmployees?hideFields=Y${paramList}</@ofbizUrl>">${uiLabelMap.CommonHideFields}</a></li></#if>
                <li style=" padding-left: 10px; "><a href="javascript:document.lookupparty.submit();">${uiLabelMap.PartyLookupParty}</a></li>
            </#if>
        </span>
        <br class="clear"/>
    </div>
    <#if parameters.hideFields?default("N") != "Y">-->
    <div class="widget-body">
      <#-- NOTE: this form is setup to allow a search by partial partyId or userLoginId; to change it to go directly to
          the viewprofile page when these are entered add the follow attribute to the form element:

           onsubmit="javascript:lookupparty('<@ofbizUrl>viewprofile</@ofbizUrl>');"
       -->
       <div class="margin-left30">
        <form method="post" name="lookupparty" action="<@ofbizUrl>findEmployees</@ofbizUrl>" class="basic-form">
            <input type="hidden" name="lookupFlag" value="Y"/>
            <input type="hidden" name="hideFields" value="Y"/>
            <table cellspacing="0">
                <tr><td style="text-align:right;">${uiLabelMap.PartyContactInformation}</td>
                    <td>
                    	<input class="input-radio" type="radio" name="extInfo" value="N" onclick="javascript:refreshInfo();" <#if extInfo == "N">checked="checked"</#if>/>&nbsp;<span class="lbl">${uiLabelMap.CommonNone}</span>
                        <input class="input-radio" type="radio" name="extInfo" value="P" onclick="javascript:refreshInfo();" <#if extInfo == "P">checked="checked"</#if>/>&nbsp;<span class="lbl">${uiLabelMap.PartyPostal}</span>
                       	<input class="input-radio" type="radio" name="extInfo" value="T" onclick="javascript:refreshInfo();" <#if extInfo == "T">checked="checked"</#if>/>&nbsp;<span class="lbl">${uiLabelMap.PartyTelecom}</span>
                        <input class="input-radio" type="radio" name="extInfo" value="O" onclick="javascript:refreshInfo();" <#if extInfo == "O">checked="checked"</#if>/>&nbsp;<span class="lbl">${uiLabelMap.CommonOther}</span>
                    </td>
                </tr>
                <tr><td style="text-align:right;">${uiLabelMap.PartyPartyId}</td>
                    <td>
                      <@htmlTemplate.lookupField value='${requestParameters.partyId?if_exists}' formName="lookupparty" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>
                    </td>
                </tr>
                <tr><td style="text-align:right;">${uiLabelMap.PartyUserLogin}</td>
                    <td><input type="text" name="userLoginId" value="${parameters.userLoginId?if_exists}"/></td>
                </tr>
                <tr><td style="text-align:right;">${uiLabelMap.PartyLastName}</td>
                    <td><input type="text" name="lastName" value="${parameters.lastName?if_exists}"/></td>
                </tr>
                <tr><td style="text-align:right;">${uiLabelMap.PartyFirstName}</td>
                    <td><input type="text" name="firstName" value="${parameters.firstName?if_exists}"/></td>
                </tr>
                <tr><td><input type="hidden" name="groupName" value="${parameters.groupName?if_exists}"/></td></tr>
                <tr><td><input type="hidden" name="roleTypeId" value="EMPLOYEE"/></td></tr>
            <#if extInfo == "P">
                <tr><td colspan="3"><hr /></td></tr><tr>
                    <td class="label arrowed">${uiLabelMap.CommonAddress1}</td>
                    <td><input type="text" name="address1" value="${parameters.address1?if_exists}"/></td>
                </tr>
                <tr><td class="label arrowed">${uiLabelMap.CommonAddress2}</td>
                    <td><input type="text" name="address2" value="${parameters.address2?if_exists}"/></td>
                </tr>
                <tr><td class="label arrowed">${uiLabelMap.CommonCity}</td>
                    <td><input type="text" name="city" value="${parameters.city?if_exists}"/></td>
                </tr>
                <tr><td class="label arrowed">${uiLabelMap.CommonStateProvince}</td>
                    <td><select name="stateProvinceGeoId">
                        <#if currentStateGeo?has_content>
                            <option value="${currentStateGeo.geoId}">${currentStateGeo.geoName?default(currentStateGeo.geoId)}</option>
                            <option value="${currentStateGeo.geoId}">---</option>
                        </#if>
                            <option value="ANY">${uiLabelMap.CommonAnyStateProvince}</option>
                            ${screens.render("component://common/widget/CommonScreens.xml#states")}
                        </select>
                    </td>
                </tr>
                <tr><td class="label arrowed">${uiLabelMap.PartyPostalCode}</td>
                    <td><input type="text" name="postalCode" value="${parameters.postalCode?if_exists}"/></td>
                </tr>
            </#if>
            <#if extInfo == "T">
                <tr><td colspan="3"><hr /></td></tr>
                <tr><td class="label arrowed">${uiLabelMap.CommonCountryCode}</td>
                    <td><input type="text" name="countryCode" value="${parameters.countryCode?if_exists}"/></td>
                </tr>
                <tr><td class="label arrowed">${uiLabelMap.PartyAreaCode}</td>
                    <td><input type="text" name="areaCode" value="${parameters.areaCode?if_exists}"/></td>
                </tr>
                <tr><td class="label arrowed">${uiLabelMap.PartyContactNumber}</td>
                    <td><input type="text" name="contactNumber" value="${parameters.contactNumber?if_exists}"/></td>
                </tr>
            </#if>
            <#if extInfo == "O">
                <tr><td colspan="3"><hr /></td></tr>
                <tr><td class="label arrowed">${uiLabelMap.PartyContactInformation}</td>
                    <td><input type="text" name="infoString" value="${parameters.infoString?if_exists}"/></td>
                </tr>
            </#if>
            </table>
            <div align="center">
            		<hr style="width: 90%"/>
                    <td><button class="btn btn-primary btn-small icon-search open-sans" type="submit" onclick="javascript:document.lookupparty.submit();">
                   &nbsp${uiLabelMap.PartyLookupParty}
                    </button>
                    <a class="btn btn-primary btn-small icon-list-ul open-sans" href="<@ofbizUrl>findEmployees?roleTypeId=EMPLOYEE&amp;hideFields=Y&amp;lookupFlag=Y</@ofbizUrl>" >&nbsp&nbsp${uiLabelMap.CommonShowAllRecords}</a>
            </div>
        </form>
        </div>
    	</div>
    <#--</#if>-->
	</div>