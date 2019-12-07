<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpanel.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>

<#include "/hrolbius/webapp/hrolbius/ftl/js/commonUtil.ftl"/>
<#assign workingShiftList = Static["com.olbius.payroll.util.TimekeepingUtils"].getAllWorkingShift(delegator)/>
<#assign sizeWorkingShift = workingShiftList.size()/> 
<#assign startTime = workingShiftList.get(0).get("shiftStartTime").getTime() /> 
<#assign endTime = workingShiftList.get(0).get("shiftEndTime").getTime() />


<#assign datafield ="[{name: 'partyId', type:'string'},
					  {name: 'partyName', type:'string'},"/>
<#assign columnlist = "{text: '${uiLabelMap.EmployeeName}', datafield: 'partyName', width: 140, cellsalign: 'left', editable: false, pinned: true},
					   {text: '${uiLabelMap.EmployeeId}', datafield: 'partyId', width: 100,cellsalign: 'left', pinned: true, editable: false,
								cellsrenderer: function (row, column, value) {
									var data = $('#jqxgrid').jqxGrid('getrowdata', row)
									if (data && data.partyId){
        								return '<a style = \"margin-left: 10px\" href=' + 'EmployeeProfile?partyId=' + data.partyId + '>' +  data.partyId + '</a>'
    								}
    							}
    					},"/>
<#assign cal = Static["java.util.Calendar"].getInstance()/>
<#assign CalendarDate =  Static["java.util.Calendar"].DATE/>   					
<#assign CalendarMonth =  Static["java.util.Calendar"].MONTH/>   					
<#assign CalendarYear =  Static["java.util.Calendar"].YEAR/>  
<#assign columngrouplist = ""/> 
<#assign dayOfWeekNameList = [uiLabelMap.CommonSundayShort, uiLabelMap.CommonMondayShort, uiLabelMap.CommonTuesdayShort, uiLabelMap.CommonWednesdayShort, uiLabelMap.CommonThursdayShort, uiLabelMap.CommonFridayShort, uiLabelMap.CommonSaturdayShort]/>					
<#list dateOfMonth as date>
	${cal.setTime(date)}
	<#assign dataFieldGroup = cal.get(CalendarDate) + "/" +  (cal.get(CalendarMonth) + 1) + "/" + cal.get(CalendarYear)/>
	<#assign datafield = datafield + "{name: 'date_"+ dataFieldGroup +"', type: 'date'},"/>
	<#assign datafield = datafield + "{name: 'startTime_"+ dataFieldGroup +"', type: 'date'},"/>
	<#assign datafield = datafield + "{name: 'endTime_"+ dataFieldGroup +"', type: 'date'},"/> 
	
	<#assign columnlist = columnlist + "{datafield: 'date_"+ dataFieldGroup +"', hidden: 'true'},"/>
	<#assign columnlist = columnlist + "{text: '${uiLabelMap.HRCommonInTime}', width: 100, columntype: 'datetimeinput', cellsalign: 'center', filterable: false, datafield: 'startTime_"+ dataFieldGroup + "', columngroup: '" + dataFieldGroup+ "', cellsformat: 'HH:mm:ss',
		createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
			editor.jqxDateTimeInput({width: cellwidth, height: cellheight, formatString: 'HH:mm:ss', showCalendarButton: false});
			if(cellvalue){
				editor.val(cellvalue);
			}
		},
		initeditor: function (row, column, editor) {
            editor.jqxDateTimeInput({formatString: 'HH:mm:ss', showCalendarButton: false});
			editor.jqxDateTimeInput('val', new Date(${startTime}));
        },
        cellendedit: function(rowid, datafield, columntype, oldvalue, newvalue){
        	var editFlag = updateEmplTimeInDate(rowid, datafield, columntype, oldvalue, newvalue);
        	return editFlag;
        }
	},"/>
	<#assign columnlist = columnlist + "{text: '${uiLabelMap.HRCommonOutTime}', columntype: 'datetimeinput', width: 100, cellsalign: 'center',filterable: false,  datafield: 'endTime_"+ dataFieldGroup + "', columngroup: '" + dataFieldGroup + "', cellsformat: 'HH:mm:ss',
		createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
			editor.jqxDateTimeInput({width: cellwidth, height: cellheight, formatString: 'HH:mm:ss', showCalendarButton: false});
			if(cellvalue){
				editor.val(cellvalue);
			}else{
				editor.val(new Date(${endTime}));
			}
		},
		initeditor: function (row, column, editor) {
            editor.jqxDateTimeInput({formatString: 'HH:mm:ss', showCalendarButton: false});
			editor.jqxDateTimeInput('val', new Date(${endTime}));
        },
        cellendedit: function(rowid, datafield, columntype, oldvalue, newvalue){        	
        	var editFlag = updateEmplTimeInDate(rowid, datafield, columntype, oldvalue, newvalue);
        	return editFlag;
        }
		
	},"/>
	<#assign columngrouplist = columngrouplist + "{text: '" + cal.get(CalendarDate) + "/" + (cal.get(CalendarMonth) + 1) + " - " + dayOfWeekNameList[cal.get(Static["java.util.Calendar"].DAY_OF_WEEK) - 1] + "', name: '" + dataFieldGroup + "', align: 'center'},"/>
