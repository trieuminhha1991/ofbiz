<script>
<#if security.hasEntityPermission("ECOMMERCE", "_ADMIN", session) || security.hasEntityPermission("PRODUCT_CONTENT", "_UPDATE", session)
|| security.hasEntityPermission("PRODUCT_CONTENT", "_CREATE", session)>
<#if webSite?has_content>
	$(document).ready(function() {
		var siteName = "${StringUtil.wrapString((webSite.siteName)?if_exists)}";
		if (siteName) {
			$("small").find(".logo-text").find("b").append(" - " + siteName);
		} else {
			confirmChoice();
		}
	});
	<#else>
	$(document).ready(function() {
		confirmChoice();
	});
</#if>
function confirmChoice() {
	if (window.location.href.search(/ListWebsites/g) < 0) {
		bootbox.confirm( "${StringUtil.wrapString(uiLabelMap.ChoiceWebSite)}", "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}", function(result) {
			if (result) {
				window.location.href = "ListWebsites";
			} else {
				confirmChoice();
			}
		});
	}
}
</#if>
</script>