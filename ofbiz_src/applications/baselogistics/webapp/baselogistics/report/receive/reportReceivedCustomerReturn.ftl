<div id="olbiusReceivedReport"></div>
<script type="text/javascript">
    $(function() {
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap.BLReceivedCustomerReturnReport)}",
            service: "facilityInventory",
            button: true,
            id: "olbiusReceivedReport",
            olap: "olapReceivedCustomerReturnReport",
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
					width: 100,
					filterable: false
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BLFacilityId)}',
					datafield: {name: 'facility_code', type: 'string'}, width: 100,
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BLFacilityName)}',
					datafield: {name: 'facility_name', type: 'string'}, width: 200,
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BLSKUCode)}',
					datafield: {name: 'product_code', type: 'string'},width: 120,
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.ProductName)}',
					datafield: {name: 'product', type: 'string'}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BLCategoryProduct)}',
					datafield: {name: 'category_id', type: 'string'}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.Unit)}',
					datafield: {name: 'uom', type: 'string'},
					width: 100
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.Quantity)}',
					datafield: {name: 'quantity', type: 'string'}, width: 100,
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
                {
                    group: "dateTime",
                    id: "dateTime"
                },
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
					OlbCore.alert.error("${uiLabelMap.BLReceivedReport}");
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
				window.open("exportReceivedCustomerReturnReportExcel?" + otherParam, "_blank");
            },
            exportFileName: '[LOG]_BC_NHAP_KHO_KH_TRA_LAI' + (new Date()).formatDate("ddMMyyyy")
        };
        
        var grid = OlbiusUtil.grid(config);
    });
    
</script>