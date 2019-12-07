<div id="inventoryTurnoverRatio"></div>
<script type="text/javascript">
	$(function () {
		var config = {
			title: '${StringUtil.wrapString(uiLabelMap.InventoryTurnoverRatio)}',
			button: true,
			service: 'facilityInventory',
			id: 'inventoryTurnoverRatio',
			olap: 'olapInventoryTurnoverRatio',
			sortable: true,
			filterable: true,
			showfilterrow: true,
			columns: [
				{ 
					text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<div style=margin:4px;>' + (value + 1) + '</div>';
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BLTime)}',
					datafield: {name: 'dateTime', type: 'string'},
					width: '10%',
					filterable: false
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BLFacilityId)}',
					datafield: {name: 'facility_code', type: 'string'}, width: '8%',
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BLFacilityName)}',
					datafield: {name: 'facility', type: 'string'}, width:  '10%',
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BLSKUCode)}',
					datafield: {name: 'product_code', type: 'string'},width: '12%',
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.ProductName)}',
					datafield: {name: 'product', type: 'string'}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BLCostInventoryBegin)}',
					datafield: {name: 'inventoryBegin', type: 'string'}, width: '10%',
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
						}
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BLCostOfGoodSold)}',
					datafield: {name: 'cogs', type: 'string'}, width: '10%',
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
						}
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BLCostInventoryEnd)}',
					datafield: {name: 'inventoryEnd', type: 'string'}, width: '10%',
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
						}
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BLTurnoverRatio)}',
					datafield: {name: 'turnoverRatio', type: 'string'}, width: '10%',
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
						}
					}
				}
			],
			popup: [
				<#if facilities?? && facilities?index_of(",", 0) != -1>
				{
					action: 'jqxGridMultiple',
					params: {
						id: 'facility',
						label: '${StringUtil.wrapString(uiLabelMap.Facility)}',
						grid: {
							source: ${StringUtil.wrapString(facilities)},
							id: "facilityId",
							width: 550,
							sortable: true,
							pagesize: 5,
							columnsresize: true,
							pageable: true,
							altrows: true,
							showfilterrow: true,
							filterable: true,
							columns:
							[
								{ text: "${StringUtil.wrapString(uiLabelMap.FacilityId)}", datafield: 'facilityCode', width: 150 }, 
								{ text: "${StringUtil.wrapString(uiLabelMap.FacilityName)}", datafield: 'facilityName' }
							]
						}
					}
				},</#if>
				{
					group: 'dateTime',
					id: 'dateTime',
					params: { index: 2 }
				}
			],
			apply: function (grid, popup) {
				return $.extend({
					facility: popup.val("facility"),
					group: ['facility'],
				}, popup.group('dateTime').val());
			},
			excel: function(obj){
            	var isExistData = false;
				var dataRow = grid._grid.jqxGrid("getrows");
				if (typeof(dataRow) != 'undefined' && dataRow.length > 0) {
					isExistData = true;
				}
				if (!isExistData) {
					OlbCore.alert.error("${uiLabelMap.BSNoDataToExport}");
					return false;
				}
				
				var otherParam = "";
				if (obj._data) {
					$.each(obj._data, function(key, value){
						otherParam += "&" + key + "=" + value;
					});
				}
				var filterObject = grid.getFilter();
				if (filterObject && filterObject.filter) {
					var filterData = filterObject.filter;
					for (var i = 0; i < filterData.length; i++) {
						otherParam += "&filter=" + filterData[i];
					}
				}
				window.open("exportInventoryTurnoverRatioExcel?" + otherParam, "_blank");
            },
            exportFileName: '[LOG]_BC_VONG_QUAY_TON_KHO_' + (new Date()).formatDate("ddMMyyyy")
		};
		var grid = OlbiusUtil.grid(config);
	});
</script>