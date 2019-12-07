<#assign price = priceMap?if_exists/>
<#assign productImageList = productImageList?if_exists/>
<div class="wtop">
	<h1>${productContentWrapper.get("PRODUCT_NAME")?if_exists}</h1>
	<div itemtype="http://schema.org/AggregateRating" itemscope="" itemprop="aggregateRating" class="product-reviews-overview">
		<div class="product-review-box">
			<div class="rating">
				<div style="width: 90%"></div>
			</div>
		</div>
		<div class="product-no-reviews">
			<a href="/productreviews/71">(Nhận xét sản phẩm)</a>
		</div>
	</div>
	<div class="fb-like" data-share ="true" data-action="like" data-layout="button_count" data-href="/DetailsProduct.html"></div>
</div>
<aside class="col-lg-4 picture">
	<img width="400" height="400" src="${productContentWrapper.get("SMALL_IMAGE_URL")?if_exists}" alt="Best slim plus"/>
</aside>
<aside class="col-lg-4">
	<div class="price">
		<#if price.competitivePrice?exists && price.price?exists && price.price &lt; price.competitivePrice>
		<div>
			${uiLabelMap.ProductCompareAtPrice}: <span class="basePrice"><@ofbizCurrency amount=price.competitivePrice isoCode=price.currencyUsed/></span>
		</div>
		</#if>
		<#if price.listPrice?exists && price.price?exists && price.price &lt; price.listPrice>
		<div>
			${uiLabelMap.ProductListPrice}: <span class="basePrice"><@ofbizCurrency amount=price.listPrice isoCode=price.currencyUsed/></span>
		</div>
		</#if>
		<#if price.listPrice?exists && price.defaultPrice?exists && price.price?exists && price.price &lt; price.defaultPrice && price.defaultPrice &lt; price.listPrice>
		<div>
			${uiLabelMap.ProductRegularPrice}: <span class="basePrice"><@ofbizCurrency amount=price.defaultPrice isoCode=price.currencyUsed/></span>
		</div>
		</#if>
		<#if price.specialPromoPrice?exists>
		<div>
			${uiLabelMap.ProductSpecialPromoPrice}: <span class="basePrice"><@ofbizCurrency amount=price.specialPromoPrice isoCode=price.currencyUsed/></span>
		</div>
		</#if>
		<div>
			<strong> <#if price.isSale?exists && price.isSale> <span class="salePrice">${uiLabelMap.OrderOnSale}!</span> <#assign priceStyle = "price-tag"/>
			<#else>
			<#assign priceStyle = "regularPrice"/>
			</#if>
			${uiLabelMap.OrderYourPrice}:
			<div id="variant_price_display" style="display:inline;">
				<#if "Y" = product.isVirtual?if_exists> ${uiLabelMap.CommonFrom} </#if><span class="${priceStyle}"><@ofbizCurrency amount=price.price isoCode=price.currencyUsed/></span>
			</div> <#if product.productTypeId?if_exists == "ASSET_USAGE" || product.productTypeId?if_exists == "ASSET_USAGE_OUT_IN">
			<#if product.reserv2ndPPPerc?exists && product.reserv2ndPPPerc != 0>
			<br/>
			<span class="${priceStyle}">${uiLabelMap.ProductReserv2ndPPPerc}<#if !product.reservNthPPPerc?exists || product.reservNthPPPerc == 0>${uiLabelMap.CommonUntil} ${product.reservMaxPersons?if_exists}</#if> <@ofbizCurrency amount=product.reserv2ndPPPerc*price.price/100 isoCode=price.currencyUsed/></span></#if> <#if product.reservNthPPPerc?exists &&product.reservNthPPPerc != 0>
			<br/>
			<span class="${priceStyle}">${uiLabelMap.ProductReservNthPPPerc} <#if !product.reserv2ndPPPerc?exists || product.reserv2ndPPPerc == 0>${uiLabelMap.ProductReservSecond} <#else> ${uiLabelMap.ProductReservThird} </#if> ${uiLabelMap.CommonUntil} ${product.reservMaxPersons?if_exists}, ${uiLabelMap.ProductEach}: <@ofbizCurrency amount=product.reservNthPPPerc*price.price/100 isoCode=price.currencyUsed/></span></#if> <#if (!product.reserv2ndPPPerc?exists || product.reserv2ndPPPerc == 0) && (!product.reservNthPPPerc?exists || product.reservNthPPPerc == 0)>
			<br/>
			${uiLabelMap.ProductMaximum} ${product.reservMaxPersons?if_exists} ${uiLabelMap.ProductPersons}.</#if>
			</#if> </strong>
		</div>
		<#if price.listPrice?exists && price.price?exists && price.price &lt; price.listPrice>
		<#assign priceSaved = price.listPrice - price.price/>
		<#assign percentSaved = (priceSaved / price.listPrice) * 100/>
		<div>
			${uiLabelMap.OrderSave}: <span class="basePrice"><@ofbizCurrency amount=priceSaved isoCode=price.currencyUsed/> (${percentSaved?int}%)</span>
		</div>
		</#if>
		<#-- show price details ("showPriceDetails" field can be set in the screen definition) -->
		<#if (showPriceDetails?exists && showPriceDetails?default("N") == "Y")>
		<#if price.orderItemPriceInfos?exists>
		<#list price.orderItemPriceInfos as orderItemPriceInfo>
		<div>
			${orderItemPriceInfo.description?if_exists}
		</div>
		</#list>
		</#if>
		</#if>
	</div>
	<span class="infopromotion">Quà khuyến mại</span>
	<div class="detailpromotion">
		• Mua 1 Best slim plus tặng 1 lọ tinh dầu hoa oải hương trị giá 300.000đ
	</div>
	<ul class="infocomes">
		<li>
			<i class="allicon-check"></i>Nhà thuốc uy tín 27 năm (since 1988)
		</li>
		<li>
			<i class="allicon-check"></i>Cam kết hàng chính hãng của <a href="#">Best Group</a>
		</li>
		<li>
			<i class="allicon-check"></i>Tư vấn bởi dược sĩ kinh nghiệm trên 5 năm
		</li>
		<li>
			<i class="allicon-check"></i>Đổi trả hàng trong vòng 7 ngày <a href="#">(tìm hiểu)</a>
		</li>
		<li>
			<i class="allicon-check"></i><a href="#">Giao hàng toàn quốc</a>, Nội thành HN - HCM giao trong 1h - 2h
		</li>
	</ul>
	<!-- sản phẩm miễn phí giao hàng để 'Giao hàng tận nơi miễn phí' nếu không để 'Giao hàng tận nơi' -->
	<form method="post" action="<@ofbizUrl>additem</@ofbizUrl>" name="addform"  style="margin: 0;">
	  <#if requestAttributes.paramMap?has_content>
	      <input type="hidden" name="itemComment" value="${requestAttributes.paramMap.itemComment?if_exists}"/>
	      <input type="hidden" name="shipBeforeDate" value="${requestAttributes.paramMap.shipBeforeDate?if_exists}"/>
	      <input type="hidden" name="shipAfterDate" value="${requestAttributes.paramMap.shipAfterDate?if_exists}"/>
	      <input type="hidden" name="itemDesiredDeliveryDate" value="${requestAttributes.paramMap.itemDesiredDeliveryDate?if_exists}"/>
	  </#if>
	  <input type="hidden" name="add_product_id" value="${product.productId}"/>
	</form>
	<a href="javascript:addItem()" class="btn-buy">MUA NGAY <span>Giao hàng miễn phí tận nơi, nhận hàng trả tiền</span></a>
	<a class="call-back" title="Để lại số điện thoại, chúng tôi sẽ gọi lại" data-target="#phoneModal" data-toggle="modal" href="#" rel="nofollow">GỌI LẠI CHO TÔI <span>Để lại số điện thoại, chúng tôi gọi lại ngay</span></a>
	<p class="call-center">
		Gọi đặt mua <span>1800.6666</span> (tư vấn miễn phí)
	</p>
