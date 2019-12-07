<style>
	.aggregates{
		font-weight: 600;
		text-align: right;
	}
</style>
<#--GRID-->
<div id="incomeStatementReportByCustomer"></div>
<@jqOlbCoreLib hasCore=true />
<script>
	var customerData = [
		<#if customers?exists>
			<#list customers as item>
				{
					partyId: "${item.partyId?if_exists}",
					partyCode: "${item.partyCode?if_exists}",
					fullName: "${item.fullName?if_exists}"
				},
			</#list>
		</#if>
	];
</script>
<script type="text/javascript">
	var configDataSync = {};
	$(function () {
		var config = {
			title: '${StringUtil.wrapString(uiLabelMap.BACCCustomerIncomeStatement)}',
			button: true,
			service: "acctgTransTotal",
			id: 'incomeStatementReportByCustomer',
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
					text: '${StringUtil.wrapString(uiLabelMap.BACCCustomerId)}',
					datafield: {name: 'partyCode', type: 'string'},
					width: '12%', filterable: true
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCCustomerName)}',
					datafield: {name: 'fullName', type: 'string'},
					width: '14%', filterable: true
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCTransactionTime)}',
					datafield: {name: 'transTime', type: 'date'}, width: 140, filterable: false
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCSaleIncome)}',
					datafield: {name: 'saleIncome', type: 'number'},
					width: 150, filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				},
    				aggregatesrenderer: function (aggregates, column, element, summaryData){
               			var renderstring = '<div class=aggregates>'
                        $.each(aggregates, function (key, value) {
  	                    	renderstring += key + formatcurrency(value);
                       	});
               		    renderstring += '</div>';
                        return renderstring;
  	             	},
  	             	aggregates: [{ '':
  	             		function (aggregatedValue, currentValue) {
  	             			if (currentValue) {
  	             		    	return aggregatedValue + currentValue;
  	             		    }
  	             		    return aggregatedValue;
  	             		}
  	             	}]
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCSaleDiscount)}',
					datafield: {name: 'saleDiscount', type: 'number'},
					width: 170, columngroup: 'deductions', filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				},
    				aggregatesrenderer: function (aggregates, column, element, summaryData){
           		    	var renderstring = '<div class=aggregates>'
                    	$.each(aggregates, function (key, value) {
                        	renderstring += key + formatcurrency(value);
                    	});
       		     		renderstring += '</div>';
                    	return renderstring;
             	  	},
             	  	aggregates: [{ '':
             			function (aggregatedValue, currentValue) {
             		    	if (currentValue) {
             		        	return aggregatedValue + currentValue;
             		        }
             		        return aggregatedValue;
             		    }
             	  	}]
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCPromotion)}',
					datafield: {name: 'promotion', type: 'number'},
					width: 150, columngroup: 'deductions', filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				},
    				aggregatesrenderer: function (aggregates, column, element, summaryData){
           		    	var renderstring = '<div class=aggregates>'
                        $.each(aggregates, function (key, value) {
                                renderstring += key + formatcurrency(value);
                        });
           		     	renderstring += '</div>';
                        return renderstring;
             	  	},
             	  	aggregates: [{ '':
             			function (aggregatedValue, currentValue) {
             		    	if (currentValue) {
             		        	return aggregatedValue + currentValue;
             		        }
             		        return aggregatedValue;
             		    }
             	  	}]
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCSaleReturn)}',
					datafield: {name: 'saleReturn', type: 'number'},
					width: 150, columngroup: 'deductions', filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				},
    				aggregatesrenderer: function (aggregates, column, element, summaryData){
           		    	var renderstring = '<div class=aggregates>'
                        $.each(aggregates, function (key, value) {
                                renderstring += key + formatcurrency(value);
                        });
           		     	renderstring += '</div>';
                        return renderstring;
             	  	},
             	  	aggregates: [{ '':
             			function (aggregatedValue, currentValue) {
             		    	if (currentValue) {
             		        	return aggregatedValue + currentValue;
             		        }
             		        return aggregatedValue;
             		    }
             	  	}]
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCNetRevenue)}',
					datafield: {name: 'netRevenue', type: 'number'},
					width: 150, filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				},
    				aggregatesrenderer: function (aggregates, column, element, summaryData){
           		    	var renderstring = '<div class=aggregates>'
                        $.each(aggregates, function (key, value) {
                                renderstring += key + formatcurrency(value);
                        });
           		     	renderstring += '</div>';
                        return renderstring;
             	  	},
             	  	aggregates: [{ '':
             			function (aggregatedValue, currentValue) {
             		    	if (currentValue) {
             		            return aggregatedValue + currentValue;
             		        }
             		        return aggregatedValue;
             		    }
             	  	}]
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCCOGS)}',
					datafield: {name: 'cogs', type: 'number'},
					width: 150, filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				},
    				aggregatesrenderer: function (aggregates, column, element, summaryData){
           		    	var renderstring = '<div class=aggregates>'
                        $.each(aggregates, function (key, value) {
                                renderstring += key + formatcurrency(value);
                        });
           		     	renderstring += '</div>';
                        return renderstring;
             	  	},
             	  	aggregates: [{ '':
             			function (aggregatedValue, currentValue) {
             		    	if (currentValue) {
             		            return aggregatedValue + currentValue;
             		        }
             		        return aggregatedValue;
             		    }
             	  	}]
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCGrossProfit)}',
					datafield: {name: 'grossProfit', type: 'number'},
					width: 150, filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				},
    				aggregatesrenderer: function (aggregates, column, element, summaryData){
           		    	var renderstring = '<div class=aggregates>'
                        $.each(aggregates, function (key, value) {
                                renderstring += key + formatcurrency(value);
                        });
           		     	renderstring += '</div>';
                        return renderstring;
             	  	},
             	  	aggregates: [{ '':
             			function (aggregatedValue, currentValue) {
             		    	if (currentValue) {
             		            return aggregatedValue + currentValue;
             		        }
             		        return aggregatedValue;
             		    }
             	  	}]
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
						id: 'party',
						label: '${StringUtil.wrapString(uiLabelMap.BACCCustomer)}',
						grid: {
							source: customerData,
							id: "partyId",
							width: 550,
							sortable: true,
							pagesize: 5,
							columnsresize: true,
							pageable: true,
							altrows: true,
							showfilterrow: true,
							filterable: true,
							columns: [
								{ text: "${StringUtil.wrapString(uiLabelMap.BACCCustomerId)}", datafield: 'partyCode', width: 150 },
			          			{ text: "${StringUtil.wrapString(uiLabelMap.BACCCustomer)}", datafield: 'fullName'}
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
					party: popup.val('party'),
					reportType: 'party'
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
			exportFileName: 'BAO_CAO_DOANH_THU_THEO_KH_' + (new Date()).formatDate("ddMMyyyy")
		};
		var grid = OlbiusUtil.grid(config);
	});
</script>
<#--CHART-->
<#include "customerIncomeStmChart.ftl" />
<#--END-->