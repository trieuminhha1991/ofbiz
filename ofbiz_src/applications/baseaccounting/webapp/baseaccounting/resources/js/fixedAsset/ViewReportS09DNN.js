var reportS09DNNObj = (function(){
	var init = function(){
		initInput();
		initDropDown();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#year").jqxNumberInput({width: '33%', height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0, digits: 4});
		$("#excelFormat").jqxCheckBox({width: 70, height: 25, groupName: 'format'});
		$("#pdfFormat").jqxCheckBox({width: 70, height: 25, groupName: 'format'});
		$("#checkAllType").jqxCheckBox({width: '96%', height: 25});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#monthQuarter"), globalVar.monthQuarterArr, {valueMember: 'id', displayMember: 'description', width: '60%', height: 25, dropDownWidth: 100});
		accutils.createJqxDropDownList($("#fixedAssetType"), globalVar.fixedAssetTypeArr, {valueMember: 'fixedAssetTypeId', displayMember: 'description', width: '96%', height: 25, checkboxes: true, placeHolder: uiLabelMap.BACCPleaseChooseAcc});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#S09DNNWindow"), 420, 260);
		$('#S09DNNWindow').jqxWindow({ resizable: false });
	};
	var initEvent = function(){
		$("#reportS09DNNBtn").click(function(e){
			accutils.openJqxWindow($("#S09DNNWindow"));
		});
		$("#S09DNNWindow").on('open', function(e){
			$("#S09DNNWindow").jqxWindow('focus');
			var date = new Date();
			$("#year").val(date.getFullYear());
			$("#monthQuarter").val('month' + date.getMonth());
			$("#pdfFormat").jqxCheckBox({checked: true});
			$("#checkAllType").jqxCheckBox({checked: true});
		});
		$("#S09DNNWindow").on('close', function(e){
			Grid.clearForm($(this));
		});
		$("#checkAllType").on('change', function (event){
			var checked = event.args.checked;
			if(checked){
				$("#fixedAssetType").jqxDropDownList({disabled: true});
			}else{
				$("#fixedAssetType").jqxDropDownList({disabled: false});
			}
		});
		$("#cancelFixedAssetReportS09").click(function(e){
			$("#S09DNNWindow").jqxWindow('close');	
		});
		$("#saveFixedAssetReportS09").click(function(e){
			var valid = $("#S09DNNWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var param = {};
			var selectedIndex = $("#monthQuarter").jqxDropDownList('getSelectedIndex');
			var monthQuarterData = globalVar.monthQuarterArr[selectedIndex];
			var type = monthQuarterData.type;
			var monthFrom = 0;
			var monthTo = 11;
			var year = $("#year").val();
			var url = '';
			if($("#pdfFormat").jqxCheckBox('checked')){
				url = 'exportFixedAssetReportS09DNNPdf';
			}else if($("#excelFormat").jqxCheckBox('checked')){
				url = 'exportFixedAssetReportS09DNNExcel';
			}
			if(type == 'month'){
				monthFrom = monthQuarterData.value; 
				monthTo = monthQuarterData.value; 
			}else if(type == 'quarter'){
				var quarter = monthQuarterData.value;
				monthFrom = quarter * 3 + 1;
				monthTo = quarter * 3 + 3;
			}
			param.dateType = type;
			if(typeof(monthQuarterData.value) != 'undefined'){
				param.monthQuarterValue = monthQuarterData.value; 
			}
			var fromDate = new Date(year, monthFrom, 1);
			var thruDate = new Date(year, monthTo, 1);
			param.fromDate = fromDate.getTime();
			param.thruDate = thruDate.getTime();
			param.year = year;
			if(!$("#checkAllType").jqxCheckBox('checked')){
				var fixedAssetTypeSelectedItems = $("#fixedAssetType").jqxDropDownList('getCheckedItems');
				var fixedAssetTypeIds = [];
				fixedAssetTypeSelectedItems.forEach(function(fixedAssetType){
					fixedAssetTypeIds.push(fixedAssetType.originalItem.fixedAssetTypeId);
				});
				param.fixedAssetTypeIds = JSON.stringify(fixedAssetTypeIds);
			}
			exportFunction(param, url);
			$("#S09DNNWindow").jqxWindow('close');
		});
	};
	
	var initValidator = function(){
		$("#S09DNNWindow").jqxValidator({
			rules: [
				{ input: '#fixedAssetType', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						var selectedAll = $("#checkAllType").jqxCheckBox('checked');
						if(selectedAll){
							return true;
						}
						var items = $("#fixedAssetType").jqxDropDownList('getCheckedItems');
						if(items.length > 0){
							return true;
						}
						return false;
					}
				},
			]
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
	reportS09DNNObj.init();
});