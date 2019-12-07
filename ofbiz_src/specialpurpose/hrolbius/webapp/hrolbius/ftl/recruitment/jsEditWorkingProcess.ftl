/******************************************************Edit Working Process**************************************************************************/
	//Handle alterSaveFamily
	$("#createNewWorkingProcess").jqxValidator({
		rules: [
			{input: '#wpFromDate', message: '${uiLabelMap.LTDateFieldRequired}', action: 'keyup, change, close', 
				rule: function (input, commit) {
                    if(input.jqxDateTimeInput('getDate') > $("#wpThruDate").jqxDateTimeInput('getDate')){
                    	return false;
                    }else{
                    	return true;
                    }
                    	
            	}
			},
			{input: '#wpThruDate', message: '${uiLabelMap.GTDateFieldRequired}', action: 'keyup, change, close', 
				rule: function (input, commit) {
                    if(input.jqxDateTimeInput('getDate') < $("#wpFromDate").jqxDateTimeInput('getDate')){
                    	return false;
                    }else{
                    	return true;
                    }
                    	
            	}
			},
		]
	});
	$("#alterSaveWP").click(function () {
		$("#createNewWorkingProcess").jqxValidator('validate');
	});

	//Handle alterSaveWP
	$("#createNewWorkingProcess").on('validationSuccess', function (event) {
		var row;
	        row = {
	        		companyName:$('#wpCompanyName').val(),
	        		emplPositionTypeId:$("#wpEmplPositionTypeId").val(),
	        		jobDescription:$("#wpJobDescription").val(),
	        		payroll:$("#wpPayroll").val(),
	        		terminationReasonId:$("#wpTerminationReasonId").val(),
	        		rewardDiscrip:$("#wpRewardDiscrip").val(),
	        		fromDate:$("#wpFromDate").jqxDateTimeInput('getDate').getTime(),
	        		thruDate:$("#wpThruDate").jqxDateTimeInput('getDate').getTime()
			  };
	        wpData[wpIndex] = row;
	        wpIndex++;
	        $("#jqxgridWP").jqxGrid('updatebounddata');
	        // select the first row and clear the selection.
	        $("#jqxgridWP").jqxGrid('clearSelection');
	        $("#jqxgridWP").jqxGrid('selectRow', 0);
	        $("#createNewWorkingProcessWindow").jqxWindow('close');
	    });
	//Create NewWorkingProcessWindow
	$("#createNewWorkingProcessWindow").jqxWindow({
        showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "80%", height: 500, minWidth: '40%', width: "50%", isModal: true,
        theme:'olbius', collapsed:false
    });
	
	//Create wpCompanyName
	$("#wpCompanyName").jqxInput({width: 195});
	
	//Create wpEmplPositionTypeId
	$("#wpEmplPositionTypeId").jqxInput({width: 195});
	
	//Create wpJobDescription
	$("#wpJobDescription").jqxInput({width: 195});
	
	//Create wpPayroll
	$("#wpPayroll").jqxInput({width: 195});
	
	//Create wpTerminationReasonId
	$("#wpTerminationReasonId").jqxInput({width: 195});
	
	//Create wpRewardDiscrip
	$("#wpRewardDiscrip").jqxInput({width: 195});
	
	//Create wpFromDate
	$("#wpFromDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy'});
	
	//Create wpThruDate
	$("#wpThruDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy'});
	
	/******************************************************End Working Process**********************************************************************/