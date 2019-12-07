<link rel="stylesheet" type="text/css" href="/obbresources/asset/owl-carousel/owl.carousel.css" media="all" />
<script type="text/javascript" src="/obbresources/asset/owl-carousel/owl.carousel.js"></script>
<script type="text/javascript" src="/obbresources/asset/owl-carousel/jquery.ba-throttle-debounce.js"></script>

<div class="flexslider mainslider">
    <div id="em_owlcarousel_15_15219_sync1" class="owl-carousel">
    	<#if mainSlide?has_content>
    	<#list mainSlide as slide>
    		<#if slide.originalImageUrl?has_content>
    		
    		<div class="item">
                <a href="<#if (slide.url)?exists><@ofbizUrl>${StringUtil.wrapString((slide.url)?if_exists)}</@ofbizUrl></#if>"> <img alt="${StringUtil.wrapString((slide.description)?if_exists)}" class="lazyOwl img-responsive" src="/obbresources/images/preload.gif" data-src="${StringUtil.wrapString((slide.originalImageUrl)?if_exists)}" /> </a>
                <div class="em-owlcarousel-description">
                    <div class="fadeInLeft em-owlcarousel-des em-owlcarousel-des-1">
                        <h4 style="font:italic 2rem/1 Lora,Helvetica Neue, Helvetica, Arial, sans-serif;  margin-bottom: 2.3rem;"></h4>
                        <h2 style="font:700 6rem/1 Raleway,Helvetica Neue, Helvetica, Arial, sans-serif;  margin-bottom: 2rem;" class="em-text-upercase"></h2>
                        <h4 class="em-text-upercase" style="font:500 2rem/1 Lato,Helvetica Neue, Helvetica, Arial, sans-serif;">${StringUtil.wrapString((slide.description)?if_exists)}</h4>
                    </div>
                </div>
            </div><!-- /.item -->
    		
    		</#if>
    	</#list>
    	
    	<#else>
    	<div class="item">
            <a href=""> <img alt="Cosmetic" class="lazyOwl img-responsive" src="/obbresources/images/preload.gif" data-src="http://placehold.it/580x500" /> </a>
            <div class="em-owlcarousel-description">
                <div class="fadeInLeft em-owlcarousel-des em-owlcarousel-des-1">
                    <h4 style="font:italic 2rem/1 Lora,Helvetica Neue, Helvetica, Arial, sans-serif;  margin-bottom: 2.3rem;">Hot discounts of the week</h4>
                    <h2 style="font:700 6rem/1 Raleway,Helvetica Neue, Helvetica, Arial, sans-serif;  margin-bottom: 2rem;" class="em-text-upercase">sale off</h2>
                    <h4 class="em-text-upercase" style="font:500 2rem/1 Lato,Helvetica Neue, Helvetica, Arial, sans-serif;">only on $99.99</h4>
                </div>
            </div>
        </div><!-- /.item -->
    	</#if>
    	
    </div>
</div>

<script>
	$(document).ready(function() {
		/* Main Slider */
		jQuery('#em_owlcarousel_15_15219_sync1').owlCarousel({
            singleItem: true,
            responsiveRefreshRate: 200,
            paginationSpeed: 2000,
            rewindSpeed: 1000,
            lazyLoad: true,
            slideSpeed: 200,
            navigation: true,
            pagination: true,
            navigationText: ["Pre", "Next"],
            transitionStyle: 'fade',
            autoPlay: true,
        });
	});
</script>