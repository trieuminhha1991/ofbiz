var viewListBonusPolicy = (function(){
	var _prefix = 'rule';
	var initrowdetails = function (index, parentElement, gridElement, datarecord) {
		var grid = $($(parentElement).children()[0]);
		$(grid).attr('id','jqxgridDetail' + index);
		var salesBonusPolicyId = datarecord.salesBonusPolicyId;
		var datafield = [];
		var columns = [];
		var columngroups = [];
		var contextMenuId = "menuRule" + salesBonusPolicyId;
		var contexMenuEle = createContextMenuEle(contextMenuId, salesBonusPolicyId);
		$(parentElement).append(contexMenuEle);
		initContextMenuDetail('jqxgridDetail' + index, contextMenuId, salesBonusPolicyId);
		for(var i = 0; i < globalVar.ruleEnumArr.length; i++){
			columngroups.push({text: globalVar.ruleEnumArr[i].description, align: 'center', name: globalVar.ruleEnumArr[i].enumId});
		}
		var renderstatusbar = function (statusbar) {
			var container = $("<div style='overflow: hidden; position: relative; background-color: whitesmoke; height: 100%'></div>");
            var addButton = $('<a style="margin-left: 5px" href="javascript:void(0)" class="grid-action-button icon-plus open-sans">' + uiLabelMap.CommonAddNew + '</a>');
            container.append(addButton);
            $(statusbar).append(container);
            addButton.click(function(event){
            	editSaleBonusPolicyRuleObj.openWindow('jqxgridDetail' + index, salesBonusPolicyId);
            });
		};
		var config = {
				url: '',
				showtoolbar : false,
				width: '96%',
				showstatusbar: globalVar.isSAM,
				statusbarheight: 30,
				renderstatusbar: renderstatusbar,
				height: 136,
				autoheight: false,
				virtualmode: false,
				editable: false,
				localization: getLocalization(),
				sortable: false,
				pageable: false,
				selectionmode: 'singlecell',
				theme: 'energyblue',
				columngroups: columngroups,
				source: {
					localdata: [],
					pagesize: 1,
				}
		};
		Grid.initGrid(config, datafield, columns, null, $(grid));
        if (globalVar.isSAM) {
            Grid.createContextMenu($(grid), $("#" + contextMenuId), false);
        }
		fillGridData($(grid), salesBonusPolicyId);
		initGridEvent($(grid), salesBonusPolicyId);
	};
	var createContextMenuEle = function(menuId, salesBonusPolicyId){
		var contextEle = $("<div id='" + menuId +"'></div>");
		var ul = $("<ul></ul>");
		ul.append($("<li action='edit' id='edit" + salesBonusPolicyId + "'><i class='icon-edit'></i>" + uiLabelMap.CommonEdit +"</li>"));
		ul.append($("<li action='delete' id='delete" + salesBonusPolicyId + "'><i class='icon-trash'></i>" + uiLabelMap.CommonDelete +"</li>"));
		contextEle.append(ul);
		return contextEle;
	};
	var initContextMenuDetail = function(gridId, contextMenuId, salesBonusPolicyId){
		createJqxMenu(contextMenuId, 30, 150);
		$("#" + contextMenuId).on('itemclick', function (event) {
			var args = event.args;
			var selectedCell = $("#" + gridId).jqxGrid('getselectedcell');
			var rowindex = selectedCell.rowindex;
			var datafield = selectedCell.datafield;
			var ruleId = datafield.substring(_prefix.length);
			var ruleEnumId = $("#" + gridId).jqxGrid('getcolumnproperty', datafield, 'columngroup');
			var action = $(args).attr("action");
			if(action == "edit"){
				editConditionRule.setSalesBonusPolicyId(salesBonusPolicyId);
				editConditionRule.setRuleId(ruleId);
				editConditionRule.setGridId(gridId);
				addSaleBonusPolicyRuleObj.setAddConditionRule(editConditionRule);//addSaleBonusPolicyRuleObj is defined in AddSalesPolicyDistributor.js
				var conditionLabel = "";
				if(ruleEnumId == "DIST_TURNOVER"){
					conditionLabel = uiLabelMap.ActualTargetPercent; 
				}else{
					conditionLabel = uiLabelMap.SKUCompletionPercent; 
				}
				addSaleBonusPolicyRuleObj.openWindow(gridId, conditionLabel);
			}else if(action == "delete"){
				bootbox.dialog(uiLabelMap.NotifyDelete,
						[{
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								deleteSalesPolicyRule(gridId, salesBonusPolicyId, ruleId);
							}	
						},
						{
							"label" : uiLabelMap.CommonCancel,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);
			}
		});
		$("#" + contextMenuId).on('shown', function () {
			var selectedCell = $("#" + gridId).jqxGrid('getselectedcell');
			var datafield = selectedCell.datafield;
			if(datafield == 'title'){
				$('#' + contextMenuId).jqxMenu('disable', 'edit' + salesBonusPolicyId, true);
				$('#' + contextMenuId).jqxMenu('disable', 'delete' + salesBonusPolicyId, true);
			}else{
				$('#' + contextMenuId).jqxMenu('disable', 'edit' + salesBonusPolicyId, false);
				$('#' + contextMenuId).jqxMenu('disable', 'delete' + salesBonusPolicyId, false);
			}
		});
	};
	var deleteSalesPolicyRule = function(gridId, salesBonusPolicyId, ruleId){
		$("#" + gridId).jqxGrid('showloadelement');
		$("#" + gridId).jqxGrid({disabled: true});
		$.ajax({
			url: 'deleteSalesBonusPolicyRule',
			data: {salesBonusPolicyId: salesBonusPolicyId, ruleId: ruleId},
			type: 'POST',
			success: function(response){
				if(response._EVENT_MESSAGE_){
					Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					viewListBonusPolicy.fillGridData($("#" + gridId), salesBonusPolicyId)
				}else{
					bootbox.dialog(response._ERROR_MESSAGE_,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]
					);
				}
			},
			complete: function(jqXHR, textStatus){
				$("#" + gridId).jqxGrid('hideloadelement');
				$("#" + gridId).jqxGrid({disabled: false});
			}
		});
	};
	var fillGridData = function(grid, salesBonusPolicyId){
		grid.jqxGrid({disabled: true});
		grid.jqxGrid('showloadelement');
		var datafield = [{name: 'title', type: 'string'}];
		var columns = [{text: '', width: '10%', datafield: 'title', editable: false}];
		var localdata = [];
		$.ajax({
			url: 'getSalesPolicyRuleAndCondAndAct',
			data: {salesBonusPolicyId: salesBonusPolicyId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					var data = response.results;
					var totalRules = Object.keys(data).length;
					var width = getColumnWidth(totalRules, 90);
					var tempRow = {title: uiLabelMap.BonusLevel};
					for(var ruleId in data){
						var ruleData = data[ruleId];
						var condition = ruleData.condition;
						var action = ruleData.action;
						datafield.push({name: _prefix + ruleId, type: 'string'});
						var conditionText = getConditionText(condition);
						columns.push({text: conditionText, datafield: _prefix + ruleId, columngroup: ruleData.ruleEnumId, width: width + "%", align: 'center', cellsalign: 'center'});
						tempRow[_prefix + ruleId] = getActionValue(action, ruleData.ruleEnumId);
					}
					localdata.push(tempRow);
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
				updateGridDatafieldAndColumns(grid, datafield, columns);
				updateGridLocalData(grid, localdata)
				grid.jqxGrid({disabled: false});
				grid.jqxGrid('hideloadelement');
			}
		});
	};
	var updateGridDatafieldAndColumns = function(grid, datafield, columns){
		var source = grid.jqxGrid('source');
		source._source.datafield = datafield;
		grid.jqxGrid('source', source);
		grid.jqxGrid('columns', columns);
	};
	var updateGridLocalData = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		grid.jqxGrid('source', source);
	};
	var getConditionText = function(conditionList){
		var text = "";
		for(var i = 0; i < conditionList.length; i++){
			var condition = conditionList[i];
			var tempText = getCellsRendererValue(condition.condValue, condition.operatorEnumId, condition.inputParamEnumId);
			if(tempText.length > 0){
				if(i > 0){
					text += " - ";
				}
				text += tempText;
			}
		}
		return text;
	};
	var getActionValue = function(actionList, ruleEnumId){
		if(actionList.length == 0){
			return;
		}
		var value = "";
		for(var i = 0; i < actionList.length; i++){
			var action = actionList[i];
			if(i > 0){
				value += " + ";
			}
			var actionEnumId = action.actionEnumId;
			var quantity = action.quantity;
			var amount = action.amount;
			if(actionEnumId == "SBPACT_ACTUAL_PERCENT"){
				value += action.quantity + "% * (" + uiLabelMap.TurnoverActualShort + ")";
			}
		}
		return value;
	};
	var getCellsRendererValue = function(value, operatorEnumId, inputParamEnumId){
		var cost = "";
		if(typeof(value) == "number"){
			var cost = "";
			for(var i = 0; i < globalVar.enumOperatorArr.length; i++){
				if(globalVar.enumOperatorArr[i].enumId == operatorEnumId){
					cost = globalVar.enumOperatorArr[i].enumCode;
					break;
				}
			}
			cost += value + "%";
		}
		return cost;
	};
	var getColumnWidth = function(totalColumns, totalWidth){
		if(typeof(totalWidth) == 'undefined'){
			totalWidth = 100
		}
		if(totalColumns == 0){
			return totalWidth;
		}
		var width = totalWidth / (totalColumns);
		if(width < 20){
			width = 20;
		}
		return width;
	};
	var initGridEvent = function(grid, salesBonusPolicyId){
		grid.on("cellclick", function (event) {
			var args = event.args;
			if(args.rightclick) {
				var rowBoundIndex = args.rowindex;
			    var dataField = args.datafield;
			    grid.jqxGrid('selectcell', rowBoundIndex, dataField);
			}
		});
	};
	return{
		initrowdetails: initrowdetails,
		fillGridData: fillGridData
	}
}());

