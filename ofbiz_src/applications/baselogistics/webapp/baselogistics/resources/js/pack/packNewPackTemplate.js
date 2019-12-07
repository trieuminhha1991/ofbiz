$(function () {
    PackTemplateObj.init();
});
var PackTemplateObj = (function () {
    var btnClick = false;
    var init = function () {
        initInputs();
        initElementComplex();
        initEvents();
        initValidateForm();
    };
    var initInputs = function () {
    };
    var initElementComplex = function () {
    };
    var initEvents = function () {
        $('#fuelux-wizard').ace_wizard().on('change', function (e, info) {
            if (info.step == 1 && (info.direction == "next")) {
                // check form valid
                $('#containerNotify').empty();
                var resultValidate = !PackInfoObj.getValidator().validate();
                if (resultValidate) return false;
                listProductSelected=OlbReqProduct.getProductGrid().getGridObj().jqxGrid('getrows');
                if (listProductSelected.length <= 0) {
                    jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
                    return false;
                }
                showConfirmPage();
            }
        }).on('finished', function (e) {
            finishCreateTransfer();
        }).on('stepclick', function (e) {
            //prevent clicking on steps
        });
    };

    function showConfirmPage() {


        for (var i = 0; i < shipmentMethodData.length; i++) {
            if (shipmentMethodData[i].shipmentMethodTypeId == $('#shipmentMethodTypeId').val()) {
                $('#shipmentMethodTypeIdDT').text(shipmentMethodData[i].description);
                break;
            }
        }

        if ($("#description").val()) {
            $("#descriptionDT").text($("#description").val());
        }
        var customerDt = $("#customerId").jqxDropDownButton('getContent');
        if (customerDt) {
            $('#customerIdDT').text(customerDt.text());
        }

        var customerDt = $("#destContactMechId").jqxDropDownButton('getContent');
        if (customerDt) {
            $('#destContactMechDT').text(customerDt.text());
        }

        if ($("#shipBeforeDate") != undefined && $("#shipBeforeDate").val() != '' && $("#shipBeforeDate") != null) {
            $("#shipBeforeDateDT").text(DatetimeUtilObj.formatFullDate($("#shipBeforeDate").jqxDateTimeInput('getDate')));
        }
        if ($("#shipAfterDate") != undefined && $("#shipAfterDate").val() != '' && $("#shipAfterDate") != null) {
            $("#shipAfterDateDT").text(DatetimeUtilObj.formatFullDate($("#shipAfterDate").jqxDateTimeInput('getDate')));
        } else {
            $("#shipAfterDateDT").text('');
        }

        if ($("#tableProduct").length > 0) {
            var totalValue = 0;
            $('#tableProduct tbody').empty();
            var tableRef = document.getElementById('tableProduct').getElementsByTagName('tbody')[0];
            for (var i in listProductSelected) {
                var data = listProductSelected[i];
                var newRow = tableRef.insertRow(tableRef.rows.length);
                var newCell0 = newRow.insertCell(0);
                var newText = document.createTextNode(i);
                newCell0.appendChild(newText);

                var newCell1 = newRow.insertCell(1);
                newText = document.createTextNode(data.productCode);
                newCell1.appendChild(newText);

                var newCell2 = newRow.insertCell(2);
                newText = document.createTextNode(data.deliveryId);
                newCell2.appendChild(newText);

                var newCell2 = newRow.insertCell(3);
                newText = document.createTextNode(data.productName);
                newCell2.appendChild(newText);

                var newCell3 = newRow.insertCell(4);
                if (data.description) {
                    newText = document.createTextNode(data.description);
                } else {
                    newText = document.createTextNode("");
                }
                newCell3.appendChild(newText);

                var newCell4 = newRow.insertCell(5);
                newCell4.className = 'align-right';
                newText = document.createTextNode(data.quantity + " (" + getUomDesc(data.quantityUomId) + ")");
                newCell4.appendChild(newText);

                var newCell5 = newRow.insertCell(6);
                newText = document.createTextNode(getUomDesc(data.quantityUomId));
                newCell5.appendChild(newText);

            }
            if (totalValue) {
                var newRowTotal = tableRef.insertRow(tableRef.rows.length);
                var newCellTotal0 = newRowTotal.insertCell(0);
                newCellTotal0.colSpan = 8;
                newCellTotal0.className = 'align-right';
                newCellTotal0.style.fontWeight = "bold";
                newCellTotal0.style.background = "#f2f2f2";
                var str = uiLabelMap.OrderItemsSubTotal.toUpperCase();
                var newTextTotal = document.createTextNode(str);
                newCellTotal0.appendChild(newTextTotal);

                var newCellTotal8 = newRowTotal.insertCell(1);
                newCellTotal8.className = 'align-right';
                newCellTotal8.style.background = "#f2f2f2";
                var newTextTotal = document.createTextNode(formatnumber(totalValue));
                newCellTotal8.appendChild(newTextTotal);
            }
        }
    }

    function finishCreateTransfer() {
        var listProducts = [];
        if (listProductSelected != undefined && listProductSelected.length > 0) {
            for (var i = 0; i < listProductSelected.length; i++) {
                var map = {};
                var data = listProductSelected[i];
                var key = 'row' + data.productId;

                var stringDate = null;
                var map = {};
                map['productId'] = data.productId;
                map['deliveryId'] = data.deliveryId;
                map['deliveryItemSeqId'] = data.deliveryItemSeqId;
                if (data.expiredDate) {
                    map['expireDate'] = data.expiredDate.getTime();
                }
                map['quantity'] = data.quantity;
                map['quantityUomId'] = data.quantityUomId;
                listProducts.push(map);
            }
        }
        // check enough to warning
        var listProducts = JSON.stringify(listProducts);
        var mess = uiLabelMap.AreYouSureCreate;
        var shipBeforeDateTmp = null;
        var shipAfterDateTmp = null;
        if ($("#shipBeforeDate").val()) {
            shipBeforeDateTmp = $("#shipBeforeDate").jqxDateTimeInput('getDate').getTime();
        }
        if ($("#shipAfterDate").val()) {
            shipAfterDateTmp = $("#shipAfterDate").jqxDateTimeInput('getDate').getTime();
        }
        var destAddress = PackInfoObj.getShippingAddressDDB();
        jOlbUtil.confirm.dialog(mess, function () {
            if (!btnClick) {
                Loading.show('loadingMacro');
                setTimeout(function () {
                    $.ajax({
                        type: 'POST',
                        url: 'createPack',
                        async: false,
                        data: {
                            customerId: PackInfoObj.getCustomerDDB().getValue(),
                            destContactMechId: PackInfoObj.getShippingAddressDDB().getValue(),
                            shipBeforeDate: shipBeforeDateTmp,
                            shipAfterDate: shipAfterDateTmp,
                            //description: $("#description").jqxInput('val').split('\n').join(' '),
                            listProducts: listProducts,
                            //packId:$("#packId").val()
                        },
                        beforeSend: function () {
                            $("#btnPrevWizard").addClass("disabled");
                            $("#btnNextWizard").addClass("disabled");
                            $("#loader_page_common").show();
                        },
                        success: function (data) {
                            viewPackDetail(data.packId);
                        },
                        error: function (data) {
                            alert("Send request is error");
                        },
                        complete: function (data) {
                            $("#loader_page_common").hide();
                            $("#btnPrevWizard").removeClass("disabled");
                            $("#btnNextWizard").removeClass("disabled");
                        },
                    });
                    Loading.hide('loadingMacro');
                }, 500);
                btnClick = true;
            }
        }, uiLabelMap.CommonCancel, uiLabelMap.OK, function () {
            btnClick = false;
        });
    }

    function viewPackDetail(packId) {
        window.location.href = 'viewDetailPack?packId=' + packId;
    }

    var initValidateForm = function () {

    };
    var reloadPages = function () {
        window.location.reload();
    };
    return {
        init: init,
        viewPackDetail: viewPackDetail,
        reloadPages: reloadPages,
    }
}());