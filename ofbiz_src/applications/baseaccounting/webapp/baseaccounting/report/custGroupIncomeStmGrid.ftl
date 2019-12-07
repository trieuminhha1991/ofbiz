<style>
	.aggregates{
		font-weight: 600;
		text-align: right;
	}
</style>
<script type="text/javascript" id="grid">
	$( document ).ready(function() {
		bindingDataToReport();
	});
	
	function bindingDataToReport(){
		var config = {
                title: ' ${uiLabelMap.BACCCustGroupIncomeStatement}',
                service: 'acctgTransTotal',
                showstatusbar: false,
                showaggregates: false,
                columns: [
                          { text: '${uiLabelMap.BACCCustomerGroupId}', datafield: 'groupId', width: 300,
                        	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
            					  for(var i = 0; i < groupData.length; i++){
        	    			  		  if(groupData[i].groupId == value){
        	    			  			  return '<span>' + groupData[i].description + ' [' +  '<span style="font-weight: bold;">' +  value  + '</span>'  + ']' + '</span>';
        	    			  		  }
            					  }
            					  return '<span>' + value + '</span>';
            				  }  
                          },
                          { text: '${uiLabelMap.BACCTransactionTime}', datafield: 'transTime', width: 150, cellsformat: 'dd/MM/yyyy'},
            			  { text: '${uiLabelMap.BACCSaleIncome}', datafield: 'saleIncome', width: 150,
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
                          { text: '${uiLabelMap.BACCSaleDiscount}', datafield: 'saleDiscount', width: 150, columngroup: 'deductions',
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
            			  { text: '${uiLabelMap.BACCPromotion}', datafield: 'promotion', width: 150, columngroup: 'deductions',
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
            			  { text: '${uiLabelMap.BACCSaleReturn}', datafield: 'saleReturn', width: 150, columngroup: 'deductions',
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
            			  { text: '${uiLabelMap.BACCNetRevenue}', datafield: 'netRevenue', width: 150,
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
            			  { text: '${uiLabelMap.BACCCOGS}', datafield: 'cogs', width: 150,
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
            			  { text: '${uiLabelMap.BACCGrossProfit}', datafield: 'grossProfit', width: 150,
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
                        ]
            };
		var dateTypeCheck = "DAY";
		var customTimePeriodYear = null;
		var dateCurrent = new Date();
		var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);		
        var configPopup = [
			{
                action : 'addDropDownList',
                params : [{
                    id : 'dateType',
                    label : '${StringUtil.wrapString(uiLabelMap.CommonPeriod)}',
                    data : date_type_source,
                    index: 0
                }],
                event : function(popup) {
                    popup.onEvent('dateType', 'select', function(event) {
                        var args = event.args;
                        var item = popup.item('dateType', args.index);
                        var filter = item.value;
                        dateTypeCheck = filter;
                        if(filter != "DAY"){
                        	popup.hide('from_date');
                        	popup.hide('thru_date');
                        	popup.show('customTimePeriodId');
                        }
                        if(filter == "DAY"){
                        	popup.show('from_date');
                        	popup.show('thru_date');
                        	popup.hide('customTimePeriodId');
                        	popup.hide('dateTypePeriodMonth');
                        	popup.hide('dateTypePeriodQuater');
                        }
                        if(filter == "YEAR"){
                        	popup.hide('dateTypePeriodMonth');
                        	popup.hide('dateTypePeriodQuater');
                        }
                        if(filter == "MONTH"){
                    		popup.show('dateTypePeriodMonth');
                    		popup.hide('dateTypePeriodQuater');
                    	}
                    	if(filter == "QUARTER"){
                    		popup.show('dateTypePeriodQuater');
                    		popup.hide('dateTypePeriodMonth');
                    	}
                    	if(filter == "WEEK"){
                    		popup.show('from_date');
                        	popup.show('thru_date');
                        	popup.hide('customTimePeriodId');
                        	popup.hide('dateTypePeriodMonth');
                        	popup.hide('dateTypePeriodQuater');
                    	}
	                    popup.resize();
                    });
                }
            },
            {
			    action : 'addDateTimeInput',
				params : [{
				    id : 'from_date',
				    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
				    value: OLBIUS.dateToString(currentFirstDay)
				}],
				before: 'thru_date'
			},
			{
                action : 'addFilterDropDownList',
                params : [{
                    id : 'groupId',
                    label : '${StringUtil.wrapString(uiLabelMap.BACCCustomerGroupId)}',
                    data : [{text:'${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}', value: undefined}].concat(listGroupDataSource),
                    index: 0,
                }]
            },
			{
                action : 'addDropDownList',
                params : [{
                    id : 'customTimePeriodId',
                    label : '${StringUtil.wrapString(uiLabelMap.Year)}',
                    data : [{text:'${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}', value: undefined}].concat(listCustomTimePeriodsYearDataSource),
                    index: 0,
                    hide: true
                }],
	            event : function(popup) {
	                popup.onEvent('customTimePeriodId', 'select', function(event) {
	                    var args = event.args;
	                    var item = popup.item('customTimePeriodId', args.index);
	                    var filter = item.value;
	                    customTimePeriodYear = filter;
	                    if(dateTypeCheck != "YEAR"){
	                    }
	                });
	            }
            },
			{
			    action : 'addDateTimeInput',
				params : [{
				    id : 'thru_date',
				    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
				    value: OLBIUS.dateToString(cur_date)
				}],
				after: 'from_date'
			},
            {
                action : 'addDropDownList',
                params : [{
                    id : 'dateTypePeriodMonth',
                    label : '${StringUtil.wrapString(uiLabelMap.KTime)}',
                    data : [{text:'${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}', value: undefined}].concat(listCustomTimePeriodsMonthDataSource),
                    index: 0,
                    hide: true
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'dateTypePeriodQuater',
                    label : '${StringUtil.wrapString(uiLabelMap.KTime)}',
                    data : [{text:'${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}', value: undefined}].concat(listCustomTimePeriodsQuaterDataSource),
                    index: 0,
                    hide: true
                }]
            },
        ];

        var grid = OLBIUS.oLapGrid('grid', config, configPopup, 'getCustGroupIncomeStm', true);
        grid.funcUpdate(function (oLap) {
        	if(dateTypeCheck == "DAY"){ 
        		var thruDate = oLap.val('thru_date');
        		var fromDate = oLap.val('from_date');
        	}
        	if(dateTypeCheck == "MONTH"){
        		var yearPeriod = oLap.val('customTimePeriodId');
        		var monthPeriod = oLap.val('dateTypePeriodMonth');
        		if(yearPeriod != null){
        			if(monthPeriod != null){
            			var thruDate = accutils.getThruDate(yearPeriod, monthPeriod, null);
            			var fromDate = accutils.getFromDate(yearPeriod, monthPeriod, null);
        			}else{
        				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectMonthByFilter)}", [{
    		                "label" : "${uiLabelMap.POCommonOK}",
    		                "class" : "btn btn-primary standard-bootbox-bt",
    		                "icon" : "fa fa-check",
    		                }]
    		            );
        				return false;
        			}
            	}else{
            		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectYearByFilter)}", [{
		                "label" : "${uiLabelMap.POCommonOK}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
    				return false;
            	}
        	}
        	if(dateTypeCheck == "QUARTER"){
        		var yearPeriod = oLap.val('customTimePeriodId');
        		var quaterPeriod = oLap.val('dateTypePeriodQuater');
        		if(yearPeriod != null){
        			if(quaterPeriod != null){
        				var thruDate = accutils.getThruDate(yearPeriod, null, quaterPeriod);
            			var fromDate = accutils.getFromDate(yearPeriod, null, quaterPeriod);
        			}else{
        				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectQuaterByFilter)}", [{
    		                "label" : "${uiLabelMap.POCommonOK}",
    		                "class" : "btn btn-primary standard-bootbox-bt",
    		                "icon" : "fa fa-check",
    		                }]
    		            );
        				return false;
        			}
        		}else{
        			bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectYearByFilter)}", [{
		                "label" : "${uiLabelMap.POCommonOK}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
    				return false;
        		}
        	}
        	if(dateTypeCheck == "WEEK"){
        		var thruDate = oLap.val('thru_date');
        		var fromDate = oLap.val('from_date');
        	}
        	if(dateTypeCheck == "YEAR"){
        		var yearPeriod = oLap.val('customTimePeriodId');
        		if(yearPeriod != null){
        			var thruDate = accutils.getThruDate(yearPeriod, null, null);
        			var fromDate = accutils.getFromDate(yearPeriod, null, null);
        		}else{
        			bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectYearByFilter)}", [{
		                "label" : "${uiLabelMap.POCommonOK}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
    				return false;
        		}
        	}
        	
        	var groupId = oLap.val('groupId');
        	oLap.update({
                'dateType': oLap.val('dateType'),
                'fromDate': fromDate,
                'thruDate': thruDate,
                'groupId': groupId,
            }, oLap.val('dateType'));
        });
        
        grid.init(function () {
            grid.runAjax();
        });
        
	}
</script>