var viewKpiRewardPunishmentObj = (function(){
	var init = function(){
		initEvent();
	};
	var initEvent = function(){
		$("#jqxgrid").on('loadCustomControlAdvance', function(){
			var nowDate = new Date();
			var startMonth = new Date(nowDate.getFullYear(), nowDate.getMonth(), 1);
			var endMonth = new Date(nowDate.getFullYear(), nowDate.getMonth() + 1, 0);
			$("#dateTimeInput").jqxDateTimeInput({width: 220, height: 25, selectionMode: 'range'});
			$("#dateTimeInput").jqxDateTimeInput('setRange', startMonth, endMonth);
			refreshGridData(startMonth, endMonth);
			$("#dateTimeInput").on('valueChanged', function(event){
				var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
				if (selection.from != null) {
					refreshGridData(selection.from, selection.to);
				}
			});
		});
	};
	var refreshGridData = function(fromDate, thruDate){
		var source = $("#jqxgrid").jqxGrid('source');
		source._source.url = "jqxGeneralServicer?sname=JQGetPerfCriteriaPolicy&fromDate=" + fromDate.getTime() + "&thruDate=" + thruDate.getTime();
		$("#jqxgrid").jqxGrid('source', source);
	};
	return{
		init: init,
	}
}());
$(document).ready(function(){
	viewKpiRewardPunishmentObj.init();
});