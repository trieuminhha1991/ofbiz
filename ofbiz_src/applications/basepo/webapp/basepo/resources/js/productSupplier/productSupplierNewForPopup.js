$(function() {
	productSupplier.init();
});

let productSupplier = (function() {
	let jqxwindow, productCurrencyUomIdDDL, canDropShipDDL, quantityUomDDL, productDDB, supplierDDB, validatorVAL;
	let init = function() {
		jqxwindow = $("#alterpopupWindowAddSupplierProduct");
		initElement();
		initEvent();
		initValidateForm();
	};
	var initElement = function() {
		jOlbUtil.contextMenu.create($("#menu"));

		jOlbUtil.input.create("#supplierProductId", {width: "98%", height: 23});
		
		jOlbUtil.numberInput.create("#lastPrice", {width: "100%", spinButtons:true, decimalDigits: 2, min: 0});
		jOlbUtil.numberInput.create("#shippingPrice", {width: "100%", spinButtons:true, decimalDigits: 2, min: 0});
		jOlbUtil.numberInput.create("#minimumOrderQuantity", {width: "100%", spinButtons:true, decimalDigits: 0, min: 1});
		$("#minimumOrderQuantity").jqxNumberInput("setDecimal", 1);

		jOlbUtil.dateTimeInput.create("#availableFromDate", {width: "100%", allowNullDate: false});
		jOlbUtil.dateTimeInput.create("#availableThruDate", {width: "100%", allowNullDate: true, value: null, minDate: new Date(new Date().setDate(new Date().getDate() - 1))});

		let config = {
			width: "100%",
			key: "preferredCurrencyUomId",
			value: "preferredCurrencyUomId",
			autoDropDownHeight: false,
			displayDetail: false,
			placeHolder: uiLabelMap.BSClickToChoose,
		}
		productCurrencyUomIdDDL = new OlbDropDownList($("#productCurrencyUomId"), [], config, []);
		
		let configCanDropShip = {
			width: "100%",
			key: "id",
			value: "description",
			autoDropDownHeight: true,
			displayDetail: false,
			placeHolder: uiLabelMap.BSClickToChoose,
			disabled : true,
			selectedIndex: 1
		}
		canDropShipDDL = new OlbDropDownList($("#canDropShip"), canDropShipData, configCanDropShip, null);
		
		let configQuantityUom = {
			width: "100%",
			key: "uomId",
			value: "description",
			autoDropDownHeight: true,
			displayDetail: false,
			placeHolder: uiLabelMap.BSClickToChoose,
			selectedIndex: 1
		}
		quantityUomDDL = new OlbDropDownList($("#quantityUomId"), [], configQuantityUom, null);
		
		var initProductStoreDrDGrid = function(dropdown, grid) {
			var datafields = [
				{name: "productId", type : "string"},
				{name: "productCode", type : "string"},
				{name: "productName", type : "string"}
			];
			var columns = [
				{text: POProductId, datafield : "productCode", width : "200"},
				{text: POProductName, datafield : "productName"}
			];
			config = {
				useUrl: true,
				widthButton: "100%",
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: datafields,
				columns: columns,
				url: "",
				useUtilFunc: true,
				key: "productId",
				keyCode: "productCode",
				description: ["productName"],
				autoCloseDropDown: true,
				filterable: true,
				displayDetail: true,
			};
			productDDB = new OlbDropDownButton(dropdown, grid, null, config, []);
		};
		initProductStoreDrDGrid($("#productId"), $("#jqxgridProduct"));

		var initSupplierDrDGrid = function(dropdown, grid) {
			var datafields = [
				{name: "partyId", type: "string"},
				{name: "partyCode", type: "string"},
				{name: "groupName", type: "string"}
			];
			var columns = [ 
				{text: uiLabelMap.POSupplierId, datafield: "partyCode", width: "200"},
				{text: uiLabelMap.POSupplierName, datafield: "groupName"}
			];
			config = {
				useUrl: true,
				widthButton: "100%",
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: datafields,
				columns: columns,
				url: "jqGetListPartySupplier&subsidiary=Y",
				useUtilFunc: true,
				key: "partyId",
				keyCode: "partyCode",
				description: ["groupName"],
				autoCloseDropDown: true,
				filterable: true
			};
			supplierDDB = new OlbDropDownButton(dropdown, grid, null, config, []);
		};
		initSupplierDrDGrid($("#txtSupplier"), $("#jqxgridSupplier"));

		jOlbUtil.windowPopup.create(jqxwindow, {width: 1100, maxWidth: 1100, height: 315, cancelButton: $("#alterCancel")});
	};
	var initEvent = function() {
		supplierDDB.getGrid().rowSelectListener(function(itemData){
			if (itemData) loadUom(itemData.partyId);
		});
		productCurrencyUomIdDDL.selectListener(function(itemData){
			loadProduct(supplierDDB.getValue(), itemData.value);
		});
		productDDB.getGrid().rowSelectListener(function(itemData){
			if (itemData) {
				loadQuantityUom(itemData.productId);
				$("#supplierProductId").val(itemData.productCode);
			}
		});

		$("#availableFromDate").on("valueChanged", function(event) {
			var jsDate = event.args.date;
			if (jsDate) $("#availableThruDate ").jqxDateTimeInput("setMinDate", new Date(new Date().setDate(jsDate.getDate() - 1)));
		});
		
		jqxwindow.on("open", function() {
			$("#availableFromDate").jqxDateTimeInput("setDate", new Date());
			$("#availableThruDate ").jqxDateTimeInput("setMinDate", new Date(new Date().setDate(new Date().getDate() - 1)));
		});
		jqxwindow.on("close", function() {
			validatorVAL.hide();
		});

		$("#alterSave").click(function() {
			if (!validatorVAL.validate()) return false;
			
			bootbox.dialog(POAreYouSureAddItem, [
				{label: wgcancel, icon: "fa fa-remove", class: "btn  btn-danger form-action-button pull-right",
					callback : function() {
						bootbox.hideAll();
					}
				},
				{label: wgok, icon: "fa-check", class: "btn btn-primary form-action-button pull-right",
					callback : function() {
						addNewSupplierForProductId(false);
					}
				}
			]);
		});

		$("#createAndContinue").click(function() {
			if (!validatorVAL.validate()) return false;
			
			bootbox.dialog(POAreYouSureAddItem + "?", [
				{label: wgcancel, icon: "fa fa-remove", class: "btn  btn-danger form-action-button pull-right",
					callback : function() {
						bootbox.hideAll();
					}
				},
				{label : wgok, icon: "fa-check", class: "btn btn-primary form-action-button pull-right",
					callback : function() {
						addNewSupplierForProductId(true);
					}
				}
			]);
		});
	};

	let addNewSupplierForProductId = function(isContinue) {
		var dataMap = getValue();
		
		$.ajax({
			url: "addNewSupplierForProductId",
			type: "POST",
			data: dataMap,
			dataType: "json",
			success: function(data){
				jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
			        	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").html(errorMessage);
			        	$("#jqxNotification").jqxNotification("open");
			        	return false;
					}, function(){
						$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
			        	$("#jqxNotification").jqxNotification("open");
			        	
			        	if (!isContinue) {
			        		jqxwindow.jqxWindow("close");
			        		supplierDDB.clearAll();
			        	}
			        	clear();
			        	$("#jqxgridSupplierProduct").jqxGrid("updatebounddata");
					}
				);
			},
			error: function(data){
				alert("Send request is error");
			},
		});
	}
	
	let loadUom = function(partyId) {
		DataAccess.executeAsync({
			url: "loadCurrencyUomIdBySupplier",
			data: { partyId: partyId }
		}, function(res) {
			productCurrencyUomIdDDL.updateSource(null, res["listProductCurrencyUomId"], function(){
				productCurrencyUomIdDDL.selectItem(null, 0);
			});
		});
	};
	let loadProduct = function(partyId, currencyUomId) {
		if (partyId && currencyUomId) {
			if (partyId !== $("#jqxgridProduct").data("partyId") || currencyUomId !== $("#jqxgridProduct").data("currencyUomId")) {
				productDDB.clearAll();
				$("#jqxgridProduct").data("partyId", partyId);
				$("#jqxgridProduct").data("currencyUomId", currencyUomId);
				productDDB.updateSource("jqxGeneralServicer?sname=JQGetPOListProductsWithoutSupplier&partyId=" + partyId + "&currencyUomId=" + currencyUomId);
			}
		}
	};
	let loadQuantityUom = function(productId) {
		if (productId) {
			$.ajax({
				url: "loadQuantityUomIdByProduct",
				type: "POST",
				data: {productId: productId},
				dataType : "json",
			}).done(function(res) {
				quantityUomDDL.updateSource(null, res["uoms"], function(){
					quantityUomDDL.selectItem(null, 0);
				});
			});
		}
	};
	
	let clear = function() {
		productCurrencyUomIdDDL.clearAll();
		productDDB.clearAll();
		quantityUomDDL.clearAll();
		$("#lastPrice").jqxNumberInput("val", 0);
		$("#shippingPrice").jqxNumberInput("val", 0);
		$("#minimumOrderQuantity").jqxNumberInput("setDecimal", 1);
		$("#availableFromDate").jqxDateTimeInput("setDate", new Date());
		$("#availableThruDate").jqxDateTimeInput("setDate", null);
		$("#supplierProductId").val("");
	};
	
	let getValue = function() {
		return {
			partyId: supplierDDB.getValue(),
			currencyUomId: productCurrencyUomIdDDL.getValue(),
			quantityUomId: quantityUomDDL.getValue(),
			productId: productDDB.getValue(),
			canDropShip: canDropShipDDL.getValue() != "N" ? "Y" : canDropShipDDL.getValue(),
			supplierProductId: $("#supplierProductId").val(),
			availableFromDate: $("#availableFromDate").jqxDateTimeInput("getDate")?$("#availableFromDate").jqxDateTimeInput("getDate").getTime():null,
			availableThruDate: $("#availableThruDate").jqxDateTimeInput("getDate")?$("#availableThruDate").jqxDateTimeInput("getDate").getTime():null,
			lastPrice: $("#lastPrice").jqxNumberInput("getDecimal"),
			shippingPrice: $("#shippingPrice").jqxNumberInput("getDecimal"),
			minimumOrderQuantity: $("#minimumOrderQuantity").jqxNumberInput("getDecimal")
		}
	};
	
	var initValidateForm = function() {
		var mapRules = [
			{input: '#supplierProductId', type: 'validInputNotNull'},
			{input: '#supplierProductId', type: 'validCannotSpecialCharactor'},
			{input: '#productCurrencyUomId', type: 'validObjectNotNull', objType: 'dropDownList'},
			{input: '#quantityUomId', type: 'validObjectNotNull', objType: 'dropDownList'},
			{input: '#txtSupplier', type: 'validObjectNotNull', objType: 'dropDownButton'},
			{input: '#productId', type: 'validObjectNotNull', objType: 'dropDownButton'},
			{input: '#availableFromDate', type: 'validDateTimeInputNotNull'},
			{input: '#availableFromDate, #availableThruDate', type: 'validCompareTwoDate', paramId1 : "availableFromDate", paramId2 : "availableThruDate"},
		]
		var extendRules = [
			{input: '#lastPrice', message: multiLang.DmsPriceNotValid, action: 'keyup', 
				rule: function(input, commit){
					var value = $(input).val();
					if(value > 0){
						return true;
					}
					return false;
				}
			},
			{input: '#minimumOrderQuantity', message: uiLabelMap.BSQuantityMustBeGreaterThanZero, action: 'keyup', 
				rule: function(input, commit){
					var value = $(input).val();
					if(value > 0){
						return true;
					}
					return false;
				}
			}
		];
		validatorVAL = new OlbValidator(jqxwindow, mapRules, extendRules, {scroll: true});
	};
	
	return {
		init: init
	};
}());