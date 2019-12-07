<#assign webSiteBackGround = Static["com.olbius.baseecommerce.backend.ContentUtils"].webSiteBackGround(delegator, webSiteId, false)/>

<#if webSiteBackGround?has_content>
<script>
	var HEADER_BACKGROUND = "${StringUtil.wrapString((webSiteBackGround.HEADER_BACKGROUND)?if_exists)}";
	var INFO_BACKGROUND = "${StringUtil.wrapString((webSiteBackGround.INFO_BACKGROUND)?if_exists)}";
	var FOOTER_BACKGROUND = "${StringUtil.wrapString((webSiteBackGround.FOOTER_BACKGROUND)?if_exists)}";
	$(document).ready(function() {
		if (HEADER_BACKGROUND) {
			$("#jm-wrapper").css("background", "url('" + HEADER_BACKGROUND + "') top center no-repeat");
			$("#jm-breadcrumbs").css("padding", "0px 0px 0px 40px");
		}
		if (INFO_BACKGROUND) {
			$("#jm-bots1").css("background-image", "url('" + INFO_BACKGROUND + "')");
		}
		if (FOOTER_BACKGROUND) {
			$("#jm-footer").css("background-image", "url('" + FOOTER_BACKGROUND + "')");
		} else {
			$("#jm-footer").css("padding", "40px 55px 40px 0px");
		}
	});
</script>
</#if>