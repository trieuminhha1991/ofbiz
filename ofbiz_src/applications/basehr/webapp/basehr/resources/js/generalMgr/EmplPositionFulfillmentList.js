var emplPositionFulFillObj = (function(){
	var _data = {};
	var init = function(){
		initInput();
		initGrid();
		initWindow();
		initEvent();
		initContextMenu();
	};
	var initInput = function(){
		$("#expirationDatePosition").jqxDateTimeInput({width: '97%', height: 25, formatString: 'dd/MM/yyyy HH:mm:ss'});
	};
	var initGrid = function(){
		var grid = $("#positionFulfillmentGrid");
		var datafields = [{name: 'employeePartyId', type: 'string'},
		                 {name: 'emplPositionId', type: 'string'},
		                 {name: 'emplPositionTypeId', type: 'string'},
		                 {name: 'description', type: 'string'},
		                 {name: 'fromDate', type: 'date'},
		                 {name: 'thruDate', type: 'date'},
		                 {name: 'partyId', type: 'string'},
		                 {name: 'managerId', type: 'string'},
		                 {name: 'managerCode', type: 'string'},
		                 {name: 'managerName', type: 'string'},
		                 ];
		
		var columns = [{text: uiLabelMap.HREmplPositionTypeId, datafield: 'emplPositionTypeId', width: '20%'},
		               {text: uiLabelMap.HrCommonPosition, datafield: 'description', width: '20%'},
		               {text: uiLabelMap.CommonFromDate, datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', columntype: 'datetimeinput', width: '12%'},
		               {text: uiLabelMap.CommonThruDate, datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', columntype: 'datetimeinput', width: '12%'},
                       {text: uiLabelMap.HRDepartmentId, datafield: 'partyId', width: '20%'},
                       {text: uiLabelMap.HRDepartmentManagerId, datafield: 'managerCode', width: '16%'},
                       {text: uiLabelMap.HRDepartmentManagerName, datafield: 'managerName', width: '16%'},
		               ];
		
		var config = {
      		width: '100%', 
      		virtualmode: true,
      		showfilterrow: true,
      		showtoolbar: false,
      		selectionmode: 'singlerow',
      		pageable: true,
      		sortable: true,
	        filterable: true,
	        editable: false,
	        url: '',
	        source: {
	        	pagesize: 5
	        }
      	};
      	Grid.initGrid(config, datafields, columns, null, grid);
        if(globalVar.isHrDirectory=="true") {
            Grid.createContextMenu(grid, $("#positionListMenu"), false);
        }
	};
	var initWindow = function(){
		createJqxWindow($("#positionFulfillmentWindow"), 920, 430);
		createJqxWindow($("#expirationDateWindow"), 400, 130);
	};
	var initEvent = function(){
		$("#positionFulfillmentWindow").on('open', function(event){
			$("#positionFulfillPartyCode").html(_data.partyCode);
			$("#positionFulfillPartyName").html(_data.fullName);
			updateGridUrl($("#positionFulfillmentGrid"), "jqxGeneralServicer?sname=JQGetListEmplPosition&partyId=" + _data.partyId);
		});
		$("#positionFulfillmentWindow").on('close', function(event){
			_data = {};
			$("#positionFulfillmentGrid").jqxGrid('clearselection');
			$("#positionFulfillmentGrid").jqxGrid('clearfilters');
			$("#positionFulfillmentGrid").jqxGrid('gotopage', 0);
			updateGridUrl($("#positionFulfillmentGrid"), '');
		});
		$("#closePositionFulfillmentList").click(function(e){
			$("#positionFulfillmentWindow").jqxWindow('close');
		});
		$("#expirationDateWindow").on('open', function(e){
			var date = new Date();
			$("#expirationDatePosition").val(date);
		});
		$("#cancelExpireEmplPosition").click(function(e){
			$("#expirationDateWindow").jqxWindow('close');
		});
		$("#saveExpireEmplPosition").click(function(e){
			warningExpirationPosition(false);
		});
        $("#positionListMenu").on('shown', function(){
            var boundIndex = $("#positionFulfillmentGrid").jqxGrid('getselectedrowindexes');
            var rowData = $("#positionFulfillmentGrid").jqxGrid("getrowdata", boundIndex);
            var currentDate = new Date();
            if(rowData.thruDate != null && rowData.thruDate <= currentDate){
                $('#positionListMenu').jqxMenu({disabled: true});
            }else{
                $('#positionListMenu').jqxMenu({disabled: false});
            }
        });
	};
	var initContextMenu = function(){
        if(globalVar.isHrDirectory=="true"){
            createJqxMenu("positionListMenu", 30, 190, {popupZIndex: 22000});
            $("#positionListMenu").on('itemclick', function (event) {
                var args = event.args;
                var boundIndex = $("#positionFulfillmentGrid").jqxGrid('getselectedrowindex');
                var action = $(args).attr("action");
                if(action == "expireImmediately"){
                    warningExpirationPosition(true);
                }else if(action == "expireOnDay"){
                    openJqxWindow($("#expirationDateWindow"));
                }
            });
        }
	};
	var warningExpirationPosition = function(immediately){
        var boundIndex = $("#positionFulfillmentGrid").jqxGrid('getselectedrowindex');
        var rowData = $("#positionFulfillmentGrid").jqxGrid('getrowdata', boundIndex);
        var data = {partyId: rowData.employeePartyId, emplPositionId: rowData.emplPositionId, fromDate: rowData.fromDate.getTime()};
        if(!immediately) {
            var thruDate = $("#expirationDatePosition").jqxDateTimeInput('val', 'date');
            data.thruDate = thruDate.getTime();
            if ((data.thruDate) < (data.fromDate)) {
                bootbox.dialog(uiLabelMap.GTDateFieldRequired,
                    [{
                        "label": uiLabelMap.CommonClose,
                        "class": "btn-danger btn-small icon-remove open-sans",
                    }]
                );
                return false;
            } else {
                bootbox.dialog(uiLabelMap.ExpireEmplPositionWarning,
                    [
                        {
                            "label": uiLabelMap.CommonSubmit,
                            "class": "icon-ok btn btn-small btn-primary",
                            "callback": function () {
                                executeExpirationPosition(immediately);
                            }
                        },
                        {
                            "label": uiLabelMap.CommonCancel,
                            "class": "btn-danger icon-remove btn-small",
                        }
                    ]
                );
            }
        }else {
            bootbox.dialog(uiLabelMap.ExpireEmplPositionWarning,
                [
                    {
                        "label": uiLabelMap.CommonSubmit,
                        "class": "icon-ok btn btn-small btn-primary",
                        "callback": function () {
                            executeExpirationPosition(immediately);
                        }
                    },
                    {
                        "label": uiLabelMap.CommonCancel,
                        "class": "btn-danger icon-remove btn-small",
                    }
                ]
            );
        }
	};
	var executeExpirationPosition = function(immediately){
		Loading.show('loadingMacro');
		var boundIndex = $("#positionFulfillmentGrid").jqxGrid('getselectedrowindex');
		var rowData = $("#positionFulfillmentGrid").jqxGrid('getrowdata', boundIndex);
		var data = {partyId: rowData.employeePartyId, emplPositionId: rowData.emplPositionId, fromDate: rowData.fromDate.getTime()};
		if(!immediately){
			var thruDate = $("#expirationDatePosition").jqxDateTimeInput('val', 'date');
			data.thruDate = thruDate.getTime();
		}

		$.ajax({
			url: 'expirationEmplPosition',
			data: data,
			type: 'POST',
			success: function(response){
				if(response._ERROR_MESSAGE_){
					bootbox.dialog(response._ERROR_MESSAGE_,
							[
								{
								  "label" : uiLabelMap.CommonClose,
					    		   "class" : "btn-danger icon-remove btn-small",
								}
							]		
						);
					return;
				}
				Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {template : 'success', appendContainer : '#containerjqxgrid'});
				$("#expirationDateWindow").jqxWindow('close');
				$("#positionFulfillmentGrid").jqxGrid('updatebounddata');
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	var openWindow = function(data){
		_data = data;
		openJqxWindow($("#positionFulfillmentWindow"));
	};
	var updateGridUrl = function(grid, url){
		var source = grid.jqxGrid('source');
		source._source.url = url;
		grid.jqxGrid('source', source);
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function(){
	emplPositionFulFillObj.init();
});