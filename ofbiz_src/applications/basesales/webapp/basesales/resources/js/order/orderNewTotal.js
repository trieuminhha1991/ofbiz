$(function(){
	OlbTotal.init();
	Loading.hide('process-loading-css');
	$(".container-add-plus").show();
	$(".wizard-actions").show();
});
var OlbTotal = (function(){
	var indexColumnQuantity = 4;
	var columnQuantity = 4;
	
	var init = function(){
		initEvent();
	};
	var initEvent = function(){
		$("#btnNextWizardTmp").on("click", function(){
			$("#btnNextWizardTmp").addClass("disabled");
			clearAllToolTipCheckInventory();
			$('#containerMsgTotal').empty();
			var resultValidate = true;
			if (typeof(OlbOrderDropShip) != 'undefined') {
				resultValidate = !OlbOrderInfo.getValidator().validate() || !OlbOrderCheckout.getValidator().validate() || !OlbOrderDropShip.getValidator().validate();
			} else {
				resultValidate = !OlbOrderInfo.getValidator().validate() || !OlbOrderCheckout.getValidator().validate();
			}
			if(resultValidate) {
				$("#btnNextWizardTmp").removeClass("disabled");
				return false;
			}
			createCartAsync();
		});
		
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(info.step == 1 && (info.direction == "next")) {
				// check form valid
				$("#btnNextWizardTmp").removeClass("disabled");
			} else if (info.step == 2 && (info.direction == "previous")) {
				jQuery('#btnNextWizard').css("display", "none");
        		jQuery('#btnNextWizardTmp').css("display", "inline-block");
        		jQuery('#btnCheckProduct').css("display", "inline-block");
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.BSAreYouSureYouWantToCreate, 
					function(){
						$("#btnPrevWizard").addClass("disabled");
						$("#btnNextWizard").addClass("disabled");
						/*window.location.href = "processSalesOrder";*/
						$.ajax({
							type: 'POST',
							url: processSalesOrderUrl,
							beforeSend: function(){
								$("#loader_page_common").show();
							},
							success: function(data){
								jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
											$("#btnPrevWizard").removeClass("disabled");
											$("#btnNextWizard").removeClass("disabled");
											
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
								        	if (data.orderId != undefined && data.orderId != null) {
								        		window.location.href = "viewOrder?orderId=" + data.orderId;
								        	}
										}
								);
							},
							error: function(data){
								alert("Send request is error");
								$("#btnPrevWizard").removeClass("disabled");
								$("#btnNextWizard").removeClass("disabled");
							},
							complete: function(data){
								$("#loader_page_common").hide();
							},
						});
					}
			);
		}).on('stepclick', function(e){
			//return false;//prevent clicking on steps
		});
		
		$("#btnCheckProduct").on('click', function(){
			destroyAllToolTipCheckInventory();
			checkProductAvailable();
		});
		
		$("#jqxgridSO").on("pagechanged", function (event) {
			OlbGridUtil.updateSource($("#jqxgridSO"), null, null, function(){
				destroyAllToolTipCheckInventory();
			});
		});
	};
	var getListProduct = function(){
		var data = $("#jqxgridSO").jqxGrid("getboundrows");
		if (typeof(data) == 'undefined') {
			jOlbUtil.alert.info("Error check data");
		}
		
		var listProd = [];
		for (var i = 0; i < data.length; i++) {
			var dataItem = data[i];
			if (dataItem != window) {
				var quantitySum = -1;
				if (typeof(dataItem) != 'undefined' && typeof(dataItem.quantity) != 'undefined' && parseInt(dataItem.quantity) > 0) {
					quantitySum = dataItem.quantity;
				}
				if (typeof(dataItem) != 'undefined' && typeof(dataItem.quantityReturnPromo) != 'undefined' && parseInt(dataItem.quantityReturnPromo) > 0) {
					if (quantitySum > -1) {
						quantitySum += dataItem.quantityReturnPromo;
					} else {
						quantitySum = dataItem.quantityReturnPromo;
					}
				}
				if (quantitySum > -1) {
					var prodItem = {
						productId: dataItem.productId,
						quantityUomId: dataItem.quantityUomId,
						quantity: quantitySum
					};
					listProd.push(prodItem);
				}
			}
		}
		return listProd;
	};
	var getListProductAll = function(){
		var data = productOrderMap;
		if (typeof(data) == 'undefined') {
			jOlbUtil.alert.info("Error check data");
		}
		
		var listProd = [];
		$.each(data, function (key, value){
			if (typeof(value) != 'undefined' && (parseInt(value.quantity) > 0 || parseInt(value.quantityReturnPromo) > 0)) {
				var prodItem = {
					productId: value.productId,
					quantityUomId: typeof(value.quantityUomId) != 'undefined' ? value.quantityUomId : '',
					quantity: typeof(value.quantity) != 'undefined' ? value.quantity : 0,
					quantityReturnPromo: typeof(value.quantityReturnPromo) != 'undefined' ? value.quantityReturnPromo : 0,
					idEAN: typeof(value.idEAN) != 'undefined' ? value.idEAN : ''
				};
				listProd.push(prodItem);
			}
		});
		return listProd;
	};
	var createCartAsync = function(){
		// process form first - general info - #initOrderEntry
		var m_customerId = jOlbUtil.getAttrDataValue('customerId');
		var m_shipToCustomerPartyId = jOlbUtil.getAttrDataValue('shipToCustomerPartyId');
		var m_shippingContactMechId = jOlbUtil.getAttrDataValue('shippingContactMechId');
		var m_salesExecutiveId = jOlbUtil.getAttrDataValue('salesExecutiveId');
		var m_agreementId = jOlbUtil.getAttrDataValue('agreementId');
		var m_favorSupplierPartyId = jOlbUtil.getAttrDataValue('favorSupplierPartyId');
		var m_shipGroupFacilityId = jOlbUtil.getAttrDataValue('shipGroupFacilityId');
		var m_facilityConsignId = jOlbUtil.getAttrDataValue('facilityConsignId');
		var m_favorDistributorPartyId = jOlbUtil.getAttrDataValue('favorDistributorPartyId');
		var dataMap = {
			orderId: typeof($('#orderId').val()) != 'undefined' ? $('#orderId').val() : '',
			externalId: typeof($('#externalId').val()) != 'undefined' ? $('#externalId').val() : '',
			/*orderName: typeof($('#orderName').val()) != 'undefined' ? $('#orderName').val() : '',*/
			partyId: typeof(m_customerId) != 'undefined' ? m_customerId : '',
			agreementId: typeof(m_agreementId) != 'undefined' ? m_agreementId : '',
			productStoreId: typeof($('#productStoreId').val()) != 'undefined' ? $('#productStoreId').val() : '',
			orderPriorityId: typeof($('#orderPriorityId').val()) != 'undefined' ? $('#orderPriorityId').val() : '',
			
			shipToCustomerPartyId: typeof(m_shipToCustomerPartyId) != 'undefined' ? m_shipToCustomerPartyId : '',
			shipping_contact_mech_id: typeof(m_shippingContactMechId) != 'undefined' ? m_shippingContactMechId : '',
			shipping_method: $('#shippingMethodTypeId').val(),
			shipping_instructions: $('#shippingInstructions').val(),
			checkOutPaymentId: $('#checkOutPaymentId').val(),
			
			salesExecutiveId: typeof(m_salesExecutiveId) != 'undefined' ? m_salesExecutiveId : '',
			requestFavorDelivery: $('#requestFavorDelivery').val(),
			favorSupplierPartyId: typeof(m_favorSupplierPartyId) != 'undefined' ? m_favorSupplierPartyId : '',
			shipGroupFacilityId: typeof(m_shipGroupFacilityId) != 'undefined' ? m_shipGroupFacilityId : '',
			
			/*internal_order_notes: $('#internalOrderNotes').val(),
			shippingNotes: $('#shippingNotes').val(),*/
			
			shipFromFacilityConsign: $('#shipFromFacilityConsign').val(),
			favorDistributorPartyId: typeof(m_favorDistributorPartyId) != 'undefined' ? m_favorDistributorPartyId : '',
			facilityConsignId: typeof(m_facilityConsignId) != 'undefined' ? m_facilityConsignId : '',
		};
		
		if (typeof($('#desiredDeliveryDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#desiredDeliveryDate').jqxDateTimeInput('getDate') != null) {
			dataMap['desiredDeliveryDate'] = $('#desiredDeliveryDate').jqxDateTimeInput('getDate').getTime();
		}
		if (typeof($('#shipAfterDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#shipAfterDate').jqxDateTimeInput('getDate') != null) {
			dataMap['shipAfterDate'] = $('#shipAfterDate').jqxDateTimeInput('getDate').getTime();
		}
		if (typeof($('#shipBeforeDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#shipBeforeDate').jqxDateTimeInput('getDate') != null) {
			dataMap['shipBeforeDate'] = $('#shipBeforeDate').jqxDateTimeInput('getDate').getTime();
		}
		
		var listProd = getListProductAll();
		
		if (listProd.length > 0) {
			dataMap.listProd = JSON.stringify(listProd);
			
			$.ajax({
				type: 'POST',
				url: 'initSalesOrderEntry',
				data: dataMap,
				beforeSend: function(){
					$("#loader_page_common").show();
				},
				success: function(data){
					jOlbUtil.processResultDataAjax(data, "default", "default", function(){
						jQuery('#btnNextWizard').css("display", "inline-block");
			    		jQuery('#btnNextWizardTmp').css("display", "none");
			    		jQuery('#btnCheckProduct').css("display", "none");
			    		jQuery('#btnNextWizard').trigger("click");
			    		$("#step2").html(data);
					});
				},
				error: function(data){
					alert("Send request is error");
				},
				complete: function(data){
					$("#btnNextWizardTmp").removeClass("disabled");
					$("#loader_page_common").hide();
				},
			});
		} else {
			jOlbUtil.alert.error(uiLabelMap.BSYouNotYetChooseProduct);
			$("#btnNextWizardTmp").removeClass("disabled");
			return false;
		}
	};
	var clearAllToolTipCheckInventory = function(){
		OlbGridUtil.closeTooltipOnRow("jqxgridSO", null, null, indexColumnQuantity, true);
	};
	var destroyAllToolTipCheckInventory = function(){
		OlbGridUtil.closeTooltipOnRow("jqxgridSO", null, null, indexColumnQuantity, true, true);
	};
	var checkProductAvailable = function(){
		var listProd = getListProduct();
		if (listProd.length > 0) {
			cleanStyleGridProduct();
			
			var checkOrgFacility = !enableFacilityConsign;
			if (enableFacilityConsign) {
				var isCheckFavorDistributor = $("#shipFromFacilityConsign").jqxCheckBox('val');
				if (isCheckFavorDistributor) {
					checkOrgFacility = false;
				} else {
					checkOrgFacility = true;
				}
			}
			if (checkOrgFacility) {
				var dataMap = {
					listProducts : JSON.stringify(listProd),
					productStoreId: typeof($('#productStoreId').val()) != 'undefined' ? $('#productStoreId').val() : ''
				};
				checkProductAvailableCore('checkProductAvailable', dataMap);
			} else {
				var favorFacilityConsignId = jOlbUtil.getAttrDataValue('facilityConsignId');
				if (OlbCore.isEmpty(favorFacilityConsignId)) {
					jOlbUtil.alert.error(uiLabelMap.BSYouNotYetChooseFacility);
					return false;
				}
				var dataMap = {
					listProducts : JSON.stringify(listProd),
					facilityId: typeof(favorFacilityConsignId) != 'undefined' ? favorFacilityConsignId : ''
				};
				checkProductAvailableCore('checkProductAvailableFacility', dataMap);
			}
		} else {
			jOlbUtil.alert.error(uiLabelMap.BSYouNotYetChooseProduct);
			cleanStyleGridProduct();
			return false;
		}
	};
	var checkProductAvailableCore = function(requestUrl, dataMap){
		$.ajax({
			type: 'POST',
			url: requestUrl,
			data: dataMap,
			beforeSend: function(){
				$("#loader_page_common").show();
			},
			success: function(data){
				processResultCheckInventory(data);
			},
			error: function(data){
				alert("Send request is error");
			},
			complete: function(data){
				$("#loader_page_common").hide();
			},
		});
	};
	var cleanStyleGridProduct = function(){
		if (typeof($("#jqxgridSO .jqx-grid-cell")) != 'undefined') {
			$("#jqxgridSO .jqx-grid-cell").removeClass('row-cell-success');
			$("#jqxgridSO .jqx-grid-cell").removeClass('row-cell-error');
		}
		var dataRow = $("#jqxgridSO").jqxGrid("getboundrows");
		if (typeof(dataRow) != 'undefined') {
			for (var i = 0; i < dataRow.length; i++) {
				var dataItem = dataRow[i];
				if (dataItem != window && dataItem != undefined) {
					dataItem.productAvailable = null;
				}
			}
		}
	};
	var processResultCheckInventory = function(data){
		jOlbUtil.processResultDataAjax(data, 'default', 
				function(){
					var dataRow = $("#jqxgridSO").jqxGrid("getboundrows");
		        	var listProductChecks = data.listProductChecks;
		        	if (typeof(dataRow) != 'undefined' && typeof(listProductChecks) != 'undefined') {
						var listProd = [];
						var icount = 0;
						var columnIndex = columnQuantity;
						for (var i = 0; i < dataRow.length; i++) {
							var dataItem = dataRow[i];
							if (dataItem != window && dataItem != undefined) {
								if ((typeof(dataItem.quantity) != 'undefined' && parseInt(dataItem.quantity) > 0) 
										|| (typeof(dataItem.quantityReturnPromo) != 'undefined' && parseInt(dataItem.quantityReturnPromo) > 0)) {
									var dataItemQuantitySum = 0;
									if (typeof(dataItem.quantity) != 'undefined' && parseInt(dataItem.quantity) > 0) dataItemQuantitySum += dataItem.quantity;
									if (typeof(dataItem.quantityReturnPromo) != 'undefined' && parseInt(dataItem.quantityReturnPromo) > 0) dataItemQuantitySum += dataItem.quantityReturnPromo;
									for (var j = 0; j < listProductChecks.length; j++) {
										var productItem = listProductChecks[j];
										if (dataItem.productId == productItem.productId 
											&& dataItem.quantityUomId == productItem.uomId 
											&& dataItemQuantitySum == productItem.quantity) {
											var obj = $("#row" + icount + "jqxgridSO div[role='gridcell']");
											if (typeof(obj) != 'undefined') {
												if (productItem.available) {
													
													dataItem.productAvailable = 'true';
													
													var availableToPromiseTotal = productItem.availableToPromiseTotal != null ? productItem.availableToPromiseTotal : '...';
													var quantityOnHandTotal = productItem.quantityOnHandTotal != null ? productItem.quantityOnHandTotal : '...';
													if (availableToPromiseTotal != quantityOnHandTotal && availableToPromiseTotal < dataItemQuantitySum){
														$(obj).addClass('row-cell-warning');
													} else {
														$(obj).addClass('row-cell-success');
													}
													
													var contentMsg = uiLabelMap.QOH + ' = ' + quantityOnHandTotal + "; " + uiLabelMap.ATP + " = " + availableToPromiseTotal;
													OlbGridUtil.displayTooltipOnRow('ERROR', null, obj, null, columnIndex, contentMsg);
													
												} else {
													$(obj).addClass('row-cell-error');
													dataItem.productAvailable = 'false';
													var availableToPromiseTotal = productItem.availableToPromiseTotal != null ? productItem.availableToPromiseTotal : '...';
													var quantityOnHandTotal = productItem.quantityOnHandTotal != null ? productItem.quantityOnHandTotal : '...';
													var contentMsg = uiLabelMap.QOH + ' = ' + quantityOnHandTotal + "; " + uiLabelMap.ATP + " = " + availableToPromiseTotal;
													OlbGridUtil.displayTooltipOnRow('ERROR', null, obj, null, columnIndex, contentMsg);
												}
											}
										}
									}
								}
								icount++;
							}
						}
						return false;
					}
		        	return true;
				}
		);
	};
	
	return {
		init: init,
	};
}());