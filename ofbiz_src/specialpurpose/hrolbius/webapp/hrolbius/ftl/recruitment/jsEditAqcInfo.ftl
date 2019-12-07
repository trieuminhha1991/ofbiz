/******************************************************Edit Family**************************************************************************/
	//Handle alterSaveFamily
	$("#createNewAcquaintance").jqxValidator({
		rules: [
			{input: '#aqcLastName', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
			{input: '#aqcFirstName', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'}
		]
	});
	$("#alterSaveAqc").click(function () {
		$("#createNewAcquaintance").jqxValidator('validate');
	});

	//Handle alterSaveFamily
	$("#createNewAcquaintance").on('validationSuccess', function (event) {
		var row;
	        row = {
	        		lastName:$('#aqcLastName').val(),
	        		middleName:$("#aqcMiddleName").val(),
	        		firstName:$("#aqcFirstName").val(),
	        		partyRelationshipTypeId:$("#aqcRelationshipTypeId").val(),
	        		birthDate:$("#aqcBirthDate").jqxDateTimeInput('getDate').getTime(),
	        		occupation:$("#aqcOccupation").val(),
	        		placeWork:$("#aqcPlaceWork").val(),
	        		phoneNumber:$("#aqcPhoneNumber").val(),
	        		knowFor:$("#aqcKnowFor").val(),
			  };
	        aqcIndex = aqcData.length - 1;
	        aqcData[++aqcIndex] = row;
	        $("#jqxgridAcq").jqxGrid('updatebounddata');
	        // select the first row and clear the selection.
	        $("#jqxgridAcq").jqxGrid('clearSelection');
	        $("#jqxgridAcq").jqxGrid('selectRow', 0);
	        $("#createNewAcquaintanceWindow").jqxWindow('close');
	    });
	//Create new family window
	$("#createNewAcquaintanceWindow").jqxWindow({
        showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "80%", height: 500, minWidth: '40%', width: "50%", isModal: true,
        modalZIndex: 1000, theme:'olbius', collapsed:false
    });
	
	//Create aqcLastName
	$("#aqcLastName").jqxInput({width: 195});
	
	//Create aqcMiddleName
	$("#aqcMiddleName").jqxInput({width: 195});
	
	//Create aqcFirstName
	$("#aqcFirstName").jqxInput({width: 195});
	
	//Create aqcRelationshipTypeId
	$("#aqcRelationshipTypeId").jqxDropDownList({selectedIndex: 0, source: friendTypeData, valueMember: 'partyRelationshipTypeId', displayMember:'description'});
	
	//Create aqcBirthDate
	$("#aqcBirthDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy'});
	
	//Create aqcOccupation
	$("#aqcOccupation").jqxInput({width: 195});
	
	//Create aqcPlaceWork
	$("#aqcPlaceWork").jqxInput({width: 195});
	
	//Create aqcPhoneNumber
	$("#aqcPhoneNumber").jqxInput({width: 195});
	
	//Create aqcKnowFor
	$("#aqcKnowFor").jqxInput({width: 195});
	/******************************************************End Edit Family**********************************************************************/