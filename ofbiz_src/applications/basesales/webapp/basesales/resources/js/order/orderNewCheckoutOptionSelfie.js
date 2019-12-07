var loadedWindowAddShipping = false;
$(function(){
	OlbOrderCheckout.init();
});
var OlbOrderCheckout = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function(){
		jOlbUtil.input.create("#shippingInstructions", {maxLength: 255});
		jOlbUtil.contextMenu.create($("#contextMenushippingContactMechGrid"));
	};
	var initElementComplex = function(){
		var configShipToParty = {
			widthButton: '100%',
			filterable: false,
			pageable: true,
			showfilterrow: false,
			dropDownHorizontalAlignment: 'right',
			datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
			columns: [
				{text: uiLabelMap.BSCustomerId, datafield: 'partyCode', width: '40%'},
				{text: uiLabelMap.BSFullName, datafield: 'fullName'}
			],
			useUrl: true,
			root: 'results',
			url: defaultPartyId != null ? 'JQGetPartiesReceiveByCustomer&partyId=' + defaultPartyId : '',
			useUtilFunc: true,
			
			key: 'partyId',
			keyCode: 'partyCode',
			description: ['fullName'],
			autoCloseDropDown: true
		};
		shipToCustomerPartyDDB = new OlbDropDownButton($("#shipToCustomerPartyId"), $("#shipToCustomerPartyGrid"), null, configShipToParty, [defaultPartyId]);
		
		var configSalesExecutive = {
			widthButton: '100%',
			dropDownHorizontalAlignment: 'right',
			datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
			columns: [
				{text: uiLabelMap.BSEmployeeId, datafield: 'partyCode', width: '40%'},
				{text: uiLabelMap.BSFullName, datafield: 'fullName'}
			],
			useUrl: true,
			root: 'results',
			url: defaultPartyId != null ? salesExecutiveUrl + '&partyId=' + defaultPartyId : '',
			useUtilFunc: true,
			filterable: true,
			
			key: 'partyId', 
			keyCode: 'partyCode', 
			description: ['fullName'],
			autoCloseDropDown: true
		};
		salesExecutiveDDB = new OlbDropDownButton(salesExecutiveIdObj, salesExecutiveGridObj, null, configSalesExecutive, []);
		//new OlbDropDownButton($("#salesExecutiveId"), $("#salesExecutiveGrid"), null, configSalesExecutive, []);
		
		var configShippingAddress = {
			widthButton: '100%',
			dropDownHorizontalAlignment: 'right',
			datafields: [
				{name: 'contactMechId', type: 'string'}, 
				{name: 'toName', type: 'string'}, 
				{name: 'attnName', type: 'string'},
				{name: 'address1', type: 'string'},
				{name: 'city', type: 'string'},
				{name: 'stateProvinceGeoId', type: 'string'},
				{name: 'stateProvinceGeoName', type: 'string'},
				{name: 'postalCode', type: 'string'},
				{name: 'countryGeoId', type: 'string'},
				{name: 'countryGeoName', type: 'string'},
				{name: 'districtGeoId', type: 'string'},
				{name: 'districtGeoName', type: 'string'},
				{name: 'wardGeoId', type: 'string'},
				{name: 'wardGeoName', type: 'string'},
			],
			columns: [
				{text: uiLabelMap.BSContactMechId, datafield: 'contactMechId', width: '20%'},
				{text: uiLabelMap.BSReceiverName, datafield: 'toName', width: '20%'},
				{text: uiLabelMap.BSOtherInfo, datafield: 'attnName', width: '20%'},
				{text: uiLabelMap.BSAddress, datafield: 'address1', width: '25%'},
				{text: uiLabelMap.BSWard, datafield: 'wardGeoName', width: '20%'},
				{text: uiLabelMap.BSCounty, datafield: 'districtGeoName', width: '20%'},
				{text: uiLabelMap.BSStateProvince, datafield: 'stateProvinceGeoName', width: '20%'},
				{text: uiLabelMap.BSCountry, datafield: 'countryGeoName', width: '20%'},
			],
			useUrl: true,
			root: 'results',
			url: defaultPartyId != null ? 'JQGetShippingAddressByPartyReceive&partyId=' + defaultPartyId : '',
			useUtilFunc: true,
			selectedIndex: 0,
			key: 'contactMechId', 
			description: ['toName', 'attnName', 'address1', 'wardGeoName', 'districtGeoName', 'city', 'countryGeoId'], 
			autoCloseDropDown: false,
			contextMenu: "contextMenushippingContactMechGrid",
			filterable: true
		};
		shippingAddressDDB = new OlbDropDownButton($("#shippingContactMechId"), $("#shippingContactMechGrid"), null, configShippingAddress, []);
		
		var urlShippingMethod = '';
		if (defaultPartyId != null && defaultProductStoreId != null) {
			urlShippingMethod = 'jqxGeneralServicer?sname=JQGetShippingMethodByCustomerAndStore&partyId=' + defaultPartyId + '&productStoreId=' + defaultProductStoreId;
		}
		var configShippingMethod = {
			width: '100%',
			placeHolder: uiLabelMap.BSClickToChoose,
			key: 'shippingMethod',
			value: 'description',
			autoDropDownHeight: false,
			selectedIndex: 0,
			displayDetail: true,
			dropDownHorizontalAlignment: 'right',
			useUrl: true,
			url: urlShippingMethod,
		};
		shippingMethodTypeDDL = new OlbDropDownList($("#shippingMethodTypeId"), null, configShippingMethod, [chosenShippingMethod]);
		
		var urlPaymentMethodType = '';
		if (defaultPartyId != null && defaultProductStoreId != null) {
			urlPaymentMethodType = 'jqxGeneralServicer?sname=JQGetPaymentMethodByCustomerAndStore&partyId=' + defaultPartyId + '&productStoreId=' + defaultProductStoreId;
		}
		var configPaymentMethodType = {
			width: '100%',
			placeHolder: uiLabelMap.BSClickToChoose,
			key: 'paymentMethodTypeId',
			value: 'description',
			autoDropDownHeight: true,
			selectedIndex: 0,
			displayDetail: true,
			dropDownHorizontalAlignment: 'right',
			useUrl: true,
			url: urlPaymentMethodType,
		};
		checkoutPaymentDDL = new OlbDropDownList($("#checkOutPaymentId"), null, configPaymentMethodType, [checkOutPaymentId]);
	};
	var initEvents = function(){
		shipToCustomerPartyDDB.getGrid().rowSelectListener(function(rowData){
	    	reloadShippingAddressGrid(rowData['partyId']);
	    });
	    shipToCustomerPartyDDB.getGrid().bindingCompleteListener(function(){
    		//shipToCustomerPartyDDB.putConfig("defaultValue", customerIdMain);
    		//shipToCustomerPartyDDB.putConfig("defaultLabel", customerRowDataMain.fullName);
	    	shipToCustomerPartyDDB.selectItem([customerIdMain]);
	    });
	    salesExecutiveDDB.getGrid().bindingCompleteListener(function(){
			var tmpSource = salesExecutiveDDB.getGridObj().jqxGrid('source');
			if (typeof(tmpSource) != 'undefined') {
				var totalrecords = tmpSource.totalrecords;
				if (totalrecords == 1) {
	    			salesExecutiveDDB.selectItem(null, 0);
				}
			}
		}, true);
	    
	    /* customerDDB.getGrid().rowSelectListener(function(rowData){
	    	customerIdMain = rowData['partyId'];
	    	customerRowDataMain = rowData;
			var productStoreItem = $("#productStoreId").jqxDropDownList('getSelectedItem');
			var productStoreId = productStoreItem.value;
			updateCheckoutOptions(customerIdMain, productStoreId);
	    });*/
	    $("#productStoreId").on('select', function(event){
			if (!isFirstLoad){
				clearCheckoutOptions();
			}
		});
		$('body').on('createContactmechComplete', function(event, contactMechIdNew){
			$('#alterpopupWindowContactMechNew').jqxWindow('close');
        	if (typeof($('#shipToCustomerPartyId')) != 'undefined') {
        		var customerId = shipToCustomerPartyDDB.getAttrDataValue();
            	reloadShippingAddressGrid(customerId, [contactMechIdNew]);
        	}
		});
		$("#addNewShippingAddress").on('click', function(event){
			if (typeof($("#alterpopupWindowContactMechNew")) != 'undefined') {
				var customerId = $("#customerId").val();
				if (!OlbCore.isNotEmpty(customerId)) {
					jOlbUtil.alert.error(uiLabelMap.BSYouNeedChooseCustomerIdBefore);
					return false;
				} else {
					var shipToPartyId = jOlbUtil.getAttrDataValue("shipToCustomerPartyId");
					if (!OlbCore.isNotEmpty(shipToPartyId)) {
						shipToPartyId = customerId;
					}
					$('#wn_partyId').val(shipToPartyId);
					$("#alterpopupWindowContactMechNew").jqxWindow('open');
					if (!loadedWindowAddShipping) {
						OlbShippingAdrNewPopup.initOther();
						loadedWindowAddShipping = true;
					}
				}
			}
		});
		$('#checkLiability').click('click', function(){
			var customerPartyIdTmp = $("#customerId").val();
			if (!OlbCore.isNotEmpty(customerPartyIdTmp)) {
				jOlbUtil.alert.error(uiLabelMap.BSYouNeedChooseCustomerIdBefore);
				return false;
			} else {
				var organizationPartyIdTmp = currentOrganizationPartyId;
				$.ajax({
					type: 'POST',
					url: 'checkLiabilityCustomer',
					data: {
						partyId : customerPartyIdTmp,
						organizationPartyId: organizationPartyIdTmp
					},
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						var message = uiLabelMap.InfoLiabilityOfCustomer + ": <br/>";
						message += "<p>" + uiLabelMap.BSPaid + ": " + formatcurrency(data.totalPayable) + "</p>";
						message += "<p>" + uiLabelMap.BSMustPay + ": " + formatcurrency(data.totalReceivable) + "</p>";
						message += "<p>" + uiLabelMap.BSLiability + ": " + formatcurrency(data.totalLiability) + "</p>";
						jOlbUtil.alert.info(message);
						
						if (data.totalLiability > 0) {
							$('#checkOutPaymentId').addClass('div-input-error');
							$('#checkOutPaymentId').removeClass('div-input-success');
						} else {
							$('#checkOutPaymentId').removeClass('div-input-error');
							$('#checkOutPaymentId').addClass('div-input-success');
						}
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
					},
				});
			}
		});
		$("#contextMenushippingContactMechGrid").on('itemclick', function (event) {
			var args = event.args;
	        var rowindex = shippingAddressDDB.getGridObj().jqxGrid('getselectedrowindex');
	        var tmpKey = $.trim($(args).text());
	        if (tmpKey == uiLabelMap.BSRefresh) {
	        	shippingAddressDDB.getGrid().updateBoundData();
	        } else if (tmpKey == uiLabelMap.BSEdit) {
	        	var data = shippingAddressDDB.getGridObj().jqxGrid("getrowdata", rowindex);
				if (data != undefined && data != null) {
					var contactMechId = data.contactMechId;
					OlbContactMechEdit.openWindowEditContactMech("" + contactMechId);
				}
	        } else if (tmpKey == uiLabelMap.BSDelete) {
	        	var data = shippingAddressDDB.getGridObj().jqxGrid("getrowdata", rowindex);
				if (data != undefined && data != null) {
					var partyId = shipToCustomerPartyDDB.getAttrDataValue();
					var contactMechId = data.contactMechId;
					
					jOlbUtil.confirm.dialog(uiLabelMap.BSAreYouSureYouWantToDelete, 
						function(){
							$.ajax({
								type: 'POST',
								url: 'deleteContactMechAjax',
								data: {
									partyId: partyId,
									contactMechId: contactMechId
								},
								beforeSend: function(){
									$("#loader_page_common").show();
								},
								success: function(data){
									jOlbUtil.processResultDataAjax(data, 
										function(data, errorMessage){
								        	$('#container').empty();
								        	$('#jqxNotification').jqxNotification({ template: 'error'});
								        	$("#jqxNotification").html(errorMessage);
								        	$("#jqxNotification").jqxNotification("open");
								        	
								        	return false;
										}, function(){
											$('#container').empty();
								        	$('#jqxNotification').jqxNotification({ template: 'info'});
								        	$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
								        	$("#jqxNotification").jqxNotification("open");
								        	
								        	shippingAddressDDB.clearAll();
								        	shippingAddressDDB.getGrid().updateBoundData();
										}
									);
								},
								error: function(data){
									alert("Send request is error");
								},
								complete: function(data){
									$("#loader_page_common").hide();
								},
							});
						}
					);
				}
	        }
		});
	};
	var updateCheckoutOptions = function(customerId, productStoreId){
		reloadAgreementGrid(customerId);
		reloadShipToCustomerGrid(customerId);
    	reloadShippingAddressGrid(customerId);
		
		if (typeof(productStoreId) != 'undefined') {
			reloadShippingMethod(customerId, productStoreId);
			reloadCheckoutPayment(customerId, productStoreId);
		}
		
		reloadSalesExecutiveGrid(customerId);
	};
	var clearCheckoutOptions = function(){
		agreementDDB.clearAll(true);
		shippingAddressDDB.clearAll(true);
		shipToCustomerPartyDDB.clearAll(true);
		salesExecutiveDDB.clearAll(true);
		shippingMethodTypeDDL.clearAll(true);
		checkoutPaymentDDL.clearAll(true);
	};
	var reloadAgreementGrid = function(customerId){
		agreementDDB.updateSource("jqxGeneralServicer?sname=JQGetAgreementsByCustomer&partyId=" + customerId, null, function(){
			agreementDDB.selectItem();
		});
	};
	var reloadShippingAddressGrid = function(customerId, selectArr){
		shippingAddressDDB.updateSource("jqxGeneralServicer?sname=JQGetShippingAddressByPartyReceive&partyId="+customerId, null, function(){
			shippingAddressDDB.selectItem(selectArr);
		});
	};
	var reloadShipToCustomerGrid = function(customerId){
		shipToCustomerPartyDDB.updateSource("jqxGeneralServicer?sname=JQGetPartiesReceiveByCustomer&partyId="+customerId, null, function(){
			shipToCustomerPartyDDB.selectItem([customerId]);
		});
	};
	var reloadSalesExecutiveGrid = function(customerId){
		salesExecutiveDDB.updateSource("jqxGeneralServicer?sname=JQGetSalesExecutiveOrderByCustomer&partyId="+customerId, null, function(){
			var tmpSource2 = salesExecutiveDDB.getGridObj().jqxGrid('source');
			if (typeof(tmpSource2) != 'undefined') {
				var totalrecords = tmpSource2.totalrecords;
				if (totalrecords == 1) {
	    			salesExecutiveDDB.selectItem(null, 0);
				}
			}
		});
	};
	var reloadShippingMethod = function(customerId, productStoreId){
		shippingMethodTypeDDL.updateSource("jqxGeneralServicer?sname=JQGetShippingMethodByCustomerAndStore&partyId=" + customerId + "&productStoreId=" + productStoreId, null, function(){
			shippingMethodTypeDDL.selectItem([chosenShippingMethod]); // GROUND_HOME@DLOG
		});
	};
	var reloadCheckoutPayment = function(customerId, productStoreId){
		var selectStr = 'EXT_COD';
		if ('SCHOOL' == $('#productStoreId').val() || 'BUSINESSES' == $('#productStoreId').val()) {
			selectStr = 'EXT_LIABILITY';
		}
		checkoutPaymentDDL.updateSource("jqxGeneralServicer?sname=JQGetPaymentMethodByCustomerAndStore&partyId=" + customerId + "&productStoreId=" + productStoreId + "&ia=Y", null, function(){
			checkoutPaymentDDL.selectItem([selectStr]);
		});
	};
	var initValidateForm = function(){
		var extendRules = [];
		// #salesExecutiveId - validInputNotNull
		var mapRules = [
	            {input: '#shippingContactMechId', type: 'validObjectNotNull', objType: 'dropDownButton'},
				{input: '#shippingMethodTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
				{input: '#checkOutPaymentId', type: 'validObjectNotNull', objType: 'dropDownList'},
			];
		validatorVAL = new OlbValidator($('#checkoutOrderEntry'), mapRules, extendRules, {scroll: true});
	};
	var getValidator = function() {
		return validatorVAL;
	};
	return {
		init: init,
		reloadShippingAddressGrid: reloadShippingAddressGrid,
		updateCheckoutOptions: updateCheckoutOptions,
		reloadSalesExecutiveGrid: reloadSalesExecutiveGrid,
		getValidator: getValidator,
	};
}());