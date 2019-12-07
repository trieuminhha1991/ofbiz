<#include "script/supplierTargetScript.ftl"/>
<#include "supplierNewTargetPopup.ftl"/>

<#assign dataField="[{ name: 'partyId', type: 'string' },
					{ name: 'partyCode', type: 'string' },
					{ name: 'groupName', type: 'string' },
					{ name: 'productId', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'quantity', type: 'number' },
					{ name: 'fromDate', type: 'date', other: 'Timestamp'},
					{ name: 'thruDate', type: 'date', other: 'Timestamp'},
					{ name: 'quantityUomId', type: 'string' }]"/>

<#assign columnlist="{ text: '${uiLabelMap.BPOSupplierId}', datafield: 'partyCode', editable: false, width: 120 },
					{ text: '${uiLabelMap.POSupplierName}', datafield: 'groupName', editable: false, width: 220,
	 					cellsrenderer: function(row, column, value) {
	 						return '<span title=' + value +'>' + value + '</span>';
	 					}
					},
					{ text: '${uiLabelMap.POProductId}', datafield: 'productCode', editable: false, minwidth: 120 },
					{ text: '${uiLabelMap.BPOProductName}', datafield: 'productName', editable: false, minwidth: 200},
					{ text: '${uiLabelMap.DAUom}', datafield: 'quantityUomId', editable: false, width: 120, filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value) {
                            for (var i = 0; i < uomData.length; i++) {
                                if (uomData[i].uomId == value) {
                                    return '<span>' + uomData[i].description + '</span>';
                                }
                            }
                            return '<span>' + value + '</span>';
                        }, createfilterwidget: function (column, columnElement, widget) {
                            var filterDataAdapter = new $.jqx.dataAdapter(uomData, {
                                autoBind: true
                            });
                            var records = filterDataAdapter.records;
                            widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'uomId', dropDownWidth: 120, dropDownHeight: 150,
                                renderer: function(index, label, value){
                                    if (uomData.length > 0) {
                                        for(var i = 0; i < uomData.length; i++){
                                            if(uomData[i].uomId == value){
                                                return '<span>' + uomData[i].description + '</span>';
                                            }
                                        }
                                    }
                                    return value;
                                }
                            });
                        }
					},
					{ text: '${uiLabelMap.BPOQuantity}', datafield: 'quantity', filtertype : 'number', width: 80,
                        cellsrenderer: function(row, column, value){
                            if (value) {
                                return '<span style=\"text-align: right\">' + value.toLocaleString(locale) +'</span>';
                            }
                        }
                    },
                    { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', filtertype : 'range', cellsformat: 'dd/MM/yyyy HH:mm:ss', width: 150, },
                    { text: '${uiLabelMap.thruDate}', datafield: 'thruDate', filtertype : 'range', cellsformat: 'dd/MM/yyyy HH:mm:ss', width: 150, }
                    "/>

<#if hasOlbPermission("MODULE", "SUP_TARGET_NEW", "CREATE")>
	<#assign addrow = "true" />
<#else>
	<#assign addrow = "false" />
</#if>

<#if hasOlbPermission("MODULE", "SUP_TARGET_DELETE", "DELETE")>
	<#assign deleterow = "true" />
<#else>
	<#assign deleterow = "false" />
</#if>

<#if hasOlbPermission("MODULE", "SUP_TARGET_EDIT", "UPDATE")>
	<#assign editable = "true" />
<#else>
	<#assign editable = "false" />
</#if>

<@jqGrid filtersimplemode="true" filterable="true" id="jqxgridSupplierTargets" dataField=dataField columnlist=columnlist showtoolbar="true" addrow=addrow
	editable="false" deleterow=deleterow clearfilteringbutton="true" addType="popup" alternativeAddPopup="alterpopupWindow"
	url="jqxGeneralServicer?sname=jqGetListSupplierTarget"
	createUrl="jqxGeneralServicer?sname=createSupplierTarget&jqaction=C"
	updateUrl="jqxGeneralServicer?sname=updateSupplierTarget&jqaction=U"
	removeUrl="jqxGeneralServicer?sname=deleteSupplierTarget&jqaction=D"
	addColumns="partyId;productId;quantityUomId;quantity(java.math.BigDecimal);fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
	deleteColumn="partyId;productId;quantityUomId;fromDate(java.sql.Timestamp)"
    contextMenuId="contextMenu" mouseRightMenu=editable
/>
<#include "supplierEditTargetPopup.ftl"/>

<div id="contextMenu" style="display: none;">
    <ul>
    <#if hasOlbPermission("MODULE", "SUP_TARGET_EDIT", "UPDATE")>
        <li id="mnitemEdit"><i class="fa fa-pencil"></i>${uiLabelMap.CommonEdit}</li>
    </#if>
        <li id="mnitemRefesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
    </ul>
</div>

<script type="text/javascript">
    $(function () {
        var mainGrid = $("#jqxgridSupplierTargets");
        var contextmenu = $("#contextMenu").jqxMenu({ theme: theme, width: 220, autoOpenPopup: false, mode: "popup" });
        contextmenu.on("itemclick", function (event) {
            var args = event.args;
            var itemId = $(args).attr("id");
            var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
            var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
            switch (itemId) {
                case "mnitemEdit":
                    var selectedRowIndex = mainGrid.jqxGrid('selectedrowindex');
                    var data = mainGrid.jqxGrid('getrowdata', selectedRowIndex);
                    supplierEditTarget.openWindow(data);
                    break;
                case "mnitemRefesh":
                    mainGrid.jqxGrid("updatebounddata");
                    break;
                default:
                    break;
            }
        });

    }());
</script>