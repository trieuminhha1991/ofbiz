//Create Context Menu
var contextMenu = $("#contextMenu").jqxMenu({ width: 230, height: 150, theme: theme, autoOpenPopup: false, mode: 'popup'});

// handle context menu clicks.
$("#contextMenu").on('itemclick', function (event) {
	var element = event.args;
	if(element.getAttribute('id') == 'approveOfferPro'){
		$("#wdwApproveProb").jqxWindow('open');
	}else if(element.getAttribute('id') == 'proposeOfferPro'){
		var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
		var submitData = {};
		submitData['partyId'] = rowData['partyId'];
		submitData['offerProbationId'] = rowData['offerProbationId'];
		submitData['approverRoleTypeId'] = rowData['approverRoleTypeId'];
		submitData['workEffortId'] = rowData['workEffortId'];
		//Sent request propose probation
		$.ajax({
			url: 'proposeOfferProbation',
			type: "POST",
			data: submitData,
			dataType: 'json',
			async: false,
			success : function(data) {
				if(!data._ERROR_MESSAGE_){
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					bootbox.confirm("${uiLabelMap.offerPob_proposeFail}", function(result) {
						return;
					});
				}
	        }
		});
	}else if(element.getAttribute('id') == 'createProbAgreement'){
		var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
		offerProbationId = rowData['offerProbationId'];
		//Create emplPositionTypeId
		$.ajax({
			url: 'getEmplPositionType',
			type: 'POST',
			data: {'emplPositionTypeId': rowData['emplPositionTypeId']},
			dataType: 'json',
			async: false,
			success : function(data) {
				if(data.responseMessage == 'success'){
					$("#emplPositionTypeIdLabel").text(data.description);
				}
			}
		});
		$("#emplPositionTypeId").val(rowData['emplPositionTypeId']);
    	
		$.ajax({
			url: 'getPartyName',
			type: 'POST',
			data: {'partyId': rowData['partyIdWork']},
			dataType: 'json',
			async: false,
			success : function(data) {
				if(data.responseMessage == 'success'){
					$("#partyIdWorkLabel").text(data.partyName);
				}
			}
		});
		$("#partyIdWork").val(rowData['partyIdWork']);
    	
		
		$("#recruitmentTypeId").val('TUYENTHANG');
		<#assign recruitmentType = delegator.findOne("RecruitmentType", {'recruitmentTypeId' : 'TUYENTHANG'}, false)>
		$("#recruitmentTypeIdLabel").text('${StringUtil.wrapString(recruitmentType?if_exists.description?if_exists)}');
		
    	//Create basicSalary
    	$("#basicSalaryLabel").text(rowData['basicSalary']);
    	$("#basicSalary").val(rowData['basicSalary']);
    	
    	//Create phoneAllowance
    	$("#phoneAllowanceLabel").text(rowData['phoneAllowance']);
    	$("#phoneAllowance").val(rowData['phoneAllowance']);
    	
    	//Create trafficAllowance
    	$("#trafficAllowanceLabel").text(rowData['trafficAllowance']);
    	$("#trafficAllowance").val(rowData['trafficAllowance']);
    	
    	//Create otherAllowance
    	$("#otherAllowanceLabel").text(rowData['otherAllowance']);
    	$("#otherAllowance").val(rowData['otherAllowance']);
    	
    	//Create percentBasicSalary
    	$("#percentBasicSalaryLabel").text(rowData['percentBasicSalary']);
    	$("#percentBasicSalary").val(rowData['percentBasicSalary']);
    	
    	//Create inductedStartDateLabel
    	inductedStartDate = rowData['inductedStartDate'];
    	$("#inductedStartDateLabel").text(inductedStartDate.getDate() + '/' + (inductedStartDate.getMonth() + 1) + '/' + inductedStartDate.getFullYear());
    	$("#inductedStartDate").val(inductedStartDate.getTime());
    	
    	//Create inductedCompletionDateLabel
    	inductedCompletionDate = rowData['inductedCompletionDate'];
    	$("#inductedCompletionDateLabel").text(inductedCompletionDate.getDate() + '/' + (inductedCompletionDate.getMonth() + 1) + '/' + inductedCompletionDate.getFullYear());
    	$("#inductedCompletionDate").val(inductedCompletionDate.getTime());
		$("#createProbAgreementWindow").jqxWindow('open');
	}else if(element.getAttribute('id') == 'createUserLogin'){
		var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
		staticPartyId = rowData['partyId'];
		$("#createNewUserLoginWindow").jqxWindow('open');
	}
});