<#--
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script type="text/javascript" src="/poresources/js/product/ProductDetail.js"></script>
-->
<style>
	.text-header {
		color: black !important;
	}
	.form-window-content-custom label {
	    margin-top: -4px;
	}
	.boder-all-profile .label {
	    font-size: 14px;
	    text-shadow: none;
	    background-color: #3a87ad !important;
		margin: 0px;
		color: white !important;
    	line-height: 14px !important;
		margin-top: -20px;
	}
	#DisplayColor {
	    width: 150px;
    	height: 20px;
		border: 1px solid #ccc;
	}
</style>
<#--
<#assign productFeatureTypes = Static["com.olbius.basepo.product.ProductUtils"].getProductFeatureTypes(delegator)!/>
<#assign listProductType = delegator.findList("ProductType", null, null, null, null, false) />
<#assign listProductFeature = delegator.findList("ProductFeature", null, null, null, null, false) />
<#assign listProductCategories = delegator.findList("ProductCategory", null, null, null, null, false) />
<#assign listUom = delegator.findList("Uom", null, null, null, null, false) />

var mapUom = {<#list listUom as item>
	"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get('description', locale)?if_exists)}",
	</#list>};
	var mapProductType = {<#list listProductType as item>
	"${item.productTypeId?if_exists}": "${StringUtil.wrapString(item.get('description', locale)?if_exists)}",
	</#list>};
	var mapProductFeature = {<#if listProductFeature?exists><#list listProductFeature as item>
	"${item.productFeatureId?if_exists}": "${StringUtil.wrapString(item.get('description', locale)?if_exists)}",
	</#list></#if>};
	var mapProductCategory = {<#if listProductCategories?exists><#list listProductCategories as item>
	"${item.productCategoryId?if_exists}": "${StringUtil.wrapString(item.categoryName?if_exists)}",
	</#list></#if>};
	
	var productFeatureTypes = [<#if productFeatureTypes?exists><#list productFeatureTypes as item>
	"${item.productFeatureTypeId?if_exists}",
	</#list></#if>];
-->
<script>
	$(function(){
		var DmsIsVirtual = "${StringUtil.wrapString(uiLabelMap.DmsIsVirtual)}";
		var DmsIsVariant = "${StringUtil.wrapString(uiLabelMap.DmsIsVariant)}";
		var BSSaleProducts = "${StringUtil.wrapString(uiLabelMap.BSSaleProducts)}";
		<#if parameters.productId?exists>
			var productIdParam = "${parameters.productId?if_exists}";
			$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.BSProductDetail)}");
			$('[data-rel=tooltip]').tooltip();
		</#if>
	});
	function openChildProduct(){
		var buttonLink = $('a[href="#prodchildren-tab"]');
		$(buttonLink).trigger("click");
	};
</script>
<#assign productVirtualTypeEnumsMap = {"PROD_FINISH": "${StringUtil.wrapString(uiLabelMap.BSProductNormal)}"}/>
<#assign productVirtualTypeEnumsMap = productVirtualTypeEnumsMap + {"PROD_VIRTUAL": "${StringUtil.wrapString(uiLabelMap.BSProductVirtualD)}"}/>
<#assign productVirtualTypeEnumsMap = productVirtualTypeEnumsMap + {"PROD_VARIANT": "${StringUtil.wrapString(uiLabelMap.BSProductVariant)}"}/>
<#assign productVirtualTypeEnumsMap = productVirtualTypeEnumsMap + {"PROD_CONFIG": "${StringUtil.wrapString(uiLabelMap.BSProductConfiguration)}"}/>

<#if !dataProdInfo?exists><#assign dataProdInfo = {}/></#if>
<div id="prodinfo-tab" class="tab-pane<#if !activeTab?exists || activeTab == "" || activeTab == "prodinfo-tab"> active</#if>">
	<div style="position:relative"><!-- class="widget-body"-->
		<div><!--class="widget-main"-->
			<div class="row-fluid">
				<div>
					<div class="row-fluid">
						<div class="span3">
							<#include "productViewImage.ftl"/>
						</div>
						<div class="span9">
							<#--form-horizontal form-window-content-custom label-text-left content-description-->
							<div class="row-fluid" style="margin-top:-12px">
								<div class="form-window-content-custom content-description-left">
									<div class="span6">
										<#-- general information -->
										<div class="row-fluid title-description">
											<label>${uiLabelMap.DmsGeneralInformation}</label>
											<div>
												<span></span>
											</div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.BSProductId}:</label>
											<div>
												<span>${dataProdInfo.productCode?if_exists}</span>
											</div>
										</div>
										<#assign parentProduct = Static["com.olbius.basesales.product.ProductWorker"].getParentProduct(dataProdInfo.productId, delegator, nowTimestamp)!/>
										<#if parentProduct?has_content>
											<div class="row-fluid">
												<label>${uiLabelMap.BSParentProductId}:</label>
												<div>
													<span><a href="<@ofbizUrl>viewProduct?productId=${parentProduct.productId}</@ofbizUrl>">${parentProduct.productCode?if_exists}</a></span>
												</div>
											</div>
										</#if>
										<div class="row-fluid">
											<label>${uiLabelMap.ProductType}:</label>
											<div>
												<span>
													<#--
													<#if product.productTypeId?exists>
														<#assign productType = delegator.findOne("ProductType", {"productTypeId": product.productTypeId}, false)!/>
														${productType?if_exists.get("description", locale)?if_exists}
													</#if>
													-->
													<#if product.productTypeId == "AGGREGATED">
														${productVirtualTypeEnumsMap["PROD_CONFIG"]}&nbsp;&nbsp;&nbsp;<a href="javascript:void(0)" onClick="javascript:openChildProduct();"><i class="fa fa-share-square-o"></i></a>
													<#else>
														<#assign isVirtual = dataProdInfo.isVirtual?default("N")/>
														<#assign isVariant = dataProdInfo.isVariant?default("N")/>
														<#if isVirtual == "N" && isVariant == "N">
															${productVirtualTypeEnumsMap["PROD_FINISH"]}
														<#elseif isVirtual == "Y" && isVariant == "N">
															${productVirtualTypeEnumsMap["PROD_VIRTUAL"]}
														<#elseif isVirtual == "N" && isVariant == "Y">
															${productVirtualTypeEnumsMap["PROD_VARIANT"]}
														</#if>
													</#if>
												</span>
											</div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.ProductProductName}:</label>
											<div>
												<span>${dataProdInfo.productName?if_exists}</span>
											</div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.BSAbbreviateName}:</label>
											<div>
												<span>${dataProdInfo.internalName?if_exists}</span>
											</div>
										</div>
										<div class="row-fluid">
											<#--
											<label>${uiLabelMap.ProductInternalName}:</label>
											<div>
												<span>${dataProdInfo.internalName?if_exists}</span>
											</div>
											-->
											<label>${uiLabelMap.BSBarcode}:</label>
											<div>
												<span id="txtBarcodeIds"><#if prodBarcode?exists>${StringUtil.wrapString(prodBarcode)}</#if></span>
											</div>
										</div>
										<#if prodPLUCode?exists>
										<div class="row-fluid">
											<label>${uiLabelMap.BSPLUCode}:</label>
											<div>
												<span>${prodPLUCode?if_exists}</span>
											</div>
										</div>
										</#if>
										<#if product.salesDiscontinuationDate?exists>
										<div class="row-fluid">
											<label>${uiLabelMap.BSSalesDiscountinuationDate}:</label>
											<div>
												<span <#if nowTimestamp.compareTo(product.salesDiscontinuationDate) &gt; 0>class="red font-bold"</#if>>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(product.salesDiscontinuationDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}</span>
											</div>
										</div>
										</#if>
										<#if product.purchaseDiscontinuationDate?exists>
										<div class="row-fluid">
											<label>${uiLabelMap.BSPurchaseDiscountinuationDate}:</label>
											<div>
												<span <#if nowTimestamp.compareTo(product.purchaseDiscontinuationDate) &gt; 0>class="red font-bold"</#if>>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(product.purchaseDiscontinuationDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}</span>
											</div>
										</div>
										</#if>
										<div class="row-fluid">
											<label>${uiLabelMap.ProductBrandName}:</label>
											<div>
												<span>
												<#if dataProdInfo.brandName?exists>
													<#assign prodBrand = delegator.findOne("PartyGroup", {"partyId": dataProdInfo.brandName}, false)!/>
													${prodBrand?if_exists.groupName?if_exists}
												</#if>
												</span>
											</div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.BSPrimaryProductCategory}:</label>
											<div>
												<span>
													<#if dataProdInfo.primaryProductCategoryId?exists>
														<#assign productCategoryPrimary = delegator.findOne("ProductCategory", {"productCategoryId": dataProdInfo.primaryProductCategoryId}, false)!/>
														${productCategoryPrimary.categoryName?if_exists}
													</#if>
												</span>
											</div>
										</div>
										<#if dataProdInfo.productCategories?exists>
										<div class="row-fluid">
											<label>${uiLabelMap.BSOtherProductCategory}:</label>
											<div>
												<span>
													<#assign primaryProductCategoryId = dataProdInfo.primaryProductCategoryId?default("")/>
													<#list dataProdInfo.productCategories as prodCategoryId>
														<#if primaryProductCategoryId != prodCategoryId>
														<#assign productCategory = delegator.findOne("ProductCategory", {"productCategoryId": prodCategoryId}, false)!/>
														${productCategory.categoryName?if_exists}<#if prodCategoryId_has_next>, </#if>
														</#if>
													</#list>
												</span>
											</div>
										</div>
										</#if>
										<div class="row-fluid">
											<label>${uiLabelMap.DmsProductTaxCatalogs}:</label>
											<div>
												<span>
													<#if dataProdInfo.productCategoryTaxId?exists>
														<#assign productCategoryTax = delegator.findOne("ProductCategory", {"productCategoryId": dataProdInfo.productCategoryTaxId}, false)!/>
														${productCategoryTax.categoryName?if_exists}
													</#if>
												</span>
											</div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.DmsDescription}:</label>
											<div>
												<span>${StringUtil.wrapString(dataProdInfo.longDescription?default(""))}</span>
											</div>
										</div>
									</div><!--.span6-->
									<div class="span6">
										<#-- general information -->
										<div class="row-fluid title-description">
											<label>${uiLabelMap.DmsDetailInformation}</label>
											<div><span></span></div>
										</div>
										<#if product.requireAmount?exists && product.requireAmount == "Y">
										<div class="row-fluid">
											<label>${uiLabelMap.BSCalculatePriceBy}:</label>
											<div>
												<#if product.amountUomTypeId?exists>
													<#assign amountUomType = delegator.findOne("UomType", {"uomTypeId": product.amountUomTypeId}, false)!/>
													<span class="red">${amountUomType.get("description", locale)?default(product.amountUomTypeId)}</span>
												</#if>
											</div>
										</div>
										</#if>
										<#if dataProdInfo.weightUomId?exists>
											<#assign weightUom = delegator.findOne("Uom", {"uomId" : dataProdInfo.weightUomId}, false)!/>
										</#if>
										<div class="row-fluid">
											<label>${uiLabelMap.BSAbbWeightUom}:</label>
											<div>
												<span><#if dataProdInfo.weightUomId?exists && weightUom?exists>${weightUom.description?if_exists}<#if weightUom.abbreviation?exists> (${weightUom.abbreviation})</#if></#if></span>
											</div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.DmsNetWeight}:</label>
											<div>
												<span>
													<#if dataProdInfo.productWeight?exists>
														${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(dataProdInfo.productWeight, "#,###.###", locale)}
													 (${weightUom?if_exists.abbreviation?if_exists})
													</#if>
												</span>
											</div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.DmsProductWeight}:</label>
											<div>
												<span>
													<#if dataProdInfo.weight?exists>
														${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(dataProdInfo.weight, "#,###.###", locale)}
													 (${weightUom?if_exists.abbreviation?if_exists})
													</#if>
												</span>
											</div>
										</div>
										<#--
										<div class="row-fluid">
											<label>${uiLabelMap.DmsProductWeightUomId}:</label>
											<div>
												<span id="txtWeightUomId">weightUomId</span>
											</div>
										</div>
										-->
										<div class="row-fluid">
											<label>${uiLabelMap.BSUnitUom}:</label>
											<div>
												<span>
												<#if dataProdInfo.quantityUomId?exists>
													<#assign quantityUom = delegator.findOne("Uom", {"uomId" : dataProdInfo.quantityUomId}, false)!/>
													<#if quantityUom?exists && quantityUom?has_content>
														<#assign quantityUomDesc = quantityUom.get("description", locale)!/>
														${quantityUomDesc?if_exists}
													</#if>
													<#--${quantityUom?if_exists.get("description", locale)}-->
												</#if>
												</span>
											</div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.BSSalesUomId}:</label>
											<div>
												<span>
												<#if dataProdInfo.salesUomId?exists>
													<#assign salesQuantityUom = delegator.findOne("Uom", {"uomId" : dataProdInfo.salesUomId}, false)!/>
													<#if salesQuantityUom?exists && salesQuantityUom?has_content>
														<#assign salesQuantityUomDesc = salesQuantityUom.get("description", locale)!/>
														${salesQuantityUomDesc?if_exists}
													</#if>
												</#if>
												</span>
											</div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.BSPurchaseUomId}:</label>
											<div>
												<span>
												<#if dataProdInfo.purchaseUomId?exists>
													<#assign purchaseQuantityUom = delegator.findOne("Uom", {"uomId" : dataProdInfo.purchaseUomId}, false)!/>
													<#if purchaseQuantityUom?exists && purchaseQuantityUom?has_content>
														<#assign purchaseQuantityUomDesc = purchaseQuantityUom.get("description", locale)!/>
														${purchaseQuantityUomDesc?if_exists}
													</#if>
												</#if>
												</span>
											</div>
										</div>
										<#--
										<div class="row-fluid">
											<label>${uiLabelMap.TaxInPrice}:</label>
											<div>
												<span id="txtTaxInPrice"></span>
											</div>
										</div>
										-->
										<#assign currencyUomId = ""/>
										<#if dataProdInfo.productListPrice?exists>
											<#assign currencyUomId = dataProdInfo.productListPrice.currencyUomId?default("")/>
										</#if>
										<div class="row-fluid">
											<label>${uiLabelMap.DmsProductListPrice} (S):</label>
											<div>
												<span>
													<#if dataProdInfo.productListPriceValue?exists>
														<@ofbizCurrency amount=dataProdInfo.productListPriceValue isoCode=currencyUomId/>
													</#if>
												</span> /${quantityUomDesc?if_exists}
											</div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.DmsProductDefaultPrice} (S):</label>
											<div>
												<span class="red" style="font-weight: bold; font-size:16px">
													<#if dataProdInfo.productDefaultPriceValue?exists>
														<@ofbizCurrency amount=dataProdInfo.productDefaultPriceValue isoCode=currencyUomId/>
													</#if>
												</span> /${quantityUomDesc?if_exists}
												<#if listProductPrices?has_content>&nbsp;(<span class="red" style="font-weight: bold;">${uiLabelMap.BSHave} ${listProductPrices?size} ${uiLabelMap.BSOtherPrice}</span>)</#if>
											</div>
										</div>
										<#--
										<div class="row-fluid">
											<label>${uiLabelMap.BSCurrencyUomId}:</label>
											<div>
												<#assign currencyUom = delegator.findOne("Uom", {"uomId" : currencyUomId}, false)!/>
												<span>
													${currencyUom?if_exists.get("description", locale)}
												</span>
											</div>
										</div>
										-->

										<#-- features -->
										<#if productFeatureApplMap?has_content>
											<div class="row-fluid title-description">
												<label>${uiLabelMap.BSProductFeature}</label>
												<div><span></span></div>
											</div>
										
											<#list productFeatureApplMap.entrySet() as entryN>
												<#assign featureTypeId = entryN.getKey()/>
												<#assign features = entryN.getValue()/>
												<#assign featureType = delegator.findOne("ProductFeatureType", {"productFeatureTypeId": featureTypeId}, false)!/>
												<div class="row-fluid">
													<label>${featureType.description?if_exists}:</label>
													<div>
														<span>
															<#if features?exists>
																<#list features as feature>
																	${feature.description?if_exists}<#if feature_has_next>, </#if>
																</#list>
															</#if>
														</span>
													</div>
												</div>
											</#list>
										</#if>
									</div><!--.span6-->
								</div><!--.form-window-content-custom-->
							</div>
						</div><!--.span9-->
					</div><!--.row-fluid-->
				</div><!--.form-horizontal-->
			</div><!--.row-fluid-->
		</div>
	</div>
