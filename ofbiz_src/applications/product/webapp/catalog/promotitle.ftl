
<#if productPromoId?has_content || (productPromoId?exists && productPromoId?has_content)>
	<i class="icon-angle-right"></i>
</#if>
<#--
<#if productPromoId?has_content>
	<#if productPromo.promoName?length &gt; 25 >
		<span class="display-inline-block hover-name" title="${productPromo.promoName}">${productPromo.promoName?substring(0, 25)}...</span>
	<#else>
		${productPromo.promoName}
	</#if>
</#if>
-->
<#if productPromoId?exists && productPromoId?has_content>
	${uiLabelMap.CommonId}: <a href="<@ofbizUrl>viewProductPromo?productPromoId=${productPromoId}</@ofbizUrl>">${productPromoId}</a>
</#if>