var theme = "olbius";
var elementCheckInJqxTreeGeo = [];

var viewListPayrollParamPosTypeObject = (function(){
	var init = function(){
			loadCustomControlAdvance();
			initJqxDropDownList(); 
			initJqxDateTimeInput();
			initJqxNumberInput();
			initJqxInput();
			initJqxValidator();
			if(!globalVar.useRoleTypeGroup){
				initJqxTreeBtn();
				initJqxTreeEvent();
			}
			btnEvent();
			initJqxGirdEvent();
			initJqxWindow();
	};
	
	var initJqxInput = function(){
		if(!globalVar.useRoleTypeGroup){
			$("#emplPositionTypeIdEdit").jqxInput({width: '97%', height: 20, disabled: true});
		}
	}
	
	var loadCustomControlAdvance = function(){
		$("#jqxgrid").on("loadCustomControlAdvance", function(){
			$("#dateTimeInput").jqxDateTimeInput({ width: 220, height: 25,  selectionMode: 'range', theme: 'olbius'});
			var fromDate = new Date(globalVar.monthStart);
			var thruDate = new Date(globalVar.monthEnd);
			$("#dateTimeInput").jqxDateTimeInput('setRange', fromDate, thruDate);
			$("#dateTimeInput").on('change', function(event){
				var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
				var fromDate = selection.from.getTime();
			    var thruDate = selection.to.getTime();
			    refreshGridData(fromDate, thruDate);
			});
		});
	};
	
	var initJqxGirdEvent = function(){
		$("#jqxgrid").on('rowdoubleclick', function (event){
			$("#popupWindowEdit").jqxWindow('open');
		});
	};
	
	var initJqxValidator = function(){
		var rules = [
						{input: '#amountValueNew', message: uiLabelMap.AmountValueGreaterThanZero, action: 'blur', 
							rule: function (input, commit){
								var value = input.val();
								if(value < 0){
									return false
								}
								return true;
							}	
						},
						{input: '#codeAddNew', message: uiLabelMap.CommonRequired, action: 'blur', 
							rule: function (input, commit){
								var value = input.val();
								if(!value){
									return false;
								}
								return true;
							}	
						},
						{input: '#emplPositionTypeIdAddNew', message: uiLabelMap.CommonRequired, action: 'blur', 
							rule: function (input, commit){
								var value = input.val();
								if(!value){
									return false;
								}
								return true;
							}	
						},
						{input: '#amountValueNew', message: uiLabelMap.AmountValueGreaterThanZero, action: 'blur', 
							rule: function (input, commit){
								var value = input.val();
								if(value <= 0){
									return false;
								}
								return true;
							}	
						}
			        ];
		if(globalVar.useRoleTypeGroup){
			rules.push({input: '#roleTypeGroupId', message: uiLabelMap.CommonRequired, action: 'blur', 
				rule: function (input, commit){
					var value = input.val();
					if(!value){
						return false;
					}
					return true;
				}	
			})
		}
		
		$("#createPayrollParamPositionTypeForm").jqxValidator({
	 		rules: rules
	 	});
		
		var rulesEdit = [
		 				{input: '#amountValueEdit', message: uiLabelMap.AmountValueGreaterThanZero, action: 'blur', 
							rule: function (input, commit){
								var value = input.val();
								if(value <= 0){
									return false
								}
								return true;
							}	
						},
						{input: '#codeEdit', message: uiLabelMap.CommonRequired, action: 'blur', 
							rule: function (input, commit){
								var value = input.val();
								if(!value){
									return false;
								}
								return true;
							}	
						},
						{input: '#emplPositionTypeIdEdit', message: uiLabelMap.CommonRequired, action: 'blur', 
							rule: function (input, commit){
								var value = input.val();
								if(!value){
									return false;
								}
								return true;
							}	
						},
			        ];
		
		if(globalVar.useRoleTypeGroup){
			rulesEdit.push({input: '#roleTypeGroupIdEdit', message: uiLabelMap.CommonRequired, action: 'blur', 
				rule: function (input, commit){
					var value = input.val();
					if(!value){
						return false;
					}
					return true;
				}	
			});
		}
		$("#editPayrollParamPositionTypeForm").jqxValidator({
	 		rules: rulesEdit
	 	});
	};
	
	var initJqxWindow = function(){
		$("#popupWindowAddNew").jqxWindow({showCollapseButton: false, autoOpen: false, height: 390, width: 550, isModal: true, theme:theme});
		$("#popupWindowEdit").jqxWindow({showCollapseButton: false, autoOpen: false, height: 350, width: 550, isModal: true, theme:theme});
		$("#popupWindowAddNew").on('open', function (event){
			$("#amountValueNew").val(0);
			$("#thruDateNew").val(null);
			$("#alterSave").removeAttr('disabled');
			$("#fromDateNew").val(new Date(globalVar.monthStart));
			/*if(globalVar.rootPartyArr.length > 0){
				$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
			}*/
		});
		$("#popupWindowAddNew").on('close', function (event){
			Grid.clearForm($(this));
			$("#periodTypeNew").html(uiLabelMap.HRCommonNotSetting);
			$("#createPayrollParamPositionTypeForm").jqxValidator('hide');
			$("#jqxTree").jqxTree("selectItem", null);
		});
		$("#popupWindowEdit").on('open', function(event){
			var rowIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			if(rowIndex > -1){
				var data = $('#jqxgrid').jqxGrid('getrowdata', rowIndex);
				fillDataInWindowEdit(data);				
			}
		});
		
		$("#popupWindowEdit").on('close', function(event){
			Grid.clearForm($(this));
			$("#editPayrollParamPositionTypeForm").jqxValidator('hide')
		});
	};
	
	var initJqxNumberInput = function(){
		$("#amountValueNew, #amountValueEdit").jqxNumberInput({ width: '98%', height: '25px', spinButtons: false, decimalDigits: 0, digits: 9, max: 999999999, theme: 'olbius', min: 0});		
	};

	var initJqxDateTimeInput = function(){
		$("#fromDateNew, #fromDateEdit").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
		$("#thruDateNew, #thruDateEdit").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius', showFooter: true});
		$("#thruDateNew").val(null);
	}; 
	
	var initJqxTreeBtn = function(){
		var config = {dropDownBtnWidth: 298, treeWidth: 298};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#jqxDropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
		//globalObject.createJqxTreeDropDownBtn($("#jqxTreeEdit"), $("#jqxDropDownButtonEdit"), globalVar.rootOrgId, globalVar.groupName, "treeEdit", "treeChildEdit", config);
	};
	
	var initJqxTreeEvent = function(){
		$("#jqxTree").on('select', function(event){
			var item = $('#jqxTree').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTree"), $("#jqxDropDownButton"));
			var partyId = item.value;
			getEmplPositionTypeInOrg(partyId, $("#emplPositionTypeIdAddNew"));
		});
	};
	
	var getEmplPositionTypeInOrg = function(partyGroupId, dropDownListEle){
		dropDownListEle.jqxDropDownList({disabled: true});
		$.ajax({
			url: 'getListEmplPositionTypeByParty',
			data: {partyId: partyGroupId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == 'success'){
					updateSourceDropdownlist(dropDownListEle, response.listReturn);
				}
			},
			complete: function(jqXHR, status){
				dropDownListEle.jqxDropDownList({disabled: false});
			}
		});
	};
	 
	var initJqxDropDownList = function(){
		if(globalVar.useRoleTypeGroup){
			createJqxDropDownList(roleTypeGroupArr, $("#roleTypeGroupId"), 'roleTypeGroupId', 'description', 25, '98%');
			createJqxDropDownList(roleTypeGroupArr, $("#roleTypeGroupIdEdit"), 'roleTypeGroupId', 'description', 25, '98%');
			createJqxDropDownList([], $("#emplPositionTypeIdEdit"), 'emplPositionTypeId', 'description', 25, '98%');
		}
		createJqxDropDownList([], $("#emplPositionTypeIdAddNew"), 'emplPositionTypeId', 'description', 25, '98%');
		createJqxDropDownList(parametersArr, $("#codeAddNew"), 'code', 'name', 25, '98%');
		createJqxDropDownList(parametersArr, $("#codeEdit"), 'code', 'name', 25, '98%');
		createJqxDropDownList(periodTypeArr, $("#periodTypeEdit"), 'periodTypeId', 'description', 25, '98%');
		
		$('#codeEdit').on('select', function (event){
			var args = event.args;
		    if (args) {
			    var index = args.index;
			    var item = args.item;
				var type = parametersArr[index].type;
				if(type == 'CONSTPERCENT'){
		    		$("#amountValueEdit").jqxNumberInput({digits: 3, symbolPosition: 'right', symbol: '%'});
		    		$("#amountLabelEdit").text(uiLabelMap.HrCommonRates);
		    		$("#amountValueEdit").val(0);
		    	}else{
		    		$("#amountValueEdit").jqxNumberInput({decimalDigits: 0, digits: 9, max: 999999999, theme: 'olbius', min: 0, symbol: ''});
		    		$("#amountLabelEdit").text(uiLabelMap.HRCommonAmount);
		    		$("#amountValueEdit").val(0);
		    	}
			}                        
		});
		$('#codeAddNew').on('select', function (event){
			var args = event.args;
		    if (args) {
			    var index = args.index;
			    var item = args.item;
				var type = parametersArr[index].type;				
				var periodTypeId = parametersArr[index].periodTypeId;
				for(var i = 0; i < periodTypeArr.length; i++){
					if(periodTypeId == periodTypeArr[i].periodTypeId){
						$("#periodTypeNew").html(periodTypeArr[i].description);
						break;
					}
				}
				if(type == 'CONSTPERCENT'){
		    		$("#amountValueNew").jqxNumberInput({digits: 3, symbolPosition: 'right', symbol: '%'});
		    		$("#amountLabelNew").text(uiLabelMap.HrCommonRates);
		    	}else{
		    		$("#amountValueNew").jqxNumberInput({decimalDigits: 0, digits: 9, max: 999999999, theme: 'olbius', min: 0, symbol: ''});
		    		$("#amountLabelNew").text(uiLabelMap.HRCommonAmount);
		    	}
				
			}                        
		});
		
		if(globalVar.useRoleTypeGroup){
			$("#roleTypeGroupId").on('select', function (event){
				$("#emplPositionTypeIdAddNew").jqxDropDownList({ disabled: true });
				var args = event.args;
				if(args){
					var value = args.item.value;
					$.ajax({
						url: "getEmplPositionTypeByRoleTypeGroup",
						data: {roleTypeGroupId: value},
						type: 'POST',
						success: function(response){
							if(response.responseMessage == "success"){
								$("#emplPositionTypeIdAddNew").jqxDropDownList('clearSelection');
								var dataArr = response.listReturn;
								updateSourceDropdownlist(dataArr, $("#emplPositionTypeIdAddNew"));
							}else{
								bootbox.dialog(response.errorMessage,
										[
											{
												"label" : uiLabelMap.CommonClose,
								    			"class" : "btn-danger icon-remove btn-small",
								    		 	"callback": function() {
								    		   
								    		    }
											}
										]	 
									);
							}
						},
						complete: function(jqXHR, textStatus){
							$("#emplPositionTypeIdAddNew").jqxDropDownList({ disabled: false});
						}
					});
				}
			});
			
			$("#roleTypeGroupIdEdit").on('select', function (event){
				$("#emplPositionTypeIdEdit").jqxDropDownList({ disabled: true });
				var args = event.args;
				if(args){
					var value = args.item.value;
					$.ajax({
						url: "getEmplPositionTypeByRoleTypeGroup",
						data: {roleTypeGroupId: value},
						type: 'POST',
						success: function(response){
							if(response.responseMessage == "success"){
								$("#emplPositionTypeIdEdit").jqxDropDownList('clearSelection');
								var dataArr = response.listReturn;
								var tmpSource = {
										localdata: dataArr,
										datatype: "array"
								}
								var tmpDataAdapter = new $.jqx.dataAdapter(tmpSource);
								$("#emplPositionTypeIdEdit").jqxDropDownList({source: tmpDataAdapter});
								if(dataArr.length < 8){
									$("#emplPositionTypeIdEdit").jqxDropDownList({autoDropDownHeight: true});
								}else{
									$("#emplPositionTypeIdEdit").jqxDropDownList({autoDropDownHeight: false});
								}
								if(globalVar.emplPositionTypeId){
									$("#emplPositionTypeIdEdit").jqxDropDownList('val', globalVar.emplPositionTypeId);
								}
							}else{
								bootbox.dialog(response.errorMessage,
										[
										 {
											 "label" : uiLabelMap.CommonClose,
											 "class" : "btn-danger icon-remove btn-small",
											 "callback": function() {
												 
											 }
										 }
										 ]	 
								);
							}
						},
						complete: function(jqXHR, textStatus){
							$("#emplPositionTypeIdEdit").jqxDropDownList({ disabled: false});
						}
					});
				}
			});
		}
	};

	var btnEvent = function(){
		$("#alterCancel").click(function(){
			 $("#popupWindowAddNew").jqxWindow('close');
		});
		
		$("#editCancel").click(function(event){
			$("#popupWindowEdit").jqxWindow('close');
		});
		$("#alterSave").click(function(){
			var valid = $("#createPayrollParamPositionTypeForm").jqxValidator('validate');
			if(!valid){
				return false;
			}
			bootbox.dialog(uiLabelMap.AddRowDataConfirm,
				[
					{
						"label": uiLabelMap.CommonSubmit,
						"class" : "icon-ok btn btn-small btn-primary",
						"callback": function(){
							submitCreatePayrollParamEmplPosType();
						}
					},
					{
						"label" : uiLabelMap.CommonCancel,
						"class" : "btn-danger icon-remove btn-small",
					 	"callback": function() {
					   
					   }
					} 
				]		
			);
		});
		$("#editSave").click(function(){
			var valid = $("#editPayrollParamPositionTypeForm").jqxValidator('validate');
			if(!valid){
				return false;
			}
			bootbox.dialog(uiLabelMap.EmplPositionTypeRateGeoApplUpdate,
				[
					{
						"label": uiLabelMap.CommonSubmit,
						"class" : "icon-ok btn btn-small btn-primary",
						"callback": function(){
							submitEditPayrollParamEmplPosType();
						}
					},
					{
						"label" : uiLabelMap.CommonCancel,
						"class" : "btn-danger icon-remove btn-small",
					 	"callback": function() {
					   
					   }
					} 
				]		
			);
		 });
	};
	
	var fillDataInWindowEdit = function(data){
		$("#payrollParamPositionTypeId").val(data.payrollParamPositionTypeId);
		globalVar.emplPositionTypeId = data.emplPositionTypeId;
		if(globalVar.useRoleTypeGroup){
			$("#roleTypeGroupIdEdit").jqxDropDownList('val', data.roleTypeGroupId);
		}else{
			for(i=0; i < emplPosTypeArr.length; i++){
				if(emplPosTypeArr[i].emplPositionTypeId == data.emplPositionTypeId){
					$("#emplPositionTypeIdEdit").val(emplPosTypeArr[i].description);
					break;
				}
			}
		}
		$("#fromDateEdit").jqxDateTimeInput('val', data.fromDate);
		$("#codeEdit").jqxDropDownList('val', data.code);
		$("#periodTypeEdit").jqxDropDownList('val', data.periodTypeId);
		if(data.thruDate){
			$("#thruDateEdit").jqxDateTimeInput('val', data.thruDate);	
		}else{
			$("#thruDateEdit").val(null);
		}
		$("#amountValueEdit").val(data.rateAmount);
	};
	
	var submitCreatePayrollParamEmplPosType = function(){
		var thruDate = $("#thruDateNew").jqxDateTimeInput('getDate');
		var row = {
			emplPositionTypeId: $('#emplPositionTypeIdAddNew').val(),
			//periodTypeId: $("#periodTypeNew").val(),
			rateAmount: $("#amountValueNew").val(),
			fromDate: $("#fromDateNew").jqxDateTimeInput('getDate'),
			thruDate: thruDate,
			code: $("#codeAddNew").val(),
		};
		if(globalVar.useRoleTypeGroup){
			row.roleTypeGroupId = $("#roleTypeGroupId").val();
		}
		
		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
		$("#popupWindowAddNew").jqxWindow('close');
	};
	
	var submitEditPayrollParamEmplPosType = function(){
		var rowIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		if(rowIndex > -1){
			var dataSubmit = {};
			var data = $('#jqxgrid').jqxGrid('getrowdata', rowIndex);
			dataSubmit["payrollParamPositionTypeId"] = data.payrollParamPositionTypeId;
			if(globalVar.useRoleTypeGroup){
				dataSubmit["emplPositionTypeId"] = $("#emplPositionTypeIdEdit").jqxDropDownList('getSelectedItem').value;
			}
			dataSubmit["code"] = $("#codeEdit").jqxDropDownList('getSelectedItem').value;
			if(globalVar.useRoleTypeGroup){
				dataSubmit["roleTypeGroupId"] = $("#roleTypeGroupIdEdit").jqxDropDownList('getSelectedItem').value;
			}
			dataSubmit["periodTypeId"] = $("#periodTypeEdit").jqxDropDownList('getSelectedItem').value;
			dataSubmit["fromDate"] = $("#fromDateEdit").jqxDateTimeInput('getDate').getTime();
			if($("#thruDateEdit").val()){
				dataSubmit["thruDate"] = $("#thruDateEdit").jqxDateTimeInput('getDate').getTime();
			}
			dataSubmit["rateAmount"] = $("#amountValueEdit").val();
						
			$.ajax({
				url: "updatePayrollParamPosTypeGeoAppl",
				type: 'POST',
				data: dataSubmit,
				success: function(data){
					var notifyEle = $("#jqxNotify"); 
					notifyEle.jqxNotification('closeLast');
					if(data.responseMessage == "success"){
						$("#jqxNotifyContainer").empty();
						notifyEle.empty();
						notifyEle.jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, 
							autoClose: true, template: "info", appendContainer: "#jqxNotifyContainer"});
						notifyEle.text(data.successMessage);
						notifyEle.jqxNotification("open");
						$('#jqxgrid').jqxGrid("updatebounddata");
					}else{
						$("#jqxNotifyContainer").empty();
						notifyEle.empty();
						notifyEle.jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, 
							autoClose: true, template: "error", appendContainer: "#jqxNotifyContainer"});
						notifyEle.text(data.errorMessage);
						notifyEle.jqxNotification("open");
					}	
				}
			 });
		}
		$("#popupWindowEdit").jqxWindow('close');
	};

	var refreshGridData = function(fromDate, thruDate){
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=JQListParamPosTypeGeo&hasrequest=Y&fromDate=" + fromDate + "&thruDate=" + thruDate;
		$("#jqxgrid").jqxGrid('source', tmpS);
	};
	
	return{
		init: init
	}
	
}());

$(document).ready(function () {
	viewListPayrollParamPosTypeObject.init();
});

function checkItemIfExists(treeCheckEle){
	for(var i = 0; i < elementCheckInJqxTreeGeo.length; i++){
		var element = $("#" + elementCheckInJqxTreeGeo[i])[0];
		var item = treeCheckEle.jqxTree('getItem', element);
		if(item){
			treeCheckEle.jqxTree("checkItem", element, true);
		}
	}						
}

function substringBySeparator(str, separator){
	if(str.indexOf(separator) > -1){
		var separatorLastIndexOf = str.lastIndexOf(separator);
		return str.substring(0, separatorLastIndexOf);		
	}else{
		return str;		
	}
}

