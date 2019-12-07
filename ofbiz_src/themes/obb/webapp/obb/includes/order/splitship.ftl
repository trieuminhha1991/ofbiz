<script language="javascript" type="text/javascript">
//<![CDATA[
function submitForm(form, mode, value) {
    if (mode == "DN") {
        // done action; payment info
        form.action="<@ofbizUrl>updateShippingOptions/checkoutpayment</@ofbizUrl>";
        form.submit();
    } else if (mode == "CS") {
        // continue shopping
        form.action="<@ofbizUrl>updateShippingOptions/showcart</@ofbizUrl>";
        form.submit();
    } else if (mode == "NA") {
        // new address
        form.action="<@ofbizUrl>updateShippingOptions/editcontactmech?DONE_PAGE=splitship&preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION</@ofbizUrl>";
        form.submit();
    } else if (mode == "SV") {
        // save option; return to current screen
        form.action="<@ofbizUrl>updateShippingOptions/splitship</@ofbizUrl>";
        form.submit();
    } else if (mode == "SA") {
        // selected shipping address
        form.action="<@ofbizUrl>updateShippingAddress/splitship</@ofbizUrl>";
        form.submit();
    }
}
//]]>
</script>
<style type="text/css">
	.margin10{
		margin-top:10px;
	}
	#shopping-cart-table select{
		width:100px;
	}
	select{
		width:270px;
	}
	textarea{
		width:248px;
	}
