/******************************************************Edit Education**************************************************************************/
	//Handle alterSaveFamily
	$("#createNewEducation").jqxValidator({
		rules: [
			{input: '#eduSchoolId', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', 
				rule: function (input, commit) {
                    if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && input.val()) {
                        return true;
                    }else if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && !input.val()){
                    	return false;
                    }else {
                    	return true;
                    }
                }
			},
			{input: '#eduMajorId', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', 
				rule: function (input, commit) {
                    if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && input.val()) {
                        return true;
                    }else if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && !input.val()){
                    	return false;
                    }else {
                    	return true;
                    }
                }
			},
			{input: '#eduStudyModeTypeId', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', 
				rule: function (input, commit) {
                    if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && input.val()) {
                        return true;
                    }else if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && !input.val()){
                    	return false;
                    }else {
                    	return true;
                    }
                }
			},
			{input: '#eduDegreeClassificationTypeId', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', 
				rule: function (input, commit) {
                    if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && input.val()) {
                        return true;
                    }else if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && !input.val()){
                    	return false;
                    }else {
                    	return true;
                    }
                }
			},
			{input: '#eduFromDate', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', 
				rule: function (input, commit) {
                    if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && input.val()) {
                        return true;
                    }else if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && !input.val()){
                    	return false;
                    }else {
                    	return true;
                    }
                }
			},
			{input: '#eduThruDate', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', 
				rule: function (input, commit) {
                    if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && input.val()) {
                        return true;
                    }else if ($('#eduSystemTypeId').val() != 'HIGHSCHOOL' && !input.val()){
                    	return false;
                    }else {
                    	return true;
                    }
                }
			},
			{input: '#eduFromDate', message: '${uiLabelMap.LTDateFieldRequired}', action: 'keyup, change, close', 
				rule: function (input, commit) {
                    if(input.jqxDateTimeInput('getDate') > $("#eduThruDate").jqxDateTimeInput('getDate') && $("#eduThruDate").jqxDateTimeInput('getDate')){
                    	return false;
                    }else{
                    	return true;
                    }
                    	
            	}
			},
			{input: '#eduThruDate', message: '${uiLabelMap.GTDateFieldRequired}', action: 'keyup, change, close', 
				rule: function (input, commit) {
                    if(input.jqxDateTimeInput('getDate') < $("#eduFromDate").jqxDateTimeInput('getDate') && $("#eduFromDate").jqxDateTimeInput('getDate')){
                    	return false;
                    }else{
                    	return true;
                    }
                    	
            	}
			},
		]
	});
	$("#alterSaveEdu").click(function () {
		$("#createNewEducation").jqxValidator('validate');
	});
	
	$("#createNewEducation").on('validationSuccess', function (event) {
		var row;
	        row = {
	        		schoolId:$('#eduSchoolId').val(),
	        		fromDate:$("#eduFromDate").jqxDateTimeInput('getDate').getTime(),
	        		thruDate:$("#eduThruDate").jqxDateTimeInput('getDate').getTime(),
	        		majorId:$("#eduMajorId").val(),
	        		studyModeTypeId:$("#eduStudyModeTypeId").val(),
	        		classificationTypeId:$("#eduDegreeClassificationTypeId").val(),
	        		educationSystemTypeId:$("#eduSystemTypeId").val(),
			  };
	        eduIndex = eduData.length - 1;
	        eduData[++eduIndex] = row;
	        $("#jqxgridEdu").jqxGrid('updatebounddata');
	        // select the first row and clear the selection.
	        $("#jqxgridEdu").jqxGrid('clearSelection');
	        $("#jqxgridEdu").jqxGrid('selectRow', 0);
	        $("#createNewEduWindow").jqxWindow('close');
	    });
	//Create new family window
	$("#createNewEduWindow").jqxWindow({
        showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "80%", height: 500, minWidth: '40%', width: "50%", isModal: true,
        modalZIndex: 1000, theme:'olbius', collapsed:false
    });
	
	//Create eduSchoolId
	$("#eduSchoolId").jqxDropDownList({source: schoolData, selectedIndex: 0, valueMember:'schoolId', displayMember:'description'});
	
	//Create eduFromDate
	$("#eduFromDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy'});
	
	//Create eduThruDate
	$("#eduThruDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy'});
	
	//Create eduMajorId
	$("#eduMajorId").jqxDropDownList({source: majorData, selectedIndex: 0, valueMember: 'majorId', displayMember:'description'});
	
	//Create eduStudyModeTypeId
	$("#eduStudyModeTypeId").jqxDropDownList({source: studyModeTypeData, selectedIndex: 0, valueMember: 'studyModeTypeId', displayMember:'description'});
	
	//Create eduDegreeClassificationTypeId
	$("#eduDegreeClassificationTypeId").jqxDropDownList({source: classificationTypeData, selectedIndex: 0, valueMember: 'classificationTypeId', displayMember:'description'});
	
	//Create eduSystemTypeId
	$("#eduSystemTypeId").jqxDropDownList({source: educationSystemTypeData, selectedIndex: 0, valueMember: 'educationSystemTypeId', displayMember:'description'});
	
	/******************************************************End Edit Family**********************************************************************/