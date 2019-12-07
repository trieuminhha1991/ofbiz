var rowUpdateIndex = -1;
theme = 'olbius';

var viewEmplPosTypeSalObject = (function(){
	var init = function(){
			initJqxDateTimeInput();
			initJqxDropDownList(); 
			initJqxNumberInput(); 
			initJqxGridEvent();
			initJqxValidator();
			initBtnEvent();
			initJqxInput();
			if(!globalVar.useRoleTypeGroup){
				initJqxTreeBtn();
				initJqxTreeEvent();
			}
			initJqxWindow();
	};
	
	var initJqxTreeBtn = function(){
		var config = {dropDownBtnWidth: 288, treeWidth: 288};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#jqxDropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
	};
	
	var initJqxTreeEvent = function(){
		$("#jqxTree").on('select', function(event){
			var item = $('#jqxTree').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTree"), $("#jqxDropDownButton"));
			var partyId = item.value;
			getEmplPositionTypeInOrg(partyId, $("#setSalaryEmplPositionTypeId"));
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
	
	var initJqxGridEvent = function(){
		$("#jqxgrid").on('rowdoubleclick', function (event){
			var rowBoundIndex = event.args.rowindex;
			var data = $('#jqxgrid').jqxGrid('getrowdata', rowBoundIndex);
			fillDataInWindow(data);
			openJqxWindow($('#editEmplPosTypeRateWindow'));
		});
	};
	
	var initJqxInput = function(){
		if(!globalVar.useRoleTypeGroup){
			$("#emplPositionTypeIdEdit").jqxInput({width: '97%', height: 20, disabled: true, valueMember: "description", displayMember: 'emplPositionTypeId'});
		}
	}
	
	var initJqxDropDownList = function(){
		//createJqxDropDownList(uomArray, $("#CurrencyUomIdSalary"), 'uomId', 'description', 25, '98%');
		createJqxDropDownList(periodTypeArr, $("#periodTypeNew"), 'periodTypeId', 'description', 25, '98%');
		createJqxDropDownList(periodTypeArr, $("#periodTypeSalary"), 'periodTypeId', 'description', 25, '98%');
		createJqxDropDownList([], $("#setSalaryEmplPositionTypeId"), 'emplPositionTypeId', 'description', 25, '98%');
		if(globalVar.useRoleTypeGroup){
			createJqxDropDownList([], $("#emplPositionTypeIdEdit"), 'emplPositionTypeId', 'description', 25, '98%');
			createJqxDropDownList(roleTypeGroupArr, $("#roleTypeGroupDropDown"), 'roleTypeGroupId', 'description', 25, '98%');	
			createJqxDropDownList(roleTypeGroupArr, $("#roleTypeGroupEdit"), 'roleTypeGroupId', 'description', 25, '98%');
		}
		initJqxDropdownlistEvent();
	};
	
	var initJqxDropdownlistEvent = function(){
		if(globalVar.useRoleTypeGroup){
			$("#roleTypeGroupDropDown").on('select', function (event){
				$("#setSalaryEmplPositionTypeId").jqxDropDownList({ disabled: true });
				var args = event.args;
				if(args){
					var value = args.item.value;
					$.ajax({
						url: "getEmplPositionTypeByRoleTypeGroup",
						data: {roleTypeGroupId: value},
						type: 'POST',
						success: function(response){
							if(response.responseMessage == "success"){
								$("#setSalaryEmplPositionTypeId").jqxDropDownList('clearSelection');
								var dataArr = response.listReturn;
								updateSourceDropdownlist($("#setSalaryEmplPositionTypeId"), dataArr);
							}else{
								bootbox.dialog(response.errorMessage,
										[
										 {
											 "label" : uiLabelMap.CommonClose,
											 "class" : "btn-danger icon-remove btn-mini",
											 "callback": function() {
												 
											 }
										 }
										 ]	 
								);
							}
						},
						complete: function(jqXHR, textStatus){
							$("#setSalaryEmplPositionTypeId").jqxDropDownList({ disabled: false});
						}
					});
				}
			});
			$("#roleTypeGroupEdit").on('select', function (event){
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
											 "class" : "btn-danger icon-remove btn-mini",
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
	
	var initJqxDateTimeInput = function(){
		$("#payrollFromDate").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
		$("#payrollFromDate").jqxDateTimeInput({disabled: false});
		$("#payrollThruDate").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius', showFooter: true});
		$("#payrollThruDate").val(null);
		
		$("#fromDateNew").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
		$("#thruDateNew").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius', showFooter: true});
		$("#thruDateNew").val(null);
		
		$("#dateTimeInput").jqxDateTimeInput({ width: 250, height: 25,  selectionMode: 'range', theme: 'olbius'});
		var fromDate = new Date(globalVar.monthStart);
		var thruDate = new Date(globalVar.monthEnd);
		$("#dateTimeInput").jqxDateTimeInput('setRange', fromDate, thruDate);
		$("#dateTimeInput").on('change', function(event){
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			var fromDate = selection.from.getTime();
		    var thruDate = selection.to.getTime();
		    refreshGridData(fromDate, thruDate);
		});
	};
	
	var submitCreateEmplPosTypeRateForm = function(){
	    //$("#alterSave").attr("disabled", "disabled");
		var thruDate = $("#thruDateNew").jqxDateTimeInput('getDate');
		 
		var row = {
			emplPositionTypeId: $('#setSalaryEmplPositionTypeId').val(),
			periodTypeId: $("#periodTypeNew").val(),
			rateAmount: $("#amountValueNew").val(),
			fromDate: $("#fromDateNew").jqxDateTimeInput('getDate'),
			thruDate: thruDate,
		};
		if(globalVar.useRoleTypeGroup){
			row.roleTypeGroupId = $("#roleTypeGroupDropDown").val();
		}
		if(globalVar.setupByGeo){
			var includeSelectedGeoArr = createGeoTreeForCreateNewObject.getSelectedGeoInclude();
			var excludeSelectedGeoArr = createGeoTreeForCreateNewObject.getSelectedGeoExclude();
			row.includeGeoId = JSON.stringify(includeSelectedGeoArr);
			row.excludeGeoId = JSON.stringify(excludeSelectedGeoArr);
		}
		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
		$("#popupWindowEmplPosTypeRate").jqxWindow('close');
	};

	var initBtnEvent = function(){
		$("#alterSave").click(function(){
			var valid = $("#createEmplPosTypeRateForm").jqxValidator('validate');
			if(!valid){
				return false;
			}
			bootbox.dialog(uiLabelMap.AddRowDataConfirm,
				[
					{
						"label": uiLabelMap.CommonSubmit,
						"class" : "icon-ok btn btn-primary btn-small",
						"callback": function(){
							submitCreateEmplPosTypeRateForm();
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
		 $("#alterCancel").click(function(){
			 $("#popupWindowEmplPosTypeRate").jqxWindow('close');
		 });
		 
		 $("#submitForm").click(function(){
			var valid = $("#editEmplPosTypeRateForm").jqxValidator('validate');
			if(!valid){
				return false;
			}
			bootbox.dialog(uiLabelMap.EmplPositionTypeRateGeoApplUpdate,
			[
				{
					"label": uiLabelMap.CommonSubmit,
					"class" : "icon-ok btn btn-small btn-primary",
					"callback": function(){
						submitEditEmplPosTypeRateForm();
					}
				},
				{
					"label" : uiLabelMap.CommonCancel,
	    			"class" : "btn-danger icon-remove btn-small",
	    		 	"callback": function() {
	    		 		//$('#editEmplPosTypeRateWindow').jqxWindow('close');
	    		    }
				}
			]);
		 });
		 $("#cancelSubmit").click(function(){
			 $('#editEmplPosTypeRateWindow').jqxWindow('close');
		 });
		 
		 $("#addNew").click(function(event){
			openJqxWindow($("#popupWindowEmplPosTypeRate")); 
		 });
		 $("#removeFilter").click(function(){
			 $("#jqxgrid").jqxGrid('clearfilters');
		 })
	};

	var submitEditEmplPosTypeRateForm = function (){
	    var dataSubmit = new Array();
		var emplPositionTypeId = $("#emplPositionTypeIdEdit").val().value;
		var fromDate = $("#payrollFromDate").jqxDateTimeInput('getDate').getTime();
		//var uomId = $("#CurrencyUomIdSalary").jqxDropDownList('getSelectedItem').value;
		var periodTypeId = $("#periodTypeSalary").jqxDropDownList('getSelectedItem').value;
		var amount = $("#amountSalary").val();
		if(globalVar.setupByGeo){
			var includeSelectedGeoArr = createGeoTreeForEditObject.getSelectedGeoInclude();
			var excludeSelectedGeoArr = createGeoTreeForEditObject.getSelectedGeoExclude(); 
			dataSubmit.push({name: "includeGeoId", value: JSON.stringify(includeSelectedGeoArr)});
			dataSubmit.push({name: "excludeGeoId", value: JSON.stringify(excludeSelectedGeoArr)});
		}
		dataSubmit.push({name: "emplPositionTypeRateId", value: $("#emplPositionTypeRateId").val()});		 
		dataSubmit.push({name: "fromDate", value: fromDate});		 
		/* dataSubmit.push({name: "uomId", value: uomId}); */
		dataSubmit.push({name: "periodTypeId", value: periodTypeId});
		dataSubmit.push({name: "rateAmount", value: amount});
		dataSubmit.push({name: "emplPositionTypeId", value: emplPositionTypeId});
		dataSubmit.push({name: "roleTypeGroupId", value: $("#roleTypeGroupEdit").val()});
		
		if($("#payrollThruDate").val()){
			dataSubmit.push({name: "thruDate", value: $("#payrollThruDate").jqxDateTimeInput('getDate').getTime()});
		}
		$.ajax({
			url: "updateEmplPositionTypeRateGeoAppl",
			type: 'POST',
			data: dataSubmit,
			success: function(data){
				$("#updateNotificationSalary").jqxNotification('closeLast');
				if(data.responseMessage == "success"){
					$("#notificationText").html(data.successMessage);
					$("#updateNotificationSalary").jqxNotification({ template: 'info' });
					$("#updateNotificationSalary").jqxNotification('open');
					$('#jqxgrid').jqxGrid("updatebounddata");
				}else{
					$("#notificationText").html(data.errorMessage);
					$("#updateNotificationSalary").jqxNotification({template: 'info' });
					$("#updateNotificationSalary").jqxNotification('open');
				}	
			}
		 });
		$('#editEmplPosTypeRateWindow').jqxWindow('close');
	}



	var initJqxValidator = function(){
		var rulesCreateNew = [
				{input: '#amountValueNew', message: uiLabelMap.AmountValueGreaterThanZero, action: 'blur', 
					rule: function (input, commit){
						var value = input.val();
						
						if(value < 0){
							return false
						}
						return true;
					}	
				},
				{input: '#setSalaryEmplPositionTypeId', message: uiLabelMap.CommonRequired, action: 'blur', 
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
			rulesCreateNew.push({input: '#roleTypeGroupDropDown', message: uiLabelMap.CommonRequired, action: 'blur', 
				rule: function (input, commit){
					var value = input.val();
					if(!value){
						return false;
					}
					return true;
				}	
			});
		}
		
		var rulesEdit = [
 			{input: '#amountSalary', message: uiLabelMap.AmountValueGreaterThanZero, action: 'blur', 
 				rule: function (input, commit){
 					var value = input.val();
 					
 					if(value < 0){
 						return false
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
 			}
         ];
		
		if(globalVar.useRoleTypeGroup){
			rulesEdit.push({input: '#roleTypeGroupEdit', message: uiLabelMap.CommonRequired, action: 'blur', 
 				rule: function (input, commit){
 					var value = input.val();
 					if(!value){
 						return false;
 					}
 					return true;
 				}	
 			});
		}
		
		if(globalVar.setupByGeo){
			rulesCreateNew = $.merge(createGeoTreeForCreateNewObject.addValidator(), rulesCreateNew);
			rulesEdit = $.merge(createGeoTreeForEditObject.addValidator(), rulesEdit);
		}
		
		$("#createEmplPosTypeRateForm").jqxValidator({
	 		rules: rulesCreateNew
	 	});
		
	 	$("#editEmplPosTypeRateForm").jqxValidator({
	 		rules: rulesEdit
	 	});
	};

	var fillDataInWindow = function(data){
		var emplPosDes = '';
		var roleTypeGroup = '';
		//rowSelectedIndex = rowBoundIndex;	
		globalVar.emplPositionTypeId = data.emplPositionTypeId;
		$("#emplPositionTypeRateId").val(data.emplPositionTypeRateId);
		if(globalVar.setupByGeo){
			createGeoTreeForEditObject.initDataJqxTreeInWindow(data);
		}
		if(globalVar.useRoleTypeGroup){
			$("#roleTypeGroupEdit").jqxDropDownList('val', data.roleTypeGroupId);
		}else{
			for(i=0; i < emplPosTypeArr.length; i++){
				if(emplPosTypeArr[i].emplPositionTypeId == data.emplPositionTypeId){
					$("#emplPositionTypeIdEdit").val({label: emplPosTypeArr[i].description, value: emplPosTypeArr[i].emplPositionTypeId});
					break;
				}
			}
		}
		if(data.thruDate){
			$("#payrollThruDate").jqxDateTimeInput('val', data.thruDate);	
		}else{
			$("#payrollThruDate").val(null);
		}	
		$("#periodTypeSalary").jqxDropDownList('selectItem', data.periodTypeId);
		$("#payrollFromDate").jqxDateTimeInput('val', data.fromDate);			
		$("#amountSalary").val(data.rateAmount);
	}


	var refreshGridData = function(fromDate, thruDate){
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?hasrequest=Y&sname=JQListEmplPositionTypeRate&fromDate=" + fromDate + "&thruDate=" + thruDate;
		$("#jqxgrid").jqxGrid('source', tmpS);
	};

	var initJqxNumberInput = function(){
		$("#amountSalary").jqxNumberInput({ width: '98%', height: '25px', spinButtons: false, decimalDigits: 0, digits: 9, max: 999999999, theme: 'olbius'});
		$("#amountValueNew").jqxNumberInput({ width: '98%', height: '25px', spinButtons: false, decimalDigits: 0, digits: 9, max: 999999999, theme: 'olbius'});
	};
	
	var initJqxWindow = function(){
		$('#editEmplPosTypeRateWindow').jqxWindow({
	        showCollapseButton: false, isModal:true, height: 310, width: 530, minWidth: 530, theme:'olbius',
	        autoOpen: false,  modalZIndex: 10000,
	        initContent: function () {
	            
	        }
	    });
		 $("#popupWindowEmplPosTypeRate").jqxWindow({showCollapseButton: false, autoOpen: false,
				height: 340, width: 530, isModal: true, theme:theme,
		 });
		 
		$("#popupWindowEmplPosTypeRate").on('open', function(event){
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			$("#fromDateNew").val(selection.from);
			if (globalVar.defaultPeriodTypeId){
				$("#periodTypeNew").val(globalVar.defaultPeriodTypeId);
			}
			if(globalVar.rootPartyArr.length > 0){
				$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
			}
		});
		 
		$("#popupWindowEmplPosTypeRate").on('close', function(event){
			if(globalVar.setupByGeo){
				createGeoTreeForCreateNewObject.uncheckAll();
			}
			Grid.clearForm($(this));
		});
		 
		$('#editEmplPosTypeRateWindow').on('close', function(event){
			if(globalVar.setupByGeo){
				createGeoTreeForEditObject.uncheckAll();
			}
			Grid.clearForm($(this));
		});
		
		 
		 $("#updateNotificationSalary").jqxNotification({
	         width: "100%", position: "top-left", opacity: 1, appendContainer: "#appendNotification",
	         autoOpen: false, animationOpenDelay: 800, autoClose: true
	     });
		 
		 $("#jqxgrid").on('cellEndEdit', function (event){
			 rowUpdateIndex = event.args.rowindex;
		 });
	};
	
	return{
		init: init
	}
	
}());

$(document).ready(function () {
	viewEmplPosTypeSalObject.init();
});		

