<#include "script/ViewPayrollTableRecordDetailScript.ftl"/>
<style type="text/css">
.aquaCell{
	background-color: aqua !important;
}
.bisqueCell{
	background-color: bisque !important;
}
.azureCell{
	background-color: azure !important;
}
.yellowCell{
	background-color: yellow !important;
}
</style>
<#assign datafield = "[{name: 'payrollTableId', type: 'string'},
					   {name: 'partyId', type: 'string'},
					   {name: 'partyCode', type: 'string'},
					   {name: 'firstName', type: 'string'},
					   {name: 'fullName', type: 'string'},
					   {name: 'emplPositionTypeDes', type: 'string'},
					   {name: 'groupName', type: 'string'},
					   {name: 'baseSalAmount', type: 'number'},"/>
					   
<script type="text/javascript">
<#assign columnlist = "{text: '#', sortable: false, filterable: false, editable: false,
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: '3%',
						    cellsrenderer: function (row, column, value) {
						        return \"<div style='margin:4px;'>\" + (value + 1) + \"</div>\";
						    }
						},
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: '12%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'firstName', width: '15%', editable: false,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
								if(rowData){
									return '<span>' + rowData.fullName + '</span>';
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', width: '16%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionTypeDes', width: '16%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.SalaryBaseFlat)}', datafield: 'baseSalAmount', width: '11%', columntype: 'numberinput', 
							filtertype: 'number',
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(typeof(value) == 'number'){
									return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
								}
							}
						},
						"/>
<#assign columngrouplist = ""/>						
<#if payrollItemTypeList?has_content>
	<#list payrollItemTypeList as payrollItemType>
		<#assign payrollItemTypeId = payrollItemType.payrollItemTypeId/>
		<#if payrollItemTypeId == "OTHER_INCOME">
			<#assign datafield = datafield + "{name: '${payrollItemTypeId}', type: 'number'},"/>
			<#assign columnlist = columnlist + "{text: '${StringUtil.wrapString(payrollItemType.description)}', width: '11%', datafield: '${payrollItemTypeId}', columntype: 'numberinput', 
													filtertype: 'number', filterable: false,
													cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
														if(typeof(value) == 'number'){
															return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
														}
													}						
												},"/> 
		<#else>
			<#assign payrollFormulaList = delegator.findByAnd("PayrollFormula", Static["org.ofbiz.base.util.UtilMisc"].toMap("payrollItemTypeId", payrollItemTypeId), null, false)/>
			<#if (payrollFormulaList?size > 1)>
				<#assign columngrouplist = columngrouplist + "{text: '${StringUtil.wrapString(payrollItemType.description)}', align: 'center', name: '${payrollItemTypeId}'},"/>
				<#list payrollFormulaList as payrollFormula>
					<#if payrollFormula.abbreviation?exists>
						<#assign description = payrollFormula.abbreviation/>
					<#else>
						<#assign description = payrollFormula.name/>
					</#if>
					<#assign datafield = datafield + "{name: '${payrollFormula.code}', type: 'number'},"/>
					<#assign columnlist = columnlist + "{text: '${StringUtil.wrapString(description)}', width: '8%', datafield: '${payrollFormula.code}', columntype: 'numberinput', 
													filtertype: 'number', columngroup: '${payrollItemTypeId}', filterable: false,
													cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
														if(typeof(value) == 'number'){
															return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
														}
													}						
												},"/>
				</#list>
			<#else>
				<#assign datafield = datafield + "{name: '${payrollItemTypeId}', type: 'number'},"/>
				<#assign columnlist = columnlist + "{text: '${StringUtil.wrapString(payrollItemType.description)}', width: '10%', datafield: '${payrollItemTypeId}', columntype: 'numberinput', 
													filtertype: 'number', filterable: false,
													cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
														if(typeof(value) == 'number'){
															return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
														}
													}						
												},"/>
			</#if>
		</#if>
	</#list>
</#if>
<#assign datafield = datafield + "{name: 'totalIncome', type: 'number'},
								  {name: 'insSalAmount', type: 'number'},"/>
