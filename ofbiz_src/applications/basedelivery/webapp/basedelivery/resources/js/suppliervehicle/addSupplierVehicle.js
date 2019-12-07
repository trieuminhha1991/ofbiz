$(function () {
    supplierNewTarget.init();
});

var supplierNewTarget = (function () {
    var validatorVAL;
    var init = function () {
        initElement();
        initEvent();
        initElementComplex();
        initValidateForm();
    };

    var initElementComplex = function () {
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
            url: 'JQGetVehicle',
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
        new OlbDropDownButton($("#vehicleId"), $("#vehicleGrid"), null, vehicleConfig, []);
    };

    var initElement = function () {
        $("#supplierId").jqxDropDownList({
            source: supplierData,
            theme: theme,
            width: 218,
            displayMember: "description",
            valueMember: "partyId",
            disabled: false,
            placeHolder: uiLabelMap.BSClickToChoose,
            autoDropDownHeight: true
        });
        $("#alterpopupWindow").jqxWindow({
            width: 800,
            height: 180,
            resizable: true,
            isModal: true,
            autoOpen: false,
            cancelButton: $("#alterCancel"),
            modalOpacity: 0.7,
            theme: theme
        });
    };

    var initEvent = function () {
        // $("#supplierId").on(
        //     "select",
        //     function(event) {
        //         var args = event.args;
        //         if (args) {
        //             var partyId = args.item.value;
        //             productDDB.updateSource(
        //                 "jqxGeneralServicer?sname=jqGetListSupplierProductConfig&partyId="
        //                 + partyId, null, null);
        //             $("#quantityUomId").jqxDropDownList("clearSelection");
        //             $("#quantityUomId").jqxDropDownList({
        //                 source : []
        //             });
        //         }
        //     });

        // $("#jqxgridProduct").on(
        //     "rowselect",
        //     function(event) {
        //         var row = $("#jqxgridProduct").jqxGrid("getrowdata",
        //             event.args.rowindex);
        //         $("#productIdTmp").val(row.productId);
        //         updateListUomByProduct(row.productId);
        //     });

        $("#alterSave").on(
            "click",
            function () {
                if (validatorVAL.validate()) {
                    var dataMap = {
                        partyId: $("#supplierId").val(),
                        vehicleId: jOlbUtil.getAttrDataValue('vehicleId')
                    };
                    $("#alterpopupWindow").jqxWindow("close");
                    $.ajax({
                        type: 'POST',
                        url: 'createSupplierVehicleAjax',
                        data: dataMap,
                        beforeSend: function(){
                            $("#loader_page_common").show();
                        },
                        success: function(data){
                            jOlbUtil.processResultDataAjax(data, "default", function(data){
                                window.location.href = 'listSupplierVehicle';
                            });
                        },
                        error: function(data){
                            alert("Send request is error");
                        },
                        complete: function(data){
                            $("#loader_page_common").hide();
                            $("#alterpopupWindow").jqxWindow("close");
                        }
                    });
                }
            });
        $("#alterCancel").on(
            "click",
            function () {
                $("#alterpopupWindow").jqxWindow("close");
            });

        $("#alterpopupWindow").on("open", function () {
            $("#supplierId").jqxDropDownList("clearSelection");
        });

        $("#alterpopupWindow").on("close", function () {
            $("#alterpopupWindow").jqxValidator("hide");
        });
    };

    var initValidateForm = function () {
        var extendRules = [];
        var mapRules = [{
            input: "#supplierId",
            type: "validInputNotNull"
        },
            {input: '#vehicleId', type: 'validObjectNotNull', objType: 'dropDownButton'}
            ];
        validatorVAL = new OlbValidator($("#alterpopupWindow"), mapRules,
            extendRules, {
                position: "bottom"
            });
    };

    return {
        init: init,
        initValidateForm: initValidateForm,
    };
}());