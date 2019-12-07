/**
 * fixedAssetStep1 variable is defined in fixedAssetNewStep1.js file
 * fixedAssetStep2 variable is defined in fixedAssetNewStep2.js file
 * fixedAssetStep3 variable is defined in fixedAssetNewStep3.js file
 */
var fixedAssetCommonObj = (function () {
    var init = function () {
        initElement();
        initDropDown();
        initInvoiceDropDown();
        initComplexInput();
        initValidator();
        initEvent();
    };


    var initDropDown = function () {
        $("#fixedAssetTypeIdEdit").jqxDropDownList({
            source: fixedAssetTypeData,
            filterable: true,
            placeHolder: uiLabelMap.filterchoosestring,
            width: '97%',
            height: '25px',
            valueMember: 'fixedAssetTypeId',
            displayMember: 'description'
        });
        if (fixedAssetTypeId) {
            $("#fixedAssetTypeIdEdit").val(fixedAssetTypeId);
        }
        $("#uomIdEdit").jqxDropDownList({
            autoDropDownHeight: true,
            source: uomData,
            placeHolder: uiLabelMap.filterchoosestring,
            width: '97%',
            height: '25px',
            valueMember: 'uomId',
            displayMember: 'description'
        });
        if (uomId) {
            $("#uomIdEdit").val(uomId);
        }
    };

    var initInvoiceDropDown = function () {
        $("#invoiceDropDownBtn").jqxDropDownButton({width: '97%', height: 25});
        var datafield = [{name: 'invoiceId', type: 'string'},
            {name: 'partyIdFrom', type: 'string'},
            {name: 'fullNameFrom', type: 'string'},
            {name: 'invoiceDate', type: 'date', other: 'Timestamp'},
            {name: 'currencyUomId', type: 'string'},
        ];

    };

    var initElement = function () {
        $("#fixedAssetIdEdit").jqxInput({width: '97%', height: '25px'});
        $("#OrganizationUsedIdView").jqxInput({width: '97%', height: '25px'});
        $("#fixedAssetNameEdit").jqxInput({width: '97%', height: '25px'});
        $("#datePurchaseEdit").jqxDateTimeInput({height: '25px', width: '97%'});
        $("#dateAcquiredEdit").jqxDateTimeInput({height: '25px', width: '97%'});

        if (dateAcquired) {
            $("#dateAcquiredEdit").val(dateAcquired);
        }
        if (datePurchase) {
            $("#datePurchaseEdit").val(datePurchase);
        }

        $("#fixedAssetCommonEditPopupWindow").jqxWindow({
            maxWidth: 900,
            minWidth: 830,
            height: 350,
            minHeight: 300,
            maxHeight: 1200,
            resizable: true,
            isModal: true,
            autoOpen: false,
            cancelButton: $("#addButtonCancel"),
            modalOpacity: 0.7,
            theme: 'olbius'
        });
    };

    var initComplexInput = function () {
        var configGrid1 =
            {
                useUrl: true,
                root: "results",
                widthButton: "97%",
                heightButton: "25px",
                showdefaultloadelement: false,
                autoshowloadelement: false,
                dropDownHorizontalAlignment: "left",
                datafields: [
                    { name: 'glAccountId', type: 'string'},
                    { name: 'accountName', type: 'string'}
                ],
                columns: [
                    {text: uiLabelMap.BACCGlAccountId, datafield: "glAccountId", width: 150},
                    {text: uiLabelMap.BACCAccountName, datafield: "accountName"}
                ],
                url: "JqxGetListGlAccountByClass&glAccountClassId=LONGTERM_ASSET",
                useUtilFunc: true,
                autoCloseDropDown: true,
                key: "glAccountId",
                description: ["accountName"],
                pagesize: 5
            };

        var configGrid2 =
            {
                useUrl: true,
                root: "results",
                widthButton: "97%",
                heightButton: "25px",
                showdefaultloadelement: false,
                autoshowloadelement: false,
                dropDownHorizontalAlignment: "left",
                datafields: [
                    { name: 'glAccountId', type: 'string'},
                    { name: 'accountName', type: 'string'}
                ],
                columns: [
                    {text: uiLabelMap.BACCGlAccountId, datafield: "glAccountId", width: 150},
                    {text: uiLabelMap.BACCAccountName, datafield: "accountName"}
                ],
                url: "JqxGetListGlAccountByClass&glAccountClassId=AMORTIZATION",
                useUtilFunc: true,
                autoCloseDropDown: true,
                key: "glAccountId",
                description: ["accountName"],
                pagesize: 5
            };
        new OlbDropDownButton($("#costGlAccountIdEdit"), $("#costGlAccountGrid"), null, configGrid1, [costGlAccountId]);
        new OlbDropDownButton($("#depGlAccountIdEdit"), $("#depGlAccountGrid"), null, configGrid2, [depGlAccountId]);
    };

    var initValidator = function () {
        $('#fixedAssetCommonEditPopupWindow').jqxValidator({
            rules: [
                { input: '#fixedAssetTypeIdEdit', message: uiLabelMap.FieldRequired, action: 'keyup, change, close',
                    rule: function (input, commit) {
                        if(input.val()){
                            return true;
                        }else{
                            return false;
                        }
                    }
                },
                { input: '#fixedAssetIdEdit', message: uiLabelMap.FieldRequired, action: 'keyup, change, close',
                    rule: function (input, commit) {
                        if(input.val()){
                            return true;
                        }else{
                            return false;
                        }
                    }
                },
                { input: '#fixedAssetNameEdit', message: uiLabelMap.FieldRequired, action: 'keyup, change, close',
                    rule: function (input, commit) {
                        if(input.val()){
                            return true;
                        }else{
                            return false;
                        }
                    }
                },
                { input: '#datePurchaseEdit', message: uiLabelMap.FieldRequired, action: 'keyup, change, close',
                    rule: function (input, commit) {
                        if(input.val()){
                            return true;
                        }else{
                            return false;
                        }
                    }
                },
                { input: '#dateAcquiredEdit', message: uiLabelMap.FieldRequired, action: 'keyup, change, close',
                    rule: function (input, commit) {
                        if(input.val()){
                            return true;
                        }else{
                            return false;
                        }
                    }
                },
                { input: '#uomIdEdit', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', rule: function (input, commit) {
                    if(input.val()){
                        return true;
                    }else{
                        return false;
                    }
                }
                },
                { input: '#costGlAccountIdEdit', message: uiLabelMap.FieldRequired, action: 'change',
                    rule: function (input, commit) {
                        if(input.val()){
                            return true;
                        }else{
                            return false;
                        }
                    }
                },
                { input: '#depGlAccountIdEdit', message: uiLabelMap.FieldRequired, action: 'change',
                    rule: function (input, commit) {
                        if(input.val()){
                            return true;
                        }else{
                            return false;
                        }
                    }
                },
                {input: '#dateAcquiredEdit', message: uiLabelMap.BACCDateFixedAssetFieldRequired, action: 'keyup, change, close',
                    rule: function (input, commit) {
                        if(input.jqxDateTimeInput('getDate') < $("#datePurchaseEdit").jqxDateTimeInput('getDate') && $("#datePurchaseEdit").jqxDateTimeInput('getDate')){
                            return false;
                        }else{
                            return true;
                        }

                    }
                }
            ]
        });
    };

    var fixedAssetTypeView, fixedAssetCostGlAccView, fixedAssetDepGlAccView;
    var initEvent = function () {
        $('#fixedAssetCommonEditPopupWindow').on('close', function (event) {
            $('#fixedAssetCommonEditPopupWindow').jqxValidator('hide');
        });

        $("#fixedAssetTypeIdEdit").on('select', function(event) {
            var args = event.args;
            if (args) {
                fixedAssetTypeView = item.label;
        }});


        $('#costGlAccountGrid').on('rowselect', function (event) {
            var args = event.args;
            var rowBoundIndex = args.rowindex;
            var data = $("#costGlAccountGrid").jqxGrid("getrowdata", rowBoundIndex);
            fixedAssetCostGlAccView = data.glAccountId + ' - ' + data.accountName;
        });

        $('#depGlAccountGrid').on('rowselect', function (event) {
            var args = event.args;
            var rowBoundIndex = args.rowindex;
            var data = $("#depGlAccountGrid").jqxGrid("getrowdata", rowBoundIndex);
            fixedAssetDepreGlAccView = data.glAccountId + ' - ' + data.accountName;
        });

        $("#addButtonSave").click(function () {
            var validate = $('#fixedAssetCommonEditPopupWindow').jqxValidator('validate');
            if (validate != false) {
                bootbox.dialog(uiLabelMap.BPOAreYouSureYouWantSave,
                    [{
                        "label": uiLabelMap.wgcancel,
                        "icon": 'fa fa-remove',
                        "class": 'btn  btn-danger form-action-button pull-right',
                        "callback": function () {
                            bootbox.hideAll();
                        }
                    },
                        {
                            "label": uiLabelMap.wgok,
                            "icon": 'fa-check',
                            "class": 'btn btn-primary form-action-button pull-right',
                            "callback": function () {
                                 updateFixedAsset();
                            }
                        }]);
            }
        });
    };

    var updateFixedAsset = function () {
        var fixedAssetId = $("#fixedAssetIdEdit").val();
        var fixedAssetName = $("#fixedAssetNameEdit").val();
        var uomId = $("#uomIdEdit").val();
        var fixedAssetTypeId = $("#fixedAssetTypeIdEdit").val();
        var datePurchaseTmp = ($('#datePurchaseEdit').jqxDateTimeInput('getDate'));
        var dateAcquiredTmp = ($('#dateAcquiredEdit').jqxDateTimeInput('getDate'));

        var dateAcquired = dateAcquiredTmp.getTime();
        var datePurchase = datePurchaseTmp.getTime();
        var costGlAccountId = jOlbUtil.getAttrDataValue('costGlAccountIdEdit');
        var depGlAccountId = jOlbUtil.getAttrDataValue('depGlAccountIdEdit');
        var dataMap = {
            fixedAssetId: fixedAssetId,
            uomId: uomId,
            fixedAssetName: fixedAssetName,
            dateAcquired: dateAcquired,
            datePurchase: datePurchase,
            costGlAccountId: costGlAccountId,
            depGlAccountId: depGlAccountId,
            fixedAssetTypeId: fixedAssetTypeId
        };
        $.ajax({
            url: "updateFixedAssetCommonInfo",
            type: "POST",
            data: dataMap,
            dataType: "json",
            success: function(data) {
                if(!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_){
                    $("#notificationAddSuccess").text(uiLabelMap.NotifiUpdateSucess);
                    $('#fixedAssetCommonEditPopupWindow').jqxWindow('close');
                    $("#jqxNotificationAddSuccess").jqxNotification('open');

                    window.location.href = 'ViewFixedAsset?fixedAssetId=' + fixedAssetId;
                    // $("#fixedAssetNameView").val(fixedAssetName);
                    // $('#fixedAssetTypeView').val(fixedAssetTypeView);
                    // $('#fixedAssetCostGlAccView').val(fixedAssetCostGlAccView);
                    // $('#fixedAssetDepGlAccView').val(fixedAssetDepGlAccView);
                    // $('#fixedAssetDateAcquiredView').val(accutils.getTimestamp(dateAcquiredTmp));
                    // $('#fixedAssetDatePurchaseView').val(accutils.getTimestamp(datePurchaseTmp));

                }else if(data._ERROR_MESSAGE_){
                    Loading.hide('loadingMacro');
                    bootbox.dialog(data._ERROR_MESSAGE_,
                        [{
                            "label" : uiLabelMap.CommonClose,
                            "class" : "btn-danger btn-small icon-remove open-sans",
                        }]
                    );
                }else if(data._ERROR_MESSAGE_LIST_){
                    Loading.hide('loadingMacro');
                    bootbox.dialog(data._ERROR_MESSAGE_LIST_[0],
                        [{
                            "label" : uiLabelMap.CommonClose,
                            "class" : "btn-danger btn-small icon-remove open-sans",
                        }]
                    );
                }
            }
        });
    };
    return {
        init: init
    }
}());
$(document).ready(function () {
    $.jqx.theme = 'olbius';
    fixedAssetCommonObj.init()
});