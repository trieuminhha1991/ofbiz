										<form method="post" action="<@ofbizUrl>modifycart</@ofbizUrl>" name="cartform">
											<input type="hidden" name="removeSelected" value="true" />
											<a href="http://demo.yithemes.com/bazar/cart/" class="cart_control">
												View Cart
											</a>
												<div class="cart_wrapper" style="display: none;">
													<div class="widget_shopping_cart_content">
														<ul class="cart_list product_list_widget ">
	// IN HERE

															<#list shoppingCart.items() as cartLine>

															<li>
																<a href="http://demo.yithemes.com/bazar/shop/bag/">
																	<#assign smallImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.getProduct(), "SMALL_IMAGE_URL", locale, dispatcher)?if_exists />
														<#if !smallImageUrl?string?has_content>
															<#assign smallImageUrl = "/images/defaultImage.jpg" />
														</#if>
														<#if smallImageUrl?string?has_content>
															<img width="100" height="80" src="<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${smallImageUrl}</@ofbizContentUrl>" alt="Product Image" class="attachment-shop_thumbnail wp-post-image" />
														</#if>
																</a>
																<a href="http://demo.yithemes.com/bazar/shop/bag/" class="name">
																	<#if cartLine.getProductId()?exists>
																		<#assign itemName = cartLine.getName()>
																		<#if itemName?length &gt; 15>
																			<#assign itemName = itemName?substring(0, 14) + " ...">
																		</#if>
															<#if cartLine.getParentProductId()?exists>
																<a href="<@ofbizCatalogAltUrl productId=cartLine.getParentProductId()/>" class="invarseColor">${itemName}</a>
															<#else>
																<a href="<@ofbizCatalogAltUrl productId=cartLine.getProductId()/>" class="invarseColor">${itemName}</a>
															</#if>
															<#else>
															<strong>${cartLine.getItemTypeDescription()?if_exists}</strong>
															</#if>
															<#if cartLine.getIsPromo()><br/><strong>${uiLabelMap.BigshopPromo}<strong></#if>
																</a>
																<#if !cartLine.getIsPromo()>
																	<input type="checkbox" name="selectedItem" value="${cartLineIndex}" />
																<#else>
																	&nbsp;
																</#if>
																<a href="javascript:removeSelected();" class="remove_item" title="Remove this item">
																	remove
																</a>
																<span class="quantity">
																	${cartLine.getQuantity()?string.number} �
																	<span class="amount">
																		<@ofbizCurrency amount=cartLine.getBasePrice() isoCode=shoppingCart.getCurrency()/>
																	 </span>
																 </span>
																<div class="border clear"></div>


															</li>

															</#list>
														</ul><!-- end product list -->

														<p class="total">${uiLabelMap.ObbCartTotal}:
															<span class="amount">
																<@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/>
															</span>
														</p>
														<p class="buttons">
															<a href="<@ofbizUrl>view/showcart</@ofbizUrl>" class="button">${uiLabelMap.OrderViewCart}</a>
															<a href="<@ofbizUrl>checkoutoptions</@ofbizUrl>" class="button">${uiLabelMap.OrderCheckout}</a>
														</p>
													</div>
												</div>
										</form>




























<div class="cartbutton" style="z-index:100">
          <a href="<@ofbizUrl>main</@ofbizUrl>" class="button">${uiLabelMap.ObbContinueShopping}</a>
          <#if (shoppingCartSize > 0)>
            <a href="javascript:document.cartform.submit();" class="button">${uiLabelMap.ObbRecalculateCart}</a>
            <a href="<@ofbizUrl>emptycart</@ofbizUrl>" class="button">${uiLabelMap.ObbEmptyCart}</a>
            <a href="javascript:removeSelected();" class="button">${uiLabelMap.ObbRemoveSelected}</a>
            <a href="<@ofbizUrl>checkoutoptions</@ofbizUrl>" class="button">${uiLabelMap.OrderCheckout}</a>
          </#if>
    </div>



    <div>