</div>
<div id="popupProductEditMainUPC" style="display:none">
	<div>${uiLabelMap.BSEditMainUpc}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<input id="we_mainupc_productId" type="hidden" value="${product.productId?if_exists}"/>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div><span>${uiLabelMap.BSProductId}: ${product.productCode?if_exists}</span></div>
					<div id="we_mainupc_upcgrid"></div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="we_mainupc_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="we_mainupc_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.core.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.util.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.grid.js"></script>
<script type="text/javascript">
	$(function(){
		OlbProductView.init();
	});
	var OlbProductView = (function(){
		var upcGRID;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($('#popupProductEditMainUPC'), {width: 620, height: 360, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#we_mainupc_alterCancel")});
		};
		var initElementComplex = function(){
			var configGridUPC = {
				datafields: [
					{name: 'productId', type: 'string'},
					{name: 'uomId', type: 'string'},
					{name: 'idValue', type: 'string'},
					{name: 'iupprm', type: 'number', formatter: 'integer'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSSTT)}', datafield: '', width: 50, sortable: false, filterable: false, editable: false, groupable: false, draggable: false, resizable: false,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=\"margin:4px;\">' + (row + 1) + '</div>';
					    }
					},
					{text: '${StringUtil.wrapString(uiLabelMap.BSUomId)}', datafield: 'uomId', width: 120},
					{text: 'UPC', datafield: 'idValue', minWidth: 120},
					{text: 'iupprm', dataField: 'iupprm', width: 140, cellsalign: 'right', filterable:false, sortable: false, columntype: 'numberinput', cellsformat: 'd',
						validation: function (cell, value) {
							if (value < 0) {
								return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
							}
							return true;
						},
						createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({decimalDigits: 0, digits: 9});
						}
				 	},
				],
				width: '100%',
				sortable: false,
				filterable: true,
				editable: true,
				pageable: true,
				pagesize: 5,
				showfilterrow: true,
				useUtilFunc: true,
				useUrl: true,
				url: 'JQGetListProductMainUpc&productId=${product.productId?if_exists}',
				groupable: false,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				selectionmode:'singlerow',
				virtualmode: true,
			};
			upcGRID = new OlbGrid($("#we_mainupc_upcgrid"), null, configGridUPC, []);
		};
		var initEvent = function(){
			$("#we_mainupc_alterSave").on("click", function(){
				var listData = upcGRID.getAllRowData();
				if (listData && listData.length <= 0) {
					jOlbUtil.alert.error("${StringUtil.wrapString(uiLabelMap.BSDataIsEmpty)}");
					return false;
				} else {
					var dataMap = {
						productId: '${product.productId?if_exists}',
						upcList: JSON.stringify(listData)
					};
					$.ajax({
						type: 'POST',
						url: 'updateProductMainUPC',
						data: dataMap,
						beforeSend: function(){
							$("#loader_page_common").show();
						},
						success: function(data){
							jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
						        	$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'error'});
						        	$("#jqxNotification").html(errorMessage);
						        	$("#jqxNotification").jqxNotification("open");
									
						        	return false;
								}, function(){
									$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
						        	$("#jqxNotification").jqxNotification("open");
						        	if (data.productId != undefined && data.productId != null) {
						        		var upcListResult = data.upcListResult;
						        		var txtBarcodeIds = "";
						        		if (upcListResult) {
						        			for (var i = 0; i < upcListResult.length; i++) {
						        				var upcItem = upcListResult[i];
						        				if (upcItem && upcItem.uomId == "${product.quantityUomId?if_exists}") {
						        					if (txtBarcodeIds.length > 0) {
						        						txtBarcodeIds += ", ";
						        					}
						        					if (upcItem.iupprm == 1) {
						        						txtBarcodeIds += "<span class='red'>";
						        					}
						        					txtBarcodeIds += upcItem.idValue;
						        					if (upcItem.iupprm == 1) {
						        						txtBarcodeIds += "</span>";
						        					}
						        				}
						        			}
						        		}
						        		$("#txtBarcodeIds").html(txtBarcodeIds);
						        		
						        		closeWindowEditMainUpc();
						        		upcGRID.updateBoundData();
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
				}
			});
		};
		var editMainUpc = function(){
			openWindowEditMainUpc();
		};
		var openWindowEditMainUpc = function(){
			$('#popupProductEditMainUPC').jqxWindow("open");
		};
		var closeWindowEditMainUpc = function(){
			$('#popupProductEditMainUPC').jqxWindow("close");
		};
		return {
			init: init,
			editMainUpc: editMainUpc
		}
	}());
</script>
