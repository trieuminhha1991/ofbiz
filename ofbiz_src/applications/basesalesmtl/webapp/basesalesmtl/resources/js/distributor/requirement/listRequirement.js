$(function(){
    ReqObj.init();
});
var ReqObj = (function(){
    var reqDetailVLD;
    var init = function(){
        initInputs();
//		initElementComplex();
        initEvents();
        initValidateForm();
    };

    var initInputs = function (){
        $("#alterPopupTransferWindow").jqxWindow({
            maxWidth: 1400, minWidth: 300, width: 1330, height: 530, minHeight: 100, maxHeight: 800, resizable: false, isModal: true, modalZIndex: 100000, zIndex: 100000, autoOpen: false, cancelButton: $("#createTransferCancel"), modalOpacity: 0.7, theme:theme
        });
        // $("#RequirementMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
        var originContactData = [];
        var destContactData = [];
        $('#originFacilityId').jqxDropDownList({disabled: true, placeHolder: uiLabelMap.PleaseSelectTitle, width: 200, source: facilityData, selectedIndex: 0, theme: theme, displayMember: 'facilityName', valueMember: 'facilityId',});
        $('#destFacilityId').jqxDropDownList({disabled: true, placeHolder: uiLabelMap.PleaseSelectTitle, width: 200, source: facilityData, selectedIndex: 0, theme: theme, displayMember: 'facilityName', valueMember: 'facilityId',});
        $('#transferTypeId').jqxDropDownList({disabled: true, placeHolder: uiLabelMap.PleaseSelectTitle, source: transferTypeData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'transferTypeId'});
        $('#shipmentMethodTypeId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: shipmentMethodData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'shipmentMethodTypeId'});
        var partyTmpData = [];
        $('#carrierPartyId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: partyTmpData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'partyId'});
        $('#needsReservesInventory').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: yesNoData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'value'});
        $('#maySplit').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: yesNoData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'value'});
        $('#originContactMechId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: originContactData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
        $('#destContactMechId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: destContactData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
        $("#shipBeforeDate").jqxDateTimeInput({width: '103%', formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false});
        $("#shipAfterDate").jqxDateTimeInput({width: '103%', formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false});
        $("#shipBeforeDate").jqxDateTimeInput('clear');
        $("#shipAfterDate").jqxDateTimeInput('clear');
        $("#transferDate").jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false});

        $("#priority").jqxNumberInput({ width:200, height: 25, min: 0,  spinButtons: true, inputMode: 'simple', decimalDigits: 0});
        $("#priority").val(0);

        if ($("#shipmentMethodTypeId").length > 0){
            $("#shipmentMethodTypeId").val("GROUND_HOME");
            update({
                shipmentMethodTypeId: $("#shipmentMethodTypeId").val(),
            }, 'getPartyCarrierByShipmentMethodAndStore' , 'listParties', 'partyId', 'fullName', 'carrierPartyId');

            if (deptTmp !== null){
                $('#carrierPartyId').jqxDropDownList('val', deptTmp);
            }
        }

        if ($("#originFacilityId").length > 0){
            update({
                facilityId: $("#originFacilityId").val(),
                contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
            }, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
        }
        if ($("#destFacilityId").length > 0){
            update({
                facilityId: $("#destFacilityId").val(),
                contactMechPurposeTypeId: "SHIPPING_LOCATION",
            }, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
        }
    };

    var initEvents = function (){

        // $("#RequirementMenu").on('itemclick', function (event) {
        //     var data = $('#jqxgridRequirement').jqxGrid('getRowData', _.last($("#jqxgridRequirement").jqxGrid('selectedrowindexes')));
        //     var tmpStr = $.trim($(args).text());
        //     if(tmpStr == uiLabelMap.ViewDetailInNewPage){
        //         if (acc == true) {
        //             window.open("accViewRequirementDetail?requirementId=" + data.requirementId, '_blank');
        //         } else {
        //             window.open("viewRequirementDetail?requirementId=" + data.requirementId, '_blank');
        //         }
        //     } else if(tmpStr == uiLabelMap.BSViewDetail){
        //         if (acc == true) {
        //             window.location.href = "accViewRequirementDetail?requirementId=" + data.requirementId;
        //         } else {
        //             window.location.href = "viewRequirementDetail?requirementId=" + data.requirementId;
        //         }
        //     } else if (tmpStr == uiLabelMap.BSRefresh){
        //         $('#jqxgridRequirement').jqxGrid('updatebounddata');
        //     }
        // });

        $('#createTransferSave').click(function(){
            var resultValidate = !reqDetailVLD.validate();
            if(resultValidate) return false;
            var selectedIndexs = $('#listRequirementItem').jqxGrid('getselectedrowindexes');
            if(selectedIndexs.length == 0){
                bootbox.dialog(uiLabelMap.YouNotYetChooseProduct, [{
                        "label" : uiLabelMap.OK,
                        "class" : "btn btn-primary standard-bootbox-bt",
                        "icon" : "fa fa-check",
                    }]
                );
                return false;
            }
            var listProducts = [];
            for(var i = 0; i < selectedIndexs.length; i++){
                var data = $('#listRequirementItem').jqxGrid('getrowdata', selectedIndexs[i]);
                var map = {};
                map['productId'] = data.productId;
                map['quantity'] = data.quantityCreate;
                map['quantityUomId'] = data.quantityUomId;
                var exp = data.expireDate;
                if (exp){
                    map['expiredDate'] = exp.getTime();
                }
                map['requirementId'] = data.requirementId;
                map['reqItemSeqId'] = data.reqItemSeqId;
                listProducts.push(map);
            }
            var shipBeforeDateTmp = null;
            var shipAfterDateTmp = null;
            var transferDateTmp =null;
            if ($("#shipBeforeDate").val()){
                shipBeforeDateTmp = $("#shipBeforeDate").jqxDateTimeInput('getDate').getTime();
            }
            if ($("#shipAfterDate").val()){
                shipAfterDateTmp = $("#shipAfterDate").jqxDateTimeInput('getDate').getTime();
            }
            if ($("#transferDate").val()){
                transferDateTmp = $("#transferDate").jqxDateTimeInput('getDate').getTime();
            }
            bootbox.dialog(uiLabelMap.AreYouSureCreate,
                [{"label": uiLabelMap.CommonCancel,
                    "icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
                    "callback": function() {bootbox.hideAll();}
                },
                    {"label": uiLabelMap.OK,
                        "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
                        "callback": function() {
                            Loading.show('loadingMacro');
                            setTimeout(function(){
                                listProducts = JSON.stringify(listProducts);
                                var inputData = {
                                    originFacilityId: $("#originFacilityId").val(),
                                    requirementId: null,
                                    destFacilityId: $("#destFacilityId").val(),
                                    originContactMechId: $("#originContactMechId").val(),
                                    destContactMechId: $("#destContactMechId").val(),
                                    transferTypeId: $("#transferTypeId").val(),
                                    shipmentMethodTypeId: $("#shipmentMethodTypeId").val(),
                                    carrierPartyId: $("#carrierPartyId").val(),
                                    shipBeforeDate: shipBeforeDateTmp,
                                    shipAfterDate: shipAfterDateTmp,
                                    transferDate: transferDateTmp,
                                    needsReservesInventory: $("#needsReservesInventory").val(),
                                    maySplit: $("#maySplit").val(),
                                    priority: $("#priority").val(),
                                    listProducts: listProducts,
                                }
                                $.ajax({
                                    type: 'POST',
                                    url: 'createTransfer',
                                    async: false,
                                    data: inputData,
                                    success: function(data){
                                        $("#jqxgridRequirement").jqxGrid('updatebounddata');
                                        $("#alterPopupTransferWindow").jqxWindow('close');
                                        viewTransferDetail(data.transferId);
                                    },
                                });
                                Loading.hide('loadingMacro');
                            }, 500);
                        }
                    }]);
        });
    };

    var initValidateForm = function (){
        var extendTransferRules = [
            {input: '#transferDate', message: uiLabelMap.PleaseChooseTransferDateOrShipBeforeAndAfter, action: 'valueChanged',
                rule: function(input, commit){
                    var shipAfterDate = $('#shipAfterDate').jqxDateTimeInput('getDate');
                    var shipBeforeDate = $('#shipBeforeDate').jqxDateTimeInput('getDate');
                    var transferDate = $('#transferDate').jqxDateTimeInput('getDate');
                    if (!transferDate){
                        if ((!shipAfterDate && !shipBeforeDate)) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                    return true;
                }
            },
            {input: '#shipAfterDate, #shipBeforeDate', message: uiLabelMap.CannotBeforeNow, action: 'valueChanged',
                rule: function(input, commit){
                    var shipAfterDate = $('#shipAfterDate').jqxDateTimeInput('getDate');
                    var shipBeforeDate = $('#shipBeforeDate').jqxDateTimeInput('getDate');
                    var nowDate = new Date();
                    if ((typeof(shipAfterDate) != 'undefined' && shipAfterDate != null && !(/^\s*$/.test(shipAfterDate)))) {
                        if (shipAfterDate < nowDate) {
                            return false;
                        }
                        if ((typeof(shipBeforeDate) != 'undefined' && shipBeforeDate != null && !(/^\s*$/.test(shipBeforeDate)))) {
                            if (shipBeforeDate < nowDate) {
                                return false;
                            }
                        }
                        return true;
                    }
                    return true;
                }
            },
            {input: '#shipAfterDate', message: uiLabelMap.CanNotAftershipBeforeDate, action: 'valueChanged',
                rule: function(input, commit){
                    var shipAfterDate = $('#shipAfterDate').jqxDateTimeInput('getDate');
                    var shipBeforeDate = $('#shipBeforeDate').jqxDateTimeInput('getDate');
                    if ((typeof(shipBeforeDate) != 'undefined' && shipBeforeDate != null && !(/^\s*$/.test(shipBeforeDate))) && (typeof(shipAfterDate) != 'undefined' && shipAfterDate != null && !(/^\s*$/.test(shipAfterDate)))) {
                        if (shipAfterDate > shipBeforeDate) {
                            return false;
                        }
                        return true;
                    }
                    return true;
                }
            },
            {input: '#shipBeforeDate', message: uiLabelMap.CanNotBeforeshipAfterDate, action: 'valueChanged',
                rule: function(input, commit){
                    var shipAfterDate = $('#shipAfterDate').jqxDateTimeInput('getDate');
                    var shipBeforeDate = $('#shipBeforeDate').jqxDateTimeInput('getDate');
                    if ((typeof(shipBeforeDate) != 'undefined' && shipBeforeDate != null && !(/^\s*$/.test(shipBeforeDate))) && (typeof(shipAfterDate) != 'undefined' && shipAfterDate != null && !(/^\s*$/.test(shipAfterDate)))) {
                        if (shipAfterDate > shipBeforeDate) {
                            return false;
                        }
                        return true;
                    }
                    return true;
                }
            },
        ];
        var mapTransferRules = [
            {input: '#originFacilityId', type: 'validObjectNotNull', objType: 'dropDownList'},
            {input: '#destFacilityId', type: 'validObjectNotNull', objType: 'dropDownList'},
            {input: '#originContactMechId', type: 'validObjectNotNull', objType: 'dropDownList'},
            {input: '#destContactMechId', type: 'validObjectNotNull', objType: 'dropDownList'},
            {input: '#carrierPartyId',type: 'validObjectNotNull', objType: 'dropDownList'},
            {input: '#shipmentMethodTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
            {input: '#transferTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
        ];
        reqDetailVLD = new OlbValidator($('#alterPopupTransferWindow'), mapTransferRules, extendTransferRules, {position: 'topcenter'});
    };

    var getLocalization = function getLocalization() {
        var localizationobj = {};
        localizationobj.pagergotopagestring = uiLabelMap.wgpagergotopagestring + ":";
        localizationobj.pagershowrowsstring = uiLabelMap.wgpagershowrowsstring + ":";
        localizationobj.pagerrangestring = uiLabelMap.wgpagerrangestring;
        localizationobj.pagernextbuttonstring = uiLabelMap.wgpagernextbuttonstring;
        localizationobj.pagerpreviousbuttonstring = uiLabelMap.wgpagerpreviousbuttonstring;
        localizationobj.sortascendingstring = uiLabelMap.wgsortascendingstring;
        localizationobj.sortdescendingstring = uiLabelMap.wgsortdescendingstring;
        localizationobj.sortremovestring = uiLabelMap.wgsortremovestring;
        localizationobj.emptydatastring = uiLabelMap.wgemptydatastring;
        localizationobj.filterselectstring = uiLabelMap.wgfilterselectstring;
        localizationobj.filterselectallstring = uiLabelMap.wgfilterselectallstring;
        localizationobj.filterchoosestring = uiLabelMap.filterchoosestring;
        localizationobj.groupsheaderstring = uiLabelMap.wgdragDropToGroupColumn;
        localizationobj.todaystring = uiLabelMap.wgtodaystring;
        localizationobj.clearstring = uiLabelMap.wgclearstring;
        return localizationobj;
    };
    var createRequirement = function(){
        if (requirementTypeId != null && requirementTypeId != undefined && requirementTypeId != ''){
            window.open('prepareCreateRequirement?requirementTypeId='+requirementTypeId,'_blank');
        } else {
            window.open('prepareCreateRequirement','_blank');
        }
    };

    var createTransfer = function (){
        var listReqSelected = Grid.getCache('jqxgridRequirement')._caches;
        if(Object.keys(listReqSelected).length === 0 || (Object.keys(listReqSelected).length === 1 && '' in listReqSelected)){
            bootbox.dialog(uiLabelMap.YouNotYetChooseRequirement, [{
                    "label" : uiLabelMap.OK,
                    "class" : "btn btn-primary standard-bootbox-bt",
                    "icon" : "fa fa-check",
                }]
            );
            return false;
        } else {
            var checkFaDiff = true;
            var checkStatusDiff = true;
            var checkReasonDiff = true;
            var reasonTmp = null;
            var listRequiremedIds = [];
            $.each(listReqSelected,function(){
                if(this !== undefined && this.requirementId !== ''){
                    var objTmp = this;
                    if (this.statusId !== "REQ_APPROVED"){
                        if (this.statusId !== "REQ_CONFIRMED"){
                            checkStatusDiff = false;
                            return false;
                        }
                    }
                    if(this.reasonEnumId !== null && this.reasonEnumId !== undefined){
                        if (reasonTmp === null){
                            reasonTmp = this.reasonEnumId;
                        } else {
                            if (reasonTmp !== this.reasonEnumId){
                                checkReasonDiff = false;
                                return false;
                            }
                        }
                    }
                    if(this.facilityId !== null && this.facilityId !== undefined){
                        if (originFacilityId === null){
                            originFacilityId = this.facilityId;
                        } else {
                            if (originFacilityId !== this.facilityId){
                                checkFaDiff = false;
                                return false;
                            }
                        }
                    }
                    if(this.destFacilityId !== null && this.destFacilityId !== undefined){
                        if (destFacilityId === null){
                            destFacilityId = this.destFacilityId;
                        } else {
                            if (destFacilityId !== this.destFacilityId){
                                checkFaDiff = false;
                                return false;
                            }
                        }
                    }
                    var mapTmp = {};
                    mapTmp["requirementId"] = this.requirementId;
                    listRequiremedIds.push(mapTmp);
                }
            });
            if (checkStatusDiff === false){
                bootbox.dialog(uiLabelMap.UtilCreateWithAppprovedRequirement, [{
                        "label" : uiLabelMap.OK,
                        "class" : "btn btn-primary standard-bootbox-bt",
                        "icon" : "fa fa-check",
                    }]
                );
                return false;
            }
            if (checkFaDiff === false){
                bootbox.dialog(uiLabelMap.MustSelectSameOrgAndDestFacility, [{
                        "label" : uiLabelMap.OK,
                        "class" : "btn btn-primary standard-bootbox-bt",
                        "icon" : "fa fa-check",
                    }]
                );
                return false;
            }
            if (checkReasonDiff === false){
                bootbox.dialog(uiLabelMap.MustSelectSamePurpose, [{
                        "label" : uiLabelMap.OK,
                        "class" : "btn btn-primary standard-bootbox-bt",
                        "icon" : "fa fa-check",
                    }]
                );
                return false;
            }
            var listRequirementItems = getRequirementItem(listRequiremedIds);
            loadGridRequirementItem(listRequirementItems);
            var listTransferTypes = getTransferTypeByReasonOfRequirement(reasonTmp);
            if (listTransferTypes.length > 0){
                for (var i = 0; i < listTransferTypes.length; i ++){
                    for (var j = 0; j < transferTypeData.length; j ++){
                        if (listTransferTypes[i].transferTypeId == transferTypeData[j].transferTypeId){
                            listTransferTypes[i]["description"] = transferTypeData[i]["description"];
                        }
                    }
                }
                $("#transferTypeId").jqxDropDownList({source: listTransferTypes, selectedIndex: 0});
            }
            $('#originFacilityId').jqxDropDownList('val', originFacilityId);
            $('#destFacilityId').jqxDropDownList('val', destFacilityId);
            $("#alterPopupTransferWindow").jqxWindow('open');
        }
    };

    function getTransferTypeByReasonOfRequirement(enumId){
        var listTransferTypes;
        $.ajax({
            url: 'getTransferTypeByEnumId',
            type: 'POST',
            data: {
                enumId: enumId,
            },
            async: false,
            success: function(res) {
                listTransferTypes = res.listTransferTypes;
            }
        }).done(function() {
        });

        return listTransferTypes;
    }

    function getRequirementItem(listIds){
        var listitems;
        var listIds = JSON.stringify(listIds);
        $.ajax({
            url: 'getItemOfMultiReqToTransfer',
            type: 'POST',
            data: {
                listRequirementIds: listIds,
            },
            async: false,
            success: function(res) {
                listitems = res.listRequirementItems;
            }
        }).done(function() {
        });

        return listitems;
    };

    var dataFieldPopup = [
        { name: 'productId', type: 'string'},
        { name: 'productCode', type: 'string'},
        { name: 'requirementId', type: 'string'},
        { name: 'reqItemSeqId', type: 'string'},
        { name: 'productName', type: 'string' },
        { name: 'internalName', type: 'string' },
        { name: 'expireDate', type: 'date', other: 'Timestamp'},
        { name: 'statusId', type: 'string' },
        { name: 'quantityUomId', type: 'string' },
        { name: 'currencyUomId', type: 'string' },
        { name: 'quantity', type: 'number' },
        { name: 'actualExecutedQuantity', type: 'number' },
        { name: 'unitCost', type: 'number' },
        { name: 'quantityCreate', type: 'number' },
        { name: 'quantityCreated', type: 'number' },
    ];
    var columnListPopup = [
        {
            text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
            groupable: false, draggable: false, resizable: false,
            datafield: '', columntype: 'number', width: 50,
            cellsrenderer: function (row, column, value) {
                return '<div style=margin:4px;>' + (value + 1) + '</div>';
            }
        },
        { text: uiLabelMap.RequirementId, dataField: 'requirementId', align: 'left', width: 100, editable: false, pinned: true,
        },
        { text: uiLabelMap.ProductId, datafield: 'productCode', align: 'left', width: 120, pinned: true, editable: false,},
        { text: uiLabelMap.ProductName, datafield: 'productName', align: 'left', minwidth: 200, editable: false,},
        { text: uiLabelMap.Unit, datafield: 'quantityUomId', align: 'left', width: 100, editable: false, filterable: false,
            cellsrenderer: function(row, column, value){
                if (value){
                    for(i=0; i < quantityUomData.length; i++){
                        if(quantityUomData[i].uomId == value){
                            return '<span style=\"text-align: right;\" title='+value+'>' + quantityUomData[i].description + '</span>';
                        }
                    }
                    return '<span style=\"text-align: right;\">_NA_</span>';
                } else {
                    return '<span style=\"text-align: right;\">_NA_</span>';
                }
                return false;
            },
        },
        { text: uiLabelMap.RequiredNumberSum, datafield: 'quantity', cellsalign: 'right', align: 'left', width: 100, editable: false, filterable: true,
            cellsrenderer: function(row, column, value){
                if (value){
                    return '<span style=\"text-align: right;\">' +  value.toLocaleString(localeStr) + '</span>';
                } else {
                    return '<span></span>';
                }
            },
        },
        { text: uiLabelMap.CreatedNumberSum, datafield: 'quantityCreated', cellsalign: 'right', align: 'left', width: 100, editable: false, filterable: true,
            cellsrenderer: function(row, column, value){
                if (value){
                    return '<span style=\"text-align: right;\">' +  value.toLocaleString(localeStr) + '</span>';
                } else {
                    return '<span style=\"text-align: right;\">0</span>';
                }
            },
        },
        { text: uiLabelMap.QuantityDelivered, datafield: 'actualExecutedQuantity', cellsalign: 'right', align: 'left', width: 100, editable: false, filterable: true,
            cellsrenderer: function(row, column, value){
                if (value){
                    return '<span style=\"text-align: right;\">' +  value.toLocaleString(localeStr) + '</span>';
                } else {
                    return '<span style=\"text-align: right;\">0</span>';
                }
            },
        },
        { text: uiLabelMap.QuantityCreateSum, datafield: 'quantityCreate', cellsalign: 'right', columntype: 'numberinput', align: 'left', width: 120, editable: true, filterable: true,
            cellsrenderer: function(row, column, value){
                return '<span style=\"text-align: right\" class=\"focus-color\" title=' + value.toLocaleString(localeStr) + '>' + value.toLocaleString(localeStr) + '</span>';
            },
            initeditor: function(row, value, editor){
                var data = $('#listRequirementItem').jqxGrid('getrowdata', row);
                editor.jqxNumberInput({ decimalDigits: 0});
                if (null === value || value === undefined){
                    editor.jqxNumberInput('val', data.quantityCreate);
                } else {
                    editor.jqxNumberInput('val', value);
                }
            },
            validation: function (cell, value) {
                var data = $('#listRequirementItem').jqxGrid('getrowdata', cell.row);
                if (value > (data.quantity - data.quantityCreated)){
                    var tmp = data.quantity - data.quantityCreated;
                    return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber + ': ' + value.toLocaleString(localeStr) + ' > ' + tmp.toLocaleString(localeStr)};
                } else{
                    if (value <= 0){
                        return { result: false, message: uiLabelMap.ExportValueMustBeGreaterThanZero};
                    } else {
                        return true;
                    }
                }
            },
        },
        { text: uiLabelMap.UnitPrice, datafield: 'unitCost', align: 'left', width: 130, editable: false,
            cellsrenderer: function(row, colum, value){
                if(value){
                    return '<span style=\"text-align: right;\">' + formatcurrency(value) +'</span>';
                } else {
                    value = 0;
                    return '<span style=\"text-align: right;\">' + formatcurrency(value) +'</span>';
                }
            },
        },
        { text: uiLabelMap.EXPRequired, dataField: 'expireDate', align: 'left', width: 130, editable: false, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
            cellsrenderer: function(row, column, value){
                if (!value){
                    return '<span style=\"text-align: right;\">_NA_</span>';
                }
            }
        },
    ];

    function loadGridRequirementItem(valueDataSoure){
        var sourceProduct =
            {
                datafields: dataFieldPopup,
                localdata: valueDataSoure,
                datatype: "array",
            };
        var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
        $("#listRequirementItem").jqxGrid({
            source: dataAdapterProduct,
            filterable: false,
            showfilterrow: false,
            theme: 'olbius',
            rowsheight: 26,
            width: '100%',
            height: 210,
            enabletooltips: true,
            autoheight: false,
            pageable: true,
            pagesize: 5,
            editable: true,
            columnsresize: true,
            localization: getLocalization(),
            selectionmode: 'checkbox',
            columns: columnListPopup,
        });
    }

    var addZero = function(i) {
        if (i < 10) {i = "0" + i;}
        return i;
    };
    var formatFullDate = function(value) {
        if (value) {
            var dateStr = "";
            dateStr += addZero(value.getDate()) + '/';
            dateStr += addZero(value.getMonth()+1) + '/';
            dateStr += addZero(value.getFullYear()) + ' ';
            dateStr += addZero(value.getHours()) + ':';
            dateStr += addZero(value.getMinutes()) + ':';
            dateStr += addZero(value.getSeconds());
            return dateStr;
        } else {
            return "";
        }
    };

    function renderHtml(data, key, value, id){
        var y = "";
        var source = new Array();
        var index = 0;
        for (var x in data){
            index = source.length;
            var row = {};
            row[key] = data[x][key];
            row['description'] = data[x][value];
            source[index] = row;
        }
        if($("#"+id).length){
            $("#"+id).jqxDropDownList('clear');
            $("#"+id).jqxDropDownList({source: source, selectedIndex: 0});
        }
    }

    function viewTransferDetail(transferId){
        window.location.href = 'viewDetailTransfer?transferId=' + transferId;
    }

    function update(jsonObject, url, data, key, value, id) {
        jQuery.ajax({
            url: url,
            type: "POST",
            data: jsonObject,
            async: false,
            success: function(res) {
                var json = res[data];
                renderHtml(json, key, value, id);
            }
        });
    }

    return {
        init: init,
        getLocalization: getLocalization,
        createRequirement: createRequirement,
        formatFullDate: formatFullDate,
        createTransfer: createTransfer,
    };
}());