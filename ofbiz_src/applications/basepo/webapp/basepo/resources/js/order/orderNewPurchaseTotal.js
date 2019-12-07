$(function() {
	OlbTotal.init();
	Loading.hide("process-loading-css");
	$(".container-add-plus").show();
	$(".wizard-actions").show();
});
var OlbTotal = (function() {
	var createClick = false;
	var editClick = false;
	var btnClick = false;
	var init = function() {
		initEvent();
	};
	var initEvent = function() {
		$("#btnPrevWizard").on("click", function() {
			btnClick = false;
			createClick = false;
			editClick = false;
		});
		
		$("#btnNextWizardTmp").on("click", function() {
			$("#btnNextWizardTmp").addClass("disabled");
			$("#containerMsgTotal").empty();
			var resultValidate = !OlbOrderInfo.getValidator().validate();
			if (resultValidate) {
				$("#btnNextWizardTmp").removeClass("disabled");
				return false;
			}
			createCartAsync();
		});

		$("#fuelux-wizard").ace_wizard()
			.on("change",
					function(e, info) {
						if (info.step == 1 && (info.direction == "next")) {
							// check form valid
							$("#btnNextWizardTmp").removeClass("disabled");
						} else if (info.step == 2 && (info.direction == "previous")) {
							jQuery("#btnNextWizard").css("display", "none");
							jQuery("#btnNextWizardTmp").css("display",
									"inline-block");
						}
					})
			.on("finished",
				function(e) {
					if (!btnClick){
						if (orderId != undefined && orderId != null) {
							// edit
							jOlbUtil.confirm.dialog(
								uiLabelMap.AreYouSureUpdate,
								function() {
									if (!editClick) {
											
										$("#btnPrevWizard").addClass("disabled");
										$("#btnNextWizard").addClass("disabled");
							 	        var listOrderItemSeqIdFounds = [];
							 	        var listOrderItemNews = [];
							 	        var listOrderItemUpdates = [];
							 	        for ( var n in productOrderMap) {
							 	        	if (!validateObject(n)) continue;
							 	        	var item =  productOrderMap[n];
							 	        	var x = item.quantity;
							 	        	if (typeof x === 'string') {
							 	        		x = x.replace(',', '.');
							 	        		x = parseFloat(x, 3, null);
										    }
							 	        	var y = item.quantityPurchase;
							 	        	if (typeof y === 'string') {
							 	        		y = y.replace(',', '.');
							 	        		y = parseFloat(y, 3, null);
							 	        	}
							 	        	if (x > 0 || y > 0){
								 	        	var itemComment = null;
								 	        	if (item.itemComment) {
								 	        		itemComment = item.itemComment.trim().split('\n').join(' ');
								 	        		itemComment = unescapeHTML(itemComment);
								 	        	}
								 	        	if (itemComment != null && itemComment != undefined && itemComment != '' && itemComment != 'null'){
								 	        		item.itemComment = itemComment;
												}
								 	        	if (item.weightUomId === undefined || item.weightUomId === null || item.weightUomId === '' || item.weightUomId === 'null'){
								 	        		delete item.weightUomId;
								 	        	}
								 	        	var lastPrice = item.lastPrice;
								 				var lastPriceStr = lastPrice.toString();
								 				if (locale == "vi") {
								 					lastPriceStr = lastPriceStr.replace(".", ",");
								 				}
								 				item.lastPrice = lastPriceStr;
								 				var orderItemSeqId = item.orderItemSeqId;
								 				if (orderItemSeqId != null) {
								 					// update
								 					item.quantity = x.toString();
									 	        	item.quantityPurchase = y.toString();
								 					listOrderItemSeqIdFounds.push(orderItemSeqId);
								 					listOrderItemUpdates.push(item);
								 				} else {
								 					// add
								 					item.quantity = y.toString();
									 	        	item.quantityPurchase = y.toString();
								 					lastPriceStr = lastPriceStr.replace(",", ".");
								 					item.lastPrice = lastPriceStr;
								 					listOrderItemNews.push(item);
								 				}
							 	        	}
							 			}
							 	        var listOrderItemSeqIdRemoves = [];
							 	        if (typeof listorderItemSeqIds != 'undefined' && listorderItemSeqIds.length > listOrderItemSeqIdFounds.length){
							 	        	// remove item
							 	        	for (var h in listorderItemSeqIds) {
							 	        		var check = false;
							 	        		for (var k in listOrderItemSeqIdFounds) {
								 	        		if (listorderItemSeqIds[h] == listOrderItemSeqIdFounds[k]){
								 	        			check = true;
								 	        			break;
								 	        		}
								 	        	}
							 	        		if (check == false){
							 	        			var rm = {};
							 	        			rm.orderItemSeqId = listorderItemSeqIds[h];
							 	        			rm.orderId = orderId;
							 	        			listOrderItemSeqIdRemoves.push(rm);
							 	        		}
							 	        	}
							 	        }
							 	        // TODO check nothing to update
							 	        var contactMechId = null;
						 				var rows = $("#shippingContactMechGrid").jqxGrid('getrows');
						 				if (typeof rows != 'undefined' && rows != null && rows.length > 0){
						 					contactMechId = rows[0].contactMechId;
						 				}
							 			var shipBeforeDateTmp = $("#shipBeforeDate").jqxDateTimeInput("getDate").getTime();
										var shipAfterDateTmp = $("#shipAfterDate").jqxDateTimeInput("getDate").getTime();
										$.ajax({
											type : "POST",
											url : "updateOrderItemsTotal",
											data : {
												orderId: orderId,
												partyIdFrom: $("#supplierId").val(),
												contactMechId: contactMechId,
												originFacilityId: $("#originFacilityId").jqxDropDownList('val'),
												currencyUomId: $("#currencyUomId").jqxDropDownList('val'),
												shipBeforeDate: shipBeforeDateTmp,
												shipAfterDate: shipAfterDateTmp,
												listOrderItemUpdate: JSON.stringify(listOrderItemUpdates),
												listOrderItemDelete: JSON.stringify(listOrderItemSeqIdRemoves),
												listOrderItemCreateNew: JSON.stringify(listOrderItemNews),
											},
											beforeSend : function() {
												$("#loader_page_common").show();
											},
											success : function(data) {
												if (data._ERROR_MESSAGE_ || data._ERROR_MESSAGE_LIST_) {
				 									jOlbUtil.alert.error(data._ERROR_MESSAGE_);
				 									$("#btnPrevWizard").removeClass("disabled");
													$("#btnNextWizard").removeClass("disabled");
				 								} else {
				 						        	if (data.orderId != undefined && data.orderId != null) {
				 						        		window.location.href = "viewDetailPO?orderId=" + data.orderId;
				 						        	}
				 								}
											},
											error : function(data) {
												alert("Send request is error");
												$("#btnPrevWizard").removeClass("disabled");
												$("#btnNextWizard").removeClass("disabled");
											},
											complete : function(
												data) {
												$("#loader_page_common").hide();
											},
										});
									}
									editClick = true;
								}, uiLabelMap.CommonCancel, uiLabelMap.OK, function(){
									editClick = false;
									btnClick = false;
								});
						} else {
							// create
							jOlbUtil.confirm.dialog(
							uiLabelMap.AreYouSureCreate,
							function() {
								if (!createClick) {
									
									$("#btnPrevWizard").addClass("disabled");
									$("#btnNextWizard").addClass("disabled");
									$.ajax({
										type : "POST",
										url : "processPurchaseOrder",
										data : {
											customTimePeriodId : customTimePeriodId,
											productPlanId : productPlanId
										},
										beforeSend : function() {
											$("#loader_page_common").show();
										},
										success : function(data) {
												if (data._ERROR_MESSAGE_ || data._ERROR_MESSAGE_LIST_) {
				 									jOlbUtil.alert.error(data._ERROR_MESSAGE_);
				 									$("#btnPrevWizard").removeClass("disabled");
													$("#btnNextWizard").removeClass("disabled");
				 								} else {
				 						        	if (data.orderId != undefined && data.orderId != null) {
				 						        		window.location.href = "viewDetailPO?orderId=" + data.orderId;
				 						        	}
				 								}
										},
										error : function(data) {
											alert("Send request is error");
											$("#btnPrevWizard").removeClass("disabled");
											$("#btnNextWizard").removeClass("disabled");
										},
										complete : function(
											data) {
											$("#loader_page_common").hide();
										},
									});
									createClick = true;
								}
							}, uiLabelMap.CommonCancel, uiLabelMap.OK, function (){
								createClick = false;
								btnClick = false;
							});
						}
						//btnClick = true;
					}
				}).on("stepclick", function(e) {
				// return false;//prevent clicking on steps
			});
	};

	var createCartAsync = function() {
		// process form first - general info - #initOrderEntry
		var supplierId = $("#supplierId").val();
		var facilityId = facilitySelected.facilityId;
		var currencyUomId = $("#currencyUomId").val();
		var contactMechId = null;
		var rows = $("#shippingContactMechGrid").jqxGrid('getrows');
		if (typeof rows != 'undefined' && rows != null && rows.length > 0){
			contactMechId = rows[0].contactMechId;
		}
		if (!contactMechId) {
			jOlbUtil.alert.error(uiLabelMap.BLFacilityNotHasAddress);
            $("#btnNextWizard").removeClass("disabled");
            $("#btnNextWizardTmp").removeClass("disabled");
			return false;	
		}
		var shipBeforeDate = $("#shipBeforeDate").jqxDateTimeInput("getDate")
				.getTime();
		var shipAfterDate = $("#shipAfterDate").jqxDateTimeInput("getDate")
				.getTime();

		var orderItems = [];
		var productIdTmps = []; 
		for ( var n in productOrderMap) {
			if (!validateObject(n)) continue;
			var itemMap = {};
			var item = productOrderMap[n];
			var quantity = item.quantityPurchase;
			if (typeof quantity === 'string') {
				quantity = quantity.replace(',', '.');
				quantity = parseFloat(quantity, 3, null);
		    }
			var quantityUomId = item.quantityUomId;
			var weightUomId = item.weightUomId?item.weightUomId:null;
			var lastPrice = item.lastPrice;
			
			var itemComment = null;
			if (item.itemComment) {
				itemComment = item.itemComment.trim().split('\n').join(' ');
			}
			var lastPriceStr = lastPrice.toString();
			if (locale == "vi") {
				lastPriceStr = lastPriceStr.replace(".", ",");
			}
			if (orderId != undefined && orderId != null){
				var quantityReceived = 0;
				var check = true;
				for (var y in listOrderItemInit){
					var _data = listOrderItemInit[y];
					var price = 0;
					if (typeof item.lastPrice == 'string') {
						price = parseFloat(_data.lastPrice.replace(",", "."));
					} else {
						price = item.lastPrice;
					}
					var price2 = 0;
					if (typeof item.lastPrice == 'number') {
						price2 = item.lastPrice;
					} else {
						price2 = parseFloat(item.lastPrice.replace(",", "."));
					}
					
					if (_data.productId == item.productId && _data.quantityUomId == item.quantityUomId && price == price2){
						var convert = _data.quantity/_data.quantityPurchase;
						quantityReceived = parseInt(item.quantityReceived/convert);
						check = false;
						break;
					}
				}
				if (!check){
					// add new order item
				}
				if (quantityReceived > 0){
					quantity = quantity + quantityReceived;
				}
			}
			productIdTmps.push(n);
			if (quantity > 0) {
				itemMap.productId = n;
				itemMap.quantity = quantity.toString();
				itemMap.quantityUomId = quantityUomId;
				itemMap.weightUomId = weightUomId;
				itemMap.lastPrice = lastPriceStr;
				if (itemComment != null && itemComment != undefined && itemComment != '' && itemComment != 'null'){
					itemMap.itemComment = itemComment;
				}
				orderItems.push(itemMap);
			}
		}
		if (orderId != undefined && orderId != null){
			var listItemReceived = $("#jqxgridOrderItemReceived").jqxGrid('getrows');
			if (listItemReceived && listItemReceived.length > 0){
				for (var u in listItemReceived) {
					var id1 = listItemReceived[u].productId;
					var checkTmp = false;
					for (var v in productIdTmps) {
						var id2 = productIdTmps[v];
						if (id1 == id2){
							checkTmp = true;
							break;
						}
					}
					if (!checkTmp){
						var itemMapTmp = {};
						itemMapTmp.productId = id1;
						var qty = listItemReceived[u].alternativeQuantity;
						itemMapTmp.quantity = qty.toString();
						itemMapTmp.quantityUomId = listItemReceived[u].quantityUomId;
						itemMapTmp.weightUomId = listItemReceived[u].weightUomId;
						var unitPriceStr = listItemReceived[u].alternativeUnitPrice.toString();
						if (locale == "vi") {
							unitPriceStr = unitPriceStr.replace(".", ",");
						}
						itemMapTmp.lastPrice = unitPriceStr;
						if (listItemReceived[u].itemComment != null && listItemReceived[u].itemComment != undefined && listItemReceived[u].itemComment != '' && listItemReceived[u].itemComment != 'null'){
							itemMapTmp.itemComment = listItemReceived[u].itemComment;
						}
						orderItems.push(itemMapTmp);
					}
				}
			}
		}
		var dataMap = {
			supplierId : supplierId,
			customTimePeriodId : customTimePeriodId,
			productPlanId : productPlanId,
			currencyUomId : currencyUomId,
			contactMechId : contactMechId,
			orderItems : JSON.stringify(orderItems),
			shipBeforeDate : shipBeforeDate,
			facilityId : facilityId,
			shipAfterDate : shipAfterDate
		};
		if (orderItems.length > 0) {
			$.ajax({
				type : "POST",
				url : "initPurchaseOrderEntry",
				data : dataMap,
				beforeSend : function() {
					$("#loader_page_common").show();
				},
				success : function(data) {
					if (data._ERROR_MESSAGE_ || data._ERROR_MESSAGE_LIST_) {
						jOlbUtil.alert.error(data._ERROR_MESSAGE_);
						$("#btnPrevWizard").removeClass("disabled");
						$("#btnNextWizard").removeClass("disabled")
					} else {
						jQuery("#btnNextWizard").css("display", "inline-block");
						jQuery("#btnNextWizardTmp").css("display", "none");
						jQuery("#btnNextWizard").trigger("click");
						$("#step2").html(data);
					}
				},
				error : function(data) {
					alert("Send request is error");
				},
				complete : function(data) {
					$("#btnNextWizardTmp").removeClass("disabled");
					$("#loader_page_common").hide();
				}
			});
		} else {
			if (orderId != undefined && orderId != null) {
				bootbox.dialog(uiLabelMap.BSYouNotYetChooseProduct + ". " + uiLabelMap.BPYouWantToCancelProductInOrder, 
				[{"label": uiLabelMap.Cancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        }, 
		        {"label": uiLabelMap.OK,
		            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": function() {
						var statusId = "ORDER_CANCELLED";
						var setItemStatus = "Y";
						changeOrderStatusCancel(orderId, statusId, setItemStatus);
		            }
		        }]);
			} else {
				jOlbUtil.alert.error(uiLabelMap.BSYouNotYetChooseProduct);
				$("#btnNextWizardTmp").removeClass("disabled");
				return false;
			}
		}
	};
	
	function changeOrderStatusCancel(orderId, statusId, setItemStatus){
		$.ajax({
			url: "changeOrderStatusPOCustom",
			type: "POST",
			data: {orderId: orderId, statusId: statusId, setItemStatus: setItemStatus},
			success: function(data) {
				window.location.href = "viewDetailPO?orderId=" + orderId;
			}
		})
	}
	var reloadPages = function(){
		window.location.reload();
	};
	
	function validateObject (object){
		if (object === undefined || object === null || object === '' || object === 'null'){
			return false;
		}
		return true;
	}
	
	return {
		init : init,
		reloadPages: reloadPages,
	};
	
}());