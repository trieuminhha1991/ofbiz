<script type="text/javascript">
	<#assign reasons = delegator.findList("ReturnReason", null, null, null, null, false)>
	var returnReasonData = [];
	<#list reasons as item>
		var row = {};
		row['returnReasonId'] = '${item.returnReasonId}';
		row['description'] = '${StringUtil.wrapString(item.get("description", locale)?if_exists)}';
		returnReasonData.push(row);
	</#list>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.SequenceId = '${StringUtil.wrapString(uiLabelMap.SequenceId)}';
	uiLabelMap.OrderId = '${StringUtil.wrapString(uiLabelMap.OrderId)}';
	uiLabelMap.ProductId = '${StringUtil.wrapString(uiLabelMap.ProductId)}';
	uiLabelMap.ProductName = '${StringUtil.wrapString(uiLabelMap.ProductName)}';
	uiLabelMap.OrderNumber = '${StringUtil.wrapString(uiLabelMap.OrderNumber)}';
	uiLabelMap.POReturnQuantity = '${StringUtil.wrapString(uiLabelMap.POReturnQuantity)}';
	uiLabelMap.Quantity = '${StringUtil.wrapString(uiLabelMap.Quantity)}';
	uiLabelMap.BPOQuantiyMustBeSmallerThanReturnableQuantity = '${StringUtil.wrapString(uiLabelMap.BPOQuantiyMustBeSmallerThanReturnableQuantity)}';
	uiLabelMap.UnitPrice = '${StringUtil.wrapString(uiLabelMap.UnitPrice)}';
	uiLabelMap.Reason = '${StringUtil.wrapString(uiLabelMap.Reason)}';
	uiLabelMap.PleaseSelectTitle = '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}';
	uiLabelMap.Unit = '${StringUtil.wrapString(uiLabelMap.Unit)}';
	uiLabelMap.ReturnPriceCannotExceedPurchasePrice = '${StringUtil.wrapString(uiLabelMap.ReturnPriceCannotExceedPurchasePrice)}';
</script>
<div class="row-fluid">
	<div class="span12">
		<div id="jqxgridProduct"></div>
	</div>
</div>

<div id="addProductPopup" class="hide popup-bound">
 	<div>${uiLabelMap.BLAddProducts}</div>
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

<script type="text/javascript" src="/poresources/js/returnSupplier/supReturnEditSupReturnProduct.js?v=001"></script>