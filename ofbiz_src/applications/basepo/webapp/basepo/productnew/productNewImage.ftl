<style type="text/css">
	.ace-thumbnails li {
		width: 16%;
		cursor: pointer;
	}
	.ace-thumbnails li.image-thumb {
		width: 14%
	}
	.ace-thumbnails li div.text {
		position: absolute;
	    width: 100%;
	    height: 100%;
	    display: none;

	    right: 0;
	    left: 0;
	    bottom: 0;
	    top: 0;
	    text-align: center;
	    color: #FFF;
	    background-color: rgba(0,0,0,0.55);
	    -webkit-transition: all .2s ease;
	    -moz-transition: all .2s ease;
	    -o-transition: all .2s ease;
	    transition: all .2s ease;
	}
	.ace-thumbnails li div.text div.inner {
		padding: 4px 0;
	    margin: 0;
	    display: inline-block;
	    vertical-align: middle;
	    max-width: 90%;
	}
	.ace-thumbnails li .inner-cancel {
		position:absolute;
		top:-5px;
		right:-2px;
		z-index:20;
		display: none;
	}
</style>
<#assign updateMode = false/>
<#if product?exists>
	<#assign updateMode = true/>
</#if>
<#if !copyMode?exists><#assign copyMode = false/></#if>

<#assign imageDefaultUrl = "/poresources/logo/product_demo.png"/>
<div class="row-fluid">
	<div class="span12">
		<div>
			<ul class="ace-thumbnails">
				<li>
					<img id="largeImage" src="${imageDefaultUrl}"/>
					<div id="largeImageDesc" class="text">
						<div class="inner">${uiLabelMap.BSLargeImage}</div>
					</div>
					<div class="inner-cancel" onClick="OlbProductNewImage.resetImageData('largeImage', 'txtLargeImage')"><i class="fa fa-remove red"></i></div>
					<input type="file" id="txtLargeImage" style="display:none;" accept="image/*"/>
					<input type="hidden" id="txtLargeImageUrl" value=""/>
				</li>
				<li class="image-thumb">
					<img id="smallImage" src="${imageDefaultUrl}"/>
					<div id="smallImageDesc" class="text">
						<div class="inner">${uiLabelMap.BSSmallImage}</div>
					</div>
					<div class="inner-cancel" onClick="OlbProductNewImage.resetImageData('smallImage', 'txtSmallImage')"><i class="fa fa-remove red"></i></div>
					<input type="file" id="txtSmallImage" style="display:none;" accept="image/*"/>
					<input type="hidden" id="txtSmallImageUrl" value=""/>
				</li>
				<li class="image-thumb">
					<img id="additional1" src="${imageDefaultUrl}"/>
					<div id="additional1Desc" class="text">
						<div class="inner">${uiLabelMap.BSOtherImages} 1</div>
					</div>
					<div class="inner-cancel" onClick="OlbProductNewImage.resetImageData('additional1', 'txtAdditional1')"><i class="fa fa-remove red"></i></div>
					<input type="file" id="txtAdditional1" style="display:none;" accept="image/*"/>
					<input type="hidden" id="txtAdditional1Url" value=""/>
				</li>
				<li class="image-thumb">
					<img id="additional2" src="${imageDefaultUrl}" accept="image/*"/>
					<div id="additional2Desc" class="text">
						<div class="inner">${uiLabelMap.BSOtherImages} 2</div>
					</div>
					<div class="inner-cancel" onClick="OlbProductNewImage.resetImageData('additional2', 'txtAdditional2')"><i class="fa fa-remove red"></i></div>
					<input type="file" id="txtAdditional2" style="display:none;" accept="image/*"/>
					<input type="hidden" id="txtAdditional2Url" value=""/>
				</li>
				<li class="image-thumb">
					<img id="additional3" src="${imageDefaultUrl}"/>
					<div id="additional3Desc" class="text">
						<div class="inner">${uiLabelMap.BSOtherImages} 3</div>
					</div>
					<div class="inner-cancel" onClick="OlbProductNewImage.resetImageData('additional3', 'txtAdditional3')"><i class="fa fa-remove red"></i></div>
					<input type="file" id="txtAdditional3" style="display:none;" accept="image/*"/>
					<input type="hidden" id="txtAdditional3Url" value=""/>
				</li>
				<li class="image-thumb">
					<img id="additional4" src="${imageDefaultUrl}"/>
					<div id="additional4Desc" class="text">
						<div class="inner">${uiLabelMap.BSOtherImages} 4</div>
					</div>
					<div class="inner-cancel" onClick="OlbProductNewImage.resetImageData('additional4', 'txtAdditional4')"><i class="fa fa-remove red"></i></div>
					<input type="file" id="txtAdditional4" style="display:none;" accept="image/*"/>
					<input type="hidden" id="txtAdditional4Url" value=""/>
				</li>
			</ul>
		</div>
	</div>
