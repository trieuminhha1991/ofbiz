var cursorPosition = 0;
jQuery.fn.extend({
	setSelection: function(selectionStart, selectionEnd) {
		    if(this.length == 0) return this;
		    input = this[0];

		    if (input.createTextRange) {
		        var range = input.createTextRange();
		        range.collapse(true);
		        range.moveEnd('character', selectionEnd);
		        range.moveStart('character', selectionStart);
		        range.select();
		    } else if (input.setSelectionRange) {
		        input.focus();
		        input.setSelectionRange(selectionStart, selectionEnd);
		    }

		    return this;
		}
})
jQuery.fn.extend({
	setCursorPosition: function(position){
	    if(this.length == 0) return this;
	    return $(this).setSelection(position, position);
	}
})
jQuery.fn.extend({
	focusEnd: function(){
		this.setCursorPosition(this.val().length);
		return this;
	}
})
jQuery.fn.extend({
	getCursorPosition: function() {
	        var el = $(this).get(0);
	        var pos = 0;
	        if('selectionStart' in el) {
	            pos = el.selectionStart;
	        } else if('selection' in document) {
	            el.focus();
	            var Sel = document.selection.createRange();
	            var SelLength = document.selection.createRange().text.length;
	            Sel.moveStart('character', -el.value.length);
	            pos = Sel.text.length - SelLength;
	        }
	        return pos;
	    }
})
function validateFormular(formular) {
	if(formular.indexOf('-') != -1){
		formular = formular.replace('-', '+');
	}
	if (formular.indexOf('(') == -1) {
		return false;
	}
	if (formular.indexOf(')') == -1) {
		return false;
	}
	if ((formular.indexOf('+') == -1)) {
		var code = formular.split('(');
		code = code[1];
		code = code.replace(')', '');
		if (isNaN(code)) {
			return false;
		}
	} else {
		var operators = formular.split('+');
		for (var i = 0; i < operators.length; i++) {
			var operator = operators[i];
			operator = operator.trim();
			if (operator.indexOf('(') == -1) {
				return false;
			}
			if (operator.indexOf(')') == -1) {
				return false;
			}
			var code = operator.split('(');
			code = code[1];
			code = code.replace(')', '');
			if (isNaN(code)) {
				return false;
			}
		}
	}
	return true;
}
function acceptFormular() {
	var offset = $("#jqxFunctionGrid").offset();

	var formular = $('#editFormular').val();
	
	if (validateFormular(formular)) {
		$("#popupWindowFormula").jqxWindow('close');
		$("#popupWindowFormulaNotValidate").jqxWindow('close');
		$("#popupWindowFormulaValidate").jqxWindow('close');
	
		$("#jqxgrid").jqxGrid('setcellvalue', editrow, "formula", formular);
		

	} else {

		$("#popupWindowFormulaNotValidate").jqxWindow({
			theme : 'olbius',
			height : 'auto',
			minHeight : 200,
			width : 300,
			position : {
				x : parseInt(offset.left) + 60,
				y : parseInt(offset.top) + 60
			}
		});

		$("#popupWindowFormulaNotValidate").jqxWindow('open');
	}
}
function validate() {
	var offset = $("#jqxFunctionGrid").offset();

	var formular = $('#editFormular').val();
	if (validateFormular(formular)) {

		$("#popupWindowFormulaValidate").jqxWindow({
			theme : 'olbius',
			height : 'auto',
			minHeight : 200,
			width : 300,
			position : {
				x : parseInt(offset.left) + 60,
				y : parseInt(offset.top) + 60
			}
		});
		$("#popupWindowFormulaValidate").jqxWindow('open');

	} else {
		$("#popupWindowFormulaNotValidate").jqxWindow({
			theme : 'olbius',
			height : 'auto',
			minHeight : 200,
			width : 300,
			position : {
				x : parseInt(offset.left) + 60,
				y : parseInt(offset.top) + 60
			}
		});
		$("#popupWindowFormulaNotValidate").jqxWindow('open');
	}

}
/*var position = $("#editFormular").getCursorPosition();*/
function add() {
	var position = $("#editFormular").getCursorPosition();
	
	var formular = $('#editFormular').val();
	if(position){
		var leng = formular.length;
		
		formular = formular.substring(0, position) + " + " + formular.substring(position, leng);
	
	}else{
		formular = formular + " + ";
	}
	
	$('#editFormular').val(formular);
	$("#editFormular").setCursorPosition(position + 3);
}
function minus() {
var position = $("#editFormular").getCursorPosition();
	
	var formular = $('#editFormular').val();
	if(position){
		var leng = formular.length;
		
		formular = formular.substring(0, position) + " - " + formular.substring(position, leng);
		/*$("#editFormular").setCursorPosition(position + 1);*/
		/*$("#editFormular").insertAtCursor("+");*/
	}else{
		formular = formular + " - ";
	}
	
	$('#editFormular').val(formular);
	$("#editFormular").setCursorPosition(position + 3);
}
function addNewTarget() {

	$("#jqxgrid").jqxGrid('addrow', null, {});
}
function saveAccTarget() {
	var griddata = $('#jqxgrid').jqxGrid('getdatainformation');
	var rowCount = griddata.rowscount;
	var accTargetReports = new Array();
	if (rowCount > 0) {
		for (var i = 0; i < rowCount; i++) {
			var row = $("#jqxgrid").jqxGrid('getrowdata', i);
			
			var accTargetReport = new Object;
			if(row.targetId){
				accTargetReport.targetId = row.targetId;
			}else{
				accTargetReport.targetId = null;
			}
			if(row.reportId){
				
				accTargetReport.reportId = row.reportId;
			}else{
				accTargetReport.reportId = null;
			}
			if(row.parentTargetId){
				accTargetReport.parentTargetId = row.parentTargetId;
			}else{
				accTargetReport.parentTargetId = null;
			}
			if(row.code){
				accTargetReport.code = row.code;
			}else{
				accTargetReport.code = null;
			}
			if(row.name){
				accTargetReport.name = row.name;
			}else{
				accTargetReport.name = null;
			}
			if(row.formula){
				var formular = row.formula;
				
				if(formular.indexOf('+') != -1){
					formular = formular.replace("+", "add"); //%20 is add
				}
				if(formular.indexOf('-') != -1){
					formular = formular.replace("+", "minus"); //%20 is minus
				}
				accTargetReport.formula =formular;
			}else{
				accTargetReport.formula = null;
			}
			if(row.demonstration){
				accTargetReport.demonstration = row.demonstration;
			}else{
				accTargetReport.demonstration = null;
			}
			if(row.description){
				accTargetReport.description = row.description;
			}else{
				accTargetReport.description = null;
			}
			if(row.displaySign){
				accTargetReport.displaySign = row.displaySign;
			}else{
				accTargetReport.displaySign = null;
			}
			if(row.displayStyle){
				accTargetReport.displayStyle = row.displayStyle;
			}else{
				accTargetReport.displayStyle = null;
			}
			if(row.orderIndex){
				accTargetReport.orderIndex = row.orderIndex;
			}else{
				accTargetReport.orderIndex = null;
			}
			if(row.unionSign){
				accTargetReport.unionSign = row.unionSign;
			}else{
				accTargetReport.unionSign = null;
			}
			
			accTargetReports.push(accTargetReport);
		}

	}
	var organizationPartyId = $('#organizationPartyId').val();
	
	var param = "accTargetReports=" + JSON.stringify(accTargetReports) + "&reportTypeId=" + reportTypeId + "&organizationPartyId="+organizationPartyId ;

	$.ajax({
		url : 'SaveTargetReport',
		data : param,
		type : 'post',
		async : false,

		success : function(data) {

			$('#jqxgrid').jqxGrid('updatebounddata');

		},
		error : function(data) {
			$('#jqxgrid').jqxGrid('updatebounddata');
		}
	});
}
function setDefault(){
	var param = "reportTypeId=" + reportTypeId;
	$.ajax({
		url : 'SetToDefaultAccTarget',
		data : param,
		type : 'post',
		async : false,

		success : function(data) {

			$('#jqxgrid').jqxGrid('updatebounddata');

		},
		error : function(data) {
			$('#jqxgrid').jqxGrid('updatebounddata');
		}
	});
}
function cancelRow(){
	$('#jqxgrid').jqxGrid('updatebounddata');
}
function cancelFormular(){
	$('#editFormular').val('');
}
function addRowCode(code){
var position = $("#editFormular").getCursorPosition();
	
	var formular = $('#editFormular').val();
	if(position){
		var leng = formular.length;
		
		formular = formular.substring(0, position) + code + formular.substring(position, leng);
		/*$("#editFormular").setCursorPosition(position + 1);*/
		/*$("#editFormular").insertAtCursor("+");*/
	}else{
		formular = formular + " - ";
	}
	
	$('#editFormular').val(formular);
	$("#editFormular").setCursorPosition(position + code.length);
}

