<style>
	.aggregates{
		font-weight: 600;
		text-align: right;
	}
</style>

<div id="customerIncomeGrid"></div>

<script type="text/javascript">
	var configDataSync = {};
	var CIPC;
	$( document ).ready(function() {
		bindingDataToReport();
	});
	
	function initConfigPopup(){
        var configPopup = [
			{
			    group: "dateTime",
			    id: "dateTime"
			},
			{
			    action: 'jqxGridElement',
			    params: {
			        id: 'customer',
			        label: "${StringUtil.wrapString(uiLabelMap.BACCCustomer)}",
			        grid: {
			        	url: 'JqxGetCustomerList',
			        	id: 'partyId',
			        	width: 550,
			        	sortable: true,
			            pagesize: 5,
			            columnsresize: true,
			            pageable: true,
			            altrows: true,
			            showfilterrow: true,
			            filterable: true,
			            datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
			            displayField: 'fullName',
			            displayAdditionField: 'partyCode',
			            gridTitle: '${StringUtil.wrapString(uiLabelMap.BSListCustomer)}',
			            clearSelectionBtnText: '${StringUtil.wrapString(uiLabelMap.BACCClearSelection)}',
			        	columns: [
				          	{ text: "${StringUtil.wrapString(uiLabelMap.BACCCustomerId)}", datafield: 'partyCode', width: 150 }, 
				          	{ text: "${StringUtil.wrapString(uiLabelMap.BACCCustomer)}", datafield: 'fullName'}
				        ]
			        }
			    },
			    hide: false
			},
        ];
        return configPopup;
	}
	
	function bindingDataToReport(){
		var configPopup = initConfigPopup();
		var config = {
                title: ' ${uiLabelMap.BACCCustomerIncomeStatement}',
                service: 'acctgTransTotal',
                button: true,
                id: "customerIncomeGrid",
                olap: "getCustomerIncomeStm",
                sortable: true,
                filterable: true,
                showfilterrow: true,
                showstatusbar: false,
                showaggregates: false,
                pagesizeoptions: [15, 20, 30, 50, 100],
                pagesize: 15,
                columns: [
                          { text: '${uiLabelMap.BACCCustomerId}', datafield: {name: 'customerCode', type: 'string'}, width: '12%',},
                          { text: '${StringUtil.wrapString(uiLabelMap.BSCustomerName)}', datafield: {name: 'customerName', type: 'string'}, width: '14%',},
                          { text: '${StringUtil.wrapString(uiLabelMap.CommonTime)}', datafield: {name: 'dateTime', type: 'string'}, width: 120},
            			  { text: '${uiLabelMap.BACCSaleIncome}', datafield: {name: 'saleIncome', type: 'number'}, width: 150,
                        	  filtertype: 'number', columntype: 'numberinput',
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
            			  },
                          { text: '${uiLabelMap.BACCSaleDiscount}', datafield: {name: 'saleDiscount', type: 'number'}, width: 150, columngroup: 'deductions',
            				  filtertype: 'number', columntype: 'numberinput',
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
            			  },
            			  { text: '${uiLabelMap.BACCPromotion}', datafield: {name: 'promotion', type: 'number'}, width: 150, columngroup: 'deductions',
            				  filtertype: 'number', columntype: 'numberinput',
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
            			  },
            			  { text: '${uiLabelMap.BACCSaleReturn}', datafield: {name: 'saleReturn', type: 'number'}, width: 150, columngroup: 'deductions',
            				  filtertype: 'number', columntype: 'numberinput',
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
            			  },
            			  { text: '${uiLabelMap.BACCNetRevenue}', datafield: {name: 'netRevenue', type: 'number'}, width: 150,
            				  filtertype: 'number', columntype: 'numberinput',
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
            			  },
            			  { text: '${uiLabelMap.BACCCOGS}', datafield: {name: 'cogs', type: 'number'}, width: 150,
            				  filtertype: 'number', columntype: 'numberinput',
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
            			  },
            			  { text: '${uiLabelMap.BACCGrossProfit}', datafield: {name: 'grossProfit', type: 'number'}, width: 150,
            				  filtertype: 'number', columntype: 'numberinput',
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
                        popup: configPopup,
                        excel: function(oLap){
                       		var params = oLap._data;
                       	 	var fromDate = params.fromDate;
                       	 	var thruDate = params.thruDate;
                       	 	if(typeof(fromDate) != 'undefined'){
                       			var tempFromDate = new Date(fromDate);
                       		 	params.fromDate = tempFromDate.getTime(); 
                       	 	}
                       	 	if(typeof(thruDate) != 'undefined'){
                       			var tempThruDate = new Date(thruDate);
                       		 	params.thruDate = tempThruDate.getTime();
                       	 	}
                       	 	exportFunction(params, "exportCustomerIncomeStmToExcel");
                        },
                        apply: function (grid, popup) {
                   			var dateTimeData = popup.group("dateTime").val();
                   			configDataSync.fromDate = dateTimeData.fromDate;
                        	configDataSync.thruDate = dateTimeData.thruDate;
                        	if(CIPC){
                        		CIPC.update();
                        	}
                   			var popupData = $.extend(dateTimeData, {partyId: popup.element("customer").val()});
                   			return $.extend({}, popupData);
                        }
            };
        //var grid = OLBIUS.oLapGrid('grid', config, configPopup, 'getCustomerIncomeStm', true);
        var grid = OlbiusUtil.grid(config);
        $('body').on("runolapservicedone", function(){
        	if(CIPC){
        		CIPC.update();
        	}
        });
        
        var exportFunction = function(parameters, url){
    		var winName = 'Export';
    		var winURL = url;
    		var form = document.createElement("form");
    		form.setAttribute("method", "post");
    		form.setAttribute("action", winURL);
    		form.setAttribute("target", "_blank");
    		for(var key in parameters){
    			if (parameters.hasOwnProperty(key) && typeof(parameters[key]) != 'undefined') {
    				var input = document.createElement('input');
    				input.type = 'hidden';
    				input.name = key;
    				input.value = parameters[key];
    				form.appendChild(input);
    			}
    		}
    		document.body.appendChild(form);
    		window.open(' ', winName);
    		form.target = winName;
    		form.submit();  
    		document.body.removeChild(form);
    	};
	}
</script>