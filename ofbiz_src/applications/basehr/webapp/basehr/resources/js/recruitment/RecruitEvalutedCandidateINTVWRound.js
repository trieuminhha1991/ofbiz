var evalutedCandidateInINTWRoundObj = (function(){
	var _recruitmentPlanId = null;
	var _roundOrder = null;
	var _candidateId = null;
	var _examinerId = null;
	var _datafieldArr = ['REC_EVAL_UNSATISFIED', 'REC_EVAL_LSATISFIED', 'REC_EVAL_LSATISFIED', 'REC_EVAL_SATISFIED', 'REC_EVAL_GOOD', 'REC_EVAL_VERY_GOOD'];
	var init = function(){
		initJqxGrid();
		initJqxRadioButton();
		initJqxWindow();
		initEvent();
		create_spinner($("#spinnerEvaluatedINTVWRound"));
		//$("#jqxNotificationrecruitINTWRoundGrid").jqxNotification({ width: "100%", appendContainer: "#containerrecruitINTWRoundGrid", opacity: 0.9, template: "info" });
	};
	var initJqxGrid = function(){
		var datafield = [{name: 'standardEvalId', type: 'string'},
		                 {name: 'standardEvalName', type: 'string'},
		                 {name: 'REC_EVAL_UNSATISFIED', type: 'bool'},
		                 {name: 'REC_EVAL_LSATISFIED', type: 'bool'},
		                 {name: 'REC_EVAL_SATISFIED', type: 'bool'},
		                 {name: 'REC_EVAL_GOOD', type: 'bool'},
		                 {name: 'REC_EVAL_VERY_GOOD', type: 'bool'},
		                ];
		var columns = [{datafield: 'standardEvalId', hidden: true},
		               {text: uiLabelMap.HRCommonStandard, datafield: 'standardEvalName', width: '20%', editable: false},
		               {text: uiLabelMap.InterviewerEvalUnsatisfied, datafield: 'REC_EVAL_UNSATISFIED',width: '16%', columntype: 'checkbox',
		            	   cellbeginedit: function (row, datafield, columntype) {
		            		   return true;
						       uncheckAllColumns(row);   
						    }
		               },
		               {text: uiLabelMap.InterviewerEvalLittleSatisfied, datafield: 'REC_EVAL_LSATISFIED', width: '16%', columntype: 'checkbox',
		            	   cellbeginedit: function (row, datafield, columntype) {
						         uncheckAllColumns(row);   
						         return true;
						    }
		               },
		               {text: uiLabelMap.InterviewerEvalSatisfied, datafield: 'REC_EVAL_SATISFIED', width: '16%', columntype: 'checkbox',
		            	   cellbeginedit: function (row, datafield, columntype) {
						         uncheckAllColumns(row);
						         return true;
						    }
		               },
		               {text: uiLabelMap.InterviewerEvalGood, datafield: 'REC_EVAL_GOOD', width: '16%', columntype: 'checkbox',
		            	   cellbeginedit: function (row, datafield, columntype) {
						         uncheckAllColumns(row);
						         return true;
						    }
		               },
		               {text: uiLabelMap.InterviewerEvalVeryGood, datafield: 'REC_EVAL_VERY_GOOD', width: '16%', columntype: 'checkbox',
		            	   cellbeginedit: function (row, datafield, columntype) {
						         uncheckAllColumns(row);
						         return true;
						    }
		               }
		               ];
		var grid = $("#recruitINTWRoundGrid");
		var config = {
				url: '',
				//rendertoolbar : rendertoolbar,
				showtoolbar : false,
				width : '100%',
				virtualmode: false,
				editable: true,
				filterable: false,
				editmode: 'dblclick',
				localization: getLocalization(),
				pagesizeoptions: [6, 12, 18, 25],
				source: {
					pagesize : 6, 
				}
		};
		if(!globalVar.editEvaluatedCandidate){
			config.editable = false;
		}
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initJqxTextArea = function(){
		$("#evaluatedINTVWRoundComment").jqxTextArea({height: 110, width: '99%', minLength: 1});
		if(!globalVar.editEvaluatedCandidate){
			$("#evaluatedINTVWRoundComment").jqxTextArea({disabled: true});
		}
	};
	var initJqxRadioButton = function(){
		$("#intvwRoundCANDPassed").jqxRadioButton({ width: 150, height: 25, checked: true});
        $("#intvwRoundCANDNotPassed").jqxRadioButton({ width: 150, height: 25});
        if(!globalVar.editEvaluatedCandidate){
        	$("#intvwRoundCANDPassed").jqxRadioButton({disabled: true});
        	$("#intvwRoundCANDNotPassed").jqxRadioButton({disabled: true});
		}
	};
	var uncheckAllColumns = function(rowIndex){
		for(var i = 0; i < _datafieldArr.length; i++){
			$("#recruitINTWRoundGrid").jqxGrid('setcellvalue', rowIndex, _datafieldArr[i], false);
		}
	};
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxTextArea();
		};
		createJqxWindow($("#evaluatedINTVWRoundWindow"), 800, 550, initContent);
		$("#evaluatedINTVWRoundWindow").on('close', function(event){
			_recruitmentPlanId = null;
			_roundOrder = null;
			_candidateId = null;
			_examinerId = null;
			refreshGrid([]);
			$("#evaluatedINTVWRoundComment").val("");
			$("#intvwRoundCANDPassed").jqxRadioButton({ checked: true});
			$("#intvwRoundCANDNotPassed").jqxRadioButton({ checked: false});
		});
		$("#evaluatedINTVWRoundWindow").on('open', function(event){
			
		});
	};
	var loadGridData = function(){
		$("#loadingEvaluatedINTVWRound").show();
		$.when(
			$.ajax({
				url: 'getRecruitmentINTVWEvalParty',
				data: {recruitmentPlanId: _recruitmentPlanId, roundOrder: _roundOrder, partyId: _candidateId, interviewerId: _examinerId},
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						refreshGrid(response.listReturn);
					}else{
						bootbox.dialog(response.errorMessage,
								[{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]
						);	
					}
				},
				complete: function(jqXHR, textStatus){
				}
			}),
			$.ajax({
				url: 'getRecruitRoundCandidateExaminer',
				data: {recruitmentPlanId: _recruitmentPlanId, roundOrder: _roundOrder, partyId: _candidateId, examinerId: _examinerId},
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						if(response.comment){
							$("#evaluatedINTVWRoundComment").val(response.comment);
						}
						if(response.resultTypeId == "ACHIEVED"){
							$("#intvwRoundCANDPassed").jqxRadioButton({checked: true});
						}else if(response.resultTypeId == "NOT_ACHIEVED"){
							$("#intvwRoundCANDNotPassed").jqxRadioButton({checked: true});
						}
					}else{
						bootbox.dialog(response.errorMessage,
								[{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]
						);	
					}
				},
			})
		).done(function(a1, a2){
			$("#loadingEvaluatedINTVWRound").hide();
		});
	};
	var getSubmitData = function(){
		var dataSubmit = {};
		var allRowsData = $("#recruitINTWRoundGrid").jqxGrid('getrows');
		var recruitmentIntvwStandardEval = [];
		for(var i = 0; i < allRowsData.length; i++){
			var rowData = allRowsData[i];
			for(var j = 0; j < _datafieldArr.length; j++){
				if(rowData[_datafieldArr[j]]){
					recruitmentIntvwStandardEval.push({standardEvalId: rowData.standardEvalId, statusId: _datafieldArr[j]});
					break;
				}
			}
		}
		dataSubmit.recruitmentIntvwStandardEval = JSON.stringify(recruitmentIntvwStandardEval);
		dataSubmit.comment = $("#evaluatedINTVWRoundComment").val();
		if($("#intvwRoundCANDPassed").jqxRadioButton('checked')){
			dataSubmit.resultTypeId = "ACHIEVED"; 
		}else{
			dataSubmit.resultTypeId = "NOT_ACHIEVED";
		}
		dataSubmit.partyId = _candidateId;
		dataSubmit.roundOrder = _roundOrder;
		dataSubmit.recruitmentPlanId = _recruitmentPlanId;
		return dataSubmit;
	};
	var setData = function(data){
		_recruitmentPlanId = data.recruitmentPlanId;
		_roundOrder = data.roundOrder;
		_candidateId = data.partyId;
		_examinerId = data.examinerId;
		prepareData();
	};
	var initEvent = function(){
		$("#cancelEvalutedINTWCAND").click(function(event){
			$("#evaluatedINTVWRoundWindow").jqxWindow('close');
		});
		if(globalVar.editEvaluatedCandidate){
			$("#saveEvalutedINTWCAND").click(function(event){
				bootbox.dialog(uiLabelMap.RecruitmentEvaluatedCandidateConfirm,
						[{
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								updateRecruitRoundCandidateExaminer();
							}	
						},
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);
			});
		}
	};
	var updateRecruitRoundCandidateExaminer = function(){
		var dataSubmit = getSubmitData();
		$("#loadingEvaluatedINTVWRound").show();
		disableAll();
		$.ajax({
			url: 'updateRecruitRoundCandidateExaminer',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage("recruitEvaluatedCandidateGrid", response.successMessage, {autoClose: true,
						template : 'info',
						appendContainer : "#containerrecruitEvaluatedCandidateGrid",
						opacity : 0.9});
					$("#recruitEvaluatedCandidateGrid").jqxGrid('updatebounddata');
					$("#evaluatedINTVWRoundWindow").jqxWindow('close');
				}else{
					bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]
					);	
				}
			},
			complete: function(jqXHR, textStatus){
				$("#loadingEvaluatedINTVWRound").hide();
				enableAll();
			}
		});
	};
	var disableAll = function(){
		$("#cancelEvalutedINTWCAND").attr("disabled", "disabled");
		$("#saveEvalutedINTWCAND").attr("disabled", "disabled");
		$("#recruitINTWRoundGrid").jqxGrid({disabled: true});
		$("#recruitINTWRoundGrid").jqxGrid({editable: false});
		$("#evaluatedINTVWRoundComment").jqxTextArea({disabled: true});
		$("#intvwRoundCANDPassed").jqxRadioButton({disabled: true});
		$("#intvwRoundCANDNotPassed").jqxRadioButton({disabled: true});
	};
	var enableAll = function(){
		$("#cancelEvalutedINTWCAND").removeAttr("disabled");
		$("#saveEvalutedINTWCAND").removeAttr("disabled");
		$("#recruitINTWRoundGrid").jqxGrid({disabled: false});
		$("#recruitINTWRoundGrid").jqxGrid({editable: true});
		$("#evaluatedINTVWRoundComment").jqxTextArea({disabled: false});
		$("#intvwRoundCANDPassed").jqxRadioButton({disabled: false});
		$("#intvwRoundCANDNotPassed").jqxRadioButton({disabled: false});
	};
	var prepareData = function(){
		loadGridData();
	};
	var refreshGrid = function(data){
		var source = $("#recruitINTWRoundGrid").jqxGrid('source');
		source._source.localdata = data;
		$("#recruitINTWRoundGrid").jqxGrid('source', source);
	};
	var openWindow = function(){
		openJqxWindow($("#evaluatedINTVWRoundWindow"));
	};
	return{
		init: init,
		openWindow: openWindow,
		setData: setData,
	}
}());
$(document).ready(function(){
	evalutedCandidateInINTWRoundObj.init();
});