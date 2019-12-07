<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<div class="row-fluid">
	<div id="container">
		<div id="jqxNotification">
			<div id="notificationContent"></div>
		</div>
	</div>
</div>
<script type="text/javascript">
var workingShiftArr = [
		<#if workingShiftList?has_content>
			<#list workingShiftList as workingShift>
			{
				workingShiftId: "${workingShift.workingShiftId}",
				workingShiftName: "${StringUtil.wrapString(workingShift.workingShiftName?if_exists)}"
			},	
			</#list>
		</#if>
];
</script>
<#include "/hrolbius/webapp/hrolbius/ftl/js/commonUtil.ftl"/>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>

<#assign dayOfWeekNameList = [uiLabelMap.CommonSundayShort, uiLabelMap.CommonMondayShort, uiLabelMap.CommonTuesdayShort, 
							  uiLabelMap.CommonWednesdayShort, uiLabelMap.CommonThursdayShort, 
							  uiLabelMap.CommonFridayShort, uiLabelMap.CommonSaturdayShort]/>

<#assign datafield ="[{name: 'partyId', type:'string'},
					  {name: 'partyName', type:'string'},					  
					  {name: 'emplPositionTypeId', type: 'string'},
					  {name: 'orgId', type: 'string'},"/>
					  
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'partyName', width: 130, cellsalign: 'left', editable: false, pinned: true},
					   {text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyId', width: 100,cellsalign: 'left', pinned: true, editable: false,
								cellsrenderer: function (row, column, value) {
									var data = $('#jqxgrid').jqxGrid('getrowdata', row)
									if (data && data.partyId){
        								return '<a style = \"margin-left: 10px\" href=' + 'EmployeeProfile?partyId=' + data.partyId + '>' +  data.partyId + '</a>'
    								}
    							}
    					},    					
    					{text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionTypeId', width: 180, editable: false},
    					{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'orgId', width: 180, editable: false},"/>

<#assign cal = Static["java.util.Calendar"].getInstance()/>
<#assign CalendarDate =  Static["java.util.Calendar"].DATE/>   					
<#assign CalendarMonth =  Static["java.util.Calendar"].MONTH/>   					
<#assign CalendarYear =  Static["java.util.Calendar"].YEAR/>
<#assign dateOfMonthList = Static["com.olbius.util.DateUtil"].createDateList(monthStart, monthEnd) />
<#list dateOfMonthList as date>
	${cal.setTime(date)}
	<#assign dataFieldGroup = "" + cal.get(CalendarDate) + (cal.get(CalendarMonth) + 1) + cal.get(CalendarYear)/>
	<#assign datafield = datafield + "{name: 'date_"+ dataFieldGroup +"', type: 'date'},"/>
	<#assign datafield = datafield + "{name: 'ws_"+ dataFieldGroup +"', type: 'string'},"/>
	
	<#assign columnlist = columnlist + "{datafield: 'date_"+ dataFieldGroup +"', hidden: true},"/>
	<#assign columnlist = columnlist + "{datafield: 'ws_" + dataFieldGroup +"', text: '" + cal.get(CalendarDate) + "/" + (cal.get(CalendarMonth) + 1) + " - " + dayOfWeekNameList[cal.get(Static["java.util.Calendar"].DAY_OF_WEEK) - 1] + "',
											columntype: 'dropdownlist', width: 100, cellsalign: 'center',filterable: false, editable: true,
											cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
												for(var i = 0; i < workingShiftArr.length; i++){
													if(workingShiftArr[i].workingShiftId == value){
														return '<span>' + workingShiftArr[i].workingShiftName + '</span>'
													}
												}
												if(value == 'EXPIRE'){
													return '<span>-------</span>';
												}else{
													return '<span>' + value + '</span>'
												}
											},
											createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight){												
												var source = {
														localdata: workingShiftArr,
										                datatype: 'array'
												};
												 var dataAdapter = new $.jqx.dataAdapter(source);
												 editor.jqxDropDownList({ source: dataAdapter,  displayMember: 'workingShiftName', valueMember: 'workingShiftId', 
													 width: cellwidth, height: cellheight, theme: 'olbius', autoDropDownHeight: true, dropDownWidth: 200,
													 renderer: function (index, label, value) {
									                    var datarecord = workingShiftArr[index];
									                    return datarecord.workingShiftName;
									                  }
												 });
												 if(cellvalue){
												 	editor.val(cellvalue);
												 }
											},
											cellbeginedit: function (rowindex, datafield, columntype) {
										        var data = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
										        if(data[datafield] == 'EXPIRE'){
										        	return false;
										        }
										    },
										    cellendedit: function(rowid, datafield, columntype, oldvalue, newvalue){        	
									        	var editFlag = updateWorkingShiftEmployee(rowid, datafield, columntype, oldvalue, newvalue);
						        				return editFlag;
									        }											
										},"/>
