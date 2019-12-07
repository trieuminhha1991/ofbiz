<script type="text/javascript" src="/ecommerceresources/js/backend/configproduct/catalog/addCategoryToCatalog.js"></script>

<style>
	.lblProdCatalogId {
	    margin-top: 3px;
	}
</style>

<div id="jqxNotificationCategory">
	<div id="notificationContentCategory"></div>
</div>

<div id="jqxwindowAddCategoryToCatalog" class='hide'>
	<div>${uiLabelMap.DmsAddProductCategoryToProdCatalog}</div>
	<div style="overflow-x: hidden;">
		<div id="containerCategory"></div>
		
		<div class="row-fluid margin-top10">
	        <div class="span12 no-left-margin">
	        	<div class="span4"><label class="text-right">${uiLabelMap.DmsProdCatalogId}&nbsp;&nbsp;&nbsp;</label></div>
	        	<div class="span8"><label class="green lblProdCatalogId"></label></div>
	        </div>
	    </div>
	    
	    <div class="row-fluid">
		    <div class="span12 no-left-margin">
			    <div class="span4"></div>
			    <div class="span8" style="padding: 9px 0px 0px 57px;">
			    	<i class='fa-plus blue'></i><a style="cursor: pointer;" onclick="RootCatagory.open()">${uiLabelMap.BEAddNewRootCategory}</a>
			    </div>
		    </div>
	    </div>
	    
	    <div class="row-fluid">
		    <div class="span12 no-left-margin">
			    <div class="span4"><label class="text-right asterisk">${uiLabelMap.BERootCategory}</label></div>
			    <div class="span8"><div id="txtCategoryId"></div></div>
		    </div>
	    </div>
	    
	    <div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelAddCategoryToCatalog" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveAddCategoryToCatalog" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<#include "addRootCategory.ftl"/>
