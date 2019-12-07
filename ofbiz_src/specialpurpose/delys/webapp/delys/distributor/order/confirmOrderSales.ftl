<div class="row-fluid">
	<div class="span6">
		<h4 class="smaller green" style="display:inline-block">
			${uiLabelMap.DAOrderGeneralInfo}
		</h4>
		<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
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
	                    <table class="table table-striped table-bordered table-hover dataTable">
	                        <tr>
	                            <td width="35%"><div><b>${uiLabelMap.OrderOrderTermType}</b></div></td>
	                            <td width="10%"><div><b>${uiLabelMap.OrderOrderTermValue}</b></div></td>
	                            <td width="10%"><div><b>${uiLabelMap.OrderOrderTermDays}</b></div></td>
	                            <td width="45%"><div><b>${uiLabelMap.CommonDescription}</b></div></td>
	                        </tr>
	                        <#assign index=0/>
	                        <#list orderTerms as orderTerm>
	                        <tr>
	                            <td width="35%"><div>${orderTerm.getRelatedOne("TermType", false).get("description",locale)}</div></td>
	                            <td width="10%"><div>${orderTerm.termValue?default("")}</div></td>
	                            <td width="10%"><div>${orderTerm.termDays?default("")}</div></td>
	                            <td width="45%"><div>${orderTerm.textValue?default("")}</div></td>
	                        </tr>
	                            <#if orderTerms.size()&lt;index>
	                        <tr><td colspan="4"><hr /></td></tr>
	                            </#if>
	                            <#assign index=index+1/>
	                        </#list>
	                    </table>
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
	                        <#if maySplit?default("N") == "N">${uiLabelMap.DAWaitEntireOrderReady}</#if>
	                        <#if maySplit?default("Y") == "Y">${uiLabelMap.DAShipAvailable}</#if>
	                    </div>
	                </td>
	            </tr>
	        -->
	        <#-- shipping instructions -->
	        <tr>
                <td align="right" valign="top" width="25%">
                    <div><b>${uiLabelMap.DASpecialInstructions}</b></div>
                </td>
                <td valign="top" width="70%">
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
	                    <div><b>${uiLabelMap.DADeliveryDate}</b></div>
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
	                    <div>${shipAfterDate}</div>
	                </td>
	            </tr>
	        </#if>
	        <#if shipBeforeDate?has_content>
	            <tr>
	                <td align="right" valign="top" width="25%">
	                    <div><b>${uiLabelMap.OrderShipBeforeDate}</b></div>
	                </td>
	                
	                <td valign="top" width="70%">
	                  	<div>${shipBeforeDate}</div>
	                </td>
	            </tr>
	        </#if>
        </table>
	</div><!--.span6-->
	<div class="span6">
		<#if !(cart?exists)><#assign cart = shoppingCart?if_exists/></#if>
			<#if cart?exists>
			<h4 class="smaller green" style="display:inline-block">${uiLabelMap.OrderShippingInformation}</h4>
			<table width="100%" class="table table-striped table-bordered table-hover dataTable">
		      	<thead>
		      		<tr>
				        <th><span>${uiLabelMap.OrderDestination}</span></th>
				        <#--<th><span>${uiLabelMap.PartySupplier}</span></th>-->
				        <#--<th><span>${uiLabelMap.ProductShipmentMethod}</span></th>-->
				        <th><span>${uiLabelMap.DAOrderItem}</span></th>
				        <th><span>${uiLabelMap.DAQuantity}</span></th>
			      	</tr>
		      	</thead>
		      	<#-- BEGIN LIST SHIP GROUPS -->
		      	<#--
		      	The structure of this table is one row per line item, grouped by ship group.
		      	The address column spans a number of rows equal to the number of items of its group.
		      	-->
		      	<tbody>
	      		<#list cart.getShipGroups() as cartShipInfo>
		      		<#assign numberOfItems = cartShipInfo.getShipItems().size()>
		      		<#if (numberOfItems > 0)>
			      	<#-- spacer goes here -->
			      	<tr>
			        	<#-- address destination column (spans a number of rows = number of cart items in it) -->
			        	<td rowspan="${numberOfItems}">
			          		<#assign contactMech = delegator.findOne("ContactMech", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId", cartShipInfo.contactMechId), false)?if_exists />
			          		<#if contactMech?has_content>
			            		<#assign address = contactMech.getRelatedOne("PostalAddress", false)?if_exists />
			          		</#if>
				          	<#if address?exists>
					            <#if address.toName?has_content><b>${uiLabelMap.DATo}:</b>&nbsp;${address.toName}<br /></#if>
					            <#if address.attnName?has_content><b>${uiLabelMap.CommonAttn}:</b>&nbsp;${address.attnName}<br /></#if>
					            <#if address.address1?has_content>${address.address1}<br /></#if>
					            <#if address.address2?has_content>${address.address2}<br /></#if>
					            <#if address.city?has_content>${address.city}</#if>
					            <#if address.stateProvinceGeoId?has_content>&nbsp;${address.stateProvinceGeoId}</#if>
					            <#if address.postalCode?has_content>, ${address.postalCode?if_exists}</#if>
				          	</#if>
			        	</td>
			        	<#-- supplier id (for drop shipments) (also spans rows = number of items) -->
			        	<#--
			        	<td rowspan="${numberOfItems}" valign="top">
			          		<#assign supplier =  delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", cartShipInfo.getSupplierPartyId()), false)?if_exists />
			          		<#if supplier?has_content>${supplier.groupName?default(supplier.partyId)}</#if>
			        	</td>
			        	-->
			        	<#-- carrier column (also spans rows = number of items) -->
				        <#--
				        <td rowspan="${numberOfItems}" valign="top">
				          	<#assign carrier =  delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", cartShipInfo.getCarrierPartyId()), false)?if_exists />
				          	<#assign method =  delegator.findOne("ShipmentMethodType", Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentMethodTypeId", cartShipInfo.getShipmentMethodTypeId()), false)?if_exists />
				          	<#if carrier?has_content>${carrier.groupName?default(carrier.partyId)}</#if>
				          	<#if method?has_content>${method.description?default(method.shipmentMethodTypeId)}</#if>
				        </td>
				        -->
			        	<#-- list each ShoppingCartItem in this group -->
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
		      	<#-- END LIST SHIP GROUPS -->
	    	</table>
		</#if>
	</div><!--.span6-->
