<!-- maybe delete -->
<#include "script/ViewTempPayrollTableListScript.ftl"/>
<div class="row-fluid" id="jqxNotifyContainer">
	<div id="jqxNotify">
		<div id="ntfContent"></div>
	</div>
</div>
<style>
#treeFormula ul li>div.jqx-checkbox{
	margin-top: 6.5px !important
}
</style>
<#assign dataField="[{ name: 'payrollTableId', type: 'string' },
					 { name: 'payrollTableName', type: 'string' },
					 { name: 'fromDate', type: 'date', other:'Timestamp' },
					 { name: 'thruDate', type: 'date', other:'Timestamp' },
					 { name: 'customTimePeriodId', type: 'string'},					 
					 { name: 'groupName', type: 'string'},					 
					 { name: 'statusId', type: 'string'}]
					"/>				

<#assign columnlist="{datafield: 'customTimePeriodId', hidden: true},
					{ text: '${uiLabelMap.HRPayrollTableId}', datafield: 'payrollTableId', width: 120, hidden: false, editable: false,
						cellsrenderer: function(row, column, value){
							return '<a href=\"viewPayrollTable?payrollTableId='+ value + '\">' + value + '</a>';
						}
					 },
 					 { text: '${uiLabelMap.PayrollTableName}', datafield: 'payrollTableName', width: 200},
					 {text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', width: 250, filterable: false, editable: false},
					 { text: '${uiLabelMap.PayrollTableTimePeriod}', datafield: 'fromDate',cellsformat: 'MM/yyyy', filtertype:'range', filterable: false, columntype: 'datetimeinput', editable: false},
					 { text: '${uiLabelMap.thruDate}', datafield: 'thruDate',cellsformat: 'dd/MM/yyyy', hidden: true},
					 
                     { text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId', width: 200, editable:false, filtertype: 'checkedlist',
                     	createfilterwidget: function(column, columnElement, widget){
						    var filterBoxAdapter = new $.jqx.dataAdapter(statuses, {autoBind: true});
							var dataSoureList = filterBoxAdapter.records;
						    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
						    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', autoDropDownHeight: false,valueMember : 'statusId', filterable:true, searchMode:'containsignorecase'});
						},
						cellsrenderer : function(row, column, value){
							var val = $('#jqxgrid').jqxGrid('getrowdata', row);
							for(var x in statuses){
								if(statuses[x].statusId  && val.statusId && statuses[x].statusId == val.statusId){
									return '<div style=\"margin-top: 6px; margin-left: 4px;\">'+statuses[x].description+'</div>';		
								}
							}
						}
                     }
					 
					"/>

<@jqGrid url="jqxGeneralServicer?sname=JQGetTempPayrollTableList" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true"
	editable="true" 
	editrefresh ="true"
	editmode="click" showlist="false"
	showtoolbar = "true" deleterow="true" jqGridMinimumLibEnable="false" mouseRightMenu="true" contextMenuId="contextMenu2"
	removeUrl="jqxGeneralServicer?sname=deletePayrollTable&jqaction=D" deleteColumn="payrollTableId"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createPayrollTableRecord" alternativeAddPopup="popupAddRow" addrow="true" addType="popup" 
	addColumns="payrollTableName;departmentList;customTimePeriodId;partyId" addrefresh="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePayrollTableRecord"  editColumns="payrollTableId;payrollTableName"
/>
	
<div id='contextMenu2' class="hide">
	<ul>
		<li action="calculatePayrollTable">
			<i class="icon-play"></i>${uiLabelMap.PayrollTableCalculate}
        </li>
        <li action="updateToPayrollSalaryItem">
          	<i class="icon-edit"></i>${uiLabelMap.UpdateOnPaySalaryItemHistory}
        </li>
	</ul>
</div>	
		
<div id="updatePaySalItemWindow" class="hide">
	<div>${uiLabelMap.UpdateOnPaySalaryItemHistory}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div style='margin-top: 5px;' id='overrideData'><label>${uiLabelMap.OverrideData}</label></div>
				<div style='margin-top: 5px;' id='notOverride'><label>${uiLabelMap.NotOverrideData}</label></div>
			</div>
		</div>
		<div class="form-action">
    		<button type="button" class='btn btn-danger form-action-button pull-right' id="updatePaySalItemCancel">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
    		<button type="button" class='btn btn-primary form-action-button pull-right' id="updatePaySalItemSave">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
    	</div>
	</div>
</div>
		
<div id="popupAddRow" class='hide'>
    <div>${uiLabelMap.EditPayrollTable}</div>
    <div class='form-window-container'>
    	<div class='form-window-content'>
    		<form id="popupAddRowForm" action="" class="">
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">
							${uiLabelMap.PayrollTableName}
						</label>
					</div>
					<div class="span7">
						<input type="text" name="payrollTableNameAdd" id="payrollTableNameAdd">
					</div>
				</div>		
								   	
		   		<div class='row-fluid margin-bottom10'>
		   			<div class='span5 text-algin-right'>
						<label class="asterisk">
			   				${uiLabelMap.Department}
			   			</label>		   			
		   			</div>
		   			<div class="span7">
		   				<div id="dropDownButton" style="margin-top: 5px;">
							<div id="jqxTree">
		   					</div>
						</div>	
		   			</div>
		   		</div>	
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class=" asterisk">${uiLabelMap.CommonTime}</label>
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
						<label class="">&nbsp;</label>					
					</div>
					<div class="span7">
						<a href="javascript:void(0)" id="configPayrollFormula">${uiLabelMap.ConfigPayrollFormula}</a>
					</div>
				</div>
	    	</form>
    	</div>
    	<div class="form-action">
    		<button type="button" class='btn btn-danger form-action-button pull-right' id="alterCancel">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
    		<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSave">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
    	</div>
    </div>
</div>  
<div id="configPayrollFormulaWindow" class="hide">
	<div>${uiLabelMap.ConfigPayrollFormula}</div>
	<div class='form-window-container'>
    	<div class='form-window-content'>
    		<div class='row-fluid margin-bottom10'>
    			<div id="dropDownButtonFormula" style="margin-top: 5px;">
		    		<div id="treeFormula" style=''></div>
    			</div>
    		</div>
	    </div>
	    <div class="form-action">
	    	<button type="button" class='btn btn-primary form-action-button pull-right' id="updateCalcFormula">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonUpdate}</button>
	    </div>
    </div>		
</div>
<script type="text/javascript" src="/hrresources/js/payroll/ViewTempPayrollTableList.js"></script>