<#--
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script type="text/javascript" src="/poresources/js/product/ProductContent.js"></script>

-->
<style>
	.text-header {
		color: black !important;
	}
	.form-window-content-custom label {
	    margin-top: -4px;
	}
	.boder-all-profile .label {
	    font-size: 14px;
	    text-shadow: none;
	    background-color: #3a87ad !important;
		margin: 0px;
		color: white !important;
    	line-height: 14px !important;
		margin-top: -20px;
	}
	.product-config-image .img-product-large {
	    width: 430px;
    	height: 430px;
    	text-align: center;
	}
	.product-config-image .img-product-small {
		width: 248px;
		height: 248px;
    	text-align: center;
	}
	.product-config-image .img-product-additional {
		width: 248px;
		height: 248px;
    	text-align: center;
	}
	img {
	    max-height: 100%;
	}
	.image-thumb {
		width:21%;
	}
	.ace-thumbnails>li {
		border: 2px solid #dddddd;
	}
</style>
<script>
	var productIdParam = "${parameters.productId?if_exists}";
</script>

<link rel="stylesheet" href="/aceadmin/assets/css/colorbox.css" />

<div class="row-fluid">
	<div class="span12">
		<div>
			<ul class="ace-thumbnails">
				<li>
					<a href="<#if dataProdImages?exists && dataProdImages.largeImageUrl?exists>${dataProdImages.largeImageUrl}<#else>/poresources/logo/product_demo.png</#if>" data-rel="colorbox">
						<img alt="150x150" src="<#if dataProdImages?exists && dataProdImages.largeImageUrl?exists>${dataProdImages.largeImageUrl}<#else>/poresources/logo/product_demo.png</#if>"/>
						<div class="text">
							<div class="inner">${uiLabelMap.BSLargeImage}</div>
						</div>
					</a>
				</li>
				<li class="image-thumb">
					<a href="<#if dataProdImages?exists && dataProdImages.smallImageUrl?exists>${dataProdImages.smallImageUrl}<#else>/poresources/logo/product_demo.png</#if>" data-rel="colorbox">
						<img alt="150x150" src="<#if dataProdImages?exists && dataProdImages.smallImageUrl?exists>${dataProdImages.smallImageUrl}<#else>/poresources/logo/product_demo.png</#if>"/>
						<div class="text">
							<div class="inner">${uiLabelMap.BSSmallImage}</div>
						</div>
					</a>
				</li>
				<li class="image-thumb">
					<a href="<#if dataProdImages?exists && dataProdImages.ADDITIONAL_IMAGE_1?exists>${dataProdImages.ADDITIONAL_IMAGE_1}<#else>/poresources/logo/product_demo.png</#if>" data-rel="colorbox">
						<img alt="150x150" src="<#if dataProdImages?exists && dataProdImages.ADDITIONAL_IMAGE_1?exists>${dataProdImages.ADDITIONAL_IMAGE_1}<#else>/poresources/logo/product_demo.png</#if>"/>
						<div class="text">
							<div class="inner">${uiLabelMap.BSOtherImages} 1</div>
						</div>
					</a>
				</li>
				<li class="image-thumb">
					<a href="<#if dataProdImages?exists && dataProdImages.ADDITIONAL_IMAGE_2?exists>${dataProdImages.ADDITIONAL_IMAGE_2}<#else>/poresources/logo/product_demo.png</#if>" data-rel="colorbox">
						<img alt="150x150" src="<#if dataProdImages?exists && dataProdImages.ADDITIONAL_IMAGE_2?exists>${dataProdImages.ADDITIONAL_IMAGE_2}<#else>/poresources/logo/product_demo.png</#if>"/>
						<div class="text">
							<div class="inner">${uiLabelMap.BSOtherImages} 2</div>
						</div>
					</a>
				</li>
				<li class="image-thumb">
					<a href="<#if dataProdImages?exists && dataProdImages.ADDITIONAL_IMAGE_3?exists>${dataProdImages.ADDITIONAL_IMAGE_3}<#else>/poresources/logo/product_demo.png</#if>" data-rel="colorbox">
						<img alt="150x150" src="<#if dataProdImages?exists && dataProdImages.ADDITIONAL_IMAGE_3?exists>${dataProdImages.ADDITIONAL_IMAGE_3}<#else>/poresources/logo/product_demo.png</#if>"/>
						<div class="text">
							<div class="inner">${uiLabelMap.BSOtherImages} 3</div>
						</div>
					</a>
				</li>
				<li class="image-thumb">
					<a href="<#if dataProdImages?exists && dataProdImages.ADDITIONAL_IMAGE_4?exists>${dataProdImages.ADDITIONAL_IMAGE_4}<#else>/poresources/logo/product_demo.png</#if>" data-rel="colorbox">
						<img alt="150x150" src="<#if dataProdImages?exists && dataProdImages.ADDITIONAL_IMAGE_4?exists>${dataProdImages.ADDITIONAL_IMAGE_4}<#else>/poresources/logo/product_demo.png</#if>"/>
						<div class="text">
							<div class="inner">${uiLabelMap.BSOtherImages} 4</div>
						</div>
					</a>
				</li>
			</ul>
		</div>
	</div>
</div>

<script type="text/javascript" src="/aceadmin/assets/js/jquery.colorbox-min.js"></script>
<script type="text/javascript">
	$(function() {
		var colorbox_params = {
			reposition:true,
			scalePhotos:true,
			scrolling:false,
			previous:'<i class="icon-arrow-left"></i>',
			next:'<i class="icon-arrow-right"></i>',
			close:'&times;',
			current:'{current} of {total}',
			maxWidth:'100%',
			maxHeight:'100%',
			onOpen:function(){
				document.body.style.overflow = 'hidden';
			},
			onClosed:function(){
				document.body.style.overflow = 'auto';
			},
			onComplete:function(){
				$.colorbox.resize();
			}
		};
	
		$('.ace-thumbnails [data-rel="colorbox"]').colorbox(colorbox_params);
		$("#cboxLoadingGraphic").append("<i class='icon-spinner orange'></i>");//let's add a custom loading icon
	})
</script>