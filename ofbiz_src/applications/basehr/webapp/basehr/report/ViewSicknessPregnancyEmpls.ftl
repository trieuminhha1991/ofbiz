<#include "script/ViewSicknessPregnancyEmplsScript.ftl" />
<script type="text/javascript" src="/hrresources/js/CommonFunction.js"></script>
<script type="text/javascript" src="/hrresources/js/popup.extends.js"></script>
<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script id="testGrid" type="text/javascript">
	var placeHolder = "";
	var filterPlaceHolder = "";
	$(function(){
		
		function getFormattedDate(date) {
			  var year = date.getFullYear();
			  var month = (1 + date.getMonth()).toString();
			  month = month.length > 1 ? month : '0' + month;
			  var day = date.getDate().toString();
			  day = day.length > 1 ? day : '0' + day;
			  return month + '/' + day + '/' + year;
		};
		var config = {
				//FIXME CHUA TEST SERVICE
				service : 'person',
				title : '${StringUtil.wrapString(uiLabelMap.InsBenefitSicknessPregnancyEmplTitle)}',
				columns : [
			           {text : '${StringUtil.wrapString(uiLabelMap.InsuranceBenefitType)}', datafield : 'description', width : '15%'},
				       {text : '${StringUtil.wrapString(uiLabelMap.HRFullName)}', datafield : 'partyName', width : '15%'},
				       {text : '${StringUtil.wrapString(uiLabelMap.SocialInsuranceNbrIdentify)}', datafield : 'insSocialNbr', width : '15%'},
				       {text : '${StringUtil.wrapString(uiLabelMap.InsuranceSocialSalaryBenefit)}', datafield : 'insuranceSalary', width : '15%',
				    	   		cellsrenderer : function(row, column, value){
				    	   			var val = value;
			        	   			if(value){
			        	   				val = formatNumber(value);
			        	   			}
			        	   			return '<span>' + val + '</span>';
				    	   		}
				       },
				       {text : '${StringUtil.wrapString(uiLabelMap.InsuranceParticipatePeriod)}', datafield : 'insParticipatePeriod', width : '15%'},
				       {text : '${StringUtil.wrapString(uiLabelMap.HRCommonInPeriod)}', datafield : 'totalDayLeave', width : '15%', columngroup : 'DayLeave'},
				       {text : '${StringUtil.wrapString(uiLabelMap.DateAccumulatedLeaveYTD)}', datafield : 'accumulatedLeave', width : '15%', columngroup : 'DayLeave'},
				       {text : '${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}', datafield : 'allowanceAmount', width : '15%',
				    	   		cellsrenderer : function(row, column, value){
				    	   			var val = value;
			        	   			if(value){
			        	   				val = formatNumber(value);
			        	   			}
			        	   			return '<span>' + val + '</span>';
				    	   		}
				       },
				       {text : '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield : 'fromDate', width : '15%',
				    	   		cellsrenderer : function(row, column, value){
				    	   			if(value){
				    	   				var date = new Date(value);
					    	   			return '<span>' + getFormattedDate(date) + '</span>';
				    	   			}
				    	   		}
				       },
				       {text : '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield : 'thruDate', width : '15%',
					    	    cellsrenderer : function(row, column, value){
					    	    	if(value){
					    	    		var date = new Date(value);
					    	   			return '<span>' + getFormattedDate(date) + '</span>';
					    	    	}
				    	   		}
				       },
				       {text : '${StringUtil.wrapString(uiLabelMap.HRNotes)}', datafield : 'comment', width : '15%'},
	           ],
				columngroups : [
		                {text : '${StringUtil.wrapString(uiLabelMap.HRNumberDayLeave)}', name : 'DayLeave', align: 'center'},
                ],
                groupable : true
		};
		var past_date = new Date(cur_date);
		past_date.setDate(1);
		var config_popup = [
                {
                	action  : 'addDateTimeInput',
                	params : [{
                		id : 'from_date',
                		label : '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}',
                		value : OLBIUS.dateToString(past_date)
                	}],
            		before : 'thru_date'
                },
                {
                	action : 'addDateTimeInput',
                	params : [{
                		id : 'thru_date',
                		label : '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}',
                		value : OLBIUS.dateToString(cur_date)
                	}],
                	after : 'from_date'
                }
        ];
		var testGrid = OLBIUS.oLapGrid('testGrid', config, config_popup, 'insBenefitSicknessPregnancyEmpl', true, true, OLBIUS.defaultLineFunc);
		testGrid.funcUpdate(function(oLap){
			oLap.update({
				fromDate : oLap.val('from_date'),
				thruDate : oLap.val('thru_date')
			});
		});
		testGrid.init(function(){
			testGrid.runAjax();
		}, false, function(oLap){
			var dataAll = oLap.getAllData();
			if(dataAll != null){
				var fromDate = oLap.val('from_date');
				var fromDate_date = new Date(fromDate);
				var fromDate_long = fromDate_date.getTime();
				
				var thruDate = oLap.val('thru_date');
				var thruDate_date = new Date(thruDate);
				var thruDate_long = thruDate_date.getTime();
				
				window.location.href = "exportSicknessPregnancyReportToExcel?fromDate=" + fromDate_long + "&thruDate=" + thruDate_long;
			}else{
				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}", [{
        		    "label" : "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
        		    "class" : "btn-small btn-primary width60px",
        		    }]
        		   );
			}
		})
	})
