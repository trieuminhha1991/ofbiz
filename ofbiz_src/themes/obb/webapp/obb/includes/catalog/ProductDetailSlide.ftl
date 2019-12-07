<#if device?if_exists == "mobile">
	<#assign limit = 4/>
</#if>
<#assign limit = 4/>
<#if categoryContentWrapper?has_content>
	<#assign categoryName = (categoryContentWrapper.get("CATEGORY_NAME"))?if_exists/>
	<#if productCategoryMembers?has_content>
		<#list productCategoryMembers as productCategoryMember>
			<#if limit?exists && productCategoryMember_index == limit>
				<#break />
			</#if>
			${setRequestAttribute("optProductId", productCategoryMember.productId)}
		    ${setRequestAttribute("productCategoryMember", productCategoryMember)}
		    ${setRequestAttribute("listIndex", productCategoryMember_index)}
			${screens.render("component://obb/widget/CatalogScreens.xml#productsummaryminislide")}
		</#list>
	</#if>
</#if>
