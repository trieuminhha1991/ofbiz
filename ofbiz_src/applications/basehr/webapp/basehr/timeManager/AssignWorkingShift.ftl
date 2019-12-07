<#include "script/AssignWorkingShiftScript.ftl"/>
<div class="row-fluid">
	<div id="container">
		<div id="jqxNotification">
			<div id="notificationContent"></div>
		</div>
	</div>
</div>
<#assign datafield ="[{name: 'partyId', type:'string'},
					  {name: 'partyCode', type:'string'},					  
					  {name: 'partyName', type:'string'},					  
					  {name: 'emplPositionTypeId', type: 'string'},
					  {name: 'orgId', type: 'string'},"/>
					  
<#assign columnlist = "{datafield: 'partyId', hidden: true},
					  {text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'partyName', width: 130, cellsalign: 'left', editable: false, pinned: true},
					   {text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: 100,cellsalign: 'left', pinned: true, editable: false},    					
    					{text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionTypeId', width: 180, editable: false, filterable: false},
    					{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'orgId', width: 180, editable: false, filterable: false},"/>


<#list dateOfMonthList as date>
	${cal.setTime(date)}
	<#assign dataFieldGroup = "" + Static["com.olbius.basehr.util.DateUtil"].getDateTowDigits(cal) + Static["com.olbius.basehr.util.DateUtil"].getMonthTowDigits(cal) + cal.get(CalendarYear)/>
	<#assign datafield = datafield + "{name: 'date_"+ dataFieldGroup +"', type: 'date'},"/>
	<#assign datafield = datafield + "{name: 'ws_"+ dataFieldGroup +"', type: 'string'},"/>
	
	<#assign columnlist = columnlist + "{datafield: 'ws_" + dataFieldGroup +"', text: '" + cal.get(CalendarDate) + "/" + (cal.get(CalendarMonth) + 1) + " - " + dayOfWeekNameList[cal.get(Static["java.util.Calendar"].DAY_OF_WEEK) - 1] + "',
											columntype: 'dropdownlist', width: 100, cellsalign: 'center',filterable: false, editable: true,
											cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
												var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
												var date = rowData['date_${dataFieldGroup}'];
												for(var i = 0; i < workingShiftArr.length; i++){
													if(workingShiftArr[i].workingShiftId == value){
														return '<span>' + workingShiftWorkType[workingShiftArr[i].workingShiftId][dayOfWeekKey[date.getDay()]] + '</span>'
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
											},
										 	initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
												if(cellvalue){
												 	editor.jqxDropDownList('val', cellvalue);
												 }										 		
										 	},
											cellbeginedit: function (rowindex, datafield, columntype) {
										        var data = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
										        if(data[datafield] == 'EXPIRE'){
										        	return false;
										        }
										    },
										    cellendedit: function(rowid, datafield, columntype, oldvalue, newvalue){        	
									        	var editFlag = viewAssignWorkingShiftObject.updateWorkingShiftEmployee(rowid, datafield, columntype, oldvalue, newvalue);
						        				return editFlag;
									        }											
										},"/>
</#list>
<#assign datafield = datafield + "]"/>  

<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.AssignWorkingShiftList}</h4>
		<div class="widget-toolbar none-content">
			<#if security.hasEntityPermission("HR_TIMEMGREXT", "_ADMIN", session)>
			<button class="grid-action-button" style="float: right;" id="assignWorkingShiftParty">
				<i class="fa-database">${uiLabelMap.AssignWorkingShiftByDepartment}</i>
			</button>
			<button class="grid-action-button" style="float: right;" id="assignWorkingShiftForEmpl">
				<i class="fa-users">${uiLabelMap.AssignWorkingShiftByEmployee}</i>
			</button>
			</#if>
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
						<div class="span12">
							<div id="jqxDropDownButton" style="margin-top: 5px;" class="pull-right">
								<div style="border: none;" id="jqxTree">
								</div>
							</div>
							<button id="filterbutton" class="grid-action-button pull-right"><i class="icon-filter"></i>${uiLabelMap.accRemoveFilter}</button>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="row-fluid">
			<#if security.hasEntityPermission("HR_TIMEMGREXT", "_ADMIN", session)>
				<#assign edit="true">
			<#else>
				<#assign edit="false">
			</#if>
			<@jqGrid url="" columnlist=columnlist dataField=datafield 
				filtersimplemode="true" editmode="selectedcell" sortable="false" showtoolbar="false" editable=edit id="jqxgrid" 
				clearfilteringbutton="true" columngrouplist=columngrouplist
				selectionmode="singlecell"
				jqGridMinimumLibEnable="false"
				/>  
		</div>
	</div>
</div>
<div id="assignWorkingWindow" class="hide">
	<div>${uiLabelMap.AssignWorkingShiftByDepartment}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">
						${uiLabelMap.OrganizationalUnit}
					</label>
				</div>
				<div class="span8">
					<input type="text" name="" id="partyAssignedWS">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">
						${uiLabelMap.CommonFromDate}
					</label>
				</div>
				<div class="span8">
					<div id="assignFromDate"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">
						${uiLabelMap.CommonThruDate}
					</label>
				</div>
				<div class="span8">
					<div id="assignThruDate"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
    		<button type="button" class='btn btn-danger form-action-button pull-right' id="alterCancel">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
    		<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSave">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
    	</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/timeManager/ViewAssignWorkingShift.js"></script>
<#if security.hasEntityPermission("HR_TIMEMGREXT", "_CREATE", session)>
<#include "AssignWorkingShiftForEmplList.ftl"/>
<script type="text/javascript" src="/hrresources/js/timeManager/AssignWorkingShift.js"></script>
</#if>