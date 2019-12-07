<#include "script/ViewEmplTimesheetListScript.ftl" />
<script type="text/javascript" src="/hrresources/js/CommonFunction.js"></script>
<#assign datafield = "[{name: 'emplTimesheetId', type: 'string'},
					   {name: 'emplTimesheetName', type: 'string'},
					   {name: 'partyId', type: 'string'},
					   {name: 'groupName', type: 'string'},
					   {name: 'fromDate', type: 'date'},
					   {name: 'thruDate', type: 'date'},
					   {name: 'statusId', type: 'string'}]"/>
					   
<#assign columnlist = "{text: '${uiLabelMap.EmplTimesheetId}', datafield: 'emplTimesheetId', width: 150, editable: false},
							{datafield: 'partyId', hidden: true},
							{text: '${uiLabelMap.EmplTimesheetName}', datafield: 'emplTimesheetName', width: 200},
							{text: '${StringUtil.wrapString(uiLabelMap.OrganizationalUnit)}', datafield: 'groupName', width: 250, filterable: false, editable: false},
							{text: '${uiLabelMap.fromDate}', datafield: 'fromDate',cellsformat: 'dd/MM/yyyy', filtertype:'range', columntype: 'datetimeinput', editable: false, width: 170},
							{text: '${uiLabelMap.thruDate}', datafield: 'thruDate',cellsformat: 'dd/MM/yyyy', filtertype:'range', columntype: 'datetimeinput' , editable: false, width: 170},
							{text: '${uiLabelMap.CommonStatus}', datafield: 'statusId', editable: false,filterType : 'checkedlist',
								cellsrenderer : function(row, column, value){
									for(var i = 0; i < statusArray.length; i++){
										if(value == statusArray[i].statusId){
											return '<div style=\"margin-top: 6px; margin-left: 4px;\">' +  statusArray[i].description + '</div>'; 
										}
									}
									return '<div style=\"margin-top: 6px; margin-left: 4px;\">' +  value + '</div>';
								},
								createfilterwidget : function(column, columnElement, widget){
									var source = {
											localdata : statusArray,
											datatype : 'array'
									};
									var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind : true});
									var dataFilter = filterBoxAdapter.records;
									//dataFilter.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
									widget.jqxDropDownList({source :dataFilter , valueMember : 'statusId', displayMember : 'description'});
									if(dataFilter.length <= 8){
										widget.jqxDropDownList({autoDropDownHeight : true});
									}else{
										widget.jqxDropDownList({autoDropDownHeight : false});
									}
								}
							}">

<div class="row-fluid" id="jqxNotifyEmplTimesheetContainer">
	<div id="jqxNotifyEmplTimesheets"></div>
</div>
<#if security.hasEntityPermission("HR_TIMESHEET", "_CREATE", session)>
	<#assign addrow = "true">
<#else>	
	<#assign addrow = "false">
</#if>
<#if security.hasEntityPermission("HR_TIMESHEET", "_DELETE", session)>
	<#assign deleterow = "true">
<#else>	
	<#assign deleterow = "false">
</#if>
<#assign customcontrol = "icon-filter open-sans@${uiLabelMap.HRCommonRemoveFilter}@javascript: void(0);@removeFilter()" >
<@jqGrid url="jqxGeneralServicer?sname=JQGetEmplTimesheets" dataField=datafield columnlist=columnlist
	editable="true" addrow=addrow addType="popup" deleterow=deleterow
	editrefresh ="true" showlist="false"
	editmode="click" id="jqxgrid" mouseRightMenu="true" contextMenuId="contextMenu"
	showtoolbar = "true"  jqGridMinimumLibEnable="false"
	removeUrl="jqxGeneralServicer?sname=deleteEmplTimesheet&jqaction=D" deleteColumn="emplTimesheetId" functionAfterRowComplete="functionAfterRowComplete()"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createEmplTimesheets" alternativeAddPopup="popupAddRow"  
	addColumns="emplTimesheetName;customTimePeriodId;importDataTimeRecord;partyId" addrefresh="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateEmplTimesheets" editColumns="emplTimesheetId;emplTimesheetName"
	customcontrol1 = customcontrol
/>

<@htmlTemplate.renderEmplWorkOverTime id="jqxgridEmplWorkOvertime" updaterow="false" jqxNotifyId="jqxNotifyEmplWorkOvertime" jqxGridInWindow="true" width="1005px" height="476"/> 
<@htmlTemplate.renderEmplWorkingLate id="jqxgridEmplWorkingLate" updaterow="false" height="97%" jqxNotifyId="jqxNotifyEmplWorkingLate" jqxGridInWindow="true"/> 

<div id="proposalApprvalTimesheet" style="display: none;">
	<div>${uiLabelMap.SendApprovalTimesheets}</div>
	<div class="row-fluid">
		<div class="span12">
			<input type="hidden" name="emplTimesheetId" id="emplTimesheetId">
			<span><b>${uiLabelMap.CannotEditDataAfterSend}. ${uiLabelMap.AreYouSureSent}</b></span>
		</div>
		<div class="span11" style="margin-top: 15px; text-align: center; margin-bottom: 0px;">
			<button class="btn btn-mini btn-primary icon-ok" id="sendTimesheet">${uiLabelMap.CommonSubmit}</button>
			<button class="btn btn-mini btn-danger icon-remove" id="cancelSendTimesheet">${uiLabelMap.CommonCancel}</button>
		</div>
	</div>
