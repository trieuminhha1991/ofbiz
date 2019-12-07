var emplTrainingRegisterObj = (function(){
	var _trainingCourseId;
	var init = function(){
		var checkRegisted = false;
		initGridEvent();
		initJqxNumberInput();
		initJqxInput();
		initJqxDropDownList();
		initJqxDateTimeInput();
		initJqxCheckBox();
		initJqxWindow();
		initBtnEvent();
		initCheckBoxEvent();
		initValidator();
		create_spinner($("#spinner-ajax"));
	};
	var disabledBtn = function(){
		$("#alterCancel").attr("disabled", "disabled");
		$("#alterSave").attr("disabled", "disabled");
	};
	
	var enabledBtn = function(){
		$("#alterCancel").removeAttr("disabled");
		$("#alterSave").removeAttr("disabled");
	};
	
	var initJqxCheckBox = function(){
		$("#registerAttTrainingCoure").jqxCheckBox({width: '98%', height: 25});
		$('#cancelRegisterAttTrainingCourse').jqxCheckBox({width: '98%', height: 25});
	};
	
	var initJqxInput = function(){
		$("#providerContact").jqxInput({width: '95%', height: 19, disabled: true});
		$("#trainingCourseId").jqxInput({width: '96%', height: 19, disabled: true});
		$("#trainingCourseName").jqxInput({width: '96%', height: 19, disabled: true});
		$("#trainingPurposeTypeId").jqxInput({width: '105%', height: 19, disabled: true});
		$("#certificate").jqxInput({width: '105%', height: 19, disabled: true});
		$('#statusId').jqxInput({width : '105%', height : 19, disabled : true});
		
		$("#TrainingMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.trainingFormTypeArr, $("#trainingFormTypeId"), 'trainingFormTypeId', 'description', 25, "98%");
		createJqxDropDownList(globalVar.geoArr, $("#geoId"), 'geoId', 'geoName', 25, "98%");
		createJqxDropDownList([], $("#trainingProvider"), "partyId", "partyName", 25, '98%');
		$("#trainingProvider").jqxDropDownList({disabled: true});
		$("#trainingFormTypeId").jqxDropDownList({disabled: true});
		$("#geoId").jqxDropDownList({disabled: true});
	};
	
	var initJqxDateTimeInput = function(){
		$("#fromDate").jqxDateTimeInput({width: '98%', height: 25, disabled: true});
		$("#registerFromDate").jqxDateTimeInput({width: '98%', height: 25, disabled: true});
		$("#thruDate").jqxDateTimeInput({width: '98%', height: 25, disabled: true});
		$("#registerThruDate").jqxDateTimeInput({width: '98%', height: 25, disabled: true});
	};
	
	var initJqxNumberInput = function(){
		$("#amountCompanyPaid").jqxNumberInput({ width: '98%', height: 25, min: 0,  spinButtons: false,decimalDigits: 0, digits: 9, max: 999999999, disabled: true});
		$("#amountEmplPaid").jqxNumberInput({ width: '98%', height: 25, min: 0,  spinButtons: false,decimalDigits: 0, digits: 9, max: 999999999, disabled: true});
	};
	
	var initGridEvent = function(){
		$("#TrainingMenu").on('itemclick', function (event) {
			var rowData = $('#jqxgrid').jqxGrid('getRowData', $("#jqxgrid").jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			if(tmpStr == uiLabelMap.Registered){
				var index = event.args.rowindex;
				$("#ajaxLoading").show();
				openJqxWindow($("#registerTrainingWindow"));
				_trainingCourseId = rowData.trainingCourseId;
				disabledBtn();
				$.ajax({
					url: 'getTrainingCourseInfo',
	        		data: {trainingCourseId: rowData.trainingCourseId},
	        		type: 'POST',
	        		success: function(response){
	        			if(response.responseMessage == 'success'){
	        				var trainingData = response.trainingCourse;
	        				trainingData.statusIdRegister = rowData.statusIdRegister;
	        				setData(trainingData);
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
	        			bootbox.dialog(errorThrown,
							[{
								"label" : uiLabelMap.CommonClose,
				    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
							}]		
						);
	        		},
	        		complete: function(jqXHR, textStatus){
	        			$("#ajaxLoading").hide();
	        			enabledBtn();
	        		}
				});
			} else if (tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgrid').jqxGrid('updatebounddata');
			}
		});
		
		$("#jqxgrid").on('rowdoubleclick', function(event){
			var index = event.args.rowindex;
			var rowData = $("#jqxgrid").jqxGrid('getrowdata', index);
			$("#ajaxLoading").show();
			openJqxWindow($("#registerTrainingWindow"));
			_trainingCourseId = rowData.trainingCourseId;
			disabledBtn();
			$.ajax({
				url: 'getTrainingCourseInfo',
        		data: {trainingCourseId: rowData.trainingCourseId},
        		type: 'POST',
        		success: function(response){
        			if(response.responseMessage == 'success'){
        				var trainingData = response.trainingCourse;
        				trainingData.statusIdRegister = rowData.statusIdRegister;
        				setData(trainingData);
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
        			bootbox.dialog(errorThrown,
						[{
							"label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
						}]		
					);
        		},
        		complete: function(jqXHR, textStatus){
        			$("#ajaxLoading").hide();
        			enabledBtn();
        		}
			});
		});
	};
	var initJqxWindow = function(){
		createJqxWindow($("#registerTrainingWindow"), 800, 555);
		$("#registerTrainingWindow").on('close', function(event){
			Grid.clearForm($(this));
			$("#registerOutOfTimeArea").hide();
			$("#registerArea").hide();
		});
	};
	
	var initCheckBoxEvent = function(){
		$('#registerAttTrainingCoure').on('checked', function(){
			$('#cancelRegisterAttTrainingCourse').jqxCheckBox('uncheck');
		});
		
		$('#cancelRegisterAttTrainingCourse').on('checked', function(){
			$('#registerAttTrainingCoure').jqxCheckBox('uncheck');
		});
	};
	
	var initBtnEvent = function(){
		$("#alterCancel").click(function(event){
			$("#registerTrainingWindow").jqxWindow('close');
		});
		$("#alterSave").click(function(event){
			var valid = $("#registerTrainingWindow").jqxValidator('validate');
			if(!valid){
				return false;
			}
			var registed = $("#registerAttTrainingCoure").jqxCheckBox('checked');
			var unregisted = $('#cancelRegisterAttTrainingCourse').jqxCheckBox('checked');
			var message = "";
			
			if (!checkRegisted || !registed){
				if(registed){
					message = uiLabelMap.RegisterTrainingConfirm;
				} else if(unregisted){
					message = uiLabelMap.UnRegisterTrainingConfirm;
				}
				
				bootbox.dialog(message,
					[{
						"label" : uiLabelMap.CommonSubmit,
		    		    "class" : "btn-primary btn-small icon-ok open-sans",
		    		    "callback": function() {
		    		    	updateEmplRegisterTraining();   	
		    		    }	
					},
					{
		    		    "label" : uiLabelMap.CommonClose,
		    		    "class" : "btn-danger btn-small icon-remove open-sans",
		    		}]		
				);
			} else {
				jOlbUtil.alert.error(uiLabelMap.SelectedUnRegisterToUpdate);
				return false;
			}
		});
	};
	
	var updateEmplRegisterTraining = function(){
		var registed = $("#registerAttTrainingCoure").jqxCheckBox('checked');
		var unregisted = $('#cancelRegisterAttTrainingCourse').jqxCheckBox('checked');
		$("#ajaxLoading").show();
		disabledBtn();
		var dataSubmit = {};
		dataSubmit.trainingCourseId = _trainingCourseId;
		if(registed){
			dataSubmit.isRegisted = "Y";
		}else if(unregisted){
			dataSubmit.isRegisted = "N";
		}
		$.ajax({
			url: 'updateEmplRegisterTraining',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				if(response._ERROR_MESSAGE_){
					bootbox.dialog(response._ERROR_MESSAGE_,
						[{
							"label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
						}]		
					);
				}else if(response._EVENT_MESSAGE_){
					$("#registerTrainingWindow").jqxWindow('close');
					Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {autoClose : true,
						template : 'info',
						appendContainer : "#containerjqxgrid",
						opacity : 0.9});
					$('#jqxgrid').jqxGrid('updatebounddata');
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
    			bootbox.dialog(errorThrown,
					[{
						"label" : uiLabelMap.CommonClose,
		    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
					}]		
				);
    		},
			complete: function(jqXHR, textStatus){
				$("#ajaxLoading").hide();
				enabledBtn();
			}
		});
	};
	
	var setData = function(data){
		$("#description").html(""); 
		if (data.description != undefined && data.description != null && data.description != ''){
			$("#description").text(data.description);
		}
		$("#trainingCourseId").val(data.trainingCourseCode);
		$("#trainingCourseName").val(data.trainingCourseName);
		if(typeof(data.trainingFormTypeId) != 'undefined' && data.trainingFormTypeId != null){
			$("#trainingFormTypeId").val(data.trainingFormTypeId);
		}
		if(typeof(data.geoId) != 'undefined' && data.geoId != null){
			$("#geoId").val(data.geoId);
		}
		if(typeof(data.certificate) != 'undefined' && data.certificate != null){
			$("#certificate").val(data.certificate);
		}
		if(!data.statusIdRegister){
			$("#statusId").val(uiLabelMap.HRCommonNotRegister);
		}else{
			$('#cancelRegisterAttTrainingCourse').show();
			for(var i=0 ; i<globalVar.statusArr.length ; i++){
				if(globalVar.statusArr[i].statusId == data.statusIdRegister){
					$("#statusId").val(globalVar.statusArr[i].description);
					break;
				}
			}
			
		}
		if(typeof(data.fromDate) != 'undefined' && data.fromDate != null){
			$("#fromDate").val(new Date(data.fromDate));
		}else{
			$("#fromDate").val(null);
		}
		
		if(typeof(data.thruDate) != 'undefined' && data.thruDate != null){
			$("#thruDate").val(new Date(data.thruDate));
		}else{
			$("#thruDate").val(null);
		}
		
		if(typeof(data.registerFromDate) != 'undefined' && data.registerFromDate != null){
			$("#registerFromDate").val(new Date(data.registerFromDate));
		}else{
			$("#registerFromDate").val(null);
		}
		if(typeof(data.registerThruDate) != 'undefined' && data.registerThruDate != null){
			$("#registerThruDate").val(new Date(data.registerThruDate));
		}else{
			$("#registerThruDate").val(null);
		}
		var now = new Date();
		var f = new Date(data.registerThruDate);
		
		if (f < now){
			$("#registerAttTrainingCoure").jqxCheckBox({'locked': true});
			$("#alterSave").hide();
			$("#registerOutOfTimeArea").show();
			$("#registerArea").hide();
		} else {
			$("#registerAttTrainingCoure").jqxCheckBox({'locked': false});
			$("#alterSave").show();
			$("#registerOutOfTimeArea").hide();
			$("#registerArea").show();
		}
		var trainingPurposeTypeIds = data.trainingPurposeTypeIds;
		if(typeof(trainingPurposeTypeIds) != null && trainingPurposeTypeIds != null){
			var trainingPurposeTypeDesc = "";
			for(var i = 0; i < trainingPurposeTypeIds.length; i++){
				for(var j = 0; j < globalVar.trainingPurposeTypeArr.length; j++){
					if(globalVar.trainingPurposeTypeArr[j].trainingPurposeTypeId == trainingPurposeTypeIds[i]){
						trainingPurposeTypeDesc += globalVar.trainingPurposeTypeArr[j].description;
						if( i < trainingPurposeTypeIds.length - 1){
							trainingPurposeTypeDesc += "; ";
						}
					}
				}
			}
			$("#trainingPurposeTypeId").val(trainingPurposeTypeDesc);
		}
		$("#amountEmplPaid").val(data.estimatedEmplPaid);
		$("#amountCompanyPaid").val(data.amountCompanySupport);
		var isCancelRegister = data.isCancelRegister;
		var message = "";
		if("Y" == isCancelRegister){
			var cancelBeforeDay = data.cancelBeforeDay;
			message = uiLabelMap.AllowCancelRegisterBefore + " " + cancelBeforeDay + " " + uiLabelMap.HRDayLowercase;
		}else{
			message = uiLabelMap.NotAllowCancelRegister;
		}
		$("#registerWarning").html('<b>' + message + '</b>');
		if(data.hasOwnProperty("statusIdRegister") && data.statusIdRegister == "TCR_REGIS"){
			checkRegisted = true;
			$("#registerAttTrainingCoure").jqxCheckBox({checked: true});
			$("#registerAttTrainingCoure").show();
			$('#cancelRegisterAttTrainingCourse').show();
		}else{
			checkRegisted = false;
			$("#registerAttTrainingCoure").jqxCheckBox({checked: false});
		}
		if(data.hasOwnProperty("statusIdRegister") && data.statusIdRegister == "TCR_NOT_REGIS" || data.statusIdRegister == "TCR_CANCEL_REGIS"){
			$("#registerAttTrainingCoure").show();
			$('#cancelRegisterAttTrainingCourse').show();
		}
		if(data.hasOwnProperty("statusIdRegister") && data.statusIdRegister == "TCR_ATTENDANCE"){
			$("#registerAttTrainingCoure").hide();
			$('#cancelRegisterAttTrainingCourse').hide();
		}
	};
	var initValidator = function(){
		$("#registerTrainingWindow").jqxValidator({
			rules: [
			        {input: '#cancelRegisterAttTrainingCourse', message: uiLabelMap.PleaseSelectOption, action: 'keyup, blur', 
			        	rule: function (input, commit){
			        		var registed = $("#registerAttTrainingCoure").jqxCheckBox('checked');
			    			var unregisted = $('#cancelRegisterAttTrainingCourse').jqxCheckBox('checked')
			    			if(!registed && !unregisted){
			    				return false;
			    			}
			    			return true;
			        	}
			        }
			]
		});
	};
	return{
		init: init
	}
}());

$(document).ready(function(){
	emplTrainingRegisterObj.init();
});