<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<#assign dataField="[{ name: 'eventId', type: 'string' },
					{ name: 'eventName', type: 'string' },
					{ name: 'facilityId', type: 'string' },
					{ name: 'facilityCode', type: 'string' },
					{ name: 'facilityName', type: 'string' },
					{ name: 'fromDate', type: 'date', other: 'Timestamp' },
					{ name: 'thruDate', type: 'date', other: 'Timestamp' },
					{ name: 'isClosed', type: 'string' }]"/>

<#assign columnlist = "
	{ text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50, cellclassname: cellclassname,
		cellsrenderer: function (row, column, value) {
			return '<div style=margin:4px;>' + (row + 1) + '</div>';
		}
	},"/>
<#if hasOlbPermission("MODULE", "PARTY_DISTRIBUTOR", "VIEW")>
	<#if hasOlbPermission("MODULE", "DISTRIBUTOR", "ADMIN")>
		<#assign columnlist = columnlist + "{ text: '${StringUtil.wrapString(uiLabelMap.PhysicalInventoryId)}', datafield: 'eventId', width: 130, cellclassname: cellclassname,
			cellsrenderer: function (row, column, value, a, b, data) {
				var link = 'disViewInventoryStockingDis?eventId=' + value;
				return '<a href=\"' + link + '\">' + value + '</a>';
			}
		},"/>
	<#else>
		<#assign columnlist = columnlist + "{ text: '${StringUtil.wrapString(uiLabelMap.PhysicalInventoryId)}', datafield: 'eventId', width: 130, cellclassname: cellclassname,
			cellsrenderer: function (row, column, value, a, b, data) {
				var link = 'viewInventoryStockingDis?eventId=' + value;
				return '<a href=\"' + link + '\">' + value + '</a>';
			}
		},"/>
	</#if>
<#else>
	<#assign columnlist = columnlist + "{ text: '${StringUtil.wrapString(uiLabelMap.PhysicalInventoryId)}', datafield: 'eventId', width: 130, cellclassname: cellclassname,
		cellsrenderer: function (row, column, value, a, b, data) {
			var link = 'InventoryStocking?eventId=' + value;
			return '<a href=\"' + link + '\">' + value + '</a>';
		}
	},"/>
</#if>

<#assign columnlist = columnlist + "{ text: '${StringUtil.wrapString(uiLabelMap.DmsStockEventName)}', datafield: 'eventName', width: 200, cellclassname: cellclassname },
	{ text: '${StringUtil.wrapString(uiLabelMap.FacilityId)}', datafield: 'facilityId', width: 130, cellclassname: cellclassname, hidden: true },
	{ text: '${StringUtil.wrapString(uiLabelMap.FacilityCode)}', datafield: 'facilityCode', width: 130, cellclassname: cellclassname},
	{ text: '${StringUtil.wrapString(uiLabelMap.FacilityName)}', datafield: 'facilityName', minwidth: 200, cellclassname: cellclassname },
	{ text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', columntype: 'datetimeinput',width: 150, cellsformat: 'dd/MM/yyyy', filtertype:'range', cellclassname: cellclassname },
	{ text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', columntype: 'datetimeinput',width: 150, cellsformat: 'dd/MM/yyyy', filtertype:'range', cellclassname: cellclassname },
	{ text: '${StringUtil.wrapString(uiLabelMap.BSIsClosed)}', datafield: 'isClosed', width: 80, cellclassname: cellclassname }
"/>

<#assign addrow = "false"/>
<#if hasOlbPermission("MODULE", "INVENTORY_STOCKING", "CREATE")>
<#assign addrow = "true"/>
</#if>

<@jqGrid id="jqxgridStockEvents" addrow=addrow clearfilteringbutton="true" editable="false" alternativeAddPopup="addStockEvent"
	columnlist=columnlist dataField=dataField contextMenuId="contextMenu" mouseRightMenu="true"
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	url="jqxGeneralServicer?sname=JQGetListStockEvents"/>

	<div id="contextMenu" style="display:none;">
		<ul>
			<li id="mnuEdit"><i class="fa fa-pencil-square-o"></i>&nbsp;${uiLabelMap.CommonEdit}</li>
		</ul>
	</div>

<#include "popup/addStockEvent.ftl"/>
<script>
	var mainGrid = $("#jqxgridStockEvents");
	var cellclassname = function (row, column, value, data) {
		if (data.thruDate) {
			var thruDate = data.thruDate.getTime();
			var nowDate = new Date().getTime();
			if (thruDate < nowDate) {
				return "jqx-grid-cell-expired";
			}
		}
		return "";
	};
	$(document).ready(function() {
		var contextmenu = $("#contextMenu").jqxMenu({ theme: theme, width: 180, autoOpenPopup: false, mode: "popup" });
		contextmenu.on("itemclick", function (event) {
			var args = event.args;
			var itemId = $(args).attr("id");
			switch (itemId) {
			case "mnuEdit":
				var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
				AddStockEvent.open(mainGrid.jqxGrid("getcellvalue", rowIndexSelected, "eventId"), mainGrid.jqxGrid("getcellvalue", rowIndexSelected, "facilityId"),
						mainGrid.jqxGrid("getcellvalue", rowIndexSelected, "eventName"));
				break;
			default:
				break;
			}
		});
		contextmenu.on("shown", function () {
			<#if hasOlbPermission("MODULE", "INVENTORY_STOCKING", "UPDATE")>
				var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
				var thruDate = mainGrid.jqxGrid("getcellvalue", rowIndexSelected, "thruDate");
				if (thruDate) {
					contextmenu.jqxMenu("disable", "mnuEdit", true);
				} else {
					contextmenu.jqxMenu("disable", "mnuEdit", false);
				}
			<#else>
				contextmenu.jqxMenu("disable", "mnuEdit", true);
			</#if>
		});
	});
</script>