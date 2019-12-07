<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<link rel="stylesheet" type="text/css" href="/ecommerceresources/css/widgetstyles.css"/>
<script type="text/javascript" src="/dpc/asset/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="/ecommerceresources/js/backend/content/contentEditorEngine.js"></script>

<#if content?exists>
	<#assign action = "renovateContent"/>
	<#else>
	<#assign action = "saveContent"/>
</#if>
<div class="poststuff">
<div class="postbox-container">
	<#include "editorbox.ftl"/>
</div>

<div class="post-body">

<form id="formContent" enctype="multipart/form-data" action="<@ofbizUrl>${action}</@ofbizUrl>" method="post">
	<input type="hidden" name="type" value="${parameters.type?if_exists}"/>
	<input type="hidden" name="productId" value="${parameters.productId?if_exists}"/>
	<input type="hidden" name="contentCategoryId" value="${parameters.contentCategoryId?if_exists}"/>
	<input type="hidden" id="productContentTypeId" name="productContentTypeId"/>
	<input type="hidden" name="contentId" value="${parameters.contentId?if_exists}"/>

	<div class='row-fluid'>
		<div class='span4'>
			<#if (content.originalImageUrl)?exists>
				<#assign originalImageUrl = content.originalImageUrl />
				<#else>
				<#assign originalImageUrl = "/poresources/logo/product_demo_large.png" />
			</#if>
			<img id="imgTitleImage" src="${StringUtil.wrapString(originalImageUrl?if_exists)}" width="388" height="170"/>
			<input type="file" name="titleImage" id="txtTitleImage" style="visibility:hidden;" accept="image/*"/>
		</div>
		<div class='span8'>
			<div class='row-fluid'>
				<div class='span2'>
					<label class='text-right'><b>${StringUtil.wrapString(uiLabelMap.BSTitle)}:</b></label>
				</div>
				<div class='span10'>
					<input name="contentName" class="contentName margin-bottom10" value="${StringUtil.wrapString((content.contentName)?if_exists)}"/>
				</div>
			</div>
			<div class='row-fluid'>
				<div class='span2'>
					<label class='text-right'><b>Permalink:</b></label>
				</div>
				<div class='span10'>
					<input type='text' id="txtPermalink" class="info-input no-margin"/>
				</div>
			</div>
			<div class='row-fluid'>
				<div class='span2'>
					<label class='text-right'><b>${StringUtil.wrapString(uiLabelMap.BSDescription)}:</b></label>
				</div>
				<div class='span10'>
					<textarea id="description" name="description">${StringUtil.wrapString((content.description)?if_exists)}</textarea>
				</div>
			</div>
		</div>
	</div>
	
	<textarea id="editor" name="editor">${StringUtil.wrapString((content.longDescription)?if_exists)}</textarea>

	<div class='row-fluid margin-top10'>
		<div class='span12'>
			<div class="widget-box">
				<div class="widget-header widget-header-blue widget-header-flat">
					<h4 class="smaller">Yoast SEO</h4>
					<div class="widget-toolbar">
						<a href="#yoastSeo" role="button" data-toggle="collapse">
							<i class="fa-chevron-down"></i>
						</a>
					</div>
				</div>
				<div class="widget-body no-padding-top collapse" id="yoastSeo">
					<div class="widget-main">
						<div class="zone info-zone" id="seoContainer">

							<div class='row-fluid margin-bottom10'>
								<div class='span2'>
									<label class='text-right seoTitle'>Snippet Preview:</label>
								</div>
								<div class='span10'>
									<label id='previewTitle'>Snippet Preview</label>
									<label id='previewLink'>Snippet Preview</label>
								</div>
							</div>

							<div class='row-fluid margin-bottom10'>
								<div class='span2'>
									<label class='text-right seoTitle'>Focus Keyword:</label>
								</div>
								<div class='span10'>
									<input name="focusKeyword" class="seoControl"/>
								</div>
							</div>

							<div class='row-fluid margin-bottom10'>
								<div class='span2'>
									<label class='text-right seoTitle'>SEO Title:</label>
								</div>
								<div class='span10'>
									<input name="SEOTitle" class="seoControl"/>
									<div><b class="red">Warning:</b>&nbsp;Title display in Google is limited to a fixed width, yours is too long.</div>
								</div>
							</div>

							<div class='row-fluid margin-bottom10'>
								<div class='span2'>
									<label class='text-right seoTitle'>Meta description:</label>
								</div>
								<div class='span10'>
									<textarea name="metaDescription" class="seoControl"></textarea>
									<div>The <p style="background-color: #ccc;display: inline;"> meta</p> description will be limited to 156 chars, chars left.</div>
								</div>
							</div>

							<div class='row-fluid margin-bottom10'>
								<div class='span2'>
									<label class='text-right seoTitle'>Meta keywords:</label>
								</div>
								<div class='span10'>
									<input name="metaKeywords" class="seoControl"/>
									<div>If you type something above it will override your <a style="display: inline;cursor: pointer;">meta keywords template</a>.</div>
								</div>
							</div>

						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</form>
</div>
</div>

<script>
	$("#ProductContent").submit(function( event ) {
		$("#sidebar-collapse").click();
		event.preventDefault();
	});
	var productId = "${parameters.productId?if_exists}";
	var contentId = "${parameters.contentId?if_exists}";

	<#if parameters.contentId?exists>
	$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.BSEditContent)}");
		<#else>
		<#if parameters.type == "PRODUCT">
			$($(".breadcrumb").children()[2]).append("<div class='green inline'>&nbsp;[${(product.productName)?if_exists}]</div>");
			<#else>
			<#if parameters.type == "TOPIC">
			$($(".breadcrumb").children()[2]).append("<div class='green inline'>&nbsp;[${(contentType.description)?if_exists}]</div>");
			</#if>
		</#if>
	</#if>
</script>