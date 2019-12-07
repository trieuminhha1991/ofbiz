

<#assign cart = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getCartObject(request) />


<#--
<div class="widget-box olbius-extra">
	<div class="widget-header widget-header-small header-color-blue2">
    	<h6>Tao moi Don hang ban ra
			--- Buoc 1: Nhap thông tin ve don hang</h6>
    	<div class="widget-toolbar">
    		<a href="#" data-action="collapse"><i class="icon-chevron-up"></i></a>
    	</div>
    </div>
    <div class="widget-body">
		<div class="widget-body-inner" style="display: block;">
			
			
		</div>
    </div>
</div>
 -->


<div class="widget-box olbius-extra ">
	<div class="widget-header widget-header-small header-color-blue2">
		<h6>${uiLabelMap.DACreateNewOrder} --- ${uiLabelMap.DAStepTwo}: ${uiLabelMap.DAInputAddProduct}</h6>
        <div class="widget-toolbar">
        	<a href="#" data-action="collapse"><i class="icon-chevron-up"></i></a>
        </div>
	</div>
	
    <div class="widget-body">
		<div class="widget-body-inner">
			<div class="widget-main">
			
			
			
			<div class="form-horizontal desc" style="padding-top:10px; padding-bottom:10px">
				<div class="span5">
					<div class="control-group">
						<label class="control-label" for="email">${uiLabelMap.DAOrderName}:</label>
						<div class="controls"><div class="span4">${cart.getOrderName()?if_exists}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label" for="email">${uiLabelMap.DAOrderId}:</label>
						<div class="controls"><div class="span4">${cart.getOrderId()?if_exists}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label" for="email">${uiLabelMap.DACurrency}:</label>
						<div class="controls"><div class="span4">${cart.getCurrency()?if_exists}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label" for="email">${uiLabelMap.DACreateOrderDate}:</label>
						<div class="controls"><div class="span4">${cart.getOrderDate()?if_exists}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label" for="email">${uiLabelMap.DADesiredDeliveryDate}:</label>
						<div class="controls"><div class="span4">${cart.getShipBeforeDate()?if_exists}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label" for="email">${uiLabelMap.DAProductStore}:</label>
						<div class="controls"><div class="span4">${cart.getProductStoreId()?if_exists}&nbsp;</div></div>
					</div>
				</div><!-- .span4 -->
				<div class="span5">
					<div class="control-group">
						<label class="control-label" for="email">${uiLabelMap.DAPersonCreate}:</label>
						<div class="controls"><div class="span4">${parameters.userLogin.userLoginId}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label" for="email">${uiLabelMap.DADistributor}:</label>
						<div class="controls"><div class="span4">${cart.getPartyId()?if_exists}&nbsp;</div></div>
					</div>
					<div class="control-group">
						<label class="control-label" for="email">${uiLabelMap.DAAddressDistributor}:</label>
						<div class="controls"><div class="span4">...</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="email">${uiLabelMap.DADebt}:</label>
						<div class="controls"><div class="span4">...</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="email">${uiLabelMap.DASup}:</label>
						<div class="controls"><div class="span4">...</div>
						</div>
					</div>
				</div><!-- .span4 -->
				<div style="clear:both"></div>
			</div>
			
			
			
			<hr />
			
				<div>
		          <#if quantityOnHandTotal?exists && availableToPromiseTotal?exists && (productId)?exists>
		            <ul>
		              <li>
		                <label>${uiLabelMap.ProductQuantityOnHand}</label>: ${quantityOnHandTotal}
		              </li>
		              <li>
		                <label>${uiLabelMap.ProductAvailableToPromise}</label>: ${availableToPromiseTotal}
		              </li>
		            </ul>
		          </#if>
		        </div>
		        
				<script language="JavaScript" type="text/javascript">
				    function showQohAtp() {
				        document.qohAtpForm.productId.value = document.quickaddform.add_product_id.value;
				        document.qohAtpForm.submit();
				    }
				    function quicklookupGiftCertificate() {
				        window.location='AddGiftCertificate';
				    }
				</script>
				<#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
				  <#assign target="productAvailabalityByFacility">
				<#else>
				  <#assign target="getProductInventoryAvailable">
				</#if>
				
				
			
				<table border="0" cellspacing="0" cellpadding="0">
					<tbody>
						<tr>
						  <td>
							<form name="qohAtpForm" method="post" action="<@ofbizUrl>${target}</@ofbizUrl>">
							  	<fieldset>
				                	<input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
					                <input type="hidden" name="productId"/>
					                <input type="hidden" id="ownerPartyId" name="ownerPartyId" value="${shoppingCart.getBillToCustomerPartyId()?if_exists}" />
							  	</fieldset>
							</form>
							
							<form method="post" class="form-horizontal mini" action="<@ofbizUrl>additem</@ofbizUrl>" name="quickaddform" style="margin: 0;">
								<div class="span5">
									<div class="control-group">
										<label class="control-label" for="add_product_id">${uiLabelMap.DAProductId}:</label>
										<div class="controls">
											<#if orderType=="PURCHASE_ORDER">
					                        	<#if partyId?has_content>                                               
					                          		<#assign fieldFormName="LookupSupplierProduct?partyId=${partyId}">
					                        	<#else>
					                          		<#assign fieldFormName="LookupSupplierProduct">
					                        	</#if>
					                      	<#else>
					                        	<#assign fieldFormName="LookupProduct">
					                      	</#if>
					                      	
					                      	<@htmlTemplate.lookupField formName="quickaddform" name="add_product_id" id="add_product_id" fieldFormName="${fieldFormName}" className="span3"/>
				                      		
					                      	<#-- 
					                      	<a href="javascript:quicklookup(document.quickaddform.add_product_id)" class="btn btn-primary btn-mini">${uiLabelMap.OrderQuickLookup}</a>
				                      		<a href="javascript:quicklookupGiftCertificate()" class="btn btn-primary btn-mini">${uiLabelMap.OrderAddGiftCertificate}</a>
					                      	<#if "PURCHASE_ORDER" == shoppingCart.getOrderType()>
					                        	<a href="javascript:showQohAtp()" class="btn btn-primary btn-mini">${uiLabelMap.ProductAtpQoh}</a>
					                      	</#if>
					                      	-->
										</div>
									</div>
									
									<div class="control-group">
										<label class="control-label" for="quantity">${uiLabelMap.DAQuantity}:</label>
										<div class="controls">
											<div>
												<input type="text" size="6" name="quantity" id="quantity" value="" class="span3">
											</div>
										</div>
									</div>
									
									<#--
									<div class="control-group">
										<label class="control-label">Kho:</label>
										<div class="controls">
											<div>
												<select name="productStoreFacilityId" class="span3">
									                <#assign currentStoreFacility = "">
									                
									                <#list productStoreFacilities as productStoreFacility>
									                  	<option value="${productStoreFacility.facilityId}"<#if productStoreFacility.facilityId == currentStoreFacility> selected="selected"</#if>>
									                  		${productStoreFacility.facilityId?if_exists}
									                  		<#if productStoreFacility.facilityName?exists> : ${productStoreFacility.facilityName?if_exists}</#if></option>
									                </#list>
								              	</select>
											</div>
										</div>
									</div>
									 -->
									
								</div>
								<div class="span5">
									<div class="control-group">
										<label class="control-label" for="itemComment">${uiLabelMap.DAComment}:</label>
										<div class="controls">
											<div style="display:inline-block">
												<div class="input-prepend">
													<textarea rows="3" class="span3" id="itemComment" name="itemComment" style="margin-top:0"></textarea>
												</div>
												<div>
													<input type="checkbox" name="useAsDefaultComment" value="true" <#if useAsDefaultComment?exists>checked="checked"</#if> />
													<span class="lbl"> ${uiLabelMap.DASetDefaultValueForOrder}</span>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div style="clear:both">
								</div>
								
								<div class="control-group">
									<label class="control-label" for="date">&nbsp;</label>
									<div class="controls">
										<div style="display:inline-block; vertical-align:bottom; margin-left:30px">
											<button class="btn btn-primary btn-small" type="submit">
												<i class="icon-ok"></i>${uiLabelMap.DAAddToOrder}
											</button>
										</div>
									</div>
								</div>
							</form>
						  </td>
						</tr>
					</tbody>
				</table>
	  
				<div class="row-fluid wizard-actions">
					<button class="btn btn-small btn-primary" onclick="window.location='<@ofbizUrl>neworder</@ofbizUrl>'">
						<i class="icon-angle-left"></i> ${uiLabelMap.DABack}</button>
					<#--
					<button class="btn btn-small btn-primary" onclick="window.location=''">
						Tiep tuc <i class="icon-angle-right icon-on-right"></i></button>
					 -->
					
					<a class="btn btn-small btn-primary <#if (shoppingCartSize <= 0)>disabled" style="pointer-events: none; cursor: default;"<#else>"</#if> href="<@ofbizUrl>checkout</@ofbizUrl>">
						${uiLabelMap.DANext} <i class="icon-angle-right icon-on-right"></i>
					</a>
					
				</div>
				
				<hr />
	  
				<div style="width:100%">
					<h6 style="font-weight:bold; display:inline-block">Danh sach san pham trong don hang</h6>
					
					<div style="display:inline-block; width:auto; float:right">
						<#if (shoppingCartSize > 0)>
						<a class="btn btn-mini btn-primary" href="javascript:document.cartform.submit()">${uiLabelMap.DARecalculateOrder}</a>
						<a class="btn btn-mini btn-primary" href="javascript:removeSelected();">${uiLabelMap.DARemoveSelected}</a>
						</#if>
						<a class="btn btn-mini btn-primary" href="<@ofbizUrl>emptycart</@ofbizUrl>">${uiLabelMap.DAClearOrder}</a>
					</div>
					
				</div>
				<div style="clear:both"></div>
				
				
				<#-- Continuation of showcart.ftl:  List of order items and forms to modify them. -->
				<#macro showAssoc productAssoc>
				  	<#assign productAssocType = (delegator.findOne("ProductAssocType", {"productAssocTypeId" : productAssoc.productAssocTypeId}, false))/>
				  	<#assign assocProduct = (delegator.findOne("Product", {"productId" : productAssoc.productIdTo}, false))/>
				  	<#if assocProduct?has_content>
					    <td style="border-left: 0px !important"><a href="<@ofbizUrl>/product?product_id=${productAssoc.productIdTo}</@ofbizUrl>" class="btn btn-info btn-mini">${productAssoc.productIdTo}</a></td>
					    <td>- ${(assocProduct.productName)?if_exists}<i>(${(productAssocType.description)?default("Unknown")})</i></td>
				  	</#if>
				</#macro>
				
			<#if (shoppingCartSize > 0)>
				<form method="post" action="<@ofbizUrl>modifycart</@ofbizUrl>" name="cartform" style="margin: 0;">
					<input type="hidden" name="removeSelected" value="false">
					<div style="overflow:auto; overflow-y:hidden">
						<table cellspacing="0" cellpadding="1" border="0" class="table table-striped dataTable table-hover table-bordered bold-head-first">
							<thead>
								<tr>
									<td rowspan="2">${uiLabelMap.DANo}</td>
									<td rowspan="2">${uiLabelMap.DACategory}</td>
									<td rowspan="2">
										${uiLabelMap.DAProduct}<br />
										<span style="font-weight:normal">(${uiLabelMap.DAProductId}, ${uiLabelMap.DAName}, ${uiLabelMap.DAFacility})</span>
									</td>
									<td rowspan="2">${uiLabelMap.DABarcode}</td>
									<td rowspan="2" style="width:10px">${uiLabelMap.DAPackingPerTray}</td>
									<td align="center" colspan="3" style="text-align:center">${uiLabelMap.DAQuantity}</td>
									<td rowspan="2">${uiLabelMap.DASumTray}</td>
									<td align="right" rowspan="2">${uiLabelMap.DAPrice}</td>
									<td align="right" rowspan="2">${uiLabelMap.DAAdjustment}</td>
									<td align="right" rowspan="2">${uiLabelMap.DASubTotalBeforeVAT}</td>
									<td align="center" rowspan="2" class="footcol"><input type="checkbox" name="selectAll" value="0" onclick="javascript:toggleAll(this);" /></td>
								</tr>
								<tr>
									<td>${uiLabelMap.DAOrdered}</td>
									<td>${uiLabelMap.DAPromos}</td>
									<td>${uiLabelMap.DASum}</td>
								</tr>
							</thead>
						  
							<tbody>
							<#assign itemsFromList = false>
					        <#list shoppingCart.items() as cartLine>
					          	<#assign cartLineIndex = shoppingCart.getItemIndex(cartLine)>
					          	<#assign lineOptionalFeatures = cartLine.getOptionalProductFeatures()>
					          	
								<tr valign="top">
									<td>
										${cartLineIndex + 1}
									</td>
									<td>
										<#assign productCategoryMembers = delegator.findByAnd("ProductCategoryMember", {"productId" : cartLine.getProductId()}, Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceNum", "productCategoryId"), false) />
										<#list productCategoryMembers as productCategoryMember>
											<#assign categoryName = delegator.findOne("ProductCategory", {"productCategoryId" : productCategoryMember.productCategoryId}, false)>
											<#if categoryName?exists>
												${categoryName.categoryName?if_exists}
											</#if>
										</#list>
									</td>
									<td>
										<div>
											<#if cartLine.getProductId()?exists>
							                	<#-- product item -->
							                    
												<a href="<@ofbizUrl>product?product_id=${cartLine.getProductId()}</@ofbizUrl>"
													 style="margin-bottom:0;">${cartLine.getProductId()}</a> - ${cartLine.getName()?default("")}
												
												<#-- inventory summary -->
									            <#if cartLine.getProductId()?exists>
									              	<#assign productId = cartLine.getProductId()>
									              	<#assign product = cartLine.getProduct()>
									              
									              	<div style="font-size:10pt; border-top:dashed 1px #ccc; margin-top:3px; padding-top:5px">
									              		<#assign facilityList = delegator.findList("ProductFacility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, cartLine.getProductId()), null, null, null, false)>
									              		<#assign facilityIterator = facilityList.iterator()>
									              		<#if facilityIterator?exists>
									              			<select style="width:250px">
										              		<#list facilityIterator as facility>
										              			<#assign resultOutput = dispatcher.runSync("getInventoryAvailableByFacility", {'productId' : cartLine.getProductId(), 'facilityId' : facility.facilityId})>
										              			<#assign totalAvailableToPromise = resultOutput.availableToPromiseTotal />
										              			<#assign totalQuantityOnHand = resultOutput.quantityOnHandTotal />
										              			
										              			<option value="${facility.facilityId?if_exists}">
										              				${facility.facilityId?if_exists} (ATP: ${totalAvailableToPromise?if_exists}, QOH: ${totalQuantityOnHand?if_exists})
										              			</option>
															</#list>
															</select>
									              		</#if>
									              		<a href="/catalog/control/EditProductInventoryItems?productId=${productId}" target="_blank">Details</a>
									              	</div>
									            </#if>
												
												<#if cartLine.getItemComment()?has_content>
												<div style="font-size:10pt; font-style:italic; border-top:dashed 1px #ccc;">
									            	Binh luan : ${cartLine.getItemComment()?if_exists}
									            </div>
									            </#if>
												
											<#else>
												<#-- this is a non-product item -->
                    							<b>${cartLine.getItemTypeDescription()?if_exists}</b> : ${cartLine.getName()?if_exists}
											</#if>
										</div>
									
										<!--
											<div style="display:inline-block">
												Gui sau ngay:
												<div class="input-prepend">
													<input class="span2 date-picker" id="id-date-picker-1" type="text" data-date-format="dd-mm-yyyy" />
													<span class="add-on">
														<i class="icon-calendar"></i>
													</span>
												</div>
											</div>
											
											<div style="display:inline-block">
												Gui truoc ngay:
												<div class="input-prepend">
													<input class="span2 date-picker" id="id-date-picker-1" type="text" data-date-format="dd-mm-yyyy" />
													<span class="add-on">
														<i class="icon-calendar"></i>
													</span>
												</div>
											</div>
										-->
									</td>
									
									<!-- <td nowrap="nowrap" align="right">&#160;</td> -->
									<td>&nbsp;</td>
									
									<#assign quycach = 6>
									<td>${quycach}</td>
									
									<#-- quantity -->
									<td nowrap="nowrap" align="center">
										<#if cartLine.getIsPromo() || cartLine.getShoppingListId()?exists>
						                    ${cartLine.getQuantity()?string.number}
						                <#else>
						                    <input size="6" style="width: 85%" type="text" name="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}"/>
						                </#if>
						                <#if (cartLine.getSelectedAmount() > 0) >
						                  <br /><b>${uiLabelMap.OrderAmount}:</b><br /><input size="6" type="text" name="amount_${cartLineIndex}" value="${cartLine.getSelectedAmount()?string.number}"/>
						                </#if>
									</td>
									
									<td nowrap="nowrap" align="center">
										0
									</td>
									
									<td>
										${cartLine.getQuantity()?string?number}
						                <#if (cartLine.getSelectedAmount() > 0) >
						                  <br /><b>${uiLabelMap.OrderAmount}:</b><br />${cartLine.getSelectedAmount()?string.number}
						                </#if>
					                </td>
					                
					                <#assign khay = cartLine.getQuantity()?string?number / quycach>
									<td>${khay?string(",##0.00")}</td>
									
									<td nowrap="nowrap" align="right">
										<#if cartLine.getIsPromo() || (shoppingCart.getOrderType() == "SALES_ORDER" && !security.hasEntityPermission("ORDERMGR", "_SALES_PRICEMOD", session))>
						                  	<@ofbizCurrency amount=cartLine.getDisplayPrice() isoCode=currencyUomId/>
						                <#else>
						                    <#if (cartLine.getSelectedAmount() > 0) >
						                        <#assign price = cartLine.getBasePrice() / cartLine.getSelectedAmount()>
						                    <#else>
						                        <#assign price = cartLine.getBasePrice()>
						                    </#if>
						                    <#-- <@ofbizAmount amount=price/> -->
						                    <@ofbizCurrency amount=price isoCode=currencyUomId/>
						                </#if>
									</td>
									<td nowrap="nowrap" align="right">
										<div><@ofbizCurrency amount=cartLine.getOtherAdjustments() isoCode=currencyUomId/></div>
									</td>
									<td nowrap="nowrap" align="right">
										<div><@ofbizCurrency amount=cartLine.getDisplayItemSubTotal() isoCode=currencyUomId/></div>
									</td>
									<td nowrap="nowrap" align="center" class="footcol">
										<div>
											<#if !cartLine.getIsPromo()>
												<input type="checkbox" name="selectedItem" onclick="javascript:checkToggle(this);"/>
											<#else>&nbsp;</#if>
											<span class="lbl">&nbsp;</span>
						            	</div>
									</td>
								</tr>
								</#list>
								
								
								<tr>
								  <td colspan="11" align="right" valign="bottom">
									<div>
									  <b>Tong cong gio hang:</b>
									</div>
								  </td>
								  <td align="right" valign="bottom">
									<div>
									  <b><@ofbizCurrency amount=shoppingCart.getGrandTotal() isoCode=currencyUomId/></b>
									</div>
								  </td>
								  <td>&nbsp;</td>
								</tr>
							</tbody>
						</table>
					</div>
				</form>
			<#else>
		    	<div class="alert alert-info">${uiLabelMap.OrderNoOrderItemsToDisplay}</div>
		  	</#if>
				
			</div>
		</div>
    </div>
</div>








