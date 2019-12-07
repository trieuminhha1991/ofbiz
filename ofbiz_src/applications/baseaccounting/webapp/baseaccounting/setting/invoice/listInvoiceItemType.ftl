<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script>
    //Prepare for product data
    <#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
    <#assign products = delegator.findList("Product", null, null, null, null, false) />
    var mapProductData = {
    <#if products?exists>
        <#list products as item>
            <#assign s1 = StringUtil.wrapString(item.get('productName', locale)?if_exists)/>
            "${item.productId?if_exists}": "${s1}",
        </#list>
    </#if>
    };

    <#assign facilitys = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("ownerPartyId", ownerPartyId)), null, null, null, false) />
    var mapFacilityData = {
    <#if facilitys?exists>
        <#list facilitys as item>
            "${item.facilityId?if_exists}": '${StringUtil.wrapString(item.get('facilityName', locale)?if_exists)}',
        </#list>
    </#if>
    };

    <#assign enumerationTypes = delegator.findList("EnumerationType", null, null, null, null, false) />
    var mapEnumerationTypeData = {
    <#if enumerationTypes?exists>
        <#list enumerationTypes as item>
            <#assign s1 = StringUtil.wrapString(item.get('description', locale)?if_exists)/>
            "${item.enumTypeId?if_exists}": "${s1}",
        </#list>
    </#if>
    };

    <#assign enumerations = delegator.findList("Enumeration", null, null, null, null, false) />
    var mapEnumerationData = {
    <#if enumerations?exists>
        <#list enumerations as item>
            <#assign s1 = StringUtil.wrapString(item.get('description', locale)?if_exists)/>
            "${item.enumId?if_exists}": "${s1}",
        </#list>
    </#if>
    };

    var enumerationDataSoure = [
    <#if enumerations?exists>
        <#list enumerations as item>
            {
                enumId: "${item.enumId?if_exists}",
                enumTypeId: "${item.enumTypeId?if_exists}",
            },
        </#list>
    </#if>
    ];


    <#assign invoiceTypes = delegator.findList("InvoiceType", null, null, null, null, false) />
    var mapInvoiceTypeData = {
    <#if invoiceTypes?exists>
        <#list invoiceTypes as item>
            <#assign s1 = StringUtil.wrapString(item.get('description', locale)?if_exists)/>
            "${item.invoiceTypeId?if_exists}": "${s1}",
        </#list>
    </#if>
    };

    var invoiceTypeData = [
    <#if invoiceTypes?exists>
        <#list invoiceTypes as item>
            <#assign s1 = StringUtil.wrapString(item.get('description', locale)?if_exists)/>
            {
                description: "${s1}",
                invoiceTypeCode: "${item.invoiceTypeCode?if_exists}",
                invoiceTypeId: "${item.invoiceTypeId?if_exists}",
            },
        </#list>
    </#if>
    ];
</script>
<div id="contentNotificationAddSuccess">
</div>
<#assign dataField="[
				{ name: 'invoiceItemTypeId', type: 'string'},
				{ name: 'invoiceTypeId', type: 'string'},
				{ name: 'description', type: 'string'},
				{ name: 'defaultGlAccountId', type: 'string'}
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

				{ text: '${uiLabelMap.BACCInvoiceItemTypeId}', datafield: 'invoiceItemTypeId', align: 'center'},
				{ text: '${uiLabelMap.BACCInvoiceTypeId}', datafield: 'invoiceTypeId', align: 'center', width: 250,
					cellsrenderer: function (row, column, value){
						if(value){
							return '<span>' + mapInvoiceTypeData[value] + '<span>';
						}
					}
				},
				{ text: '${uiLabelMap.Description}', datafield: 'description', align: 'center'},
				{ text: '${uiLabelMap.accDefaultGlAccountId}', datafield: 'defaultGlAccountId', align: 'center'},
			"/>

<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
id="jqxgridInvoiceItemType" addrefresh="true" filterable="true"
url="jqxGeneralServicer?sname=JQGetListInvoiceItemType"
customTitleProperties="BACCListInvoiceItemType"
sortdirection="desc" defaultSortColumn="createdStamp"
customcontrol1="fa fa-plus-circle@${uiLabelMap.AddNew}@javascript:addEnumInvoiceType()"
mouseRightMenu="true" contextMenuId="menuInvoiceItemType" />

