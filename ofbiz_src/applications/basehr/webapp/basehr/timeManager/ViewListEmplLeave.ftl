<style type="text/css">
.aquaCell{
	background-color: #a2f0a8 !important;
}
.redCell{
	background-color: #ffd4d4 !important;
}
.grayCell{
	background-color: #daddd5 !important;
}
</style>
<#include "script/ViewListEmplLeaveScript.ftl"/>

<#assign datafield = "[
	{name: 'emplLeaveId', type: 'string'},
	{name: 'partyId', type: 'string'},
	{name: 'partyCode', type: 'string'},
	{name: 'fullName', type: 'string'},
	{name: 'emplPositionTypeDesc', type: 'string'},
	{name: 'groupName', type: 'string'},
	{name: 'statusId', type: 'string'},
	{name: 'fromDate', type: 'date', other : 'Timestamp'},
	{name: 'thruDate', type: 'date', other : 'Timestamp'},
	{name: 'dateApplication', type: 'date', other : 'Timestamp'},	
	{name: 'nbrDayLeave', type: 'number'},	
	{name: 'emplLeaveReasonTypeId', type: 'string'},
	{name: 'workingShiftId', type: 'string'},
	{name: 'fromDateLeaveTypeId', type: 'string'},
	{name: 'thruDateLeaveTypeId', type: 'string'},
	{name: 'description', type: 'string'},
	{name: 'commentApproval', type: 'string'},
]"/>

<script type="text/javascript">
<#assign columnlist = "
			{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId', editable: false, width: '11%', filterType : 'checkedlist',
				cellsrenderer: function(row, column, value){
					for(var i = 0; i < globalVar.statusArr.length; i++){
						if(globalVar.statusArr[i].statusId == value){
							return '<span title=' + value + '>' + globalVar.statusArr[i].description + '</span>';		
						}
					}
					return '<span>' + value + '</span>';
				},
				createfilterwidget : function(column, columnElement, widget){
					var source = {
							localdata : globalVar.statusArr,
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
				},
				cellclassname: function (row, column, value, data) {
				    var statusId = data.statusId;
				    if(statusId == 'LEAVE_APPROVED'){
				    	return 'aquaCell';
				    }else if(statusId == 'LEAVE_CANCEL'){
				    	return 'grayCell';
				    }else if(statusId == 'LEAVE_REJECTED'){
				    	return 'redCell';
				    }
				}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: '12%', editable: false,
				cellclassname: function (row, column, value, data) {
				    var statusId = data.statusId;
				    if(statusId == 'LEAVE_APPROVED'){
				    	return 'aquaCell';
				    }else if(statusId == 'LEAVE_CANCEL'){
				    	return 'grayCell';
				    }else if(statusId == 'LEAVE_REJECTED'){
				    	return 'redCell';
				    }
				}	
			},
			{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'fullName', width: '15%', editable: false,
				cellclassname: function (row, column, value, data) {
				    var statusId = data.statusId;
				    if(statusId == 'LEAVE_APPROVED'){
				    	return 'aquaCell';
				    }else if(statusId == 'LEAVE_CANCEL'){
				    	return 'grayCell';
				    }else if(statusId == 'LEAVE_REJECTED'){
				    	return 'redCell';
				    }
				}	
			},
			{text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionTypeDesc', width: '18%', editable: false, sortable: false,
				cellclassname: function (row, column, value, data) {
				    var statusId = data.statusId;
				    if(statusId == 'LEAVE_APPROVED'){
				    	return 'aquaCell';
				    }else if(statusId == 'LEAVE_CANCEL'){
				    	return 'grayCell';
				    }else if(statusId == 'LEAVE_REJECTED'){
				    	return 'redCell';
				    }
				}	
			},
			{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', width: '18%', editable: false, sortable: false,
				cellclassname: function (row, column, value, data) {
				    var statusId = data.statusId;
				    if(statusId == 'LEAVE_APPROVED'){
				    	return 'aquaCell';
				    }else if(statusId == 'LEAVE_CANCEL'){
				    	return 'grayCell';
				    }else if(statusId == 'LEAVE_REJECTED'){
				    	return 'redCell';
				    }
				}	
			},
			{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', columntype: 'datetimeinput', 
				cellsformat: 'dd/MM/yyyy HH:mm:ss', width: '15%', filterType : 'range',
				cellclassname: function (row, column, value, data) {
				    var statusId = data.statusId;
				    if(statusId == 'LEAVE_APPROVED'){
				    	return 'aquaCell';
				    }else if(statusId == 'LEAVE_CANCEL'){
				    	return 'grayCell';
				    }else if(statusId == 'LEAVE_REJECTED'){
				    	return 'redCell';
				    }
				}	
			},
			{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', columntype: 'datetimeinput', 
				cellsformat: 'dd/MM/yyyy HH:mm:ss', width: '15%', filterType : 'range',
				cellclassname: function (row, column, value, data) {
				    var statusId = data.statusId;
				    if(statusId == 'LEAVE_APPROVED'){
				    	return 'aquaCell';
				    }else if(statusId == 'LEAVE_CANCEL'){
				    	return 'grayCell';
				    }else if(statusId == 'LEAVE_REJECTED'){
				    	return 'redCell';
				    }
				}	
			},
			{text: '${StringUtil.wrapString(uiLabelMap.HRNumberDayLeave)}', datafield: 'nbrDayLeave', columntype: 'numberinput', width: '8%', filterType : 'number',
				cellclassname: function (row, column, value, data) {
				    var statusId = data.statusId;
				    if(statusId == 'LEAVE_APPROVED'){
				    	return 'aquaCell';
				    }else if(statusId == 'LEAVE_CANCEL'){
				    	return 'grayCell';
				    }else if(statusId == 'LEAVE_REJECTED'){
				    	return 'redCell';
				    }
				}	
			},
			{text: '${StringUtil.wrapString(uiLabelMap.ApplicationDate)}', datafield: 'dateApplication', columntype: 'datetimeinput', 
				cellsformat: 'dd/MM/yyyy', width: '11%', filterType : 'range',
				cellclassname: function (row, column, value, data) {
				    var statusId = data.statusId;
				    if(statusId == 'LEAVE_APPROVED'){
				    	return 'aquaCell';
				    }else if(statusId == 'LEAVE_CANCEL'){
				    	return 'grayCell';
				    }else if(statusId == 'LEAVE_REJECTED'){
				    	return 'redCell';
				    }
				}	
			},							
			{text: '${StringUtil.wrapString(uiLabelMap.CommonReason)}', datafield: 'emplLeaveReasonTypeId', width: '20%',filterType : 'checkedlist',
				cellsrenderer: function(row, column, value){
					for(var i = 0; i < globalVar.emplLeaveReasonArr.length; i++){
						if(globalVar.emplLeaveReasonArr[i].emplLeaveReasonTypeId == value){
							return '<span title=' + value + '>' + globalVar.emplLeaveReasonArr[i].description + '</span>';		
						}
					}
					return '<span>' + value + '</span>';
				},
				createfilterwidget : function(column, columnElement, widget){
					var source = {
							localdata : globalVar.emplLeaveReasonArr,
							datatype : 'array'
					};
					var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind : true});
					var dataFilter = filterBoxAdapter.records;
					//dataFilter.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
					widget.jqxDropDownList({source :dataFilter , valueMember : 'emplLeaveReasonTypeId', displayMember : 'description'});
					if(dataFilter.length <= 8){
						widget.jqxDropDownList({autoDropDownHeight : true});
					}else{
						widget.jqxDropDownList({autoDropDownHeight : false});
					}
				},
				cellclassname: function (row, column, value, data) {
				    var statusId = data.statusId;
				    if(statusId == 'LEAVE_APPROVED'){
				    	return 'aquaCell';
				    }else if(statusId == 'LEAVE_CANCEL'){
				    	return 'grayCell';
				    }else if(statusId == 'LEAVE_REJECTED'){
				    	return 'redCell';
				    }
				}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.NoteOfApprove)}', datafield: 'commentApproval', width: '20%',
				 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
					 return defaulthtml;
				 },
				 cellclassname: function (row, column, value, data) {
					    var statusId = data.statusId;
					    if(statusId == 'LEAVE_APPROVED'){
					    	return 'aquaCell';
					    }else if(statusId == 'LEAVE_CANCEL'){
					    	return 'grayCell';
					    }else if(statusId == 'LEAVE_REJECTED'){
					    	return 'redCell';
					    }
					}
			},
