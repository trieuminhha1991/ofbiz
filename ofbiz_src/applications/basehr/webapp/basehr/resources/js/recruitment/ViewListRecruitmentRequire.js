var viewRecruitReqObj = (function(){
	var init = function(){
		initInput();
		initJqxTreeButton();
		initEvent();
		initJqxNotification();
		initJqxContextMenu();
		var date = new Date();
		refreshGrid(date.getFullYear(), globalVar.rootPartyArr[0].partyId);
		var selectedItem = $('#jqxTree').jqxTree('getSelectedItem');
		setDropdownContent(selectedItem.element, $("#jqxTree"), $("#dropDownButton"));
	};
	
	var initJqxContextMenu = function(){
		createJqxMenu("contextMenu", 30, 150);
		$("#contextMenu").on('itemclick', function(event){
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == 'approver'){
            	recruitReqApprObj.setData(dataRecord);
            	recruitReqApprObj.openWindow();
            }else if(action == "edit"){
            	//initWizard.openEditRecruitmentRequire(dataRecord);
            	editRecruitReqObj.setData(dataRecord);
            	editRecruitReqObj.openWindow();
            }
		});
		$("#contextMenu").on('shown', function(event){
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
			if(dataRecord.createdByPartyId == globalVar.userLoginPartyId){
				$("#contextMenu").jqxMenu('disable', "editRecruitmentRequireMenu", false);
			}else{
				$("#contextMenu").jqxMenu('disable', "editRecruitmentRequireMenu", true);
			}
		});
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
		$("#addNewHRRecReq").click(function(event){
			initWizard.openWindow();
		});
		$("#clearFilter").click(function(event){
			$("#jqxgrid").jqxGrid('clearfilters');
		});
	};
	
	var refreshGrid = function(year, partyId){
		refreshBeforeReloadGrid($("#jqxgrid"));
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=JQGetRecruitmentRequire&hasrequest=Y&year=" + year + "&partyId=" + partyId;
		$("#jqxgrid").jqxGrid('source', tmpS);
	};
	
	return{
		init: init
	}
}());

$(document).ready(function(){
	viewRecruitReqObj.init();
	recuritmentReqCondObj.init($("#recruitReqCondGrid"));//recuritmentReqCondObj is defined in RecruitmentCondition.js
});