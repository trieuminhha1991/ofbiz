<script type="text/javascript" src="/ecommerceresources/js/backend/configwebsite/imageManager.js"></script>

<div id="containerSlide"></div>
<input type="file" id="txtSlideImage" style="visibility:hidden;" accept="image/*"/>
<a class="pull-right" id="addImage" style="margin: 0px 2px 0px 0px;cursor: pointer;"><i class='fa fa-plus'></i>${uiLabelMap.BSAddImage}</a>
<div id="slideGrid"></div>
	
<div id="jqxwindowViewImage" style="display:none;">
	<div>${uiLabelMap.BSViewImage}</div>
	<div style="overflow-x: hidden;">
		
		<div style="height: 280px;overflow: auto;" id="imgContainer">
			<img id="previewImage" src="/poresources/logo/product_demo_large.png"/>
		</div>
	
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;">
		<div class="row-fluid margin-top10">
			<div class="span1 captionContainer"><label class="text-right">${uiLabelMap.BSLink}</label></div>
			<div class="span6 captionContainer"><input type="text" id="txtUrl"/></div>
			<div class="span5">
				<button id='btnCancel' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
				<button id='btnSaveImage' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<div id="jqxNotificationNestedSlide">
	<div id="notificationContentNestedSlide"></div>
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