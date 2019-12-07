$(function(){
	ConfigLabelObj.init();
});
var ConfigLabelObj = (function() {
	var validatorAdd;
	var validatorEdit;
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		jOlbUtil.windowPopup.create($("#alterpopupWindow"), {maxWidth: 1200, minWidth: 300, width: 550, minHeight: 200, height: 250, maxHeight: 1000, resizable: false, modalZIndex: 10000, isModal: true, autoOpen: false, cancelButton: $("#newCancel"), modalOpacity: 0.7, theme:theme});
		jOlbUtil.windowPopup.create($("#editpopupWindow"), {maxWidth: 1200, minWidth: 300, width: 550, minHeight: 200, height: 250, maxHeight: 1000, resizable: false, modalZIndex: 10000, isModal: true, autoOpen: false, cancelButton: $("#editCancel"), modalOpacity: 0.7, theme:theme});
		$("#product").jqxDropDownButton({width: 300}); 
		$('#product').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');

		initGridProductIdList($("#productId"));
		initLabelList("#labelId", []);
		
		$("#contextMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$("#alterpopupWindow").on('close', function (){
			$("#productId").jqxGrid('clearSelection');
			$("#labelId").jqxComboBox('clearSelection');
		});
		
		$("#editpopupWindow").on('close', function (){
			$("#productIdEdit").jqxComboBox('clearSelection');
			$("#labelIdEdit").jqxComboBox('clearSelection');
		});
		
		$("#productId").on('rowselect', function (event) {
	        var args = event.args;
	        var rowData = args.row;
	        var rows = $("#productId").jqxGrid('selectedrowindexes');
	        var description = uiLabelMap.PleaseSelectTitle; 
			if (rows.length == 1) {
				description = rowData.productName + ' ['+rowData.productCode+']';
			} else if (rows.length > 1) {
				description = rowData.productName + ' ['+rowData.productCode+'], ... (' + rows.length + ' ' + uiLabelMap.Product +')';
			}
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
	        $('#product').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$("#productId").on('rowunselect', function (event) {
			var args = event.args;
			var rowData = args.row;
			var rows = $("#productId").jqxGrid('selectedrowindexes');
			var description = uiLabelMap.PleaseSelectTitle; 
			if (rows.length == 1) {
				description = rowData.productName + ' ['+rowData.productCode+']';
			} else if (rows.length > 1) {
				description = rowData.productName + ' ['+rowData.productCode+'], ... (' + rows.length + ' ' + uiLabelMap.Product +')';
			}
			var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
			$('#product').jqxDropDownButton('setContent', dropDownContent);
		});
		
		$("#newSave").click(function (){
			var resultValidate = validatorAdd.validate();
			if(!resultValidate) return false;
			var items = $("#productId").jqxGrid('selectedrowindexes'); 
			var listProductIds = [];
			if (items.length > 0){
				for (var i = 0; i < items.length; i ++){
					var data = $("#productId").jqxGrid('getrowdata', items[i]);
					var map = {
						productId: data.productId,
					}
					listProductIds.push(map);
				}
			} 
			var lables = $("#labelId").jqxComboBox('getSelectedItems'); 
			var listInventoryItemLabelIds = [];
			if (lables.length > 0){
				for (var i = 0; i < lables.length; i ++){
					var map = {
						inventoryItemLabelId: lables[i].originalItem.inventoryItemLabelId,
					}
					listInventoryItemLabelIds.push(map);
				}
			} 
			var list1 = JSON.stringify(listProductIds);
			var list2 = JSON.stringify(listInventoryItemLabelIds);
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
				    		url: "createConfigLabels",
				    		type: "POST",
				    		async: false,
				    		data: {
				    			listProductIds: list1,
				    			listInventoryItemLabelIds: list2,
				    		},
				    		success: function (res){
				    			$("#jqxgridConfigLabel").jqxGrid('updatebounddata'); 
				    			$("#alterpopupWindow").jqxWindow('close');
				    		}
				    	});
					Loading.hide('loadingMacro');
			    	}, 500);
			    }
			}]);
		});
		
		$("#editSave").click(function (){
			var resultValidate = validatorEdit.validate();
			if(!resultValidate) return false;
			
			var lables = $("#labelIdEdit").jqxComboBox('getSelectedItems'); 
			var listInventoryItemLabelIds = [];
			if (lables.length > 0){
				for (var i = 0; i < lables.length; i ++){
					var map = {
						inventoryItemLabelId: lables[i].originalItem.inventoryItemLabelId,
					}
					listInventoryItemLabelIds.push(map);
				}
				var list2 = JSON.stringify(listInventoryItemLabelIds);
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
					    		url: "updateConfigLabelByProduct",
					    		type: "POST",
					    		async: false,
					    		data: {
					    			productId: $("#editProductId").val(),
					    			listInventoryItemLabelIds: list2,
					    		},
					    		success: function (res){
					    			$("#jqxgridConfigLabel").jqxGrid('updatebounddata'); 
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
					    		url: "deleteAllConfigLabelByProduct",
					    		type: "POST",
					    		async: false,
					    		data: {
					    			productId: $("#editProductId").val(),
					    		},
					    		success: function (res){
					    			$("#jqxgridConfigLabel").jqxGrid('updatebounddata'); 
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
			var data = $('#jqxgridConfigLabel').jqxGrid('getRowData', $("#jqxgridConfigLabel").jqxGrid('selectedrowindexes'));
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
							$.ajax({
					    		url: "deleteConfigLabelByProduct",
					    		type: "POST",
					    		async: false,
					    		data: {
					    			productId: data.productId,
					    		},
					    		success: function (res){
					    			$("#jqxgridConfigLabel").jqxGrid('updatebounddata'); 
					    		}
					    	});
						Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);
			} else if (tmpStr == uiLabelMap.Edit){
				$("#productIdEdit").text(data.productCode);
				$("#editProductId").val(data.productId);
				var listInventoryItemLabelIds = [];
				$.ajax({
		    		url: "getInventoryItemLabelByProduct",
		    		type: "POST",
		    		async: false,
		    		data: {
		    			productId: data.productId,
		    		},
		    		success: function (res){
		    			listInventoryItemLabelIds = res.listInventoryItemLabelIds;
		    		}
		    	});
				initLabelList("#labelIdEdit", listInventoryItemLabelIds);
				$("#editpopupWindow").jqxWindow('open');
			} else if (tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgridConfigLabel').jqxGrid('updatebounddata');
			}
		});
	};
	var initValidateForm = function(){
		var extendRules1 = [
			{
				input: '#product', 
			    message: uiLabelMap.FieldRequired, 
			    action: 'blur', 
			    position: 'right',
			    rule: function (input) {
			    	var x = $("#productId").jqxGrid('getselectedrowindexes'); 
			    	if (x.length <= 0){
			    		return false;
			    	}
				   	return true;
			    }
			},
           ];
   		var mapRules1 = [
        	{input: '#labelId', type: 'validInputNotNull'},
           ];
   		validatorAdd = new OlbValidator($('#alterpopupWindow'), mapRules1, extendRules1, {position: 'right'});
   		
   		var extendRules2 = [
	                    ];
 		var mapRules2 = [
      	{input: '#labelIdEdit', type: 'validInputNotNull'},
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
			multiSelect: true,
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
	
	var initGridProductIdList = function(grid){	
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
				url: 'JQGetListProductByOrganiztion',                
				source: {pagesize: 10}
		};
		
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	return {
		init: init,
	}
}());