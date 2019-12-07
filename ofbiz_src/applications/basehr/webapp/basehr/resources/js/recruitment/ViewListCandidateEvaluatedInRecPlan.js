var candidateEvalutedRecPlanObj = (function(){
	var init = function(){
		initGrid();
		initEvent();
		initContextMenu();
	};
	var initGrid = function(){
		var source = $("#jqxgrid").jqxGrid('source');
		var date = new Date();
		source._source.url = "jqxGeneralServicer?sname=JQGetListRecruitmentPlanEngagedBoard&year=" + date.getFullYear();
		$("#jqxgrid").jqxGrid('source', source);
	};
	var initEvent = function(){
		$("#jqxgrid").on('loadCustomControlAdvance', function(){
			var date = new Date();
			$("#yearCustomTimePeriod").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0, decimal: date.getFullYear()});
			$("#yearCustomTimePeriod").on('valueChanged', function(event) {
				var year = event.args.value;
				refreshGridData(year);
			});
		});
	};
	var initContextMenu = function(){
		createJqxMenu("contextMenu", 30, 260);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == "viewListCandidates"){
            	evaluatedCandidateObj.openWindow();//evaluatedCandidateObj is defined in RecruitEvaluatedCandidates.js
            	evaluatedCandidateObj.setData(dataRecord);
            }
		});
	};
	var refreshGridData = function(year){
		var source = $("#jqxgrid").jqxGrid('source');
		source._source.url = "jqxGeneralServicer?sname=JQGetListRecruitmentPlanEngagedBoard&year=" + year;
		$("#jqxgrid").jqxGrid('source', source);
	};
	return{
		init: init
	}
}());

$(document).ready(function(){
	candidateEvalutedRecPlanObj.init();
});