<form method="post" action="<@ofbizUrl>modifycart</@ofbizUrl>" name="cartform">
      <div class="cart-info">
      <input type="hidden" name="removeSelected" value="false" />
      <table>
        <thead>
            <tr>
              <td class="image" scope="row">${uiLabelMap.BigshopImage}</td>
              <td class="name" scope="row">${uiLabelMap.OrderProduct}</td>

              <td class="price" scope="row">${uiLabelMap.ObbUnitPrice}</td>
              <td class="price" scope="row">${uiLabelMap.ObbAdjustments}</td>
              <td class="total" scope="row">${uiLabelMap.ObbItemTotal}</td>
              <td class="total" scope="row"><input type="checkbox" name="selectAll" value="0" onclick="javascript:toggleAll(this);" /></td>
            </tr>
        </thead>
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

          <tr id="cartItemDisplayRow_${cartLineIndex}">
                  <#if cartLine.getProductId()?exists>
                    <#-- product item -->
                    <#-- start code to display a small image of the product -->
                    <#if cartLine.getParentProductId()?exists>
                      <#assign parentProductId = cartLine.getParentProductId() />
                    <#else>
                      <#assign parentProductId = cartLine.getProductId() />
                    </#if>
                    <#assign smallImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.getProduct(), "SMALL_IMAGE_URL", locale, dispatcher)?if_exists />
                    <#if !smallImageUrl?string?has_content><#assign smallImageUrl = "/images/defaultImage.jpg" /></#if>
                    <#if smallImageUrl?string?has_content>
            <td class="image">
                      <a href="<@ofbizCatalogAltUrl productId=parentProductId/>">
                        <img src="<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${smallImageUrl}</@ofbizContentUrl>" alt="Product Image" class="imageborder" />
                      </a>
            </td>
                    <#else>
                     <td></td>
                    </#if>
                    <#-- end code to display a small image of the product -->
            <td class="name">
                    <#-- ${cartLineIndex} - -->
                    <a href="<@ofbizCatalogAltUrl productId=parentProductId/>" class="linktext">${cartLine.getProductId()} -
                    ${cartLine.getName()?if_exists}</a> : ${cartLine.getDescription()?if_exists}
                    <#-- For configurable products, the selected options are shown -->
                    <#if cartLine.getConfigWrapper()?exists>
                      <#assign selectedOptions = cartLine.getConfigWrapper().getSelectedOptions()?if_exists />
                      <#if selectedOptions?exists>
                        <div>&nbsp;</div>
                        <#list selectedOptions as option>
                          <div>
                            ${option.getDescription()}
                          </div>
                        </#list>
                      </#if>
                    </#if>

                    <#-- if inventory is not required check to see if it is out of stock and needs to have a message shown about that... -->
                    <#assign itemProduct = cartLine.getProduct() />
                    <#assign isStoreInventoryNotRequiredAndNotAvailable = Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryRequiredAndAvailable(request, itemProduct, cartLine.getQuantity(), false, false) />
                    <#if isStoreInventoryNotRequiredAndNotAvailable && itemProduct.inventoryMessage?has_content>
                        (${itemProduct.inventoryMessage})
                    </#if>

                  <#else>
                    <#-- this is a non-product item -->
            <td>${cartLine.getItemTypeDescription()?if_exists}: ${cartLine.getName()?if_exists}
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
                  </#if>
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
                  <#-- this is the old way, it lists out the options and is not as nice as the drop-down
                  <ul>
                  <#list cartLine.getAlternativeOptionProductIds() as alternativeOptionProductId>
                    <#assign alternativeOptionName = Static["org.ofbiz.product.product.ProductWorker"].getGwpAlternativeOptionName(delegator, alternativeOptionProductId, requestAttributes.locale) />
                    <li><a href="<@ofbizUrl>setDesiredAlternateGwpProductId?alternateGwpProductId=${alternativeOptionProductId}&alternateGwpLine=${cartLineIndex}</@ofbizUrl>" class="button">Select: ${alternativeOptionName?default(alternativeOptionProductId)}</a></li>
                  </#list>
                  </ul>
                  -->
                </#if>

            <br/>
                <#if cartLine.getShoppingListId()?exists>
                  <#assign itemsFromList = true />
                  <a href="<@ofbizUrl>editShoppingList?shoppingListId=${cartLine.getShoppingListId()}</@ofbizUrl>" class="linktext">L</a>&nbsp;&nbsp;
                <#elseif cartLine.getIsPromo()>
                  <#assign promoItems = true />
                  <a href="<@ofbizUrl>view/showcart</@ofbizUrl>" class="button">${uiLabelMap.BigshopPromo}</a>&nbsp;&nbsp;
                <#else>
                  &nbsp;
                </#if>
            </td>
            <#-- gift wrap option -->
            <#assign showNoGiftWrapOptions = false />
            <td >
              <#assign giftWrapOption = lineOptionalFeatures.GIFT_WRAP?if_exists />
              <#assign selectedOption = cartLine.getAdditionalProductFeatureAndAppl("GIFT_WRAP")?if_exists />
              <#if giftWrapOption?has_content>
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
              <#else>
                &nbsp;
              </#if>
            </td>
            <#-- end gift wrap option -->

            <td class="quantity">
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

                </#if>
            </td>
            <td class="price"><@ofbizCurrency amount=cartLine.getDisplayPrice() isoCode=shoppingCart.getCurrency()/></td>
            <td class="price"><@ofbizCurrency amount=cartLine.getOtherAdjustments() isoCode=shoppingCart.getCurrency()/></td>
            <td class="total"><@ofbizCurrency amount=cartLine.getDisplayItemSubTotal() isoCode=shoppingCart.getCurrency()/></td>
            <td class="total"><#if !cartLine.getIsPromo()><input type="checkbox" name="selectedItem" value="${cartLineIndex}" onclick="javascript:checkToggle(this);" /><#else>&nbsp;</#if></td>
          </tr>
        </#list>
    </tbody>
    </table>
    <#--

    <table>
        <#if shoppingCart.getAdjustments()?has_content>
            <tr>
              <td><b>${uiLabelMap.CommonSubTotal}:</b></td>
              <td><@ofbizCurrency amount=shoppingCart.getDisplaySubTotal() isoCode=shoppingCart.getCurrency()/></td>
              <td>&nbsp;</td>
            </tr>
            <#if (shoppingCart.getDisplayTaxIncluded() > 0.0)>
              <tr>
                <th>${uiLabelMap.OrderSalesTaxIncluded}:</th>
                <td><@ofbizCurrency amount=shoppingCart.getDisplayTaxIncluded() isoCode=shoppingCart.getCurrency()/></td>
                <td>&nbsp;</td>
              </tr>
            </#if>
            <#list shoppingCart.getAdjustments() as cartAdjustment>
              <#assign adjustmentType = cartAdjustment.getRelatedOne("OrderAdjustmentType", true) />
              <tr>
                <td>
                    ${uiLabelMap.ObbAdjustment} - ${adjustmentType.get("description",locale)?if_exists}
                    <#if cartAdjustment.productPromoId?has_content><a href="<@ofbizUrl>showPromotionDetails?productPromoId=${cartAdjustment.productPromoId}</@ofbizUrl>" class="button">${uiLabelMap.CommonDetails}</a></#if>:
                </td>
                <td><@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal()) isoCode=shoppingCart.getCurrency()/></td>
                <td>&nbsp;</td>
              </tr>
            </#list>
        </#if>
        <tr>
          <td><b>${uiLabelMap.ObbCartTotal}:</b></td>
          <td><@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/></td>
        </tr>
        <#if itemsFromList>
        <tr>
          <td>L - ${uiLabelMap.ObbItemsfromShopingList}.</td>
        </tr>
        </#if>
        <#if promoItems>
        <tr>
          <td>P - ${uiLabelMap.ObbPromotionalItems}.</td>
        </tr>
        </#if>
        <#if !itemsFromList && !promoItems>
	        <tr>
			<td>&nbsp;</td>
	          <td>&nbsp;</td>
	        </tr>
        </#if>
        <tr>
          <td>
              <#if sessionAttributes.userLogin?has_content && sessionAttributes.userLogin.userLoginId != "anonymous">
              <select name="shoppingListId" class="selectBox">
                <#if shoppingLists?has_content>
                  <#list shoppingLists as shoppingList>
                    <option value="${shoppingList.shoppingListId}">${shoppingList.listName}</option>
                  </#list>
                </#if>
                <option value="">---</option>
                <option value="">${uiLabelMap.OrderNewShoppingList}</option>
              </select>
              &nbsp;&nbsp;
              <a href="javascript:addToList();" class="button">${uiLabelMap.ObbAddSelectedtoList}</a>&nbsp;&nbsp;
              <#else>
               ${uiLabelMap.OrderYouMust} <a href="<@ofbizUrl>checkLogin/main</@ofbizUrl>" class="button">${uiLabelMap.CommonBeLogged}</a>
                ${uiLabelMap.OrderToAddSelectedItemsToShoppingList}.&nbsp;
              </#if>
          </td>
          <td></td>
        </tr>
        <tr>
          <td>
              <#if sessionAttributes.userLogin?has_content && sessionAttributes.userLogin.userLoginId != "anonymous">
              &nbsp;&nbsp;
              <a href="<@ofbizUrl>createCustRequestFromCart</@ofbizUrl>" class="button">${uiLabelMap.OrderCreateCustRequestFromCart}</a>&nbsp;&nbsp;
              &nbsp;&nbsp;
              <a href="<@ofbizUrl>createQuoteFromCart</@ofbizUrl>" class="button">${uiLabelMap.OrderCreateQuoteFromCart}</a>&nbsp;&nbsp;
              <#else>
               ${uiLabelMap.OrderYouMust} <a href="<@ofbizUrl>checkLogin/main</@ofbizUrl>" class="button">${uiLabelMap.CommonBeLogged}</a>
                ${uiLabelMap.ObbToOrderCreateCustRequestFromCart}.&nbsp;
              </#if>
          </td>
          <td></td>
        </tr>
        <tr>
          <td>
            <input type="checkbox" onclick="javascript:document.cartform.submit()" name="alwaysShowcart" <#if shoppingCart.viewCartOnAdd()>checked="checked"</#if>/>${uiLabelMap.ObbAlwaysViewCartAfterAddingAnItem}.
          </td>
          <td></td>
        </tr>
      </table>

     -->
      </div>
    </form>


