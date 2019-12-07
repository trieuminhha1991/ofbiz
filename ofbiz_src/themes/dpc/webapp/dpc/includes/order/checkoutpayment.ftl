<#if shoppingCart?exists>
	<#assign cart = shoppingCart?if_exists />
	<#assign items = cart.items()/>

	<div id="step2" class='step-container'>
		<ul class="step">
		    <li class="finalstep one">
		        <div><i class="iconcart-step"></i><span>1</span></div>
		        <label>Đặt mua </label>
		        <b>›</b>
		    </li>
		    <li class="two  activestep">
		        <div><i class="iconcart-step"></i><span>2</span></div>
		        <label>Chọn cách thanh toán</label>
		    </li>
		</ul>
		<div class="infocart">
		    <aside class="left-cart-s2">
		        <p><b>Họ &amp; tên: </b> ${(profile.fullName)?if_exists}</p>
		        <p><b>Địa chỉ:</b> ${(profile.address)?if_exists}, ${(profile.districtGeoName)?if_exists}, ${(profile.cityGeoName)?if_exists} </p>
		        <p><b>Điện thoại: </b>${(profile.phoneNumber)?if_exists}</p>
		        <br>
		        <div class='row'>
				<div class='col-lg-12'>
					<table class="table table-bordered">
						<thead>
							<tr class="row1">
								<th class="title">Tên sản phẩm</th>
								<th width="15%" class="title" align="center">Giá</th>
								<th width="15%" class="title" align="center">SL</th>
								<th width="15%" class="title" align="center">Tổng tiền</th>
							</tr>
						</thead>
						<tbody>
							<#list items as item>
								<tr class="row0">
									<td><a href="#"><b>${item.getName()}</h3></b></td>
									<td align="center">
										<span class="price-cart"><@ofbizCurrency amount=item.getItemSubTotal() isoCode=cart.getCurrency()/></span>
									</td>
									<td align="center">
										<span>${item.getQuantity()}</span>
									</td>
									<td align="center">
										<span class="price-cart" id="lblTotal">
											<@ofbizCurrency amount=item.getDisplayItemSubTotal() isoCode=cart.getCurrency()/>
										</span>
									</td>
								</tr>
							</#list>
						</tbody>
					</table>
				</div>
		        </div>
		        <!-- <p><b>Phí vận chuyển: </b><span class="price-cart">1.000.000đ</span></p> -->
		        <p><b>Tổng tiền: </b><span class="price-cart"><@ofbizCurrency amount=cart.getDisplayGrandTotal() isoCode=cart.getCurrency()/></span></p>
		    </aside>
		    <aside class="right-cart-s2">
			<form name='finalcheckout' action="<@ofbizUrl>checkoutorder</@ofbizUrl>" id="checkoutInfoForm" method="POST">
				<input type="hidden" name="checkoutpage" value="confirm" />
				<input type="hidden" name="shipBeforeDate" value="${request.getAttribute("shipBeforeDate")?if_exists}"/>
				<span class="text-pay">Chọn 1 trong 2 cách thanh toán miễn phí sau</span>

		         <div class="moneypay">
				 <input type="radio" id="checkOutPaymentId_COD" name="checkOutPaymentId" value="EXT_COD" checked/>
	                  <label for="checkOutPaymentId_COD">Trả bằng tiền mặt khi nhận hàng</label>
		            </div>
		         <div class="moneypay">
				<input type="radio" id="checkOutPaymentId_OFFLINE" name="checkOutPaymentId" value="EXT_OFFLINE" <#if "EXT_OFFLINE" == checkOutPaymentId>checked="checked"</#if> />
                  <label for="checkOutPaymentId_OFFLINE">Thanh toán trực tuyến (ATM, Visa)<span>Với thẻ ATM cần có Internet Banking</span></label>
		         </div>
		         <input type="text" placeholder="Bạn có yêu cầu gì nữa không?" class="form-control" name="shipping_instructions">
		         <button>Đặt hàng</button>
			</form>
		    </aside>
		</div>
	</div>
</#if>