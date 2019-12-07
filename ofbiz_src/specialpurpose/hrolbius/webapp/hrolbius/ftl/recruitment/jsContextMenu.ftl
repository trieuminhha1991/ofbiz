//Create Context Menu
var contextMenu = $("#contextMenu").jqxMenu({ width: 230, height: 200, theme: theme, autoOpenPopup: false, mode: 'popup'});
<#assign currentWorkEffort = delegator.findOne("WorkEffort", {"workEffortId" : parameters.workEffortId}, false)>
<#if currentWorkEffort.workEffortTypeId == "RECRUITMENT_PROCESS">
	$("#contextMenu").jqxMenu('disable', 'sendEmail', true);
	$("#contextMenu").jqxMenu('disable', 'scoreInterview', true);
	$("#contextMenu").jqxMenu('disable', 'scoreExam', true);
	$("#contextMenu").jqxMenu('disable', 'proposeProbation', true);
	$("#contextMenu").jqxMenu('disable', 'confirmed', true);
	$("#contextMenu").jqxMenu('disable', 'contacted', true);
<#elseif currentWorkEffort.workEffortTypeId == "ROUND_EXAM">
	$("#contextMenu").jqxMenu('disable', 'agreePreliminary', true);
	$("#contextMenu").jqxMenu('disable', 'scoreInterview', true);
	$("#contextMenu").jqxMenu('disable', 'proposeProbation', true);
<#elseif currentWorkEffort.workEffortTypeId == "ROUND_INTERVIEW">
	$("#contextMenu").jqxMenu('disable', 'agreePreliminary', true);
	$("#contextMenu").jqxMenu('disable', 'scoreExam', true);
	$("#contextMenu").jqxMenu('disable', 'proposeProbation', true);
<#elseif currentWorkEffort.workEffortTypeId == "ROUND_SELECTED">
	$("#contextMenu").jqxMenu('disable', 'sendEmail', true);
	$("#contextMenu").jqxMenu('disable', 'agreePreliminary', true);
	$("#contextMenu").jqxMenu('disable', 'scoreInterview', true);
	$("#contextMenu").jqxMenu('disable', 'agreePreliminary', true);
	$("#contextMenu").jqxMenu('disable', 'scoreExam', true);
	$("#contextMenu").jqxMenu('disable', 'confirmed', true);
	$("#contextMenu").jqxMenu('disable', 'contacted', true);
</#if>
$('#jqxgrid').on('rowClick', function (event) {
	<#if currentWorkEffort.workEffortTypeId == "RECRUITMENT_PROCESS">
		var rowindexes = $('#jqxgrid').jqxGrid('getselectedrowindexes');
	    var disable = false; 
	    for(var i = 0; i < rowindexes.length; i++){
	    	var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindexes[i]);
	    	if(rowData['workEffortName'] != ''){
	    		disable = true;
	    		break;
	    	}
	    }
	    $("#contextMenu").jqxMenu('disable', 'agreePreliminary', disable);
    <#elseif currentWorkEffort.workEffortTypeId == "ROUND_INTERVIEW">
	    var rowindexes = $('#jqxgrid').jqxGrid('getselectedrowindexes');
	    var disableContact = false;
	    var disableConfirm = false;
	    for(var i = 0; i < rowindexes.length; i++){
	    	var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindexes[i]);
	    	if(rowData['availabilityStatusId'] == 'AAS_CONTACTED'){
	    		disableContact = true;
	    		break;
	    	}
	    }
	    for(var i = 0; i < rowindexes.length; i++){
	    	var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindexes[i]);
	    	if(rowData['availabilityStatusId'] == 'AAS_CONFIRMED'){
	    		disableConfirm = true;
	    		disableContact = true;
	    		break;
	    	}
	    }
	    $("#contextMenu").jqxMenu('disable', 'contacted', disableContact);
	    $("#contextMenu").jqxMenu('disable', 'confirmed', disableConfirm);
	</#if>
});

