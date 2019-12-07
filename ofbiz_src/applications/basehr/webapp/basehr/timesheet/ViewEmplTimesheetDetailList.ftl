<#include "script/ViewEmplTimesheetDetailListScript.ftl"/>

<#assign datafield = "[{name: 'timekeepingDetailId', type: 'string'},
                       {name: 'timekeepingDetailName', type: 'string'},
                       {name: 'partyId', type: 'string'},
                       {name: 'groupName', type: 'string'},
                       {name: 'fromDate', type: 'date'},
                       {name: 'thruDate', type: 'date'},
                       {name: 'statusId', type: 'string'}
					  ]"/>
					  
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.EmplTimesheetDetail)}', datafield: 'timekeepingDetailName', width: '30%',
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
							   var timekeepingDetailId = rowData.timekeepingDetailId;
							   return '<a href=\"ViewTimekeepingDetailParty?timekeepingDetailId='+ timekeepingDetailId + '\" title=\"${StringUtil.wrapString(uiLabelMap.ViewDetails)}\">' + value + '</a>';
						   }
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.OrganizationalUnit)}', datafield: 'groupName', width: '25%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', editable: false, 
						   columntype: 'datetimeinput', width: '20%', cellsformat: 'dd/MM/yyyy',filtertype: 'range'},
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', editable: false, 
							   columntype: 'datetimeinput', width: '25%', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
					   "/>
</script>					  

<@jqGrid filtersimplemode="false" filterable="true" showtoolbar="true" dataField=datafield columnlist=columnlist  
				clearfilteringbutton="true"  editable="false" deleterow="false" selectionmode="singlerow"
				addrow="true" alternativeAddPopup="AddEmplTimesheetDetailWindow" addType="popup" 
				showlist="false" sortable="true"  mouseRightMenu="true" 
				contextMenuId="contextMenu" url="jqxGeneralServicer?sname=JQGetListTimekeepingDetail" jqGridMinimumLibEnable="false"/>

<#include "AddNewEmplTimesheetDetail.ftl"/>
