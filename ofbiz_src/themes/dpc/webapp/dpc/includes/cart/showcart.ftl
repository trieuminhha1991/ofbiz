<div id="step1" class='step-container'>
	<ul class="step">
		<li class="one activestep">
			<div>
				<i class="iconcart-step"></i><span>1</span>
			</div>
			<label>Đặt mua </label>
			<b>›</b>
		</li>
		<li class="two">
			<div>
				<i class="iconcart-step"></i><span>2</span>
			</div>
			<label>Chọn cách thanh toán</label>
		</li>
	</ul>
	<#assign fixedAssetExist = shoppingCart.containAnyWorkEffortCartItems() /> <#-- change display format when rental items exist in the shoppingcart -->
	<aside class="left_cart content_one">
		<form action="<@ofbizUrl>modifycart</@ofbizUrl>" method="post" name="cartform">
			<input type="hidden" name="removeSelected" value="false" />
			<table class="table table-bordered">
				<thead>
					<tr class="row1">
						<th class="title">Tên sản phẩm</th>
						<th width="15%" class="title" align="center">Giá</th>
						<th width="15%" class="title" align="center">Số lượng</th>
						<th width="15%" class="title" align="center">Tổng tiền</th>
						<th width="10%" class="title" align="center" style='position: relative'>
							<span class='reject-title' id="reject">Huỷ bỏ</span>
							<input class='reject' type="checkbox" name="selectAll" value="0" onclick="javascript:Cart.toggleAll(this);" />
						</th>
					</tr>
				</thead>
				<tbody>
					<#assign itemsFromList = false />
					<#assign promoItems = false />
					<#list shoppingCart.items() as cartLine>
					<#assign cartLineIndex = shoppingCart.getItemIndex(cartLine) />
					<#assign lineOptionalFeatures = cartLine.getOptionalProductFeatures() />
					<tr id="cartItemDisplayRow_${cartLineIndex}">
						<td>
							<a href="<@ofbizCatalogAltUrl productId=parentProductId/>">
								<#assign smallImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.getProduct(), "SMALL_IMAGE_URL", locale, dispatcher)?if_exists />
								<#if !smallImageUrl?string?has_content>
								<#assign smallImageUrl = "/images/defaultImage.jpg" />
								</#if>
								<#if smallImageUrl?string?has_content>
								</#if>
								<img width="80" border="0" src="<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${smallImageUrl}</@ofbizContentUrl>" alt="Product Image" class="imageborder" />
								<b>
									${cartLine.getName()?if_exists}
								</b>
							</a>
						</td>
						<td align="center">
							<span class="price-cart"> <@ofbizCurrency amount=cartLine.getDisplayPrice() isoCode=shoppingCart.getCurrency()/></span>
						</td>
						<td align="center">
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
		                    <#else>
		                        ${cartLine.getQuantity()?string.number}
		                    </#if>
			                <#else><#-- Is Promo or Shoppinglist -->
			                       <#if fixedAssetExist == true><#if cartLine.getReservStart()?exists><table><tr><td>&nbsp;</td><td><input type="text" class="inputBox" size="10" name="reservStart_${cartLineIndex}" value=${cartLine.getReservStart()?string}/></td><td><input type="text" class="inputBox" size="2" name="reservLength_${cartLineIndex}" value="${cartLine.getReservLength()?string.number}"/></td></tr><tr><td>&nbsp;</td><td><input type="text" class="inputBox" size="3" name="reservPersons_${cartLineIndex}" value=${cartLine.getReservPersons()?string.number} /></td><td><#else>
			                           <table><tr><td>--</td><td>--</td></tr><tr><td>--</td><td></#if>
			                        <input size="10" class="w30" type="text" name="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}" /></td></tr></table>
			                    <#else><#-- fixedAssetExist -->
			                        <input size="10" class="w30" type="text" name="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}" />
			                    </#if>
			                </#if>
						</td>
						<td align="center"><span class="price-cart" id="lblTotal"> <@ofbizCurrency amount=cartLine.getDisplayItemSubTotal() isoCode=shoppingCart.getCurrency()/></span></td>
						<td align="center">
							<#if !cartLine.getIsPromo()><input type="checkbox" name="selectedItem" value="${cartLineIndex}" onclick="javascript:Cart.checkToggle(this);" /><#else>&nbsp;</#if>
						</td>
					</tr>
					</#list>
					<tr>
						<td colspan="5" valign="middle">
						<div class="left">
							 <button class="updatecart" type="button" id="updatecart">
								Cập nhập giỏ hàng
							</button>
						</div>
						<div class="right">
							<p>
								<b>Giá sản phẩm: </b>
								<span class="price-cart">
									<@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/>
								</span>
							</p>
							<p>
								<b>Phí vận chuyển:</b><span class="price-cart">
									<@ofbizCurrency amount=orderShippingTotal isoCode=shoppingCart.getCurrency()/></span>
							</p>
							<p>
								<b>Tổng tiền:</b>
								<span class="price-cart">
									<@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/>
								</span>
							</p>
						</div></td>
					</tr>
				</tbody>
			</table>
		</form>
		<div class="procode">
			<div class="left">
				<form action="<@ofbizUrl>addpromocode</@ofbizUrl>" method="post">
					<p>
						Nhập mã giảm giá nếu có
					</p>
					<input type="text" id="promotion-code" class="topinput" class='no-space' name="productPromoCodeId">
					<button type="submit" class="btntop">
						Áp dụng
					</button>
				</form>
			</div>
			<div class="right">
				<p>
					<b>Giá sản phẩm: </b><span class="price-cart"><@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/></span>
				</p>
				<p>
					<b>Phí vận chuyển:</b><span class="price-cart"><@ofbizCurrency amount=orderShippingTotal isoCode=shoppingCart.getCurrency()/></span>
				</p>
				<p>
					<b>Tổng tiền:</b><span class="price-cart"><@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/></span>
				</p>
				<button type="button" onclick="window.location.href='<@ofbizUrl>main</@ofbizUrl>'">
					&lt;&lt; Quay lại mua tiếp
				</button>
			</div>
		</div>
	</aside>
	<aside class="right_cart content_one">
		<#include "../customer/profile/checkoutprofile.ftl"/>
	</aside>
</div>
<script>
	var shoppingCartSize = ${shoppingCartSize};
</script>