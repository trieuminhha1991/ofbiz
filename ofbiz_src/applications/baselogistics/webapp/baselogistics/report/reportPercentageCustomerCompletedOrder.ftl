<div id="percentageCustomerCompletedOrder"></div>
<script type="text/javascript">
	$(function () {
		var config = {
			title: '${StringUtil.wrapString(uiLabelMap.BLPercentageCustomerCompletedOrder)}',
			button: true,
			service: 'orderStatus',
			id: 'percentageCustomerCompletedOrder',
			olap: 'olapPercentageCustomerCompletedOrder',
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
					text: '${StringUtil.wrapString(uiLabelMap.BLCustomerCode)}',
					datafield: {name: 'party_code', type: 'string'}, width: 200,
					cellsrenderer: function (row, column, value) {
						return '<div class=\"text-left\">' + value + '</div>';
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BLCustomerName)}',
					datafield: {name: 'party_name', type: 'string'}, width: 400,
					cellsrenderer: function (row, column, value) {
						return '<div class=\"text-left\">' + value + '</div>';
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BLTotalOrderCompleted)}',
					datafield: {name: 'order_num', type: 'string'}, width: 200,
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
						}
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BLTotalOrder)}',
					datafield: {name: 'order_num_total', type: 'string'}, width: 230,
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
						}
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BLPercentageOrder)}',
					datafield: {name: 'percentage_order', type: 'string'}, width: 230,
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
						}
					}
				},  
			],
			popup: [
				{
					group: 'dateTime',
					id: 'dateTime',
					params: { index: 2 }
				}
			],
			apply: function (grid, popup) {
				return $.extend({
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
				window.open("exportPercentageCustomerCompletedOrderExcel?" + otherParam, "_blank");
            },
            exportFileName: '[LOG]_BC_KHACH_HANG_HOAN_THANH_DON_HANG_BAN_' + (new Date()).formatDate("ddMMyyyy")
		};
		var grid = OlbiusUtil.grid(config);
	});
</script>