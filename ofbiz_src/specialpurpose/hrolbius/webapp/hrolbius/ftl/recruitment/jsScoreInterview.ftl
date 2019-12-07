$("#scoreInterviewWindow").jqxWindow({
    showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "90%", height: 630, minWidth: '40%', width: "90%", isModal: true,
    theme:theme, collapsed:false,
    initContent: function () {
    	// Create jqxTabs.
        $('#jqxScoreInterviewTabs').jqxTabs({ width: '98%',theme: theme, height: 530, position: 'top',disabled:true,
        	initTabContent:function (tab) {
        		if(tab == 0){
        			//Create face
        			$('#face').jqxNumberInput({ decimalDigits: 0, width: 150, min: 0, max: 10, spinButtons: true, inputMode: 'simple'});
        			
        			//Create figure
        			$('#figure').jqxNumberInput({ decimalDigits: 0, width: 150, min: 0, max: 10, spinButtons: true, inputMode: 'simple'});
        			
        			//Create voice
        			$('#voice').jqxNumberInput({ decimalDigits: 0, min: 0, width: 150, max: 10, spinButtons: true, inputMode: 'simple'});
        			
        			//Create communication
        			$('#communication').jqxNumberInput({ decimalDigits: 0, min: 0, width: 150, max: 10, spinButtons: true, inputMode: 'simple'});
        			
        			//Create confidence
        			$('#confidence').jqxNumberInput({ decimalDigits: 0, min: 0, width: 150, max: 10, spinButtons: true, inputMode: 'simple'});
        			
        			//Create circumstance
        			$('#circumstance').jqxNumberInput({ decimalDigits: 0, min: 0, width: 150, max: 10, spinButtons: true, inputMode: 'simple'});
        			
        			//Create agility
        			$('#agility').jqxNumberInput({ decimalDigits: 0, min: 0, width: 150, max: 10, spinButtons: true, inputMode: 'simple'});
        			
        			//Create logic
        			$('#logic').jqxNumberInput({ decimalDigits: 0, min: 0, width: 150, max: 10, spinButtons: true, inputMode: 'simple'});
        			
        			//Create answer
        			$('#answer').jqxNumberInput({ decimalDigits: 0, min: 0, width: 150, max: 10, spinButtons: true, inputMode: 'simple'});
        			
        			//Create honest
        			$('#honest').jqxNumberInput({ decimalDigits: 0, min: 0, width: 150, max: 10, spinButtons: true, inputMode: 'simple'});
        			
        			//Create experience
        			$('#experience').jqxNumberInput({ decimalDigits: 0, min: 0, width: 150, max: 10, spinButtons: true, inputMode: 'simple'});
        			
        			//Create expertise
        			$('#expertise').jqxNumberInput({ decimalDigits: 0, min: 0, width: 150, max: 10, spinButtons: true, inputMode: 'simple'});
        			
        			$("#parentBackgroundId").jqxDropDownList({source: parentBackgroundData, width: 150, selectedIndex: 0, valueMember: "parentBackgroundId", displayMember: "description"});
        			
        			$("#siblingBackgroundId").jqxDropDownList({source: siblingBackgroundData,width: 150, selectedIndex: 0, valueMember: "siblingBackgroundId", displayMember: "description"});
        			
        			$("#spousesBackgroundId").jqxDropDownList({source: spousesBackgroundData,width: 150, selectedIndex: 0, valueMember: "spousesBackgroundId", displayMember: "description"});
        			
        			$("#childBackgroundId").jqxDropDownList({source: childBackgroundData,width: 150, selectedIndex: 0, valueMember: "childBackgroundId", displayMember: "description"});
        			
        			$("#workChangeId").jqxDropDownList({source: workChangeData,width: 150, selectedIndex: 0, valueMember: "workChangeId", displayMember: "description"});
        			
        			$("#uniCertificateId").jqxDropDownList({source: uniCertificateData,width: 150, selectedIndex: 0, valueMember: "uniCertificateId", displayMember: "description"});
        			
        			$("#itCertificateId").jqxDropDownList({source: itCertificateData,width: 150, selectedIndex: 0, valueMember: "itCertificateId", displayMember: "description"});
        			
        			$("#engCertificateId").jqxDropDownList({source: engCertificateData,width: 150, selectedIndex: 0, valueMember: "engCertificateId", displayMember: "description"});
        			
        			$("#teamWorkId").jqxDropDownList({source: teamWorkData,width: 150, selectedIndex: 0, valueMember: "teamWorkId", displayMember: "description"});
        			
        			$("#aloneWorkId").jqxDropDownList({source: aloneWorkData,width: 150, selectedIndex: 0, valueMember: "aloneWorkId", displayMember: "description"});
        			
        			$("#workChangeId").jqxDropDownList({source: workChangeData,width: 150, selectedIndex: 0, valueMember: "workChangeId", displayMember: "description"});
        			
        			$("#currentSal").jqxNumberInput({ decimalDigits: 0, min: 0, width: 150, spinButtons: true });
        			
        			$("#proposeSal").jqxNumberInput({ decimalDigits: 0, min: 0, width: 150, spinButtons: true });
        			
        			//Create jobRequirable
        			$('#jobRequirable').jqxNumberInput({ decimalDigits: 0, min: 0, width: 150, max: 10, spinButtons: true, inputMode: 'simple'});
        		}else if(tab == 1){
        			$("#generalRate").jqxEditor({width: 800});
        		}else{	
        			$("#propose").jqxEditor({width: 800, height: 300});
        			$("#isNextRound").jqxCheckBox({checked:false});
        			$("#resultId").jqxDropDownList({source: resultData, width: 150, selectedIndex: 0, valueMember: "resultId", displayMember: "description"});
        		}
        	}
        });
        
        $("#jqxScoreInterviewTabs").jqxTabs('enableAt', 0);
    }
});
$(".score-next").click(function(){
	var selectedItem = $("#jqxScoreInterviewTabs").jqxTabs('selectedItem');
	$("#jqxScoreInterviewTabs").jqxTabs('disableAt', selectedItem);
	$("#jqxScoreInterviewTabs").jqxTabs('enableAt', selectedItem + 1);
	$("#jqxScoreInterviewTabs").jqxTabs('next');
});

