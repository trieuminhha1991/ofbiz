<#if parameters.contentId?exists>
<link rel="stylesheet" type="text/css" href="/ecommerceresources/css/widgetstyles.css"/>
	<script type="text/javascript" src="/dpc/asset/ckeditor/ckeditor.js"></script>

	<#assign content = Static["com.olbius.baseecommerce.backend.ConfigWebSiteServices"].getContentById(delegator, userLogin, parameters.contentId) />

	<form id="formContent" action="<@ofbizUrl>updateConfigFooter</@ofbizUrl>" method="post">
		<input type="hidden" name="contentId" value="${StringUtil.wrapString((content.contentId)?if_exists)}"/>
		<textarea id="editor" name="editor">${StringUtil.wrapString((content.longDescription)?if_exists)}</textarea>
	</form>

	<script>
	$(document).ready(function() {
		ckEditor = CKEDITOR.replace('editor', {
			extraPlugins: 'uploadimage,image2,save,wordcount',
			uploadUrl: 'quickUpload',
			height: 400,
			filebrowserImageUploadUrl: 'browserImageUpload',
			stylesSet: [
				{ name: 'Narrow image', type: 'widget', widget: 'image', attributes: { 'class': 'image-narrow' } },
				{ name: 'Wide image', type: 'widget', widget: 'image', attributes: { 'class': 'image-wide' } }
			],
			image2_alignClasses: [ 'image-align-left', 'image-align-center', 'image-align-right' ],
			image2_disableResizer: true
		});
	});
	</script>
</#if>