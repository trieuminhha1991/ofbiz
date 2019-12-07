<#assign currencyUomId = "VND"/>
<div class="block block-layered-nav ">
	<div class="block-title">
		<strong><span>${uiLabelMap.BEBrands}</span></strong>
	</div>
	<div class="block-content">
	<dl id="narrow-by-list" class="narrow-by-list scroll-inside">
			<dd class="even">
				<ol>
					<li>
						<a class="brandtag pointer <#if !(parameters.brand)?exists>brand-sorted</#if>" >${uiLabelMap.BEAllBrands} (${brTotalSize?if_exists})</a>
					</li>
					<#if brands?exists>
						<#list brands as brand>
							<li>
								<a class="brandtag pointer <#if (parameters.brand)?exists><#if parameters.brand=="${brand.brandName}">brand-sorted</#if></#if>" data-brand="${brand.brandName}">
									${(brand.groupName)?if_exists} (${(brand.count)?if_exists})
								</a>
							</li>
						</#list>
					</#if>
				</ol>
			</dd>
        </dl>
    </div>
</div>
<script>
	<#if catalogId?exists>
		var catalogId = '${catalogId}';
		var catId;
	<#elseif productCategoryId?exists>
		var catalogId;
		var catId = '${productCategoryId}';
	</#if>
</script>
<script type="text/javascript" src="/obbresources/js/filter.js"></script>

<link rel="stylesheet" type="text/css" href="/obbresources/asset/perfect-scrollbar/css/perfect-scrollbar.min.css">
<script type="text/javascript" src="/obbresources/asset/perfect-scrollbar/js/perfect-scrollbar.jquery.js"></script>
<script type="text/javascript" src="/obbresources/asset/perfect-scrollbar/js/perfect-scrollbar.js"></script>
<script>
	$(document).ready(function() {
		(function($) {
			$(".scroll-inside").perfectScrollbar();
		})(jQuery);
	});
</script>