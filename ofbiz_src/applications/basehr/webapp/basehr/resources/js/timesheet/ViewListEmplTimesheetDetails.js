var viewListEmplTimeDetailObject = (function(){
	var init = function(){
		createEmplTimesheetAtt();
	    createEmplTimekeepingSignGrid();
	    createTimesheetGeneralGrid();
	    initJqxGridEvent();
	    initJqxDropDownTreeBtn();
	    initEvent();
	};
	
	var initJqxDropDownTreeBtn = function(){
		createJqxTreeForTimesheetInfo($("#jqxTree"), $("#jqxDropDownButton"), $("#jqxTimesheetAtt"), "jqxGeneralServicer?hasrequest=Y&sname=JQEmplTimesheetAttendance");
		createJqxTreeForTimesheetInfo($("#jqxTreeGeneral"), $("#jqxDropDownButtonGeneral"), $("#jqxEmplTimesheetGeneral"), "jqxGeneralServicer?hasrequest=Y&sname=getEmplTimesheetGeneral", "General");
	};

	var createJqxTreeForTimesheetInfo = function(jqxTreeDiv, dropdownButton, grid, url, suffix){
		if(typeof(suffix) == "undefined"){
			suffix = "";
		}
		var config = {dropDownBtnWidth: 250, treeWidth: 250};
		globalObject.createJqxTreeDropDownBtn(jqxTreeDiv, dropdownButton, globalVar.rootPartyArr, "tree" + suffix, "treeChild" + suffix, config);
		 
		jqxTreeDiv.on('select', function(event){
	    	var id = event.args.element.id;
	    	var item = jqxTreeDiv.jqxTree('getItem', args.element);
	    	setDropdownContent(item, jqxTreeDiv, dropdownButton);
			
	    	var tmpS = grid.jqxGrid('source');
	    	var index = $("#jqxgrid").jqxGrid('getselectedrowindex');
	    	var selectTimesheet = $("#jqxgrid").jqxGrid('getrowdata', index);
			var emplTimesheetId = selectTimesheet.emplTimesheetId;
			var value = jqxTreeDiv.jqxTree('getItem', $("#"+id)[0]).value;
			//tmpS._source.data = {emplTimesheetId: emplTimesheetId, partyGroupId: value};
			tmpS._source.url = url + "&emplTimesheetId=" + emplTimesheetId + "&partyGroupId=" + value;
			grid.jqxGrid('source', tmpS);
	     });
	     //createExpandEventJqxTree(jqxTreeDiv);
	};
	
	var createTimesheetGeneralGrid = function(){
		var dataFieldTimesheetOverview = [{name: 'partyId', type:'string'},
		                                  {name: 'partyCode', type:'string'},
		                                  {name: 'partyName', type:'string'},
		                                  {name: 'emplPositionTypeId', type:'string'},
		                                  {name: 'overtimeRegister', type: 'number'},
			      						  {name: 'overtimeActual', type: 'number'},
			      						  {name: 'totalWorkingLateHour', type:'number'},
			      						  {name: 'totalDayLeave', type: 'number'},
			      						  {name: 'totalDayLeavePaidApproved', type: 'number'},
			      						  {name: 'totalDayWork', type: 'number'}];
		var columnEmplTimesheetGeneral = [{datafield: 'partyId', hidden: true},
		                                  {text: uiLabelMap.EmployeeId, datafield: 'partyCode',  editable: false,  cellsalign: 'left', width: 110, pinned: true},
		              					{text: uiLabelMap.EmployeeName, datafield: 'partyName', editable: false, cellsalign: 'left', width: 160, pinned: true},
		              					{text: uiLabelMap.HREmplFromPositionType, datafield: 'emplPositionTypeId',  editable: false, cellsalign: 'right', width: 150,
		              						cellsrenderer: function (row, column, value){
		              							for(var i = 0; i < emplPosTypeTimeSheetOverview.length; i++){
		              								if(emplPosTypeTimeSheetOverview[i].emplPositionTypeId == value){
		              									return '<div style=\"\">' + emplPosTypeTimeSheetOverview[i].description + '</div>';		
		              								}
		              							}
		              						}	
		              					},
		              					{text: uiLabelMap.OvertimeRegister, datafield: 'overtimeRegister', editable: false, cellsalign: 'right', width: 130, filtertype: 'number'},
		              					{text: uiLabelMap.OvertimeActual, datafield: 'overtimeActual', editable: false, cellsalign: 'right', width: 130, filtertype: 'number'},
		              					{text: uiLabelMap.TotalWorkingLateMinutes, datafield: 'totalWorkingLateHour' , editable: false, cellsalign: 'right', width: 130, filtertype: 'number'}, 
		              					{text: uiLabelMap.TotalDayLeave, datafield: 'totalDayLeave', editable: false, cellsalign: 'right', width: 150, filtertype: 'number'},
		              					{text: uiLabelMap.TotalDayPaidLeave, datafield: 'totalDayLeavePaidApproved', editable: false, cellsalign: 'right', width: 160, filtertype: 'number'},					
		              					{text: uiLabelMap.TotalTimeKeeping, datafield: 'totalDayWork', editable: false, cellsalign: 'right', width: 170, filtertype: 'number'}];	      						  
	  	
		var config = {
				url: '',
				showtoolbar : false,
				width : '100%',
				virtualmode: true,
				editable: false,
				filterable: true,
				selectionmode: 'singlerow',
				autoshowloadelement: true,
				showdefaultloadelement: true,
				localization: getLocalization(),
		};
		Grid.initGrid(config, dataFieldTimesheetOverview, columnEmplTimesheetGeneral, null, $("#jqxEmplTimesheetGeneral"));
	};

	var createEmplTimesheetAtt = function (){
		var config = {
				url: '',
				height: 485,
				autoheight: false,
				showtoolbar : false,
				width : '100%',
				virtualmode: true,
				editable: false,
				filterable: true,
				selectionmode: 'singlecell',
				autoshowloadelement: true,
				showdefaultloadelement: true,
				localization: getLocalization(),
		};
		Grid.initGrid(config, [], [], null, $("#jqxTimesheetAtt"));
	};

	var createEmplTimekeepingSignGrid = function(){
		var emplTimekeepingSignDatafield = [{name: 'emplTimekeepingSignId', type: 'string'},
	                                    {name: 'emplTimesheetId', type: 'string'},
	                                    {name: 'description', type: 'string'},
	                                    {name: 'sign', type: 'string'},
	                                    {name: 'dateAttendance', type: 'date'},
	                                    {name: 'partyId', type: 'string'},
	                                    {name: 'workday', type: 'number'},
	                                    {name: 'hours', type: 'number'}];
		
		var emplTimekeepingSignColumns = [{datafiled: 'partyId', hidden: true},
	                                  {datafiled: 'emplTimekeepingSignId', hidden: true},
	                                  {datafiled: 'emplTimesheetId', hidden: true},
	                                  {datafield: 'dateAttendance', hidden: true, cellsformat: 'dd/MM/yyyy', editable: false},
	                                  {datafield: 'hours', text: uiLabelMap.CommonHoursNumber, width: 65, editable: true,
	                                	 cellsrenderer: function(row, column, value){
	                                		 if(value){
	                                			return '<div style="text-align:right">' + value + '</div>'; 
	                                		 }
	                                	 },
	                                	 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight){
	                                		 editor.jqxInput({width: '100%', height: '100%'});
	                                	 }, 
	                                	 cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
	         	                            // return the old value, if the new value is empty.
	         	                            if (!newvalue || newvalue == "") return oldvalue;
	         	                        },
	         	                       	validation: function(cell, value){
	                            		  var pattern = /^([0-9]+\.?[0-9]*)$/;
	                            		  if(value && !value.match(pattern)){
	                            			  return {result: false,  message: uiLabelMap.OnlyInputNumberGreaterThanZero};	  
	                            		  }
	                            		  return true;
	                                	},
	                                	cellbeginedit: function (row, datafield, columntype){
	                                		var rowselectedIndexs = $("#jqxGridEmplTimekeepingSign").jqxGrid('getselectedrowindexes');
	                                		if(rowselectedIndexs.indexOf(row) < 0){
	                                			return false;
	                                		}
	                                		return true;
	                                	}
	                                  },
	                                  {datafield: 'workday', text: uiLabelMap.Workday, width: 80, editable: true,
	                                	  createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight){
	                                		  editor.jqxInput({width: '100%', height: '100%'});
	                                	  },
	                                	  validation: function(cell, value){
	                                		  var pattern = /^([0-9]+\.?[0-9]*)$/;
	                                		  if(value && !value.match(pattern)){
	                                			  return {result: false,  message: uiLabelMap.OnlyInputNumberGreaterThanZero};	  
	                                		  }
	                                		  return true;
	                                	  },
	                                	  cellbeginedit: function (row, datafield, columntype){
		                                		var rowselectedIndexs = $("#jqxGridEmplTimekeepingSign").jqxGrid('getselectedrowindexes');
		                                		if(rowselectedIndexs.indexOf(row) < 0){
		                                			return false;
		                                		}
		                                		return true;
		                                	}
	                                  },
	                                  {datafield: 'sign', text: uiLabelMap.EmplTimekeepingSign, width: 70, editable: false},
	                                  {datafield: 'description', text: uiLabelMap.CommonDescription, editable: false}
	                                  ];

		var emplTimekeepingSignSource = {
			localdata: [],
			datafields: emplTimekeepingSignDatafield,
			datatype: 'array',	
			updaterow: function(rowid, rowdata, commit){
				//$("#jqxGridEmplTimekeepingSign").jqxGrid({disabled: true});
				$("#jqxGridEmplTimekeepingSign").jqxGrid('showloadelement');
				var tmpDate = rowdata.dateAttendance;
				rowdata.dateAttendance = new Date(tmpDate).getTime();
				$.ajax({
					url: 'updateEmplTimesheetAttendance',
					type: 'POST',
					data: rowdata,
					success: function(response){
						if(response.responseMessage == 'success'){
							commit(true);
							$("#jqxTimesheetAtt").jqxGrid('updatebounddata');
						}else{
							$("#jqxNotificationTimesheetInDay").jqxNotification('closeLast');
							$("#jqxNotificationTimesheetInDayContent").text(response._ERROR_MESSAGE_);
		   					$("#jqxNotificationTimesheetInDay").jqxNotification({template: 'error'});
		   					$("#jqxNotificationTimesheetInDay").jqxNotification("open");
							commit(false);
						}
					},
					error: function(jqXHR, textStatus, errorThrown){
						commit(false);
					},
					complete: function(jqXHR, textStatus){
						//$("#jqxGridEmplTimekeepingSign").jqxGrid({disabled: false});
						$("#jqxGridEmplTimekeepingSign").jqxGrid('hideloadelement');
					}
				});
			}
	};

		var emplTimekeepingSignAdapter = new $.jqx.dataAdapter(emplTimekeepingSignSource, {autoBind: true});
		var rowsheight = 25;
		var maxHeight = 380;

		$("#jqxGridEmplTimekeepingSign").jqxGrid({
			width: 500,
			height: 394,
			editable: true,
			localization: getLocalization(),
			editmode: 'selectedcell',
		    source: emplTimekeepingSignAdapter,
			showtoolbar: false,
			rendertoolbar: function (toolbar) {
			var container = $("<div style='margin: 5px; text-align: right'></div>");
			toolbar.append(container);
			container.append('<i class="icon-ok"></i><input id="updateEmplTimekeepingSign" type="button" value="' + uiLabelMap.CommonUpdate + '" />');
				$("#updateEmplTimekeepingSign").jqxButton({theme: 'olbius'});
			},
		    pageSizeOptions: ['15','20', '30'],
		    pagerMode: 'advanced',
		    columnsresize: true,
		    pageable: true,
		    pagesize: 15,
		    columns: emplTimekeepingSignColumns,
		    selectionmode: 'checkbox',
		    theme: 'olbius'
		});
	};
	
	var initJqxGridEvent = function(){
		$("#jqxTimesheetAtt").on("celldoubleclick", function(event){
			var args = event.args;
			var rowBoundIndex = args.rowindex;
			var columnIndex = args.columnindex;
			//dataField have format like: dd/MM/yyyy
			var dataField = args.datafield;
			var data = $("#jqxTimesheetAtt").jqxGrid('getrowdata', rowBoundIndex);
			var emplTimesheetId = data.emplTimesheetId;
			var partyId = data.partyId;
			var dateStr = dataField.split("/");
			var date = new Date(dateStr[2], dateStr[1], dateStr[0]);
			//get startTime, endTime of party in day
			$("#startTimeIn").text("");	
			$("#endTimeOut").text("");	
			$.ajax({
				url: "getPartyAttendance",
				type: "POST",
				data: {partyId: partyId, dateKeeping: date.getTime()},
				success: function(data){
					$("#startTimeIn").text(data.startTime);	
					$("#endTimeOut").text(data.endTime);	
				},
				error:function(){
					
				}
			});
			$("#partyName").text(data.partyName);
			//$("#jqxGridEmplTimekeepingSign").jqxGrid({disabled: true});
			$("#jqxGridEmplTimekeepingSign").jqxGrid('showloadelement');
			
			$.ajax({
				url: 'getEmplTimesheetAttendanceWorkdayAndHours',
				data: {partyId: partyId, dateAttendance: date.getTime(), emplTimesheetId: emplTimesheetId},
				type: 'POST',
				success: function(response){
					var listReturn = response.listReturn;
					var localData = new Array();
					var selectedrowindexes = new Array();
					for(var i = 0; i < emplTimekeepingSignArr.length; i++){
						var row = new Array();
						row["emplTimesheetId"] = emplTimesheetId;
						row["emplTimekeepingSignId"] = emplTimekeepingSignArr[i].emplTimekeepingSignId; 
						for(var j = 0; j < listReturn.length; j++){
							if(listReturn[j].emplTimekeepingSignId == row["emplTimekeepingSignId"]){
								selectedrowindexes.push(i);
								row["hours"] = listReturn[j].hours;
								row["workday"] = listReturn[j].workday;
							}
						} 
						row["sign"] = emplTimekeepingSignArr[i].sign;
						row["partyId"] = partyId;
						row['emplTimesheetId'] = emplTimesheetId;
						row['description']= emplTimekeepingSignArr[i].description;
						row['dateAttendance'] = date; 
						localData.push(row);
					}
					
					var sourceEmplTimekeepingSign = $("#jqxGridEmplTimekeepingSign").jqxGrid('source');	
					sourceEmplTimekeepingSign._source.localdata = localData;
					$("#jqxGridEmplTimekeepingSign").jqxGrid('source', sourceEmplTimekeepingSign);
					$("#jqxGridEmplTimekeepingSign").jqxGrid({selectedrowindexes: selectedrowindexes});
				},
				complete: function(jqXHR, textStatus){
					//$("#jqxGridEmplTimekeepingSign").jqxGrid({disabled: false});
					$("#jqxGridEmplTimekeepingSign").jqxGrid('hideloadelement');
				}
			});
			$("#jqxWindowEmplTimesheetInDay").jqxWindow('setTitle', uiLabelMap.EmplTimesheetInDay + ": " + date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getFullYear());
			$("#jqxWindowEmplTimesheetInDay").jqxWindow('open');
		});
		$("#jqxGridEmplTimekeepingSign").on('rowunselect', function(event){
			var args = event.args;
			var rowBoundIndex = args.rowindex;
			var data = $("#jqxGridEmplTimekeepingSign").jqxGrid('getrowdata', rowBoundIndex);
			if(data && data.emplTimekeepingSignId){
				bootbox.dialog(uiLabelMap.NotifyDelete, 
						[{
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-mini icon-ok",
							"callback": function() {
								data.workday = null;
								data.hours = null;
								data.editType = "DELETE";
								var id = $('#jqxGridEmplTimekeepingSign').jqxGrid('getrowid', rowBoundIndex);
								$("#jqxGridEmplTimekeepingSign").jqxGrid('updaterow', id, data);
							}
						},
						{
							"label" : uiLabelMap.CommonCancel,
							"class" : "btn-danger icon-remove btn-mini",
							"callback": function() {
								$("#jqxGridEmplTimekeepingSign").jqxGrid('selectrow', rowBoundIndex);
							}
						}]);
			}
		});
	};	
	var initEvent = function(){
		$("#clearFilterBtnGeneral").click(function(event){
			$("#jqxEmplTimesheetGeneral").jqxGrid('clearfilters');
		});
		$("#clearFilterBtnDetail").click(function(event){
			$("#jqxTimesheetAtt").jqxGrid('clearfilters');
		});
	};
	return{
		init: init
	}
}());


$(document).ready(function(){
	viewListEmplTimeDetailObject.init();
});