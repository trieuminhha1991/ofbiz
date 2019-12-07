var payrollTablePtyDetailObj = (function(){
	var _loadingData = false;
	var _updateGridData = false;
	var _initGridIncome = false;
	var _initGridDeduction = false;
	var _INCOME = "INCOME";
	var _DEDUCTION = "DEDUCTION";
	var init = function(){
		initSimpleInput();
		initJqxWindow();
		initEvent();
		initJqxNotification();
		create_spinner($("#spinnerPartySalDetail"));
	};
	var initSimpleInput = function(){
		$("#partyIdDetail").jqxInput({width: '82.5%', height: 20, placeHolder: uiLabelMap.CommonSearch + "..."});
		$("#fullNameDetail").jqxInput({width: '96%', height: 20, disabled: true});
		$("#baseSalaryAmountDetail").jqxNumberInput({width: '98%', height: '25px', spinButtons: true, readOnly: true, disabled: true, decimalDigits: 0, max: 999999999, digits: 9});
		$("#insSalaryAmountDetail").jqxNumberInput({width: '98%', height: '25px', spinButtons: true, readOnly: true, disabled: true, decimalDigits: 0, max: 999999999, digits: 9});
	};
	var initWidgets = function(tab){
		switch (tab) {
			case 0:
				initIncomeGrid();
				break;
			case 1:
				initDeductionGrid();
				break;
		}
	};
	//==================================== income grid ====================================
	var initIncomeGrid = function(){
		var datafield = [{name: 'code', type: 'string'},
		                 {name: 'partyId', type: 'string'},
		                 {name: 'payrollTableId', type: 'string'},
		                 {name: 'formulaName', type: 'string'},
		                 {name: 'payrollItemTypeId', type: 'string'},
		                 {name: 'amount', type: 'number'},
		                 {name: 'taxableTypeId', type: 'string'}
		                 ];
		var columns = [{text: uiLabelMap.HRIncome, datafield: 'formulaName', width: '27%'},
		               {text: uiLabelMap.PayrollItemType, datafield: 'payrollItemTypeId', width: '25%', 
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								for(var i = 0; i < globalVar.allPayrollItemTypeArr.length; i++){
									if(globalVar.allPayrollItemTypeArr[i].payrollItemTypeId == value){
										return "<div style='margin: 3px 5px 0px 5px'>" + globalVar.allPayrollItemTypeArr[i].description + "</div>"; 
									}
								}
							}
		               },
		               {text: uiLabelMap.HRCommonAmount, datafield: 'amount', width: '15%', columntype: 'numberinput',
		            	   	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		            	   		if(typeof(value) == 'number'){
		            	   			return '<div style="text-align: right; margin: 3px 5px 0px 5px">' + formatcurrency(value) + '</div>';
		   						}
	            	   		},
					   },
					   {text: uiLabelMap.TaxableType, datafield: 'taxableTypeId', width: '33%',
						   	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
							   	for(var i = 0; i < globalVar.taxableTypeArr.length; i++){
									if(globalVar.taxableTypeArr[i].taxableTypeId == value){
										return "<div style='margin: 3px 5px 0px 5px'>" + globalVar.taxableTypeArr[i].description + "</div>"; 
									}
							   	}
						   	}
					   }
		               ];
		var renderstatusbar = function (statusbar) {
			var container = $("<div style='overflow: hidden; position: relative; background-color: whitesmoke; height: 100%'></div>");
            var addButton = $('<a style="margin-left: 5px" href="javascript:void(0)" class="grid-action-button icon-plus open-sans">' + uiLabelMap.CommonAddNew + '</a>');
            container.append(addButton);
            $(statusbar).append(container);
            addButton.click(function(event){
            	addEmplIncomeObj.openWindow();
            });
		};
		var config = {
				url: '',
				showtoolbar : false,
				width : '100%',
				height: '100%',
				autoheight: false,
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				theme: 'energyblue',
				pageable: true,
				showstatusbar: globalVar.isEditable,
				statusbarheight: globalVar.isEditable? 34 : 0,
				renderstatusbar: globalVar.isEditable? renderstatusbar: null,
				//sortable: true,
				source: globalVar.isEditable?{
												pagesize: 5,
												removeUrl: "jqxGeneralServicer?jqaction=D&sname=deletePayrollTableRecordPartyAmount", 
												deleteColumns: "payrollTableId;partyId;code",
												deletesuccessfunction: function(){
													payrollTablePtyDetailObj.setUpdateGridData(true);
												}
											}:
											{
												pagesize: 5,
											}
		};
		var grid = $("#gridIncome");
		Grid.initGrid(config, datafield, columns, null, grid);
		if(globalVar.isEditable){
			Grid.createContextMenu(grid, $("#contextMenuIncome"), false);
		}
		updateGridSource(grid, _INCOME);
		_initGridIncome = true;
	};
	
	//================= deduction grid ============================
	var initDeductionGrid = function(){
		var datafield = [{name: 'code', type: 'string'},
		                 {name: 'partyId', type: 'string'},
		                 {name: 'payrollTableId', type: 'string'},
		                 {name: 'formulaName', type: 'string'},
		                 {name: 'amount', type: 'number'},
		                 {name: 'exempted', type: 'string'}
		                 ];
		var columns = [{text: uiLabelMap.HRDeduction, datafield: 'formulaName', width: '33%'},
		               {text: uiLabelMap.HRCommonAmount, datafield: 'amount', width: '33%', columntype: 'numberinput',
		            	   	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		            	   		if(typeof(value) == 'number'){
		            	   			return '<div style="text-align: right; margin: 3px 5px 0px 5px">' + formatcurrency(value) + '</div>';
		   						}
	            	   		},
					   },
					   {text: uiLabelMap.IsExemptedTax, datafield: 'exempted', width: '34%', editable: false,
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
							   	if(value == 'Y'){
									return "<label style='text-align: center; margin-top: 8px'><input type='checkbox' disabled='disabled' checked='checked'><span class='lbl'></span></label>";
								}else{
									return "<label style='text-align: center; margin-top: 8px'><input type='checkbox' disabled='disabled'><span class='lbl'></span></label>";
								}
						   }
					   }
		               ];
		var renderstatusbar = function (statusbar) {
			var container = $("<div style='overflow: hidden; position: relative; background-color: whitesmoke; height: 100%'></div>");
            var addButton = $('<a style="margin-left: 5px" href="javascript:void(0)" class="grid-action-button icon-plus open-sans">' + uiLabelMap.CommonAddNew + '</a>');
            container.append(addButton);
            $(statusbar).append(container);
            addButton.click(function(event){
            	addEmplDeductionObj.openWindow();
            });
		};
		var config = {
				url: '',
				showtoolbar : false,
				width : '100%',
				height: '100%',
				autoheight: false,
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				//sortable: true,
				theme: 'energyblue',
				pageable: true,
				showstatusbar: globalVar.isEditable,
				statusbarheight: globalVar.isEditable? 34: 0,
				source: globalVar.isEditable?	{
													pagesize: 5,
													removeUrl: "jqxGeneralServicer?jqaction=D&sname=deletePayrollTableRecordPartyAmount", 
													deleteColumns: "payrollTableId;partyId;code",
													deletesuccessfunction: function(){
														payrollTablePtyDetailObj.setUpdateGridData(true);
													}
												}:
												{pagesize: 5,},
				renderstatusbar: globalVar.isEditable? renderstatusbar: null
		};
		var grid = $("#gridDeduction");
		Grid.initGrid(config, datafield, columns, null, grid);
		if(globalVar.isEditable){
			Grid.createContextMenu(grid, $("#contextMenuDeduction"), false);
		}
		updateGridSource(grid, _DEDUCTION);
		_initGridDeduction = true;
	};
	var initJqxTabs = function(){
		$('#jqxTabDetail').jqxTabs({ width: '99,5%', height: globalVar.isEditable? 320 : 286,  initTabContent: initWidgets, theme: 'fresh'});
	};
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxTabs();
		};
		createJqxWindow($("#payrollPartyAmountDetailWindow"), 800, globalVar.isEditable? 500: 466, initContent);
	};
	var updateGridSource = function(grid, payrollCharacteristicId){
		var source = grid.jqxGrid('source');
		var partyCode = $("#partyIdDetail").val();
		var url = "";
		if(typeof(payrollCharacteristicId) != 'undefined' && payrollCharacteristicId.length > 0){
			url = "jqxGeneralServicer?sname=JQGetPayrollTableRecordPartyDetail&payrollCharacteristicId=" + payrollCharacteristicId 
						+ "&partyCode=" + partyCode + "&payrollTableId=" + globalVar.payrollTableId + "&pagesize=0";
		}
		source._source.url = url;
		grid.jqxGrid('source', source);
	};
	var openWindow = function(){
		openJqxWindow($("#payrollPartyAmountDetailWindow"))
	};
	var setData = function(data){
		$("#partyIdDetail").val(data.partyCode);
		$("#fullNameDetail").val(data.fullName);
		$("#baseSalaryAmountDetail").val(data.baseSalAmount);
		$("#insSalaryAmountDetail").val(data.insSalAmount);
	};
	var initEvent = function(){
		$("#payrollPartyAmountDetailWindow").on('close', function(event){
			$("#partyIdDetail").val("");
			$("#fullNameDetail").val("");
			$("#baseSalaryAmountDetail").val(0);
			$("#insSalaryAmountDetail").val(0);
			$('#jqxTabDetail').jqxTabs('select', 0);
			if(_initGridDeduction){
				updateGridSource($("#gridDeduction"));
			}
			if(_initGridIncome){
				updateGridSource($("#gridIncome"));
			}
			if(_updateGridData){
				$("#jqxgrid").jqxGrid('updatebounddata');
			}
			_updateGridData = false;
			_loadingData = false;
		});
		$("#payrollPartyAmountDetailWindow").on('open', function(event){
			if(_initGridDeduction){
				updateGridSource($("#gridDeduction"), _DEDUCTION);
			}
			if(_initGridIncome){
				updateGridSource($("#gridIncome"), _INCOME);
			}
		});
		$("#gridIncome").on("bindingcomplete", function(event){
			$("#gridIncome").jqxGrid('refresh');
		});
		$("#gridDeduction").on("bindingcomplete", function(event){
			$("#gridDeduction").jqxGrid('refresh');
		});
		$("#alterCancel").click(function(event){
			$("#payrollPartyAmountDetailWindow").jqxWindow('close');
		});
		$("#partyIdDetail").on('keypress', function(event){
			if(event.which == 13 || event.keyCode == 13){
				reloadPartySalData();
			}
		});
		$("#searchBtn").click(function(event){
			reloadPartySalData();
		});
	};
	var reloadPartySalData = function(){
		var partyCode = $("#partyIdDetail").val();
		if(!_loadingData){
			_loadingData = true;
			$("#loadingPartySalDetail").show();
			$("#baseSalaryAmountDetail").val(null);
			$("#insSalaryAmountDetail").val(null);
			$("#fullNameDetail").val("");
			$("#partyIdDetail").jqxInput({disabled: true});
			$("#searchBtn").attr("disabled", 'disabled');
			$("#alterCancel").attr("disabled", 'disabled');
			if(_initGridDeduction){
				updateGridSource($("#gridDeduction"), _DEDUCTION);
			}
			if(_initGridIncome){
				updateGridSource($("#gridIncome"), _INCOME);
			}
			$.ajax({
				url: 'getPayrollTableRecordPartyAmount',
				type: 'POST',
				data: {payrollTableId: globalVar.payrollTableId, partyCode: partyCode},
				success: function(response){
					if(response.responseMessage == "success"){
						$("#baseSalaryAmountDetail").val(response.basicAmount);
						$("#insSalaryAmountDetail").val(response.insAmount);
						$("#fullNameDetail").val(response.fullName);
					}else{
						bootbox.dialog(response.errorMessage,
								[
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);
					}
				},
				complete:  function(jqXHR, textStatus){
					$("#loadingPartySalDetail").hide();
					$("#partyIdDetail").jqxInput({disabled: false});
					$("#searchBtn").removeAttr("disabled");
					$("#alterCancel").removeAttr("disabled");
					_loadingData = false;
				}
			});
		}
	};
	var setUpdateGridData = function(value){
		_updateGridData = value;
	};
	var initJqxNotification = function(){
		$("#jqxNotificationgridIncome").jqxNotification({ width: "100%", appendContainer: "#containergridIncome", opacity: 0.9, template: "info" });
		$("#jqxNotificationgridDeduction").jqxNotification({ width: "100%", appendContainer: "#containergridDeduction", opacity: 0.9, template: "info" });
	};
	return{
		init: init,
		openWindow: openWindow,
		setData: setData,
		setUpdateGridData: setUpdateGridData,
	}
}());

$(document).ready(function() {
	payrollTablePtyDetailObj.init();
});