$(".score-back").click(function(){
	var selectedItem = $("#jqxScoreInterviewTabs").jqxTabs('selectedItem');
	$("#jqxScoreInterviewTabs").jqxTabs('enableAt', selectedItem - 1);
	$("#jqxScoreInterviewTabs").jqxTabs('disableAt', selectedItem);
	$('#jqxScoreInterviewTabs').jqxTabs('previous');
});

$("#scoreSubmit").click(function(){
	var submitData = {};
	submitData['face'] = $("#face").val();
	submitData['figure'] = $("#figure").val();
	submitData['voice'] = $("#voice").val();
	submitData['communication'] = $("#communication").val();
	submitData['confidence'] = $("#confidence").val();
	submitData['agility'] = $("#agility").val();
	submitData['circumstance'] = $("#circumstance").val();
	submitData['logic'] = $("#logic").val();
	submitData['answer'] = $("#answer").val();
	submitData['honest'] = $("#honest").val();
	submitData['experience'] = $("#experience").val();
	submitData['expertise'] = $("#expertise").val();
	submitData['jobRequirable'] = $("#jobRequirable").val();
	submitData['workChangeId'] = $("#workChangeId").val();
	submitData['parentBackgroundId'] = $("#parentBackgroundId").val();
	submitData['siblingBackgroundId'] = $("#siblingBackgroundId").val();
	submitData['spousesBackgroundId'] = $("#spousesBackgroundId").val();
	submitData['childBackgroundId'] = $("#childBackgroundId").val();
	submitData['uniCertificateId'] = $("#uniCertificateId").val();
	submitData['itCertificateId'] = $("#itCertificateId").val();
	submitData['engCertificateId'] = $("#engCertificateId").val();
	submitData['teamWorkId'] = $("#teamWorkId").val();
	submitData['aloneWorkId'] = $("#aloneWorkId").val();
	submitData['currentSal'] = $("#currentSal").val();
	submitData['proposeSal'] = $("#proposeSal").val();
	submitData['jobRequirable'] = $("#jobRequirable").val();
	submitData['generalRate'] = $("#generalRate").val();
	submitData['propose'] = $("#propose").val();
	submitData['isNextRound'] = $("#isNextRound").val() == true ? 'Y' : 'N';
	submitData['resultId'] = $("#resultId").val();
	submitData['workEffortId'] = '${parameters.workEffortId}';
	submitData['partyId'] = selectedPartyId;
	//Sent request create applicant
	$.ajax({
		url: 'scoreInterview',
		type: "POST",
		data: submitData,
		dataType: 'json',
		async: false,
		success : function(data) {
			if(data.responseMessage == 'error'){
				bootbox.confirm("Đánh giá phỏng vấn ứng viên không thành công!", function(result) {
					return;
				});
			}else{
				$("#scoreInterviewWindow").jqxWindow('close');
				$("#jqxgrid").jqxGrid('updatebounddata');
			}
        }
	});
});