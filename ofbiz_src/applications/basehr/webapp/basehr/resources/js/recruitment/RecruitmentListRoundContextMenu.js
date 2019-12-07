var recruitListRoundContextMenuObj = (function(){
	var init = function(){
		initJqxMenu();
		initMenuEvent();
	};
	var initJqxMenu = function(){
		var liElement = $("#contextMenuRoundRec>ul>li").length;
		var contextMenuHeight = 30 * liElement;
		$("#contextMenuRoundRec").jqxMenu({ width: 180, height: contextMenuHeight, 
			autoOpenPopup: false, mode: 'popup', popupZIndex: 22000});
		
		liElement = $("#contextMenuRoundRecParty>ul>li").length;
		contextMenuHeight = 30 * liElement;
		$("#contextMenuRoundRecParty").jqxMenu({ width: 230, height: contextMenuHeight, 
			autoOpenPopup: false, mode: 'popup', popupZIndex: 22000});

		liElement = $("#contextMenuExamierEval>ul>li").length;
		contextMenuHeight = 30 * liElement;
		$("#contextMenuExamierEval").jqxMenu({ width: 200, height: contextMenuHeight, 
			autoOpenPopup: false, mode: 'popup', popupZIndex: 22000});
	};
	
	var initMenuEvent = function(){
		$("#contextMenuExamierEval").on('itemclick', function(event){
			var args = event.args;
			var rowindex = $("#recruitCandidateExaminerGrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#recruitCandidateExaminerGrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == "viewDetail"){
            	var selectedItem = $("#listBoxRoundRec").jqxListBox('getSelectedItem');
            	if(selectedItem){
            		var originalItem = selectedItem.originalItem;
            		var enumRoundTypeId = originalItem.enumRoundTypeId;
            		if(enumRoundTypeId == "RECRUIT_ROUND_TEST"){
            			evalutedCandidateInTestRoundObj.openWindow();//evalutedCandidateInTestRoundObj is defined in RecruitEvalutedCandidateTestRound.js
            			evalutedCandidateInTestRoundObj.setData(dataRecord);
            		}else{
            			evalutedCandidateInINTWRoundObj.openWindow();//evalutedCandidateInINTWRoundObj is defined in RecruitEvalutedCandidateINTVWRound.js
            			evalutedCandidateInINTWRoundObj.setData(dataRecord);
            		}
            	}
            }
		});
		$("#contextMenuExamierEval").on('shown', function(event){
			var rowindex = $("#recruitCandidateExaminerGrid").jqxGrid('getselectedrowindex');
			var dataRecord = $("#recruitCandidateExaminerGrid").jqxGrid('getrowdata', rowindex);
			var statusId = dataRecord.statusId;
			if(statusId == "REC_INTW_ASSESS"){
				$(this).jqxMenu('disable', "viewDetailEvalCandidate", false);
			}else{
				$(this).jqxMenu('disable', "viewDetailEvalCandidate", true);
			}
		});
		$("#contextMenuRoundRecParty").on('itemclick', function(event){
			var args = event.args;
			var rowindex = $("#recruitRoundCandidateGrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#recruitRoundCandidateGrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == "storeInterviewRec"){
            	dataRecord.roundOrder = $("#listBoxRoundRec").jqxListBox('getSelectedItem').value;
            	storeInterviewCandidateObj.openWindow();// storeInterviewCandidateObj is defined in RecruitmentStoreInterviewOfCandidate.js
            	storeInterviewCandidateObj.setData(dataRecord);
            }else if(action == "receiveCandidate"){
            	var recruitmentSelectedIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            	var recruitmentSelectedData = $("#jqxgrid").jqxGrid('getrowdata', recruitmentSelectedIndex);
            	var recruitmentReceiveCandidateData = {};
            	recruitmentReceiveCandidateData.candidateId = dataRecord.recruitCandidateId;
            	recruitmentReceiveCandidateData.fullName = dataRecord.fullName;
            	recruitmentReceiveCandidateData.partyId = dataRecord.partyId;
            	recruitmentReceiveCandidateData.partyGroupId = recruitmentSelectedData.partyId;
            	recruitmentReceiveCandidateData.emplPositionTypeId = recruitmentSelectedData.emplPositionTypeId;
            	recruitmentReceiveCandidateData.salaryAmount = recruitmentSelectedData.salaryAmount;
            	recruitmentReceiveCandidateData.month = recruitmentSelectedData.month;
            	recruitmentReceiveCandidateData.year = recruitmentSelectedData.year;
            	recruitmentReceiveCandidateData.recruitmentPlanId = recruitmenRoundListObj.getRecruitmentPlanId();//recruitmenRoundListObj is defined in RecruitListRound.js
            	recruitmentReceiveCandidateObj.setData(recruitmentReceiveCandidateData);
				recruitmentReceiveCandidateObj.openWindow();//recruitmentReceiveCandidate is defined in recruitmentReceiveCandidate.js
			}else if(action == "agreePassRecruitment"){
				var data = {partyId : dataRecord.partyId, recruitmentPlanId : recruitmenRoundListObj.getRecruitmentPlanId()};
				$("#loadingUpdateCandidateResult").show();
				$.ajax({
					type : 'POST',
					data : data,
					url : 'updateRecruitCandidateRoundToZero',
					success : function(response){
						if(response._EVENT_MESSAGE_){
							$("#recruitStoreInterviewWindow").jqxWindow('close');
							$("#ntfRecruitRoundCandidateGrid").jqxNotification('closeLast');
							$("#ntfTextRecruitRoundCandidateGrid").text(response._EVENT_MESSAGE_);
							$("#ntfRecruitRoundCandidateGrid").jqxNotification({ template: 'info' });
							$("#ntfRecruitRoundCandidateGrid").jqxNotification('open');
							$("#recruitRoundCandidateGrid").jqxGrid('updatebounddata');
						}else{
							bootbox.dialog(response._ERROR_MESSAGE_,
								[{
									"label" : uiLabelMap.CommonClose,
					    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
								}]		
							);
						}
					},
					complete: function(jqXHR, textStatus){
						$("#loadingUpdateCandidateResult").hide();
					}
				})
//				refreshBeforeReloadGrid($("#recruitRoundCandidateGrid"));
//				var tempS = $("#recruitRoundCandidateGrid").jqxGrid('source');
//				tempS._source.url = "jqxGeneralServicer?sname=JQGetListCandidateInRecruitRound&recruitmentPlanId=" + recruitmenRoundListObj.getRecruitmentPlanId() + "&roundOrder=" + "0";
//				console.log(tempS);
			}
		});
		
		$("#contextMenuRoundRec").on('itemclick', function(event){
			var args = event.args;
			var action = $(args).attr("id");
			if(action == "candidateInterviewScheduling"){
				var roundOrder = $("#listBoxRoundRec").jqxListBox('getSelectedItem').value
				recruitCalListObj.openWindow(); // recruitCalListObj is defined in RecruitmentCalendarSchedule.js
				//recruitmenRoundListObj is defined in RecruitListRound.js
				recruitCalListObj.setData({recruitmentPlanId: recruitmenRoundListObj.getRecruitmentPlanId(), roundOrder: roundOrder});
			}else if(action == "examResultBatchUpdate"){
				
			}
		});
		
		$("#contextMenuRoundRecParty").on('shown', function(){
			var rowindex = $("#recruitRoundCandidateGrid").jqxGrid('getselectedrowindex');
			var dataRecord = $("#recruitRoundCandidateGrid").jqxGrid('getrowdata', rowindex);
			var statusId = dataRecord.statusId;
			if(statusId == "RR_RECRUITING"){
				$("#contextMenuRoundRecParty").jqxMenu('disable', "storeInterviewRec", false);
				$("#contextMenuRoundRecParty").jqxMenu('disable', "agreePassRecruitment", false);
				$("#contextMenuRoundRecParty").jqxMenu('disable', "receiveCandidate", true);
			}else if(statusId == "RR_REC_PASSED"){
				$("#contextMenuRoundRecParty").jqxMenu('disable', "storeInterviewRec", true);
				$("#contextMenuRoundRecParty").jqxMenu('disable', "agreePassRecruitment", false);
				$("#contextMenuRoundRecParty").jqxMenu('disable', "receiveCandidate", true);
			}else if(statusId == "RR_REC_NOTPASSED"){
				$("#contextMenuRoundRecParty").jqxMenu('disable', "storeInterviewRec", true);
				$("#contextMenuRoundRecParty").jqxMenu('disable', "agreePassRecruitment", false);
				$("#contextMenuRoundRecParty").jqxMenu('disable', "receiveCandidate", true);
			}else if(statusId == "RR_REC_RECEIVE"){
				$("#contextMenuRoundRecParty").jqxMenu('disable', "storeInterviewRec", true);
				$("#contextMenuRoundRecParty").jqxMenu('disable', "agreePassRecruitment", true);
				$("#contextMenuRoundRecParty").jqxMenu('disable', "receiveCandidate", false);
			}else if(statusId == "RR_REC_EMPL"){
				$("#contextMenuRoundRecParty").jqxMenu('disable', "agreePassRecruitment", true);
				$("#contextMenuRoundRecParty").jqxMenu('disable', "receiveCandidate", true);
				$("#contextMenuRoundRecParty").jqxMenu('disable', "storeInterviewRec", true);
			}
		});
		
		$("#contextMenuRoundRec").on('shown', function(){
			var item = $("#listBoxRoundRec").jqxListBox('getSelectedItem');
			if(item && item.value == 0){
				$("#contextMenuRoundRec").jqxMenu('disable', "candidateInterviewScheduling", true);
				$("#contextMenuRoundRec").jqxMenu('disable', "examResultUpdate", true);
			}else{
				$("#contextMenuRoundRec").jqxMenu('disable', "candidateInterviewScheduling", false);
				$("#contextMenuRoundRec").jqxMenu('disable', "examResultUpdate", false);
			}
		});
	};
	
	var attachContextMenu = function () {
		// open the context menu when the user presses the mouse right button.
		$("#listBoxRoundRec").find("div[role='option']").mousedown(function (event) {
            var target = $(event.target).parents("div[role='option']");
            var rightClick = isRightClick(event);
            if(rightClick && target != null) {
            	/*var idItem = target.attr("id");
            	var listItemStr = "listitem";
            	var index = idItem.substring(listItemStr.length, listItemStr.length + 1);
            	$("#listBoxRoundRec").jqxListBox('selectIndex', index);*/
                var scrollTop = $(window).scrollTop();
                var scrollLeft = $(window).scrollLeft();
                $("#contextMenuRoundRec").jqxMenu('open', parseInt(event.clientX) + 5 + scrollLeft, parseInt(event.clientY) + 5 + scrollTop);
                return true;
             }else{
            	 $("#contextMenuRoundRec").jqxMenu('close');
             }
		});
    };
    
    var detachContextMenu = function(){
    	$("#listBoxRoundRec").find("div[role='option']").unbind('mousedown');
    };
    
    var isRightClick = function(event) {
    	var rightclick;
        if (!event) var event = window.event;
        if (event.which){ 
        	rightclick = (event.which == 3);
        }else if (event.button){
        	rightclick = (event.button == 2);
        } 
        return rightclick;
    };
	return {
		init: init,
		attachContextMenu: attachContextMenu,
		detachContextMenu: detachContextMenu,
		
	}
}());

$(document).ready(function(){
	recruitListRoundContextMenuObj.init();
});