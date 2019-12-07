var equipmentAllocationsReportObj = (function(){
	var init = function(){
		initInput();
		initDropDown();
		initWindow();
		initEvent();
	};
	var initInput = function(){
		$("#yearAllocations").jqxNumberInput({width: '34%', height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0, digits: 4});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#monthAllocations"), globalVar.monthArr, {valueMember: 'value', displayMember: 'description', width: '60%', height: 25, dropDownWidth: 100});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#equipAllocationsReportWindow"), 400, 150);
		$('#equipAllocationsReportWindow').jqxWindow({ resizable: false });
	};
	var initEvent = function(){
		$("#equipAllocationsReportBtn").click(function(e){
			accutils.openJqxWindow($("#equipAllocationsReportWindow"));
		});
		$("#equipAllocationsReportWindow").on('open', function(e){
			$("#equipAllocationsReportWindow").jqxWindow('focus');
			var date = new Date();
			$("#yearAllocations").val(date.getFullYear());
			$("#monthAllocations").val(date.getMonth());
		});
		$("#equipAllocationsReportWindow").on('close', function(e){
			Grid.clearForm($(this));
		});
		$("#saveEquipAllocationsReport").click(function(e){
			var param = {};
			var month = $("#monthAllocations").val();
			var year = $("#yearAllocations").val();
			var fromDate = new Date(year, month, 1);
			var nextMonth = parseInt(month) + 1;
			var thruDate = new Date(year, nextMonth, 0);
			param.month = month;
			param.fromDate = fromDate.getTime();
			param.thruDate = thruDate.getTime();
			param.year = year;
			exportFunction(param);
			$("#equipAllocationsReportWindow").jqxWindow('close');
		});
		$("#cancelEquipAllocationsReport").click(function(e){
			$("#equipAllocationsReportWindow").jqxWindow('close');
		});
	};
	var exportFunction = function(parameters){
		var form = document.createElement("form");
		form.setAttribute("method", "post");
		form.setAttribute("action", "exportEquipmentAllocationsReportExcel");
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
	
	return{
		init: init
	}
}());

$(document).ready(function(){
	$.jqx.theme = 'olbius';
	equipmentAllocationsReportObj.init();
});