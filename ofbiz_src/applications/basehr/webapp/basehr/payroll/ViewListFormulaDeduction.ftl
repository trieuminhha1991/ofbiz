<#include "script/ViewListFormulaDeductionScript.ftl"/>
<#assign dataField="[{ name: 'code', type: 'string' },
					 { name: 'name', type: 'string' },
					 { name: 'function', type: 'string'},
					 {name: 'payrollCharacteristicId', type: 'string'},
					 {name: 'exempted', type: 'string'},
					 {name: 'editAction', type: 'string'}]
					"/>
<script type="text/javascript">

<#assign columnlist="{ text: '${uiLabelMap.formulaCode}', datafield: 'code', width: 180},
 					 { text: '${uiLabelMap.formulaName}', datafield: 'name', width: 220},
					 { text: '${uiLabelMap.formulaDescription}', datafield: 'function', width: 420, 
					 	cellsrenderer: function(row, column, value){
                     		 return '<div><code style=\"white-space: normal;\">' + value + '</code></div>';
                     	}
					 },
					 {text: '${uiLabelMap.CommonCharacteristic}', datafield: 'payrollCharacteristicId', width: 120,filterType : 'checkedlist',
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
									 localdata : characDeductionArr,
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
					 {text: '${uiLabelMap.IsExemptedTax}', datafield: 'exempted',   cellsalign: 'center',filterable : false,
					 	cellsrenderer: function(row, column, value){
					 		if(value == 'Y'){
								return \"<label style='text-align: center; margin-top: 8px'><input type='checkbox' disabled='disabled' checked='checked'><span class='lbl'></span></label>\";
							}else{
								return \"<label style='text-align: center; margin-top: 8px'><input type='checkbox' disabled='disabled'><span class='lbl'></span></label>\";
							}		
					 	}
					 },
					 "/>
</script>
<#--<!-- {text: '', width: '50', columntype: 'template', editable: false, filterable: false, datafield: 'editAction',
					 	cellsrenderer: function(row, columnfield, value, defaulthtml, columnproperties){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	                		return '<div style=\"text-align: center; margin-bottom: 2px\"><a href=\"EditFormula?code='+ data.code +'\" class=\"btn btn-mini btn-primary icon-edit\" ></a></div>';
	                	   }
					 } -->
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
<@jqGrid url="jqxGeneralServicer?sname=JQGetFormulaDeduction" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true" deleterow=deleterow jqGridMinimumLibEnable="false"
	editable="false" removeUrl="jqxGeneralServicer?sname=deletePayrollFormula&jqaction=D"
	addrow=addrow alternativeAddPopup="createDeductionFormulaWindow" addType="popup" id="jqxgrid"
	deleteColumn="code" showlist="false"
	editrefresh ="true"
	editmode="click" sortable="false"
	showtoolbar = "true"
	autorowheight = "true"
/>

<#if security.hasEntityPermission("HR_MGRPAYROLL", "_CREATE", session)>	
	<#include "CreateNewFormulaDeduction.ftl"/>
	<#include "CreateNewFormula.ftl"/>
</#if>