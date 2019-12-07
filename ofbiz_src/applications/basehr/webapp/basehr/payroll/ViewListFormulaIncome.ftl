<#include "script/ViewListFormulaIncomeScript.ftl"/>
<#assign dataField="[{ name: 'code', type: 'string' },
					 { name: 'name', type: 'string' },
					 { name: 'function', type: 'string'},
					 {name: 'payrollCharacteristicId', type: 'string'},
					 {name: 'payrollItemTypeId', type: 'string'},
					 {name: 'taxableTypeId', type: 'string'},
					 {name: 'taxRate', type: 'number'},
					 {name: 'editAction', type: 'string'}]
					"/>
<script type="text/javascript">
   <#assign columnlist="{ text: '${uiLabelMap.formulaCode}', datafield: 'code', width: 140},
						{ text: '${uiLabelMap.formulaName}', datafield: 'name'},
						{ text: '${uiLabelMap.formulaDescription}', datafield: 'function', 
							cellsrenderer: function(row, column, value){
								 return '<div><code style=\"white-space: normal;\">' + value + '</code></div>';
							}
						},
						{text: '${uiLabelMap.CommonCharacteristic}', datafield: 'payrollCharacteristicId', width: '100',filterType : 'checkedlist',
							cellsrenderer: function(row, column, value){
							 	for(var i = 0; i < characteristicArr.length; i++){
							 		if(characteristicArr[i].payrollCharacteristicId == value){
							 			return '<div style=\"margin-left: 3px\">' + characteristicArr[i].description + '</div>';
							 		}
							 	}		
							 	return '<div style=\"margin-left: 3px\">' + value + '</div>';
							 },
							 createfilterwidget : function(column, columnElement, widget){
								 var source = {
										 localdata : characIncomeArr,
										 datatype : 'array'
								 };
								 var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind : true});
								 var dataFilter = filterBoxAdapter.records;
								 //dataFilter.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								 widget.jqxDropDownList({source: dataFilter, displayMember: 'description', valueMember : 'payrollCharacteristicId'});
								 if(dataFilter.length <= 8){
									 widget.jqxDropDownList({autoDropDownHeight : true});
								 }else{
									 widget.jqxDropDownList({autoDropDownHeight : false})
								 }
							 }
						},
						{text: '${uiLabelMap.PayrollItemType}', datafield: 'payrollItemTypeId', width: '120',filterType : 'checkedlist',
							cellsrenderer: function(row, column, value){
							 	for(var i = 0; i < payrollItemTypeArr.length; i++){
							 		if(payrollItemTypeArr[i].payrollItemTypeId == value){
							 			return '<div style=\"margin-left: 3px\">' + payrollItemTypeArr[i].description + '</div>';
							 		}
							 	}		
							 	return '<div style=\"margin-left: 3px\">' + value + '</div>';
							 },
							 createfilterwidget : function(column, columnElement, widget){
								 var source = {
										 localdata : payrollItemTypeArr,
										 datatype : 'array'
								 };
								 var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind : true});
								 var dataFilter = filterBoxAdapter.records;
								 //dataFilter.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								 widget.jqxDropDownList({source: dataFilter, displayMember: 'description', valueMember : 'payrollItemTypeId'});
								 if(dataFilter.length <= 8){
									 widget.jqxDropDownList({autoDropDownHeight : true});
								 }else{
									 widget.jqxDropDownList({autoDropDownHeight : false})
								 }
							 }
						},
						{text: '${uiLabelMap.TaxableType}', datafield: 'taxableTypeId', width: 200, cellsalign: 'left',filterType : 'checkedlist',
							cellsrenderer: function(row, column, value){
							 	for(var i = 0; i < taxableTypeArr.length; i++){
							 		if(taxableTypeArr[i].taxableTypeId == value){
							 			return '<div style=\"margin-left: 3px\">' + taxableTypeArr[i].description + '</div>';
							 		}
							 	}		
							 	return '<div style=\"margin-left: 3px\">' + value + '</div>';
							 },
							 createfilterwidget : function(column, columnElement, widget){
								 var source = {
										 localdata : taxableTypeArr,
										 datatype : 'array'
								 };
								 var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind : true});
								 var dataFilter = filterBoxAdapter.records;
								 //dataFilter.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								 widget.jqxDropDownList({source: dataFilter, displayMember: 'description', valueMember : 'taxableTypeId'});
								 if(dataFilter.length <= 8){
									 widget.jqxDropDownList({autoDropDownHeight : true});
								 }else{
									 widget.jqxDropDownList({autoDropDownHeight : false})
								 }
							 }
						},
						{text: '${uiLabelMap.HrTaxRate}', datafield: 'taxRate', width: 100,
							cellsrenderer: function(row, column, value){
								if(value){
									return '<div style=\"text-align: right; margin-right: 3px\">' + value + '%</div>';
								}else{
									return '<div style=\"text-align: center\">${uiLabelMap.HRCommonNotSetting}</div>';
								}
							}
						},
						"/>
</script>	
			
<div class="row-fluid">
	<div id="appendNotification">
		<div id="createFormulaNtf">
			<span id="notificationText"></span>
		</div>
	</div>
</div>
<#if security.hasEntityPermission("HR_MGRPAYROLL", "_CREATE", session)>
	<#assign addrow = "true">
	<#assign deleterow = "true">
<#else>
	<#assign addrow = "false">
	<#assign deleterow = "false">	
</#if>
					 
<@jqGrid url="jqxGeneralServicer?sname=JQGetFormulaIncome" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true" deleterow=deleterow jqGridMinimumLibEnable="false"
	editable="false" removeUrl="jqxGeneralServicer?sname=deletePayrollFormula&jqaction=D"
	addrow=addrow alternativeAddPopup="createIncomeFormulaWindow" addType="popup" 
	deleteColumn="code" showlist="false"
	editrefresh ="true" sortable="false"
	editmode="click"
	showtoolbar="true"
	autorowheight = "true"
/>
<#--<!-- customcontrol1="icon-plus-sign open-sans@${uiLabelMap.NewFormula}@EditFormula?payrollCharacteristicId=INCOME" -->
<#if security.hasEntityPermission("HR_MGRPAYROLL", "_CREATE", session)>	
	<#include "CreateNewFormulaIncome.ftl"/>
	<#include "CreateNewFormula.ftl"/>
</#if>