</#list>
<#assign datafield = datafield + "]"/>  

<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.AssignWorkingShiftList}</h4>
		<div class="widget-toolbar none-content">
			<button class="grid-action-button" style="float: right;" id="assignWorkingShiftParty">
				<i class="fa-database">${uiLabelMap.AssignWorkingShiftByDepartment}</i>
			</button>
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">	
					<div class='row-fluid margin-bottom10'>
						<div id="dateTimeInput"></div>
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class="span12" style="margin-right: 15px">
							<div id="jqxDropDownButton" style="margin-top: 5px;" class="pull-right">
								<div style="border: none;" id="jqxTree">
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<@jqGrid url="" columnlist=columnlist dataField=datafield 
				filtersimplemode="true" editmode="selectedcell" sortable="false" showtoolbar="false" editable="true" id="jqxgrid" 
				clearfilteringbutton="true" columngrouplist=columngrouplist
				selectionmode="singlecell"
				jqGridMinimumLibEnable="false"
				/>  
		</div>
	</div>
</div>
<script type="text/javascript">
$(document).ready(function () {
	initJqxDateTime();	
	initBtnEvent();
	initJqxNotification();
});
</script>
<#if expandedList?has_content>
	<#assign expandTreeId=expandedList[0]>
<#else>
	<#assign expandTreeId="">
</#if>
<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup expandTreeId=expandTreeId id="jqxTree" dropdownBtnId="jqxDropDownButton"
jqxTreeSelectFunc="jqxTreeSelectFunc" setDropdownContentJsFunc="setDropdownContent"/>

<script type="text/javascript">
function initJqxDateTime(){
	$("#dateTimeInput").jqxDateTimeInput({ width: 220, height: 25,  selectionMode: 'range', theme: 'olbius'});
	var fromDate = new Date(${monthStart.getTime()});
	var thruDate = new Date(${monthEnd.getTime()});
	$("#dateTimeInput").jqxDateTimeInput('setRange', fromDate, thruDate);
	$("#dateTimeInput").on('change', function(event){
		var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
		var fromDate = selection.from.getTime();
	    var thruDate = selection.to.getTime();
	    var item = $("#jqxTree").jqxTree('getSelectedItem');
	    var partyId = item.value;
	    updateJqxDataFieldColumn(selection.from, selection.to);
	    refreshGridData(partyId, fromDate, thruDate);
	});
}

function setDropdownContent(element){
	 var item = $("#jqxTree").jqxTree('getItem', element);
	 var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + item.label + '</div>';
    $("#jqxDropDownButton").jqxDropDownButton('setContent', dropDownContent);
}
function jqxTreeSelectFunc(event){
	var dataField = event.args.datafield;
	var rowBoundIndex = event.args.rowindex;
	var id = event.args.element.id;
	var item = $('#jqxTree').jqxTree('getItem', event.args.element);
	setDropdownContent(event.args.element);
	var tmpS = $("#jqxgrid").jqxGrid('source');
	var partyId = item.value;
	var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
   	var fromDate = selection.from.getTime();
   	var thruDate = selection.to.getTime(); 
   	refreshGridData(partyId, fromDate, thruDate);        
}

