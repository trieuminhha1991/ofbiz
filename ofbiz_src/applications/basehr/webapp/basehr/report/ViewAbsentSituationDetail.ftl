<#include "script/ViewAbsentSituationDetailScript.ftl"/>
<script type="text/javascript" src="/poresources/js/popup_extend_grid.js"></script>
<script type="text/javascript" id="test">
	$(function(){
		var past_date = new Date(cur_date);
		past_date.setDate(1);
		
		var listPersonFactoryJs = [];
		for(var x in listPersonFactory){
			var  personFactory = {
					text : listPersonFactory[x].fullName,
					value : listPersonFactory[x].partyId
			};
			listPersonFactoryJs.push(personFactory);
		}
		var config = {
				  //FIXME CHUA TEST Service
	              service : 'person',
				  title: '${StringUtil.wrapString(uiLabelMap.AbsentSituationDetailReport)}',
	              columns : [
                        {datafield : 'partyId', type : 'string', hidden : 'true'},
						{text : '${StringUtil.wrapString(uiLabelMap.HRSequenceNbr)}',width : '5%', datafield : 'STT',columntype: 'number',
								cellsrenderer : function(row, column, value){
									return '<div style=margin:4px;>' + (value + 1) + '</div>';
								}
						},
						{text : '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield : 'partyName', type : 'string', width : '16%'},
						{text : '${StringUtil.wrapString(uiLabelMap.HRCommonFromDate)}', datafield : 'fromDate', width : '16%', cellsformat : 'dd/MM/yyyy',
								cellsrenderer : function(row, column, value){
									var date = new Date(value.time);
									function pad(s) { return (s < 10) ? '0' + s : s; }
									return '<div style=margin:4px;>' + [pad(date.getDate()), pad(date.getMonth() + 1), pad(date.getFullYear())].join('/') + '</div>';
								}
						},
						{text : '${StringUtil.wrapString(uiLabelMap.HRCommonThruDate)}', datafield : 'thruDate', width : '16%', cellsformat : 'dd/MM/yyyy',
								cellsrenderer : function(row, column, value){
									var date = new Date(value.time);
									function pad(s) { return (s < 10) ? '0' + s : s; }
									return '<div style=margin:4px;>' + [pad(date.getDate()), pad(date.getMonth() + 1), pad(date.getFullYear())].join('/') + '</div>';
								}
						},
						<#list emplLeaveReasonType as elrt>
							{text : '${elrt.description?if_exists}', datafield : '${elrt.emplLeaveReasonTypeId?if_exists}',type : 'number', width : '10%', columngroup : 'emplLeaveReasonType'},
						</#list>
                 ],
                 columngroups : [
                     	{text : '${StringUtil.wrapString(uiLabelMap.EmplLeaveReasonType)}', align: 'center', name: 'emplLeaveReasonType'},       
                 ]
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
                	}],
                },
                {
                	action : 'addDateTimeInput',
                	params : [{
                		id : 'fromDate',
                		label : '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}',
                		value : OLBIUS.dateToString(past_date)
                	}],
                	before : 'thruDate'
                },
                {
                    action : 'addDateTimeInput',
                    params : [{
                        id : 'thruDate',
                        label : '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}',
                        value: OLBIUS.dateToString(cur_date)
                    }],
                    after: 'fromDate'
                },
        ];
        
		var testGrid = OLBIUS.oLapGrid('test', config, config_popup, 'absentEmplDetail', true, true, OLBIUS.defaultLineFunc);
		
		testGrid.funcUpdate(function(oLap){
			oLap.update({
				'fromDate' : oLap.val('fromDate'),
				'thruDate': oLap.val('thruDate'),
				'employeeId' : oLap.val('employeeId'),
			})
		});
		
		testGrid.init(function() {
			testGrid.runAjax();
		}, false, function(oLap){
			var dataAll = oLap.getAllData();
			if(dataAll.length != 0){
				var fromDate_input = oLap.val("fromDate");
				var thruDate_input = oLap.val("thruDate");
				var fromDate = new Date(fromDate_input);
				var fromDate_long = fromDate.getTime();
				var thruDate = new Date(thruDate_input);
				var thruDate_long = thruDate.getTime();
				var employeeId = oLap.val('employeeId');
				
				window.location.href = "exportAbsentDetailReportToExcel?fromDate=" + fromDate_long + "&thruDate=" + thruDate_long + "&employeeId=" + employeeId;
			}else{
				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}", [{
        		    "label" : "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
        		    "class" : "btn-small btn-primary width60px",
        		    }]
        		   );
			}
		});
	});
</script>
<script type="text/javascript" id="absent_time_colchart">
	$(function(){
		var past_date = new Date(cur_date);
		past_date.setDate(1);
		var config = {
			chart : {
				type : 'column'
			},
			title : {
				text : '${StringUtil.wrapString(uiLabelMap.AbsentTimeChart)}',
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
		    	  action : 'addDateTimeInput',
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
		      },
        ];
		
		var absent_time_report = OLBIUS.oLapChart('absent_time_colchart', config, config_popup, 'absentTimeChart', true, true, OLBIUS.defaultColumnFunc);
		
		absent_time_report.funcUpdate(function (oLap) {
            oLap.update({
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
            });
        });
		
		absent_time_report.init(function(){
			absent_time_report.runAjax();
		})
	});
</script>
<script type="text/javascript" id="absent_time_pie">
	$(function(){
		var past_date = new Date(cur_date);
		past_date.setDate(1);
		var config = {
			chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
            	text : '${StringUtil.wrapString(uiLabelMap.AbsentReasonAnalyzeChart)}',
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
		    	  action : 'addDateTimeInput',
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
		      },
		];
		var absent_time_pie = OLBIUS.oLapChart('absent_time_pie', config, config_popup, 'absentTimePieChart', true, true, OLBIUS.defaultPieFunc);
		
		absent_time_pie.funcUpdate(function(oLap){
			oLap.update({
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date')
            });
		});
		
		absent_time_pie.init(function(){
			absent_time_pie.runAjax();
		})
	});
</script>