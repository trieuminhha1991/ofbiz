<ul class="mobile-menu">
	<#assign catCount = 0/>
	<#if cataCatList?exists>
		<#list cataCatList as proCat>
		<#assign catCount=catCount+1/>
		<li class="mobile-menu-item">
			<a href="<@ofbizUrl>productCategoryList?catId=${proCat.category.productCategoryId}</@ofbizUrl>"
			title="${proCat.category.categoryName?if_exists}">
			<div class='mobile-menu-content'>
				<i class="${(proCat.category.icon)?if_exists}">&nbsp;</i>&nbsp;
				${proCat.category.categoryName?if_exists}
			</div> </a>
			<#assign catChild = proCat.completedTree/>
			<#if catChild?is_collection>
			<div class='mobile-menu-child'>
				<#list catChild as catcc>
				<ul class='mobile-menu mobile-child-menu'>
					<li class='mobile-menu-item'>
						<a href="<@ofbizUrl>productCategoryList?catId=${catcc.productCategoryId}</@ofbizUrl>"
							title="${catcc.categoryName?if_exists}">
							<div class='mobile-menu-content'>
								<i class="${(catcc.icon)?if_exists}">&nbsp;</i>&nbsp;
								${catcc.categoryName?if_exists}
							</div>
						</a>
					</li>
				</ul>
				</#list>
			</div>
			</#if>
		</li>
		</#list>
	</#if>
</ul>