<#assign columnlist = columnlist + "{text: '${StringUtil.wrapString(uiLabelMap.TotalIncome)}', width: '12%', datafield: 'totalIncome', columntype: 'numberinput', 
										filtertype: 'number',
										cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
											if(typeof(value) == 'number'){
												return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
											}
										},
										cellclassname: function (row, column, value, data) {
										    return 'bisqueCell';
										}
									},
									{text: '${StringUtil.wrapString(uiLabelMap.InsuranceSalaryShort)}', width: '12%', datafield: 'insSalAmount', columntype: 'numberinput', 
										filtertype: 'number',
										cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
											if(typeof(value) == 'number'){
												return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
											}
										}		
									},"/>
									
<#if orgPaidFormulaList?has_content>
	<#assign columngrouplist = columngrouplist + "{text: '${StringUtil.wrapString(orgPaidChar.description?if_exists)}', align: 'center', name: '${orgPaidChar.payrollCharacteristicId}'},"/>
	<#list orgPaidFormulaList as orgPaidFormula>
		<#if orgPaidFormula.abbreviation?exists>
			<#assign description = orgPaidFormula.abbreviation/>
		<#else>
			<#assign description = orgPaidFormula.name/>
		</#if>
		<#assign datafield = datafield + "{name: '${orgPaidFormula.code}', type: 'number'},"/>	
		<#assign columnlist = columnlist + "{text: '${StringUtil.wrapString(description)}', width: '10%', datafield: '${orgPaidFormula.code}', columntype: 'numberinput', 
				filtertype: 'number', columngroup: '${orgPaidChar.payrollCharacteristicId}', filterable: false,
				cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
					if(typeof(value) == 'number'){
						return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
					}
				},
				cellclassname: function (row, column, value, data) {
				    return 'aquaCell';
				}
			},"/>
	</#list>
	<#assign datafield = datafield + "{name: 'totalOrgPaid', type: 'number'},"/>
	<#assign columnlist = columnlist + "{text: '<b>${StringUtil.wrapString(uiLabelMap.CommonTotal)}</b>', width: '10%', datafield: 'totalOrgPaid', columntype: 'numberinput', 
		filtertype: 'number', columngroup: '${orgPaidChar.payrollCharacteristicId}',
		cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
			if(typeof(value) == 'number'){
				return '<span style=\"text-align: right\"><b>' + formatcurrency(value) + '</b></span>';
			}
		},
		cellclassname: function (row, column, value, data) {
		    return 'aquaCell';
		}
	},"/>
</#if>
<#if deductionFormulaList?has_content>
	<#assign columngrouplist = columngrouplist + "{text: '${StringUtil.wrapString(deductionChar.description?if_exists)}', align: 'center', name: '${deductionChar.payrollCharacteristicId}'},"/>
	<#list deductionFormulaList as deductionFormula>
		<#if deductionFormula.abbreviation?exists>
			<#assign description = deductionFormula.abbreviation/>
		<#else>
			<#assign description = deductionFormula.name/>
		</#if>
		<#assign datafield = datafield + "{name: '${deductionFormula.code}', type: 'number'},"/>	
		<#assign columnlist = columnlist + "{text: '${StringUtil.wrapString(description)}', width: '10%', datafield: '${deductionFormula.code}', columntype: 'numberinput', 
				filtertype: 'number', columngroup: '${deductionChar.payrollCharacteristicId}', filterable: false,
				cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
					if(typeof(value) == 'number'){
						return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
					}
				},
				cellclassname: function (row, column, value, data) {
				    return 'azureCell';
				}
			},"/>
	</#list>
	<#assign datafield = datafield + "{name: 'totalDedution', type: 'number'},"/>
	<#assign columnlist = columnlist + "{text: '<b>${StringUtil.wrapString(uiLabelMap.CommonTotal)}</b>', width: '10%', datafield: 'totalDedution', columntype: 'numberinput', 
											filtertype: 'number', columngroup: '${deductionChar.payrollCharacteristicId}',
											cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
												if(typeof(value) == 'number'){
													return '<span style=\"text-align: right\"><b>' + formatcurrency(value) + '</b></span>';
												}
											},
											cellclassname: function (row, column, value, data) {
											    return 'azureCell';
											}
										},
	"/>
