<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#include "popup/viewRoutesAssigned.ftl"/>
<#include "popup/viewRoutesAssignedOnMap.ftl"/>
<#include "popup/viewStoresAssigned.ftl"/>
<#include "popup/viewStoresAssignedOnMap.ftl"/>
<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_GT", userLogin, true)
        ||Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_MT", userLogin, true)>
<#include "popup/viewCheckInHistoryOnMap.ftl"/>
</#if>
<#include "../../loader/loader.ftl"/>
<#assign mouseRightMenu="true" />
<#if security.hasEntityPermission("PARTYSALESMAN", "_VIEW", session)>
<div id="contextMenu" style="display:none;">
	<ul>
		<li id="viewListRoutesAssigned"><i class="fa-eye"></i>&nbsp;&nbsp;${uiLabelMap.BSViewListRoutesAssigned}</li>
		<li id="viewListStoresAssigned"><i class="fa-eye"></i>&nbsp;&nbsp;${uiLabelMap.BSViewListStoresAssigned}</li>
        <li id="viewListRoutesAssignedOnMap"><i class="fa-map-marker"></i>&nbsp;&nbsp;${uiLabelMap.BSViewListStoresAssignedOnMap}</li>
		<#--<li id="viewStoresAssignedOnMap"><i class="fa-map-marker"></i>&nbsp;&nbsp;${uiLabelMap.BSViewListStoresAssignedOnMap}</li>-->
		<#--<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_GT", userLogin, true)
        ||Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_MT", userLogin, true)>
			<li id="viewCheckIn"><i class="fa-map-marker"></i>&nbsp;&nbsp;${uiLabelMap.BSCheckInHistory}</li>
		</#if>-->
	</ul>
</div>
<script>
	var mapGender = {'M': multiLang.male, 'F': multiLang.female};
	$(document).ready(function() {
		var mainGrid = $("#jqxgridSalesman");
		$("#contextMenu").jqxMenu({ theme: "olbius", width: 290, autoOpenPopup: false, mode: "popup"});
		$("#contextMenu").on("itemclick", function (event) {
	        var args = event.args;
	        var itemId = $(args).attr("id");
	        switch (itemId) {
			case "viewListStoresAssigned":
				var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
				var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
				if (rowData) {
					$(".salesmanInfo").text(rowData.fullName + " [" + rowData.partyId + "]");
					ListStores.open(rowData.partyId);
				}
				break;
			case "viewListRoutesAssigned":
				var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
				var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
				if (rowData) {
					$(".salesmanInfo").text(rowData.fullName + " [" + rowData.partyId + "]");
					ListRoutes.open(rowData.partyId);
				}
				break;
			case "viewListRoutesAssignedOnMap":
                var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
                var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
                if (rowData) {
                    $(".salesmanInfo").text(rowData.fullName + " [" + rowData.partyId + "]");
                    setTimeout(function(){ OlbRouteSalesmanOnMap.open(rowData); }, 300);

                }
				break;
			/*case "viewStoresAssignedOnMap":
				var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
				var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
				if (rowData) {
					$(".salesmanInfo").text(rowData.fullName + " [" + rowData.partyId + "]");
					setTimeout(function(){ ListStoresOnMap.open(rowData); }, 300);
					
				}
				break;*/
			<#--<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_GT", userLogin, true)
        ||Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_MT", userLogin, true)>
			case "viewCheckIn":
				var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
				var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
				if (rowData) {
					$(".salesmanInfo").text(rowData.fullName + " [" + rowData.partyId + "]");
					setTimeout(function(){ checkInHistoryOnMap.open(rowData); }, 300);
					
				}
				break;
				</#if>-->
			default:
				break;
			}
		});
	});
</script>
	<#else>
	<#assign mouseRightMenu="false">
</#if>
<#assign dataField="[{ name: 'partyId', type: 'string' },
					{ name: 'partyCode', type: 'string' },
					{ name: 'fullName', type: 'string' },
					{ name: 'gender', type: 'string'},
				    { name: 'birthDate', type: 'date', other: 'date'},
					{ name: 'contactNumber', type: 'string' },
					{ name: 'supervisorName', type: 'string' }]"/>
<#assign columnlist = "{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (row + 1) + '</div>';
						    }
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: 150},
						{ text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'fullName', minWidth: 200},
						{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyGender)}' , datafield: 'gender', width: 100, columntype: 'dropdownlist',
							cellsrenderer: function (row, column, value) {
								value?value=mapGender[value]:value;
						        return '<div style=margin:4px;>' + value + '</div>';
						    }
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyBirthDate)}', datafield: 'birthDate', cellsformat: 'dd/MM/yyyy', width: 200, filtertype: 'range', columntype: 'datetimeinput'},
						{ text: '${StringUtil.wrapString(uiLabelMap.PhoneNumber)}', datafield: 'contactNumber', width: 200, sortable: false},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSSupervisor)}', datafield: 'supervisorName', sortable: false, filterable: false, width: 200}"/>
<#if partyIdFrom?exists>
	<#assign url = "jqxGeneralServicer?sname=JQGetListSalesmanAssigned&partyIdFrom=${partyIdFrom}"/>
<#else>
	<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_GT", userLogin, true)
	|| Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_MT", userLogin, true)>
		<#assign url = "jqxGeneralServicer?sname=JQGetListSalesman"/>
	<#else>
		<#assign url = "jqxGeneralServicer?sname=JQGetListSalesmanAssigned"/>
	</#if>
</#if>

<@jqGrid url=url dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
	showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup"
	defaultSortColumn="partyId" sortdirection="desc" id="jqxgridSalesman" 
	addrow="false" contextMenuId="contextMenu" mouseRightMenu=mouseRightMenu/>