</aside>
<#include "/dpc/webapp/dpc/includes/content/storeInfo.ftl"/>
<hr/>
<div class="productinfo">
	<div class="col-lg-8">
		<div class="row">
			<h3>Hình ảnh của Thuốc giảm cân Best Slim Plus</h3>
			<#if Static["org.ofbiz.base.util.UtilValidate"].isNotEmpty(slideOfProduct)>
			<div data-ride="carousel" class="carousel slide" id="myCarousel">
				<ol class="carousel-indicators">
					<#assign active = "active"/>
					<#list slideOfProduct as slide>
						<li data-slide-to="${slide_index}" data-target="#myCarousel" class="${active?if_exists}"></li>
						<#assign active = ""/>
					</#list>
				</ol>
				<div class="carousel-inner">
						<#assign active = "active"/>
						<#list slideOfProduct as slide>
							<div class="item ${active?if_exists}">
								<img width="833" height="425" title="${StringUtil.wrapString((product.internalName)?if_exists)}" alt="${StringUtil.wrapString((product.internalName)?if_exists)}" src="${StringUtil.wrapString((slide.originalImageUrl)?if_exists)}"/>
								<div class="carousel-caption">
									${StringUtil.wrapString((slide.description)?if_exists)}
								</div>
							</div>
						<#assign active = ""/>
						</#list>
				</div>
				<a data-slide="prev" href="#myCarousel" class="left carousel-control"><span class="glyphicon glyphicon-chevron-left"></span></a>
				<a data-slide="next" href="#myCarousel" class="right carousel-control"><span class="glyphicon glyphicon-chevron-right"></span></a>
			</div>
			</#if>
		</div>
	</div>
	<div class="col-lg-4">
		<h3>Thông tin sản phẩm</h3>
		<ul>
			<li>
				<span>Thương hiệu: </span>
				<div>
					<a href="#">${(product.brandName)?if_exists}</a>
				</div>
			</li>
			<#if (productSpecifications.effects)?exists>
			<li>
				<span>Công dụng: </span>
				<div>
					${StringUtil.wrapString((productSpecifications.effects)?if_exists)}
				</div>
			</li>
			</#if>
			<#if (productSpecifications.composition)?exists>
			<li>
				<span>Thành phần: </span>
				<div>
					${StringUtil.wrapString((productSpecifications.composition)?if_exists)}
				</div>
			</li>
			</#if>
			<#if (productSpecifications.shelfLife)?exists>
			<li>
				<span>Hạn sử dụng: </span>
				<div>
					${StringUtil.wrapString((productSpecifications.shelfLife)?if_exists)}
				</div>
			</li>
			</#if>
			<#if (productSpecifications.users)?exists>
			<li>
				<span>Đối tượng sử dụng: </span>
				<div>
					${StringUtil.wrapString((productSpecifications.users)?if_exists)}
				</div>
			</li>
			</#if>
			<#if (productSpecifications.instructions)?exists>
			<li>
				<span>Hướng dẫn sử dụng:</span>
				<div>
					${StringUtil.wrapString((productSpecifications.instructions)?if_exists)}
				</div>
			</li>
			</#if>
			<li>
				<span>Xuất xứ: </span>
				<div>
					<a href="#">Mỹ</a>
				</div>
			</li>
			<#if (productSpecifications.license)?exists>
			<li>
				<span>Giấy phép:</span>
				<div>
					${StringUtil.wrapString((productSpecifications.license)?if_exists)}
				</div>
			</li>
			</#if>
			<#if (productSpecifications.packing)?exists>
			<li>
				<span>Quy cách:</span>
				<div>
					${(productSpecifications.packing)?if_exists}
				</div>
			</li>
			</#if>
			<#if (productSpecifications.contraindications)?exists>
			<li>
				<span>Chống chỉ định:</span>
				<div>
					${StringUtil.wrapString((productSpecifications.contraindications)?if_exists)}
				</div>
			</li>
			</#if>
		</ul>
	</div>
