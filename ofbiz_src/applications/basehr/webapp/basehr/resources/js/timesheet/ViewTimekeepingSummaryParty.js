var viewTimekeepingSummaryPartyObj = (function(){
	var init = function(){
		initContextMenu();
		initEvent();
	};
	var initContextMenu = function(){
		createJqxMenu("contextMenu", 30, 200);
	};
	var initEvent = function(){
		$("#contextMenu").on('itemclick', function (event) {
			var action = $(args).attr("action");
			var rowIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var rowData = $("#jqxgrid").jqxGrid('getrowdata', rowIndex);
			if(action == "refreshData"){
				updateTimekeepingSummaryParty(globalVar.timekeepingSummaryId);
			}else if(action == "viewDetail"){
				summaryPtyDetailObj.setData(rowData);
				summaryPtyDetailObj.openWindow();
			}
		});
	};
	var updateTimekeepingSummaryParty = function(timekeepingSummaryId){
		$("#jqxgrid").jqxGrid({disabled: true});
		$("#jqxgrid").jqxGrid('showloadelement');
		$.ajax({
			url: 'updateTimekeepingSummaryPartyFromTimekeepingDetail',
			data: {timekeepingSummaryId: timekeepingSummaryId},
			type: 'POST',
			success: function(response){
				if(response._EVENT_MESSAGE_){
					Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					Grid.renderMessage('jqxgrid', response._ERROR_MESSAGE_, {autoClose: true,
						template : 'error', appendContainer: "#containerjqxgrid", opacity : 0.9});
				}
			},
			complete: function(jqXHR, textStatus){
				$("#jqxgrid").jqxGrid({disabled: false});
				$("#jqxgrid").jqxGrid('hideloadelement');
			}
		});
	};
	return{
		init: init
	}
}());

var summaryPtyDetailObj = (function(){
	var _data = {};
	var init = function(){
		initGrid();
		initJqxWindow();
		initEvent();
	};
	var initGrid = function(){
		var datafield = [{name: 'text', type: 'string'},
		                 {name: 'value', type: 'string'},
		                 {name: 'workdayNumber', type: 'number'}
		                 ];
		var columns = [{text: uiLabelMap.HRCommonWorkday, datafield: 'text', width: '75%', ediable: false, align: 'center'},
		               {text: uiLabelMap.HRCommonTotalWorkday, datafield: 'workdayNumber', width: '25%', editable: true, 
							columntype: 'numberinput', align: 'center', cellsalign: 'right',
			 				createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight){
		                        if(row > 5){
                                    editor.jqxNumberInput({ width: cellwidth, height: cellheight, spinButtons: true, decimalDigits: 0, inputMode: 'simple'});
                                }else{
                                    editor.jqxNumberInput({ width: cellwidth, height: cellheight, spinButtons: true, decimalDigits: 1, inputMode: 'simple'});
                                }
			 				},
			 				initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
                                if(row > 5) {
                                    editor.jqxNumberInput({decimalDigits: 0});
                                }else{
                                    editor.jqxNumberInput({decimalDigits: 1});
                                }
			 					if(cellvalue){
			 						editor.val(cellvalue);
			 					}
			 				},
                            validation: function (cell, value) {
                                if(OlbCore.isNotEmpty(value) && value >=0){
                                    return true;
                                }
                                return { result: false, message: uiLabelMap.ValueMustGreaterOrEqualThanZero};
                            }
		               }
		               ];
		var config = {
	      		width: '99%', 
	      		autoheight: true,
	      		virtualmode: false,
	      		showfilterrow: false,
	      		showtoolbar: false,
	      		pageable: true,
	      		sortable: false,
	      		filterable: false,
	      		editable: true,
	      		selectionmode: 'singlerow',
	      		source:{
	      			pagesize: 10
	      		}
	      	};
		Grid.initGrid(config, datafield, columns, null, $("#summaryPtyDetailGrid"));
	};
	var initJqxWindow = function(){
		createJqxWindow($("#timekeepingSummaryPtyDetailWindow"), 550, 380);
	};
	var openWindow = function(){
		openJqxWindow($("#timekeepingSummaryPtyDetailWindow"));
	};
	var setData = function(data){
		_data = data;
		var localdata = [];
		for(var key in _data){
			if(key == "workdayStandard" || key == "workdayActual" || key == "totalWorkdayPaid" || key == "overtimeHoursNormal"
				|| key == "overtimeHoursWeekend" || key == "overtimeHoursHoliday" || key == "totalMinuteLate" || key == "totalWorkLate"){
				var column = $('#jqxgrid').jqxGrid('getcolumn', key);
				localdata.push({text: column.text, workdayNumber: _data[key], value: key});
			}
		}
		updateGridLocalData(localdata);
	};
	var initEvent = function(){
		$("#timekeepingSummaryPtyDetailWindow").on('open', function(event){
			$(this).jqxWindow('setTitle', uiLabelMap.TimekeepingSummaryPartyDetail + " - " + _data.fullName);
		});
		$("#timekeepingSummaryPtyDetailWindow").on('close', function(event){
			updateGridLocalData([]);
			_data = {};
		});
		$("#cancelSummaryPtyDetail").click(function(event){
			$("#timekeepingSummaryPtyDetailWindow").jqxWindow('close');
		});
		$("#saveSummaryPtyDetail").click(function(event){
			$("#summaryPtyDetailGrid").jqxGrid('showloadelement');
			$("#summaryPtyDetailGrid").jqxGrid({disabled: true});
			$("#cancelSummaryPtyDetail").attr("disabled", "disabled");
			$("#saveSummaryPtyDetail").attr("disabled", "disabled");
			var rows = $("#summaryPtyDetailGrid").jqxGrid('getrows');
			var dataSubmit = {};
			for(var i = 0; i < rows.length; i++){
				var rowData = rows[i];
				dataSubmit[rowData.value] = rowData.workdayNumber;
			}
			dataSubmit.partyId = _data.partyId;
			dataSubmit.timekeepingSummaryId = globalVar.timekeepingSummaryId;
			$.ajax({
				url: 'updateTimekeepingSummaryParty',
				data: dataSubmit,
				type: 'POST',
				success: function(response){
					if(response._EVENT_MESSAGE_){
						Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {autoClose: true,
							template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
						$("#timekeepingSummaryPtyDetailWindow").jqxWindow('close');
						$("#jqxgrid").jqxGrid('updatebounddata');
					}else{
						setData(_data);
						bootbox.dialog(response._ERROR_MESSAGE_,
								[
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);
					}
				},
				complete: function(jqXHR, textStatus){
					$("#summaryPtyDetailGrid").jqxGrid('hideloadelement');
					$("#summaryPtyDetailGrid").jqxGrid({disabled: false});
					$("#cancelSummaryPtyDetail").removeAttr("disabled");
					$("#saveSummaryPtyDetail").removeAttr("disabled");
				}
			});
		});
	};
	var updateGridLocalData = function(localdata){
		var source = $("#summaryPtyDetailGrid").jqxGrid('source');
		source._source.localdata = localdata;
		$("#summaryPtyDetailGrid").jqxGrid('source', source);
	};
	return{
		init: init,
		openWindow: openWindow,
		setData: setData
	}
}());

$(document).ready(function(){
	viewTimekeepingSummaryPartyObj.init();
	summaryPtyDetailObj.init();
});