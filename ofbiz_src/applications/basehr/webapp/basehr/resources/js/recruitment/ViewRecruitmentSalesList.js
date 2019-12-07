var recruitmentSalesObj = (function(){
	var init = function(){
		initJqxDropDownList();
		initJqxTreeButton();
		initJqxNotification();
		initContextMenu();
		initEvent();
	};
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.customTimePeriodArr, $("#yearCustomTimePeriod"), "customTimePeriodId", "periodName", 25, 100);
		if(globalVar.hasOwnProperty("selectYearCustomTimePeriodId")){
			$("#yearCustomTimePeriod").val(globalVar.selectYearCustomTimePeriodId);
		}
	};
	
	var initJqxNotification = function(){
		$("#jqxNotificationNtf").jqxNotification({width: '100%', autoClose: true, template : 'info', appendContainer : "#containerNtf", opacity : 0.9});
	};
	
	var initJqxTreeButton = function(){
		var config = {dropDownBtnWidth: 270, treeWidth: 270};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#dropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
		$('#jqxTree').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTree').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTree"), $("#dropDownButton"));
			var partyId = item.value;
			var customTimePeriodId = $("#yearCustomTimePeriod").val();
			if(typeof(customTimePeriodId) != 'undefined' && customTimePeriodId.length > 0){
				refreshGrid(customTimePeriodId, partyId);
			}
		});
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
	};
	var initContextMenu = function(){
		createJqxMenu("contextMenu", 30, 180);
	};
	var initEvent = function(){
		$("#addNewRecEmpl").click(function(event){
			recruitmentSalesAddEmplObj.openWindow();//recruitmentSalesAddEmplObj is defined in RecruitmentSalesAddNewEmpl.js
		});
		$("#contextMenu").on('itemclick', function(event){
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == "addNewEmployee"){
            	recruitmentSalesAddEmplRecInfo.setData(dataRecord);//recruitmentSalesAddEmplRecInfo is defined in RecruitmentSalesAddNewEmpl.js
            	recruitmentSalesAddEmplObj.openWindow();
            }else if("viewListEmpl" == action){
            	recruitedSaleEmplListObj.setData(dataRecord);//recruitedSaleEmplListObj is defined in RecruitmentSalesListEmpl.js
            	recruitedSaleEmplListObj.openWindow();
            }else if(action == "recruitmentOffer"){
            	recruitmentOfferObj.setData(dataRecord);//recruitmentOfferObj is defined in RecruitmentSalesOffer.js
            	recruitmentOfferObj.openWindow();
            }
		});
	};
	var refreshGrid = function(customTimePeriodId, partyId){
		refreshBeforeReloadGrid($("#jqxgrid"));
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=JQGetRecruitmentPlanSales&customTimePeriodId=" + customTimePeriodId + "&partyId=" + partyId;
		$("#jqxgrid").jqxGrid('source', tmpS);
	};
	return{
		init: init
	}
}());
$(document).ready(function(){
	recruitmentSalesObj.init();
});