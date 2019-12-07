<div class="wbread">
	<ul class="breadcrumb">
	<#assign isDefaultTheme = !layoutSettings.VT_FTR_TMPLT_LOC?contains("multiflex")>
	<#if isDefaultTheme>
		<li itemscope="itemscope" itemtype="http://data-vocabulary.org/Breadcrumb">
			<a title="Trang chủ" href="<@ofbizUrl>main</@ofbizUrl>" itemprop="url"><i class="allicon-home"></i></a><span class="separator">›</span>
		</li>
	<#else>
	    <li itemscope="itemscope" itemtype="http://data-vocabulary.org/Breadcrumb">
			<a title="Trang chủ" href="<@ofbizUrl>main</@ofbizUrl>" itemprop="url"><i class="allicon-home"></i></a><span class="separator">›</span>
		</li>
	</#if>
	    <#-- Show the category branch -->
	    <#assign crumbs = Static["org.ofbiz.product.category.CategoryWorker"].getTrail(request)/>
	    <#list crumbs as crumb>
	         <#if catContentWrappers?exists && catContentWrappers[crumb]?exists>
	            <#if !isDefaultTheme>
			<li itemscope="itemscope" itemtype="http://data-vocabulary.org/Breadcrumb">
						<a title="TPCN giảm cân" rel="category tag" href="#" itemprop="url">
							 <#if catContentWrappers[crumb].get("CATEGORY_NAME")?exists>
		                     ${catContentWrappers[crumb].get("CATEGORY_NAME")}
		                   <#elseif catContentWrappers[crumb].get("DESCRIPTION")?exists>
		                     ${catContentWrappers[crumb].get("DESCRIPTION")}
		                   <#else>
		                     ${crumb}
		                   </#if>
	                   </a><span class="separator">›</span>
					</li>
	            <#else>
			<li itemscope="itemscope" itemtype="http://data-vocabulary.org/Breadcrumb">
						<#if catContentWrappers[crumb].get("CATEGORY_NAME")?exists>
		                   ${catContentWrappers[crumb].get("CATEGORY_NAME")}
		                 <#elseif catContentWrappers[crumb].get("DESCRIPTION")?exists>
		                   ${catContentWrappers[crumb].get("DESCRIPTION")}
		                 <#else>
		                   ${crumb}
		                 </#if>
					</li>
	               <#if crumb_has_next> &gt;</#if>
	            </#if>
	            <#assign previousCategoryId = crumb />
	         </#if>
	    </#list>
	    <#-- Show the product, if there is one -->
	    <#if productContentWrapper?exists>
	      <#if isDefaultTheme>
		<li itemscope="itemscope" itemtype="http://data-vocabulary.org/Breadcrumb">
				<a>${productContentWrapper.get("PRODUCT_NAME")?if_exists}</a>
			</li>
	      <#else>
		<li itemscope="itemscope" itemtype="http://data-vocabulary.org/Breadcrumb">
				<a>${productContentWrapper.get("PRODUCT_NAME")?if_exists}</a>
			</li>
	      </#if>
	    </#if>
	</ul>
</div>