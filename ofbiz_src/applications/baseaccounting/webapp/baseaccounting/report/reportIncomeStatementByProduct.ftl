<style>
	.aggregates{
		font-weight: 600;
		text-align: right;
	}
</style>
<#--GRID-->
<div id="incomeStatementReportByProduct"></div>
<@jqOlbCoreLib hasCore=true />
<script>
	<#assign products = delegator.findList("Product", null, null, null, null, false)>
	var productData = [];
	<#list products as product>
		var row = {};
		row['productName'] = "${product.productName?if_exists}";
		row['productCode'] = "${product.productCode?if_exists}";
		row['productId'] = "${product.productId?if_exists}";
		productData.push(row);
	</#list>
</script>
<script type="text/javascript">
	var configDataSync = {};
	$(function () {
		var config = {
			title: '${StringUtil.wrapString(uiLabelMap.BACCProductIncomeStatement)}',
			button: true,
			service: "acctgTransTotal",
			id: 'incomeStatementReportByProduct',
			olap: 'olbiusReportIncomeStatement',
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
					text: '${StringUtil.wrapString(uiLabelMap.BACCProductId)}',
					datafield: {name: 'productCode', type: 'string'},
					width: '10%', filterable: true, pinned: true
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCProductName)}',
					datafield: {name: 'productName', type: 'string'}, width: 300, pinned: true
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCTransactionTime)}',
					datafield: {name: 'transTime', type: 'date'}, width: 140, filterable: false
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCSaleIncome)}',
					datafield: {name: 'saleIncome', type: 'number'},
					width: 150, filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				}    				
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCSaleDiscount)}',
					datafield: {name: 'saleDiscount', type: 'number'},
					width: 170, columngroup: 'deductions', filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				}    				
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCPromotion)}',
					datafield: {name: 'promotion', type: 'number'},
					width: 150, columngroup: 'deductions', filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				}    				
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCSaleReturn)}',
					datafield: {name: 'saleReturn', type: 'number'},
					width: 150, columngroup: 'deductions', filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				}    				
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCNetRevenue)}',
					datafield: {name: 'netRevenue', type: 'number'},
					width: 150, filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				}    				
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCCOGS)}',
					datafield: {name: 'cogs', type: 'number'},
					width: 150, filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				}    				
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCGrossProfit)}',
					datafield: {name: 'grossProfit', type: 'number'},
					width: 150, filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				}    				
				}
			],
			columngroups: [
                   { text: '${uiLabelMap.BACCDeductions}', align: 'center', name: 'deductions' },
                   { text: '${uiLabelMap.BACCSaleIncome}', align: 'center', name: 'saleIncome' },
            ],
			popup: [
				{
					action: 'jqxGridMultiple',
					params: {
						id : 'product',  
						label : '${StringUtil.wrapString(uiLabelMap.BACCProduct)}',
						grid: {
							source: productData,
							id: "productId",
							width: 550,
							sortable: true,
							pagesize: 5,
							columnsresize: true,
							pageable: true,
							altrows: true,
							showfilterrow: true,
							filterable: true,
							columns: [
								{ text: "${StringUtil.wrapString(uiLabelMap.BACCProductId)}", datafield: 'productCode', width: 150 }, 
								{ text: "${StringUtil.wrapString(uiLabelMap.BACCProductName)}", datafield: 'productName' }
							]
						}
					}
				},
				{
					group: "dateTime",
					id: "dateTime",
					params: { index: 2 }
				}
			],
			apply: function (grid, popup) {
				var dateTimeData = popup.group("dateTime").val();
       			configDataSync.fromDate = dateTimeData.fromDate;
            	configDataSync.thruDate = dateTimeData.thruDate;
				return $.extend({
					product: popup.val('product'),
					reportType: 'product'
				}, popup.group("dateTime").val());
			},
			excel: function(obj){
            	var isExistData = false;
				var dataRow = grid._grid.jqxGrid("getrows");
				if (typeof(dataRow) != 'undefined' && dataRow.length > 0) {
					isExistData = true;
				}
				if (!isExistData) {
					jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
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
				window.location.href = "exportIncomeStatementExcel?" + otherParam;
            },
			exportFileName: 'BAO_CAO_DOANH_THU_THEO_SP_' + (new Date()).formatDate("ddMMyyyy")
		};
		var grid = OlbiusUtil.grid(config);
	});
</script>
<#--CHART-->
<#include "productIncomeStmChart.ftl" />
<#--END-->