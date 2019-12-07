<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<script type="text/javascript" src="/logresources/js/util/StringUtil.js?v=1.0.5"></script>

<script type="text/javascript">
	var listOrderItemData = [];
	var facilitySelected = null;
	var supplierSelected = null;
	<#assign customTimePeriodId = parameters.customTimePeriodId !>
	<#assign productPlanId = parameters.productPlanId !>
	var orderId = <#if orderId?exists>"${orderId}"<#else>null</#if>;
	var productOrderMap = [];
	var customTimePeriodId = null;
	<#if customTimePeriodId?has_content>
 		customTimePeriodId = "${customTimePeriodId?if_exists}";
 	</#if>
	var productPlanId = null;
	<#if productPlanId?has_content>
 		productPlanId = "${productPlanId?if_exists}";
 		<#assign planHeader = delegator.findOne("ProductPlanHeader", false, {"productPlanId", productPlanId?if_exists})!>
 		<#if planHeader?has_content>
 			<#if planHeader.supplierPartyId?has_content>
 				<#assign partyGroup = delegator.findOne("PartyAndPartyGroup", false, {"partyId", planHeader.supplierPartyId?if_exists})!>
				<#if planHeader?has_content>
					supplierSelected = {
 						partyId: "${StringUtil.wrapString(partyGroup.partyId?if_exists)}",
 						partyCode: "${StringUtil.wrapString(partyGroup.partyCode?if_exists)}",
 						groupName: "${StringUtil.wrapString(partyGroup.groupName?if_exists)}",
					};
				</#if>
 			</#if>
 		</#if>
 	</#if>
	
	var currencyUomId = null;

	var facilityData = [<#list listFacility as facility>{
		facilityId: "${facility.facilityId?if_exists}",
		description: "${facility.facilityName?if_exists}",
	},</#list>];
	
	var uomData = [<#list listUom as uom>{
		uomId: "${uom.uomId?if_exists}",
		description: "${StringUtil.wrapString(uom.get("description", locale)?if_exists)}",
	},</#list>];
	
	var uomConfigData = [];
	
	var cellclassname = function (row, column, value, data) {
	var data = $('#jqxgridProduct').jqxGrid('getrowdata',row);
		var productId = data.productId;
		var item = productOrderMap[data.productId];
		var check = false;
		if (orderId && item && item.quantityReceived && item.quantityReceived > 0) {
			check = true;
		}
		if (data['purchaseDiscontinuationDate'] != undefined && data['purchaseDiscontinuationDate'] != null) {
			var now = new Date();
			var ex = new Date(data['purchaseDiscontinuationDate']);
			if (ex <= now){
	        	return 'background-cancel';
	        }
	    } else if (orderId && check){
	    	if (column == 'quantityPurchase' || column == 'itemComment') {
				return 'background-prepare';
	    	}
	    } else {
	    	if (column == 'quantityPurchase' || column == 'lastPrice' || column == 'uomId' || column == 'itemComment') {
				return 'background-prepare';
	    	}
	    }
	}
	
	function changeIconChev(elm){
		if(elm.attr('class') == "icon-chevron-down"){
			elm.attr('class', 'icon-chevron-up');
		} else{
			elm.attr('class', 'icon-chevron-down');
		}
	}
	
	function unescapeHTML(escapedStr) {
     	var div = document.createElement('div');
     	div.innerHTML = escapedStr;
     	var child = div.childNodes[0];
     	return child ? child.nodeValue : '';
 	};
	 
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
	uiLabelMap.BSContactMechId = "${StringUtil.wrapString(uiLabelMap.BSContactMechId)}";
	uiLabelMap.BSReceiverName = "${StringUtil.wrapString(uiLabelMap.BSReceiverName)}";
	uiLabelMap.BSOtherInfo = "${StringUtil.wrapString(uiLabelMap.BSOtherInfo)}";
	uiLabelMap.BSAddress = "${StringUtil.wrapString(uiLabelMap.BSAddress)}";
	uiLabelMap.BSCity = "${StringUtil.wrapString(uiLabelMap.BSCity)}";
	uiLabelMap.BSStateProvince = "${StringUtil.wrapString(uiLabelMap.BSStateProvince)}";
	uiLabelMap.BSCountry = "${StringUtil.wrapString(uiLabelMap.BSCountry)}";
	uiLabelMap.BSCounty = "${StringUtil.wrapString(uiLabelMap.BSCounty)}";
	uiLabelMap.BSWard = "${StringUtil.wrapString(uiLabelMap.BSWard)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.POSupplierId = "${StringUtil.wrapString(uiLabelMap.POSupplierId)}";
	uiLabelMap.POSupplierName = "${StringUtil.wrapString(uiLabelMap.POSupplierName)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.BPFacilityNotHasAddress = "${StringUtil.wrapString(uiLabelMap.BPFacilityNotHasAddress)}";
	uiLabelMap.AreYouSureUpdate = "${StringUtil.wrapString(uiLabelMap.AreYouSureUpdate)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.DmsBootboxCancelPO = "${StringUtil.wrapString(uiLabelMap.DmsBootboxCancelPO)}";
	uiLabelMap.Cancel = "${StringUtil.wrapString(uiLabelMap.Cancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.BPSearchProductToAdd = "${StringUtil.wrapString(uiLabelMap.BPSearchProductToAdd)}";
	uiLabelMap.BPGetBasePrice = "${StringUtil.wrapString(uiLabelMap.BPGetBasePrice)}";
	uiLabelMap.BPReset = "${StringUtil.wrapString(uiLabelMap.BPReset)}";
	uiLabelMap.BPYouWantToCancelProductInOrder = "${StringUtil.wrapString(uiLabelMap.BPYouWantToCancelProductInOrder)}";
	
	uiLabelMap.BPOProductId = "${StringUtil.wrapString(uiLabelMap.BPOProductId)}";
	uiLabelMap.BPOProductName = "${StringUtil.wrapString(uiLabelMap.BPOProductName)}";
	uiLabelMap.BLPackingForm = "${StringUtil.wrapString(uiLabelMap.BLPackingForm)}";
	uiLabelMap.BSPurchaseUomId = "${StringUtil.wrapString(uiLabelMap.BSPurchaseUomId)}";
	uiLabelMap.BLPurchaseQtySum = "${StringUtil.wrapString(uiLabelMap.BLPurchaseQtySum)}";
	uiLabelMap.BLQuantityEATotal = "${StringUtil.wrapString(uiLabelMap.BLQuantityEATotal)}";
	uiLabelMap.UnitPrice = "${StringUtil.wrapString(uiLabelMap.UnitPrice)}";
	uiLabelMap.BPOTotal = "${StringUtil.wrapString(uiLabelMap.BPOTotal)}";
	uiLabelMap.Note = "${StringUtil.wrapString(uiLabelMap.Note)}";
	uiLabelMap.BSDiscountinuePurchase = "${StringUtil.wrapString(uiLabelMap.BSDiscountinuePurchase)}";
	uiLabelMap.BLReceivedNumberSum = "${StringUtil.wrapString(uiLabelMap.BLReceivedNumberSum)}";
	
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
 	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
 	uiLabelMap.QOH = "${StringUtil.wrapString(uiLabelMap.QOH)}";
 	uiLabelMap.BSPurchaseUomId = "${StringUtil.wrapString(uiLabelMap.BSPurchaseUomId)}";
 	uiLabelMap.BLPurchaseQtySum = "${StringUtil.wrapString(uiLabelMap.BLPurchaseQtySum)}";
	uiLabelMap.ByPurchaseQuantityUom = "${StringUtil.wrapString(uiLabelMap.ByPurchaseQuantityUom)}";
 	uiLabelMap.BSDiscountinuePurchase = "${StringUtil.wrapString(uiLabelMap.BSDiscountinuePurchase)}";
 	uiLabelMap.BLQuantityEATotal = "${StringUtil.wrapString(uiLabelMap.BLQuantityEATotal)}";
 	uiLabelMap.ByBaseQuantityUom = "${StringUtil.wrapString(uiLabelMap.ByBaseQuantityUom)}";
 	uiLabelMap.Note = "${StringUtil.wrapString(uiLabelMap.Note)}";
 	uiLabelMap.validContainSpecialCharacter = "${StringUtil.wrapString(uiLabelMap.validContainSpecialCharacter)}";
 	uiLabelMap.UnitPrice = "${StringUtil.wrapString(uiLabelMap.UnitPrice)}";
	
	uiLabelMap.BPOProductId = "${StringUtil.wrapString(uiLabelMap.BPOProductId)}";
 	uiLabelMap.BPOProductName = "${StringUtil.wrapString(uiLabelMap.BPOProductName)}";
 	uiLabelMap.QOH = "${StringUtil.wrapString(uiLabelMap.QOH)}";
 	uiLabelMap.BLPackingForm = "${StringUtil.wrapString(uiLabelMap.BLPackingForm)}";
 	uiLabelMap.BSPurchaseUomId = "${StringUtil.wrapString(uiLabelMap.BSPurchaseUomId)}";
 	uiLabelMap.BLPurchaseQtySum = "${StringUtil.wrapString(uiLabelMap.BLPurchaseQtySum)}";
 	uiLabelMap.ByPurchaseQuantityUom = "${StringUtil.wrapString(uiLabelMap.ByPurchaseQuantityUom)}";
 	uiLabelMap.BPOCheckGreaterThan = "${StringUtil.wrapString(uiLabelMap.BPOCheckGreaterThan)}";
 	uiLabelMap.BPOTotal = "${StringUtil.wrapString(uiLabelMap.BPOTotal)}";
 	uiLabelMap.BSProductProductNotFound = "${StringUtil.wrapString(uiLabelMap.BSProductProductNotFound)}";
 	uiLabelMap.BPProductNotFoundOrHasBeenExpiredSupplied = "${StringUtil.wrapString(uiLabelMap.BPProductNotFoundOrHasBeenExpiredSupplied)}";
 	uiLabelMap.BLFacilityId = "${StringUtil.wrapString(uiLabelMap.BLFacilityId)}";
 	uiLabelMap.FacilityName = "${StringUtil.wrapString(uiLabelMap.FacilityName)}";
 	uiLabelMap.BSStartDateMustLessThanOrEqualFinishDate = "${StringUtil.wrapString(uiLabelMap.BSStartDateMustLessThanOrEqualFinishDate)}";
 	uiLabelMap.BLFacilityNotHasAddress = "${StringUtil.wrapString(uiLabelMap.BLFacilityNotHasAddress)}";
 	uiLabelMap.MOQ = "${StringUtil.wrapString(uiLabelMap.MOQ)}";
 	uiLabelMap.BPORestrictMOQ = "${StringUtil.wrapString(uiLabelMap.BPORestrictMOQ)}";
</script>
<script type="text/javascript" src="/poresources/js/order/orderNewPurchaseTotal.js?v=1.1.4"></script>