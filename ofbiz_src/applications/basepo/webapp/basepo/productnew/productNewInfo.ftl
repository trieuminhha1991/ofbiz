<#assign listTaxCategory = delegator.findByAnd("ProductCategory", {"productCategoryTypeId", "TAX_CATEGORY"}, null, false)!/>
<#assign listWeightUom = delegator.findByAnd("Uom", {"uomTypeId", "WEIGHT_MEASURE"}, null, false)!/>
<#--<#assign listQuantityUom = delegator.findByAnd("Uom", {"uomTypeId", "PRODUCT_PACKING"}, null, false)!/>-->
<#assign listCurrencyUom = delegator.findByAnd("Uom", {"uomTypeId", "CURRENCY_MEASURE"}, null, false)!/>
<#assign currentCurrencyUomId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCurrencyUom(delegator)!/>
<#assign listProductFeatureType = delegator.findByAnd("ProductFeatureType", null, null, false)!/>
<#assign quantityUomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, true)!/>
<style type="text/css">
	#horizontalScrollBarjqxGridAlterUom {
		visibility: hidden !important;
	}
	#jqxGridAlterUom .jqx-grid-column-header, #jqxgridSupplierProduct .jqx-grid-column-header{
		background-color: #438EB9;
    	color: #FFF;
	}
</style>
<script type="text/javascript">
	var taxCategoryData = [
		<#if listTaxCategory?has_content>
			<#list listTaxCategory as item>
			{	productCategoryId: "${item.productCategoryId}",
				categoryName: "${StringUtil.wrapString(item.categoryName?if_exists)}"
			},
			</#list>
		</#if>
	];
	var productTypeId = "FINISHED_GOOD";
	<#if productTypeId?exists>
	    productTypeId = "${productTypeId}";
	</#if>

	var weightUomData = [
		<#if listWeightUom?has_content>
			<#list listWeightUom as item>
			{	uomId: "${item.uomId}",
				abbreviation: "${item.abbreviation?if_exists}",
				description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
			},
			</#list>
		</#if>
	];
	var quantityUomData = [
		<#if quantityUomList?has_content>
			<#list quantityUomList as item>
			{	uomId: "${item.uomId}",
				abbreviation: "${item.abbreviation?if_exists}",
				description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
			},
			</#list>
		</#if>
	];
	var currencyUomDataInfo = [
		<#if listCurrencyUom?has_content>
			<#list listCurrencyUom as item>
			{	uomId: "${item.uomId}",
				abbreviation: "${item.abbreviation?if_exists}",
				description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
			},
			</#list>
		</#if>
	];
	var featureTypeData = [
		<#if listProductFeatureType?has_content>
			<#list listProductFeatureType as item>
			{	productFeatureTypeId: "${item.productFeatureTypeId}",
				description: "${StringUtil.wrapString(item.description?if_exists)}"
			},
			</#list>
		</#if>
	];
</script>
<#assign updateMode = false/>
<#if product?exists>
	<#assign updateMode = true/>
</#if>
<#if !copyMode?exists><#assign copyMode = false/></#if>