</div>
<div class="col-lg-8">
	<div class="row">
		<aside class="contentpro">
		
		<#assign resultValue = dispatcher.runSync("loadProductIntroduction", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", product_id, "contentId", parameters.contentId))/>
		<#if Static["org.ofbiz.service.ServiceUtil"].isSuccess(resultValue)>
			<#assign productIntroduction = resultValue.get("productIntroduction")/>
		</#if>
		<#if Static["org.ofbiz.base.util.UtilValidate"].isNotEmpty(productIntroduction)>
			${StringUtil.wrapString((productIntroduction.longDescription)?if_exists)}
		</#if>
		</aside>
	</div>
</div>
<div class="col-lg-4">
	<aside class="sidebar">
		<h3>Bài viết nên xem</h3>
		<ul>
			<li>
				<a href="#" title=""><img width="100" height="67" src="/dpc/DemoImg/6-cong-dung-lam-dep-tu-qua-chanh.jpg" alt=""/> <h4>12 động tác tập thể dục giúp bạn tăng tốc quá trình giảm cân khi sử dụng Best Slim Plus</h4> <span>5 giờ trước | 23 <i class="allicon-cmt"></i></span> </a>
			</li>
			<li>
				<a href="#" title=""><img width="100" height="67" src="/dpc/DemoImg/6-cong-dung-lam-dep-tu-qua-chanh.jpg" alt=""/> <h4>12 công dụng của lá sen mà không phải ai cũng biết</h4> <span>5 giờ trước | 23 <i class="allicon-cmt"></i></span> </a>
			</li>
			<li>
				<a href="#" title=""><img width="100" height="67" src="/dpc/DemoImg/6-cong-dung-lam-dep-tu-qua-chanh.jpg" alt=""/> <h4>9 công dụng tuyệt với nếu mỗi ngày bạn uống một tách trà xanh</h4> <span>5 giờ trước | 23 <i class="allicon-cmt"></i></span> </a>
			</li>
			<li>
				<a href="#" title=""><img width="100" height="67" src="/dpc/DemoImg/6-cong-dung-lam-dep-tu-qua-chanh.jpg" alt=""/> <h4>Thừ cân béo phì, bạn chỉ nhìn thấy bề nổi của “tảng băng chìm bất cứ lúc nào”</h4> <span>5 giờ trước | 23 <i class="allicon-cmt"></i></span> </a>
			</li>
			<li>
				<a href="#" title=""><img width="100" height="67" src="/dpc/DemoImg/6-cong-dung-lam-dep-tu-qua-chanh.jpg" alt=""/> <h4>Anh Quốc: Cô gái chết bất thường sau khi sử dụng thuốc giảm cân không rõ nguồn gốc</h4> <span>5 giờ trước | 23 <i class="allicon-cmt"></i></span> </a>
			</li>
		</ul>
	</aside>
</div>
<div class="clearfix"></div>

<div class="tag">
	<span>Thẻ sản phẩm: </span>
	<a href="#" title="">Best slim (2)</a><a href="#" title="">Thuốc giảm cân (20)</a><a href="#" title="">Best slim (2)</a><a href="#" title="">Thuốc giảm cân (20)</a>
</div>

<script>
	function addItem() {
		if (document.addform.add_product_id.value == 'NULL') {
			//showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.CommonPleaseSelectAllRequiredOptions}");
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
</script>