</#list>    										  
<#assign datafield = datafield + "]"/>
<script type="text/javascript">
	var updateRowCustom = function(rowid, updateData, commit){
		
	};
	$(document).ready(function () {
		$("#jqxgrid").on('cellEndEdit', function (event) {
		    // event arguments.
		    var args = event.args;
		    // column data field.
		    var dataField = event.args.datafield;
		    // row's bound index.
		    var rowBoundIndex = event.args.rowindex;
		    // cell value
		    var value = args.value;
		    // cell old value.
		    var oldvalue = args.oldvalue;
		    // row's data.
		    var rowData = args.row;
		    var suffixIndex = dataField.indexOf("_");
		    var suffix = dataField.substring(suffixIndex);
		    var startEndTime = dataField.substring(0, suffixIndex); 
		    var row = {};
		    row["partyId"] = rowData.partyId;
		    row["date"] = rowData["date" + suffix];
		    row[startEndTime] = value;
		    var rowid = $('#jqxgrid').jqxGrid('getrowid', rowBoundIndex);
		    //$("#jqxgrid").jqxGrid('updaterow', rowid, row);
		});	
	});
	
	function updateEmplTimeInDate(rowid, dataField, columntype, oldvalue, newvalue){
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
	}
</script>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.DetailEmplTimekeepingReportTiltle}</h4>
		<div class="widget-toolbar none-content">
			
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class="row-fluid">
						<div class="span3" style="margin: 0; padding: 0">
							<label style="display: inline;">${uiLabelMap.CommonMonth} / ${uiLabelMap.CommonYear}</label>
						</div>
						<div class="span9" style="margin: 0">
							<div class="row-fluid">
								<div class="span3">
									<div id="month"></div>
								</div>
								<div class="span3">
									<div id="year"></div>
								</div>
								<div class="span2">
									<button class="btn-primary btn-mini btn" id="btnSearch">
										<i class="icon-search"></i>
									</button>
								</div>
							</div>
						</div>
					</div>	
				</div>	
				<div class="span6">
					<div class="row-fluid">
						<div class="form-horizontal" style="margin-top: -8px">
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.CommonDepartment}</label>
								<div class="controls">
									<div id="dropDownButton">
										<div style="border: none;" id="jqxTree">
											
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">	
			<@jqGrid url="jqxGeneralServicer?month=${month}&year=${year}&hasrequest=Y&sname=JQEmplListTimeKeeping&partyGroupId=${defaultPartyId}" 
				columnlist=columnlist dataField=datafield 
				filtersimplemode="true" editmode="selectedcell" sortable="false" showtoolbar="false" editable="true" id="jqxgrid" 
				clearfilteringbutton="true" columngrouplist=columngrouplist mouseRightMenu="true" contextMenuId="contextMenu"
				updateRowFunction="updateRowCustom" selectionmode="singlecell"
				jqGridMinimumLibEnable="false"
				/>
		</div>
	</div>
</div>
	<#assign currentYear = currentDateTime.get(Static["java.util.Calendar"].YEAR)>
	<#assign currentMonth = currentDateTime.get(Static["java.util.Calendar"].MONTH) + 1>
	
<div id="EmplTimeKeeping">
</div>	

<script type="text/javascript">


