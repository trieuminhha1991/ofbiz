<#assign file = "script/ViewEmplSalaryItemHistoryScript.ftl"/>
<#include file/>
<style>
.highlightCell{
	background-color: #DDEE90 !important;
	color: #009900 !important
}
</style>
<#assign datafield = "[
					   {name: 'partyIdTo', type: 'string'},
					   {name: 'partyName', type: 'string'},
					   {name: 'emplPositionType', type: 'string'},
					   {name: 'customTimePeriodId', type: 'string'},
					   {name: 'currDept', type: 'string'},
					   {name: 'fromDate', type: 'date'},
					   {name: 'thruDate', type: 'date'},
					   {name: 'totalIncome', type: 'number'},	
					   {name: 'totalDeduction', type: 'number'},	
					   {name: 'salaryActual', type: 'number'},	
					   {name: 'invoiceIds', type: 'string'},	
					   {name: 'highlight', type: 'bool'},	
					  ]"/>
					  
<script type="text/javascript">

<#assign columnlist = "{datafield: 'customTimePeriodId', hidden: true},
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyIdTo', width: '12%',
							cellclassname: function (row, column, value, data) {
							    if (data.highlight) {
							        return 'highlightCell';
							    }
							}	
						},
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'partyName', filterable: true, editable: false, 
							cellsalign: 'left', width: '13%',
							cellclassname: function (row, column, value, data) {
							    if (data.highlight) {
							        return 'highlightCell';
							    }
							}		
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'currDept',editable: false, 
							cellsalign: 'left', width: '18%',
							cellclassname: function (row, column, value, data) {
							    if (data.highlight) {
							        return 'highlightCell';
							    }
							}		
						},
						{text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionType', filterable: true, 
							editable: false, cellsalign: 'left', width: '17%',
							cellclassname: function (row, column, value, data) {
							    if (data.highlight) {
							        return 'highlightCell';
							    }
							}		
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate',filterType : 'range',
							editable: false, cellsalign: 'left', width: '11%', cellsformat: 'dd/MM/yyyy ', columntype: 'datetimeinput',
							cellclassname: function (row, column, value, data) {
							    if (data.highlight) {
							        return 'highlightCell';
							    }
							}		
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate',filterType : 'range',
							editable: false, cellsalign: 'left', width: '11%', cellsformat: 'dd/MM/yyyy ', columntype: 'datetimeinput',
							cellclassname: function (row, column, value, data) {
							    if (data.highlight) {
							        return 'highlightCell';
							    }
							}		
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonIncome)}', datafield: 'totalIncome',filterType : 'number',
							editable: false, cellsalign: 'right', width: '12%',
							cellsrenderer: function (row, column, value) {
				 		 		 return \"<div style='margin-top: 9.5px; text-align: right;'>\" + formatcurrency(value) + \"</div>\";
			 		 		 },
			 		 		cellclassname: function (row, column, value, data) {
							    if (data.highlight) {
							        return 'highlightCell';
							    }
							}	
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonDeduction)}', datafield: 'totalDeduction', 
							filterType : 'number',editable: false, cellsalign: 'right', width: '12%',
							cellsrenderer: function (row, column, value) {
				 		 		 return \"<div style='margin-top: 9.5px; text-align: right;'>\" + formatcurrency(value) + \"</div>\";
			 		 		 },
			 		 		cellclassname: function (row, column, value, data) {
							    if (data.highlight) {
							        return 'highlightCell';
							    }
							}	
						},
						{text: '${StringUtil.wrapString(uiLabelMap.RealSalaryPaid)}', 
							datafield: 'salaryActual', filterType : 'number', editable: false, cellsalign: 'right', width: '13%',
							cellsrenderer: function (row, column, value) {
				 		 		return \"<div style='margin-top: 9.5px; text-align: right;'>\" + formatcurrency(value) + \"</div>\";
			 		 		},
							cellclassname: function (row, column, value, data) {
								if (data.highlight) {
							        return 'highlightCell';
							    }
							}	
						},
						{text: '${StringUtil.wrapString(uiLabelMap.HRInvoiceId)}', datafield: 'invoiceIds',
							editable: false, cellsalign: 'right', width: '13%',
							cellsrenderer: function (row, column, value) {
				 		 		if(value && value.length > 0){
				 		 			var retVal = '<a>' + value[0] + '</a>';
				 		 			for(var i = 1; i < value.length; i++){
				 		 				retVal += ', <a>' + value[i] + '</a>';
				 		 			}
				 		 			return '<span>' + retVal + '</span>';
				 		 		}
				 		 		return '<span>'+ value +'</span>';
			 		 		},
							cellclassname: function (row, column, value, data) {
								if (data.highlight) {
							        return 'highlightCell';
							    }
							}	
						}
						"/>
</script>
<div id="jqxcontainer"></div>
<div id="jqxNotify">
    <div id="jqxNotifyContent">
    </div>
</div>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.PageTitleListEmplSalary}</h4>
		<div class="widget-toolbar none-content">
			<#if security.hasEntityPermission("HR_MGRPAYROLL", "_CREATE", session)>
				<button id="createInvoice" class="grid-action-button fa-indent" style="font-size: 15px !important">${uiLabelMap.CreatePayrollInvoice}</button>
			</#if>
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class="row-fluid">
						<div class="span3" ">
							<div id="monthCustomTime"></div>
						</div>
						<div class="span6" style="margin-left: 0px">
							<div id="yearCustomTime"></div>
						</div>
					</div>	
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class="span12" style="margin-right: 15px">
							<div id="dropDownButton" style="" class="pull-right">
								<div style="border: none;" id="jqxTree">
										
								</div>
							</div>
							<button id="removeFilter" style="margin-top:0px;" class="grid-action-button icon-filter open-sans pull-right">${uiLabelMap.HRCommonRemoveFilter}</button>
						</div>
					</div>
				</div>
			</div>
		</div>	
		<div class="row-fluid">
			<@jqGrid filtersimplemode="true" addType="popup" dataField=datafield columnlist=columnlist clearfilteringbutton="true" showtoolbar="false" 
				 filterable="true" alternativeAddPopup="popupWindowAddPartyAttend" deleterow="false" editable="false" addrow="false"
				 url="" id="jqxgrid"  mouseRightMenu="true" contextMenuId="contextMenu" selectionmode="checkbox"
				 removeUrl="" deleteColumn="" updateUrl="" editColumns="" jqGridMinimumLibEnable="false" />		
		</div>
	</div>
</div>

<div id='contextMenu' style="display: none">
	<ul>
		<li action="viewEmplSalaryDetail">
			<i class="fa-list-alt"></i>${uiLabelMap.EmplSalaryItemDetails}
        </li>
        <li action="viewOrgDetailPaid">
          	<i class="fa-credit-card"></i>${uiLabelMap.OrgDetailPaid}
        </li>
        <li action="viewInvoiceItem">
          	<i class="icon-file"></i>${uiLabelMap.ViewAndCreateInvoice}
        </li>
	</ul>
</div>
	
<script type="text/javascript" src="/hrresources/js/payroll/ViewEmplSalaryItemHistory.js"></script>
<#include "component://basehr/webapp/basehr/payroll/ViewEmplSalaryItemHistoryDetail.ftl"/>

<#if security.hasEntityPermission("HR_MGRPAYROLL", "_CREATE", session)>
	<script type="text/javascript" src="/hrresources/js/payroll/CreateInvoiceEmplSalaryItem.js"></script>
</#if>	
