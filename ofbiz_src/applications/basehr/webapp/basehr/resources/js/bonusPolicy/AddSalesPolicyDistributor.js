var addSaleBonusPolicyObj = (function(){
	var init = function(){
		initSimpleInput();
		initTurnoverGrid();
		initSKUGrid();
		initJqxWindow();
		initEvent();
		initJqxValidator();
		create_spinner($("#spinnerCreateNew"));
	};
	var initSimpleInput = function(){
		$("#salesBonusPolicyName").jqxInput({width: '96%', height: 20});
		$("#description").jqxInput({width: '96%', height: 20});
		$("#fromDate").jqxDateTimeInput({width: '98%', height: 25});
		$("#thruDate").jqxDateTimeInput({width: '98%', height: 25, showFooter: true});
	};
	var getDataField = function(){
		var datafield = [{name: 'valueFrom', type: 'number'},
		                 {name: 'valueTo', type: 'number'},
		                 {name: 'operatorEnumIdFrom', type: 'string'},
		                 {name: 'operatorEnumIdTo', type: 'string'},
		                 {name: 'actionValue', type: 'number'},
		                 ];
		return datafield;
	};
	var getColumns = function(grid){
		var columns = [
				       {text: uiLabelMap.BSCondition, datafield: 'valueFrom', columntype: 'numberinput', width: '23%', align: 'center',
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								var rowData = grid.jqxGrid('getrowdata', row);
								var inputEnum = rowData.operatorEnumIdFrom;
								var cost = getCellsRendererValue(value, inputEnum);
								return '<span style="text-align: right">' + cost + '</span>';
							},
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								editor.jqxNumberInput({ width: cellwidth, height: cellheight, digits: 3, spinButtons: true, min: 0, decimalDigits: 1, inputMode: 'advanced'})
							}
				       },
				       {text: uiLabelMap.BSAnd, datafield: 'valueTo', columntype: 'numberinput', width: '23%', align: 'center',
				    	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
				    		   	var rowData = grid.jqxGrid('getrowdata', row);
								var inputEnum = rowData.operatorEnumIdTo;
								var cost = getCellsRendererValue(value, inputEnum);	
								return '<span style="text-align: right">' + cost + '</span>';
							},
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								editor.jqxNumberInput({ width: cellwidth, height: cellheight, digits: 3, spinButtons: true, min: 0, decimalDigits: 1, inputMode: 'advanced'})
							}
				       },
				       {text: uiLabelMap.BonusLevel, datafield: 'actionValue', width: '54%', columntype: 'numberinput', cellsalign: 'right', align: 'center',
				    	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
				    		   var rowData = grid.jqxGrid('getrowdata', row);
				    		   if(rowData){
				    			   	return "<span>" + value + "% * " + uiLabelMap.TurnoverActual + "</span>";
				    		   }
				    	   }
				       }
				       ];
		return columns;
	};
	var initTurnoverGrid = function(){
		var grid = $("#turnoverRuleGrid");
		var datafield = getDataField();
		var columns = getColumns(grid);
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "turnoverRuleGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5 style='max-width: 60%; overflow: hidden'>" + uiLabelMap.SalesBonus + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        var customcontrol1 = "icon-plus open-sans@" + uiLabelMap.BSAddNew + "@javascript:void(0)@addSaleBonusPolicyRuleObj.openWindow('" + id + "', '" + uiLabelMap.ActualTargetPercent + "')";
	        Grid.createCustomControlButton(grid, container, customcontrol1);
	        Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, "",
                    "", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
		};
		var config = {
				url: '',
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '99%',
				virtualmode: false,
				editable: false,
				localization: getLocalization(),
				theme: 'olbius',
				sortable: true,
				source: {
					localdata: [],
					pagesize: 5,
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initSKUGrid = function(){
		var grid = $("#skuRuleGrid");
		var datafield = getDataField();
		var columns = getColumns(grid);
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "skuRuleGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5 style='max-width: 60%; overflow: hidden'>" + uiLabelMap.SKUBonus + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        var customcontrol1 = "icon-plus open-sans@" + uiLabelMap.BSAddNew + "@javascript:void(0)@addSaleBonusPolicyRuleObj.openWindow('" + id + "', '" + uiLabelMap.SKUCompletionPercent +"')";
	        Grid.createCustomControlButton(grid, container, customcontrol1);
	        Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, "",
                    "", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
		};
		var config = {
				url: '',
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '99%',
				virtualmode: false,
				editable: false,
				localization: getLocalization(),
				theme: 'olbius',
				sortable: true,
				source: {
					localdata: [],
					pagesize: 5,
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var getCellsRendererValue = function(value, operatorEnumId){
		var cost = "";
		if(typeof(value) == "number"){
			var cost = "";
			for(var i = 0; i < globalVar.enumOperatorArr.length; i++){
				if(globalVar.enumOperatorArr[i].enumId == operatorEnumId){
					cost = globalVar.enumOperatorArr[i].enumCode;
					break;
				}
			}
			cost += " " + value + "%";
		}
		return cost;
	};
	var initJqxWindow = function(){
		createJqxWindow($("#popupAddRow"), 850, 490);
	};
	var getData = function(){
		var data = {};
		data.salesBonusPolicyName = $("#salesBonusPolicyName").val();
		data.description = $("#description").val();
		data.fromDate = $("#fromDate").jqxDateTimeInput('val', 'date').getTime();
		var thruDate = $("#thruDate").jqxDateTimeInput('val', 'date');
		if(thruDate){
			data.thruDate = thruDate.getTime();
		}
		var turnoverData = [];
		var turnoverGridRows = $("#turnoverRuleGrid").jqxGrid('getrows');
		for(var i = 0; i < turnoverGridRows.length; i++){
			var rowData = turnoverGridRows[i];
			turnoverData.push({
				valueFrom: rowData.valueFrom, 
				valueTo: rowData.valueTo, 
				operatorEnumIdFrom: rowData.operatorEnumIdFrom, 
				operatorEnumIdTo: rowData.operatorEnumIdTo,
				actionValue: rowData.actionValue
			});
		} 
		data.turnoverData = JSON.stringify(turnoverData);
		
		var skuData = [];
		var skuGridRows = $("#skuRuleGrid").jqxGrid('getrows');
		for(var i = 0; i < skuGridRows.length; i++){
			var rowData = skuGridRows[i];
			skuData.push({
				valueFrom: rowData.valueFrom, 
				valueTo: rowData.valueTo, 
				operatorEnumIdFrom: rowData.operatorEnumIdFrom, 
				operatorEnumIdTo: rowData.operatorEnumIdTo,
				actionValue: rowData.actionValue
			});
		} 
		data.skuData = JSON.stringify(skuData);
		return data;
	};
	var initEvent = function(){
		$("#popupAddRow").on('close', function(event){
			$("#salesBonusPolicyName").val("");
			$("#description").val("");
		});
		$("#popupAddRow").on('open', function(event){
			var date = new Date();
			date.setDate(1);
			$("#fromDate").val(date);
			$("#thruDate").val(null);
			addSaleBonusPolicyRuleObj.setAddConditionRule(addConditionRuleWhenCreate);
		});
		$("#cancelAdd").click(function(event){
			$("#popupAddRow").jqxWindow('close');
		});
		$("#saveAdd").click(function(event){
			var valid = $("#popupAddRow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.HrCreateNewConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createSalesBonusPolicy();
						}	
					},
					{
						"label" : uiLabelMap.CommonCancel,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
	};
	var initJqxValidator = function(){
		$("#popupAddRow").jqxValidator({
			rules: [
				{input : '#salesBonusPolicyName', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#fromDate', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input: '#thruDate', message: uiLabelMap.MustGreaterThanEffectiveDate, action: 'blur',
					rule : function(input, commit){
						var thruDate = input.jqxDateTimeInput('val', 'date');
						var fromDate = $('#fromDate').jqxDateTimeInput('val', 'date');
						if(thruDate){
							if(thruDate < fromDate){
								return false;
							}
						}
						return true;
					}
				}
			]
		});
	};
	var createSalesBonusPolicy = function(){
		var data = getData();
		disableAll();
		$.ajax({
			url: 'createDistributorBonusPolicy',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
					$("#popupAddRow").jqxWindow('close');
				}else{
					bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]
					);
				}
			},
			complete: function(jqXHR, textStatus){
				enableAll();
			}
		});
	};
	var disableAll = function(){
		$("#loadingCreateNew").show();
		$("#cancelAdd").attr("disabled", "disabled");
		$("#saveAdd").attr("disabled", "disabled");
	};
	var enableAll = function(){
		$("#loadingCreateNew").hide();
		$("#cancelAdd").removeAttr("disabled");
		$("#saveAdd").removeAttr("disabled");
	};
	return{
		init: init
	}
}());