</#if>
<#assign datafield = datafield + " {name: 'actualReceipt', type: 'number'}"/>
<#assign columnlist = columnlist + "{text: '<b>${StringUtil.wrapString(uiLabelMap.ActualReceipt)}</b>', width: '10%', datafield: 'actualReceipt', columntype: 'numberinput', 
										filtertype: 'number',
										cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
											if(typeof(value) == 'number'){
												return '<span style=\"text-align: right\"><b>' + formatcurrency(value) + '</b></span>';
											}
										},
										cellclassname: function (row, column, value, data) {
										    return 'yellowCell';
										}
									}"/>
<#assign datafield = datafield + "]"/> 	
</script>		
<div class="row-fluid">
	<div class="span12 alert alert-success margin-bottom10">
		${uiLabelMap.CommonStatus}:&nbsp;<span id="statusIdDesc">${StringUtil.wrapString(payrollTableRecordStatus.description)}</span>
	</div>
</div>	
<#assign statusId = payrollTableRecord.statusId/>
<#if security.hasEntityPermission("HR_MGRPAYROLL", "_UPDATE", session)>
	<#assign update = "true"/>
<#else>
	<#assign update = "false"/>
</#if>
<#assign listpartiesId = Static["com.olbius.basehr.util.SecurityUtil"].getPartiesByRolesWithCurrentOrg(userLogin, Static["com.olbius.basehr.util.PropertiesUtil"].ACC_MANAGER_ROLE, delegator)/>
<#if security.hasEntityPermission("HR_MGRPAYROLL", "_APPROVE", session) && listpartiesId?seq_contains(userLogin.partyId)>
	<#assign approval = "true"/>
<#else>
	<#assign approval = "false"/>	
</#if>
<#assign isEditable = "false"/>
<#assign customcontrol2=""/>
<#assign customcontrol3 = "" >
<#assign isCreateInvoice = "false"/>
<#if update == "true">
	<#if statusId == "PYRLL_TABLE_CREATED">
		<#assign customcontrol1 = "fa-calculator open-sans@${uiLabelMap.PayrollTableCalculate}@javascript:void(0);@payrollTableRecordDetailObj.calculatePayrollTable()" >
		<#assign isEditable = "true"/>
	<#elseif statusId == "PYRLL_TABLE_CALC" || statusId == "PYRLL_TABLE_REJECT">
		<#assign customcontrol1 = "fa-calculator open-sans@${uiLabelMap.PayrollTableCalculate}@javascript: void(0);@payrollTableRecordDetailObj.calculatePayrollTable()" >
		<#assign customcontrol2="fa-paper-plane-o open-sans@${uiLabelMap.SendRequestApproval}@javascript: void(0);@payrollTableRecordDetailObj.sendRequestApproval()"/>
		<#assign customcontrol3="fa-arrow-right open-sans@${uiLabelMap.HRCommonRounding}@javascript:void(0);@payrollTableRecordDetailObj.roundingNumber()"/>
		<#assign isEditable = "true"/>
	<#elseif statusId == "PYRLL_TABLE_ACCEPT">
		<#assign customcontrol1 = "fa-money open-sans@${uiLabelMap.CreatePayrollInvoice}@javascript: void(0);@payrollTableInvoiceObj.openWindow()" >
		<#assign customcontrol2 = "fa-list-ul open-sans@${uiLabelMap.PayrollInvoiceList}@javascript: void(0);@partyCreatedInvoiceObj.openWindow()" >
		<#assign isCreateInvoice = "true"/>
	<#elseif statusId == "PYRLL_TABLE_INVOICED">
		<#assign customcontrol1 = "fa-money open-sans@${uiLabelMap.CreatePayrollInvoice}@javascript: void(0);@payrollTableInvoiceObj.openWindow()" >
		<#assign customcontrol2 = "fa-list-ul open-sans@${uiLabelMap.PayrollInvoiceList}@javascript: void(0);@partyCreatedInvoiceObj.openWindow()" >
		<#assign isCreateInvoice = "true"/>
	<#else>
		<#assign customcontrol1 = "" >	
	</#if>
</#if>
<#if approval == "true" && statusId == "PYRLL_WAIT_APPR">
	<#assign customcontrol3 = "fa-paint-brush open-sans@${uiLabelMap.HRApprove}@javascript: void(0);@approvalPayrollObj.openWindow()" >
