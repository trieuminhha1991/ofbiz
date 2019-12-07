	//Handle click submit
	$("#submit").click(function(){
		var newAppData = {};
		newAppData['birthDate'] = $("#birthDate").jqxDateTimeInput('getDate').getTime();
		newAppData['lastName'] = $("#lastName").val();
		newAppData['middleName'] = $("#middleName").val();
		newAppData['firstName'] = $("#firstName").val();
		newAppData['gender'] = $("#gender").val();
		newAppData['birthPlace'] = $("#birthPlace").val();
		newAppData['height'] = $("#height").val();
		newAppData['weight'] = $("#weight").val();
		newAppData['idNumber'] = ($("#idNumber").val()).replace(/_/g, "");;
		newAppData['idIssuePlace'] = $("#idIssuePlace").val();
		if($("#idIssueDate").jqxDateTimeInput('getDate')){
			newAppData['idIssueDate'] = $("#idIssueDate").jqxDateTimeInput('getDate').getTime();
		}
		newAppData['maritalStatus'] = $("#maritalStatus").val();
		newAppData['numberChildren'] = $("#numberChildren").val();
		newAppData['ethnicOrigin'] = $("#ethnicOrigin").val();
		newAppData['religion'] = $("#religion").val();
		newAppData['nativeLand'] = $("#nativeLand").val();
		newAppData['homeTel'] = $("#homeTel").val();
		newAppData['mobile'] = $("#mobile").val();
		newAppData['diffTel'] = $("#diffTel").val();
		newAppData['email'] = $("#email").val();
		newAppData['prAddress'] = $("#prAddress").val();
		newAppData['prCountry'] = $("#prCountry").val();
		newAppData['prProvince'] = $("#prProvince").val();
		newAppData['prDistrict'] = $("#prDistrict").val();
		newAppData['prWard'] = $("#prWard").val();
		newAppData['crAddress'] = $("#crAddress").val();
		newAppData['crCountry'] = $("#crCountry").val();
		newAppData['crProvince'] = $("#crProvince").val();
		newAppData['crDistrict'] = $("#crDistrict").val();
		newAppData['crWard'] = $("#crWard").val();
		newAppData['fmData'] = JSON.stringify(fmData);
		newAppData['eduData'] = JSON.stringify(eduData);
		newAppData['wpData'] = JSON.stringify(wpData);
		newAppData['skillData'] = JSON.stringify(skillData);
		newAppData['jqxSpecialSkillEditor'] = $("#jqxSpecialSkillEditor").val();
		newAppData['badHealth'] = $("#badHealth").val() == true ? 'Y' : 'N';
		newAppData['badHealthDetail'] = $("#badHealthDetail").val().trim();
		newAppData['badInfo'] = $("#badInfo").val() == true ? 'Y' : 'N';
		newAppData['badInfoDetail'] = $("#badInfoDetail").val();
		newAppData['aqcData'] = JSON.stringify(aqcData);
		newAppData['workEffortId'] = '${parameters.workEffortId}';
		if($('#sourceTypeId')){
			newAppData['sourceTypeId'] = $('#sourceTypeId').val();
		}
		if($('#referredByPartyId')){
			newAppData['referredByPartyId'] = $('#referredByPartyId').val();
		}
		newAppData['comments'] = $('#jqxOverviewEditor').val();
		if($('#finAccountName')){
			newAppData['finAccountName'] = $('#finAccountName').val();
		}
		if($('#finAccountCode')){
			newAppData['finAccountCode'] = $('#finAccountCode').val();
		}
		//Sent request create applicant
		$.ajax({
			url: 'createApplicant',
			type: "POST",
			data: newAppData,
			dataType: 'json',
			async: false,
			success : function(data) {
				if(data.responseMessage == 'error'){
					bootbox.confirm("Thêm mới ứng viên vào đợt tuyển dụng không thành công!", function(result) {
						return;
					});
				}else{
					$("#alterpopupNewApplicant").jqxWindow('close');
					$("#jqxgrid").jqxGrid('updatebounddata');
				}
	        }
		});
	});