<#include "script/ViewTimekeepingDetailPartyScript.ftl"/>

<#assign datafield = "[{name: 'partyId', type:'string'},
						{name: 'timekeepingDetailId', type: 'string'},
						{name: 'partyCode', type: 'string'},
						{name: 'fullName', type: 'string'},
						{name: 'firstName', type: 'string'},
						{name: 'emplPositionTypeDes', type: 'string'},
						{name: 'groupName', type: 'string'},
						{name: 'workdayStandard', type: 'number', other: 'Double'},
						{name: 'workdayActual', type: 'number', other: 'Double'},
						"/>
						
<script type="text/javascript">
<#assign dayOfWeekNameList = [uiLabelMap.CommonSundayShort, uiLabelMap.CommonMondayShort, uiLabelMap.CommonTuesdayShort, uiLabelMap.CommonWednesdayShort, uiLabelMap.CommonThursdayShort, uiLabelMap.CommonFridayShort, uiLabelMap.CommonSaturdayShort]/>
	<#assign columnlist = "{datafield: 'partyId', hidden: true},
						   {datafield: 'timekeepingDetailId', hidden: true},
						   {text: '#', sortable: false, filterable: false, editable: false, groupable: false, draggable: false, resizable: false,
		                      datafield: '', columntype: 'number', width: 30, pinned: true,
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
							"/>
	<#assign intervalInDays = Static["org.ofbiz.base.util.UtilDateTime"].getIntervalInDays(fromDate, thruDate)/>
	<#assign cal = Static["java.util.Calendar"].getInstance()/>
	<#assign CalendarDate =  Static["java.util.Calendar"].DATE/>   					
	<#assign CalendarMonth =  Static["java.util.Calendar"].MONTH/>   					
	<#assign CalendarYear =  Static["java.util.Calendar"].YEAR/>
	${cal.setTime(fromDate)}
	<#assign currentDate = cal.get(CalendarDate)/>
	<#list currentDate..(currentDate + intervalInDays) as index>
		${cal.set(CalendarDate, index)}
		<#assign dataFieldSuffix = cal.get(CalendarDate) + "/" +  (cal.get(CalendarMonth) + 1) + "/" + cal.get(CalendarYear)/>
		<#assign datafield = datafield + "{name: '"+ cal.getTimeInMillis() +"', type: 'number'},"/>
		<#assign columnlist = columnlist + "{text: '" + cal.get(CalendarDate) + "/" + (cal.get(CalendarMonth) + 1) + "-" + dayOfWeekNameList[cal.get(Static["java.util.Calendar"].DAY_OF_WEEK) - 1] 
												+ "', cellsalign: 'center', datafield: '" + cal.getTimeInMillis() + "', align: 'center', width: '8%', filterable: false, sortable: false, },"/>
	</#list>
	<#assign datafield = datafield + "]"/>		 		
</script>	
<@jqGrid filtersimplemode="false" filterable="true" showtoolbar="true" dataField=datafield columnlist=columnlist  
				clearfilteringbutton="true"  editable="false" deleterow="false" 
				addrow="false" showlist="false" sortable="true" mouseRightMenu="true" 
				contextMenuId="contextMenu" url="jqxGeneralServicer?sname=JQGetTimekeepingDetailParty&timekeepingDetailId=${parameters.timekeepingDetailId}"
				selectionmode="singlecell" 
				jqGridMinimumLibEnable="false"/>
				
<div id="contextMenu" class="hide">
	<ul>
		<li id="viewDetailDatekeeping">
			<i class="fa fa-file-image-o"></i>${uiLabelMap.ViewDetails}
        </li>
		<li id="reloadExcelData">
			<i class="fa fa-file-excel-o"></i>${uiLabelMap.ReloadData}
        </li>
		<li id="updateDataFromRelatedModule">
			<i class="fa fa-refresh"></i>${uiLabelMap.UpdateDataFromRelatedModule}
        </li>
	</ul>
</div>		
<div id="updateDataRelatedWindow" class="hide">
	<div>${uiLabelMap.DmsRefreshData}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span12'>
					<div class="span6">
						<div id="emplLeaveCheck" style="margin-left: 0px !important; margin-top: 5px"><span style="font-size: 14px">${uiLabelMap.HREmployeeLeave}</span></div>
					</div>
					<div class="span6">
						<div id="holidayCheck" style="margin-left: 0px !important; margin-top: 5px"><span style="font-size: 14px">${uiLabelMap.CommonHoliday}</span></div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4'>
					<label>${uiLabelMap.CommonFromDate}</label>
				</div>
				<div class="span8">
					<div id="fromDateTimekeeping"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4'>
					<label>${uiLabelMap.CommonThruDate}</label>
				</div>
				<div class="span8">
					<div id="thruDateTimekeeping"></div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingUpdateDataRelated" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerUpdateDataRelated"></div>
				</div>
			</div>
		</div>
		<div class='form-action'>
			<button type="button" class="btn btn-danger form-action-button pull-right icon-remove open-sans" id="cancelUpdateData">${uiLabelMap.CommonCancel}</button>
			<button type="button" class="btn btn-primary form-action-button pull-right icon-ok open-sans" id="saveUpdateData">${uiLabelMap.CommonSubmit}</button>
		</div>
	</div>	
</div>				

<#include "ViewTimekeepingDetailPartyDatekeeping.ftl"/>
<#include "ViewTimekeepingDetailPartyReloadData.ftl"/>
<script type="text/javascript" src="/hrresources/js/timesheet/ViewTimekeepingDetailParty.js"></script>	
<script type="text/javascript" src="/hrresources/js/timesheet/TimekeepingUpdateDataRelated.js"></script>	