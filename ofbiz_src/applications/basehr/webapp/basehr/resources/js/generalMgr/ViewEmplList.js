var emplListInfo = (function(){
	var init = function(){
		initJqxDateTime();
		initBtnEvent();
		initJqxWindow();
		initJqxTreeBtn();
		initJqxTreeEvent();
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
	};

	var initJqxTreeBtn = function(){
		var config = {dropDownBtnWidth: 250, treeWidth: 250};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#jqxDropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
	};

	var initJqxTreeEvent = function(){
		$("#jqxTree").on('select', function(event){
			var item = $('#jqxTree').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTree"), $("#jqxDropDownButton"));
			var partyId = item.value;
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			var fromDate = selection.from.getTime();
			var thruDate = selection.to.getTime();
			refreshGridData(partyId, fromDate, thruDate);
		});
	};

	var initJqxDateTime = function(){
		$("#dateTimeInput").jqxDateTimeInput({ width: 250, height: 25,  selectionMode: 'range', theme: 'olbius'});
		var fromDate = new Date(globalVar.monthStart);
		var thruDate = new Date(globalVar.monthEnd);
		$("#dateTimeInput").jqxDateTimeInput('setRange', fromDate, thruDate);
		$("#dateTimeInput").on('change', function(event){
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			var fromDate = selection.from.getTime();
		    var thruDate = selection.to.getTime();
		    var item = $("#jqxTree").jqxTree('getSelectedItem');
		    var partyId = item.value;
		    refreshGridData(partyId, fromDate, thruDate);
		});
	};

	var initBtnEvent = function(){
		$("#addNewEmployee").click(function(event){
			openJqxWindow($("#addNewEmployeeWindow"));
		});
        $("#exportExcelEmplList").click(function(event){
            var fromDate = new Date(globalVar.monthStart);
            var thruDate = new Date(globalVar.monthEnd);
                var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
                var fromDate = selection.from.getTime();
                var thruDate = selection.to.getTime();
                var item = $("#jqxTree").jqxTree('getSelectedItem');
                var partyId = item.value;
                exportExcelEmplList(partyId, fromDate, thruDate);
        });
		$("#clearFilterBtn").click(function(event){
			$("#jqxgrid").jqxGrid('clearfilters');
		});
		$("#jqxgrid").on("filter", function (event){

		});
	};

	var initJqxWindow = function(){
		createJqxWindow($("#addNewEmployeeWindow"), 850, 570);
		$("#addNewEmployeeWindow").on('close', function(event){
			if(typeof(resetData) === 'function'){
				resetData();
				hideValidate();
			}
		});
		$("#addNewEmployeeWindow").on('open', function(event){
			$("#fuelux-wizard").wizard('setState');
		});
	}

	var refreshGridData = function(partyGroupId, fromDate, thruDate){
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = '';
		$('#jqxgrid').jqxGrid('gotopage', 0);
		$("#jqxgrid").jqxGrid('source', tmpS);
		tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=getEmployeeListDetailInfo&hasrequest=Y&partyGroupId=" + partyGroupId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
		//tmpS._source.url = "jqxGeneralServicer?sname=JQListEmployeeDetailInfo&partyGroupId=" + partyGroupId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
		$("#jqxgrid").jqxGrid('source', tmpS);
	};
    var exportExcelEmplList = function(partyGroupId, fromDate, thruDate){
        var form = document.createElement("form");
        form.setAttribute("method", "POST");
        form.setAttribute("action", "exportExcelEmplList");
        form.setAttribute("target", "_blank");

		var hiddenField = document.createElement("input");
		hiddenField.setAttribute("type", "hidden");
		hiddenField.setAttribute("name", "partyGroupId");
		hiddenField.setAttribute("value", partyGroupId);
		form.appendChild(hiddenField);

		var hiddenField0 = document.createElement("input");
		hiddenField0.setAttribute("type", "hidden");
		hiddenField0.setAttribute("name", "fromDate");
		hiddenField0.setAttribute("value", fromDate);
		form.appendChild(hiddenField0);

		var hiddenField1 = document.createElement("input");
		hiddenField1.setAttribute("type", "hidden");
		hiddenField1.setAttribute("name", "thruDate");
		hiddenField1.setAttribute("value", thruDate);
		form.appendChild(hiddenField1);

		var hiddenField2 = document.createElement("input");
		hiddenField2.setAttribute("type", "hidden");
		hiddenField2.setAttribute("name", "hasrequest");
		hiddenField2.setAttribute("value", "Y");
		form.appendChild(hiddenField2);

        if(OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)){
            $.each(filterObjData.data, function(key, value){
                var hiddenField3 = document.createElement("input");
                hiddenField3.setAttribute("type", "hidden");
                hiddenField3.setAttribute("name", key);
                hiddenField3.setAttribute("value", value);
                form.appendChild(hiddenField3);
            });
        }

        document.body.appendChild(form);
        form.submit();
    };

	return {
		init: init,
		refreshGridData: refreshGridData,
        exportExcelEmplList: exportExcelEmplList
	}
}());

