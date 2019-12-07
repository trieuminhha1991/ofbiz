<#include "script/ViewWorkingOvertimeRegisListScript.ftl"/>
<#assign datafield = "[{name: 'workOvertimeRegisId', type: 'string'},
						{name: 'partyId', type: 'string'},
		                {name: 'partyCode', type: 'string'},
				      	{name: 'statusId', type: 'string'},
		                {name: 'firstName', type: 'string'},
		                {name: 'fullName', type: 'string'},
				      	{name: 'emplPositionTypeDesc', type: 'string'},
				      	{name: 'department', type: 'string'},
				      	{name: 'dateRegistered', type: 'date'},
				      	{name: 'fromDate', type: 'date'},
				      	{name: 'thruDate', type: 'date'},
				      	{name: 'startTime', type: 'date'},
				      	{name: 'endTime', type: 'date'},
				      	{name: 'reasonRegister', type: 'string'},
				      	{name: 'reasonApproval', type: 'string'},
				      	{name: 'firstNameAppr', type: 'string'},
				      	{name: 'fullNameAppr', type: 'string'}
				      	]"/>
<script type="text/javascript">
<#assign columnlist = "
					{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId', editable: false, columntype: 'dropdownlist', width: '15%',
						filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < globalVar.statusWorkOvertimeArr.length; i++){
								if(globalVar.statusWorkOvertimeArr[i].statusId == value){
									return '<div style=\"margin-top: 4px; margin-left: 2px\">' + globalVar.statusWorkOvertimeArr[i].description + '</div>';		
								}
							}
						},
						 createfilterwidget : function(column, columnfield, widget){
							   var source = {
									   localdata : globalVar.statusWorkOvertimeArr,
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
					{text: '${StringUtil.wrapString(uiLabelMap.HREmplOvertimeDateRegis)}', datafield: 'dateRegistered', width: '13%', 
							cellsalign: 'left', editable: false, columntype: 'datetimeinput', 
							cellsformat: 'dd/MM/yyyy', filterable : true, filterType : 'range'},
					{text: '${StringUtil.wrapString(uiLabelMap.CommonBeginingFromDate)}', datafield: 'fromDate', width: '13%', cellsalign: 'left', 
							editable: false, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', filterable : true, filterType : 'range'},
					{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', width: '13%', cellsalign: 'left', 
							editable: false, columntype: 'datetimeinput', 
							cellsformat: 'dd/MM/yyyy', filterable : true, filterType : 'range'},
					{text: '${StringUtil.wrapString(uiLabelMap.WorkingOvertimeStartTime)}', datafield: 'startTime', width: '13%',
								cellsformat: 'HH:mm:ss', editable: false, filterable: false},
					{text: '${StringUtil.wrapString(uiLabelMap.WorkingOvertimeEndTime)}', datafield: 'endTime', width: '13%', 
									cellsformat: 'HH:mm:ss', editable: false, filterable: false},
					{text: '${StringUtil.wrapString(uiLabelMap.WorkingOvertimeReason)}', datafield: 'reasonRegister', width: '25%'},
					{text: '${StringUtil.wrapString(uiLabelMap.HRCommonApprover)}', datafield: 'firstNameAppr', width: '15%',
						cellsrenderer: function(row, column, value){
	                		var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
	                		if(rowData && rowData.fullNameAppr){
	                			return '<span>' + rowData.fullNameAppr + '</span>';
	                		}
	                	}
					},
					{text: '${StringUtil.wrapString(uiLabelMap.ReasonNotAppr)}', datafield: 'reasonApproval', width: '25%'}
					"/> 	
</script>	
<#if security.hasEntityPermission("HR_TIMEMGR", "_APPROVE", session)>
	<#assign mouseRightMenu="true"/>
	<#assign contextMenuId="contextMenu"/>
<#else>
	<#assign mouseRightMenu="false"/>
	<#assign contextMenuId=""/>	
</#if>			      	
<@jqGrid url="jqxGeneralServicer?sname=JQGetWorkOvertimeRegister" dataField=datafield columnlist=columnlist
		editable="true"  showlist="true" sortable="true"
		showtoolbar="true" deleterow="false" jqGridMinimumLibEnable="false"
		addrow="true" addType="popup" alternativeAddPopup="AddWorkOTRegisterWindow"
		updateUrl="jqxGeneralServicer?jqaction=U&sname=updateEmplWorkovertime" 
		editColumns="workOvertimeRegisId;statusId;actualStartTime;actualEndTime"
		mouseRightMenu=mouseRightMenu contextMenuId=contextMenuId
		/>
${setContextField("addNewWindow", "AddWorkOTRegisterWindow")}
<#include "AddWorkingOvertimeRegister.ftl"/>
<#if security.hasEntityPermission("HR_TIMEMGR", "_APPROVE", session)>
	<div id="contextMenu" class="hide">
		<ul>
			<li action="approver">
				<i class="fa fa-paint-brush"></i>${uiLabelMap.HRApprove}
	        </li>
		</ul>
	</div>
	<#include "ApprWorkingOvertimeRegister.ftl"/>
</#if>

