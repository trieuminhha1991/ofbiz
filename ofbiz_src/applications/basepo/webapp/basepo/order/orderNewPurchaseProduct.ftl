<link href="/poresources/css/order/productSupplierForPurchase.css" type="text/css" rel="stylesheet"/>
<script>

<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
var quantityUomData = [];
<#list uoms as item>
	var row = {};
	<#assign descPackingUom = StringUtil.wrapString(item.description?if_exists)/>
	row['quantityUomId'] = "${item.uomId?if_exists}";
	row['description'] = "${descPackingUom?if_exists}";
	quantityUomData.push(row);
</#list>

<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
var weightUomData = [];
<#list weightUoms as item>
	var row = {};
	<#assign descWUom = StringUtil.wrapString(item.abbreviation?if_exists)/>
	row['uomId'] = "${item.uomId?if_exists}";
	row['description'] = "${descWUom?if_exists}";
	weightUomData.push(row);
</#list>

var getUomDesc = function (uomId){
	for (var i = 0; i < quantityUomData.length; i ++) {
		if (quantityUomData[i].quantityUomId == uomId) {
			return quantityUomData[i].description;
		}
	}
	for (var i = 0; i < weightUomData.length; i ++) {
		if (weightUomData[i].uomId == uomId) {
			return weightUomData[i].description;
		}
	}
}
</script>
<div class="row-fluid">
<div class="span12">
<div id="jqxgridProduct"></div>
<div id="menuProduct" style="display:none;">
	<ul>
		<li><i class="fa fa-money"></i>${StringUtil.wrapString(uiLabelMap.BPGetBasePrice)}</li>
		<li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BPReset)}</li>
	</ul>
</div>
</div>
</div>
<div id="addProductPopup" class="hide popup-bound">
 	<div>${uiLabelMap.Product}</div>
 	<div class='form-window-container'>
 		<div class='form-window-content'>
 	        <div class="row-fluid">
 	    		<div class="span12">
 	    			<div id="jqxgridProductAdd"></div>
				</div>
 			</div>
 		</div>
 		<div class="form-action popup-footer">
 	        <button id="addProductCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
 	        <button id="addProductSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonAdd}</button>
 		</div>
 	</div>
 </div>
 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.search.remote.js"></script>
<script type="text/javascript" src="/poresources/js/order/orderNewPurchaseProduct.js?v=1.1.5"></script>