</div>
<script type="text/javascript">
	$(function(){
		$(".ace-thumbnails li").hover(function(){
			$(this).find("div.text").show();
			$(this).find("div.inner-cancel").show();
		}, function(){
			$(this).find("div.text").hide();
			$(this).find("div.inner-cancel").hide();
		});
		OlbProductNewImage.init();
	});
	var OlbProductNewImage = (function(){
		var init = function(){
			initEvent();
			
			initUpdateMode();
		};
		var initEvent = function(){
			// 1. large image
				$("#largeImageDesc").click(function() {
					$("#txtLargeImage").click();
				});
				$("#txtLargeImage").change(function(){
					readURL(this, $('#largeImage'));
					$("#txtLargeImageUrl").val("");
				});
			// 2. small image
				$("#smallImageDesc").click(function() {
					$("#txtSmallImage").click();
				});
				$("#txtSmallImage").change(function(){
					readURL(this, $('#smallImage'));
					$("#txtSmallImageUrl").val("");
				});
			// 3. additional 01 image
				$("#additional1Desc").click(function() {
					$("#txtAdditional1").click();
				});
				$("#txtAdditional1").change(function(){
					readURL(this, $('#additional1'));
					$("#txtAdditional1Url").val("");
				});
			// 4. additional 02 image
				$("#additional2Desc").click(function() {
					$("#txtAdditional2").click();
				});
				$("#txtAdditional2").change(function(){
					readURL(this, $('#additional2'));
					$("#txtAdditional2Url").val("");
				});
			// 5. additional 03 image
				$("#additional3Desc").click(function() {
					$("#txtAdditional3").click();
				});
				$("#txtAdditional3").change(function(){
					readURL(this, $('#additional3'));
					$("#txtAdditional3Url").val("");
				});
			// 6. additional 04 image
				$("#additional4Desc").click(function() {
					$("#txtAdditional4").click();
				});
				$("#txtAdditional4").change(function(){
					readURL(this, $('#additional4'));
					$("#txtAdditional4Url").val("");
				});
		};
		var readURL = function(input, img) {
			if (input.files && input.files[0]) {
		        var reader = new FileReader();
		        reader.onload = function (e) {
		            img.attr('src', e.target.result);
		        }
		        reader.readAsDataURL(input.files[0]);
		    }
		};
		var getValue = function() {
			var value = new Object();
			value.largeImageUrl = $("#txtLargeImageUrl").val();
			value.smallImageUrl = $("#txtSmallImageUrl").val();
			value.additionalImage1Url = $("#txtAdditional1Url").val();
			value.additionalImage2Url = $("#txtAdditional2Url").val();
			value.additionalImage3Url = $("#txtAdditional3Url").val();
			value.additionalImage4Url = $("#txtAdditional4Url").val();
			
			if ($('#txtLargeImage').prop('files')[0]) {
				value.largeImage = $('#txtLargeImage').prop('files')[0];
			}
			if ($('#txtSmallImage').prop('files')[0]) {
				value.smallImage = $('#txtSmallImage').prop('files')[0];
			}
			if ($('#txtAdditional1').prop('files')[0]) {
				value.additionalImage1 = $('#txtAdditional1').prop('files')[0];
			}
			if ($('#txtAdditional2').prop('files')[0]) {
				value.additionalImage2 = $('#txtAdditional2').prop('files')[0];
			}
			if ($('#txtAdditional3').prop('files')[0]) {
				value.additionalImage3 = $('#txtAdditional3').prop('files')[0];
			}
			if ($('#txtAdditional4').prop('files')[0]) {
				value.additionalImage4 = $('#txtAdditional4').prop('files')[0];
			}
			//var value = _.extend(value, additionalImages);
			return value;
		};
		var resetImageData = function(imgId, inputId){
			$("#" + imgId).attr('src', '${imageDefaultUrl}');
			$("#" + inputId).val("");
			$("#" + inputId + "Url").val("");
		};
		var initUpdateMode = function(){
			<#if updateMode && contentProductMap?has_content>
				<#if contentProductMap.largeImageUrl?exists>
					$('#largeImage').attr('src', encodeURI("${StringUtil.wrapString(contentProductMap.largeImageUrl)}"));
					$('#txtLargeImageUrl').val(encodeURI("${StringUtil.wrapString(contentProductMap.largeImageUrl)}"));
				</#if>
				<#if contentProductMap.smallImageUrl?exists>
					$('#smallImage').attr('src', encodeURI("${StringUtil.wrapString(contentProductMap.smallImageUrl)}"));
					$('#txtSmallImageUrl').val(encodeURI("${StringUtil.wrapString(contentProductMap.smallImageUrl)}"));
				</#if>
				<#if contentProductMap.ADDITIONAL_IMAGE_1?exists>
					$('#additional1').attr('src', encodeURI("${StringUtil.wrapString(contentProductMap.ADDITIONAL_IMAGE_1)}"));
					$('#txtAdditional1Url').val(encodeURI("${StringUtil.wrapString(contentProductMap.ADDITIONAL_IMAGE_1)}"));
				</#if>
				<#if contentProductMap.ADDITIONAL_IMAGE_2?exists>
					$('#additional2').attr('src', encodeURI("${StringUtil.wrapString(contentProductMap.ADDITIONAL_IMAGE_2)}"));
					$('#txtAdditional2Url').val(encodeURI("${StringUtil.wrapString(contentProductMap.ADDITIONAL_IMAGE_2)}"));
				</#if>
				<#if contentProductMap.ADDITIONAL_IMAGE_3?exists>
					$('#additional3').attr('src', encodeURI("${StringUtil.wrapString(contentProductMap.ADDITIONAL_IMAGE_3)}"));
					$('#txtAdditional3Url').val(encodeURI("${StringUtil.wrapString(contentProductMap.ADDITIONAL_IMAGE_3)}"));
				</#if>
				<#if contentProductMap.ADDITIONAL_IMAGE_4?exists>
					$('#additional4').attr('src', encodeURI("${StringUtil.wrapString(contentProductMap.ADDITIONAL_IMAGE_4)}"));
					$('#txtAdditional4Url').val(encodeURI("${StringUtil.wrapString(contentProductMap.ADDITIONAL_IMAGE_4)}"));
				</#if>
			</#if>
		};
		return {
			init: init,
			getValue: getValue,
			resetImageData: resetImageData,
		};
	}());
</script>