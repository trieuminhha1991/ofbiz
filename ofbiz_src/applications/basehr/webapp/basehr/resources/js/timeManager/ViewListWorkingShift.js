var viewListWorkingShiftObject = (function(){
	var init = function(){
			initJqxInput();
			initJqxDatetimeInput();
			initJqxGridWorkWeek();
			initJqxNotification();
			initJqxNumberInput();
			initJqxgridEvent();
			initJqxCheckBox();
			initJqxWindow();
	};
	
	var initJqxInput = function (){
		$("#workingShiftId, #workingShiftName").jqxInput({width: '95%', height: 20, theme: 'olbius'});
		$("#workingShiftId").jqxInput({disabled: true});
	};

	var initJqxDatetimeInput = function(){
		$("#shiftStartTime, #shiftBreakStartTime, #shiftBreakEndTime, #shiftEndTime, " +
				"#startOverTimeAfterShift, #endOverTimeAfterShift").jqxDateTimeInput({
			formatString: 'HH:mm:ss', 
			showCalendarButton: false,
			width: '89%', 
			height: '25px',
			theme: 'olbius'
		});
		$("#startOverTimeAfterShift, #endOverTimeAfterShift").jqxDateTimeInput({
			disabled: true
		});
	};

	var initJqxGridWorkWeek = function(){
		var datafield =  globalObject.getDayOfWeekList();
		var columnlist = globalObject.getColumnGridCreateNewWS();
		var rendertoolbar = function (toolbar){
			var jqxheader = $("<div id='toolbarcontainer' class='widget-header'><h4>" + uiLabelMap.WorkingShifWorkTypeWorkWeek + "</h4><div id='toolbarButtonContainer' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
		};
		var editColumns = globalVar.editColumns;	
		
		var config = {
		   		width: '100%', 
		   		autoheight: true,
		   		virtualmode: true,
		   		showfilterrow: true,
		   		showtoolbar: true,
		   		rendertoolbar: rendertoolbar,
		   		selectionmode: 'singlecell',
		   		pageable: true,
		   		sortable: false,
		        filterable: false,
		        editable: globalVar.editable,
		        rowsheight: 26,
		        url: 'JQGetWorkingShiftDayWeek&hasrequest=Y',                
		        source: {pagesize: 10}
	   	};
		if(globalVar.editable){
			config.editmode = 'dblclick';
		}
	   	Grid.initGrid(config, datafield, columnlist, null, $("#jqxgridShiftWorkType"));
	   	//$("#jqxgridShiftWorkType").jqxGrid('selectionmode', 'singlecell');
	}

	var initJqxNotification = function(){
		$("#jqxNotificationjqxgridShiftWorkType").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#containerjqxgridShiftWorkType"});
		$("#jqxNotification").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#containerNtf"});
		$("#jqxNotificationEditConfigPartyWS").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#containerNtfEditConfigPartyWS"});
	};

	var initJqxNumberInput = function (){
		$("#minMinuteOvertime, #allowLateMinute").jqxNumberInput({ 
			width: '97%', height: '25px',  spinButtons: true, theme: 'olbius', inputMode: 'simple',
			decimalDigits: 0, min: 0
		});
		$("#minMinuteOvertime").jqxNumberInput({
			disabled: true
		});
	};

	var initJqxgridEvent = function(){
		$("#jqxgrid").on("rowdoubleclick", function(event){
			var index = event.args.rowindex;
			showRowGridDetail(index);
		});
	};
	
	var initJqxCheckBox = function (){
		$("#allowOTAfterShiftEdit").jqxCheckBox({theme: 'olbius', height: 25});
		
		 $("#allowOTAfterShiftEdit").on('change', function (event) {
			$("#minMinuteOvertime").jqxNumberInput({ disabled: !event.args.checked });
			$("#startOverTimeAfterShift, #endOverTimeAfterShift").jqxDateTimeInput({
				 disabled: !event.args.checked 
			});
		 });
	};

	var initJqxWindow = function (){
		createJqxWindow($("#workingShiftEditWindow"), 850, 550);
		$("#workingShiftEditWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
		
		$("#workingShiftEditWindow").on('open', function(event){
		});
		
	};
	
	var showRowGridDetail = function(rowindex){
		openJqxWindow($("#workingShiftEditWindow"));
		var data = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
		var tmpS = $("#jqxgridShiftWorkType").jqxGrid('source');
		tmpS._source.url = 'jqxGeneralServicer?sname=JQGetWorkingShiftDayWeek&hasrequest=Y&workingShiftId=' + data.workingShiftId;
		$("#jqxgridShiftWorkType").jqxGrid('source', tmpS);
		$("#workingShiftEditWindow").jqxWindow('setTitle', uiLabelMap.WorkingShiftInfo + ' ' + data.workingShiftName);
		fillData(data);
	};

	var fillData = function (data){	
		$("#workingShiftId").val(data.workingShiftId);
		$("#workingShiftName").val(data.workingShiftName);
		$("#minMinuteOvertime").val(data.minMinuteOvertime);
		$("#allowLateMinute").val(data.allowLateMinute);
		$("#shiftStartTime").val(data.shiftStartTime);
		var isAllowOTAfterShift = data.isAllowOTAfterShift;
		if("Y" == isAllowOTAfterShift){
			$("#allowOTAfterShiftEdit").jqxCheckBox({checked: true});
		}
		if(data.shiftBreakStart){
			$("#shiftBreakStartTime").val(data.shiftBreakStart);	
		}else{
			$("#shiftBreakStartTime").val(null);
		}
		if(data.shiftBreakEnd){
			$("#shiftBreakEndTime").val(data.shiftBreakEnd);
		}else{
			$("#shiftBreakEndTime").val(null);
		}
		$("#shiftEndTime").val(data.shiftEndTime);
		if(data.startOverTimeAfterShift){
			$("#startOverTimeAfterShift").val(data.startOverTimeAfterShift);
		}else{
			$("#startOverTimeAfterShift").val(null);
		}
		if(data.endOverTimeAfterShift){
			$("#endOverTimeAfterShift").val(data.endOverTimeAfterShift);
		}else{
			$("#endOverTimeAfterShift").val(null);
		}
	};
	
	return{
		init: init,
		showRowGridDetail: showRowGridDetail
	}
}());

$(document).ready(function(){
	viewListWorkingShiftObject.init();
});

function RemoveFilter(){
	$('#jqxgrid').jqxGrid('clearfilters');
}