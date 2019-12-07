<#if productIds?has_content>

<style type="text/css">
	.details, .yith-wcwl-add-to-wishlist, .woocommerce .product .compare-button, .yit_share share {
		display:inline-block
	}

	.buttons .woocommerce.product.compare-button {
		float:none;
	}

	.wishlist {
		margin-top:0;
	}

	.buttons .woocommerce.product.compare-button, .details {
		margin-right:10px
	}
	.rating{
		margin-bottom:10px;
	}

	.product-thumbnail {
		padding-top:10px;
	}

	.product-thumbnail > div.thumbnail-wrapper {
		position:relative;
		width:212px;
		margin-left:auto;
		margin-right:auto;
	}

	.product-thumbnail .group, .thumbnail-wrapper, .product-meta, .product-actions-wrapper{
		text-align:center
	}

	table tr > td {
		text-align:center; padding:10px 0; border: 1px solid #ccc;
	}
</style>
    <div class="block">
	    <h2 class="margintop5">${uiLabelMap.OrderProductsForPromotion}:</h2>
	    <div class="screenlet-body">
	        <#if (listSize > 0)>
	            <table border="0" width="100%" cellpadding="2">
	                <tr>
	                <td align="right">
	                    <span>
	                    <b>
	                    <#if (viewIndex > 0)>
	                    <a href="<@ofbizUrl>showPromotionDetails?productPromoId=${productPromoId?if_exists}&amp;VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndex-1}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonPrevious}</a> |
	                    </#if>
	                    ${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}
	                    <#if (listSize > highIndex)>
	                    | <a href="<@ofbizUrl>showPromotionDetails?productPromoId=${productPromoId?if_exists}&amp;VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndex+1}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonNext}</a>
	                    </#if>
	                    </b>
	                    </span>
	                </td>
	                </tr>
	            </table>
	        </#if>

	        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
	          <tr>
	            <td><div>${uiLabelMap.CommonQualifier}</div></td>
	            <td><div>${uiLabelMap.CommonBenefit}</div></td>
	            <td><div>&nbsp;</div></td>
	          </tr>
	        <#if (listSize > 0)>
	          <#list productIds[lowIndex..highIndex-1] as productId>
	              <tr>
	                <td><div>[<#if productIdsCond.contains(productId)>x<#else>&nbsp;</#if>]</div></td>
	                <td><div>[<#if productIdsAction.contains(productId)>x<#else>&nbsp;</#if>]</div></td>
	                <td>
	                  ${setRequestAttribute("optProductId", productId)}
	                  ${setRequestAttribute("listIndex", productId_index)}
	                  ${screens.render(productsummaryScreen)}
	                </td>
	              </tr>
	          </#list>
	        </#if>
	        </table>
	    </div>
	</div>
</#if>
