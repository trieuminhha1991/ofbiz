var storeInterviewCandidateObj = (function(){
	var _partyId = null;
	var _recruitmentPlanId = null;
	var _roundOrder = null;
	var init = function(){
		initJqxInput();
		initJqxDateTimeInput();
		initJqxCheckBox();
		initJqxGrid();
		initJqxPannel();
		initJqxWindow();
		initEvent();
		create_spinner($("#spinnerUpdateCandidateResult"));
	};
	
	var initJqxInput = function(){
		$("#candidateIdStoreInterview").jqxInput({width: '89%', height: 20, disabled: true});
		$("#fullNameStoreInterview").jqxInput({width: '89%', height: 20, disabled: true});
		$("#genderStoreInterview").jqxInput({width: '89%', height: 20, disabled: true});
		$("#birthDateStoreInterview").jqxInput({width: '89%', height: 20, disabled: true});
		$("#trainingStoreInterview").jqxInput({width: '89%', height: 20, disabled: true});
		$("#classificationStoreInterview").jqxInput({width: '89%', height: 20, disabled: true});
		$("#majorIdStoreInterview").jqxInput({width: '89%', height: 20, disabled: true});
		$("#recRoundStoreInterview").jqxInput({width: '89%', height: 20, disabled: true, valueMember: 'roundOrder', displayMember: 'roundName'});
	};
	
	var initJqxPannel = function(){
		$("#recruitStoreInterviewPartyInfoPanel").jqxPanel({ width: '99%', height: '37%', scrollBarSize: 15, autoUpdate: true});
		$("#recruitStoreInterviewResultsPanel").jqxPanel({ width: '99%', height: '57%', scrollBarSize: 15, autoUpdate: true});
	};
	
	
	var initJqxDropDown = function(){
		
	};
	
	var initJqxEditor = function(){
		$("#commentStoreInterview").jqxEditor({ 
    		width: '100%',
            theme: 'olbiuseditor',
            tools: '',
            height: 120,
        });
	};
	
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxEditor();
		};
		createJqxWindow($("#recruitStoreInterviewWindow"), 815, 580, initContent);
		$("#recruitStoreInterviewWindow").on('close', function(event){
			_recruitmentPlanId = null;
			_partyId = null;
			_roundOrder = null;
			Grid.clearForm($(this));
		});
		$("#recruitStoreInterviewWindow").on('open', function(event){
			
		});
	};
	
	var initJqxDateTimeInput = function(){
		var maxDate = new Date();
		//maxDate.setDate(maxDate.getDate() + 1);
		$("#dateInterviewStore").jqxDateTimeInput({width: '91%', height: 25, formatString: "dd/MM/yyyy HH:mm", max: maxDate});
		$("#dateInterviewStore").val(null);
	};
	
	var initJqxNumberInput = function(){
		
	};
	
	var initJqxCheckBox = function(){
		$("#moveNextRound").jqxCheckBox({ width: 120, height: 25});
	};
	
	var initJqxGrid = function(){
		var datafield = [{name: 'recruitmentPlanId', type: 'string'},
		                 {name: 'partyId', type: 'string'},
		                 {name: 'roundOrder', type: 'number'},
		                 {name: 'examinerId', type: 'string'},
		                 {name: 'examinerName', type: 'string'},
		                 {name: 'statusId', type: "string"},
		                 {name: 'resultTypeId', type: 'string'},
		                 {name: 'comment', type: 'string'},
		                 ];
		var columns = [{datafield: 'recruitmentPlanId', hidden: true},
		               {datafield: 'partyId', hidden: true},
		               {datafield: 'roundOrder', hidden: true},
		               {datafield: 'examinerId', hidden: true},
		               {text: uiLabelMap.RecruitmentInterviewerMarker, datafield: 'examinerName', width: '22%', editable: false},
		               {text: uiLabelMap.CommonStatus, datafield: 'statusId', width: '22%', editable: false,
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		            		   for(var i = 0; i < globalVar.statusEvaluatedCandidateArr.length; i++){
		            			   if(globalVar.statusEvaluatedCandidateArr[i].statusId == value){
		            				   return '<span>' + globalVar.statusEvaluatedCandidateArr[i].description + '</span>';
		            			   }
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   }
		               },
		               {text: uiLabelMap.HRCommonResults, datafield: 'resultTypeId', width: '22%', editable: false,
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		            		   for(var i = 0; i < globalVar.recruitmentResultTypeArr.length; i++){
		            			   if(globalVar.recruitmentResultTypeArr[i].resultTypeId == value){
		            				   return '<span>' + globalVar.recruitmentResultTypeArr[i].description + '</span>';
		            			   }
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   }
		               },
		               {text: uiLabelMap.HRCommonComment, datafield: 'comment', width: '34%'}
		               ];
		
		var grid = $("#recruitCandidateExaminerGrid");
		var config = {
				url: '',
				showtoolbar : false,
				width : '100%',
				virtualmode: true,
				editable: false,
				filterable: false,
				localization: getLocalization(),
				source: {
					pagesize : 5, 
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
		Grid.createContextMenu(grid, $("#contextMenuExamierEval"), false);
	};
	
	var openWindow = function(){
		openJqxWindow($("#recruitStoreInterviewWindow"));
	};
	
	var setData = function(data){
		_partyId = data.partyId;
		_recruitmentPlanId = data.recruitmentPlanId;
		_roundOrder = data.roundOrder;
		$("#candidateIdStoreInterview").val(data.recruitCandidateId);
		$("#fullNameStoreInterview").val(data.fullName);
		if(data.gender){
			var genderStr = "";
			for(var i = 0; i < globalVar.genderArr.length; i++){
				if(globalVar.genderArr[i].genderId == data.gender){
					genderStr = globalVar.genderArr[i].description;
					break;
				}
			}
			$("#genderStoreInterview").val(genderStr);
		}
		if(data.birthDate){
			var birthDate = new Date(data.birthDate);
			var birthDateStr = getDate(birthDate) + "/" + getMonth(birthDate) + "/" + birthDate.getFullYear();
			$("#birthDateStoreInterview").val(birthDateStr);
		}
		if(data.educationSystemTypeId){
			var educationSystemTypeIdStr = "";
			for(var i = 0; i < globalVar.educationSystemTypeArr.length; i++){
				if(globalVar.educationSystemTypeArr[i].educationSystemTypeId == data.educationSystemTypeId){
					educationSystemTypeIdStr = globalVar.educationSystemTypeArr[i].description;
					break;
				}
			}
			$("#trainingStoreInterview").val(educationSystemTypeIdStr);
		}
		if(data.classificationTypeId){
			var classificationTypeIdStr = "";
			for(var i = 0; i < globalVar.degreeClassTypeArr.length; i++){
				if(globalVar.degreeClassTypeArr[i].classificationTypeId == data.classificationTypeId){
					classificationTypeIdStr = globalVar.degreeClassTypeArr[i].description;
					break;
				}
			}
			$("#classificationStoreInterview").val(classificationTypeIdStr);
		}
		if(data.majorDesc){
			$("#majorIdStoreInterview").val(data.majorDesc);
		}
		$("#recRoundStoreInterview").jqxInput('val', {label: data.roundName, value: data.roundOrder});
		if(data.dateInterview){
			$("#dateInterviewStore").val(data.dateInterview);
		}
		updateGridData(_recruitmentPlanId, _partyId, _roundOrder);
	};
	
	var updateGridData = function(recruitmentPlanId, partyId, roundOrder){
		refreshBeforeReloadGrid($("#recruitCandidateExaminerGrid"));
		var tempS = $("#recruitCandidateExaminerGrid").jqxGrid('source');
		tempS._source.url = "jqxGeneralServicer?sname=JQGetListRecruitRoundCandidateExaminer&recruitmentPlanId=" + recruitmentPlanId 
							+ "&partyId=" + partyId + "&roundOrder=" + roundOrder;
		$("#recruitCandidateExaminerGrid").jqxGrid('source', tempS);
	};
	
	var getData = function(){
		var data = {partyId: _partyId, recruitmentPlanId: _recruitmentPlanId, roundOrder: _roundOrder};
		
		var dateInterview = $("#dateInterviewStore").jqxDateTimeInput('val', 'date');
		if(dateInterview){
			data.dateInterview = dateInterview.getTime();
		}
		var moveNextRound = $("#moveNextRound").jqxCheckBox('checked');
		if(moveNextRound){
			data.moveNextRound = "Y";
		}else{
			data.moveNextRound = "N";
		}
		data.comment = $("#commentStoreInterview").val();
		
		return data;
	};
	
	var initEvent = function(){
		$("#cancelStoreInterview").click(function(event){
			$("#recruitStoreInterviewWindow").jqxWindow('close');
		});
		$("#saveStoreInterview").click(function(event){
			bootbox.dialog(uiLabelMap.RecruitmentRoundResultCandidateIsNotChange,
				[{
					"label" : uiLabelMap.CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok open-sans",
	    		    "callback": function() {
	    		    	var dataSubmit = getData();
	    				$("#loadingUpdateCandidateResult").show();
	    				disableBtn();
	    				$.ajax({
	    					url: 'updateRecruitRoundCandidateResult',
	    					data: dataSubmit,
	    					type: 'POST', 
	    					success: function(response){
	    						if(response.responseMessage == 'success'){
	    							$("#recruitStoreInterviewWindow").jqxWindow('close');
	    							$("#ntfRecruitRoundCandidateGrid").jqxNotification('closeLast');
	    							$("#ntfTextRecruitRoundCandidateGrid").text(response.successMessage);
	    							$("#ntfRecruitRoundCandidateGrid").jqxNotification({ template: 'info' });
	    							$("#ntfRecruitRoundCandidateGrid").jqxNotification('open');
	    							$("#recruitRoundCandidateGrid").jqxGrid('updatebounddata');
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
	    						$("#loadingUpdateCandidateResult").hide();
	    						enableBtn();
	    					}
	    				});	
	    		    }	
				},
				{
	    		    "label" : uiLabelMap.CommonClose,
	    		    "class" : "btn-danger btn-small icon-remove open-sans",
	    		}]		
			);
		});
	};
	var disableBtn = function(){
		$("#cancelStoreInterview").attr("disabled", 'disabled');
		$("#saveStoreInterview").attr("disabled", 'disabled');
	};
	var enableBtn = function(){
		$("#saveStoreInterview").removeAttr("disabled");
		$("#cancelStoreInterview").removeAttr("disabled");
	};
	return{
		init: init,
		openWindow: openWindow,
		setData: setData
	}
}());
$(document).ready(function(){
	storeInterviewCandidateObj.init();
});