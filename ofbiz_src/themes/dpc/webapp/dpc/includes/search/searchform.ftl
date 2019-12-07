<div id="search">
	<form  name="keywordsearchform" method="post" action="<@ofbizUrl>keywordsearch</@ofbizUrl>">
		<input type="hidden" name="VIEW_SIZE" value="10" />
		<input type="hidden" name="PAGING" value="Y" />
		<#if productCategory?exists>
		<input type="hidden" name="SEARCH_CATEGORY_ID" value="${productCategory.productCategoryId?if_exists}" />
		</#if>
		<button type="submit" class="button-search"></button>
		<input type="text" value="" placeholder="" id="filter_name" name="SEARCH_STRING">
	</form>
</div>