var viewEmplTimekeepingObject = (function(){
	var editRow = -1;
	var cellEdit = -1;
	var theme = 'olbius';
	var dateTimeValue;
	var init = function(){
			initJqxDateTimeInput();
			if(globalVar.hasPermission){
				initContextMenu();
			}
			initBtnEvent();
			initJqxNbrInput();
			initGrid();
			initJqxValidator();
			initJqxNotification();
			initAceInputFile();
			initJqxTreeDropDownBtn();
			initJqxDropDownList();
			initJqxWindow();
	};
	var initJqxDropDownList = function(){
		var sourceArr = [
		                 {pattern: "dd/MM/yyyy", description: "dd/MM/yyyy"},
		                 {pattern: "MM/dd/yyyy", description: "MM/dd/yyyy"},
		];
		createJqxDropDownList(sourceArr, $("#dateTimePattern"), "pattern", "description", 25, "98%");
		var sourceOverrideData = [
		                          {description: uiLabelMap.OnlyDeleteDataCoincide, value: "onlyDeleteDataCoincide"},
		                          {description: uiLabelMap.DeleteAllDataExists, value: "deleteAllData"}
		];
		createJqxDropDownList(sourceOverrideData, $("#overrideDataWay"), "value", "description", 25, "99%");
		
	};
	
	var initJqxNotification = function(){
		$("#updateNotification").jqxNotification({
	        width: "100%", position: "top-left", opacity: 1, appendContainer: "#appendNotification",
	        autoOpen: false, animationOpenDelay: 800, autoClose: true
	    });
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#importExcelWindow"), 630, 510);
		$("#importExcelWindow").on('open', function(event){
			$("#numberLineTitle").val(1);
			$("#dateTimePattern").jqxDropDownList({selectedIndex: 0});
			var month = $("#month").val();
			var year = $("#year").val();
			var startDate = new Date(year, month - 1, 1);
			var endDate = new Date(year, month, 0);
			$("#fromDate").val(startDate);
			$("#thruDate").val(endDate);
			$("#overrideDataWay").jqxDropDownList({selectedIndex: 0});
		});
		$("#importExcelWindow").on('close', function(event){
			resetGridCellValue();
			clearAceInputFile($("#uploadedFile"));
			$('#importExcelWindow').jqxValidator('hide');
		});
	};
	
	var initAceInputFile = function(){
		$('#uploadedFile').ace_file_input({
	  		no_file:'No File ...',
			btn_choose: uiLabelMap.CommonChooseFile,
			droppable:false,
			onchange:null,
			thumbnail:false,	
			width: '100px',
			whitelist:'gif|png|jpg|jpeg',
			preview_error : function(filename, error_code) {
			}

		}).on('change', function(){
		});
	};
	
	var initJqxNbrInput = function(){
		$("#numberLineTitle").jqxNumberInput({ width: '80%', height: '25px', inputMode: 'simple', spinButtons: true, decimalDigits: 0 });
	}

	var initJqxValidator = function(){
		$('#importExcelWindow').jqxValidator({
			rules: [
			        {
			        	input: '#upLoadFileForm',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'keyup, blur',
			        	rule: function (input, commit){
			        		if(!$("#uploadedFile").val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        }
			]
		});
	};

	var initGrid  = function(){
		var datafield = [
				{name: 'fieldInDatabase', type: 'string'},
				{name: 'description', type: 'string'},
				{name: 'fieldInExcel', type: 'string'},
		];
		var colums = [
				{datafield: 'fieldInDatabase', hidden: true, editable: false},
				{text: uiLabelMap.FieldInDatabase, datafield: 'description', width: '50%', editable: false},
				{text: uiLabelMap.FieldInExcelFile, datafield: 'fieldInExcel', width: '50%', columntype: 'numberinput',
					cellsalign: 'right', 
					validation: function (cell, value) {                    
	                    if (!value && value < 1) {
	                        return { result: false, message: uiLabelMap.ValueGreaterThanOne };
	                    }
	                    return true;
	                }
	            }
		];
		
		var data = [
				{fieldInDatabase: "partyId", description: uiLabelMap.EmployeeId, fieldInExcel: 1},           
				{fieldInDatabase: "dateAttendance", description: uiLabelMap.DateAttendance, fieldInExcel: 2},           
				{fieldInDatabase: "startTime", description: uiLabelMap.TimesheetTimeIn, fieldInExcel: 3},           
				{fieldInDatabase: "endTime", description: uiLabelMap.TimesheetTimeOut, fieldInExcel: 4},           
		]; 
		
		var source = {
				localdata: data,
	            datatype: "array",
	            datafields: datafield,
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#gridMapField").jqxGrid({
			width: '100%',
			autoheight: true,
			theme: 'olbius',
	        source: dataAdapter,
	        editable: true,
	        columns: colums,
	        editmode: 'dblclick'
		});	
	};

	var resetGridCellValue = function(){
		$("#gridMapField").jqxGrid('setcellvalue', 0, "fieldInExcel", 1);
		$("#gridMapField").jqxGrid('setcellvalue', 1, "fieldInExcel", 2);
		$("#gridMapField").jqxGrid('setcellvalue', 2, "fieldInExcel", 3);
		$("#gridMapField").jqxGrid('setcellvalue', 3, "fieldInExcel", 4);
	};

	var initBtnEvent = function(){
		$("#importFromExcel").click(function(event){
			openJqxWindow($("#importExcelWindow"));
		});
		
		$("#alterCancel").click(function(event){
			$('#importExcelWindow').jqxWindow('close');
		});
		$("#removeFilter").click(function(event){
			$("#jqxgrid").jqxGrid('clearfilters');
		});
		searchAction();
		alterSaveAction();
	};
	
	var searchAction = function(){
		$("#btnSearch").click(function(){
			var month = $("#month").val();
			var year = $("#year").val();
			var item = $('#jqxTree').jqxTree('getSelectedItem');
			if(!item){
				return;
			}
			
			var datafieldEdit = new Array();
			var columnlistEdit = new Array();
			var columngrouplistEdit = new Array();
			datafieldEdit.push({"name": "partyId", "type": "string"});
			datafieldEdit.push({"name": "partyCode", "type": "string"});
			datafieldEdit.push({"name": "partyName", "type": "string"});
			
			columnlistEdit.push({hidden: true, 'datafield': 'partyId','editable': false});
			columnlistEdit.push({'text': uiLabelMap.EmployeeName, 'datafield': 'partyName', 'width': 160, 'cellsalign': 'left', 'editable': false, 'pinned': true});
			columnlistEdit.push({'text': uiLabelMap.EmployeeId, 'datafield': 'partyCode', 'cellsalign': 'left', 'editable': false, 'pinned': true, width: 120});
			var fromDate = new Date(year, month - 1, 1);
			var thruDate = new Date(year, month, 0);
			while(fromDate.getTime() <= thruDate.getTime()){
				var columnGroup = fromDate.getDate() + "/" + (fromDate.getMonth() + 1) + "/" + fromDate.getFullYear();
				var columnGroupText = fromDate.getDate() + "/" + (fromDate.getMonth() + 1) + " - " + weekday[fromDate.getDay()];
				var dateVal = fromDate.getTime();
				datafieldEdit.push({name: "date_" + columnGroup, type: "date"});
				datafieldEdit.push({name: "startTime_" + columnGroup, type: "date"});
				datafieldEdit.push({name:  "endTime_" + columnGroup, type: "date"});
				columnlistEdit.push({"datafield": "date_"+ columnGroup, hidden: true});
				columnlistEdit.push({text: uiLabelMap.HRCommonInTime, width: 100, cellsalign: "center", filterable: false, 
					datafield: "startTime_"+ columnGroup, columngroup: columnGroup , cellsformat: "HH:mm:ss", columntype: 'datetimeinput',
						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
							editor.jqxDateTimeInput({width: cellwidth, height: cellheight, formatString: 'HH:mm:ss', showCalendarButton: false});
							if(cellvalue){
								editor.val(cellvalue);
							}
						},
						cellvaluechanging: function (row, datafield, columntype, oldvalue, newvalue) {
							if (newvalue == '' || !newvalue) return oldvalue;
					    },
					    initeditor: function (row, column, editor) {
				            editor.jqxDateTimeInput({formatString: 'HH:mm:ss', showCalendarButton: false});
							editor.jqxDateTimeInput('val', new Date(globalVar.startTime));
				        },
				        cellendedit: function(rowid, datafield, columntype, oldvalue, newvalue){
				        	var editFlag = updateEmplTimeInDate(rowid, datafield, columntype, oldvalue, newvalue);
				        	return editFlag;
				        	
				        }
					});
				columnlistEdit.push({"text": uiLabelMap.HRCommonOutTime, "width": 100, "cellsalign": "center", "filterable": false, 
					"datafield": "endTime_"+ columnGroup, "columngroup": columnGroup , "cellsformat": "HH:mm:ss", columntype: 'datetimeinput',
					createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
						editor.jqxDateTimeInput({width: cellwidth, height: cellheight, formatString: 'HH:mm:ss', showCalendarButton: false});
						if(cellvalue){
							editor.val(cellvalue);
						}
					},
					cellvaluechanging: function (row, datafield, columntype, oldvalue, newvalue) {
						if (newvalue == '' || !newvalue) return oldvalue;
				    },
				    initeditor: function (row, column, editor) {
			            editor.jqxDateTimeInput({formatString: 'HH:mm:ss', showCalendarButton: false});
						editor.jqxDateTimeInput('val', new Date(globalVar.endTime));
			        },
			        cellendedit: function(rowid, datafield, columntype, oldvalue, newvalue){        	
			        	var editFlag = updateEmplTimeInDate(rowid, datafield, columntype, oldvalue, newvalue);
			        	return editFlag;
			        	
			        }
				});
				columngrouplistEdit.push({"text": columnGroupText, "name": columnGroup, 'align': 'center'});
				fromDate.setDate(fromDate.getDate() + 1);
			}
			$("#jqxgrid").jqxGrid('columns', columnlistEdit);
			$("#jqxgrid").jqxGrid('columngroups', columngrouplistEdit);
			var source = jQuery("#jqxgrid").jqxGrid('source');
			source._source.dataFields = datafieldEdit;
			source._source.url = "jqxGeneralServicer?month=" + (month - 1) +"&year=" + year + "&hasrequest=Y&sname=JQEmplListTimeKeeping&partyGroupId=" + item.value;
			$("#jqxgrid").jqxGrid('source', source);
		});
	};
	
	var alterSaveAction = function(){
		$("#alterSave").click(function(event){
			var valid = $('#importExcelWindow').jqxValidator('validate');
			if(!valid){
				return;
			}
			var form = jQuery("#upLoadFileForm");
			var file = form.find('input[type=file]').eq(0);
			if(file.data('ace_input_files')){
				var fileUpload = $('#uploadedFile')[0].files[0];
				$("#_uploadedFile_fileName").val(fileUpload.name);
				$("#_uploadedFile_contentType").val(fileUpload.type);
				var dataSubmit = new FormData(jQuery('#upLoadFileForm')[0]);	
			}else{
				var dataSubmit = new FormData();				
			}
			var fromDate = $("#fromDate").jqxDateTimeInput('val', 'date');
			var thruDate = $("#thruDate").jqxDateTimeInput('val', 'date');
			var overrideDataWay = $("#overrideDataWay").val();
			if(overrideDataWay){
				dataSubmit.append('overrideDataWay', overrideDataWay);
			}
			if(fromDate){
				dataSubmit.append('fromDate', fromDate.getTime());
			}
			if(thruDate){
				dataSubmit.append('thruDate', thruDate.getTime());
			}
			dataSubmit.append('startLine', $("#numberLineTitle").val());
			var rows = $("#gridMapField").jqxGrid('getrows');
			for(var i = 0; i < rows.length; i++){			
				dataSubmit.append(rows[i].fieldInDatabase, rows[i].fieldInExcel);
			}
			dataSubmit.append('dateTimePattern', $("#dateTimePattern").val());
			$("#jqxgrid").jqxGrid({disabled: true});
			$("#jqxgrid").jqxGrid('showloadelement');
			$.ajax({
				url: "importExcelFileTimekeeping",
				data: dataSubmit,
				type: 'POST',
				cache: false,			        
				processData: false, // Don't process the files
				contentType: false, // Set content type to false as jQuery will tell the server its a query string request
				success: function(data){
					$("#updateNotification").jqxNotification('closeLast');
					if(data._EVENT_MESSAGE_){
						$("#notificationText").text(data._EVENT_MESSAGE_);
						$("#updateNotification").jqxNotification({ template: 'info' });
						$("#updateNotification").jqxNotification('open');
						$("#jqxgrid").jqxGrid('updatebounddata');
					}else{
						$("#notificationText").text(data._ERROR_MESSAGE_);
						$("#updateNotification").jqxNotification({ template: 'error' });
						$("#updateNotification").jqxNotification('open');				
					}
				},
				complete: function(){
					$("#jqxgrid").jqxGrid({disabled: false});
					$("#jqxgrid").jqxGrid('hideloadelement');
				}
			});
			$('#importExcelWindow').jqxWindow('close');
		});
	};
	
	var initJqxDateTimeInput = function(){
		$("#month").jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 2, spinMode: 'simple',  inputMode: 'simple', min: 1, max: 12});
		$("#year").jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 4, spinMode: 'simple',  inputMode: 'simple'});
		$("#month").val(globalVar.currentMonth);
		$("#year").val(globalVar.currentYear);
		$("#fromDate").jqxDateTimeInput({width: '100%', height: 25});
		$("#thruDate").jqxDateTimeInput({width: '98%', height: 25});
	};

	var initContextMenu = function(){
		var liElement = $("#contextMenu>ul>li").length;
		var contextMenuHeight = 30 * liElement;
		$("#contextMenu").jqxMenu({ width: 180, height: contextMenuHeight, autoOpenPopup: false, mode: 'popup' , theme:'olbius'});
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			if($(args).attr("action") == 'delete'){
				var args = event.args;
				var cells = $('#jqxgrid').jqxGrid('getselectedcells');
				if(cells.length> 0){
					var cell = cells[0];
					var index = cell.rowindex;
					var rowData = $('#jqxgrid').jqxGrid('getrowdata', index);
					var dataField = cell.datafield;
					var dataSubmit = {};
					var suffixIndex = dataField.indexOf("_");
				    var suffix = dataField.substring(suffixIndex);
					dataSubmit["partyId"] = rowData.partyId;
					dataSubmit["date"] = rowData["date" +  suffix].getTime();
					$('#jqxgrid').jqxGrid({'disabled': true});
	           		$('#jqxgrid').jqxGrid('showloadelement');
					$.ajax({
						url: 'deletePartyAttendance',
						data: dataSubmit,
						type: 'POST',
						success: function(data){
							if(data.responseMessage == "success"){
								$("#jqxgrid").jqxGrid('setcellvalue', index, dataField, null);
							}
						},
						complete: function(jqXHR, status){
							$('#jqxgrid').jqxGrid({'disabled': false});	
	            			$('#jqxgrid').jqxGrid('hideloadelement');
	            			$('#jqxgrid').jqxGrid('clearselection');
						}
					});
				}else{
					bootbox.dialog(uiLabelMap.SelectCellBeforeDelete,
						[
							{
							    "label" : uiLabelMap.CommonSubmit,
							    "class" : "btn-primary btn-mini icon-ok",
							    "callback": function() {
							    }
							}, 
						]
					);
				}
			}
		});
	};
	
	var initJqxTreeDropDownBtn = function(){
		var config = {dropDownBtnWidth: 250, treeWidth: 250};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#dropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
		$("#jqxTree").on('select', function(event){
		  	var id = event.args.element.id;
		  	var item = $("#jqxTree").jqxTree('getItem', args.element);
		  	setDropdownContent(item, $(this), $("#dropDownButton"));
			var month = $("#month").val();
			var year = $("#year").val();
			var tmpS = $("#jqxgrid").jqxGrid('source');
			var value = $("#jqxTree").jqxTree('getItem', $("#"+id)[0]).value;
			tmpS._source.url = "jqxGeneralServicer?hasrequest=Y&sname=JQEmplListTimeKeeping&partyGroupId=" + value + "&month=" + (month - 1) + "&year=" + year;
			$("#jqxgrid").jqxGrid('source', tmpS);
		});
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
	};
	
	var updateEmplTimeInDate = function(rowid, dataField, columntype, oldvalue, newvalue){
		var rowData = $('#jqxgrid').jqxGrid('getrowdatabyid', rowid);
		var suffixIndex = dataField.indexOf("_");
	    var suffix = dataField.substring(suffixIndex);
	    var startEndTime = dataField.substring(0, suffixIndex); 
	    var row = {};
	    var date = rowData["date" + suffix];
	    
	    row["partyId"] = rowData.partyId;
	    if(date){
	    	row["date"] = date.getTime();	
	    }
	    row[startEndTime] = newvalue.getTime();
	    $("#jqxgrid").jqxGrid('showloadelement');
	    $("#jqxgrid").jqxGrid({disabled: true});
	    var commit = false;
	    $.ajax({
	    	url: 'updateEmplAttendanceTracker',
	    	data: row,
	    	type: 'POST',
	    	async: false,
	    	success: function(data){
	    		if(data.responseMessage == 'success'){
	    			commit = true;
	    		}
	    	},
	    	error: function(jqXHR, textStatus, errorThrown){
	    		commit = false		
	    	},
	    	complete: function(jqXHR, textStatus){
	    		$("#jqxgrid").jqxGrid('hideloadelement');
	    		$("#jqxgrid").jqxGrid({disabled: false});
	    		$('#jqxgrid').jqxGrid('clearselection');
	    	}
	    });
	    return commit;
	};
	
	return{
		init: init,
		updateEmplTimeInDate: updateEmplTimeInDate
	}
}());

$(document).ready(function () {
	viewEmplTimekeepingObject.init();
});