<div id="alterpopupWindow" class="hide">
    <div class="row-fluid">
    ${uiLabelMap.AddConfigureLogisticsInvoices}
    </div>
    <div class='form-window-container'>
        <input class="hide" id="invoiceTypeIdInput"/>
        <div class="row-fluid">
            <div class="span6">
                <div class="row-fluid margin-bottom10">
                    <div class="span5" style="text-align: right">
                        <label class="asterisk"> ${uiLabelMap.BACCInvoiceTypeId}: </label>
                    </div>
                    <div class="span7">
                        <div id="invoiceTypeId" style="width: 100%" class="green-label">
                            <div id="jqxgridInvoiceTypeId">
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row-fluid margin-bottom10">
                    <div class="span5" style="text-align: right">
                        <label> ${uiLabelMap.BACCInvoiceItemTypeId}: </label>
                    </div>
                    <div class="span7">
                        <input id="invoiceItemTypeId" style="width: 100%">
                        </input>
                    </div>
                </div>
            </div>
            <div class="span6 no-left-margin">
                <div class="row-fluid margin-bottom10">
                    <div class="span5" style="text-align: right">
                        <label> ${uiLabelMap.Description}: </label>
                    </div>
                    <div class="span7">
                        <input id="description" style="width: 100%">
                        </input>
                    </div>
                </div>
                <div class="row-fluid margin-bottom10">
                    <div class="span5" style="text-align: right">
                        <label> ${uiLabelMap.accDefaultGlAccountId}: </label>
                    </div>
                    <div class="span7">
                        <input id="defaultGlAccountId" style="width: 100%">
                        </input>
                    </div>
                </div>
            </div>
        </div>
        <div class="form-action">
            <div class='row-fluid'>
                <div class="span12 margin-top20" style="margin-bottom:10px;">
                    <button id="addButtonCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
                    <button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
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
<div id="jqxNotificationAddSuccess" >
    <div id="notificationAddSuccess">
    </div>
