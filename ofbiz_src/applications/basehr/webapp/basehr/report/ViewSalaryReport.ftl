<#include "script/ViewSalaryReportScript.ftl" />
<script type="text/javascript" src="/hrresources/js/popup.extends.js"></script>
<script type="text/javascript" src="/poresources/js/popup_extend_grid.js"></script>
<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript" src="/hrresources/js/CommonFunction.js"></script>
<script id="salarygrid" type="text/javascript">
	var placeHolder ='';
	var filterPlaceHolder = '';
	$(function(){
		var now = new Date();
		var year = now.getFullYear();
		var listPersonFactoryJs = [];
		for(var x in listPersonFactory){
			var  personFactory = {
					text : listPersonFactory[x].fullName,
					value : listPersonFactory[x].partyId
			};
			listPersonFactoryJs.push(personFactory);
		}
		var config = {
				//FIXME CHUA TEST SERVICE
				service : 'person',
				title : '${StringUtil.wrapString(uiLabelMap.SalaryMonth)}',
				pagesize: 15,
				columns : [
			           {datafield : 'partyId', hidden : 'true'},
			           {text : '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield : 'partyName', width : '20%'},
			           {text : '${StringUtil.wrapString(uiLabelMap.SalaryBaseFlat)}', datafield : 'LUONG_CO_BAN', width : '20%',
			        	   		cellsrenderer : function(row, column, value){
			        	   			var val = value;
			        	   			if(value){
			        	   				val = formatNumber(value);
			        	   			}
			        	   			return '<span>' + val + '</span>';
			        	   		}
			           },
			           {text : '${StringUtil.wrapString(uiLabelMap.RealWages)}', datafield : 'LUONG_THUC_TE', width : '20%', columngroup : 'INCOME',
				        	    cellsrenderer : function(row, column, value){
			        	   			var val = value;
			        	   			if(value){
			        	   				val = formatNumber(value);
			        	   			}
			        	   			return '<span>' + val + '</span>';
			        	   		}
			           },
			           {text : '${StringUtil.wrapString(uiLabelMap.OvertimeWages)}', datafield : 'LUONG_THEM_GIO', width : '20%', columngroup : 'INCOME',
				        	   cellsrenderer : function(row, column, value){
			        	   			var val = value;
			        	   			if(value){
			        	   				val = formatNumber(value);
			        	   			}
			        	   			return '<span>' + val + '</span>';
			        	   		}
			           },
			           {text : '${StringUtil.wrapString(uiLabelMap.HREmplAllowances)}', datafield : 'PHU_CAP', width : '20%', columngroup : 'INCOME',
				        	   cellsrenderer : function(row, column, value){
			        	   			var val = value;
			        	   			if(value){
			        	   				val = formatNumber(value);
			        	   			}
			        	   			return '<span>' + val + '</span>';
			        	   		}
			           },
			           {text : '${StringUtil.wrapString(uiLabelMap.Bonus)}', datafield : 'THUONG', width : '20%', columngroup : 'INCOME',
				        	   cellsrenderer : function(row, column, value){
			        	   			var val = value;
			        	   			if(value){
			        	   				val = formatNumber(value);
			        	   			}
			        	   			return '<span>' + val + '</span>';
			        	   		}
			           },
			           {text : '${StringUtil.wrapString(uiLabelMap.CommonDeduction)}', datafield : 'KHAU_TRU', width : '20%',
				        	   cellsrenderer : function(row, column, value){
			        	   			var val = value;
			        	   			if(value){
			        	   				val = formatNumber(value);
			        	   			}
			        	   			return '<span>' + val + '</span>';
			        	   		}
			           },
			           {text : '${StringUtil.wrapString(uiLabelMap.ActualReceipt)}', datafield : 'LUONG_THUC_LINH', width : '20%',
				        	   cellsrenderer : function(row, column, value){
			        	   			var val = value;
			        	   			if(value){
			        	   				val = formatNumber(value);
			        	   			}
			        	   			return '<span>' + val + '</span>';
			        	   		}
			           },
	           ], 
	           columngroups : [
                       {text : '${StringUtil.wrapString(uiLabelMap.CommonIncome)}', name : 'INCOME', align: 'center'}
               ],
		};
		
		var config_popup = [
                {
                	action : 'addJqxGridMultil',
                	params : [{
                		id : 'employeeId',
                		title1 : '${StringUtil.wrapString(uiLabelMap.EmployeeId)}',
                		title2 : '${StringUtil.wrapString(uiLabelMap.EmployeeName)}',
                		label : '${StringUtil.wrapString(uiLabelMap.CommonEmployee)}',
                		data : listPersonFactoryJs,
                		value: []
                	}]
                },
                {
                	action : 'addJqxNumberInput',
                	params : [{
                		id : 'year',
                		label : '${StringUtil.wrapString(uiLabelMap.HolidayYear)}',
                		value : year,
                		digits : 4,
                		decimalDigits : 0
                	}]
                },
                {
                	action : 'addDropDownList',
                	params : [{
                		id : 'month',
                		label : '${StringUtil.wrapString(uiLabelMap.HRCommonMonth)}',
                		data : monthSort,
                		index : 0
                	}]
                }
        ];
		var testGrid = OLBIUS.oLapGrid('salarygrid', config, config_popup, 'salaryDetailReport', true, true, OLBIUS.defaultLineFunc);
		
		testGrid.funcUpdate(function(oLap){
			oLap.update({
				'month' : oLap.val('month'),
				'year': oLap.val('year'),
				'employeeId' : oLap.val('employeeId'),
			})
		});
		
		testGrid.init(function(){
			testGrid.runAjax();
		})
	})
</script>
<script id="chart" type="text/javascript">
	$(function(){
		var rootPartyArr = [];
		for(var x in globalVar.rootPartyArr){
			var tmp = {
					partyId : globalVar.rootPartyArr[x].partyId,
					partyName : globalVar.rootPartyArr[x].partyName
			};
			rootPartyArr.push(tmp);
		}
		var config = {
				chart: {
	                plotBackgroundColor: null,
	                plotBorderWidth: null,
	                plotShadow: false
	            },
				title : {
					text : '${StringUtil.wrapString(uiLabelMap.WagesDepCompareChart)}'
				},
				tooltip: {
	                pointFormat: '<span style="color:{series.color}">{point.y}</span>: <b>{point.percentage:.1f}%</b>'
	            },
	            series: [{
	                type: 'pie'
	            }],
	            plotOptions: {
	                pie: {
	                    allowPointSelect: true,
	                    cursor: 'pointer',
	                    dataLabels: {
	                        enabled: true,
	                        format: '<b>{point.name}({point.y})</b>: {point.percentage:.1f} %',
	                        style: {
	                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
	                        }
	                    }
	                }
	            }
		};
		var config_popup = [
				{
					action : 'addJqxNumberInput',
					params : [{
						id : 'year_chart',
						label : '${StringUtil.wrapString(uiLabelMap.HolidayYear)}',
						value : (new Date()).getFullYear(),
						digits : 4,
						groupSeparator: '',
						decimalDigits : 0
					}]
				},
                {
                	action : 'addJqxTree',
                	params : [{
                		id : 'organization',
                		label : '${StringUtil.wrapString(uiLabelMap.OrganizationalUnit)}',
                		source : rootPartyArr,
                		value: rootPartyArr[0]['partyId']
                	}],
                },
                {
                	action : 'addDropDownListMultil',
                	params : [{
                		id : 'month_chart',
                		label : '${StringUtil.wrapString(uiLabelMap.HRCommonMonth)}',
                		data : monthSort,
                		index : 2
                	}],
                },
        ];
		
		var testChart = OLBIUS.oLapChart('chart', config, config_popup, 'salaryCompareChart', true, true, OLBIUS.defaultPieFunc);
		
		testChart.funcUpdate(function(oLap){
			oLap.update({
				'monthChart[]' : oLap.val('month_chart'),
				'yearChart' : oLap.val('year_chart'),
				'organization' : oLap.val('organization')
			})
		});
		testChart.init(function(){
			testChart.runAjax();
		})
	});
</script>
<script id="chart2" type="text/javascript">
	$(function(){
		var now = new Date();
		year = now.getFullYear();
		var rootPartyArr = [];
		for(var x in globalVar.rootPartyArr){
			var tmp = {
					partyId : globalVar.rootPartyArr[x].partyId,
					partyName : globalVar.rootPartyArr[x].partyName
			};
			rootPartyArr.push(tmp);
		};
		
		var config = {
			chart : {
				type : 'column'
			},
			title : {
				text : '${StringUtil.wrapString(uiLabelMap.WageFluctuationsChart)}',
				x : -20
			},
			 plotOptions: {
		            series: {
		                pointWidth: 30
		            }
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
					id : 'yearFrom',
					label : '${StringUtil.wrapString(uiLabelMap.FromYear)}',
					value : year,
					digits : 4,
					decimalDigits : 0
				}]
			},
			{
				action : 'addJqxNumberInput',
				params : [{
					id : 'yearTo',
					label : '${StringUtil.wrapString(uiLabelMap.ThruYear)}',
					value : year,
					digits : 4,
					decimalDigits : 0
				}]
			},
			{
				action : 'addJqxTree',
				params : [{
					id : 'organization2',
					label : '${StringUtil.wrapString(uiLabelMap.OrganizationalUnit)}',
					source : rootPartyArr,
					value: rootPartyArr[0]['partyId']
				}]
			}
		];
		
		var testChart2 = OLBIUS.oLapChart('chart2', config, config_popup, 'salaryFluctuationChart', true, true, OLBIUS.defaultColumnFunc);
		
		testChart2.funcUpdate(function(oLap){
			oLap.update({
				'fromYear' : oLap.val('yearFrom'),
				'thruYear' : oLap.val('yearTo'),
				'organization' : oLap.val('organization2')
			})
		});
		
		testChart2.init(function(){
			testChart2.runAjax();
		});
	});
</script>
