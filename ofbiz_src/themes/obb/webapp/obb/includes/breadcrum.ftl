<div id="jm-breadcrumbs" class="wrap clearfix">
	<div class="main clearfix">
		<div class="inner clearfix">
			<div id="jm-pathway" class="clearfix">
				<h4 class="no-display">You're currently on:</h4>
				<ul class="breadcrumbs">
					<li class="home">
					  <a href="<@ofbizUrl>main</@ofbizUrl>" title="${uiLabelMap.BEGoToHomePage}">${uiLabelMap.BEHome}</a>
					</li>
					<#if categoryRollUp?exists>
						<#list categoryRollUp as cate>
							<li <#if !cate_has_next>class="breadcrumbs_end"</#if>>
							  <i class="fa fa-caret-right"></i>
							  <a href="<@ofbizUrl>productCategoryList?catId=${cate.productCategoryId}</@ofbizUrl>" title="">&nbsp;${cate.getString("categoryName")}</a>
							</li>
						</#list>
					</#if>
					<#if catalog?exists>
						<li class="breadcrumbs_end">
						  <i class="fa fa-caret-right"></i>
						  <a href="<@ofbizUrl>productCategoryList?catId=${cate.productCategoryId}</@ofbizUrl>" title="">&nbsp;${catalog.getString("catalogName")}</a>
						</li>
					</#if>
				</ul>
			</div>
		</div>
	</div>
</div>
