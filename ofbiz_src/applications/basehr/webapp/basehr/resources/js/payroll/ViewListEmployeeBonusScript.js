var viewListEmplBonusObject = (function(){
	var init = function(){
		initJqxDateTime();
		initJqxTreeDropDownBtn();	
	};
	var initJqxDateTime = function (){
		$("#dateTimeInput").jqxDateTimeInput({ width: 250, height: 25,  selectionMode: 'range', theme: 'olbius'});
		var fromDate = new Date(globalVar.prevMonthStart);
		var thruDate = new Date(globalVar.prevMonthEnd);
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

	var initJqxTreeDropDownBtn = function (){
		var dropdownButton = $("#jqxDropDownButton");
		var jqxTreeDiv = $("#jqxTree");
		var config = {dropDownBtnWidth: '300px', treeWidth: '300px'};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#jqxDropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
		
		jqxTreeDiv.on('select', function(event){
			var id = event.args.element.id;
	    	var item = $("#jqxTree").jqxTree('getItem', args.element);
	    	setDropdownContent(item, jqxTreeDiv, dropdownButton);
	    	var partyId = item.value;
	    	var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
	    	var fromDate = selection.from.getTime();
	    	var thruDate = selection.to.getTime();
	    	refreshGridData(partyId, fromDate, thruDate);
		});
		if(globalVar.rootPartyArr.length > 0){
			jqxTreeDiv.jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
	};

	var refreshGridData = function(partyGroupId, fromDate, thruDate){
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?hasrequest=Y&sname=getEmplListBonusInPeriod&partyGroupId=" + partyGroupId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
		$("#jqxgrid").jqxGrid('source', tmpS);
	};
	return{
		init: init
	}
}());

$(document).ready(function(){
	viewListEmplBonusObject.init();
});
