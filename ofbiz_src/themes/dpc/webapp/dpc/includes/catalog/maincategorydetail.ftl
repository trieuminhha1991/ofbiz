<#if productCategoryMembers?has_content>
      <ul class="homeproduct">
        <#list productCategoryMembers as productCategoryMember>
            ${setRequestAttribute("optProductId", productCategoryMember.productId)}
            ${setRequestAttribute("productCategoryMember", productCategoryMember)}
            ${setRequestAttribute("listIndex", productCategoryMember_index)}
            ${screens.render(productsummaryScreen)}
        </#list>
      </ul>
<#else>
    <hr />
    <center>${uiLabelMap.ProductNoProductsInThisCategory}</center>
</#if>

