var trainingCourseInfo = (function(){
	var init = function(){
		initJqxInput();
		initJqxDropDownList();
		initJqxCombobox();
		initJqxDateTimeInput();
		initJqxValidator();
	};
	
	var initJqxInput = function(){
		$("#trainingCourseId" + globalVar.createNewSuffix).jqxInput({width: '96%', height: 20});
		$("#trainingCourseName" + globalVar.createNewSuffix).jqxInput({width: '96%', height: 20});
		$("#location" + globalVar.createNewSuffix).jqxInput({width: '96%', height: 20});
		$("#certificate" + globalVar.createNewSuffix).jqxInput({width: '105.2%', height: 20});
		
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.trainingFormTypeArr, $("#trainingFormTypeId" + globalVar.createNewSuffix), 'trainingFormTypeId', 'description', 25, "98%");
		//createJqxDropDownList(globalVar.geoArr, $("#geoId" + globalVar.createNewSuffix), 'geoId', 'geoName', 25, "98%");
	};
	
	var initJqxCombobox = function(){
		$("#trainingPurposeTypeId" + globalVar.createNewSuffix).jqxComboBox({source: globalVar.trainingPurposeTypeArr, multiSelect: true, 
			width: "106%", height: 25, valueMember: "trainingPurposeTypeId", displayMember: "description", checkboxes: true});
		if(globalVar.trainingPurposeTypeArr.length < 8){
			$("#trainingPurposeTypeId" + globalVar.createNewSuffix).jqxComboBox({autoDropDownHeight: true});
		}else{
			$("#trainingPurposeTypeId" + globalVar.createNewSuffix).jqxComboBox({autoDropDownHeight: false});
		}
	};
	
	var initJqxDateTimeInput = function(){
		var today = new Date(globalVar.nowTimestamp);
		var yesterday = new Date(globalVar.nowTimestamp);
		yesterday.setDate(today.getDate() - 1);
		var twoDayAgo = new Date(globalVar.nowTimestamp);
		twoDayAgo.setDate(today.getDate() - 2);
		$("#fromDate" + globalVar.createNewSuffix).jqxDateTimeInput({width: '98%', height: 25});
		$("#registerFromDate" + globalVar.createNewSuffix).jqxDateTimeInput({width: '98%', height: 25});
		$("#thruDate" + globalVar.createNewSuffix).jqxDateTimeInput({width: '98%', height: 25});
		$("#thruDate" + globalVar.createNewSuffix).jqxDateTimeInput('clear');
		$("#registerThruDate" + globalVar.createNewSuffix).jqxDateTimeInput({width: '98%', height: 25});
		$("#registerThruDate" + globalVar.createNewSuffix).val(yesterday);
		$("#registerFromDate" + globalVar.createNewSuffix).val(twoDayAgo);
	};
	
	var initJqxEditor = function(){
		$('#description' + globalVar.createNewSuffix).jqxInput({ width: '104.3%', height: 160});
		$('#description' + globalVar.createNewSuffix).val("");
	};
	
	var initInjqxWindow = function(){
		initJqxEditor();
	};
	
	var getData = function(){
		var data = {};
		data.trainingCourseCode = $("#trainingCourseId" + globalVar.createNewSuffix).val();
		data.trainingCourseName = $("#trainingCourseName" + globalVar.createNewSuffix).val();
		var trainingFormTypeId = $("#trainingFormTypeId" + globalVar.createNewSuffix).val();
		if(trainingFormTypeId){
			data.trainingFormTypeId = trainingFormTypeId;
		}
		var trainingPurposeTypeRecords = $("#trainingPurposeTypeId" + globalVar.createNewSuffix).jqxComboBox('getCheckedItems');
		if(trainingPurposeTypeRecords.length > 0){
			var trainingPurposeTypeArr = [];
			for(var i = 0; i < trainingPurposeTypeRecords.length; i++){
				trainingPurposeTypeArr.push(trainingPurposeTypeRecords[i].value);
			}
			data.trainingPurposeTypeIds = JSON.stringify(trainingPurposeTypeArr);
		}
		/*var geoId = $("#geoId" + globalVar.createNewSuffix).val();
		if(geoId){
			data.geoId = geoId;
		}*/
		data.location = $("#location" + globalVar.createNewSuffix).val();
		var fromDate = $("#fromDate" + globalVar.createNewSuffix).jqxDateTimeInput('val', 'date');
		if(fromDate){
			data.fromDate = fromDate.getTime();
		}
		var registerFromDate = $("#registerFromDate" + globalVar.createNewSuffix).jqxDateTimeInput('val', 'date');
		if(registerFromDate){
			data.registerFromDate = registerFromDate.getTime();
		}
		var thruDate = $("#thruDate" + globalVar.createNewSuffix).jqxDateTimeInput('val', 'date');
		if(thruDate){
			data.thruDate = thruDate.getTime();
		}
		var registerThruDate = $("#registerThruDate" + globalVar.createNewSuffix).jqxDateTimeInput('val', 'date');
		if(registerThruDate){
			data.registerThruDate = registerThruDate.getTime();
		}
		var description = $('#description' + globalVar.createNewSuffix).val().split('\n').join(' ');
		if(description){
			data.description = description; 
		}
		
		var certificate = $("#certificate" + globalVar.createNewSuffix).val();
		if(typeof(certificate) != 'undefined' && certificate.length > 0){
			data.certificate = certificate;
		}
		return data;
	};
	
	var initJqxValidator = function(){
		$("#trainingCourseInfo").jqxValidator({
			position: 'bottom',
			rules:[
			       {
			    	   input : "#trainingPurposeTypeId" + globalVar.createNewSuffix, message : uiLabelMap.InvalidChar, action : 'keyup,blur',
			    	   rule : function(input, commit){
			    		   var val = input.val();
			    		   if(val){
			    			   if(validationNameWithoutHtml(val)){
			    				   return false;
			    			   }
			    		   }
			    		   return true;
			    	   }
			       },
			       {input : "#location" + globalVar.createNewSuffix, message : uiLabelMap.InvalidChar, action : 'blur,keyup',
			    	   rule : function(input, commit){
			    		   var val = input.val();
			    		   if(val){
			    			   if(validationNameWithoutHtml(val)){
			    				   return false;
			    			   }
			    		   }
			    		   return true;
			    	   }
			       },
			       {
			    	   input : '#certificate' + globalVar.createNewSuffix, message : uiLabelMap.InvalidChar, action : 'blur,keyup',
			    	   rule : function(input, commit){
			    		   var val = input.val();
			    		   if(val){
			    			   if(validationNameWithoutHtml(val)){
			    				   return false;
			    			   }
			    		   }
			    		   return true;
			    	   }
			       },
			       { input: '#trainingCourseId' + globalVar.createNewSuffix, message: uiLabelMap.FieldRequired, action: 'keyup,blur', rule: 'required' },
			       { input: '#trainingCourseId' + globalVar.createNewSuffix, message: uiLabelMap.OnlyContainInvalidChar, action: 'keyup, blur',
			    	   rule: function (input, commit){
			    		   var value = input.val();
			    		   if(value){
			    			   if(/^[a-zA-Z0-9-_]*$/.test(value) == false) {
			    				   return false;
			    			   }
			    		   }
			    		   return true;
			    	   }
			       },
			       { input: '#trainingCourseName' + globalVar.createNewSuffix, message: uiLabelMap.FieldRequired, action: 'keyup, blur', rule: 'required' },
			       { input: '#trainingCourseName' + globalVar.createNewSuffix, message: uiLabelMap.HRContainSpecialSymbol, action: 'keyup, blur',
			    	   rule: function (input, commit){
			    		   var value = input.val();
			    		   if(value && validationNameWithoutHtml(value)){
			    			   return false;
			    		   }
			    		   return true;
			    	   }
			       },
			       { 
			    	   input: '#fromDate' + globalVar.createNewSuffix, message: uiLabelMap.FieldRequired, action: 'valueChanged', 
			    	   rule: function (input, commit){
			    		   if(!input.val()){
			    			   return false;
			    		   }
			    		   return true;
			    	   } 
			       },
			       { 
			    	   input: '#thruDate' + globalVar.createNewSuffix, message: uiLabelMap.FieldRequired, action: 'valueChanged', 
			    	   rule: function (input, commit){
			    		   if(!input.val()){
			    			   return false;
			    		   }
			    		   return true;
			    	   } 
			       },
			       { 
			    	   input: '#thruDate' + globalVar.createNewSuffix, message: uiLabelMap.EstimatedThruDateMustGreaterEqualFromDate, action: 'valueChanged', 
			    	   rule: function (input, commit){
			    		   var fromDate = $("#fromDate" + globalVar.createNewSuffix).jqxDateTimeInput('val', 'date');
			    		   var thruDate = input.jqxDateTimeInput('val', 'date');
			    		   if(thruDate && thruDate < fromDate){
			    			   return false;
			    		   }
			    		   return true;
			    	   } 
			       },
			       { 
			    	   input: '#registerFromDate' + globalVar.createNewSuffix, message: uiLabelMap.FieldRequired, action: 'valueChanged', 
			    	   rule: function (input, commit){ 
			    		   if(!input.val()){
			    			   return false;
			    		   }
			    		   return true;
			    	   } 
			       },
			       { 
			    	   input: '#registerThruDate' + globalVar.createNewSuffix, message: uiLabelMap.FieldRequired, action: 'valueChanged', 
			    	   rule: function (input, commit){
			    		   if(!input.val()){
			    			   return false;
			    		   }
			    		   return true;
			    	   } 
			       },
			       { 
			    	   input: '#registerThruDate' + globalVar.createNewSuffix, 
			    	   message: uiLabelMap.RegisterThruDateMustGreaterEqualFromDate, action: 'valueChanged', 
			    	   rule: function (input, commit){
			    		   var fromDate = $("#registerFromDate" + globalVar.createNewSuffix).jqxDateTimeInput('val', 'date');
			    		   var thruDate = input.jqxDateTimeInput('val', 'date');
			    		   if(thruDate && thruDate < fromDate){
			    			   return false;
			    		   }
			    		   return true;
			    	   } 
			       },
			       { 
			    	   input: '#registerThruDate' + globalVar.createNewSuffix, 
			    	   message: uiLabelMap.RegisterThruDateMustLessThanStartDateTraining, action: 'valueChanged', 
			    	   rule: function (input, commit){
			    		   var fromDateStartTraing = $("#fromDate" + globalVar.createNewSuffix).jqxDateTimeInput('val', 'date');
			    		   var thruDateEndRegis = input.jqxDateTimeInput('val', 'date');
			    		   if(fromDateStartTraing && thruDateEndRegis >= fromDateStartTraing){
			    			   return false;
			    		   }
			    		   return true;
			    	   } 
			       },
//			       {
//			    	   input : '#description' + globalVar.createNewSuffix, message : uiLabelMap.InvalidChar, action : 'keyup,blur',
//			    	   rule :function(input, commit){
//			    		   var val = input.val();
//			    		   if(val){
//			    			   if(validationNameWithoutHtml(val)){
//			    				   return false;
//			    			   }
//			    		   }
//			    		   return true;
//			    	   }
//			       },
			       ]
		});
	};
	
	var validate = function(){
		return $("#trainingCourseInfo").jqxValidator('validate');
	};
	var hideValidate = function(){
		$("#trainingCourseInfo").jqxValidator('hide');
	};
	
	var reset = function(){
		$("#trainingPurposeTypeId" + globalVar.createNewSuffix).jqxComboBox('uncheckAll');
		Grid.clearForm($("#trainingCourseInfo"));
	};
	return{
		init: init,
		initInjqxWindow: initInjqxWindow,
		validate: validate,
		hideValidate: hideValidate,
		getData: getData,
		reset: reset
	}
}());
