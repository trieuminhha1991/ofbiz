var openingBalanceObj = (function(){
	var init = function(){
		initTreeGrid();
		initNotification();
	};
	var initTreeGrid = function(){
		var grid = $("#glAccountBalanceTree");
		var datafield = [{name: 'glAccountId', type: 'string'},
		                 {name: 'parentGlAccountId', type: 'string'},
		                 {name: 'accountCode', type: 'string'},
		                 {name: 'accountName', type: 'string'},
		                 {name: 'openingDrBalance', type: 'number'},
		                 {name: 'openingCrBalance', type: 'number'}
		                 ];
		var columns = [{text: uiLabelMap.BACCAccountNumber, dataField: 'accountCode', width: '20%'},
		               {text: uiLabelMap.BACCAccountName, dataField: 'accountName', width: '28%'},
		               {text: uiLabelMap.BACCOpeningDrBalance, dataField: 'openingDrBalance', width: '26%',
		            	   aggregates: ['sum'],
		            	   aggregatesRenderer: function (aggregatesText, column, element, aggregates, type) {
		            		   var renderstring = "";
		            		   $.each(aggregates, function (key, value) {
		            			   renderstring += '<div style="font-size: 15px; padding: 8px; text-align: right">' + uiLabelMap.BACCAmountTotal + ': ' + formatcurrency(value) + '</div>';
		            		   });
		            		   return renderstring;
		            	   },
		            	   cellsRenderer: function (row, column, value, rowData) {
		            		   if(typeof(value) == "number"){
		            			   return '<div style="text-align: right">' + formatcurrency(value) + '</div>';
		            		   }
		            	   }
		               },	
		               {text: uiLabelMap.BACCOpeningCrBalance, dataField: 'openingCrBalance', width: '26%',
		            	   aggregates: ['sum'],
		            	   aggregatesRenderer: function (aggregatesText, column, element, aggregates, type) {
		            		   var renderstring = "";
		            		   $.each(aggregates, function (key, value) {
		            			   renderstring += '<div style="font-size: 15px; padding: 8px; text-align: right">' + uiLabelMap.BACCAmountTotal + ': ' + formatcurrency(value) + '</div>';
		            		   });
        		              return renderstring;
		            	   },
		            	   cellsRenderer: function (row, column, value, rowData) {
		            		   if(typeof(value) == "number"){
		            			   return '<div style="text-align: right">' + formatcurrency(value) + '</div>';
		            		   }
		            	   }
		               },	
					  ];
		var source = {
				dataType: "json",
				dataFields: datafield,
				hierarchy:
                {
                    keyDataField: { name: 'glAccountId' },
                    parentDataField: { name: 'parentGlAccountId' }
                },
                id: 'glAccountId',
                type: 'POST',
                root: 'listReturn',
                url: 'getListGlAccountBalance'
		};
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "glAccountBalanceTree";
			var jqxheaderStr = "<div id='toolbarcontainer" + id + "' class='widget-header'><h4>" 
								+ uiLabelMap.BACCBalanceAccount + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>";
			var jqxheader = $(jqxheaderStr);
			toolbar.append(jqxheader);
			var container = $("#toolbarButtonContainer" + id);
			
			var customcontrol1 = "icon-edit open-sans@" + uiLabelMap.CommonUpdate + "@javascript:void(0)@updateOpeningBalanceObj.openEditWindow()";
			Grid.createCustomControlButton(grid, container, customcontrol1);
			
			var buttonContainer = $('<div class="custom-control-toolbar"></div>');
			var buttonExpend = $('<a id="btnExpend' + id + '" style="color:#438eb9;" href="javascript:void(0);"><i class="fa fa-expand"></i></a>');
			var buttonCollapse = $('<a id="btnCollapse' + id + '" style="color:#438eb9;" href="javascript:void(0);"><i class="fa fa-compress"></i></a>');
			buttonContainer.append(buttonCollapse);
			buttonContainer.append(buttonExpend);
			$(container).append(buttonContainer);
			
			$("#btnExpend" + id).click(function(){
				grid.jqxTreeGrid('expandAll', true);
			});
			$("#btnCollapse" + id).click(function(){
				grid.jqxTreeGrid('collapseAll', true);
			});
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		grid.jqxTreeGrid(
        {
            width: '100%',
            height: 510,
            source: dataAdapter,
            showAggregates: true,
            aggregatesHeight: 50,
            sortable: false, 
            ready: function(){
            	grid.jqxTreeGrid('expandAll');
            },
            theme: 'olbius',
            columns: columns,
            localization: getLocalization(),
            showToolbar: true,
        	rendertoolbar: rendertoolbar
        });
	};
	
	var initNotification = function(){
		$("#jqxNotificationjqxgrid").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#containerjqxgrid"});
	};
	return{
		init: init,
	}
}());

