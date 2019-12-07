<script type="text/javascript" src="/ecommerceresources/js/backend/configproduct/catalog/addCategory.js"></script>

<div id="alterpopupWindow" style="display:none;">	
	<div>${uiLabelMap.AddProductCategory}</div>
	<div style="overflow-x: hidden;">
		<div class="row-fluid">
        	<div class="span3"><label class="text-right asterisk">${uiLabelMap.DmsCategoryId}</label></div>
        	<div class="span9"><input type="text" id="txtProductCategoryId" /></div>
	    </div>
	    <div class="row-fluid">
        	<div class="span3"><label class="text-right asterisk">${uiLabelMap.DmsCategoryName}</label></div>
        	<div class="span9"><input type="text" id="txtCategoryName" /></div>
	    </div>
	    <div class="row-fluid margin-bottom10">
		    <div class="span3"><label class="text-right asterisk">${uiLabelMap.BERootCategory}</label></div>
		    <div class="span9"><div id="txtRootCategory"></div></div>
	    </div>
	    <div class="row-fluid margin-bottom10">
		    <div class="span3"><label class="text-right">${uiLabelMap.DmsPrimaryParentCategoryId}&nbsp;&nbsp;&nbsp;</label></div>
		    <div class="span9"><div id="txtPrimaryParentCategoryId"></div></div>
	    </div>
	    <div class="row-fluid">
		    <div class="span3"><label class="text-right">${uiLabelMap.BSSequenceNumber}&nbsp;&nbsp;&nbsp;</label></div>
		    <div class="span9"><input type="number" id="txtSequenceNumber" /></div>
	    </div>
	    <div class="row-fluid">
        	<div class="span3"><label class="text-right">${uiLabelMap.DmsDescription}&nbsp;&nbsp;&nbsp;</label></div>
        	<div class="span9"><textarea id="tarDescription" style="margin: 10px 0px; width: 418px; height: 130px;"></textarea></div>
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

<script>
	<#if security.hasEntityPermission("ECOMMERCE", "_CREATE", session)>
		var urlCreate = "createProductCategoryWithWebSite";
		<#else>
		var urlCreate = "createProductCategoryAjax";
	</#if>
</script>