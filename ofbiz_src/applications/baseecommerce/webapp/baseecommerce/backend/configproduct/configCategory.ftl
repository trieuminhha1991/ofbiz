<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>

<script type="text/javascript" src="/ecommerceresources/js/backend/configproduct/configCategory.js"></script>

<style>
.img-product-large {
    width: 100%;
	height: auto;
	text-align: center;
}
</style>

<div id="containerSlide"></div>
<input type="file" id="txtSlideImage" style="visibility:hidden;" accept="image/*"/>
<#if parameters.productCategoryId?exists>
	<div class="row-fluid">
		<div class="span12 no-left-margin boder-all-profile">
			<span class="text-header">${uiLabelMap.BSCategoryImage}</span>
			
			<div class="row-fluid">
				<div class="span5">
					<div class="row-fluid margin-top10">
						<div class="span11 center">
							<div class="img-product-large">
								<img id="largeImage" src="/poresources/logo/product_demo_vertical.png"/>
							</div>
							<input type="file" id="txtLargeImage" style="visibility:hidden;" accept="image/*"/>
						</div>
						<div class="span1"></div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span2"><label class="text-right">${uiLabelMap.icon}</label></div>
						<div class="span5"><input type="text" id="txtIcon"/></div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span2"><label class="text-right">${uiLabelMap.BSLink}</label></div>
						<div class="span5"><input type="text" id="txtLink"/></div>
					</div>
					<div class="row-fluid">
						<div class="span10 no-left-margin">
							<button id='btnSaveConfig' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
						</div>
					</div>
				</div>
				<div class="span7 fontawesome-icon-list">
					<h3 class="header smaller lighter blue">Icons</h3>
					<div class="row-fluid margin-top10">
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-ambulance"></i> ambulance</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-video-camera"></i> video-camera</a></div>
						<div class="fa-hover span3"><a><i class="fa fa-archive"></i> archive</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-area-chart"></i> area-chart</a></div>
					</div>
					<div class="row-fluid">
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-tablet"></i> tablet</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-bar-chart-o"></i> bar-chart-o <span class="text-muted">(alias)</span></a></div>
						<div class="fa-hover span3"><a><i class="fa fa-barcode"></i> barcode</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-birthday-cake"></i> birthday-cake</a></div>
					</div>
					<div class="row-fluid">
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-plane"></i> plane</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-rocket"></i> rocket</a></div>
						<div class="fa-hover span3"><a><i class="fa fa-recycle"></i> recycle</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-space-shuttle"></i> space-shuttle</a></div>
					</div>
					<div class="row-fluid">
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-subway"></i> subway</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-taxi"></i> taxi</a></div>
						<div class="fa-hover span3"><a><i class="fa fa-train"></i> train</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-truck"></i> truck</a></div>
					</div>
					<div class="row-fluid">
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-check-circle"></i> check-circle</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-bolt"></i> bolt</a></div>
						<div class="fa-hover span3"><a><i class="fa fa-briefcase"></i> briefcase</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-child"></i> child</a></div>
					</div>
					<div class="row-fluid">
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-circle-o-notch"></i> circle-o-notch</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-clock-o"></i> clock-o</a></div>
						<div class="fa-hover span3"><a><i class="fa fa-coffee"></i> coffee</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-comment-o"></i> comment-o</a></div>
					</div>
					<div class="row-fluid">
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-comments-o"></i> comments-o</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-diamond"></i> diamond</a></div>
						<div class="fa-hover span3"><a><i class="fa fa-fire"></i> fire</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-flag"></i> flag</a></div>
					</div>
					<div class="row-fluid">
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-graduation-cap"></i> graduation-cap</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-heart"></i> heart</a></div>
						<div class="fa-hover span3"><a><i class="fa fa-recycle"></i> recycle</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-thumbs-o-up"></i> thumbs-o-up</a></div>
					</div>
					<div class="row-fluid">
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-heart-o"></i> heart-o</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-heartbeat"></i> heartbeat</a></div>
						<div class="fa-hover span3"><a><i class="fa fa-hospital-o"></i> hospital-o</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-medkit"></i> medkit</a></div>
					</div>
					<div class="row-fluid">
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-plus-square"></i> plus-square</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-stethoscope"></i> stethoscope</a></div>
						<div class="fa-hover span3"><a><i class="fa fa-user-md"></i> user-md</a></div>
						<div class="fa-hover span3 no-left-margin"><a><i class="fa fa-wheelchair"></i> wheelchair</a></div>
					</div>
					<div class="row-fluid">
						<div class="span3 pull-right view-more">
							<a class="mouse-pointer" href="https://fortawesome.github.io/Font-Awesome/icons/" target="_blank">${uiLabelMap.BSViewMore}...</a>
						</div>
					</div>
					
				</div>
			</div>
		</div>
	</div>
</#if>

<div class="row-fluid">
	<div class="span12 no-left-margin boder-all-profile">
		<span class="text-header">${uiLabelMap.BSHorizontalBanner}</span>
		<#if parameters.productCategoryId?exists>
			<a class="pull-right" id="addImage" style="margin: 0px 2px 0px 0px;cursor: pointer;"><i class='fa fa-plus'></i>${uiLabelMap.BSAddImage}</a>
		</#if>
		<div id="slideGrid"></div>
	</div>
</div>
	
<div id="jqxwindowViewImage" style="display:none;">
	<div>${uiLabelMap.BSViewImage}</div>
	<div style="overflow-x: hidden;">
		
		<div style="height: 280px;overflow: auto;" id="imgContainer">
			<img id="previewImage" src="/poresources/logo/product_demo_large.png" width="677" height="264"/>
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
	
	var productCategoryId = "${parameters.productCategoryId?if_exists}";
	var url = "JQGetConfigCategory?productCategoryId=" + productCategoryId;
	var addUrl = "addBannerCategory";
	$("#captionContainer").addClass('hidden');
	$("#imgContainer").css('height', 315);
</script>
