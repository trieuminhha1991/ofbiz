if (typeof (AddSupplier) == "undefined") {
    var AddSupplier = (function() {
        var jqxwindow, mainGrid, extendId = new Object();
        var initJqxElements = function() {
            jqxwindow.jqxWindow({
                width : 950,
                maxWidth : 900,
                theme : theme,
                height : 370,
                resizable : false,
                isModal : true,
                autoOpen : false,
                cancelButton : $("#alterCancel"),
                modalOpacity : 0.7
            });

            $("#txtTaxAuthPartyId").jqxComboBox({
                source : taxAuthorities,
                displayMember : "description",
                valueMember : "taxAuthPartyId",
                width : 218,
                height : 30,
                theme : theme
            });
            $("#txtCurrencyUomId").jqxComboBox({
                source : currencyUom,
                displayMember : "description",
                valueMember : "uomId",
                width : 218,
                height : 30,
                theme : theme
            });

            AddressProcessor.initComboboxGeo("", "PROVINCE", "finAccountState");
            AddressProcessor.initComboboxGeo("", "COUNTRY", "finAccountCountry");

            $("#txtCurrencyUomId").jqxComboBox("val", "VND");
        };
        var handleEvents = function() {
            jqxwindow.on("open", function() {
                if (jqxwindow.data("partyId")) {
                    $("#addSupplierPopupTitle").text(
                        multiLang.POUpdateSupplierManager);
                } else {
                    $("#addSupplierPopupTitle").text(
                        multiLang.POAddSupplierManager);
                }
                if(typeof(defaultCountryGeoId) != "undefined"){
                    $("#finAccountCountry").val(defaultCountryGeoId);
                }
            });
            jqxwindow.on("close", function() {
                jqxwindow.jqxValidator("hide");
                clean();
            });
            $("#alterSave").click(function() {
                if (jqxwindow.jqxValidator("validate")) {
                    var data = AddSupplier.getValue();
                    var url = "createPartySupplier";
                    if (data.partyId) {
                        url = "updatePartySupplier";
                    }
                    DataAccess.execute({
                        url : url,
                        data : AddSupplier.getValue()
                    }, AddSupplier.notify);
                    jqxwindow.jqxWindow("close");
                }
            });
            AddressProcessor.initEventComboboxGeo("PROVINCE", "finAccountCountry", "finAccountState", "", "COUNTRY");
        };
        var initValidator = function() {
            jqxwindow.jqxValidator({
                rules : [
                    {
                        input : "#txtSupplierId",
                        message : uiLabelMap.BPCharacterIsNotValid,
                        action : "keyup, blur",
                        rule : function(input, commit) {
                            if(OlbCore.isEmpty(input.val())){
                                return true;
                            }
                            return checkRegex(input.val(), uiLabelMap.BPCheckId);
                        }
                    },
                    {
                        input : "#txtSupplierId",
                        message : multiLang.DmsPartyCodeAlreadyExists,
                        action : "change",
                        rule : function(input, commit) {
                            var partyCode = input.val();
                            if (partyCode) {
                                var check = DataAccess
                                    .getData({
                                        url : "checkPartyCode",
                                        data : {
                                            partyId : jqxwindow
                                                .data("partyId"),
                                            partyCode : partyCode
                                        },
                                        source : "check"
                                    });
                                if ("false" == check) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    },
                    {
                        input : "#txtSupplierName",
                        message : uiLabelMap.BPfieldRequired,
                        action : "keyup, blur",
                        rule : function(input, commit) {
                            if(OlbCore.isNotEmpty(input.val())){
                                return true;
                            }
                            return false;
                        }
                    },
                    {
                        input : "#txtSupplierName",
                        message : uiLabelMap.BPCharacterIsNotValid,
                        action : "keyup, blur",
                        rule : function(input, commit) {
                            return checkRegex(input.val(), uiLabelMap.BPCheckFullName);
                        }
                    },
                    {
                        input : "#txtTaxCode",
                        message : uiLabelMap.BPTaxCodeIsNotValid,
                        action : "keyup, blur",
                        rule : function(input, commit) {
                            if(OlbCore.isEmpty(input.val())){
                                return true;
                            }
                            return checkRegex(input.val(), uiLabelMap.BPCheckTaxCode);
                        }
                    },
                    {
                        input : "#txtEmail",
                        message : uiLabelMap.BPEmailIsNotValid,
                        action : "keyup, blur",
                        rule : function(input, commit) {
                            if(OlbCore.isEmpty(input.val())){
                                return true;
                            }
                            return checkRegex(input.val(), uiLabelMap.BPCheckEmail);
                        }
                    },
                    {
                        input : "#txtTelecomNumber",
                        message : uiLabelMap.BPPhoneIsNotValid,
                        action : "keyup, blur",
                        rule : function(input, commit) {
                            if(OlbCore.isEmpty(input.val())){
                                return true;
                            }
                            return checkRegex(input.val(), uiLabelMap.BPCheckPhone);
                        }
                    },
                    {
                        input : "#txtAddress",
                        message : uiLabelMap.BPfieldRequired,
                        action : "keyup, blur",
                        rule : "required"
                    },
                    {
                        input : "#txtCurrencyUomId",
                        message : multiLang.fieldRequired,
                        action : "change",
                        rule : function(input, commit) {
                            if (input.jqxComboBox("val")) {
                                return true;
                            }
                            return false;
                        }
                    },
                    {
                        input : "#finAccountCode",
                        message : multiLang.fieldRequired,
                        action : "blur",
                        rule : function(input, commit) {
                            var finAccountName = $("#finAccountName").val();
                            var finAccountState = $("#finAccountState").val();
                            if ((finAccountName.length > 0 || finAccountState.length > 0) && !$(input).val()) {
                                return false;
                            }
                            return true;
                        }
                    },
                    {
                        input : "#finAccountName",
                        message : multiLang.fieldRequired,
                        action : "blur",
                        rule : function(input, commit) {
                            var finAccountCode = $("#finAccountCode").val();
                            if (finAccountCode.length > 0 && !$(input).val()) {
                                return false;
                            }
                            return true;
                        }
                    },
                    {
                        input : "#finAccountState",
                        message : multiLang.fieldRequired,
                        action : "blur",
                        rule : function(input, commit) {
                            var finAccountCode = $("#finAccountCode").val();
                            if (finAccountCode.length > 0 && !$(input).val()) {
                                return false;
                            }
                            return true;
                        }
                    },
                ],
                scroll : false
            });
        };
        var setValue = function(data) {
            if (data) {
                jqxwindow.data("partyId", data.partyId)
                $("#txtSupplierId").val(data.partyCode);
                $("#txtSupplierName").val(data.groupName);
                $("#txtTaxCode").val(data.taxCode);
                $("#txtTaxAuthPartyId").jqxComboBox("val", data.taxAuth);
                $("#txtCurrencyUomId").jqxComboBox("val",
                    data.preferredCurrencyUomId);
                $("#txtEmail").val(data.infoString);
                $("#txtTelecomNumber").val(data.contactNumber);
                if (data.locationContactMechId) {
                    $("#txtAddress").data("contactMechId",
                        data.locationContactMechId);
                    AddressProcessor
                        .loadPostalAddress(data.locationContactMechId);
                    AddressProcessor.setContactMechId();
                }
                extendId.emailContactMechId = data.emailContactMechId;
                extendId.phoneContactMechId = data.phoneContactMechId;
            }
        };
        var getValue = function() {
            var value = {
                partyId : jqxwindow.data("partyId"),
                partyCode : $("#txtSupplierId").val(),
                groupName : $("#txtSupplierName").val(),
                taxCode : $("#txtTaxCode").val(),
                taxAuth : $("#txtTaxAuthPartyId").jqxComboBox("val"),
                preferredCurrencyUomId : $("#txtCurrencyUomId").jqxComboBox(
                    "val"),
                infoString : $("#txtEmail").val(),
                contactNumber : $("#txtTelecomNumber").val(),
                partyRelationshipTypeId: "SUPPLIER_REL_DELIVERY"
            };
            if($("#finAccountName").val() && $("#finAccountCode").val()){
                value.finAccountName = $("#finAccountName").val();
                value.finAccountCode = $("#finAccountCode").val();
                value.stateProvinceGeoId = $("#finAccountState").val();
                value.countryGeoId = $("#finAccountCountry").val();
            }
            value = _.extend(value, AddressProcessor.getValue(), extendId);
            return value;
        };
        var clean = function() {
            jqxwindow.data("partyId", "")
            $("#txtSupplierId").val("");
            $("#txtSupplierName").val("");
            $("#txtTaxCode").val("");
            $("#txtEmail").val("");
            $("#finAccountName").val("");
            $("#finAccountCode").val("");
            $("#finAccountState").jqxComboBox('clearSelection');
            $("#finAccountCountry").jqxComboBox('clearSelection');
            $("#txtTelecomNumber").val("");
            $("#txtAddress").val("");
            $("#txtAddress").data("contactMechId", "");
            extendId = new Object();
        };
        var open = function(data) {
            if (data) {
                AddSupplier.setValue(data);
            }
            var wtmp = window;
            var tmpwidth = jqxwindow.jqxWindow("width");
            jqxwindow.jqxWindow({
                position : {
                    x : (wtmp.outerWidth - tmpwidth) / 2,
                    y : pageYOffset + 70
                }
            });
            jqxwindow.jqxWindow("open");
        };
        var notify = function(res) {
            if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
                Grid.renderMessage(mainGrid.attr("id"), multiLang.updateError,
                    {
                        autoClose : true,
                        template : "error",
                        appendContainer : "#container"
                        + mainGrid.attr("id"),
                        opacity : 0.9
                    });
            } else {
                Grid.renderMessage(mainGrid.attr("id"),
                    multiLang.updateSuccess, {
                        autoClose : true,
                        template : "info",
                        appendContainer : "#container"
                        + mainGrid.attr("id"),
                        opacity : 0.9
                    });
                mainGrid.jqxGrid("updatebounddata");
            }
        };
        return {
            init : function() {
                jqxwindow = $("#alterpopupWindow");
                mainGrid = $("#jqxgridSupplier");
                initJqxElements();
                handleEvents();
                initValidator();
                AddressProcessor.init();
            },
            setValue : setValue,
            getValue : getValue,
            notify : notify,
            open : open
        };
    })();
}