$(document).ready(function () {
	var editRow = -1;
	var cellEdit = -1;
	var theme = 'olbius';
	var dateTimeValue;
	initJqxDateTimeInput();
	initContextMenu();
	
	jQuery("#btnSearch").click(function(){
		var month = $("#month").val();
		var year = $("#year").val();
		var item = $('#jqxTree').jqxTree('getSelectedItem');
		if(!item){
			item = $("#jqxTree").jqxTree('getItem', $("#${defaultPartyId}_timekeeping")[0]);
		}
		
		var datafieldEdit = new Array();
		var columnlistEdit = new Array();
		var columngrouplistEdit = new Array();
		datafieldEdit.push({"name": "partyId", "type": "string"});
		datafieldEdit.push({"name": "partyName", "type": "string"});
		
		columnlistEdit.push({'text': '${uiLabelMap.EmployeeName}', 'datafield': 'partyName', 'width': 100, 'cellsalign': 'left', 'editable': false, 'pinned': true});
		columnlistEdit.push({'text': '${uiLabelMap.EmployeeId}', 'datafield': 'partyId', 'cellsalign': 'left', 'editable': false, 'pinned': true,
							cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row)
								if (data && data.partyId){
									return '<a style = \"margin-left: 10px\" href=' + 'EmployeeProfile?partyId=' + data.partyId + '>' +  data.partyId + '</a>'
								}
							}
						});
		var fromDate = new Date(year, month - 1, 1);
		var thruDate = new Date(year, month, 0);
		while(fromDate.getTime() < thruDate.getTime()){
			var columnGroup = fromDate.getDate() + "/" + (fromDate.getMonth() + 1) + "/" + fromDate.getFullYear();
			var columnGroupText = fromDate.getDate() + "/" + (fromDate.getMonth() + 1) + " - " + weekday[fromDate.getDay()];
			var dateVal = fromDate.getTime();
			datafieldEdit.push({"name": "date_" + columnGroup, "type": "date"});
			datafieldEdit.push({"name": "startTime_" + columnGroup, "type": "date"});
			datafieldEdit.push({"name":  "endTime_" + columnGroup, "type": "date"});
			columnlistEdit.push({"datafield": "date_"+ columnGroup, hidden: true});
			columnlistEdit.push({"text": "${uiLabelMap.HRCommonInTime}", "width": 100, "cellsalign": "center", "filterable": false, 
				"datafield": "startTime_"+ columnGroup, "columngroup": columnGroup , "cellsformat": "HH:mm:ss",
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
						editor.jqxDateTimeInput('val', new Date(${startTime}));
			        },
			        cellendedit: function(rowid, datafield, columntype, oldvalue, newvalue){        	
			        	var editFlag = updateEmplTimeInDate(rowid, datafield, columntype, oldvalue, newvalue);
			        	return editFlag;
			        	
			        }
				});
			columnlistEdit.push({"text": "${uiLabelMap.HRCommonOutTime}", "width": 100, "cellsalign": "center", "filterable": false, 
				"datafield": "endTime_"+ columnGroup, "columngroup": columnGroup , "cellsformat": "HH:mm:ss",
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
					editor.jqxDateTimeInput('val', new Date(${endTime}));
		        },
		        cellendedit: function(rowid, datafield, columntype, oldvalue, newvalue){        	
		        	var editFlag = updateEmplTimeInDate(rowid, datafield, columntype, oldvalue, newvalue);
		        	return editFlag;
		        	
		        }
			});
			columngrouplistEdit.push({"text": columnGroupText, "name": columnGroup, 'align': 'center'});
			fromDate.setDate(fromDate.getDate() + 1);
		}
		jQuery("#jqxgrid").jqxGrid('columns', columnlistEdit);
		jQuery("#jqxgrid").jqxGrid('columngroups', columngrouplistEdit);
		var source = jQuery("#jqxgrid").jqxGrid('source');
		source._source.dataFields = datafieldEdit;
		source._source.url = "jqxGeneralServicer?month=" + (month - 1) +"&year=" + year + "&hasrequest=Y&sname=JQEmplListTimeKeeping&partyGroupId=" + item.value;
		jQuery("#jqxgrid").jqxGrid('source', source);
	});
	
	
	/*==================jqxTree dropdown define==================*/
	var dataTree = new Array();
	<#list treePartyGroup as tree>
		var row = {};
		row["id"] = "${tree.id}_timekeeping";
		row["text"] = "${tree.text}";
		row["parentId"] = "${tree.parentId}_timekeeping";
		row["value"] = "${tree.idValueEntity}";
		dataTree[${tree_index}] = row;
	</#list>
	var sourceTree =
	{
	    datatype: "json",
	    datafields: [
	    	{ name: 'id'},
	        { name: 'parentId'},
	        { name: 'text'} ,
	        { name: 'value'}
	    ],
	    id: 'id',
	    localdata: dataTree
	};
	var dataAdapterTree = new $.jqx.dataAdapter(sourceTree);
	// perform Data Binding.
	dataAdapterTree.dataBind();
	// get the tree items. The first parameter is the item's id. The second parameter is the parent item's id. The 'items' parameter represents 
	// the sub items collection name. Each jqxTree item has a 'label' property, but in the JSON data, we have a 'text' field. The last parameter 
	// specifies the mapping between the 'text' and 'label' fields.  
	var recordsTree = dataAdapterTree.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'text', map: 'label'}]);
	$("#dropDownButton").jqxDropDownButton({ width: '270px', height: 25, theme: 'olbius'});
	$('#jqxTree').jqxTree({source: recordsTree,width: "270px", height: "240px", theme: 'olbius'});
	 <#if expandedList?has_content>
	 	<#list expandedList as expandId>
	 		$('#jqxTree').jqxTree('expandItem', $("#${expandId}_timekeeping")[0]);
	 	</#list>
	 </#if>    
	 <#if expandedList?has_content>
	 	<#assign defaultPartyId = expandedList.get(expandedList?size - 1)>
	 	var initElement = $("#${expandedList.get(0)}_timekeeping")[0];
	 	setDropdownContent(initElement);
	 </#if>
	 
	 $('#jqxTree').on('select', function(event){
    	var id = event.args.element.id;
    	var item = $('#jqxTree').jqxTree('getItem', args.element);
    	setDropdownContent(item);
        var month = $("#month").val();
		var year = $("#year").val();
		var tmpS = $("#jqxgrid").jqxGrid('source');
		var value = jQuery("#jqxTree").jqxTree('getItem', $("#"+id)[0]).value;
		tmpS._source.url = "jqxGeneralServicer?hasrequest=Y&sname=JQEmplListTimeKeeping&partyGroupId=" + value + "&month=" + (month - 1) + "&year=" + year;
		$("#jqxgrid").jqxGrid('source', tmpS);
     });
	/*==============./end jqxTree dropdown define ================*/
	$("#updateNotification").jqxNotification({
        width: "100%", position: "top-left", opacity: 1, appendContainer: "#appendNotification",
        autoOpen: false, animationOpenDelay: 800, autoClose: false
    });
	
});

