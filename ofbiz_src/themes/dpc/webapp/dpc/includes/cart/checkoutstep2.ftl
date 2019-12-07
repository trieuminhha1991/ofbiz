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
	        <p><b>Họ &amp; tên: </b> Nguyễn Văn A</p>
	        <p><b>Địa chỉ:</b> 514 3 tháng 2, p14, q10 </p>
	        <p><b>Điện thoại: </b>123456</p>
	        <br>
	        <p><b>Sản phẩm: </b><a href="#">Best Slim Plus giảm cân thế hệ mới</a></p>
	        <p><b>Giá sản phẩm: </b><span class="price-cart">1.000.000đ</span></p>
	        <p><b>Phí vận chuyển: </b><span class="price-cart">1.000.000đ</span></p>
	        <p><b>Tổng tiền: </b><span class="price-cart">1.000.000đ</span></p>
	    </aside>
	    <aside class="right-cart-s2">
		<form name='finalcheckout' action="<@ofbizUrl>checkoutorder</@ofbizUrl>">
			<input type="hidden" name="checkoutorder" value="payment" />
		<input type="hidden" name="BACK_PAGE" value="checkoutorder" />
			<span class="text-pay">Chọn 1 trong 2 cách thanh toán miễn phí sau</span>
		         <div class="moneypay">
		                 <input id="Radio1" type="radio" name="pay">
		                <label>Trả bằng tiền mặt khi nhận hàng</label>
		            </div>
		         <div class="moneypay">
		                <input id="Radio2" type="radio" name="pay">
		                <label>Thanh toán trực tuyến (ATM, Visa)<span>Với thẻ ATM cần có Internet Banking</span></label>
		         </div>
		         <input type="text" placeholder="Bạn có yêu cầu gì nữa không?" class="form-control">
		         <button type="button">Đặt hàng</button>
		</form>
	    </aside>
	</div>
</div>
