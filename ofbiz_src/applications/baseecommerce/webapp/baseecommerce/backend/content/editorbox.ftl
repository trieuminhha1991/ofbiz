<#if parameters.type == "PRODUCT">
<div class='row-fluid'>
	<div class='span4'>
		<label class='text-right'><b>${uiLabelMap.BSContentType}:</b></label>
	</div>
	<div class='span8'>
		<select id="productContentType">
			<option value="RELATED_ARTICLE">${uiLabelMap.BSContentAboutProduct}</option>
			<option value="INTRODUCTION">${uiLabelMap.BSProductIntroduction}</option>
		</select>
	</div>
</div>

<script>

	$(document).ready(function() {
		$("#productContentType").on('change', function() {
			$("#productContentTypeId").val($("#productContentType").val());
//			if ($("#productContentType").val() == "INTRODUCTION") {
//				var contentId = DataAccess.getData({
//								url: "loadProductIntroduction",
//								data: {productId: "${parameters.productId?if_exists}"},
//								source: "contentId"});
//				if (contentId) {
//					window.location.href = "ContentEditorEngine?contentId=" + contentId + "&productId=" + productId + "&type=PRODUCT";
//				}
//			}
		});
		if ("${parameters.productContentType?if_exists}") {
			$("#contentType").val("${parameters.productContentType?if_exists}");
		}
	});
</script>
</#if>