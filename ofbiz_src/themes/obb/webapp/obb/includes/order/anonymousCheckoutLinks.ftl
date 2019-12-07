<script language="javascript" type="text/javascript">
function submitForm(form) {
   form.submit();
}
</script>
<div class="cart">
	<div class="screenlet">
	    <a href="<@ofbizUrl>setCustomer</@ofbizUrl>" <#if callSubmitForm?exists>onclick="javascript:submitForm(document.${parameters.formNameValue?if_exists});"</#if>>Personal Info</a>
	    <#if (enableShippingAddress)?exists>
	        <span style="margin-left:10px;margin-right:10px;">|</span><a href="<@ofbizUrl>setShipping</@ofbizUrl>" <#if callSubmitForm?exists>onclick="javascript:submitForm(document.${parameters.formNameValue?if_exists});"</#if>>Shipping Address</a>
	    <#else>
	        <span style="margin-left:10px;margin-right:10px;">|</span><span class="buttontextdisabled">Shipping Address</span>
	    </#if>
	    <#if (enableShipmentMethod)?exists>
	        <span style="margin-left:10px;margin-right:10px;">|</span><a href="<@ofbizUrl>setShipOptions</@ofbizUrl>" <#if callSubmitForm?exists>onclick="javascript:submitForm(document.${parameters.formNameValue?if_exists});"</#if>>Shipping Options</a>
	    <#else>
	        <span style="margin-left:10px;margin-right:10px;">|</span><span class="buttontextdisabled">Shipping Options</span>
	    </#if>
	    <#if (enablePaymentOptions)?exists>
	        <span style="margin-left:10px;margin-right:10px;">|</span><a href="<@ofbizUrl>setPaymentOption</@ofbizUrl>" <#if callSubmitForm?exists>onclick="javascript:submitForm(document.${parameters.formNameValue?if_exists});"</#if>>Payment Options</a>
	    <#else>
	        <span style="margin-left:10px;margin-right:10px;">|</span><span class="buttontextdisabled">Payment Options</span>
	    </#if>
	    <#if (enablePaymentInformation)?exists>
	        <span style="margin-left:10px;margin-right:10px;">|</span><span style="margin-left:10px;margin-right:10px;">|</span><a href="<@ofbizUrl>setPaymentInformation?paymentMethodTypeId=${requestParameters.paymentMethodTypeId?if_exists}</@ofbizUrl>" <#if callSubmitForm?exists>onclick="javascript:submitForm(document.${parameters.formNameValue?if_exists});"</#if>>Payment Information</a>
	    <#else>
	        <span style="margin-left:10px;margin-right:10px;">|</span><span class="buttontextdisabled">Payment Information</span>
	    </#if>
	    <#if (enableReviewOrder)?exists>
	        <span style="margin-left:10px;margin-right:10px;">|</span><a href="<@ofbizUrl>reviewOrder</@ofbizUrl>" <#if callSubmitForm?exists>onclick="javascript:submitForm(document.${parameters.formNameValue?if_exists});"</#if>>Review Order</a>
	    <#else>
	        <span style="margin-left:10px;margin-right:10px;">|</span><span class="buttontextdisabled">Review Order</span>
	    </#if>
	</div>
</div>
