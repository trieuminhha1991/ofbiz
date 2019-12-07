$(document).ready(function() {
	ObjCapa.init();
});
var ObjCapa = (function() {
	var grid = $("#jqxGridConfigs"); 
	var gridProduct = $("#jqxGridProduct"); 
	var popupAdd = $("#alterpopupWindow"); 
	var popupEdit = $("#editPopupWindow"); 
	var validatorVAL = null;
	var uomFrom = null;
	var uomTo = null;
	var productSelected = null;
	var listUoms = [];
	var objEdit = null;
	var init = function() { 
		initInput();
		initElementComplex(); 
		initEvents();
		initValidateForm();

	};
	
	var initInput = function() { 
		
		$("#contextMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
		popupAdd.jqxWindow({
			maxWidth: 800, minWidth: 300, width: 540, height: 320, minHeight: 100, maxHeight: 656, resizable: false, isModal: true, modalZIndex: 10000, zIndex: 10000, autoOpen: false, cancelButton: $("#addCancel"), modalOpacity: 0.7, theme:theme           
		});
		popupEdit.jqxWindow({
			maxWidth: 800, minWidth: 300, width: 540, height: 320, minHeight: 100, maxHeight: 656, resizable: false, isModal: true, modalZIndex: 10000, zIndex: 10000, autoOpen: false, cancelButton: $("#editCancel"), modalOpacity: 0.7, theme:theme           
		});
		$("#product").jqxDropDownButton({width: 300, theme: theme}); 
		$('#product').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		var config = {filterable: true, checkboxes: false, displayMember: 'description', valueMember: 'uomFromId', 
				width: 300, height: 25, searchMode: 'contains', theme: theme, placeHolder: uiLabelMap.PleaseSelectTitle};
		
		var config2 = {filterable: true, checkboxes: false, displayMember: 'description', valueMember: 'uomToId', 
				width: 300, height: 25, searchMode: 'contains', theme: theme, placeHolder: uiLabelMap.PleaseSelectTitle};
		
		createJqxDropDownListExt($("#uomFromId"), shipmentPackingUom, config);
		createJqxDropDownListExt($("#uomToId"), [], config);
		
		$("#quantityConvert").jqxNumberInput({width : 300, height : '25px',	spinButtons : true,	theme : theme, decimalDigits: 0});
		$("#quantityConvertEdit").jqxNumberInput({width : 300, height : '25px',	spinButtons : true,	theme : theme, decimalDigits: 0});
	}
	
	var initElementComplex = function() {
		initGrid(grid);
		initGridProduct(gridProduct);
	}
	
	var initGrid = function (grid) {
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: '18%', editable: false, pinned: true,
			},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 100, editable:false,},
			{ text: uiLabelMap.uomFromId, dataField: 'uomFromId', width: '15%', editable:false,
				cellsrenderer: function (row, column, value) {
					if (value){
						return '<span>' +getUomDesc(value)+ '</span>';
					}
			        return '<span></span>';
			    }
			},
			{ text: uiLabelMap.uomToId, dataField: 'uomToId', width: '15%', editable:false,
				cellsrenderer: function (row, column, value) {
					if (value){
						return '<span>' +getUomDesc(value)+ '</span>';
					}
			        return '<span></span>';
			    }
			},
			{ text: uiLabelMap.ConversionFactor, datafield: 'quantityConvert', width: 200, filtertype:'number', align: 'center', cellsalign: 'right',
				cellsrenderer: function (row, column, value) {
					if (value){
						return '<span class="align-right">' +formatnumber(value)+ '</span>';
					}
			        return '<span>0</span>';
			    }
			},
        ];
		
		var datafield = [
         	{ name: 'productCode', type: 'string'},
         	{ name: 'productId', type: 'string'},
         	{ name: 'productName', type: 'string'},
         	{ name: 'uomFromId', type: 'string'},
         	{ name: 'uomToId', type: 'string'},
			{ name: 'quantityConvert', type: 'number'},
			{ name: 'fromDate', type: 'date', other: 'timestamp'},
			{ name: 'thruDate', type: 'date', other: 'timestamp'},
		 	]
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "PartyPeriod";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.ConfigCapacity + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        var customcontrol1 = "icon-plus open-sans@" + uiLabelMap.AddNew + "@javascript:void(0)@ObjCapa.openPopupAdd()";
	        Grid.createCustomControlButton(grid, container, customcontrol1);
		}; 
		
		var config = {
			width: '100%', 
	   		virtualmode: true,
	   		showtoolbar: true,
	   		rendertoolbar: rendertoolbar,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: false,
	        rowsheight: 26,
	        rowdetails: false,
	        useUrl: true,
	        url: 'jqGetConfigCapacitys',                
	        source: {pagesize: 15}
	  	};
	  	Grid.initGrid(config, datafield, columns, null, grid);
		Grid.createContextMenu(grid, $("#contextMenu"), false);
	}
	
	var initGridProduct = function(grid){
		var datafield =  [
		                  { name: 'productId', type: 'string'},
		                  { name: 'productCode', type: 'string'},
		                  { name: 'productName', type: 'string'},
		                  ];
		var columnlist = [
		                  { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
		                	  groupable: false, draggable: false, resizable: false,
		                	  datafield: '', columntype: 'number', width: 50,
		                	  cellsrenderer: function (row, column, value) {
		                		  return '<div style="cursor: pointer;">' + (value + 1) + '</div>';
		                	  }
		                  },
		                  { text: uiLabelMap.ProductId, datafield: 'productCode', align: 'left', width: 200, pinned: true, editable: false,
		                	  cellsrenderer: function (row, column, value) {
		                		  var data = grid.jqxGrid('getrowdata', row);
		                		  if (!value){
		                			  value = data.productId
		                			  return '<div style="cursor: pointer;">' + value + '</div>';
		                		  }
		                		  return '<div style="cursor: pointer;">' + value + '</div>';
		                	  }
		                  },
		                  { text: uiLabelMap.ProductName, datafield: 'productName', align: 'left', minwidth: 150, pinned: true, editable: false,
		                	  cellsrenderer: function (row, column, value) {
		                		  return '<div style="cursor: pointer;">' + value + '</div>';
		                	  }
		                  },
		                  ];
		var config = {
				width: '100%', 
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
				url: 'JQGetPOListProducts',                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initEvents = function() {
		
		$("#contextMenu").on('itemclick', function (event) {
			var data = grid.jqxGrid('getRowData', grid.jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			facilitySelectedId = data.facilityId;
			if(tmpStr == uiLabelMap.AddNew){
				openPopupAdd();
			}
			if(tmpStr == uiLabelMap.Edit){
				openPopupEdit(data);
			}
			if(tmpStr == uiLabelMap.Delete){
				var fromDate = new Date(data.fromDate);
				var x = fromDate.getTime();
				thruDateRelation(data.productId, data.uomFromId, data.uomToId, x);
			}
			if(tmpStr == uiLabelMap.BSRefresh){
				grid.jqxGrid('updatebounddata');
			}
		});
		
		 $("#uomFromId").on('change', function (event) {
			 uomFrom = {};
             if (event.args) {
                 var item = event.args.item;
                 if (item) {
                	 uomFrom = item.originalItem;
                 }
             }
         });
		 $("#uomToId").on('change', function (event) {
			 uomTo = {};
			 if (event.args) {
				 var item = event.args.item;
				 if (item) {
					 uomTo = item.originalItem;
				 }
			 }
		 });
		 
		 popupAdd.on('close', function (event) {
			 uomFrom = {};
			 uomTo = {};
			 productSelected = {};
			 gridProduct.jqxGrid('clearSelection');
			 $("#uomFromId").jqxDropDownList('clearSelection'); 
			 $("#uomFromId").jqxDropDownList('uncheckAll'); 
			 
			 $("#uomToId").jqxDropDownList('clearSelection'); 
			 $("#uomToId").jqxDropDownList('uncheckAll'); 
			 
			 $('#product').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
				
			 validatorVAL.hide();
			 $('#quantityConvert').jqxDateTimeInput('clear');
		 });
		 
		 gridProduct.on('rowclick', function (event) {
		        var args = event.args;
		        var rowBoundIndex = args.rowindex;
		        var rowData = gridProduct.jqxGrid('getrowdata', rowBoundIndex);
		        if (rowData){
		        	productSelected = {};
		        	productSelected = $.extend({}, rowData);
			        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ rowData.productCode + ' - ' + rowData.productName +'</div>';
			        $('#product').jqxDropDownButton('setContent', dropDownContent);
			        gridProduct.jqxGrid('selectrow', rowBoundIndex);
			        $("#product").jqxDropDownButton('close');
			        
			        updateUomByProduct(rowData.productId);
		        }
	    });
		 
		 $("#addSave").on('click', function (event) {
			var resultValidate = validatorVAL.validate();
			if(!resultValidate) return false;
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
				    		var convert = $('#quantityConvert').jqxNumberInput('val');
							var data = {
				    			productId: productSelected.productId,
				    			uomFromId: uomFrom.uomFromId,
				    			uomToId: uomTo.uomToId,
				    			quantityConvert: convert,
				    		};
							$.ajax({
					    		url: "createConfigPacking",
					    		type: "POST",
					    		async: false,
					    		data: data,
					    		success: function (res){
					    			popupAdd.jqxWindow('close');
					    			grid.jqxGrid('updatebounddata');
					    		}
					    	});
						Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);
		 });
		 
		 $("#editSave").on('click', function (event) {
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
					    		var convert = $('#quantityConvertEdit').jqxNumberInput('val');
					    		var x = new Date(objEdit.fromDate);
								var data = {
					    			productId: objEdit.productId,
					    			uomFromId: objEdit.uomFromId,
					    			uomToId: objEdit.uomToId,
					    			quantityConvert: convert,
					    			fromDate: x.getTime(),
					    		};
								$.ajax({
						    		url: "updateConfigPacking",
						    		type: "POST",
						    		async: false,
						    		data: data,
						    		success: function (res){
						    			popupEdit.jqxWindow('close');
						    			grid.jqxGrid('updatebounddata');
						    		}
						    	});
							Loading.hide('loadingMacro');
					    	}, 500);
					    }
					 }]);
		 });
	}
	
	var thruDateRelation = function (productId, uomFromId, uomToId, fromDate){
		var data = {
				productId: productId,
				uomFromId: uomFromId,
				uomToId: uomToId,
				fromDate: fromDate,
		};
		
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
			    		url: "deleteConfigPacking",
			    		type: "POST",
			    		async: false,
			    		data: data,
			    		success: function (res){
			    			grid.jqxGrid('updatebounddata');
			    		}
			    	});
					Loading.hide('loadingMacro');
		    	}, 500);
		    }
		}]);
	}
	
	var initValidateForm = function(){
		var extendRules = [
			{
				input: '#product', 
			    message: uiLabelMap.FieldRequired, 
			    action: 'blur', 
			    position: 'right',
			    rule: function (input) {
			    	if (!productSelected){
			    		return false;
			    	}
				   	return true;
			    }
			},
			{
				input: '#uomFromId', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					if (!uomFrom){
						return false;
					}
					return true;
				}
			},
			{
				input: '#uomToId', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					if (!uomTo){
						return false;
					}
					return true;
				}
			},
			{
				input: '#quantityConvert', 
				message: uiLabelMap.BIENeedGreaterThanZero, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					var x = $("#quantityConvert").jqxNumberInput('val');
					if (x <= 0){
						return false;
					}
					return true;
				}
			},
		];
   		var mapRules = [
			{input: '#quantityConvert', type: 'validInputNotNull'},
        ];
   		validatorVAL = new OlbValidator(popupAdd, mapRules, extendRules, {position: 'right'});
	};
	
	var getValidator = function(){
    	return validatorVAL;
    };
    
    function createJqxDropDownListExt(elemenDiv, sourceArr, config){
    	var source = {
    			localdata: sourceArr,
    	        datatype: "array"	
    	};
    	var dataAdapter = new $.jqx.dataAdapter(source);
    	
    	config.source = dataAdapter;
    	elemenDiv.jqxDropDownList(config);
    	if(typeof renderer != "undefined"){
    		elemenDiv.jqxDropDownList({renderer: renderer});
    	};
    	
    	if(sourceArr.length < 8){
    		elemenDiv.jqxDropDownList({autoDropDownHeight: true});
    	}
    }
    
    function updateUomByProduct(productId){
    	listUoms = [];
    	$.ajax({
    		url: "getProductPackingUomWithConvertNumbers",
    		type: "POST",
    		async: false,
    		data: {
    			productId: productId,
    		},
    		success: function (res){
    			if (res.listUoms){
    				listUoms = res.listUoms;
    				var list = [];
    				for (var i in listUoms){
    					var map = {
    							uomToId: listUoms[i].quantityUomId,
    					}
    					for (var j in uomData){
    						if (listUoms[i].quantityUomId == uomData[j].uomId){
    							map.description = uomData[j].description;
    							break;
    						}
    					}
    					list.push(map);
    				}
    				$("#uomToId").jqxDropDownList({source: list});
    			}
    		}
    	});
    }
    
    var openPopupAdd = function (){
    	popupAdd.jqxWindow('open');
    }
    var openPopupEdit = function (data){
    	objEdit = {};
    	objEdit = $.extend({}, data);
    	$("#productDT").text(data.productCode + " - " + data.productName);
    	$("#uomFromIdDT").text(getUomDesc(data.uomFromId));
    	$("#uomToIdDT").text(getUomDesc(data.uomToId));
    	$("#quantityConvertEdit").jqxNumberInput('val', data.quantityConvert);
    	popupEdit.jqxWindow('open');
    }
    
	return {
		init : init,
		openPopupAdd: openPopupAdd,
	}
}());