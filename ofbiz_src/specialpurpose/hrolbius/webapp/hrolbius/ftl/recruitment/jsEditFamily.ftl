/******************************************************Edit Family**************************************************************************/
	//Handle alterSaveFamily
	$("#createNewFamily").jqxValidator({
		rules: [
			{input: '#fmLastName', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
			{input: '#fmFirstName', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'}
		]
	});
	$("#alterSaveFamily").click(function () {
		$("#createNewFamily").jqxValidator('validate');
	});
	
	$("#createNewFamily").on('validationSuccess', function (event) {
		var row;
	        row = {
	        		lastName:$('#fmLastName').val(),
	        		middleName:$("#fmMiddleName").val(),
	        		firstName:$("#fmFirstName").val(),
	        		partyRelationshipTypeId:$("#fmRelationshipTypeId").val(),
	        		birthDate:$("#fmBirthDate").jqxDateTimeInput('getDate').getTime(),
	        		occupation:$("#fmOccupation").val(),
	        		placeWork:$("#fmPlaceWork").val(),
	        		phoneNumber:$("#fmPhoneNumber").val(),
	        		emergencyContact:$("#fmEmergencyContact").val() == true ? 'Y' : 'N'
			  };
	        fmIndex = fmData.length - 1;
	        fmData[++fmIndex] = row;
	        $("#jqxgridFamily").jqxGrid('updatebounddata');
	        // select the first row and clear the selection.
	        $("#jqxgridFamily").jqxGrid('clearSelection');
	        $("#jqxgridFamily").jqxGrid('selectRow', 0);
	        $("#createNewFamilyWindow").jqxWindow('close');
	    });
	//Create new family window
	$("#createNewFamilyWindow").jqxWindow({
        showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "80%", height: 500, minWidth: '40%', width: "50%", isModal: true,
        modalZIndex: 1000, theme:'olbius', collapsed:false
    });
	
	//Create fmLastName
	$("#fmLastName").jqxInput({width: 195});
	
	//Create fmMiddleName
	$("#fmMiddleName").jqxInput({width: 195});
	
	//Create fmFirstName
	$("#fmFirstName").jqxInput({width: 195});
	
	//Create fmRelationshipTypeId
	$("#fmRelationshipTypeId").jqxDropDownList({selectedIndex: 0, source: partyRelaTypeData, valueMember: 'partyRelationshipTypeId', displayMember:'description'});
	
	//Create fmBirthDate
	$("#fmBirthDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy'});
	
	//Create fmOccupation
	$("#fmOccupation").jqxInput({width: 195});
	
	//Create fmPlaceWork
	$("#fmPlaceWork").jqxInput({width: 195});
	
	//Create fmPhoneNumber
	$("#fmPhoneNumber").jqxInput({width: 195});
	
	//Create fmEmergencyContact
	$("#fmEmergencyContact").jqxCheckBox({ width: 120, height: 25});
	/******************************************************End Edit Family**********************************************************************/