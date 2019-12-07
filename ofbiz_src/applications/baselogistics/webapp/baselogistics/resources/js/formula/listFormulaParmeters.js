$(function(){
	ListFormulaParam.init();
});
var ListFormulaParam = (function() {
	var validatorNew;
	var validatorEdit;
	
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		
		jOlbUtil.windowPopup.create($("#alterpopupWindowFormulaParameter"), {maxWidth: 1200, minWidth: 300, width: 700, minHeight: 200, height: 435, maxHeight: 1000, resizable: false, modalZIndex: 10000, isModal: true, autoOpen: false, cancelButton: $("#newParamCancel"), modalOpacity: 0.7, theme:theme});
		jOlbUtil.windowPopup.create($("#editpopupWindowFormulaParameter"), {maxWidth: 1200, minWidth: 300, width: 700, minHeight: 200, height: 410, maxHeight: 1000, resizable: false, modalZIndex: 10000, isModal: true, autoOpen: false, cancelButton: $("#editParamCancel"), modalOpacity: 0.7, theme:theme});
		$('#newParameterTypeId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, height: 25, source: parameterTypeData, selectedIndex: 0, width: 300, theme: theme, displayMember: 'description', valueMember: 'parameterTypeId'});
		
		$("#newParameterCode").jqxInput({width: 295, theme: theme, height: 24, placeHolder: "a-z, A-Z, _"});
		$("#newParameterName").jqxInput({width: 295, theme: theme, height: 24});
		$("#newParameterValue").jqxInput({width: 295, theme: theme, height: 24});
		$("#newParameterDefaultValue").jqxInput({width: 295, theme: theme, height: 24});
		$("#newParameterDescription").jqxInput({ width: 300, height: 65, theme: theme});
		
		$('#editParameterTypeId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, height: 26, source: parameterTypeData, selectedIndex: 0, width: 300, theme: theme, displayMember: 'description', valueMember: 'parameterTypeId'});
		
		$("#editParameterCode").jqxInput({width: 295, theme: theme, height: 24, placeHolder: "a-z, A-Z, _"});
		$("#editParameterValue").jqxInput({width: 295, theme: theme, height: 24});
		$("#editParameterName").jqxInput({width: 295, theme: theme, height: 24});
		$("#editParameterDefaultValue").jqxInput({width: 295, theme: theme, height: 24});
		$("#editParameterDescription").jqxInput({ width: 300, height: 65, theme: theme});
		
		$("#newProductStoreId").jqxDropDownButton({width: 300, height: 26}); 
		$('#newProductStoreId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.BLApplyForAll+'</div>');
		initProductStoreList();
		
//		$("#editProductStoreId").jqxDropDownButton({width: 300, height: 26}); 
//		initProductStoreListEdit();
		
		$("#contextMenuParameter").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
		
	};
	
	var initProductStoreListEdit = function(){
		var datafieldStoreEdit =  [
	  		{ name: 'productStoreId', type: 'string'},
	  		{ name: 'storeName', type: 'string'},
	  		{ name: 'payToPartyId', type: 'string'},
	        { name: 'fullName', type: 'string'},
	  	];
	  	var columnlistStoreEdit = [
	      { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
	  	    groupable: false, draggable: false, resizable: false,
	  	    datafield: '', columntype: 'number', width: 50,
	  	    cellsrenderer: function (row, column, value) {
	  	        return '<div style=margin:4px;>' + (value + 1) + '</div>';
	  	    }
	  	},
	  	{ text: uiLabelMap.BLProductStoreId, datafield: 'productStoreId', align: 'left', width: 200, pinned: true, editable: false,},
	  	{ text: uiLabelMap.BLStoreName, datafield: 'storeName', align: 'left', minwidth: 200, editable: false,},
	  	{ text: uiLabelMap.Owner, datafield: 'fullName', align: 'left', width: 250, editable: false,},
	  	];
	  	var configStoreEdit = {
	  		width: '100%', 
	  		virtualmode: true,
		  	showtoolbar: false,
		  	selectionmode: 'checkbox',
		  	pageable: true,
		  	sortable: true,
		  	filterable: true,	        
		  	editable: false,
	      	rowsheight: 26,
	      	rowdetails: false,
	      	useUrl: true,
	      	url: 'getListProductStore',                
	      	source: {pagesize: 10}
	  	};
	  	Grid.initGrid(configStoreEdit, datafieldStoreEdit, columnlistStoreEdit, null, $("#jqxgridListProductStoreEdit"));
	};
	
	var initProductStoreList = function(){
		var datafield =  [
	  		{ name: 'productStoreId', type: 'string'},
	  		{ name: 'storeName', type: 'string'},
	  		{ name: 'payToPartyId', type: 'string'},
	        { name: 'fullName', type: 'string'},
	  	];
	  	var columnlist = [
	      { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
	  	    groupable: false, draggable: false, resizable: false,
	  	    datafield: '', columntype: 'number', width: 50,
	  	    cellsrenderer: function (row, column, value) {
	  	        return '<div style=margin:4px;>' + (value + 1) + '</div>';
	  	    }
	  	},
	  	{ text: uiLabelMap.BLProductStoreId, datafield: 'productStoreId', align: 'left', width: 200, pinned: true, editable: false,},
	  	{ text: uiLabelMap.BLStoreName, datafield: 'storeName', align: 'left', minwidth: 200, editable: false,},
	  	{ text: uiLabelMap.Owner, datafield: 'fullName', align: 'left', width: 250, editable: false,},
	  	];
	  	var config = {
	  		width: '100%', 
	  		virtualmode: true,
		  	showtoolbar: false,
		  	selectionmode: 'checkbox',
		  	pageable: true,
		  	sortable: true,
		  	filterable: true,	        
		  	editable: false,
	      	rowsheight: 26,
	      	rowdetails: false,
	      	useUrl: true,
	      	url: 'getListProductStore',                
	      	source: {pagesize: 10}
	  	};
	  	Grid.initGrid(config, datafield, columnlist, null, $("#jqxgridListProductStore"));
	};
	
	var initElementComplex = function() {
	};
	var initEvents = function() {
			
		$("#contextMenuParameter").on('itemclick', function (event) {
			var data = $('#jqxgridFromulaParameter').jqxGrid('getRowData', $("#jqxgridFromulaParameter").jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			if (tmpStr == uiLabelMap.Edit){
				if (data.parameterTypeId == "PARAM_SYSTEM") {
					jOlbUtil.alert.error(uiLabelMap.BLCannotEditSystemParameter);
    				return false;
				}
				if (data.parameterTypeId == "PAR_DEACTIVATED") {
					jOlbUtil.alert.error(uiLabelMap.BLParameterHasBeenDeactivated);
    				return false;
				}
				$('#editParameterTypeId').jqxDropDownList('val', data.parameterTypeId);
				$("#editParameterCode").jqxInput('val', data.parameterCode);
				$("#editParameterName").jqxInput('val', data.parameterName);
				$("#editParameterValue").jqxInput('val', data.parameterValue);
				$("#editParameterDefaultValue").jqxInput('val', data.defaultValue);
				$("#editParameterDescription").jqxInput('val', data.description);
				
				$("#editParameterId").val(data.parameterId);
				
//				$('#editProductStoreId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.BLApplyForAll+'</div>');
				
				$("#editpopupWindowFormulaParameter").jqxWindow('open');
			} else if (tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgridFromulaParameter').jqxGrid('updatebounddata');
			} else if (tmpStr == uiLabelMap.Delete){
				if (data.parameterTypeId == "PARAM_SYSTEM") {
					jOlbUtil.alert.error(uiLabelMap.BLCannotDeleteSystemParameter);
    				return false;
				}
				deleteFormulaParameter(data.parameterId);
			} else if (tmpStr == uiLabelMap.BLDeactivated){
				if (data.statusId == "PAR_DEACTIVATED") {
					jOlbUtil.alert.error(uiLabelMap.BLParameterHasBeenDeactivated);
					return false;
				} else {
					changeFormulaParameterStatus(data.parameterId, "PAR_DEACTIVATED");
				}
			} else if (tmpStr == uiLabelMap.BLActivated){
				if (data.statusId == "PAR_ACTIVATED") {
					jOlbUtil.alert.error(uiLabelMap.BLParameterHasBeenActivated);
					return false;
				} else {
					changeFormulaParameterStatus(data.parameterId, "PAR_ACTIVATED");
				}
			}
		});
		
		$('#newParamSave').on('click', function(event){
			var validate = getNewValidator();
			if (!validate) return;
			var code = $("#newParameterCode").jqxInput('val');
			var value = $("#newParameterValue").jqxInput('val');
			var name = $("#newParameterName").jqxInput('val');
			var defaultValue = $("#newParameterDefaultValue").jqxInput('val');
			var description = $("#newParameterDescription").jqxInput('val');
			var parameterTypeId = $('#newParameterTypeId').jqxDropDownList('val');
			var listProductStoreIds = []; 
			var rows = $("#jqxgridListProductStore").jqxGrid('getselectedrowindexes');
	        if (rows && rows.length >= 1){
	        	for (var i = 0; i < rows.length; i ++){
	        		var data = $("#jqxgridListProductStore").jqxGrid('getrowdata', rows[i]);
	        		var map = {};
	        		map["productStoreId"] = data.productStoreId;
	        		listProductStoreIds.push(map);
	        	}
	        }
	        createFormulaParameter(code, name, value, parameterTypeId, defaultValue, description, JSON.stringify(listProductStoreIds));
		});
		
		$('#editParamSave').on('click', function(event){
			var validate = getEditValidator();
			if (!validate) return;
			var parameterId = $("#editParameterId").val();
			var code = $("#editParameterCode").jqxInput('val');
			var value = $("#editParameterValue").jqxInput('val');
			var name = $("#editParameterName").jqxInput('val');
			var defaultValue = $("#editParameterDefaultValue").jqxInput('val');
			var description = $("#editParameterDescription").jqxInput('val');
			var parameterTypeId = $('#editParameterTypeId').jqxDropDownList('val');
	        updateFormulaParameter(parameterId, code, name, value, parameterTypeId, defaultValue, description);
		});
		
		$("#alterpopupWindowFormulaParameter").on('close', function (){
			$("#jqxgridListProductStore").jqxGrid('clearselection');
			$('#newProductStoreId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.BLApplyForAll+'</div>');
			
			$('#newParameterTypeId').jqxDropDownList({selectedIndex: 0});
			
			$("#newParameterCode").jqxInput('clear');
			$("#newParameterName").jqxInput('clear');
			$("#newParameterValue").jqxInput('clear');
			$("#newParameterDefaultValue").jqxInput('clear');
			$("#newParameterDescription").jqxInput('clear');
		});
		
		$("#jqxgridListProductStore").on('rowselect', function (event) {
	        var args = event.args;
	        var rows = $("#jqxgridListProductStore").jqxGrid('getselectedrowindexes');
	        var desc = null;
	        if (rows && rows.length == 1){
	        	var data = $("#jqxgridListProductStore").jqxGrid('getrowdata', rows[0]);
	        	desc = data.storeName;
	        } else if (rows.length > 1) {
	        	var data = $("#jqxgridListProductStore").jqxGrid('getrowdata', rows[0]);
	        	desc = data.storeName + ' ...(' +rows.length+ ' ' +uiLabelMap.BLProductStore+')';
	        } else {
				desc = uiLabelMap.BLApplyForAll;
			}
	        
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ desc+'</div>';
	        $('#newProductStoreId').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$("#jqxgridListProductStore").on('rowunselect', function (event) {
			var args = event.args;
			var rows = $("#jqxgridListProductStore").jqxGrid('getselectedrowindexes');
			var desc = null;
			if (rows && rows.length == 1){
				var data = $("#jqxgridListProductStore").jqxGrid('getrowdata', rows[0]);
				desc = data.storeName;
			} else if (rows.length > 1) {
				var data = $("#jqxgridListProductStore").jqxGrid('getrowdata', rows[0]);
				desc = data.storeName + ' ...(' +rows.length+ ' ' +uiLabelMap.BLProductStore+')';
			} else {
				desc = uiLabelMap.BLApplyForAll;
			}
			
			var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ desc+'</div>';
			$('#newProductStoreId').jqxDropDownButton('setContent', dropDownContent);
		});
	};
	var initValidateForm = function(){
		var extendRulesNew = [
				{input: '#newParameterCode', message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter, action: 'blur', rule: 
				    function (input, commit) {
				    	var value = $(input).val();
						if(value && !(/^[a-zA-Z_]+$/.test(value))){
							return false;
						}
						return true;
					}
				},
              ];
   		var mapRulesNew = [
   	            {input: '#newParameterValue', type: 'validInputNotNull'},
   	            {input: '#newParameterName', type: 'validInputNotNull'},
   	            {input: '#newParameterCode', type: 'validInputNotNull'},
   	            {input: '#newParameterTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
   	            ];
   		validatorNew = new OlbValidator($('#formFormulaParameterAddNew'), mapRulesNew, extendRulesNew, {position: 'right'});
   		
   		var extendRulesEdit = [
		];
		var mapRulesEdit = [
			{input: '#editParameterValue', type: 'validInputNotNull'},
			{input: '#editParameterName', type: 'validInputNotNull'},
			{input: '#editParameterTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
			{input: '#editParameterCode', type: 'validInputNotNull'},
		];
		validatorEdit = new OlbValidator($('#formFormulaParameterEdit'), mapRulesEdit, extendRulesEdit, {position: 'right'});
	};
	
    var getNewValidator = function(){
    	return validatorNew.validate();
    };
    
    var getEditValidator = function(){
    	return validatorEdit.validate();
    };
    
    var changeFormulaParameterStatus = function (parameterId, statusId){
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
			    		url: "changeFormulaParameterStatus",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			parameterId: parameterId,
			    			statusId: statusId,
			    		},
			    		success: function (res){
			    			if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
			    				if (res._ERROR_MESSAGE_.indexOf("OLBIUS_PARAMETER_IN_USING") > -1 || res._ERROR_MESSAGE_LIST_.indexOf("OLBIUS_PARAMETER_IN_USING") > -1){
			    					jOlbUtil.alert.error(uiLabelMap.BLCannotDelete + '. ' + uiLabelMap.BLParameterInUsing);
			    					return false;
			    				}
			    				jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
			    				return false;
			    			}
			    			$("#jqxgridFromulaParameter").jqxGrid('updatebounddata');
			    		}
			    	});
			    	Loading.hide('loadingMacro');
            	}, 500);
            }
		}]);
    };
    
    var deleteFormulaParameter = function (parameterId){
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
			    		url: "deleteFormulaParameter",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			parameterId: parameterId,
			    		},
			    		success: function (res){
			    			if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
			    				if (res._ERROR_MESSAGE_.indexOf("OLBIUS_PARAMETER_IN_USING") > -1 || res._ERROR_MESSAGE_LIST_.indexOf("OLBIUS_PARAMETER_IN_USING") > -1){
			    					jOlbUtil.alert.error(uiLabelMap.BLCannotDelete + '. ' + uiLabelMap.BLParameterInUsing);
			    					return false;
			    				}
			    				jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
			    				return false;
			    			}
			    			$("#jqxgridFromulaParameter").jqxGrid('updatebounddata');
			    		}
			    	});
			    	Loading.hide('loadingMacro');
            	}, 500);
            }
		}]);
    };
    
    var createFormulaParameter = function (parameterCode, parameterName, parameterValue, parameterTypeId, defaultValue, description, listProductStoreIds){
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
			    		url: "createFormulaParameterTotal",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			parameterCode: parameterCode,
			    			parameterValue: parameterValue,
			    			parameterName: parameterName,
			    			parameterTypeId: parameterTypeId,
			    			defaultValue: defaultValue,
			    			description: description,
			    			listProductStoreIds: listProductStoreIds,
			    		},
			    		success: function (res){
			    			if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
			    				if (res._ERROR_MESSAGE_.indexOf("OLBIUS_PARAMETER_CODE_EXISTED") > -1 || res._ERROR_MESSAGE_LIST_.indexOf("OLBIUS_PARAMETER_CODE_EXISTED") > -1){
			    					jOlbUtil.alert.error(uiLabelMap.BLParameterCodeExisted);
			    					return false;
			    				}
			    				jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
			    				return false;
			    			}
			    			$("#jqxgridFromulaParameter").jqxGrid('updatebounddata');
			    			$("#alterpopupWindowFormulaParameter").jqxWindow('close');
			    		}
			    	});
			    	Loading.hide('loadingMacro');
            	}, 500);
            }
		}]);
    };
    
    var updateFormulaParameter = function (parameterId, parameterCode, parameterName, parameterValue, parameterTypeId, defaultValue, description){
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
			    		url: "updateFormulaParameter",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			parameterId: parameterId,
			    			parameterCode: parameterCode,
			    			parameterValue: parameterValue,
			    			parameterName: parameterName,
			    			parameterTypeId: parameterTypeId,
			    			defaultValue: defaultValue,
			    			description: description,
			    		},
			    		success: function (res){
			    			if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
			    				if (res._ERROR_MESSAGE_.indexOf("OLBIUS_PARAMETER_CODE_EXISTED") > -1 || res._ERROR_MESSAGE_LIST_.indexOf("OLBIUS_PARAMETER_CODE_EXISTED") > -1){
			    					jOlbUtil.alert.error(uiLabelMap.BLParameterCodeExisted);
			    					return false;
			    				}
			    				jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
			    				return false;
			    			}
			    			$("#jqxgridFromulaParameter").jqxGrid('updatebounddata');
			    			$("#editpopupWindowFormulaParameter").jqxWindow('close');
			    		}
			    	});
			    	Loading.hide('loadingMacro');
            	}, 500);
            }
		}]);
    };
    
	return {
		init: init,
		getNewValidator: getNewValidator,
		getEditValidator: getEditValidator,
	}
}());