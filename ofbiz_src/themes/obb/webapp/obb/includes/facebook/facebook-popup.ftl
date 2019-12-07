<script type="text/javascript" src="/obbresources/js/facebook/fbPopup.js"></script>
<script>
	jQuery(document).ready(function($){
		jQuery(".fb-login-bt").click(function(){
			login(jQuery(this));
		});
		jQuery('.fb-icon').click(function(){
			$('.fb-box').addClass('fb-box-in');
		});
		jQuery('.fb-back-icon').click(function(){
			$('.fb-box').removeClass('fb-box-in');
		});
	});
</script>
<div class='fb-icon'>

</div>
<div class="fb-box">
	<div class="fb-inner">
		<div class="fb-login-bt marginbottom-10">
			<i class='fb-icon-small'></i>
			<span>Connect us</span>
		</div>
		<div class="fb-like-box"
			data-href="<#if facebookSettings.FB_PAGE_URL?exists>${facebookSettings.FB_PAGE_URL}<#else></#if>"
			data-colorscheme="<#if facebookSettings.FB_THEME?exists>${facebookSettings.FB_THEME}<#else>light</#if>"
			data-show-faces="<#if facebookSettings.FB_PAGE_FRIEND_SHOW?exists>${facebookSettings.FB_PAGE_FRIEND_SHOW}<#else>true</#if>"
			data-header="<#if facebookSettings.FB_PAGE_HEADER_SHOW?exists>${facebookSettings.FB_PAGE_HEADER_SHOW}<#else>true</#if>"
			data-stream="<#if facebookSettings.FB_PAGE_POST_SHOW?exists>${facebookSettings.FB_PAGE_POST_SHOW}<#else>false</#if>"
			data-show-border="<#if facebookSettings.FB_PAGE_BORDER_SHOW?exists>${facebookSettings.FB_PAGE_BORDER_SHOW}<#else>false</#if>">
		</div>
	</div>
	<div class="fb-back-icon"> </div>
</div>
