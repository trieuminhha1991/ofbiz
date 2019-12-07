var viewListEmplPayrollParamObject = (function(){
	var init = function(){
		initJqxDateTime();
		initJqxNotification();
		initJqxTreeDropDownBtn();
		initBtnEvent();
	};
	
	var initJqxTreeDropDownBtn = function(){
		var config = {dropDownBtnWidth: 250, treeWidth: 250};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#jqxDropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
		 
		$('#jqxTree').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTree').jqxTree('getItem', event.args.element);
		  	setDropdownContent(event.args.element, $("#jqxTree"), $("#jqxDropDownButton"));	     
		  	var partyId = item.value;
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			var fromDate = selection.from.getTime();
			var thruDate = selection.to.getTime();
			refreshGridData(partyId, fromDate, thruDate);
		});
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
	};
	
	var initJqxNotification = function(){
		$("#jqxNtf").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#notifyContainer"});
	};
	
	var initJqxDateTime = function(){
		$("#dateTimeInput").jqxDateTimeInput({ width: 250, height: 25,  selectionMode: 'range', theme: 'olbius'});
		var fromDate = new Date(globalVar.monthStart);
		var thruDate = new Date(globalVar.monthEnd);
		$("#dateTimeInput").jqxDateTimeInput('setRange', fromDate, thruDate);
		$("#dateTimeInput").on('change', function(event){
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			var fromDate = selection.from.getTime();
		    var thruDate = selection.to.getTime();
		    var item = $("#jqxTree").jqxTree('getSelectedItem');
		    var partyId = item.value;
			refreshGridData(partyId, fromDate, thruDate);
		});
	};
	
	var initBtnEvent = function(){
		$('#removeFilter').click(function(){
			$('#jqxgrid').jqxGrid('clearfilters');
		})
	}
	
	var refreshGridData = function(partyGroupId, fromDate, thruDate){
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?hasrequest=Y&sname=getEmplBonusAllowances&partyGroupId=" + partyGroupId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
		$("#jqxgrid").jqxGrid('source', tmpS);
	};	
	
	return{
		init: init
	}
}());

$(document).ready(function () {
	viewListEmplPayrollParamObject.init();
});