function initJqxDateTimeInput(){
	$("#month").jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
		digits: 2, spinMode: 'simple',  inputMode: 'simple', min: 1, max: 12});
	$("#year").jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
		digits: 4, spinMode: 'simple',  inputMode: 'simple'});
	$("#month").val(${currentMonth});
	$("#year").val(${currentYear});
}

function initContextMenu(){
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
				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.SelectCellBeforeDelete)}",
					[
						{
						    "label" : "${uiLabelMap.CommonSubmit}",
						    "class" : "btn-primary btn-mini icon-ok",
						    "callback": function() {
						    }
						}, 
					]
				);
			}
		}
	});
}

function setDropdownContent(element){
	var item = $("#jqxTree").jqxTree('getItem', element);
	var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + item.label + '</div>';
    $("#dropDownButton").jqxDropDownButton('setContent', dropDownContent);
}
</script>	

<div id="EmplTimeKeepingSearchPopup">
	<div id="windowHeader">
	</div>	
	<div id="windowContent">
	</div>
</div>	
<div class="row-fluid">
	<div id="appendNotification">
		<div id="updateNotification">
			<span id="notificationText"></span>
		</div>
	</div>	
</div>
<div id="contextMenu" class="hide">
	<ul>
		<li action="delete">${StringUtil.wrapString(uiLabelMap.Delete)}</li>
	</ul>
</div>
<style>     
#EmplTimeKeeping .green {
    color: black\9;
    background-color: #b6ff00\9;
}
#EmplTimeKeeping .yellow {
    color: black\9;
    background-color: yellow\9;
}
#EmplTimeKeeping .red {
    color: black\9;
    background-color: #FF6600;
}
#EmplTimeKeeping .green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
    color: black;
    background-color: #b6ff00;
}
#EmplTimeKeeping .yellow:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .yellow:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
    color: black;
    background-color: yellow;
}
#EmplTimeKeeping .red:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .red:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
    color: black;
    background-color: #e83636;
}
#EmplTimeKeeping .white{
	color: black;
    background-color: #ffffff;
}
</style>
