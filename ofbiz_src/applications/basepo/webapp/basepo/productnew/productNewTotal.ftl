<#--<div class="row-fluid">
	<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
	    <ul class="wizard-steps wizard-steps-square">
	        <li data-target="#productinfo" class="active">
	            <span class="step">1. ${uiLabelMap.GeneralInformation}</span>
	        </li>
	        <li data-target="#productprice">
	            <span class="step">2. ${uiLabelMap.BSProductPrice}</span>
	        </li>
		</ul>
	</div>
	<div class="step-content row-fluid position-relative" id="step-container">
		<div class="step-pane active" id="generalInfo">
			
		</div>
	</div>
	<div class="row-fluid wizard-actions">
		<button class="btn btn-small btn-prev" id="btnPrevWizard">
			<i class="icon-arrow-left"></i>
			${uiLabelMap.BSPrev}
		</button>
		<button class="btn btn-small btn-success btn-next" id="btnNextWizard" data-last="${uiLabelMap.BSFinish}">
			${uiLabelMap.BSNext}
			<i class="icon-arrow-right icon-on-right"></i>
		</button>
	</div>
</div>
-->
<script type="text/javascript">
	Loading.show();
</script>

<#assign updateMode = false/>
<#if product?exists>
	<#assign updateMode = true/>
</#if>
<#if !copyMode?exists><#assign copyMode = false/></#if>

<div id="container" style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
</div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent"></div>
</div>

<div class="row-fluid">
	<div style="min-height: 460px;">
		<div id="screen1">
			<#include "productNewInfo.ftl"/>
			
			<hr class="small-margin"/>
			
			<div class="row-fluid margin-top10">
				<div class="span4">
					<#include "productNewImage.ftl"/>
				</div>
				<div class="span8">
					<#include "productNewSupplier.ftl"/>
				</div>
			</div>
		</div><!--#screen1-->
		<div id="screen2" style="display:none">
			<#include "productNewInfoMore.ftl"/>
		</div><!--#screen2-->
	</div>
	<div class="pull-left">
		<a href="javascript:void(0);" id="alterShowMore"><i class='fa-angle-down'></i>${uiLabelMap.BSOtherSetting}</a>
		<#--style="<#if updateMode && product.productTypeId == "AGGREGATED"><#else>display:none</#if>"-->
	</div>
	<div class="pull-right">
		<button id="alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		<button id="alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BSResetEdit}</button>
	</div>
</div>

