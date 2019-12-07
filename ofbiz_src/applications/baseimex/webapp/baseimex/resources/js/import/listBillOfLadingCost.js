$(document).ready(function() {
	ObjCost.init();
});
var ObjCost = (function() {
	var grid = $("#jqxGridBillCosts");
	var gridCostAccBase = $("#jqxGridCostAccBase");
	var popupWindowAddNewCost = $("#popupWindowAddNewCost");
	var jqxGridBOLCost = $("#jqxGridBOLCost");
	var validatorVAL = null;
	var errorMessage = null;
	var totalTax = 0;
	var key = uiLabelMap.Total;
	var invoiceTypeSelected = null;
	var init = function() { 
		initInput();
		initElementComplex(); 
		initEvents();
		initValidator();
	};
	
	var initInput = function() { 
		$("#billOfLading").jqxDropDownButton({width: 300, theme: theme});
		$('#billOfLading').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		if (billSelected != null){
			$('#billOfLading').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+billSelected.billNumber+'</div>');
			$("#billOfLading").jqxDropDownButton({disabled: true});
		}
		
		$("#costAccBase").jqxDropDownButton({width: 300, theme: theme});
		$('#costAccBase').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		$("#costPriceTemporary").jqxNumberInput({
			width : '300px',
			height : '25px',
			spinButtons : true,
			theme : theme
		});
		$("#costPriceActual").jqxNumberInput({
			width : '300px',
			height : '25px',
			spinButtons : true,
			theme : theme
		});
        $("#exchangedRate").jqxNumberInput({
            width : '300px',
            height : '25px',
            spinButtons : true,
            theme : theme
        });
		popupWindowAddNewCost.jqxWindow({
			maxWidth : 600,
			minWidth : 200,
			width : 630,
			minHeight : 100,
			height : 340,
			resizable : false,
			cancelButton : $("#alterCancelCost"),
			keyboardNavigation : true,
			keyboardCloseKey : 15,
			isModal : true,
			autoOpen : false,
			modalOpacity : 0.7,
			theme : theme
		});
		$('#jqxCostMenu').jqxMenu({ width: '300px', autoOpenPopup: false, mode: 'popup', theme: theme});
	}
	
	var productGridCellclass = function (row, column, value, data) {
		return 'background-prepare';
	}
	
	var initElementComplex = function() {
		initGridCost(grid);
		initGridCostAccBase(gridCostAccBase);
		if (!billSelected){
			initGridBillOfLading(jqxGridBOLCost);
		}
	}
	
	var initGridBillOfLading = function (grid) {
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				groupable: false, draggable: false, resizable: false,
				datafield: '', columntype: 'number', width: 50,
				cellsrenderer: function (row, column, value) {
					return '<div style=margin:4px;>' + (value + 1) + '</div>';
				}
			},		
			{ text: uiLabelMap.BIEBillId, dataField: 'billNumber', minwidth: 100, 
				cellsrenderer: function(row, column, value) {
				}
			},
			{ text: uiLabelMap.BIEDepartureDate, dataField: 'departureDate', editable: false, align: 'left', width: 140, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			},
			{ text: uiLabelMap.BIEArrivalDate, dataField: 'arrivalDate', editable: false, align: 'left', width: 140, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			},
			];
		
		var datafield = [
			{ name: 'billNumber', type: 'string'},
			{ name: 'billId', type: 'string'},
			{ name: 'departureDate', type: 'date', other: 'Timestamp'},
			{ name: 'arrivalDate', type: 'date', other: 'Timestamp'},
			]
		
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
				url: 'jqGetBillOfLading',                
				source: {pagesize: 15}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	}
	
	var initGridCost = function (grid) {
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.BIEBillId, dataField: 'billNumber', width: 150,
				cellsrenderer: function(row, column, value) {
				}
			},
			{ text: uiLabelMap.BACCInvoiceItemTypeId, dataField: 'description', minwidth: 120,
				aggregatesrenderer: function (aggregates, column, element) {
					var renderstring = "<div class='align-right margin-top10 margin-right5'><b>" +uiLabelMap.Total + "</b></div>";
					return renderstring;
				}
			},
			{ text: uiLabelMap.costPriceTemporary, dataField: 'costPriceTemporary', width: 200, filtertype: 'number', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					return '<span class="align-right">' + formatnumber(value)  + '</span>';
				},
				aggregates: ['sum'],
				aggregatesrenderer: function (aggregates, column, element) {
					var renderstring = "<div class='jqx-widget-content jqx-widget-content-" + theme + "' style='float: left; width: 100%; height: 100%; '>";
					$.each(aggregates, function (key, value) {
						var color = 'red';
						renderstring += '<div style="color: ' + color + '; position: relative; margin-top: 10px; margin-right: 2px; text-align: right; overflow: hidden;"><b>'+ formatcurrency(value) + '</b></div>';
					});
					renderstring += "</div>";
					return renderstring;
				}
			},
			{ text: uiLabelMap.costPriceActual, dataField: 'costPriceActual', width: 200, filtertype: 'number',
				cellsrenderer: function(row, column, value) {
					return '<span class="align-right">' + formatnumber(value) + '</span>';
				},
				aggregates: ['sum'],
				aggregatesrenderer: function (aggregates, column, element) {
					var renderstring = "<div class='jqx-widget-content jqx-widget-content-" + theme + "' style='float: left; width: 100%; height: 100%; '>";
					$.each(aggregates, function (key, value) {
						var color = 'red';
						renderstring += '<div style="color: ' + color + '; position: relative; margin-top: 10px; margin-right: 2px; text-align: right; overflow: hidden;"><b>'+ formatcurrency(value) + '</b></div>';
					});
					renderstring += "</div>";
					return renderstring;
				}
			},
        ];
		
		var datafield = [
			{ name: 'billId', type: 'string'},
			{ name: 'description', type: 'string'},
			{ name: 'billNumber', type: 'string'},
			{ name: 'costPriceTemporary', type: 'number'},
			{ name: 'costPriceActual', type: 'number'},
		 	]
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "Cost";
			var me = this;
			var jqxheader = $("<div id='toolbarcost" + id +"' class='widget-header'><h4>" + uiLabelMap.BillOfLadingBudget + "</h4><div id='toolbarButtonCost" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonCost' + id);
	        var maincontainer = $("#toolbarcost" + id);
	        var customcontrol1 = "icon-plus open-sans@" + uiLabelMap.AddNew + "@javascript:void(0)@ObjCost.openPopupAdd()";
	        Grid.createCustomControlButton(grid, container, customcontrol1);
		}; 
		
		var url = "jqGetBillCost";
		if (billId != null){
			url = "jqGetBillCost&billId=" + billId;
		}
		
		var config = {
			width: '100%', 
	   		virtualmode: true,
	   		showaggregates: true,
	   		showstatusbar: true,
	   		statusbarheight: 40,
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
	        url: url,                
	        source: {pagesize: 15}
	  	};
	  	Grid.initGrid(config, datafield, columns, null, grid);
		Grid.createContextMenu(grid, $("#jqxCostMenu"), false);
	}
	
	var initGridCostAccBase = function (grid) {
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				groupable: false, draggable: false, resizable: false,
				datafield: '', columntype: 'number', width: 50,
				cellsrenderer: function (row, column, value) {
					return '<div style=margin:4px;>' + (value + 1) + '</div>';
				}
			},		
			{ text: uiLabelMap.BACCInvoiceItemTypeId, dataField: 'description', minwidth: 120,},
			];
		
		var datafield = [
			{ name: 'costAccBaseId', type: 'string'},
			{ name: 'invoiceItemTypeId', type: 'string'},
			{ name: 'costAccBaseId', type: 'string'},
			{ name: 'description', type: 'string'},
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
				url: 'jqGetCostAccBase&costAccountingTypeId=COST_BILLOFLA',                
				source: {pagesize: 15}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var initEvents = function() {
		$("#jqxCostMenu").on('itemclick', function (event) {
			var liId = event.args.id;
			if (liId == "refreshCost"){
				grid.jqxGrid('updatebounddata');
			}
		});
		
		popupWindowAddNewCost.on('close', function (event) {
			$('#costPriceTemporary').jqxNumberInput('val', 0);
            $('#costPriceActual').jqxNumberInput('val', 0);
            $('#exchangedRateDiv').addClass('hide');
			gridCostAccBase.jqxGrid('clearSelection');
			$('#costAccBase').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		});
		
		gridCostAccBase.on('rowclick', function (event) {
            errorMessage = null;
			var args = event.args;
			var rowBoundIndex = args.rowindex;
            $("#costPriceTemporary").jqxNumberInput({ disabled: false });
            $('#costPriceTemporary').jqxNumberInput('val', 0);
            var rowData = gridCostAccBase.jqxGrid('getrowdata', rowBoundIndex);
			if(rowData.invoiceItemTypeId === 'PINV_IMPTAX_ITEM' || rowData.invoiceItemTypeId === 'PITM_SPEC_TAX') {
                $.ajax({
                    type: 'POST',
                    url: 'calculateTempTax',
                    data: {
                        costAccBaseId: rowData.costAccBaseId,
                        billId: billId
                    },
                    dataType: "json",
                    beforeSend: function(){
                        //$("#loader_page_common").show();
                    },
                    success: function(data){
                        if(data.errorMessage) {
                            errorMessage = data.errorMessage;
                            bootbox.dialog(data.errorMessage,
                                [
                                    {
                                        "label" : uiLabelMap.CommonClose,
                                        "class" : "btn-danger btn-small icon-remove open-sans",
                                    }]
                            );
                        } else {
                            totalTax = data.totalTax;
                            $('#costPriceTemporary').jqxNumberInput('val', data.totalTax);
                            $("#costPriceTemporary").jqxNumberInput({ disabled: true });
                            if(currencyUomId && currencyUomId !=='VND')
                                $("#exchangedRateDiv").removeClass("hide");
                        }
                    },
                    error: function(data){
                        console.log("Send request is error");
                    },
                    complete: function(data){
                        //$("#loader_page_common").hide();
                    }
                });
            }
			invoiceTypeSelected = $.extend({}, rowData);
			var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ rowData.description +'</div>';
			$('#costAccBase').jqxDropDownButton('setContent', dropDownContent);
			$("#costAccBase").jqxDropDownButton('close');
		});
		
		gridCostAccBase.on('bindingcomplete', function (event) {
			if (invoiceTypeSelected != null){
				var rows = gridCostAccBase.jqxGrid('getrows');
				if (rows != undefined && rows.length > 0) {
					for (var i in rows){
						if (rows[i].costAccBaseId == invoiceTypeSelected.costAccBaseId){
							var index = gridCostAccBase.jqxGrid('getrowboundindexbyid', rows[i].uid);
							gridCostAccBase.jqxGrid('selectrow', index);
							break;
						}
					}
				}
			}
		});

		$("#exchangedRate").on('valueChanged', function(event) {
		    console.log(event.args.value);
		    var value = event.args.value;
		    var total = totalTax * value;
            $('#costPriceTemporary').jqxNumberInput('val', total);
        });
		
		$("#alterSaveCost").on('click', function(){
			if (!validatorVAL.validate()){
				return false;
			}
			if(errorMessage !== null) {
                bootbox.dialog(errorMessage,
                    [
                        {
                            "label" : uiLabelMap.CommonClose,
                            "class" : "btn-danger btn-small icon-remove open-sans",
                        }]
                );
                return false;
            }
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
			    		var data = getData();
			    		$.ajax({
			    			url : "updateCostBillAcc",
			    			type : "POST",
			    			data : data,
			    			async : false,
			    			success : function(res) {
			    				if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
			    					if (res._ERROR_MESSAGE_){
			    						jOlbUtil.alert.error(res._ERROR_MESSAGE_);
			    					}
			    					if (res._ERROR_MESSAGE_LIST_){
			    						jOlbUtil.alert.error(res._ERROR_MESSAGE_LIST_[0]);
			    					}
			    					return false;
			    				} else {
			    					grid.jqxGrid('updatebounddata');
			    					popupWindowAddNewCost.jqxWindow('close');
			    				}
			    			}
			    		});
			    		
			    		Loading.hide('loadingMacro');
			    	}, 500);
	            }
	        }]);
		});
	};
	
	function getData() {
		var costPriceTemporary = $('#costPriceTemporary').jqxNumberInput('val');
		costPriceTemporary = costPriceTemporary.toString();
		var costPriceActual = $('#costPriceActual').jqxNumberInput('val');
		costPriceActual = costPriceActual.toString();
		var exchangedRate = $('#exchangedRate').jqxNumberInput('val');
        exchangedRate = exchangedRate.toString();
		var data = {
				billId: billSelected.billId,
				invoiceItemTypeId: invoiceTypeSelected.invoiceItemTypeId,
				costAccBaseId: invoiceTypeSelected.costAccBaseId,	
				costPriceTemporary: costPriceTemporary,
				costPriceActual: costPriceActual,
                exchangedRate: exchangedRate
		};
		return data;
	}
	
	var openPopupAdd = function() {
        errorMessage =  null;
		popupWindowAddNewCost.jqxWindow('open');
	}
	
	var initValidator = function() {
		var extendRules = [
			{
				input: '#billOfLading', 
			    message: uiLabelMap.FieldRequired, 
			    action: 'blur',
			    position: 'right',
			    rule: function (input) {
			    	if (billSelected == null){
			    		return false;
			    	}
				   	return true;
			    }
			},
			{
				input: '#costAccBase', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					if (invoiceTypeSelected == null){
						return false;
					}
					return true;
				}
			},
            {
                input: '#exchangedRate',
                message: uiLabelMap.FieldRequired,
                action: 'valueChanged',
                position: 'right',
                rule: function (input) {
                    var value = $(input).val();
                    //console.log(value);
                    if ($("#exchangedRateDiv").is(":visible") == false)
                    	return true;
                    if(!currencyUomId || currencyUomId == 'VND') return true;
                    return value > 0;
                }
            },
		];
   		var mapRules = [
			{input: '#totalNetWeight', type: 'validInputNotNull'},
			{input: '#totalGrossWeight', type: 'validInputNotNull'},
        ];
   		validatorVAL = new OlbValidator($("#popupWindowAddNewCost"), mapRules, extendRules, {position: 'right'});
	};
	
	return {
		init : init,
		openPopupAdd: openPopupAdd,
	}
}());