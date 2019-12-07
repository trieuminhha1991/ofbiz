<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script type="text/javascript">
    if (uiLabelMap == undefined) var uiLabelMap = {};
    uiLabelMap.BPCheckPhone = '${StringUtil.wrapString(uiLabelMap.BPCheckPhone)}';
    uiLabelMap.BPCheckEmail = '${StringUtil.wrapString(uiLabelMap.BPCheckEmail)}';
    uiLabelMap.BPCheckFullName = '${StringUtil.wrapString(uiLabelMap.BPCheckFullName)}';
    uiLabelMap.BPCheckTaxCode = '${StringUtil.wrapString(uiLabelMap.BPCheckTaxCode)}';
    uiLabelMap.BPCheckId = '${StringUtil.wrapString(uiLabelMap.BPCheckId)}';
    uiLabelMap.BPCheckPostalCode = '${StringUtil.wrapString(uiLabelMap.BPCheckPostalCode)}';
    uiLabelMap.BPCheckAddress = '${StringUtil.wrapString(uiLabelMap.BPCheckAddress)}';
    uiLabelMap.BPPostalCodeIsNotValid = '${StringUtil.wrapString(uiLabelMap.BPPostalCodeIsNotValid)}';
    uiLabelMap.BPCharacterIsNotValid = '${StringUtil.wrapString(uiLabelMap.BPCharacterIsNotValid)}';
    uiLabelMap.BPTaxCodeIsNotValid = '${StringUtil.wrapString(uiLabelMap.BPTaxCodeIsNotValid)}';
    uiLabelMap.BPPhoneIsNotValid = '${StringUtil.wrapString(uiLabelMap.BPPhoneIsNotValid)}';
    uiLabelMap.BPEmailIsNotValid = '${StringUtil.wrapString(uiLabelMap.BPEmailIsNotValid)}';
    uiLabelMap.BPfieldRequired = '${StringUtil.wrapString(uiLabelMap.BPfieldRequired)}';
    function checkRegex(value,regexUiLabel){
        if(OlbCore.isNotEmpty(regexUiLabel) && OlbCore.isNotEmpty(value)){
            var regexCheck = new RegExp(regexUiLabel);
            if(regexCheck.test(value)){
                return true;
            }
        }
        return false;
    }
</script>

<#assign dataField="[{ name: 'partyId', type: 'string' },
					{ name: 'partyCode', type: 'string'},
					{ name: 'preferredCurrencyUomId', type: 'string'},
					{ name: 'groupName', type: 'string' },
					{ name: 'taxCode', type: 'string' },
					{ name: 'taxAuth', type: 'string' },
					{ name: 'infoString', type: 'string' },
					{ name: 'emailContactMechId', type: 'string' },
					{ name: 'contactNumber', type: 'string' },
					{ name: 'phoneContactMechId', type: 'string' },
					{ name: 'addressDetail', type: 'string' },
					{ name: 'locationContactMechId', type: 'string' }]"/>

<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.BPOSequenceId)}',sortable: false, filterable: false, pinned: true,
						groupable: false, draggable: false, resizable: false, datafield: '', columntype: 'number', width: 50,
						cellsrenderer: function (row, column, value) {
							return \"<div style='margin-top: 3px; text-align: left; '>\" + (value + 1)+ \"</div>\";
						}
					},
					{ text: '${uiLabelMap.BDSupplierId}', datafield: 'partyCode', width: 150, pinned: true,
					    cellsrenderer: function (row, column, value){
					        var data = $('#jqxgridSupplier').jqxGrid('getrowdata', row);
					        return '<span><a href=\"viewSupplier?partyId=' + data.partyId + '\">' + value + '</a></span>';
					    }
					},
					{ text: '${uiLabelMap.BDSupplierName}', datafield: 'groupName', minwidth: 250 },
					{ text: '${uiLabelMap.PreferredCurrencyUomId}', datafield: 'preferredCurrencyUomId', width: 150, columntype: 'dropdownlist', filtertype : 'checkedlist' },
					{ text: '${uiLabelMap.BPOAddress1}', datafield: 'addressDetail', width: 300 },
					{ text: '${uiLabelMap.POEmailAddr}', datafield: 'infoString', width: 180 },
					{ text: '${uiLabelMap.POTelecomNumber}', datafield: 'contactNumber', width: 180 }"/>

<#assign addrow = "true" />
<@jqGrid filtersimplemode="true" customTitleProperties="BDListSupplier" filterable="true" id="jqxgridSupplier" dataField=dataField columnlist=columnlist
showtoolbar="true" addrow=addrow editable="false" editmode="selectedcell" deleterow="false" clearfilteringbutton="true"
addType="popup"
alternativeAddPopup="alterpopupWindow"
url="jqxGeneralServicer?sname=jqGetListPartySupplier" defaultSortColumn="groupName"
contextMenuId="contextMenu" mouseRightMenu="true"/>

<div id="contextMenu" style="display: none;">
    <ul>
        <li id="mnitemViewdetailnewtab"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}</li>
        <li id="mnitemViewdetail"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
        <li id="mnitemRefesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
    </ul>
</div>

<#include "popup/addSupplier.ftl"/>

<script>
    $(document).ready(function() {
        var mainGrid = $("#jqxgridSupplier");
        var contextmenu = $("#contextMenu").jqxMenu({ theme: theme, width: 220, autoOpenPopup: false, mode: "popup" });
        contextmenu.on("itemclick", function (event) {
            var args = event.args;
            var itemId = $(args).attr("id");
            var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
            var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
            switch (itemId) {
                case "mnitemEdit":
                    AddSupplier.open(rowData);
                    break;
                case "mnitemViewdetailnewtab":
                    if (rowData) {
                        var partyId = rowData.partyId;
                        var url = "viewSupplier?partyId=" + partyId;
                        var win = window.open(url, "_blank");
                        win.focus();
                    }
                    break;
                case "mnitemViewdetail":
                    if (rowData) {
                        var partyId = rowData.partyId;
                        var url = "viewSupplier?partyId=" + partyId;
                        var win = window.open(url, "_self");
                        win.focus();
                    }
                    break;
                case "mnitemRefesh":
                    mainGrid.jqxGrid("updatebounddata");
                    break;
                default:
                    break;
            }
        });
        AddSupplier.init();
    });
</script>