var contextMenuObject = (function(){
	var init = function(){
		createJqxMenu("contextMenu", 30, 195);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == 'editFamilyInfo'){
            	emplFamilyObj.setData(dataRecord);
            	emplFamilyObj.openWindow();
            }else if(action == "editEmplInfo"){
            	$("#ajaxLoading_editEmplInfo").show();
            	editEmplObject.disabledBtn();
            	editEmplObject.openEditWindow();
            	$.ajax({
            		url: 'getPartyWorkInfo',
            		data: {partyId: dataRecord.partyId},
            		type: 'POST',
            		success: function(response){
        				//var workInfo = response.workInfo;
        				dataRecord.terminationReasonId = response.terminationReasonId;
        				dataRecord.workingStatusId = response.workingStatusId;
        				dataRecord.insuranceSocialNbr = response.insuranceSocialNbr;
        				if("resignDate" in response){
        					dataRecord.resignDate = new Date(response.resignDate);
        				}
        				if("dateParticipateIns" in response){
        					dataRecord.dateParticipateIns = new Date(response.dateParticipateIns);
        				}
        				dataRecord.emplPositionClassType = response.emplPositionClassType;
        				dataRecord.salaryBaseFlat = response.salaryBaseFlat;
        				dataRecord.periodTypeId = response.periodTypeId;
            			if(response.probationaryDeadline){
            				$("#probationaryDeadline").val(new Date(response.probationaryDeadline));
            			}else{
            				$("#probationaryDeadline").val(null);
            			}
            		},
            		error: function(jqXHR, textStatus, errorThrown){

					},
					complete: function(jqXHR, textStatus){
						editEmplObject.setData(dataRecord);
						$("#ajaxLoading_editEmplInfo").hide();
						editEmplObject.enabledBtn();
					}
            	});
            }else if(action == 'editPersonInfoContact'){
            	editEmplInfoCommonObj.openWindow();
            	editEmplInfoCommonObj.showLoading();
            	$.ajax({
            		url: 'getEditEmplInformation',
            		data: {partyId: dataRecord.partyId},
            		type: 'POST',
            		success: function(response){
            			if(response.responseMessage == 'success'){
            				editEmplGeneralInfo.setData(response.personInfo);
            				editPermanentResInfo.setData(response.permanentResInfo);
            				editCurrResInfo.setData(response.currResInfo);
            				editPhoneObj.setData(response.phoneMobileInfo);
            				editEmailObj.setData(response.emailAddressInfo);
            			}else{
            				bootbox.dialog(response.errorMessage,
            						[{
            							"label" : uiLabelMap.CommonClose,
            			    		    "class" : "btn-danger btn-small icon-remove open-sans",
            						}]
            					);
            			}
            		},
            		error: function(jqXHR, textStatus, errorThrown){

					},
            		complete: function(jqXHR, textStatus){
            			editEmplInfoCommonObj.hideLoading();
            		}
            	});
            }else if(action == "changeJobPosition"){
            	editJobPositionObj.openWindow(dataRecord);
            }else if(action == "resetPassword"){
            	resetPasswordObj.openWindow(dataRecord);
            }else if(action == "positionFulfillmentList"){
            	emplPositionFulFillObj.openWindow(dataRecord);
            }else if(action == "infoAccount"){
            	emplInfoAccountObj.openWindow(dataRecord);
            }
		});
	};
	return{
		init: init
	}
}());

