<#if productData?exists>
<script>
	var productId = '${productId}';
</script>
<style type="text/css">
  #jm-main .inner{
    border: none !important;
    padding:0px !important;
  }
</style>
<#-- variable setup -->
<#assign productImageList = productImageList?if_exists />
<#-- end variable setup -->
<#-- virtual product javascript -->
${virtualJavaScript?if_exists}
${virtualVariantJavaScript?if_exists}
<script type="text/javascript">
    if( jQuery ){
	    jQuery( "#ja-tab-products" ).ready( function($) {
//		$( "#ja-tab-products" ).jaContentTabs();
	    });
    }
    jQuery(document).ready(function() {
        urllocation =  window.location;
        if(urllocation.toString().indexOf("#review-form") > 0){
           jQuery("ul.ja-tab-navigator").find("a|[href='#ja-tabitem-reviews']").trigger("click");
           window.location = "#ja-tabitem-reviews";
        }
        if(sessionStorage.validated !== "true"){
		// popup();
        }
    });
</script>
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
               document.location = '<@ofbizUrl>product?category_id=${categoryId?if_exists}&amp;product_id=' + document.addform.add_product_id.value + '</@ofbizUrl>';
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
<#assign productAdditionalImageAI1 = productContentWrapper.get("ADDITIONAL_IMAGE_1")?if_exists />
<#assign productAdditionalImageAI2 = productContentWrapper.get("ADDITIONAL_IMAGE_2")?if_exists />
<#assign productAdditionalImageAI3 = productContentWrapper.get("ADDITIONAL_IMAGE_3")?if_exists />
<#assign productAdditionalImageAI4 = productContentWrapper.get("ADDITIONAL_IMAGE_4")?if_exists />
<#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")?if_exists />
<#assign productLargeImageUrlAI = productContentWrapper.get("ORIGINAL_IMAGE_URL")?if_exists />
<#if productImageList?has_content && firstLargeImage?has_content>
    <#assign productLargeImageUrl = firstLargeImage />
</#if>
<style type="text/css">
	.bgwhite{
		background-color:white;
	}
	.elastislide-carousel ul li.active a{
		border: 1px solid #f04e46 !important;
		border-color: #F04E46 !important;
	}