<#assign priceDecimalDigits = 2>
<div id="form-product-info" class="row-fluid">
	<div class="span12">
		<div class="row-fluid form-horizontal form-window-content-custom content-align-left">
			<div class="span4">
				<div class="row-fluid" id="divProductVirtualType">
					<div class="span4">
						<label class="required">${uiLabelMap.BSProductType}</label>
					</div>
					<div class="span8">
						<div id="productVirtualTypeId"></div>
					</div>
				</div>
				<div class="row-fluid virtual-group" style="display:none">
					<div class="span4">
						<label>${uiLabelMap.BSFeatureType}</label>
					</div>
					<div class="span8">
						<div id="featureTypeId">
							<div id="featureTypeGrid"></div>
						</div>
					</div>
				</div>
				<div class="row-fluid variant-group" style="display:none">
					<div class="span4">
						<label class="required">${uiLabelMap.BSAbbProductVirtual}</label>
					</div>
					<div class="span8">
						<div id="parentProductId">
							<div id="parentProductGrid"></div>
						</div>
					</div>
				</div>
				<div id="variant-feature-container" class="row-fluid variant-group" style="display:none">
				</div>
				<div class="row-fluid">
					<label>${uiLabelMap.BSDescription}&nbsp;&nbsp;&nbsp;<i id="btnEditDescription" class="fa fa-pencil blue"></i></label>
					<div id="descriptionTxt"><#if updateMode>${StringUtil.wrapString(product.longDescription?default(""))}</#if></div>
				</div>
				<div class="row-fluid">
					<div>
						<label>${uiLabelMap.BSAlternativeUom}&nbsp;&nbsp;&nbsp;<i id="btnAddAlterUom" class="fa fa-pencil blue"></i></label>
					</div>
					<div id="containerGridAlterUom" style="visibility: hidden">
						<div id="jqxGridAlterUom"></div>
					</div>
				</div>
			</div><!--.span4-->
			<div class="span4">
				<div class="row-fluid">
					<div class="span4">
						<label>${uiLabelMap.BSProductId}</label>
					</div>
					<div class="span8">
						<input class="span12" type="text" id="productCode" value=""/>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label class="required">${uiLabelMap.BSProductName}</label>
					</div>
					<div class="span8">
						<input class="span12" type="text" id="productName" value=""/>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label>${uiLabelMap.BSAbbreviateName}</label>
					</div>
					<div class="span8">
						<input class="span12" type="text" id="internalName" value=""/>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<#--<label>${uiLabelMap.BSInternalCallName}</label>-->
						<label>${uiLabelMap.BSBarcode}</label>
					</div>
					<div class="span8">
						<#--<input class="span12" type="hidden" id="internalName" value=""/>-->
						<input class="span12" type="text" id="barcode" value=""/>
					</div>
				</div>
				<hr class="small-margin"/>
				<div class="row-fluid">
					<div class="span4">
						<label>${uiLabelMap.ProductBrandName}</label>
					</div>
					<div class="span8">
						<div id="brandName"></div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label>${uiLabelMap.BSPrimaryCategory}</label>
					</div>
					<div class="span8">
						<div id="primaryProductCategoryId"></div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label>${uiLabelMap.BSOtherProductCategory}</label>
					</div>
					<div class="span8">
						<div id="productCategoryIds"></div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label <#if (productTypeId?exists && "PRD_TAX_INV_MANL" == productTypeId)>class="required" </#if>>${uiLabelMap.BSTaxProductCategory}</label>
					</div>
					<div class="span8">
						<div id="taxProductCategoryId"></div>
					</div>
				</div>
			</div><!--.span4-->
			<div class="span4">
				<div class="row-fluid">
					<div class="span4">
						<label>${uiLabelMap.BSAbbProductWeight}</label>
					</div>
					<div class="span8">
						<div id="productWeight"></div>
						<#--
						<div class="container-add-plus">
							<a tabindex="-1" href="javascript:void(0);" class="clear-value" onclick="OlbProductNewInfo.clearNumberInput('#productWeight');"><i class="fa fa-eraser"></i></a>
			   			</div>
						-->
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label>${uiLabelMap.BSAbbWeightAfterPacked}</label>
					</div>
					<div class="span8">
						<div id="weight"></div>
						<#--
						<div class="container-add-plus">
							<a tabindex="-1" href="javascript:void(0);" class="clear-value" onclick="OlbProductNewInfo.clearNumberInput('#weight');"><i class="fa fa-eraser"></i></a>
			   			</div>
						-->
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label>${uiLabelMap.BSAbbWeightUom}</label>
					</div>
					<div class="span8">
						<div id="weightUomId"></div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label></label>
					</div>
					<div class="span8">
						<div></div>
					</div>
				</div>
				<hr class="small-margin"/>
				<div class="row-fluid">
					<div class="span4">
						<label class="required">${uiLabelMap.BSUnitUom}</label>
					</div>
					<div class="span8">
						<div class="container-add-plus">
							<div id="quantityUomId"></div>
							<a id="quickAddNewUom" tabindex="-1" href="javascript:void(0);" class="add-value"><i class="fa fa-plus"></i></a>
							<a id="extendOtherUom" tabindex="-1" href="javascript:void(0);" class="add-value"><i class="fa fa-caret-square-o-down" aria-hidden="true"></i></a>
							<div id="popoverOtherUom" style="display:none">
								<div class="row-fluid">
									<div class="span5">
										<label>${uiLabelMap.BSSalesUomId}</label>
									</div>
									<div class="span7">
										<div id="salesUomId"></div>
									</div>
								</div>
								<div class="row-fluid">
									<div class="span5">
										<label>${uiLabelMap.BSPurchaseUomId}</label>
									</div>
									<div class="span7">
										<div id="purchaseUomId"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label class="container-price required">${uiLabelMap.BSListPrice} S</label>
					</div>
					<div class="span8">
						<div id="productListPrice"></div>
						<#--
						<div class="container-add-plus">
							<a tabindex="-1" href="javascript:void(0);" class="clear-value" onclick="OlbProductNewInfo.clearNumberInput('#productListPrice');"><i class="fa fa-eraser"></i></a>
			   			</div>
						-->
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label class="container-price required">${uiLabelMap.BSDefaultPrice} S</label>
					</div>
					<div class="span8">
						<div id="productDefaultPrice"></div>
						<#--
						<div class="container-add-plus">
							<a tabindex="-1" href="javascript:void(0);" class="clear-value" onclick="OlbProductNewInfo.clearNumberInput('#productDefaultPrice');"><i class="fa fa-eraser"></i></a>
			   			</div>
						-->
					</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label>${uiLabelMap.BSCurrencyUomId}</label>
					</div>
					<div class="span8">
						<div id="currencyUomId"></div>
					</div>
				</div>
			</div><!--.span4-->
		</div>
	</div>
</div>
<div id="alterpopupWindowDescriptionEdit" style="display:none">
	<div>${uiLabelMap.BSEditDescription}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span12'>
							<textarea id="we_description"></textarea>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="we_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="we_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<#include "productNewInfoAlterUom.ftl"/>
<#include "../productuom/productUomNewPopup.ftl"/>
<#assign permitUpdatePurUom = false>
<#if hasOlbPermission("MODULE", "PRODUCTPO_EDIT_PURUOM", "")><#assign permitUpdatePurUom = true></#if>

