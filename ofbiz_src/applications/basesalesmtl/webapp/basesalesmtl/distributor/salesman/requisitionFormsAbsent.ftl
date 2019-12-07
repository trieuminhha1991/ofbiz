<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign datafield = "[
	{name: 'emplLeaveId', type: 'string'},
	{name: 'partyId', type: 'string'},
	{name: 'partyName', type: 'string'},
	{name: 'emplPositionType', type: 'string'},
	{name: 'department', type: 'string'},
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
	{name: 'commentApproval', type: 'string'}]"/>

<#assign columnlist="
	{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId', width: 150, filterType : 'checkedlist',
		cellsrenderer: function(row, colum, value){
			value?value=mapStatusItem[value]:value;
	        return '<span>' + value + '</span>';
		},
		createfilterwidget: function (column, htmlElement, editor) {
        	editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'description', valueMember: 'statusId' ,
                renderer: function (index, label, value) {
                	if (index == 0) {
                		return value;
					}
				    return mapStatusItem[value];
                }
        	});
		}
	},
	{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyId', width: 200},
	{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'partyName', minWidth: 200, filterable: false},
	{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', width: 200, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
	{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', width: 200, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
	{text: '${StringUtil.wrapString(uiLabelMap.HRNumberDayLeave)}', datafield: 'nbrDayLeave', cellsalign: 'right', width: 150, filterType : 'number', filterable: false},
	"/>
					
<@jqGrid addrow="false" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
	 deleterow="false" editable="false" dataField=datafield columnlist=columnlist
	 url="jqxGeneralServicer?sname=JQgetListSalesManEmplLeave" filterable="true" sortable="false"
/>

<script>
var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
	statusId: '${item.statusId?if_exists}',
	description: "${StringUtil.wrapString(item.get("description", locale))}"
   },</#list></#if>];
var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
		"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
</script>