<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript">
	
	var returnId = '${parameters.returnId?if_exists}';
	var listStatusItemReturnHeader = [<#list listStatusItemReturnHeader as listStatus>{
			statusId: "${listStatus.statusId?if_exists}",
			description: "${StringUtil.wrapString(listStatus.get("description", locale)?if_exists)}",
		},</#list>];
	
	var partyOrders = [<#list partyCompleteOrder as item>{
			orderId: "${item.orderId?if_exists}",
			orderDate: "${item.orderDate?if_exists}"
		},</#list>];
	
	var returnItems = [
		<#list returnItems as returnItem>
		<#assign productGe = delegator.findOne("Product",{"productId" : "${returnItem.productId}"},false) !>
		{
			returnId: "${returnItem.returnId?if_exists}",
			returnItemSeqId: "${returnItem.returnItemSeqId?if_exists}",
			orderId: "${returnItem.orderId?if_exists}",
			productId: "${returnItem.productId?if_exists}",
			productCode: "${productGe.productCode?if_exists}",
			productName: "${productGe.productName?if_exists}",
			requireAmount: "${productGe.requireAmount?if_exists}",
			description: "${returnItem.description?if_exists}",
			returnQuantity: "${returnItem.returnQuantity?if_exists}",
			returnAmount: "${returnItem.returnAmount?if_exists}",
			quantityUomId: "${returnItem.quantityUomId?if_exists}",
			weightUomId: "${returnItem.weightUomId?if_exists}",
			returnPrice: "${returnItem.returnPrice?if_exists}",
			returnReasonId: "${returnItem.returnReasonId?if_exists}",
			returnTypeId: "${returnItem.returnTypeId?if_exists}",
			expectedItemStatus: "${returnItem.expectedItemStatus?if_exists}"
		},
		</#list>
	];
	
	var listReturnReasons = [<#list listReturnReasons as returnReason>{
		returnReasonId: "${returnReason.returnReasonId?if_exists}",
		description: "${StringUtil.wrapString(returnReason.get("description",locale)?if_exists)}"
	},</#list>];
	
	<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "PRODUCT_PACKING")), null, null, null, false) />
	var quantityUomData = [
	   	<#if quantityUoms?exists>
	   		<#list quantityUoms as item>
	   			{
	   				uomId: "${item.uomId?if_exists}",
	   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
	   			},
	   		</#list>
	   	</#if>
	];
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "WEIGHT_MEASURE")), null, null, null, false) />
	var weightUomData = [
	   	<#if weightUoms?exists>
	   		<#list weightUoms as item>
	   			{
	   				uomId: "${item.uomId?if_exists}",
	   				description: "${StringUtil.wrapString(item.get('abbreviation', locale)?if_exists)}"
	   			},
	   		</#list>
	   	</#if>
	];
	
	function getUomDescription(uomId) {
	 	for (x in quantityUomData) {
	 		if (quantityUomData[x].uomId == uomId) {
	 			return quantityUomData[x].description;
	 		}
	 	}
	 	for (x in weightUomData) {
	 		if (weightUomData[x].uomId == uomId) {
	 			return weightUomData[x].description;
	 		}
	 	}
 	}
 	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${uiLabelMap.BSClickToChoose}";
	uiLabelMap.BSValueIsNotEmptyK = "${uiLabelMap.BSValueIsNotEmptyK}";
	uiLabelMap.BPOAreYouSureYouWantCreate = "${uiLabelMap.BPOAreYouSureYouWantCreate}";
	uiLabelMap.BPOPleaseSelectSupplier = "${uiLabelMap.BPOPleaseSelectSupplier}";
	uiLabelMap.BPOPleaseSelectProduct = "${uiLabelMap.BPOPleaseSelectProduct}";
	uiLabelMap.BSYouNotYetChooseProduct = "${uiLabelMap.BSYouNotYetChooseProduct}";
	uiLabelMap.BSAreYouSureYouWantToCreate = "${uiLabelMap.BSAreYouSureYouWantToCreate}";
	uiLabelMap.BPOPleaseSelectProductQuantity = "${uiLabelMap.BPOPleaseSelectProductQuantity}";
	uiLabelMap.BPORestrictMOQ = "${uiLabelMap.BPORestrictMOQ}";
	uiLabelMap.BPOSequenceId = "${uiLabelMap.BPOSequenceId}";
	uiLabelMap.BPOContactMechId = "${uiLabelMap.BPOContactMechId}";
	uiLabelMap.BPOReceiveName = "${uiLabelMap.BPOReceiveName}";
	uiLabelMap.BPOOtherInfo = "${uiLabelMap.BPOOtherInfo}";
	uiLabelMap.BPOAddress1 = "${uiLabelMap.BPOAddress1}";
	uiLabelMap.BPOCity = "${uiLabelMap.BPOCity}";
	uiLabelMap.wgok = "${uiLabelMap.wgok}";
	uiLabelMap.wgcancel = "${uiLabelMap.wgcancel}";
	uiLabelMap.wgupdatesuccess = "${uiLabelMap.wgupdatesuccess}";
	uiLabelMap.validRequiredValueGreatherOrEqualToDay = "${uiLabelMap.validRequiredValueGreatherOrEqualToDay}";
	uiLabelMap.POOrderId = "${uiLabelMap.POOrderId}";
	uiLabelMap.POOrderDate = "${uiLabelMap.POOrderDate}";
	uiLabelMap.BPOProductCanNotReturn = "${uiLabelMap.BPOProductCanNotReturn}";
	uiLabelMap.bootBoxReturnConfirmDeleteItem = "${uiLabelMap.bootBoxReturnConfirmDeleteItem}";
	uiLabelMap.SaveSucess = "${uiLabelMap.SaveSucess}";
	uiLabelMap.POReturnReason = "${uiLabelMap.POReturnReason}";
	uiLabelMap.unitPrice = "${uiLabelMap.unitPrice}";
	uiLabelMap.POReturnQuantity = "${uiLabelMap.POReturnQuantity}";
	uiLabelMap.DmsProduct = "${uiLabelMap.DmsProduct}";
	uiLabelMap.POProductId = "${uiLabelMap.POProductId}";
	uiLabelMap.orderedQuantityLabel = "${uiLabelMap.orderedQuantityLabel}";
	uiLabelMap.BPOQuantity = "${uiLabelMap.BPOQuantity}";
	uiLabelMap.bootBoxReturnConfirm = "${uiLabelMap.bootBoxReturnConfirm}";
	uiLabelMap.BPOQuantiyMustBeSmallerThanReturnableQuantity = "${uiLabelMap.BPOQuantiyMustBeSmallerThanReturnableQuantity}";
	uiLabelMap.Unit = "${uiLabelMap.Unit}";

</script>
<style>
	#orderItemForReturnorderIdfilterwidget .jqx-input-olbius {
		margin: 2px 0px 0px 4px !important;
	}
	#orderItemForReturnproductCodefilterwidget .jqx-input-olbius {
		margin: 2px 0px 0px 4px !important;
	}
	#orderItemForReturnitemDescriptionfilterwidget .jqx-input-olbius {
		margin: 2px 0px 0px 4px !important;
	}
	#columntableorderItemForReturn .jqx-checkbox-default {
		display: none;
	}
</style>