var deliveryDDB, distributorGroupDDB, vehicleDDB, contractorDDB;

$(function () {
    OlbQuotationInfo.init();
});
var OlbQuotationInfo = (function () {
    var validatorVAL;

    var init = function () {
        initElement();
        initElementComplex();
        initEvent();
        initValidateForm();
    };
    var initElement = function () {
        //$('#description').jqxTextArea({height: 40, width: '100%', minLength: 1});

        jOlbUtil.dateTimeInput.create("#requiredByDate", {
            width: '100%',
            allowNullDate: true,
            value: null,
            disabled: true
        });
        jOlbUtil.dateTimeInput.create("#requirementStartDate", {
            width: '100%',
            allowNullDate: true,
            value: null
        });
        $('#totalWeightId').val(0);
        $('#totalWeightId').text($('#totalWeightId').val() + " kg");
        if (typeof(requirementSelected.requiredByDate) != "undefined") {
            $('#requiredByDate').jqxDateTimeInput('setDate', requirementSelected.fromDate);
        } else {
            $('#requiredByDate').jqxDateTimeInput('setDate', new Date());
        }
        if (typeof(requirementSelected.requirementStartDate) != "undefined") {
            $('#requirementStartDate').jqxDateTimeInput('setDate', requirementSelected.thruDate);
        }
    };
    var processDataRowSelect = function (rowBoundIndex) {
        var data = $("#jqxgridDelivery").jqxGrid("getrowdata", rowBoundIndex);
        if (data) {
            var idStr = data.deliveryId;
            var weight = parseFloat($('#totalWeightId').val()) + parseFloat(data.totalWeight);
            $('#totalWeightId').val(weight.toFixed(2));
            $('#totalWeightId').text($('#totalWeightId').val() + " kg");
            if (typeof(productPricesMap[idStr]) != "undefined") {
                var itemValue = productPricesMap[idStr];
                itemValue.selected = true;
                productPricesMap[idStr] = itemValue;
            } else {
                var itemValue = {};
                itemValue.deliveryId = data.deliveryId;
                itemValue.partyIdFrom = data.partyIdFrom;
                itemValue.partyIdTo = data.partyIdTo;
                itemValue.orderId = data.orderId;
                itemValue.deliveryDate = data.deliveryDate;
                itemValue.createDate = data.createDate;
                itemValue.totalWeight = data.totalWeight;
                itemValue.destContactMechId = data.destContactMechId;
                itemValue.originContactMechId = data.originContactMechId;
                itemValue.selected = true;
                productPricesMap[idStr] = itemValue;
            }
        }
    };

    var initElementComplex = function () {
        initContractor();
        initVehicle();
        initDistributorGroup();
        initDeliveryTable();
    };

    var initDeliveryTable = function () {

        var datafields = [
            {name: 'deliveryId', type: 'string'},
            {name: 'partyIdFrom', type: 'string'},
            {name: 'partyIdTo', type: 'string'},
            {name: 'deliveryDate', type: 'date', other: 'Timestamp'},
            {name: 'orderId', type: 'string'},
            {name: 'createDate', type: 'date', other: 'Timestamp'},
            {name: 'destContactMechId', type: 'string'},
            {name: 'originContactMechId', type: 'string'},
            {name: 'statusId', type: 'string'},
            {name: 'totalWeight', type: 'string'}
        ];

        var columnlist = [
            {
                text: uiLabelMap.BDDeliveryId,
                dataField: 'deliveryId',
                pinned: true,
                width: '13%'
            },
            {
                text: uiLabelMap.BDPartyIdFrom,
                dataField: 'partyIdFrom',
                cellClassName: cellClass,
                width: '20%',
                cellsrenderer: function (row, column, value) {
                    var partyName = value;
                    $.ajax({
                        url: 'getPartyName',
                        type: 'POST',
                        data: {partyId: value},
                        dataType: 'json',
                        async: false,
                        success: function (data) {
                            if (!data._ERROR_MESSAGE_) {
                                partyName = data.partyName;
                            }
                        }
                    });
                    return '<span title' + value + '>' + partyName + '</span>';
                }
            },
            {
                text: uiLabelMap.BDPartyIdTo,
                dataField: 'partyIdTo',
                cellClassName: cellClass,
                width: '20%',
                cellsrenderer: function (row, column, value) {
                    var partyName = value;
                    $.ajax({
                        url: 'getPartyName',
                        type: 'POST',
                        data: {partyId: value},
                        dataType: 'json',
                        async: false,
                        success: function (data) {
                            if (!data._ERROR_MESSAGE_) {
                                partyName = data.partyName;
                            }
                        }
                    });
                    return '<span title' + value + '>' + partyName + '</span>';
                }
            },
            {
                text: uiLabelMap.BDDesContactMechId,
                dataField: 'destContactMechId',
                cellClassName: cellClass,
                width: '25%',
                cellsrenderer: function (row, column, value) {
                    var address = value;
                    $.ajax({
                        url: 'getContactMechName',
                        type: 'POST',
                        data: {contactMechId: value},
                        dataType: 'json',
                        async: false,
                        success: function (data) {
                            if (!data._ERROR_MESSAGE_) {
                                address = data.fullName;
                            }
                        }
                    });
                    return '<span title' + value + '>' + address + '</span>';
                }
            },
            {
                text: uiLabelMap.BDTotalWeight + '(kg)',
                dataField: 'totalWeight',
                cellsformat: 'd'
            },
            {
                text: uiLabelMap.CommonStatus,
                dataField: 'statusId',
                cellClassName: cellClass,
                filtertype: 'checkedlist',
                width: '13%',
                cellsrenderer: function (row, column, value) {
                    if (orderStatusData.length > 0) {
                        for (var i = 0; i < orderStatusData.length; i++) {
                            if (value == orderStatusData[i].statusId) {
                                return '<span>' + orderStatusData[i].description + '</span>';
                            }
                        }
                    }
                    return '<span title=' + value + '>' + value + '</span>';
                },
                createfilterwidget: function (column, columnElement, widget) {
                    if (orderStatusData.length > 0) {
                        var filterDataAdapter = new $.jqx.dataAdapter(orderStatusData, {
                            autoBind: true
                        });
                        var records = filterDataAdapter.records;
                        widget.jqxDropDownList({
                            source: records, displayMember: 'statusId', valueMember: 'statusId',
                            renderer: function (index, label, value) {
                                if (orderStatusData.length > 0) {
                                    for (var i = 0; i < orderStatusData.length; i++) {
                                        if (orderStatusData[i].statusId == value) {
                                            return '<span>' + orderStatusData[i].description + '</span>';
                                        }
                                    }
                                }
                                return value;
                            }
                        });
                        widget.jqxDropDownList('checkAll');
                    }
                }
            },
            {
                text: uiLabelMap.BDDeliveryDate,
                dataField: 'deliveryDate',
                cellClassName: cellClass,
                cellsformat: 'dd/MM/yyyy',
                filtertype: 'range',
                width: '13%',
                cellsrenderer: function (row, column, value) {
                    return '<span>' + jOlbUtil.dateTime.formatFullDate(value) + '</span>';
                }
            },
            {
                text: uiLabelMap.BDCreateDate,
                dataField: 'createDate',
                cellClassName: cellClass,
                cellsformat: 'dd/MM/yyyy',
                filtertype: 'range',
                width: '13%',
                cellsrenderer: function (row, colum, value) {
                    return '<span>' + jOlbUtil.dateTime.formatFullDate(value) + '</span>';
                }
            }];


        var configDelivery = {
            datafields: datafields,
            columns: columnlist,
            showdefaultloadelement: false,
            autoshowloadelement: false,
            useUtilFunc: false,
            dropDownHorizontalAlignment: 'right',
            editmode: "click",
            selectionmode: "checkbox",
            useUrl: true,
            url: "jqxGeneralServicer?sname=JQGetListDelivery",
            clearfilteringbutton: false,
            editable: false,
            alternativeAddPopup: 'alterpopupWindow',
            filterable: true,
            showfilterrow: true,
            pageable: true,
            pagesize: 15,
            showtoolbar: false,
            width: '100%',
            bindresize: true,
            groupable: true,
            localization: getLocalization(),
            showtoolbar: true,
            showdefaultloadelement: true,
            autoshowloadelement: true,
            virtualmode: true
        };

        deliveryDDB = new OlbGrid($("#jqxgridDelivery"), null, configDelivery, []);
    };

    var initContractor = function () {
        var configPartyGroup = {
            useUrl: true,
            root: 'results',
            widthButton: '100%',
            showdefaultloadelement: false,
            autoshowloadelement: false,
            datafields: [{name: 'partyId', type: 'string'}, {name: 'groupName', type: 'string'}],
            columns: [
                {text: uiLabelMap.BDPartyId, datafield: 'partyId', width: '30%'},
                {text: uiLabelMap.BDGroupName, datafield: 'groupName', width: '70%'},
            ],
            url: 'jqGetListPartySupplier',
            useUtilFunc: true,
            key: 'partyId',
            description: function (rowData) {
                if (rowData) {
                    var descriptionValue = rowData['groupName'];
                    return descriptionValue;
                }
            },
            autoCloseDropDown: true,
            filterable: true,
            sortable: true
        };
        contractorDDB = new OlbDropDownButton($("#contractorId"), $("#contractorGrid"), null, configPartyGroup, []);
    };

    var initVehicle = function () {
        var vehicleConfig = {
            useUrl: true,
            root: 'results',
            widthButton: '100%',
            showdefaultloadelement: false,
            autoshowloadelement: false,
            datafields: [{name: 'vehicleId', type: 'string'}, {
                name: 'licensePlate',
                type: 'string'
            }],
            columns: [
                {text: uiLabelMap.BDVehicleId, datafield: 'vehicleId', width: '30%'},
                {text: uiLabelMap.BDLicensePlate, datafield: 'licensePlate', width: '70%'},
            ],
            url: '', //JQGetVehicleBySupplier&SupplierId=xxx
            useUtilFunc: true,
            key: 'vehicleId',
            description: function (rowData) {
                if (rowData) {
                    var descriptionValue = rowData['licensePlate'];
                    return descriptionValue;
                }
            },
            autoCloseDropDown: true,
            filterable: true,
            sortable: true,
        };
        vehicleDDB = new OlbDropDownButton($("#vehicleId"), $("#vehicleGrid"), null, vehicleConfig, []);
    };

    var initDistributorGroup = function () {
        // var configDistributorGroup = {
        //     useUrl: false,
        //     root: 'results',
        //     widthButton: '100%',
        //     showdefaultloadelement: false,
        //     autoshowloadelement: false,
        //     datafields: [{
        //         name: 'productStoreGroupId',
        //         type: 'string'
        //     }, {name: 'productStoreGroupName', type: 'string'}],
        //     columns: [
        //         {
        //             text: uiLabelMap.BDProductStoreGroupId,
        //             datafield: 'productStoreGroupId',
        //             width: '30%'
        //         },
        //         {
        //             text: uiLabelMap.BDProductStoreGroupName,
        //             datafield: 'productStoreGroupName',
        //             width: '70%'
        //         },
        //     ],
        //     // url: 'JQGetProductStoreGroup',
        //     useUtilFunc: true,
        //     key: 'productStoreGroupId',
        //     description: function (rowData) {
        //         if (rowData) {
        //             var descriptionValue = rowData['productStoreGroupName'];
        //             return descriptionValue;
        //         }
        //     },
        //     autoCloseDropDown: true,
        //     filterable: true,
        //     sortable: true
        // };
        // distributorGroupDDB = new OlbDropDownButton($("#distributorGroupId"), $("#distributorGroupGrid"), allGroup, configDistributorGroup, []);

        var configDistributorGroup = {
            width:'100%',
            height: 25,
            key: "productStoreGroupId",
            value: "productStoreGroupName",
            displayDetail: false,
            dropDownWidth: 'auto',
            placeHolder: uiLabelMap.BSClickToChoose,
            autoDropDownHeight: true,
            addNullItem: true
        };
        distributorGroupDDB = new OlbDropDownList($("#distributorGroupId"), allGroup, configDistributorGroup, []);
    };

    var selectableCheckBox = true;
    var updateDeliveryOptions = function (productStoreGroupId) {
        deliveryDDB.updateSource("jqxGeneralServicer?sname=JQGetDeliveryByGroup&productStoreGroupId=" + productStoreGroupId, null, function () {
        });
    };

    var updateVehicleOptions = function (supplierId) {
        vehicleDDB.updateSource("jqxGeneralServicer?sname=JQGetVehicleBySupplierId&supplierId=" + supplierId, null, function () {
        });
    }

    var initEvent = function () {
        distributorGroupDDB.selectListener(function (rowData) {
            var productStoreGroupId = rowData['value'];
            updateDeliveryOptions(productStoreGroupId);
        });


        contractorDDB.getGrid().rowSelectListener(function(rowData) {
            var supplierId =rowData['partyId'];
            updateVehicleOptions(supplierId);

        });

        $("#jqxgridDelivery").on("bindingcomplete", function (event) {
            var dataRow = $("#jqxgridDelivery").jqxGrid("getboundrows");
            if (typeof(dataRow) != 'undefined') {
                var icount = 0;
                $.each(dataRow, function (key, value) {
                    if (value) {
                        var isSelected = false;
                        var idStr = value.deliveryId;
                        if (typeof(productPricesMap[idStr]) != "undefined") {
                            var itemValue = productPricesMap[idStr];
                            if (itemValue.selected) {
                                $('#jqxgridDelivery').jqxGrid('selectrow', icount);
                                isSelected = true;
                            }
                        }
                        if (OlbElementUtil.isNotEmpty(value.orderId) && !isSelected) {
                            $('#jqxgridDelivery').jqxGrid('unselectrow', icount);
                        }
                    }
                    icount++;
                });
                selectableCheckBox = true;
            }
        });
        $('#jqxgridDelivery').on('rowselect', function (event) {
            if (selectableCheckBox) {
                var args = event.args;
                var rowBoundIndex = args.rowindex;
                if (Object.prototype.toString.call(rowBoundIndex) === '[object Array]') {
                    for (var i = 0; i < rowBoundIndex.length; i++) {
                        processDataRowSelect(rowBoundIndex[i]);
                    }
                } else {
                    processDataRowSelect(rowBoundIndex);
                }
            }
        });

        $('#contractorGrid').on('rowselect', function (event) {
            var args = event.args;
            var rowBoundIndex = args.rowindex;
            var data = $("#contractorGrid").jqxGrid("getrowdata", rowBoundIndex);
            $("#strContractorId").text(data.groupName);
        });

        $('#vehicleGrid').on('rowselect', function (event) {
            var args = event.args;
            var rowBoundIndex = args.rowindex;
            var data = $("#vehicleGrid").jqxGrid("getrowdata", rowBoundIndex);
            $("#strVehicleName").text(data.licensePlate + '[' + data.vehicleId + ']');
        });

        $('#jqxgridDelivery').on('rowunselect', function (event) {
            var rowindexes = $("#jqxgridDelivery").jqxGrid("getselectedrowindexes");
            if (selectableCheckBox) {
                var args = event.args;
                var rowBoundIndex = args.rowindex;
                var data = $("#jqxgridDelivery").jqxGrid("getrowdata", rowBoundIndex);
                if (typeof(data) != 'undefined') {
                    var idStr = data.deliveryId;
                    var weight = parseFloat($('#totalWeightId').val()) - parseFloat(data.totalWeight);
                    $('#totalWeightId').val(weight.toFixed(2));
                    $('#totalWeightId').text($('#totalWeightId').val() + " kg");

                    if (typeof(productPricesMap[idStr]) != "undefined") {
                        var itemValue = productPricesMap[idStr];
                        itemValue.selected = false;
                        productPricesMap[idStr] = itemValue;
                    }
                }
            }
        });
    };
    var initValidateForm = function () {
        var mapRules = [
            {input: '#requiredByDate', type: 'validDateTimeInputNotNull'},
            {input: '#requirementStartDate', type: 'validDateTimeInputNotNull'},
            {input: '#requirementStartDate', type: 'validDateCompareToday'},
            {input: '#contractorId', type: 'validObjectNotNull', objType: 'dropDownButton'},
            {
                input: '#requiredByDate, #requirementStartDate',
                type: 'validCompareTwoDate',
                paramId1: "requiredByDate",
                paramId2: "requirementStartDate"
            }
        ];
        validatorVAL = new OlbValidator($('#initRequirementEntry'), mapRules, null, {scroll: true});
    };
    var getValidator = function () {
        return validatorVAL;
    };
    return {
        init: init,
        getValidator: getValidator
    };
}());