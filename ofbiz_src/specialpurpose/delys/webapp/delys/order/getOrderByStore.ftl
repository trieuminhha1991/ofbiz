<div id="OrdersList" class="widget-box olbius-extra">   
<div class="widget-header widget-header-small header-color-blue2">
        <h6>${uiLabelMap.OrderOrderList}</h6>
        <div class="widget-toolbar">
       	<a href="#" data-action="collapse"><i class="icon-chevron-up"></i></a>
		</div>
    </div>
     <div class="widget-body">
    <div class="widget-body-inner">
    <div class="widget-main">
        <table class="table table-striped table-bordered table-hover dataTable" cellspacing='0' style="width: 100%; ">
          <tr class="header-row">
            <td width="15%">${uiLabelMap.CommonDate}</td>
            <td width="10%">${uiLabelMap.OrderOrder} ${uiLabelMap.CommonNbr}</td>
            <td width="10%">${uiLabelMap.OrderOrderName}</td>
            <td width="10%">${uiLabelMap.OrderOrderType}</td>
            <td width="10%">${uiLabelMap.OrderOrderBillFromParty}</td>
            <td width="10%">${uiLabelMap.OrderOrderBillToParty}</td>
            <td width="10%">${uiLabelMap.OrderProductStore}</td>
            <td width="10%">${uiLabelMap.CommonAmount}</td>
            <td width="10%">${uiLabelMap.OrderTrackingCode}</td>
<#--            <#if state.hasFilter('filterInventoryProblems') || state.hasFilter('filterAuthProblems') || state.hasFilter('filterPOsOpenPastTheirETA') || state.hasFilter('filterPOsWithRejectedItems') || state.hasFilter('filterPartiallyReceivedPOs')> -->
                <td width="10%">${uiLabelMap.CommonStatus}</td>
                <td width="5%">${uiLabelMap.CommonFilter}</td>
          </tr>
          <#list parameters.listOrderDis as orderHeader>
            <#assign status = orderHeader.getRelatedOne("StatusItem", true)>
            <#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader)>
            <#assign billToParty = orh.getBillToParty()?if_exists>
            <#assign billFromParty = orh.getBillFromParty()?if_exists>
            <#if billToParty?has_content>
                <#assign billToPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", billToParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
                <#assign billTo = billToPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")/>
                <#-- <#assign billTo = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(billToParty, true)?if_exists> -->
            <#else>
              <#assign billTo = ''/>
            </#if>
            <#if billFromParty?has_content>
              <#assign billFrom = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(billFromParty, true)?if_exists>
            <#else>
              <#assign billFrom = ''/>
            </#if>
            <#assign productStore = orderHeader.getRelatedOne("ProductStore", true)?if_exists />
            <tr>
              <td><#if orderHeader.orderDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "", locale, timeZone)!}</#if></td>
              <td>
                <a href="<@ofbizUrl>orderview?orderId=${orderHeader.orderId}</@ofbizUrl>" class="btn btn-primary btn-mini">${orderHeader.orderId}</a>
              </td>
              <td>${orderHeader.orderName?if_exists}</td>
              <td>${orderHeader.getRelatedOne("OrderType", true).get("description",locale)}</td>
              <td>${billFrom?if_exists}</td>
              <td>${billTo?if_exists}</td>
              <td><#if productStore?has_content>${productStore.storeName?default(productStore.productStoreId)}</#if></td>
              <td><@ofbizCurrency amount=orderHeader.grandTotal isoCode=orderHeader.currencyUom/></td>
              <td>
                <#assign trackingCodes = orderHeader.getRelated("TrackingCodeOrder", null, null, false)>
                <#list trackingCodes as trackingCode>
                    <#if trackingCode?has_content>
                        <a class="btn btn-primary btn-mini" href="/marketing/control/FindTrackingCodeOrders?trackingCodeId=${trackingCode.trackingCodeId}&amp;externalLoginKey=${requestAttributes.externalLoginKey?if_exists}">${trackingCode.trackingCodeId}</a><br />
                    </#if>
                </#list>
              </td>
              <td>${orderHeader.getRelatedOne("StatusItem", true).get("description",locale)}</td>
              <#if state.hasFilter('filterInventoryProblems') || state.hasFilter('filterAuthProblems') || state.hasFilter('filterPOsOpenPastTheirETA') || state.hasFilter('filterPOsWithRejectedItems') || state.hasFilter('filterPartiallyReceivedPOs')>
              <td>
                  <#if filterInventoryProblems.contains(orderHeader.orderId)>
                    Inv&nbsp;
                  </#if>
                  <#if filterAuthProblems.contains(orderHeader.orderId)>
                   Aut&nbsp;
                  </#if>
                  <#if filterPOsOpenPastTheirETA.contains(orderHeader.orderId)>
                    ETA&nbsp;
                  </#if>
                  <#if filterPOsWithRejectedItems.contains(orderHeader.orderId)>
                    Rej&nbsp;
                  </#if>
                  <#if filterPartiallyReceivedPOs.contains(orderHeader.orderId)>
                    Part&nbsp;
                  </#if>
              </td>
              <#else>
              <td>&nbsp;</td>
              </#if>
            </tr>
          </#list>
          <#if !listOrderDis?has_content>
            <tr><td colspan="9"><h3>${uiLabelMap.OrderNoOrderFound}</h3></td></tr>
          </#if>
        </table>
        <@pagination/>
    </div>
    </div>
</div>
</div>