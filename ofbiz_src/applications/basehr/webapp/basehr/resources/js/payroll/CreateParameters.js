var createParameterObject = (function(){
	var init = function(){
			initJqxWindow()
			btnEvent();
			initJqxDropDownList();
			initJqxInput();
			initJqxValidator();
	};
	
	var initJqxValidator = function(){
		$("#popupAddRow").jqxValidator({
		   	rules: [{
			    input: '#codeadd',
				message: uiLabelMap.FieldRequired + ' ' + uiLabelMap.CommonAnd + ' ' + uiLabelMap.IdNotSpace,
				action: 'blur',
				rule: function (input, commit) {
	                var s= $("#codeadd").val();
	                if (/\s/.test(s) || !s) {
					    return false;
					}
					return true;
	            }
			},{
			    input: '#nameadd',
				message: uiLabelMap.FieldRequired,
				action: 'blur',
				rule: 'required'
			},{
	            input: "#parameterTypeAdd", 
	            message: uiLabelMap.FieldRequired, 
	            action: 'blur', 
	            rule: function (input, commit) {
	                var index = $("#parameterTypeAdd").jqxDropDownList('getSelectedIndex');
	                return index != -1;
	            }
	        },
	        {
	            input: "#periodTypeIdDd", 
	            message: uiLabelMap.FieldRequired, 
	            action: 'blur', 
	            rule: function (input, commit) {
	                var index = $("#periodTypeIdDd").jqxDropDownList('getSelectedIndex');
	                return index != -1;
	            }
	        },
	        {
	            input: "#actualvalueadd", 
	            message: uiLabelMap.NumberRequired, 
	            action: 'change', 
	            rule: function (input, commit) {
	            	var val = $("#actualvalueadd").val();
	                return isNaN(val) ? false : true;
	            }
	        }]
		 });
	};

	var initJqxWindow = function(){
		$("#popupAddRow").jqxWindow({
			maxWidth: 520, minWidth: 520, width: 520, height: 350, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#Cancel")           
	    });
		$("#popupAddRow").on('close', function (event) { 
	    	Grid.clearForm($(this));
	    	$("#popupAddRow").jqxValidator('hide');
	    });
	};

	var initJqxInput = function(){
		$("#codeadd").jqxInput({width: '97%', height: '24', theme: 'olbius'});
	    $("#actualvalueadd").jqxInput({width: '97%', height: '24', theme: 'olbius'});
	    $("#nameadd").jqxInput({width: '97%', height: '24', theme: 'olbius'});
	};

	var initJqxDropDownList = function(){
		createJqxDropDownList(periodTypes, $('#periodTypeIdDd'), "periodTypeId", "description", 25, '98%');
		createJqxDropDownList(parameterTypes, $('#parameterTypeAdd'), "code", "description", 25, '98%');
		createJqxDropDownList(paramCharacteristicArr, $('#characteristicDropDown'), "paramCharacteristicId", "description", 25, '98%');
	};

	var btnEvent = function(){
		$("#cancelBtn").click(function(){
			$("#popupAddRow").jqxWindow('close');
		});
		$("#saveBtn").click(function () {
			if(!$('#popupAddRow').jqxValidator('validate')){
				return;
			}
			var i = $('#periodTypeIdDd').jqxDropDownList('getSelectedItem');
			var j = $('#parameterTypeAdd').jqxDropDownList('getSelectedItem');
			var periodTypeId = i ? i.value : "";
			var type = j ? j.value : "";
	    	var row = { 
	    		code: $("#codeadd").val(),
	    		name: $("#nameadd").val(),
	    		periodTypeId: periodTypeId,
	    		type: type,
	    		actualValue : $("#actualvalueadd").val(),
	    		paramCharacteristicId: $("#characteristicDropDown").val()
	    		/* description : $("#descriptionadd").val() */
	    	  };
	    	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        // select the first row and clear the selection.
	        $("#jqxgrid").jqxGrid('clearSelection');                        
	        $("#jqxgrid").jqxGrid('selectRow', 0);  
	        $("#popupAddRow").jqxWindow('close');
	    });
	};

	return{
		init: init
	}
}());

$(document).ready(function(){	
	createParameterObject.init();
});

