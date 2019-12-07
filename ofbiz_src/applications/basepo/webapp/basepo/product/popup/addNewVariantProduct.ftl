<script type="text/javascript" src="/poresources/js/product/addNewProductVariant.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<style>
	label {
		margin-top: 4px;
	}
	.row-fluid {
		min-height: 40px;
	}
	.add-feature {
		margin-left: -10px;
		margin-top: 0px;
		position: absolute;
	}
</style>

<div id="addProductVariant" style="display:none;">
	<div>${uiLabelMap.DmsAddNewProductVariant}</div>
	<div>
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="text-right">${uiLabelMap.DmsVariantFromProduct}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span7"><label id="txtProductIdFrom" class="green"></label></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="text-right asterisk">${uiLabelMap.ProductProductName}</label></div>
					<div class="span7"><input type="text" id="txtProductName" tabindex="5"/></div>
				</div>
			</div>
			<div class="span6">
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="text-right asterisk">${uiLabelMap.ProductProductId}</label></div>
					<div class="span7"><input type="text" id="txtProductCode" tabindex="4"/></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="text-right asterisk">${uiLabelMap.ProductInternalName}</label></div>
					<div class="span7"><input type="text" id="txtInternalName" tabindex="6"/></div>
				</div>
			</div>
		</div>
		
		<div class="row-fluid">
			<#if productFeatureTypes?exists>
				<#list productFeatureTypes as feature>
					<#if feature_index % 2 == 0>
					<div class="row-fluid">
					<div class="span6">
					<#else>
					<div class="span6">
					</#if>
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right">${StringUtil.wrapString(feature.get("description", locale))}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span7"><div id="txt${feature.productFeatureTypeId?if_exists}"></div></div>
							<li id="addProductTaste" class="green icon-plus add-feature" title="${uiLabelMap.DmsAddFeatureProduct}" data-featuretype="${feature.productFeatureTypeId?if_exists}"></li>
						</div>
					</div>
					
					<#if feature_index % 2 != 0>
					</div>
					</#if>
				</#list>
			</#if>
		</div>
		
		<div class="row-fluid margin-top10" style="margin-left: 38px;width: 95%;">
			<div class="span2"><label class="text-right">${uiLabelMap.DmsDescription}&nbsp;</label></div>
			<div class="span10"><textarea id="txtDescription" tabindex="7"></textarea></div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span12">
				<button id="btnCancelProductVariant" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="btnSaveProductVariant" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<#include "component://basepo/webapp/basepo/product/popup/addProductFeature.ftl"/>