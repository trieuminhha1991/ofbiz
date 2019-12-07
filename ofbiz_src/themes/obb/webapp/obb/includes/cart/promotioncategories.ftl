
<#if productPromoCategoryIncludeList?has_content || productPromoCategoryExcludeList?has_content || productPromoCategoryAlwaysList?has_content>
<div class="block">
    <h2 class="margintop5">${uiLabelMap.OrderPromotionCategories}:</h2>
    <div class="screenlet-body">
        <#if productPromoCategoryIncludeList?has_content>
          <div>${uiLabelMap.OrderPromotionProductsInCategories}:</div>
          <#list productPromoCategoryIncludeList as productPromoCategory>
            <#assign productCategory = productPromoCategory.getRelatedOne("ProductCategory", true)>
            <div>
                -&nbsp;<a class="linkcolor" href="<@ofbizUrl>category/~category_id=${productPromoCategory.productCategoryId}</@ofbizUrl>" class="buttontext">${(productCategory.description)?default(productPromoCategory.productCategoryId)}</a>
                <#if productPromoCategory.includeSubCategories?if_exists = "Y">(${uiLabelMap.OrderIncludeSubCategories})</#if>
            </div>
          </#list>
        </#if>
        <#if productPromoCategoryExcludeList?has_content>
          <div>${uiLabelMap.OrderExcludeCategories}</div>
          <#list productPromoCategoryExcludeList as productPromoCategory>
            <#assign productCategory = productPromoCategory.getRelatedOne("ProductCategory", true)>
            <div>
                -&nbsp;<a class="linkcolor" href="<@ofbizUrl>category/~category_id=${productPromoCategory.productCategoryId}</@ofbizUrl>" class="buttontext">${(productCategory.description)?default(productPromoCategory.productCategoryId)}</a>
                <#if productPromoCategory.includeSubCategories?if_exists = "Y">(${uiLabelMap.OrderIncludeSubCategories})</#if>
            </div>
          </#list>
        </#if>
        <#if productPromoCategoryAlwaysList?has_content>
          <div>${uiLabelMap.OrderAlwaysList}</div>
          <#list productPromoCategoryAlwaysList as productPromoCategory>
            <#assign productCategory = productPromoCategory.getRelatedOne("ProductCategory", true)>
            <div>
                -&nbsp;<a class="linkcolor" href="<@ofbizUrl>category/~category_id=${productPromoCategory.productCategoryId}</@ofbizUrl>" class="buttontext">${(productCategory.description)?default(productPromoCategory.productCategoryId)}</a>
                <#if productPromoCategory.includeSubCategories?if_exists = "Y">(${uiLabelMap.OrderIncludeSubCategories})</#if>
            </div>
          </#list>
        </#if>
    </div>
</div>
</#if>
