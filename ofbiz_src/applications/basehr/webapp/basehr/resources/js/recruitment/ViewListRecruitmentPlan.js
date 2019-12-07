var viewListRecruitPlanObj = (function(){
	var init = function(){
		initInput();
		initJqxTreeButton();
		initJqxNotification();
		initContextMenu();
		initPopover();
		initEvent();
		var date = new Date();
		refreshGrid(date.getFullYear(), globalVar.rootPartyArr[0].partyId);
		var selectedItem = $('#jqxTree').jqxTree('getSelectedItem');
		setDropdownContent(selectedItem.element, $("#jqxTree"), $("#dropDownButton"));
	};
	
	var initInput = function(){
		var date = new Date();
		$("#year").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0, decimal: date.getFullYear()});
	};
	
	var initJqxNotification = function(){
		$("#jqxNotificationNtf").jqxNotification({width: '100%', autoClose: true, template : 'info', appendContainer : "#containerNtf", opacity : 0.9});
	};
	
	var initJqxTreeButton = function(){
		var config = {dropDownBtnWidth: 270, treeWidth: 270};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#dropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
	};
	var initPopover = function(){
		if(globalVar.hasPermissionAdmin){
			$("#popoverAddNew").jqxPopover({offset: {left: -50, top:0}, arrowOffsetValue: 50, title: uiLabelMap.HRSelectCommon + "...", showCloseButton: true, selector: $("#addNewSelection") });
		}
	};
	var initEvent = function(){
		$("#year").on('valueChanged', function(event) {
			var year = event.args.value;
			var selectedItem = $('#jqxTree').jqxTree('getSelectedItem');
			var partyId = selectedItem.value;
			refreshGrid(year, partyId);
		});
		
		$('#jqxTree').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTree').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTree"), $("#dropDownButton"));
			var partyId = item.value;
			var year = $("#year").val();
			refreshGrid(year, partyId);
		});
		$("#clearFilter").click(function(event){
			$("#jqxgrid").jqxGrid('clearfilters');
		});
		$("#addNewHRRecPlan").click(function(event){
			wizardObj.openWindow();
			$("#popoverAddNew").jqxPopover('close');
		});
		$("#addHRRecPlanBaseRecruitReq").click(function(event){
			recruitPlanBaseRequireObj.openWindow();
			$("#popoverAddNew").jqxPopover('close');
		});
	};
	
	var initContextMenu = function(){
		createJqxMenu("contextMenu", 30, 180);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == "viewRecruitmentPlan")
            {
				openJqxWindow($("#RecruitmentPlanDetail"));// RecruitmentPlanDetailObj is defined in RecruitmentPlanDetailObj.js
			}else if(action == "viewListCandidates"){
            	recruitmentCandidateListObj.refreshGridData(dataRecord.recruitmentPlanId);
            	recruitmentCandidateListObj.openWindow();// recruitmentCandidateListObj is defined in RecruitmentCreateCandidate.js
            }else if(action == "viewListRecruitRound"){
            	recruitmenRoundListObj.openWindow();//recruitmenRoundListObj is defined in RecruitListRound.js
            	recruitmenRoundListObj.setRecruitmentPlanId(dataRecord.recruitmentPlanId);
            }else if(action == "viewRecruitmentListCost"){
            	recruitmentCostItemListObj.setData(dataRecord);////recruitmentCostItemListObj is defined in RecruitmentViewCostItemList.js
            	recruitmentCostItemListObj.openWindow();//recruitmentCostItemListObj is defined in RecruitmentViewCostItemList.js
            }
		});
	};
	
	var refreshGrid = function(year, partyId){
		refreshBeforeReloadGrid($("#jqxgrid"));
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=JQGetRecruitmentPlan&hasrequest=Y&year=" + year + "&partyId=" + partyId;
		$("#jqxgrid").jqxGrid('source', tmpS);
	};
	
	return{
		init: init
	}
}());

$(document).ready(function(){
	viewListRecruitPlanObj.init();
});
