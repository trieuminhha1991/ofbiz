<script type="text/javascript" src="/poresources/js/product/generalInfo.js"></script>

<style>
	#addCatagory {
		margin-left: 225px;
		margin-top: -34px;
		position: absolute;
	}
	.jqx-checkbox.jqx-checkbox-olbius {
		margin-left: -3px !important;
	}
</style>

<div class="row-fluid margin-top10">
	<div class="span3"><label class="text-right asterisk">${uiLabelMap.ProductProductId}</label></div>
	<div class="span3"><input type="text" name="txtProductId" id="txtProductId" class="no-space" tabindex="2"/></div>
	<div class="span3"><label class="text-right">${uiLabelMap.ProductBrandName}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span3"><div id="txtBrandName" tabindex="3"></div></div>
</div>
<div class="row-fluid margin-top10">
	<div class="span3"><label class="text-right asterisk">${uiLabelMap.ProductInternalName}</label></div>
	<div class="span3"><input type="text" name="txtInternalName" id="txtInternalName" tabindex="4"/></div>
	<div class="span3"><label class="text-right asterisk">${uiLabelMap.ProductProductName}</label></div>
	<div class="span3"><input type="text" name="txtProductName" id="txtProductName" tabindex="5"/></div>
</div>
<div class="row-fluid margin-top10">
	<div class="span3"><label class="text-right">${uiLabelMap.DmsCatalog}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span3"><div id="txtCatalog" tabindex="4"></div></div>
	<div class="span3"><label class="text-right">${uiLabelMap.DmsProductCatalogs}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span3">
		<div id="txtProductCategoryId" tabindex="6"></div>
		<li id="addCatagory" class="hide green icon-plus" title="${uiLabelMap.AddProductCategory}"></li>
	</div>
</div>
<div class="row-fluid margin-top10">
	<div class="span3"><label class="text-right">${uiLabelMap.BSPrimaryProductCategory}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span3"><div id="txtPrimaryProductCategoryId" tabindex="7"></div></div>
	<div class="span3"><label class="text-right asterisk">${uiLabelMap.DmsProductTaxCatalogs}</label></div>
	<div class="span3"><div id="txtTaxCatalogs" tabindex="8"></div></div>
</div>
<div class="row-fluid margin-top20">
	<div class="span3"><label class="text-right">${uiLabelMap.DmsDescription}&nbsp;&nbsp;&nbsp;</label></div>
	<div class="span9"><textarea id="description1" tabindex="9"></textarea></div>
</div>
		
<div id="alterpopupWindow" style="display:none;">	
	<div>${uiLabelMap.AddProductCategory}</div>
	<div style="overflow-x: hidden;">
		<div class="row-fluid">
	        <div class="span12 no-left-margin">
	        	<div class="span3" style="text-align: right;margin-top: 8px;">${uiLabelMap.DmsCategoryId}<span style="color:red;"> *</span></div>
	        	<div class="span9"><input type="text" id="txtProductCategoryIdAdd" /></div>
	        </div>
	    </div>
	    <div class="row-fluid">
	        <div class="span12 no-left-margin">
	        	<div class="span3" style="text-align: right;margin-top: 8px;">${uiLabelMap.DmsCategoryName}<span style="color:red;"> *</span></div>
	        	<div class="span9"><input type="text" id="txtCategoryNameAdd" /></div>
	        </div>
	    </div>
	    <div class="row-fluid">
	        <div class="span12 no-left-margin">
	        	<div class="span3" style="text-align: right;margin-top: 8px;">${uiLabelMap.DmsDescription}&nbsp;&nbsp;&nbsp;</div>
	        	<div class="span9"><textarea id="tarDescriptionAdd"></textarea></div>
	        </div>
	    </div>
	    <div class="form-action">
			<div class="row-fluid">
				<div class="span12 margin-top10">
					<button id="alterCancel" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
					<button id="alterSave" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>