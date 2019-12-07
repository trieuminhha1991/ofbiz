<script type="text/javascript" src="/ecommerceresources/js/backend/configproduct/catalog/addRootCategory.js"></script>

<div id="alterpopupAddRootCategory" style="display:none;">	
	<div>${uiLabelMap.AddProductCategory}</div>
	<div style="overflow-x: hidden;">
		<div class="row-fluid">
	        <div class="span12 no-left-margin">
	        	<div class="span3"><label class="text-right asterisk">${uiLabelMap.DmsCategoryId}</label></div>
	        	<div class="span9"><input type="text" id="txtProductCategoryId" /></div>
	        </div>
	    </div>
	    <div class="row-fluid">
	        <div class="span12 no-left-margin">
	        	<div class="span3"><label class="text-right asterisk">${uiLabelMap.DmsCategoryName}</label></div>
	        	<div class="span9"><input type="text" id="txtCategoryName" /></div>
	        </div>
	    </div>
	    <div class="row-fluid">
	        <div class="span12 no-left-margin">
	        	<div class="span3"><label class="text-right">${uiLabelMap.DmsDescription}&nbsp;&nbsp;&nbsp;</label></div>
	        	<div class="span9"><textarea id="tarDescription" style="margin: 10px 0px; width: 418px; height: 176px;"></textarea></div>
	        </div>
	    </div>
	    <div class="form-action">
			<div class="row-fluid">
				<div class="span12 margin-top10">
					<button id="cancelAddRootCategory" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveRootCategory" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<#assign allCategories = delegator.findList("ProductCategory", null, null, null, null, false) />

<script>
var listCategoryIds = [<#if allCategories?exists><#list allCategories as item>
"${item.productCategoryId?if_exists}".toLowerCase(),
</#list></#if>];
</script>
