var viewAssignWorkingShiftObject = (function(){
	var init = function(){
		initJqxDateTime();	
		initBtnEvent();
		initJqxNotification();
		initJqxInput();
		initJqxTreeBtn();
		initJqxTreeEvent();
		initJqxWindow();
		initJqxValidator();
	};
	
	var initJqxValidator = function(){
		$('#assignWorkingWindow').jqxValidator({
			rules : [
			         {
			        	 input : '#assignFromDate',
			        	 message : uiLabelMap.FromDateLessThanEqualThruDate,
			        	 action : 'blur',
			        	 rule : function(input, commit){
			        		 if($('#assignThruDate').jqxDateTimeInput('getDate') <= input.jqxDateTimeInput('getDate')){
			        			 return false;
			        		 }
			        		 return true;
			        	 }
			         },
			         {
			        	 input : '#assignThruDate',
			        	 message : uiLabelMap.GTDateFieldRequired,
			        	 action : 'blur',
			        	 rule : function(input, commit){
			        		 if($('#assignFromDate').jqxDateTimeInput('getDate') >= input.jqxDateTimeInput('getDate')){
			        			 return false;
			        		 }
			        		 return true;
			        	 }
			         },
	         ]
		})
	};
	
	var initJqxTreeBtn = function(){
		var config = {dropDownBtnWidth: 250, treeWidth: 250};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#jqxDropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
	};
	
	var initJqxTreeEvent = function(){
		$("#jqxTree").on('select', function(event){
			var item = $('#jqxTree').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTree"), $("#jqxDropDownButton"));
			var tmpS = $("#jqxgrid").jqxGrid('source');
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
	
	var initJqxInput = function(){
		$("#partyAssignedWS").jqxInput({width: '96%', height: 20, disabled: true});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#assignWorkingWindow"), 400, 230);
		$("#assignWorkingWindow").on('open', function(event){
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			var fromDate = selection.from;
		    var thruDate = selection.to;
		    var item = $('#jqxTree').jqxTree('getSelectedItem');
		    $("#assignFromDate").val(fromDate);
			$("#assignThruDate").val(thruDate);
			$("#partyAssignedWS").val(item.label);
		});
		$("#assignWorkingWindow").on('close', function(){
			$('#assignWorkingWindow').jqxValidator('hide');
		})
	};
	
	var initJqxDateTime = function(){
		$("#dateTimeInput").jqxDateTimeInput({ width: 220, height: 25,  selectionMode: 'range', theme: 'olbius'});
		var fromDate = new Date(globalVar.monthStart);
		var thruDate = new Date(globalVar.monthEnd);
		$("#dateTimeInput").jqxDateTimeInput('setRange', fromDate, thruDate);
		$("#dateTimeInput").on('change', function(event){
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			var fromDate = selection.from.getTime();
		    var thruDate = selection.to.getTime();
		    var item = $("#jqxTree").jqxTree('getSelectedItem');
		    var partyId = item.value;
		    updateJqxDataFieldColumn(partyId, new Date(selection.from), new Date(selection.to));
		   // refreshGridData(partyId, fromDate, thruDate);
		});
		$("#assignFromDate").jqxDateTimeInput({ width: '98%', height: 25, theme: 'olbius'});
		$("#assignThruDate").jqxDateTimeInput({ width: '98%', height: 25, theme: 'olbius'});
	};
	
	function initJqxNotification(){
		$("#jqxNotification").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#container"});
	}

	var initBtnEvent = function(){
		$("#filterbutton").click(function(event){
			$("#jqxgrid").jqxGrid('clearfilters');
		})
	};
	
	var updateJqxDataFieldColumn = function(partyId, fromDate, thruDate){
		var datafieldEdit = new Array();
		var columnlistEdit = new Array();
		datafieldEdit.push({name: "partyId", type: "string"});
		datafieldEdit.push({name: "partyCode", type: "string"});
		datafieldEdit.push({name: "partyName", type: "string"});
		datafieldEdit.push({name: "emplPositionTypeId", type: "string"});
		datafieldEdit.push({name: "orgId", type: "string"});
		columnlistEdit.push({datafield: 'partyId',editable: false, hidden: true});
		columnlistEdit.push({text: uiLabelMap.EmployeeName, datafield: 'partyName', width: 130, cellsalign: 'left', editable: false, pinned: true});
		columnlistEdit.push({text: uiLabelMap.EmployeeId, datafield: 'partyCode', cellsalign: 'left', editable: false, pinned: true});
		
		columnlistEdit.push({text: uiLabelMap.HrCommonPosition, datafield: 'emplPositionTypeId', width: 180, editable: false});
		columnlistEdit.push({text: uiLabelMap.CommonDepartment, datafield: 'orgId', width: 180, editable: false});
		var fDate = new Date(fromDate);
		while(fromDate.getTime() < thruDate.getTime()){
			var column = "" + getDate(fromDate)  + getMonth(fromDate) + fromDate.getFullYear();		
			var columnText = fromDate.getDate() + "/" + getMonth(fromDate) + " - " + weekday[fromDate.getDay()];
			var dateVal = fromDate.getTime();
			datafieldEdit.push({name: "date_" + column, type: "date"});
			datafieldEdit.push({name: "ws_"+ column, type: "string"});
			columnlistEdit.push({datafield: "date_"+ column, hidden: true});
			columnlistEdit.push({datafield: 'ws_' + column, text: columnText,
									columntype: 'dropdownlist', width: 100, cellsalign: 'center',filterable: false,
									cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
										var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
										var date = rowData['date_' + column];
										for(var i = 0; i < workingShiftArr.length; i++){
											if(workingShiftArr[i].workingShiftId == value && date){
												return '<span>' + workingShiftWorkType[workingShiftArr[i].workingShiftId][dayOfWeekKey[date.getDay()]] + '</span>'
											}
										}
										if(value == 'EXPIRE'){
											return '<span>-------</span>';
										}else{
											return '<span>' + value + '</span>';
										}
									},
									createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight){
										var source = {
												localdata: workingShiftArr,
								                datatype: 'array'
										};
										 var dataAdapter = new $.jqx.dataAdapter(source);
										 editor.jqxDropDownList({ source: dataAdapter,  displayMember: 'workingShiftName', valueMember: 'workingShiftId', 
											 width: cellwidth, height: cellheight, autoDropDownHeight: true, dropDownWidth: 200,
										 });
										 if(cellvalue){
										 	editor.val(cellvalue);
										 }
									},
									cellbeginedit: function (rowindex, datafield, columntype) {
								        var data = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
								        if(data.datafield == 'EXPIRE'){
								        	return false;
								        }
								    },
								    cellendedit: function(rowid, datafield, columntype, oldvalue, newvalue){        	
							        	var editFlag = updateWorkingShiftEmployee(rowid, datafield, columntype, oldvalue, newvalue);
							        	return editFlag;
							        }
								});
			fromDate.setDate(fromDate.getDate() + 1);
		}
		
		$("#jqxgrid").jqxGrid('columns', columnlistEdit);
		var source = jQuery("#jqxgrid").jqxGrid('source');
		source._source.datafields = datafieldEdit;
		source._source.url = "jqxGeneralServicer?sname=getWorkingShiftEmployee&hasrequest=Y&partyGroupId=" + partyId + "&fromDate=" + fDate.getTime() + "&thruDate=" + thruDate.getTime();
		$("#jqxgrid").jqxGrid('source', source);
	};

	var refreshGridData = function(partyId, fromDate, thruDate){
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=getWorkingShiftEmployee&hasrequest=Y&partyGroupId=" + partyId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
		$("#jqxgrid").jqxGrid('source', tmpS);
	};

	var updateWorkingShiftEmployee = function(rowid, datafield, columntype, oldvalue, newvalue){
		var rowData = $('#jqxgrid').jqxGrid('getrowdatabyid', rowid);
		var suffixIndex = datafield.indexOf("_");
		var suffix = datafield.substring(suffixIndex);
		var date = rowData["date" + suffix];
		var row = {}; 
		row.partyId = rowData.partyId;
		row.workingShiftId = newvalue;
		row.date = date.getTime();
		$("#jqxgrid").jqxGrid('showloadelement');
		$("#jqxgrid").jqxGrid({disabled: true});
		var commit = false;
		$.ajax({
			url: 'updateWorkingShiftEmployee',
			data: row,
			type: 'POST',
			async: false,
			success:function(response){
				$("#jqxNotification").jqxNotification('closeLast');
				if(response.responseMessage == 'success'){
	   			commit = true;
	   		}else{
	   			$("#notificationContent").text(response.errorMessage);
					$("#jqxNotification").jqxNotification({template: 'error'});
					$("#jqxNotification").jqxNotification("open");
	   		}
			},
			error: function(jqXHR, textStatus, errorThrown){
	   		commit = false		
	   	},
	   	complete: function(jqXHR, textStatus){
	   		$("#jqxgrid").jqxGrid('hideloadelement');
	   		$('#jqxgrid').jqxGrid('clearselection');
	   		$("#jqxgrid").jqxGrid({disabled: false});
	   	}
		});
		return commit;
	};
	
	return{
		init: init,
		updateWorkingShiftEmployee: updateWorkingShiftEmployee
	}
}());
$(document).ready(function () {
	viewAssignWorkingShiftObject.init();
});

