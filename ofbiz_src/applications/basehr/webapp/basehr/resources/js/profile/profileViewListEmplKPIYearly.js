var viewEmplListKPIYearlyObj = (function(){
	var now = new Date();
	var _year = now.getFullYear();
	var periodTypeId = "YEARLY";
	var init = function(){
		initJqxNumberInput();
		initBtnEvent();
		refreshGridData(_year);
	};
	
	var initJqxNumberInput = function(){
		$('#year_yearly').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 4, spinMode: 'simple',  inputMode: 'simple', value : _year});
	};
	
	var initBtnEvent = function(){
		$('#year_yearly').on('valueChanged', function(event){
			var value = event.args.value;
			refreshGridData(value);
		});
	};
	
	var refreshGridData = function(year){
		var tmpS = $('#jqxgrid_yearly').jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=JQGetListKPIOfEmployee&year=" + year + "&periodTypeId=" + periodTypeId;
		$('#jqxgrid_yearly').jqxGrid('source', tmpS);
	};
	
	return{
		init: init,
	}
}());
$(document).ready(function(){
	viewEmplListKPIYearlyObj.init();
});