</div>
<div class="row-fluid">
	<div class="span12">
		<h4 class="smaller green" style="display:inline-block">${uiLabelMap.DAOrderItems}</h4>
	    <#if maySelectItems?default(false)>
            <a href="javascript:document.addOrderToCartForm.add_all.value="true";document.addOrderToCartForm.submit()" class="btn btn-mini btn-primary">${uiLabelMap.OrderAddAllToCart}</a>
            <a href="javascript:document.addOrderToCartForm.add_all.value="false";document.addOrderToCartForm.submit()" class="btn btn-mini btn-primary">${uiLabelMap.OrderAddCheckedToCart}</a>
        </#if>
    	<table width="100%" border="0" cellpadding="0" class="table table-striped table-bordered table-hover dataTable">
  			<thead>
  				<tr valign="bottom">
		            <th width="55%" class="align-center"><span><b>${uiLabelMap.DAProduct}</b></span></th>
		            <#--
		            <th width="6%" align="right" class="align-right"><span><b>${uiLabelMap.DAQuantity}</b></span></th>
		            <th width="6%" align="right" class="align-right"><span><b>${uiLabelMap.DAUnitPrice}</b></span></th>
		            -->
		            <th width="9%" align="right" class="align-right"><span><b>${uiLabelMap.DAQuantityUomId}</b></span></th>
		            <th width="9%" align="right" class="align-right"><span><b>${uiLabelMap.DAAlternativeQuantity}</b></span></th>
		            <th width="9%" align="right" class="align-right"><span><b>${uiLabelMap.DAAlternativeUnitPrice}</b></span></th>
		            <th width="9%" align="right" class="align-right"><span><b>${uiLabelMap.DAAdjustment}</b></span></th>
		            <th width="9%" align="right" class="align-right"><span><b>${uiLabelMap.DAItemTotal}</b></span></th>
	          	</tr>
  			</thead>
  			<tbody>
  				<#list listOrderItem as orderItem>
  					<tr>
  						<#if isFull>
  							<td colspan="6">${orderItem.itemDescription?if_exists}</td>
  						<#else>
  							<td>${StringUtil.wrapString(orderItem.itemDescription?if_exists)}</td>
  							<#--
			               	<td align="right" valign="top" class="align-right">
			                  <div nowrap="nowrap">${orderItem.quantity?string.number}</div>
			                </td>
			                <td align="right" valign="top" class="align-right">
			                  <div nowrap="nowrap"><@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/></div>
			                </td>
			               	-->
			                <td align="right" valign="top" class="align-right">
			                  	<div nowrap="nowrap">${orderItem.quantityUomId?if_exists}</div>
			                </td>
			                <td align="right" valign="top" class="align-right">
			                  	<div nowrap="nowrap"><#if orderItem.alternativeQuantity?exists>${orderItem.alternativeQuantity?string.number}</#if></div>
			                </td>
			                <td align="right" valign="top" class="align-right">
			                  	<div nowrap="nowrap"><#if orderItem.alternativeUnitPrice?exists><@ofbizCurrency amount=orderItem.alternativeUnitPrice isoCode=currencyUomId/></#if></div>
			                </td>
			                <td align="right" valign="top" class="align-right">
			                  	<div nowrap="nowrap"><@ofbizCurrency amount=orderItem.adjustment?if_exists isoCode=currencyUomId/></div>
			                </td>
			                <td align="right" valign="top" nowrap="nowrap" class="align-right">
			                  	<div><@ofbizCurrency amount=orderItem.itemTotal?if_exists isoCode=currencyUomId/></div>
			                </td>
  						</#if>
  					</tr>
  				</#list>
  				<#if !orderItems?has_content>
	             	<tr><td colspan="6"><font color="red">${uiLabelMap.checkhelpertotalsdonotmatchordertotal}</font></td></tr>
	           	</#if>
  				<#list listWorkEffort as workEffort>
  					<tr>
  						<td colspan="6">${workEffort?if_exists}</td>
  					</tr>
  				</#list>
  				<#--
  				<#list listItemAdjustment as itemAdjustment>
  					<tr>
		                <td align="right"><div>${StringUtil.wrapString(itemAdjustment.description?if_exists)}</div></td>
		                <td>&nbsp;</td>
		                <td>&nbsp;</td>
		                <td>&nbsp;</td>
		                <td align="right" class="align-right">
		                  	<div><@ofbizCurrency amount=itemAdjustment.value isoCode=currencyUomId/></div>
		                </td>
		                <td>&nbsp;</td>
		        	</tr>
  				</#list>
  				-->
  				<tr>
		            <td align="right" colspan="5" class="align-right"><div><b>${uiLabelMap.DATotal}</b></div></td>
		            <td align="right" nowrap="nowrap" class="align-right"><div>&nbsp;<#if orderSubTotal?exists><@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/></#if></div></td>
	          	</tr>
		        <#list headerAdjustmentsToShow?if_exists as orderHeaderAdjustment>
		        	<tr>
		              	<td align="right" colspan="5" class="align-right"><div><b>${orderHeaderAdjustment.description?if_exists}</b></div></td>
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
		            <td align="right" colspan="5" class="align-right"><div><b>${uiLabelMap.DAAbbValueAddedTax}</b></div></td>
		            <td align="right" nowrap="nowrap" class="align-right"><div><#if orderTaxTotal?exists><@ofbizCurrency amount=orderTaxTotal isoCode=currencyUomId/></#if></div></td>
	          	</tr>
	          	<tr>
		            <td align="right" colspan="5" class="align-right"><div><b>${uiLabelMap.DAGrandTotalOrder}</b></div></td>
		            <td align="right" nowrap="nowrap" class="align-right">
		              <div><#if orderGrandTotal?exists><@ofbizCurrency amount=orderGrandTotal isoCode=currencyUomId/></#if></div>
		            </td>
	          	</tr>
       		</tbody>
    	</table>
	</div>
</div>



