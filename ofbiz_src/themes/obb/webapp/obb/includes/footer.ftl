<!-- BOTTOM SPOTLIGHT -->
<div id="jm-bots1" class="jm-spotlight wrap clearfix">
	<div class="main col2-set clearfix">
		<div class="inner clearfix">
			<div class="about-follow-us">
				<div class="block block-aboutus">
					<div class="block-title"><strong><span>${uiLabelMap.ObbAboutUs}</span></strong></div>
					<div class="block-content">
						${StringUtil.wrapString((aboutUs.description)?if_exists)}
					</div>
				</div>
				<div class="block block-followus">
					<div class="block-title"><strong><span>${uiLabelMap.ObbFollow}</span></strong></div>
						<div class="block-content">
							<ul class="list-socials">
								<li class="first"><a href="https://www.facebook.com/bhappy.vn"><em class="fa fa-facebook">&nbsp;</em></a></li>
								<li><a href="#"> <em class="fa fa-google-plus">&nbsp;</em></a></li>
							<!--	<li><a href="#"><em class="fa fa-twitter">&nbsp;</em></a></li>
								<li class="last"><a href="#"> <em class="fa fa-youtube">&nbsp;</em></a></li> -->
							</ul>
						</div>
					</div>
				</div>
				<div class="block block-subscribe">
				    <#include "customer/miniSignUpForContactList.ftl"/>
				</div>
				<div class="block block-information col-1 first">
					<div class="col-inner">
						<div class="block-title"><strong><span>${uiLabelMap.ObbInformation}</span></strong></div>
						<div class="block-content">
							<ul>
								<li><a href="<@ofbizUrl>viewcontent?cId=about</@ofbizUrl>">${uiLabelMap.ObbAboutUs}</a></li>
								<li><a href="<@ofbizUrl>viewcontent?cId=termcond</@ofbizUrl>">${uiLabelMap.ObbTermACond}</a></li>
								<li>
								<#if userLogin?has_content && userLogin.userLoginId != "anonymous">
								  <a href="<@ofbizUrl>contactus</@ofbizUrl>">${uiLabelMap.ObbContactUs}</a></li>
								<#else>
								  <a href="<@ofbizUrl>AnonContactus</@ofbizUrl>">${uiLabelMap.ObbContactUs}</a></li>
								</#if>
								<li><a href="<@ofbizUrl>SiteMap</@ofbizUrl>">${uiLabelMap.ObbSiteMap}</a></li>
							</ul>
						</div>
					</div>
				</div>
				<div class="block block-why-us col-2">
					<div class="col-inner">
						<div class="block-title"><strong><span>${uiLabelMap.ObbPolicy}</span></strong></div>
						<div class="block-content">
						<ul>
							<li><a href="<@ofbizUrl>viewcontent?cId=c_csbaomat</@ofbizUrl>">${uiLabelMap.ObbPrivacyPolicy}</a></li>
							<li><a href="<@ofbizUrl>viewcontent?cId=c_ckdambao</@ofbizUrl>">${uiLabelMap.ObbCommitment}</a></li>
							<li><a href="<@ofbizUrl>viewcontent?cId=c_doitrahang</@ofbizUrl>">${uiLabelMap.ObbReturnsAndExchanges}</a></li>
							<li><a href="<@ofbizUrl>viewcontent?cId=c_ptthanhtoan</@ofbizUrl>">${uiLabelMap.ObbPaymentMethods}</a></li>
							<li><a href="<@ofbizUrl>viewcontent?cId=c_ptvanchuyen</@ofbizUrl>">${uiLabelMap.ObbShippingPolicy}</a></li>
						</ul>
					</div>
				</div>
			</div>
			<div class="block block-my-account col-3">
				<div class="col-inner">
					<div class="block-title"><strong><span>${uiLabelMap.ObbMyAccount2}</span></strong></div>
					<div class="block-content">
						<ul>
							<li><a href="<@ofbizUrl>showcart</@ofbizUrl>">${uiLabelMap.ObbViewCart}</a></li>
						<!--	<li><a href="<@ofbizUrl>editShoppingList</@ofbizUrl>">${uiLabelMap.ObbMyWishList}</a></li>
							<li><a href="<@ofbizUrl>onePageCheckout</@ofbizUrl>">${uiLabelMap.ObbCheckout}</a></li> -->
							<li><a href="<@ofbizUrl>orderhistory</@ofbizUrl>">${uiLabelMap.ObbTrackMyOrder}</a></li>
						</ul>
					</div>
				</div>
			</div>
			<div class="block block-links col-4 last">
				<div class="col-inner">
					<div class="block-title"><strong><span>${uiLabelMap.ObbUsefulLinks}</span></strong></div>
					<div class="block-content">
						<ul>
							<li><a href="<@ofbizUrl>contentcategory?catContentId=HO-TRO</@ofbizUrl>">${uiLabelMap.ObbHelpFAQ2}</a></li>
							<li><a href="<@ofbizUrl>viewcontent?cId=c_giftvoucher</@ofbizUrl>">${uiLabelMap.ObbGiftVouchers}</a></li>
							<li><a href="<@ofbizUrl>viewcontent?cId=c_hddathang</@ofbizUrl>">${uiLabelMap.ObbHelpBuy}</a></li>
							<li><a href="<@ofbizUrl>viewcontent?cId=c_listweb</@ofbizUrl>">${uiLabelMap.ObbListWeb}</a></li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<!-- //BOTTOM SPOTLIGHT -->

