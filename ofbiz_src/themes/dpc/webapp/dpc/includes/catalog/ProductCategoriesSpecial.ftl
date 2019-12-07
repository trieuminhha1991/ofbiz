<#assign categoryName = categoryContentWrapper.get("CATEGORY_NAME")?if_exists/>
<div class="box">
	<div class="box-heading">
		<span>${categoryName}</span>
		<div class="pagers">
			<div class="btn-toolbar">
				<div class="btn-group">
					<button class="vNextSpecialSp btnc" type="button"><img src="/bigshop/images/previous.png"/></button>
					<button class="vPrevSpecialSp btnc" type="button"><img src="/bigshop/images/next.png"/></button>
				</div>
			</div>
		</div>
	</div>
	<div class="box-content">
		<div class="box-product">
			<#if productCategoryMembers?has_content>
				<ul class="vProductItems cycle-slideshow vertical clearfix"
											    data-cycle-fx="carousel"
											    data-cycle-timeout=2000
											    data-cycle-slides="> li"
											    data-cycle-next=".vPrevSpecialSp"
											    data-cycle-prev=".vNextSpecialSp"
											    data-cycle-carousel-visible="2"
											    data-cycle-carousel-vertical="true"
											    >
					<#list productCategoryMembers as productCategoryMember>
						${setRequestAttribute("optProductId", productCategoryMember.productId)}
					    ${setRequestAttribute("productCategoryMember", productCategoryMember)}
					    ${setRequestAttribute("listIndex", productCategoryMember_index)}
						${screens.render(productLeftShowScreen)}
					</#list>
				</ul>
			<#else>
			<hr />
			<span class="pull-left">${uiLabelMap.ProductNoProductsInThisCategory}</span>
			</#if>
		</div>
	</div>
</div>
