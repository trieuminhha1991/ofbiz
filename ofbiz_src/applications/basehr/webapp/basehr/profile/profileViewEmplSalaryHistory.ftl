<#include "script/profileViewEmplSalaryHistorycript.ftl"/>
<script type="text/javascript" src="/hrresources/js/profile/emplSalaryHistory.js?v=0.0.1"></script>

<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.PayrollTableParty}</h4>
		<div class="widget-toolbar none-content">
			<div class="row-fluid">
					<div id="year" style="display: inline-block; float: right; margin-left: 5px"></div>
					<div id="month" style="display: inline-block; float: right;"></div>
			</div>	
		</div>
	</div>
</div>
<#assign datafield = "[{name: 'payrollTableId', type: 'string'},
						 {name: 'code', type: 'string'},
						 {name: 'formulaName', type: 'string'},
						 {name: 'amount', type: 'number'}
						]"/>

<#assign columnlistIncome = "{text:'${StringUtil.wrapString(uiLabelMap.HRIncome)}', datafield: 'formulaName', width: '60%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}', datafield: 'amount',
				   			cellsrenderer: function (row, column, value) {
								return '<span>' + formatcurrency(value) + '</span>';
			 		 		 },
			 		 		 aggregates: [{'':
			 		 		 	function (aggregatedValue, currentValue, column, record) {
			 		 		 		return aggregatedValue + currentValue;
			 		 		 	}
			 		 		 }],
			 		 		 aggregatesrenderer: function (aggregates) {
			 		 		 	var renderstring = '${StringUtil.wrapString(uiLabelMap.SumSalary)}: ';
			 		 		 	$.each(aggregates, function (key, value) {
			 		 		 		renderstring += formatcurrency(value);
			 		 		 	});
			 		 		 	return '<div style=\"margin-top: 5px; margin-left: 8px\">' + renderstring + '</div>';
			 		 		 }
					   }"/>	

<#assign columnlistDeduction = "{text:'${StringUtil.wrapString(uiLabelMap.HRDeduction)}', datafield: 'formulaName'},
					   {text: '${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}', datafield: 'amount',
				   			cellsrenderer: function (row, column, value) {
								return '<span>' + formatcurrency(value) + '</span>';
			 		 		 },
			 		 		 aggregates: [{'<b>${StringUtil.wrapString(uiLabelMap.SumSalary)}</b> ':
			 		 		 	function (aggregatedValue, currentValue, column, record) {
			 		 		 		return aggregatedValue + currentValue;
			 		 		 	}
			 		 		 }],
			 		 		 aggregatesrenderer: function (aggregates) {
			 		 		 	var renderstring = '${StringUtil.wrapString(uiLabelMap.SumSalary)}: ';
			 		 		 	$.each(aggregates, function (key, value) {
			 		 		 		renderstring += formatcurrency(value);
			 		 		 	});
			 		 		 	return '<div style=\"margin-top: 5px; margin-left: 8px\">' + renderstring + '</div>';
			 		 		 }
					   }"/>	
					   
<div class="row-fluid" style="margin-top: 25px; ">
	<div class="span12">
		<div class="span6">
			<div class="row-fluid">
				<div class="span12">
					<div class="form-legend" style="margin-bottom: 15px; padding: 5px 2px 0px 8px !important">
						<div class="contain-legend">
							<span class="content-legend" style="font-size: 15px">
								<a href="javascript:void(0)">
									${StringUtil.wrapString(uiLabelMap.HRPayrollInformation)}</a>
							</span>
						</div>
						<div class="row-fluid" style="position: relative;">
							<div class="span12" style="margin-top: 10px;" id="emplSalaryInfo">
								
							</div>
							<div class="row-fluid no-left-margin">
								<div id="loadingSalaryInfo" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
									<div class="loader-page-common-custom" id="spinnerSalaryInfo"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="span6">
			<div class="row-fluid">
				<div class="span12">
					<div class="form-legend" style="margin-bottom: 15px; padding: 5px 2px 0px 8px !important">
						<div class="contain-legend">
							<span class="content-legend" style="font-size: 15px">
								<a href="javascript:void(0)">
									${StringUtil.wrapString(uiLabelMap.HREmplAllowances)}</a>
							</span>
						</div>
						<div class="row-fluid" style="position: relative;">
							<div class="span12" style="margin-top: 10px;" id="emplAllowanceInfo">
								
							</div>
							<div class="row-fluid no-left-margin">
								<div id="loadingAllowanceInfo" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
									<div class="loader-page-common-custom" id="spinnerAllowanceInfo"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>		

<div class="row-fluid" style="border-bottom:1px dotted #d0d8e0; margin-top: 10px"></div>
			   
<div class="row-fluid" style="text-align: center;">
	<h3 style="color: #4383b4" class="open-sans" id="payslipTitle">${uiLabelMap.HRPayslip}</h3>
</div>
<div class="row-fluid">
	<span class="blue"><i class="fa-forward"></i><label style="display: inline-block;"><b>${uiLabelMap.ActualReceipt}</b>: <span id="acutalReceiveSalView" style="color: red"></span></label>&nbsp;&nbsp;<i class="fa-ellipsis-v"></i>&nbsp;&nbsp;</span>
	<span class="blue"><i class="fa-plus"></i><label style="display: inline-block; color: #62bd33"><b>${uiLabelMap.TotalIncome}</b>: <span id="totalIncomeView" style="color: red"></span></label>&nbsp;&nbsp;<i class="fa-ellipsis-v"></i>&nbsp;&nbsp;</span>
	<span class="blue"><i class="fa-minus"></i><label style="display: inline-block; color: rgb(103, 75, 85)"><b>${uiLabelMap.TotalDeduction}</b>: <span id="totalDeductionView" style="color: red"></span></label></span>
</div>
<div class="row-fluid" style="margin-top: 15px">
	<div class="span12">
		<div class="span6">
			<div class="row-fluid">
				<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlistIncome  showtoolbar="false"
						 filterable="false" deleterow="false" editable="false" addrow="false" viewSize="10" 
						 url="jqxGeneralServicer?sname=JQPartyPayrollAmount&hasrequest=Y&payrollCharacteristicId=INCOME" 
						 id="jqxgridIncome" jqGridMinimumLibEnable="false" sortable="true"  
						 selectionmode="singlerow" />					   						
			</div>		
		</div>
		<div class="span6">
			<div class="row-fluid">
				<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlistDeduction  showtoolbar="false" 
					 filterable="false" deleterow="false" editable="false" addrow="false" viewSize="10"  
					 url="jqxGeneralServicer?sname=JQPartyPayrollAmount&hasrequest=Y&payrollCharacteristicId=DEDUCTION" id="jqxgridDeduction" 
					 jqGridMinimumLibEnable="false" sortable="true"
					 selectionmode="singlerow" />
				
			</div>
		</div>		
	</div>
</div>					   
<!-- <div class="row-fluid" style="margin-top: 15px">
	<h5 style="color: #4383b4" class="open-sans">${uiLabelMap.HRDeductions}</h5>
</div> -->
