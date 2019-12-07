<#assign extInfo = parameters.extInfo?default("N")>

<div id="findOptions" class="widget-box transparent no-bottom-border">
    <div class="widget-body" >
      <#-- NOTE: this form is setup to allow a search by partial partyId or userLoginId; to change it to go directly to
          the viewprofile page when these are entered add the follow attribute to the form element:

           onsubmit="javascript:lookupparty('<@ofbizUrl>viewprofile</@ofbizUrl>');"
       -->
       <div>
        <form method="post" name="lookupparty" action="<@ofbizUrl>findApplicant</@ofbizUrl>" class="basic-form">
            <input type="hidden" name="lookupFlag" value="Y"/>
            <input type="hidden" name="hideFields" value="Y"/>
            <div class="row-fluid">
            <div class="span12">
            <div class="span6">
            <table cellspacing="0" style="margin-top: 25px;">
                <tr>
                    <td style="text-align: left;"><label class="padding-bottom5 padding-right15">${uiLabelMap.PartyContactInformation}</label></td>
                    <td></td>
                    <td><input type="radio" name="extInfo" value="N" onclick="javascript:refreshInfo();" <#if extInfo == "N">checked="checked"</#if>><span class="lbl">${uiLabelMap.CommonNone}</span></td>
                    <td><input type="radio" name="extInfo" value="P" onclick="javascript:refreshInfo();" <#if extInfo == "P">checked="checked"</#if>><span class="lbl">${uiLabelMap.PartyPostal}</span></td>
                    <td><input type="radio" name="extInfo" value="T" onclick="javascript:refreshInfo();" <#if extInfo == "T">checked="checked"</#if>><span class="lbl">${uiLabelMap.PartyTelecom}</span></td>
                    <td><input type="radio" name="extInfo" value="O" onclick="javascript:refreshInfo();" <#if extInfo == "O">checked="checked"</#if>><span class="lbl">${uiLabelMap.CommonOther}</span></td>
                </tr>
                <tr>
                	<td style="text-align: left;"><label class="padding-bottom5 padding-right15">${uiLabelMap.PartyPartyId}</label></td>
                    <td colspan="4">
                      <@htmlTemplate.lookupField value='${requestParameters.partyId?if_exists}' formName="lookupparty" name="partyId" id="partyId" fieldFormName="LookupPerson"/>
                    </td>
                </tr>
                <tr>
                	<td style="text-align: left;"><label class="padding-bottom5 padding-right15">${uiLabelMap.PartyUserLogin}</label></td>
                    <td colspan="4"><input type="text" name="userLoginId" value="${parameters.userLoginId?if_exists}" /></td>
                </tr>
                <tr>
                	<td style="text-align: left;"><label class="padding-bottom5 padding-right15">${uiLabelMap.PartyLastName}</label></td>
                    <td colspan="4"><input type="text" name="lastName" value="${parameters.lastName?if_exists}"/></td>
                </tr>
                <tr>
                	<td style="text-align: left;"><label class="padding-bottom5 padding-right15">${uiLabelMap.PartyFirstName}</label></td>
                    <td colspan="4"><input type="text" name="firstName" value="${parameters.firstName?if_exists}"/></td>
                </tr>
                <tr><td><input type="hidden" name="groupName" value="${parameters.groupName?if_exists}"/></td></tr>
                <tr><td><input type="hidden" name="roleTypeId" value="APPLICANT"/></td></tr>
                <#if extInfo == "N">
                <tr>
                    <td></td>
                    <td style="text-align: center;" colspan="4"><button class="btn btn-success btn-small" type="submit" onclick="javascript:document.lookupparty.submit();">
                    <i class="icon-search"></i>${uiLabelMap.CommonSearch}
                    </button></td>    
                </tr>
                </#if>
            </table>
            </div>
            <div class="span6">
            <table cellspacing="0" style="margin-left:40px;">
            <#if extInfo == "P">
                <tr>
                    <td style="text-align: left;"> <label class="padding-bottom5 padding-right15">${uiLabelMap.CommonAddress1}</label></td>
                    <td colspan="4"><input type="text" name="address1" value="${parameters.address1?if_exists}"/></td>
                </tr>
                <tr><td style="text-align: left;"><label class="padding-bottom5 padding-right15">${uiLabelMap.CommonAddress2}</label></td>
                    <td colspan="4"><input type="text" name="address2" value="${parameters.address2?if_exists}"/></td>
                </tr>
                <tr><td style="text-align: left;"><label class="padding-bottom5 padding-right15">${uiLabelMap.CommonCity}</label></td>
                    <td colspan="4"><input type="text" name="city" value="${parameters.city?if_exists}"/></td>
                </tr>
                <tr><td style="text-align: left;"><label class="padding-bottom5 padding-right15">${uiLabelMap.CommonStateProvince}</label></td>
                    <td colspan="4"><select name="stateProvinceGeoId">
                        <#if currentStateGeo?has_content>
                            <option value="${currentStateGeo.geoId}">${currentStateGeo.geoName?default(currentStateGeo.geoId)}</option>
                            <option value="${currentStateGeo.geoId}">---</option>
                        </#if>
                            <option value="ANY">${uiLabelMap.CommonAnyStateProvince}</option>
                            ${screens.render("component://common/widget/CommonScreens.xml#states")}
                        </select>
                    </td>
                </tr>
                <tr><td style="text-align: left;"><label class="padding-bottom5 padding-right15">${uiLabelMap.PartyPostalCode}</label></td>
                    <td colspan="4"><input type="text" name="postalCode" value="${parameters.postalCode?if_exists}"/></td>
                </tr>
            </#if>
            <#if extInfo == "T">
                <tr><td style="text-align: left;"><label class="padding-bottom5 padding-right15">${uiLabelMap.CommonCountryCode}</label></td>
                    <td colspan="4"><input type="text" name="countryCode" value="${parameters.countryCode?if_exists}"/></td>
                </tr>
                <tr><td style="text-align: left;"><label class="padding-bottom5 padding-right15">${uiLabelMap.PartyAreaCode}</label></td>
                    <td colspan="4"><input type="text" name="areaCode" value="${parameters.areaCode?if_exists}"/></td>
                </tr>
                <tr><td style="text-align: left;"><label class="padding-bottom5 padding-right15">${uiLabelMap.PartyContactNumber}</label></td>
                    <td colspan="4"><input type="text" name="contactNumber" value="${parameters.contactNumber?if_exists}"/></td>
                </tr>
            </#if>
            <#if extInfo == "O">
                <tr><td style="text-align: left;"><label class="padding-bottom5 padding-right15">${uiLabelMap.PartyContactInformation}</label></td>
                    <td colspan="4"><input type="text" name="infoString" value="${parameters.infoString?if_exists}"/></td>
                </tr>
            </#if>
            </table>
            </div>
            </div>
            </div>
            <#if extInfo != "N">
				<div style="text-align: center; margin-top: 10px;">
                    <button class="btn btn-success btn-small" type="submit" onclick="javascript:document.lookupparty.submit();">
                        <i class="icon-search"></i>${uiLabelMap.CommonSearch}
                    </button>
                </div>
			</#if>	            
        </form>
        </div>
    	</div>
  <#--  </#if>-->
	</div>
	 <#if parameters.hideFields?default("N") != "Y">
        <script language="JavaScript" type="text/javascript">
        <!--//
          document.lookupparty.partyId.focus();
        //-->
        </script>
  	</#if>