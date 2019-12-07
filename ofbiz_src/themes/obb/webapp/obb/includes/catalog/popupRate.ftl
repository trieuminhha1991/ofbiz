<script type="text/javascript" src="/obbresources/asset/js/jquery.barrating.min.js"></script>

<div id="dialogRate" class="popover editable-container editable-popup fade in">
	<div class="arrow"></div>
	<h3 class="popover-title">${uiLabelMap.BEReview}<i id="closeDialog" class="fa fa-times pull-right pointer"></i></h3>
	<div class="popover-content">
		<div class="editableform-loading" style="display: none;"></div>
		
		<#if security.hasEntityPermission("PRODUCT", "_REVIEW", session)>
			<form id="reviewProduct" class="form-inline editableform" method="post" action="<@ofbizUrl>createProductReview</@ofbizUrl>">
				<input type="hidden" name="productStoreId" value="${productStore.productStoreId}" />
				<input type="hidden" name="productId" value="" />
				<input type="hidden" name="product_id" value="" />
				<input type="hidden" name="category_id" value="" />
				<div class="row">
					<div class="col-lg-5 no-padding-right">
						<select id="slRate" name="productRating">
							<option value=""></option>
							<option value="10">10</option>
							<option value="20">20</option>
							<option value="30">30</option>
							<option value="40">40</option>
							<option value="50">50</option>
						</select>
					</div>
					<div class="col-lg-7 no-padding-left">
						<label id="slRateValue" class="blue"></label>
					</div>
				</div>	
				<div class="row">
					<div class="col-lg-12">
						<textarea class="area-review full-width-input" name="productReview" placeholder="${uiLabelMap.BEAddYourReview}..."></textarea>
					</div>
				</div>
				<div id="btnRate" class="row margin-top10" style="display: none;">
					<div class="col-lg-12">
						<button class='btn-mini btn-primary pull-right'>
							<i class='fa fa-check'>&nbsp;</i>&nbsp;${uiLabelMap.BESend}
						</button>
					</div>
				</div>
			</form>
			<#else>
			${uiLabelMap.BEYouMust}&nbsp;<a href="<@ofbizUrl>checkLogin/main</@ofbizUrl>">${uiLabelMap.ObbLogIn}</a>&nbsp;${uiLabelMap.BEToReviewThisItem}
		</#if>
		
	</div>
</div>

<script>
	function openRate(e, productId, categoryId) {
		$("#dialogRate").removeClass("left");
		$("#dialogRate").removeClass("right");
		if (($(window).width() - $(e).offset().left) > 500) {
			$("#dialogRate").addClass("right");
			$("#dialogRate").dialog({
				  position: { my: "left+30 top-100", of: e  },
			});
		} else {
			$("#dialogRate").addClass("left");
			$("#dialogRate").dialog({
				  position: { my: "right-100 top-100", of: e  },
			});
		}
		$("input[name='productId']").val(productId);
		$("input[name='product_id']").val(productId);
		$("input[name='category_id']").val(categoryId);
	}
	$(document).ready(function() {
		$("#closeDialog").click(function() {
			if ($("#btnRate").is(":visible")) {
				$("#btnRate").slideUp(1);
			}
			$("textarea[name='productReview']").val("");
			$("#dialogRate").dialog('destroy');
			jQuery('#slRate').barrating('clear');
		});
		(function($) {
			$('#slRate').barrating({
			    theme: 'fontawesome-stars',
			    hoverState: true
		    }).change(function() {
		    	if ($(this).val()) {
		    		if (!$("#btnRate").is(":visible")) {
		    			$("#btnRate").slideDown(200);
					}
				} else {
					if ($("#btnRate").is(":visible")) {
						$("#btnRate").slideUp(200);
					}
				}
		    	switch ($(this).val()) {
				case "10":
					$("#slRateValue").text("${StringUtil.wrapString(uiLabelMap.BEBad)}");
					break;
				case "20":
					$("#slRateValue").text("${StringUtil.wrapString(uiLabelMap.BEMediocre)}");
					break;
				case "30":
					$("#slRateValue").text("${StringUtil.wrapString(uiLabelMap.BEOrdinary)}");
					break;
				case "40":
					$("#slRateValue").text("${StringUtil.wrapString(uiLabelMap.BEGood)}");
					break;
				case "50":
					$("#slRateValue").text("${StringUtil.wrapString(uiLabelMap.BEAwesome)}");
					break;
				default:
					$("#slRateValue").text("");
					break;
				}
			});
		})(jQuery);
	});
</script>