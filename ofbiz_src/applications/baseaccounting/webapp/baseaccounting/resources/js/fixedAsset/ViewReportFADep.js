var reportFADepObj = (function(){
	var init = function(){
		initInput();
		initDropDown();
		initWindow();
		initEvent();
	};
	
	var initInput = function(){
		$("#yearFADep").jqxNumberInput({width: '34%', height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0, digits: 4});
	};
	
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#monthFADep"), globalVar.monthArr, {valueMember: 'value', displayMember: 'description', width: '60%', height: 25, dropDownWidth: 100});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#faDepReportWindow"), 400, 150);
		$('#faDepReportWindow').jqxWindow({ resizable: false });
	};
	
	var initEvent = function(){
		$("#reportFADepBtn").click(function(e){
			accutils.openJqxWindow($("#faDepReportWindow"));
		});
		$("#faDepReportWindow").on('open', function(event){
			$("#faDepReportWindow").jqxWindow('focus');
			var date = new Date();
			$("#yearFADep").val(date.getFullYear());
			$("#monthFADep").val(date.getMonth());
		});
		$("#faDepReportWindow").on('close', function(event){
			$("#yearFADep").val(0);
			$("#monthFADep").jqxDropDownList('clearSelection');
		});
		
		$("#cancelFADepReport").click(function(e){
			$("#faDepReportWindow").jqxWindow('close');
		});
		$("#saveFADepReport").click(function(e){
			var param = {};
			var month = $("#monthFADep").val();
			var year = $("#yearFADep").val();
			var fromDate = new Date(year, month, 1);
			var nextMonth = parseInt(month) + 1;
			var thruDate = new Date(year, nextMonth, 0);
			param.month = month;
			param.fromDate = fromDate.getTime();
			param.thruDate = thruDate.getTime();
			param.year = year;
			exportFunction(param);
			$("#faDepReportWindow").jqxWindow('close');
		});
	};
	
	var exportFunction = function(parameters){
		var form = document.createElement("form");
		form.setAttribute("method", "post");
		form.setAttribute("action", "exportFADepPeriodsReportExcel");
		form.setAttribute("target", "_blank");
		for(var key in parameters){
			if (parameters.hasOwnProperty(key)) {
				var input = document.createElement('input');
				input.type = 'hidden';
				input.name = key;
				input.value = parameters[key];
				form.appendChild(input);
			}
		}
		document.body.appendChild(form);
		form.submit();  
	};
	
	return {
		init: init
	};
}());

$(document).ready(function(){
	$.jqx.theme = 'olbius';
	reportFADepObj.init();
});