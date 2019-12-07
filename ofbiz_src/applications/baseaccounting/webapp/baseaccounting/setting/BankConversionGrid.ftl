<#assign dataField="[{ name: 'bankId', type: 'string' },
					 { name: 'bankName', type: 'string' },
					 { name: 'bankAddress', type: 'string' },
					]"/>
<#assign columnlist="{ text: '${uiLabelMap.BACCBankId}', datafield: 'bankId', width: 200,editable : false},
					 { text: '${uiLabelMap.BACCBankName}', datafield: 'bankName', width: 200},
					 { text: '${uiLabelMap.BACCBankAddress}', datafield: 'bankAddress'}
					 "/>
<@jqGrid addrow="true" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListBankConversion" dataField=dataField columnlist=columnlist  deleterow="true"
		 removeUrl="jqxGeneralServicer?sname=deleteBankConversion&jqaction=D" deleteColumn="bankId"	alternativeAddPopup="alterpopupWindow" mouseRightMenu="true" contextMenuId="menuForConversion"
	 />

<div id='menuForConversion' style="display:none;">
    <ul>
        <li><i class="fa fa-edit"></i>${uiLabelMap.Edit}</li>
        <li><i class="fa red fa-trash"></i>${StringUtil.wrapString(uiLabelMap.CommonDelete)}</li>
    </ul>
</div>
<script>
    $(function () {
        initMenu();
        initEvents();
    });

    var initMenu = function() {
        $("#menuForConversion").jqxMenu({ theme: theme, width: 240, autoOpenPopup: false, mode: "popup" });
    };
    var initEvents = function() {
        $("#menuForConversion").on('itemclick', function (event) {
            var data = $('#jqxgrid').jqxGrid('getRowData', $("#jqxgrid").jqxGrid('selectedrowindexes'));
            var tmpStr = $.trim($(args).text());
            if (tmpStr ==  "${StringUtil.wrapString(uiLabelMap.Edit)}") {
                accutils.openJqxWindow($("#editPopupWindow"));
                EditBank.initData(data);
            } else if (tmpStr == "${StringUtil.wrapString(uiLabelMap.CommonDelete)}") {
                deleteVehicle(data.bankId);
            } else if (tmpStr == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}") {
                updateGridData();
            }
        });
    };

    var updateGridData = function () {
        $("#jqxgrid").jqxGrid('updatebounddata');
    };


    var deleteVehicle = function (bankId) {
        bootbox.dialog("${StringUtil.wrapString(uiLabelMap.AreYouSureDelete)}",
                [{
                    "label": "${StringUtil.wrapString(uiLabelMap.CommonCancel)}",
                    "icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
                    "callback": function () { bootbox.hideAll(); }
                },
                    {
                        "label": "${StringUtil.wrapString(uiLabelMap.CommonSave)}",
                        "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
                        "callback": function () {
                            Loading.show('loadingMacro');
                            setTimeout(function () {
                                jQuery.ajax({
                                    url: "deleteBankConversion",
                                    type: "POST",
                                    data: {
                                        bankId: bankId
                                    },
                                    async: false,
                                    success: function (res) {
                                        if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
                                            jOlbUtil.alert.error(uiLabelMap.CheckLinkedData);
                                            return false;
                                        } else {
                                            updateGridData();
                                        }
                                    }
                                });
                                Loading.hide('loadingMacro');
                            }, 300);
                        }
                    }]);
    }
</script>
