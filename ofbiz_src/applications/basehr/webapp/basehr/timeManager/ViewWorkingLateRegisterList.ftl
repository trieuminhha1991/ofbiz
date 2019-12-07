<#include "script/ViewWorkingLateRegisterListScript.ftl"/>

<#assign datafield = "[ {name: 'workingLateRegisterId', type: 'string'},
						{name: 'partyId', type: 'string'},
		                {name: 'partyCode', type: 'string'},
				      	{name: 'statusId', type: 'string'},
		                {name: 'firstName', type: 'string'},
		                {name: 'fullName', type: 'string'},
				      	{name: 'emplPositionTypeDesc', type: 'string'},
				      	{name: 'department', type: 'string'},
				      	{name: 'fromDate', type: 'date'},
				      	{name: 'thruDate', type: 'date'},
				      	{name: 'weekdayAppl', type: 'string'},
				      	{name: 'lateInStartShift', type: 'number'},
				      	{name: 'earlyOutShiftBreak', type: 'number'},
				      	{name: 'lateInShiftBreak', type: 'number'},
				      	{name: 'earlyOutEndShift', type: 'number'},
				      	{name: 'reasonRegister', type: 'string'},
				      	{name: 'reasonApproval', type: 'string'},
				      	{name: 'firstNameAppr', type: 'string'},
				      	{name: 'fullNameAppr', type: 'string'},
				      	{name: 'enumId', type: 'string'}
				      	]"/>
<script type="text/javascript">
<#assign columnlist = "
						{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId', editable: false, columntype: 'dropdownlist', width: '15%',
							filtertype: 'checkedlist',
							cellsrenderer: function(row, column, value){
								for(var i = 0; i < globalVar.statusListWorkingLateArr.length; i++){
									if(globalVar.statusListWorkingLateArr[i].statusId == value){
										return '<div style=\"margin-top: 4px; margin-left: 2px\">' + globalVar.statusListWorkingLateArr[i].description + '</div>';		
									}
								}
							},
							 createfilterwidget : function(column, columnfield, widget){
								   var source = {
										   localdata : globalVar.statusListWorkingLateArr,
										   datatype : 'array'
								   };
								   var filterBoxAdapter = new $.jqx.dataAdapter(source , {autoBind : true});
								   dataSoureList = filterBoxAdapter.records;
								   widget.jqxDropDownList({source: dataSoureList, displayMember: 'description', valueMember : 'statusId', autoDropDownHeight : true});
							   },
						},
					    {text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: '13%', cellsalign: 'left', editable: false},
					    {text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'firstName', width: '15%', cellsalign: 'left', editable: false,
					    	cellsrenderer: function(row, column, value){
					    		var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
					    		if(rowData && rowData.fullName){
					    			return '<span>' + rowData.fullName + '</span>';
					    		}
					    	}
					    },
					    {text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'department', width: '20%'},
						{text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionTypeDesc', width: '20%'},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', width: '13%', cellsalign: 'left', 
							editable: false, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', filterable : true, filterType : 'range'},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', width: '13%', cellsalign: 'left', 
							editable: false, columntype: 'datetimeinput', 
							cellsformat: 'dd/MM/yyyy', filterable : true, filterType : 'range'},
						{text: '${StringUtil.wrapString(uiLabelMap.HRCommonApplyFor)}', datafield: 'weekdayAppl', width: '20%',
								cellsrenderer: function(row, column, value){
									var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
						    		if(rowData){
						    			if(rowData.enumId == '_NA_'){
						    				return '<span>${StringUtil.wrapString(uiLabelMap.AllDayOfWeek)}</span>';
						    			}
						    			return '<span>' + value + '</span>';
						    		}
								}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.WorkingLateInStartShift)} (${StringUtil.wrapString(uiLabelMap.HRMinute)})', 
							datafield: 'lateInStartShift',width: '15%', columntype: 'numberinput', filtertype: 'nubmer', cellsalign: 'right'	
						},
						{text: '${StringUtil.wrapString(uiLabelMap.WorkingEarlyOutShiftBreak)} (${StringUtil.wrapString(uiLabelMap.HRMinute)})', 
							datafield: 'earlyOutShiftBreak', width: '15%', columntype: 'numberinput', filtertype: 'nubmer', cellsalign: 'right'	
						},
						{text: '${StringUtil.wrapString(uiLabelMap.WorkingLateIntShiftBreak)} (${StringUtil.wrapString(uiLabelMap.HRMinute)})', 
							datafield: 'lateInShiftBreak', width: '15%', columntype: 'numberinput', filtertype: 'nubmer', cellsalign: 'right'	
						},
						{text: '${StringUtil.wrapString(uiLabelMap.WorkingEarlyOutEndShift)} (${StringUtil.wrapString(uiLabelMap.HRMinute)})', 
							datafield: 'earlyOutEndShift', width: '15%', columntype: 'numberinput', filtertype: 'nubmer', cellsalign: 'right'	
						},
						{text: '${StringUtil.wrapString(uiLabelMap.ReasonRegisterWorkOvertime)}', datafield: 'reasonRegister', width: '25%'},
						{text: '${StringUtil.wrapString(uiLabelMap.HRCommonApprover)}', datafield: 'firstNameAppr', width: '15%',
							cellsrenderer: function(row, column, value){
		                		var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
		                		if(rowData && rowData.fullNameAppr){
		                			return '<span>' + rowData.fullNameAppr + '</span>';
		                		}
		                	}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.ReasonNotAppr)}', datafield: 'reasonReject', width: '25%'}
					"/>
</script>				     
<#if security.hasEntityPermission("HR_TIMEMGR", "_APPROVE", session)>
	<#assign mouseRightMenu="true"/>
	<#assign contextMenuId="contextMenu"/>
<#else>
	<#assign mouseRightMenu="false"/>
	<#assign contextMenuId=""/>	
</#if>			      	
<@jqGrid url="jqxGeneralServicer?sname=JQGetWorkingLateRegister" dataField=datafield columnlist=columnlist
		editable="true"  showlist="true" sortable="true"
		showtoolbar="true" deleterow="false" jqGridMinimumLibEnable="false"
		addrow="true" addType="popup" alternativeAddPopup="AddWorkLateRegisterWindow"
		updateUrl="jqxGeneralServicer?jqaction=U&sname=updateEmplWorkovertime" 
		editColumns="workOvertimeRegisId;statusId;actualStartTime;actualEndTime"
		mouseRightMenu=mouseRightMenu contextMenuId=contextMenuId
		/>
		
<#include "AddWorkingLateRegister.ftl"/>	
<#if security.hasEntityPermission("HR_TIMEMGR", "_APPROVE", session)>
	<div id="contextMenu" class="hide">
		<ul>
			<li action="approver">
				<i class="fa fa-paint-brush"></i>${uiLabelMap.HRApprove}
	        </li>
		</ul>
	</div>
	<#include "ApprWorkingLateRegister.ftl"/>
</#if>
	
 	