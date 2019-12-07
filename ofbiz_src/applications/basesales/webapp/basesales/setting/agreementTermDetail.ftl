<link rel="stylesheet" type="text/css" href="/ecommerceresources/css/widgetstyles.css"/>
<script type="text/javascript" src="/dpc/asset/ckeditor/ckeditor.js"></script>

<#assign agreementTerm = delegator.findOne("TermTypeAttr", {"termTypeId" : parameters.termTypeId, "attrName": "defaultValue"}, true)>

<form id="formContent" enctype="multipart/form-data" action="updateTermTypeAttr" method="post">
	<input type="hidden" name="termTypeId" value="${parameters.termTypeId?if_exists}"/>
	<input type="hidden" name="attrName" value="defaultValue"/>
	
	<textarea id="attrValue" name="attrValue">${StringUtil.wrapString((agreementTerm.attrValue)?if_exists)}</textarea>
</form>

<script>
	$().ready(function() {
		ckEditor = CKEDITOR.replace('attrValue', {
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