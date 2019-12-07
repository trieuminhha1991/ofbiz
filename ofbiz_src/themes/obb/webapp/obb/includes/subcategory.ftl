<#if listChildrenCategory?exists>
	<div class="block block-layered-nav ">
		<div class="block-title">
			<strong><span>${uiLabelMap.BECategory}</span></strong>
		</div>
		<div class="block-content">
			<dl id="narrow-by-list" class="narrow-by-list scroll-inside">
                <dd class="odd">
                    <ol>
						<#list listChildrenCategory as cate>
							<li>
								<a href="<@ofbizUrl>productCategoryList?catId=${cate.get("productCategoryId")}</@ofbizUrl>">
									<span class="sidebarcatname">${cate.get("categoryName")}</span>
								</a>
							</li>
						</#list>
					</ol>
				</dd>
            </dl>
        </div>
	</div>
</#if>