</script>
<script type="text/javascript" id="chart">
	var placeHolder = "";
	var filterPlaceHolder = "";
	$(function(){
		var now = new Date();
		var year = now.getFullYear();
		var month = now.getMonth();
		var config = {
				chart : {
					type : 'column'
				},
				title : {
					text : '${StringUtil.wrapString(uiLabelMap.AllowanceFluctuationSicknessPregnancyChart)}',
					x : -20
				},
				xAxis : {
					type: 'category',
					labels : {
	                    style: {
	                        fontSize: '13px',
	                        fontFamily: 'Verdana, sans-serif'
	                    }
					},
					title : {
	                    text: null
	                }
				},
				yAxis : {
					plotLines: [{
	                    value: 0,
	                    width: 1,
	                    color: '#000000'
	                }],
	                title : {
	                    text: null
	                },
	                min: 0
				},
				legend: {
	                enabled: true
	            },
	            tooltip : {
	            	formatter : function(){
	            		return "total of chosen months in " + this.x + " : " + '<b>' + formatNumber(this.y) + '</b>';
	            	}
	            }
		};
		var config_popup = [
                {
                	action : 'addJqxNumberInput',
                	params : [{
                		id : 'year_from',
                		label : '${StringUtil.wrapString(uiLabelMap.FromYear)}',
                		value : year,
                		digits : 4,
                		decimalDigits : 0
                	}]
                },
                {
                	action : 'addJqxNumberInput',
                	params : [{
                		id : 'year_to',
                		label : '${StringUtil.wrapString(uiLabelMap.ThruYear)}',
                		value : year,
                		digits : 4,
                		decimalDigits : 0
                	}]
                },
                {
                	action : 'addDropDownListMultil',
                	params : [{
                		id : 'month',
                		label : '${StringUtil.wrapString(uiLabelMap.HRCommonMonth)}',
                		data : monthSort,
                		index : month
                	}]
                }
        ];
		var testChart = OLBIUS.oLapChart('chart', config, config_popup, 'sicknessPregnancyAllowanceFluct', true, true, OLBIUS.defaultColumnFunc);
		testChart.funcUpdate(function(oLap){
			oLap.update({
				'yearFrom' : oLap.val('year_from'),
				'yearTo' : oLap.val('year_to'),
				'month[]' : oLap.val('month'),
				'type' : 'Fluctuation'
			})
		});
		testChart.init(function(){
			testChart.runAjax();
		});
	})
</script>
<script type="text/javascript" id="chartAnalys">
	$(function(){
		var placeHolder = "";
		var filterPlaceHolder = "";
		var now = new Date();
		var year = now.getFullYear();
		var month = now.getMonth();
		var config = {
				chart : {
					type : 'column'
				},
				title : {
					text : '${StringUtil.wrapString(uiLabelMap.SicknessPregnancyAllowanceMonthlyAnalysisChart)}',
					x : -20
				},
				xAxis : {
					type: 'category',
					labels : {
	                    style: {
	                        fontSize: '13px',
	                        fontFamily: 'Verdana, sans-serif'
	                    }
					},
					title : {
	                    text: null
	                }
				},
				yAxis : {
					plotLines: [{
	                    value: 0,
	                    width: 1,
	                    color: '#000000'
	                }],
	                title : {
	                    text: null
	                },
	                min: 0
				},
				legend: {
	                enabled: true
	            },
		};
		var config_popup = [
	            {
	            	action : 'addJqxNumberInput',
	            	params : [{
	            		id : 'year_from2',
	            		label : '${StringUtil.wrapString(uiLabelMap.FromYear)}',
	            		value : year,
	            		digits : 4,
	            		decimalDigits : 0
	            	}]
	            },
	            {
	            	action : 'addJqxNumberInput',
	            	params : [{
	            		id : 'year_to2',
	            		label : '${StringUtil.wrapString(uiLabelMap.ThruYear)}',
	            		value : year,
	            		digits : 4,
	            		decimalDigits : 0
	            	}]
	            },
	            {
	            	action : 'addDropDownListMultil',
	            	params : [{
	            		id : 'month2',
	            		label : '${StringUtil.wrapString(uiLabelMap.HRCommonMonth)}',
	            		data : monthSort,
	            		index : month
	            	}]
	            },
	            {
	            	action : 'addDropDownList',
	            	params : [{
	            		id : 'option',
	            		label : '${StringUtil.wrapString(uiLabelMap.TypeOfView)}',
	            		data : typeOfView,
	            		index : 0
	            	}]
	            }
	    ];
		var testChart1 = OLBIUS.oLapChart('chartAnalys', config, config_popup, 'sicknessPregnancyAllowanceFluct', true, true, OLBIUS.defaultColumnFunc);
		testChart1.funcUpdate(function(oLap){
			oLap.update({
				'yearFrom' : oLap.val('year_from2'),
				'yearTo' : oLap.val('year_to2'),
				'month[]' : oLap.val('month2'),
				'type' : 'Analysis',
				'option' : oLap.val('option')
			})
		});
		testChart1.init(function(){
			testChart1.runAjax();
		})
	})
</script>