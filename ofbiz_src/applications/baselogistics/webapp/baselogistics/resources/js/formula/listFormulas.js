$(function(){
	ListFormula.init();
});
var ListFormula = (function() {
	var validatorNew;
	var validatorEdit;
	var listOperations = ['+', '-', '*', '/'];
	var listNumbers = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'];
	var listBoundChars = ['(', ')'];
	var dotChar = ['.'];
	var listWords = ['q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l','z', 'x', 'c', 'v', 'b', 'n', 'm',
	                 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L','Z', 'X', 'C', 'V', 'B', 'N', 'M'
	                 ];
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		var heightFormulaPopup = 550;
		$('#newFormulaTypeId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, height: 25, source: formulaTypeData, selectedIndex: 0, width: 300, theme: theme, displayMember: 'description', valueMember: 'formulaTypeId'});
		$('#editFormulaTypeId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, height: 25, source: formulaTypeData, selectedIndex: 0, width: 300, theme: theme, displayMember: 'description', valueMember: 'formulaTypeId'});
		
		if (formulaTypeId) {
			$('#newFormulaTypeId').jqxDropDownList('val', formulaTypeId);
		}
		
		jOlbUtil.windowPopup.create($("#alterpopupWindowFormula"), {maxWidth: 1200, minWidth: 300, width: 950, minHeight: 200, height: heightFormulaPopup, maxHeight: 1000, resizable: false, modalZIndex: 10000, isModal: true, autoOpen: false, cancelButton: $("#newForCancel"), modalOpacity: 0.7, theme:theme});
		jOlbUtil.windowPopup.create($("#editpopupWindowFormula"), {maxWidth: 1200, minWidth: 300, width: 950, minHeight: 200, height: heightFormulaPopup, maxHeight: 1000, resizable: false, modalZIndex: 10000, isModal: true, autoOpen: false, cancelButton: $("#editForCancel"), modalOpacity: 0.7, theme:theme});
		
		$("#newFormulaCode").jqxInput({width: 295, theme: theme, height: 24, placeHolder: "a-z, A-Z, _"});
		$("#newFormulaName").jqxInput({width: 295, theme: theme, height: 24});
		$("#newFormulaValue").jqxInput({width: 695, theme: theme, height: 30});
		$("#newFormulaDescription").jqxInput({ width: 300, height: 110, theme: theme});
		
		$("#editFormulaCode").jqxInput({width: 295, theme: theme, height: 24, placeHolder: "a-z, A-Z, _"});
		$("#editFormulaValue").jqxInput({width: 695, theme: theme, height: 30});
		$("#editFormulaName").jqxInput({width: 295, theme: theme, height: 24});
		$("#editFormulaDescription").jqxInput({ width: 300, height: 110, theme: theme});
		
		new Suggest.LocalMulti("newFormulaValue", "suggest", listParameterDataCode, {dispAllKey: true});
		new Suggest.LocalMulti("editFormulaValue", "editSuggest", listParameterDataCode, {dispAllKey: true});
		
		initParameterList();
		initParameterEditList();
		
		$("#contextMenuFormula").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	};
	
	var initParameterEditList = function(){
		var datafieldParamEdit =  [
			{ name: 'parameterId', type: 'string'},
			{ name: 'parameterCode', type: 'string'},
			{ name: 'parameterName', type: 'string'},
			{ name: 'parameterValue', type: 'string'},
			{ name: 'parameterTypeId', type: 'string'},
			{ name: 'defaultValue', type: 'string'},
			{ name: 'description', type: 'string'},
	  	];
	  	var columnlistParamEdit = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true, 
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},
			{ text: uiLabelMap.BLFormulaParameterId, datafield: 'parameterCode', pinned: true, width:120, 
			},
			{ text: uiLabelMap.BLParameterName, datafield: 'parameterName', width:150,
			},
			{ text: uiLabelMap.BLFormulaParameterValue, datafield: 'parameterValue', minwidth: 200,  
			},
			{ text: uiLabelMap.BLDefaultValue, datafield: 'defaultValue', minwidth: 200, 
			},
			{ text: uiLabelMap.BLFormulaParameterType, datafield: 'parameterTypeId', width:150,
				cellsrenderer: function(row, column, value){
					 if (value){
						 return '<span>'+ getParameterDescription(value)+'</span>';
					 }
				 }, 
			},
			{ text: uiLabelMap.Description, datafield: 'description', width: 200, 
			},                  
	  	];
	  	var configParamEdit = {
	  		width: 703, 
	  		virtualmode: true,
		  	showtoolbar: false,
		  	selectionmode: 'singlerow',
		  	pageable: true,
		  	sortable: true,
		  	filterable: true,	        
		  	editable: false,
	      	rowsheight: 26,
	      	rowdetails: false,
	      	useUrl: false,
	      	source: {pagesize: 5}
	  	};
	  	Grid.initGrid(configParamEdit, datafieldParamEdit, columnlistParamEdit, null, $("#jqxgridListParametersEdit"));
	  	var tmpS = $("#jqxgridListParametersEdit").jqxGrid('source');
		tmpS._source.localdata = listParameterData;
	    $("#jqxgridListParametersEdit").jqxGrid('source', tmpS);
	    $("#jqxgridListParametersEdit").jqxGrid('updatebounddata');
	};

	
	var initParameterList = function(){
		var datafieldParam =  [
			{ name: 'parameterId', type: 'string'},
			{ name: 'parameterCode', type: 'string'},
			{ name: 'parameterName', type: 'string'},
			{ name: 'parameterValue', type: 'string'},
			{ name: 'parameterTypeId', type: 'string'},
			{ name: 'defaultValue', type: 'string'},
			{ name: 'description', type: 'string'},
	  	];
	  	var columnlistParam = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true, 
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},
			{ text: uiLabelMap.BLFormulaParameterId, datafield: 'parameterCode', pinned: true, width:120, 
			},
			{ text: uiLabelMap.BLParameterName, datafield: 'parameterName', width:250,
			},
			{ text: uiLabelMap.BLFormulaParameterValue, datafield: 'parameterValue', minwidth: 160, 
				cellsrenderer: function(row, column, value){
					var data = $('#jqxgridListParameters').jqxGrid('getrowdata', row);
				 	if (data.parameterTypeId == 'PARAM_SYSTEM'){
					 	return '<span>'+uiLabelMap.BLUpdateWhenCalculating+'</span>';
				 	} else {
				 		if (typeof parseInt(value) == 'number') {
				 			return '<span style="text-align: right">'+formatnumber(value)+'</span>';
				 		}
				 	}
			 	}, 
			},
			{ text: uiLabelMap.BLDefaultValue, datafield: 'defaultValue', minwidth: 160, 
				cellsrenderer: function(row, column, value){
					var data = $('#jqxgridListParameters').jqxGrid('getrowdata', row);
				 	if (data.parameterTypeId == 'PARAM_SYSTEM'){
					 	return '<span>' + uiLabelMap.BLUpdateWhenCalculating +'</span>';
				 	} else {
				 		if (typeof parseInt(value) == 'number') {
				 			return '<span style="text-align: right">'+formatnumber(value)+'</span>';
				 		}
				 	}
			 	}, 
			},
			{ text: uiLabelMap.BLFormulaParameterType, datafield: 'parameterTypeId', width:150,
				cellsrenderer: function(row, column, value){
					 if (value){
						 return '<span>'+ getParameterDescription(value)+'</span>';
					 }
				 }, 
			},
			{ text: uiLabelMap.Description, datafield: 'description', width: 200, 
			},                  
	  	];
	  	var configParam = {
	  		width: 703, 
	  		virtualmode: true,
		  	showtoolbar: false,
		  	selectionmode: 'singlerow',
		  	pageable: true,
		  	sortable: true,
		  	filterable: true,	        
		  	editable: false,
	      	rowsheight: 26,
	      	rowdetails: false,
	      	useUrl: false,
	      	source: {pagesize: 5}
	  	};
	  	Grid.initGrid(configParam, datafieldParam, columnlistParam, null, $("#jqxgridListParameters"));
	  	var tmpS = $("#jqxgridListParameters").jqxGrid('source');
		tmpS._source.localdata = listParameterData;
	    $("#jqxgridListParameters").jqxGrid('source', tmpS);
	    $("#jqxgridListParameters").jqxGrid('updatebounddata');
	};
	
	var initElementComplex = function() {
	};
	
	var initEvents = function() {
		$("#contextMenuFormula").on('itemclick', function (event) {
			var data = $('#jqxgridFromula').jqxGrid('getRowData', $("#jqxgridFromula").jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			if (tmpStr == uiLabelMap.Edit){
				if (data.statusId == "FML_DEACTIVATED") {
					jOlbUtil.alert.error(uiLabelMap.BLFormulaHasBeenDeactivated);
					return false;
				}
				$("#editFormulaId").val(data.formulaId);
				$("#editFormulaCode").jqxInput('val', data.formulaCode);
				$("#editFormulaName").jqxInput('val', data.formulaName);
				$("#editFormulaValue").jqxInput('val', data.formulaValue);
				$("#editFormulaDescription").jqxInput('val', data.description);
				
				$("#editpopupWindowFormula").jqxWindow('open');
			} else if (tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgridFromula').jqxGrid('updatebounddata');
			} else if (tmpStr == uiLabelMap.Delete){
				deleteFormula(data.formulaId);
			} else if (tmpStr == uiLabelMap.BLDeactivated){
				if (data.statusId == "FML_DEACTIVATED") {
					jOlbUtil.alert.error(uiLabelMap.BLFormulaHasBeenDeactivated);
					return false;
				} else {
					changeFormulaStatus(data.formulaId, "FML_DEACTIVATED");
				}
			} else if (tmpStr == uiLabelMap.BLActivated){
				if (data.statusId == "FML_ACTIVATED") {
					jOlbUtil.alert.error(uiLabelMap.BLFormulaHasBeenActivated);
					return false;
				} else {
					changeFormulaStatus(data.formulaId, "FML_ACTIVATED");
				}
			}
			
		});
		
		$('#newForSave').on('click', function(event){
			var validate = getNewValidator();
			if (!validate) return;
			var code = $("#newFormulaCode").jqxInput('val');
			var value = $("#newFormulaValue").jqxInput('val');
			var name = $("#newFormulaName").jqxInput('val');
			var type = $("#newFormulaTypeId").jqxDropDownList('val');
			var description = $("#newFormulaDescription").jqxInput('val');
	        createFormula(code.trim(), name.trim(), value.trim(), type, description.trim());
		});
		
		$('#editForSave').on('click', function(event){
			var validate = getEditValidator();
			if (!validate) return;
			var id = $("#editFormulaId").val();
			var code = $("#editFormulaCode").jqxInput('val');
			var value = $("#editFormulaValue").jqxInput('val');
			var name = $("#editFormulaName").jqxInput('val');
			var type = $("#editFormulaTypeId").jqxDropDownList('val');
			var description = $("#editFormulaDescription").jqxInput('val');
	        updateFormula(id, code.trim(), name.trim(), value.trim(), type, description.trim());
		});
		
		$("#alterpopupWindowFormula").on('close', function (){
			$("#newFormulaCode").jqxInput('clear');
			$("#newFormulaName").jqxInput('clear');
			$("#newFormulaValue").jqxInput('clear');
			$("#newFormulaDescription").jqxInput('clear');
			validatorNew.hide();
		});
		
		$("#editpopupWindowFormula").on('close', function (){
			$("#editFormulaCode").jqxInput('clear');
			$("#editFormulaName").jqxInput('clear');
			$("#editFormulaValue").jqxInput('clear');
			$("#editFormulaDescription").jqxInput('clear');
			validatorEdit.hide();
		});
		
		$("#jqxgridListParameters").on('rowselect', function (event) {
		    var args = event.args;
		    var rowBoundIndex = args.rowindex;
		    var rowData = args.row;
			if (rowData){
				var paramCode = rowData.parameterCode;
				var pos = getCurrentPossition ("newFormulaValue");
				var start = pos.start;
				var end = pos.end;
				var value = $("#newFormulaValue").jqxInput('val');
				var newValue = value.slice(0, start) + " " + paramCode + " " + value.slice(end);
				$("#newFormulaValue").jqxInput('val', newValue);
				$("#newFormulaValue").focus().setCursorPosition(start+paramCode.length+1);
				setTimeout(function(){
					$('#jqxgridListParameters').jqxGrid('unselectrow', rowBoundIndex);
				}, 200);
			}
		});
		
		$("#jqxgridListParametersEdit").on('rowselect', function (event) {
		    var args = event.args;
		    var rowBoundIndex = args.rowindex;
		    var rowData = args.row;
			if (rowData){
				var paramCode = rowData.parameterCode;
				var pos = getCurrentPossition ("editFormulaValue");
				var start = pos.start;
				var end = pos.end;
				var value = $("#editFormulaValue").jqxInput('val');
				var newValue = value.slice(0, start) + " " + paramCode + " " + value.slice(end);
				$("#editFormulaValue").jqxInput('val', newValue);
				$("#editFormulaValue").focus().setCursorPosition(start+paramCode.length+1);
				setTimeout(function(){
					$('#jqxgridListParametersEdit').jqxGrid('unselectrow', rowBoundIndex);
				}, 200);
			}
		});
	};
	
	var initValidateForm = function(){
		var extendRulesForNew = [
				{input: '#newFormulaCode', message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter, action: 'blur', rule: 
				    function (input, commit) {
				    	var value = $(input).val();
						if(value && !(/^[a-zA-Z_]+$/.test(value))){
							return false;
						}
						return true;
					}
				},
                 {input: '#newFormulaValue', message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter, action: 'change, blur', rule: 
                     function (input, commit) {
                     	var value = $(input).val();
             			if(value && !(/^[a-zA-Z0-9_\*\.\+\-\/ \)\(]+$/.test(value))){
             				return false;
             			}
             			return true;
                 	}
                 },
                 {input: '#newFormulaValue', message: uiLabelMap.BLFormulaCharFirstWrong, action: 'change, blur', rule: 
                     function (input, commit) {
                     	var value = $(input).val();
             			var validateVal = validateFormulaFirstChar(value);
             			if (!validateVal) return false;
             			return true;
                 	}
                 },
                 {input: '#newFormulaValue', message: uiLabelMap.BLFormulaCharLastWrong, action: 'change, blur', rule: 
                     function (input, commit) {
                     	var value = $(input).val();
             			var validateVal = validateFormulaLastChar(value);
             			if (!validateVal) return false;
             			return true;
                 	}
                 },
                 {input: '#newFormulaValue', message: uiLabelMap.BLFormulaContainParameterNotExisted, action: 'change, blur', rule: 
                     function (input, commit) {
                     	var value = $(input).val();
             			var validateVal = validateFormulaParameter(value);
             			if (!validateVal) return false;
             			return true;
                 	}
                 },
                 {input: '#newFormulaValue', message: uiLabelMap.BLFormulaMissingOpenParenthesis, action: 'change, blur', rule: 
                	 function (input, commit) {
	                	 var value = $(input).val();
	                	 var validateVal = validateFormulaOpenParenthesis(value);
	                	 if (!validateVal) return false;
	                	 return true;
                 	}
                 },
                 {input: '#newFormulaValue', message: uiLabelMap.BLFormulaMissingCloseParenthesis, action: 'change, blur', rule: 
                	 function (input, commit) {
	                	 var value = $(input).val();
	                	 var validateVal = validateFormulaCloseParenthesis(value);
	                	 if (!validateVal) return false;
	                	 return true;
	                 }
                 },
                 {input: '#newFormulaValue', message: uiLabelMap.BLFormulaPossitionOperatorWrong, action: 'change, blur', rule: 
                	 function (input, commit) {
	                	 var value = $(input).val();
	                	 var validateVal = validatePositionOperators(value);
	                	 if (!validateVal) return false;
	                	 return true;
	                 }
                 },
                 {input: '#newFormulaValue', message: uiLabelMap.BLFormulaNeedToHaveParamOrNumber, action: 'change, blur', rule: 
                	 function (input, commit) {
	                	 var value = $(input).val();
	                	 var validateVal = validateContainsParamOrNumber(value);
	                	 if (!validateVal) return false;
	                	 return true;
	                 }
                 },
                 {input: '#newFormulaValue', message: uiLabelMap.BLFormulaNotAllowUnderscore, action: 'change, blur', rule: 
                	 function (input, commit) {
	                	 var value = $(input).val();
	                	 var validateVal = validateUnderscore(value);
	                	 if (!validateVal) return false;
	                	 return true;
	                 }
                 },
                 {input: '#newFormulaValue', message: uiLabelMap.BLFormulaNeedOperationBetweenParam, action: 'change, blur', rule: 
                	 function (input, commit) {
	                	 var value = $(input).val();
	                	 var validateVal = validatePositionParams(value);
	                	 if (!validateVal) return false;
	                	 return true;
	                 }
                 },
              ];
   		var mapRulesForNew = [
   	            {input: '#newFormulaCode', type: 'validInputNotNull'},
   	            {input: '#newFormulaName', type: 'validInputNotNull'},
   	            {input: '#newFormulaValue', type: 'validInputNotNull'},
   	            ];
   		validatorNew = new OlbValidator($('#formFormulaAddNew'), mapRulesForNew, extendRulesForNew, {position: 'bottomcenter'});
   		
   		var extendRulesForEdit = [
			{input: '#editFormulaCode', message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter, action: 'blur', rule: 
			    function (input, commit) {
			    	var value = $(input).val();
					if(value && !(/^[a-zA-Z_]+$/.test(value))){
						return false;
					}
					return true;
				}
			},
               {input: '#editFormulaValue', message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter, action: 'change, blur', rule: 
                   function (input, commit) {
                   	var value = $(input).val();
           			if(value && !(/^[a-zA-Z0-9_\*\.\+\-\/ \)\(]+$/.test(value))){
           				return false;
           			}
           			return true;
               	}
               },
               {input: '#editFormulaValue', message: uiLabelMap.BLFormulaCharFirstWrong, action: 'change, blur', rule: 
                   function (input, commit) {
                   	var value = $(input).val();
           			var validateVal = validateFormulaFirstChar(value);
           			if (!validateVal) return false;
           			return true;
               	}
               },
               {input: '#editFormulaValue', message: uiLabelMap.BLFormulaCharLastWrong, action: 'change, blur', rule: 
                   function (input, commit) {
                   	var value = $(input).val();
           			var validateVal = validateFormulaLastChar(value);
           			if (!validateVal) return false;
           			return true;
               	}
               },
               {input: '#editFormulaValue', message: uiLabelMap.BLFormulaContainParameterNotExisted, action: 'change, blur', rule: 
                   function (input, commit) {
                   	var value = $(input).val();
           			var validateVal = validateFormulaParameter(value);
           			if (!validateVal) return false;
           			return true;
               	}
               },
               {input: '#editFormulaValue', message: uiLabelMap.BLFormulaMissingOpenParenthesis, action: 'change, blur', rule: 
              	 function (input, commit) {
                	 var value = $(input).val();
                	 var validateVal = validateFormulaOpenParenthesis(value);
                	 if (!validateVal) return false;
                	 return true;
               	}
               },
               {input: '#editFormulaValue', message: uiLabelMap.BLFormulaMissingCloseParenthesis, action: 'change, blur', rule: 
              	 function (input, commit) {
                	 var value = $(input).val();
                	 var validateVal = validateFormulaCloseParenthesis(value);
                	 if (!validateVal) return false;
                	 return true;
                 }
               },
               {input: '#editFormulaValue', message: uiLabelMap.BLFormulaPossitionOperatorWrong, action: 'change, blur', rule: 
              	 function (input, commit) {
                	 var value = $(input).val();
                	 var validateVal = validatePositionOperators(value);
                	 if (!validateVal) return false;
                	 return true;
                 }
               },
               {input: '#editFormulaValue', message: uiLabelMap.BLFormulaNeedToHaveParamOrNumber, action: 'change, blur', rule: 
              	 function (input, commit) {
                	 var value = $(input).val();
                	 var validateVal = validateContainsParamOrNumber(value);
                	 if (!validateVal) return false;
                	 return true;
                 }
               },
               {input: '#editFormulaValue', message: uiLabelMap.BLFormulaNotAllowUnderscore, action: 'change, blur', rule: 
              	 function (input, commit) {
                	 var value = $(input).val();
                	 var validateVal = validateUnderscore(value);
                	 if (!validateVal) return false;
                	 return true;
                 }
               },
               {input: '#editFormulaValue', message: uiLabelMap.BLFormulaNeedOperationBetweenParam, action: 'change, blur', rule: 
              	 function (input, commit) {
                	 var value = $(input).val();
                	 var validateVal = validatePositionParams(value);
                	 if (!validateVal) return false;
                	 return true;
                 }
               },
            ];
		var mapRulesForEdit = [
			{input: '#editFormulaCode', type: 'validInputNotNull'},
			{input: '#editFormulaName', type: 'validInputNotNull'},
			{input: '#editFormulaValue', type: 'validInputNotNull'},
		];
		validatorEdit = new OlbValidator($('#formFormulaEdit'), mapRulesForEdit, extendRulesForEdit, {position: 'bottomcenter'});
	};
	
	var getCurrentPossition = function (inputId) {
	    var ctl = document.getElementById(inputId);
	    var startPos = ctl.selectionStart;
	    var endPos = ctl.selectionEnd;
	    var map = {
	    	start: startPos,
	    	end: endPos,
	    };
	    return map;
	}
	
	var validateUnderscore = function (formula) {
		if (formula.length == 0) return true;
		for (var i = 0; i < formula.length - 1; i ++) {
			var cur = formula[i];
			var next = formula[i + 1];
			if ((cur == "_" && !listWords.includes(next)) || (!listWords.includes(cur) && next == "_")) {
				return false;
			}
		}
		return true;
	};
	
	var validateContainsParamOrNumber = function (formula) {
		var reg = /\d/;
		if (reg.test(formula)) {
			return true;
		}
		for (var i = 0; i < formula.length; i ++) {
			if (listWords.includes(formula[i])){
				return true;
			}
		}
		return false;
	};
	
	var validateFormulaOpenParenthesis = function (formula){
		if (formula.length == 0) return true;
		var char = "(";
		var countOpen = getQuantityChar(formula, char);
		char = ")";
		var countClose = getQuantityChar(formula, char);
		if (countOpen < countClose) return false;
		return true;
	};
	
	var validateFormulaCloseParenthesis = function (formula){
		if (formula.length == 0) return true;
		var char = "(";
		var countOpen = getQuantityChar(formula, char);
		char = ")";
		var countClose = getQuantityChar(formula, char);
		if (countClose < countOpen) return false;
		return true;
	};
	
	var getQuantityChar = function(value, char) {
	     if (char == ")") {
    	 	char = "\\)";
	     }
	     if (char == "(") {
	    	 char = "\\(";
	     }
	     return (value.match(new RegExp(char, "g")) || []).length;
    };
	
	var validatePositionOperators = function (formula) {
		formula = formula.trim();
		if (formula.length == 0) return true;
		formula = formula.replace(/ /g, "");
		for (var i = 0; i < formula.length - 1; i ++) {
			var cur = formula[i];
			var next = formula[i + 1];
			if (listOperations.includes(cur) && listOperations.includes(next)) {
				return false;
			}
			if ("(" == cur && ("*" == next || "/" == next)) {
				return false;
			}
		}
		return true;
	};
	
	var validatePositionParams = function (formula) {
		formula = formula.trim();
		if (formula.length == 0) return true;
		var x = formula.replace(/ /g, ";");
		var tmp = x.split(";");
		var y = [];
		for (var n = 0; n < tmp.length; n ++){
			if (tmp[n] != "") {
				y.push(tmp[n]);
			}
		}
		for (var i = 0; i < y.length - 1; i ++) {
			var cur = y[i];
			var next = y[i + 1];
			if (listParameterDataCode.includes(cur) && listParameterDataCode.includes(next)) {
				return false;
			}
			if ((listNumbers.includes(cur.charAt(cur.length-1)) && listParameterDataCode.includes(next)) || (listNumbers.includes(next.charAt(0)) && listParameterDataCode.includes(cur))){
				return false;
			}
		}
		return true;
	};
	
	var validateFormulaParameter = function (formula) {
		formula = formula.trim();
		if (formula.length == 0) return true;
		var listParameterFound = findParameterInFormula(formula);
		var listNotParameter = [];
		for (var i = 0; i < listParameterFound.length; i ++){
			var par = listParameterFound[i]; 
			if (!listParameterDataCode.includes(par)){
				listNotParameter.push(par);
			}
		}
		if (listNotParameter.length > 0) return false;
		return true;
	}
	
	var validateFormulaFirstChar = function (formula) {
		formula = formula.trim();
		var length = formula.length;
		if (length == 0) return true;
		var firstChar = formula.charAt(0);
		if (firstChar != '(') {
			if (!listNumbers.includes(firstChar)){
				if (firstChar == '*' || firstChar == '\/') {
					return false;
				}
				var end = 1;
				for (var i = 1; i < length; i ++){
					var y = formula.charAt(i);
					if (listOperations.includes(y) || listNumbers.includes(y) || listBoundChars.includes(y) || dotChar.includes(y) || /\s/.test(y) || i == length - 1) {
						if (i == length - 1) {
							end = i  + 1;
						} else {
							end = i;
						}
						break;
					}
				}
				var par = formula.substring(0, end);
				if (!listParameterDataCode.includes(par)){
					return false;
				}
			}
		}
		return true;
	};
	
	var validateFormulaLastChar = function (formula) {
		formula = formula.trim();
		var length = formula.length;
		if (length == 0) return true;
		var lastChar = formula.charAt(length - 1);
		if (lastChar != ')') {
			if (!listNumbers.includes(lastChar)){
				if (dotChar.includes(lastChar)) {
					return false;
				}
				if (listOperations.includes(lastChar)) {
					return false;
				}
				var begin = length - 1;
				for (var i = length - 1; i > -1; i --){
					var y = formula.charAt(i);
					if (listOperations.includes(y) || listNumbers.includes(y) || listBoundChars.includes(y) || dotChar.includes(y) || /\s/.test(y) || i == 0) {
						if (i == 0) {
							begin = i;
						} else {
 							begin = i + 1;
						}
						break;
					} 
				}
				var par = formula.substring(begin, length);
				if (!listParameterDataCode.includes(par)){
					return false;
				}
			}
		}
		return true;
	};
	
	var findParameterInFormula = function (formula) {
		var length = formula.length;
		var listParameterFound = [];
		var begin = 0;
		var end = 0;
		for (var i = 0; i < length; i ++){
			if (i >= end || (i == length - 1 && end > length)) {
				var x = formula.charAt(i);
				if (listWords.includes(x)) {
					begin = i;
					for (var j = i + 1; j < length; j ++){
						var y = formula.charAt(j);
						if (listOperations.includes(y) || listNumbers.includes(y) || listBoundChars.includes(y) || /\s/.test(y) || dotChar.includes(y)) {
							end = j;
							break;
						} else if (j == length - 1){
							end = j + 1;
							break;
						}
					}
					var par = formula.substring(begin, end);
					listParameterFound.push(par);
				} else {
					begin = i + 1;
				}
			}
		}
		return listParameterFound;
	};
	
	
	var changeFormulaStatus = function (formulaId, statusId){
    	bootbox.dialog(uiLabelMap.AreYouSureUpdate, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll();}
        }, 
        {"label": uiLabelMap.OK,
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	Loading.show('loadingMacro');
            	setTimeout(function(){
			    	$.ajax({
			    		url: "changeFormulaStatus",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			formulaId: formulaId,
			    			statusId: statusId
			    		},
			    		success: function (res){
			    			if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
			    				if (res._ERROR_MESSAGE_.indexOf("OLBIUS_FORMULA_IN_USING") > -1 || res._ERROR_MESSAGE_LIST_.indexOf("OLBIUS_FORMULA_IN_USING") > -1){
			    					jOlbUtil.alert.error(uiLabelMap.BLCannotDelete + '. ' + uiLabelMap.BLFormulaInUsing);
			    					return false;
			    				}
			    				jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
			    				return false;
			    			}
			    			$("#jqxgridFromula").jqxGrid('updatebounddata');
			    		}
			    	});
			    	Loading.hide('loadingMacro');
            	}, 500);
            }
		}]);
    };
	
	var deleteFormula = function (formulaId){
    	bootbox.dialog(uiLabelMap.AreYouSureDelete, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll();}
        }, 
        {"label": uiLabelMap.OK,
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	Loading.show('loadingMacro');
            	setTimeout(function(){
			    	$.ajax({
			    		url: "deleteFormula",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			formulaId: formulaId,
			    		},
			    		success: function (res){
			    			if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
			    				if (res._ERROR_MESSAGE_.indexOf("OLBIUS_FORMULA_IN_USING") > -1 || res._ERROR_MESSAGE_LIST_.indexOf("OLBIUS_FORMULA_IN_USING") > -1){
			    					jOlbUtil.alert.error(uiLabelMap.BLCannotDelete + '. ' + uiLabelMap.BLFormulaInUsing);
			    					return false;
			    				}
			    				jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
			    				return false;
			    			}
			    			$("#jqxgridFromula").jqxGrid('updatebounddata');
			    		}
			    	});
			    	Loading.hide('loadingMacro');
            	}, 500);
            }
		}]);
    };
    
    var updateFormula = function (formulaId, newFormulaCode, newFormulaName, newFormulaValue, newFormulaTypeId, newDescription){
    	bootbox.dialog(uiLabelMap.AreYouSureUpdate, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll();}
        }, 
        {"label": uiLabelMap.OK,
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	Loading.show('loadingMacro');
            	setTimeout(function(){
			    	$.ajax({
			    		url: "updateFormulaLogistics",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			formulaId: formulaId,
			    			formulaCode: newFormulaCode,
			    			formulaValue: newFormulaValue,
			    			formulaTypeId: newFormulaTypeId,
			    			formulaName: newFormulaName,
			    			description: newDescription,
			    		},
			    		success: function (res){
			    			if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
			    				if (res._ERROR_MESSAGE_.indexOf("OLBIUS_FORMULA_CODE_EXISTED") > -1 || res._ERROR_MESSAGE_LIST_.indexOf("OLBIUS_FORMULA_CODE_EXISTED") > -1){
			    					jOlbUtil.alert.error(uiLabelMap.BLFormulaCodeExisted);
			    					return false;
			    				}
			    				jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
			    				return false;
			    			}
			    			$("#jqxgridFromula").jqxGrid('updatebounddata');
			    			$("#editpopupWindowFormula").jqxWindow('close');
			    		}
			    	});
			    	Loading.hide('loadingMacro');
            	}, 500);
            }
		}]);
    };
	
    var createFormula = function (newFormulaCode, newFormulaName, newFormulaValue, newFormulaTypeId, newDescription){
    	bootbox.dialog(uiLabelMap.AreYouSureCreate, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll();}
        }, 
        {"label": uiLabelMap.OK,
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	Loading.show('loadingMacro');
            	setTimeout(function(){
			    	$.ajax({
			    		url: "createFormulaLogistics",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			formulaCode: newFormulaCode,
			    			formulaValue: newFormulaValue,
			    			formulaTypeId: newFormulaTypeId,
			    			formulaName: newFormulaName,
			    			description: newDescription,
			    		},
			    		success: function (res){
			    			if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
			    				if (res._ERROR_MESSAGE_.indexOf("OLBIUS_FORMULA_CODE_EXISTED") > -1 || res._ERROR_MESSAGE_LIST_.indexOf("OLBIUS_FORMULA_CODE_EXISTED") > -1){
			    					jOlbUtil.alert.error(uiLabelMap.BLFormulaCodeExisted);
			    					return false;
			    				}
			    				jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
			    				return false;
			    			}
			    			$("#jqxgridFromula").jqxGrid('updatebounddata');
			    			$("#alterpopupWindowFormula").jqxWindow('close');
			    		}
			    	});
			    	Loading.hide('loadingMacro');
            	}, 500);
            }
		}]);
    }
    $.fn.setCursorPosition = function (pos) {
        this.each(function (index, elem) {
            if (elem.setSelectionRange) {
                elem.setSelectionRange(pos, pos);
            } else if (elem.createTextRange) {
                var range = elem.createTextRange();
                range.collapse(true);
                range.moveEnd('character', pos);
                range.moveStart('character', pos);
                range.select();
            }
        });
        return this;
    };
    
    var insertCharacter = function (idElement, character){
    	var pos = getCurrentPossition ("newFormulaValue");
		var start = pos.start;
		var end = pos.end;
		var value = $("#newFormulaValue").jqxInput('val');
		newValue = value;
		if (character == "+" || character == "-") {
			newValue = value.slice(0, start) + " " + character + " " + value.slice(end);
		} else if (character == ".") {
			if (value.slice(end) != " ") {
				newValue = value.slice(0, start) + character + value.slice(end);
			} else {
				newValue = value.slice(0, start) + character;
			}
		} else {
			var startChar1 = value.slice(start - 1, start);
			if (startChar1 == "*" || startChar1 == "/") {
				var startChar2 = value.slice(end + 1, start);
				if (startChar2 == "+" || startChar2 == "-") {
					newValue = value.slice(0, start) + " " + character + " " + value.slice(end);
				} else {
					newValue = value.slice(0, start) + character + value.slice(end);
				}
			} else {
				if (startChar1 == " ") {
					newValue = value.slice(0, start - 1) + character + value.slice(end);
				} else {
					if (startChar1 == "+" || startChar1 == "-") {
						newValue = value.slice(0, start) + " " + character + value.slice(end);
					} else {
						newValue = value.slice(0, start) + character + value.slice(end);
					}
				}
			}
		}
		$("#newFormulaValue").jqxInput('val', newValue);
		$("#newFormulaValue").focus().setCursorPosition(start+character.length+1);
    };
    
    var getNewValidator = function(){
    	return validatorNew.validate();
    }
    var getEditValidator = function(){
    	return validatorEdit.validate();
    }
	return {
		init: init,
		getNewValidator: getNewValidator,
		getEditValidator: getEditValidator,
		insertCharacter: insertCharacter,
	}
}());