<#-- file header.ftl cuoi cua file -->


<!-- Top Right part Links 	  -->

    <div id="welcome">
      <#include "language.ftl"> <!-- Comment: Da su dung -->s

      <!-- Comment: login -->
      <#if userLogin?has_content && userLogin.userLoginId != "anonymous">
			<a href="#">${uiLabelMap.CommonWelcome}&nbsp;${sessionAttributes.autoName?html}!</a><a href="<@ofbizUrl>logout</@ofbizUrl>">${uiLabelMap.CommonLogout}</a>
	  <#else/>
			<script type="text/javascript">
				function opencompare(){
					var params = [
				    'height='+screen.height,
				    'width='+screen.width,
				    'fullscreen=yes' // only works in IE, but here for completeness
					].join(',');
					     // and any other options from
					     // https://developer.mozilla.org/en/DOM/window.open

					var popup = window.open('<@ofbizUrl>compareProducts</@ofbizUrl>', 'compareProducts', params);
					popup.moveTo(0,0);
				}
			</script>
			<a href="#">${uiLabelMap.CommonWelcome}!</a><a href="<@ofbizUrl>${checkLoginUrl}</@ofbizUrl>">${uiLabelMap.CommonLogin}</a><a href="<@ofbizUrl>newcustomer</@ofbizUrl>">${uiLabelMap.EcommerceRegister}</a>
	  </#if>
	  <!-- Comment: end login -->

	  <div class="links">...
        <ul>
	      <#if !userLogin?has_content || (userLogin.userLoginId)?if_exists != "anonymous">
	        <li><a href="<@ofbizUrl>viewprofile</@ofbizUrl>">${uiLabelMap.CommonProfile}</a></li>
	        <li><a href="<@ofbizUrl>messagelist</@ofbizUrl>">${uiLabelMap.CommonMessages}</a></li>
	        <li><a href="<@ofbizUrl>ListQuotes</@ofbizUrl>">${uiLabelMap.OrderOrderQuotes}</a></li>
	        <li><a href="<@ofbizUrl>ListRequests</@ofbizUrl>">${uiLabelMap.OrderRequests}</a></li>
	        <li><a href="<@ofbizUrl>editShoppingList</@ofbizUrl>">${uiLabelMap.ObbShoppingLists}</a></li>
	        <li><a href="<@ofbizUrl>orderhistory</@ofbizUrl>">${uiLabelMap.ObbOrderHistory}</a></li>
	        <li><a href="#" onclick="opencompare();">${uiLabelMap.BigshopViewCompare}</a></li>
	      </#if>
	      <#if catalogQuickaddUse>
	        <li><a class="invarseColor" href="<@ofbizUrl>quickadd</@ofbizUrl>">${uiLabelMap.CommonQuickAdd}</a></li>
	      </#if>
		</ul>
      </div>
    </div>
    <div id="logo">
      <#if sessionAttributes.overrideLogo?exists>
        <img src="<@ofbizContentUrl>${sessionAttributes.overrideLogo}</@ofbizContentUrl>" alt="Logo"/>
      <#elseif catalogHeaderLogo?exists>
        <img src="<@ofbizContentUrl>${catalogHeaderLogo}</@ofbizContentUrl>" alt="Logo"/>
      <#elseif layoutSettings.VT_HDR_IMAGE_URL?has_content>
        <img src="<@ofbizContentUrl>${layoutSettings.VT_HDR_IMAGE_URL.get(0)}</@ofbizContentUrl>" alt="Logo"/>
      </#if>
    </div>



    <#include "search/searchform.ftl">
    <#include "cart/minicart.ftl"> <!-- Comment: Da su dung -->




