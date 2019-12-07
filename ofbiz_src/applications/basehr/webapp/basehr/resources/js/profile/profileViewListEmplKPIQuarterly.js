var viewEmplListKPIQuarterlyObj = (function(){
	var now = new Date();
	var year = now.getFullYear();
	var quarter = parseInt(now.getMonth()/3) + 1;
	var periodTypeId = "QUARTERLY";
	var init = function(){
		initJqxNumberInput();
		initBtnEvent();
		refreshGridData(quarter, year, quarter, year);
	};
	
	var initJqxNumberInput = function(){
		$('#quarter_from').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 2, spinMode: 'simple',  inputMode: 'simple', min: 1, max: 4, value : quarter});
		$('#year_from_quarter').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 4, spinMode: 'simple',  inputMode: 'simple', value : year});
		$('#quarter_to').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 2, spinMode: 'simple',  inputMode: 'simple', min: 1, max: 4, value : quarter});
		$('#year_to_quarter').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 4, spinMode: 'simple',  inputMode: 'simple', value : year});
	};
	
	var initBtnEvent = function(){
		$('#searchApproveKPI_quarter').click(function(){
			var quarter_from = $('#quarter_from').val();
			var year_from_quarter = $('#year_from_quarter').val();
			var quarter_to = $('#quarter_to').val();
			var year_to_quarter = $('#year_to_quarter').val();
			
			if(quarter_from == 0 || year_from_quarter == 0 || quarter_to == 0 || year_to_quarter == 0){
				bootbox.dialog(uiLabelMap.ChooseTimeRangeAppKPI, [{
					"label" : uiLabelMap.CommonClose,
					"class" : "btn-danger btn-small icon-remove open-sans",
				}])
			}else{
				refreshGridData(quarter_from, year_from_quarter, quarter_to, year_to_quarter);
			}
		})
	};
	
	var refreshGridData = function(quarter_from, year_from_quarter, quarter_to, year_to_quarter){
		var tmpS = $('#jqxgrid_quarterly').jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=JQGetListKPIOfEmployee&quarter_from=" + quarter_from + "&year_from_quarter=" + year_from_quarter + 
							"&quarter_to=" + quarter_to + "&year_to_quarter=" + year_to_quarter + "&periodTypeId=" + periodTypeId;
		$('#jqxgrid_quarterly').jqxGrid('source', tmpS);
	};
	
	
	return{
		init: init,
	}
}());

function getMonday(d) {
  d = new Date(d);
  var day = d.getDay(),
      diff = d.getDate() - day + (day == 0 ? -6:1); // adjust when day is sunday
  return new Date(d.setDate(diff));
}
$(document).ready(function(){
	viewEmplListKPIQuarterlyObj.init();
});