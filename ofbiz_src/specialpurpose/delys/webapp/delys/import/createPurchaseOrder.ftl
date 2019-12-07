<#-- allow the order type to be set in parameter, so only the appropriate section (Sales or Purchase Order) shows up -->
  <#if parameters.orderTypeId?has_content>
    <#assign shoppingCartOrderType = parameters.orderTypeId>
  </#if>
<#if security.hasEntityPermission("ORDERMGR", "_PURCHASE_CREATE", session)>
  <#if shoppingCartOrderType == "PURCHASE_ORDER">
    <div class="widget-box transparent no-border-bottom olbius-extra" > 
    <div style="margin-top:10px">
    <div >
    <div >
      <form method="post" name="poentryform" action="<@ofbizUrl>${parameters.formAction}</@ofbizUrl>">
      <input type='hidden' name='finalizeMode' value='type'/>
      <input type='hidden' name='orderMode' value='PURCHASE_ORDER'/>
      <input type='hidden' name='userLoginId' value='${parameters.userLogin.userLoginId}'/>
      <table width="100%" border='0' cellspacing='0' cellpadding='0'>
        <tr>
          <td>&nbsp;</td>
          <td width="300" align='right' valign='middle' nowrap="nowrap"><div style="margin-top:-7px" class='tableheadtext'>${uiLabelMap.OrderOrderEntryInternalOrganization}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <select name="billToCustomerPartyId"<#if sessionAttributes.orderMode?default("") == "SALES_ORDER"> disabled</#if>>
                <#list organizations as organization>
                  <#assign organizationName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(organization, true)/>
                    <#if (organizationName.length() != 0)>
                      <option value="${organization.partyId}">${organization.partyId} - ${organizationName}</option>
                    </#if>
                </#list>
              </select>
            </div>
          </td>
        </tr>
   <#--     <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap="nowrap"><div  style="margin-top:-7px" class='tableheadtext'>${uiLabelMap.CommonUserLoginId}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <@htmlTemplate.lookupField value='${parameters.userLogin.userLoginId}'formName="poentryform" name="userLoginId" id="userLoginId_purchase" fieldFormName="LookupUserLoginAndPartyDetails"/>
            </div>
          </td>
        </tr>
    -->
        <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap="nowrap"><div  style="margin-top:-7px" class='tableheadtext'>${uiLabelMap.PartySupplier}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <select name="supplierPartyId"<#if sessionAttributes.orderMode?default("") == "SALES_ORDER"> disabled</#if>>
                <#list suppliers as supplier>
                  <option value="${supplier.partyId}">[${supplier.partyId}] - ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(supplier, true)}</option>
                </#list>
              </select>
            </div>
          </td>
        </tr>
        <tr>
          <td></td>
          <td></td>
          <td></td>
          <td>
          	<a class="btn btn-small btn-primary" href="javascript:document.poentryform.submit();">${uiLabelMap.CommonContinue}
         		<i class="icon-arrow-right icon-on-right"></i>
          	</a>
          </td>
        </tr>
      </table>
      </form>
    </div>
    </div>
    </div>
  </#if>
</#if>