<#if hasOlbPermission("MODULE", "PRODUCTPO", "CREATE")>
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" src="/crmresources/js/progressing.js"></script>
<script type="text/javascript" src="/crmresources/js/notify.js"></script>
<script type="text/javascript" src="/crmresources/js/Underscore1.8.3.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollview.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcolorpicker.js"></script>
<script type="text/javascript" src="/poresources/js/product/addNewProduct.js"></script>
<script type="text/javascript" src="/crmresources/js/DataAccess.js"></script>
<style>
	label {
		margin-top: 4px;
	}
	.row-fluid {
		min-height: 40px;
	}
	.boder-all-profile {
		border: 1px solid #ccc;
	}
</style>

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>

<div class="row-fluid">
	<div id="step1" class="span12 no-left-margin boder-all-profile">
		<span class="text-header">${uiLabelMap.ProductType}</span>
		<#include "productType.ftl"/>
	</div>
	<div id="step2" class="span12 no-left-margin boder-all-profile">
		<span class="text-header">${uiLabelMap.DmsGeneralInformation}</span>
		<#include "generalInfo.ftl"/>
	</div>
	<div id="step3" class="span12 no-left-margin boder-all-profile">
		<span class="text-header">${uiLabelMap.DmsDetailInformation}</span>
		<#include "detailsInfo.ftl"/>
	</div>
	<#if hasOlbPermission("MODULE", "SUP_PRODUCT_VIEW", "VIEW")>
		<div id="step4" class="span12 no-left-margin boder-all-profile">
			<span class="text-header">${uiLabelMap.DmsSupplier}</span>
			<#include "listSupplier.ftl"/>
		</div>
	</#if>
	<div class="row-fluid">
		<div class="span12 margin-top10">
			<button id="btnSaveAndContinue" class="btn btn-success form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.BSCreateAndContinue}</button>
			<button id="btnSave" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<#assign prodCatalogs = delegator.findList("ProdCatalog", null, null, null, null, false) />
<#assign listProductCategories = delegator.findList("ProductCategory", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productCategoryTypeId", "CATALOG_CATEGORY"), null, null, null, false) />
<#assign listTaxCategory = delegator.findList("ProductCategory", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productCategoryTypeId", "TAX_CATEGORY"), null, null, null, false) />
<#assign listQuantityUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
<#assign listWeightUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false) />
<#assign listCurrencyUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false) />
<#assign listProductVirtual = delegator.findList("Product", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("isVirtual", "Y"), null, null, null, false) />
<#assign listPartyRelationship = delegator.findList("ListPartySupplierByRole", null, null, null, null, false) />
<#assign listProductType = delegator.findList("ProductType", null, null, null, null, false) />
<#assign listUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
<script>
	var listUoms = [<#if listUoms?exists><#list listUoms as item>{
		uomId: "${item.uomId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
	},</#list></#if>];

	var listProductType = [<#list listProductType as item>{
		productTypeId: "${item.productTypeId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
	},</#list>];

	var partyRelationshipData = [<#list listPartyRelationship as partyRelationship>{
		partyId: "${partyRelationship.partyId?if_exists}",
		groupName: "${partyRelationship.groupName?if_exists}"
	},</#list>];

	var canDropShipData = [{id:"Y", description:"${uiLabelMap.CommonYes}"}, {id:"N", description:"${uiLabelMap.CommonNo}"}];

	var listProductVirtual = [<#if listProductVirtual?exists><#list listProductVirtual as item>{
		productId: "${item.productId?if_exists}",
		productName: "${StringUtil.wrapString(item.productName?if_exists)}"
	},</#list></#if>];

	var listProductCategorys = [<#if listProductCategories?exists><#list listProductCategories as item>
		"${item.productCategoryId?if_exists}".toLowerCase(),
	</#list></#if>];

	var listTaxCategory = [<#if listTaxCategory?exists><#list listTaxCategory as item>{
		productCategoryId: "${item.productCategoryId?if_exists}",
		categoryName: "${StringUtil.wrapString(item.categoryName?if_exists)}"
	},</#list></#if>];

	var mapProductCategory = {<#if listProductCategories?exists><#list listProductCategories as item>
		"${item.productCategoryId?if_exists}": "${StringUtil.wrapString(item.categoryName?if_exists)}",
	</#list></#if>};

	var mapTaxCategory = {<#if listTaxCategory?exists><#list listTaxCategory as item>
		"${item.productCategoryId?if_exists}": "${StringUtil.wrapString(item.categoryName?if_exists)}",
	</#list></#if>};

	var listQuantityUom = [<#if listQuantityUom?exists><#list listQuantityUom as item>{
		uomId: "${item.uomId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
	},</#list></#if>];

	var mapQuantityUom = {<#if listQuantityUom?exists><#list listQuantityUom as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};

	var listCurrencyUom = [<#if listCurrencyUom?exists><#list listCurrencyUom as item>{
		uomId: "${item.uomId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
	},</#list></#if>];

	var mapCurrencyUom = {<#if listCurrencyUom?exists><#list listCurrencyUom as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};

	var listWeightUom = [<#if listWeightUom?exists><#list listWeightUom as item>{
		uomId: "${item.uomId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
	},</#list></#if>];

	var mapWeightUom = {<#if listWeightUom?exists><#list listWeightUom as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};

	var productListPrice = {<#if productListPrice?exists>
		currencyUomId: "${productListPrice.currencyUomId}",
		price: "${productListPrice.price}",
		taxInPrice: "${productListPrice.taxInPrice}"
	</#if>};

	var productDefaultPrice = {<#if productDefaultPrice?exists>
		currencyUomId: "${productDefaultPrice.currencyUomId}",
		price: "${productDefaultPrice.price}",
		taxInPrice: "${productDefaultPrice.taxInPrice}"
	</#if>};

var prodCatalogs = [<#if prodCatalogs?exists><#list prodCatalogs as item>{
			prodCatalogId: "${item.prodCatalogId?if_exists}",
			catalogName: "${StringUtil.wrapString(item.catalogName?if_exists)}"
		},</#list></#if>];

var updateMode = false;
var productIdParameters = "${(parameters.productId)?if_exists}";
var productIdOrg = "${(parameters.productIdOrg)?if_exists}";
var filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";

<#if parameters.productId?exists>
$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.DmsEditProduct)}");
<#--$($(".widget-header").find("h4")).html("${StringUtil.wrapString(uiLabelMap.DmsEditProduct)}");-->
</#if>
</script>
<#else>
	<h2> ${uiLabelMap.DmsNotPermission}</h2>
</#if>