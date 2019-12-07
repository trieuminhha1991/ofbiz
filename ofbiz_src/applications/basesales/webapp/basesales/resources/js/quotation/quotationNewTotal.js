$(function(){
	OlbQuotationTotal.init();
});
var OlbQuotationTotal = (function(){
	var init = function(){
		initElement();
		initEvent();
	};
	var initElement = function(){
		jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
	};
	var initEvent = function(){
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(info.step == 1 && (info.direction == "next")) {
				// check form valid
				if(!OlbQuotationInfo.getValidator().validate()) return false;
				/*
				var isCheckValid = false;
				var quotationTypeId = OlbQuotationInfo.getObj().productQuotationTypeDDL.getValue();
				if (quotationTypeId == "PROD_CAT_PRICE_FOD") {
					isCheckValid = checkValidManual("CATEGORY");
				} else {
					isCheckValid = checkValidManual("PRODUCT", productPricesMap);
				}
				if (!isCheckValid) return isCheckValid;
				
				transferDataToConfirm();*/
				
				// prepare data step 2
				prepareDataToStep2();
			} else if(info.step == 2 && (info.direction == "next")) {
				var isCheckValid = false;
				var quotationTypeId = OlbQuotationInfo.getObj().productQuotationTypeDDL.getValue();
				if (quotationTypeId == "PROD_CAT_PRICE_FOD") {
					isCheckValid = checkValidManual("CATEGORY");
				} else {
					isCheckValid = checkValidManual("PRODUCT", productPricesMap);
				}
				if (!isCheckValid) return isCheckValid;
				
				transferDataToConfirm();
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.BSAreYouSureYouWantToCreate, function() {
            	finishCreateQuotation();
            });
		}).on('stepclick', function(e){
			//prevent clicking on steps
		});
	};
	var prepareDataToStep2 = function(){
		//productPricesMap = {};
		var quotationTypeId = OlbQuotationInfo.getObj().productQuotationTypeDDL.getValue();
		if (quotationTypeId == "PROD_PRICE_FOL") { // product price modify
			$("#qni-case1").show();
			$("#qni-case2").hide();
		} else if (quotationTypeId == "PROD_CAT_PRICE_FOD") { // category price modify
			$("#qni-case1").hide();
			$("#qni-case2").show();
		} else if (quotationTypeId == "PROD_PRICE_FLAT") {
			// default is PROD_PRICE_FLAT product price override
			$("#qni-case1").show();
			$("#qni-case2").hide();
			updateGridProdItems();
		}
	};
	var updateGridProdItems = function(){
		var itemsStr = "";
		var storeItems = OlbQuotationInfo.getObj().productStoreCBB.getValue();
		var storeGroupItems = OlbQuotationInfo.getObj().productStoreGroupCBB.getValue();
		if (storeItems) {
			for (var i = 0; i < storeItems.length; i++) {
				itemsStr += "&productStoreIds=" + storeItems[i];
			}
		}
		if (storeGroupItems) {
			for (var i = 0; i < storeGroupItems.length; i++) {
				itemsStr += "&productStoreGroupIds=" + storeGroupItems[i];
			}
		}
		if ($("#isSelectAllProductStore").val()) itemsStr += "&isSelectAllProductStore=Y";
		// load list product
		//OlbGridUtil.updateSource($('#jqxgridProd'), "jqxGeneralServicer?sname=" + newUrlUpdateGridItems + itemsStr);
		OlbGridUtil.updateSource($('#jqxgridQuotItemsProdAdd'), "jqxGeneralServicer?sname=JQListProductAndTaxByCatalog" + itemsStr);
	};
	var checkValidManual = function(type, productPricesMapParam){
		if (typeof(productPricesMapParam) == "undefined") var productPricesMapParam = {};
		var isNotChooseProductYet = false;
		dataSelected = [];
		dataSelectedCate = [];
		var count = 0;
		if (type == "PRODUCT") {
			var message = uiLabelMap.BSExistProductHaveNotPriceIs + ": ";
			var hasMessage = false;
			var isFirst = true;
			$.each(productPricesMapParam, function(key, value){
				var itemMap = value;
				if (itemMap.selected) {
					count++;
					if (OlbCore.isEmpty(itemMap.listPrice) || itemMap.listPrice <= 0) {
						hasMessage = true;
						if (!isFirst) message += ", ";
						message += itemMap.productCode;
						isFirst = false;
					} else {
						dataSelected.push(itemMap);
					}
				}
			});
			message += "</span>";
			
			if (count <= 0) {
				jOlbUtil.alert.error(uiLabelMap.BSYouNotYetChooseProduct);
				return false;
			}
			
			if (hasMessage) {
				jOlbUtil.alert.error(message);
				return false;
			}
			return true;
		} else if (type == "CATEGORY") {
			var productPricesMapParam = $("#jqxgridCategoryItem").jqxGrid('getboundrows');
			if (!productPricesMapParam) var productPricesMapParam = {};
			$.each(productPricesMapParam, function(key, value){
				if (OlbCore.isNotEmpty(value.amount) && value.amount != 0) {
					dataSelectedCate.push(value);
				}
			});
			if (dataSelectedCate.length <= 0) {
				jOlbUtil.alert.error(uiLabelMap.BSYouNotYetChooseRow);
				return false;
			}
			return true;
		}
		return false;
	};
	var transferDataToConfirm = function(){
		$("#strProductQuotationId").text($("#productQuotationId").val());
		$("#strQuotationName").text($("#quotationName").val());
		$("#strDescription").text($("#description").val());
		$("#strCurrencyUomId").text($("#currencyUomId").val());
		$("#strFromDate").text($("#fromDate").val());
		$("#strThruDate").text($("#thruDate").val());
		$("#strPartyId").text($("#partyId").val());
		
		var strProductStoreIds = [];
		var isSelectAllProductStore = $("#isSelectAllProductStore").val();
		var itemsSelected = $("#productStoreIds").jqxComboBox("getSelectedItems");
		if (OlbCore.isNotEmpty(itemsSelected)) {
			for (var i = 0; i < itemsSelected.length; i++) {
				var item = itemsSelected[i];
				if (item != null) {
					strProductStoreIds.push(item.label);
				}
			}
		}
		if (strProductStoreIds.length > 0) {
			$("#strProductStoreIds").text(strProductStoreIds.join(", "));
		} else {
			$("#strProductStoreIds").text("");
		}
		if (isSelectAllProductStore) {
			$("#strProductStoreIds").text(uiLabelMap.BSSelectAll);
		}
		
		var strProductStoreGroupIds = [];
		var storeGroupItemsSelected = $("#productStoreGroupIds").jqxComboBox("getSelectedItems");
		if (OlbCore.isNotEmpty(storeGroupItemsSelected)) {
			for (var i = 0; i < storeGroupItemsSelected.length; i++) {
				var item = storeGroupItemsSelected[i];
				if (item != null) {
					strProductStoreGroupIds.push(item.label);
				}
			}
		}
		if (strProductStoreGroupIds.length > 0) {
			$("#strProductStoreGroupIds").text(strProductStoreGroupIds.join(", "));
		} else {
			$("#strProductStoreGroupIds").text("");
		}
		/*
		var salesChannel = $("#salesMethodChannelEnumId").val();
		if (OlbCore.isNotEmpty(salesChannel)) {
			for (var i = 0; i < salesMethodChannelEnumData.length; i++) {
				var salesItem = salesMethodChannelEnumData[i];
				if (salesChannel == salesItem.enumId) {
					$("#strSalesMethodChannelEnumId").text(salesItem.description);
				}
			}
		}
		
		var partyApply = $("#roleTypeId").jqxComboBox('getSelectedItems');
		var strValue = "";
		var isFirst = true;
		if (OlbCore.isNotEmpty(partyApply) && OlbCore.isNotEmpty(roleTypeData)) {
			for (var i = 0; i < partyApply.length; i++) {
				var partyApplyItem = partyApply[i];
				for (var j = 0; j < roleTypeData.length; j++) {
					var roleTypeItem = roleTypeData[j];
					if (roleTypeItem.roleTypeId == partyApplyItem.value) {
						if (!isFirst) strValue += ", ";
						strValue += roleTypeItem.description;
						isFirst = false;
					}
				}
			}
		}
		$("#strRoleTypeId").html(strValue);
		*/
		
		/*var sourceSuccess = {
			localdata: dataSelected,
			dataType: "array",
			datafields: dataFieldProductItems,
	   	}
		var dataAdapter = new $.jqx.dataAdapter(sourceSuccess);
        $("#jqxgridProdSelected").jqxGrid({source: dataAdapter});
        */
		//datafieldCategoryItemConfirm, columnlistCategoryItemConfirm
		var quotationTypeId = OlbQuotationInfo.getObj().productQuotationTypeDDL.getValue();
		if (quotationTypeId == "PROD_CAT_PRICE_FOD") {
			$("#jqxgridProdSelected").jqxGrid('columns', columnlistCategoryItemConfirm);
			var tmpSource = $("#jqxgridProdSelected").jqxGrid('source');
			if (typeof(tmpSource) != 'undefined') {
				tmpSource._source.datafields = datafieldCategoryItemConfirm;
				tmpSource._source.localdata = dataSelectedCate;
				$("#jqxgridProdSelected").jqxGrid('source', tmpSource);
			}
		} else {
			$("#jqxgridProdSelected").jqxGrid('columns', columnListItemsProdConfirm);
			var tmpSource = $("#jqxgridProdSelected").jqxGrid('source');
			if (typeof(tmpSource) != 'undefined') {
				tmpSource._source.datafields = dataFieldItemsProdConfirm;
				tmpSource._source.localdata = dataSelected;
				$("#jqxgridProdSelected").jqxGrid('source', tmpSource);
			}
		}
		setTimeout(function(){
			$(window).resize();
		}, 600);
	};
	var finishCreateQuotation = function(){
		var dataMap = OlbQuotationInfo.getValues();
		dataMap.productQuotationModuleTypeId = "SALES_QUOTATION";
		
		var urlCreateUpdateQuotation = "";
		if (updateMode && !copyMode) {
			urlCreateUpdateQuotation = "updateQuotationAjax";
		} else {
			urlCreateUpdateQuotation = "createQuotationAjax";
		}
		$.ajax({
			type: 'POST',
			url: urlCreateUpdateQuotation,
			data: dataMap,
			beforeSend: function(){
				$("#btnPrevWizard").addClass("disabled");
				$("#btnNextWizard").addClass("disabled");
				$("#loader_page_common").show();
			},
			success: function(data){
				processResultCreateQuotation(data);
			},
			error: function(data){
				alert("Send request is error");
			},
			complete: function(data){
				$("#loader_page_common").hide();
				$("#btnPrevWizard").removeClass("disabled");
				$("#btnNextWizard").removeClass("disabled");
			},
		});
	};
	var processResultCreateQuotation = function(data){
		jOlbUtil.processResultDataAjax(data, "default", function(data){
					$('#container').empty();
		        	$('#jqxNotification').jqxNotification({ template: 'info'});
		        	$("#jqxNotification").html(uiLabelMap.wgcreatesuccess);
		        	$("#jqxNotification").jqxNotification("open");
		        	var productQuotationId = data.productQuotationId;
		        	if (OlbCore.isNotEmpty(productQuotationId)) {
		        		window.location.href = "viewQuotation?productQuotationId=" + productQuotationId;
		        	}
				}
		);
	};
	return {
		init : init,
	}
}());