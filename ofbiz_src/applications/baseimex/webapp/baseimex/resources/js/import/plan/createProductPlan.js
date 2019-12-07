$(document).ready(function() {
	ObjPlan.init();
});
var ObjPlan = (function() {
	var grid = $("#jqxgirdProductPlan"); 
	var gridParty = $("#jqxGridListParty"); 
	var gridPeriod = $("#jqxGridTimePeriod"); 
	var validatorVAL = null;
	var init = function() { 
		initInput();
		initElementComplex(); 
		initEvents();
		initValidateForm();

	};
	
	var initInput = function() { 
		$("#party").jqxDropDownButton({width: 300, theme: theme, dropDownHorizontalAlignment: 'right'}); 
		$('#party').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$("#customTimePeriod").jqxDropDownButton({width: 300, theme: theme, dropDownHorizontalAlignment: 'right'}); 
		$('#customTimePeriod').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
//		$("#customTimePeriod").jqxDropDownList({ source: listPeriod, width: 300,  theme: theme, displayMember: 'periodName', valueMember: 'customTimePeriodId', disabled: false, placeHolder: uiLabelMap.PleaseSelectTitle});
		$("#currencyUomId").jqxDropDownList({ source: [], width: 300,  theme: theme, displayMember: 'uomId', valueMember: 'description', disabled: false, placeHolder: uiLabelMap.PleaseSelectTitle});
		
		$("#productPlanName").jqxInput({ width: 300, height: 25, theme: theme});
		$("#productPlanCode").jqxInput({ width: 300, height: 25, theme: theme});
		$("#description").jqxInput({ width: 300, height: 25, theme: theme});
	}
	
	var createProductPlan = function(){
		window.location.href = 'createImExPlan';
	}
	
	var initElementComplex = function() {
		initGridParty(gridParty);
		initGridTimePeriod(gridPeriod);
	}
	
	var initGridParty = function(grid){
		var datafield =  [
		                  { name: 'partyId', type: 'string'},
		                  { name: 'partyCode', type: 'string'},
		                  { name: 'groupName', type: 'string'},
		                  ];
		var columnlist = [
		                  { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
		                	  groupable: false, draggable: false, resizable: false,
		                	  datafield: '', columntype: 'number', width: 50,
		                	  cellsrenderer: function (row, column, value) {
		                		  return '<div style="cursor: pointer;">' + (value + 1) + '</div>';
		                	  }
		                  },
		                  { text: uiLabelMap.SupplierId, datafield: 'partyCode', align: 'left', width: 200, pinned: true, editable: false,
		                	  cellsrenderer: function (row, column, value) {
		                		  var data = grid.jqxGrid('getrowdata', row);
		                		  if (!value){
		                			  value = data.partyId
		                			  return '<div style="cursor: pointer;">' + value + '</div>';
		                		  }
		                		  return '<div style="cursor: pointer;">' + value + '</div>';
		                	  }
		                  },
		                  { text: uiLabelMap.SupplierName, datafield: 'groupName', align: 'left', minwidth: 150, pinned: true, editable: false,
		                	  cellsrenderer: function (row, column, value) {
		                		  return '<div style="cursor: pointer;">' + value + '</div>';
		                	  }
		                  },
		                  ];
		var config = {
				width: 500, 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'singlerow',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: false,
				rowsheight: 26,
				rowdetails: false,
				useUrl: true,
				url: 'jqGetListPartySupplier',                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initGridTimePeriod = function(grid){
		var datafield =  [
			{ name: 'customTimePeriodId', type: 'string'},
			{ name: 'periodName', type: 'string'},
			{ name: 'fromDate', type: 'date'},
			{ name: 'thruDate', type: 'date'},
			];
		var columnlist = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				groupable: false, draggable: false, resizable: false,
				datafield: '', columntype: 'number', width: 50,
				cellsrenderer: function (row, column, value) {
					return '<div style="cursor: pointer;">' + (value + 1) + '</div>';
				}
			},
			{ text: uiLabelMap.Year, datafield: 'periodName', align: 'left', minwidth: 100, pinned: true, editable: false,
				cellsrenderer: function (row, column, value) {
					return '<div style="cursor: pointer;">' + value + '</div>';
				}
			},
			{ text: uiLabelMap.FromDate, datafield: 'fromDate', align: 'left', width: 150, editable: false, cellsformat: 'dd/MM/yyyy', filtertype: 'range',
				cellsrenderer: function (row, column, value) {
				}
			},
			{ text: uiLabelMap.ThruDate, datafield: 'thruDate', align: 'left', width: 150, editable: false, cellsformat: 'dd/MM/yyyy', filtertype: 'range',
				cellsrenderer: function (row, column, value) {
				}
			},
			];
		var config = {
				width: 500, 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'singlerow',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: false,
				rowsheight: 26,
				rowdetails: false,
				useUrl: true,
				url: '',                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initEvents = function() {
		
//		$("#customTimePeriod").on('change', function (event) {
//			var item = $("#customTimePeriod").jqxDropDownList('getSelectedItem');
//			if (item != undefined){
//				if (item.originalItem != undefined){
//					customTimePeriod = $.extend({}, item.originalItem);
//				}
//			}
//		});
		$("#currencyUomId").on('change', function (event) {
			var item = $("#currencyUomId").jqxDropDownList('getSelectedItem');
			if (item != undefined){
				if (item.originalItem != undefined){
					currencyUom = $.extend({}, item.originalItem);
					updateGridPeriod();
				}
			}
		});
		 gridParty.on('rowclick', function (event) {
		        var args = event.args;
		        var rowBoundIndex = args.rowindex;
		        var rowData = gridParty.jqxGrid('getrowdata', rowBoundIndex);
		        if (rowData){
		        	partySelected = rowData;
			        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ rowData.partyCode + ' - ' + rowData.groupName +'</div>';
			        $('#party').jqxDropDownButton('setContent', dropDownContent);
			        gridParty.jqxGrid('selectrow', rowBoundIndex);
			        
			        updateCurrencyUom(partySelected.partyId);
			        $("#party").jqxDropDownButton('close');
		        }
		    });
		 
		 gridPeriod.on('rowclick', function (event) {
			 var args = event.args;
			 var rowBoundIndex = args.rowindex;
			 var rowData = gridPeriod.jqxGrid('getrowdata', rowBoundIndex);
			 if (rowData){
				 customTimePeriod = rowData;
				 var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ customTimePeriod.periodName +'</div>';
				 $('#customTimePeriod').jqxDropDownButton('setContent', dropDownContent);
				 gridPeriod.jqxGrid('selectrow', rowBoundIndex);
				 $("#customTimePeriod").jqxDropDownButton('close');
			 }
		 });
		 
		 $("#addSave").on('click', function (event) {
			var resultValidate = validatorVAL.validate();
			if(!resultValidate) return false;
			var item = $("#customTimePeriod").jqxDropDownList('getSelectedItem');
    		if(item != null){
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
			    			var valuePeriod = item.value;
			    			var productPlanName = $('#productPlanName').val();
			    			var currencyUomId = $('#currencyUomId').jqxDropDownList("val");
			    			
			    			$.ajax({
			    				url: "createProductPlan",
			    				type: "POST",
			    				data: {
			    					customTimePeriodId: valuePeriod, 
			    					supplierPartyId: partySelected.partyId, 
			    					currencyUomId: currencyUomId, 
			    					productPlanName: productPlanName, 
			    					productPlanTypeId: "IMPORT_PLAN", 
			    					statusId: "IMPORT_PLAN_CREATED"
	    						},
			    				dataType: "json",
			    				success: function(data) {
			    					if (data._ERROR_MESSAGE_ != undefined && data._ERROR_MESSAGE_ != null) {
										jOlbUtil.alert.error(uiLabelMap.UpdateError + ". " + data._ERROR_MESSAGE_);
										Loading.hide("loadingMacro");
										return false;
									}
			    					grid.jqxGrid('updatebounddata');
			    				}
			    			});
						Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);
    		}
		 });
	}
	
	var updateCurrencyUom = function(partyId) {
		$.ajax({
			url : "getSupplierCurrencyUom",
			type : "POST",
			data : {
				partyId : partyId,
			},
			dataType : "json",
			success : function(data) {
				
			}
		}).done(function(data) {
			var listCurrencyUoms = data.listCurrencyUoms;
			var currencyCombo = [];
			if (listCurrencyUoms != undefined && listCurrencyUoms.length > 0) {
				for (var i = 0; i < listCurrencyUoms.length; i ++) {
					var x = {};
					x.uomId = listCurrencyUoms[i].uomId;
					x.description = listCurrencyUoms[i].abbreviation;
					currencyCombo.push(x);
				}
			}
			$("#currencyUomId").jqxDropDownList({ source : currencyCombo, disabled : false });
			$("#currencyUomId").jqxDropDownList('selectIndex', 0);
			updateGridPeriod();
		});
	}
	
	var updateGridPeriod = function () {
		if (partySelected && currencyUom) {  
			var url = 'jqxGeneralServicer?sname=jqGetCustomTimePeriodImportPlan&periodTypeId=COMMERCIAL_YEAR&supplierPartyId='+partySelected.partyId+'&currencyUomId='+currencyUom.uomId;
			gridPeriod.jqxGrid("source")._source.url = url;
			gridPeriod.jqxGrid("updatebounddata");
		}
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ uiLabelMap.PleaseSelectTitle +'</div>';
		$('#customTimePeriod').jqxDropDownButton('setContent', dropDownContent);
		gridPeriod.jqxGrid('clearselection');
		customTimePeriod = {};
	}
	
	var initValidateForm = function(){
		var extendRules = [
			{
				input: '#party', 
			    message: uiLabelMap.FieldRequired, 
			    action: 'blur', 
			    position: 'right',
			    rule: function (input) {
			    	if (!partySelected){
			    		return false;
			    	}
				   	return true;
			    }
			},
			{
				input: '#customTimePeriod', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					if (!customTimePeriod){
						return false;
					}
					return true;
				}
			},
			{
				input: '#currencyUomId', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					var item = $("#currencyUomId").jqxDropDownList('getSelectedItem');
					if (!item){
						return false;
					}
					return true;
				}
			},
		];
   		var mapRules = [
   			{input: '#productPlanName', type: 'validInputNotNull'},
        ];
   		validatorVAL = new OlbValidator($("#AddPlan"), mapRules, extendRules, {position: 'right'});
	};
	
	var getValidator = function(){
    	return validatorVAL;
    };
    
    var viewDetailPlan = function (productPlanId){
    	window.location.href = 'listImExPlanItem?productPlanId=' + productPlanId;
    }
    
	return {
		init : init,
		createProductPlan:createProductPlan,
	}
}());