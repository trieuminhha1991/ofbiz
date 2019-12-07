$(function(){
	PrRelationshipObj.init();
});
var PrRelationshipObj = (function() {
	var validatorAdd;
	var validatorEdit;
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		jOlbUtil.windowPopup.create($("#alterpopupWindow"), {maxWidth: 1200, minWidth: 300, width: 1100, minHeight: 200, height: 250, maxHeight: 1000, resizable: false, modalZIndex: 10000, isModal: true, autoOpen: false, cancelButton: $("#newCancel"), modalOpacity: 0.7, theme:theme});
		jOlbUtil.windowPopup.create($("#editpopupWindow"), {maxWidth: 1200, minWidth: 300, width: 1100, minHeight: 200, height: 250, maxHeight: 1000, resizable: false, modalZIndex: 10000, isModal: true, autoOpen: false, cancelButton: $("#editCancel"), modalOpacity: 0.7, theme:theme});
		
		$("#productFrom").jqxDropDownButton({width: 300}); 
		$('#productFrom').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');

		$("#productTo").jqxDropDownButton({width: 300}); 
		$('#productTo').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		initGridProductIdFromList($('#productIdFrom'));
		initGridProductIdToList($('#productIdTo'));
		
		initLabelList("#inventoryItemLabelIdFrom", []);
		initLabelList("#inventoryItemLabelIdTo", []);
		
		initLabelList("#inventoryItemLabelIdFromEdit", []);
		initLabelList("#inventoryItemLabelIdToEdit", []);
		
		$("#contextMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$("#alterpopupWindow").on('close', function (){
			$("#productIdFrom").jqxGrid('clearSelection');
			$("#inventoryItemLabelIdFrom").jqxComboBox('clearSelection');
			$("#productIdTo").jqxGrid('clearSelection');
			$("#inventoryItemLabelIdTo").jqxComboBox('clearSelection');
		});
		
		$("#editpopupWindow").on('close', function (){
			$("#inventoryItemLabelIdFromEdit").jqxComboBox('clearSelection');
			$("#inventoryItemLabelIdToEdit").jqxComboBox('clearSelection');
		});
		
		$("#productIdFrom").on('rowselect', function (event) {
	        var args = event.args;
	        var rowData = args.row;
	        var rows = $("#productIdFrom").jqxGrid('selectedrowindexes');
	        var description = null; 
	        if (rows.length <= 1) {
	        	description = rowData.productName + ' ['+rowData.productCode+']';
	        } else {
	        	description = rowData.productName + ' ['+rowData.productCode+'], ... (' + rows.length + ' ' + uiLabelMap.Product +')';
	        }
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
	        $('#productFrom').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$("#productIdTo").on('rowselect', function (event) {
	        var args = event.args;
	        var rowData = args.row;
	        var rows = $("#productIdFrom").jqxGrid('selectedrowindexes');
	        var description = null; 
	        if (rows.length <= 1) {
	        	description = rowData.productName + ' ['+rowData.productCode+']';
	        } else {
	        	description = rowData.productName + ' ['+rowData.productCode+'], ... (' + rows.length + ' ' + uiLabelMap.Product +')';
	        }
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
	        $('#productTo').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$("#productIdFrom").on('rowunselect', function (event) {
			var args = event.args;
			var rowData = args.row;
			var rows = $("#productIdFrom").jqxGrid('selectedrowindexes');
			var description = uiLabelMap.PleaseSelectTitle; 
			if (rows.length == 1) {
				description = rowData.productName + ' ['+rowData.productCode+']';
			} else if (rows.length > 1) {
				description = rowData.productName + ' ['+rowData.productCode+'], ... (' + rows.length + ' ' + uiLabelMap.Product +')';
			}
			var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
			$('#productFrom').jqxDropDownButton('setContent', dropDownContent);
		});
		
		$("#productIdTo").on('rowunselect', function (event) {
			var args = event.args;
			var rowData = args.row;
			var rows = $("#productIdFrom").jqxGrid('selectedrowindexes');
			var description = uiLabelMap.PleaseSelectTitle; 
			if (rows.length == 1) {
				description = rowData.productName + ' ['+rowData.productCode+']';
			} else if (rows.length > 1) {
				description = rowData.productName + ' ['+rowData.productCode+'], ... (' + rows.length + ' ' + uiLabelMap.Product +')';
			}
			var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
			$('#productTo').jqxDropDownButton('setContent', dropDownContent);
		});
		
		$("#newSave").click(function (){
			var resultValidate = !validatorAdd.validate();
			if(resultValidate) return false;
			
			var prFroms = $("#productIdFrom").jqxGrid('selectedrowindexes');
			var listProductIdFroms = [];
			if (prFroms.length > 0){
				for (var i = 0; i < prFroms.length; i ++){
					var data = $("#productIdFrom").jqxGrid('getrowdata', prFroms[i]);
					var map = {
						productId: data.productId,
					}
					listProductIdFroms.push(map);
				}
			} 
			
			var prTos = $("#productIdTo").jqxGrid('selectedrowindexes');
			var listProductIdTos = [];
			if (prTos.length > 0){
				for (var i = 0; i < prTos.length; i ++){
					var data = $("#productIdTo").jqxGrid('getrowdata', prTos[i]);
					var map = {
						productId: data.productId,
					}
					listProductIdTos.push(map);
				}
			}
			
			var lableFroms = $("#inventoryItemLabelIdFrom").jqxComboBox('getSelectedItem'); 
			var listInventoryItemLabelIdFroms = [];
			if (lableFroms != null && lableFroms != undefined){
				var map = {
					inventoryItemLabelId: lableFroms.originalItem.inventoryItemLabelId,
				}
				listInventoryItemLabelIdFroms.push(map);
			} 
			
			var lableTos = $("#inventoryItemLabelIdTo").jqxComboBox('getSelectedItem'); 
			var listInventoryItemLabelIdTos = [];
			if (lableTos != null && lableTos != undefined){
				var map = {
					inventoryItemLabelId: lableTos.originalItem.inventoryItemLabelId,
				}
				listInventoryItemLabelIdTos.push(map);
			} 
			
			var list1 = JSON.stringify(listProductIdFroms);
			var list2 = JSON.stringify(listInventoryItemLabelIdFroms);
			var list3 = JSON.stringify(listProductIdTos);
			var list4 = JSON.stringify(listInventoryItemLabelIdTos);
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
				    		url: "createProductRelationships",
				    		type: "POST",
				    		async: false,
				    		data: {
				    			listProductIdFroms: list1,
				    			listInventoryItemLabelIdFroms: list2,
				    			listProductIdTos: list3,
				    			listInventoryItemLabelIdTos: list4,
				    		},
				    		success: function (res){
				    			$("#jqxgridProductRelationship").jqxGrid('updatebounddata'); 
				    			$("#alterpopupWindow").jqxWindow('close');
				    		}
				    	});
					Loading.hide('loadingMacro');
			    	}, 500);
			    }
			}]);
		});
		
		$("#editSave").click(function (){
			var resultValidate = !validatorEdit.validate();
			if(resultValidate) return false;
			
			var lableFrom = $("#inventoryItemLabelIdFromEdit").jqxComboBox('getSelectedItem'); 
			var lableTo = $("#inventoryItemLabelIdToEdit").jqxComboBox('getSelectedItem'); 
			var listInventoryItemLabelIdFroms = [];
			var listInventoryItemLabelIdTos = [];
			if (lableFrom != null && lableFrom != undefined && lableTo != null && lableTo != undefined){
				var map1 = {
					inventoryItemLabelId: lableFrom.originalItem.inventoryItemLabelId,
				}
				listInventoryItemLabelIdFroms.push(map1);
				var list1 = JSON.stringify(listInventoryItemLabelIdFroms);
				
				var map2 = {
					inventoryItemLabelId: lableTo.originalItem.inventoryItemLabelId,
				}
				listInventoryItemLabelIdTos.push(map2);
				var list2 = JSON.stringify(listInventoryItemLabelIdTos);
				
				bootbox.dialog(uiLabelMap.AreYouSureSave, 
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
					    		url: "updateProductRelationship",
					    		type: "POST",
					    		async: false,
					    		data: {
					    			productIdFrom: $("#inputProductIdFromEdit").val(),
					    			productIdTo: $("#inputProductIdToEdit").val(),
					    			listInventoryItemLabelIdFroms: list1,
					    			listInventoryItemLabelIdTos: list2,
					    		},
					    		success: function (res){
					    			$("#jqxgridProductRelationship").jqxGrid('updatebounddata'); 
					    			$("#editpopupWindow").jqxWindow('close');
					    		}
					    	});
						Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);
			} else {
				bootbox.dialog(uiLabelMap.AreYouSureSave, 
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
					    		url: "deleteProductRelationship",
					    		type: "POST",
					    		async: false,
					    		data: {
					    			productIdFrom: $("#inputProductIdFromEdit").val(),
					    			productIdTo: $("#inputProductIdToEdit").val(),
					    			inventoryItemLabelFrom: $("#inputLabelFromEdit").val(),
					    			inventoryItemLabelTo: $("#inputLabelToEdit").val(),
					    		},
					    		success: function (res){
					    			$("#jqxgridProductRelationship").jqxGrid('updatebounddata'); 
					    			$("#editpopupWindow").jqxWindow('close');
					    		}
					    	});
						Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);
			}
		});
		
		$("#contextMenu").on('itemclick', function (event) {
			var data = $('#jqxgridProductRelationship').jqxGrid('getRowData', $("#jqxgridProductRelationship").jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			if (tmpStr == uiLabelMap.CommonDelete){
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
				    		var tmp = new Date(data.fromDate);
							$.ajax({
					    		url: "deleteProductRelationship",
					    		type: "POST",
					    		async: false,
					    		data: {
					    			productIdFrom: data.productIdFrom,
					    			inventoryItemLabelIdFrom: data.inventoryItemLabelIdFrom,
					    			productIdTo: data.productIdTo,
					    			inventoryItemLabelIdTo: data.inventoryItemLabelIdTo,
					    			fromDate: tmp.getTime(),
					    		},
					    		success: function (res){
					    			$("#jqxgridProductRelationship").jqxGrid('updatebounddata'); 
					    		}
					    	});
						Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);
			} else if (tmpStr == uiLabelMap.Edit){
				$("#productIdFromEdit").text('');
				$("#productIdFromEdit").text(data.productIdFrom);
				$("#inputProductIdFromEdit").val(data.productIdFrom);
				$("#productIdToEdit").text('');
				$("#productIdToEdit").text(data.productIdTo);
				$("#inputProductIdToEdit").val(data.productIdTo);
				var inventoryItemLabelIdFrom = data.inventoryItemLabelIdFrom;
				var inventoryItemLabelIdTo = data.inventoryItemLabelIdTo;
				
				$("#inputLabelFromEdit").val(data.inventoryItemLabelIdFrom);
				$("#inputLabelToEdit").val(data.inventoryItemLabelIdTo);
				
				$("#inventoryItemLabelIdFromEdit").jqxComboBox('val', inventoryItemLabelIdFrom); 
				$("#inventoryItemLabelIdToEdit").jqxComboBox('val', inventoryItemLabelIdTo); 
				
				$("#editpopupWindow").jqxWindow('open');
			} else if (tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgridProductRelationship').jqxGrid('updatebounddata');
			}
		});
	};
	var initValidateForm = function(){
		var extendRules1 = [
			{
				input: '#productFrom', 
			    message: uiLabelMap.FieldRequired, 
			    action: 'blur', 
			    position: 'right',
			    rule: function (input) {
			    	var x = $("#productIdFrom").jqxGrid('getselectedrowindexes'); 
			    	if (x.length <= 0){
			    		return false;
			    	}
				   	return true;
			    }
			},
			{
				input: '#productTo', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					var x = $("#productIdTo").jqxGrid('getselectedrowindexes'); 
					if (x.length <= 0){
						return false;
					}
					return true;
				}
			},
           ];
   		var mapRules1 = [
			{input: '#inventoryItemLabelIdFrom', type: 'validInputNotNull'},
			{input: '#inventoryItemLabelIdTo', type: 'validInputNotNull'},
		];
   		validatorAdd = new OlbValidator($('#alterpopupWindow'), mapRules1, extendRules1, {position: 'right'});
   		
   		var extendRules2 = [
             ];
     		var mapRules2 = [
  			{input: '#inventoryItemLabelIdFromEdit', type: 'validInputNotNull'},
  			{input: '#inventoryItemLabelIdToEdit', type: 'validInputNotNull'},
  		];
 		validatorEdit = new OlbValidator($('#editpopupWindow'), mapRules2, extendRules2, {position: 'right'});
	};
	
	var initLabelList = function(comboBox, selectArr){
		var configLabelList = {
			placeHolder: uiLabelMap.BSClickToChoose,
			key: "inventoryItemLabelId",
    		value: "description",
			width:'100%',
			height: 25,
    		displayDetail: true,
			dropDownWidth: 'auto',
			multiSelect: false,
			autoComplete: true,
			searchMode: 'containsignorecase',
			renderer : null,
			renderSelectedItem : null,
			useUrl: true,
			url: 'jqxGeneralServicer?sname=jqGetInventoryItemLabels',
		};
		new OlbComboBox($(comboBox), null, configLabelList, selectArr);
	};
	
	var getProductDataFields = function(){
		var datafield = [{ name: 'productId', type: 'string' },
						{ name: 'productCode', type: 'string' },
						{ name: 'productName', type: 'string' },
						];
		return datafield;
	};
	
	var getProductColumnLists = function(){
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 150, editable: false, pinned: true,},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 150, editable:false,},
        ];
		return columns;
	};
	
	var initGridProductIdFromList = function(grid){	
		var datafield = getProductDataFields();
		var columns = getProductColumnLists();
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
				url: 'JQGetListProductByOrganiztion&hasVirtualProd=N',                
				source: {pagesize: 10}
		};
		
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initGridProductIdToList = function(grid){	
		var datafield = getProductDataFields();
		var columns = getProductColumnLists();
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
				url: 'JQGetListProductByOrganiztion&hasVirtualProd=N',                
				source: {pagesize: 10}
		};
		
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	return {
		init: init,
	}
}());