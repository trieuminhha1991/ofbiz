<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script type="text/javascript" src="/poresources/js/product/ProductDetail.js"></script>
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
<#assign productFeatureTypes = Static["com.olbius.basepo.product.ProductUtils"].getProductFeatureTypes(delegator) />

<div class="form-horizontal form-window-content-custom label-text-left content-description">
	<div class="row-fluid">
		<div class="span12 no-left-margin boder-all-profile">
			<span class="text-header">${uiLabelMap.ProductType}</span>
			<span id="productStatus" class="pull-right label label-info label-large arrowed-in-right arrowed"></span>
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.ProductType}:</label></div>
						<div class="span7"><span id="txtProductType"></span></div>
					</div>
				</div>
				<div class="span6">
					<div id="dpcolor" class="row-fluid margin-top10 hide">
						<div class="span5"><label class="text-right">${uiLabelMap.DmsDisplayColor}:</label></div>
						<div class="span7"><div id="DisplayColor"></div></div>
					</div>
				</div>
			</div>
			<div class="row-fluid feature-container hide">
				<#if productFeatureTypes?exists>
					<#list productFeatureTypes as feature>
						<#if feature_index % 2 == 0>
						<div class="row-fluid">
						</#if>
						<div class="span6">
							<div class="row-fluid margin-top10">
								<div class="span5"><label class="text-right">${StringUtil.wrapString(feature.get("description", locale))}&nbsp;&nbsp;&nbsp;</label></div>
								<div class="span7"><div id="txt${feature.productFeatureTypeId?if_exists}"></div></div>
							</div>
						</div>
						
						<#if feature_index % 2 != 0>
						</div>
						</#if>
					</#list>
				</#if>
			</div>
			
		</div>
	</div>
	
	<div class="row-fluid">
		<div class="span12 no-left-margin boder-all-profile">
			<span class="text-header">${uiLabelMap.DmsGeneralInformation}</span>
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.ProductProductId}:</label></div>
						<div class="span7"><span id="txtProductId"></span></div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.ProductInternalName}:</label></div>
						<div class="span7"><span id="txtInternalName"></span></div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.DmsProductCatalogs}:</label></div>
						<div class="span7"><span id="txtProductCategoryId"></span></div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.DmsProductTaxCatalogs}:</label></div>
						<div class="span7"><span id="txtTaxCatalogs"></span></div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.ProductBrandName}:</label></div>
						<div class="span7"><span id="txtBrandName"></span></div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.ProductProductName}:</label></div>
						<div class="span7"><span id="txtProductName"></span></div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.BSPrimaryProductCategory}:</label></div>
						<div class="span7"><span id="txtPrimaryProductCategoryId"></span></div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.DmsDescription}:</label></div>
						<div class="span7"><span id="description1"></span></div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="row-fluid">
		<div class="span12 no-left-margin boder-all-profile">
			<span class="text-header">${uiLabelMap.DmsDetailInformation}</span>
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.DmsProductWeight}:</label></div>
						<div class="span7"><span id="txtWeight"></span></div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.DmsProductWeightUomId}:</label></div>
						<div class="span7"><span id="txtWeightUomId"></span></div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.DACurrencyUomId}:</label></div>
						<div class="span7"><span id="txtCurrencyUomId"></span></div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.DmsProductDefaultPrice}:</label></div>
						<div class="span7"><span id="txtProductDefaultPrice"></span></div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.DmsNetWeight}:</label></div>
						<div class="span7"><span id="txtProductWeight"></span></div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.DmsQuantityUomId}:</label></div>
						<div class="span7"><span id="txtQuantityUomId"></span></div>
					</div>
					<div class="row-fluid margin-top10 hide">
						<div class="span5"><label class="text-right">${uiLabelMap.TaxInPrice}:</label></div>
						<div class="span7"><span id="txtTaxInPrice"></span></div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.DmsProductListPrice}:</label></div>
						<div class="span7"><span id="txtProductListPrice"></span></div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<#if hasOlbPermission("MODULE", "PRODPRICE_VIEW", "")>
	<div class="row-fluid">
		<div class="span12 no-left-margin boder-all-profile">
			<span class="text-header">${uiLabelMap.ListProductPrice}</span>
			<#assign UpdateMode = "false" />
			<#include "component://basesales/webapp/basesales/product/listPricesOfProduct.ftl"/>
		</div>
	</div>
	<#else>
	<#assign currencyUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false) />
	<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
	<script>
	var currencyUoms = [<#if currencyUoms?exists><#list currencyUoms as item>{
		uomId: "${item.uomId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var mapCurrencyUom = {<#if currencyUoms?exists><#list currencyUoms as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale))}",
	</#list></#if>};
	var quantityUoms = [<#if quantityUoms?exists><#list quantityUoms as item>{
		uomId: "${item.uomId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var mapQuantityUom = {<#if quantityUoms?exists><#list quantityUoms as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale))}",
	</#list></#if>};
	</script>
	</#if>
	
	<div class="row-fluid">
		<div class="span12 no-left-margin boder-all-profile">
			<span class="text-header">${uiLabelMap.DmsSupplier}</span>
			<#assign UpdateSupplier = "false" />
			<#assign showtoolbarSupplier = "false" />
			<#include "listSupplier.ftl"/>
		</div>
	</div>
</div>

<#assign listProductType = delegator.findList("ProductType", null, null, null, null, false) />
<#assign listProductFeature = delegator.findList("ProductFeature", null, null, null, null, false) />
<#assign listProductCategories = delegator.findList("ProductCategory", null, null, null, null, false) />
<#assign listUom = delegator.findList("Uom", null, null, null, null, false) />
<script>
	var mapUom = {<#list listUom as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list>};
	var mapProductType = {<#list listProductType as item>
		"${item.productTypeId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list>};
	var mapProductFeature = {<#if listProductFeature?exists><#list listProductFeature as item>
		"${item.productFeatureId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
	var mapProductCategory = {<#if listProductCategories?exists><#list listProductCategories as item>
		"${item.productCategoryId?if_exists}": "${StringUtil.wrapString(item.categoryName?if_exists)}",
	</#list></#if>};
	
	var productFeatureTypes = [<#if productFeatureTypes?exists><#list productFeatureTypes as item>
			"${item.productFeatureTypeId?if_exists}",
	</#list></#if>];
	
	var DmsIsVirtual = "${StringUtil.wrapString(uiLabelMap.DmsIsVirtual)}";
	var DmsIsVariant = "${StringUtil.wrapString(uiLabelMap.DmsIsVariant)}";
	var BSSaleProducts = "${StringUtil.wrapString(uiLabelMap.BSSaleProducts)}";
<#if parameters.productId?exists>
	var productIdParam = "${parameters.productId?if_exists}";
	$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.BSProductDetail)}");
	$('[data-rel=tooltip]').tooltip();
</#if>

</script>