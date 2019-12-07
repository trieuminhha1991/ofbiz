<!--Start of Zopim Live Chat Script-->
<#assign zopim = Static["com.olbius.obb.ConfigUtils"].getZopimConfig(delegator, webSiteId)/>
<script>
	setTimeout(function(){
		<#if zopim?exists>
			${StringUtil.wrapString(zopim)}
		</#if>
	}, 2000);

</script>
<!--End of Zopim Live Chat Script-->