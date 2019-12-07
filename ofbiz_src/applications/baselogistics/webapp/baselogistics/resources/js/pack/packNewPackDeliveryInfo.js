$(function () {
    PackInfoObj.init();
});
var PackInfoObj = (function () {
    var validatorVAL;
    var init = function () {
        initInputs();
        initElementComplex();
        initEvents();
        initValidateForm();
    };
    var initInputs = function () {
        var originContactData = [];
        var destContactData = [];

        var partyTmpData = [];
        //$('#originContactMechId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: originContactData, selectedIndex: 0, width: 300, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
        //$('#destContactMechId').jqxInput({ width: 300});
        $("#shipBeforeDate").jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm', disabled: false});
        $("#shipAfterDate").jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm', disabled: false});
        $("#shipBeforeDate").jqxDateTimeInput('clear');
        $("#shipAfterDate").jqxDateTimeInput('clear');
        //$('#destContactMechId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: destContactData, selectedIndex: 0, width: 300, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
        //$("#description").jqxInput({ width: 300, height: 65});
        //$("#packId").jqxInput({width: 300});
        //$("#partyIdTo").jqxInput({ width: 300});
        /*if ($("#shipmentMethodTypeId").length > 0){
            $("#shipmentMethodTypeId").val("GROUND_HOME");
            update({
                shipmentMethodTypeId: $("#shipmentMethodTypeId").val(),
                }, 'getPartyCarrierByShipmentMethodAndStore' , 'listParties', 'partyId', 'fullName', 'carrierPartyId');
        }*/
    };


    var initElementComplex = function () {
        //initGridProduct(grid);
        initProductStoreGrid();
        initShippingAddress();
        initDeliveryGrid();
    }
    var getCustomerDDB = function () {
        return customerDDB;
    }

    var getShippingAddressDDB = function () {
        return shippingAddressDDB;
    }
    var initProductStoreGrid = function () {
        var configCustomer = {
            useUrl: true,
            root: 'results',
            widthButton: '100%',
            showdefaultloadelement: false,
            autoshowloadelement: false,
            datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {
                name: 'partyName',
                type: 'string'
            },
                {name: 'telecomId', type: 'string'}, {name: 'telecomName', type: 'string'}, {
                    name: 'postalAddressName',
                    type: 'string'
                }],
            columns: [
                {text: uiLabelMap.BSCustomerId, datafield: 'partyCode', width: '24%'},
                {text: uiLabelMap.BSFullName, datafield: 'partyName', width: '30%'},
                {text: uiLabelMap.BSPhone, datafield: 'telecomName', width: '20%'},
                {text: uiLabelMap.BSAddress, datafield: 'postalAddressName', width: '60%'},
            ],
            url: 'JQGetCustomers',
            useUtilFunc: true,

            key: 'partyId',
            keyCode: 'partyCode',
            description: ['partyName'],
            autoCloseDropDown: true,
            filterable: true,
            sortable: true,
        };
        customerDDB = new OlbDropDownButton($("#customerId"), $("#customerGrid"), null, configCustomer, []);
    }
    var initShippingAddress = function () {
        var configShippingAddress = {
            widthButton: '100%',
            width: 800,
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
                {text: uiLabelMap.BSContactMechId, datafield: 'contactMechId', width: '100px'},
                {text: uiLabelMap.BSReceiverName, datafield: 'toName', width: '140px'},
                {text: uiLabelMap.BSOtherInfo, datafield: 'attnName', width: '140px'},
                {text: uiLabelMap.BSAddress, datafield: 'address1', width: '25%'},
                {text: uiLabelMap.BSWard, datafield: 'wardGeoName', width: '20%'},
                {text: uiLabelMap.BSCounty, datafield: 'districtGeoName', width: '120px'},
                {text: uiLabelMap.BSStateProvince, datafield: 'stateProvinceGeoName', width: '100px'},
                {text: uiLabelMap.BSCountry, datafield: 'countryGeoName', width: '100px'},
            ],
            useUrl: true,
            root: 'results',
            url: '',//defaultPartyId != null ? 'JQGetShippingAddressByPartyReceive&partyId=' + defaultPartyId : '',
            useUtilFunc: true,
            selectedIndex: 0,
            key: 'contactMechId',
            description: ['toName', 'attnName', 'address1', 'wardGeoName', 'districtGeoName', 'city', 'countryGeoId'],
            autoCloseDropDown: false,
            contextMenu: "contextMenushippingContactMechGrid",
            filterable: true,
            sortable: true,
        };
        shippingAddressDDB = new OlbDropDownButton($("#destContactMechId"), $("#destContactMechGrid"), null, configShippingAddress, []);

    }


    var initDeliveryGrid = function () {
        var configDeliveryGrid = {
            widthButton: '100%',
            width: 380,
            dropDownHorizontalAlignment: 'right',
            datafields: [
                {name: 'deliveryId', type: 'string'},
                {name: 'partyIdTo', type: 'string'},
                {name: 'orderId', type: 'string'},

            ],
            columns: [
                {text: uiLabelMap.DeliveryId, datafield: 'deliveryId', width: '100px'},
                {text: uiLabelMap.BSCustomerId, datafield: 'partyIdTo', width: '140px'},
                {text: uiLabelMap.OrderId, datafield: 'orderId', width: '140px'},
            ],
            useUrl: true,
            root: 'results',
            url: '',//defaultPartyId != null ? 'JQGetShippingAddressByPartyReceive&partyId=' + defaultPartyId : '',
            useUtilFunc: true,
            key: 'deliveryId',
            //description: ['toName', 'attnName', 'address1', 'wardGeoName', 'districtGeoName', 'city', 'countryGeoId'],
            autoCloseDropDown: false,
            showClearButton:true,
            //contextMenu: "contextMenushippingContactMechGrid",
            filterable: true,
            sortable: true,
            selectionmode:'checkbox'
        };
        deliveryDDB = new OlbDropDownButton($("#deliveryId"), $("#deliveryGrid"), null, configDeliveryGrid, []);

    }
    var initEvents = function () {
        customerDDB.getGrid().rowSelectListener(function (rowData) {
            customerIdMain = rowData['partyId'];
            customerRowDataMain = rowData;
            reloadShippingAddressGrid(customerIdMain);
            reloadDeliveryGrid(customerIdMain);

        });
        deliveryDDB.getGrid().rowSelectListener(function (rowData) {
            deliveryIdMain = deliveryDDB.getValue2();
            updateProductGridData(deliveryIdMain);

        });
        deliveryDDB.getGrid().rowUnSelectListener(function (rowData) {
            deliveryIdMain = deliveryDDB.getValue2();
            updateProductGridData(deliveryIdMain);

        });

    }
    var initValidateForm = function () {
        var extendRules = [
            {
                input: '#shipAfterDate', message: uiLabelMap.CannotBeforeNow, action: 'change', position: 'topcenter',
                rule: function (input, commit) {
                    var value = $('#shipAfterDate').jqxDateTimeInput('getDate');
                    var nowDate = new Date();
                    if (value < nowDate) {
                        return false;
                    }
                    return true;
                }
            },
            {
                input: '#shipAfterDate',
                message: uiLabelMap.BLTimeDistanceNotValid,
                action: 'change',
                position: 'topcenter',
                rule: function (input, commit) {
                    var value1 = $('#shipAfterDate').jqxDateTimeInput('getDate');
                    var value2 = $('#shipBeforeDate').jqxDateTimeInput('getDate');
                    if (value2 && value1 && value2 < value1) {
                        return false;
                    }
                    return true;
                }
            },
            {
                input: '#shipBeforeDate', message: uiLabelMap.CannotBeforeNow, action: 'change', position: 'topcenter',
                rule: function (input, commit) {
                    var value = $('#shipBeforeDate').jqxDateTimeInput('getDate');
                    var nowDate = new Date();
                    if (value < nowDate) {
                        return false;
                    }
                    return true;
                }
            },

        ];
        var mapRules = [
            {input: '#shipBeforeDate', type: 'validInputNotNull', action: 'valueChanged'},
            {input: '#shipAfterDate', type: 'validInputNotNull', action: 'valueChanged'},
        ];
        validatorVAL = new OlbValidator($('#initPack'), mapRules, extendRules, {position: 'right'});
    };
    var productGridCellclass = function (row, column, value, data) {
        var data = grid.jqxGrid('getrowdata', row);
        if (data['purchaseDiscontinuationDate'] != undefined && data['purchaseDiscontinuationDate'] != null) {
            var now = new Date();
            var ex = new Date(data['purchaseDiscontinuationDate']);
            if (ex <= now) {
                return 'background-cancel';
            }
        } else {
            if (column == 'quantity' || column == 'description') {
                return 'background-prepare';
            }
        }
    }

    function renderHtml(data, key, value, id) {
        var y = "";
        var source = new Array();
        var index = 0;
        for (var x in data) {
            index = source.length;
            var row = {};
            row[key] = data[x][key];
            row['description'] = data[x][value];
            source[index] = row;
        }
        if ($("#" + id).length) {
            $("#" + id).jqxDropDownList('clear');
            $("#" + id).jqxDropDownList({source: source, selectedIndex: 0});
        }
    }

    function update(jsonObject, url, data, key, value, id) {
        jQuery.ajax({
            url: url,
            type: "POST",
            data: jsonObject,
            async: false,
            success: function (res) {
                var json = res[data];
                renderHtml(json, key, value, id);
            }
        });
    }

    var reloadShippingAddressGrid = function (customerId, selectArr) {
        shippingAddressDDB.updateSource("jqxGeneralServicer?sname=JQGetShippingAddressByPartyReceive&partyId=" + customerId, null, function () {
            shippingAddressDDB.selectItem(selectArr);
        });

    };
    var reloadDeliveryGrid = function (customerId, selectArr) {
        deliveryDDB.clearAll();
        deliveryDDB.updateSource("jqxGeneralServicer?sname=JQGetListDeliveryByCustomer&customerId=" + customerId, null, function () {

        });
    };
    var getValidator = function () {
        return validatorVAL;
    };

    function updateProductGridData(deliveryIdMain) {

        var element = $("#jqxGridProduct");
        var facId = null;
        var tmpS = element.jqxGrid('source');
        if (tmpS) {
            var curUrl = tmpS._source.url;

            var newUrl = "jqxGeneralServicer?sname=JQGetListDeliveryItemAndProductId";
            for (var x in deliveryIdMain) {

                newUrl+="&deliveryId[]="+deliveryIdMain[x];
            }
            if (newUrl != curUrl) {
                tmpS._source.url = newUrl;
                element.jqxGrid('source', tmpS);
            }
        }
    }

    return {
        init: init,
        getValidator: getValidator,
        getCustomerDDB: getCustomerDDB,
        getShippingAddressDDB: getShippingAddressDDB
    }
}());