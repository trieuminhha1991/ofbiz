<div class="crosssell">
    <h2>${uiLabelMap.OrderPromotionInformation}:</h2>
    <div class="widget-toolbar">
	<a href="#" data-action="collapse"><i class="icon-chevron-up"></i></a>
    </div>
<div class="widget-body">
   <div class="widget-body-inner">
		<div class="fieldset">
		<h2 class="legend">${uiLabelMap.OrderPromotionsApplied}:</h2>
		<ul>
			<#list shoppingCart.getProductPromoUseInfoIter() as productPromoUseInfo>
			<li style="list-style: none; margin-top5"> <i class="icon-caret-right blue"></i>
			<#-- TODO: when promo pretty print is done show promo short description here -->
				${uiLabelMap.OrderPromotion} <a href="<@ofbizUrl>showPromotionDetails?productPromoId=${productPromoUseInfo.productPromoId?if_exists}</@ofbizUrl>" class="btn btn-mini btn-info">${uiLabelMap.CommonDetails}</a>
			<#if productPromoUseInfo.productPromoCodeId?has_content> - ${uiLabelMap.OrderWithPromoCode} [${productPromoUseInfo.productPromoCodeId}]</#if>
			<#if (productPromoUseInfo.totalDiscountAmount != 0)> - ${uiLabelMap.CommonTotalValue} <@ofbizCurrency amount=(-1*productPromoUseInfo.totalDiscountAmount) isoCode=shoppingCart.getCurrency()/></#if>
			<#if productPromoUseInfo.productPromoCodeId?has_content>
				<a href="<@ofbizUrl>removePromotion?promoCode=${productPromoUseInfo.productPromoCodeId?if_exists}</@ofbizUrl>" class="btn btn-info btn-mini">${uiLabelMap.OrderRemovePromotion}</a>
			</#if>
			</li>
			<#if (productPromoUseInfo.quantityLeftInActions > 0)>
			<li style="list-style: none;"> <i class="icon-caret-right blue"></i>- Could be used for ${productPromoUseInfo.quantityLeftInActions} more discounted item<#if (productPromoUseInfo.quantityLeftInActions > 1)>s</#if> if added to your cart.</li>
			</#if>
		</#list>
		</ul>
	</div>
        <div class="fieldset">
	<h2 class="legend">${uiLabelMap.OrderCartItemUseinPromotions}:</h2>
	<ul>
	        <#list shoppingCart.items() as cartLine>
	            <#assign cartLineIndex = shoppingCart.getItemIndex(cartLine)>
	            <#if cartLine.getIsPromo()>
	                <li style="list-style: none;"> <i class="icon-caret-right blue"></i>${uiLabelMap.OrderItemN} ${cartLineIndex+1} [${cartLine.getProductId()?if_exists}] - ${uiLabelMap.OrderIsAPromotionalItem}</li>
	            <#else>
	                <li style="list-style: none;"> <i class="icon-caret-right blue"></i>${uiLabelMap.OrderItemN} ${cartLineIndex+1} [${cartLine.getProductId()?if_exists}] - ${cartLine.getPromoQuantityUsed()?string.number}/${cartLine.getQuantity()?string.number} ${uiLabelMap.CommonUsed} - ${cartLine.getPromoQuantityAvailable()?string.number} ${uiLabelMap.CommonAvailable}
	                    <ul>
	                        <#list cartLine.getQuantityUsedPerPromoActualIter() as quantityUsedPerPromoActualEntry>
	                            <#assign productPromoActualPK = quantityUsedPerPromoActualEntry.getKey()>
	                            <#assign actualQuantityUsed = quantityUsedPerPromoActualEntry.getValue()>
	                            <#assign isQualifier = "ProductPromoCond" == productPromoActualPK.getEntityName()>
	                            <li style="list-style: none;"> <i class="icon-caret-right blue"></i>&nbsp;&nbsp;-&nbsp;${actualQuantityUsed} ${uiLabelMap.CommonUsedAs} <#if isQualifier>${uiLabelMap.CommonQualifier}<#else>${uiLabelMap.CommonBenefit}</#if> ${uiLabelMap.OrderOfPromotion} <a href="<@ofbizUrl>showPromotionDetails?productPromoId=${productPromoActualPK.productPromoId}</@ofbizUrl>" class="btn btn-info btn-mini">${uiLabelMap.CommonDetails}</a></li>
	                            <!-- productPromoActualPK ${productPromoActualPK.toString()} -->
	                        </#list>
	                    </ul>
	                    <ul>
	                        <#list cartLine.getQuantityUsedPerPromoFailedIter() as quantityUsedPerPromoFailedEntry>
	                            <#assign productPromoFailedPK = quantityUsedPerPromoFailedEntry.getKey()>
	                            <#assign failedQuantityUsed = quantityUsedPerPromoFailedEntry.getValue()>
	                            <#assign isQualifier = "ProductPromoCond" == productPromoFailedPK.getEntityName()>
	                            <li style="list-style: none;"> <i class="icon-caret-right blue"></i>&nbsp;&nbsp;-&nbsp;${uiLabelMap.CommonCouldBeUsedAs} <#if isQualifier>${uiLabelMap.CommonQualifier}<#else>${uiLabelMap.CommonBenefit}</#if> ${uiLabelMap.OrderOfPromotion} <a href="<@ofbizUrl>showPromotionDetails?productPromoId=${productPromoFailedPK.productPromoId}</@ofbizUrl>" class="btn btn-info btn-mini">${uiLabelMap.CommonDetails}</a></li>
	                            <!-- Total times checked but failed: ${failedQuantityUsed}, productPromoFailedPK ${productPromoFailedPK.toString()} -->
	                        </#list>
	                    </ul>
	                    <#list cartLine.getQuantityUsedPerPromoCandidateIter() as quantityUsedPerPromoCandidateEntry>
	                        <#assign productPromoCandidatePK = quantityUsedPerPromoCandidateEntry.getKey()>
	                        <#assign candidateQuantityUsed = quantityUsedPerPromoCandidateEntry.getValue()>
	                        <#assign isQualifier = "ProductPromoCond" == productPromoCandidatePK.getEntityName()>
	                        <!-- Left over not reset or confirmed, shouldn't happen: ${candidateQuantityUsed} Might be Used (Candidate) as <#if isQualifier>${uiLabelMap.CommonQualifier}<#else>${uiLabelMap.CommonBenefit}</#if> ${uiLabelMap.OrderOfPromotion} [${productPromoCandidatePK.productPromoId}] -->
	                        <!-- productPromoCandidatePK ${productPromoCandidatePK.toString()} -->
	                    </#list>
	                </li>
	            </#if>
	        </#list>
		</ul>
	</div>
	</div>
</div>