var editEmplObject = (function(){
	var _partyId;
	var init = function(){
		initSimpleInput();
		initJqxDateTimeInput();
		initJqxDropDownList();
		initJqxNumberInput();
		initJqxWindow();
		initBtnEvent();
		initJqxValidator();
		create_spinner($("#spinner-ajax_editEmplInfo"));
	};

	var initJqxValidator = function(){
		$("#editEmplInfoWindow").jqxValidator({
			rules: [
				{
				    input: '#dateJoinCompanyUpdate', message: uiLabelMap.NotEnterDateJoinCompany, action: 'blur',
				    rule: function (input, commit) {
				        var thruDate = $("#resignDateUpdate").jqxDateTimeInput('val', 'date');
				        var fromDate = input.jqxDateTimeInput('val', 'date');
				        if(thruDate && !fromDate){
				        	return false;
				        }
				        return true;
				    }
				},
				{
					input: '#dateJoinCompanyUpdate', message: uiLabelMap.DateJoinCompanyMustLessThanResignDate, action: 'blur',
					rule: function (input, commit) {
						var thruDate = $("#resignDateUpdate").jqxDateTimeInput('val', 'date');
				        var fromDate = input.jqxDateTimeInput('val', 'date');
				        if(thruDate && fromDate && thruDate <= fromDate){
				        	return false;
				        }
				        return true;
					}
				},
				{
					input: '#reasonResignUpdate', message: uiLabelMap.FieldRequired, action: 'blur',
					rule: function (input, commit) {
						var workingStatus = $("#statusIdUpdate").val();
						if(workingStatus && workingStatus != "EMPL_WORKING"){
							if(!input.val()){
								return false;
							}
						}
						return true;
					}
				},
				{
					input: '#resignDateUpdate', message: uiLabelMap.FieldRequired, action: 'blur',
					rule: function (input, commit) {
						var workingStatus = $("#statusIdUpdate").val();
						if(workingStatus && workingStatus != "EMPL_WORKING"){
							if(!input.val()){
								return false;
							}
						}
						return true;
					}
				},
				{
					input: '#resignDateUpdate', message: uiLabelMap.ResignDateAfterNowDate, action: 'blur',
					rule: function (input, commit) {
						var workingStatus = $("#statusIdUpdate").val();
						if(workingStatus == "EMPL_WORKING"){
							var resignDate = $(input).jqxDateTimeInput('val', 'date');
							var nowDate = new Date(globalVar.nowTimestamp);
							if(resignDate && resignDate < nowDate){
								return false;
							}
						}
						return true;
					}
				},
			],
			position: 'bottom'
		});
	};

	var initBtnEvent = function(){
		$("#cancelUpdateEmpl").click(function(event){
			$("#editEmplInfoWindow").jqxWindow('close');
		});
		$("#saveUpdateEmpl").click(function(event){
			var valid = $("#editEmplInfoWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var nowDate = new Date(globalVar.nowTimestamp);
			var thruDate = $("#resignDateUpdate").jqxDateTimeInput('val', 'date');
			var workingStatusId = $("#statusIdUpdate").val();
			updateEmploymentInfo();
		});
	};

	var initJqxNumberInput = function(){
		$("#salaryBaseFlatUpdate").jqxNumberInput({width: '45%', height: '25px', spinButtons: false, decimalDigits: 0, digits: 9, max: 999999999, disabled: true, theme: 'olbius'});
	};

	var initSimpleInput = function(){
		$("#partyIdFromUpdate").jqxInput({width: '96%', height: 20, disabled: true, displayMember: "partyId", valueMember: "partyName"});
		$("#emplPositionTypeIdUpdate").jqxInput({width: '96%', height: 20, disabled: true});
		/*$("#emplPositionClassTypeUpdate").jqxInput({width: '96%', height: 20, disabled: true});*/
		$("#insuranceSocialNbrUpdate").jqxInput({width: '96%', height: 20, disabled: false});
		$("#partyIdToUpdate").jqxInput({width: '96%', height: 20, disabled: true});
		$("#employeeNameUpdate").jqxInput({width: '96%', height: 20, disabled: true});
	};
	var initJqxDateTimeInput = function(){
		$("#dateJoinCompanyUpdate").jqxDateTimeInput({width: '98%', height: 25, disabled: true});
		$("#resignDateUpdate").jqxDateTimeInput({width: '98%', height: 25, disabled: false, showFooter: true});
		$("#insParticipateFromUpdate").jqxDateTimeInput({width: '98%', height: 25, disabled: false});
		$("#probationaryDeadline").jqxDateTimeInput({width: '98%', height: 25});
	};

	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.periodTypeArr, $("#periodTypeSalUpdate"), "periodTypeId", "description", 25, "51%");
		$("#periodTypeSalUpdate").jqxDropDownList({disabled: true});
		createJqxDropDownList(globalVar.statusWorkingArr, $("#statusIdUpdate"), "statusId", "description", 25, "98%");
		createJqxDropDownList(globalVar.terminationReasonArr, $("#reasonResignUpdate"), "terminationReasonId", "description", 25, "98%");
		$("#statusIdUpdate").on('select', function(event){
			var args = event.args;
			if(args){
				var value = args.item.value;
				if(value == 'EMPL_WORKING'){
					$("#reasonResignUpdate").jqxDropDownList({disabled: true});
					$("#reasonResignUpdate").jqxDropDownList('clearSelection');
					$("#resignDateUpdate").val(null);
					//$("#resignDateUpdate").jqxDateTimeInput({disabled: true});
				}else{
					$("#reasonResignUpdate").jqxDropDownList({disabled: false});
					//$("#resignDateUpdate").jqxDateTimeInput({disabled: false});
				}
			}
		});
	};

	var initJqxWindow = function(){
		createJqxWindow($("#editEmplInfoWindow"), 800, 380);
		$("#editEmplInfoWindow").on('close', function(event){
			Grid.clearForm($(this));
			_partyId = "";
		});
		$("#editEmplInfoWindow").jqxValidator('hide');
	};
	var openEditWindow = function(){
		openJqxWindow($("#editEmplInfoWindow"));
	};

	var updateEmploymentInfo = function(){
		var dataSubmit = {};
		dataSubmit.partyId = _partyId;
		dataSubmit.partyCode = $("#partyIdToUpdate").val();
		var dateJoinCompany = $("#dateJoinCompanyUpdate").jqxDateTimeInput('val', 'date');
		if(dateJoinCompany){
			dataSubmit.dateJoinCompany = dateJoinCompany.getTime();
		}
		dataSubmit.workingStatusId = $("#statusIdUpdate").val();
		dataSubmit.terminationReasonId = $("#reasonResignUpdate").val();
		var resignDateUpdate = $("#resignDateUpdate").jqxDateTimeInput('val', 'date');
		if(resignDateUpdate){
			dataSubmit.thruDate = resignDateUpdate.getTime();
		}
		var dateParticipateIns = $("#insParticipateFromUpdate").jqxDateTimeInput('val', 'date');
		if(dateParticipateIns){
			dataSubmit.dateParticipateIns = dateParticipateIns.getTime();
		}
		dataSubmit.insuranceSocialNbr = $("#insuranceSocialNbrUpdate").val();
		var dateProbationaryDeadline = $("#probationaryDeadline").jqxDateTimeInput("val", "date");
		if(dateProbationaryDeadline){
			dataSubmit.probationaryDeadline = dateProbationaryDeadline.getTime();
		}
		$("#ajaxLoading_editEmplInfo").show();
		disabledBtn();
		$.ajax({
			url: 'updateEmploymentInfo',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
    			if(response.responseMessage == 'success'){
    				$("#editEmplInfoWindow").jqxWindow('close');
    				$("#updateNotification").jqxNotification('closeLast');
    				$("#notificationText").text(response.successMessage);
					$("#updateNotification").jqxNotification({ template: 'info' });
					$("#updateNotification").jqxNotification('open');
					$("#jqxgrid").jqxGrid('updatebounddata');
    			}else{
    				bootbox.dialog(response.errorMessage,
						[{
							"label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",
						}]
					);
    			}
    		},
    		error: function(jqXHR, textStatus, errorThrown){

			},
			complete: function(jqXHR, textStatus){
				enabledBtn();
				$("#ajaxLoading_editEmplInfo").hide();
			}
		});
	};

	var setData = function(data){
		_partyId = data.partyId;
		$("#partyIdToUpdate").val(data.partyCode);
		$("#employeeNameUpdate").val(data.fullName);
		$("#partyIdFromUpdate").val({value: data.partyGroupId, label: data.department});
		$("#emplPositionTypeIdUpdate").val(data.emplPositionType);
		if(data.dateJoinCompany){
			$("#dateJoinCompanyUpdate").val(data.dateJoinCompany);
			$("#dateJoinCompanyUpdate").jqxDateTimeInput({disabled: true});
		}else{
			$("#dateJoinCompanyUpdate").val(null);
			$("#dateJoinCompanyUpdate").jqxDateTimeInput({disabled: false});
		}
		if("terminationReasonId" in data){
			$("#reasonResignUpdate").val(data.terminationReasonId);
		}
		if("workingStatusId" in data){
			$("#statusIdUpdate").val(data.workingStatusId);
		}
		if("insuranceSocialNbr" in  data){
			$("#insuranceSocialNbrUpdate").val(data.insuranceSocialNbr);
		}
		if("resignDate" in data){
			$("#resignDateUpdate").val(data.resignDate);
		}else{
			$("#resignDateUpdate").val(null);
		}
		if("dateParticipateIns" in data){
			$("#insParticipateFromUpdate").val(data.dateParticipateIns);
		}else{
			$("#insParticipateFromUpdate").val(null);
		}
		if("emplPositionClassType" in data){
			/*$("#emplPositionClassTypeUpdate").val(data.emplPositionClassType);*/
		}
		if(data.probationaryDeadline){
			var dateProbationaryDeadline = new Date(data.probationaryDeadline);
			$("#probationaryDeadline").val(dateProbationaryDeadline);
		}else{
			$("#probationaryDeadline").val(null);
		}
		if("salaryBaseFlat" in data){
			$("#salaryBaseFlatUpdate").val(data.salaryBaseFlat);
			$("#periodTypeSalUpdate").val(data.periodTypeId);
		}
	};

	var disabledBtn = function(){
		$("#cancelUpdateEmpl").attr("disabled", "disabled");
		$("#saveUpdateEmpl").attr("disabled", "disabled");
	};
	var enabledBtn = function(){
		$("#cancelUpdateEmpl").removeAttr("disabled");
		$("#saveUpdateEmpl").removeAttr("disabled");
	};
	return{
		init: init,
		openEditWindow: openEditWindow,
		setData: setData,
		disabledBtn: disabledBtn,
		enabledBtn: enabledBtn
	}
}());

$(document).ready(function () {
	emplListInfo.init();
	contextMenuObject.init();
	editEmplObject.init();
});
