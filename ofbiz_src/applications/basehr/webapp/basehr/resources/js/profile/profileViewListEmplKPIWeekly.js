var viewEmplListKPIWeeklyObj = (function(){
	var curr = new Date();
	var first = curr.getDate() - curr.getDay() + 1; // First day is the day of the month - the day of the week
	var last = first + 6; // last day is the first day + 6

	var firstday = new Date(curr.setDate(first));
	var lastday = new Date(curr.setDate(last));
	
	var firstday_time = firstday.getTime();
	var lastday_time = lastday.getTime();
	var periodTypeId = "WEEKLY";
	
	var init = function(){
		initJqxDateTimeInput();
		initJqxDateTimeInputEvent();
		refreshGridData(firstday_time, lastday_time);
	};
	
	var initJqxDateTimeInputEvent = function(){
		$("#dateTimeInput_Weekly").on('change', function(event){
			var fromDate = event.args.date.from;
			var thruDate = event.args.date.to;
			
			var fromDate_time = fromDate.getTime();
			var thruDate_time = thruDate.getTime();
		    refreshGridData(fromDate_time, thruDate_time);
		});
	};
	
	var initJqxDateTimeInput = function(){
		$("#dateTimeInput_Weekly").jqxDateTimeInput({ width: 250, height: 25, theme: 'olbius', showWeekNumbers : true, selectionMode: 'range'});
		$("#dateTimeInput_Weekly").jqxDateTimeInput('setRange', firstday, lastday);
	};
	var refreshGridData = function(fromDate, thruDate){
		if(fromDate && thruDate){
			refreshBeforeReloadGrid($("#jqxgrid_weekly"));
			var tmpS = $("#jqxgrid_weekly").jqxGrid('source');
			tmpS._source.url = "jqxGeneralServicer?sname=JQGetListKPIOfEmployee&fromDate=" + fromDate + "&thruDate=" + thruDate + "&periodTypeId=" + periodTypeId;
			$("#jqxgrid_weekly").jqxGrid('source', tmpS);
		}
	};
	
	return{
		init: init,
	}
}());
$(document).ready(function(){
	viewEmplListKPIWeeklyObj.init();
});