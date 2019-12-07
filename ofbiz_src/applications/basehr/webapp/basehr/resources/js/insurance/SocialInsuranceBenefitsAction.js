var actionObject = (function(){
	var createNewInsuranceAllowancePaymentDecl = function(benefitClassTypeId){
		var customTimePeriodId = $("#monthCustomTime").val();
		$("#jqxNtf").jqxNotification('closeLast');
		$.ajax({
			url: 'createNewInsuranceAllowancePaymentDecl',
			data: {customTimePeriodId: customTimePeriodId, benefitClassTypeId: benefitClassTypeId},
			type: 'POST',
			success: function(data){
				if(data.responseMessage == "success"){
					$("#jqxNtfContent").text(data.successMessage);
					$("#jqxNtf").jqxNotification({template: 'info'});
					$("#jqxNtf").jqxNotification("open");
					refreshTimesSettingJqxDropDownList(data.insAllowancePaymentDeclId, benefitClassTypeId);	
				}else{
					$("#jqxNtfContent").text(data.errorMessage);
					$("#jqxNtf").jqxNotification({template: 'error'});
					$("#jqxNtf").jqxNotification("open");
				}
			}, 
			error: function(jqXHR, textStatus, errorThrown){
				
			},
			complete: function( jqXHR, textStatus){
				
			}
		});
	};
	var refreshTimesSettingJqxDropDownList = function(selectItem, benefitClassTypeId){
		var item = $("#monthCustomTime").jqxDropDownList('getSelectedItem');
		getInsuranceAllowancePaymentDecl(item.value, benefitClassTypeId, "ITEM", selectItem);
	};
	
	var getInsuranceAllowancePaymentDecl = function(customTimePeriodId, benefitClassTypeId, selectType, select){
		$("#timeSetting").jqxDropDownList('clearSelection');	
		$.ajax({
			url: 'getInsAllowancePaymentByCustomPeriod',
			data: {customTimePeriodId: customTimePeriodId, benefitClassTypeId: benefitClassTypeId},
			type: 'POST',
			success: function(data){
				if(data.responseMessage == "success"){
					if(data.listReturn && data.listReturn.length > 0){
						var tmpS = {
							localdata: data.listReturn,
			                datatype: "array"	
						};
						var tmpDataAdapter = new $.jqx.dataAdapter(tmpS);
						$("#timeSetting").jqxDropDownList({source: tmpDataAdapter});	
						if(selectType == "INDEX"){
							$("#timeSetting").jqxDropDownList('selectIndex', select);
						}else if(selectType == "ITEM"){
							$("#timeSetting").jqxDropDownList('selectItem', select);
						}
						$("#warning").hide();
						$("#jqxGridInsuranceContainer").css("visibility", "visible");
						
					}else{
						$("#timeSetting").jqxDropDownList({source: []});
						$("#warning").show();
						$("#jqxGridInsuranceContainer").css("visibility", "hidden");
					}
				}else{
					bootbox.dialog(uiLabelMap.ErrorWhenRetrieveData,
						[{
			    		    "label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-mini icon-remove",
			    		    "callback": function() {
			    		    }
			    		}]		
					);
				}
			}
		});
	};
	var getDataEmplLeaveSelected = function(gridEle, selectedindexes){
		var dataSubmit = [];
		for(var i = 0; i < selectedindexes.length; i++){
			var rowData = gridEle.jqxGrid('getrowdata', selectedindexes[i]);
			var tempData = {emplLeaveId: rowData.emplLeaveId, benefitTypeId: rowData.benefitTypeId};
			var dateParticipateIns = rowData.dateParticipateIns;
			if(dateParticipateIns){
				tempData.dateParticipateIns = dateParticipateIns.getTime();
			}
			if(rowData.insuranceSalary){
				tempData.insuranceSalary = rowData.insuranceSalary;
			}
			if(rowData.dayLeaveInRegulation){
				tempData.dayLeaveInRegulation = rowData.dayLeaveInRegulation;
			}
			if(rowData.totalDayLeave){
				tempData.totalDayLeave = rowData.totalDayLeave;
			}
			if(rowData.accumulatedLeave){
				tempData.accumulatedLeave = rowData.accumulatedLeave;
			}
			if(rowData.dayLeaveFamily){
				tempData.dayLeaveFamily = rowData.dayLeaveFamily;
			}
			if(rowData.dayLeaveConcentrate){
				tempData.dayLeaveConcentrate = rowData.dayLeaveConcentrate;
			}
			if(rowData.statusConditionBenefit){
				tempData.statusConditionBenefit = rowData.statusConditionBenefit;
			}
			dataSubmit.push(tempData);
		}
		return dataSubmit;
	};
	var addEmplLeaveToInsAllowancePaymentDecl = function(){
		var emplLeaveIndexs = $("#emplLeaveListGrid").jqxGrid('getselectedrowindexes');
		var dataSubmit = getDataEmplLeaveSelected($("#emplLeaveListGrid"), emplLeaveIndexs);
		$("#emplLeaveBenefitListWindow").jqxWindow('close');
		disabledJqxGrid($("#jqxgrid"));
		$.ajax({
			url: 'addEmplLeaveToInsAllowancePaymentDecl',
			data: {emplLeaveInInsDecl: JSON.stringify(dataSubmit), insAllowancePaymentDeclId: $("#timeSetting").val()},
			type: 'POST',
			success: function(response){
				$("#jqxNtf").jqxNotification('closeLast');
				if(response.responseMessage == "success"){
					$("#jqxNtfContent").text(response.successMessage);
					$("#jqxNtf").jqxNotification({template: 'info'});
					$("#jqxNtf").jqxNotification("open");
					$("#jqxgrid").jqxGrid('updatebounddata');	
				}else{
					$("#jqxNtfContent").text(response.errorMessage);
					$("#jqxNtf").jqxNotification({template: 'error'});
					$("#jqxNtf").jqxNotification("open");
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				
			},
			complete: function(jqXHR, textStatus){
				enableJqxGrid($("#jqxgrid"));
			}
		});
	};
	
	var createInsuranceAllowancePaymentDecl = function(){
		var data = {};
		data.partyId = $("#partyIdNew").jqxInput('val').value;
		data.insAllowancePaymentDeclId = $("#timeSetting").jqxDropDownList('val');
		
		data.benefitTypeId = $("#benefitType").val();
		data.insuranceParticipatePeriod = $("#insuranceParticipatePeriod").val();
		data.insuranceSalary = $("#insuranceSocialSalaryBenefit").val();
		data.statusConditionBenefit = $("#insuranceStatusCondBenefit").val();
		var timeConditionBenefit = $("#insuranceTimeCondBenefit").jqxDateTimeInput('val', 'date');
		if(timeConditionBenefit){
			data.timeConditionBenefit = timeConditionBenefit.getTime();
		}
		data.fromDateLeave = $("#leaveFromDate").jqxDateTimeInput('val', 'date').getTime();
		data.leaveThruDate = $("#leaveThruDate").jqxDateTimeInput('val', 'date').getTime();
		data.accumulatedLeave = $("#accumulatedLeaveYTD").val();
		data.allowanceAmount = $("#allowanceAmountInPeriod").val();
		$.ajax({
			url: 'createPartyInsuranceAllowancePaymentDecl',
			data: data,
			type: 'POST',
			success: function(response){
				$("#jqxNtf").jqxNotification('closeLast');
				if(response.responseMessage == "success"){
					$("#jqxNtfContent").text(response.successMessage);
					$("#jqxNtf").jqxNotification({template: 'info'});
					$("#jqxNtf").jqxNotification("open");
					$("#jqxgrid").jqxGrid('updatebounddata');	
				}else{
					$("#jqxNtfContent").text(response.errorMessage);
					$("#jqxNtf").jqxNotification({template: 'error'});
					$("#jqxNtf").jqxNotification("open");
				}
			}
		});
	};
	
	var deleteInsuranceAllowancePaymentDecl = function(){
		var rowindex = $("#jqxgrid").jqxGrid("getselectedrowindex");
		var rowid = $('#jqxgrid').jqxGrid('getrowid', rowindex);
		$("#jqxgrid").jqxGrid('deleterow', rowid);
	};
	
	var getEmplLeaveList = function(partyId, benefitClassTypeId){
		var tempS = $("#emplLeaveListGrid").jqxGrid('source');
		var customTimePeriodId = $("#monthCustomTime").val();
		tempS._source.url = "jqxGeneralServicer?sname=JQgetListEmplLeaveByCustomTimePeriod&hasrequest=Y&partyId="+ partyId 
		        + "&customTimePeriodId=" + customTimePeriodId + "&benefitClassTypeId=" + benefitClassTypeId;
		$("#emplLeaveListGrid").jqxGrid('source', tempS);
	};
	var getCustomTimePeriodByParent = function(monthCustomTimeEle, value, selectItem){
		$.ajax({
			url: "getCustomTimePeriodByParent",
			data: {parentPeriodId: value},
			type: 'POST',
			success: function(data){
				if(data.listCustomTimePeriod){
					var listCustomTimePeriod = data.listCustomTimePeriod;
					var selectItemArr = [];
					if(typeof(selectItem) == "undefined"){
						selectItemArr = listCustomTimePeriod.filter(function(item, index, array){
							var nowTimestamp = globalVar.startDate;
							if(item.fromDate <= nowTimestamp && item.thruDate >= nowTimestamp){
								return item;
							}
						});
						if(selectItemArr.length > 0){
							selectItem = selectItemArr[0].customTimePeriodId;
						}
					}
					updateSourceDropdownlist(monthCustomTimeEle, listCustomTimePeriod);
					if(selectItem.length > 0){
						monthCustomTimeEle.jqxDropDownList('selectItem', selectItem);
					}else{
						monthCustomTimeEle.jqxDropDownList({selectedIndex: 0 });
					}
				}
			},
			complete: function(jqXHR, textStatus){
				
			}
		});
	};
	var calculateInsAllowanceAmount = function(gridEle, selectedindexes){
		eventObject.disableBtn();
		gridEle.jqxGrid({ disabled: true});
		gridEle.jqxGrid('showloadelement');
		var dataSubmit = getDataEmplLeaveSelected(gridEle, selectedindexes);
		$.ajax({
			url: 'calcInsAllowanceAmountEmplLeave',
			data: {emplLeaveAllowance: JSON.stringify(dataSubmit)},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					var results = response.results;
					if(results){
						for(var emplLeaveId in results){
							if(results.hasOwnProperty(emplLeaveId)){
								var data = results[emplLeaveId];
								for(var key in data){
									if(data.hasOwnProperty(key)){
										gridEle.jqxGrid('setcellvaluebyid', emplLeaveId, key, data[key]);
									}
								}
							}
						}
					}
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
				eventObject.enableBtn();
				gridEle.jqxGrid({ disabled: false});
				gridEle.jqxGrid('hideloadelement');
			}
		});
	};
	return{
		createNewInsuranceAllowancePaymentDecl: createNewInsuranceAllowancePaymentDecl,
		refreshTimesSettingJqxDropDownList: refreshTimesSettingJqxDropDownList,
		getInsuranceAllowancePaymentDecl: getInsuranceAllowancePaymentDecl,
		createInsuranceAllowancePaymentDecl: createInsuranceAllowancePaymentDecl,
		getEmplLeaveList: getEmplLeaveList,
		addEmplLeaveToInsAllowancePaymentDecl: addEmplLeaveToInsAllowancePaymentDecl,
		deleteInsuranceAllowancePaymentDecl: deleteInsuranceAllowancePaymentDecl,
		getCustomTimePeriodByParent: getCustomTimePeriodByParent,
		calculateInsAllowanceAmount: calculateInsAllowanceAmount
	}
}());

