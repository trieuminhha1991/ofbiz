<#if partyTaxAuthInfoAndDetailList?exists>
    <#list partyTaxAuthInfoAndDetailList as partyTaxAuthInfoAndDetail>
        <div>
            <a href="<@ofbizUrl>deleteCustomerTaxAuthInfo?partyId=${partyId}&amp;taxAuthPartyId=${partyTaxAuthInfoAndDetail.taxAuthPartyId}&amp;taxAuthGeoId=${partyTaxAuthInfoAndDetail.taxAuthGeoId}&amp;fromDate=${partyTaxAuthInfoAndDetail.fromDate}</@ofbizUrl>" class="buttontext">X</a>
            [${partyTaxAuthInfoAndDetail.geoCode}] ${partyTaxAuthInfoAndDetail.geoName} (${partyTaxAuthInfoAndDetail.groupName?if_exists}): ${uiLabelMap.PartyTaxId} [${partyTaxAuthInfoAndDetail.partyTaxId?default("N/A")}], ${uiLabelMap.PartyTaxIsExempt} [${partyTaxAuthInfoAndDetail.isExempt?default("N")}]
        </div>
    </#list><br/>
    <div>
        <span>${uiLabelMap.PartyTaxAddInfo}:</span>
        <select name="taxAuthPartyGeoIds">
          <option></option>
          <#list taxAuthorityAndDetailList as taxAuthorityAndDetail>
            <option value="${taxAuthorityAndDetail.taxAuthPartyId}::${taxAuthorityAndDetail.taxAuthGeoId}">[${taxAuthorityAndDetail.geoCode}] ${taxAuthorityAndDetail.geoName} (${taxAuthorityAndDetail.groupName?if_exists})</option>
          </#list>
        </select>
        <span>${uiLabelMap.CommonId}: </span><input class="input-text" type="text" name="partyTaxId" size="12" maxlength="40"/>

        <#if productStore.showTaxIsExempt?default("Y") == "Y">
        <span>${uiLabelMap.PartyTaxIsExempt} </span><input type="checkbox" name="isExempt" value="Y"/>
        <#else/>
        <input type="hidden" name="isExempt" value="N"/>
        </#if>
    </div>
</#if>
