<#include "script/ViewTimekeepingSummaryPartyScript.ftl"/>
<#assign datafield = "[{name: 'partyId', type:'string'},
						{name: 'timekeepingSummaryId', type: 'string'},
						{name: 'partyCode', type: 'string'},
						{name: 'fullName', type: 'string'},
						{name: 'firstName', type: 'string'},
						{name: 'emplPositionTypeDes', type: 'string'},
						{name: 'groupName', type: 'string'},
						{name: 'workdayStandard', type: 'number', other: 'Double'},
						{name: 'workdayActual', type: 'number', other: 'Double'},
						{name: 'totalWorkdayPaid', type: 'number', other: 'Double'},
						{name: 'overtimeHoursNormal', type: 'number', other: 'Double'},
						{name: 'overtimeHoursWeekend', type: 'number', other: 'Double'},
						{name: 'overtimeHoursHoliday', type: 'number', other: 'Double'},
						{name: 'totalMinuteLate', type: 'number', other: 'Long'},
						{name: 'totalWorkLate', type: 'number', other: 'Long'}]
						"/>
						
<script type="text/javascript">
<#assign columnlist = "{datafield: 'partyId', hidden: true},
						{datafield: 'timekeepingSummaryId', hidden: true},
						{text: '#', sortable: false, filterable: false, editable: false, groupable: false, draggable: false, resizable: false,
						   datafield: '', columntype: 'number', width: 45, pinned: true,
						   cellsrenderer: function (row, column, value) {
						       return \"<div style='margin:4px;'>\" + (value + 1) + \"</div>\";
						   }   
						},
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: '11%', editable: false, pinned: true, },
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'firstName', width: '15%', editable: false, pinned: true,
							   cellsrenderer: function (row, column, value) {
								   var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
								   if(rowData){
									   return '<span>' + rowData.fullName + '</span>'; 
								   }
							   }
						   },
						{text: '${StringUtil.wrapString(uiLabelMap.HRCommonJobTitle)}', datafield: 'emplPositionTypeDes', width: '16%', editable: false, pinned: false},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', width: '15%'},
						{text: '${StringUtil.wrapString(uiLabelMap.WorkdayStandard)}', datafield: 'workdayStandard', width: '10%', columntype: 'numberinput', cellsalign: 'center', filterType: 'number'},
						{text: '${StringUtil.wrapString(uiLabelMap.ActualWorkday)}', datafield: 'workdayActual', width: '10%', columntype: 'numberinput', cellsalign: 'center', filterType: 'number'},
						{text: '${StringUtil.wrapString(uiLabelMap.PaidWorkday)}', datafield: 'totalWorkdayPaid', width: '10%', columntype: 'numberinput', cellsalign: 'center', filterType: 'number'},
						{text: '${StringUtil.wrapString(uiLabelMap.TotalNormalOvertime)}', datafield: 'overtimeHoursNormal', width: '17%', columntype: 'numberinput', cellsalign: 'center', filterType: 'number'},
						{text: '${StringUtil.wrapString(uiLabelMap.TotalWeekendOvertime)}', datafield: 'overtimeHoursWeekend', width: '15%', columntype: 'numberinput', cellsalign: 'center', filterType: 'number'},
						{text: '${StringUtil.wrapString(uiLabelMap.TotalHolidayOvertime)}', datafield: 'overtimeHoursHoliday', width: '16%', columntype: 'numberinput', cellsalign: 'center', filterType: 'number'},
						{text: '${StringUtil.wrapString(uiLabelMap.TotalMinutesWorkLate)}', datafield: 'totalMinuteLate', width: '12%', columntype: 'numberinput', cellsalign: 'center', filterType: 'number'},
						{text: '${StringUtil.wrapString(uiLabelMap.TotalTimesWorkLate)}', datafield: 'totalWorkLate', width: '12%', columntype: 'numberinput', cellsalign: 'center', filterType: 'number'},
							"/>
</script>			

<@jqGrid filtersimplemode="false" filterable="true" showtoolbar="true" dataField=datafield columnlist=columnlist  
				clearfilteringbutton="true"  editable="false" deleterow="false" selectionmode="singlerow"
				addrow="false" showlist="false" sortable="true" mouseRightMenu="true" 
				contextMenuId="contextMenu" url="jqxGeneralServicer?sname=JQGetTimekeepingSummaryParty&timekeepingSummaryId=${parameters.timekeepingSummaryId?if_exists}"
				jqGridMinimumLibEnable="false"/>	
						
<div id="contextMenu" class="hide">
	<ul>
		<li action="viewDetail">
			<i class="fa fa-eye"></i>${uiLabelMap.ViewDetails}
        </li>
		<li action="refreshData">
			<i class="fa fa-refresh"></i>${uiLabelMap.DmsRefreshData}
        </li>
	</ul>
</div>	
<div id="timekeepingSummaryPtyDetailWindow" class="hide">
	<div>${uiLabelMap.TimekeepingSummaryPartyDetail}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="summaryPtyDetailGrid"></div>
		</div>
		<div class='form-action'>
			<button type="button" class="btn btn-danger form-action-button pull-right icon-remove open-sans" id="cancelSummaryPtyDetail">${uiLabelMap.CommonClose}</button>
			<button type="button" class="btn btn-primary form-action-button pull-right icon-ok open-sans" id="saveSummaryPtyDetail">${uiLabelMap.CommonUpdate}</button>
		</div>
	</div>	
</div>			
<script type="text/javascript" src="/hrresources/js/timesheet/ViewTimekeepingSummaryParty.js?v=1.0.0"></script>