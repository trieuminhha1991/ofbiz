<#if !payrollTimestampResult?exists>
	<#assign payrollTimestampResult = dispatcher.runSync("getPayrollTableRecordTimestamp", Static["org.ofbiz.base.util.UtilMisc"].toMap("payrollTableId", parameters.payrollTableId, "userLogin", userLogin))>	
</#if>
<#if payrollTimestampResult?exists>
	<#assign listTimestamp = payrollTimestampResult.get("listTimestamp")>
	<#if listTimestamp?has_content>
		<#if !tempFromDate?exists>		
			<#assign tempFromDate = listTimestamp.get(0).get("fromDate")>	
		</#if>
		<#assign tempThruDate = listTimestamp.get(0).get("thruDate")>
		  
		<#assign formulaList = delegator.findByAnd("PayrollTableCode", Static["org.ofbiz.base.util.UtilMisc"].toMap("payrollTableId", parameters.payrollTableId), Static["org.ofbiz.base.util.UtilMisc"].toList("code"), false)>
		<#assign formulaListStr = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(formulaList, "code", true)>
		<#assign entityCondiontion = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("code", Static["org.ofbiz.entity.condition.EntityJoinOperator"].IN, formulaListStr)>
		<#assign incomeCondition = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("payrollCharacteristicId", "INCOME")>
		<#assign deductionCondition = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("payrollCharacteristicId", "DEDUCTION")>
		<#assign taxDeductionCondition = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("payrollCharacteristicId", "TAX_DEDUCTION")>
		<#assign orgPaidCondition = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("payrollCharacteristicId", "ORG_PAID")>
		<#assign otherFormulaConds =  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("payrollCharacteristicId", null)>
		
		<#assign listFormulaIncomeOrdered = delegator.findList("PayrollFormula", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(entityCondiontion, 
																						Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND,
																						incomeCondition), null, Static["org.ofbiz.base.util.UtilMisc"].toList("payrollItemTypeId"), null, false)>
	
		<#assign listFormulaDeductionOrdered = delegator.findList("PayrollFormula", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(entityCondiontion, 
																						Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND,
																						deductionCondition), null, null, null, false)>

		<#assign listFormulaTaxDeductionOrdered = delegator.findList("PayrollFormula", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(entityCondiontion, 
																						Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND,
																						taxDeductionCondition), null, null, null, false)>
		<#assign listFormulaOrgPaidOrdered = delegator.findList("PayrollFormula", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(entityCondiontion, 
																						Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND,
																						orgPaidCondition), null, null, null, false)>																																																																		
		<#assign listFormulaOthers = delegator.findList("PayrollFormula", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(entityCondiontion, 
																						Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND,
																						otherFormulaConds), null, null, null, false)>				
		
	   <div id="treeGrid"></div>	  
	   
	   <script type="text/javascript">
	   		
	   		<#assign data = Static["com.olbius.payroll.PayrollUtil"].convertCalcSalaryEmplToJson(dispatcher, parameters.payrollTableId, tempFromDate, tempThruDate, userLogin, locale, timeZone)/>
	   		var data = [${StringUtil.wrapString(data)}]; 
	   		
	   		$(document).ready(function () {
	   			var source = {
	   				dataType: 'json',
	   				datafields:[
	   				    {name: 'partyName', type: 'string'},
	   				    {name: 'partyId', type:'string'},
	   				    {name: 'numberWorkDay', type: 'string'},	   				    
	   				    <#list formulaListStr as formulaCode>
	   				 		<#assign formula = delegator.findOne("PayrollFormula", Static["org.ofbiz.base.util.UtilMisc"].toMap("code", formulaCode), false)/>
	   				 		{name: '${formulaCode}', type: 'float'},
	   				    </#list>
	   				 	{name: 'totalFormulaIncome', type: 'float'},
		   				{name: 'totalFormulaDeduction', type: 'float'},
		   				{name: 'totalOrgPaid', type:'float'},
		   				{name: 'totalFormulaTaxDeduction', type: 'float'},
		   				{name: 'realSalaryPaid', type: 'float'},
	   				 	{ name: 'expanded',type: 'bool'},
	   				 	{name: 'children', type: 'array'}
	   				],
	   				hierarchy:
                    {
                        root: "children",
                    },
                    id: 'partyId',
                    localData: data,
                    pager: function (pagenum, pagesize, oldpagenum) {
	                    // callback called when a page or page size is changed.
	                }
	   			};
	   			
	   			<#assign payrollItemTypeIdList = []>
	   			var columnlist = [
					/* {text: '', datafield: 'departmentId', hidden: true}, */   			                  
	   			    {text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', pinned: true, datafield: 'partyName', cellsalign: 'left', editable: false, width: 170, filterable: false},
	   				{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyId',cellsalign: 'left', editable: false,width: 100, filterable: false,
						cellsrenderer: function (row, column, value) {
        					return '<a style = \"margin-left: 10px\" href=' + 'EmployeeProfile?partyId=' + value + '>' +  value + '</a>';
						}
					},
					{text: '${StringUtil.wrapString(uiLabelMap.NumberWorkDay)}', datafield: 'numberWorkDay',cellsalign: 'right', editable: false,
						width: 90, filterable: false,
						/* cellsrenderer: function (row, column, value) {
        					return '<a style = \"margin-left: 10px\" href=' + 'EmployeeProfile?partyId=' + value + '>' +  value + '</a>';
						} */
					},
					/*formula others*/
					<#list listFormulaOthers as formula>
						{text: '${StringUtil.wrapString(formula.name)}',  datafield: '${formula.get("code")}',  cellsalign: 'right',width: '120px', filterable: false,
							cellsrenderer: function (row, column, value) {
								return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
			 		 		 }
						},
					</#list>
						
					/* formula income */
					<#list listFormulaIncomeOrdered as formula>						
						<#assign payrollCharacteristicId = formula.getString("payrollCharacteristicId")/>
				   		<#assign width = '120'>				   		
						<#if formula.getString("payrollItemTypeId")?has_content>
							<#assign payrollItemTypeId = formula.getString("payrollItemTypeId")/>
							<#if (payrollItemTypeIdList?seq_index_of(payrollItemTypeId) < 0)>
								<#assign payrollItemTypeIdList = payrollItemTypeIdList + [payrollItemTypeId]>
							</#if>
							{text: '${StringUtil.wrapString(formula.name)}', columngroup: '${payrollItemTypeId}', datafield: '${formula.get("code")}', cellsalign: 'right',width: '${width}', filterable: false,
								cellsrenderer: function (row, column, value) {
									return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
				 		 		 }	
							},
						<#else>
							{text: '${StringUtil.wrapString(formula.name)}', columngroup: '${payrollCharacteristicId}', datafield: '${formula.get("code")}',  cellsalign: 'right',width: '${width}', filterable: false,
								cellsrenderer: function (row, column, value) {
					 		 		 return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
				 		 		 }
							},
						</#if>						
					</#list>
					{text: '${uiLabelMap.TotalIncome}', columngroup: "INCOME", datafield: 'totalFormulaIncome', cellsalign: 'right',width: 120, filterable: false,
						cellsrenderer: function (row, column, value) {
			 		 		 return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
		 		 		 }
					},
					
					/* formula deduction */
					<#list listFormulaDeductionOrdered as formula>						
				   		<#assign width = '120'>				   		
						{text: '${StringUtil.wrapString(formula.name)}', columngroup: '${formula.getString("payrollCharacteristicId")}', datafield: '${formula.get("code")}',  cellsalign: 'right',width: '${width}', filterable: false,
							cellsrenderer: function (row, column, value) {
								return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
			 		 		 }
						},
					</#list>
					{text: '${uiLabelMap.TotalDeduction}', columngroup: "DEDUCTION", datafield: 'totalFormulaDeduction', cellsalign: 'right',width: 120, filterable: false,
						cellsrenderer: function (row, column, value) {
			 		 		 return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
		 		 		 }
					},
					
					/* formula organization paid */
					<#list listFormulaOrgPaidOrdered as formula>						
				   		<#assign width = '120'>				   		
						{text: '${StringUtil.wrapString(formula.name)}', columngroup: '${formula.getString("payrollCharacteristicId")}', datafield: '${formula.get("code")}', cellsalign: 'right',width: '${width}', filterable: false,
							cellsrenderer: function (row, column, value) {
								return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
			 		 		 }	
						},						
					</#list>
					{text: '${uiLabelMap.TotalOrgPaid}', columngroup: "ORG_PAID", datafield: 'totalOrgPaid', cellsalign: 'right',width: 120, filterable: false,
						cellsrenderer: function (row, column, value) {
			 		 		 return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
		 		 		 }
					},
					
					/* formula tax deduction */	
					<#list listFormulaTaxDeductionOrdered as formula>						
				   		<#assign width = '120'>				   		
						{text: '${StringUtil.wrapString(formula.name)}', columngroup: '${formula.getString("payrollCharacteristicId")}', datafield: '${formula.get("code")}', cellsalign: 'right',width: '${width}', filterable: false,
							cellsrenderer: function (row, column, value) {
								return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
			 		 		 }	
						},						
					</#list>
					{text: '${uiLabelMap.TotalTaxDeduction}', columngroup: "TAX_DEDUCTION", datafield: 'totalFormulaTaxDeduction', cellsalign: 'right',width: 120, filterable: false,
						cellsrenderer: function (row, column, value) {
			 		 		 return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
		 		 		 }
					},
					{text: '${uiLabelMap.RealSalaryPaid}', datafield: 'realSalaryPaid', cellsalign: 'right',width: 120, filterable: false,
						cellsrenderer: function (row, column, value) {
			 		 		 return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
		 		 		 }
					}
				];
	   			
	   			var columngroups = [
					<#list payrollItemTypeIdList as payrollItemId>
						<#assign payrollItemType = delegator.findOne("PayrollItemType", Static["org.ofbiz.base.util.UtilMisc"].toMap("payrollItemTypeId", payrollItemId), false)>
						{text: '${StringUtil.wrapString(payrollItemType.description)}', align: 'center', name: '${payrollItemType.payrollItemTypeId}',  parentGroup: "INCOME"},
					</#list>
	   				<#list payrollCharList as characteristic>
	   					{text: '${StringUtil.wrapString(characteristic.description)}', align: 'center', name: '${characteristic.payrollCharacteristicId}'},
	   				</#list>
	   				
                ];
	   			var dataAdapter = new $.jqx.dataAdapter(source,{
	   				autoBind: false,
	   				downloadComplete: function (data, status, xhr) {
	   					//dataAdapter.totalrecords = data.TotalRows;
	                },
	   			});
	   			
	   			$("#treeGrid").jqxTreeGrid(
	            {	
	            	theme: 'olbius',
	                width: '100%',
	                source: dataAdapter,
	                pageSize: 20,
	                pageSizeOptions: ['20', '30', '50', '100', '200', '500'],
	                pageable: true,
	                pagerMode: 'advanced',
	                columnsResize: true,
	                //altRows: true,
	                columns: columnlist,
	                columnGroups: columngroups
	            });
	   		});
		</script>
	</#if>
</#if>
