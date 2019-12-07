<style type="text/css">
	#ship-info-container {
		height: 155px;
		overflow: hidden;
		-webkit-transition: height 1s ease-in-out;
		transition: height 1s ease-in-out;
		padding-bottom:3px;
	}
	#view-more-note-ship-info {
		margin-top:-23px;
	}
	.table td.item-comment-td {
		padding: 2px !important;
	}
	.item-comment-td .item-comment-e {
		display:none;
	}
	.item-comment-td .item-comment-e input{
		width:100px;
		height:20px;
		font-size:11px;
		padding:2px;
	}
	.item-comment-td .item-comment-e [class^="icon-"]:before, [class*=" icon-"]:before{
		margin-right:0;
	}
</style>
<#if productPromoUseInfos?exists>
	<#assign containerRefreshId = "windowEditContactMechContainer"/>
	<#assign recalculateOrderPromoUrl = "recalculateOrderPromoUpdate"/>
	<#include "orderNewPromoUseDetailsInline.ftl"/>
</#if>
<div class="row-fluid">
	<div class="span12">
		<h4 class="smaller green" style="display:inline-block">${uiLabelMap.BSOrderItems}</h4>
	    <#if maySelectItems?default(false)>
            <a href="javascript:document.addOrderToCartForm.add_all.value="true";document.addOrderToCartForm.submit()" class="btn btn-mini btn-primary">${uiLabelMap.OrderAddAllToCart}</a>
            <a href="javascript:document.addOrderToCartForm.add_all.value="false";document.addOrderToCartForm.submit()" class="btn btn-mini btn-primary">${uiLabelMap.OrderAddCheckedToCart}</a>
        </#if>
    	<table width="100%" border="0" cellpadding="0" class="table table-striped table-bordered table-hover dataTable">
  			<thead>
  				<tr valign="bottom">
		            <th width="39%" class="align-center"><span><b>${uiLabelMap.BSProductId} - ${uiLabelMap.BSProductName}</b></span></th>
		            <#--
		            <th width="6%" align="right" class="align-right"><span><b>${uiLabelMap.BSQuantity}</b></span></th>
		            <th width="6%" align="right" class="align-right"><span><b>${uiLabelMap.BSUnitPrice}</b></span></th>
		            -->
		            <th width="16%"><span><b>${uiLabelMap.BSNote}</b></span></th>
		            <th width="8%" align="right" class="align-center"><span><b>${uiLabelMap.BSUom}</b></span></th>
		            <th width="7%" align="right" class="align-center"><span><b>${uiLabelMap.BSQuantityOrder}</b></span></th>
		            <th width="10%" align="right" class="align-center"><span><b>${uiLabelMap.BSUnitPrice}</b></span></th>
		            <th width="10%" align="right" class="align-center"><span><b>${uiLabelMap.BSAdjustment}</b></span></th>
		            <th width="11%" align="right" class="align-center"><span><b>${uiLabelMap.BSItemTotal}</b></span></th>
	          	</tr>
  			</thead>
  			<tbody>
  				<#assign itemsAdjustmentTotal = 0>
  				<#assign hasPromoItem = false>
  				<#list listOrderItem as orderItem>
  					<tr>
  						<#if isFull>
  							<td colspan="7">${orderItem.itemDescription?if_exists}</td>
  						<#else>
  							<td<#if orderItem.isPromo> class="background-promo"</#if>>
								${StringUtil.wrapString(orderItem.itemDescription?if_exists)}
								<#if "PRODPROMO_ORDER_ITEM" == orderItem.orderItemTypeId>
									 - <span class="red">(<b>${uiLabelMap.BSProductReturnPromo}</b>)</span>
								</#if>
								<#if orderItem.alternativeOptionProductIds?exists>
									<#list orderItem.alternativeOptionProductIds as alternativeOptionProductId>
										<#assign alternativeOptionProduct = delegator.findOne("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", alternativeOptionProductId), true)>
					                    <#assign alternativeOptionName = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(alternativeOptionProduct, "PRODUCT_NAME", locale, dispatcher)?if_exists>
					                    <div><a href="javascript:void(0);" class="btn btn-info btn-mini" onClick="updateDesireAlternateGwpProductCart('${alternativeOptionProductId}', '${orderItem.cartLineIndexes}');">
					                    	${uiLabelMap.BSSelect}: ${alternativeOptionName?default(alternativeOptionProductId)}
											<#--<@ofbizUrl>setDesiredAlternateGwpProductId?alternateGwpProductId=${alternativeOptionProductId}&amp;alternateGwpLine=${orderItem.cartLineIndexes}</@ofbizUrl>-->
										</a></div>
									</#list>
								</#if>
							</td>
  							<#--
			               	<td align="right" valign="top" class="align-right">
			                  <div nowrap="nowrap">${orderItem.quantity?string.number}</div>
			                </td>
			                <td align="right" valign="top" class="align-right">
			                  <div nowrap="nowrap"><@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/></div>
			                </td>
			               	-->
							<td align="right" valign="top" class="item-comment-td<#if orderItem.isPromo> background-promo</#if>">
			                  	<div id="item-comment-c-${orderItem_index}">
			                  		<#--<input type="text" id="comments_${orderItem_index}" value=""/>-->
									<#--<span class="editable" id="comments_${orderItem_index}">${orderItem.cartLineIndexes?if_exists}</span>-->
									<span class="item-comment-r">${orderItem.comments?if_exists}</span>
									<div class="item-comment-e">
										<input type="text" id="comments_${orderItem_index}" value="${orderItem.comments?if_exists}"/>
										<button class="btn btn-mini btn-primary" onClick="updateComment('${orderItem_index}', '${orderItem.cartLineIndexes?if_exists}');"><i class="open-sans icon-save"></i></button>
										<button class="btn btn-mini btn-danger" onClick="cancelComment('${orderItem_index}');"><i class="open-sans icon-remove"></i></button>
									</div>
			                  	</div>
			                </td>
			                <td align="right" valign="top" class="align-center<#if orderItem.isPromo> background-promo</#if>">
			                  	<div nowrap="nowrap">${orderItem.quantityUomDescription?if_exists}</div>
			                </td>
			                <td align="right" valign="top" class="align-right<#if orderItem.isPromo> background-promo</#if>">
			                  	<div nowrap="nowrap"><#if orderItem.alternativeQuantity?exists>${orderItem.alternativeQuantity?string.number}</#if></div>
			                </td>
			                <td align="right" valign="top" class="align-right<#if orderItem.isPromo> background-promo</#if>">
			                  	<div nowrap="nowrap"><#if orderItem.alternativeBasePrice?exists><@ofbizCurrency amount=orderItem.alternativeBasePrice isoCode=currencyUomId locale=locale/></#if></div>
			                </td>
			                <td align="right" valign="top" class="align-right<#if orderItem.isPromo> background-promo</#if>">
			                  	<div nowrap="nowrap">
			                  		<#if orderItem.isPromo>
			                  			<#assign hasPromoItem = true>
										<#assign itemsAdjustmentTotal = itemsAdjustmentTotal + orderItem.adjustment>
									</#if>
									<@ofbizCurrency amount=orderItem.adjustment?if_exists isoCode=currencyUomId/>
								</div>
			                </td>
			                <td align="right" valign="top" nowrap="nowrap" class="align-right<#if orderItem.isPromo> background-promo</#if>">
			                  	<div><@ofbizCurrency amount=orderItem.itemTotal?if_exists isoCode=currencyUomId/></div>
			                </td>
  						</#if>
  					</tr>
  				</#list>
  				<#if !orderItems?has_content>
	             	<tr><td colspan="7"><font color="red">${uiLabelMap.checkhelpertotalsdonotmatchordertotal}</font></td></tr>
	           	</#if>
  				<#list listWorkEffort as workEffort>
  					<tr>
  						<td colspan="7">${workEffort?if_exists}</td>
  					</tr>
  				</#list>
  				<#list listItemAdjustment?if_exists as itemAdjustment>
  					<tr>
		                <td align="right" colspan="6" class="align-right"><div>${StringUtil.wrapString(itemAdjustment.description?if_exists)}</div></td>
		                <td align="right" class="align-right">
		                	<#if itemAdjustment.value?exists && itemAdjustment.value &lt; 0>
	                  			<div>(<@ofbizCurrency amount=-itemAdjustment.value isoCode=currencyUomId/>)</div>
	              			<#else>
              					<@ofbizCurrency amount=itemAdjustment.value isoCode=currencyUomId/>
              				</#if>
	                  	</td>
		        	</tr>
  				</#list>
				<#if hasPromoItem && totalTaxOrderItemPromo?exists>
				<#--
				<tr>
        			<td align="right" class="align-right" colspan="6"><div>${uiLabelMap.BSItemsPromoTaxTotal}</div></td>
        			<td align="right" class="align-right" nowrap="nowrap">
                		<@ofbizCurrency amount=totalTaxOrderItemPromo isoCode=currencyUomId/>
    				</td>
      			</tr>
				-->
				<tr>
        			<td align="right" class="align-right" colspan="6"><div>${uiLabelMap.BSItemsPromoTaxTotalDicount}</div></td>
        			<td align="right" class="align-right" nowrap="nowrap">
                		(<@ofbizCurrency amount=totalTaxOrderItemPromo isoCode=currencyUomId/>)
    				</td>
      			</tr>
      			<tr>
        			<td align="right" class="align-right" colspan="6"><div><b><i>${uiLabelMap.BSItemsPromoAdjustmentTotal}</i></b></div></td>
        			<td align="right" class="align-right" nowrap="nowrap">
        				<#assign itemsTaxTotal = itemsAdjustmentTotal - totalTaxOrderItemPromo/>
        				<#if itemsTaxTotal?exists && itemsTaxTotal &lt; 0>
        					(<@ofbizCurrency amount=-itemsTaxTotal isoCode=currencyUomId rounding=0/>)
        				<#else>
        					<@ofbizCurrency amount=itemsTaxTotal isoCode=currencyUomId rounding=0/>
        				</#if>
    				</td>
      			</tr>
      			</#if>

				<tr><td colspan="6"></td><td></td></tr>
  				<tr>
		            <td align="right" colspan="6" class="align-right"><div><b>${uiLabelMap.BSTotal}</b></div></td><#--OrderSubTotal-->
		            <td align="right" nowrap="nowrap" class="align-right"><div>&nbsp;<#if orderSubTotal?exists><@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/></#if></div></td>
	          	</tr>
		        <#list headerAdjustmentsToShow?if_exists as orderHeaderAdjustment>
		        	<tr>
		              	<td align="right" colspan="6" class="align-right"><div><b>${orderHeaderAdjustment.description?if_exists}</b></div></td>
		              	<td align="right" nowrap="nowrap" class="align-right">
		              		<div>
		              			<#assign oHAdjustment = localOrderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment)>
		              			<#if oHAdjustment?exists && oHAdjustment &lt; 0>
		              				(<@ofbizCurrency amount=-oHAdjustment isoCode=currencyUomId/>)
		              			<#else>
		              				<@ofbizCurrency amount=oHAdjustment isoCode=currencyUomId/>
		              			</#if>
		              		</div>
		              	</td>
		            </tr>
	          	</#list>
	          	<#--<tr>
		            <td align="right" colspan="4"><div><b>${uiLabelMap.FacilityShippingAndHandling}</b></div></td>
		            <td align="right" nowrap="nowrap"><div><#if orderShippingTotal?exists><@ofbizCurrency amount=orderShippingTotal isoCode=currencyUomId/></#if></div></td>
	          	</tr>-->
	          	<tr>
		            <td align="right" colspan="6" class="align-right"><div><b>${uiLabelMap.BSAbbValueAddedTax}</b></div></td><#--OrderSalesTax-->
		            <td align="right" nowrap="nowrap" class="align-right"><div><#if orderTaxTotal?exists><@ofbizCurrency amount=orderTaxTotal isoCode=currencyUomId/></#if></div></td>
	          	</tr>
	          	<tr>
		            <td align="right" colspan="6" class="align-right"><div><b>${uiLabelMap.BSGrandTotalOrder}</b></div></td><#--OrderGrandTotal-->
		            <td align="right" nowrap="nowrap" class="align-right">
		              <div><#if orderGrandTotal?exists><@ofbizCurrency amount=orderGrandTotal isoCode=currencyUomId/></#if></div>
		            </td>
	          	</tr>
	          	<tr>
		            <td align="right" colspan="6" class="align-right"><div style="text-transform: uppercase;"><b>${uiLabelMap.BSGrandTotalOrder} (${uiLabelMap.BSRounded})</b></div></td><#--OrderGrandTotal-->
		            <td align="right" nowrap="nowrap" class="align-right">
		              <div style="font-size: 14px"><b><#if orderGrandTotal?exists><@ofbizCurrency amount=orderGrandTotal isoCode=currencyUomId rounding=0/></#if></b></div>
		            </td>
	          	</tr>
       		</tbody>
    	</table>
	</div>
</div>
<#include "script/orderEditConfirmScript.ftl">