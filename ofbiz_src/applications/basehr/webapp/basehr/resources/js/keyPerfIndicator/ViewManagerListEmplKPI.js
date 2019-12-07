var periodTypeId = "DAILY";
var kpiAssignEmplObj = (function(){
	var init = function(){
		initJqxDateTimeInput();
		initJqxTreeBtn();
		initJqxTreeEvent();
		initEvent();
		initLyEvent();
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
	};
	
	var initLyEvent = function(){
		$("#daily_li").click(function(){
			var data = getData();
			periodTypeId = "DAILY";
			partyId = data.partyId;
			fromDate = data.fromDate;
			thruDate = data.thruDate;
			
			refreshGridData(partyId, fromDate, thruDate, periodTypeId);
		});
		$("#weekly_li").click(function(){
			var data = getData();
			periodTypeId = "WEEKLY";
			partyId = data.partyId;
			fromDate = data.fromDate;
			thruDate = data.thruDate;
			
			refreshGridData(partyId, fromDate, thruDate, periodTypeId);
		});
		$("#monthly_li").click(function(){
			var data = getData();
			periodTypeId = "MONTHLY";
			partyId = data.partyId;
			fromDate = data.fromDate;
			thruDate = data.thruDate;
			
			refreshGridData(partyId, fromDate, thruDate, periodTypeId);
		});
		$("#quarterly_li").click(function(){
			var data = getData();
			periodTypeId = "QUARTERLY";
			partyId = data.partyId;
			fromDate = data.fromDate;
			thruDate = data.thruDate;
			
			refreshGridData(partyId, fromDate, thruDate, periodTypeId);
		});
		$("#yearly_li").click(function(){
			var data = getData();
			periodTypeId = "YEARLY";
			partyId = data.partyId;
			fromDate = data.fromDate;
			thruDate = data.thruDate;
			
			refreshGridData(partyId, fromDate, thruDate, periodTypeId);
		});
		
	};
	
	var getData = function(){
		var data = {};
		var item = $("#jqxTree").jqxTree('getSelectedItem');
	    var partyId = item.value;
	    var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
		var fromDate = selection.from.getTime();
	    var thruDate = selection.to.getTime();
	    
		data.partyId = partyId;
		data.fromDate = fromDate;
		data.thruDate = thruDate;
		
		return data;
	};
	
	var initEvent = function(){
		$("#viewListEmplKPI").click(function(event){
			viewEmplListKPIObj.openWindow();
		});
		$("#assignEmplKPIByPos").click(function(event){
			settingKPIPosObj.openWindow();
		});
		$("#jqxgrid").on('rowdoubleclick', function(event){
			var args = event.args;
		    var boundIndex = args.rowindex;
		    var data = $("#jqxgrid").jqxGrid('getrowdata', boundIndex);
		    viewEmplListKPIObj.openWindow();
			viewEmplListKPIObj.setPartyIdForViewKPI(data);
		});
		$("#removeFilter").click(function(){
			$("#jqxgrid").jqxGrid('clearfilters');
		})
	};
	
	var initJqxDateTimeInput = function(){
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
		    refreshGridData(partyId, fromDate, thruDate, periodTypeId);
		});
	};
	
	var initJqxTreeBtn = function(){
		var config = {dropDownBtnWidth: 250, treeWidth: 250};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#dropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
	};
	
	var initJqxTreeEvent = function(){
		$("#jqxTree").on('select', function(event){
			var item = $('#jqxTree').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTree"), $("#dropDownButton"));
			var partyId = item.value;
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			var fromDate = selection.from.getTime();
			var thruDate = selection.to.getTime();
			refreshGridData(partyId, fromDate, thruDate, periodTypeId);
		});
	};
	
	var refreshGridData = function(partyId, fromDate, thruDate, periodTypeIds){
		refreshBeforeReloadGrid($("#jqxgrid"));
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=getPartyKPIOverview&hasrequest=Y&partyGroupId=" + partyId + "&fromDate=" + fromDate + "&thruDate=" + thruDate + "&periodTypeId=" + periodTypeIds;
		$("#jqxgrid").jqxGrid('source', tmpS);
	};
	
	
	var updateGrid = function(){
		$("#jqxgrid").jqxGrid('updatebounddata');
	};
	
	return{
		init: init,
		updateGrid: updateGrid
	}
}());

$(document).ready(function(){
	kpiAssignEmplObj.init();
});