<#include "script/ViewWorkSituationReportScript.ftl"/>
<script id="workSituationReport" type="text/javascript">
$(function(){
var config_workingprocess = {
		title: '${StringUtil.wrapString(uiLabelMap.HREmplWorkingProcess)}',
        columns : [
                   {text : '${StringUtil.wrapString(uiLabelMap.HREffectiveDate)}'},
                   {text : '${StringUtil.wrapString(uiLabelMap.HRCommonJobTitle)}'},
                   {text : '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}'},
                   {text : '${StringUtil.wrapString(uiLabelMap.CommonLocation)}'},
                   {text : '${StringUtil.wrapString(uiLabelMap.DirectManager)}'},
       ],
	};
	var configPopup = [
                  {
                	  action : 'addDateTimeInput',
                	  params : [{
                		  id : 'from_date',
                		  label : '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}',
                		  value: OLBIUS.dateToString(past_date)
                	  }],
                	  before: 'thru_date'
                  },
                  {
                	  action : 'addDateTimeInput',
                	  params : [{
                		  id : 'thru_date',
                		  label : '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}',
                		  value : OLBIUS.dateToString(cur_date)
                	  }],
                	  before : 'from_date'
                  }
      ];
	var testGrid_workingprocess = OLBIUS.oLapGrid('workSituationReport', config_workingprocess, configPopup, '');
	
	testGrid_workingprocess.funcUpdate(function(oLap){
		oLap.update({
			'fromDate' : oLap.val('from_date'),
			'thruDate' : oLap.val('thru_date'),
			'employeeId' : oLap.val('employee'),
		})
	});
	
	testGrid_workingprocess.init(function(){
		testGrid_workingprocess.runAjax();
	})
});
</script>