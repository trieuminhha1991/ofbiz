<#include "/obb/webapp/obb/includes/season/background.ftl"/>

<!-- BEGIN: HEADER -->
<h1 id="logo"><a href="<@ofbizUrl>main</@ofbizUrl>" title="${uiLabelMap.ObbSiteName}" style="background-image: url(/obbresources/images/bhappy-05.png) !important;background-size:253px 72px;">${uiLabelMap.ObbSiteName}</a></h1>
<p class="no-display"><a href="#main"><strong>${uiLabelMap.BESkipContent} »</strong></a></p>

<!-- BEGIN: SITE SEARCH -->
<div id="jm-search" class="has-toggle">
	<div class="btn-toggle search-toggle">
		<i class="fa fa-search"></i>
	</div>
	<div class="inner-toggle">
		<form id="search_mini_form" action="<@ofbizUrl>keywordsearch</@ofbizUrl>" method="GET">
			<input type="hidden" name="VIEW_SIZE" value="20">
			<input type="hidden" name="hoz" value="Y">
			<div class="jm-search-category">
				<div class="select-box lits-category hidden-mobile">
					<div class='search-hidden' id="searchHidden"></div>
					<select name="SEARCH_CATALOG_ID" id="category_search_field">
						<option value="#" selected>${uiLabelMap.BEAllCategory}</option>
						<#if context.cataCatList?exists>
							<#list context.cataCatList as proCat>
								<option value="${proCat.category.productCategoryId}">${proCat.category.categoryName?if_exists}</option>
							</#list>
						</#if>
					</select>
				</div>
				<div class="form-search">
					<input type="search" maxlength="128" class="input-text" id="searchInput" title="Search" placeholder="${uiLabelMap.BESearchHomePlaceholder}" value="" name="name">
					<button type="button" title="${uiLabelMap.ObbSearch}" id="searchButton" class="button hidden-mobile" onclick="onSearch()"><span><span>${uiLabelMap.ObbSearch}</span></span></button>
				</div>
			</div>
		</form>
	</div>
</div>
<script>
    function onSearch() {
        var keyword = jQuery("#searchInput").val();
        if(keyword){
            jQuery("#search_mini_form").submit();
        } else {
        	jQuery("#searchInput").focus();
		}
    }
    function resizeSearchBox(obj){
	var parentWidth = $('.jm-search-category').width();
		var val = obj.find(":selected").text();
		$('#searchHidden').text(val);
		var width = $('#searchHidden').outerWidth();
		obj.parent().width(width + 5);
		var re = parentWidth - $('.select-box').outerWidth();
		var button = $('#searchButton');
		var input = $('#searchInput');
		var outInput = input.outerWidth() - input.width() - 1;
		var buttonWidth = 2 * button.outerWidth() - button.innerWidth();
		input.width(re - buttonWidth - outInput - 5);
		$('.form-search').width(re);
    }
    $(document).ready(function(){
	$('#category_search_field').change(function(){
		var obj = $(this);
		resizeSearchBox(obj);
	});
	$(window).resize(function(){
		resizeSearchBox($('#category_search_field'));
	});
    });
</script>
<!-- END: SITE SEARCH -->
