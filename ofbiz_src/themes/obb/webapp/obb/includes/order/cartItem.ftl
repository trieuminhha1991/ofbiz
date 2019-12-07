<tr id="${id}" class='cart-item'>
	<td>
	<div class='row'>
		<div class='col-lg-4'>
			<a> <#if !smallImageUrl?string?has_content>
			<#assign smallImageUrl = "/images/defaultImage.jpg" />
			</#if> <img width="80" border="0" src="<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${smallImageUrl}</@ofbizContentUrl>" alt="Product Image" class="imageborder" /> </a>
		</div>
		<div class='col-lg-8 name'>
			<a href="<@ofbizUrl>productmaindetail?product_id=${productId}</@ofbizUrl>"> <b> ${itemName} </b> </a>
		</div>
	</div></td>
	<td align="center"><div class="price-cart"> <@ofbizCurrency amount=displayPrice isoCode=isoCode/></div></td>
	<td align="center">
		<#if !isPromo>
			<input size="10" class="w30" type="text" name="update_${cartLineIndex}" value="${quantity}" />
		<#else>
		<div>${quantity}</div>
		</#if>
	</td>
	<td align="center"><div class="price-cart" id="lblTotal"> <@ofbizCurrency amount=subTotal isoCode=isoCode/></div></td>
	<td align="center"> <#if !isPromo>
	<div class='text-align-center checkbox-container'>
		<a onclick="CommonUtils.removeCartItem('${cartLineIndex}')"><i class="fa fa-times red"></i></a>
	</div> <#else>&nbsp;</#if> </td>
</tr>