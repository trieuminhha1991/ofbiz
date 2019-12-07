<#include "script/ViewInsuranceRecordsReportScript.ftl"/>
<#include "script/overideConfigPara.ftl" />
<script type="text/javascript" src="/hrresources/js/popup.extends.js"></script>
<script type="text/javascript" src="/poresources/js/popup.extend.js"></script>
<script type="text/javascript" src="/poresources/js/popup_extend_grid.js"></script>
<script type="text/javascript" src="/hrresources/js/CommonFunction.js"></script>
<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<style type="text/css">
.aquaCell{
	background-color: aqua !important;
}
.bisqueCell{
	background-color: bisque !important;
}
.azureCell{
	background-color: azure !important;
}
.yellowCell{
	background-color: yellow !important;
}
</style>
<script type="text/javascript" id="grid">
	$(function(){
		var config = {
				service : 'payrollJob',
				title : '${StringUtil.wrapString(uiLabelMap.PayrollSummaryReport)}',
				filterable: true,
	            showfilterrow: true,
				columns : [
			           {text : '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield : 'partyCode', width : '12%', filterable: true},
			           {text : '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield : 'fullName', width : '15%', filterable: true},
			           {text : '${StringUtil.wrapString(uiLabelMap.DepartmentName)}', datafield : 'departmentName', width : '18%', filterable: true},
			           {text : '${StringUtil.wrapString(uiLabelMap.FromDate)}', datafield: 'fromDate', width : '13%', filterable: false,
			        	   cellsrenderer: function (row, column, value) {
		                    	if (value) {
		                			value = new Date(value).toTimeOlbius();
								}
						        return "<div class=\"text-right\">" + value + "</div>";
						    }
			           },
			           {text : '${StringUtil.wrapString(uiLabelMap.ThruDate)}', datafield: 'thruDate', width : '13%', 
			        	   columntype: 'datetimeinput', filterable: false,
			        	   cellsrenderer: function (row, column, value) {
		                    	if (value) {
		                			value = new Date(value).toTimeOlbius();
								}
						        return "<div class=\"text-right\">" + value + "</div>";
						    }
			           },
			           <#if payrollItemTypeList?has_content>
			           	   <#list payrollItemTypeList as payrollItemType>
			           	   {text: '${StringUtil.wrapString(payrollItemType.description)}', datafield: '${payrollItemType.payrollItemTypeId}_item', width: '13%',
			           			columntype: 'numberinput', filtertype: 'number', columngroup : 'INCOME', filterable: false,
			           			cellsrenderer: function(row, column, value){
									if(typeof(value) == 'number'){
										return '<span style=\"text-align: right\">' + formatcurrency(value) + '<span>';
									}
								}
			           	   },
			           	   </#list>
			           </#if>
			           {text: '${StringUtil.wrapString(uiLabelMap.SumSalary)}', datafield: 'totalIncome', width: '13%',
		           			columntype: 'numberinput', filtertype: 'number', columngroup : 'INCOME', filterable: false,
		           			cellsrenderer: function(row, column, value){
								if(typeof(value) == 'number'){
									return '<span style=\"text-align: right\">' + formatcurrency(value) + '<span>';
								}
							},
							cellclassname: function (row, column, value, data) {
							    return 'bisqueCell';
							}
							
		           	   },
			           {text: '${StringUtil.wrapString(uiLabelMap.TotalDeduction)}', datafield: 'totalDeduction', width: '13%',
		           			columntype: 'numberinput', filtertype: 'number', filterable: false,
		           			cellsrenderer: function(row, column, value){
								if(typeof(value) == 'number'){
									return '<span style=\"text-align: right\">' + formatcurrency(value) + '<span>';
								}
							},
							cellclassname: function (row, column, value, data) {
							    return 'aquaCell';
							}
		           	   },
			           {text: '${StringUtil.wrapString(uiLabelMap.RealSalaryPaid)}', datafield: 'actualReceive', width: '13%',
		           			columntype: 'numberinput', filtertype: 'number', filterable: false,
		           			cellsrenderer: function(row, column, value){
								if(typeof(value) == 'number'){
									return '<span style=\"text-align: right\">' + formatcurrency(value) + '<span>';
								}
							},
							cellclassname: function (row, column, value, data) {
							    return 'yellowCell';
							}
		           	   }
		       ],
		       columngroups : [
                       {text : '${StringUtil.wrapString(uiLabelMap.CommonIncome)}', name : 'INCOME', align: 'center'}
               ],
		};
		var config_popup = [
			{
                action : 'addDateTimeInput',
                params : [{
                    id : 'from_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                    value: OLBIUS.dateToString(past_date),
                 	disabled: true,
                }],
                before: 'thru_date'
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'thru_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date),
                    disabled: true,
                }],
                after: 'from_date'
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'from_date_1',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                    value: OLBIUS.dateToString(past_date),
                    hide: true,
                }],
                before: 'thru_date_1'
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'thru_date_1',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date),
                    hide: true,
                }],
                after: 'from_date_1'
            },
            {
		        action : 'addDropDownList',
		        params : [{
		            id : 'customTime',
		            label : '${StringUtil.wrapString(uiLabelMap.TypeTimeLabel)}',
		            data : customDate,
		            index: 2,
		        }],
                event : function(popup) {
                    popup.onEvent('customTime', 'select', function(event) {
                        var args = event.args;
                        var item = popup.item('customTime', args.index);
                        var filter = item.value;
                        popup.clear('from_date');
                        popup.clear('thru_date');
                        if(filter == 'oo') {
                            popup.show('from_date_1');
                            popup.show('thru_date_1');
                            popup.hide('from_date');
                            popup.hide('thru_date');
                        } else {
                        	popup.show('from_date');
                            popup.show('thru_date');
                        	popup.hide('from_date_1');
                            popup.hide('thru_date_1');
                        }
                        popup.resize();
                    });
                }
		    },
        ];
		var payrollGrid = OLBIUS.oLapGrid('grid', config, config_popup, 'getPayrollTableReport', true, true, OLBIUS.defaultLineFunc);
		payrollGrid.funcUpdate(function(oLap){
			gFromDate = oLap.val('from_date_1'),
            gThruDate = oLap.val('thru_date_1'),
			oLap.update({
				'fromDate' : gFromDate,
				'thruDate' : gThruDate,
				'customTime' : oLap.val('customTime'),
			});
		});
		payrollGrid.init(function(){
			payrollGrid.runAjax();
		})
	});
</script>