<#if product?exists>
	<#assign productId = product.productId />
	<#else>
		<#assign productId = parameters.productId />
		<#assign productStoreId = parameters.productStoreId />
</#if>
<#assign productReviews = Static["com.olbius.basepo.product.ProductContentUtils"].productReviews(delegator, userLogin, productId, productStoreId)/>
<div class='comment-list'>
	<#if productReviews?has_content>
		<#list productReviews as review>
			<#if review.userName?exists>
				<#assign shortcut = review.userName?substring(0, 1)/>
				<#else>
				<#assign shortcut = "G"/>
			</#if>
			<div class='comment-item'>
				<div class='comment-icon'>${shortcut}</div>
				<div class='comment-wrapper'>
					<div class='comment-header'>
					
						<div class="user-rated">${StringUtil.wrapString((review.userName)?if_exists)}</div>
						<div class="sub-title">${uiLabelMap.BEHasRatedProduct}</div>
						<div class="star-rated">
							<select id="rate${review.productReviewId}" style="float: right">
								<option value=""></option>
								<option value="10">10</option>
								<option value="20">20</option>
								<option value="30">30</option>
								<option value="40">40</option>
								<option value="50">50</option>
							</select>
						</div>
						<script>
							(function($) {
								$('#rate${review.productReviewId}').barrating({
								    theme: 'fontawesome-stars',
								    readonly: true
							    });
								if ("${(review.productRating)?if_exists}") {
									var averageRating = "${(review.productRating)?if_exists}";
									$('#rate${review.productReviewId}').barrating('set', averageRating);
								}
							})(jQuery);
						</script>
						
					</div>
					<#if review.productReview?exists>
					<div class='comment-content'>
						${StringUtil.wrapString((review.productReview)?if_exists)}
					</div>
					</#if>
				</div>
			</div>
		</#list>
		<#else>
			${uiLabelMap.BENoReview}
	</#if>
</div>