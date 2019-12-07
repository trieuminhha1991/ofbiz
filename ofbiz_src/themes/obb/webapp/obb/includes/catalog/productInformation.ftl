<script type="text/javascript" src="/obbresources/asset/js/scroll-detect.js"></script>
<script type="text/javascript" src="/obbresources/asset/js/jquery.barrating.min.js"></script>


<div id="product_description">
	<ul id="product-nav" class="nav nav-tabs responsive">
		<li>
			<a data-toggle="tab" href="#tab_content_product_introduction">
				${uiLabelMap.BSProductIntroduction}
	        </a>
        </li>
	    <li <#if !(parameters.rev)?exists>class="active"</#if>>
	    	<a data-toggle="tab" href="#tab_content_product_specifications">
	    		${uiLabelMap.BSProductSpecifications}
	        </a>
	    </li>
	    <li <#if (parameters.rev)?exists>class="active"</#if>>
		    <a data-toggle="tab" href="#tab_content_product_review">
		    	${uiLabelMap.BEReview}
		    </a>
	    </li>
	</ul>
	
	<div id="product-content" class="tab-content responsive">
		<div id="tab_content_product_introduction" class="tab-pane fade">
			<div class="std">
				<#assign productIntroduction = Static["com.olbius.basepo.product.ProductContentUtils"].productIntroduction(delegator, product.productId, parameters.contentId)/>
				<#if Static["org.ofbiz.base.util.UtilValidate"].isNotEmpty(productIntroduction)>
					${StringUtil.wrapString((productIntroduction.get("longDescription"))?if_exists)}
					<#else>
					${uiLabelMap.BENoIntroduction}
				</#if>
			</div>
		</div>
		<div id="tab_content_product_specifications" class="tab-pane fade <#if !(parameters.rev)?exists> in active</#if>">
		
		<#assign productSpecifications = Static["com.olbius.basepo.product.ProductContentUtils"].loadProductSpecifications(delegator, webSiteId, product.productId)/>
		
		    <table class="table table-striped table-bordered" id="tblGeneralAttribute">
		    <tbody>
				<#if (product.brandName)?exists>
				<#assign brandName = product.getRelatedByAnd("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "brandName"))?if_exists>
				<#if brandName?has_content>
					<tr><td class="col-md-3"><strong>${uiLabelMap.ProductBrandName}:</strong></td>
					<td class="col-md-9">${StringUtil.wrapString((brandName.get(0).groupName)?if_exists)}</td></tr>
				</#if>
				</#if>
				<#if (productSpecifications.effects)?exists>
				<#if StringUtil.wrapString(productSpecifications.effects) != "<div></div>">
					<tr><td class="col-md-3"><strong>${uiLabelMap.BECEffects}:</strong></td>
					<td class="col-md-9">${StringUtil.wrapString((productSpecifications.effects)?if_exists)}</td></tr>
				</#if>
				</#if>
				<#if (productSpecifications.composition)?exists>
				<#if StringUtil.wrapString(productSpecifications.composition) != "<div></div>">
					<tr><td class="col-md-3"><strong>${uiLabelMap.BECComposition}:</strong></td>
					<td class="col-md-9">${StringUtil.wrapString((productSpecifications.composition)?if_exists)}</td></tr>
				</#if>
				</#if>
				<#if (productSpecifications.shelfLife)?exists>
				<#if StringUtil.wrapString(productSpecifications.shelfLife) != "<div></div>">
					<tr><td class="col-md-3"><strong>${uiLabelMap.BECShelfLife}:</strong></td>
					<td class="col-md-9">${StringUtil.wrapString((productSpecifications.shelfLife)?if_exists)}</td></tr>
				</#if>
				</#if>
				<#if (productSpecifications.users)?exists>
				<#if StringUtil.wrapString(productSpecifications.users) != "<div></div>">
					<tr><td class="col-md-3"><strong>${uiLabelMap.BECUsers}:</strong></td>
					<td class="col-md-9">${StringUtil.wrapString((productSpecifications.users)?if_exists)}</td></tr>
				</#if>
				</#if>
				<#if (productSpecifications.instructions)?exists>
				<#if StringUtil.wrapString(productSpecifications.instructions) != "<div></div>">
					<tr><td class="col-md-3"><strong>${uiLabelMap.BECInstructions}:</strong></td>
					<td class="col-md-9">${StringUtil.wrapString((productSpecifications.instructions)?if_exists)}</td></tr>
				</#if>
				</#if>
				<#if (productSpecifications.license)?exists>
				<#if StringUtil.wrapString(productSpecifications.license) != "<div></div>">
					<tr><td class="col-md-3"><strong>${uiLabelMap.BECLicense}:</strong></td>
					<td class="col-md-9">${StringUtil.wrapString((productSpecifications.license)?if_exists)}</td></tr>
				</#if>
				</#if>
				<#if (productSpecifications.packing)?exists>
				<#if StringUtil.wrapString(productSpecifications.packing) != "<div></div>">
					<tr><td class="col-md-3"><strong>${uiLabelMap.BECPacking}:</strong></td>
					<td class="col-md-9">${StringUtil.wrapString((productSpecifications.packing)?if_exists)}</td></tr>
				</#if>
				</#if>
				<#if (productSpecifications.contraindications)?exists>
				<#if StringUtil.wrapString(productSpecifications.contraindications) != "<div></div>">
					<tr><td class="col-md-3"><strong>${uiLabelMap.BECContraindications}:</strong></td>
					<td class="col-md-9">${StringUtil.wrapString((productSpecifications.contraindications)?if_exists)}</td></tr>
				</#if>
				</#if>
			</tbody>
			</table>
		</div>
		<div id="tab_content_product_review" class="tab-pane fade <#if (parameters.rev)?exists> in active</#if>">
		<div id="reviewIt" class="review-rating">
				<#if security.hasEntityPermission("PRODUCT", "_REVIEW", session)>
					<form id="reviewProduct2" class="form-inline editableform" method="post" action="<@ofbizUrl>createProductReview</@ofbizUrl>">
						<input type="hidden" name="productStoreId" value="${productStoreId}" />
						<input type="hidden" name="productId" value="${product.productId}" />
						<input type="hidden" name="product_id" value="${product.productId}" />
						<input type="hidden" name="category_id" value="${categoryId?if_exists}" />
						<div class="row review-star">
							<div class="col-lg-2 col-md-2 col-sm-2 col-xs-4 no-padding-right">
								<select id="slRateThis" name="productRating">
									<option value=""></option>
									<option value="10">10</option>
									<option value="20">20</option>
									<option value="30">30</option>
									<option value="40">40</option>
									<option value="50">50</option>
								</select>
							</div>
							<div class="col-lg-10 col-md-10 col-sm-8 col-xs-8 no-padding-left">
								<label id="slRateValueThis"></label>
							</div>
						</div>	
						<div class="row review-star">
							<div class="col-lg-12">
								<textarea class="area-review-full-width full-width-input" id="productReview" name="productReview" placeholder="${uiLabelMap.BEAddYourReview}..."></textarea>
							</div>
						</div>
						<div id="btnRateThis" class="row margin-top10" style="display: none;">
							<div class="col-lg-12">
								<button id="btnReview" type="button" class='btn-mini btn-primary pull-right'>
									<i class='fa fa-check'>&nbsp;</i>&nbsp;${uiLabelMap.BESend}
								</button>
							</div>
						</div>
					</form>
					<#else>
					${uiLabelMap.BEYouMust}&nbsp;<a href="<@ofbizUrl>checkLogin/main</@ofbizUrl>">${uiLabelMap.ObbLogIn}</a>&nbsp;${uiLabelMap.BEToReviewThisItem}
				</#if>
			</div>
			<div id="ReviewsContainer">
				${screens.render("component://obb/widget/CatalogScreens.xml#productReviews")}
			</div>	
		</div>
	</div>
