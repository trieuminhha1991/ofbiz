<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#-- Continuation of showcart.ftl:  List of order items and forms to modify them.
	 Extend from showcartitems.ftl file -->
<#macro showAssoc productAssoc>
  	<#assign productAssocType = (delegator.findOne("ProductAssocType", {"productAssocTypeId" : productAssoc.productAssocTypeId}, false))/>
  	<#assign assocProduct = (delegator.findOne("Product", {"productId" : productAssoc.productIdTo}, false))/>
  	<#if assocProduct?has_content>
    	<td style="border-left: 0px !important"><a href="<@ofbizUrl>/product?product_id=${productAssoc.productIdTo}</@ofbizUrl>" class="btn btn-info btn-mini open-sans">${productAssoc.productIdTo}</a></td>
    	<td>- ${(assocProduct.productName)?if_exists}<i>(${(productAssocType.description)?default("Unknown")})</i></td>
  	</#if>
</#macro>

<div class="widget-box olbius-extra no-border-bottom transparent">
    <div class="widget-body">
  	<div class="widget-body-inner">
   		<div class="widget-main">
  		<#if (shoppingCartSize > 0)>
    		<form method="post" action="<@ofbizUrl>modifyCartPayPromo</@ofbizUrl>" name="cartform" style="margin: 0;">
      			<input type="hidden" name="removeSelected" value="false"/>
      			<table cellspacing="0" cellpadding="1" border="0" class="table table-striped dataTable table-hover table-bordered">
        			<thead>
        				<tr>
	          				<th>
              					${uiLabelMap.ProductProduct}
              					<input type="hidden" name="GWALL" value=""/>
	          				</th>
	          				<th align="center" nowrap="nowrap">${uiLabelMap.ProductInventory} (${uiLabelMap.ProductAtp}/${uiLabelMap.ProductQoh})</th>
	          				<th align="certer" nowrap="nowrap">${uiLabelMap.DAExpireDate}</th>
				          	<th align="center" nowrap="nowrap">${uiLabelMap.OrderQuantity}</th>
				          	<th align="right" nowrap="nowrap">${uiLabelMap.CommonUnitPrice}</th>
				          	<th align="right" nowrap="nowrap">${uiLabelMap.OrderAdjustments}</th>
				          	<th align="right" nowrap="nowrap">${uiLabelMap.OrderItemTotal}</th>
	          				<th align="center">
					          	<label>
									<input type="checkbox" name="selectAll" value="0" onclick="javascript:toggleAll(this);" />
									<span class="lbl"></span>
								</label>
	          				</th>
	        			</tr>
        			</thead>

        			<tbody>
        				<#assign itemsFromList = false>
	        			<#list shoppingCart.items() as cartLine>
				          	<#assign cartLineIndex = shoppingCart.getItemIndex(cartLine)>
				          	<#assign lineOptionalFeatures = cartLine.getOptionalProductFeatures()>
			          		<tr valign="top">
	        					<td>
	        						<#if cartLine.getProductId()?exists>
					                    <#-- product item -->
					                    <a href="<@ofbizUrl>editProduct?product_id=${cartLine.getProductId()}</@ofbizUrl>" class="margin-top-nav-10" title="orderId = ${cartLine.associatedOrderId?if_exists}, orderItemSeqId = ${cartLine.associatedOrderItemSeqId?if_exists}">
					                    	${cartLine.getProductId()}
				                    	</a>:&nbsp;
				                    	<i>${cartLine.getDescription()?if_exists}</i>
				                  	<#else>
					                    <#-- this is a non-product item -->
					                    <b>${cartLine.getItemTypeDescription()?if_exists}</b>:&nbsp;&nbsp;
					                    ${cartLine.getName()?if_exists}
				                  	</#if>
	    							<#-- display the item's features -->
			                   		<#assign features = "">
				                   	<#if cartLine.getFeaturesForSupplier(dispatcher,shoppingCart.getPartyId())?has_content>
				                       	<#assign features = cartLine.getFeaturesForSupplier(dispatcher, shoppingCart.getPartyId())>
				                   	<#elseif cartLine.getStandardFeatureList()?has_content>
				                       	<#assign features = cartLine.getStandardFeatureList()>
				                   	</#if>
				                   	<#if features?has_content>
				                     	<br /><i>${uiLabelMap.ProductFeatures}: <#list features as feature>${feature.description?default("")} </#list></i>
				                   	</#if>
				                    <#-- show links to survey response for this item -->
				            	</td>
				            	
				            	<#-- inventory summary -->
					            <#if cartLine.getProductId()?exists>
					              	<#assign productId = cartLine.getProductId()>
					              	<#assign product = cartLine.getProduct()>
			            			<td>
					                  	<div>
					                  	<#if availableToPromiseMap.get(productId)?exists && quantityOnHandMap.get(productId)?exists>
						                    ${availableToPromiseMap.get(productId)} / ${quantityOnHandMap.get(productId)}
						                    
						                    <#if Static["org.ofbiz.entity.util.EntityTypeUtil"].hasParentType(delegator, "ProductType", "productTypeId", product.productTypeId, "parentTypeId", "MARKETING_PKG")>
						                    	${uiLabelMap.ProductMarketingPackageATP} = ${mktgPkgATPMap.get(productId)}, ${uiLabelMap.ProductMarketingPackageQOH} = ${mktgPkgQOHMap.get(productId)}
							                    <#if (mktgPkgATPMap.get(cartLine.getProductId()) < cartLine.getQuantity()) && (shoppingCart.getOrderType() == 'SALES_ORDER')>
							                      	<#assign backOrdered = cartLine.getQuantity() - mktgPkgATPMap.get(cartLine.getProductId())/>
							                      	<span style="color: red; font-size: 15px;">[${backOrdered?if_exists}]</span>
							                    </#if>
						                    </#if>
						                    <#if (availableToPromiseMap.get(cartLine.getProductId()) <= 0) && (shoppingCart.getOrderType() == 'SALES_ORDER') && product.productTypeId! != "DIGITAL_GOOD" && product.productTypeId! != "MARKETING_PKG_AUTO" && product.productTypeId! != "MARKETING_PKG_PICK">
						                      	<span style="color: red;">[${cartLine.getQuantity()}]</span>
						                    <#else>
						                      	<#if (availableToPromiseMap.get(cartLine.getProductId()) < cartLine.getQuantity()) && (shoppingCart.getOrderType() == 'SALES_ORDER') && product.productTypeId != "DIGITAL_GOOD" && product.productTypeId != "MARKETING_PKG_AUTO" && product.productTypeId != "MARKETING_PKG_PICK">
							                        <#assign backOrdered = cartLine.getQuantity() - availableToPromiseMap.get(cartLine.getProductId())/>
							                        <span style="color: red;">[${backOrdered?if_exists}]</span>
						                      	</#if>
						                    </#if>
						                </#if>
					                  	</div>
					                  	<#-- ship before/after date -->
	
		        						<#-- Show Associated Products (not for Variants) -->
		          						
		
						                <#--
						                <#if (cartLine.getIsPromo() && cartLine.getAlternativeOptionProductIds()?has_content)>
						                  	 Show alternate gifts if there are any... 
						                  	<div>${uiLabelMap.OrderChooseFollowingForGift}:</div>
						                  	<#list cartLine.getAlternativeOptionProductIds() as alternativeOptionProductId>
							                    <#assign alternativeOptionProduct = delegator.findOne("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", alternativeOptionProductId), true)>
							                    <#assign alternativeOptionName = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(alternativeOptionProduct, "PRODUCT_NAME", locale, dispatcher)?if_exists>
							                    <div><a href="<@ofbizUrl>setDesiredAlternateGwpProductId?alternateGwpProductId=${alternativeOptionProductId}&amp;alternateGwpLine=${cartLineIndex}</@ofbizUrl>" class="btn btn-info btn-mini">Select: ${alternativeOptionName?default(alternativeOptionProductId)}</a></div>
						                  	</#list>
						                </#if>
						                -->
					            	</td>
					            	<td>
					            		<#-- NOTE: Delivered for serialized inventory means shipped to customer so they should not be displayed here any more -->
					            		<#assign productInventoryItems = delegator.findByAnd("InventoryItemFilterAtpQoh", {"productId" : productId}, ['facilityId', '-datetimeReceived', '-inventoryItemId'], false) />
					            		<#if productInventoryItems?exists && productInventoryItems?has_content && productInventoryItems?size &gt; 0>
					            			<select name="fromInventoryItemId_${cartLineIndex}" style="width:150px; margin-bottom:0px">
					            				<option value=""></option>
						            			<#list productInventoryItems as inventoryItem>
						            				<#if inventoryItem.inventoryItemTypeId?if_exists == "NON_SERIAL_INV_ITEM">
														<#if inventoryItem.curInventoryItemTypeId?exists>
								            				<option value="${(inventoryItem.inventoryItemId)?if_exists}" 
								            					<#if cartLine.getAttribute("fromInventoryItemId")?exists && cartLine.getAttribute("fromInventoryItemId") == inventoryItem.inventoryItemId>selected="selected"</#if>>
								            					<#if inventoryItem.expireDate?exists>${(inventoryItem.expireDate)?string("dd/MM/yyyy")}
								            					<#else>__/__/____</#if>
								            					, 
								            					<#if inventoryItem.inventoryItemTypeId?if_exists == "NON_SERIAL_INV_ITEM">
																	${(inventoryItem.availableToPromiseTotal)?default("NA")}
																	/ ${(inventoryItem.quantityOnHandTotal)?default("NA")}
																</#if>
																,
							            					 	<#if inventoryItem.facilityId?exists>
								            					 	${inventoryItem.facilityId}
							            					 	<#else>
								            					 	<#if inventoryItem.containerId?exists>
								            					 		${inventoryItem.containerId}
								            					 	</#if>
							            					 	</#if>
							            					 	
								            				</option>
							            				</#if>
						            				</#if>
						            			</#list>
						            		</select>
						            	<#else>
						            		<select name="fromInventoryItemId" style="width:150px; margin-bottom:0px" disabled>
					            				<option value=""></option>
					            			</select>
					            		</#if>
					            	</td>
					            <#else>
					            	<td></td>
					            	<td></td>
	    						</#if>
								
					            <#-- gift wrap option -->
	            				<#-- end gift wrap option -->
	            				
					            <td nowrap="nowrap" align="center">
					            <#-- && !cartLine.associatedOrderId?exists && !cartLine.associatedOrderItemSeqId?exists-->
					                <#if (cartLine.getIsPromo()) || cartLine.getShoppingListId()?exists>
					                    ${cartLine.getQuantity()?string.number}
					                <#else>
					                    <input size="6" style="width: 85%; margin-bottom:0px" type="text" name="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}"/>
					                </#if>
					                <#if (cartLine.getSelectedAmount() > 0) >
					                  	<br /><b>${uiLabelMap.OrderAmount}:</b><br />
					                  	<input size="6" type="text" name="amount_${cartLineIndex}" style="margin-bottom:0px" value="${cartLine.getSelectedAmount()?string.number}"/>
					                </#if>
					            </td>
					            <td nowrap="nowrap" align="right">
					                <#if cartLine.getIsPromo() || (shoppingCart.getOrderType() == "SALES_ORDER" && !security.hasEntityPermission("ORDERMGR", "_SALES_PRICEMOD", session))>
					                  	<@ofbizCurrency amount=cartLine.getDisplayPrice() isoCode=currencyUomId/>
					                <#else>
					                    <#if (cartLine.getSelectedAmount() > 0) >
					                        <#assign price = cartLine.getBasePrice() / cartLine.getSelectedAmount()>
					                    <#else>
					                        <#assign price = cartLine.getBasePrice()>
					                    </#if>
					                    <#--<input size="8" style="width: 85%" type="text" name="price_${cartLineIndex}" value="<@ofbizAmount amount=price/>"/>-->
					                    <@ofbizCurrency amount=price isoCode=currencyUomId/>
					                </#if>
					            </td>
	            				<td nowrap="nowrap" align="right"><div><@ofbizCurrency amount=cartLine.getOtherAdjustments() isoCode=currencyUomId/></div></td>
	            				<td nowrap="nowrap" align="right"><div><@ofbizCurrency amount=cartLine.getDisplayItemSubTotal() isoCode=currencyUomId/></div></td>
	            				<td nowrap="nowrap" align="center">
	            					<div>
										<input type="checkbox" name="selectedItem" value="${cartLineIndex}" onclick="javascript:checkToggle(this);"/>
										<span class="lbl"></span>
	            					</div>
	    						</td>
	          				</tr>
						</#list>
				        <#if shoppingCart.getAdjustments()?has_content>
				            <tr><td colspan="8"></td></tr>
			              	<tr>
				                <td colspan="6" nowrap="nowrap" align="right"><div>${uiLabelMap.OrderSubTotal}:</div></td>
				                <td colspan="2" nowrap="nowrap" align="right"><div><@ofbizCurrency amount=shoppingCart.getSubTotal() isoCode=currencyUomId/></div></td>
			              	</tr>
				            <#list shoppingCart.getAdjustments() as cartAdjustment>
				              	<#assign adjustmentType = cartAdjustment.getRelatedOne("OrderAdjustmentType", true)>
				              	<tr>
				                	<td colspan="6" nowrap="nowrap" align="right">
				                  		<div>
				                    		<i>${uiLabelMap.OrderAdjustment}</i> - ${adjustmentType.get("description",locale)?if_exists}
				                    		<#if cartAdjustment.productPromoId?has_content><a href="<@ofbizUrl>showPromotionDetails?productPromoId=${cartAdjustment.productPromoId}</@ofbizUrl>" class="btn btn-info btn-mini">${uiLabelMap.CommonDetails}</a></#if>:
				                  		</div>
				                	</td>
				                	<td colspan="2" nowrap="nowrap" align="right"><div><@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal()) isoCode=currencyUomId/></div></td>
				              	</tr>
				            </#list>
				        </#if>
        			</tbody>
        			
			        <tfoot>
			        	<tr>
				          	<td colspan="6" align="right" valign="bottom">
				            	<div><b>${uiLabelMap.OrderCartTotal}:</b></div>
				          	</td>
				          	<td align="right" valign="bottom" colspan="2">
				            	<div><b><@ofbizCurrency amount=shoppingCart.getGrandTotal() isoCode=currencyUomId/></b></div>
				          	</td>
				        </tr>
			        </tfoot>
			  	</table>
			</form>
	  	<#else>
	    	<div class="alert alert-info open-sans">${uiLabelMap.DANoOrderItemsToDisplay}</div>
	  	</#if>
		</div><!--.widget-main-->
	</div><!--.widget-body-inner-->
	</div><!--.widget-body-->
</div><!--.widget-box-->


