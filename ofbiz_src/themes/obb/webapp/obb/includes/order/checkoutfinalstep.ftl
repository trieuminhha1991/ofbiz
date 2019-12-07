<div class='step-container success-container' id="step3">
	<div class="orderdetails">
		<h3 class='cart-info-title'>${uiLabelMap.BECheckoutSuccess}</h3>
		<h3 class='cart-info-title'>${uiLabelMap.BEThankful}</h3>
		<div class='row order-success-content'>
			<div class='col-lg-6 col-md-6'>
				<p>
					<b>${uiLabelMap.BEOrderInformation}</b>
				</p>
				<p>
					<#if request.getAttribute("orderId")?exists>
						<#assign orderId =  request.getAttribute("orderId")/>
					</#if>
					1. ${uiLabelMap.BEOrderId}: <#if orderId?exists> <b> ${orderId?if_exists}</b> (<a href="<@ofbizUrl>orderstatus?orderId=${request.getAttribute("orderId")?if_exists}</@ofbizUrl>">chi tiết</a>).</#if>
				</p>
				<p>
					2. ${uiLabelMap.BEShippingAddress}: <span id='address'><#if basicInfo?has_content><b>${(basicInfo.address)?if_exists}, ${(basicInfo.districtGeoName)?if_exists}, ${(basicInfo.cityGeoName)?if_exists}</b>.</#if></span>
				</p>
				<p>
					3. ${uiLabelMap.BEShippingPhone}: <span id='phone'><#if basicInfo?has_content><b>${(basicInfo.phoneNumber)?if_exists}.</b></#if></span>
				</p>
				<p>
					4. ${uiLabelMap.BEShippingBeforeDate}: <b> ${request.getAttribute("shipBeforeDate")?if_exists}.</b>
				</p>
				<p>
					5. <b id="payment"> ${uiLabelMap[request.getAttribute("checkOutPaymentId")?if_exists]}.</b>
				</p>
				<p>
					${uiLabelMap.BEShippingConfirm}.
				</p>
				<p>
					${uiLabelMap.BECallSupport} <b>+33 763 571 929</b>
				</p>
			</div>
			<div class='col-lg-6 col-md-6'>
				<div class='delivery'>
					<img src="/obbresources/images/delivery.png">
				</div>
			</div>
		</div>
	</div>
	<div class='text-align-center'>
		<a class="btn-primary" href="<@ofbizUrl>main</@ofbizUrl>" title="Trang chủ"><i class='fa fa-opencart'>&nbsp;</i> ${uiLabelMap.BEContinueShopping}</a>
	</div>
</div>
