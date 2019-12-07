<#if path?exists>
	<script type="text/javascript">
		window.parent.CKEDITOR.tools.callFunction("0", encodeURI("${StringUtil.wrapString(path)}"), "");
	</script>
</#if>