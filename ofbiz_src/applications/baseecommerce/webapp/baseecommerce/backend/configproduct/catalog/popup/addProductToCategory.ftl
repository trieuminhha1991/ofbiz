<script type="text/javascript" src="/ecommerceresources/js/backend/configproduct/catalog/addProductToCategory.js"></script>
<script src="/crmresources/js/generalUtils.js"></script>

<div id="jqxwindowAddProductToCategory" class="hide">
	<div>${uiLabelMap.DmsAddProductToCategory}</div>
	<div style="overflow-x: hidden;">
	
		<div class="row-fluid margin-top10">
	        <div class="span12 no-left-margin">
	        	<div class="span4"><label class="text-right">${uiLabelMap.DmsProductCatalogs}&nbsp;&nbsp;&nbsp;</label></div>
	        	<div class="span8"><label id="lblProductCategoryId" style="margin-top: 5px;" class="green"></label></div>
	        </div>
	    </div>
	    
	    <div class="row-fluid margin-top10">
		    <div class="span12 no-left-margin">
			    <div class="span4"><label class="text-right asterisk">${uiLabelMap.DmsProduct}</label></div>
			    <div class="span8">
			    	<div id="txtProductId">
						<div style="border-color: transparent;" id="jqxgridProductId" tabindex="5"></div>
					</div>
		    	</div>
		    </div>
	    </div>
	    
	    <div class="row-fluid margin-top10">
		    <div class="span12 no-left-margin">
			    <div class="span4"><label class="text-right asterisk">${uiLabelMap.DmsSequence}</label></div>
			    <div class="span8"><div id="txtSequenceNum"></div></div>
		    </div>
	    </div>
	    
	    <div class="form-action">
			<div class="row-fluid">
				<div class="span12 margin-top10">
					<button id="cancelAddProductToCategory" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveAddProductToCategory" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>
