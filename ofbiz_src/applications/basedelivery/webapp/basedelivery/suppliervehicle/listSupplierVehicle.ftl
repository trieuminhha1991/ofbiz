<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/imexresources/js/notify.js"></script>

<script type="text/javascript">
    <#assign vehicles = delegator.findByAnd("VehicleV2", null, null, false)/>
    var vehicleData = [
    <#if vehicles?exists>
        <#list vehicles as vehicle>
            {
                vehicleId: '${vehicle.vehicleId}',
                licensePlate: '${StringUtil.wrapString(vehicle.get("licensePlate", locale))}'
            },
        </#list>
    </#if>];
</script>

<script>
    var commercialPeriodArray = [
    <#if listPeriod?has_content>
        <#list listPeriod as item>
        ${item.periodName?if_exists},
        </#list>
    </#if>
    ];
    var currentTime = new Date();
    var currentYear = currentTime.getFullYear();
</script>

<#assign dataField="[{ name: 'supplierVehicleId', type: 'string' },
					{ name: 'vehicleId', type: 'string'},
					{ name: 'partyId', type: 'string'},
					{ name: 'fromDate', type: 'string' },
					{ name: 'thruDate', type: 'string' },
					{ name: 'statusId', type: 'string' }
					]
					"/>

<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.BDSequenceId)}',sortable: false, filterable: false, pinned: true,
						groupable: false, draggable: false, resizable: false, datafield: '', columntype: 'number', width: 50,
						cellsrenderer: function (row, column, value) {
							return \"<div style='margin-top: 3px; text-align: left; '>\" + (value + 1)+ \"</div>\";
						}
					},
					{ text: '${uiLabelMap.BDSupplierVehicleId}', datafield: 'supplierVehicleId', width: '13%' },
					{ text: '${uiLabelMap.BDPartyId}', datafield: 'partyId', minwidth: 250,
					    cellsrenderer: function(row, column, value){
					  var partyName = value;
					  $.ajax({
							url: 'getPartyName',
							type: 'POST',
							data: {partyId: value},
							dataType: 'json',
							async: false,
							success : function(data) {
								if(!data._ERROR_MESSAGE_){
									partyName = data.partyName;
								}
					        }
						});
					  return '<span title' + value + '>' + partyName + '</span>';}
					},
					{ text: '${uiLabelMap.BDVehicleId}', datafield: 'vehicleId', width: 200, cellsrenderer: function(row, column, value){
					    if(!value) return '_NA_';
						if (vehicleData.length > 0) {
							for(var i = 0 ; i < vehicleData.length; i++){
    							if (value == vehicleData[i].vehicleId){
    							return \"<span><a href='editVehicle?vehicleId=\" + value + \"'>\" + vehicleData[i].licensePlate + '[' + vehicleData[i].vehicleId + ']' +\"</a></span>\";
    							}
    						}
						}
						return '<span title=' + value +'>' + value + '</span>';} },
					"/>

<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist showtoolbar="true" addrow="true"
editable="false" deleterow="false" id="jqxgridSupplierTargets"
addrefresh="true" clearfilteringbutton="true" addType="popup"
alternativeAddPopup="alterpopupWindow"
url="jqxGeneralServicer?sname=JQGetListSupplierVehicle"
/>

<#include "script/supplierVehicleScript.ftl"/>
<#include "popup/addSupplierVehicle.ftl"/>
<div id="contextMenu" style="display: none;">
    <ul>
        <li id="mnitemEdit"><i class="fa fa-pencil"></i>${uiLabelMap.CommonEdit}</li>
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