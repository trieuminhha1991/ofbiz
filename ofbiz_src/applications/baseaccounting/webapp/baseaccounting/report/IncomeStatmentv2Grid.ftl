<#include "script/reportDataCommon.ftl">
<#include "script/reportIncomeData.ftl">

<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript" src="/accresources/js/report/extend.popup.js"></script>
<script type="text/javascript" id="grid">
	$( document ).ready(function() {
		bindingDataToReport();
	});
	
	
	function bindingDataToReport(){
		var config = {
                title: ' ${uiLabelMap.BACCCustGroupIncomeStatement}',
                service: 'acctgTransTotal',
                columns: [
                  { text: '${uiLabelMap.BACCProductName}', datafield: 'productId', width: 300,
					  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						  for(var i = 0; i < productData.length; i++){
						  if(productData[i].productId == value){
							  return '<span>' + productData[i].description + ' [' +  '<span style="font-weight: bold;">' +  value  + '</span>'  + ']' + '</span>';
						  }
					  }
						  return '<span>' + value + '</span>';
					  }
                  },
                  { text: '${uiLabelMap.BACCCategoryName}', datafield: 'categoryId', width: 300,
    				  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
    					  for(var i = 0; i < categoryData.length; i++){
	    			  		  if(categoryData[i].categoryId == value){
	    			  			  return '<span>' + categoryData[i].description + ' [' +  '<span style="font-weight: bold;">' +  value  + '</span>'  + ']' + '</span>';
	    			  		  }
    					  }
    					  return '<span>' + value + '</span>';
    				  }
                  },
                  { text: '${uiLabelMap.BACCCustomerGroupId}', datafield: 'groupId', width: 300,
                	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
    					  for(var i = 0; i < groupData.length; i++){
	    			  		  if(groupData[i].groupId == value){
	    			  			  return '<span>' + groupData[i].description + '[' + value  + ']' + '</span>';
	    			  		  }
    					  }
    					  return '<span>' + value + '</span>';
    				  }
                  },
                  { text: '${uiLabelMap.BACCTransactionTime}', datafield: 'transTime', width: 150, cellsformat: 'dd/MM/yyyy'
    		      },
    			  { text: '${uiLabelMap.BACCSaleIncome}', datafield: 'saleIncome', width: 150,
    				  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						  return '<span>' + formatcurrency(value) + '</span>';
    				  }  
    			  },
                  { text: '${uiLabelMap.BACCSaleDiscount}', datafield: 'saleDiscount', width: 150, columngroup: 'deductions',
    				  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						  return '<span>' + formatcurrency(value) + '</span>';
    				  }  
    			  },
    			  { text: '${uiLabelMap.BACCPromotion}', datafield: 'promotion', width: 150, columngroup: 'deductions',
    				  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						  return '<span>' + formatcurrency(value) + '</span>';
    				  }
    			  },
    			  { text: '${uiLabelMap.BACCSaleReturn}', datafield: 'saleReturn', width: 150, columngroup: 'deductions',
    				  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						  return '<span>' + formatcurrency(value) + '</span>';
    				  }  
    			  },
    			  { text: '${uiLabelMap.BACCNetRevenue}', datafield: 'netRevenue', width: 150,
    				  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						  return '<span>' + formatcurrency(value) + '</span>';
    				  }  
    			  },
    			  { text: '${uiLabelMap.BACCCOGS}', datafield: 'cogs', width: 150,
    				  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						  return '<span>' + formatcurrency(value) + '</span>';
    				  }  
    			  },
    			  { text: '${uiLabelMap.BACCGrossProfit}', datafield: 'grossProfit', width: 150,
    				  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						  return '<span>' + formatcurrency(value) + '</span>';
    				  }  
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
            {
                action : 'addFilterDropDownList',
                params : [{
                    id : 'productId',
                    label : '${StringUtil.wrapString(uiLabelMap.BACCProduct)}',
                    data : [{text:'${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}', value: undefined}].concat(listProductDataSource),
                    index: 0,
                }]
            },
            {
                action : 'addFilterDropDownList',
                params : [{
                    id : 'categoryId',
                    label : '${StringUtil.wrapString(uiLabelMap.BACCCategory)}',
                    data : [{text:'${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}', value: undefined}].concat(listCategoriesDataSource),
                    index: 0,
                }]
            },
            {
                action : 'addFilterDropDownList',
                params : [{
                    id : 'partyId',
                    label : '${StringUtil.wrapString(uiLabelMap.BACCCustomer)}',
                    data : [{text:'${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}', value: undefined}].concat(listPartyDataSource),
                    index: 0,
                }]
            },
            
        ];
        
        
        var grid = OLBIUS.oLapGrid('grid', config, configPopup, 'getLoyalIncomeStatement', true);
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
        				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.BACCSelectMonthByFilter)}", [{
    		                "label" : "${uiLabelMap.POCommonOK}",
    		                "class" : "btn btn-primary standard-bootbox-bt",
    		                "icon" : "fa fa-check",
    		                }]
    		            );
        				return false;
        			}
            	}else{
            		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.BACCSelectYearByFilter)}", [{
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
        				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.BACCSelectQuaterByFilter)}", [{
    		                "label" : "${uiLabelMap.POCommonOK}",
    		                "class" : "btn btn-primary standard-bootbox-bt",
    		                "icon" : "fa fa-check",
    		                }]
    		            );
        				return false;
        			}
        		}else{
        			bootbox.dialog("${StringUtil.wrapString(uiLabelMap.BACCSelectYearByFilter)}", [{
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
        			bootbox.dialog("${StringUtil.wrapString(uiLabelMap.BACCSelectYearByFilter)}", [{
		                "label" : "${uiLabelMap.POCommonOK}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
    				return false;
        		}
        	}
        	var productId = oLap.val('productId');
            var categoryId = oLap.val('categoryId');
            var partyId = oLap.val('partyId');
            
        	oLap.update({
                'dateType': oLap.val('dateType'),
                'fromDate': fromDate,
                'thruDate': thruDate,
                'productId': productId,
                'categoryId': categoryId,
                'partyId': partyId,
            }, oLap.val('dateType'));
        });
        
        grid.init(function () {
            grid.runAjax();
        	}, 
	        function(oLap){
	        	var dataAll = oLap.getAllData();
	        	if(dataAll.length != 0){
	        		var dateType = oLap.val('dateType');
	        		var productId = oLap.val('productId');
	                var categoryId = oLap.val('categoryId');
	                var partyId = oLap.val('partyId');
	                var fromDate;
	                var thruDate;
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
	            				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.BACCSelectMonthByFilter)}", [{
	        		                "label" : "${uiLabelMap.POCommonOK}",
	        		                "class" : "btn btn-primary standard-bootbox-bt",
	        		                "icon" : "fa fa-check",
	        		                }]
	        		            );
	            				return false;
	            			}
	                	}else{
	                		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.BACCSelectYearByFilter)}", [{
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
	            				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.BACCSelectQuaterByFilter)}", [{
	        		                "label" : "${uiLabelMap.POCommonOK}",
	        		                "class" : "btn btn-primary standard-bootbox-bt",
	        		                "icon" : "fa fa-check",
	        		                }]
	        		            );
	            				return false;
	            			}
	            		}else{
	            			bootbox.dialog("${StringUtil.wrapString(uiLabelMap.BACCSelectYearByFilter)}", [{
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
	            			bootbox.dialog("${StringUtil.wrapString(uiLabelMap.BACCSelectYearByFilter)}", [{
	    		                "label" : "${uiLabelMap.POCommonOK}",
	    		                "class" : "btn btn-primary standard-bootbox-bt",
	    		                "icon" : "fa fa-check",
	    		                }]
	    		            );
	        				return false;
	            		}
	            	}
	            	thruDate = new Date(thruDate);
	            	fromDate = new Date(fromDate);
	            	thruDate = thruDate.getTime();
	            	fromDate = fromDate.getTime();
	            	href = "exportIncomeStatementToExcel?dateType=" + dateType + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
	            	if(productId){
	            		href += "&productId=" + productId;
	            	}
	            	if(categoryId){
	            		href += "&categoryId=" + categoryId;
	            	}
	            	if(partyId){
	            		href += "&partyId=" + partyId;
	            	}
	            	window.location.href = href;
	        	}else{
	        		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}", [{
	        		    "label" : "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
	        		    "class" : "btn-small btn-primary width60px",
	        		    }]
	        		   );
	        	}
	        });
	}
</script>