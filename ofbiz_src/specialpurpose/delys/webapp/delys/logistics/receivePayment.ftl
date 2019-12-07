<#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session)>
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h4 class="header smaller lighter blue"><b>${uiLabelMap.OrderReceiveOfflinePayments}</b></h4>
    </div>
    <div class="screenlet-body">
    <div style="float:right;margin-top:-45px">
      <a href="javascript:document.receivePaymentForm.submit()" class="open-sans icon-save">${uiLabelMap.CommonSave}</a>
	</div>
      <form method="post" action="<@ofbizUrl>/receivePayments?orderId=${orderId}</@ofbizUrl>" name="receivePaymentForm">
        <#if requestParameters.workEffortId?exists>
            <input type="hidden" name="workEffortId" value="${requestParameters.workEffortId}" />
        </#if>
        <input type="hidden" name="partyId" value="${orderRoles[0].partyId}" />

        <#if paymentMethods?has_content>
        <table class="basic-table" cellspacing='0' style="margin-left: -1.3%;">
          <tr class="header-row">
            <td width="30%" align="right">${uiLabelMap.PaymentMethod}</td>
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="1">${uiLabelMap.OrderAmount}</td>
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="70%">${uiLabelMap.OrderReference}</td>
          </tr>
          <#list paymentMethods as payMethod>
          <tr>
            <td width="30%" align="right">${payMethod.get("description",locale)?default(payMethod.paymentMethodId)}</td>
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="1"><input type="text" size="7" name="${payMethod.paymentMethodId}_amount" /></td>
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="70%"><input type="text" size="15" name="${payMethod.paymentMethodId}_reference" /></td>
          </tr>
          </#list>
        </table>
        </#if>
        <br /> <br />
        <#if paymentMethodTypes?has_content>
        <table class="basic-table" cellspacing='0'>
          <tr class="header-row">
            <td width="30%" align="right">${uiLabelMap.OrderPaymentType}</td>
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="1">${uiLabelMap.OrderAmount}</td>
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="70%">${uiLabelMap.OrderReference}</td>
          </tr>
          <#list paymentMethodTypes as payType>
          <tr>
            <td width="30%" align="right">${payType.get("description",locale)?default(payType.paymentMethodTypeId)}</td>
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="1"><input type="text" size="7" name="${payType.paymentMethodTypeId}_amount" /></td>
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="70%"><input type="text" size="15" name="${payType.paymentMethodTypeId}_reference" /></td>
          </tr>
          </#list>
        </table>
        </#if>
      </form>
    </div>
</div>
<br />
<#else>
  <div class="alert alert-danger">${uiLabelMap.OrderViewPermissionError}</div>
</#if>