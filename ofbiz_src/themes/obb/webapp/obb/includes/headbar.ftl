<div class="main">
	<div class="inner clearfix">
		<!-- BEGIN: WELCOME FIXME: apply locale -->
		<div class="jm-tl-head">
			<div class="jm-datetime">
				<i class="fa fa-phone hotline"></i>
				<div class="obb-hotline"><a class="hotline" href="tel:0936166620">HOTLINE: 093 6166 620</a></div>
			</div>

			<div class="jm-navigation hide" id="top-nav">
				<span class="jm-nav-text"><i class="fa fa-bars"></i>${uiLabelMap.BSCategories}</span>
				<div class="jm-nav-category vertical-mega-menu">

				</div>
			</div>
		</div>
		<!-- END: WELCOME -->
		<!-- BEGIN: MY CART-->
		<#include "/obb/webapp/obb/includes/cart/microcart.ftl"/>
		<!-- END: MY CART -->
		<!-- BEGIN: QUICK ACCESS -->
		<div class="action-bar-container">
		<div id="jm-quickaccess" class="quick-access has-toggle">
			<div class="btn-toggle quickaccess-toggle">
				<i class="fa fa-user"></i><strong>${uiLabelMap.BEMyAccount}</strong>
			</div>
			<div class="inner-toggle">
				<div class="shop-access">
					<ul class="links">
                        <li class=""><a href="<@ofbizUrl>dashboard</@ofbizUrl>" title="${uiLabelMap.BEMyAccount}">${uiLabelMap.BEMyAccount}</a></li>
                       <!-- <li><a href="<@ofbizUrl>editShoppingList</@ofbizUrl>" title="${uiLabelMap.ObbMyWishList}">${uiLabelMap.ObbMyWishList}</a></li>
                        <li><a href="<@ofbizUrl>checkoutoptions</@ofbizUrl>" title="${uiLabelMap.ObbCheckout}" class="top-link-checkout">${uiLabelMap.ObbCheckout}</a></li> -->
                        <#if userLogin?has_content && userLogin.userLoginId != "anonymous">
							<li><a href="<@ofbizUrl>orderhistory</@ofbizUrl>" title="${uiLabelMap.ObbOrderHistory}">${uiLabelMap.ObbOrderHistory}</a></li>
							<li class=" last"><a href="<@ofbizUrl>logout/main</@ofbizUrl>" title="${uiLabelMap.ObbLogOut}">${uiLabelMap.ObbLogOut}</a></li>
						<#else>
                        	<li class=" last"><a href="<@ofbizUrl>checkLogin/main</@ofbizUrl>" title="${uiLabelMap.ObbLogIn}">${uiLabelMap.ObbLogIn}</a></li>
                        </#if>
                    </ul>
				</div>
			</div>
		</div>
		<!-- END: QUICK ACCESS -->
		<!-- BEGIN: QUICK ACCESS -->
		<div id="jm-setting" class="quick-setting has-toggle">
		<!-- 
			<div class="btn-toggle setting-toggle">
				<#assign localeStr = locale?string>
				<#if localeStr != "vi" && localeStr != "vi_VN" && localeStr != "en" && localeStr != "fr">
					<#assign localeStr = "en">
				</#if>
				<img src="/obbresources/skin/frontend/default/jm_megamall/images/flags/${localeStr}.png">
			</div>
			<div class="inner-toggle inner-toggle-right">
				<div class="form-language">
					<label for="select-language">${uiLabelMap.ObbYourLanguage}:</label>
					<ul id="select-language" class="ul-dropdown">
						<#assign availableLocales = Static["org.ofbiz.base.util.UtilMisc"].availableLocales()/>
				        <#list availableLocales as availableLocale>
				        	<#if locale.toString() != availableLocale.toString()>	
					        	<li>
									<a onclick="submitLanguage('${availableLocale.toString()}')" class="lang-flag lang-default" title="${availableLocale.getDisplayName(availableLocale)}" href="javascript:void(0);">
										<img src="/obbresources/skin/frontend/default/jm_megamall/images/flags/${availableLocale.toString()}.png">
									</a>
								</li>
							</#if>
						</#list>
					</ul>
					<form method="post" name="chooseLanguage" id="chooseLanguage" action="<@ofbizUrl>setSessionLocale</@ofbizUrl>" style="display:none;">
						<input name="newLocale" id="newlocale" type="text" value="en"/>
						<script type="text/javascript">
							function submitLanguage(lcl){
								jQuery('input:hidden#newlocale').val(lcl);
								jQuery("#chooseLanguage").submit();
							}
						</script>
					</form>
				</div>
				<!-- <div class="colors-setting"><label for="select-colors">${uiLabelMap.ObbColor}:</label>
					<a class="colors-default" href="javascript:void(0);"><span>default</span></a>
				</div> -->
			</div>
		</div>
	
		<div id="jm-set-location" class="quick-setting">
			<#if productStore.productStoreId == "ECOMMERCE_02">
				<i id="iSetLocation" class="fa fa-map-marker location pointer" title="${uiLabelMap.BSSouth}"></i>
				<#else>
				<i id="iSetLocation" class="fa fa-map-marker location pointer" title="${uiLabelMap.BSNorth}"></i>
			</#if>
		</div>
		</div>
		<!-- END: QUICK ACCESS -->
	</div>
