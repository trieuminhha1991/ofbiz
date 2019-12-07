<#-- variable setup -->
<#assign productImageList = productImageList?if_exists />
<#-- end variable setup -->
<#-- virtual product javascript -->
${virtualJavaScript?if_exists}
${virtualVariantJavaScript?if_exists}
<script>var productId = '${productId}';</script>
<script type="text/javascript" src="/images/selectall.js"></script>
<script type="text/javascript">
	//<![CDATA[
	var detailImageUrl = null;
	function setAddProductId(name) {

	document.addform.add_product_id.value = name;
	if (document.addform.quantity == null) return;
	if (name == '' || name == 'NULL' || isVirtual(name) == true) {
	document.getElementById("addCart1").style.display = "none";
	document.getElementById("addCart2").style.display = "block";
	} else {
	document.getElementById("addCart1").style.display = "block";
	document.getElementById("addCart2").style.display = "none";
	}
	}
	function setVariantPrice(sku) {
	if (sku == '' || sku == 'NULL' || isVirtual(sku) == true) {
	var elem = document.getElementById('variant_price_display');
	var txt = document.createTextNode('');
	if(elem.hasChildNodes()) {
	elem.replaceChild(txt, elem.firstChild);
	} else {
	elem.appendChild(txt);
	}
	}
	else {
	var elem = document.getElementById('variant_price_display');
	var price = getVariantPrice(sku);
	var txt = document.createTextNode(price);
	if(elem.hasChildNodes()) {
	elem.replaceChild(txt, elem.firstChild);
	} else {
	elem.appendChild(txt);
	}
	}
	}
	function isVirtual(product) {
	var isVirtual = false;
	<#if virtualJavaScript?exists>
	for (i = 0; i < VIR.length; i++) {
	if (VIR[i] == product) {
	isVirtual = true;
	}
	}
	</#if>
	return isVirtual;
	}
	function addItem() {
	if (document.addform.add_product_id.value == 'NULL') {
	showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.CommonPleaseSelectAllRequiredOptions}");
	return;
	} else {
	if (isVirtual(document.addform.add_product_id.value)) {
	document.location = '<@ofbizUrl>product?category_id=${categoryId?if_exists}&amp;product_id=</@ofbizUrl>' + document.addform.add_product_id.value;
	return;
	} else {
	document.addform.submit();
	}
	}
	}

	function popupDetail(specificDetailImageUrl) {
	if( specificDetailImageUrl ) {
	detailImageUrl = specificDetailImageUrl;
	}
	else {
	var defaultDetailImage = "${firstDetailImage?default(mainDetailImageUrl?default("_NONE_"))}";
	if (defaultDetailImage == null || defaultDetailImage == "null" || defaultDetailImage == "") {
	defaultDetailImage = "_NONE_";
	}

	if (detailImageUrl == null || detailImageUrl == "null") {
	detailImageUrl = defaultDetailImage;
	}
	}

	if (detailImageUrl == "_NONE_") {
	hack = document.createElement('span');
	hack.innerHTML="${uiLabelMap.CommonNoDetailImageAvailableToDisplay}";
	showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.CommonNoDetailImageAvailableToDisplay}");
	return;
	}
	detailImageUrl = detailImageUrl.replace(/\&\#47;/g, "/");
	popUp("<@ofbizUrl>detailImage?detail=" + detailImageUrl + "</@ofbizUrl>", 'detailImage', '600', '600');
	}

	function toggleAmt(toggle) {
	if (toggle == 'Y') {
	changeObjectVisibility("add_amount", "visible");
	}

	if (toggle == 'N') {
	changeObjectVisibility("add_amount", "hidden");
	}
	}

	function findIndex(name) {
	for (i = 0; i < OPT.length; i++) {
	if (OPT[i] == name) {
	return i;
	}
	}
	return -1;
	}

	function getList(name, index, src) {
	currentFeatureIndex = findIndex(name);

	if (currentFeatureIndex == 0) {
	// set the images for the first selection
	if (IMG[index] != null) {
	if (document.images['mainImage'] != null) {
	document.images['mainImage'].src = IMG[index];
	detailImageUrl = DET[index];
	}
	}

	// set the drop down index for swatch selection
	document.forms["addform"].elements[name].selectedIndex = (index*1)+1;
	}

	if (currentFeatureIndex < (OPT.length-1)) {
	// eval the next list if there are more
	var selectedValue = document.forms["addform"].elements[name].options[(index*1)+1].value;
	if (index == -1) {
	<#if featureOrderFirst?exists>
	var Variable1 = eval("list" + "${featureOrderFirst}" + "()");
	</#if>
	} else {
	var Variable1 = eval("list" + OPT[(currentFeatureIndex+1)] + selectedValue + "()");
	}
	// set the product ID to NULL to trigger the alerts
	setAddProductId('NULL');

	// set the variant price to NULL
	setVariantPrice('NULL');
	} else {
	// this is the final selection -- locate the selected index of the last selection
	var indexSelected = document.forms["addform"].elements[name].selectedIndex;

	// using the selected index locate the sku
	var sku = document.forms["addform"].elements[name].options[indexSelected].value;

	// display alternative packaging dropdown
	ajaxUpdateArea("product_uom", "<@ofbizUrl>ProductUomDropDownOnly</@ofbizUrl>", "productId=" + sku);

	// set the product ID
	setAddProductId(sku);

	// set the variant price
	setVariantPrice(sku);

	// check for amount box
	toggleAmt(checkAmtReq(sku));
	}
	}

	function validate(x){
	var msg=new Array();
	msg[0]="Please use correct date format [yyyy-mm-dd]";

	var y=x.split("-");
	if(y.length!=3){ showAlert(msg[0]);return false; }
	if((y[2].length>2)||(parseInt(y[2])>31)) { showAlert(msg[0]); return false; }
	if(y[2].length==1){ y[2]="0"+y[2]; }
	if((y[1].length>2)||(parseInt(y[1])>12)){ showAlert(msg[0]); return false; }
	if(y[1].length==1){ y[1]="0"+y[1]; }
	if(y[0].length>4){ showAlert(msg[0]); return false; }
	if(y[0].length<4) {
	if(y[0].length==2) {
	y[0]="20"+y[0];
	} else {
	showAlert(msg[0]);
	return false;
	}
	}
	return (y[0]+"-"+y[1]+"-"+y[2]);
	}

	function showAlert(msg){
	showErrorAlert("${uiLabelMap.CommonErrorMessage2}", msg);
	}

	function additemSubmit(){
	<#if product.productTypeId?if_exists == "ASSET_USAGE" || product.productTypeId?if_exists == "ASSET_USAGE_OUT_IN">
	newdatevalue = validate(document.addform.reservStart.value);
	if (newdatevalue == false) {
	document.addform.reservStart.focus();
	} else {
	document.addform.reservStart.value = newdatevalue;
	document.addform.submit();
	}
	<#else>
	document.addform.submit();
	</#if>
	}

	function addShoplistSubmit(){
	<#if product.productTypeId?if_exists == "ASSET_USAGE" || product.productTypeId?if_exists == "ASSET_USAGE_OUT_IN">
	if (document.addToShoppingList.reservStartStr.value == "") {
	document.addToShoppingList.submit();
	} else {
	newdatevalue = validate(document.addToShoppingList.reservStartStr.value);
	if (newdatevalue == false) {
	document.addToShoppingList.reservStartStr.focus();
	} else {
	document.addToShoppingList.reservStartStr.value = newdatevalue;
	// document.addToShoppingList.reservStart.value = ;
	document.addToShoppingList.reservStartStr.value.slice(0,9)+" 00:00:00.000000000";
	document.addToShoppingList.submit();
	}
	}
	<#else>
	document.addToShoppingList.submit();
	</#if>
	}

	<#if product.virtualVariantMethodEnum?if_exists == "VV_FEATURETREE" && featureLists?has_content>
	function checkRadioButton() {
	var block1 = document.getElementById("addCart1");
	var block2 = document.getElementById("addCart2");
	<#list featureLists as featureList>
	<#list featureList as feature>
	<#if feature_index == 0>
	var myList = document.getElementById("FT${feature.productFeatureTypeId}");
	if (myList.options[0].selected == true){
	block1.style.display = "none";
	block2.style.display = "block";
	return;
	}
	<#break>
	</#if>
	</#list>
	</#list>
	block1.style.display = "block";
	block2.style.display = "none";
	}
	</#if>

	function displayProductVirtualVariantId(variantId) {
	if(variantId){
	document.addform.product_id.value = variantId;
	}else{
	document.addform.product_id.value = '';
	variantId = '';
	}

	var elem = document.getElementById('product_id_display');
	var txt = document.createTextNode(variantId);
	if(elem.hasChildNodes()) {
	elem.replaceChild(txt, elem.firstChild);
	} else {
	elem.appendChild(txt);
	}

	var priceElem = document.getElementById('variant_price_display');
	var price = getVariantPrice(variantId);
	var priceTxt = null;
	if(price){
	priceTxt = document.createTextNode(price);
	}else{
	priceTxt = document.createTextNode('');
	}
	if(priceElem.hasChildNodes()) {
	priceElem.replaceChild(priceTxt, priceElem.firstChild);
	} else {
	priceElem.appendChild(priceTxt);
	}
	}
	//]]>
	$(function(){
	$('a[id^=productTag_]').click(function(){
	var id = $(this).attr('id');
	var ids = id.split('_');
	var productTagStr = ids[1];
	if (productTagStr) {
	$('#productTagStr').val(productTagStr);
	$('#productTagsearchform').submit();
	}
	});
	})
