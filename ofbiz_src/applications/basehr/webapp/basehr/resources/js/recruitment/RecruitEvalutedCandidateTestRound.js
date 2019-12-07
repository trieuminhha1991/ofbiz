var evalutedCandidateInTestRoundObj = (function(){
	var _recruitmentPlanId = null;
	var _roundOrder = null;
	var _candidateId = null;
	var _examinerId = null;
	var init = function(){
		initJqxGrid();
		initJqxRadioButton();
		initJqxWindow();
		initEvent();
		create_spinner($("#spinnerEvaluatedTestRound"));
		//$("#jqxNotificationrecruitTestRoundSubjGrid").jqxNotification({ width: "100%", appendContainer: "#containerrecruitTestRoundSubjGrid", opacity: 0.9, template: "info" });
	};
	var initJqxGrid = function(){
		var datafield= [{name: 'subjectId', type: 'string'},
		                {name: 'subjectName', type: 'string'},
		                {name: 'point', type: 'number'},
						{name: 'ratio', type: 'number'}];
		var columns = [{datafield: 'subjectId', hidden: true},
	                     {text: uiLabelMap.RecruitmentSubjectName, width: '40%', datafield: 'subjectName', editable: false},
	                     {text: uiLabelMap.HRCommonRatio, width: '30%', datafield: 'ratio', editable: true, columntype: 'numberinput',
	                    	 cellsalign: 'right',
	                    	 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
	                    	        editor.jqxNumberInput({width: cellwidth, height: cellwidth,  spinButtons: true, min: 0, max: 1, decimalDigits: 1});
	                    	 }
	                     },
	                     {text: uiLabelMap.HRCommonPoint, width: '30%', datafield: 'point', editable: true, columntype: 'numberinput',
	                    	 cellsalign: 'right',
	                    	 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
	                    		 editor.jqxNumberInput({width: cellwidth, height: cellwidth,  spinButtons: true, min: 0, max: 1, decimalDigits: 1});
	                    	 }
	                     }
	                     ];
		var grid = $("#recruitTestRoundSubjGrid");
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "recruitTestRoundSubjGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.RecruitmentSubject + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
			var container = $('#toolbarButtonContainer' + id);
			var maincontainer = $("#toolbarcontainer" + id);
		};
		var config = {
				url: '',
				rendertoolbar : rendertoolbar,
				showtoolbar : true,
				width : '100%',
				virtualmode: false,
				editable: true,
				filterable: false,
				editmode: 'dblclick',
				localization: getLocalization(),
				source: {
					pagesize : 5, 
				}
			};
		if(!globalVar.editEvaluatedCandidate){
			config.editable = false;
		}
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initJqxTextArea = function(){
		$("#evaluatedtestRoundComment").jqxTextArea({height: 110, width: '99%', minLength: 1});
		if(!globalVar.editEvaluatedCandidate){
			$("#evaluatedtestRoundComment").jqxTextArea({disabled: true});
		}
	};
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxTextArea();
		};
		createJqxWindow($("#evaluatedCANDTestRoundWindow"), 750, 550, initContent);
		$("#evaluatedCANDTestRoundWindow").on('close', function(event){
			_recruitmentPlanId = null;
			_roundOrder = null;
			_candidateId = null;
			_examinerId = null;
			refreshGrid([]);
			$("#evaluatedtestRoundComment").val("");
			$("#testRoundCANDPassed").jqxRadioButton({ checked: true});
			$("#testRoundCANDNotPassed").jqxRadioButton({ checked: false});
		});
		$("#evaluatedCANDTestRoundWindow").on('open', function(event){
			
		});
	};
	var getSubmitData = function(){
		var dataSubmit = {};
		var allRowsData = $("#recruitTestRoundSubjGrid").jqxGrid('getrows');
		var subjectObj = [];
		for(var i = 0; i < allRowsData.length; i++){
			var rowData = allRowsData[i];
			subjectObj.push({subjectId: rowData.subjectId, point: rowData.point});
		}
		dataSubmit.recruitmentRoundSubject = JSON.stringify(subjectObj);
		dataSubmit.comment = $("#evaluatedtestRoundComment").val();
		if($("#testRoundCANDPassed").jqxRadioButton('checked')){
			dataSubmit.resultTypeId = "ACHIEVED"; 
		}else{
			dataSubmit.resultTypeId = "NOT_ACHIEVED";
		}
		dataSubmit.partyId = _candidateId;
		dataSubmit.roundOrder = _roundOrder;
		dataSubmit.recruitmentPlanId = _recruitmentPlanId;
		return dataSubmit;
	};
	
	var prepareData = function(){
		$("#loadingEvaluatedTestRound").show();
		$.when(
				$.ajax({
					url: 'getListRecruitmentRoundSubjectParty',
					data: {recruitmentPlanId: _recruitmentPlanId, roundOrder: _roundOrder, partyId: _candidateId, examinerId: _examinerId},
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
								$("#evaluatedtestRoundComment").val(response.comment);
							}
							if(response.resultTypeId == "ACHIEVED"){
								$("#testRoundCANDPassed").jqxRadioButton({checked: true});
							}else if(response.resultTypeId == "NOT_ACHIEVED"){
								$("#testRoundCANDNotPassed").jqxRadioButton({checked: true});
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
			$("#loadingEvaluatedTestRound").hide();
		});
	};
	var initJqxRadioButton = function(){
		$("#testRoundCANDPassed").jqxRadioButton({ width: 150, height: 25, checked: true});
        $("#testRoundCANDNotPassed").jqxRadioButton({ width: 150, height: 25});
        if(!globalVar.editEvaluatedCandidate){
        	$("#testRoundCANDPassed").jqxRadioButton({disabled: true});
        	$("#testRoundCANDNotPassed").jqxRadioButton({disabled: true});
		}
	};
	var initEvent = function(){
		$("#cancelEvalutedTestCAND").click(function(event){
			$("#evaluatedCANDTestRoundWindow").jqxWindow('close');
		});
		if(globalVar.editEvaluatedCandidate){
			$("#saveEvalutedTestCAND").click(function(event){
				bootbox.dialog(uiLabelMap.RecruitmentEvaluatedCandidateConfirm,
						[{
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								updateRecruitRoundCandidateSubject();
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
	var updateRecruitRoundCandidateSubject = function(){
		var dataSubmit = getSubmitData()
		$("#loadingEvaluatedTestRound").show();
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
					$("#evaluatedCANDTestRoundWindow").jqxWindow('close');
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
				$("#loadingEvaluatedTestRound").hide();
				enableAll();
			}
		});
	};
	var disableAll =function(){
		$("#saveEvalutedTestCAND").attr("disabled", 'disabled');
		$("#cancelEvalutedTestCAND").attr("disabled", 'disabled');
		$("#recruitTestRoundSubjGrid").jqxGrid({disabled: true});
		$("#recruitTestRoundSubjGrid").jqxGrid({editable: false});
		$("#evaluatedtestRoundComment").jqxTextArea({disabled: true});
		$("#testRoundCANDPassed").jqxRadioButton({disabled: true});
		$("#testRoundCANDNotPassed").jqxRadioButton({disabled: true});
	};
	var enableAll =function(){
		$("#saveEvalutedTestCAND").removeAttr("disabled");
		$("#cancelEvalutedTestCAND").removeAttr("disabled");
		$("#recruitTestRoundSubjGrid").jqxGrid({disabled: false});
		$("#recruitTestRoundSubjGrid").jqxGrid({editable: true});
		$("#evaluatedtestRoundComment").jqxTextArea({disabled: false});
		$("#testRoundCANDPassed").jqxRadioButton({disabled: false});
		$("#testRoundCANDNotPassed").jqxRadioButton({disabled: false});
	};
	var setData = function(data){
		_recruitmentPlanId = data.recruitmentPlanId;
		_roundOrder = data.roundOrder;
		_candidateId = data.partyId;
		_examinerId = data.examinerId;
		prepareData();
	};
	var refreshGrid = function(data){
		var source = $("#recruitTestRoundSubjGrid").jqxGrid('source');
		source._source.localdata = data;
		$("#recruitTestRoundSubjGrid").jqxGrid('source', source);
	};
	var openWindow = function(){
		openJqxWindow($("#evaluatedCANDTestRoundWindow"));
	};
	return{
		init: init,
		setData: setData,
		openWindow: openWindow
	}
}());
$(document).ready(function(){
	evalutedCandidateInTestRoundObj.init();
});