</style>
<div id="jm-container" class="jm-col1-layout wrap not-breadcrumbs clearfix">
	<div class="main clearfix">
		<div id="jm-mainbody" class="clearfix">
			<div id="jm-main">
				<div class="inner clearfix">
					<div id="jm-current-content" class="clearfix">
						<div class="cart">
						<div class="page-title title-buttons">
							<h1>${uiLabelMap.OrderItemGroups}</h1>
					</div>
							<div class="screenlet">
							    <div class="screenlet-body">
							        <table id="shopping-cart-table2" class="data-table cart-table">
							          <#assign shipGroups = cart.getShipGroups()>
							          <#if (shipGroups.size() > 0)>
							            <#assign groupIdx = 0>
							            <#list shipGroups as group>
							              <#assign shipEstimateWrapper = Static["org.ofbiz.order.shoppingcart.shipping.ShippingEstimateWrapper"].getWrapper(dispatcher, cart, groupIdx)>
							              <#assign carrierShipmentMethods = shipEstimateWrapper.getShippingMethods()>
							              <#assign groupNumber = groupIdx + 1>
							              <form method="post" action="#" name="editgroupform${groupIdx}" style="margin: 0;">
							                <input type="hidden" name="groupIndex" value="${groupIdx}"/>
							                <tr>
							                  <td>
							                    <div><b>${uiLabelMap.CommonGroup} ${groupNumber}:</b></div>
							                    <#list group.getShipItems() as item>
							                      <#assign groupItem = group.getShipItemInfo(item)>
							                      <div>&nbsp;&nbsp;&nbsp;${item.getName()} - (${groupItem.getItemQuantity()})</div>
							                    </#list>
							                  </td>
							                  <td>
							                    <div style="width:100%;">
										<h5 style="float:left;">Lựa chọn địa chỉ</h5>
										<p style="float:left;">&nbsp;|&nbsp;Thêm:&nbsp;</p>
										<a style="float:left;" href="javascript:submitForm(document.editgroupform${groupIdx}, 'NA', '');" class="buttontext">Địa chỉ</a>
							                    </div>
							                    <div>
							                      <#assign selectedContactMechId = cart.getShippingContactMechId(groupIdx)?default("")>
							                      <select name="shippingContactMechId" onchange="javascript:submitForm(document.editgroupform${groupIdx}, 'SA', null);">
							                        <option value="">${uiLabelMap.OrderSelectShippingAddress}</option>
							                        <#list shippingContactMechList as shippingContactMech>
							                          <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress", false)>
							                          <option value="${shippingAddress.contactMechId}" <#if (shippingAddress.contactMechId == selectedContactMechId)>selected="selected"</#if>>${shippingAddress.address1}</option>
							                        </#list>
							                      </select>
							                    </div>
							                    <#if cart.getShipmentMethodTypeId(groupIdx)?exists>
							                      <#assign selectedShippingMethod = cart.getShipmentMethodTypeId(groupIdx) + "@" + cart.getCarrierPartyId(groupIdx)>
							                    <#else>
							                      <#assign selectedShippingMethod = "">
							                    </#if>
							                    <h5 class="margin10">Lựa chọn hình thức vận chuyển</h5>
							                    <select name="shipmentMethodString">
							                      <option value="">${uiLabelMap.OrderSelectShippingMethod}</option>
							                      <#list carrierShipmentMethods as carrierShipmentMethod>
							                        <#assign shippingEst = shipEstimateWrapper.getShippingEstimate(carrierShipmentMethod)?default(-1)>
							                        <#assign shippingMethod = carrierShipmentMethod.shipmentMethodTypeId + "@" + carrierShipmentMethod.partyId>
							                        <option value="${shippingMethod}" <#if (shippingMethod == selectedShippingMethod)>selected="selected"</#if>>
							                          <#if carrierShipmentMethod.partyId != "_NA_">
							                            ${carrierShipmentMethod.partyId?if_exists}&nbsp;
							                          </#if>
							                          ${carrierShipmentMethod.description?if_exists}
							                          <#if shippingEst?has_content>
							                            &nbsp;-&nbsp;
							                            <#if (shippingEst > -1)>
							                              <@ofbizCurrency amount=shippingEst isoCode=cart.getCurrency()/>
							                            <#else>
							                              ${uiLabelMap.OrderCalculatedOffline}
							                            </#if>
							                          </#if>
							                        </option>
							                      </#list>
							                    </select>

							                    <h5 class="margin10">${uiLabelMap.OrderSpecialInstructions}</h5>
							                    <textarea class='textAreaBox' cols="35" rows="3" wrap="hard" name="shippingInstructions">${cart.getShippingInstructions(groupIdx)?if_exists}</textarea>
							                  </td>
							                  <td>
							                    <div>
										<h5 class="margin10">Tùy chọn vận chuyển nhóm</h5>
							                      <select name="maySplit">
							                        <#assign maySplitStr = cart.getMaySplit(groupIdx)?default("")>
							                        <option value="">${uiLabelMap.OrderSplittingPreference}</option>
							                        <option value="false" <#if maySplitStr == "N">selected="selected"</#if>>${uiLabelMap.OrderShipAllItemsTogether}</option>
							                        <option value="true" <#if maySplitStr == "Y">selected="selected"</#if>>${uiLabelMap.OrderShipItemsWhenAvailable}</option>
							                      </select>
							                    </div>
							                   <div>
											<h5 class="margin10">Tùy chọn quà tặng</h5>
							                      <select name="isGift">
							                        <#assign isGiftStr = cart.getIsGift(groupIdx)?default("")>
							                        <option value="">${uiLabelMap.OrderIsGift} ?</option>
							                        <option value="false" <#if isGiftStr == "N">selected="selected"</#if>>${uiLabelMap.OrderNotAGift}</option>
							                        <option value="true" <#if isGiftStr == "Y">selected="selected"</#if>>${uiLabelMap.OrderYesIsAGift}</option>
							                      </select>
							                    </div>

							                    <h5 class="margin10">${uiLabelMap.OrderGiftMessage}</h5>
							                    <textarea class='textAreaBox' cols="30" rows="3" wrap="hard" name="giftMessage">${cart.getGiftMessage(groupIdx)?if_exists}</textarea>
							                  </td>
							                  <td>
										<a href="javascript:void(0);" onclick="javascript:submitForm(document.editgroupform${groupIdx}, 'SV', null);">${uiLabelMap.CommonSave}</a>
							                </tr>
							                <#assign groupIdx = groupIdx + 1>
							              </form>
							            </#list>
							          <#else>
							            <div>${uiLabelMap.OrderNoShipGroupsDefined}.</div>
							          </#if>
							        </table>
							    </div>
							</div>
							<div class="screenlet" style="margin-top:20px;">
							    <div class="page-title title-buttons">
								<h1>Cấu hình nhóm sản phẩm</h1>
						</div>
							    <div class="screenlet-body">
							        <table id="shopping-cart-table" class="data-table cart-table" style="border:1px solid #ebebeb">
							          <tr>
							            <td style="border-right:1px solid #ebebeb;"><div><b>${uiLabelMap.OrderProduct}</b></div></td>
							            <td align="center"><div><b>${uiLabelMap.OrderTotalQty}</b></div></td>
							            <td align="center"><div><b>${uiLabelMap.OrderMoveQty}</b></div></td>
							            <td align="center"><div><b>Từ nhóm</b></div></td>
							            <td align="center"><div><b>Tới nhóm</b></div></td>
							            <td align="center">&nbsp;</td>
							          </tr>

							          <#list cart.items() as cartLine>
							            <#assign cartLineIndex = cart.getItemIndex(cartLine)>
							            <tr>
							              <form method="post" action="<@ofbizUrl>updatesplit</@ofbizUrl>" name="editgroupform" id="editgroupform${cartLine_index}" style="margin: 0;">
							                <input type="hidden" name="itemIndex" value="${cartLineIndex}"/>
							                <td style="border-right:1px solid #ebebeb;">
							                  <div>
							                    <#if cartLine.getProductId()?exists>
							                      <#-- product item -->
							                      <#-- start code to display a small image of the product -->
							                      <#assign smallImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.getProduct(), "SMALL_IMAGE_URL", locale, dispatcher)?if_exists>
							                      <#if !smallImageUrl?string?has_content><#assign smallImageUrl = "/images/defaultImage.jpg"></#if>
							                      <#if smallImageUrl?string?has_content>
							                        <a href="<@ofbizUrl>product?product_id=${cartLine.getProductId()}</@ofbizUrl>">
							                          <img src="<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${smallImageUrl}</@ofbizContentUrl>" class="cssImgSmall" alt="" />
							                        </a>
							                      </#if>
							                      <#-- end code to display a small image of the product -->
							                      <a href="<@ofbizUrl>product?product_id=${cartLine.getProductId()}</@ofbizUrl>" class="buttontext">
							                      ${cartLine.getName()?if_exists}</a> : ${cartLine.getDescription()?if_exists}

							                      <#-- display the registered ship groups and quantity -->
							                      <#assign itemShipGroups = cart.getShipGroups(cartLine)>
							                      <#list itemShipGroups.entrySet() as group>
							                        <div>
							                          <#assign groupNumber = group.getKey() + 1>
							                          <b>Group - </b>${groupNumber} / <b>${uiLabelMap.CommonQuantity} - </b>${group.getValue()}
							                        </div>
							                      </#list>

							                      <#-- if inventory is not required check to see if it is out of stock and needs to have a message shown about that... -->
							                      <#assign itemProduct = cartLine.getProduct()>
							                      <#assign isStoreInventoryNotRequiredAndNotAvailable = Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryRequiredAndAvailable(request, itemProduct, cartLine.getQuantity(), false, false)>
							                      <#if isStoreInventoryNotRequiredAndNotAvailable && itemProduct.inventoryMessage?has_content>
							                        <b>(${itemProduct.inventoryMessage})</b>
							                      </#if>

							                    <#else>
							                      <#-- this is a non-product item -->
							                      <b>${cartLine.getItemTypeDescription()?if_exists}</b> : ${cartLine.getName()?if_exists}
							                    </#if>
							                  </div>

							                </td>
							                <td align="center">
							                  <div>${cartLine.getQuantity()?string.number}&nbsp;&nbsp;&nbsp;</div>
							                </td>
							                <td align="center">
							                  <input size="6" class="inputBox" type="text" name="quantity" value="${cartLine.getQuantity()?string.number}"/>
							                </td>
							                <td>
							                  <div>${uiLabelMap.CommonFrom}:
							                    <select name="fromGroupIndex" class="selectBox">
							                      <#list itemShipGroups.entrySet() as group>
							                        <#assign groupNumber = group.getKey() + 1>
							                        <option value="${group.getKey()}">${uiLabelMap.CommonGroup} ${groupNumber}</option>
							                      </#list>
							                    </select>
							                  </div>
							                </td>
							                <td>
							                  <div>${uiLabelMap.CommonTo}:
							                    <select name="toGroupIndex" class="selectBox">
							                      <#list 0..(cart.getShipGroupSize() - 1) as groupIdx>
							                        <#assign groupNumber = groupIdx + 1>
							                        <option value="${groupIdx}">${uiLabelMap.CommonGroup} ${groupNumber}</option>
							                      </#list>
							                      <option value="-1">${uiLabelMap.CommonNew} ${uiLabelMap.CommonGroup}</option>
							                    </select>
							                  </div>
							                </td>
							                <td>
										<a onclick="document.getElementById('editgroupform${cartLine_index}').submit();" href="javascript:void();">${uiLabelMap.CommonSubmit}</a>
									</td>
							              </form>
							            </tr>
							          </#list>
							        </table>
							    </div>
							</div>

							<table class="margin10">
							  <tr class="first last">
		                        <td class="a-right last">
                                    <button type="button" title="${uiLabelMap.OrderBacktoShoppingCart}" class="button btn-continue" onclick="setLocation('<@ofbizUrl>view/showcart</@ofbizUrl>')"><span><span>${uiLabelMap.OrderBacktoShoppingCart}</span></span></button>
							<button type="button" value="${uiLabelMap.CommonContinue}" onclick="setLocation('<@ofbizUrl>view/checkoutpayment</@ofbizUrl>')" title="${uiLabelMap.CommonContinue}" class="button btn-empty" id="empty_cart_button"><span><span>${uiLabelMap.CommonContinue}</span></span></button>
						</td>
		                    </tr>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
</div>