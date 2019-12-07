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
	<aside class="right_cart content_one">
		<#include "../customer/checkoutuserprofile.ftl"/>
	</aside>
</div>
<script>
</script>