function updateJqxDataFieldColumn(fromDate, thruDate){
	var datafieldEdit = new Array();
	var columnlistEdit = new Array();
	datafieldEdit.push({name: "partyId", type: "string"});
	datafieldEdit.push({name: "partyName", type: "string"});
	datafieldEdit.push({name: "emplPositionTypeId", type: "string"});
	datafieldEdit.push({name: "orgId", type: "string"});
	columnlistEdit.push({text: '${uiLabelMap.EmployeeName}', datafield: 'partyName', width: 130, cellsalign: 'left', editable: false, pinned: true});
	columnlistEdit.push({text: '${uiLabelMap.EmployeeId}', datafield: 'partyId', cellsalign: 'left', editable: false, pinned: true,
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row)
							if (data && data.partyId){
								return '<a style = \"margin-left: 10px\" href=' + 'EmployeeProfile?partyId=' + data.partyId + '>' +  data.partyId + '</a>'
							}
						}
					});
	
	columnlistEdit.push({text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionTypeId', width: 180, editable: false});
	columnlistEdit.push({text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'orgId', width: 180, editable: false});
	while(fromDate.getTime() < thruDate.getTime()){
		var column = fromDate.getDate().toString + (fromDate.getMonth() + 1) + fromDate.getFullYear().toString;
		var columnText = fromDate.getDate() + "/" + (fromDate.getMonth() + 1) + " - " + weekday[fromDate.getDay()];
		var dateVal = fromDate.getTime();
		datafieldEdit.push({name: "date_" + column, type: "date"});
		datafieldEdit.push({name: "ws_"+ column, type: "string"});
		columnlistEdit.push({datafield: "date_"+ column, hidden: true});
		columnlistEdit.push({datafield: 'ws_' + column, text: columnText,
								columntype: 'dropdownlist', width: 100, cellsalign: 'center',filterable: false,
								cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
									for(var i = 0; i < workingShiftArr.length; i++){
										if(workingShiftArr[i].workingShiftId == value){
											return '<span>' + workingShiftArr[i].workingShiftName + '</span>'
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
	source._source.dataFields = datafieldEdit;
	$("#jqxgrid").jqxGrid('source', source);
}

function updateWorkingShiftEmployee(rowid, datafield, columntype, oldvalue, newvalue){
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
    		$("#jqxgrid").jqxGrid({disabled: false});
    		$('#jqxgrid').jqxGrid('clearselection');
    	}
	});
	return commit;
}

function refreshGridData(partyId, fromDate, thruDate){
	var tmpS = $("#jqxgrid").jqxGrid('source');
	tmpS._source.url = "jqxGeneralServicer?sname=getWorkingShiftEmployee&hasrequest=Y&partyGroupId=" + partyId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
	$("#jqxgrid").jqxGrid('source', tmpS);
}

function initJqxNotification(){
	$("#jqxNotification").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#container"});
}

function initBtnEvent(){
	$("#assignWorkingShiftParty").click(function(event){
		var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
		var fromDate = selection.from.getTime();
	    var thruDate = selection.to.getTime();
	    var item = $('#jqxTree').jqxTree('getSelectedItem');
		var partyId = item.value;
	    $("#jqxgrid").jqxGrid('showloadelement');
	    $("#jqxgrid").jqxGrid({disabled: true});
	    $.ajax({
	    	url: 'assignWorkingShiftForParty',
	    	data: {fromDate: fromDate, thruDate: thruDate, partyId: partyId},
	    	type: 'POST',
	    	success: function(response){
	    		$("#jqxNotification").jqxNotification('closeLast');
				if(response.responseMessage == 'success'){
					$("#notificationContent").text(response.successMessage);
					$("#jqxNotification").jqxNotification({template: 'info'});
					$("#jqxNotification").jqxNotification("open");
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					$("#notificationContent").text(response.errorMessage);
					$("#jqxNotification").jqxNotification({template: 'error'});
					$("#jqxNotification").jqxNotification("open");
				}
	    	},
	    	complete:  function(jqXHR, textStatus){
				$("#jqxgrid").jqxGrid({disabled: false});
				$("#jqxgrid").jqxGrid('hideloadelement');
	    	}
	    });
	});
}
</script>
