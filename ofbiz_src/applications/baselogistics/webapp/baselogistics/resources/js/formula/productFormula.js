$(function(){
	PrFormula.init();
});
var PrFormula = (function() {
	var validatorNew;
	var validatorEdit;
	
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		$("#fromDate").jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false, theme: theme});
		$("#fromDate").jqxDateTimeInput('clear');
		$("#thruDate").jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false, theme: theme});
		$("#thruDate").jqxDateTimeInput('clear');
		$("#fromDateEdit").jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false, theme: theme});
		$("#fromDateEdit").jqxDateTimeInput('clear');
		$("#thruDateEdit").jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false, theme: theme});
		$("#thruDateEdit").jqxDateTimeInput('clear');
		
		jOlbUtil.windowPopup.create($("#alterpopupWindow"), {maxWidth: 1200, minWidth: 300, width: 650, minHeight: 200, height: 295, maxHeight: 1000, resizable: false, modalZIndex: 10000, isModal: true, autoOpen: false, cancelButton: $("#newCancel"), modalOpacity: 0.7, theme:theme});
		jOlbUtil.windowPopup.create($("#editpopupWindow"), {maxWidth: 1200, minWidth: 300, width: 650, minHeight: 200, height: 295, maxHeight: 1000, resizable: false, modalZIndex: 10000, isModal: true, autoOpen: false, cancelButton: $("#editCancel"), modalOpacity: 0.7, theme:theme});
		
		$("#productId").jqxDropDownButton({width: 300}); 
		$('#productId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		$("#formulaId").jqxDropDownButton({width: 300}); 
		$('#formulaId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		initProductGrid();
		initFormulaGrid();
		
		$("#contextMenuProductFormula").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
		
	};
	
	var initFormulaGrid = function(facilityId){
		var url = null;
		if (formulaTypeId) {
			url = 'jQGetListFormulas&formulaTypeId=' + formulaTypeId;
		} else {
			url = 'jQGetListFormulas';
		}
		var datafield =  [
			{name: 'formulaId', type: 'string'},
			{name: 'formulaCode', type: 'string'},
			{name: 'formulaName', type: 'string'},
			{name: 'formulaValue', type: 'string'},	
			{name: 'description', type: 'string'},
      	];
      	var columnlist = [
              { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{text: uiLabelMap.BLFormulaId, datafield: 'formulaCode', width: '20%', },
				{text: uiLabelMap.BLFormulaName, datafield: 'formulaName', width: '20%', },
				{text: uiLabelMap.BLFormula, datafield: 'formulaValue', width: '50%',},
       		   	{text: uiLabelMap.Description, datafield: 'description', width: '30%',},
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
	        useUrl: true,
	        url: url,                
	        source: {pagesize: 10}
      	};
      	Grid.initGrid(config, datafield, columnlist, null, $("#jqxgridListFormula"));
	};
	var initProductGrid = function(facilityId){
		var datafield =  [
		                  {name: 'productId', type: 'string'},
		                  {name: 'productCode', type: 'string'},	
		                  {name: 'productCategoryId', type: 'string'},
		                  {name: 'productName', type: 'string'},
		                  ];
		var columnlist = [
		                  { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
		                	  groupable: false, draggable: false, resizable: false,
		                	  datafield: '', columntype: 'number', width: 50,
		                	  cellsrenderer: function (row, column, value) {
		                		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
		                	  }
		                  },
		                  {text: uiLabelMap.ProductId, datafield: 'productCode', width: '20%', },
		                  {text: uiLabelMap.ProductName, datafield: 'productName', width: '80%',},
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
				useUrl: true,
				url: 'JQGetListProductByOrganiztion',                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columnlist, null, $("#jqxgridListProduct"));
	};
	
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$("#contextMenuProductFormula").on('itemclick', function (event) {
			var data = $('#jqxgridFromularProduct').jqxGrid('getRowData', $("#jqxgridFromularProduct").jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			if (tmpStr == uiLabelMap.Edit){
				$("#productText").text(data.productName);
				$("#formulaText").text(data.formulaName);
				
				$("#editProductId").val(data.productId);
				$("#editFormulaId").val(data.formulaId);
				
				$("#fromDateEdit").jqxDateTimeInput('val', data.fromDate);
				$("#thruDateEdit").jqxDateTimeInput('val', data.thruDate);
				
				$("#editpopupWindow").jqxWindow('open');
				
			} else if (tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgridFromularProduct').jqxGrid('updatebounddata');
			} else if (tmpStr == uiLabelMap.Delete){
				var dateTmp = new Date(data.fromDate);
				deleteProductFormula(data.productId, data.formulaId, dateTmp.getTime());
			}
		});
		
		$('#newSave').on('click', function(event){
			var validate = getNewValidator();
			if (!validate) return;
			
			var listProductIds = []; 
			var rows1 = $("#jqxgridListProduct").jqxGrid('getselectedrowindexes');
			if (rows1 && rows1.length >= 1){
	        	for (var i = 0; i < rows1.length; i ++){
	        		var data = $("#jqxgridListProduct").jqxGrid('getrowdata', rows1[i]);
	        		var map = {};
	        		map["productId"] = data.productId;
	        		listProductIds.push(map);
	        	}
	        }
			var listFormulaIds = []; 
			var rows2 = $("#jqxgridListFormula").jqxGrid('getselectedrowindexes');
	        if (rows2 && rows2.length >= 1){
	        	for (var i = 0; i < rows2.length; i ++){
	        		var data = $("#jqxgridListFormula").jqxGrid('getrowdata', rows2[i]);
	        		var map = {};
	        		map["formulaId"] = data.formulaId;
	        		listFormulaIds.push(map);
	        	}
	        }
	        
	        var fromDateTmp = null;
	        if ($("#fromDate").jqxDateTimeInput('getDate')){
	        	fromDateTmp = $("#fromDate").jqxDateTimeInput('getDate').getTime();
	        }
	        var thruDateTmp = null;
	        if ($("#thruDate").jqxDateTimeInput('getDate')){
	        	thruDateTmp = $("#thruDate").jqxDateTimeInput('getDate').getTime();
	        }
	        
	        createProductFormula(JSON.stringify(listProductIds), JSON.stringify(listFormulaIds), fromDateTmp, thruDateTmp);
	        
		});
		
		$('#editSave').on('click', function(event){
			var validate = getEditValidator();
			if (!validate) return;
			
			var productId = $("#editProductId").val();
			var formulaId = $("#editFormulaId").val();
	        
	        var fromDateTmp = null;
	        if ($("#fromDateEdit").jqxDateTimeInput('getDate')){
	        	fromDateTmp = $("#fromDateEdit").jqxDateTimeInput('getDate').getTime();
	        }
	        var thruDateTmp = null;
	        if ($("#thruDateEdit").jqxDateTimeInput('getDate')){
	        	thruDateTmp = $("#thruDateEdit").jqxDateTimeInput('getDate').getTime();
	        }
	        updateProductFormula(productId, formulaId, fromDateTmp, thruDateTmp);
	        
		});
		
		$("#jqxgridListFormula").on('rowselect', function (event) {
	        var args = event.args;
	        var rows = $("#jqxgridListFormula").jqxGrid('getselectedrowindexes');
	        var desc = null;
	        if (rows && rows.length == 1){
	        	var data = $("#jqxgridListFormula").jqxGrid('getrowdata', rows[0]);
	        	desc = data.formulaName;
	        } else if (rows.length > 1) {
	        	var data = $("#jqxgridListFormula").jqxGrid('getrowdata', rows[0]);
	        	desc = data.formulaName + ' ...(' +rows.length+ ' ' +uiLabelMap.BLFormula+')';
	        } else {
				desc = uiLabelMap.PleaseSelectTitle;
			}
	        
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ desc+'</div>';
	        $('#formulaId').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$("#jqxgridListProduct").on('rowselect', function (event) {
	        var args = event.args;
	        var rows = $("#jqxgridListProduct").jqxGrid('getselectedrowindexes');
	        var desc = null;
	        if (rows && rows.length == 1){
	        	var data = $("#jqxgridListProduct").jqxGrid('getrowdata', rows[0]);
	        	desc = data.productName;
	        } else if (rows.length > 1) {
	        	var data = $("#jqxgridListProduct").jqxGrid('getrowdata', rows[0]);
	        	desc = data.productName + ' ...(' +rows.length+ ' ' +uiLabelMap.Product+')';
	        } else {
	        	desc = uiLabelMap.PleaseSelectTitle;
			}
	        
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ desc+'</div>';
	        $('#productId').jqxDropDownButton('setContent', dropDownContent);
	    });
	};
	var initValidateForm = function(){
		var extendRulesNew = [
				{
					input: '#productId', 
				    message: uiLabelMap.FieldRequired, 
				    action: 'blur', 
				    position: 'right',
				    rule: function (input) {
				    	var products = $("#jqxgridListProduct").jqxGrid('getselectedrowindexes'); 
				    	if (products.length <= 0){
				    		return false;
				    	}
					   	return true;
				    }
				},
				{
					input: '#formulaId', 
					message: uiLabelMap.FieldRequired, 
					action: 'blur', 
					position: 'right',
					rule: function (input) {
						var formulas = $("#jqxgridListFormula").jqxGrid('getselectedrowindexes'); 
						if (formulas.length <= 0){
							return false;
						}
						return true;
					}
				},
              ];
   		var mapRulesNew = [
   	            {input: '#fromDate', type: 'validInputNotNull'},
   	            ];
   		validatorNew = new OlbValidator($('#formAddNew'), mapRulesNew, extendRulesNew, {position: 'right'});
   		
   		var extendRulesEdit = [
		];
		var mapRulesEdit = [
			{input: '#fromDateEdit', type: 'validInputNotNull'},
		];
		validatorEdit = new OlbValidator($('#formEdit'), mapRulesEdit, extendRulesEdit, {position: 'right'});
	};
	
	var createProductFormula = function (listProductIds, listFormulaIds, fromDate, thruDate){
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
			    		url: "createFormulaProductMulti",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			listProductIds: listProductIds,
			    			listFormulaIds: listFormulaIds,
			    			fromDate: fromDate,
			    			thruDate: thruDate,
			    		},
			    		success: function (res){
			    			if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
			    				jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
			    				return false;
			    			}
			    			$("#jqxgridFromularProduct").jqxGrid('updatebounddata');
			    			$("#alterpopupWindow").jqxWindow('close');
			    		}
			    	});
			    	Loading.hide('loadingMacro');
            	}, 500);
            }
		}]);
	};
	
	var updateProductFormula = function (productId, formulaId, fromDate, thruDate){
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
			    		url: "updateFormulaProduct",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			productId: productId,
			    			formulaId: formulaId,
			    			fromDate: fromDate,
			    			thruDate: thruDate,
			    		},
			    		success: function (res){
			    			if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
			    				jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
			    				return false;
			    			}
			    			$("#jqxgridFromularProduct").jqxGrid('updatebounddata');
			    			$("#editpopupWindow").jqxWindow('close');
			    		}
			    	});
			    	Loading.hide('loadingMacro');
            	}, 500);
            }
		}]);
	};
	
	var deleteProductFormula = function (productId, formulaId, fromDate){
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
			    		url: "deleteFormulaProduct",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			productId: productId,
			    			formulaId: formulaId,
			    			fromDate: fromDate,
			    		},
			    		success: function (res){
			    			if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
			    				jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
			    				return false;
			    			}
			    			$("#jqxgridFromularProduct").jqxGrid('updatebounddata');
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