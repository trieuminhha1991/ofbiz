<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<div id="contentNotificationAddSuccess">
</div>

<script>
    <#assign invoiceTypes = delegator.findList("InvoiceType", null, null, null, null, false) />
    var mapInvoiceTypeData = {
    <#if invoiceTypes?exists>
        <#list invoiceTypes as item>
            <#assign s1 = StringUtil.wrapString(item.get('description', locale)?if_exists)/>
            "${item.invoiceTypeId?if_exists}": "${s1}",
        </#list>
    </#if>
    };
</script>
<#assign dataField="[
				{ name: 'invoiceTypeId', type: 'string'},
				{ name: 'invoiceTypeCode', type: 'string'},
				{ name: 'description', type: 'string'},
			]"/>
<#assign columnlist="
				{
				    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{ text: '${uiLabelMap.BACCListInvoiceTypeCode}', datafield: 'invoiceTypeId', align: 'center'
				},
				{ text: '${uiLabelMap.BACCInvoiceTypeId}', datafield: 'description', align: 'center'
				},
				{ text: '${uiLabelMap.BACCInvoiceTypeDescription}', datafield: 'invoiceTypeCode', align: 'center',
				cellsrenderer: function (row, column, value){
			    	var rowData = $('#jqxgirdInvoiceType').jqxGrid('getrowdata', row);
				    if(rowData.invoiceTypeId){
                        return '<span>' + mapInvoiceTypeData[rowData.invoiceTypeId] + '<span>';
                        }
                    }
                }
			"/>

<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
id="jqxgirdInvoiceType" addrefresh="true" filterable="true"
url="jqxGeneralServicer?sname=JQGetListInvoiceType"
customTitleProperties="BACCListInvoiceType"
customcontrol1="fa fa-plus-circle@${uiLabelMap.AddNew}@javascript:addInvoiceType()"
mouseRightMenu="true" contextMenuId="menuInvoiceItemType" />

<div id="alterpopupWindow" class="hide">
    <div class="row-fluid">
    ${uiLabelMap.AddConfigureLogisticsInvoices}
    </div>
    <div class='form-window-container'>
        <input class="hide" id="invoiceTypeIdInput">
        <div class="row-fluid">
            <div class="span6">
                <div class="row-fluid margin-bottom10">
                    <div class="span5" style="text-align: right">
                        <label class="asterisk"> ${uiLabelMap.BACCInvoiceTypeCode}: </label>
                    </div>
                    <div class="span7">
                        <input id="invoiceTypeCode" style="width: 100%">
                        </input>
                    </div>
                </div>
            </div>
            <div class="span6 no-left-margin">
                <div class="row-fluid margin-bottom10">
                    <div class="span5" style="text-align: right">
                        <label class="asterisk"> ${uiLabelMap.Description}: </label>
                    </div>
                    <div class="span7">
                        <input id="description" style="width: 100%">
                        </input>
                    </div>
                </div>
            </div>
        </div>
        <div class="form-action">
            <div class='row-fluid'>
                <div class="span12 margin-top20" style="margin-bottom:10px;">
                    <button id="addButtonCancel"
                            class='btn btn-danger form-action-button pull-right'
                            style="margin-right:20px !important;"><i
                            class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
                    <button id="addButtonSave"
                            class='btn btn-primary form-action-button pull-right'><i
                            class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
                </div>
            </div>
        </div>
    </div>
</div>

<div id='menuInvoiceItemType' style="display:none;">
    <ul>
        <li><i class="fa-trash"></i>&nbsp;&nbsp;${uiLabelMap.PODeleteRowGird}</li>
        <li><i class="fa fa-pencil-square-o"></i>&nbsp;&nbsp;${uiLabelMap.Edit}</li>
    </ul>
</div>
<div id="jqxNotificationAddSuccess">
    <div id="notificationAddSuccess">
    </div>
</div>
<script>

    $("#jqxNotificationAddSuccess").jqxNotification({
        width: "100%",
        appendContainer: "#contentNotificationAddSuccess",
        opacity: 0.9,
        autoClose: true,
        template: "success"
    });
    $("#invoiceTypeCode").jqxInput({width: 195});
    $("#description").jqxInput({width: 195});
    $("#alterpopupWindow").jqxWindow({
        maxWidth: 900,
        minWidth: 830,
        height: 150,
        minHeight: 100,
        maxHeight: 1200,
        resizable: true,
        isModal: true,
        autoOpen: false,
        cancelButton: $("#addButtonCancel"),
        modalOpacity: 0.7,
        theme: 'olbius'
    });

    $("#menuInvoiceItemType").jqxMenu({
        width: 170,
        autoOpenPopup: false,
        mode: 'popup',
        theme: 'olbius'
    });
    $("#menuInvoiceItemType").on('itemclick', function (event) {
        requirementIdData = "";
        var args = event.args;
        var rowindex = $("#jqxgirdInvoiceType").jqxGrid('getselectedrowindex');
        var dataRecord = $("#jqxgirdInvoiceType").jqxGrid('getrowdata', rowindex);
        var invoiceTypeId = dataRecord.invoiceTypeId;
        var invoiceTypeCode = dataRecord.invoiceTypeCode;
        var description = dataRecord.description;
        if ($.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.PODeleteRowGird)}") {
            bootbox.dialog("${uiLabelMap.ConfirmDelete}",
                    [{
                        "label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}",
                        "icon": 'fa fa-remove',
                        "class": 'btn  btn-danger form-action-button pull-right',
                        "callback": function () {
                            bootbox.hideAll();
                        }
                    },
                        {
                            "label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
                            "icon": 'fa-check',
                            "class": 'btn btn-primary form-action-button pull-right',
                            "callback": function () {
                                deleteInvoiceType(invoiceTypeId);
                            }
                        }]);
        }
        else {
            editInvoiceType(invoiceTypeId, invoiceTypeCode, description);
        }
    });

    function checkRegex(value,regexUiLabel){
        if(OlbCore.isNotEmpty(regexUiLabel) && OlbCore.isNotEmpty(value)){
            var regexCheck = new RegExp(regexUiLabel);
            if(regexCheck.test(value)){
                return true;
            }
        }
        return false;
    }

    $('#alterpopupWindow').jqxValidator({
        rules: [
            {
                input: '#invoiceTypeCode',
                message: '${uiLabelMap.POCheckIsEmptyCreateLocationFacility}',
                action: 'valueChanged, blur',
                rule: function () {
                    var enumTypeIdInput = $('#invoiceTypeCode').val();
                    if (enumTypeIdInput == "") {
                        return false;
                    } else {
                        return true;
                    }
                    return true;
                }
            },
            {
                input: "#invoiceTypeCode",
                message: '${uiLabelMap.BPCharacterIsNotValid}',
                action: "keyup, blur",
                rule: function (input, commit) {
                    return checkRegex(input.val(), '${StringUtil.wrapString(uiLabelMap.BPCheckFullName)}');
                }
            },
            {
                input: '#description',
                message: '${uiLabelMap.POCheckIsEmptyCreateLocationFacility}',
                action: 'valueChanged, blur',
                rule: function () {
                    var enumIdInput = $('#description').val();
                    if (enumIdInput == "") {
                        return false;
                    } else {
                        return true;
                    }
                    return true;
                }
            }
        ]
    });

    var checkUpdateInsert = false;
    function addInvoiceType() {
        checkUpdateInsert = false;
        $('#alterpopupWindow').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.BACCAddInvoiceType)}');
        $("#alterpopupWindow").jqxWindow('open');
    }

    $("#addButtonSave").click(function () {
        var validate = $('#alterpopupWindow').jqxValidator('validate');
        if (validate != false) {
            var invoiceTypeCode = $('#invoiceTypeCode').val();
            var description = $('#description').val();
            if (checkUpdateInsert == false) {
                bootbox.dialog("${uiLabelMap.POAreYouSureAddItem}",
                        [{
                            "label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}",
                            "icon": 'fa fa-remove',
                            "class": 'btn  btn-danger form-action-button pull-right',
                            "callback": function () {
                                bootbox.hideAll();
                            }
                        },
                            {
                                "label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
                                "icon": 'fa-check',
                                "class": 'btn btn-primary form-action-button pull-right',
                                "callback": function () {
                                    createInvoiceType(invoiceTypeCode, description);
                                }
                            }]);
            }
            if (checkUpdateInsert == true) {
                var invoiceTypeId = $('#invoiceTypeIdInput').val();
                bootbox.dialog("${uiLabelMap.AreYouSureUpdate}",
                        [{
                            "label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}",
                            "icon": 'fa fa-remove',
                            "class": 'btn  btn-danger form-action-button pull-right',
                            "callback": function () {
                                bootbox.hideAll();
                            }
                        },
                            {
                                "label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
                                "icon": 'fa-check',
                                "class": 'btn btn-primary form-action-button pull-right',
                                "callback": function () {
                                    updateInvoiceType(invoiceTypeId, invoiceTypeCode, description);
                                }
                            }]);
            }
        }
    });

    function createInvoiceType(invoiceTypeCode, description) {
        $.ajax({
            url: "createInvoiceType",
            type: "POST",
            data: {invoiceTypeCode: invoiceTypeCode, description: description},
            dataType: "json",
            success: function (data) {
                if (!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_) {
                    $('#jqxgirdInvoiceType').jqxGrid('updatebounddata');
                    $('#alterpopupWindow').jqxWindow('close');
                    $("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.CreateSuccessfully)}');
                    $("#jqxNotificationAddSuccess").jqxNotification('open');
                } else if (data._ERROR_MESSAGE_) {
                    Loading.hide('loadingMacro');
                    bootbox.dialog(data._ERROR_MESSAGE_,
                            [{
                                "label": "${StringUtil.wrapString(uiLabelMap.CommonClose)}",
                                "class": "btn-danger btn-small icon-remove open-sans",
                            }]
                    );
                } else if (data._ERROR_MESSAGE_LIST_) {
                    Loading.hide('loadingMacro');
                    bootbox.dialog(data._ERROR_MESSAGE_LIST_[0],
                            [{
                                "label": "${StringUtil.wrapString(uiLabelMap.CommonClose)}",
                                "class": "btn-danger btn-small icon-remove open-sans",
                            }]
                    );
                }
            }
        });
    }

    function updateInvoiceType(invoiceTypeId, invoiceTypeCode, description) {
        $.ajax({
            url: "updateInvoiceType",
            type: "POST",
            data: {
                invoiceTypeId: invoiceTypeId,
                invoiceTypeCode: invoiceTypeCode,
                description: description
            },
            dataType: "json",
            success: function (data) {
                if (!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_) {
                    $('#jqxgirdInvoiceType').jqxGrid('updatebounddata');
                    $('#alterpopupWindow').jqxWindow('close');
                    $("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.UpdateSuccessfully)}');
                    $("#jqxNotificationAddSuccess").jqxNotification('open');
                } else if (data._ERROR_MESSAGE_) {
                    Loading.hide('loadingMacro');
                    bootbox.dialog(data._ERROR_MESSAGE_,
                            [{
                                "label": uiLabelMap.CommonClose,
                                "class": "btn-danger btn-small icon-remove open-sans",
                            }]
                    );
                } else if (data._ERROR_MESSAGE_LIST_) {
                    Loading.hide('loadingMacro');
                    bootbox.dialog(data._ERROR_MESSAGE_LIST_[0],
                            [{
                                "label": uiLabelMap.CommonClose,
                                "class": "btn-danger btn-small icon-remove open-sans",
                            }]
                    );
                }
            }
        });
    }

    $('#alterpopupWindow').on('close', function (event) {
        $('#alterpopupWindow').jqxValidator('hide');
        $('#description').val("");
        $('#invoiceTypeCode').val("");
        $('#invoiceTypeCode').removeAttr('disabled');
    });

    function deleteInvoiceType(invoiceTypeId) {
        $.ajax({
            url: "deleteInvoiceType",
            type: "POST",
            data: {invoiceTypeId: invoiceTypeId},
            dataType: "json",
            success: function (data) {
                if (!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_) {
                    $("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiDeleteSucess)}');
                    $("#jqxNotificationAddSuccess").jqxNotification('open');
                    $('#jqxgirdInvoiceType').jqxGrid('updatebounddata');
                } else if (data._ERROR_MESSAGE_) {
                    Loading.hide('loadingMacro');
                    bootbox.dialog(data._ERROR_MESSAGE_,
                            [{
                                "label": "${StringUtil.wrapString(uiLabelMap.CommonClose)}",
                                "class": "btn-danger btn-small icon-remove open-sans",
                            }]
                    );
                } else if (data._ERROR_MESSAGE_LIST_) {
                    Loading.hide('loadingMacro');
                    bootbox.dialog(data._ERROR_MESSAGE_LIST_[0],
                            [{
                                "label": "${StringUtil.wrapString(uiLabelMap.CommonClose)}",
                                "class": "btn-danger btn-small icon-remove open-sans",
                            }]
                    );
                }
            }
        });
    }
    function editInvoiceType(invoiceTypeId, invoiceTypeCode, description) {
        checkUpdateInsert = true;
        $('#description').val(description);
        $('#invoiceTypeCode').val(invoiceTypeCode);
        $('#invoiceTypeCode').attr('disabled', 'disabled');
        $('#invoiceTypeIdInput').val(invoiceTypeId);
        $('#alterpopupWindow').jqxWindow('setTitle', "${StringUtil.wrapString(uiLabelMap.BACCEditInvoiceType)}");
        $("#alterpopupWindow").jqxWindow('open');
    }

</script>
