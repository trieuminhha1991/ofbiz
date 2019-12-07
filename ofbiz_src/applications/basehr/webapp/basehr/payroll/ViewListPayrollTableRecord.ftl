<#include "script/ViewListPayrollTableRecordScript.ftl"/>
<#assign dataField="[{ name: 'payrollTableId', type: 'string' },
					 { name: 'payrollTableName', type: 'string'},
					 { name: 'fromDate', type: 'date',},
					 { name: 'thruDate', type: 'date',},
					 { name: 'statusId', type: 'string'},
					 { name: 'totalOrgPaid', type: 'number'},
					 { name: 'totalAcutalReceipt', type: 'number'},
					 ]
					"/>				
					
<#assign columnlist="{ text: '${uiLabelMap.PayrollTableName}', datafield: 'payrollTableName', width: '20%',
 					 	cellsrenderer: function(row, column, value){
 					 		var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
							return '<a href=\"ViewPayrollTableRecordDetail?payrollTableId='+ rowData.payrollTableId + '\">' + value + '</a>';
						}
 					 },
                     { text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId', width: '16%', editable:false, filtertype: 'checkedlist',
                     	createfilterwidget: function(column, columnElement, widget){
						    var filterBoxAdapter = new $.jqx.dataAdapter(globalVar.statuses, {autoBind: true});
							var dataSoureList = filterBoxAdapter.records;
						    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
						    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', autoDropDownHeight: false,valueMember : 'statusId'});
						},
						cellsrenderer : function(row, column, value){
							var val = $('#jqxgrid').jqxGrid('getrowdata', row);
							for(var i = 0; i < globalVar.statuses.length; i++){
								if(globalVar.statuses[i].statusId == value){
									return '<div style=\"margin-top: 6px; margin-left: 4px;\">'+ globalVar.statuses[i].description+'</div>';		
								}
							}
						}
                     },
                    {text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', cellsformat: 'dd/MM/yyyy',filtertype: 'range', width: '13%', editable: false,},
					{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy',filtertype: 'range', 
						width: '13%', columntype: 'datetimeinput',
					},
					{text: '${StringUtil.wrapString(uiLabelMap.TotalOrgPaidInsurance)}', datafield: 'totalOrgPaid', width: '19%',
						columntype: 'numberinput', filtertype: 'number',
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
							if(typeof(value) == 'number'){
								return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
							}
						}
					},
					{text: '${StringUtil.wrapString(uiLabelMap.TotalRealSalaryPaid)}', datafield: 'totalAcutalReceipt', 
						columntype: 'numberinput', filtertype: 'number', width: '19%',
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
							if(typeof(value) == 'number'){
								return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
							}
						}
					},
                     
"/>		

<#if security.hasEntityPermission("HR_MGRPAYROLL", "_CREATE", session)>
	<#assign addrow = "true"/>
<#else>
	<#assign addrow = "false"/>
</#if>
<#if security.hasEntityPermission("HR_MGRPAYROLL", "_UPDATE", session)>
	<#assign update = "true"/>
<#else>
	<#assign update = "false"/>
</#if>
<#if security.hasEntityPermission("HR_MGRPAYROLL", "_APPROVE", session)>
	<#assign approval = "true"/>
<#else>
	<#assign approval = "false"/>	
</#if>

<script type="text/javascript">
    var currencyUomId = "${defaultOrganizationPartyCurrencyUomId?if_exists}";
    var filterObjData = new Object();
</script>
<#assign customcontrol1 = "fa fa-file-excel-o open-sans@${uiLabelMap.HRPExportExcel}@javascript:void(0); listPayrollExcelObj.exportExcel()">
<@jqGrid url="jqxGeneralServicer?sname=JQGetListPayrollTableRecord" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true" editable="false"  showlist="false" showtoolbar = "true" deleterow="false" jqGridMinimumLibEnable="false"
	removeUrl="jqxGeneralServicer?sname=deletePayrollTable&jqaction=D" deleteColumn="payrollTableId"
	alternativeAddPopup="createPayrollTableRecord" addrow=addrow addType="popup" 
	mouseRightMenu="true" contextMenuId="contextMenu"	
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePayrollTableRecord"  editColumns="payrollTableId;payrollTableName"
    customcontrol1=customcontrol1 isSaveFormData="true" formData="filterObjData"
/>
	
<div id='contextMenu' class="hide">
	<ul>
		<#if update== "true">
		<li action="calculatePayrollTable" id="calculatePayroll"><i class="fa fa-calculator"></i>${uiLabelMap.PayrollTableCalculate}</li>
		<li action="sendRequestAppr" id="sendRequestAppr"><i class="fa fa-paper-plane-o"></i>${StringUtil.wrapString(uiLabelMap.SendRequestApproval)}</li>
		</#if>
		<#if approval== "true">
		<li action="approve" id="approvalPayroll"><i class="fa-paint-brush"></i>${StringUtil.wrapString(uiLabelMap.HRApprove)}</li>
		</#if>
	</ul>
</div>		
<#if addrow == "true">	
	<div id="createPayrollTableRecord" class="hide">
		<div>${uiLabelMap.EditPayrollTable}</div>
		<div class='form-window-container'>
	    	<div class='form-window-content'>
	    		<div class='row-fluid margin-bottom10'>
					<div class="span4 text-algin-right">
						<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HRCommonMonthYear)}</label>
					</div>
					<div class="span8">
						<div class="row-fluid">
							<div class="span12">
								<div class="span3">
									<div id="monthNew"></div>
								</div>
								<div class="span3">
									<div id="yearNew"></div>
								</div>
							</div>
						</div>
					</div>							
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class="asterisk">${uiLabelMap.PayrollTableName}</label>
					</div>
					<div class="span8">
						<input type="text" id="payrollTableNameAdd">
					</div>
				</div>	
				<div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class=""></label>
					</div>
					<div class="span8">
						<div id="isCalsBaseOnTimekeeping" style="margin: 5px 0 0 0 !important"><span style="font-size: 14px">${uiLabelMap.CalculateBaseOnTimekeeping}</span></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div id="timekeepingSummaryGrid"></div>
				</div>
				<div class="row-fluid no-left-margin">
					<div id="loadingCreateNew" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerCreateNew"></div>
					</div>
				</div>
	    	</div>
	    	<div class="form-action">
	    		<button type="button" class='btn btn-danger form-action-button pull-right' id="alterCancel">
					<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
	    		<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSave">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
	    	</div>
	    </div>	
	</div>	
	<script type="text/javascript" src="/hrresources/js/payroll/AddPayrollTableRecord.js"></script>
</#if>

<script type="text/javascript" src="/hrresources/js/payroll/ViewListPayrollTableRecord.js"></script>
			