</#if>
<#assign customControlAdvance = "<div class ='pull-right jqx-button'><a id='exportExcelPayrollDetail' class='grid-action-button fa-file-excel-o open-sans' style='margin-right: 5px; margin-top:10px' href='javascript:payrollTableRecordDetailObj.exportExcelPayrollDetail(${parameters.payrollTableId});'>${uiLabelMap.HRPExportExcel}</a></div>"/>
<@jqGrid filtersimplemode="false" filterable="true" showtoolbar="true" dataField=datafield columnlist=columnlist
				clearfilteringbutton="true"  editable="false" deleterow="false" selectionmode="singlerow"
				addrow="false" columngrouplist=columngrouplist
				customcontrol1=customcontrol1 customcontrol2=customcontrol2 customcontrol3=customcontrol3
				mouseRightMenu="true" contextMenuId="contextMenu" customControlAdvance=customControlAdvance isSaveFormData="true" formData="filterObjData"
				showlist="true" sortable="false" url="jqxGeneralServicer?sname=JQGetListPayrollTableRecordParty&payrollTableId=${payrollTableId}" jqGridMinimumLibEnable="false"/>
<div id='contextMenu' class="hide">
	<ul>
		<li action="detail">
			<i class="fa fa-file-text-o"></i>${uiLabelMap.CommonDetail}
        </li>
	</ul>
</div>

<#assign listPartyApprPayrollTable = Static["com.olbius.basehr.payroll.util.PayrollUtil"].getListPartyApprovalPayrollTable(delegator, userLogin, payrollTableId)!/>

<div class="row-fluid" style="margin-top: 20px;">
<#if listPartyApprPayrollTable?has_content>
	<#assign size = listPartyApprPayrollTable?size/>
	<#list listPartyApprPayrollTable as partyApprPayrollTable>
			<div class="well well-small" style="margin-bottom: 5px">
				<div class="row-fluid">
					<#assign approvalTimes = size - partyApprPayrollTable_index/>
					<b>${StringUtil.wrapString(uiLabelMap.HRApprove)} ${StringUtil.wrapString(uiLabelMap.HRCommonTimes)} ${approvalTimes}:&nbsp;${StringUtil.wrapString(partyApprPayrollTable.fullName)}</b>
					<b>&nbsp;&#45;&nbsp;${StringUtil.wrapString(partyApprPayrollTable.description)}&nbsp;</b>
					&#40;${StringUtil.wrapString(uiLabelMap.ApprovalDate)}&#58;&nbsp;${partyApprPayrollTable.statusDatetime?if_exists?string["dd/MM/yyyy HH:mm"]}&#41;
				</div>
				<#if partyApprPayrollTable.changeReason?has_content>
					<div class="row-fluid">
						<b>${StringUtil.wrapString(uiLabelMap.CommonReason)}:</b>&nbsp;${StringUtil.wrapString(partyApprPayrollTable.changeReason)}
					</div>
				</#if>
			</div>
	</#list>
</#if>
</div>

<#include "ViewPayrollTablePartyDetail.ftl"/>
<#if approval == "true">
	<#include "ApprovalPayrolTable.ftl"/>
</#if>
<#if isCreateInvoice == "true">
	<#include "PayrollTableInvoice.ftl"/>
</#if>
<div id="roundingAmountWindow" class="hide">
	<div>${uiLabelMap.HRCommonRounding}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
    		<div class='row-fluid margin-bottom10'>
				<div class="span6 text-algin-right">
					<label class="">${uiLabelMap.RoundingToNumberOf}</label>
				</div>
				<div class="span6">
					<div id="roundingAmountNbr"></div>
				</div>
			</div>
		</div>
		<div class="row-fluid no-left-margin">
			<div id="loadingRounding" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
				<div class="loader-page-common-custom" id="spinnerRounding"></div>
			</div>
		</div>	
		<div class="form-action">
    		<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelRounding">
				<i class='icon-remove'></i>${uiLabelMap.CommonClose}</button>
    		<button type="button" class='btn btn-primary form-action-button pull-right' id="saveRounding">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
    	</div>	
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/payroll/ViewPayrollTableRecordDetail.js?v=0.0.1"></script>