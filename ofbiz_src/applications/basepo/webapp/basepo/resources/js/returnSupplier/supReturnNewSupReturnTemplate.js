$(function() {
	supReturnNewTmp.init();
});

var supReturnNewTmp = (function() {
	var btnClick = false;
	var init = function() {
		initElement();
		initEvent();
		initElementComplex();
		initValidateForm();
	};

	var initElement = function() {
	};

	var initElementComplex = function() {
	};

	var initEvent = function() {

		$("#fuelux-wizard").ace_wizard().on("change", function(e, info) {
			if (info.step == 1 && (info.direction == "next")) {
				// check form valid
				$("#containerNotify").empty();
				var resultValidate = !supReturnInfo.getValidator().validate();
				if (resultValidate)
					return false;
				if (listProductSelected.length <= 0) {
					jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
					return false;
				}
                showConfirmPage();
			}
            // if (info.step == 2 && (info.direction == "next")) {
            //     // check form valid
            //     $("#containerNotify").empty();
            //     showConfirmPage();
            // }
		}).on("finished", function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureCreate, function() {
				Loading.show("loadingMacro");
				setTimeout(function() {
					if (!btnClick) {
						finishCreateSupReturn(listProductSelected, listProductPromoSelected);
						btnClick = true;
					}
					Loading.hide("loadingMacro");
				}, 500);
			}, uiLabelMap.CommonCancel, uiLabelMap.OK, function (){
				btnClick = false;
			});
		}).on("stepclick", function(e) {
			// prevent clicking on steps
		});
	};

	var finishCreateSupReturn = function(listProductSelected, listProductPromoSelected) {
		var supplier = $("#toPartyId").val();
		var currencyUomId = $("#currencyUomId").jqxDropDownList("val");
		var entryDate = $("#entryDate").jqxDateTimeInput("getDate").getTime();
		var description = $("#description").jqxInput("val").split("\n").join(" ")
		createNewReturnSupplier(supplier, currencyUomId, entryDate, description, listProductSelected, listProductPromoSelected);
	};

	function showConfirmPage() {
		var supSelectedId = $("#toPartyId").val();
		$("#supplierIdDT").text($("#supplier").val());
		var currencyUomId = $("#currencyUomId").jqxDropDownList("val");
		for (var i = 0; i < currencyUomData.length; i++) {
			var obj = currencyUomData[i];
			if (obj.uomId == currencyUomId) {
				$("#currencyUomIdDT").text(obj.description);
			}
		}
		if ($("#entryDate") != undefined && $("#entryDate").val() != "" && $("#entryDate") != null) {
			$("#entryDateDT").text(DatetimeUtilObj.formatFullDate($("#entryDate").jqxDateTimeInput("getDate")));
		}
		if ($("#description").val()) {
			$("#descriptionDT").text($("#description").val());
		}
		if (facilitySelected) {
			if (facilitySelected.facilityCode){
				$("#destinationFacilityIdDT").text(facilitySelected.facilityCode + ' - ' + facilitySelected.facilityName);
			} else {
				$("#destinationFacilityIdDT").text(facilitySelected.facilityId + ' - ' + facilitySelected.facilityName);
			}
		}
		var tmpSource = $("#jqxgridProductSelected").jqxGrid("source");
		if (typeof (tmpSource) != "undefined") {
			tmpSource._source.localdata = listProductSelected;
			$("#jqxgridProductSelected").jqxGrid("source", tmpSource);
		}
        tmpSource = $("#jqxgridProductPromoSelected").jqxGrid("source");
        if (typeof (tmpSource) != "undefined") {
            tmpSource._source.localdata = listProductPromoSelected;
            $("#jqxgridProductPromoSelected").jqxGrid("source", tmpSource);
        }
	}

    // function showProductPromoPage() {
    //     var supSelectedId = $("#toPartyId").val();
    //     $("#supplierIdPP").text($("#supplier").val());
    //     if ($("#entryDate") != undefined && $("#entryDate").val() != "" && $("#entryDate") != null) {
    //         $("#entryDatePP").text(DatetimeUtilObj.formatFullDate($("#entryDate").jqxDateTimeInput("getDate")));
    //     }
    //     if ($("#description").val()) {
    //         $("#descriptionPP").text($("#description").val());
    //     }
    //     if (facilitySelected) {
    //         if (facilitySelected.facilityCode){
    //             $("#destinationFacilityIdPP").text(facilitySelected.facilityCode + ' - ' + facilitySelected.facilityName);
    //         } else {
    //             $("#destinationFacilityIdPP").text(facilitySelected.facilityId + ' - ' + facilitySelected.facilityName);
    //         }
    //     }
    //     var listOrderIds = supReturnInfo.getListOrderIds();
    //     updateGridProductPromo(listOrderIds);
    // };

    var updateGridProductPromo = function(listOrderIds) {
        var listObj = [];
        for (var i = 0; i < listOrderIds.length; i++) {
            var row = {};
            row["orderId"] = listOrderIds[i];
            listObj.push(row);
        }
        listObj = JSON.stringify(listObj);
        var listOrderItems = [];
        $.ajax({
            type : "POST",
            url : "getOrderPromoItemsByOrdersToReturn",
            data : {
                "listOrderIds" : listObj
            },
            dataType : "json",
            async : false,
            success : function(response) {
                listOrderItems = response.listOrderItems;
            },
            error : function(response) {
                alert("Error:" + response);
            }
        }).done(function() {
            SupReturnProductPromoObj.loadProductPromo(listOrderItems);
        });
    };

	var createNewReturnSupplier = function(supplier, currencyUomId, entryDate, description, listProductSelected, listProductPromoSelected) {
		for (x in listProductSelected){
			delete listProductSelected[x]["productName"];
			delete listProductSelected[x]["itemDescription"];
			if (listProductSelected[x].weightUomId === undefined || listProductSelected[x].weightUomId === null || listProductSelected[x].weightUomId === '' || listProductSelected[x].weightUomId === 'null'){
				delete listProductSelected[x]['weightUomId'];
			}
		}
		listProductSelected = JSON.stringify(listProductSelected);
        listProductPromoSelected = JSON.stringify(listProductPromoSelected);
		$.ajax({
			beforeSend : function() {
				$("#loader_page_common").show();
			},
			complete : function() {
				$("#loader_page_common").hide();
			},
			url : "createNewReturnSupplierJson",
			type : "POST",
			data : {
				returnHeaderTypeId : "VENDOR_RETURN",
				statusId : "SUP_RETURN_REQUESTED",
				toPartyId : supplier,
				currencyUomId : currencyUomId,
				needsInventoryReceive : "N",
				destinationFacilityId : facilitySelected.facilityId,
				description : description,
				orderItems : listProductSelected,
                adjustmentPromoItems: listProductPromoSelected,
			},
			dataType : "json",
			success : function(data) {
				if (!data._ERROR_MESSAGE_) {
					window.location.href = "viewGeneralReturnSupplier?returnId="
							+ data.returnId;
				}
			}
			}).done(function(data) {
		});
	};

	var initValidateForm = function() {
		var mapRules = [];
		var extendRules = [];
	};

	return {
		init : init,
	};
}());