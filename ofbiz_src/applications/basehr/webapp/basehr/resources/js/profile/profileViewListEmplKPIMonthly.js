var viewEmplListKPIMonthlyObj = (function(){
	var now = new Date();
	var year = now.getFullYear();
	var month = now.getMonth() + 1;
	var periodTypeId = "MONTHLY";
	var init = function(){
		initJqxNumberInput();
		initBtnEvent();
		refreshGridData(month, year, month, year);
	};
	
	var initJqxNumberInput = function(){
		$('#month_from').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 2, spinMode: 'simple',  inputMode: 'simple', min: 1, max: 12, value : month});
		$('#year_from').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 4, spinMode: 'simple',  inputMode: 'simple', value : year});
		$('#month_to').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 2, spinMode: 'simple',  inputMode: 'simple', min: 1, max: 12, value : month});
		$('#year_to').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 4, spinMode: 'simple',  inputMode: 'simple', value : year});
	};
	
	var initBtnEvent = function(){
		$('#searchApproveKPI').click(function(){
			var month_from = $('#month_from').val();
			var year_from = $('#year_from').val();
			var month_to = $('#month_to').val();
			var year_to = $('#year_to').val();
			
			if(month_from == 0 || year_from == 0 || month_to == 0 || year_to == 0){
				bootbox.dialog(uiLabelMap.ChooseTimeRangeAppKPI, [{
					"label" : uiLabelMap.CommonClose,
					"class" : "btn-danger btn-small icon-remove open-sans",
				}])
			}else{
				refreshGridData(month_from,year_from,month_to,year_to);
			}
		})
	};
	
	var refreshGridData = function(month_from,year_from,month_to,year_to){
		var tmpS = $('#jqxgrid_monthly').jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=JQGetListKPIOfEmployee&month_from=" + month_from + "&year_from=" + year_from + 
							"&month_to=" + month_to + "&year_to=" + year_to + "&periodTypeId=" + periodTypeId;
		$('#jqxgrid_monthly').jqxGrid('source', tmpS);
	};
	
	return{
		init: init,
	}
}());
$(document).ready(function(){
	viewEmplListKPIMonthlyObj.init();
});