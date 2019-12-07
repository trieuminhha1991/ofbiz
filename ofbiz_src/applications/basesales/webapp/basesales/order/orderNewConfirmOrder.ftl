<style type="text/css">
	#ship-info-container {
		max-height: 173px;
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
<div class="row-fluid">
	<div class="span6">
		<h4 class="smaller green" style="display:inline-block">
			${uiLabelMap.BSOrderGeneralInfo}
		</h4>
		<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
	        <#if partyLiability?exists>
	        <tr>
                <td align="right" valign="top" width="25%">
                    <span><b>${uiLabelMap.BSLiability}</b> </span>
                </td>
                <td valign="top" width="70%">
                    <@ofbizCurrency amount=partyLiability isoCode=currentCurrencyId/>
                </td>
            </tr>
            </#if>
            <#if partyLiabilityAfter?exists>
	        <tr>
                <td align="right" valign="top" width="25%">
                    <span><b>${uiLabelMap.BSLiabilityAfter}</b> </span>
                </td>
                <td valign="top" width="70%">
                    <@ofbizCurrency amount=partyLiabilityAfter isoCode=currentCurrencyId/>
                </td>
            </tr>
            </#if>
	        <#-- order name -->
	        <#if (orderName?has_content)>
	            <tr>
	                <td align="right" valign="top" width="25%">
	                    <span><b>${uiLabelMap.OrderOrderName}</b> </span>
	                </td>
	                <td valign="top" width="70%">
	                    ${orderName}
	                </td>
	            </tr>
	        </#if>
	        <#-- order for party -->
	        <#if (orderForParty?exists)>
	            <tr>
	                <td align="right" valign="top" width="25%">
	                    <span><b>${uiLabelMap.OrderOrderFor}</b> </span>
	                </td>
	                <td valign="top" width="70%">
	                    ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(orderForParty, false)} [${orderForParty.partyId}]
	                </td>
	            </tr>
	        </#if>
	        <#if (cart.getPoNumber()?has_content)>
	            <tr>
	                <td align="right" valign="top" width="25%">
	                    <span><b>${uiLabelMap.OrderPONumber}</b> </span>
	                </td>
	                <td valign="top" width="70%">
	                    ${cart.getPoNumber()}
	                </td>
	            </tr>
	            
	        </#if>
	        <#if orderTerms?has_content>
	            <tr>
	                <td align="right" valign="top" width="25%">
	                	<div><b>${uiLabelMap.OrderOrderTerms}</b></div>
	                </td>
	                <td valign="top" width="70%">
	                    <a href="#modal-order-terms" role="button" class="green" data-toggle="modal">${uiLabelMap.BSViewListOrderTerm}</a>
	                    
	                    <div id="modal-order-terms" class="modal hide fade" tabindex="-1">
							<div class="modal-header no-padding">
								<div class="table-header">
									<button type="button" class="close" data-dismiss="modal">&times;</button>
									${uiLabelMap.OrderOrderTerms}
								</div>
							</div>

							<div class="modal-body no-padding">
								<div class="row-fluid">
									<table class="table table-striped table-bordered table-hover no-margin-bottom no-border-top">
				                        <thead>
				                        	<tr>
					                            <th width="40%"><div><b>${uiLabelMap.OrderOrderTermType}</b></div></th>
					                            <th width="15%"><div><b>${uiLabelMap.OrderOrderTermValue}</b></div></th>
					                            <th width="20%"><div><b>${uiLabelMap.OrderOrderTermDays}</b></div></th>
					                            <th width="25%"><div><b>${uiLabelMap.CommonDescription}</b></div></th>
					                        </tr>
				                        </thead>
				                        <tbody>
				                        	<#assign index=0/>
					                        <#list orderTerms as orderTerm>
					                        <tr>
					                            <td><div>${orderTerm.getRelatedOne("TermType", false).get("description",locale)}</div></td>
					                            <td><div>${orderTerm.termValue?default("")}</div></td>
					                            <td><div>${orderTerm.termDays?default("")}</div></td>
					                            <td><div>${orderTerm.textValue?default("")}</div></td>
					                        </tr>
					                            <#if orderTerms.size()&lt;index>
					                        <tr><td colspan="4"><hr /></td></tr>
					                            </#if>
					                            <#assign index=index+1/>
					                        </#list>
				                        </tbody>
									</table>
								</div>
							</div>

							<div class="modal-footer">
								<div class="pagination pull-right no-margin">
									<button class="btn btn-small btn-danger pull-left" data-dismiss="modal">
										<i class="icon-remove"></i>
										${uiLabelMap.BSClose}
									</button>
								</div>
							</div>
						</div><!--PAGE CONTENT ENDS-->
	                </td>
	            </tr>
	        </#if>
	        <#-- tracking number -->
	        <#if trackingNumber?has_content>
	            <tr>
	                <td align="right" valign="top" width="25%">
	                    <div><b>${uiLabelMap.OrderTrackingNumber}</b></div>
	                </td>
	                <td valign="top" width="70%">
	                    <#-- TODO: add links to UPS/FEDEX/etc based on carrier partyId  -->
	                    <div>${trackingNumber}</div>
	                </td>
	            </tr>
	            
	        </#if>
	        <#-- splitting preference -->
	        <#--
	        	<tr>
	                <td align="right" valign="top" width="25%">
	                    <div><b>${uiLabelMap.OrderSplittingPreference}</b></div>
	                </td>
	                <td valign="top" width="70%">
	                    <div>
	                        <#if maySplit?default("N") == "N">${uiLabelMap.BSWaitEntireOrderReady}</#if>
	                        <#if maySplit?default("Y") == "Y">${uiLabelMap.BSShipAvailable}</#if>
	                    </div>
	                </td>
	            </tr>
	        -->
			<#if internalOrderNotes?has_content>
			<tr>
                <td align="right" valign="top" width="35%">
                    <div><b>${uiLabelMap.BSInternalNote}</b></div>
                </td>
                <td valign="top" width="65%">
                    <div>
                		<#if internalOrderNotes?size &gt; 1>
			            	<ul>
			            	<#list internalOrderNotes as internalNoteItem>
			            		<li>${internalNoteItem}</li>
			            	</#list>
			            	</ul>
			            <#else>
			            	<#list internalOrderNotes as internalNoteItem>
			            		${internalNoteItem}
			            	</#list>
			            </#if>
				    </div>
                </td>
            </tr>
            </#if>
	        <#if cart.getPartyId()?exists>
				<#assign partyDisplaies = delegator.findByAnd("PartyFullNameDetailSimple", {"partyId" : cart.getPartyId()}, null, false)!/>
				<#if partyDisplaies?has_content>
					<#assign partyDisplay = partyDisplaies?first/>
		            		<tr>
                                <td align="right" valign="top" width="25%">
                                    <span><b>${uiLabelMap.BSCustomerName}</b> </span>
                                </td>
                                <td valign="top" width="70%">
									${partyDisplay.fullName?if_exists}
                                </td>
                            </tr>
				</#if>
			</#if>
	        
	        
	        <#-- gift settings -->
	        <#--
	        <#if orderType != "PURCHASE_ORDER" && (productStore.showCheckoutGiftOptions)?if_exists != "N">
	            <tr>
	                <td align="right" valign="top" width="25%">
	                    <div><b>${uiLabelMap.OrderGift}</b></div>
	                </td>
	                <td valign="top" width="70%">
	                    <div>
	                        <#if isGift?default("N") == "N">${uiLabelMap.OrderThisOrderNotGift}</#if>
	                        <#if isGift?default("N") == "Y">${uiLabelMap.OrderThisOrderGift}</#if>
	                    </div>
	                </td>
	            </tr>
	            <#if giftMessage?has_content>
	            <tr>
	                <td align="right" valign="top" width="25%">
	                    <div><b>${uiLabelMap.OrderGiftMessage}</b></div>
	                </td>
	                <td valign="top" width="70%">
	                    <div>${giftMessage}</div>
	                </td>
	            </tr>
	            </#if>
	        </#if>
	        -->
	        <#if defaultItemDeliveryDate?has_content>
	        	<tr>
	                <td align="right" valign="top" width="25%">
	                    <div><b>${uiLabelMap.BSDesiredDeliveryDate}</b></div>
	                </td>
	                
	                <td valign="top" width="70%">
	                    <div>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(defaultItemDeliveryDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}</div>
	                </td>
	            </tr>
	        </#if>
	        <#if shipAfterDate?has_content>
	            <tr>
	                <td align="right" valign="top" width="25%">
	                    <div><b>${uiLabelMap.OrderShipAfterDate}</b></div>
	                </td>
	                
	                <td valign="top" width="70%">
	                    <div>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipAfterDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}</div>
	                </td>
	            </tr>
	        </#if>
	        <#if shipBeforeDate?has_content>
	            <tr>
	                <td align="right" valign="top" width="25%">
	                    <div><b>${uiLabelMap.OrderShipBeforeDate}</b></div>
	                </td>
	                
	                <td valign="top" width="70%">
	                  	<div>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipBeforeDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}</div>
	                </td>
	            </tr>
	        </#if>
			<#assign orderPriority = cart.getAttribute("priority")!>
			<#if orderPriority?exists>
				<#assign priority = delegator.findOne("Enumeration", {"enumId": orderPriority}, false)!>
				<tr>
	                <td align="right" valign="top" width="25%">
	                    <div><b>${uiLabelMap.BSPriority}</b></div>
	                </td>
	                <td valign="top" width="70%">
	                  	<div>${priority?if_exists.description?if_exists}</div>
	                </td>
	            </tr>
			</#if>
        </table>
	</div><!--.span6-->
	<div class="span6">
		<h4 class="smaller green" style="display:inline-block">${uiLabelMap.OrderShippingInformation}</h4>
		<div>
			<div id="ship-info-container">
				<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
                    <tr>
                        <td align="right" valign="top" width="35%">
                            <div><b>${uiLabelMap.BSNoteShipping}</b></div>
                        </td>
                        <td valign="top" width="65%">
                            <div>
                    	<#if shippingInstructions?has_content>
							${shippingInstructions}
						</#if>
                            </div>
                        </td>
                    </tr>
                    <tr>
		                <td align="right" valign="top" width="25%">
		                    <span><b>${uiLabelMap.OrderDestination}</b> </span>
		                </td>
		                <td valign="top" width="70%">
		                    <#if listShipGroup?has_content>
						        <#assign cartShipInfo = listShipGroup?first/>
						        ${StringUtil.wrapString(cartShipInfo.address?if_exists)}
							</#if>
		                </td>
		            </tr>
		            <#if cart.getShipToCustomerPartyId()?exists>
		            	<#assign partyDisplaies = delegator.findByAnd("PartyFullNameDetailSimple", {"partyId" : cart.getShipToCustomerPartyId()}, null, false)!/>
						<#assign displayPartyPhones = delegator.findByAnd("PartyAndTelecomNumberOrder", {"partyId" : cart.getShipToCustomerPartyId()}, null, false)!/>
		            	<#if partyDisplaies?has_content>
		            		<#assign partyDisplay = partyDisplaies?first/>
		            		<tr>
				                <td align="right" valign="top" width="25%">
				                    <span><b>${uiLabelMap.BSPartyReceive}</b> </span>
				                </td>
				                <td valign="top" width="70%">
				                    ${partyDisplay.fullName?if_exists}
									<#if displayPartyPhones?has_content>
										(<#list displayPartyPhones as displayPartyPhone>
											${displayPartyPhone.contactNumber?string}<#if displayPartyPhone_has_next>, </#if>
										</#list>)
									</#if>

				                </td>
				            </tr>
		            	</#if>
		            </#if>
				</table>
				<#-- OLD code
				<table width="100%" class="table table-striped table-bordered dataTable">
			      	<thead>
			      		<tr>
					        <th><span>${uiLabelMap.OrderDestination}</span></th>
					        <th><span>${uiLabelMap.BSOrderItem}</span></th>
					        <th><span>${uiLabelMap.BSUom}</span></th>
					        <th><span>${uiLabelMap.BSQuantity}</span></th>
				      	</tr>
			      	</thead>
			      	<tbody>
			      	<#if listShipGroup?has_content>
			      		<#list listShipGroup as cartShipInfo>
			      			<td rowspan="${cartShipInfo.numberOfItems?if_exists}">
			      				${StringUtil.wrapString(cartShipInfo.address?if_exists)}
			      			</td>
			      			<#if cartShipInfo.productShipItems?exists>
			      				<#list cartShipInfo.productShipItems as productItem>
			      					<#if productItem.itemIndex &gt; 0><tr></#if>
			      					<td valign="top"> ${productItem.productCode?default(productItem.productId)} </td>
			      					<td valign="top"> ${productItem.quantityUomDesc?if_exists} </td>
						        	<td valign="top" style="width:100px; text-align:right"> ${productItem.quantity?string.number} </td>
						        	<#if productItem.itemIndex == 0></tr></#if>
			      				</#list>
			      			</#if>
			      		</#list>
					</#if>
			      	</tbody>
		    	</table>
				-->
		    </div>
		    <#if listOrderItem?exists && listOrderItem?size &gt; 4>
		    <div id="view-more-note-ship-info">
  				<a href="javascript:void(0);" class="btn btn-minier btn-default"><i class="fa-expand"></i></a>
		    </div>
		    </#if>
		</div>
		<#--
		<#if !(cart?exists)><#assign cart = shoppingCart?if_exists/></#if>
			<#if cart?exists>
			<h4 class="smaller green" style="display:inline-block">${uiLabelMap.OrderShippingInformation}</h4>
			<table width="100%" class="table table-striped table-bordered table-hover dataTable">
		      	<thead>
		      		<tr>
				        <th><span>${uiLabelMap.OrderDestination}</span></th>
				        // <th><span>${uiLabelMap.PartySupplier}</span></th>
				        // <th><span>${uiLabelMap.ProductShipmentMethod}</span></th>
				        <th><span>${uiLabelMap.BSOrderItem}</span></th>
				        <th><span>${uiLabelMap.BSQuantity}</span></th>
			      	</tr>
		      	</thead>
		      	// BEGIN LIST SHIP GROUPS 
		      	
		      	// The structure of this table is one row per line item, grouped by ship group.
		      	// The address column spans a number of rows equal to the number of items of its group.
		      	
		      	<tbody>
	      		<#list cart.getShipGroups() as cartShipInfo>
		      		<#assign numberOfItems = cartShipInfo.getShipItems().size()>
		      		<#if (numberOfItems > 0)>
			      	// spacer goes here
			      	<tr>
			        	// address destination column (spans a number of rows = number of cart items in it)
			        	<td rowspan="${numberOfItems}">
			          		<#assign contactMech = delegator.findOne("ContactMech", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId", cartShipInfo.contactMechId), false)?if_exists />
			          		<#if contactMech?has_content>
			            		<#assign address = contactMech.getRelatedOne("PostalAddress", false)?if_exists />
			          		</#if>
				          	<#if address?exists>
					            <#if address.toName?has_content><b>${uiLabelMap.BSTo}:</b>&nbsp;${address.toName}<br /></#if>
					            <#if address.attnName?has_content><b>${uiLabelMap.CommonAttn}:</b>&nbsp;${address.attnName}<br /></#if>
					            <#if address.address1?has_content>${address.address1}<br /></#if>
					            <#if address.address2?has_content>${address.address2}<br /></#if>
					            <#if address.city?has_content>${address.city}</#if>
					            <#if address.stateProvinceGeoId?has_content>&nbsp;${address.stateProvinceGeoId}</#if>
					            <#if address.postalCode?has_content>, ${address.postalCode?if_exists}</#if>
				          	</#if>
			        	</td>
			        	// supplier id (for drop shipments) (also spans rows = number of items)
			        	
			        	//<td rowspan="${numberOfItems}" valign="top">
			          	//	<#assign supplier =  delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", cartShipInfo.getSupplierPartyId()), false)?if_exists />
			          	//	<#if supplier?has_content>${supplier.groupName?default(supplier.partyId)}</#if>
			        	//</td>
			        	
			        	// carrier column (also spans rows = number of items) 
				        
				        //<td rowspan="${numberOfItems}" valign="top">
				        //  	<#assign carrier =  delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", cartShipInfo.getCarrierPartyId()), false)?if_exists />
				        //  	<#assign method =  delegator.findOne("ShipmentMethodType", Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentMethodTypeId", cartShipInfo.getShipmentMethodTypeId()), false)?if_exists />
				        //  	<#if carrier?has_content>${carrier.groupName?default(carrier.partyId)}</#if>
				        //  	<#if method?has_content>${method.description?default(method.shipmentMethodTypeId)}</#if>
				        //</td>
				        
			        	// list each ShoppingCartItem in this group
			        	<#assign itemIndex = 0 />
		        		<#list cartShipInfo.getShipItems() as shoppingCartItem>
			        		<#if (itemIndex > 0)> <tr> </#if>
					        <td valign="top"> ${shoppingCartItem.getProductId()?default("")} - ${shoppingCartItem.getName()?default("")} </td>
					        <td valign="top"> ${cartShipInfo.getShipItemInfo(shoppingCartItem).getItemQuantity()?default("0")} </td>
			        		<#if (itemIndex == 0)> </tr> </#if>
			        		<#assign itemIndex = itemIndex + 1 />
			        	</#list>
			      	</tr>
			      	</#if>
		      	</#list>
		      	</tbody>
		      	// END LIST SHIP GROUPS 
	    	</table>
		</#if>
		-->
	</div><!--.span6-->
</div>
<#assign enableEnterPromoCode = true>
<#if productPromoUseInfos?exists || enableEnterPromoCode>
	<#assign containerRefreshId = "step2"/>
	<#assign recalculateOrderPromoUrl = "recalculateOrderPromo"/>
	<#assign addPromoCodeUrl = "addPromoCode"/>
	<#include "orderNewPromoUseDetailsInline.ftl"/>
	<#--${screens.render("component://basesales/widget/OrderScreens.xml#PromoUseDetailsInline")}-->
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
					                    <div><a href="javascript:void(0);" class="btn btn-info btn-mini align-left" onClick="updateDesireAlternateGwpProduct('${alternativeOptionProductId}', '${orderItem.cartLineIndexes}');">
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
			                  		<span class="item-comment-icon" data-rel="tooltip" title="${uiLabelMap.BSDoubleClickMe}" data-placement="left"><i class="fa fa-comment-o" aria-hidden="true"></i></span>
			                  		<#--<input type="text" id="comments_${orderItem_index}" value=""/>-->
									<#--<span class="editable" id="comments_${orderItem_index}">${orderItem.cartLineIndexes?if_exists}</span>-->
									<span class="item-comment-r">${orderItem.comments?if_exists}</span>
									<div class="item-comment-e">
										<input type="text" id="comments_${orderItem_index}" value="${orderItem.comments?if_exists}"/>
										<button id="btnCommentUpdate_${orderItem_index}" class="btn btn-mini btn-primary" onClick="updateComment('${orderItem_index}', '${orderItem.cartLineIndexes?if_exists}');"><i class="open-sans icon-save"></i></button>
										<button id="btnCommentCancel_${orderItem_index}" class="btn btn-mini btn-danger" onClick="cancelComment('${orderItem_index}');"><i class="open-sans icon-remove"></i></button>
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
			                  	<div nowrap="nowrap"><@ofbizCurrency amount=orderItem.adjustment?if_exists isoCode=currencyUomId/></div>
			                </td>
			                <td align="right" valign="top" nowrap="nowrap" class="align-right<#if orderItem.isPromo> background-promo</#if>">
			                  	<div><@ofbizCurrency amount=orderItem.itemTotal?if_exists isoCode=currencyUomId/></div>
			                </td>
  						</#if>
  					</tr>
  				</#list>
  				<#if !orderItems?has_content>
	             	<tr><td colspan="7"><font color="red">${uiLabelMap.BSDoNotHaveAnyProduct}</font></td></tr><#--checkhelpertotalsdonotmatchordertotal-->
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
  				<tr>
		            <td align="right" colspan="6" class="align-right"><div><b>${uiLabelMap.BSShippingAndHandling}</b></div></td><#--OrderSubTotal-->
		            <td align="right" nowrap="nowrap" class="align-right"><div>&nbsp;<#if orderShippingTotal?exists><@ofbizCurrency amount=orderShippingTotal isoCode=currencyUomId/></#if></div></td>
	          	</tr>
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
       		</tbody>
    	</table>
	</div>
</div>
<script type="text/javascript">
	$(function(){
		$('[data-rel=tooltip]').tooltip();
		
		$("#view-more-note-ship-info").on("click", function() {
			if ($("#ship-info-container").hasClass("active")) {
				$("#view-more-note-ship-info i").removeClass("fa-compress");
				$("#view-more-note-ship-info i").addClass("fa-expand");
				$("#ship-info-container").removeClass("active");
				$("#ship-info-container").height('173');
				$("#ship-info-container").css('max-height', '173');
			} else {
				$("#view-more-note-ship-info i").removeClass("fa-expand");
				$("#view-more-note-ship-info i").addClass("fa-compress");
				$("#ship-info-container").addClass("active");
				$("#ship-info-container").height($("#ship-info-container table").height());
				$("#ship-info-container").css('max-height', $("#ship-info-container table").height());
			}
		});
		$("td.item-comment-td").on("dblclick", function(){
			var parent = $(this).closest(".item-comment-td");
			if (typeof(parent) != "undefined") {
				var editBox = $(parent).find(".item-comment-e");
				var boxIcon = $(parent).find(".item-comment-icon");
				if (typeof(editBox) != "undefined") {
					$(editBox).show();
					$(boxIcon).hide();
					$(editBox).find("input").focus();
				}
			}
		});
		
		$("input[id^='comments_']").keypress(function(event) {
			if (event.which == 13) {
				var targetElement = event.target;
				if (targetElement) {
					var targetId = $(targetElement).attr("id");
					if (targetId) {
						var numberSuffix = "btnCommentUpdate_" + targetId.substring(9, targetId.length);
						$("#" + numberSuffix).click();
					}
				}
			}
		});
	});
	function cancelComment(index){
		var viewBox = $("#item-comment-c-" + index + " .item-comment-r");
		if (typeof(viewBox) != "undefined") $(viewBox).show();
		var boxIcon = $("#item-comment-c-" + index + " .item-comment-icon");
		if (typeof(boxIcon) != "undefined") $(boxIcon).show();
		var editBox = $("#item-comment-c-" + index + " .item-comment-e");
		if (typeof(editBox) != "undefined") $(editBox).hide();
	}
	function updateComment(index, cartLine){
		var commentVal = $("#comments_" + index).val();
		$.ajax({
			type: 'POST',
			url: 'updateCartItemComment',
			data: {
				itemComment: commentVal,
				cartLine: cartLine
			},
			beforeSend: function(){
				$("#loader_page_common").show();
			},
			success: function(data){
				processResultUpdateComment(index, data);
			},
			error: function(data){
				alert("Send request is error");
			},
			complete: function(data){
				$("#loader_page_common").hide();
			},
		});
	};
	var processResultUpdateComment = function(index, data){
		if (data.thisRequestUri == "json") {
    		var errorMessage = "";
	        if (data._ERROR_MESSAGE_LIST_ != null) {
	        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
	        		errorMessage += "<p><b>${uiLabelMap.BSErrorUper}</b>: " + data._ERROR_MESSAGE_LIST_[i] + "</p>";
	        	}
	        }
	        if (data._ERROR_MESSAGE_ != null) {
	        	errorMessage += "<p><b>${uiLabelMap.BSErrorUper}</b>: " + data._ERROR_MESSAGE_ + "</p>";
	        }
	        if (errorMessage != "") {
	        	$('#container').empty();
	        	$('#jqxNotification').jqxNotification({ template: 'error'});
	        	$("#jqxNotification").html(errorMessage);
	        	$("#jqxNotification").jqxNotification("open");
	        } else {
	        	var newComment = data.newComment;
	        	$("#comments_" + index).val(newComment);
	        	$("#item-comment-c-" + index + " .item-comment-r").text(newComment);
				cancelComment(index);
	        	
	        	//$('#container').empty();
	        	//$('#jqxNotification').jqxNotification({ template: 'info'});
	        	//$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
	        	//$("#jqxNotification").jqxNotification("open");
	        }
	        return false;
    	} else {
    		$("#step2").html(data);
    		$('#container').empty();
        	$('#jqxNotification').jqxNotification({ template: 'info'});
        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
        	$("#jqxNotification").jqxNotification("open");
    		return true;
    	}
	};
	function updateDesireAlternateGwpProduct(alternateGwpProductId, alternateGwpLine) {
		$.ajax({
			type: 'POST',
			url: 'setDesiredAlternateGwpProductIdAjax',
			data: {
				alternateGwpProductId: alternateGwpProductId,
				alternateGwpLine: alternateGwpLine
			},
			beforeSend: function(){
				$("#loader_page_common").show();
			},
			success: function(data){
				processResultUpdateAfterDesireGwp(data);
			},
			error: function(data){
				alert("Send request is error");
			},
			complete: function(data){
				$("#loader_page_common").hide();
			},
		});
	};
	var processResultUpdateAfterDesireGwp = function(data){
		if (data.thisRequestUri == "json") {
    		var errorMessage = "";
	        if (data._ERROR_MESSAGE_LIST_ != null) {
	        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
	        		errorMessage += "<p><b>${uiLabelMap.BSErrorUper}</b>: " + data._ERROR_MESSAGE_LIST_[i] + "</p>";
	        	}
	        }
	        if (data._ERROR_MESSAGE_ != null) {
	        	errorMessage += "<p><b>${uiLabelMap.BSErrorUper}</b>: " + data._ERROR_MESSAGE_ + "</p>";
	        }
	        if (errorMessage != "") {
	        	$('#container').empty();
	        	$('#jqxNotification').jqxNotification({ template: 'error'});
	        	$("#jqxNotification").html(errorMessage);
	        	$("#jqxNotification").jqxNotification("open");
	        } else {
	        	$('#container').empty();
	        	$('#jqxNotification').jqxNotification({ template: 'info'});
	        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
	        	$("#jqxNotification").jqxNotification("open");
	        }
	        return false;
    	} else {
    		$("#step2").html(data);
    		$('#container').empty();
        	$('#jqxNotification').jqxNotification({ template: 'info'});
        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
        	$("#jqxNotification").jqxNotification("open");
    		return true;
    	}
	};
</script>