var eventObject = (function(){
	var init = function(){
		initBtnEvent();
		initCustomTimeDropdownlistEvent();
	};
	var initBtnEvent = function(){
		$("#removeFilter").click(function(){
			$("#jqxgrid").jqxGrid('clearfilters');
		});
		$("#excelBtn").click(function(event){
			var data = {};
			var yearCustomTimePeriodId = $("#yearCustomTime").val();
			var monthCustomTimePeriodId = $("#monthCustomTime").val();
			data.yearCustomTimePeriodId = yearCustomTimePeriodId;
			data.customTimePeriodId = monthCustomTimePeriodId;
			exportSocicalInsBenefitObj.setData(data);//var exportSocicalInsBenefitObj is defined in ExportSocialInsuranceBenefitsExcel.js
			exportSocicalInsBenefitObj.openWindow();
		});
		$("#btnCalcAllowanceAmount").click(function(event){
			$("#emplLeaveListGrid").jqxGrid('hidevalidationpopups');
			var selectedindexes = $("#emplLeaveListGrid").jqxGrid('getselectedrowindexes');
			if(selectedindexes.length <= 0){
				bootbox.dialog(uiLabelMap.NoPartyChoose,
						[
						{
							"label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",
						}
						]
				);
				return;
			}
			var valid = true;
			for(var i = 0; i < selectedindexes.length; i++){
				var rowData = $("#emplLeaveListGrid").jqxGrid('getrowdata', selectedindexes[i]);
				if(!rowData.benefitTypeId){
					valid = false;
					$("#emplLeaveListGrid").jqxGrid('showvalidationpopup', selectedindexes[i], "benefitTypeId", uiLabelMap.BenefitTypeIsNotSelected);
				}
			}
			if(!valid){
				return;
			}
			actionObject.calculateInsAllowanceAmount($("#emplLeaveListGrid"), selectedindexes);
		});
		$("#addNew").click(function(event){
			bootbox.dialog(uiLabelMap.CreateNewInsuranceDeclaration,
					[
					{
						"label" : uiLabelMap.CommonSubmit,
		    		    "class" : "btn-primary btn-small icon-ok open-sans",
		    		    "callback": function() {
		    		    	actionObject.createNewInsuranceAllowancePaymentDecl("SICKNESS_PREGNANCY");   	
		    		    }		
					},
					{
						"label" : uiLabelMap.CommonClose,
		    		    "class" : "btn-danger btn-small icon-remove open-sans",
					}
					]
			);
		});
		
		$("#deleteBtn").click(function(event){
			var rowselect = $("#jqxgrid").jqxGrid("getselectedrowindex");
			if(rowselect < 0){
				bootbox.dialog(uiLabelMap.HRNoRowSelect,
				[{
					 "label" : uiLabelMap.CommonClose,
		    		 "class" : "btn-danger btn-small icon-remove open-sans",
				 }]
				);
				return;
			}
			bootbox.dialog(uiLabelMap.wgdeleteconfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
		    		    "class" : "btn-primary btn-small icon-ok open-sans",
		    		    "callback": function() {
		    		    	actionObject.deleteInsuranceAllowancePaymentDecl();
		    		    	
		    		    }
					 },
					 {
		    		    "label" : uiLabelMap.CommonCancel,
		    		    "class" : "btn-danger btn-small icon-remove open-sans",
		    		 }
					 ]		
			);
		});
		
		$("#btnSave").click(function(event){
			var valid = $("#popupWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			$(this).attr("disabled", "disabled");
			bootbox.dialog(uiLabelMap.InsuranceConfirmAddEmpl,
					[
					 {
						 "label" : uiLabelMap.CommonSubmit,
			    		    "class" : "btn-primary btn-small icon-ok open-sans",
			    		    "callback": function() {
			    		    	actionObject.createInsuranceAllowancePaymentDecl();
			    		    	$("#btnSave").removeAttr("disabled");
			    		    	$("#popupWindow").jqxWindow('close');
			    		    }
					 },
					 {
		    		    "label" : uiLabelMap.CommonCancel,
		    		    "class" : "btn-danger btn-small icon-remove open-sans",
		    		    "callback": function() {
		    		    	$("#btnSave").removeAttr("disabled");
		    		    }
		    		 }
					 ]		
			);
		});
		
		$("#getTimeParticipateIns").click(function(event){
			var party = $("#partyIdNew").val();
			if(typeof(party.value) == 'undefined' || !party){
				bootbox.dialog(uiLabelMap.NoPartyChoose,
						[{
			    		    "label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",
			    		    "callback": function() {
			    		    }
			    		}]		
					);
				return;
			}
			getTimeParticipateInsurance(party.value);
		});
		
		$("#getInfoEmplLeave").click(function(event){
			if(globalVar.rootPartyArr.length > 0){
				$("#jqxTreeEmplLeave").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_treeLeave")[0]);
			}
			openJqxWindow($("#emplLeaveBenefitListWindow"));
		});
		
		$("#btnCancel").click(function(event){
			$("#popupWindow").jqxWindow('close');
		});
		
		$("#btnCancelEmplLeave").click(function(event){
			$("#emplLeaveBenefitListWindow").jqxWindow('close');
		});
		$("#btnSaveEmplLeave").click(function(event){
			$("#emplLeaveListGrid").jqxGrid('hidevalidationpopups');
			var selectedindexes = $("#emplLeaveListGrid").jqxGrid('getselectedrowindexes');
			if(selectedindexes.length == 0){
				bootbox.dialog(uiLabelMap.NoPartyChoose,
						[
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}
						]		
				);
				return;
			}
			var valid = true;
			for(var i = 0; i < selectedindexes.length; i++){
				var rowData = $("#emplLeaveListGrid").jqxGrid('getrowdata', selectedindexes[i]);
				if(!rowData.benefitTypeId){
					valid = false;
					$("#emplLeaveListGrid").jqxGrid('showvalidationpopup', selectedindexes[i], "benefitTypeId", uiLabelMap.BenefitTypeIsNotSelected);
				}
			}
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.AddEmplLeaveToInsAllowancePaymentDecl,
					[
					{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							actionObject.addEmplLeaveToInsAllowancePaymentDecl();
						}
					},
					{
						"label" : uiLabelMap.CommonCancel,
						"class" : "btn-danger btn-small icon-remove open-sans",
						"callback": function() {
							
						}
					}
					]		
			);
		});
		
	};

	var getTimeParticipateInsurance = function(partyId){
		$("#insuranceParticipatePeriod").jqxInput({disabled: true});
		$("#insuranceParticipatePeriod").val(null);
		$.ajax({
			url: 'getTimeParticipateInsuranceOfParty',
			data: {partyId: partyId},
			success: function(response){
				if(response.responseMessage == 'success'){
					$("#insuranceParticipatePeriod").val(response.dateParticipateIns);
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
				$("#insuranceParticipatePeriod").jqxInput({disabled: false});
			}
		});
	};

	var initCustomTimeDropdownlistEvent = function(){
		$("#timeSetting").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var insAllowancePaymentDeclId = item.value;
				var tmpS = $("#jqxgrid").jqxGrid('source');
				tmpS._source.url = "jqxGeneralServicer?sname=getPartyInsuranceAllowancePayment&hasrequest=Y&insAllowancePaymentDeclId=" + insAllowancePaymentDeclId;
				$("#jqxgrid").jqxGrid('source', tmpS);
			}
		});
		
		$("#monthCustomTime").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var value = item.value;
				actionObject.getInsuranceAllowancePaymentDecl(value, "SICKNESS_PREGNANCY", "INDEX", 0);				
			}
		});
		
		$("#yearCustomTime").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var value = item.value;
				$.ajax({
					url: "getCustomTimePeriodByParent",
					data: {parentPeriodId: value},
					type: 'POST',
					success: function(data){
						if(data.listCustomTimePeriod){
							var listCustomTimePeriod = data.listCustomTimePeriod;
							var selectItem = listCustomTimePeriod.filter(function(item, index, array){
								var nowTimestamp = globalVar.startDate;
								if(item.fromDate <= nowTimestamp && item.thruDate >= nowTimestamp){
									return item;
								}
							});
							updateSourceDropdownlist($("#monthCustomTime"), listCustomTimePeriod);
							if(selectItem.length > 0){
								$("#monthCustomTime").jqxDropDownList('selectItem', selectItem[0].customTimePeriodId);
							}else{
								$("#monthCustomTime").jqxDropDownList({selectedIndex: 0 });
							}
						}
					},
					complete: function(jqXHR, textStatus){
						
					}
				});
			}
		});
		socialInsuranceBenefitObject.setDefaultValueJqxDropDownList();
	};
	var disableBtn = function(){
		$("#btnCancelEmplLeave").attr("disabled", "disabled");
		$("#btnSaveEmplLeave").attr("disabled", "disabled");
		$("#btnCalcAllowanceAmount").attr("disabled", "disabled");
	};
	var enableBtn = function(){
		$("#btnCancelEmplLeave").removeAttr("disabled");
		$("#btnSaveEmplLeave").removeAttr("disabled");
		$("#btnCalcAllowanceAmount").removeAttr("disabled");
	};
	return{
		init: init,
		disableBtn: disableBtn,
		enableBtn: enableBtn
	}
}());

$(document).ready(function () {
	eventObject.init();
});