var addConditionRuleWhenCreate = (function(){
	var execute = function(grid, jqxWindow, rowData){
		if(grid.length > 0){
			grid.jqxGrid('addrow', null, rowData);
		}
		jqxWindow.jqxWindow('close');
	};
	var prepareData = function(){
		$("#operatorInputFrom").jqxDropDownList({selectedIndex: 0});
		$("#operatorInputTo").jqxDropDownList({selectedIndex: 0});
		$("#ignoreTo").jqxCheckBox({checked: false});
		$("#ignoreFrom").jqxCheckBox({checked: false});
		return;
	};
	return{
		execute: execute,
		prepareData: prepareData
	}
}());
var addSaleBonusPolicyRuleObj = (function(){
	var _gridId;
	var _addConditionRule;
	var init = function(){
		initDropDown();
		initSimpleInput();
		initJqxWindow();
		initJqxValidator();
		initEvent();
		create_spinner($("#spinnerCreateNewRule"));
	};
	var initDropDown = function(){
		createJqxDropDownList(globalVar.enumOperatorGreaterArr, $("#operatorInputFrom"), "enumId", "enumCode", 25, "99%");
		createJqxDropDownList(globalVar.enumOperatorLessThanArr, $("#operatorInputTo"), "enumId", "enumCode", 25, "99%");
	};
	var initSimpleInput = function(){
		$("#valueFrom").jqxNumberInput({ width: "99%", height: 25, digits: 3, spinButtons: true, min: 0, decimalDigits: 0, symbolPosition: 'right', symbol: '%'});
		$("#ignoreFrom").jqxCheckBox({ width: "99%", height: 25});
		$("#valueTo").jqxNumberInput({ width: "99%", height: 25, digits: 3, spinButtons: true, min: 0, decimalDigits: 0, symbolPosition: 'right', symbol: '%'});
		$("#ignoreTo").jqxCheckBox({ width: "99%", height: 25});
		$("#valueAction").jqxNumberInput({ width: "100%", height: 25, spinButtons: true, min: 0, digits: 3, decimalDigits: 1, symbolPosition: 'right', symbol: '%'});
	};
	var initJqxWindow = function(){
		createJqxWindow($("#AddDistPolicyRuleWindow"), 450, 230);
	};
	var initJqxValidator = function(){
		$("#AddDistPolicyRuleWindow").jqxValidator({
			rules: [
				{input : '#operatorInputFrom', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						var checked = $("#ignoreFrom").jqxCheckBox('checked');
						if(!checked){
							if(!input.val()){
								return false;
							}
						}
						return true;
					}
				},
				{input : '#operatorInputTo', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						var checked = $("#ignoreTo").jqxCheckBox('checked');
						if(!checked){
							if(!input.val()){
								return false;
							}
						}
						return true;
					}
				},
				{input : '#valueFrom', message : uiLabelMap.ValueIsInvalid, action: 'none', 
					rule : function(input, commit){
						var checkedFrom = $("#ignoreFrom").jqxCheckBox('checked');
						var checkedTo = $("#ignoreTo").jqxCheckBox('checked');
						if(!checkedFrom && !checkedTo){
							var valueFrom = input.val();
							var valueTo = $("#valueTo").val(); 
							if(valueFrom > valueTo){
								return false;
							}
						}
						return true;
					}
				},
				{input : '#valueTo', message : uiLabelMap.ValueIsInvalid, action: 'none', 
					rule : function(input, commit){
						var checkedFrom = $("#ignoreFrom").jqxCheckBox('checked');
						var checkedTo = $("#ignoreTo").jqxCheckBox('checked');
						if(!checkedFrom && !checkedTo){
							var valueFrom = $("#valueFrom").val();
							var valueTo = input.val(); 
							if(valueFrom > valueTo){
								return false;
							}
						}
						return true;
					}
				},
				{input : '#ignoreFrom', message : uiLabelMap.ConditionIsNotSetting, action: 'none', 
		        	rule : function(input, commit){
		        		var checkedFrom = $("#ignoreFrom").jqxCheckBox('checked');
		        		var checkedTo = $("#ignoreTo").jqxCheckBox('checked');
		        		if(checkedFrom && checkedTo){
		        			return false;
		        		}
		        		return true;
		        	}
		        },
			]
		});
	};
	var initEvent = function(){
		$("#AddDistPolicyRuleWindow").on('open', function(event){
			_addConditionRule.prepareData();
		});
		$("#AddDistPolicyRuleWindow").on('close', function(event){
			Grid.clearForm($(this));
			_gridId = "";
		});
		$("#ignoreTo").on('change', function (event){
			var checked = event.args.checked;
			$("#operatorInputTo").jqxDropDownList({disabled: checked});
			$("#valueTo").jqxNumberInput({disabled: checked});
		});
		$("#ignoreFrom").on('change', function (event){
			var checked = event.args.checked;
			$("#operatorInputFrom").jqxDropDownList({disabled: checked});
			$("#valueFrom").jqxNumberInput({disabled: checked});
		});
		$("#cancelAddCond").click(function(event){
			$("#AddDistPolicyRuleWindow").jqxWindow('close');
		});
		$("#saveAddCond").click(function(event){
			var valid = $("#AddDistPolicyRuleWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var checkedFrom = $("#ignoreFrom").jqxCheckBox('checked');
			var checkedTo = $("#ignoreTo").jqxCheckBox('checked');
			var row = {};
			if(!checkedFrom){
				row.valueFrom = $("#valueFrom").val();
				row.operatorEnumIdFrom = $("#operatorInputFrom").val();
			}
			if(!checkedTo){
				row.valueTo = $("#valueTo").val();
				row.operatorEnumIdTo = $("#operatorInputTo").val();
			}
			row.actionValue = $("#valueAction").val();
			if(_addConditionRule){
				_addConditionRule.execute($("#" + _gridId), $("#AddDistPolicyRuleWindow"), row);
			}
		});
	};
	var disableAll = function(){
		$("#cancelAddCond").attr("disabled", "disabled");
		$("#saveAddCond").attr("disabled", "disabled");
	};
	var enableAll = function(){
		$("#cancelAddCond").removeAttr("disabled");
		$("#saveAddCond").removeAttr("disabled");
	};
	var openWindow = function(gridId, conditionLabel){
		openJqxWindow($("#AddDistPolicyRuleWindow"));
		_gridId = gridId;
		if(typeof(conditionLabel) != "undefined" && conditionLabel.length > 0){
			$("#conditionLabel").html(conditionLabel);
		}
	};
	var closeWindow = function(){
		$("#AddDistPolicyRuleWindow").jqxWindow('close');
	};
	var setAddConditionRule = function(obj){
		_addConditionRule = obj;
	};
	return{
		init: init,
		openWindow: openWindow,
		closeWindow: closeWindow,
		setAddConditionRule: setAddConditionRule
	}
}());

$(document).ready(function(){
	addSaleBonusPolicyObj.init();
	addSaleBonusPolicyRuleObj.init();
});