</style>
<div id="messages_product_view"></div>
<div class="product-view">
    <div class="product-essential">
    <div class="row">
		<div class="col-lg-6 col-md-6">
			<div class="img-wraper">
				<#if productLargeImageUrl?string?has_content>
					<img class="product-primary-img" id="demo4" src="<@ofbizContentUrl>${contentPathPrefix?if_exists}${productLargeImageUrl?if_exists}</@ofbizContentUrl>">
				<#else>
                    <img class="product-primary-img" id="demo4" src="/images/defaultImage.jpg"/>
                </#if>
			</div>
			<#if productLargeImageUrl?has_content || (productAdditionalImage1?string?has_content && productAdditionalImage1 != 'null') || (productAdditionalImage2?string?has_content && productAdditionalImage2 != 'null') || (productAdditionalImage3?string?has_content && productAdditionalImage3 != 'null') || (productAdditionalImage4?string?has_content && productAdditionalImage4 != 'null')>
				<ul id="demo4carousel" class="elastislide-list products-grid product-thumb-img" style="max-height:80px!important;margin-bottom: 10px;">
					<#if productLargeImageUrl?string?has_content>
						<li id="lmain" class="obb-produc-image-small"><a href="#"><img src="<@ofbizContentUrl>${productLargeImageUrl}</@ofbizContentUrl>" data-largeimg="<@ofbizContentUrl>${productLargeImageUrl}</@ofbizContentUrl>" /></a></li>
					</#if>
					<#if productAdditionalImage1?string?has_content && productAdditionalImage1 != 'null'>
						<li id="la1" class="obb-produc-image-small"><a href="#"><img src="<@ofbizContentUrl>${productAdditionalImage1}</@ofbizContentUrl>" data-largeimg="<@ofbizContentUrl>${productAdditionalImageAI1}</@ofbizContentUrl>" /></a></li>
					</#if>
					<#if productAdditionalImage2?string?has_content && productAdditionalImage2 != 'null'>
						<li id="la2" class="obb-produc-image-small"><a href="#"><img src="<@ofbizContentUrl>${productAdditionalImage2}</@ofbizContentUrl>" data-largeimg="<@ofbizContentUrl>${productAdditionalImageAI2}</@ofbizContentUrl>" /></a></li>
					</#if>
					<#if productAdditionalImage3?string?has_content && productAdditionalImage3 != 'null'>
						<li id="la3" class="obb-produc-image-small"><a href="#"><img src="<@ofbizContentUrl>${productAdditionalImage3}</@ofbizContentUrl>" data-largeimg="<@ofbizContentUrl>${productAdditionalImageAI3}</@ofbizContentUrl>" /></a></li>
					</#if>
					<#if productAdditionalImage4?string?has_content && productAdditionalImage4 != 'null'>
						<li id="la4" class="obb-produc-image-small"><a href="#"><img src="<@ofbizContentUrl>${productAdditionalImage4}</@ofbizContentUrl>" data-largeimg="<@ofbizContentUrl>${productAdditionalImageAI4}</@ofbizContentUrl>" /></a></li>
					</#if>
				</ul>
			</#if>
			<script type="text/javascript" src="/obbresources/ImageZoom/demo/js/jquery-1.8.3.min.js"></script>
			<script type="text/javascript" src="/obbresources/ImageZoom/demo/js/jquery.imagezoom.min.js"></script>
			<script type="text/javascript" src="/obbresources/ImageZoom/demo/js/modernizr.custom.17475.js"></script>
			<script type="text/javascript" src="/obbresources/ImageZoom/demo/js/jquery.elastislide.js"></script>
			<script type="text/javascript">
				$.noConflict();
				jQuery( document ).ready(function( $ ) {
					//demo4   standard mode
					var carsousel = $('#demo4carousel').elastislide({start:0, minItems:4,
						onClick:function( el, pos, evt ) {
							el.siblings().removeClass("active");
							el.addClass("active");
							carsousel.setCurrent( pos );
							evt.preventDefault();
							// for imagezoom to change image
							var demo4obj = $('#demo4').data('imagezoom');
							if (demo4obj) {
								demo4obj.changeImage(el.find('img').attr('src'),el.find('img').data('largeimg'));
							} else {
								$('#demo4').attr("src", el.find('img').data('largeimg'));
							}
						},
						onReady:function(){
							//init imagezoom with many options
							if ("desktop" == "${device}") {
								$('#demo4').ImageZoom({type:'standard',zoomSize:[450,350],bigImageSrc:'<@ofbizContentUrl>${productLargeImageUrl}</@ofbizContentUrl>',offset:[10,-4],zoomViewerClass:'bgwhite',onShow:function(obj){obj.$viewer.hide().fadeIn(500);},onHide:function(obj){obj.$viewer.show().fadeOut(500);}});
							}
							$('#demo4carousel li:eq(0)').addClass('active');
							<#if productLargeImageUrl?has_content>
								$('#lmain').click();
							<#elseif productAdditionalImage1?has_content>
								$('#la1').click();
							<#elseif productAdditionalImage2?has_content>
								$('#la2').click();
							<#elseif productAdditionalImage3?has_content>
								$('#la3').click();
							<#elseif productAdditionalImage4?has_content>
								$('#la4').click();
							</#if>
							<#if (productAdditionalImage1?string?has_content && productAdditionalImage1 != 'null') || (productAdditionalImage2?string?has_content && productAdditionalImage2 != 'null') || (productAdditionalImage3?string?has_content && productAdditionalImage3 != 'null') || (productAdditionalImage4?string?has_content && productAdditionalImage4 != 'null')>
							<#else>
									$(".elastislide-horizontal").addClass("hide");
							</#if>
							// change zoomview size when window resize
							$(window).resize(function(){
								var demo4obj = $('#demo4').data('imagezoom');
								if (demo4obj) {
									winWidth = $(window).width();
									if(winWidth>900)
									{
										demo4obj.changeZoomSize(650,378);
									}
									else
									{
										demo4obj.changeZoomSize( winWidth*0.4,winWidth*0.4*0.625);
									}
								}
							});
						}
					});

				});
			</script>
		</div>
		<div class="col-lg-6 col-md-6">
			<div class="product-name">
				<h1>
					<a href="<@ofbizUrl>productmaindetail?product_id=${product.productId?if_exists}</@ofbizUrl>" title="${productData.productName?if_exists}">${productData.productName?if_exists}</a>
				</h1>
			</div>
			<div class="product-code">
				${uiLabelMap.FormFieldTitle_productId}: ${productData.productCode?if_exists}
			</div>
			<div class="ratings">
			    <#if numRatings gt 0>
			    <div class="rating-box">
			    <div class="rating" style="width:${(averageRating*2)?string?replace(",", ".")}%"></div>
				</div>
				<p class="rating-links">
					<p href="javascript:void(0);">${numRatings} ${uiLabelMap.ObbReviews}</p>
					<span class="separator">|</span>
			        <a class="link-review" href="<@ofbizUrl>reviewProduct?category_id=${categoryId?if_exists}&amp;product_id=${product.productId}</@ofbizUrl>">${uiLabelMap.ObbAddYourReview}</a>
			    </p>
			      <#else>
			      <div class="rating-box">
					<div class="rating"></div>
					</div>
					<p class="rating-links">
				            <p href="javascript:void(0);">0 ${uiLabelMap.ObbReview}</p>
							<span class="separator">|</span>
				            <a class="link-review" href="<@ofbizUrl>reviewProduct?category_id=${categoryId?if_exists}&amp;product_id=${product.productId}</@ofbizUrl>">${uiLabelMap.ObbProductBeTheFirstToReviewThisProduct}</a>
				        </p>
			          </#if>
			</div>
			<div class="price-box">
				<span class="regular-price" id="product-price-28">
					<span class="price">
				          <#if price.competitivePrice?has_content && price.price?exists && price.price &lt; price.competitivePrice>
				            ${uiLabelMap.ProductCompareAtPrice}: <@ofbizCurrency amount=price.competitivePrice isoCode=price.currencyUsed />
				            <br/>
				          </#if>

				          <#if price.listPrice?has_content && price.defaultPrice?exists && price.price?exists && price.price &lt; price.defaultPrice && price.defaultPrice &lt; price.listPrice>
				            <span class="obb-gtt-price-label">${uiLabelMap.BEMarketPrice}: </span> <span class="obb-gtt-price-value"><@ofbizCurrency amount=price.defaultPrice isoCode=price.currencyUsed /></span>
				            <br/>
				          </#if>
				          <#if price.specialPromoPrice?has_content>
				            <span class="obb-gdb-price-label">${uiLabelMap.BESpecialPrice}: </span> <span class="obb-gdb-price-value"><@ofbizCurrency amount=price.specialPromoPrice isoCode=price.currencyUsed /></span>
				            <br/>
				          </#if>
				          <div>
				              <#if price.isSale?has_content && price.isSale>
				                ${uiLabelMap.OrderOnSale}!
				                <#assign priceStyle = "salePrice" />
				              <#else>
				                <#assign priceStyle = "regularPrice" />
				              </#if>
				                <span class="obb-gnq-price-label">${uiLabelMap.BEPriceOf} nhanhqua:</span> <span class="obb-gnq-price-value"><#if "Y" = product.isVirtual?if_exists> ${uiLabelMap.CommonFrom} </#if><@ofbizCurrency amount=price.price isoCode=price.currencyUsed />
				                <#if product.productTypeId?if_exists == "ASSET_USAGE" || product.productTypeId?if_exists == "ASSET_USAGE_OUT_IN">
					                <#if product.reserv2ndPPPerc?exists && product.reserv2ndPPPerc != 0>${uiLabelMap.ProductReserv2ndPPPerc}<#if !product.reservNthPPPerc?exists || product.reservNthPPPerc == 0>${uiLabelMap.CommonUntil} ${product.reservMaxPersons?if_exists}</#if> <@ofbizCurrency amount=product.reserv2ndPPPerc*price.price/100 isoCode=price.currencyUsed /></#if>
					                <#if product.reservNthPPPerc?exists &&product.reservNthPPPerc != 0>${uiLabelMap.ProductReservNthPPPerc} <#if !product.reserv2ndPPPerc?exists || product.reserv2ndPPPerc == 0>${uiLabelMap.ProductReservSecond} <#else> ${uiLabelMap.ProductReservThird} </#if> ${uiLabelMap.CommonUntil} ${product.reservMaxPersons?if_exists}, ${uiLabelMap.ProductEach}: <@ofbizCurrency amount=product.reservNthPPPerc*price.price/100 isoCode=price.currencyUsed /></#if>
					                <#if (!product.reserv2ndPPPerc?exists || product.reserv2ndPPPerc == 0) && (!product.reservNthPPPerc?exists || product.reservNthPPPerc == 0)>${uiLabelMap.ProductMaximum} ${product.reservMaxPersons?if_exists} ${uiLabelMap.ProductPersons}.</#if>
				                 </#if></span><br/>
								 <#if price.listPrice?has_content && price.price?exists && price.price &lt; price.listPrice>
								<span class="obb-gny-price-label">${uiLabelMap.BEProductListPrice}: </span> <span class="obb-gny-price-value"><@ofbizCurrency amount=price.listPrice isoCode=price.currencyUsed /></span>
								<br/>
								</#if>
				          </div>
				          <#if price.listPrice?has_content && price.price?has_content && price.price &lt; price.listPrice>
				            <#assign priceSaved = price.listPrice - price.price />
				            <#assign percentSaved = (priceSaved / price.listPrice) * 100 />
				            <span class="obb-tk-price-label">${uiLabelMap.BESaved}: </span><span class="obb-tk-price-value"> <@ofbizCurrency amount=priceSaved isoCode=price.currencyUsed /> (${percentSaved?int}%)</span>
				          </#if>
				          <#-- show price details ("showPriceDetails" field can be set in the screen definition) -->
				          <#if (showPriceDetails?has_content && showPriceDetails?default("N") == "Y")>
				              <#if price.orderItemPriceInfos?exists>
				                  <#list price.orderItemPriceInfos as orderItemPriceInfo>
				                      ${orderItemPriceInfo.description?if_exists}
				                  </#list>
				              </#if>
				          </#if>
					</span>
				</span>
			</div>
			<div class="form-product-info">
				<form method="post" action="<@ofbizUrl>additem</@ofbizUrl>" name="addform" style="margin: 0;">
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
										<dt><label class="required"><em>*</em>${feature.description}:</label></dt>
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
									<dt><label class="required"><em>*</em>${currentType}:</label></dt>
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
			                  <span class="regular-price"><span class="price">${uiLabelMap.BEProductPrice}: <div id="variant_price_display"> </div></span></span>
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
				                  <div>minimum order quantity for ${secondVariantName!} ${variantName!} is ${minimumQuantity!}</div>
				                </#if>
				              </#list>
				            <#elseif minimumQuantity?exists && minimumQuantity?has_content && minimumQuantity &gt; 0>
				               <div>minimum order quantity for ${productData.productName?if_exists} is ${minimumQuantity!}</div>
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
					<script type="text/javascript">
						jcart = jQuery.noConflict();
							jcart(document).ready(function() {
							document.getElementById("addCart1").style.display = "block";
							document.getElementById("addCart2").style.display = "none";
						});
					</script>
					</#if>
						<#-- <p class="availability in-stock">
						${uiLabelMap.ObbAvailability}:
						<#if inStock>
							<span>${uiLabelMap.ObbInStock}</span>
						<#else>
							<span>${uiLabelMap.ObbOutOfStock}</span>
						</#if>
					</p> -->
						<div class="product-options-bottom">
							<div class='row'>
								<div class='col-lg-12'>
									<#if sessionAttributes.userLogin?has_content && sessionAttributes.userLogin.userLoginId != "anonymous">
										<ul class="add-to-links hide">
										    <li class="no-padding-left"><a href="javascript:void(0);" onclick="document.addToShoppingList.submit();" class="link-wishlist">${uiLabelMap.ObbAddToWishlist2}</a></li>
										    <li><span class="separator">|</span> <a href="javascript:void(0);" onclick="document.addToCompare1form.submit();" class="link-compare">${uiLabelMap.ObbAddToCompare2}</a></li>
										</ul>
									<#else>
										<div class="hide">
											${uiLabelMap.ObbYouMust} <a href="<@ofbizUrl>checkLogin/main</@ofbizUrl>" class="link-wishlist">${uiLabelMap.CommonBeLogged}</a>
											${uiLabelMap.ObbAddSelectedItemsToShoppingList}
										</div>
									</#if>
								</div>
							</div>
							<div class='row'>
								<div class='col-lg-12 margin-top10'>
									<div class="add-to-cart row" id="addCart1" style="display:none;">
										<div class='col-lg-6 col-md-5 col-sm-5 col-xs-6 no-padding-left'>
											<label for="qty">${uiLabelMap.ObbQuantity}:</label>
							                <input type="text" name="quantity" id="qty" maxlength="12" value="1" title="Qty" class="input-text qty">
										</div>
										<div class='col-lg-6 col-md-7 col-sm-7 col-xs-6 no-padding-left'>
											<a href="javascript:void(0);" onclick="document.addform.submit();" class="optionsboxadd" id="optionsbox28">
											<button type="button" title="${uiLabelMap.ObbAddToCart}" class="button btn-cart"><span><span>${uiLabelMap.ObbAddToCart}</span></span></button>
										    </a>
										</div>
						            </div>
						            <div class="add-to-cart row" id="addCart2" style="display:block;">
								<div class='col-lg-6 col-md-6 col-sm-6 col-xs-6 no-padding-left'>
									<label for="qty">${uiLabelMap.ObbQuantity}:</label>
								</div>
								<div class='col-lg-6 col-md-6 col-sm-6'>
									<input type="text" name="quantityTMP" id="qty" maxlength="12" value="1" title="Qty" class="input-text qty">
									        <a href="javascript:void(0);" onclick="alert('Hãy lựa chọn cấu hình phù hợp')" class="optionsboxadd" id="optionsbox28">
											<button type="button" title="${uiLabelMap.ObbAddToCart}" class="button btn-cart"><span><span>${uiLabelMap.ObbAddToCart}</span></span></button>
										    </a>
								</div>
						            </div>
								</div>
							</div>
						</div>
					</fieldset>
				</form>
			<form method="post" action="<@ofbizUrl>addToCompare</@ofbizUrl>" name="addToCompare1form" style="display:none;">
			<input type="hidden" name="productId" value="${product.productId}">
			<input type="hidden" name="mainSubmitted" value="Y">
	    </form>
	    <form name="addToShoppingList" method="post" action="<@ofbizUrl>addItemToShoppingList/product</@ofbizUrl>" style="display:none;">
			<input type="hidden" name="productId" value="${product.productId}">
			<input type="hidden" name="product_id" value="${product.productId}">
			<input type="hidden" name="productStoreId" value="${productStoreId}">
			<input type="hidden" name="reservStart" value="">
			<input type="hidden" name="shoppingListId" value="DemoWishList">
			<input type="hidden" name="quantity" size="5" value="1">
			<input type="hidden" name="reservStartStr" value="">
		</form>
			<form method="post" action="<@ofbizUrl>addToCompare</@ofbizUrl>" name="addToCompare1form" style="display:none;">
			<input type="hidden" name="productId" value="${product.productId}">
			<input type="hidden" name="mainSubmitted" value="Y">
	    </form>
	    <form name="addToShoppingList" method="post" action="<@ofbizUrl>addItemToShoppingList/product</@ofbizUrl>" style="display:none;">
				<input type="hidden" name="productId" value="${product.productId}">
		<input type="hidden" name="product_id" value="${product.productId}">
		<input type="hidden" name="productStoreId" value="${productStoreId}">
		<input type="hidden" name="reservStart" value="">
		<input type="hidden" name="shoppingListId" value="10115">
		<input type="hidden" size="5" name="quantity" value="1">
		<input type="hidden" name="reservStartStr" value="">
			</form>
			</div>
			<div class="form-supplier-info">
				<#if context.listSup?has_content>
					<div class="supplier-info-list">
						<h2 class="title-supplier-list">${uiLabelMap.BEProductsAreDistributedBy}:</h2>
						<ul id="list-supplier">
							<#list context.listSup as ls>
								<li data-id='${ls.facebookId}'>
									<div class="fb-friend-avatar"><img class="avatar-fb" src="/images/fb-avatar-blank.jpg"></img></div>
									<div class="fb-friend-detail">
										<div class='fb-friend-name'><a href="<@ofbizUrl>supplierdetail?supId=${ls.partyId}</@ofbizUrl>">${ls.firstName?if_exists}&nbsp;${ls.lastName?if_exists}&nbsp;</a></div>
										<div class="fb-suppiler-action">
											<div class="rating-box">
												<div class="rating"></div>
											</div>
											<div class="fb-viewon">
												<a title="Chat với người bán" target="_blank" href="https://facebook.com/messages/">
													<i class="fa fa-messenger"></i>
												</a>
											</div>
										</div>
									</div>
								</li>
							</#list>
						</ul>
					</div>
					<script type="text/javascript" src="/obbresources/js/facebook/fbmutual.js"></script>
				</#if>
			</div>
			<div class="clearer"></div>
			<div class="row">
				<div class='col-lg-12 call-to-action'>
					<ul class="list-info">
						<a href="tel:+33753338181"><li class="phone"><em class="fa fa-phone"></em>+33 763 571 929 (Viber)</li></a>
						<a href="tel:+0936166620"><li class="phone"><em class="fa fa-mobile"></em>&nbsp;&nbsp;093 6166 620</li></a>
						<a onclick="return window.scrollTo(0,document.body.scrollHeight);return false;" class="pointer"><li class="address"><em class="fa fa-home"></em>2 rue Marivaux, Paris, France</li></a>
					</ul>
				</div>
			</div>
			<div class="short-description">
				<#if product.longDescription?has_content>
					${StringUtil.wrapString(product.longDescription?if_exists)}
					<#else>
					${uiLabelMap.BENoDescription}
				</#if>
            </div>
            <div class="obb-fbsharing">
				${setContextField("currentUrl", "productmaindetail?product_id=${product.productId?if_exists}")}
				${screens.render("component://obb/widget/CommonScreens.xml#facebooksharing")}
			</div>
		</div>
		<div class="box-collateral box-up-sell long-description margin-top10" style="width:100%;">
			<div class="content-upsell">
				<#include "component://obb/webapp/obb/includes/catalog/productInformation.ftl">
			</div>
		</div>
	</div>
</div>
</div>
<#else>
	<#include "component://obb/webapp/obb/includes/catalog/PageNotFound.ftl">
</#if>