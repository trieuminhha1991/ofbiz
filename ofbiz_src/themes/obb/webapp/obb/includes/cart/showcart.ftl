<script type="text/javascript">
//<![CDATA[
function toggle(e) {
    e.checked = !e.checked;
}
function checkToggle(e) {
    var cform = document.cartform;
    if (e.checked) {
        var len = cform.elements.length;
        var allchecked = true;
        for (var i = 0; i < len; i++) {
            var element = cform.elements[i];
            if (element.name == "selectedItem" && !element.checked) {
                allchecked = false;
            }
            cform.selectAll.checked = allchecked;
        }
    } else {
        cform.selectAll.checked = false;
    }
}
function toggleAll(e) {
    var cform = document.cartform;
    var len = cform.elements.length;
    for (var i = 0; i < len; i++) {
        var element = cform.elements[i];
        if (element.name == "selectedItem" && element.checked != e.checked) {
            toggle(element);
        }
    }
}
function removeSelected() {
    var cform = document.cartform;
    cform.removeSelected.value = true;
    cform.submit();
}
function addToList() {
    var cform = document.cartform;
    cform.action = "<@ofbizUrl>addBulkToShoppingList</@ofbizUrl>";
    cform.submit();
}
function gwAll(e) {
    var cform = document.cartform;
    var len = cform.elements.length;
    var selectedValue = e.value;
    if (selectedValue == "") {
        return;
    }

    var cartSize = ${shoppingCartSize};
    var passed = 0;
    for (var i = 0; i < len; i++) {
        var element = cform.elements[i];
        var ename = element.name;
        var sname = ename.substring(0,16);
        if (sname == "option^GIFT_WRAP") {
            var options = element.options;
            var olen = options.length;
            var matching = -1;
            for (var x = 0; x < olen; x++) {
                var thisValue = element.options[x].value;
                if (thisValue == selectedValue) {
                    element.selectedIndex = x;
                    passed++;
                }
            }
        }
    }
    if (cartSize > passed && selectedValue != "NO^") {
        showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.ObbSelectedGiftWrap}");
    }
    cform.submit();
}
//]]>
</script>
<#assign fixedAssetExist = shoppingCart.containAnyWorkEffortCartItems() />
<div id="jm-container" class="jm-col1-layout wrap not-breadcrumbs clearfix">
	<div class="main clearfix">
		<div id="jm-mainbody" class="clearfix">
			<div id="jm-main">
				<div class="inner clearfix">
					<div id="jm-current-content" class="clearfix">
						<div class="cart">
						<div class="page-title title-buttons">
							<h1>${uiLabelMap.ObbShoppingCart}</h1>
					</div>
					<#if ((sessionAttributes.lastViewedProducts)?has_content && sessionAttributes.lastViewedProducts?size > 0)>
					          <#assign continueLink = "/productmaindetail?product_id=" + sessionAttributes.lastViewedProducts.get(0) />
					        <#else>
					          <#assign continueLink = "/main" />
					        </#if>
					        <div class="block">
							<div style="padding-bottom:10px;">
							        <a href="<@ofbizUrl>${continueLink}</@ofbizUrl>" class="submenutext">${uiLabelMap.ObbContinueShopping}</a><span style="padding-left:5px;padding-right:5px;">|</span>
							        <#if (shoppingCartSize > 0)><a href="<@ofbizUrl>checkoutoptions</@ofbizUrl>" class="submenutext">${uiLabelMap.OrderCheckout}</a><span style="padding-left:5px;padding-right:5px;">|</span><#else><span class="submenutextrightdisabled">${uiLabelMap.OrderCheckout}</span><span style="padding-left:5px;padding-right:5px;">|</span></#if>
							        ${uiLabelMap.CommonQuickAdd}
						        </div>
					            <form method="post" action="<@ofbizUrl>additem<#if requestAttributes._CURRENT_VIEW_?has_content>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="quickaddform" id="quickaddform">
					                <fieldset>
								<ul class="form-list">
									<#-- check if rental data present  insert extra fields in Quick Add-->
							                <#if (product?exists && product.getString("productTypeId") == "ASSET_USAGE") || (product?exists && product.getString("productTypeId") == "ASSET_USAGE_OUT_IN")>
										<li class="fields">
											<div class="customer-name">
													<div class="field name-firstname">
													<label for="reservStart" class="required"><em>*</em>${uiLabelMap.ObbStartDate}</label>
										                    <input type="text" class="input-text required-entry"" size="10" name="reservStart" id="reservStart" value="${requestParameters.reservStart?default("")}" />
											</div>
											<div class="field name-firstname">
										                    <label for="reservLength" class="required"><em>*</em>${uiLabelMap.ObbLength}</label>
										                    <input type="text" class="input-text required-entry"" size="2" name="reservLength" id="reservLength" value="${requestParameters.reservLength?default("")}" />
									                    </div>
									                    <div class="field name-firstname">
												<label for="reservLength" class="required"><em>*</em>${uiLabelMap.OrderNbrPersons}</label>
												<input type="text" class="input-text required-entry"" size="3" name="reservPersons" value="${requestParameters.reservPersons?default("1")}" />
											</div>
							                    </li>
							                </#if>
									<li class="fields">
									<div class="customer-name">
												<div class="field name-firstname">
													<label for="add_product_id" class="required"><em>*</em>${uiLabelMap.ObbProductNumber}</label>
												<input type="text" class="input-text required-entry"" name="add_product_id" id="add_product_id" value="${requestParameters.add_product_id?if_exists}" />
									                </div>
									                <div class="field name-firstname">
										                <label for="quantity" class="required"><em>*</em>${uiLabelMap.CommonQuantity}</label>
												<input type="text" class="input-text required-entry"" size="5" name="quantity" id="quantity" value="${requestParameters.quantity?default("1")}" />
											</div>
										</div>
									</li>
						                </ul>
						                <button type="submit" value="empty_cart" title="${uiLabelMap.OrderAddToCart}" class="button btn-empty" id="empty_cart_button">
									<span><span>${uiLabelMap.OrderAddToCart}</span></span>
								</button>
					                </fieldset>
					            </form>
					            <script type="text/javascript">
								    //<![CDATA[
								        var dataFormTmp = new VarienForm('quickaddform', true);
								    //]]>
								</script>
					        </div>
					        <div style="padding-bottom:5px;border-image: url(/obbresources/skin/frontend/default/default/images/bkg_divider1.gif repeat);">
				              <#--<a href="<@ofbizUrl>main</@ofbizUrl>" class="lightbuttontext">[${uiLabelMap.ObbContinueShopping}]</a>-->
				              <#if (shoppingCartSize > 0)>
				                <a href="javascript:document.cartform.submit();" class="submenutext">${uiLabelMap.ObbRecalculateCart}</a><span style="padding-left:5px;padding-right:5px;">|</span>
				                <a href="<@ofbizUrl>emptycart</@ofbizUrl>" class="submenutext">${uiLabelMap.ObbEmptyCart}</a><span style="padding-left:5px;padding-right:5px;">|</span>
				                <a href="javascript:removeSelected();" class="submenutext">${uiLabelMap.ObbRemoveSelected}</a><span style="padding-left:5px;padding-right:5px;">|</span>
				              <#else>
				                <span class="submenutextdisabled">${uiLabelMap.ObbRecalculateCart}</span>
				                <span class="submenutextdisabled"><span style="padding-left:5px;padding-right:5px;">|</span>${uiLabelMap.ObbEmptyCart}</span>
				                <span class="submenutextdisabled"><span style="padding-left:5px;padding-right:5px;">|</span>${uiLabelMap.ObbRemoveSelected}</span>
				              </#if>
				              <#if (shoppingCartSize > 0)><a href="<@ofbizUrl>checkoutoptions</@ofbizUrl>" class="submenutextright">${uiLabelMap.OrderCheckout}</a><#else><span class="submenutextrightdisabled">${uiLabelMap.OrderCheckout}</span></#if>
					        </div>
					        <#if (shoppingCartSize > 0)>
						<form action="<@ofbizUrl>modifycart</@ofbizUrl>" name="cartform" method="POST">
								<fieldset>
								<input type="hidden" name="removeSelected" value="false" />
						            <table id="shopping-cart-table" class="data-table cart-table">
						                <colgroup>
									<col width="1">
									<col>
									<col>
									        <col width="1">
							                <col width="1">
							                <col width="1">
	                                        <col width="1">
				                            <col width="1">
				                            <col width="1">
			                            </colgroup>
			                            <thead>
								            <tr>
								              <th></th>
								              <th rowspan="1"><span class="nobr">${uiLabelMap.OrderProduct}</span></th>
								              <th rowspan="1"><span class="nobr">${uiLabelMap.OrderProduct}</span></th>
								              <#if asslGiftWraps?has_content && productStore.showCheckoutGiftOptions?if_exists != "N">>
								                <th scope="row">
								                  <select class="selectBox" name="GWALL" onchange="javascript:gwAll(this);">
								                    <option value="">${uiLabelMap.ObbGiftWrapAllItems}</option>
								                    <option value="NO^">${uiLabelMap.ObbNoGiftWrap}</option>
								                    <#list allgiftWraps as option>
								                      <option value="${option.productFeatureId}">${option.description} : ${option.defaultAmount?default(0)}</option>
								                    </#list>
								                  </select>
							                    </th>
								              </#if>
								              <#if fixedAssetExist == true>
								                <td>
								                    <table>
								                        <tr>
								                            <td>- ${uiLabelMap.ObbStartDate} -</td>
								                            <td>- ${uiLabelMap.ObbNbrOfDays} -</td>
								                        </tr>
								                        <tr>
								                            <td >- ${uiLabelMap.ObbNbrOfPersons} -</td>
								                            <td >- ${uiLabelMap.CommonQuantity} -</td>
								                        </tr>
								                    </table>
								                </td>
								              <#else>
								                <th rowspan="1"><span class="nobr">${uiLabelMap.CommonQuantity}</span></th>
								              </#if>
								              <th rowspan="1"><span class="nobr">${uiLabelMap.ObbUnitPrice}</span></th>
								              <th rowspan="1"><span class="nobr">${uiLabelMap.ObbAdjustments}</span></th>
								              <th rowspan="1"><span class="nobr">${uiLabelMap.ObbItemTotal}</span></th>
								              <th rowspan="1"><span class="nobr"><input type="checkbox" name="selectAll" value="0" onclick="javascript:toggleAll(this);" /></span></th>
								            </tr>
								        </thead>
						                <tfoot>
						                    <tr class="first last">
						                        <td colspan="50" class="a-right last">
	                                                <button type="button" <#if (shoppingCartSize == 0)>disabled</#if> title="${uiLabelMap.ObbContinueShopping}" class="button btn-continue" onclick="setLocation('<@ofbizUrl>main</@ofbizUrl>')"><span><span>${uiLabelMap.ObbContinueShopping}</span></span></button>
	                                                <button type="button" style="margin-left:10px;" title="${uiLabelMap.ObbCheckout}" class="button btn-continue" onclick="setLocation('<@ofbizUrl>checkoutoptions</@ofbizUrl>')"><span><span>${uiLabelMap.ObbCheckout}</span></span></button>
	                                                <button type="button" <#if (shoppingCartSize == 0)>disabled</#if> onclick="javascript:document.cartform.submit();" name="update_cart_action" value="update_qty" title="${uiLabelMap.ObbUpdateShoppingCart}" class="button btn-update"><span><span>${uiLabelMap.ObbUpdateShoppingCart}</span></span></button>
									<button type="button" <#if (shoppingCartSize == 0)>disabled</#if> name="update_cart_action" value="empty_cart" onclick="setLocation('<@ofbizUrl>emptycart</@ofbizUrl>')" title="${uiLabelMap.ObbClearShoppingCart}" class="button btn-empty" id="empty_cart_button"><span><span>${uiLabelMap.ObbClearShoppingCart}</span></span></button>
						                            <!--[if lt IE 8]>
						                            <input type="hidden" id="update_cart_action_container" />
						                            <script type="text/javascript">
						                            //<![CDATA[
						                                Event.observe(window, 'load', function()
						                                {
						                                    // Internet Explorer (lt 8) does not support value attribute in button elements
						                                    $emptyCartButton = $('empty_cart_button');
						                                    $cartActionContainer = $('update_cart_action_container');
						                                    if ($emptyCartButton && $cartActionContainer) {
						                                        Event.observe($emptyCartButton, 'click', function()
						                                        {
						                                            $emptyCartButton.setAttribute('name', 'update_cart_action_temp');
						                                            $cartActionContainer.setAttribute('name', 'update_cart_action');
						                                            $cartActionContainer.setValue('empty_cart');
						                                        });
						                                    }

						                                });
						                            //]]>
						                            </script>
									<![endif]-->
									</td>
						                    </tr>
						                </tfoot>
						                <tbody>
									<#assign itemsFromList = false />
									        <#assign promoItems = false />
									        <#list shoppingCart.items() as cartLine>

									          <#assign cartLineIndex = shoppingCart.getItemIndex(cartLine) />
									          <#assign lineOptionalFeatures = cartLine.getOptionalProductFeatures() />
									          <#-- show adjustment info -->
									          <#list cartLine.getAdjustments() as cartLineAdjustment>
									            <!-- cart line ${cartLineIndex} adjustment: ${cartLineAdjustment} -->
									          </#list>
							<tr  id="cartItemDisplayRow_${cartLineIndex}" class="<#if cartLine_index%2==0>odd<#else>even</#if>">
								<td>
										                <#if cartLine.getShoppingListId()?exists>
										                  <#assign itemsFromList = true />
										                  <a href="<@ofbizUrl>editShoppingList?shoppingListId=${cartLine.getShoppingListId()}</@ofbizUrl>" class="linktext">L</a>&nbsp;&nbsp;
										                <#elseif cartLine.getIsPromo()>
										                  <#assign promoItems = true />
										                  <a href="<@ofbizUrl>view/showcart</@ofbizUrl>" class="button">P</a>&nbsp;&nbsp;
										                <#else>
										                  &nbsp;
										                </#if>
										            </td>
												<td>
													<#if cartLine.getProductId()?exists>
														<#-- product item -->
										                    <#-- start code to display a small image of the product -->
										                    <#if cartLine.getParentProductId()?exists>
										                      <#assign parentProductId = cartLine.getParentProductId() />
										                    <#else>
										                      <#assign parentProductId = cartLine.getProductId() />
										                    </#if>
														<#assign smallImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.getProduct(), "MEDIUM_IMAGE_URL", locale, dispatcher)?if_exists />
										                    <#if !smallImageUrl?string?has_content><#assign smallImageUrl = "/images/defaultImage.jpg" /></#if>
										                    <#if smallImageUrl?string?has_content>
										                      <a href="<@ofbizUrl>productmaindetail?product_id=${parentProductId}</@ofbizUrl>" class="product-image">
										                        <img src="<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${smallImageUrl}</@ofbizContentUrl>" alt="Product Image" width="110" height="110"/>
										                      </a>
										                    </#if>
								                    </td>
								                    <td>
													<h2 class="product-name">
										                    <a href="<@ofbizUrl>productmaindetail?product_id=${parentProductId}</@ofbizUrl>">${cartLine.getProductId()} - ${cartLine.getName()?if_exists}</a>
										                </h2>
										                <dl class="item-options">
										                <#-- For configurable products, the selected options are shown -->
										                    <#if cartLine.getConfigWrapper()?exists>
										                      <#assign selectedOptions = cartLine.getConfigWrapper().getSelectedOptions()?if_exists />
										                      <#if selectedOptions?exists>
										                        <#list selectedOptions as option>
										                          <dt>Option</dt>
															<dd>${option.getDescription()}</dd>
										                          </div>
										                        </#list>
										                      </#if>
										                    </#if>
									                    </dl>
										                    <#-- if inventory is not required check to see if it is out of stock and needs to have a message shown about that... -->
										                    <#assign itemProduct = cartLine.getProduct() />
										                    <#assign isStoreInventoryNotRequiredAndNotAvailable = Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryRequiredAndAvailable(request, itemProduct, cartLine.getQuantity(), false, false) />
										                    <#if isStoreInventoryNotRequiredAndNotAvailable && itemProduct.inventoryMessage?has_content>
										                        (${itemProduct.inventoryMessage})
										                    </#if>
											<#else>
										                    <#-- this is a non-product item -->
										                    ${cartLine.getItemTypeDescription()?if_exists}: ${cartLine.getName()?if_exists}
										                </#if>
										                <#assign attrs = cartLine.getOrderItemAttributes()/>
									                    <#if attrs?has_content>
									                      <#assign attrEntries = attrs.entrySet()/>
									                      <ul>
									                      <#list attrEntries as attrEntry>
									                          <li>
									                              ${attrEntry.getKey()} : ${attrEntry.getValue()}
									                          </li>
									                      </#list>
									                      </ul>
									                      <#if (cartLine.getIsPromo() && cartLine.getAlternativeOptionProductIds()?has_content)>
											                  <#-- Show alternate gifts if there are any... -->
											                  <div class="tableheadtext">${uiLabelMap.OrderChooseFollowingForGift}:</div>
											                  <select name="dummyAlternateGwpSelect${cartLineIndex}" onchange="setAlternateGwp(this);" class="selectBox">
											                  <option value="">- ${uiLabelMap.OrderChooseAnotherGift} -</option>
											                  <#list cartLine.getAlternativeOptionProductIds() as alternativeOptionProductId>
											                    <#assign alternativeOptionName = Static["org.ofbiz.product.product.ProductWorker"].getGwpAlternativeOptionName(dispatcher, delegator, alternativeOptionProductId, requestAttributes.locale) />
											                    <option value="<@ofbizUrl>setDesiredAlternateGwpProductId?alternateGwpProductId=${alternativeOptionProductId}&alternateGwpLine=${cartLineIndex}</@ofbizUrl>">${alternativeOptionName?default(alternativeOptionProductId)}</option>
											                  </#list>
											                  </select>
											                </#if>
									                    </#if>
		                                            </td>
												<#-- gift wrap option -->
										            <#assign showNoGiftWrapOptions = false />
										            <#assign giftWrapOption = lineOptionalFeatures.GIFT_WRAP?if_exists />
										            <#assign selectedOption = cartLine.getAdditionalProductFeatureAndAppl("GIFT_WRAP")?if_exists />
										            <#if giftWrapOption?has_content>
									                 <td >
										                <select class="selectBox" name="option^GIFT_WRAP_${cartLineIndex}" onchange="javascript:document.cartform.submit()">
										                  <option value="NO^">${uiLabelMap.ObbNoGiftWrap}</option>
										                  <#list giftWrapOption as option>
										                    <option value="${option.productFeatureId}" <#if ((selectedOption.productFeatureId)?exists && selectedOption.productFeatureId == option.productFeatureId)>selected="selected"</#if>>${option.description} : ${option.amount?default(0)}</option>
										                  </#list>
										                </select>
										              <#elseif showNoGiftWrapOptions>
										                <select class="selectBox" name="option^GIFT_WRAP_${cartLineIndex}" onchange="javascript:document.cartform.submit()">
										                  <option value="">${uiLabelMap.ObbNoGiftWrap}</option>
										                </select>
										              </td>
										             </#if>
										            <#-- end gift wrap option -->
												 <td>
										                <#if cartLine.getIsPromo() || cartLine.getShoppingListId()?exists>
										                       <#if fixedAssetExist == true>
										                        <#if cartLine.getReservStart()?exists>
										                            <table >
										                                <tr>
										                                    <td>&nbsp;</td>
										                                    <td>${cartLine.getReservStart()?string("yyyy-mm-dd")}</td>
										                                    <td>${cartLine.getReservLength()?string.number}</td></tr>
										                                <tr>
										                                    <td>&nbsp;</td>
										                                    <td>${cartLine.getReservPersons()?string.number}</td>
										                                    <td>
										                        <#else>
										                            <table >
										                                <tr>
										                                    <td >--</td>
										                                    <td>--</td>
										                                </tr>
										                                <tr>
										                                    <td>--</td>
										                                    <td>
										                        </#if>
										                        ${cartLine.getQuantity()?string.number}</td></tr></table>
										                    <#else><#-- fixedAssetExist -->
										                        ${cartLine.getQuantity()?string.number}
										                    </#if>
										                <#else><#-- Is Promo or Shoppinglist -->
										                       <#if fixedAssetExist == true><#if cartLine.getReservStart()?exists><table><tr><td>&nbsp;</td><td><input type="text" class="inputBox" size="10" name="reservStart_${cartLineIndex}" value=${cartLine.getReservStart()?string}/></td><td><input type="text" class="inputBox" size="2" name="reservLength_${cartLineIndex}" value="${cartLine.getReservLength()?string.number}"/></td></tr><tr><td>&nbsp;</td><td><input type="text" class="inputBox" size="3" name="reservPersons_${cartLineIndex}" value=${cartLine.getReservPersons()?string.number} /></td><td><#else>
										                           <table><tr><td>--</td><td>--</td></tr><tr><td>--</td><td></#if>
										                        <input name="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}" size="6" title="Qty" class="input-text qty" maxlength="12"></td></tr></table>
										                    <#else><#-- fixedAssetExist -->
										                        <input name="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}" size="6" title="Qty" class="input-text qty" maxlength="12">
										                    </#if>
										                </#if>
										            </td>
											<td>
												<span class="cart-price">
			                                                <span class="price"><@ofbizCurrency amount=cartLine.getDisplayPrice() isoCode=shoppingCart.getCurrency()/></span>
												</span>
											</td>
										            <td>
												<span class="cart-price">
			                                                <span class="price"><@ofbizCurrency amount=cartLine.getDisplayPrice() isoCode=shoppingCart.getCurrency()/></span>
												</span>
										            </td>
											<td>
												<span class="cart-price">
			                                                <span class="price"><@ofbizCurrency amount=cartLine.getDisplayItemSubTotal() isoCode=shoppingCart.getCurrency()/></span>
												</span>
										            </td>
										            <td><#if !cartLine.getIsPromo()><input type="checkbox" name="selectedItem" value="${cartLineIndex}" onclick="javascript:checkToggle(this);" /><#else>&nbsp;</#if></td>
												</tr>
											</#list>
						</tbody>
							</table>
								<script type="text/javascript">decorateTable('shopping-cart-table')</script>
							</fieldset>
						</form>
						<div class="cart-collaterals">
						        <div class="col3-set">
						            <div class="col-1">
								<form name="addpromocodeform" id="addpromocodeform" action="<@ofbizUrl>addpromocode<#if requestAttributes._CURRENT_VIEW_?has_content>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" method="post">
											<fieldset>
												<div class="block block-discount discount">
												    <div class="block-title"><strong><span>${uiLabelMap.ProductPromoCodes}</span></strong></div>
												    <div class="block-content discount-form">
											            <input type="hidden" name="remove" id="remove-coupone" value="0">
											            <div class="input-box">
											                <input class="input-text required-entry" id="productPromoCodeId" name="productPromoCodeId" value="">
											            </div>
											            <div class="buttons-set">
											                <button type="submit" title="${uiLabelMap.OrderAddCode}" class="button" value="Apply Coupon"><span><span>${uiLabelMap.OrderAddCode}</span></span></button>
							                            </div>
											        </div>
											    </div>
										    </fieldset>
										</form>
										<script type="text/javascript">
										    //<![CDATA[
										        var dataFormPromo = new VarienForm('addpromocodeform', true);
										    //]]>
										</script>
						            </div>
						            <div class="col-2">
						                <div class="block block-totals totals">
						                    <div class="block-title"><strong><span>Chi tiết chiết khấu</span></strong></div>
						                    <div class="block-content">
									<#if shoppingCart.getAdjustments()?has_content>
											    <table id="shopping-cart-totals-table">
												        <colgroup>
														<col>
														<col width="1">
												        </colgroup>
												        <tfoot>
												            <tr>
															    <td style="" class="a-right" colspan="1">
															        <strong>${uiLabelMap.ObbCartTotal}</strong>
															    </td>
															    <td style="" class="a-right">
															        <strong><span class="price"><@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/></span></strong>
															    </td>
															</tr>
															<#if itemsFromList>
															<tr>
													          <td colspan="2">L - ${uiLabelMap.ObbItemsfromShopingList}.</td>
													        </tr>
													        </#if>
													        <#if promoItems>
													        <tr>
													          <td colspan="2">P - ${uiLabelMap.ObbPromotionalItems}.</td>
													        </tr>
													        </#if>
												        </tfoot>
												        <tbody>
														<tr>
															    <td style="" class="a-right" colspan="1">
															        <strong>${uiLabelMap.CommonSubTotal}</strong>
															    </td>
															    <td style="" class="a-right">
															        <strong><span class="price"><@ofbizCurrency amount=shoppingCart.getDisplaySubTotal() isoCode=shoppingCart.getCurrency()/></span></strong>
															    </td>
															</tr>
															<#if (shoppingCart.getDisplayTaxIncluded() > 0.0)>
												              <tr>
												                <td style="" class="a-right" colspan="1"><strong>${uiLabelMap.OrderSalesTaxIncluded}</strong></td>
												                <td style="" class="a-right"><strong><span class="price"><@ofbizCurrency amount=shoppingCart.getDisplayTaxIncluded() isoCode=shoppingCart.getCurrency()/></span></strong></td>
												              </tr>
												            </#if>
												            <#list shoppingCart.getAdjustments() as cartAdjustment>
												              <#assign adjustmentType = cartAdjustment.getRelatedOne("OrderAdjustmentType", true) />
												              <tr>
												                <td style="" class="a-right" colspan="1">
												                    ${uiLabelMap.ObbAdjustment} - ${adjustmentType.get("description",locale)?if_exists}
												                    <#if cartAdjustment.productPromoId?has_content><a href="<@ofbizUrl>showPromotionDetails?productPromoId=${cartAdjustment.productPromoId}</@ofbizUrl>" class="button">${uiLabelMap.CommonDetails}</a></#if>
												                </td>
												                <td style="" class="a-right"><span class="price"><@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal()) isoCode=shoppingCart.getCurrency()/></span></td>
												              </tr>
												            </#list>
												        </tbody>
												    </table>
												    <ul class="checkout-types">
												        <li>
														<button type="button" title="${uiLabelMap.OrderCheckout}" class="button btn-proceed-checkout btn-checkout" onclick="window.location='<@ofbizUrl>checkoutoptions</@ofbizUrl>';"><span><span>${uiLabelMap.OrderCheckout}</span></span></button>
														</li>
											</ul>
										<#else>
											Bạn chưa có chiết khấu.
								</#if>
					                    </div>
						                </div>
									</div>
					            </div>
				            </div>
				            <#if (shoppingCartSize?default(0) > 0)>
						  <br />
							  ${screens.render("component://obb/widget/CartScreens.xml#promoUseDetailsInline")}
							</#if>
				            <#if associatedProducts?has_content>
					            <div class="crosssell">
								    <h2>${uiLabelMap.ObbYouMightAlsoIntrested}:</h2>
								    <ul id="crosssell-products-list" class="products-grid">
									<#list associatedProducts as assocProduct>
								            <div>
								                ${setRequestAttribute("optProduct", assocProduct)}
								                ${setRequestAttribute("listIndex", assocProduct_index)}
								                ${screens.render("component://obb/widget/CatalogScreens.xml#productsummarymini")}
								            </div>
								        </#list>
								    </ul>
							    </div>
						    </#if>
					</#if>
					</div>
				</div>
				</div>
			</div>
		</div>
	</div>
</div>
<#-- FIXME include with full support -->