var actionMenuObject = (function(){
	var init = function(){
		initContextMenu();
	};
	
	var executeTimesheetActInMenu = function(dataRecord, event, status){
		var args = event.args;	
	    var emplTimesheetId = dataRecord.emplTimesheetId;
	    var fromDate = new Date(dataRecord.fromDate); 
	    var thruDate = new Date(dataRecord.thruDate); 
	    if(status && status == "EMPL_TS_CALC"){
	    	bootbox.dialog(uiLabelMap.EmplTimesheetIsCalculating,
	   			[
					{
					    "label" : uiLabelMap.CommonSubmit,
					    "class" : "btn-primary btn-small icon-ok open-sans",
					    "callback": function() {
					    }
					}, 
				]
	    	);
	    	return;
	    }
	   	if($(args).attr("action") == 'refresh'){
	   		reCalcEmplTimesheet(emplTimesheetId); 	  
	    }else if($(args).attr("action") == 'approvalWorkOvertime'){
	    	updateApprovalWorkOvertime(fromDate, thruDate);
	    }else if($(args).attr("action") == 'overallEmplTimesheet'){
	    	overallEmplTimesheets(dataRecord);
	    }else if($(args).attr("action") == 'emplWorkingLate'){
	    	overallEmplWorkingLate(emplTimesheetId);
	    }else if($(args).attr("action") == 'sendApprovalTimesheets'){
	    	sendApprovalTimesheets(emplTimesheetId);
	    }else if($(args).attr("action") == 'timeRecorderDetails'){
	    	showTimesheetDetails(dataRecord, $("#emplTimesheetAttendancePopup"));
	    }
	};

	var reCalcEmplTimesheet = function (emplTimesheetId){
		$('#jqxgrid').jqxGrid({'disabled': true});
			$('#jqxgrid').jqxGrid('showloadelement');
			$("#jqxNotifyEmplTimesheets").jqxNotification('closeLast');
		$.ajax({
			url: "reimportTimesheetDataFromTimeRecord",
			data: {emplTimesheetId: emplTimesheetId},
			type: 'POST',
			success:function(data){
				if(data._EVENT_MESSAGE_){
					$("#jqxNotifyEmplTimesheets").html(data._EVENT_MESSAGE_);
					$("#jqxNotifyEmplTimesheets").jqxNotification({template: 'info'})
					$("#jqxNotifyEmplTimesheets").jqxNotification("open");
					//will call service update of jqx, need fix this
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					$("#jqxNotifyEmplTimesheets").html(data._ERROR_MESSAGE_);
					$("#jqxNotifyEmplTimesheets").jqxNotification({template: 'error'})
					$("#jqxNotifyEmplTimesheets").jqxNotification("open");
				}
			},
			complete: function(jqXHR, status){
				$('#jqxgrid').jqxGrid({'disabled': false});	
				$('#jqxgrid').jqxGrid('hideloadelement');
			}
		});  
	};

	var sendApprovalTimesheets = function(emplTimesheetId){
		$("#emplTimesheetId").val(emplTimesheetId);
		$("#proposalApprvalTimesheet").jqxWindow('open');
	};

	var showTimesheetDetails = function (data, emplTimesheetAttendancePopup){
		var emplTimesheetId = data.emplTimesheetId;
		var partyId = data.partyId;
		var fromDate = new Date(data.fromDate);
		var thruDate = new Date(data.thruDate);
		var dataFieldTimesheet = new Array();
		var columnsTimesheet = new Array();
		dataFieldTimesheet.push({"name": "partyId", "type": "string"});
		dataFieldTimesheet.push({"name": "partyCode", "type": "string"});
		dataFieldTimesheet.push({"name": "emplTimesheetId", "type": "string"});
		dataFieldTimesheet.push({"name": "partyName", "type": "string"});
		columnsTimesheet.push({'datafield': 'emplTimesheetId', 'width': 100, 'cellsalign': 'left', 'hidden': true});
		columnsTimesheet.push({'datafield': 'partyId', 'hidden': true});
		columnsTimesheet.push({'text': uiLabelMap.EmployeeId, 'datafield': 'partyCode', 'width': 120, 'cellsalign': 'left', 'editable': false, 'pinned': true});
		columnsTimesheet.push({'text': uiLabelMap.EmployeeName, 'datafield': 'partyName', 'width': 140, 'cellsalign': 'left', 'editable': false, 'pinned': true});
		
		while(fromDate.getTime() <= thruDate.getTime()){
			var date = fromDate.getDate() + "/" + fromDate.getMonth() + "/" + fromDate.getFullYear();
			var textDate = fromDate.getDate() + "/" + (fromDate.getMonth() + 1) + " - " + weekday[fromDate.getDay()];
			dataFieldTimesheet.push({"name": date, "type": "string"});
			dataFieldTimesheet.push({"name": date + "_hours", "type": "string"});
		
			columnsTimesheet.push({datafield: date + "_hours", hidden: true});
			columnsTimesheet.push({"text": textDate, datafield: date, width: 85, cellsalign: 'center',
										cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
											var valueRet = "";
											//"value" is array type and have format like that [emplTimekeepingSignId1, emplTimekeepingSignId2, ...] 
											if(value){
												//console.log(value);
												for(var j = 0; j < value.length; j++){
													for(var i = 0; i < emplTimekeepingSignArr.length; i++){
														if(emplTimekeepingSignArr[i].emplTimekeepingSignId == value[j]){
															valueRet += emplTimekeepingSignArr[i].sign;
														}
													}
													if(j < value.length - 1){
														valueRet += ", ";
													}
												}
												return '<div style="text-align:center">' +valueRet + '</div>';
											}else{
												return value;
											}
										}
								  });
			fromDate.setDate(fromDate.getDate() + 1); 
		}
		
		$("#jqxTimesheetAtt").jqxGrid('columns', columnsTimesheet);
		var sourceTimesheetAtt = $("#jqxTimesheetAtt").jqxGrid('source');
		//sourceTimesheetAtt._source.data = {emplTimesheetId: emplTimesheetId};
		sourceTimesheetAtt._source.url = '';
		sourceTimesheetAtt._source.datafields = dataFieldTimesheet;
		$("#jqxTimesheetAtt").jqxGrid('source', sourceTimesheetAtt);
		
		//updateTreeTimesheetDetails(partyId, data.groupName, $("#jqxTree"), "tsDetail", "tsDetailChild");
		//$("#jqxTimesheetAtt").jqxGrid('updatebounddata');
		
		openJqxWindow(emplTimesheetAttendancePopup);
	}

	var overallEmplTimesheets = function(dataRecord){
		var emplTimesheetId = dataRecord.emplTimesheetId;
		var partyId = dataRecord.partyId;
		//updateTreeTimesheetDetails(partyId, dataRecord.groupName, $("#jqxTreeGeneral"), "tsGeneral", "tsGeneralChild");
		openJqxWindow($("#jqxWindowEmplTimesheetGeneral"));
	};

	var initContextMenu = function(){
		var liElement = $("#contextMenu>ul>li").length;
		var contextMenuHeight = 30 * liElement; 
		$("#contextMenu").jqxMenu({ width: 280, height: contextMenuHeight, autoOpenPopup: false, mode: 'popup' , theme:'olbius'});
		
		$("#contextMenu").on('itemclick', function (event) {
			//var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
	        checkEmplTimesheetStatus(dataRecord, event);
		});
	};

	var checkEmplTimesheetStatus = function(dataRecord, event){
		var emplTimesheetId = dataRecord.emplTimesheetId;
		var statusId = dataRecord.statusId;
		var updatebounddata = false;
		$.ajax({
			url: 'getEmplTimesheetStatus',
			data: {emplTimesheetId: emplTimesheetId},
			type: 'POST',
			success: function(data){
				if(data.responseMessage == "success"){
					var newstatusId = data.statusId;
					if(newstatusId != statusId){
						updatebounddata = true;
						statusId = newstatusId
					}
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				
			},
			complete: function (jqXHR, status){
				executeTimesheetActInMenu(dataRecord, event, statusId);
				if(updatebounddata){
					$("#jqxgrid").jqxGrid('updatebounddata');
				}
			}
		});
	};
	
	/*var updateTreeTimesheetDetails = function(rootPartyId, rootName, jqxTreeDiv, suffix, suffixChild){
		var dataTreeGroup = [
				{
					"id": rootPartyId + "_" + suffix,
					"parentid": "-1",
					"text": rootName,
					"value": rootPartyId
				},
				{
					"id": rootPartyId +"_" + suffixChild,
					"parentid": rootPartyId + "_" + suffix,
					"text": "Loading...",
					"value": "getListPartyRelByParent"
				}
		];
		var source =
	    {
	        datatype: "json",
	        datafields: [
	            { name: 'id' },
	            { name: 'parentid' },
	            { name: 'text' },
	            { name: 'value' }
	        ],
	        id: 'id',
	        localdata: dataTreeGroup
	    };	
		var dataAdapter = new $.jqx.dataAdapter(source);
		dataAdapter.dataBind();
		var records = dataAdapter.getRecordsHierarchy('id', 'parentid', 'items', [{ name: 'text', map: 'label'}]);
		jqxTreeDiv.jqxTree({source: records});
		jqxTreeDiv.jqxTree("selectItem", $("#" + rootPartyId + "_" + suffix)[0]);
	};*/
	
	return{
		init: init
	}
}());

$(document).ready(function(){
	actionMenuObject.init();
});

