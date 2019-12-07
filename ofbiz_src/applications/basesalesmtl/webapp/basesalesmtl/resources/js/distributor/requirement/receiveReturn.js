$(document).ready(function() {
	ReceiveReturn.init($("#jqxgridSO"));
});
var listProductSelected = [];
var mapPriceEdit = {};
var mapProductSelected = {};
var mapReasonEdit = {};
if (typeof (ReceiveReturn) == "undefined") {
	var flagX = true;
	var ReceiveReturn = (function() {
		var mainGrid, btnReceive;

        var init = function(grid) {
            mainGrid = grid;
            btnReceive = $("#btnReceiveProduct");
            initJqxElements();
            handleEvents();
            initValidator();
        };
		var initJqxElements = function() {
			$("#facilityId").jqxDropDownList({ source: facilities, displayMember: "facilityName", valueMember: "facilityId", width: "200px", theme: "olbius", placeHolder: multiLang.filterchoosestring,
				dropDownHeight: 250, autoDropDownHeight: true, selectedIndex: 0});
			$("#jqxNotification").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
		};
		var handleEvents = function() {
			mainGrid.on('rowselect', function (event) {
			    var args = event.args;
			    var rowBoundIndex = args.rowindex;
			    mainGrid.jqxGrid('begincelledit', rowBoundIndex, "quantity");
			    if (btnReceive.hasClass("disabled")) {
					btnReceive.removeClass("disabled");
				}
			});
			mainGrid.on('rowunselect', function (event) {
				if (_.isEmpty(mainGrid.jqxGrid("getselectedrowindexes"))) {
					if (!btnReceive.hasClass("disabled")) {
						btnReceive.addClass("disabled");
					}
				} else {
					if (btnReceive.hasClass("disabled")) {
						btnReceive.removeClass("disabled");
					}
				}
			});

            $('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
                if(info.step == 1 && (info.direction == "next")) {
                    // check form valid
                    // if(!OlbNewTripStep1.getValidator().validate()) return false;

                    // var isCheckValid = checkValidManual();
                    // if (!isCheckValid) return false;
                    if(!checkValidManual()) return false;
                    transferDataToConfirm();
                }
            }).on('finished', function(e) {
                    jOlbUtil.confirm.dialog(uiLabelMap.BSAreYouSureYouWantToReceiveReturn, function() {
                        if (flagX) {
                            if ($("#containerWarehouse").jqxValidator("validate")) {                                
                                var data = new Array();
                                for ( var x in listProductSelected) {
                                	var dataItem = {};
                                    dataItem = {
                                    		productId: listProductSelected[x].productId,
                                            description: listProductSelected[x].description,
                                            quantityUomId: listProductSelected[x].quantityUomId,
                                            returnQuantity: listProductSelected[x].quantity,
                                            returnReasonId: listProductSelected[x].returnReason,
                                            returnPrice: listProductSelected[x].returnPrice
                                    };
                                    if( dataItem.returnPrice == null || dataItem.returnPrice == 0){
                                    	dataItem.returnPrice = ""
                                    }
                                    if( dataItem.returnReasonId == null){
                                    	dataItem.returnReasonId = ""
                                    }
                                    data.push(dataItem);
                                }
                                if (data) {
                                    flagX = false;
                                    $.ajax({
                                        url: "distributorReceiveReturn",
                                        type: "POST",
                                        async: true,
                                        data: {
                                            items: JSON.stringify(data),
                                            destinationFacilityId: $("#facilityId").jqxDropDownList("val")
                                        },
                                        beforeSend: function(){
                                            $("#loader_page_common").show();
                                        },
                                        success: function(data) {
                                            notify(data);
                                        },
                                        complete: function(data){
                                            $("#loader_page_common").hide();
                                        }
                                    });
                                }
                            }
                        }
                    });
            }).on('stepclick', function(e){
                //prevent clicking on steps
            });

			// btnReceive.click(function() {
			// 	if (flagX) {
			// 		if ($("#containerWarehouse").jqxValidator("validate")) {
			// 			var rowindexes = mainGrid.jqxGrid("getselectedrowindexes");
			// 			var data = new Array();
			// 			for ( var x in rowindexes) {
			// 				data.push({
			// 					productId: mainGrid.jqxGrid('getcellvalue', rowindexes[x], "productId"),
			// 					description: mainGrid.jqxGrid('getcellvalue', rowindexes[x], "productName"),
			// 					quantityUomId: mainGrid.jqxGrid('getcellvalue', rowindexes[x], "quantityUomId"),
			// 					returnQuantity: mainGrid.jqxGrid('getcellvalue', rowindexes[x], "quantity"),
			// 					returnReasonId: mainGrid.jqxGrid('getcellvalue', rowindexes[x], "returnReason")
			// 				});
			// 			}
			// 			if (data) {
			// 				flagX = false;
			// 				DataAccess.execute({ url: "distributorReceiveReturn",
			// 									 data: { items : JSON.stringify(data),
			// 										 	destinationFacilityId: $("#facilityId").jqxDropDownList("val")}
			// 									}, ReceiveReturn.notify);
			// 			}
			// 		}
			// 	}
			// });
		};

        var checkValidManual = function(){
            var rowindexes = mainGrid.jqxGrid("getselectedrowindexes");
            if(rowindexes.length <= 0) {
                jOlbUtil.alert.error(uiLabelMap.BSYouNotYetChooseRecord);
                return false;
            }
            return true;
        };

		var cellendedit = function (row, datafield, columntype, oldvalue, newvalue) {
			var rowData = mainGrid.jqxGrid('getrowdata', row);
			if(rowData){
				switch (datafield) {
				case "quantity":				
					if (newvalue > 0) {
	                    mainGrid.jqxGrid('selectrow', row);
	                    $.each(listProductSelected, function(i){
							var olb = listProductSelected[i];
							if (olb.productId == rowData.productId ){
								listProductSelected.splice(i,1);
								return false;
							}
						});
						var item = $.extend({}, rowData);
						item.quantity = newvalue;
						mapProductSelected[item.productId] = newvalue;
						listProductSelected.push(item);	
					} else {
						mainGrid.jqxGrid('selectrow', row);
						var item = $.extend({}, rowData);
						mapProductSelected[item.productId] = 0;
	                    $.each(listProductSelected, function(i){
							var olb = listProductSelected[i];
							if (olb.productId == rowData.productId ){
								listProductSelected.splice(i,1);
								return false;
							}
						});
						mainGrid.jqxGrid('unselectrow', row);
						mainGrid.jqxGrid('setcellvalue', row, "returnReason", null);
						mainGrid.jqxGrid('setcellvalue', row, "returnPrice", null);
					}
					break;
				case "returnReason":
					var quantity = mapProductSelected[rowData.productId];
					if (newvalue) {
						if (quantity > 0) {
							mainGrid.jqxGrid('selectrow', row);
							$.each(listProductSelected, function(i){
								var olb = listProductSelected[i];
								if (olb.productId == rowData.productId ){
									listProductSelected.splice(i,1);
									return false;
								}
							});
							var item = $.extend({}, rowData);
							item.returnReason = newvalue;
							listProductSelected.push(item);	
							
						} else {
							setTimeout(function() {
					    		mainGrid.jqxGrid('begincelledit', row, "quantity");
							}, 200);
						}
						mapReasonEdit[rowData.productId] = newvalue;
					}
					break;
				case "returnPrice":
					var quantity = mapProductSelected[rowData.productId];
					if (newvalue) {
						if (quantity > 0) {
							mainGrid.jqxGrid('selectrow', row);
							$.each(listProductSelected, function(i){
								var olb = listProductSelected[i];
								if (olb.productId == rowData.productId ){
									listProductSelected.splice(i,1);
									return false;
								}
							});
							var item = $.extend({}, rowData);
							item.returnPrice = newvalue;
							listProductSelected.push(item);
							
						} else {
							setTimeout(function() {
					    		mainGrid.jqxGrid('begincelledit', row, "quantity");
							}, 200);
						}
						mapPriceEdit[rowData.productId] = newvalue;
					}
					break;
				default:
					break;
				}
			}
	    };
	    var initValidator = function() {
	    	$("#containerWarehouse").jqxValidator({
			    rules: [
			        { input: '#facilityId', message: multiLang.fieldRequired, action: 'valueChanged',
				           	rule: function (input, commit) {
				           		if (input.jqxDropDownList("val")) {
									return true;
								}
				           		return false;
				           	}
				        }],
			           position: 'bottom'
			});
		};

	    var transferDataToConfirm = function() {
            var tmpSource = $("#jqxgridOrderConfirm").jqxGrid('source');

            // var m_vehicleId = jOlbUtil.getAttrDataValue('vehicleId');
            if(listProductSelected != 'undefined'){             
                tmpSource._source.localdata = listProductSelected;
                $("#jqxgridOrderConfirm").jqxGrid('source', tmpSource);
            }
        }
		var notify = function(res) {
			Loading.hide();
			flagX = true;
			$("#jqxNotification").jqxNotification("closeLast");
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotification").jqxNotification({ template: "error"});
				$("#notificationContent").text(multiLang.updateError);
				$("#jqxNotification").jqxNotification("open");
			}else {
				$("#jqxNotification").jqxNotification({ template: "info"});
				$("#notificationContent").text(multiLang.updateSuccess);
				$("#jqxNotification").jqxNotification("open");
				setTimeout(function() {
					window.location.href = "viewReturnOrder?returnId=" + res.returnId;;
				}, 1000);
			}
		};
		return {
            init: init,
			notify: notify,
			cellendedit: cellendedit
		}
	})();
}