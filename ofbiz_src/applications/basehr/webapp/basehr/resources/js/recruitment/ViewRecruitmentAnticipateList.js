var viewRecruitmentAnticipateObj = (function(){
	var init = function(){
		initInput();
		initJqxTreeButton();
		initJqxNotification();
		initJqxContextMenu();
		initEvent();
		var date = new Date();
		refreshGrid(date.getFullYear(), globalVar.rootPartyArr[0].partyId);
		var selectedItem = $('#jqxTree').jqxTree('getSelectedItem');
		setDropdownContent(selectedItem.element, $("#jqxTree"), $("#dropDownButton"));
	};
	var initJqxNotification = function(){
		$("#jqxNotificationNtf").jqxNotification({width: '100%', autoClose: true, template : 'info', appendContainer : "#containerNtf", opacity : 0.9});
	};
	var initJqxContextMenu = function(){
		createJqxMenu("contextMenu", 30, 150);
		$("#contextMenu").on('itemclick', function(event){
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == 'approver'){
            	if(apprRecruitAnticipateObj){
            		apprRecruitAnticipateObj.openWindow();//apprRecruitAnticipateObj is defined in ApprovalRecruitmentAnticipate.js 
            		apprRecruitAnticipateObj.setData(dataRecord);
            	}
            }else if(action == "edit"){
            	if(editRecruitmentAnticipateObj){
            		editRecruitmentAnticipateObj.openWindow();//editRecruitmentAnticipateObj is defined in EditRecruitmentAnticipate.js 
            		editRecruitmentAnticipateObj.setData(dataRecord);
            	}
            }
		});
		$("#contextMenu").on('shown', function(event){
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
			if(dataRecord.createdByPartyId == globalVar.userLoginPartyId){
				$("#contextMenu").jqxMenu('disable', "editRecruitAnticipate", false);
			}else{
				$("#contextMenu").jqxMenu('disable', "editRecruitAnticipate", true);
			}
		});
	};
	var initInput = function(){
		var date = new Date();
		$("#year").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0, decimal: date.getFullYear()});
	};
	
	var initJqxTreeButton = function(){
		var config = {dropDownBtnWidth: 270, treeWidth: 270};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#dropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
	};
	var initEvent = function(){
		$('#jqxTree').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTree').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTree"), $("#dropDownButton"));
			var partyId = item.value;
			var year = $("#year").val();
			refreshGrid(year, partyId);
		});
		$("#year").on('valueChanged', function(event) {
			var year = event.args.value;
			var selectedItem = $('#jqxTree').jqxTree('getSelectedItem');
			var partyId = selectedItem.value;
			refreshGrid(year, partyId);
		});
	};
	var refreshGrid = function(year, partyId){
		refreshBeforeReloadGrid($("#jqxgrid"));
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=JQGetRecruitmentAnticipateList&hasrequest=Y&year=" + year + "&partyId=" + partyId;
		$("#jqxgrid").jqxGrid('source', tmpS);
	};
	return{
		init: init
	}
}());
$(document).ready(function () {
	viewRecruitmentAnticipateObj.init();
});