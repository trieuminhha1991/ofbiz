var initWizard = (function(){
	var _functionAfterChooseEmpl = null;
	var _agreementId = null;
	var init = function(){
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
	        if(info.step == 1 && (info.direction == "next")) {
	        	return createEmplAgrObjectstep1.validate();
	        }
	    }).on('finished', function(e) {
	    	if(!globalVar.editFlag){
	    		actionObject.checkAgreementExists();
	    	}else{
	    		if(globalVar.hasPermission){
	    			var agreementId = viewListEmplAgreementObject.getSelectedAgreementId();
	    			actionObject.checkUpdateAgreement(agreementId);
	    		}else{
	    			$("#" + globalVar.jqxWindowId).jqxWindow('close');
	    		}
	    	}
	    }).on('stepclick', function(e){
	    	
	    });
		initJqxGridSearchEmpl();
		if(globalVar.createWindow){
			initJqxWindow();
			initJqxWindowEvent();
		}
	};
	
	var resetStep = function(){
		$('#fuelux-wizard').wizard('previous');
		_functionAfterChooseEmpl = null;
		_agreementId = null;
	};
	
	var initJqxWindowEvent = function(){
		$('#popupWindowEmplList').on('open', function(event){
			$("#createAgreementStep1").jqxValidator('hide');
			if(typeof(globalVar.expandTreeId) != 'undefined'){
				$("#jqxTreeEmplList").jqxTree('expandItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
				$('#jqxTreeEmplList').jqxTree('selectItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
			}
		});
		$('#popupWindowEmplList').on('close', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
		});
	};
	
	var initJqxGridSearchEmpl = function(){
		createJqxGridSearchEmpl($("#EmplListInOrg"), {uiLabelMap: uiLabelMap});
		$("#EmplListInOrg").on('rowdoubleclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var data = $("#EmplListInOrg").jqxGrid('getrowdata', boundIndex);
			if(typeof(_functionAfterChooseEmpl) == "function"){
				_functionAfterChooseEmpl(data);
			}
		    $('#popupWindowEmplList').jqxWindow('close');
		});
	};
	
	var initJqxWindow = function(){
		var initContent = function () {  
	    	initJqxSplitter();
	    };
		createJqxWindow($('#popupWindowEmplList'), 900, 540, initContent);
	};
	var initJqxSplitter =  function(){
		$("#splitterEmplList").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
	};
	
	var setFunctionAfterChooseEmpl = function(value){
		_functionAfterChooseEmpl = value;
	};
	var setAgreementId = function(agreementId){
		_agreementId = agreementId;
	};
	var getAgreementId = function(){
		return _agreementId;
	};
	return {
		init: init,
		resetStep: resetStep,
		setFunctionAfterChooseEmpl: setFunctionAfterChooseEmpl,
		setAgreementId: setAgreementId,
		getAgreementId: getAgreementId
	}
}());

var editEmplAgreementObject = (function(){
	var setAgreementData = function(data){
		globalVar.editFlag = true;		
		var agreementId = data.agreementId;
		initWizard.setAgreementId(agreementId);
		createEmplAgrObjectstep1.fillData(data);
		createEmplAgrObjectstep2.fillData({agreementId: agreementId,partyId: data.partyIdRep, fullName: data.partyRepName});
		openJqxWindow($("#" + globalVar.jqxWindowId));
	};
	return{
		setAgreementData: setAgreementData
	}
}());

var setupObject = (function(){
	var init = function(){
		initJqxWindow();
		create_spinner($("#spinner-ajax"));
	};
	
	var closeWindow = function(){
		$("#" + globalVar.jqxWindowId).jqxWindow('close');
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#" + globalVar.jqxWindowId), 900, 500);
		$("#" + globalVar.jqxWindowId).on('close', function(event){
			createEmplAgrObjectstep1.resetData();
			initWizard.resetStep();
			createEmplAgrObjectstep2.resetData();
			globalVar.editFlag = false;
		});
		$("#" + globalVar.jqxWindowId).on('open', function(event){
			if(!globalVar.editFlag){
				var d = new Date();
				d.setHours(0,0,0, 0);
				$("#payRate" + globalVar.suffix).val(100);
				$("#fromDate" + globalVar.suffix).val(null);
				$("#thruDate" + globalVar.suffix).val(null);
				$("#agreementDate" + globalVar.suffix).val(d);
				$("#partyIdFrom" + globalVar.suffix).jqxInput('val', {label: globalVar.groupName, value: globalVar.rootOrgId});
				$(this).jqxWindow('setTitle', uiLabelMap.HRNewAgreement)
			}else{
				$(this).jqxWindow('setTitle', uiLabelMap.HREditAgreement)
			}
		});
	};
	return{
		init: init,
		closeWindow: closeWindow
	}
}());