<script type="text/javascript">
	$(function(){
		OlbProductNewInfo.init();
		
		setTimeout(function(){
			$("#productCode").focus();
		}, 100);
	});
	var OlbProductNewInfo = (function(){
		var productVirtualTypeDDL;
		var parentProductDDB;
		var primaryProductCategoryCBB;
		var productCategoriesCCB;
		var brandNameCBB;
		var taxProductCategoryDDL;
		var weightUomDDL;
		var quantityUomDDL;
		var currencyUomCBB;
		var validatorVAL;
		var featureTypeIdsCBB;
		var salesUomDDL;
		var purchaseUomDDL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initPopover();
			initEvent();
			initValidateForm();
			
			initUpdateMode();
		};
		var initPopover = function(){
			$("#popoverOtherUom").jqxPopover({width: 300, isModal: true, offset: {left: -90, top:0}, arrowOffsetValue: 90, title: "${uiLabelMap.BSOtherUom}", showCloseButton: true, selector: $("#extendOtherUom")});
			var uomAvailableData = [
				{uomId: "", description: "---"},
			];
			var configOtherUom = {
				width:'100%',
				height: 25,
				key: "uomId",
	    		value: "description",
	    		displayDetail: false,
				selectedIndex: 0,
				dropDownWidth: 'auto',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				autoDropDownHeight: true,
			};
			salesUomDDL = new OlbDropDownList($("#salesUomId"), uomAvailableData, configOtherUom, []);
			purchaseUomDDL = new OlbDropDownList($("#purchaseUomId"), uomAvailableData, $.extend({"disabled": <#if permitUpdatePurUom>false<#else>true</#if>}, configOtherUom), null);
			
			$("#popoverOtherUom").on("close", function(){
				var iSalesUomIdSelected = salesUomDDL.getValue();
				var iPurchaseUomIdSelected = purchaseUomDDL.getValue();
				if (OlbCore.isNotEmpty(iSalesUomIdSelected) || OlbCore.isNotEmpty(iPurchaseUomIdSelected)) {
					var iTarget = $("#extendOtherUom > i");
					if (!iTarget.hasClass("blue")) {
						iTarget.addClass("blue");
					}
				} else {
					$("#extendOtherUom > i").removeClass("blue");
				}
			});
			
			$("#popoverOtherUom").on("open", function(){
				var iSalesUomIdSelected = salesUomDDL.getValue();
				var iPurchaseUomIdSelected = purchaseUomDDL.getValue();
				var iUomAvailableData = [
					{uomId: "", description: "---"},
				];
				
				var iQuantityUomId = quantityUomDDL.getValue();
				if (iQuantityUomId) {
					var iQuantityUomLabel = quantityUomDDL.getLabel();
					iUomAvailableData.push({"uomId": iQuantityUomId, "description": iQuantityUomLabel});
				}
				
				var iAlterUomData = getAlterUomDataList();
				if (iAlterUomData) {
					for (var x in iAlterUomData) {
						var jItem = iAlterUomData[x];
						var jUomId = jItem.uomFromId;
						if (jUomId) {
							for (var i = 0 ; i < uomData.length; i++){
								if (jUomId == uomData[i].uomId){
									iUomAvailableData.push({"uomId": jUomId, "description": uomData[i].description});
								}
							}
						}
					}
				}
				
				salesUomDDL.updateSource(null, iUomAvailableData, function(){
					salesUomDDL.selectItem([iSalesUomIdSelected]);
				});
				purchaseUomDDL.updateSource(null, iUomAvailableData, function(){
					purchaseUomDDL.selectItem([iPurchaseUomIdSelected]);
				});
			});
		};
		var resetPopoverOtherUom = function(){
			salesUomDDL.clearAll();
			purchaseUomDDL.clearAll();
			$("#extendOtherUom > i").removeClass("blue");
		};
		var setValuePopoverOtherUom = function(salesUomId, purchaseUomId){
			var iSalesUomIdSelected = salesUomId;
			var iPurchaseUomIdSelected = purchaseUomId;
			var iUomAvailableData = [
				{uomId: "", description: "---"},
			];
			
			var iQuantityUomId = quantityUomDDL.getValue();
			if (iQuantityUomId) {
				var iQuantityUomLabel = quantityUomDDL.getLabel();
				iUomAvailableData.push({"uomId": iQuantityUomId, "description": iQuantityUomLabel});
			}
			
			var iAlterUomData = getAlterUomDataList();
			if (iAlterUomData) {
				for (var x in iAlterUomData) {
					var jItem = iAlterUomData[x];
					var jUomId = jItem.uomFromId;
					if (jUomId) {
						for (var i = 0 ; i < uomData.length; i++){
							if (jUomId == uomData[i].uomId){
								iUomAvailableData.push({"uomId": jUomId, "description": uomData[i].description});
							}
						}
					}
				}
			}
			
			salesUomDDL.updateSource(null, iUomAvailableData, function(){
				salesUomDDL.selectItem([iSalesUomIdSelected]);
			});
			purchaseUomDDL.updateSource(null, iUomAvailableData, function(){
				purchaseUomDDL.selectItem([iPurchaseUomIdSelected]);
			});
			
			if (OlbCore.isNotEmpty(iSalesUomIdSelected) || OlbCore.isNotEmpty(iPurchaseUomIdSelected)) {
				var iTarget = $("#extendOtherUom > i");
				if (!iTarget.hasClass("blue")) {
					iTarget.addClass("blue");
				}
			} else {
				$("#extendOtherUom > i").removeClass("blue");
			}
		};
		var initUpdateMode = function(){
			<#assign virtualProductType = "PROD_FINISH"/>
			<#if updateMode>
				<#if product.productTypeId == "AGGREGATED">
					<#assign virtualProductType = "PROD_CONFIG"/>
				<#else>
					<#if product.isVirtual == "Y">
						<#assign virtualProductType = "PROD_VIRTUAL"/>
					</#if>
					<#if product.isVariant == "Y">
						<#assign virtualProductType = "PROD_VARIANT"/>
					</#if>
				</#if>
			</#if>
			productVirtualTypeDDL.selectItem(["${virtualProductType}"]);
			<#if virtualProductType == "PROD_VARIANT" && parentProductId?exists>
				$.ajax({
		    		type: "POST",
		    		url: "getChildProductFeaturesOptionAjax",
		    		data: {"parentProductId": "${parentProductId}", "productId": "${product.productId}"},
		    		beforeSend: function(){
		    			$("#loader_page_common").show();
		    		},
		    		success: function(data){
		    			jOlbUtil.processResultDataAjax(data, "default", "default", function(){
				    		$("#variant-feature-container").html(data);
						});
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
					}
		    	});
			</#if>
			
			<#if updateMode>
				$("#productCode").jqxInput("val", "${StringUtil.wrapString(product.productCode?if_exists)}");
				$("#productName").jqxInput("val", "${StringUtil.wrapString(product.productName?if_exists)}");
				$("#internalName").jqxInput("val", "${StringUtil.wrapString(product.internalName?if_exists)}");
				$("#barcode").jqxInput("val", "${StringUtil.wrapString(prodBarcode?if_exists)}");
				
				<#if featureTypeIds?has_content>
					<#list featureTypeIds as featureTypeId>
						featureTypeIdsCBB.selectItem(["${featureTypeId}"]);
					</#list>
				</#if>
				<#if parentProductId?exists>
					parentProductDDB.getGrid().bindingCompleteListener(function(){
						parentProductDDB.selectItem(["${parentProductId}"]);
					}, true);
				</#if>
				<#if product.primaryProductCategoryId?exists>
					primaryProductCategoryCBB.selectItem(["${product.primaryProductCategoryId}"]);
				</#if>
				<#if productCategoryIds?has_content>
					<#list productCategoryIds as productCategoryId>
						productCategoriesCCB.selectItem(["${productCategoryId}"]);
					</#list>
				</#if>
				<#if productCategoryTaxIds?has_content>
					<#list productCategoryTaxIds as productCategoryId>
						taxProductCategoryDDL.selectItem(["${productCategoryId}"]);
					</#list>
				</#if>
				<#if product.brandName?exists>
					brandNameCBB.selectItem(["${product.brandName}"]);
				</#if>
				<#if product.weightUomId?exists>
					weightUomDDL.selectItem(["${product.weightUomId}"]);
				</#if>
				<#--<#if product.quantityUomId?exists>
					quantityUomDDL.selectItem(["${product.quantityUomId}"]);
				</#if>-->
				<#if currencyUomId?exists>
					currencyUomCBB.selectItem(["${currencyUomId}"]);
				</#if>

				var iSalesUomId = <#if product.salesUomId?exists>"${product.salesUomId}"<#else>""</#if>;
				var iPurchaseUomId = <#if product.purchaseUomId?exists>"${product.purchaseUomId}"<#else>""</#if>;
				setValuePopoverOtherUom(iSalesUomId, iPurchaseUomId);
			<#else>
				<#if parameters.productCategoryId?exists>
					primaryProductCategoryCBB.selectItem(["${parameters.productCategoryId}"]);
				</#if>
			</#if>
		};
		var initElement = function(){
			jOlbUtil.input.create("#productCode", {width: '94%'});
			jOlbUtil.input.create("#productName", {width: '94%'});
			jOlbUtil.input.create("#internalName", {width: '94%'});
			jOlbUtil.input.create("#barcode", {width: '94%'});
			jOlbUtil.numberInput.create("#productWeight", {width: 150, spinButtons: true, digits: 6, decimalDigits: 3, allowNull: true, showClearButton: true});
			jOlbUtil.numberInput.create("#weight", {width: 150, spinButtons: true, digits: 6, decimalDigits: 3, allowNull: true, showClearButton: true});
			jOlbUtil.numberInput.create("#productDefaultPrice", {width: 150, spinButtons: false, digits: 8, decimalDigits: ${priceDecimalDigits}, allowNull: true, showClearButton: true});
			jOlbUtil.numberInput.create("#productListPrice", {width: 150, spinButtons: false, digits: 8, decimalDigits: ${priceDecimalDigits}, allowNull: true, showClearButton: true});
			
			setTimeout(function(){
				var locale = "${locale}";
				if(locale == "vi"){
					$("#productWeight").jqxNumberInput({decimalSeparator: ",", groupSeparator: "."});
					$("#weight").jqxNumberInput({decimalSeparator: ",", groupSeparator: "."});
					$("#productDefaultPrice").jqxNumberInput({decimalSeparator: ",", groupSeparator: "."});
					$("#productListPrice").jqxNumberInput({decimalSeparator: ",", groupSeparator: "."});
				}
				
				<#if updateMode>
					$("#productWeight").jqxNumberInput("val", <#if product.productWeight?exists>"${product.productWeight}"<#else>null</#if>);
					$("#weight").jqxNumberInput("val", <#if product.weight?exists>"${product.weight}"<#else>null</#if>);
					$("#productDefaultPrice").jqxNumberInput("val", <#if productDefaultPriceValue?exists>"${productDefaultPriceValue}"<#else>null</#if>);
					$("#productListPrice").jqxNumberInput("val", <#if productListPriceValue?exists>"${productListPriceValue}"<#else>null</#if>);
				<#else>
					$("#productWeight").jqxNumberInput("val", null);
					$("#weight").jqxNumberInput("val", null);
					$("#productDefaultPrice").jqxNumberInput("val", null);
					$("#productListPrice").jqxNumberInput("val", null);
				</#if>
			}, 50);
			
			jOlbUtil.windowPopup.create($("#alterpopupWindowDescriptionEdit"), {width: 580, height: 420, cancelButton: $("#we_alterCancel")});
			$("#we_description").jqxEditor({theme: "olbiuseditor", width: "98%", height: 280});
		};
		var initElementComplex = function(){
			if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
			uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
			uiLabelMap.BSProductId = "${StringUtil.wrapString(uiLabelMap.BSProductId)}";
			uiLabelMap.BSProductName = "${StringUtil.wrapString(uiLabelMap.BSProductName)}";
			
			var productVirtualTypeEnumsData = [
				{enumId: "PROD_FINISH", description: "${StringUtil.wrapString(uiLabelMap.BSProductNormal)}"},
				{enumId: "PROD_VIRTUAL", description: "${StringUtil.wrapString(uiLabelMap.BSProductVirtualD)}"},
				{enumId: "PROD_VARIANT", description: "${StringUtil.wrapString(uiLabelMap.BSProductVariant)}"},
				{enumId: "PROD_CONFIG", description: "${StringUtil.wrapString(uiLabelMap.BSProductConfiguration)}"}
			];
			var configProdVirtalType = {
				width:'100%',
				height: 25,
				key: "enumId",
	    		value: "description",
	    		displayDetail: false,
				selectedIndex: 0,
				dropDownWidth: 'auto',
				placeHolder: uiLabelMap.BSClickToChoose,
				autoDropDownHeight: true,
				<#if updateMode && !copyMode>disabled: true,</#if>
			};
			productVirtualTypeDDL = new OlbDropDownList($("#productVirtualTypeId"), productVirtualTypeEnumsData, configProdVirtalType, []);
			if("PRD_TAX_INV_MANL" == productTypeId) {
                $("#divProductVirtualType").hide();
            };
			
			var urlSnameParentProd=<#if !updateMode || (updateMode && product.isVariant == "Y")>"JQGetListProductAll&isVirtual=Y"<#else>""</#if>;
			var configParentProduct = {
				useUrl: true,
				root: 'results',
				widthButton: '100%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [{name: 'productId', type: 'string'}, {name: 'productCode', type: 'string'}, 
							{name: 'productName', type: 'string'}, {name: 'primaryProductCategoryId', type: 'string'}, 
							{name: 'quantityUomId'}],
				columns: [
					{text: uiLabelMap.BSProductId, datafield: 'productCode', width: '30%'},
					{text: uiLabelMap.BSProductName, datafield: 'productName', width: '70%'},
				],
				url: urlSnameParentProd,
				useUtilFunc: true,
				
				key: 'productId',
				keyCode: 'productCode',
				description: ['productName'],
				autoCloseDropDown: true,
				filterable: true,
				sortable: true,
			};
			parentProductDDB = new OlbDropDownButton($("#parentProductId"), $("#parentProductGrid"), null, configParentProduct, []);
			
			var configFeatureTypeList = {
				width:'100%',
				height: 25,
				key: "productFeatureTypeId",
	    		value: "description",
	    		displayDetail: true,
				dropDownWidth: 'auto',
				multiSelect: true,
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: false,
				url: '',
				autoComplete: true,
			};
			featureTypeIdsCBB = new OlbComboBox($("#featureTypeId"), featureTypeData, configFeatureTypeList, []);
			
			var configProductCatIdList = {
				width:'100%',
				height: 25,
				key: "productCategoryId",
	    		value: "categoryName",
	    		displayDetail: true,
				dropDownWidth: 400,
				multiSelect: false,
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: true,
				<#--url: 'jqxGeneralServicer?sname=JQListCategoryByCatalog&showAll=Y&pagesize=0',-->
				url: 'jqxGeneralServicer?sname=JQListProductCategory&pagesize=0&productCategoryTypeId=CATALOG_CATEGORY&showChildren=N',
				autoComplete: true,
				showClearButton: true,
			};
			primaryProductCategoryCBB = new OlbComboBox($("#primaryProductCategoryId"), null, configProductCatIdList, []);
			
			var configProductCatIdsList = {
				width:'100%',
				height: 25,
				key: "productCategoryId",
	    		value: "categoryName",
	    		displayDetail: true,
				dropDownWidth: 400,
				multiSelect: true,
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: true,
				<#--url: 'jqxGeneralServicer?sname=JQListCategoryByCatalog&showAll=Y&pagesize=0',-->
				url: 'jqxGeneralServicer?sname=JQListProductCategory&pagesize=0&productCategoryTypeId=CATALOG_CATEGORY&showChildren=N',
				autoComplete: true,
			};
			productCategoriesCCB = new OlbComboBox($("#productCategoryIds"), null, configProductCatIdsList, []);
			
			var configBrand = {
				width:'100%',
				height: 25,
				root: "brands",
				key: "partyId",
	    		value: "groupName",
	    		displayDetail: true,
				dropDownWidth: 'auto',
				multiSelect: false,
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: true,
				url: 'getListProductBrands',
			};
			brandNameCBB = new OlbComboBox($("#brandName"), null, configBrand, []);
			
			var configTaxCategory = {
				width:'100%',
				height: 25,
				key: "productCategoryId",
	    		value: "categoryName",
	    		displayDetail: true,
				dropDownWidth: 400,
				autoDropDownHeight: 'auto',
				multiSelect: false,
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: false,
				url: '',
				showClearButton: true,
			};
			taxProductCategoryDDL = new OlbDropDownList($("#taxProductCategoryId"), taxCategoryData, configTaxCategory, []);
			
			var configWeightUom = {
				width: 150,
				height: 25,
				key: "uomId",
	    		value: "description",
	    		displayDetail: false,
				dropDownWidth: 'auto',
				autoDropDownHeight: 'auto',
				multiSelect: false,
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: false,
				url: '',
				renderer: function (index, label, value) {
					var datasource = $("#weightUomId").jqxDropDownList("source");
					if (datasource) {
						var datarecords = datasource.records;
						if (datarecords) {
							var datarecord = datarecords[index];
							if (datarecord) {
			            		return label + " [" + datarecord.abbreviation + "]";
							}
						}
					}
					return label;
		        },
			};
			weightUomDDL = new OlbDropDownList($("#weightUomId"), weightUomData, configWeightUom, []);
			
			var configQuantityUom = {
				width: 150,
				height: 25,
				key: "uomId",
	    		value: "description",
	    		displayDetail: false,
				dropDownWidth: 'auto',
				autoDropDownHeight: 'auto',
				multiSelect: false,
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: false,
				//url: 'jqxGeneralServicer?sname=JQListProductUom&pagesize=0',
				renderer: function (index, label, value) {
					var datasource = $("#quantityUomId").jqxDropDownList("source");
					if (datasource) {
						var datarecords = datasource.records;
						if (datarecords) {
							var datarecord = datarecords[index];
							if (datarecord) {
			            		return label + " [" + datarecord.abbreviation + "]";
							}
						}
					}
					return label;
		        },
		        <#if updateMode && !copyMode>disabled: true,</#if>
			};
			quantityUomDDL = new OlbDropDownList($("#quantityUomId"), quantityUomData, configQuantityUom, [<#if updateMode>"${product.quantityUomId?if_exists}"</#if>]);
			
			var configCurrencyUom = {
				width: 150,
				height: 25,
				key: "uomId",
	    		value: "abbreviation",
	    		displayDetail: false,
				dropDownWidth: 200,
				autoDropDownHeight: false,
				multiSelect: false,
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: false,
				url: '',
				renderer: function (index, label, value) {
					var datasource = $("#currencyUomId").jqxComboBox("source");
					if (datasource) {
						var datarecords = datasource.records;
						if (datarecords) {
							var datarecord = datarecords[index];
							if (datarecord) {
			            		return label + " [" + datarecord.description + "]";
							}
						}
					}
					return label;
		        },
			};
			currencyUomCBB = new OlbComboBox($("#currencyUomId"), currencyUomDataInfo, configCurrencyUom, ["${currentCurrencyUomId?if_exists}"]);
			var alterUomData = [
				<#if configPackingAppls?has_content>
					<#list configPackingAppls as item>
					{	productId: "${item.productId}",
						uomFromId: "${item.uomFromId}",
						uomToId: "${item.uomToId}",
						fromDate: "${item.fromDate}",
						thruDate: "${item.thruDate?if_exists}",
						quantityConvert: "${item.quantityConvert?if_exists}",
						price: "${item.price?if_exists}",
						barcode: "${item.barcode?if_exists}",
					},
					</#list>
				</#if>
			];
			if (alterUomData.length > 0) {
				$("#containerGridAlterUom").css("visibility", "visible");
			}
			var configGridAlterUom = {
				theme: 'bootstrap',
				datafields: [
					{name: 'uomFromId', type: 'string'},
					{name: 'uomToId', type: 'string'},
					{name: 'quantityConvert', type: 'string'},
					{name: 'fromDate', type: 'date', other: 'Timestamp'},
					{name: 'thruDate', type: 'date', other: 'Timestamp'},
					{name: 'barcode', type: 'string'},
					{name: 'price', type: 'number'},
				],
				columns: [
					{text: '${uiLabelMap.BSId}', dataField: 'uomFromId', width: '25%', editable: false,
						cellsrenderer: function(row, column, value){
					 		var returnVal = '<div class=\"innerGridCellContent align-center\">';
				   			for (var i = 0 ; i < uomData.length; i++){
								if (value == uomData[i].uomId){
									returnVal += uomData[i].description + '</div>';
			   						return returnVal;
								}
							}
				   			returnVal += value + '</div>';
			   				return returnVal;
						},
					}, 
					{text: '${uiLabelMap.BSQty}', dataField: 'quantityConvert', width: '20%', cellClassName: 'background-prepare', cellsalign: 'right', columntype: 'numberinput', cellsformat: 'd',
						cellsrenderer: function(row, column, value) {
					 		var str = '<div class=\"innerGridCellContent align-right\">';
					 		if (typeof(value) != 'undefined') {
						 		str += formatnumber(value);
					 		} else {
								str += value;
							}
							str += '</div>';
							return str;
					 	},
					 	validation: function (cell, value) {
							if (value < 1) {
								return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
							}
							return true;
						},
						createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({decimalDigits: 0, digits: 9});
						}
					},
					{text: '${uiLabelMap.BSBarcode}', dataField: 'barcode', width: '30%', cellClassName: 'background-prepare', columntype: 'input', 
						cellsrenderer: function(row, column, value) {
					 		var str = '<div class=\"innerGridCellContent align-right\">';
							str += value;
							str += '</div>';
							return str;
					 	},
						createeditor: function (row, cellvalue, editor) {
							editor.jqxInput({width: '100%', theme: OlbCore.theme});
						}
					},
					{text: '${uiLabelMap.BSPrice} (S)', dataField: 'price', cellClassName: 'background-prepare', cellsalign: 'right', columntype: 'numberinput', cellsformat: 'd${priceDecimalDigits}',
						cellsrenderer: function(row, column, value) {
					 		var str = '<div class=\"innerGridCellContent align-right\">';
					 		if (OlbCore.isNotEmpty(value)) {
						 		str += formatnumber(value, "${locale}", ${priceDecimalDigits});
					 		} else {
								str += value;
							}
							str += '</div>';
							return str;
					 	},
					 	validation: function (cell, value) {
							if (value < 0) {
								return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
							}
							return true;
						},
						createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({spinButtons: false, digits: 8, decimalDigits: ${priceDecimalDigits}, allowNull: true, min: 0, width: '100%', theme: OlbCore.theme});
						}
					},
				],
				width: '100%',
				height: 200,
				sortable: false,
				filterable: false,
				pageable: false,
				pagesize: 5,
				showfilterrow: false,
				useUtilFunc: false,
				useUrl: false,
				url: '',
				groupable: false,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				selectionmode:'singlerow',
				virtualmode: false,
				editable: true,
				editmode: 'dblclick',
			};
			new OlbGrid($("#jqxGridAlterUom"), alterUomData, configGridAlterUom, []);
		};
		var initEvent = function(){
			$("#btnEditDescription").on("click", function(){
				$("#alterpopupWindowDescriptionEdit").jqxWindow("open");
				$("#we_description").jqxEditor('focus');
			});
			$("#we_alterSave").on("click", function(){
				var processValue = $("#we_description").jqxEditor('val');
				<#--
				if (typeof(processValue) != 'undefined') {
					var hasBegin = /^(<div><div>){1}(.)*$/.test(processValue);
					var hasEnd = /^(.)*(<\/div>(.)+<\/div>){1}$/.test(processValue);
					if (hasBegin && hasEnd) {
						processValue = processValue.substring(5, processValue.length - 7);
					}
				} else {
					processValue = "";
				}
				-->
				$("#descriptionTxt").html(processValue);
				$("#alterpopupWindowDescriptionEdit").jqxWindow("close");
			});
			$("#we_alterCancel").on("click", function(){
				$("#we_description").jqxEditor('val', $("#descriptionTxt").html());
				$("#alterpopupWindowDescriptionEdit").jqxWindow("close");
			});
			quantityUomDDL.selectListener(function(itemData){
				resetPopoverOtherUom();
				
				// remove item in alter uom
				if (OlbPageAlterUom) OlbPageAlterUom.removeItemAlterUomSelect(itemData.value, true);
			});
			<#--
			$("#productVirtualTypeId").on("change", function(event){
				var args = event.args;
    			if (args) {
    				var item = args.item;
    				if (item) {
    					var value = item.value;
    					if ("PROD_VIRTUAL" == value) {
    						// display select features type
    						$(".variant-group").hide();
    						$(".virtual-group").show();
    					} else if ("PROD_VARIANT" == value) {
    						// display select value of feature
    						$(".virtual-group").hide();
    						$(".variant-group").show();
    					}
    				}
    			}
			});
			-->
			productVirtualTypeDDL.selectListener(function(itemData, index){
				var value = itemData.value;
				if ("PROD_VIRTUAL" == value) {
					// display select features type
					$(".variant-group").hide();
					$(".virtual-group").show();
					$(".container-price").addClass("required");
					//$("#alterShowMore").hide();
					$("#prodConfigItemContainer").hide();
				} else if ("PROD_VARIANT" == value) {
					// display select value of feature
					$(".virtual-group").hide();
					$(".variant-group").show();
					$(".container-price").removeClass("required");
					//$("#alterShowMore").hide();
					$("#prodConfigItemContainer").hide();
				} else if ("PROD_FINISH" == value) {
					$(".virtual-group").hide();
					$(".variant-group").hide();
					$(".container-price").addClass("required");
					//$("#alterShowMore").hide();
					$("#prodConfigItemContainer").hide();
				} else if ("PROD_CONFIG" == value) {
					productTypeId = "AGGREGATED";
					//$("#alterShowMore").show();
					$("#prodConfigItemContainer").show();
				}
			});
			parentProductDDB.getGrid().rowSelectListener(function(rowData){
				//var primaryProductCategoryId = rowData.primaryProductCategoryId;
				//if (primaryProductCategoryId) primaryProductCategoryCBB.selectItem([primaryProductCategoryId]);
				if (rowData.quantityUomId) quantityUomDDL.selectItem([rowData.quantityUomId]);
				if (rowData.productName) $("#productName").jqxInput("val", rowData.productName);
				
		    	$.ajax({
		    		type: "POST",
		    		url: "getChildProductFeaturesOptionAjax",
		    		data: {"parentProductId": rowData.productId},
		    		beforeSend: function(){
		    			$("#loader_page_common").show();
		    		},
		    		success: function(data){
		    			jOlbUtil.processResultDataAjax(data, "default", "default", function(){
				    		$("#variant-feature-container").html(data);
						});
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
					}
		    	});
		    });
		    
		    $("#quickAddNewUom").on("click", function(){
		    	if (typeof(OlbPageProdPackingUomNew) != "undefined") {
		    		OlbPageProdPackingUomNew.openWindowProdUomNew();
		    	} 
		    });
		    
		    $("#wn_ppu_alterSave").on("click", function(){
				if (!OlbPageProdPackingUomNew.getValidator().validate()) return false;
				
				var dataMap = OlbPageProdPackingUomNew.getValue();
				$.ajax({
					type: 'POST',
					url: "createProductUom",
					data: dataMap,
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
						        	$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html(errorMessage);
						        	$("#jqxNotification").jqxNotification("open");
						        	return false;
								}, function(){
									$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
						        	$("#jqxNotification").jqxNotification("open");
						        	if (data.uomId != undefined && data.uomId != null) {
						        		quantityUomDDL.updateBoundData();
						        		quantityUomDDL.selectItem([data.uomId]);
						        		OlbPageProdPackingUomNew.closeWindowProdUomNew();
						        	}
								}
						);
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
					},
				});
			});
			
			$("#btnAddAlterUom").on("click", function(){
				OlbPageAlterUom.openWindowNew();
			});
		};
		var initValidateForm = function(){
			var extendRules = [
				{input: '#productDefaultPrice', message: validFieldRequire, action: 'keyup', 
					rule: function(input, commit){
						var productVirtualTypeId = productVirtualTypeDDL.getValue();
						if ("PROD_VIRTUAL" == productVirtualTypeId) {
							return OlbValidatorUtil.validElement(input, commit, 'validInputNotNull');
						} else if ("PROD_VARIANT" == productVirtualTypeId) {
							return true;
						} else if ("PROD_FINISH" == productVirtualTypeId) {
							return OlbValidatorUtil.validElement(input, commit, 'validInputNotNull');
						}
						return true;
					}
				},
                {input: '#productDefaultPrice', message: validFieldRequire, action: 'keyup',
                    rule: function(input, commit){
                        var productVirtualTypeId = productVirtualTypeDDL.getValue();
                        if ("PROD_VIRTUAL" == productVirtualTypeId) {
                            return OlbValidatorUtil.validElement(input, commit, 'validInputNotNull');
                        } else if ("PROD_VARIANT" == productVirtualTypeId) {
                            return true;
                        } else if ("PROD_FINISH" == productVirtualTypeId) {
                            return OlbValidatorUtil.validElement(input, commit, 'validInputNotNull');
                        }
                        return true;
                    }
                },
				{input: '#taxProductCategoryId', message: validFieldRequire, action: 'keyup',
					rule: function(input, commit){
				    if("PRD_TAX_INV_MANL" == productTypeId) {
                        return OlbValidatorUtil.validElement(input, commit, 'validInputNotNull');
                    }
                    return true;
					}
				},
				{input: '#parentProductId', message: validFieldRequire, action: 'change', 
					rule: function(input, commit){
						var productVirtualTypeId = productVirtualTypeDDL.getValue();
						if ("PROD_VARIANT" == productVirtualTypeId) {
							return OlbValidatorUtil.validElement(input, commit, 'validObjectNotNull', {objType: 'dropDownButton'});
						}
						return true;
					}
				},
				{input: '#weightUomId', message: validFieldRequire, action: 'keyup', 
					rule: function(input, commit){
						var amountUomTypeId = OlbProductInfoMoreNew.getObj().amountUomTypeDDL.getValue();
						if (OlbCore.isNotEmpty(amountUomTypeId)) {
							return OlbValidatorUtil.validElement(input, commit, 'validInputNotNull');
						}
						return true;
					}
				},
			];
			var mapRules = [
		            {input: '#productCode', type: 'validCannotSpecialCharactor'},
		            <#--{input: '#barcode', type: 'validOnlyContainCharacterAZaz09'},-->
					{input: '#productVirtualTypeId', type: 'validInputNotNull'},
					{input: '#productName', type: 'validInputNotNull'},
					{input: '#quantityUomId', type: 'validObjectNotNull', objType: 'dropDownList'},
	            ];
			validatorVAL = new OlbValidator($('#form-product-info'), mapRules, extendRules, {position: 'bottom'});
			<#--{input: '#productCode', type: 'validInputNotNull'},-->
		};
		var getValidator = function(){
			return validatorVAL;
		};
		var clearNumberInput = function(element){
			jOlbUtil.numberInput.clear(element);
		};
		var getAlterUomDataList = function(){
			var returnArray = new Array();
			var dataRows = $("#jqxGridAlterUom").jqxGrid("getboundrows");
			if (dataRows) {
				for (var i = 0; i < dataRows.length; i++) {
					var dataItem = dataRows[i];
					if (dataItem != window && OlbCore.isNotEmpty(dataItem.uomFromId)) {
						if (dataItem.fromDate) {
							dataItem.fromDate = (new Date(dataItem.fromDate)).getTime();
						}
						if (dataItem.thruDate) {
							dataItem.thruDate = (new Date(dataItem.thruDate)).getTime();
						}
						returnArray.push(dataItem);
					}
				}
			}
			return returnArray;
		};
		var getAlterUomData = function(){
			var returnArray = getAlterUomDataList();
			return JSON.stringify(returnArray);
		};
		var getObj = function(){
			return {
				"productVirtualTypeDDL": productVirtualTypeDDL,
				"parentProductDDB": parentProductDDB,
				"primaryProductCategoryCBB": primaryProductCategoryCBB,
				"productCategoriesCCB": productCategoriesCCB,
				"brandNameCBB": brandNameCBB,
				"taxProductCategoryDDL": taxProductCategoryDDL,
				"weightUomDDL": weightUomDDL,
				"quantityUomDDL": quantityUomDDL,
				"currencyUomCBB": currencyUomCBB,
				"featureTypeIdsCBB": featureTypeIdsCBB,
				"salesUomDDL": salesUomDDL,
				"purchaseUomDDL": purchaseUomDDL,
			};
		};
		var getValue = function(){
			// miss: displayColor, feature
			// miss: dayN, shelflife, taxInPrice, uomFromId1, quantityConvert1, thruDate1
			
			var productVirtualTypeId = productVirtualTypeDDL.getValue();
			var dataMap = {};
			dataMap.productVirtualTypeId 		= productVirtualTypeId;
			dataMap.parentProductId 			= parentProductDDB.getValue();
			dataMap.productCategoryIds 			= productCategoriesCCB.getValue();
			dataMap.brandName 					= brandNameCBB.getValue();
			dataMap.weightUomId 				= weightUomDDL.getValue();
			dataMap.quantityUomId 				= quantityUomDDL.getValue();
			dataMap.salesUomId 					= salesUomDDL.getValue();
			dataMap.purchaseUomId 				= purchaseUomDDL.getValue();
			dataMap.currencyUomId 				= currencyUomCBB.getValue();
			dataMap.featureTypeIds 				= featureTypeIdsCBB.getValue();
			dataMap.productTypeId 				= productTypeId;
			
			var primaryProductCategoryId = primaryProductCategoryCBB.getValue();
			if (primaryProductCategoryId == null) primaryProductCategoryId = "";
			dataMap.primaryProductCategoryId = primaryProductCategoryId;
			
			var taxProductCategoryId = taxProductCategoryDDL.getValue();
			if (taxProductCategoryId == null) taxProductCategoryId = "";
			dataMap.taxProductCategoryId = taxProductCategoryId;
			var processValue = $("#we_description").jqxEditor('val');
			<#--
			if (typeof(processValue) != 'undefined') {
				var hasBegin = /^(<div><div>){1}(.)*$/.test(processValue);
				var hasEnd = /^(.)*(<\/div>(.)+<\/div>){1}$/.test(processValue);
				if (hasBegin && hasEnd) {
					processValue = processValue.substring(5, processValue.length - 7);
				}
			} else {
				processValue = "";
			}
			-->
			dataMap.productDescription = processValue;
			
			dataMap.productCode = $("#productCode").val();
			dataMap.productName = $("#productName").val();
			dataMap.internalName = $("#internalName").val();
			dataMap.barcode = $("#barcode").val();
			dataMap.productWeight = $("#productWeight").jqxNumberInput("getDecimal");
			dataMap.weight = $("#weight").jqxNumberInput("getDecimal");
			
			var productDefaultPrice = $("#productDefaultPrice").jqxNumberInput("getDecimal");
			var productListPrice = $("#productListPrice").jqxNumberInput("getDecimal");
			dataMap.productDefaultPrice = productDefaultPrice; //OlbCore.formatValueStrNumber(productDefaultPrice, "${locale}");
			dataMap.productListPrice = productListPrice; //OlbCore.formatValueStrNumber(productListPrice, "${locale}");
			dataMap.isPriceIncludedVat = "Y";
			
			var isVirtual = "N";
			var isVariant = "N";
			if (productVirtualTypeId == "PROD_VIRTUAL") {
				isVirtual = "Y";
			} else if (productVirtualTypeId == "PROD_VARIANT") {
				isVariant = "Y";
			};
			dataMap.isVirtual = isVirtual;
			dataMap.isVariant = isVariant;
			
			var featureIds = new Array();
			$('[id^="featureProduct_"]').each(function(i, obj) {
			    var featureId = $(obj).jqxDropDownList('getSelectedItem');
				if (OlbCore.isNotEmpty(featureId)) {
					featureIds.push(featureId.value);
				}
			});
			dataMap.featureIds = featureIds;
			
			dataMap.alterUomData = getAlterUomData();
			return dataMap;
		};
		return {
			init: init,
			getObj: getObj,
			getValidator: getValidator,
			clearNumberInput: clearNumberInput,
			getAlterUomData: getAlterUomData,
			resetPopoverOtherUom: resetPopoverOtherUom,
			getValue: getValue,
		};
	}());
</script>