</div>


<script type="text/javascript">
//<![CDATA[
jQuery = jQuery.noConflict(true);
(function($) {
	jQuery( document ).ready(function( $ ) {
		if (($('#jm-col1 .block-account').length > 0)&($(window).width()<968)){
			$('#jm-quickaccess .shop-access').hide();
			$('#jm-col1 .block-account').clone().prependTo($('#jm-quickaccess .inner-toggle'));
		}
		$(window).resize(function(){
			if (!$('#jm-quickaccess .block-account').length){
				if (($('#jm-col1 .block-account').length > 0)&($(window).width()<968)){
					$('#jm-quickaccess .shop-access').hide();
					$('#jm-col1 .block-account').clone().prependTo($('#jm-quickaccess .inner-toggle'));
				}
			}
		});
		var left;
		if($('#left-nav').length != 0){
			left = $('#left-nav');
		}else{
			left = $("#main-nav");
		}
		if(left){
			var top = left.offset().top;
			var height = left.height();
			var offset = top + height;
			$(window).scroll(function () {
				var scroll = $(window).scrollTop();
				if(scroll >= offset){
					$('#top-nav').show();
				}else if(scroll <= top){
					$('#top-nav').hide();
				}

			});
		}
	});

	$("#jm-head #jm-quickaccess,#jm-setting").mouseenter(function (){
		$(this).children().addClass("active");
		if ($(window).width()<462){
			//$("#jm-header").css("z-index",1000); //this code effect to mobile
		}

	}).mouseleave(function (){
		//$("#jm-header").css("z-index",""); //this code effect to mobile
		$(this).children().removeClass("active");

	});
	$("#jm-search,#jm-mycart").mouseenter(function(){
		if(!$(this).siblings(".inner-toggle").hasClass("active")) {
			//$("#jm-header").css("z-index",1000);
			$(this).children().addClass("active");
		}

	}).mouseleave(function(){
		if($(this).children(".inner-toggle").hasClass("active")) {
			$("#jm-header").css("z-index","");
			$(this).children().removeClass("active")
		}

	});
	$("#jm-search .fa-search").click(function(){
		$("#searchInput").focus();
	});
	$('input').blur(function() {
		$(".zopim").removeClass("hide");
	}).focus(function() {
		$(".zopim").addClass("hide");
	});
	$("#jm-quickaccess .btn-toggle").hover(function(e){
		$("#jm-quickaccess").toggleClass("active");
		if($("#jm-quickaccess").hasClass("active")){
				if(window.myaccountIScrol !== undefined && window.myaccountIScrol !== null){
					 window.myaccountIScrol.destroy();
					 window.myaccountIScrol  = null;
				}
				 if($("#myaccountscroll").length){
				  windowheight = $(window).height()-$("#jm-head").height();
				  windowheight = windowheight - parseInt($("#jm-quickaccess .inner-toggle").css("padding-top"));
				  if($("#jm-quickaccess .inner-toggle").height() > windowheight){
					 $("#myaccountscroll").css("height",windowheight);
				  }
				  setTimeout(function(){
					  window.myaccountIScrol = new iScroll("myaccountscroll",{vScrollbar: true, useTransform: true,hScrollbar: false});
				  },100);
				 }else{
					quickaccess = $("#jm-quickaccess .inner-toggle").html();
					myaccount = $('<div class="inner-togglecontent" />').append($("#jm-quickaccess .inner-toggle").html());
					myaccount.css({float:"left",height:"auto"});
					$("#jm-quickaccess .inner-toggle").html("");
					myaccountscroll = $('<div id="myaccountscroll" />');
					myaccount.appendTo(myaccountscroll);

					windowheight = $(window).height()-$("#jm-head").height();
					windowheight = windowheight - parseInt($("#jm-quickaccess .inner-toggle").css("padding-top"));
					myaccountscroll.appendTo($("#jm-quickaccess .inner-toggle"));

						setTimeout(function(){
							  if($("#jm-quickaccess .inner-toggle").height() > windowheight){
								myaccountscroll.css("height",windowheight);
								window.myaccountIScrol = new iScroll("myaccountscroll",{vScrollbar: true, useTransform: true,hScrollbar: false});
								 window.myaccountIScrol.refresh();
							  }

						},100);

				   }
		}

	});
})(jQuery);
//]]>
</script>