var editConditionRule = (function(){
	var _salesBonusPolicyId;
	var _ruleId;
	var _gridId;
	var _conditionFrom = {};
	var _conditionTo = {};
	var _action = {};
	var prepareData = function(){
		$("#loadingCreateNewRule").show();
		$("#cancelAddCond").attr("disabled", "disabled");
		$("#saveAddCond").attr("disabled", "disabled");
		$.ajax({
			url: 'getSalePolicyCondAndActOfRule',
			data: {salesBonusPolicyId: _salesBonusPolicyId, ruleId: _ruleId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					var conditionList = response.condition;
					var actionList = response.action;
					if(actionList){
						 _action = actionList[0]; 
					}
					_conditionFrom = conditionList[0];
					$("#valueFrom").jqxNumberInput('val', _conditionFrom.condValue);
					$("#operatorInputFrom").val(_conditionFrom.operatorEnumId);
					if(conditionList.length > 1){
						_conditionTo = conditionList[1];
						$("#valueTo").jqxNumberInput('val', _conditionTo.condValue);
						$("#operatorInputTo").val(_conditionTo.operatorEnumId);
					}else{
						_conditionTo = {};
						$("#ignoreTo").jqxCheckBox({ checked: true});
					}
					$("#valueAction").val(_action.quantity);
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
			complete: function(jqXHR, textStatus){
				$("#loadingCreateNewRule").hide();
				$("#cancelAddCond").removeAttr("disabled");
				$("#saveAddCond").removeAttr("disabled");
			}
		});
	};
	var execute = function(grid, jqxWindow, data){
		$("#cancelAddCond").attr("disabled", "disabled");
		$("#saveAddCond").attr("disabled", "disabled");
		$("#loadingCreateNewRule").show();
		if(Object.keys(_conditionTo).length > 0){
			data.condSeqIdTo = _conditionTo.condSeqId;
		}
		data.condSeqIdFrom = _conditionFrom.condSeqId;
		data.ruleId = _ruleId;
		data.salesBonusPolicyId = _salesBonusPolicyId;
		data.actSeqId = _action.actSeqId;
		$.ajax({
			url: 'updateSalePolicyCondAndAct',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					jqxWindow.jqxWindow('close');
					viewListBonusPolicy.fillGridData($("#" + _gridId), _salesBonusPolicyId);
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
			complete: function(jqXHR, textStatus){
				$("#cancelAddCond").removeAttr("disabled");
				$("#saveAddCond").removeAttr("disabled");
				$("#loadingCreateNewRule").hide();
			}
		});
	};
	var setSalesBonusPolicyId = function(salesBonusPolicyId){
		_salesBonusPolicyId = salesBonusPolicyId;
	};
	var setRuleId = function(ruleId){
		_ruleId = ruleId;
	};
	var setGridId = function(gridId){
		_gridId = gridId;
	};
	return{
		execute: execute,
		setSalesBonusPolicyId: setSalesBonusPolicyId,
		setRuleId: setRuleId,
		prepareData: prepareData,
		setGridId: setGridId
	}
}());

var editSaleBonusPolicyRuleObj = (function(){
	var _gridId;
	var _salesBonusPolicyId;
	var init = function(){
		initDropDown();
		initSimpleInput();
		initJqxWindow();
		initJqxValidator();
		initEvent();
		create_spinner($("#spinnerEditRule"));
	};
	var initDropDown = function(){
		createJqxDropDownList(globalVar.ruleEnumArr, $("#enumRuleList"), "enumId", "description", 25, "96%");
		createJqxDropDownList(globalVar.enumOperatorGreaterArr, $("#operatorInputFromEdit"), "enumId", "enumCode", 25, "99%");
		createJqxDropDownList(globalVar.enumOperatorLessThanArr, $("#operatorInputToEdit"), "enumId", "enumCode", 25, "99%");
	};
	var initSimpleInput = function(){
		$("#valueFromEdit").jqxNumberInput({ width: "99%", height: 25, digits: 3, spinButtons: true, min: 0, decimalDigits: 0, symbolPosition: 'right', symbol: '%'});
		$("#ignoreFromEdit").jqxCheckBox({ width: "99%", height: 25});
		$("#valueToEdit").jqxNumberInput({ width: "99%", height: 25, digits: 3, spinButtons: true, min: 0, decimalDigits: 0, symbolPosition: 'right', symbol: '%'});
		$("#ignoreToEdit").jqxCheckBox({ width: "99%", height: 25});
		$("#valueActionEdit").jqxNumberInput({ width: "100%", height: 25, spinButtons: true, min: 0, digits: 3, decimalDigits: 1, symbolPosition: 'right', symbol: '%'});
	};
	var initJqxValidator = function(){
		$("#EditDistPolicyRuleWindow").jqxValidator({
			rules: [
				{input : '#enumRuleList', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#operatorInputFromEdit', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						var checked = $("#ignoreFromEdit").jqxCheckBox('checked');
						if(!checked){
							if(!input.val()){
								return false;
							}
						}
						return true;
					}
				},
				{input : '#operatorInputToEdit', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						var checked = $("#ignoreToEdit").jqxCheckBox('checked');
						if(!checked){
							if(!input.val()){
								return false;
							}
						}
						return true;
					}
				},
				{input : '#valueFromEdit', message : uiLabelMap.ValueIsInvalid, action: 'none', 
					rule : function(input, commit){
						var checkedFrom = $("#ignoreFromEdit").jqxCheckBox('checked');
						var checkedTo = $("#ignoreToEdit").jqxCheckBox('checked');
						if(!checkedFrom && !checkedTo){
							var valueFromEdit = input.val();
							var valueToEdit = $("#valueToEdit").val(); 
							if(valueFromEdit >= valueToEdit){
								return false;
							}
						}
						return true;
					}
				},
				{input : '#valueToEdit', message : uiLabelMap.ValueIsInvalid, action: 'none', 
					rule : function(input, commit){
						var checkedFrom = $("#ignoreFromEdit").jqxCheckBox('checked');
						var checkedTo = $("#ignoreToEdit").jqxCheckBox('checked');
						if(!checkedFrom && !checkedTo){
							var valueFromEdit = $("#valueFromEdit").val();
							var valueToEdit = input.val(); 
							if(valueFromEdit >= valueToEdit){
								return false;
							}
						}
						return true;
					}
				},
				{input : '#ignoreFromEdit', message : uiLabelMap.ConditionIsNotSetting, action: 'none', 
		        	rule : function(input, commit){
		        		var checkedFrom = $("#ignoreFromEdit").jqxCheckBox('checked');
		        		var checkedTo = $("#ignoreToEdit").jqxCheckBox('checked');
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
		$("#EditDistPolicyRuleWindow").on('open', function(event){
			$("#operatorInputFromEdit").jqxDropDownList({selectedIndex: 0});
			$("#operatorInputToEdit").jqxDropDownList({selectedIndex: 0});
			$("#ignoreToEdit").jqxCheckBox({checked: false});
			$("#ignoreFromEdit").jqxCheckBox({checked: false});
			$("#enumRuleList").jqxDropDownList({selectedIndex: 0});
		});
		$("#EditDistPolicyRuleWindow").on('close', function(event){
			Grid.clearForm($(this));
			//_gridId = "";
		});
		$("#ignoreToEdit").on('change', function (event){
			var checked = event.args.checked;
			$("#operatorInputToEdit").jqxDropDownList({disabled: checked});
			$("#valueToEdit").jqxNumberInput({disabled: checked});
		});
		$("#ignoreFromEdit").on('change', function (event){
			var checked = event.args.checked;
			$("#operatorInputFromEdit").jqxDropDownList({disabled: checked});
			$("#valueFromEdit").jqxNumberInput({disabled: checked});
		});
		$("#cancelEditCond").click(function(event){
			$("#EditDistPolicyRuleWindow").jqxWindow('close');
		});
		$("#saveEditCond").click(function(event){
			var valid = $("#EditDistPolicyRuleWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var checkedFrom = $("#ignoreFromEdit").jqxCheckBox('checked');
			var checkedTo = $("#ignoreToEdit").jqxCheckBox('checked');
			var row = {};
			if(!checkedFrom){
				row.valueFrom = $("#valueFromEdit").val();
				row.operatorEnumIdFrom = $("#operatorInputFromEdit").val();
			}
			if(!checkedTo){
				row.valueTo = $("#valueToEdit").val();
				row.operatorEnumIdTo = $("#operatorInputToEdit").val();
			}
			row.actionValue = $("#valueActionEdit").val();
			addRuleToSalesBonusPolicy({ruleData: JSON.stringify([row]), ruleEnumId: $("#enumRuleList").val()});
			
		});
	};
	var addRuleToSalesBonusPolicy = function(data){
		data.salesBonusPolicyId = _salesBonusPolicyId;
		disableAll();
		$.ajax({
			url: 'addRuleToSalesBonusPolicy',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#EditDistPolicyRuleWindow").jqxWindow('close');
					viewListBonusPolicy.fillGridData($("#" + _gridId), _salesBonusPolicyId);
					_gridId = "";
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
		$("#loadingEditRule").show();
		$("#cancelEditCond").attr("disabled", "disabled");
		$("#saveEditCond").attr("disabled", "disabled");
	};
	var enableAll = function(){
		$("#loadingEditRule").hide();
		$("#cancelEditCond").removeAttr("disabled");
		$("#saveEditCond").removeAttr("disabled");
	};
	var initJqxWindow = function(){
		createJqxWindow($("#EditDistPolicyRuleWindow"), 455, 270);
	};
	var openWindow = function(gridId, salesBonusPolicyId){
		_gridId = gridId; 
		_salesBonusPolicyId = salesBonusPolicyId;
		openJqxWindow($("#EditDistPolicyRuleWindow"));
	};
	 
	return {
		openWindow: openWindow,
		init: init
	}
}());

$(document).ready(function(){
	editSaleBonusPolicyRuleObj.init();
});