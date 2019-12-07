<#if device?if_exists == "desktop">
	<#assign limit = 4/>
</#if>
<#if categoryContentWrapper?has_content>
	<#assign categoryName = categoryContentWrapper.get("CATEGORY_NAME")?if_exists/>
	<#if productCategoryMembers?has_content>
		<#list productCategoryMembers as productCategoryMember>
			<#if limit?exists && productCategoryMember_index == limit>
				<#break />
			</#if>
			${setRequestAttribute("optProductId", productCategoryMember.productId)}
			${screens.render("component://obb/widget/CatalogScreens.xml#productsummarymini")}
		</#list>
	</#if>
</#if>
