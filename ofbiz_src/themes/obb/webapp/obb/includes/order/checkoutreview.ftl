
<script language="JavaScript" type="text/javascript">
<!--
    var clicked = 0;
    function processOrder() {
        if (clicked == 0) {
            clicked++;
            //window.location.replace("<@ofbizUrl>processorder</@ofbizUrl>");
            document.${parameters.formNameValue}.processButton.value="${uiLabelMap.OrderSubmittingOrder}";
            document.${parameters.formNameValue}.processButton.disabled=true;
            document.${parameters.formNameValue}.submit();
        } else {
            showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.YoureOrderIsBeingProcessed}");
        }
    }
// -->
</script>
<div id="jm-container" class="jm-col1-layout wrap not-breadcrumbs clearfix">
	<div class="main clearfix">
		<div id="jm-mainbody" class="clearfix">
			<div id="jm-main">
				<div class="inner clearfix">
					<div id="jm-current-content" class="clearfix">
					  <div class="page-title">
					<h1>${uiLabelMap.OrderFinalCheckoutReview}</h1>
					  </div>
					  <div>
						<#if !isDemoStore?exists && isDemoStore><p>${uiLabelMap.OrderDemoFrontNote}.</p></#if>
						<#if cart?exists && 0 < cart.size()>
						  ${screens.render("component://obb/widget/OrderScreens.xml#orderheader")}
						  <br />
						  ${screens.render("component://obb/widget/OrderScreens.xml#orderitems")}
						<#else>
						  <h3>${uiLabelMap.OrderErrorShoppingCartEmpty}.</h3>
						</#if>
					  </div>
					  <form type="post" action="<@ofbizUrl>processorder</@ofbizUrl>" name="${parameters.formNameValue}">
						<#if (requestParameters.checkoutpage)?has_content>
				            <input type="hidden" name="checkoutpage" value="${requestParameters.checkoutpage}" />
				          </#if>
				          <#if (requestAttributes.issuerId)?has_content>
				            <input type="hidden" name="issuerId" value="${requestAttributes.issuerId}" />
				          </#if>
						  <table width="100%" style="margin-top:20px;">
							  <tr valign="top">
							    <td>
							    </td>
							    <td align="right" style="text-align:right;">
								<button type="button" name="processButton" value="${uiLabelMap.OrderSubmitOrder}" onclick="processOrder();" title="${uiLabelMap.OrderSubmitOrder}" class="button btn-empty" id="empty_cart_button">
									<span><span>${uiLabelMap.OrderSubmitOrder}</span></span>
								</button>
							    </td>
							  </tr>
						  </table>
					  </form>
				  </div>
			  </div>
		  </div>
	  </div>
  </div>
</div>
