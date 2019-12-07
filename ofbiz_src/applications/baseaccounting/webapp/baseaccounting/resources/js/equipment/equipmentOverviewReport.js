var equipmentOverviewReportObj = (function(){
	var init = function(){
		initInput();
		initDropDown();
		initWindow();
		initEvent();
	};
	var initInput = function(){
		$("#year").jqxNumberInput({width: '34%', height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0, digits: 4});
		$("#excelFormat").jqxCheckBox({width: 70, height: 25, groupName: 'format'});
		$("#pdfFormat").jqxCheckBox({width: 70, height: 25, groupName: 'format'});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#month"), globalVar.monthArr, {valueMember: 'value', displayMember: 'description', width: '60%', height: 25, dropDownWidth: 100});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#equipOverviewReportWindow"), 420, 190);
		$('#equipOverviewReportWindow').jqxWindow({ resizable: false });
	};
	var initEvent = function(){
		$("#overviewReportBtn").click(function(e){
			accutils.openJqxWindow($("#equipOverviewReportWindow"));
		});
		$("#equipOverviewReportWindow").on('open', function(e){
			$("#equipOverviewReportWindow").jqxWindow('focus');
			var date = new Date();
			$("#year").val(date.getFullYear());
			$("#month").val(date.getMonth());
			$("#pdfFormat").jqxCheckBox({checked: true});
		});
		$("#equipOverviewReportWindow").on('close', function(e){
			Grid.clearForm($(this));
		});
		$("#saveEquipOverviewReport").click(function(e) {
			var param = {};
			var month = $("#month").val();
			var year = $("#year").val();
			var url = '';
			if($("#pdfFormat").jqxCheckBox('checked')) {
				url = 'exportEquipmentOverviewReportPdf';
			} else if($("#excelFormat").jqxCheckBox('checked')) {
				url = 'exportEquipmentOverviewReportExcel';
			}
			var fromDate = new Date(year, month, 1);
			var nextMonth = parseInt(month) + 1;
			var thruDate = new Date(year, nextMonth, 0);
			param.month = month;
			param.fromDate = fromDate.getTime();
			param.thruDate = thruDate.getTime();
			param.year = year;
			exportFunction(param, url);
			$("#equipOverviewReportWindow").jqxWindow('close');
		});
		$("#cancelEquipOverviewReport").click(function(e){
			$("#equipOverviewReportWindow").jqxWindow('close');
		});
	};
	var exportFunction = function(parameters, url){
		var form = document.createElement("form");
		form.setAttribute("method", "post");
		form.setAttribute("action", url);
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
	equipmentOverviewReportObj.init();
});