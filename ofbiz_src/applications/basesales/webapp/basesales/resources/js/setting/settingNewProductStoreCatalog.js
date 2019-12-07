$(function(){
	OlbSettingProductStoreNewCatalog.init();
});

var OlbSettingProductStoreNewCatalog = (function(){
	var init = (function(){
		initWindow();
		initInput();
		initDropDownList();
		eventValidate();
		eventAdd();
		eventClose();
	});
	
	var initWindow = (function(){
		$('#alterpopupWindow').jqxWindow({ width: 500, height : 250,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7, title: addNew3});
		$('#alterpopupWindow').jqxWindow('resizable', false);
	});
	
	var initInput = (function(){
		jOlbUtil.dateTimeInput.create("#fromDateAdd", {width: '100%', showFooter: true, height:28});
		jOlbUtil.dateTimeInput.create("#thruDateAdd", {width: '100%', showFooter: true, height:28, allowNullDate: true});
		$('#thruDateAdd').val(null);
		$("#sequenceNumAdd").jqxNumberInput({width: '100%', height: 28, spinButtons: false, digits: 3, inputMode: 'simple', decimalDigits: 0 });
	});
	
	var initDropDownList = (function(){
		$("#prodCatalogIdAdd").jqxDropDownList({ source: catalogList, width: '100%', height: '28px', displayMember: "catalogName", valueMember: "prodCatalogId", dropDownHeight: 200, autoDropDownHeight: true, placeHolder: choose,});
	});
	
	var eventValidate = (function(){
		$('#ProStoCatForm').jqxValidator({
		   	rules : [
				{input: '#prodCatalogIdAdd', message: validateProductCatalog, action: 'blur', 
					rule: function (input, commit) {
						var value = $(input).val();
						value = value.replace(/[^\w]/gi, '');
						var res = '';
						for(var x in value){
							res += value[x].toUpperCase();
						}
						var result = $('#prodCatalogIdAdd').val(res);
						if(/^\s*$/.test(result)){
							return false;
						}
						return true;
					}
				},
				{input: '#fromDateAdd', message: validateFromDate1, action: 'blur', rule: 
					function (input, commit) {
						if($('#fromDateAdd').jqxDateTimeInput('getDate') == null || $('#fromDateAdd').jqxDateTimeInput('getDate') == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#fromDateAdd', message: validateFromDate2, action: 'blur', rule: 
					function (input, commit) {
						if($('#fromDateAdd').jqxDateTimeInput('getDate') < nowTimee) {
							return false;
						}
						return true;
					}
				},
				{input: '#fromDateAdd', message: validateFromDate2, action: 'blur', rule: 
					function (input, commit) {
						var thruDateA = $('#thruDateAdd').jqxDateTimeInput('val', 'date');
						if(thruDateA){
							if($('#fromDateAdd').jqxDateTimeInput('val', 'date').getTime() > $('#thruDateAdd').jqxDateTimeInput('val', 'date').getTime()) {
								return false;
							}
						}
						return true;
					}
				},
				{input: '#sequenceNumAdd', message: validateSquenceNum, action: 'blur', rule: 
					function (input, commit) {
						if($('#sequenceNumAdd').jqxNumberInput('getDecimal') <= 0) {
							return false;
						}
						return true;
					}
				},
			]
		});
	});
	
	var eventAdd = (function(){
		$('#alterSave1').click(function(){
			$('#ProStoCatForm').jqxValidator('validate');
		});
		
		$('#ProStoCatForm').on('validationSuccess',function(){
			var row = {};
			row = {
					prodCatalogId : $('#prodCatalogIdAdd').val(),
					productStoreId : productStoreId,
					fromDate : $('#fromDateAdd').jqxDateTimeInput('val', 'date'),
					thruDate : $('#thruDateAdd').jqxDateTimeInput('val', 'date'),
					sequenceNum : $('#sequenceNumAdd').val()
			};
			
			$("#jqxgrid").jqxGrid('addRow', null, row, "first");
			$("#jqxgrid").jqxGrid('clearSelection');                        
			$("#jqxgrid").jqxGrid('selectRow', 0);  
			$("#alterpopupWindow").jqxWindow('close');
		});
	});	
	
	var eventClose = (function(){
		$('#alterpopupWindow').on('close',function(){
			$('#ProStoCatForm').jqxValidator('hide');
			$('#jqxgrid').jqxGrid('refresh');
			$('#prodCatalogIdAdd').jqxDropDownList('clearSelection');
			$('#thruDateAdd').val(null);
			$('#sequenceNumAdd').val(0);
		});
	});
	
	return {
		init: init,
	}
}());




