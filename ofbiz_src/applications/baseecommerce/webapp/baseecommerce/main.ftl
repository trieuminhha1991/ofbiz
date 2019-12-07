<#-- Render the category page -->
<#if requestAttributes.productCategoryId?has_content>
<#--   ${screens.render("component://ecommerce/widget/CatalogScreens.xml#bestSellingCategory")}
  ${screens.render("component://obb/widget/CatalogScreens.xml#category-include")} -->
<#else>
  <center><h2>${uiLabelMap.EcommerceNoPROMOTIONCategory}</h2></center>
</#if>
