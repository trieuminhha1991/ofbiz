$(function(){
	OlbSettingPrio.init();
});
var OlbSettingPrio = (function(){
	var initWindowPrio = (function(){
		$('#alterpopupWindow1').jqxWindow({ width: 500, height : 250,resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7, title: addNew8});
	});
	
	var initInput = (function(){
		jOlbUtil.input.create('#priorityIdAdd',{width: '98%', height: 24, minLength: 1});
		jOlbUtil.input.create('#descriptionAdd',{width: '98%', height: 24, minLength: 1});
		jOlbUtil.input.create('#enumCodeAdd',{width: '98%', height: 24, minLength: 1});
		$('#sequenceIdAdd').jqxNumberInput({width: '100%', height: 28, min: 1, spinButtons: true, decimalDigits: 0, inputMode: 'simple', digits: 2 });
	});
	
	var initValidate = function(){
		$('#CreatePriorityForm').jqxValidator({
			rules : [
				{input: '#priorityIdAdd', message: notEmpty, action: 'blur', 
					rule: function (input, commit) {
						var value = $(input).val();
						value = value.replace(/[^\w]/gi, '');
						var res = '';
						for(var x in value){
							res += value[x].toUpperCase();
						}
						var result = $('#priorityIdAdd').val(res);
						if(/^\s*$/.test(result)){
							return false;
						}
						return true;
					}
				},
				{input: '#descriptionAdd', message:notEmpty, action: 'blur', rule: 
					function (input, commit) {
						var value = $(input).val();
						if(/^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
		        {input: '#enumCodeAdd', message: notEmpty, action: 'blur', rule: 
					function (input, commit) {
						var value = $(input).val();
						if(/^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
				{input: '#sequenceIdAdd', message: validateSequence, action: 'blur', rule: 
					function (input, commit) {
						var value = $(input).val();
						if(/^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
				{input: '#sequenceIdAdd', message: validateSequence, action: 'blur', rule: 
					function (input, commit) {
						if($('#sequenceIdAdd').jqxNumberInput('getDecimal') > 0) {
							return true;
						}
						return false;
					}
				},
			]
		});
	};
	
	var eventAdd = function(){
		$('#alterSave1').click(function(){
			$('#CreatePriorityForm').jqxValidator('validate');
		});
		
		$('#CreatePriorityForm').on('validationSuccess',function(){
			var row = {};
			row = {
				enumId : $('#priorityIdAdd').val(),
				description : $('#descriptionAdd').val(),
				sequenceId : $('#sequenceIdAdd').val(),
				enumCode : $('#enumCodeAdd').val(),
			};
			$("#jqxgrid").jqxGrid('addRow', null, row, "first");
			$("#jqxgrid").jqxGrid('clearSelection');                        
			$("#jqxgrid").jqxGrid('selectRow', 0);  
			$("#alterpopupWindow1").jqxWindow('close');
		});
	};
	
	var eventClose = function(){
		$('#alterpopupWindow1').on('close',function(){
			$('#CreatePriorityForm').jqxValidator('hide');
			$('#jqxgrid').jqxGrid('refresh');
			$('#priorityIdAdd').val(null);
			$('#descriptionAdd').val(null);
			$('#enumCodeAdd').val(null);
			$('#sequenceIdAdd').val(0);
		});
	};
	
	var init = (function(){
		initWindowPrio();
		initInput();
		initValidate();
		eventAdd();
		eventClose();
	});
	
	return {
		init: init,
		olbPagePrio: olbPagePrio
	}
}());
