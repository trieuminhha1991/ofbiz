var editTrainingCourseInfo = (function(){
	var init = function(){
		initJqxInput();
		initJqxDropDownList();
		initJqxCombobox();
		initJqxDateTimeInput();
		initJqxWindow();
		initJqxValidator();
		initEvent();
		create_spinner($("#spinnerTrainingCourseInfo"));
	};
	
	var initJqxInput = function(){
		$("#trainingCourseCode").jqxInput({width: '96%', height: 20});
		$("#trainingCourseName").jqxInput({width: '96%', height: 20});
		$("#location").jqxInput({width: '96%', height: 20});
		$("#certificate").jqxInput({width: '105%', height: 20});
		
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.trainingFormTypeArr, $("#trainingFormTypeId"), 'trainingFormTypeId', 'description', 25, "98%");
	};
	
	var initJqxCombobox = function(){
		$("#trainingPurposeTypeId").jqxComboBox({source: globalVar.trainingPurposeTypeArr, multiSelect: true, 
			width: "106%", height: 25, valueMember: "trainingPurposeTypeId", displayMember: "description", checkboxes: true});
		if(globalVar.trainingPurposeTypeArr.length < 8){
			$("#trainingPurposeTypeId").jqxComboBox({autoDropDownHeight: true});
		}else{
			$("#trainingPurposeTypeId").jqxComboBox({autoDropDownHeight: false});
		}
	};
	
	var initJqxDateTimeInput = function(){
		$("#fromDate").jqxDateTimeInput({width: '98%', height: 25});
		$("#registerFromDate").jqxDateTimeInput({width: '98%', height: 25});
		$("#thruDate").jqxDateTimeInput({width: '98%', height: 25});
		$("#registerThruDate").jqxDateTimeInput({width: '98%', height: 25});
	};
	
	var initJqxEditor = function(){
		$('#description').jqxEditor({ 
			width: '106%',
			theme: 'olbiuseditor',
			tools: 'datetime | clear | backcolor | font | bold italic underline',
			height: 180,
		});	
		$('#description').val("");
	};
	
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxEditor();
		};
		createJqxWindow($("#editTrainingInfoWindow"), 850, 540, initContent);
		$("#editTrainingInfoWindow").on('open', function(event){
			loadingData();
		});
		$("#editTrainingInfoWindow").on('close', function(event){
			Grid.clearForm($("#editTrainingInfoWindow"));
			$("#trainingPurposeTypeId").jqxComboBox('uncheckAll');
			hideValidate();
		});
	};
	var loadingData = function(){
		$("#loadingTrainingCourseInfo").show();
		$("#cancelEditTrainingCourseInfo").attr("disabled", "disabled");
		$("#saveEditTrainingCourseInfo").attr("disabled", "disabled");
		$.ajax({
    		url: 'getTrainingCourseInfo',
    		data: {trainingCourseId: globalVar.trainingCourseId},
    		type: 'POST',
    		success: function(response){
    			if(response.responseMessage == 'success'){
    				var trainingData = response.trainingCourse;
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
    			$("#loadingTrainingCourseInfo").hide();
    			$("#cancelEditTrainingCourseInfo").removeAttr("disabled");
    			$("#saveEditTrainingCourseInfo").removeAttr("disabled");
    		}
    	});
	};
	var getData = function(){
		var data = {};
		data.trainingCourseCode = $("#trainingCourseCode").val();
		data.trainingCourseName = $("#trainingCourseName").val();
		var trainingFormTypeId = $("#trainingFormTypeId").val();
		if(trainingFormTypeId){
			data.trainingFormTypeId = trainingFormTypeId;
		}
		var trainingPurposeTypeRecords = $("#trainingPurposeTypeId").jqxComboBox('getCheckedItems');
		if(trainingPurposeTypeRecords.length > 0){
			var trainingPurposeTypeArr = [];
			for(var i = 0; i < trainingPurposeTypeRecords.length; i++){
				trainingPurposeTypeArr.push(trainingPurposeTypeRecords[i].value);
			}
			data.trainingPurposeTypeIds = JSON.stringify(trainingPurposeTypeArr);
		}
		data.location = $("#location").val();
		var fromDate = $("#fromDate").jqxDateTimeInput('val', 'date');
		if(fromDate){
			data.fromDate = fromDate.getTime();
		}
		var registerFromDate = $("#registerFromDate").jqxDateTimeInput('val', 'date');
		if(registerFromDate){
			data.registerFromDate = registerFromDate.getTime();
		}
		var thruDate = $("#thruDate").jqxDateTimeInput('val', 'date');
		if(thruDate){
			data.thruDate = thruDate.getTime();
		}
		var registerThruDate = $("#registerThruDate").jqxDateTimeInput('val', 'date');
		if(registerThruDate){
			data.registerThruDate = registerThruDate.getTime();
		}
		var description = $('#description').val();
		if(description){
			data.description = description; 
		}
		
		var certificate = $("#certificate").val();
		if(typeof(certificate) != 'undefined' && certificate.length > 0){
			data.certificate = certificate;
		}
		return data;
	};
	
	var setData = function(data){
		$("#trainingCourseCode").val(data.trainingCourseCode);
		$("#trainingCourseName").val(data.trainingCourseName);
		if(typeof(data.trainingFormTypeId) != 'undefined' && data.trainingFormTypeId != null){
			$("#trainingFormTypeId").val(data.trainingFormTypeId);
		}
		if(typeof(data.location) != 'undefined' && data.location != null){
			$("#location").val(data.location);
		}
		if(typeof(data.description) != 'undefined' && data.description != null){
			$("#description").val(data.description);
		}
		if(typeof(data.certificate) != 'undefined' && data.certificate != null){
			$("#certificate").val(data.certificate);
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
		var trainingPurposeTypeIds = data.trainingPurposeTypeIds;
		if(typeof(trainingPurposeTypeIds) != null && trainingPurposeTypeIds != null){
			for(var i = 0; i < trainingPurposeTypeIds.length; i++){
				$("#trainingPurposeTypeId").jqxComboBox('checkItem', trainingPurposeTypeIds[i]);
			}
		}
	};
	
	var initJqxValidator = function(){
		$("#editTrainingInfoWindow").jqxValidator({
			position: 'bottom',
			rules:[
			       { input: '#trainingCourseCode', message: uiLabelMap.FieldRequired, action: 'keyup, blur', rule: 'required' },
			       { input: '#trainingCourseName', message: uiLabelMap.FieldRequired, action: 'keyup, blur', rule: 'required' },
			       { 
			    	   input: '#fromDate', message: uiLabelMap.FieldRequired, action: 'keyup, blur', 
			    	   rule: function (input, commit){
			    		   if(!input.val()){
			    			   return false;
			    		   }
			    		   return true;
			    	   } 
			       },
			       { 
			    	   input: '#thruDate', message: uiLabelMap.FieldRequired, action: 'keyup, blur', 
			    	   rule: function (input, commit){
			    		   if(!input.val()){
			    			   return false;
			    		   }
			    		   return true;
			    	   } 
			       },
			       { 
			    	   input: '#thruDate', message: uiLabelMap.EstimatedThruDateMustGreaterEqualFromDate, 
			    	   action: 'keyup, blur', 
			    	   rule: function (input, commit){
			    		   var fromDate = $("#fromDate").jqxDateTimeInput('val', 'date');
			    		   var thruDate = input.jqxDateTimeInput('val', 'date');
			    		   if(thruDate && thruDate < fromDate){
			    			   return false;
			    		   }
			    		   return true;
			    	   } 
			       },
			       { 
			    	   input: '#registerFromDate', message: uiLabelMap.FieldRequired, action: 'keyup, blur', 
			    	   rule: function (input, commit){
			    		   if(!input.val()){
			    			   return false;
			    		   }
			    		   return true;
			    	   } 
			       },
			       { 
			    	   input: '#registerThruDate', message: uiLabelMap.FieldRequired, action: 'keyup, blur', 
			    	   rule: function (input, commit){
			    		   if(!input.val()){
			    			   return false;
			    		   }
			    		   return true;
			    	   } 
			       },
			       { 
			    	   input: '#registerThruDate', message: uiLabelMap.RegisterThruDateMustGreaterEqualFromDate, 
			    	   action: 'keyup, blur', 
			    	   rule: function (input, commit){
			    		   var fromDate = $("#registerFromDate").jqxDateTimeInput('val', 'date');
			    		   var thruDate = input.jqxDateTimeInput('val', 'date');
			    		   if(thruDate && thruDate <= fromDate){
			    			   return false;
			    		   }
			    		   return true;
			    	   } 
			       },
			       { 
			    	   input: '#registerThruDate', message: uiLabelMap.RegisterThruDateMustLessThanStartDateTraining, action: 'keyup, blur', 
			    	   rule: function (input, commit){
			    		   var fromDateStartTraing = $("#fromDate").jqxDateTimeInput('val', 'date');
			    		   var thruDateEndRegis = input.jqxDateTimeInput('val', 'date');
			    		   if(thruDateEndRegis >= fromDateStartTraing){
			    			   return false;
			    		   }
			    		   return true;
			    	   } 
			       },
			       ]
		});
	};
	
	var validate = function(){
		return $("#editTrainingInfoWindow").jqxValidator('validate');
	};
	var hideValidate = function(){
		$("#editTrainingInfoWindow").jqxValidator('hide');
	};
	var initEvent = function(){
		$("#editTrainingCourseInfoBtn").click(function(event){
			editTrainingCourseInfo.openWindow();
		});
		$("#cancelEditTrainingCourseInfo").click(function(event){
			$("#editTrainingInfoWindow").jqxWindow('close');
		});
		$("#saveEditTrainingCourseInfo").click(function(event){
			if(!validate()){
				return;
			}
			var data = getData();
			$("#loadingTrainingCourseInfo").show();
			$("#cancelEditTrainingCourseInfo").attr("disabled", "disabled");
			$("#saveEditTrainingCourseInfo").attr("disabled", "disabled");
			data.trainingCourseId = globalVar.trainingCourseId;
			$.ajax({
				url: 'updateTrainingCourse',
				data: data,
				type: 'POST',
				success: function(response){
					if(response.responseMessage == 'success'){
						Grid.renderMessage('EditTraining', response.successMessage, 
								{autoClose: true, template : 'info',appendContainer : "#containerEditTraining",opacity : 0.9});
						updateViewData();
						$("#editTrainingInfoWindow").jqxWindow('close');
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
					$("#loadingTrainingCourseInfo").hide();
	    			$("#cancelEditTrainingCourseInfo").removeAttr("disabled");
	    			$("#saveEditTrainingCourseInfo").removeAttr("disabled");
				}
			});
		});
	};
	var updateViewData = function(){
		$("#trainingCodeView").html($("#trainingCourseCode").val());
		var registerFromDate = $("#registerFromDate").jqxDateTimeInput('val', 'date');
		$("#registerFromDateView").html(getDate(registerFromDate) + "/" + getMonth(registerFromDate) + "/" + registerFromDate.getFullYear());
		var registerThruDate = $("#registerThruDate").jqxDateTimeInput('val', 'date');
		$("#registerThruDateView").html(getDate(registerThruDate) + "/" + getMonth(registerThruDate) + "/" + registerThruDate.getFullYear());
		var fromDate = $("#fromDate").jqxDateTimeInput('val', 'date');
		$("#fromDateView").html(getDate(fromDate) + "/" + getMonth(fromDate) + "/" + fromDate.getFullYear());
		var thruDate = $("#thruDate").jqxDateTimeInput('val', 'date');
		$("#thruDateView").html(getDate(thruDate) + "/" + getMonth(thruDate) + "/" + thruDate.getFullYear());
		var trainingPurposeTypeCheckeds = $("#trainingPurposeTypeId").jqxComboBox('getCheckedItems');
		var trainingPurposeTypeList = [];
		var trainingPurposeTypeDesc = uiLabelMap.HRCommonNotSetting; 
		for(var i = 0; i < trainingPurposeTypeCheckeds.length; i++){
			trainingPurposeTypeList.push(trainingPurposeTypeCheckeds[i].label);
		}
		if(trainingPurposeTypeList.length > 0){
			trainingPurposeTypeDesc = trainingPurposeTypeList.join(", ");
		}
		$("#trainingPurposeTypeView").html(trainingPurposeTypeDesc);
		var location = $("#location").val();
		if(location.length == 0){
			location = uiLabelMap.HRCommonNotSetting;
		}
		$("#locationView").html(location);
		$("#trainingCourseNameView").html($("#trainingCourseName").val());
		var certificate = $("#certificate").val();
		if(certificate.length == 0){
			certificate = uiLabelMap.HRCommonNotSetting;
		}
		$("#certificateView").html(certificate);
		var trainingFormTypeSelected = $("#trainingFormTypeId").jqxDropDownList('getSelectedItem');
		var trainingFormTypeDesc = uiLabelMap.HRCommonNotSetting;
		if(trainingFormTypeSelected){
			trainingFormTypeDesc = trainingFormTypeSelected.label;
		}
		$("#trainingFormTypeView").html(trainingFormTypeDesc);
		var description = $("#description").jqxEditor('val');
		if(description.length == 0){
			description = uiLabelMap.HRCommonNotSetting;
		}
		$("#descriptionView").html(description);
	};
	var openWindow = function(){
		openJqxWindow($("#editTrainingInfoWindow"));
	};
	return{
		init: init,
		getData: getData,
		setData: setData,
		openWindow: openWindow
	}
}());

$(document).ready(function(){
	editTrainingCourseInfo.init();
});