var actionObject = (function(){
	var getPositionOfEmplDropDownList = function(dataSubmit, callback){
		$.ajax({
			url: 'getEmplPositionTypeOfEmployee',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == 'success'){
					var listReturn = response.listReturn;
					callback(listReturn);
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				
			},
			complete: function(jqXHR, textStatus){
				
			} 
		});
	};
	
	var checkUpdateAgreement = function(agreementId){
		$("#ajaxLoading").show();
		$("#btnNext").attr("disabled", "disabled");
		$("#btnPrev").attr("disabled", "disabled");
		var fromDate = $("#fromDate" + globalVar.suffix).jqxDateTimeInput('val', 'date');
		var partyIdTo = $("#partyIdTo" + globalVar.suffix).val();
		var hideLoading = true;
		$.ajax({
			url: 'checkUpdateAgreementStatus',
			data: {agreementId: agreementId, partyIdTo: partyIdTo, fromDate: fromDate.getTime()},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					if(response.warningMessage){
						var message = response.warningMessage;
						bootbox.dialog(message,
								[
								{
					    		    "label" : uiLabelMap.CommonSubmit,
					    		    "class" : "btn-primary btn-small icon-ok open-sans",
					    		    "callback": function(){
					    		    	updateAgreement(agreementId);
					    		    }
					    		},
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}
								]		
							);
					}else{
						hideLoading = false;
						updateAgreement(agreementId);
					}
				}else{
					bootbox.dialog(uiLabelMap.ErrorOccurWhenUpdateAgreement + ": " + response.errorMessage,
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
				if(hideLoading){
					$("#btnNext").removeAttr("disabled");
					$("#btnPrev").removeAttr("disabled");
					$("#ajaxLoading").hide();
				}
			}
		});
	};
	
	var checkAgreementExists = function(){
		$("#ajaxLoading").show();
		$("#btnNext").attr("disabled", "disabled");
		$("#btnPrev").attr("disabled", "disabled");
		var fromDate = $("#fromDate" + globalVar.suffix).jqxDateTimeInput('val', 'date');
		var thruDate = $("#thruDate" + globalVar.suffix).jqxDateTimeInput('val', 'date');
		var partyIdTo = $("#partyIdTo" + globalVar.suffix).val();
		var agreementTypeId = $("#agreementTypeId" + globalVar.suffix).val();
		var dataSubmit = {partyIdTo: partyIdTo, fromDate: fromDate.getTime(), agreementTypeId: agreementTypeId};
		if(thruDate){
			dataSubmit.thruDate = thruDate.getTime();
		}
		$.ajax({
			url: 'getAgreementEffectiveOfParty',
			data: dataSubmit,
			type: 'POST',			
			success: function(response){
				if(response.responseMessage == "success"){
					if(response.agreementCode){
						var message;
						if(response.warningMessage){
							message = response.warningMessage; 
						}else{
							message = uiLabelMap.HRAgreementHaveCode + ": " + response.agreementCode + " " + uiLabelMap.HRWillExpireDate + " " 
							+ uiLabelMap.HRCommonWhen + " " + uiLabelMap.AgreementCreatedNew + ". " + uiLabelMap.AreYouSure
						}
						bootbox.dialog(message,
								[
								{
					    		    "label" : uiLabelMap.CommonSubmit,
					    		    "class" : "btn-primary btn-small icon-ok open-sans",
					    		    "callback": function(){
					    		    	createNewAgreement();
					    		    }
					    		},
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}
								]		
							);
					}else{
						bootbox.dialog(uiLabelMap.CreateAgreementConfirm,
								[{
								    "label" : uiLabelMap.CommonSubmit,
								    "class" : "btn-primary btn-small icon-ok open-sans",
								    "callback": function(){
								    	createNewAgreement();
								    }
								},
								{
					    		    "label" : uiLabelMap.CommonClose,
					    		    "class" : "btn-danger btn-small icon-remove open-sans",
					    		}]		
							);
					}
				}else{
					bootbox.dialog(uiLabelMap.ErrorOccurWhenCreateAgreement + ": " + response.errorMessage,
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
				$("#btnNext").removeAttr("disabled");
				$("#btnPrev").removeAttr("disabled");
				$("#ajaxLoading").hide();
			}
		});
	};
	
	var updateAgreement = function(agreementId){
		$("#ajaxLoading").show();
		$("#btnNext").attr("disabled", "disabled");
		$("#btnPrev").attr("disabled", "disabled");
		var dataStep1 = createEmplAgrObjectstep1.getData();
		var dataStep2 = createEmplAgrObjectstep2.getData();
		var data = $.extend({}, dataStep1, dataStep2);
		data.agreementId = initWizard.getAgreementId();
		$.ajax({
			url: 'updateEmploymentAgreement',
			type: 'POST',
			data: data,
			success: function(response){
				if(response._EVENT_MESSAGE_){
					$("#jqxgrid").jqxGrid('updatebounddata');
					Grid.updateGridMessage("jqxgrid", 'info', response._EVENT_MESSAGE_);
					setupObject.closeWindow();
				}else{
					bootbox.dialog(uiLabelMap.ErrorOccurWhenUpdateAgreement + ": " + response._ERROR_MESSAGE_,
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
				$("#btnNext").removeAttr("disabled");
				$("#btnPrev").removeAttr("disabled");
				$("#ajaxLoading").hide();
			}
		});
	};
	
	var createNewAgreement = function(){
		$("#ajaxLoading").show();
		$("#btnNext").attr("disabled", "disabled");
		$("#btnPrev").attr("disabled", "disabled");
		var dataStep1 = createEmplAgrObjectstep1.getData();
		var dataStep2 = createEmplAgrObjectstep2.getData();
		var data = $.extend({}, dataStep1, dataStep2);
		$.ajax({
			url: 'createEmploymentAgreement',
			type: 'POST',
			data: data,
			success: function(response){
				if(response._EVENT_MESSAGE_){
					$("#jqxgrid").jqxGrid('updatebounddata');
					Grid.updateGridMessage("jqxgrid", 'info', response._EVENT_MESSAGE_);
					setupObject.closeWindow();
				}else{
					bootbox.dialog(uiLabelMap.ErrorOccurWhenCreateAgreement + ": " + response._ERROR_MESSAGE_,
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
				$("#btnNext").removeAttr("disabled");
				$("#btnPrev").removeAttr("disabled");
				$("#ajaxLoading").hide();
			}
		});
	};
	
	return {
		getPositionOfEmplDropDownList: getPositionOfEmplDropDownList,
		checkAgreementExists: checkAgreementExists,
		checkUpdateAgreement: checkUpdateAgreement
	}
}());

var agreementDurationObj = (function(){
	var init = function(){
		$("#periodLength").jqxNumberInput({width: "98%", height: 25,  spinButtons: true, decimalDigits: 0, inputMode: 'simple'});
		createJqxDropDownList(globalVar.agreementPeriodUomArr, $("#uomIdAgrPeriod"), "uomId", "abbreviation", 25, '95%');
		createJqxWindow($("#newAgrDurationWindow"), 350, 180);
		$("#agreementeriodName").jqxInput({width: '96%', height: 20});
		create_spinner($("#spinnerAgreementPeriod"));
		initEvent();
		initJqxValidator();
	};
	var initEvent = function(){
		$("#newAgrDurationWindow").on('open', function(event){
			$("#uomIdAgrPeriod").jqxDropDownList({selectedIndex: 0});
		});
		$("#newAgrDurationWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
		$("#cancelCreateAgrDur").click(function(){
			$("#newAgrDurationWindow").jqxWindow('close');
		});
		$("#saveCreateAgrDur").click(function(event){
			var valid = $("#newAgrDurationWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			disableAll();
			$("#loadingAgreementPeriod").show();
			var data = getData();
			$.ajax({
				url: 'createAgrDuration',
				data: data,
				type: 'POST',
				success: function(response){
					if(response._EVENT_MESSAGE_){
						var source = $("#agreementDuration" + globalVar.suffix).jqxDropDownList('source');
						source._source.url = "getAgrDurationList";
						$("#agreementDuration" + globalVar.suffix).jqxDropDownList('source', source);
						$("#newAgrDurationWindow").jqxWindow('close');
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
					$("#loadingAgreementPeriod").hide();
					enableAll();
				}
			});
		});
	};
	var getData = function(){
		var data = {};
		data.description = $("#agreementeriodName").val();
		data.periodLength = $("#periodLength").val();
		data.uomId = $("#uomIdAgrPeriod").val();
		return data;
	};
	var disableAll = function(){
		$("#saveCreateAgrDur").attr("disabled", "disabled");
		$("#cancelCreateAgrDur").attr("disabled", "disabled");
		$("#agreementeriodName").jqxInput({disabled: true});
		$("#periodLength").jqxNumberInput({disabled: true});
		$("#uomIdAgrPeriod").jqxDropDownList({disabled: true});
	};
	var enableAll = function(){
		$("#saveCreateAgrDur").removeAttr("disabled");
		$("#cancelCreateAgrDur").removeAttr("disabled");
		$("#agreementeriodName").jqxInput({disabled: false});
		$("#periodLength").jqxNumberInput({disabled: false});
		$("#uomIdAgrPeriod").jqxDropDownList({disabled: false});
	};
	var initJqxValidator = function(){
		$("#newAgrDurationWindow").jqxValidator({
			rules: [
				{input : '#agreementeriodName', message: uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},     
				{input : '#agreementeriodName', message: uiLabelMap.HRContainSpecialSymbol, action : 'blur',
					rule : function(input, commit){
						if(input.val() && validationNameWithoutHtml(input.val())){
							return false;
						}
						return true;
					}
				},     
				{input : '#periodLength', message: uiLabelMap.ValueMustBeGreateThanZero, action : 'blur',
					rule : function(input, commit){
						if(input.val() <= 0){
							return false;
						}
						return true;
					}
				},     
				{input : '#uomIdAgrPeriod', message: uiLabelMap.ValueMustBeGreateThanZero, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},     
			]
		});
	};
	var openWindow = function(){
		openJqxWindow($("#newAgrDurationWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function () {
	initWizard.init();
	setupObject.init();
	createEmplAgrObjectstep1.init();	
	createEmplAgrObjectstep2.init();
	agreementDurationObj.init();
});