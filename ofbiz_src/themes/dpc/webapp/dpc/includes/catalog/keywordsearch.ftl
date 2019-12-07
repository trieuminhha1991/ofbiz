 <#macro paginationControls>
    <#assign viewIndexMax = Static["java.lang.Math"].ceil((listSize - 1)?double / viewSize?double)>
      <#if (viewIndexMax?int > 0)>
            <#-- End Page Select Drop-Down -->
            <#if (viewIndex?int > 0)>
                <#-- a href="<@ofbizUrl>category/~category_id=${productCategoryId}/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex?int - 1}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonPrevious}</a --> |
                <b><a href="<@ofbizUrl>keywordsearch/~category_id=CLOTHINGS_PROMOTIONS/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex?int - 1}/~clearSearch=N</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonPrevious}</a> |</b>
            </#if>
            <#if ((listSize?int - viewSize?int) > 0)>
                <b>${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</b>
            </#if>
            <#if highIndex?int < listSize?int>
             <#-- | <a href="<@ofbizUrl>category/~category_id=${productCategoryId}/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex?int + 1}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonNext}</a -->
             <b>| <a href="<@ofbizUrl>keywordsearch/~category_id=CLOTHINGS_PROMOTIONS/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex+1}/~clearSearch=N</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonNext}</a></b>
            </#if>
 </#if>
</#macro>
<div id="cartdiv">
		<h2>${uiLabelMap.ProductYouSearchedFor}:</h2>
		<table>
			<tr>
				<#list searchConstraintStrings as searchConstraintString>
				    <td><a href="<@ofbizUrl>keywordsearch?removeConstraint=${searchConstraintString_index}&amp;clearSearch=N</@ofbizUrl>" class="buttontext">X</a>&nbsp;<b>${searchConstraintString}</b></td>
				</#list>
			</tr>
		</table>
	<br />
	<div>${uiLabelMap.CommonSortedBy}: ${searchSortOrderString}</div>
	<br />
	<#if !productIds?has_content>
	  <h3>&nbsp;${uiLabelMap.ProductNoResultsFound}.</h3>
	</#if>
		<#if productIds?has_content>
			<div class="product-filter"><b>
		    <div class="display"><b>Display:</b> <a title="List" class="grid-icon" onclick="display('list');">List</a><span class="list1-icon">Grid</span></div>
		 </div>
			<@paginationControls/>
			<div class="product-grid">

		        <#list productIds as productId> <#-- note that there is no boundary range because that is being done before the list is put in the content -->
		            ${setRequestAttribute("optProductId", productId)}
		            ${setRequestAttribute("listIndex", productId_index)}
		            ${screens.render(productsummaryScreen)}
		        </#list>
			</div><!--end row-->
		</#if>
	<@paginationControls/>
</div><!--end featuredItems-->
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