</div>
<script>

    $("#jqxNotificationAddSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationAddSuccess", opacity: 0.9, autoClose: true, template: "success" });

    $("#invoiceTypeId").jqxDropDownButton();
    $("#description").jqxInput({ width: 195});
    $("#invoiceItemTypeId").jqxInput({width: 195});
    $("#defaultGlAccountId").jqxInput({width: 195});
    $("#alterpopupWindow").jqxWindow({
        maxWidth: 900, minWidth: 830, height:215 ,minHeight: 100, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme:'olbius'
    });

    $("#menuInvoiceItemType").jqxMenu({ width: 170, autoOpenPopup: false, mode: 'popup', theme: 'olbius'});

    $("#menuInvoiceItemType").on('itemclick', function (event) {
        requirementIdData = "";
        var args = event.args;
        var rowindex = $("#jqxgridInvoiceItemType").jqxGrid('getselectedrowindex');
        var dataRecord = $("#jqxgridInvoiceItemType").jqxGrid('getrowdata', rowindex);
        var invoiceItemTypeId = dataRecord.invoiceItemTypeId;
        var invoiceTypeId = dataRecord.invoiceTypeId;
        var description = dataRecord.description;
        var defaultGlAccountId = dataRecord.defaultGlAccountId;
        if ($.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.PODeleteRowGird)}") {
            bootbox.dialog("${uiLabelMap.ConfirmDelete}",
                    [{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}",
                        "icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
                        "callback": function() {bootbox.hideAll();}
                    },
                        {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
                            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
                            "callback": function() {
                                deleteInvoiceItemType(invoiceItemTypeId);
                            }
                        }]);
        }
        else{
            editInvoiceItemType(invoiceItemTypeId, invoiceTypeId, defaultGlAccountId, description);
        }
    });

    $('#alterpopupWindow').jqxValidator({
        rules:
                [
                    { input: '#invoiceItemTypeId', message: '${uiLabelMap.POCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur',
                        rule: function () {
                            var enumTypeIdInput = $('#invoiceItemTypeId').val();
                            if(enumTypeIdInput == ""){
                                return false;
                            }else{
                                return true;
                            }
                            return true;
                        }
                    },
                    { input: '#invoiceTypeId', message: '${uiLabelMap.POCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur',
                        rule: function () {
                            var enumTypeIdInput = $('#invoiceTypeId').val();
                            if(enumTypeIdInput == ""){
                                return false;
                            }else{
                                return true;
                            }
                            return true;
                        }
                    },
                    { input: '#description', message: '${uiLabelMap.POCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur',
                        rule: function () {
                            var enumIdInput = $('#description').val();
                            if(enumIdInput == ""){
                                return false;
                            }else{
                                return true;
                            }
                            return true;
                        }
                    },

                    { input: '#defaultGlAccountId', message: '${uiLabelMap.NumberFieldRequired}', action: 'valueChanged, blur',
                        rule: function () {
                            var enumIdInput = $('#defaultGlAccountId').val();
                            return isFinite(enumIdInput);
                        }
                    }
                ]
    });

    var sourceInvoiceType =
            {
                datafields:[{name: 'invoiceTypeId', type: 'string'},
                    {name: 'invoiceTypeCode', type: 'string'},
                    {name: 'description', type: 'string'},
                ],
                localdata: invoiceTypeData,
                datatype: "array",
            };
    var dataAdapterInvoiceItemType = new $.jqx.dataAdapter(sourceInvoiceType);
    $("#jqxgridInvoiceTypeId").jqxGrid({
        source: dataAdapterInvoiceItemType,
        filterable: true,
        showfilterrow: true,
        theme: 'olbius',
        autoheight:true,
        pageable: true,
        columns: [{text: '${uiLabelMap.LogInvoiceItemTypeId}', datafield: 'invoiceTypeId', width: '180'},
            {text: '${uiLabelMap.Description}', datafield: 'description'},
        ]
    });

    $("#jqxgridInvoiceTypeId").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxgridInvoiceTypeId").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['description'] +'</div>';
        $('#invoiceTypeId').jqxDropDownButton('setContent', dropDownContent);
        $('#invoiceTypeId').jqxDropDownButton('close');
        $('#invoiceTypeIdInput').val(row.invoiceTypeId);
    });


    var checkUpdateInsert = false;
    function addEnumInvoiceType(){
        checkUpdateInsert = false;
        $('#alterpopupWindow').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.BACCAddInvoiceItemType)}');
        $("#alterpopupWindow").jqxWindow('open');
    }

    $("#addButtonSave").click(function () {
        var validate = $('#alterpopupWindow').jqxValidator('validate');
        if(validate != false){
            if(checkUpdateInsert == false){
                bootbox.dialog("${uiLabelMap.POAreYouSureAddItem}",
                        [{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}",
                            "icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
                            "callback": function() {bootbox.hideAll();}
                        },
                            {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
                                "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
                                "callback": function() {
                                    createInvoiceItemType();
                                }
                            }]);
            }
            if(checkUpdateInsert == true){
                var invoiceItemTypeId = $('#invoiceItemTypeId').val();
                bootbox.dialog("${uiLabelMap.AreYouSureUpdate}",
                        [{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}",
                            "icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
                            "callback": function() {bootbox.hideAll();}
                        },
                            {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
                                "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
                                "callback": function() {
                                    updateInvoiceItemType();
                                }
                            }]);
            }
        }
    });

    function createInvoiceItemType(){
        var data = getData();
        $.ajax({
            url: "createInvoiceItemType",
            type: "POST",
            data: data,
            dataType: "json",
            success: function(data) {
                if(!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_){
                    $('#jqxgridInvoiceItemType').jqxGrid('updatebounddata');
                    $('#alterpopupWindow').jqxWindow('close');
                    if(value == "create"){
                        $("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.CreateSuccessfully)}');
                        $("#jqxNotificationAddSuccess").jqxNotification('open');
                    }
                }else if(data._ERROR_MESSAGE_){
                    Loading.hide('loadingMacro');
                    bootbox.dialog(data._ERROR_MESSAGE_,
                            [{
                                "label" : '${StringUtil.wrapString(uiLabelMap.CommonClose)}',
                                "class" : "btn-danger btn-small icon-remove open-sans",
                            }]
                    );
                }else if(data._ERROR_MESSAGE_LIST_){
                    Loading.hide('loadingMacro');
                    bootbox.dialog(data._ERROR_MESSAGE_LIST_[0],
                            [{
                                "label" : '${StringUtil.wrapString(uiLabelMap.CommonClose)}',
                                "class" : "btn-danger btn-small icon-remove open-sans",
                            }]
                    );
                }
            }
        });
    }

    var getData = function() {
        var invoiceTypeId = $('#invoiceTypeIdInput').val();
        var description = $('#description').val();
        var invoiceItemTypeId = $('#invoiceItemTypeId').val();
        var defaultGlAccountId = $('#defaultGlAccountId').val();
        return {
            invoiceItemTypeId: invoiceItemTypeId,
            invoiceTypeId: invoiceTypeId,
            description: description,
            defaultGlAccountId: defaultGlAccountId
        };
    };
    function updateInvoiceItemType(){
        $.ajax({
            url: "updateInvoiceItemType",
            type: "POST",
            data: getData(),
            dataType: "json",
            success: function(data) {
            }
        }).done(function(data) {
            var value = data["value"];
            $('#jqxgridInvoiceItemType').jqxGrid('updatebounddata');
            $('#alterpopupWindow').jqxWindow('close');
            if(value == "update"){
                $("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.UpdateSuccessfully)}');
                $("#jqxNotificationAddSuccess").jqxNotification('open');
            }
        });
    }

    $('#alterpopupWindow').on('close', function (event) {
        $('#alterpopupWindow').jqxValidator('hide');
        $('#jqxgridInvoiceTypeId').jqxGrid('clearselection');
        $('#invoiceTypeId').jqxDropDownButton('setContent', "");
        $('#description').val("");
        $('#invoiceItemTypeId').val("");
        $('#invoiceItemTypeId').removeAttr('disabled');

    });

    function deleteInvoiceItemType(invoiceItemTypeId){
        $.ajax({
            url: "deleteInvoiceItemType",
            type: "POST",
            data: {invoiceItemTypeId: invoiceItemTypeId},
            dataType: "json",
            success: function(data) {
                if(!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_){
                    $("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiDeleteSucess)}');
                    $("#jqxNotificationAddSuccess").jqxNotification('open');
                    $('#jqxgridInvoiceItemType').jqxGrid('updatebounddata');
                }else if(data._ERROR_MESSAGE_){
                    Loading.hide('loadingMacro');
                    bootbox.dialog(data._ERROR_MESSAGE_,
                            [{
                                "label" : "${StringUtil.wrapString(uiLabelMap.CommonClose)}",
                                "class" : "btn-danger btn-small icon-remove open-sans",
                            }]
                    );
                }else if(data._ERROR_MESSAGE_LIST_){
                    Loading.hide('loadingMacro');
                    bootbox.dialog(data._ERROR_MESSAGE_LIST_[0],
                            [{
                                "label" : "${StringUtil.wrapString(uiLabelMap.CommonClose)}",
                                "class" : "btn-danger btn-small icon-remove open-sans",
                            }]
                    );
                }
            }
        });
    }
    function editInvoiceItemType( invoiceItemTypeId, invoiceTypeId, defaultGlAccountId, description){
        checkUpdateInsert = true;
        $('#invoiceTypeId').jqxDropDownButton('setContent', mapInvoiceTypeData[invoiceTypeId]);
        $('#description').val(description);
        $('#invoiceTypeIdInput').val(invoiceTypeId);
        $('#invoiceItemTypeId').val(invoiceItemTypeId);
        $('#invoiceItemTypeId').attr('disabled', 'disabled');
        $('#defaultGlAccountId').val(defaultGlAccountId);
        $('#alterpopupWindow').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.BACCEditInvoiceItemType)}');
        $("#alterpopupWindow").jqxWindow('open');
    }
</script>
