<div class="navinews">
	<h1 class='navi-header'>
		<a href="<@ofbizUrl>newcontent</@ofbizUrl>" title="">
			${uiLabelMap.BEContentCategory}
		</a>
	</h1>
	<div class='navi-content'>
		<ul class="navinews">
			<#if contentCategories?exists>
				<#list contentCategories as category>
				<li>
					<a class='navinews-link'
						href="<@ofbizUrl>contentcategory?catContentId=${category.contentCategoryId}</@ofbizUrl>">
						<i class="${category.icon?if_exists}"></i>&nbsp;${category.categoryName}
					</a>
					<#assign contents = category.contents/>
						<#if contents?has_content>
					<ul class='navinews level2'>
						<#list contents as content>
						<li>
							<a class='navinews-link'
								href="<@ofbizUrl>viewcontent?cId=${content.contentId}</@ofbizUrl>">
								${content.contentName}
							</a>
						</li>
						</#list>
					</ul>
					</#if>
				</li>
				</#list>
			</#if>
		</ul>
	</div>
</div>
