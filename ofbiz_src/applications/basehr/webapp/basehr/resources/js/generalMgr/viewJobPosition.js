
var viewJobPositionObject = (function(){
	var init = function(){
		initJqxWindow();
		initJqxGrid();
		initJqxNotification();
		initJqxTree();
		initJqxTreeEvent();
		initJqxDateTimeInput();
		initBtnEvent();
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
	};
	
	var initBtnEvent = function(){
		$("#removeFilter").click(function(){
			$("#jqxgrid").jqxGrid('clearfilters');
		})
	}
	
	var initJqxTree = function(){
		var config = {dropDownBtnWidth: 300, treeWidth: 300};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#dropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
	};
	
	var initJqxTreeEvent = function(){
		$("#jqxTree").on('select', function(event){			
			var id = event.args.element.id;
		    var item = $(this).jqxTree('getItem', event.args.element);
	    	setDropdownContent(event.args.element, $("#jqxTree"), $("#dropDownButton"));
	    	var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
	    	if(selection){
	    		var fromDate = selection.from.getTime();
	    		var thruDate = selection.to.getTime();
	    		refreshGridData(item.value, fromDate, thruDate);
	    	}
	    	
	    });
	};
	
	var initJqxDateTimeInput = function(){
		$("#dateTimeInput").jqxDateTimeInput({ width: 250, height: 25,  selectionMode: 'range', theme: 'olbius'});
		var fromDate = new Date(globalVar.monthStart);
		var thruDate = new Date(globalVar.monthEnd);
		$("#dateTimeInput").on('change', function(event){
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			var fromDate = selection.from.getTime();
		    var thruDate = selection.to.getTime();
		    var item = $("#jqxTree").jqxTree('getSelectedItem');
		    if(item){
		    	var partyId = item.value;
		    	refreshGridData(partyId, fromDate, thruDate);
		    }
		});
		$("#dateTimeInput").jqxDateTimeInput('setRange', fromDate, thruDate);
	};
	
	var initJqxWindow = function(){
		$("#jqxWindowPositionDetail").jqxWindow({
			showCollapseButton: false, height: 490, width: 720,
			theme: 'olbius', isModal: true, autoOpen: false,             
		});
	};
	
	var initJqxGrid = function(){
		var jqxGridAdapter = getGridSource();
		var columns = getGridColumns();
		$("#jqxGridEmplPosition").jqxGrid({
			source: jqxGridAdapter, 
			width: 700, 
			height: 438,
			autoheight: false,
			virtualmode: true,
			localization: getLocalization(),
			rendergridrows: function () {
	            return jqxGridAdapter.records;
	        },
	        pageSizeOptions: ['15', '30', '50', '100'],
	        pagerMode: 'advanced',
	        columnsResize: true,
	        pageable: true,
	        editable: false,
	        columns: columns,
	        selectionmode: 'singlerow',
	        theme: 'olbius'
		});
	};
	
	var getGridSource = function(){
		var url = 'getPositionByPositionTypeAndParty';
		var jqxGridSource = {
	    		datafields: [
	    			{name: 'emplPositionId', type: 'string'},
	    			{name: 'description', type: 'string'},
	    			{name: 'partyId', type: 'string'},
	    			{name: 'partyCode', type: 'string'},
	    			{name: 'employeePartyId', type: 'string'},
	    			{name: 'employeePartyName', type: 'string'},
	    			{name: 'fromDate', type: 'date', other: 'Timestamp'},
	    			{name: 'thruDate', type: 'date', other: 'Timestamp'},
	    			{name: 'actualFromDate', type: 'date', other: 'Timestamp'},
	    			{name: 'actualThruDate', type: 'date', other: 'Timestamp'}
	    		],
	    		cache: false,
	    		datatype: 'json',
				type: 'POST',
				data: {},
		        url: url,
		        //id: 'emplPositionId',
		        beforeprocessing: function (data) {
		        	jqxGridSource.totalrecords = data.TotalRows;
		        },
		        pagenum: 0,
		        pagesize: 15,
		        root: 'listReturn'
	    }; 
		var jqxGridAdapter = new $.jqx.dataAdapter(jqxGridSource);
		return jqxGridAdapter;
	};
	
	var getGridColumns = function(){
		var jqxGridColums = [
   			{text: uiLabelMap.EmplFulfillmentPosition, datafield: 'employeePartyName', width: 160,
   				cellsrenderer: function(row, column, value){
   					var data = $('#jqxGridEmplPosition').jqxGrid('getrowdata', row);  					
 					if(data.employeePartyId){
 						return '<span>' + value +'</span>';
 					}else{
 						return '<span><a href="javascript:void(0)" onclick="assignPositionForEmpl(jqxGridEmplPosition, ' + row + ')" title="' + uiLabelMap.AssignPosForEmpl +'" ><i class="icon-plus"></i><i>' + uiLabelMap.HRNotYet + '</i></a></span>';
 					}
 				}	
   			},
   			{text: uiLabelMap.EmployeeId, datafield: 'partyCode', width: 130},
   			{text: uiLabelMap.FromDateFulfillment, datafield: 'fromDate', cellsalign: 'left', width: 160, cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
   			{text: uiLabelMap.CommonThruDate, datafield: 'thruDate', cellsalign: 'left', width: 120, cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
   			{text: uiLabelMap.PositionActualFromDate, datafield: 'actualFromDate', cellsalign: 'left', cellsformat: 'dd/MM/yyyy ', columntype: 'template'}
 		];
		return jqxGridColums;
	};
	
	var initJqxNotification = function(){
		$("#jqxNotify").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", 
			appendContainer: "#jqxNotifyContainer"});
	};
	
	return{
		init: init
	}
}());

$(document).ready(function () {
	viewJobPositionObject.init();
});


function jqxTreeSelectFunc(event){
	var args = event.args;
    var item = $('#jqxTree').jqxTree('getItem', args.element);
    var partyId = item.value;
    var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
   	if(selection){
	    var fromDate = selection.from.getTime();
	   	var thruDate = selection.to.getTime();
		refreshGridData(partyId, fromDate, thruDate);
   	}
}
