<div id="top_navigation" class='checkout-process'>
	<ul class="steps text-center">
		<li class="checkout-1
			<#if request.getAttribute("currentpage")?exists
				&& (request.getAttribute("currentpage") == "shippinginfo"
				|| request.getAttribute("currentpage") == "confirm")>complete</#if>
			<#if (request.getAttribute("currentpage")?exists && request.getAttribute("currentpage") == "login") || !request.getAttribute("currentpage")?exists>active</#if>">
			<a href="javascript:void(0)">
			<div class="bullet-checkout">
				<span class="number">1</span>
			</div> ${uiLabelMap.BELogin} </a>
		</li>
		<li class="checkout-2 current
			<#if request.getAttribute("currentpage")?exists
				&& request.getAttribute("currentpage") == "confirm">complete</#if>
			<#if request.getAttribute("currentpage")?exists && request.getAttribute("currentpage") == "shippinginfo">active</#if>">
			<a href="javascript:void(0)">
			<div class="bullet-checkout">
				<span class="number">2</span>
			</div> ${uiLabelMap.BEShippingInformation}</a>
		</li>
		<li class="checkout-3 <#if request.getAttribute("currentpage")?exists && request.getAttribute("currentpage") == "confirm">active</#if>">
			<a href="javascript:void(0)">
			<div class="bullet-checkout">
				<span class="number">3</span>
			</div> ${uiLabelMap.BEPaymentMethod}</a>
		</li>
	</ul>
</div>