"/>
</script>
<div class="row-fluid">
	<div id="notifyContainer">
		<div id="jqxNtf">
			<div id="jqxNtfContent"></div>
		</div>
	</div>
</div>	

<div class="widget-box transparent no-bottom-border">
	<div class="widget-header" style="min-height: 30px">
		<h4>${uiLabelMap.HRLeaveManagement}</h4>
		<div class="widget-toolbar none-content">
			<div id="yearNumberInput"></div>
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class="span12" style="margin-right: 15px">
							<div id="dropDownButton" style="margin-top: 5px;" class="">
								<div style="border: none;" id="jqxTree">
										
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<div class="span12" style="margin-right: 15px">
							<button id="removeFilter" class="grid-action-button icon-filter open-sans pull-right">${uiLabelMap.HRCommonRemoveFilter}</button>
							<button id="updateFromTimeTracker" class="grid-action-button fa-clock-o open-sans pull-right">${uiLabelMap.UpdateLeaveActualFromTimeTracker}</button>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<@jqGrid addrow="false" isShowTitleProperty="false" id="jqxgrid" filtersimplemode="true" showtoolbar="false" clearfilteringbutton="false"
					 deleterow="false" editable="false" dataField=datafield columnlist=columnlist
					 url="" filterable="true" showlist="false" autorowheight="true"
					 createUrl="" width="100%" bindresize="false"
					 addColumns="" sortable="false"
					 removeUrl="" deleteColumn=""
					 mouseRightMenu="true" contextMenuId="contextMenu"
					 updateUrl="" jqGridMinimumLibEnable="false"
					 editColumns=""
				/>
		</div>
	</div>
</div>
<div id='contextMenu' class="hide">
	<ul>
		<li action="approver" id="approver">
			<i class="fa fa-paint-brush"></i>${uiLabelMap.HRApprove}
        </li>
	</ul>
</div>
<#include "ApprovalEmplLeave.ftl"/>
<div id="updateLeaveTimeTrackerWindow" class="hide">
	<div>${uiLabelMap.UpdateEmplLeaveActualByTimeTracker}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label>${uiLabelMap.UpdateFromDate}</label>
				</div>
				<div class="span8">
					<div id="fromDateUpdateTimeTracker"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label>${uiLabelMap.CommonThruDate}</label>
				</div>
				<div class="span8">
					<div id="thruDateUpdateTimeTracker"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="btnCancelUpdate" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSaveUpdate">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>		
</div>
<script type="text/javascript" src="/hrresources/js/timeManager/ViewListEmplLeave.js"></script>