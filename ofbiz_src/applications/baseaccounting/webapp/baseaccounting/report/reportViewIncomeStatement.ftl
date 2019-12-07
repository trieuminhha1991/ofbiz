<#include "script/reportDataCommon.ftl">
<#include "script/reportIncomeData.ftl">

<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>

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
                title: ' ${uiLabelMap.BACCIncomeStatement}',
                service: 'acctgTransTotal',
                showstatusbar: false,
                showaggregates: false,
                filterable: true,
                showfilterrow: true,
                pagesize: 15,
                columns: [
                  { text: '${uiLabelMap.BACCProductId}', datafield: 'productCode', width: '11%', filterable: true, filtertype: 'textbox'},
                  { text: '${uiLabelMap.BACCProductName}', datafield: 'productName', width: '22%', filterable: true, filtertype: 'textbox'},
                  { text: '${uiLabelMap.BSProductCategoryType}', datafield: 'categoryId', width: '15%', filtertype: 'textbox', 
    				  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
    					  for(var i = 0; i < categoryData.length; i++){
    			  		  if(categoryData[i].categoryId == value){
    			  			  return '<span>' + categoryData[i].description + ' [' +  '<span style="font-weight: bold;">' +  value  + '</span>'  + ']' + '</span>';
    			  		  }
    			  	  }
    					  return '<span>' + value + '</span>';
    				  }
                  },
                  { text: '${uiLabelMap.BACCCustomerId}', datafield: 'partyCode', width: '11%', filtertype: 'textbox'},
                  { text: '${uiLabelMap.BACCCustomerName}', datafield: 'fullName', width: '20%', filtertype: 'textbox'},
                  { text: '${uiLabelMap.BACCTransactionTime}', datafield: 'transTime', width: '13%', cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput', 
                	  filtertype: 'range', filterable: false},
    			  { text: '${uiLabelMap.BACCSaleIncome}', datafield: 'saleIncome', width: '14%', filtertype: 'number', columntype: 'numberinput',
    				  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						  return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				  }
    			  },
                  { text: '${uiLabelMap.BACCSaleDiscount}', datafield: 'saleDiscount', width: '14%', 
    				  columngroup: 'deductions', filtertype: 'number', columntype: 'numberinput',
    				  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						  return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				  },
    			  },
    			  { text: '${uiLabelMap.BACCPromotion}', datafield: 'promotion', width: '14%', 
    				  columngroup: 'deductions', filtertype: 'number', columntype: 'numberinput',
    				  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						  return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				  }
    			  },
    			  { text: '${uiLabelMap.BACCSaleReturn}', datafield: 'saleReturn', width: '14%', 
    				  columngroup: 'deductions', filtertype: 'number', columntype: 'numberinput',
    				  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						  return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				  },
    			  },
    			  { text: '${uiLabelMap.BACCNetRevenue}', datafield: 'netRevenue', width: '17%',
    				  filtertype: 'number', columntype: 'numberinput',
    				  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						  return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				  },
    			  },
    			  { text: '${uiLabelMap.BACCCOGS}', datafield: 'cogs', width: '17%', filtertype: 'number', columntype: 'numberinput',
    				  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						  return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				  },
    			  },
    			  { text: '${uiLabelMap.BACCGrossProfit}', datafield: 'grossProfit', width: '17%', filtertype: 'number', columntype: 'numberinput',
    				  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						  return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				  },
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
                action : 'addJqxGridMultilWithFilter',
                params : [{
                    id : 'productId',
                    label : '${StringUtil.wrapString(uiLabelMap.BACCProduct)}',
                    url: 'JqxGetProducts',
                    gridWidth: 550,
		        	sortable: true,
		            pagesize: 5,
		            columnsresize: true,
		            pageable: true,
		            altrows: true,
		            showfilterrow: true,
		            filterable: true,
		            datafields: [{name: 'productId', type: 'string'}, {name: 'productCode', type: 'string'}, {name: 'productName', type: 'string'}],
		            displayField: 'productName',
		            displayAdditionField: 'productCode',
		            //gridTitle: '${StringUtil.wrapString(uiLabelMap.BSListCustomer)}',
		            clearSelectionBtnText: '${StringUtil.wrapString(uiLabelMap.BACCClearSelection)}',
		        	columns: [
			          	{ text: "${StringUtil.wrapString(uiLabelMap.BACCProductId)}", datafield: 'productCode', width: 150 }, 
			          	{ text: "${StringUtil.wrapString(uiLabelMap.BACCProductName)}", datafield: 'productName'}
			        ]
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
                action : 'addJqxGridMultilWithFilter',
                params : [{
                    id : 'partyId',
                    label : '${StringUtil.wrapString(uiLabelMap.BACCCustomer)}',
                    url : 'JqxGetCustomerList',
                    gridWidth: 550,
		        	sortable: true,
		            pagesize: 5,
		            columnsresize: true,
		            pageable: true,
		            altrows: true,
		            showfilterrow: true,
		            filterable: true,
		            datafields: [{name: 'partytId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
		            displayField: 'fullName',
		            displayAdditionField: 'partyCode',
		            //gridTitle: '${StringUtil.wrapString(uiLabelMap.BSListCustomer)}',
		            clearSelectionBtnText: '${StringUtil.wrapString(uiLabelMap.BACCClearSelection)}',
		        	columns: [
			          	{ text: "${StringUtil.wrapString(uiLabelMap.BACCCustomerId)}", datafield: 'partyCode', width: 150 }, 
			          	{ text: "${StringUtil.wrapString(uiLabelMap.BACCCustomer)}", datafield: 'fullName'}
			        ]
                }]
            }
        ];
        
        var grid = OLBIUS.oLapGrid('grid', config, configPopup, 'getIncomeStatement', true);
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
        
		var exportIncomeStatementToExcel = function(oLap){
			var params = {
					productId: oLap.val('productId'),
					categoryId: oLap.val('categoryId'),
					partyId: oLap.val('partyId'),
					dateType: oLap.val('dateType')
			}
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
	       	params.fromDate = fromDate;
	       	params.thruDate = thruDate;
	       	exportFunction(params, "exportIncomeStatementToExcel");
		}
		
		grid.init(function () {grid.runAjax();}, false, exportIncomeStatementToExcel);
	}
	
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
	
</script>