<div class="container_loader">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpopover.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.extend.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript">
	$(function(){
		OlbProductNewTotal.init();
		
		Loading.hide();
	});
	
	var OlbProductNewTotal = (function(){
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.notification.create($("#container"), $("#jqxNotification"), null, {width: 'auto', autoClose: true});
		};
		var initEvent = function(){
			<#--
			$('#fuelux-wizard').ace_wizard().on('change' , function(e, info) {
		        if (info.step == 1 && (info.direction == "next")) {
		        	
		        }
		    }).on('finished', function(e) {
		    	
		    }).on('stepclick', function(e){
				//return false;//prevent clicking on steps
			});
			-->
			$("#alterSave").on("click", function(){
				if (!OlbProductNewInfo.getValidator().validate()) {
					if ($("#alterShowMore").hasClass("active")) {
						openScreen1();
						OlbProductNewInfo.getValidator().validate()
					}
					return false;
				};
				if (!OlbProductInfoMoreNew.getValidator().validate()) {
					if (!$("#alterShowMore").hasClass("active")) {
						openScreen2();
						OlbProductInfoMoreNew.getValidator().validate()
					}
					return false;
				};
				jOlbUtil.confirm.dialog("<#if !updateMode || copyMode>${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToCreate)}<#else>${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToUpdate)}</#if>?", 
					function(){
						createProduct();
					}
				);
			});
			$("#alterCancel").on("click", function(){
				jOlbUtil.confirm.dialog("${StringUtil.wrapString(uiLabelMap.BSThisActionWillClearAllTypingDataAreYouSure)}", 
					function(){
						location.reload();
					}
				);
			});
			$("#alterShowMore").on("click", function(){
				if ($("#alterShowMore").hasClass("active")) {
					// go to screen 1
					if (!OlbProductInfoMoreNew.getValidator().validate()) {
						return false;
					};
					openScreen1();
				} else {
					// go to screen 2
					if (!OlbProductNewInfo.getValidator().validate()) {
						return false;
					};
					openScreen2();
				}
			});
		};
		var openScreen1 = function(){
			$("#alterShowMore").removeClass("active");
			$("#screen1").show();
			$("#screen2").hide();
			
			$("#alterShowMore").html("<i class='fa-angle-down'></i>${uiLabelMap.BSOtherSetting}");
		};
		var openScreen2 = function(){
			$("#alterShowMore").addClass("active");
			$("#screen1").hide();
			$("#screen2").show();
			OlbProductInfoMoreNew.updateInfoScreen2();
			
			$("#alterShowMore").html("<i class='fa-angle-up'></i>${uiLabelMap.BSReturnBack}");
		};
		var createProduct = function(){
			$("#alterSave").addClass("disabled");
			$("#alterCancel").addClass("disabled");
			
			var dataMap = {};
			// product info
			var infoDataMap = OlbProductNewInfo.getValue();
			dataMap = _.extend(dataMap, infoDataMap);
			
			// product image
			var images = OlbProductNewImage.getValue();
			dataMap = _.extend(dataMap, images);
			
			// supplier product
			dataMap.supplierProduct = OlbSupplierProductNew.getValue();
			
			// product info more
			var infoMoreDataMap = OlbProductInfoMoreNew.getValue();
			dataMap = _.extend(dataMap, infoMoreDataMap);
			
			var form_data= new FormData();
			if (OlbCore.isNotEmpty(dataMap.productTypeId)) form_data.append("productTypeId", dataMap.productTypeId);
			if (OlbCore.isNotEmpty(dataMap.isVirtual)) form_data.append("isVirtual", dataMap.isVirtual);
			if (OlbCore.isNotEmpty(dataMap.isVariant)) form_data.append("isVariant", dataMap.isVariant);
			if (OlbCore.isNotEmpty(dataMap.parentProductId)) form_data.append("parentProductId", dataMap.parentProductId);
			if (OlbCore.isNotEmpty(dataMap.productCode)) form_data.append("productCode", dataMap.productCode);
			if (typeof(dataMap.primaryProductCategoryId) != "undefined") form_data.append("primaryProductCategoryId", dataMap.primaryProductCategoryId);
			if (typeof(dataMap.taxProductCategoryId) != "undefined") form_data.append("taxProductCategoryId", dataMap.taxProductCategoryId);
			if (OlbCore.isNotEmpty(dataMap.internalName)) form_data.append("internalName", dataMap.internalName);
			if (OlbCore.isNotEmpty(dataMap.barcode)) form_data.append("barcode", dataMap.barcode);
			if (OlbCore.isNotEmpty(dataMap.brandName)) form_data.append("brandName", dataMap.brandName);
			if (OlbCore.isNotEmpty(dataMap.productName)) form_data.append("productName", dataMap.productName);
			if (OlbCore.isNotEmpty(dataMap.longDescription)) form_data.append("longDescription", dataMap.longDescription);
			if (OlbCore.isNotEmpty(dataMap.productCategoryIds)) {
				var lengthList = dataMap.productCategoryIds.length;
				for (var i = 0; i < lengthList; i++) {
					form_data.append("productCategoryIds", dataMap.productCategoryIds[i]);
				}
			}
			if (OlbCore.isNotEmpty(dataMap.quantityUomId)) form_data.append("quantityUomId", dataMap.quantityUomId);
			if (typeof(dataMap.salesUomId) != "undefined") form_data.append("salesUomId", dataMap.salesUomId);
			if (typeof(dataMap.purchaseUomId) != "undefined") form_data.append("purchaseUomId", dataMap.purchaseUomId);
			if (OlbCore.isNotEmpty(dataMap.weightUomId)) form_data.append("weightUomId", dataMap.weightUomId);
			if (OlbCore.isNotEmpty(dataMap.weight)) form_data.append("weight", ""+dataMap.weight);
			if (OlbCore.isNotEmpty(dataMap.productWeight)) form_data.append("productWeight", ""+dataMap.productWeight);
			if (OlbCore.isNotEmpty(dataMap.currencyUomId)) form_data.append("currencyUomId", dataMap.currencyUomId);
			if (OlbCore.isNotEmpty(dataMap.productDefaultPrice)) form_data.append("productDefaultPrice", dataMap.productDefaultPrice);
			if (OlbCore.isNotEmpty(dataMap.productListPrice)) form_data.append("productListPrice", dataMap.productListPrice);
			if (OlbCore.isNotEmpty(dataMap.isPriceIncludedVat)) form_data.append("isPriceIncludedVat", dataMap.isPriceIncludedVat);
			<#if updateMode && !copyMode>form_data.append("productId", "${product.productId}");</#if>
			
			if (OlbCore.isNotEmpty(dataMap.featureTypeIds)) {
				var lengthList = dataMap.featureTypeIds.length;
				for (var i = 0; i < lengthList; i++) {
					form_data.append("featureTypeIds", dataMap.featureTypeIds[i]);
				}
			}
			if (OlbCore.isNotEmpty(dataMap.featureIds)) {
				var lengthList = dataMap.featureIds.length;
				for (var i = 0; i < lengthList; i++) {
					form_data.append("featureIds", dataMap.featureIds[i]);
				}
			}
			
			if (typeof(dataMap.largeImageUrl) != "undefined") form_data.append("largeImageUrl", dataMap.largeImageUrl);
			if (typeof(dataMap.smallImageUrl) != "undefined") form_data.append("smallImageUrl", dataMap.smallImageUrl);
			if (typeof(dataMap.additionalImage1Url) != "undefined") form_data.append("additionalImage1Url", dataMap.additionalImage1Url);
			if (typeof(dataMap.additionalImage2Url) != "undefined") form_data.append("additionalImage2Url", dataMap.additionalImage2Url);
			if (typeof(dataMap.additionalImage3Url) != "undefined") form_data.append("additionalImage3Url", dataMap.additionalImage3Url);
			if (typeof(dataMap.additionalImage4Url) != "undefined") form_data.append("additionalImage4Url", dataMap.additionalImage4Url);
			if (typeof(dataMap.largeImage) != "undefined") form_data.append("largeImage", dataMap.largeImage);
			if (typeof(dataMap.smallImage) != "undefined") form_data.append("smallImage", dataMap.smallImage);
			if (typeof(dataMap.additionalImage1) != "undefined") form_data.append("additionalImage1", dataMap.additionalImage1);
			if (typeof(dataMap.additionalImage2) != "undefined") form_data.append("additionalImage2", dataMap.additionalImage2);
			if (typeof(dataMap.additionalImage3) != "undefined") form_data.append("additionalImage3", dataMap.additionalImage3);
			if (typeof(dataMap.additionalImage4) != "undefined") form_data.append("additionalImage4", dataMap.additionalImage4);
			if (OlbCore.isNotEmpty(dataMap.supplierProduct)) form_data.append("supplierProduct", dataMap.supplierProduct);
			if (OlbCore.isNotEmpty(dataMap.alterUomData)) form_data.append("alterUomData", dataMap.alterUomData);
			if (OlbCore.isNotEmpty(dataMap.productConfigItem)) form_data.append("productConfigItem", dataMap.productConfigItem);
			if (OlbCore.isNotEmpty(dataMap.salesDiscontinuationDate)) form_data.append("salesDiscontinuationDate", dataMap.salesDiscontinuationDate);
			if (OlbCore.isNotEmpty(dataMap.purchaseDiscontinuationDate)) form_data.append("purchaseDiscontinuationDate", dataMap.purchaseDiscontinuationDate);
			if (typeof(dataMap.amountUomTypeId) != "undefined") form_data.append("amountUomTypeId", dataMap.amountUomTypeId);
			if (typeof(dataMap.idPLUCode) != "undefined") form_data.append("idPLUCode", dataMap.idPLUCode);
			
			$.ajax({
				type: 'POST',
				url: '<#if !updateMode || copyMode>createProductAdvance<#else>updateProductAdvance</#if>',
				cache : false,
				contentType : false,
				processData : false,
				data: form_data,
				beforeSend: function(){
					$("#loader_page_common").show();
				},
				success: function(data){
					jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
					        	$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'error'});
					        	$("#jqxNotification").html(errorMessage);
					        	$("#jqxNotification").jqxNotification("open");
					        	
					        	$("#alterSave").removeClass("disabled");
								$("#alterCancel").removeClass("disabled");
								
					        	return false;
							}, function(){
								$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html("<#if updateMode>${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}<#else>${StringUtil.wrapString(uiLabelMap.wgcreatesuccess)}</#if>");
					        	$("#jqxNotification").jqxNotification("open");
					        	if (data.productId != undefined && data.productId != null) {
					        		window.location.href = "viewProduct?productId=" + data.productId;
					        	}
							}
					);
				},
				error: function(data){
					alert("Send request is error");
					$("#alterSave").removeClass("disabled");
					$("#alterCancel").removeClass("disabled");
				},
				complete: function(data){
					$("#loader_page_common").hide();
				},
			});
		};
		return {
			init: init,
		};
	}());
</script>