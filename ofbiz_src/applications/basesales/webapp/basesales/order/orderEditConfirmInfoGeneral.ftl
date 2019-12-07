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
	        <tr>
                <td align="right" valign="top" width="35%">
                    <div><b>${uiLabelMap.BSInternalNote}</b></div>
                </td>
                <td valign="top" width="65%">
                    <div>
                    	<#if internalOrderNotes?has_content>
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
				        </#if>
				    </div>
                </td>
            </tr>
	        <#-- shipping instructions -->
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
            <tr>
                <td align="right" valign="top" width="25%">
                    <div><b>${uiLabelMap.BSEstimateDistanceDelivery}</b></div>
                </td>
                <td valign="top" width="70%">
                  	<div>
                  		<#if estimateDistanceDelivery?exists>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(estimateDistanceDelivery, "#,##0.0", locale)}<#else>___</#if> (km)
                  	</div>
                </td>
            </tr>
        </table>
	</div><!--.span6-->
	<div class="span6">
		<h4 class="smaller green" style="display:inline-block">${uiLabelMap.OrderShippingInformation}</h4>
		<div>
			<div id="ship-info-container">
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
			      	<#if listShipGroup?exists>
			      		<#list listShipGroup as cartShipInfo>
			      			<td rowspan="${cartShipInfo.numberOfItems?if_exists}">
			      				${StringUtil.wrapString(cartShipInfo.address?if_exists)}
			      			</td>
			      			<#if cartShipInfo.productShipItems?exists>
			      				<#list cartShipInfo.productShipItems as productItem>
			      					<#if productItem.itemIndex &gt; 0><tr></#if>
			      					<td valign="top"> ${productItem.productId?if_exists} </td>
			      					<td valign="top"> ${productItem.quantityUomDesc?if_exists} </td>
						        	<td valign="top" style="width:100px; text-align:right"> ${productItem.quantity?string.number} </td>
						        	<#if productItem.itemIndex == 0></tr></#if>
			      				</#list>
			      			</#if>
			      		</#list>
			      	</#if>
			      	</tbody>
		    	</table>
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