var updateOpeningBalanceObj = (function(){
	var init = function(){
		initGrid();
		initWindow();
		initEvent();
	};
	var initGrid = function(){
		var grid = $("#updateGlAccountBalGrid");
		var cellDeditClassname = function (row, column, value, data) {
			if (data.accountType == 'CREDIT'){
				return "disableCellEditor";
			}
		};
		var cellCreditClassname = function (row, column, value, data) {
			if (data.accountType == 'DEBIT'){
				return "disableCellEditor";
			}
		};
		var datafield = [{name: 'glAccountId', type: 'string'},
		                 {name: 'accountCode', type: 'string'},
		                 {name: 'accountType', type: 'string'},
		                 {name: 'accountName', type: 'string'},
		                 {name: 'openingDrBalance', type: 'number'},
		                 {name: 'openingCrBalance', type: 'number'}];
		
		var columns = [
		               {text: uiLabelMap.BACCAccountNumber, datafield: 'accountCode',  width: '20%', editable: false, cellclassname: 'disableCellEditor'},
		               {text: uiLabelMap.BACCAccountName, datafield: 'accountName', width: '28%', editable: false, cellclassname: 'disableCellEditor'},
		               {text: uiLabelMap.BACCOpeningDrBalance, dataField: 'openingDrBalance', filterable: false, columntype: 'numberinput', width: '26%',
		            	   cellclassname: cellDeditClassname,
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		            	   		var data = grid.jqxGrid('getrowdata', row);
		            	   		var amount = "";
		            	   		if(typeof(value) == "number"){
		            	   			amount = formatcurrency(value);
		            	   		}
		            	   		return '<span style="text-align: right">' + amount + '</span>';
		            	   	},
		            	   	initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
		            	   		editor.jqxNumberInput({inputMode: 'advanced', decimalDigits: 0, digits: 12, max: 100000000000});
		            	   	},
		            	   	cellbeginedit: function (row, datafield, columntype) {
		            	   		var data = grid.jqxGrid('getrowdata', row);
		            	   		if (data.accountType == 'CREDIT') {
		            	   			return false;
		            	   		}
		            	   	},
		            	   	aggregates: ['sum'],
							aggregatesrenderer: function (aggregates) {
								var renderstring = "";
								$.each(aggregates, function (key, value) {
									renderstring += '<div style="font-size: 15px; padding: 8px; text-align: right">' + uiLabelMap.BACCAmountTotal + ': ' + formatcurrency(value) + '</div>';
								});
								return renderstring;
							}
		               },
		               {text: uiLabelMap.BACCOpeningCrBalance, dataField: 'openingCrBalance', filterable: false, columntype: 'numberinput', width: '26%',
		            	   cellclassname: cellCreditClassname,
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		            		   	var data = grid.jqxGrid('getrowdata', row);
		            		   	var amount = "";
		            		   	if(typeof(value) == "number"){
		            	   			amount = formatcurrency(value);
		            	   		}
		            	   		return '<span style="text-align: right">' + amount + '</span>';
		            	   },
		            	   initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
		            		   editor.jqxNumberInput({inputMode: 'advanced', decimalDigits: 0, digits: 12, max: 100000000000});
		            	   },
		            	   cellbeginedit: function (row, datafield, columntype) {
		            		   var data = grid.jqxGrid('getrowdata', row);
		            		   if (data.accountType == 'DEBIT') {
		            			   return false;   
		            		   }
		            	   },
		            	   aggregates: ['sum'],
		            	   aggregatesrenderer: function (aggregates) {
        		              var renderstring = "";
        		              $.each(aggregates, function (key, value) {
        		                  renderstring += '<div style="font-size: 15px; padding: 8px; text-align: right">' + uiLabelMap.BACCAmountTotal + ': ' + formatcurrency(value) + '</div>';
        		              });
        		              return renderstring;
        		          }
	                  },
	                  ];
		
		var config = {
				url: '',
				showtoolbar : false,
				width : '100%',
				virtualmode: false,
				editable: true,
				localization: getLocalization(),
				pageable: true,
				selectionmode: 'singlecell',
				filterable: true,
				showaggregates: true,
				showstatusbar: true,
				statusbarheight: 35,
				editmode: 'dblclick',
				source:{
					pagesize: 10,
					localdata: []
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#updateGlAccountBalWindow"), 880, 500);
	};
	var initEvent = function(){
		$("#updateGlAccountBalWindow").on('open', function(){
			initOpen();
		});
		$("#updateGlAccountBalWindow").on('close', function(){
			updateGridLocalData([]);
			$('#updateGlAccountBalGrid').jqxGrid('clearfilters');
		});
		$("#saveEditGlAccBal").click(function(){
			var drAmount = $("#updateGlAccountBalGrid").jqxGrid('getcolumnaggregateddata', 'openingDrBalance', ['sum']);
			var crAmount = $("#updateGlAccountBalGrid").jqxGrid('getcolumnaggregateddata', 'openingCrBalance', ['sum']);
			if(drAmount.sum != crAmount.sum){
				bootbox.dialog(uiLabelMap.BACCDrNotEqualCr,
						[
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);
				return;
			}
			Loading.show('loadingMacro');
			var rows = $("#updateGlAccountBalGrid").jqxGrid('getrows');
			var data = {glAccountBalance: JSON.stringify(rows)};
			
			$.ajax({
				url: 'createGlAccountBal',
				data: data,
				type: 'POST',
				success: function(response) {
					if(response.responseMessage == "error"){
						bootbox.dialog(response.errorMessage,
								[{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);
						return;
					}
					Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
					$("#updateGlAccountBalWindow").jqxWindow('close');
					$("#glAccountBalanceTree").jqxTreeGrid('updateBoundData');
				},
				complete: function(){
					Loading.hide('loadingMacro');
				}
			});
		});
		$("#cancelEditGlAccBal").click(function(){
			$("#updateGlAccountBalWindow").jqxWindow('close');
		});
	};
	var initOpen = function(){
		//$("#updateGlAccountBalGrid").jqxGrid({disabled: true});
		$("#updateGlAccountBalGrid").jqxGrid('showloadelement');
		$.ajax({
			url: 'getGlAccountOrganization',
			success: function(response){
				if(response._ERROR_MESSAGE_ || response._ERROR_MESSAGE_LIST_){
					updateGridLocalData([]);
					var errorMess = typeof(response._ERROR_MESSAGE_) != "undefined" ? response._ERROR_MESSAGE_: response._ERROR_MESSAGE_LIST_[0];
					bootbox.dialog(errorMess,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
					return;
				}
				updateGridLocalData(response.listReturn);
			},
			complete: function(){
				//$("#updateGlAccountBalGrid").jqxGrid({disabled: false});
				$("#updateGlAccountBalGrid").jqxGrid('hideloadelement');
			}
		});
	};
	var updateGridLocalData = function(localdata){
		var source = $("#updateGlAccountBalGrid").jqxGrid('source');
		source._source.localdata = localdata;
		$("#updateGlAccountBalGrid").jqxGrid('source', source);
	};
	var openEditWindow = function(){
		accutils.openJqxWindow($("#updateGlAccountBalWindow"));
	};
	return{
		init: init,
		openEditWindow: openEditWindow
	}
}());

$(document).ready(function () {
	openingBalanceObj.init();
	updateOpeningBalanceObj.init();
});