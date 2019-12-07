$(function(){
	ParamStore.init();
});
var ParamStore = (function() {
	var validatorNew;
	var validatorEdit;
	
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		$("#fromDateParamStore").jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false, theme: theme});
		$("#fromDateParamStore").jqxDateTimeInput('clear');
		$("#thruDateParamStore").jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false, theme: theme});
		$("#thruDateParamStore").jqxDateTimeInput('clear');
		
		$("#parameterValueStore").jqxInput({width: 295, theme: theme, height: 24});
		$("#parameterValueStoreEdit").jqxInput({width: 295, theme: theme, height: 24});
		
		$("#fromDateParamStoreEdit").jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false, theme: theme});
		$("#fromDateParamStoreEdit").jqxDateTimeInput('clear');
		$("#thruDateParamStoreEdit").jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false, theme: theme});
		$("#thruDateParamStoreEdit").jqxDateTimeInput('clear');
		
		jOlbUtil.windowPopup.create($("#alterpopupWindowParamStore"), {maxWidth: 1200, minWidth: 300, width: 650, minHeight: 200, height: 320, maxHeight: 1000, resizable: false, modalZIndex: 10000, isModal: true, autoOpen: false, cancelButton: $("#newParamStoreCancel"), modalOpacity: 0.7, theme:theme});
		jOlbUtil.windowPopup.create($("#editpopupWindowParamStore"), {maxWidth: 1200, minWidth: 300, width: 650, minHeight: 200, height: 320, maxHeight: 1000, resizable: false, modalZIndex: 10000, isModal: true, autoOpen: false, cancelButton: $("#editParamStoreCancel"), modalOpacity: 0.7, theme:theme});
		
		$("#productStoreParameterId").jqxDropDownButton({width: 300, height: 26}); 
		$('#productStoreParameterId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		$("#parameterStoreId").jqxDropDownButton({width: 300, height: 26}); 
		$('#parameterStoreId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		initParameterGrid();
		initProductStoreGrid();
		
		$("#contextMenuParamStore").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	};
	
	var initProductStoreGrid = function(){
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
	  	Grid.initGrid(config, datafield, columnlist, null, $("#jqxgridListStoreParameters"));
	};
	
	var initParameterGrid = function(){
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
					var data = $('#jqxgridListParameterStores').jqxGrid('getrowdata', row);
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
					var data = $('#jqxgridListParameterStores').jqxGrid('getrowdata', row);
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
		  	selectionmode: 'checkbox',
		  	pageable: true,
		  	sortable: true,
		  	filterable: true,	        
		  	editable: false,
	      	rowsheight: 26,
	      	rowdetails: false,
	      	useUrl: false,
	      	source: {pagesize: 5}
	  	};
	  	Grid.initGrid(configParam, datafieldParam, columnlistParam, null, $("#jqxgridListParameterStores"));
	  	var tmpS = $("#jqxgridListParameterStores").jqxGrid('source');
		tmpS._source.localdata = listParameterCustomizeData;
	    $("#jqxgridListParameterStores").jqxGrid('source', tmpS);
	    $("#jqxgridListParameterStores").jqxGrid('updatebounddata');
	};
	
	var initElementComplex = function() {
	};
	var initEvents = function() {
		
		$("#contextMenuParamStore").on('itemclick', function (event) {
			var data = $('#jqxgridParameterProductStore').jqxGrid('getRowData', $("#jqxgridParameterProductStore").jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			if (tmpStr == uiLabelMap.Edit){
				$("#parameterText").text(data.parameterName);
				$("#productStoreText").text(data.storeName);
				
				$("#parameterEditId").val(data.parameterId);
				$("#productStoreEditId").val(data.productStoreId);
				
				$("#parameterValueStoreEdit").jqxInput('val', data.parameterValue);
				
				$("#fromDateParamStoreEdit").jqxDateTimeInput('val', data.fromDate);
				$("#thruDateParamStoreEdit").jqxDateTimeInput('val', data.thruDate);
				
				$("#editpopupWindowParamStore").jqxWindow('open');
			} else if (tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgridParameterProductStore').jqxGrid('updatebounddata');
			} else if (tmpStr == uiLabelMap.Delete){
				var dateTmp = new Date(data.fromDate);
				deleteParameterProductStore(data.parameterId, data.productStoreId, dateTmp.getTime());
			}
		});
		
		$('#newParamStoreSave').on('click', function(event){
			var validate = getNewValidator();
			if (!validate) return;
			
			var value = $("#parameterValueStore").jqxInput('val');
			var listParameterIds = []; 
			var rows1 = $("#jqxgridListParameterStores").jqxGrid('getselectedrowindexes');
			if (rows1 && rows1.length >= 1){
	        	for (var i = 0; i < rows1.length; i ++){
	        		var data = $("#jqxgridListParameterStores").jqxGrid('getrowdata', rows1[i]);
	        		var map = {};
	        		map["parameterId"] = data.parameterId;
	        		listParameterIds.push(map);
	        	}
	        }
			var listProductStoreIds = []; 
			var rows2 = $("#jqxgridListStoreParameters").jqxGrid('getselectedrowindexes');
	        if (rows2 && rows2.length >= 1){
	        	for (var i = 0; i < rows2.length; i ++){
	        		var data = $("#jqxgridListStoreParameters").jqxGrid('getrowdata', rows2[i]);
	        		var map = {};
	        		map["productStoreId"] = data.productStoreId;
	        		listProductStoreIds.push(map);
	        	}
	        }
	        
	        var fromDateParamStore = null;
	        if ($("#fromDateParamStore").jqxDateTimeInput('getDate')){
	        	fromDateParamStore = $("#fromDateParamStore").jqxDateTimeInput('getDate').getTime();
	        }
	        var thruDateParamStore = null;
	        if ($("#thruDateParamStore").jqxDateTimeInput('getDate')){
	        	thruDateParamStore = $("#thruDateParamStore").jqxDateTimeInput('getDate').getTime();
	        }
	        
	        createParameterProductStore(JSON.stringify(listParameterIds), JSON.stringify(listProductStoreIds), value, fromDateParamStore, thruDateParamStore);
	        
		});
		
		$('#editParamStoreSave').on('click', function(event){
			var validate = getEditValidator();
			if (!validate) return;
			
			var parameterId = $("#parameterEditId").val();
			var productStoreId = $("#productStoreEditId").val();
			
			var fromDateParamStore = null;
	        if ($("#fromDateParamStoreEdit").jqxDateTimeInput('getDate')){
	        	fromDateParamStore = $("#fromDateParamStoreEdit").jqxDateTimeInput('getDate').getTime();
	        }
	        var thruDateParamStore = null;
	        if ($("#thruDateParamStoreEdit").jqxDateTimeInput('getDate')){
	        	thruDateParamStore = $("#thruDateParamStoreEdit").jqxDateTimeInput('getDate').getTime();
	        }
		    
	        var value =  $("#parameterValueStoreEdit").jqxInput('val');
	        updateParameterProductStore(parameterId, productStoreId, value, fromDateParamStore, thruDateParamStore);
		        
		});
		
		$("#jqxgridListStoreParameters").on('rowselect', function (event) {
	        var args = event.args;
	        var rows = $("#jqxgridListStoreParameters").jqxGrid('getselectedrowindexes');
	        var desc = null;
	        if (rows && rows.length == 1){
	        	var data = $("#jqxgridListStoreParameters").jqxGrid('getrowdata', rows[0]);
	        	desc = data.storeName;
	        } else if (rows.length > 1) {
	        	var data = $("#jqxgridListStoreParameters").jqxGrid('getrowdata', rows[0]);
	        	desc = data.storeName + ' ...(' +rows.length+ ' ' +uiLabelMap.BLProductStore+')';
	        } else {
	        	desc = uiLabelMap.PleaseSelectTitle;
			}
	        
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ desc+'</div>';
	        $('#productStoreParameterId').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$("#jqxgridListParameterStores").on('rowselect', function (event) {
	        var args = event.args;
	        var rows = $("#jqxgridListParameterStores").jqxGrid('getselectedrowindexes');
	        var desc = null;
	        if (rows && rows.length == 1){
	        	var data = $("#jqxgridListParameterStores").jqxGrid('getrowdata', rows[0]);
	        	desc = data.parameterName;
	        } else if (rows.length > 1) {
	        	var data = $("#jqxgridListParameterStores").jqxGrid('getrowdata', rows[0]);
	        	desc = data.parameterName + ' ...(' +rows.length+ ' ' +uiLabelMap.BLFormulaParameter+')';
	        } else {
	        	desc = uiLabelMap.PleaseSelectTitle;
			}
	        
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ desc+'</div>';
	        $('#parameterStoreId').jqxDropDownButton('setContent', dropDownContent);
	    });
	};
	var initValidateForm = function(){
		var extendRulesNewParamStore = [
				{input: '#productStoreParameterId', message: uiLabelMap.FieldRequired, action: 'blur', rule: 
				    function (input, commit) {
				    	var value = $(input).val();
				    	var rows = $("#jqxgridListStoreParameters").jqxGrid('getselectedrowindexes');
						if(rows.length <= 0){
							return false;
						}
						return true;
					}
				},
				{input: '#parameterStoreId', message: uiLabelMap.FieldRequired, action: 'blur', rule: 
				    function (input, commit) {
				    	var value = $(input).val();
				    	var rows = $("#jqxgridListParameterStores").jqxGrid('getselectedrowindexes');
				    	if(rows.length <= 0){
							return false;
						}
						return true;
					}
				},
          ];
   		var mapRulesNewParamStore = [
   	            {input: '#fromDateParamStore', type: 'validInputNotNull'},
   	            {input: '#parameterValueStore', type: 'validInputNotNull'},
   	            ];
   		validatorNew = new OlbValidator($('#formAddNewParamStore'), mapRulesNewParamStore, extendRulesNewParamStore, {position: 'right'});
   		
   		var extendRulesEditParamStore = [
		];
		var mapRulesEditParamStore = [
			{input: '#fromDateParamStoreEdit', type: 'validInputNotNull'},
			{input: '#parameterValueStoreEdit', type: 'validInputNotNull'},
		];
		validatorEdit = new OlbValidator($('#formEditParamStore'), mapRulesEditParamStore, extendRulesEditParamStore, {position: 'right'});
	};
	
	var createParameterProductStore = function (listParameterIds, listProductStoreIds, parameterValue, fromDate, thruDate){
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
			    		url: "createParameterProductStoreMulti",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			parameterValue: parameterValue,
			    			listParameterIds: listParameterIds,
			    			listProductStoreIds: listProductStoreIds,
			    			fromDate: fromDate,
			    			thruDate: thruDate,
			    		},
			    		success: function (res){
			    			if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
			    				jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
			    				return false;
			    			}
			    			$("#jqxgridParameterProductStore").jqxGrid('updatebounddata');
			    			$("#alterpopupWindowParamStore").jqxWindow('close');
			    		}
			    	});
			    	Loading.hide('loadingMacro');
            	}, 500);
            }
		}]);
	};
	
	var updateParameterProductStore = function (parameterId, productStoreId, parameterValue, fromDate, thruDate){
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
			    		url: "updateFormulaParameterProductStore",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			parameterValue: parameterValue,
			    			parameterId: parameterId,
			    			productStoreId: productStoreId,
			    			fromDate: fromDate,
			    			thruDate: thruDate,
			    		},
			    		success: function (res){
			    			if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
			    				jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
			    				return false;
			    			}
			    			$("#jqxgridParameterProductStore").jqxGrid('updatebounddata');
			    			$("#editpopupWindowParamStore").jqxWindow('close');
			    		}
			    	});
			    	Loading.hide('loadingMacro');
            	}, 500);
            }
		}]);
	};
	
	var deleteParameterProductStore = function (parameterId, productStoreId, fromDate){
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
			    		url: "deleteFormulaParameterProductStore",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			parameterId: parameterId,
			    			productStoreId: productStoreId,
			    			fromDate: fromDate,
			    		},
			    		success: function (res){
			    			if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
			    				jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
			    				return false;
			    			}
			    			$("#jqxgridParameterProductStore").jqxGrid('updatebounddata');
			    		}
			    	});
			    	Loading.hide('loadingMacro');
            	}, 500);
            }
		}]);
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
	}
}());