// handle context menu clicks.
$("#contextMenu").on('itemclick', function (event) {
	var element = event.args;
	if(element.getAttribute('id') == 'sendEmail'){
		var applTmp = new Array();
		var rowindexes = $('#jqxgrid').jqxGrid('getselectedrowindexes');
		for(var i = 0; i < rowindexes.length; i++){
			var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindexes[i]);
			$.ajax({
				url: 'getPersonalEmail',
				type: "POST",
				data: {'partyId' : rowData['partyId'], 'contactMechPurposeTypeId': 'PERSONAL_EMAIL'},
				dataType: 'json',
				async: false,
				success : function(data) {
					if(data.emailAddress){
						rowData['emailAddress'] = data['emailAddress'];
					}
				}
			});
			applTmp[i] = rowData;
		}
		selectedApplData = applTmp;
		$("#sendEmailWindow").jqxWindow('open');
	}else if(element.getAttribute('id') == 'agreePreliminary'){
		var applTmp = new Array();
		var rowindexes = $('#jqxgrid').jqxGrid('getselectedrowindexes');
		for(var i = 0; i < rowindexes.length; i++){
			var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindexes[i]);
			applTmp[i] = rowData;
		}
		var submitData = {};
		submitData['listApplicant'] = JSON.stringify(applTmp);
		//send request agree preliminary
		$.ajax({
			url: 'agreePreliminary',
			type: "POST",
			data: submitData,
			dataType: 'json',
			async: false,
			success : function(data) {
				if(data.responseMessage == 'success'){
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					
				}
			}
		});
	}else if(element.getAttribute('id') == 'scoreInterview'){
		var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
		selectedPartyId = rowData['partyId'];
		$("#invFullname").text(rowData['lastName'] + " " + rowData['middleName'] + " " + rowData['firstName']);
		$("#invGender").text(rowData['gender'] == 'M' ? '${StringUtil.wrapString(uiLabelMap.Male)}' : '${StringUtil.wrapString(uiLabelMap.Female)}');
		var birthDate = rowData['birthDate'];
		$("#invBirthDate").text(birthDate.getDate() + "/" + (birthDate.getMonth() + 1) + "/" + birthDate.getFullYear());
		//send request getWorkEffortAttr
		$.ajax({
			url: 'getWorkEffortAttr',
			type: "POST",
			data: {'partyId': rowData['partyId'], 'attrName': 'emplPositionTypeId'},
			dataType: 'json',
			async: false,
			success : function(data1) {
				if(data1.responseMessage == 'success'){
					$.ajax({
						url: 'getEmplPositionType',
						type: 'POST',
						data: {'emplPositionTypeId': data1['attrValue']},
						dataType: 'json',
						async: false,
						success : function(data) {
							if(data.responseMessage == 'success'){
								$("#invEmplPositionType").text(data.description);
							}
						}
					});
				}
			}
		});
		
		//send request getWorkEffortAttr
		$.ajax({
			url: 'getWorkEffortAttr',
			type: "POST",
			data: {'partyId': rowData['partyId'], 'attrName': 'partyId'},
			dataType: 'json',
			async: false,
			success : function(data1) {
				if(data1.responseMessage == 'success'){
					$.ajax({
						url: 'getPartyName',
						type: 'POST',
						data: {'partyId': data1['attrValue']},
						dataType: 'json',
						async: false,
						success : function(data) {
							if(data.responseMessage == 'success'){
								$("#invPartyId").text(data.partyName);
							}
						}
					});
				}
			}
		});
		
		$("#scoreInterviewWindow").jqxWindow('open');
	}else if(element.getAttribute('id') == 'scoreExam'){
		var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
		selectedPartyId = rowData['partyId'];
		$("#examFullname").text(rowData['lastName'] + " " + rowData['middleName'] + " " + rowData['firstName']);
		$("#examGender").text(rowData['gender'] == 'M' ? '${StringUtil.wrapString(uiLabelMap.Male)}' : '${StringUtil.wrapString(uiLabelMap.Female)}');
		var date = Date.parse(rowData['birthDate']);
		$("#examBirthDate").text(date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getFullYear());
		//send request getWorkEffortAttr
		$.ajax({
			url: 'getWorkEffortAttr',
			type: "POST",
			data: {'partyId': rowData['partyId'], 'attrName': 'emplPositionTypeId'},
			dataType: 'json',
			async: false,
			success : function(data1) {
				if(data1.responseMessage == 'success'){
					$.ajax({
						url: 'getEmplPositionType',
						type: 'POST',
						data: {'emplPositionTypeId': data1['attrValue']},
						dataType: 'json',
						async: false,
						success : function(data) {
							if(data.responseMessage == 'success'){
								$("#examEmplPositionType").text(data.description);
							}
						}
					});
				}
			}
		});
		
		//send request getWorkEffortAttr
		$.ajax({
			url: 'getWorkEffortAttr',
			type: "POST",
			data: {'partyId': rowData['partyId'], 'attrName': 'partyId'},
			dataType: 'json',
			async: false,
			success : function(data1) {
				if(data1.responseMessage == 'success'){
					$.ajax({
						url: 'getPartyName',
						type: 'POST',
						data: {'partyId': data1['attrValue']},
						dataType: 'json',
						async: false,
						success : function(data) {
							if(data.responseMessage == 'success'){
								$("#examPartyId").text(data.partyName);
							}
						}
					});
				}
			}
		});
		$("#scoreExamWindow").jqxWindow('open');
	}else if(element.getAttribute('id') == 'proposeProbation'){
		var rowindexes = $('#jqxgrid').jqxGrid('getselectedrowindexes');
		offerProbData = new Array();
		for(var rowindex = 0; rowindex < rowindexes.length; rowindex++){
			var offerProb = {};
			var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindexes[rowindex]);
			offerProb = {};
			offerProb['workEffortId'] = ${parameters.workEffortId};
			offerProb['partyId'] = rowData['partyId'];
			$.ajax({
				url: 'getWorkEffortAttr',
				type: "POST",
				data: {'partyId': rowData['partyId'], 'attrName': 'emplPositionTypeId'},
				dataType: 'json',
				async: false,
				success : function(data1) {
					if(data1.responseMessage == 'success'){
						$.ajax({
							url: 'getEmplPositionType',
							type: 'POST',
							data: {'emplPositionTypeId': data1['attrValue']},
							dataType: 'json',
							async: false,
							success : function(data) {
								if(data.responseMessage == 'success'){
									offerProb['emplPositionTypeId'] = data1['attrValue'];
								}
							}
						});
					}
				}
			});
			
			//send request getWorkEffortAttr
			$.ajax({
				url: 'getWorkEffortAttr',
				type: "POST",
				data: {'partyId': rowData['partyId'], 'attrName': 'partyId'},
				dataType: 'json',
				async: false,
				success : function(data1) {
					if(data1.responseMessage == 'success'){
						$.ajax({
							url: 'getPartyName',
							type: 'POST',
							data: {'partyId': data1['attrValue']},
							dataType: 'json',
							async: false,
							success : function(data) {
								if(data.responseMessage == 'success'){
									offerProb['partyIdWork'] = data1['attrValue'];
								}
							}
						});
					}
				}
			});
			
			offerProbData[rowindex] = offerProb;
		}
		$("#jqxgridOfferProb").jqxGrid('updatebounddata');
		$("#proposeProbWindow").jqxWindow('open');
	}else if(element.getAttribute('id') == 'contacted'){
		var applTmp = new Array();
		var rowindexes = $('#jqxgrid').jqxGrid('getselectedrowindexes');
		for(var i = 0; i < rowindexes.length; i++){
			var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindexes[i]);
			applTmp[i] = rowData;
		}
		var submitData = {};
		submitData['listApplicant'] = JSON.stringify(applTmp);
		submitData['statusId'] = 'AAS_CONTACTED';
		//send request agree preliminary
		$.ajax({
			url: 'updateAvailability',
			type: "POST",
			data: submitData,
			dataType: 'json',
			async: false,
			success : function(data) {
				if(data.responseMessage == 'success'){
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					
				}
			}
		});
	}else if(element.getAttribute('id') == 'confirmed'){
		var applTmp = new Array();
		var rowindexes = $('#jqxgrid').jqxGrid('getselectedrowindexes');
		for(var i = 0; i < rowindexes.length; i++){
			var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindexes[i]);
			applTmp[i] = rowData;
		}
		var submitData = {};
		submitData['listApplicant'] = JSON.stringify(applTmp);
		submitData['statusId'] = 'AAS_CONFIRMED';
		//send request agree preliminary
		$.ajax({
			url: 'updateAvailability',
			type: "POST",
			data: submitData,
			dataType: 'json',
			async: false,
			success : function(data) {
				if(data.responseMessage == 'success'){
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					
				}
			}
		});
	}
});