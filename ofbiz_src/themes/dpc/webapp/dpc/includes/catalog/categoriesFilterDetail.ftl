<script type="text/javascript">
    function callDocumentByPaginate(info) {
        var str = info.split('~');
        var checkUrl = '<@ofbizUrl>categoryAjaxFired</@ofbizUrl>';
        if(checkUrl.search("http"))
            var ajaxUrl = '<@ofbizUrl>categoryAjaxFired</@ofbizUrl>';
        else
            var ajaxUrl = '<@ofbizUrl>categoryAjaxFiredSecure</@ofbizUrl>';

        //jQuerry Ajax Request
        jQuery.ajax({
            url: ajaxUrl,
            type: 'POST',
            data: {"category_id" : str[0], "VIEW_SIZE" : str[1], "VIEW_INDEX" : str[2], "sortOrder" : str[3]},
            error: function(msg) {
                alert("An error occured loading content! : " + msg);
            },
            success: function(msg) {
                jQuery('#content').html(msg);
            }
        });
     }
</script>

<div class="box">
	  <#macro paginationControls>
    <#assign viewIndexMax = Static["java.lang.Math"].ceil((listSize - 1)?double / viewSize?double)>
      <#if (viewIndexMax?int > 0)>
            <#-- End Page Select Drop-Down -->
            <#if (viewIndex?int > 0)>
                <#-- a href="<@ofbizUrl>category/~category_id=${productCategoryId}/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex?int - 1}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonPrevious}</a --> |
                <a href="javascript: void(0);" onclick="callDocumentByPaginate('${productCategoryId}~${viewSize}~${viewIndex?int - 1}~${sortOrder}');" class="buttontext">${uiLabelMap.CommonPrevious}</a> |
            </#if>
            <#if ((listSize?int - viewSize?int) > 0)>
                <span>${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
            </#if>
            <#if highIndex?int < listSize?int>
             <#-- | <a href="<@ofbizUrl>category/~category_id=${productCategoryId}/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex?int + 1}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonNext}</a -->
             | <a href="javascript: void(0);" onclick="callDocumentByPaginate('${productCategoryId}~${viewSize}~${viewIndex?int + 1}~${sortOrder}');" class="buttontext">${uiLabelMap.CommonNext}</a>
            </#if>
 </#if>
</#macro>
	 <div class="product-filter"><b>
	    <div class="display"><b>Display:</b> <a title="List" class="grid-icon" onclick="display('list');">List</a><span class="list1-icon">Grid</span></div>
			<#if showFilter?exists>
				<form name="formFilter" action="<@ofbizUrl>category/~category_id=${productCategoryId}</@ofbizUrl>" method="post" style="margin:0px">
					<input type=hidden name="sortAscending" value="${sortAscending?if_exists}"/>
				    <div class="limit">
						${uiLabelMap.CommonShow}:
						<select name="viewSize" onchange="javascript:form.submit();">
							<option value="8" <#if viewSize?int==8>selected="selected"</#if>>8</option>
							<option value="16" <#if viewSize?int==16>selected="selected"</#if>>16</option>
							<option value="20" <#if viewSize?int==20>selected="selected"</#if>>20</option>
							<option value="24" <#if viewSize?int==24>selected="selected"</#if>>24</option>
							<option value="32" <#if viewSize?int==32>selected="selected"</#if>>32</option>
							<option value="40" <#if viewSize?int==40>selected="selected"</#if>>40</option>
						</select>
					</div>
				    <div class="sort"><b>${uiLabelMap.CommonSortedBy}:</b>
				      <select name="sortOrder" onchange="javascript:document.formFilter.submit();">
				        <option value="" <#if sortOrder?has_content && sortOrder.equals("SortKeywordRelevancy")>selected="selected"</#if>>${uiLabelMap.EcommerceDefault}</option>
				        <option value="SortProductField:productName" <#if sortOrder?has_content && sortOrder.equals("SortProductField:productName")>selected="selected"</#if>>${uiLabelMap.ProductProductName}</option>
				        <option value="SortProductField:totalQuantityOrdered" <#if sortOrder?has_content && sortOrder.equals("SortProductField:totalQuantityOrdered")>selected="selected"</#if>>${uiLabelMap.ProductPopularityByOrders}</option>
				        <option value="SortProductField:totalTimesViewed" <#if sortOrder?has_content && sortOrder.equals("SortProductField:totalTimesViewed")>selected="selected"</#if>>${uiLabelMap.ProductPopularityByViews}</option>
				        <option value="SortProductField:averageCustomerRating" <#if sortOrder?has_content && sortOrder.equals("SortProductField:averageCustomerRating")>selected="selected"</#if>>${uiLabelMap.ProductCustomerRating}</option>
				        <option value="SortProductPrice:DEFAULT_PRICE" <#if sortOrder?has_content && sortOrder.equals("SortProductPrice:DEFAULT_PRICE")>selected="selected"</#if>>${uiLabelMap.ProductDefaultPrice}</option>
				      </select>
				    </div>
				</form>
			</#if>
	 </div>
  <#-- END	${screens.render("component://bigshop/widget/CatalogScreens.xml#categorydetailFilter")} -->
	<#if productIds?has_content>
	    <#-- Pagination -->
	    <#if paginateEcommerceStyle?exists>
	        <@paginationControls/>
	    <#else>
		    <#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
	        <#assign commonUrl = "category?category_id="+ parameters.category_id?if_exists + "&"/>
	        <#--assign viewIndex = viewIndex - 1/-->
	        <#assign viewIndexFirst = 0/>
	        <#assign viewIndexPrevious = viewIndex - 1/>
	        <#assign viewIndexNext = viewIndex + 1/>
	        <#assign viewIndexLast = Static["java.lang.Math"].floor(listSize/viewSize)/>
	        <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount", lowIndex, "highCount", highIndex, "total", listSize)/>
	        <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
	        <@nextPrev commonUrl=commonUrl ajaxEnabled=false javaScriptEnabled=false paginateStyle="nav-pager" paginateFirstStyle="nav-first" viewIndex=viewIndex highIndex=highIndex listSize=listSize viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel="" paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl="" ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel="" paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
	    </#if>
		<ul class="homeproduct">
	        <#list productIds as productId>
	            ${setRequestAttribute("optProductId", productId)}
	            ${setRequestAttribute("listIndex", productId_index)}
	            ${screens.render(productsummaryScreen)}
	        </#list>
        </ul>
	   <#if paginateEcommerceStyle?exists>
        <@paginationControls/>
       </#if>
	<#else>
	    <hr />
	    <div>${uiLabelMap.ProductNoProductsInThisCategory}</div>
	</#if>
</div>
<script type="text/javascript">
function display(view) {
	if (view == 'list') {
		$('.product-grid').attr('class', 'product-list');

		$('.product-list > div').each(function(index, element) {
			html  = '<div class="right">';
			html += '  <div class="cart">' + $(element).find('.cart').html() + '</div>';
			html += '  <div class="wishlist">' + $(element).find('.wishlist').html() + '</div>';
			html += '  <div class="compare">' + $(element).find('.compare').html() + '</div>';
			html += '</div>';

			html += '<div class="left">';

			var image = $(element).find('.image').html();

			if (image != null) {
				html += '<div class="image">' + image + '</div>';
			}

			var price = $(element).find('.price').html();

			if (price != null) {
				html += '<div class="price">' + price  + '</div>';
			}

			html += '  <div class="name">' + $(element).find('.name').html() + '</div>';
			html += '  <div class="description">' + $(element).find('.description').html() + '</div>';

			var rating = $(element).find('.rating').html();

			if (rating != null) {
				html += '<div class="rating">' + rating + '</div>';
			}

			html += '</div>';


			$(element).html(html);
		});

		$('.display').html('<b>Display:</b> <span class="grid1-icon">List</span> <a title="Grid" class="list-icon" onclick="display(\'grid\');">Grid</a>');

		$.cookie('display', 'list');
	} else {
		$('.product-list').attr('class', 'product-grid');

		$('.product-grid > div').each(function(index, element) {
			html = '';

			var image = $(element).find('.image').html();

			if (image != null) {
				html += '<div class="image">' + image + '</div>';
			}

			html += '<div class="name">' + $(element).find('.name').html() + '</div>';
			html += '<div class="description">' + $(element).find('.description').html() + '</div>';

			var price = $(element).find('.price').html();

			if (price != null) {
				html += '<div class="price">' + price  + '</div>';
			}

			var rating = $(element).find('.rating').html();

			if (rating != null) {
				html += '<div class="rating">' + rating + '</div>';
			}

			html += '<div class="cart">' + $(element).find('.cart').html() + '</div>';
			html += '<div class="wishlist">' + $(element).find('.wishlist').html() + '</div>';
			html += '<div class="compare">' + $(element).find('.compare').html() + '</div>';

			$(element).html(html);
		});

		$('.display').html('<b>Display:</b> <a title="List" class="grid-icon" onclick="display(\'list\');">List</a><span class="list1-icon">Grid</span>');

		$.cookie('display', 'grid');
	}
}

view = $.cookie('display');

if (view) {
	display(view);
} else {
	display('grid');
}
</script>