<#assign janrainEnabled = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("ecommerce.properties", "janrain.enabled")>
<#assign appName = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("ecommerce.properties", "janrain.appName")>

<h1>${uiLabelMap.CommonLogin}</h1>
<div class="login-content">

	<#if janrainEnabled == "Y">
			<script type="text/javascript">
			(function() {
			    if (typeof window.janrain !== 'object') window.janrain = {};
			    window.janrain.settings = {};

			    janrain.settings.tokenUrl = '<@ofbizUrl fullPath="true" secure="true">janrainCheckLogin';

			    function isReady() { janrain.ready = true; };
			    if (document.addEventListener) {
			      document.addEventListener("DOMContentLoaded", isReady, false);
			    } else {
			      window.attachEvent('onload', isReady);
			    }

			    var e = document.createElement('script');
			    e.type = 'text/javascript';
			    e.id = 'janrainAuthWidget';

			    if (document.location.protocol === 'https:') {
			      e.src = 'https://rpxnow.com/js/lib/${appName}/engage.js';
			    } else {
			      e.src = 'http://widget-cdn.rpxnow.com/js/lib/${appName}/engage.js';
			    }

			    var s = document.getElementsByTagName('script')[0];
			    s.parentNode.insertBefore(e, s);
			})();
			</script>


			<div class="left">
			  <h2>${uiLabelMap.CommonRegistered}</h2>
			  <div class="screenlet-body">
			  <table width="100%" class="Signlogin">
			      <tr>
			          <td>
			          <form method="post" action="<@ofbizUrl>login</@ofbizUrl>" name="loginform" class="horizontal">
			            <fieldset>
			              <div>
			                <label for="userName">${uiLabelMap.CommonUsername}</label>
			                <input type="text" id="userName" name="USERNAME" value="<#if requestParameters.USERNAME?has_content>${requestParameters.USERNAME}<#elseif autoUserLogin?has_content>${autoUserLogin.userLoginId}</#if>"/>
			              </div>
			              <#if autoUserLogin?has_content>
			                <p>(${uiLabelMap.CommonNot} ${autoUserLogin.userLoginId}? <a href="<@ofbizUrl>${autoLogoutUrl}</@ofbizUrl>">${uiLabelMap.CommonClickHere}</a>)</p>
			              </#if>
			              <div>
			                <label for="password">${uiLabelMap.CommonPassword}:</label>
			                <input type="password" id="password" name="PASSWORD" value=""/>
			              </div>
			              <div>
			                <input type="submit" class="button" value="${uiLabelMap.CommonLogin}"/>
			              </div>
			              <div>
			                <label for="newcustomer_submit">${uiLabelMap.CommonMayCreateNewAccountHere}:</label>
			                <a href="<@ofbizUrl>newcustomer</@ofbizUrl>">${uiLabelMap.CommonMayCreate}</a>
			              </div>
			            </fieldset>
			          </form>
			          </td>
			          <td><div id="janrainEngageEmbed"></div></td>
			      </tr>
			  </table>
			  </div>
			</div>

	<#else>

			<div class="left">
			  <h2>${uiLabelMap.CommonRegistered}</h2>
			  <div class="content">
			    <form method="post" action="<@ofbizUrl>login</@ofbizUrl>" name="loginform" class="horizontal">
			          <b>${uiLabelMap.CommonUsername}</b></br>
			          <input type="text" id="userName" name="USERNAME" value="<#if requestParameters.USERNAME?has_content>${requestParameters.USERNAME}<#elseif autoUserLogin?has_content>${autoUserLogin.userLoginId}</#if>"/>
			          <br/><br/>
			        <#if autoUserLogin?has_content>
			          <p>(${uiLabelMap.CommonNot} ${autoUserLogin.userLoginId}? <a href="<@ofbizUrl>${autoLogoutUrl}</@ofbizUrl>">${uiLabelMap.CommonClickHere}</a>)</p>
			        </#if>
			          <b>${uiLabelMap.CommonPassword}:</b><br/>
			          <input type="password" id="password" name="PASSWORD" value=""/><br/><br/>
			        <div>
			          <input type="submit" class="button" value="${uiLabelMap.CommonLogin}"/>
			        </div>
			    </form>
			  </div>
			</div>

	</#if>



	<div class="right">
		<h2>${uiLabelMap.CommonNewUser}</h2>
		<div class="content">
			<form method="post" action="<@ofbizUrl>newcustomer</@ofbizUrl>">
				<label for="newcustomer_submit">${uiLabelMap.CommonMayCreateNewAccountHere}:</p>
				<input type="submit" class="button" id="newcustomer_submit" value="${uiLabelMap.CommonMayCreate}"/>
			</form>
		</div>
	</div>

	<div class="left">
		<h2>${uiLabelMap.CommonForgotYourPassword}</h2>
		<div class="content">
			<form method="post" action="<@ofbizUrl>forgotpassword</@ofbizUrl>" class="horizontal">
				<b>${uiLabelMap.CommonUsername}</b><br/>
				<input type="text" id="forgotpassword_userName" name="USERNAME" value="<#if requestParameters.USERNAME?has_content>${requestParameters.USERNAME}<#elseif autoUserLogin?has_content>${autoUserLogin.userLoginId}</#if>"/><br/><br/>
				<input type="submit" class="button" name="GET_PASSWORD_HINT" value="${uiLabelMap.BigshopGetPasswordHint}"/>
				<input type="submit" class="button" name="EMAIL_PASSWORD" value="${uiLabelMap.BigshopEmailPassword}"/>
			</form>
		</div>
	</div>