</script>
<#macro showUnavailableVarients>
<#if unavailableVariants?exists>
<ul>
	<#list unavailableVariants as prod>
	<#assign features = prod.getRelated("ProductFeatureAppl", null, null, false)/>
	<li>
		<#list features as feature>
		<em>${feature.getRelatedOne("ProductFeature", false).description}</em><#if feature_has_next>, </#if>
		</#list>
		<span>${uiLabelMap.ProductItemOutOfStock}</span>
	</li>
	</#list>
</ul>
</#if>
</#macro>
<#assign productAdditionalImage1 = productContentWrapper.get("XTRA_IMG_1_LARGE")?if_exists />
<#assign productAdditionalImage2 = productContentWrapper.get("XTRA_IMG_2_LARGE")?if_exists />
<#assign productAdditionalImage3 = productContentWrapper.get("XTRA_IMG_3_LARGE")?if_exists />
<#assign productAdditionalImage4 = productContentWrapper.get("XTRA_IMG_4_LARGE")?if_exists />
<#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")?if_exists />
<#if productImageList?has_content && firstLargeImage?has_content>
<#assign productLargeImageUrl = firstLargeImage />
</#if>
<div id="messages_product_view"></div>
<div class="product-view quick-view-popup" style="width: 100%">
	<div class="product-essential">
		<div class="product-img-box">
			<p class="product-image product-image-zoom">
				<#if productLargeImageUrl?string?has_content>
				<img id="image" src="<@ofbizContentUrl>${contentPathPrefix?if_exists}${productLargeImageUrl?if_exists}</@ofbizContentUrl>" alt="${productContentWrapper.get("PRODUCT_NAME")?if_exists}" title="${productContentWrapper.get("PRODUCT_NAME")?if_exists}">
				<#else>
				<img id="image" src="/images/defaultImage.jpg" name="image" alt="${productContentWrapper.get("PRODUCT_NAME")?if_exists}" />
				</#if>
			</p>
			<#if productImageList?has_content || productAdditionalImage1?string?has_content || productAdditionalImage2?string?has_content || productAdditionalImage3?string?has_content || productAdditionalImage4?string?has_content>
			<div class="jm-product-lemmon">
				<div class="prev">
					<span><i class="fa fa-caret-left"></i></span>
				</div>
				<div class="more-views">
					<ul class="thumbnail-container">
						<#if productLargeImageUrl?string?has_content>
						<li class="thumbnail-img">
							<a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productLargeImageUrl}</@ofbizContentUrl>" title="${productContentWrapper.get("PRODUCT_NAME")?if_exists}"> <i class="fa fa-caret-up"></i> <img style="width:69px;height:69px;" src="<@ofbizContentUrl>${productLargeImageUrl}</@ofbizContentUrl>" vspace="5" hspace="5" alt="" /> </a>
						</li>
						</#if>
						<#if productImageList?has_content>
						<#list productImageList as productImage>
						<li class="thumbnail-img">
							<a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productImage.productImage}</@ofbizContentUrl>" title="${productContentWrapper.get("PRODUCT_NAME")?if_exists}"> <i class="fa fa-caret-up"></i> <img style="width:69px;height:69px;" src="<@ofbizContentUrl>${productImage.productImageThumb}</@ofbizContentUrl>" vspace="5" hspace="5" alt="" /> </a>
						</li>
						</#list>
						<#else>
						<#if productAdditionalImage1?string?has_content && productAdditionalImage1 != 'null'>
						<li class="thumbnail-img">
							<a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage1}</@ofbizContentUrl>" title="${productContentWrapper.get("PRODUCT_NAME")?if_exists}"> <i class="fa fa-caret-up"></i> <img style="width:69px;height:69px;" src="<@ofbizContentUrl>${productAdditionalImage1}</@ofbizContentUrl>" vspace="5" hspace="5" alt="" /> </a>
						</li>
						</#if>
						<#if productAdditionalImage2?string?has_content && productAdditionalImage2 != 'null'>
						<li class="thumbnail-img">
							<a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage2}</@ofbizContentUrl>" title="${productContentWrapper.get("PRODUCT_NAME")?if_exists}"> <i class="fa fa-caret-up"></i> <img style="width:69px;height:69px;" src="<@ofbizContentUrl>${productAdditionalImage2}</@ofbizContentUrl>" vspace="5" hspace="5" alt="" /> </a>
						</li>
						</#if>
						<#if productAdditionalImage3?string?has_content && productAdditionalImage3 != 'null'>
						<li class="thumbnail-img">
							<a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage3}</@ofbizContentUrl>" title="${productContentWrapper.get("PRODUCT_NAME")?if_exists}"> <i class="fa fa-caret-up"></i> <img style="width:69px;height:69px;" src="<@ofbizContentUrl>${productAdditionalImage3}</@ofbizContentUrl>" vspace="5" hspace="5" alt="" /> </a>
						</li>
						</#if>
						<#if productAdditionalImage4?string?has_content && productAdditionalImage4 != 'null'>
						<li class="thumbnail-img">
							<a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage4}</@ofbizContentUrl>" title="${productContentWrapper.get("PRODUCT_NAME")?if_exists}"> <i class="fa fa-caret-up"></i> <img style="width:69px;height:69px;" src="<@ofbizContentUrl>${productAdditionalImage4}</@ofbizContentUrl>" vspace="5" hspace="5" alt="" /> </a>
						</li>
						</#if>
						</#if>
					</ul>
				</div>
				<div class="next">
					<span><i class="fa fa-caret-right"></i></span>
				</div>
				<script type="text/javascript" src="/obbresources/skin/frontend/default/jm_megamall/js/easy-slider.js"></script>
				<script type="text/javascript">
					// <![CDATA[
					jQuery.noConflict();
					jQuery(document).ready(function($) {
						$(".more-views").easySlider({
							mainImg : "p.product-image img",
							btnNext : ".jm-product-lemmon .next",
							btnPrev : ".jm-product-lemmon .prev",
							animate : true,
							loop : true,
							speed : 300,
							width : 100,
							width_img : 100
						});
						<#if productLargeImageUrl?string?has_content || (productAdditionalImage1?string?has_content && productAdditionalImage1 != 'null') || (productAdditionalImage2?string?has_content && productAdditionalImage2 != 'null') || (productAdditionalImage3?string?has_content && productAdditionalImage3 != 'null') || (productAdditionalImage4?string?has_content && productAdditionalImage4 != 'null')>
						<#else>
							$(".jm-product-lemmon").addClass("hide");
						</#if>
						//var optionsZoom2 = new Product.Zoom(document.getElementById('image'), 'track', 'handle', 'zoom_in', 'zoom_out', 'track_hint');
					});

					// ]]>
				</script>
			</div>
			<#else>
			<script type="text/javascript">
				// <![CDATA[
				jQuery.noConflict();
				/*jQuery(document).ready(function($){
				document.log(document.getElementById('image'));
				var optionsZoom2 = new Product.Zoom(document.getElementById('image'), 'track', 'handle', 'zoom_in', 'zoom_out', 'track_hint');
				});*/
				// ]]>
			</script>
			</#if>
		</div>
		<div class="product-shop">
			<div class="product-name quick-view">
				<h1><a href="<@ofbizUrl>productmaindetail?product_id=${product.productId?if_exists}</@ofbizUrl>" title="${productContentWrapper.get("PRODUCT_NAME")?if_exists}">${productContentWrapper.get("PRODUCT_NAME")?if_exists}</a></h1>
			</div>
			<div class="ratings">
				<div class="rating-box">
					<#if numRatings gt 0>
					<div class="rating" style="width:${(averageRating*2)?string?replace(",", ".")}%"></div>
				</div>
				<p class="rating-links">
					
					<#if numRatings gt 1>
					<a href="<@ofbizUrl>productmaindetail?product_id=${product.productId?if_exists}&rev=Y</@ofbizUrl>">${numRatings} ${uiLabelMap.ObbReviews}</a>
					<#else>
					<a href="<@ofbizUrl>productmaindetail?product_id=${product.productId?if_exists}&rev=Y</@ofbizUrl>">${numRatings} ${uiLabelMap.ObbReview}</a>
					</#if>
					
					<span class="separator">|</span>
					<a class="link-review" href="javascript:reviewIt()">${uiLabelMap.ObbAddYourReview}</a>
				</p>
				<#else>
					<div class="rating"></div>
				</div>
				<p class="rating-links">
					<a href="<@ofbizUrl>productmaindetail?product_id=${product.productId?if_exists}&rev=Y</@ofbizUrl>">0 ${uiLabelMap.ObbReview}</a>
					<span class="separator">|</span>
					<a class="link-review" href="javascript:reviewIt()">${uiLabelMap.ObbProductBeTheFirstToReviewThisProduct}</a>
				</p>
				</#if>
			
			
			<div id="reviewIt" style="display: none;" class="margin-top10">
				
				<#if security.hasEntityPermission("PRODUCT", "_REVIEW", session)>
					<form id="reviewProduct" class="form-inline editableform" method="post" action="<@ofbizUrl>createProductReview</@ofbizUrl>">
						<input type="hidden" name="productStoreId" value="${productStoreId}" />
						<input type="hidden" name="productId" value="${product.productId}" />
						<input type="hidden" name="product_id" value="${product.productId}" />
						<input type="hidden" name="category_id" value="${categoryId?if_exists}" />
						<div class="row">
							<div class="col-lg-3 no-padding-right">
								<select id="slRateThis" name="productRating">
									<option value=""></option>
									<option value="10">10</option>
									<option value="20">20</option>
									<option value="30">30</option>
									<option value="40">40</option>
									<option value="50">50</option>
								</select>
							</div>
							<div class="col-lg-7 no-padding-left">
								<label id="slRateValueThis"></label>
							</div>
						</div>	
						<div class="row">
							<div class="col-lg-12">
								<textarea class="area-review-full-width full-width-input" name="productReview" placeholder="${uiLabelMap.BEAddYourReview}..."></textarea>
							</div>
						</div>
						<div id="btnRateThis" class="row margin-top10" style="display: none;">
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
				
			<div class="margin-top-10">
				<#assign isPreview = "true"/>
				<#assign currentUrl = "productmaindetail?product_id=${product.productId?if_exists}"/>
				${setContextField("currentUrl", currentUrl)}
				${screens.render("component://obb/widget/CommonScreens.xml#facebooksharing")}
				<script>
					FB.XFBML.parse(); 
				</script>
			</div>
		</div>
		<div class="price-box">
			<span class="regular-price" id="product-price-28"> <span class="price"> <#-- for prices:
					- if price < competitivePrice, show competitive or "Compare At" price
					- if price < listPrice, show list price
					- if price < defaultPrice and defaultPrice < listPrice, show default
					- if isSale show price with salePrice style and print "On Sale!"
					-->
					<#if price.competitivePrice?has_content && price.price?exists && price.price &lt; price.competitivePrice>
					${uiLabelMap.ProductCompareAtPrice}: <@ofbizCurrency amount=price.competitivePrice isoCode=price.currencyUsed />
					<br/>
					</#if>

					<#if price.listPrice?has_content && price.defaultPrice?exists && price.price?exists && price.price &lt; price.defaultPrice && price.defaultPrice &lt; price.listPrice> <span class="obb-gtt-price-label">${uiLabelMap.BEMarketPrice}: </span> <span class="obb-gtt-price-value"><@ofbizCurrency amount=price.defaultPrice isoCode=price.currencyUsed /></span>
					<br/>
					</#if>
					<#if price.specialPromoPrice?has_content> <span class="obb-gdb-price-label">${uiLabelMap.BESpecialPrice}: </span> <span class="obb-gdb-price-value"><@ofbizCurrency amount=price.specialPromoPrice isoCode=price.currencyUsed /></span>
					<br/>
					</#if>
					<div>
						<#if price.isSale?has_content && price.isSale>
						${uiLabelMap.OrderOnSale}!
						<#assign priceStyle = "salePrice" />
						<#else>
						<#assign priceStyle = "regularPrice" />
						</#if>
						<span class="obb-gnq-price-label">${uiLabelMap.BEPriceIn} nhanhqua:</span><span class="obb-gnq-price-value"><#if "Y" = product.isVirtual?if_exists> ${uiLabelMap.CommonFrom} </#if><@ofbizCurrency amount=price.price isoCode=price.currencyUsed />
							<#if product.productTypeId?if_exists == "ASSET_USAGE" || product.productTypeId?if_exists == "ASSET_USAGE_OUT_IN">
							<#if product.reserv2ndPPPerc?exists && product.reserv2ndPPPerc != 0>${uiLabelMap.ProductReserv2ndPPPerc}<#if !product.reservNthPPPerc?exists || product.reservNthPPPerc == 0>${uiLabelMap.CommonUntil} ${product.reservMaxPersons?if_exists}</#if> <@ofbizCurrency amount=product.reserv2ndPPPerc*price.price/100 isoCode=price.currencyUsed /></#if>
							<#if product.reservNthPPPerc?exists &&product.reservNthPPPerc != 0>${uiLabelMap.ProductReservNthPPPerc} <#if !product.reserv2ndPPPerc?exists || product.reserv2ndPPPerc == 0>${uiLabelMap.ProductReservSecond} <#else> ${uiLabelMap.ProductReservThird} </#if> ${uiLabelMap.CommonUntil} ${product.reservMaxPersons?if_exists}, ${uiLabelMap.ProductEach}: <@ofbizCurrency amount=product.reservNthPPPerc*price.price/100 isoCode=price.currencyUsed /></#if>
							<#if (!product.reserv2ndPPPerc?exists || product.reserv2ndPPPerc == 0) && (!product.reservNthPPPerc?exists || product.reservNthPPPerc == 0)>${uiLabelMap.ProductMaximum} ${product.reservMaxPersons?if_exists} ${uiLabelMap.ProductPersons}.</#if> </#if></span>
						<br/>
						<#if price.listPrice?has_content && price.price?exists && price.price &lt; price.listPrice>
						<span class="obb-gny-price-label">${uiLabelMap.BEProductListPrice}: </span><span class="obb-gny-price-value"><@ofbizCurrency amount=price.listPrice isoCode=price.currencyUsed /></span>
						<br/>
						</#if>
					</div> <#if price.listPrice?has_content && price.price?has_content && price.price &lt; price.listPrice>
					<#assign priceSaved = price.listPrice - price.price />
					<#assign percentSaved = (priceSaved / price.listPrice) * 100 /> <span class="obb-tk-price-label">${uiLabelMap.BESaved}: </span><span class="obb-tk-price-value"> <@ofbizCurrency amount=priceSaved isoCode=price.currencyUsed /> (${percentSaved?int}%)</span> </#if>
					<#-- show price details ("showPriceDetails" field can be set in the screen definition) -->
					<#if (showPriceDetails?has_content && showPriceDetails?default("N") == "Y")>
					<#if price.orderItemPriceInfos?exists>
					<#list price.orderItemPriceInfos as orderItemPriceInfo>
					${orderItemPriceInfo.description?if_exists}
					</#list>
					</#if>
					</#if> </span> </span>
		</div>
		<div class="clearer"></div>
		<form method="post" action="<@ofbizUrl>additem</@ofbizUrl>" name="addform" id="addform" style="margin: 0;">
			<input type="hidden" name="quantityUomId" value="${product.quantityUomId?if_exists}">
			<fieldset>
				<#assign inStock = true />
				<#-- Variant Selection -->
				<#if product.isVirtual?if_exists?upper_case == "Y">
				<#if product.virtualVariantMethodEnum?if_exists == "VV_FEATURETREE" && featureLists?has_content>
				<div class="product-options" id="product-options-wrapper">
					<dl class="last">
						<#list featureLists as featureList>
						<#list featureList as feature>
						<#if feature_index == 0>
						<dt>
							<label class="required"><em>*</em>${feature.description}:</label>
						</dt>
						<dd>
							<select id="FT${feature.productFeatureTypeId}" name="FT${feature.productFeatureTypeId}" class="required-entry product-custom-option" onchange="javascript:checkRadioButton();">
								<option value="select" selected="selected">${uiLabelMap.ObbSelectOption}</option>
								<#else>
								<option value="${feature.productFeatureId}">${feature.description} <#if feature.price?exists>(+ <@ofbizCurrency amount=feature.price?string isoCode=feature.currencyUomId />)</#if></option>
								</#if>
								</#list>
							</select>
						</dd>
						</#list>
					</dl>
					<input type="hidden" name="add_product_id" value="${product.productId}" />
				</div>
				</#if>
				<#if !product.virtualVariantMethodEnum?exists || product.virtualVariantMethodEnum == "VV_VARIANTTREE">
				<#if variantTree?exists && (variantTree.size() &gt; 0)>
				<div class="product-options" id="product-options-wrapper">
					<dl class="last">
						<#list featureSet as currentType>
						<dt>
							<label class="required"><em>*</em>${currentType}:</label>
						</dt>
						<dd>
							<select name="FT${currentType}" onchange="javascript:getList(this.name, (this.selectedIndex-1), 1);" class="required-entry product-custom-option">
								<option>${featureTypes.get(currentType)}</option>
							</select>
						</dd>
						</#list>
					</dl>
				</div>
				<div id="product_uom"></div>
				<input type="hidden" name="product_id" value="${product.productId}"/>
				<input type="hidden" name="add_product_id" value="NULL"/>
				<div>
					<strong><span id="product_id_display"> </span></strong>
					<span class="regular-price"><span class="price">${uiLabelMap.BEProductPrice}:<div id="variant_price_display"></div></span></span>
				</div>
				<#else>
				<input type="hidden" name="add_product_id" value="NULL"/>
				<#assign inStock = false />
				</#if>
				</#if>

				<#if variantPriceList?exists>
				<#list variantPriceList as vpricing>
				<#assign variantName = vpricing.get("variantName")?if_exists>
				<#assign secondVariantName = vpricing.get("secondVariantName")?if_exists>
				<#assign minimumQuantity = vpricing.get("minimumQuantity")>
				<#if minimumQuantity &gt; 0>
				<div>
					minimum order quantity for ${secondVariantName!} ${variantName!} is ${minimumQuantity!}
				</div>
				</#if>
				</#list>
				<#elseif minimumQuantity?exists && minimumQuantity?has_content && minimumQuantity &gt; 0>
				<div>
					minimum order quantity for ${productContentWrapper.get("PRODUCT_NAME")?if_exists} is ${minimumQuantity!}
				</div>
				</#if>
				<#else>
				<input type="hidden" name="add_product_id" value="${product.productId}" />
				<#if mainProducts?has_content>
				<input type="hidden" name="product_id" value=""/>
				<select class="custom" name="productVariantId" onchange="javascript:displayProductVirtualVariantId(this.value);">
					<option value="">Select Unit Of Measure</option>
					<#list mainProducts as mainProduct>
					<option value="${mainProduct.productId}">${mainProduct.uomDesc} : ${mainProduct.piecesIncluded}</option>
					</#list>
				</select>
				</#if>
				<#if (availableInventory?exists) && (availableInventory <= 0)>
				<#assign inStock = false />
				</#if>
				</#if>
				<#--
				<p class="availability in-stock">
					${uiLabelMap.ObbAvailability}:
					<#if inStock>
					<span>${uiLabelMap.ObbInStock}</span>
					<#else>
					<span>${uiLabelMap.ObbOutOfStock}</span>
					</#if>
				</p>
				-->
				<div class="row">
					<#if sessionAttributes.userLogin?has_content && sessionAttributes.userLogin.userLoginId != "anonymous">
						<div class="col-lg-12">
							<ul class="add-to-links hide">
								<li>
									<a href="javascript:void(0);" onclick="document.addToShoppingList.submit();" class="link-wishlist">${uiLabelMap.ObbAddToWishlist2}</a>
								</li>
								<li>
									<a href="javascript:void(0);" onclick="document.addToCompare1form.submit();" class="link-compare">${uiLabelMap.ObbAddToCompare2}</a>
								</li>
							</ul>
						</div>
						<#else>
						<div class="hide">
							${uiLabelMap.ObbYouMust} <a href="<@ofbizUrl>checkLogin/main</@ofbizUrl>">${uiLabelMap.BELogIn}</a>
							${uiLabelMap.ObbAddSelectedItemsToShoppingList}
						</div>
					</#if>
					<div class='col-lg-6 col-md-6'>
						<label for="qty">${uiLabelMap.ObbQuantity}:</label>
						<input type="text" name="quantity" id="qty" maxlength="12" value="1" title="Qty" class="input-text qty">
					</div>
					<div class='col-lg-6 col-md-6'>
						<a href="javascript:void(0);" onclick="document.addform.submit();" class="optionsboxadd" id="optionsbox28">
						<button type="button" title="${uiLabelMap.ObbAddToCart}" class="button btn-cart">
							<span><span>${uiLabelMap.ObbAddToCart}</span></span>
						</button> </a>
					</div>
				</div>
			</fieldset>
		</form>

		<form method="post" action="<@ofbizUrl>addToCompare</@ofbizUrl>" name="addToCompare1form" style="display:none;">
			<input type="hidden" name="productId" value="548161">
			<input type="hidden" name="mainSubmitted" value="Y">
		</form>
		<form name="addToShoppingList" method="post" action="<@ofbizUrl>addItemToShoppingList/product</@ofbizUrl>" style="display:none;">
			<input type="hidden" name="productId" value="FLS_Top_271683">
			<input type="hidden" name="product_id" value="FLS_Top_271683">
			<input type="hidden" name="productStoreId" value="10030">
			<input type="hidden" name="reservStart" value="">
			<input type="hidden" name="shoppingListId" value="10115">
			<input type="hidden" size="5" name="quantity" value="1">
			<input type="hidden" name="reservStartStr" value="">
		</form>
		<div class="short-description">
			<!-- <h2>${uiLabelMap.ObbQuickOverview}</h2>
			<div class="std"><p>${productContentWrapper.get("DESCRIPTION")?if_exists}</p><br> -->
			<div class="std">
				<#if product.longDescription?has_content>
					${StringUtil.wrapString(product.longDescription?if_exists)}
				<#else>
					${uiLabelMap.BENoDescription}
			</#if>
			</div>
		</div>
	</div>
</div>
</div>
<script type="text/javascript" src="/obbresources/asset/js/jquery.barrating.min.js"></script>
<script>
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