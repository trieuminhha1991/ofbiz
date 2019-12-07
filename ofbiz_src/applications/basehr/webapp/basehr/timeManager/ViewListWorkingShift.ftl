<#include "script/ViewListWorkingShiftScript.ftl"/>
<#assign datafield = "[{name: 'workingShiftId', type: 'string'},
						{name: 'workingShiftName', type: 'string'},
						{name: 'shiftStartTime', type: 'date'},
						{name: 'shiftBreakStart', type: 'date'},
						{name: 'shiftBreakEnd', type: 'date'},
						{name: 'shiftEndTime', type: 'date'},
						{name: 'startOverTimeAfterShift', type: 'date'},
						{name: 'endOverTimeAfterShift', type: 'date'},
						{name: 'allowLateMinute', type: 'number'},
						{name: 'isAllowOTAfterShift', type: 'string'},
						{name: 'minMinuteOvertime', type: 'number'}]"/>
						
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.WorkingShiftId)}', datafield: 'workingShiftId', width: 120,
							cellsrenderer : function(row, column, value){
								return '<span><a href=\"javascript:void(0)\" onclick=\"viewListWorkingShiftObject.showRowGridDetail(' + row + ')\">' + value + '</a></span>';
							}
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.WorkingShiftName)}', datafield: 'workingShiftName', width: 180},
					   {text: '${StringUtil.wrapString(uiLabelMap.WorkingShiftStart)}', datafield: 'shiftStartTime', width: 130, filterable : false,
						   cellsformat: 'HH:mm', columntype: 'datetimeinput'
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.WorkingShiftBreakStart)}', datafield: 'shiftBreakStart', width: 190, filterable : false,
						   cellsformat: 'HH:mm', columntype: 'datetimeinput'
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.WorkingShiftBreakEnd)}', datafield: 'shiftBreakEnd', width: 190, filterable : false,
						   cellsformat: 'HH:mm', columntype: 'datetimeinput'
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.WorkingShiftEnd)}', datafield: 'shiftEndTime', filterable : false,
						   cellsformat: 'HH:mm', columntype: 'datetimeinput'
					   },
					   {datafield: 'minMinuteOvertime', hidden: true},
					   {datafield: 'allowLateMinute', hidden: true},
					   {datafield: 'isAllowOTAfterShift', hidden: true}
					   "/>

</script>
<div id="containerNtf">
</div>
<div id="jqxNotification">
	<div id="notificationContent"></div>
</div>	
<#if security.hasEntityPermission("HR_TIMEMGR", "_ADMIN", session)>
	<#assign addrow="true">
<#else>
	<#assign addrow="false">
</#if>
<#assign customcontrol = "icon-filter open-sans@${uiLabelMap.HRCommonRemoveFilter}@javascript: void(0);@RemoveFilter()">
<@jqGrid url="jqxGeneralServicer?sname=JQgetListWorkingShift&hasrequest=Y" dataField=datafield columnlist=columnlist
	clearfilteringbutton="true"
	editable="false"
	filterable="true" 
	jqGridMinimumLibEnable="false" 
	deleterow="false"
	autorowheight="true" jqGridMinimumLibEnable="false" sortable="false"
	showtoolbar = "true" filterable="true" clearfilteringbutton="false" showlist="false"
	updateUrl="" editColumns=""
	customcontrol1="icon-cog@${uiLabelMap.ConfigPartyWorkingShift}@#javascript:void(0)@configPartyWorkingShift()"
	customcontrol2=customcontrol
	removeUrl="jqxGeneralServicer?sname=&jqaction=D" deleteColumn=""
	createUrl="jqxGeneralServicer?sname=&jqaction=C" alternativeAddPopup="popupAddRow" addrow=addrow addType="popup" 
	addColumns=""/>	
	
<#if security.hasEntityPermission("HR_TIMEMGR", "_ADMIN", session)>
	<#include "CreateWorkingShift.ftl" />
</#if>

<div id="workingShiftEditWindow" class="hide">
	<div></div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="asterisk">${uiLabelMap.WorkingShiftIdFull}</label>
							</div>
							<div class="span7">
								<input type="text" id="workingShiftId">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="asterisk">${uiLabelMap.WorkingShiftNameFull}</label>
							</div>
							<div class="span7">
								<input type="text" id="workingShiftName">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.MinuteAllowWorkLate}</label>
							</div>
							<div class="span7">
								<div id="allowLateMinute"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.MinimumMinuteCalcOT}</label>
							</div>
							<div class="span7">
								<div id="minMinuteOvertime"></div>
							</div>
						</div>
						
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.AllowWorkingOvertimeAfterShift}</label>
							</div>
							<div class="span7">
								<div style="margin: 5px 0 0 16px">
									<div id="allowOTAfterShiftEdit"></div>
								</div>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="">${uiLabelMap.WorkingShiftStart}</label>
							</div>
							<div class="span5">
								<div id="shiftStartTime"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="">${uiLabelMap.WorkingShiftBreakStart}</label>
							</div>
							<div class="span5">
								<div id="shiftBreakStartTime"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label>${uiLabelMap.WorkingShiftBreakEnd}</label>
							</div>
							<div class="span5">
								<div id="shiftBreakEndTime"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="">${uiLabelMap.WorkingShiftEnd}</label>
							</div>
							<div class="span5">
								<div id="shiftEndTime"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="">${uiLabelMap.StartOverTimeAfterShift}</label>
							</div>
							<div class="span5">
								<div id="startOverTimeAfterShift"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="">${uiLabelMap.EndOverTimeAfterShift}</label>
							</div>
							<div class="span5">
								<div id="endOverTimeAfterShift"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<hr/>
			</div>
			<div class="row-fluid">
				<div id="containerjqxgridShiftWorkType">
				</div>
				<div id="jqxNotificationjqxgridShiftWorkType">
					<div id="notificationContentjqxgridShiftWorkType"></div>
				</div>
				<div id="jqxgridShiftWorkType"></div>
			</div>
		</div>
		<#if security.hasEntityPermission("HR_TIMEMGR", "_ADMIN", session)>
			<div class="form-action">
				<button id="btnCancel" class='btn btn-danger form-action-button pull-right'>
					<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSave">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</#if>
	</div>
</div>	
<#include "configPartyWorkingShift.ftl"/>
<script type="text/javascript" src="/hrresources/js/timeManager/ViewListWorkingShift.js"></script>