</div>
<div id="emplTimesheetAttendancePopup" class="hide">
	<div>${uiLabelMap.EmplTimesheetAttendance}</div>
	<div class='form-window-container'>
		<div class="row-fluid">
			<div class="span12">
				<div class="pull-right" style="margin-right: 5px">
					<div id="jqxDropDownButton">
						<div style="border: none;" id="jqxTree">
						</div>
					</div>
				</div>
				<div class="pull-right" style="margin-right: 5px">
					<button id="clearFilterBtnDetail" class="pull-right grid-action-button fa-filter open-sans">${uiLabelMap.accRemoveFilter}</button>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12">
					<div id="jqxTimesheetAtt"></div>
				</div>
			</div>
		</div>
		
	</div>
</div>

<div id="jqxWindowEmplTimesheetGeneral" class="hide">
	<div>${uiLabelMap.EmplTimekeepingReportTilte}</div>
	<div>
		<div class="row-fluid">
			<div class="span12">
				
				<div class="pull-right" style="margin-right: 5px">
					<div id="jqxDropDownButtonGeneral">
						<div style="border: none;" id="jqxTreeGeneral">
						</div>
					</div>
				</div>
				<div class="pull-right" style="margin-right: 5px">
					<button id="clearFilterBtnGeneral" class="pull-right grid-action-button fa-filter open-sans">${uiLabelMap.accRemoveFilter}</button>
				</div>
			</div>
		</div>
		<div class="row-fluid">	
			<div id="jqxEmplTimesheetGeneral"></div>
		</div>	
	</div>		
</div>

<div id="jqxWindowEmplTimesheetInDay" class="hide">
	<div>${uiLabelMap.EmplTimesheetInDay}</div>
	<div class='form-window-container'>
		<div class=''>
			<div class="row-fluid">
				<div id="notifyContainer">
					<div id="jqxNotificationTimesheetInDay">
						<div id="jqxNotificationTimesheetInDayContent"></div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="row-fluid">
					<div class="span12">
						<span><b>${uiLabelMap.EmployeeName}</b></span>: <span id="partyName"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span12">
						<div class="span5">
							<span><b>${uiLabelMap.HRCommonInTime}</b></span>: <span id="startTimeIn"></span>
						</div>
						<div class="span5">
							<span><b>${uiLabelMap.HRCommonOutTime}</b></span>: <span id="endTimeOut"></span>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid" id="jqxGridEmplTimekeepingSign"></div>
		</div>
	</div>
</div>

<div id='popupAddRow' class="hide">
	<div>${uiLabelMap.AddEmplTimesheet}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form name="popupAddRow" action="" class="form-horizontal">
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">
							${uiLabelMap.EmplTimesheetName}
						</label>
					</div>
					<div class="span7">
						<input type="text" name="emplTimesheetNameAdd" id="emplTimesheetNameAdd">
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
		   			<div class='span5 text-algin-right'>
						<label class="asterisk">
			   				${uiLabelMap.OrganizationalUnit}
			   			</label>		   			
		   			</div>
		   			<div class="span7">
		   				<div id="dropDownButtonAddNew" style="margin-top: 5px;">
							<div id="jqxTreeAddNew">
		   					</div>
						</div>	
		   			</div>
		   		</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${uiLabelMap.CommonTime}</label>
					</div>
					<div class="span7">
						<div class="row-fluid">
							<div class="span12">
								<div class="span5">
									<div id="monthCustomTime"></div>
								</div>
								<div class="span6">
									<div id="yearCustomTime"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label>${uiLabelMap.ImportDataFromTimeRecorder}</label>
					</div>
		   			<div class="span7">
		   				<div style="margin-left: 16px; margin-top: 4px">
			   				<div id="checkImportData"></div>
		   				</div>
		   			</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="alterCancel">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSave">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>


<div id='contextMenu' style="display: none">
    <ul>
        <li action="overallEmplTimesheet">
        	${uiLabelMap.EmplTimekeepingReportTilte}
        </li>
        <li action="timeRecorderDetails">
        	${uiLabelMap.EmplTimesheetAttendance}
        </li>
        <li action="emplWorkingLate">
        	${uiLabelMap.HREmplWorkingLateList}
        </li>
        <li action="approvalWorkOvertime">
        	${uiLabelMap.ListEmplWorkOvertime}
        </li>
        <!-- <li action="sendApprovalTimesheets">
        	${uiLabelMap.SendApprovalTimesheets}
        </li> -->
        <li action="refresh">
        	${uiLabelMap.RefreshEmplTimesheets}
        </li>          
   </ul>
</div>
<script type="text/javascript" src="/hrresources/js/timesheet/ViewEmplTimesheetList.js"></script>
<script type="text/javascript" src="/hrresources/js/timesheet/ViewListEmplTimesheetDetails.js"></script>
<script type="text/javascript" src="/hrresources/js/timesheet/EditEmplTimesheet.js"></script>
<script type="text/javascript" src="/hrresources/js/timesheet/ViewEmplTimesheetListActionMenu.js"></script>