</div>

<script type="text/javascript" src="/obbresources/asset/js/jquery.barrating.min.js"></script>
<script>
	$(document).ready(function() {
		$("#btnReview").click(function() {
			$("#ReviewsContainer").html("");
			$.ajax({
		        url: "createProductReviewAjax?" + $("#reviewProduct2").serialize(),
		        type: "POST",
		        async: false,
		    }).done(function(res) {
		    	if (res) {
		    		jQuery('#slRateThis').barrating('clear');
		    		if ($("#btnRateThis").is(":visible")) {
		    			$("#btnRateThis").slideUp(1);
		    		}
					$("#productReview").val("");
					$("#ReviewsContainer").html(res);
				}
			});
		});
	});
	if ("${(parameters.rev)?if_exists}") {
		$("html, body").delay(2000).animate({scrollTop: ($('#tab_content_product_review').offset().top - 200) }, 1000);
	}
	function reviewIt() {
		$("#reviewIt").slideToggle(400);
	}
	jQuery('#slRateThis').barrating({
	    theme: 'fontawesome-stars',
	    hoverState: true
    }).change(function() {
    	if ($(this).val()) {
    		if (!$("#btnRateThis").is(":visible")) {
    			$("#btnRateThis").slideDown(200);
    		}
		} else {
			if ($("#btnRateThis").is(":visible")) {
				$("#btnRateThis").slideUp(200);
			}
		}
    	switch ($(this).val()) {
		case "10":
			$("#slRateValueThis").text("${StringUtil.wrapString(uiLabelMap.BEBad)}");
			break;
		case "20":
			$("#slRateValueThis").text("${StringUtil.wrapString(uiLabelMap.BEMediocre)}");
			break;
		case "30":
			$("#slRateValueThis").text("${StringUtil.wrapString(uiLabelMap.BEOrdinary)}");
			break;
		case "40":
			$("#slRateValueThis").text("${StringUtil.wrapString(uiLabelMap.BEGood)}");
			break;
		case "50":
			$("#slRateValueThis").text("${StringUtil.wrapString(uiLabelMap.BEAwesome)}");
			break;
		default:
			$("#slRateValueThis").text("");
			break;
		}
	});
</script>