<#-- NOTE: this template is used for the orderstatus screen in ecommerce AND for order notification emails through the OrderNoticeEmail.ftl file -->
<#-- the "urlPrefix" value will be prepended to URLs by the ofbizUrl transform if/when there is no "request" object in the context -->
<#if baseEcommerceSecureUrl?exists><#assign urlPrefix = baseEcommerceSecureUrl/></#if>
<div class="screenlet">
  <h3>
      <#assign numColumns = 8>
      <#if maySelectItems?default("N") == "Y" && roleTypeId?if_exists == "PLACING_CUSTOMER">
          <#assign numColumns = 11>
          <a href="javascript:document.addCommonToCartForm.add_all.value='true';document.addCommonToCartForm.submit()" class="submenutext">${uiLabelMap.OrderAddAllToCart}</a>
          <span style="margin-left:5px;margin-right:5px;">|</span><a href="javascript:document.addCommonToCartForm.add_all.value='false';document.addCommonToCartForm.submit()" class="submenutext">${uiLabelMap.OrderAddCheckedToCart}</a>
          <span style="margin-left:5px;margin-right:5px;">|</span><a href="<@ofbizUrl fullPath="true">createShoppingListFromOrder?orderId=${orderHeader.orderId}&amp;frequency=6&amp;intervalNumber=1&amp;shoppingListTypeId=SLT_AUTO_REODR</@ofbizUrl>" class="submenutextright">${uiLabelMap.OrderSendMeThisEveryMonth}</a><span style="margin-left:5px;margin-right:5px;">|</span>
      </#if>
      ${uiLabelMap.OrderOrderItems}
  </h3>
  <div class='scroll-mobile'>
  <table id="shopping-cart-table" class="data-table cart-table">
    <thead>
    <tr>
      <th>${uiLabelMap.OrderProduct}</th>
      <#if maySelectItems?default("N") == "Y">
        <th>${uiLabelMap.OrderQtyOrdered}</th>
        <th>${uiLabelMap.OrderQtyPicked}</th>
        <th>${uiLabelMap.OrderQtyShipped}</th>
        <th>${uiLabelMap.OrderQtyCanceled}</th>
      <#else>
        <th></th>
        <th></th>
        <th></th>
        <th>${uiLabelMap.OrderQtyOrdered}</th>
      </#if>
      <th >${uiLabelMap.ObbUnitPrice}</th>
      <th >${uiLabelMap.OrderAdjustments}</th>
      <th >${uiLabelMap.CommonSubtotal}</th>
      <#if maySelectItems?default("N") == "Y" && roleTypeId?if_exists == "PLACING_CUSTOMER">
        <th colspan="3"></th>
      </#if>
    </tr>
    </thead>
    <tfoot>
    <tr>
      <th colspan="5"></th>
      <th colspan="2">${uiLabelMap.CommonSubtotal}</th>
      <td><@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/></td>
      <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>
    </tr>
    <#list headerAdjustmentsToShow as orderHeaderAdjustment>
      <tr>
        <th colspan="5"></th>
        <th colspan="2">${localOrderReadHelper.getAdjustmentType(orderHeaderAdjustment)}</th>
        <td><@ofbizCurrency amount=localOrderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment) isoCode=currencyUomId/></td>
        <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>
      </tr>
    </#list>
    <tr>
      <th colspan="5"></th>
      <th colspan="2">${uiLabelMap.OrderShippingAndHandling}</th>
      <td><@ofbizCurrency amount=orderShippingTotal isoCode=currencyUomId/></td>
      <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>
    </tr>
    <tr>
      <th colspan="5"></th>
      <th colspan="2">${uiLabelMap.OrderSalesTax}</th>
      <td><@ofbizCurrency amount=orderTaxTotal isoCode=currencyUomId/></td>
      <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>
    </tr>
    <tr>
	  <th colspan="5"></th>
      <th colspan="2">${uiLabelMap.OrderGrandTotal}</th>
      <td>
        <@ofbizCurrency amount=orderGrandTotal isoCode=currencyUomId/>
      </td>
      <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>
    </tr>
    </tfoot>
    <tbody>
    <#list orderItems as orderItem>
      <#-- get info from workeffort and calculate rental quantity, if it was a rental item -->
      <#assign rentalQuantity = 1> <#-- no change if no rental item -->
      <#if orderItem.orderItemTypeId == "RENTAL_ORDER_ITEM" && workEfforts?exists>
        <#list workEfforts as workEffort>
          <#if workEffort.workEffortId == orderItem.orderItemSeqId>
            <#assign rentalQuantity = localOrderReadHelper.getWorkEffortRentalQuantity(workEffort)>
            <#assign workEffortSave = workEffort>
            <#break>
          </#if>
        </#list>
      <#else>
        <#assign WorkOrderItemFulfillments = orderItem.getRelated("WorkOrderItemFulfillment", null, null, false)?if_exists>
        <#if WorkOrderItemFulfillments?has_content>
          <#list WorkOrderItemFulfillments as WorkOrderItemFulfillment>
            <#assign workEffortSave = WorkOrderItemFulfillment.getRelatedOne("WorkEffort", true)?if_exists>
            <#break>
           </#list>
        </#if>
      </#if>
      <tr>
        <#if !orderItem.productId?exists || orderItem.productId == "_?_">
          <td >
            ${orderItem.itemDescription?default("")}
          </td>
        <#else>
          <#assign product = orderItem.getRelatedOne("Product", true)?if_exists/> <#-- should always exist because of FK constraint, but just in case -->
          <td>
            <a href="<@ofbizUrl>productmaindetail?product_id=${orderItem.productId}</@ofbizUrl>" class="linktext">${orderItem.productId} - ${orderItem.itemDescription?default("")}</a>
            <#assign orderItemAttributes = orderItem.getRelated("OrderItemAttribute", null, null, false)/>
            <#if orderItemAttributes?has_content>
                <ul>
                <#list orderItemAttributes as orderItemAttribute>
                    <li>
                        ${orderItemAttribute.attrName} : ${orderItemAttribute.attrValue}
                    </li>
                </#list>
                </ul>
            </#if>
            <#if product?has_content>
              <#if product.piecesIncluded?exists && product.piecesIncluded?long != 0>
                  [${uiLabelMap.OrderPieces}: ${product.piecesIncluded}]
              </#if>
              <#if (product.quantityIncluded?exists && product.quantityIncluded != 0) || product.quantityUomId?has_content>
                <#assign quantityUom = product.getRelatedOne("QuantityUom", true)?if_exists/>
                  [${uiLabelMap.BEQuantityUomId}: ${product.quantityIncluded?if_exists} ${((quantityUom.abbreviation)?default(product.quantityUomId))?if_exists}]
              </#if>
              <#if (product.weight?exists && product.weight != 0) || product.weightUomId?has_content>
                <#assign weightUom = product.getRelatedOne("WeightUom", true)?if_exists/>
                  [${uiLabelMap.CommonWeight}: ${product.weight?if_exists} ${((weightUom.abbreviation)?default(product.weightUomId))?if_exists}]
              </#if>
              <#if (product.productHeight?exists && product.productHeight != 0) || product.heightUomId?has_content>
                <#assign heightUom = product.getRelatedOne("HeightUom", true)?if_exists/>
                  [${uiLabelMap.CommonHeight}: ${product.productHeight?if_exists} ${((heightUom.abbreviation)?default(product.heightUomId))?if_exists}]
              </#if>
              <#if (product.productWidth?exists && product.productWidth != 0) || product.widthUomId?has_content>
                <#assign widthUom = product.getRelatedOne("WidthUom", true)?if_exists/>
                  [${uiLabelMap.CommonWidth}: ${product.productWidth?if_exists} ${((widthUom.abbreviation)?default(product.widthUomId))?if_exists}]
              </#if>
              <#if (product.productDepth?exists && product.productDepth != 0) || product.depthUomId?has_content>
                <#assign depthUom = product.getRelatedOne("DepthUom", true)?if_exists/>
                  [${uiLabelMap.CommonDepth}: ${product.productDepth?if_exists} ${((depthUom.abbreviation)?default(product.depthUomId))?if_exists}]
              </#if>
            </#if>
            <#if maySelectItems?default("N") == "Y">
              <#assign returns = orderItem.getRelated("ReturnItem", null, null, false)?if_exists>
              <#if returns?has_content>
                <#list returns as return>
                  <#assign returnHeader = return.getRelatedOne("ReturnHeader", false)>
                  <#if returnHeader.statusId != "RETURN_CANCELLED">
                    <#if returnHeader.statusId == "RETURN_REQUESTED" || returnHeader.statusId == "RETURN_APPROVED">
                      <#assign displayState = "Return Pending">
                    <#else>
                      <#assign displayState = "Returned">
                    </#if>
                    ${displayState} (#${return.returnId})
                  </#if>
                </#list>
              </#if>
            </#if>
          </td>
          <#if !(maySelectItems?default("N") == "Y")>
            <td></td>
            <td></td>
            <td></td>
          </#if>
          <td>
            ${orderItem.quantity?string.number}
          </td>
          <#if maySelectItems?default("N") == "Y">
          <td>
            <#assign pickedQty = localOrderReadHelper.getItemPickedQuantityBd(orderItem)>
            <#if pickedQty gt 0 && orderHeader.statusId == "ORDER_APPROVED">${pickedQty?default(0)?string.number}<#else>${pickedQty?default(0)?string.number}</#if>
          </td>
          <td>
            <#assign shippedQty = localOrderReadHelper.getItemShippedQuantity(orderItem)>
            ${shippedQty?default(0)?string.number}
          </td>
          <td>
            <#assign canceledQty = localOrderReadHelper.getItemCanceledQuantity(orderItem)>
            ${canceledQty?default(0)?string.number}
          </td>
          </#if>
          <td>
            <@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/>
          </td>
          <td>
            <@ofbizCurrency amount=localOrderReadHelper.getOrderItemAdjustmentsTotal(orderItem) isoCode=currencyUomId/>
          </td>
          <td>
            <#if workEfforts?exists>
              <@ofbizCurrency amount=localOrderReadHelper.getOrderItemTotal(orderItem)*rentalQuantity isoCode=currencyUomId/>
            <#else>
              <@ofbizCurrency amount=localOrderReadHelper.getOrderItemTotal(orderItem) isoCode=currencyUomId/>
            </#if>
          </td>
          <#if maySelectItems?default("N") == "Y" && roleTypeId?if_exists == "PLACING_CUSTOMER">
            <td></td>
            <td>
              <input name="item_id" value="${orderItem.orderItemSeqId}" type="checkbox"/>
            </td>
            <td></td>
          </#if>
        </#if>
      </tr>
      <#-- now cancel reason and comment field -->
     
      <#-- show info from workeffort if it was a rental item -->
      
      
      <#-- show the order item ship group info -->
      <#assign orderItemShipGroupAssocs = orderItem.getRelated("OrderItemShipGroupAssoc", null, null, false)?if_exists>
      <#if orderItemShipGroupAssocs?has_content>
        <#list orderItemShipGroupAssocs as shipGroupAssoc>
          <#assign shipGroup = shipGroupAssoc.getRelatedOne("OrderItemShipGroup", false)?if_exists>
          <#assign shipGroupAddress = (shipGroup.getRelatedOne("PostalAddress", false))?if_exists>
          
        </#list>
      </#if>
    </#list>
    
    <tr><td colspan="${numColumns}"></td></tr>
    </tbody>
  </table>
  </div>
  <style type="text/css">
	.data-table tfoot th{
		border-top: 1px solid #ebebeb !important;
		text-align:right;
		vertical-align:middle;
	}
  </style>
</div>
