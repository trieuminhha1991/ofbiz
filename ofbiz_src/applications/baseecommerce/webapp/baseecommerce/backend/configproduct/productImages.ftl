<script type="text/javascript" src="/ecommerceresources/js/backend/configproduct/productImages.js"></script>

<div class="product-config-image">
<div class="span12 no-left-margin boder-all-profile">
<span class="text-header">${uiLabelMap.BSProductImages}</span>
	<div class="row-fluid margin-top10">
		<div class="span1"></div>
		<div class="span5">
			<div class="img-product-large">
				<img id="largeImage" src="/poresources/logo/product_demo.png"/>
			</div>
			<input type="file" id="txtLargeImage" style="visibility:hidden;" accept="image/*"/>
		</div>
		<div class="span1"></div>
		<div class="span3">
			<div class="img-product-small">
				<img id="smallImage" src="/poresources/logo/product_demo.png"/>
			</div>
			<input type="file" id="txtSmallImage" style="visibility:hidden;" accept="image/*"/>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span1"></div>
		<div class="span5 border-top"><label style="text-align: center">${uiLabelMap.BSLargeImage}</label></div>
		<div class="span1"></div>
		<div class="span3 border-top"><label style="text-align: center">${uiLabelMap.BSSmallImage}</label></div>
	</div>
</div>

<div class="span12 no-left-margin boder-all-profile margin-bottom10">
<span class="text-header">${uiLabelMap.BSAdditionalImages}</span>
	<div class="row-fluid">
		<div class="span3">
			<div class="img-product-additional">
				<img id="additional1" src="/poresources/logo/product_demo.png"/>
			</div>
			<input type="file" id="txtAdditional1" style="visibility:hidden;" accept="image/*"/>
		</div>
		<div class="span3">
			<div class="img-product-additional">
				<img id="additional2" src="/poresources/logo/product_demo.png" accept="image/*"/>
			</div>
			<input type="file" id="txtAdditional2" style="visibility:hidden;" accept="image/*"/>
		</div>
		<div class="span3">
			<div class="img-product-additional">
				<img id="additional3" src="/poresources/logo/product_demo.png"/>
			</div>
			<input type="file" id="txtAdditional3" style="visibility:hidden;" accept="image/*"/>
		</div>
		<div class="span3">
			<div class="img-product-additional">
				<img id="additional4" src="/poresources/logo/product_demo.png"/>
			</div>
			<input type="file" id="txtAdditional4" style="visibility:hidden;" accept="image/*"/>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span3 border-top center">1</div>
		<div class="span3 border-top center">2</div>
		<div class="span3 border-top center">3</div>
		<div class="span3 border-top center">4</div>
	</div>
</div>

<div class="span12 no-left-margin boder-all-profile margin-bottom10 hide">
	<span class="text-header">${uiLabelMap.BSSlideImagesManage}</span>
	<div id="containerSlide"></div>
	<input type="file" id="txtSlideImage" style="visibility:hidden;" accept="image/*"/>
	<a class="pull-right" id="addImage" style="margin: 0px 2px 0px 0px;cursor: pointer;"><i class='fa fa-plus'></i>${uiLabelMap.BSAddImage}</a>
	<div id="slideGrid"></div>
</div>
</div>

<div id="jqxwindowViewImage" style="display:none;">
	<div>${uiLabelMap.BSViewImage}</div>
	<div style="overflow-x: hidden;">
		
		<img id="previewImage" src="/poresources/logo/product_demo_large.png" width="677" height="264"/>
		
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<div class="span3"><label class="text-right">${uiLabelMap.BSLink}</label></div>
				<div class="span9"><input type="text" id="txtUrl"/></div>
			</div>
		</div>
		
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;">
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id='btnCancel' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
				<button id='btnSaveImage' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>


<div id="jqxNotificationNestedSlide">
	<div id="notificationContentNestedSlide">
	</div>
</div>

<div id='contextMenuSlide' style="display:none;">
	<ul>
		<li id='viewImage'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.BSViewImage}</li>
		<li id='activateSlide'><i class="fa-frown-o"></i>&nbsp;&nbsp;${uiLabelMap.DmsDeactivate}</li>
	</ul>
</div>

<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "CONTENT_STATUS"), null, null, null, false) />

<script>
var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
	'${item.statusId?if_exists}': '${StringUtil.wrapString(item.get("description", locale)?if_exists)}',
</#list></#if>};
</script>