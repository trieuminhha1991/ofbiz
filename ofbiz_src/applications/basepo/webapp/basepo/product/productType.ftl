<script type="text/javascript" src="/poresources/js/product/productType.js"></script>

<style>
	#addProductColor {
		margin-left: 225px;
		margin-top: -27px;
		position: absolute;
	}
	#selectColor {
		bottom: 13px;right: 5px;
		position: absolute;
		z-index: 99999;
	}
	#displayColor {
		width: 218px;
		height: 30px;
		border: 1px solid #ccc;
	}
	.add-feature {
		margin-left: -5px;
		margin-top: 0px;
		position: absolute;
	}
	hr {
		margin: 5px 0;
	}
</style>
<#assign productFeatureTypes = Static["com.olbius.basepo.product.ProductUtils"].getProductFeatureTypes(delegator) />

<div class="row-fluid">
	<div class="span6"></div>
	<div class="span3">
		<div id="colorPicker" style="position: fixed;z-index: 99998;">
			<div id="jqxDisplayColor"></div>
			<button id="selectColor" onlick="closePicker()">Ok</button>
		</div>
	</div>
</div>
<div class="row-fluid">
	<div class="span3"><label class="text-right asterisk">${uiLabelMap.ProductType}</label></div>
	<div class="span3"><div id="txtProductType"></div></div>
</div>
<hr/>
<div class="row-fluid margin-top20">
	<div class="span3"><label class="text-right">${uiLabelMap.DmsIsVirtual}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span1"><div id="IsVirtual" style="margin-top: 6px;"></div></div>
	<div class="span2"><label class="text-right">${uiLabelMap.DmsIsVariant}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span1"><div id="IsVariant" style="margin-top: 6px;"></div></div>
	<div class="span5">
		<div class="row-fluid variant hidden">
			<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsVariantFromProduct}</label></div>
			<div class="span7"><div id="divVariantFromProduct"></div></div>
		</div>
		<div class="row-fluid virtual hidden">
			<div class="span5"><label class="text-right">${uiLabelMap.DmsDisplayColor}&nbsp;&nbsp;&nbsp;</label></div>
			<div class="span7"><div id="displayColor"></div></div>
		</div>
	</div>
</div>
<hr/>
<div class="row-fluid feature hidden">
<h4>${uiLabelMap.DmsProductFeature}</h4>
	<#if productFeatureTypes?exists>
		<#list productFeatureTypes as feature>
			<#if feature_index % 2 == 0>
			<div class="row-fluid">
			</#if>
			
			<div class="span3"><label class="text-right">${StringUtil.wrapString(feature.get("description", locale))}&nbsp;&nbsp;&nbsp;</label></div>
			<div class="span3"><div id="txt${feature.productFeatureTypeId?if_exists}"></div></div>
			
			<li id="addProductTaste" class="green icon-plus hidden add-feature" title="${uiLabelMap.DmsAddFeatureProduct}" data-featuretype="${feature.productFeatureTypeId?if_exists}"></li>
			
			<#if feature_index % 2 != 0>
			</div>
			</#if>
		</#list>
	</#if>
</div>

<#include "component://basepo/webapp/basepo/product/popup/addProductFeature.ftl"/>

<script>
	var mapProductFeatureType = {<#if productFeatureTypes?exists><#list productFeatureTypes as item>
		"${item.productFeatureTypeId?if_exists}": "${StringUtil.wrapString(item.get("description", locale))}",
	</#list></#if>};
	var productFeatureTypes = [<#if productFeatureTypes?exists><#list productFeatureTypes as item>
		"${item.productFeatureTypeId?if_exists}",
	</#list></#if>];
</script>