<!-- BEGIN: FOOTER -->
<div id="jm-footer" class="wrap clearfix">
	<div class="main">
		<div class="inner clearfix">
			<div class="inner2 clearfix">
			  <div class="jm-info clearfix"></div>
				<div class="jm-legal">
				
				<table>
					<tr>
						<td>Thông tin tài khoản thanh toán:</td>
						<td style="padding-left: 10px;">VCB 0031 00025 3195 Hải Phòng Nguyễn Thị Huyền Trang</td>
					</tr>
					<tr>
						<td></td>
						<td style="padding-left: 10px;">TCB 1903 034 9174 666 Tô Hiệu, Hải Phòng Nguyễn Thị Huyền Trang</td>
					</tr>
				</table>
				<br/>
				Copyright © 2015 Bhappy.vn. All Rights Reserved.Powered by <a href="https://www.facebook.com/bhappy.vn" title="Bhappy" rel="nofollow">Bhappy</a>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-55842259-1', 'auto');
  ga('require', 'displayfeatures');
  ga('send', 'pageview');

</script>
<!-- END: FOOTER -->
<div id="cboxOverlay" style="display: none;"></div>
<div id="colorbox" class="" role="dialog" tabindex="-1" style="display: block;">
	<div id="cboxWrapper">
		<div>
			<div id="cboxTopLeft" style="float: left;"></div>
			<div id="cboxTopCenter" style="float: left;"></div>
			<div id="cboxTopRight" style="float: left;"></div>
		</div>
		<div style="clear: left;">
			<div id="cboxMiddleLeft" style="float: left;">
			</div>
			<div id="cboxContent" style="float: left;">
				<div id="cboxTitle" style="float: left;"></div>
				<div id="cboxCurrent" style="float: left;"></div>
				<button type="button" style="border:0;" id="cboxPrevious"></button>
				<button type="button" style="border:0;" id="cboxNext"></button>
				<button id="cboxSlideshow"></button>
				<div id="cboxLoadingOverlay" style="float: left;"></div>
				<div id="cboxLoadingGraphic" style="float: left;"></div>
			</div>
			<div id="cboxMiddleRight" style="float: left;"></div>
		</div>
		<div style="clear: left;">
			<div id="cboxBottomLeft" style="float: left;"></div>
			<div id="cboxBottomCenter" style="float: left;"></div>
			<div id="cboxBottomRight" style="float: left;"></div>
		</div>
	</div>
	<div style="position: absolute; width: 9999px; visibility: hidden; display: none; max-width: none;"></div>
</div>
<iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d5248.720782590947!2d2.332451662314348!3d48.87040590815822!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x47e66e3bcba3deb1%3A0xa204c2b159245312!2s2+Rue+de+Marivaux%2C+75002+Paris%2C+Ph%C3%A1p!5e0!3m2!1svi!2s!4v1457503620135" width="100%" height="400" frameborder="0" style="border:0" allowfullscreen></iframe>
