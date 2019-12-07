<!--Start of Zopim Live Chat Script-->
<#assign google = Static["com.olbius.obb.ConfigUtils"].getGoogleAnalyticConfig(delegator, webSiteId)/>
<script>
	<#if google?exists>
		${StringUtil.wrapString(google)}
	</#if>

</script>
<!--End of Zopim Live Chat Script-->