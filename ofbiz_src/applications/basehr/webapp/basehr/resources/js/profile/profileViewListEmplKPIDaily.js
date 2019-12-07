var viewEmplListKPIObj = (function(){
	var now = new Date();
	var first_day_month = new Date(now.getFullYear(), now.getMonth(), 1);
	var last_day_month = new Date(now.getFullYear(), now.getMonth() + 1, 0);
	
	var first_day_month_time = first_day_month.getTime();
	var last_day_month_time = last_day_month.getTime();
	var periodTypeId = "DAILY";
	
	var init = function(){
		initJqxDateTimeInput();
		initJqxDateTimeInputEvent();
		refreshGridData(first_day_month_time,last_day_month_time);
	};
	
	var initJqxDateTimeInputEvent = function(){
		$("#dateTimeInput_daily").on('change', function(event){
			var fromDate = event.args.date.from;
			var thruDate = event.args.date.to;
			
			var fromDate_time = fromDate.getTime();
			var thruDate_time = thruDate.getTime();
			
			refreshGridData(fromDate_time, thruDate_time);
		});
	};
	
	var initJqxDateTimeInput = function(){
		$("#dateTimeInput_daily").jqxDateTimeInput({ width: 250, height: 25, theme: 'olbius', selectionMode: 'range'});
		$("#dateTimeInput_daily").jqxDateTimeInput('setRange', first_day_month, last_day_month);
	};
	
	var refreshGridData = function(fromDate, thruDate){
		if(fromDate && thruDate){
			refreshBeforeReloadGrid($("#jqxgrid_daily"));
			var tmpS = $("#jqxgrid_daily").jqxGrid('source');
			tmpS._source.url = "jqxGeneralServicer?sname=JQGetListKPIOfEmployee&fromDate=" + fromDate + "&thruDate=" + thruDate + "&periodTypeId=" + periodTypeId;
			$("#jqxgrid_daily").jqxGrid('source', tmpS);
		}
	};
	
	return{
		init: init,
	}
}());
$(document).ready(function(){
	viewEmplListKPIObj.init();
});