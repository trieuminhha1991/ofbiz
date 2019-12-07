$("#scoreExamWindow").jqxWindow({
    showCollapseButton: false, maxHeight: 1000, autoOpen: false,modalZIndex: 10000, maxWidth: "90%", height: 400, minWidth: '40%', width: "50%", isModal: true,
    theme:theme, collapsed:false,cancelButton:$("#alterCancelScoreExam"),
    initContent: function () {
    	$("#examIsNextRound").jqxCheckBox({checked:false});
    	$("#core").jqxMaskedInput({ width: 145, height: 25, mask: '#####'});
		$("#examResultId").jqxDropDownList({source: resultData, width: 150, selectedIndex: 0, valueMember: "resultId", displayMember: "description"});
    }
});
$("#alterSaveScoreExam").click(function(){
	var submitData = {};
	submitData['core'] = $("#core").val();
	submitData['resultId'] = $("#examResultId").val();
	submitData['isNextRound'] = $("#examIsNextRound").val() == true ? 'Y' : 'N';
	submitData['workEffortId'] = '${parameters.workEffortId}';
	submitData['partyId'] = selectedPartyId;
	//Sent request create applicant
	$.ajax({
		url: 'scoreExam',
		type: "POST",
		data: submitData,
		dataType: 'json',
		async: false,
		success : function(data) {
			if(data.responseMessage == 'error'){
				bootbox.confirm("Đánh giá thi tuyển ứng viên không thành công!", function(result) {
					return;
				});
			}else{
				$("#jqxgrid").jqxGrid('updatebounddata');
				$("#scoreExamWindow").jqxWindow('close');
			}
        }
	});
});