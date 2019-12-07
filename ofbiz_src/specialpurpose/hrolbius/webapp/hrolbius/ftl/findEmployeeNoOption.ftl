<#assign extInfo = parameters.extInfo?default("N")>

<div id="findEmployee" class="widget-box transparent no-bottom-border">
    <div class="widget-header widget-header-blue widget-header-flat wi1dget-header-large">
    <h4></i>${uiLabelMap.CommonFind} ${uiLabelMap.HumanResEmployee}</h4>
       <#-- <span class="widget-toolbar">
            <#if parameters.hideFields?default("N") == "Y">
                <li><a class="btn btn-mini btn-primary" href="<@ofbizUrl>FindEmployees?hideFields=N${paramList}</@ofbizUrl>"><i class="icon-double-angle-up"></i>&nbsp;${uiLabelMap.CommonShowLookupFields}&nbsp;</a></li>
            <#else>
            <#if partyList?exists><li><a class="btn btn-mini btn-primary" href="<@ofbizUrl>FindEmployees?hideFields=Y${paramList}</@ofbizUrl>"><i class="icon-double-angle-down"></i>&nbsp;${uiLabelMap.CommonHideFields}</a></li></#if>
                <li style=" padding-left: 10px; "><a class="btn btn-mini btn-success" href="javascript:document.lookupparty.submit();">${uiLabelMap.PartyLookupParty}&nbsp;<i class="icon-search"></i></a></li>
            </#if>
        </span>-->
        <br class="clear"/>
    </div>
     <#-- <#if parameters.hideFields?default("N") != "Y">-->
    <div class="widget-body">
      <#-- NOTE: this form is setup to allow a search by partial partyId or userLoginId; to change it to go directly to
          the viewprofile page when these are entered add the follow attribute to the form element:

           onsubmit="javascript:lookupparty('<@ofbizUrl>viewprofile</@ofbizUrl>');"
       -->
       <div class="margin-left30" style="margin-top:5px;">
        <form method="post" name="lookupparty" action="<@ofbizUrl>FindEmployees</@ofbizUrl>" class="basic-form">
            <input type="hidden" name="lookupFlag" value="Y"/>
            <input type="hidden" name="hideFields" value="Y"/>
            <table cellspacing="0">
                <tr><td class="arrowed">${uiLabelMap.EmployeeId}</td>
                    <td>
                      <@htmlTemplate.lookupField value='${requestParameters.partyId?if_exists}' formName="lookupparty" name="partyId" id="partyId" fieldFormName="LookupEmployeeNew"/>
                    </td>
                </tr>
                <tr><td class="arrowed">${uiLabelMap.PartyUserLogin}</td>
                    <td><input type="text" name="userLoginId" value="${parameters.userLoginId?if_exists}"/></td>
                </tr>
                <tr><td class="arrowed">${uiLabelMap.PartyLastName}</td>
                    <td><input type="text" name="lastName" value="${parameters.lastName?if_exists}"/></td>
                </tr>
                <tr><td class="arrowed">${uiLabelMap.PartyFirstName}</td>
                    <td><input type="text" name="firstName" value="${parameters.firstName?if_exists}"/></td>
                </tr>
                <tr><td><input type="hidden" name="groupName" value="${parameters.groupName?if_exists}"/></td></tr>
                <tr><td><input type="hidden" name="roleTypeId" value="EMPLOYEE"/></td></tr>
            <#if extInfo == "P">
                <tr><td colspan="3"><hr /></td></tr><tr>
                    <td class="arrowed">${uiLabelMap.CommonAddress1}</td>
                    <td><input type="text" name="address1" value="${parameters.address1?if_exists}"/></td>
                </tr>
                <tr><td class="arrowed">${uiLabelMap.CommonAddress2}</td>
                    <td><input type="text" name="address2" value="${parameters.address2?if_exists}"/></td>
                </tr>
                <tr><td class="arrowed">${uiLabelMap.CommonCity}</td>
                    <td><input type="text" name="city" value="${parameters.city?if_exists}"/></td>
                </tr>
                <tr><td class="arrowed">${uiLabelMap.CommonStateProvince}</td>
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
                <tr><td class="arrowed">${uiLabelMap.PartyPostalCode}</td>
                    <td><input type="text" name="postalCode" value="${parameters.postalCode?if_exists}"/></td>
                </tr>
            </#if>
            <#if extInfo == "T">
                <tr><td colspan="3"><hr /></td></tr>
                <tr><td class="arrowed">${uiLabelMap.CommonCountryCode}</td>
                    <td><input type="text" name="countryCode" value="${parameters.countryCode?if_exists}"/></td>
                </tr>
                <tr><td class="arrowed">${uiLabelMap.PartyAreaCode}</td>
                    <td><input type="text" name="areaCode" value="${parameters.areaCode?if_exists}"/></td>
                </tr>
                <tr><td class="arrowed">${uiLabelMap.PartyContactNumber}</td>
                    <td><input type="text" name="contactNumber" value="${parameters.contactNumber?if_exists}"/></td>
                </tr>
            </#if>
            <#if extInfo == "O">
                <tr><td colspan="3"><hr /></td></tr>
                <tr><td class="arrowed">${uiLabelMap.PartyContactInformation}</td>
                    <td><input type="text" name="infoString" value="${parameters.infoString?if_exists}"/></td>
                </tr>
            </#if>
            </table>
            <div align="center">
                    <td><button class="btn btn-success btn-small" type="submit" onclick="javascript:document.lookupparty.submit();">
                    <i class="icon-search"></i>${uiLabelMap.PartyLookupParty}
                    </button>
                    <a class="btn btn-primary btn-small" href="<@ofbizUrl>FindEmployees?roleTypeId=EMPLOYEE&amp;hideFields=Y&amp;lookupFlag=Y</@ofbizUrl>" >${uiLabelMap.CommonShowAllRecords}&nbsp;<i class="icon-lightbulb"></i></a>
            </div>
        </form>
        </div>
    	</div>
      <#--</#if> -->
	</div>
    <#if parameters.hideFields?default("N") != "Y">
        <script language="JavaScript" type="text/javascript">
    <!--//
      document.lookupparty.partyId.focus();
    //-->
        </script>
    </#if>