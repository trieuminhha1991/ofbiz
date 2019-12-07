<style>
	.aggregates{
		font-weight: 600;
		text-align: right;
	}
</style>
<#include "script/reportDataCommon.ftl">
<@jqOlbCoreLib />
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<div id="expenseStatementGrid"></div>
<script type="text/javascript">
$(document).ready(function() {
	var config = {
			title: '${StringUtil.wrapString(uiLabelMap.BACCExpenseStatement)}',
            service: 'acctgTransTotal',
            button: true,
            id: "expenseStatementGrid",
            olap: "getExpenseStatementOlap",
            sortable: true,
            filterable: true,
            showfilterrow: true,
            pagesizeoptions: [15, 20, 30, 50, 100, 550],
            pagesize: 15,
            columns: [
            		{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
						groupable: false, draggable: false, resizable: false, datafield: '', columntype: 'number', width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (value + 1) + '</div>';
						}
					}, 
            		{  text: '${uiLabelMap.BACCTransactionTime}', datafield: {name: 'transTime', type: 'string'}, filterable: false,  width: 200},
					{  text: '${uiLabelMap.BACCGlAccountId}', datafield: {name: 'glAccountId', type: 'string'}, width: 150},
                    {  text: '${uiLabelMap.BACCGlAccountName}', datafield: {name: 'accountName', type: 'string'}},
                  	{  text: '${uiLabelMap.BACCAmount}', datafield: {name: 'amount', type: 'number'}, width: 300, cellsalign: 'right', filtertype: 'number',
                      	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
                    	  	if(value){
    						  	return '<span  class=align-right>' + formatcurrency(value) + '</span>';
    					  	} else {
    					  		return '<span  class=align-right>' + formatcurrency(0) + '</span>';
    					  	}
		          		}
                  	}
            ],
            popup: [
				{
				    group: "dateTime",
				    id: "dateTime",
				    params: {
                    	index: 0
                    }
				},        
            ],
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
					$.each(obj._data, function(key, value) {
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
				window.open("exportExpenseStatementExcel?" + otherParam, "_blank");
			},
            exportFileName: 'BAO_CAO_CHI_PHI_' + (new Date()).formatDate("ddMMyyyy"),
            apply: function (grid, popup) {
            	var dateTimeData = popup.group("dateTime").val();
            	return dateTimeData;
            }
	};
	var grid = OlbiusUtil.grid(config);
});
</script>