var createNewFormulaOtherObject = (function(){
	var init = function(){
		initJqxInput();
		initJqxValidator();
	};
	var initJqxInput =  function(){
		$("#newFormulaCode").jqxInput({height: 19, width: '98%', theme: 'olbius'});
		$("#newFormulaName").jqxInput({height: 19, width: '98%', theme: 'olbius'});
	};
	
	var initJqxValidator = function (){
		$('#createNewFormulaForm').jqxValidator({
			rules:[
			       {input: '#newFormulaCode', message: uiLabelMapOther.CommonRequired, action: 'blur',  rule: 'required'},
			       {input: '#newFormulaName', message: uiLabelMapOther.CommonRequired, action: 'blur',  rule: 'required'},
			]
		});
	};
	
	var validate = function(){
		return $('#createNewFormulaForm').jqxValidator('validate');
	};
	
	var hideValidate = function(){
		$('#createNewFormulaForm').jqxValidator('hide');
	};
	
	var getData = function(){
		var data = new Array();
		data.push({name: "code", value: $("#newFormulaCode").val()});
		data.push({name: "name", value: $("#newFormulaName").val()});
		return data;
	};
	
	return{
		init: init,
		validate: validate,
		hideValidate: hideValidate,
		getData: getData
	}
}());

$(document).ready(function() {
	createNewFormulaOtherObject.init();
});