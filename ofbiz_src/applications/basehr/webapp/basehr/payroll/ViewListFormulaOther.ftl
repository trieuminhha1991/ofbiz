<#include "script/ViewListFormulaOtherScript.ftl"/>
<#assign dataField="[{ name: 'code', type: 'string' },
					 { name: 'name', type: 'string' },
					 { name: 'function', type: 'string'},
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
<@jqGrid url="jqxGeneralServicer?sname=JQGetFormulaOther" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true" deleterow=deleterow
	editable="false" removeUrl="jqxGeneralServicer?sname=deletePayrollFormula&jqaction=D"
	deleteColumn="code" jqGridMinimumLibEnable="false"
	editrefresh ="true"
	editmode="click" showlist="false"
	showtoolbar = "true"
	autorowheight = "true"
	addrow=addrow alternativeAddPopup="settingFormula" addType="popup"
/>

<#if security.hasEntityPermission("HR_MGRPAYROLL", "_CREATE", session)>	
	${setContextField("includeFile", "component://basehr/webapp/basehr/payroll/CreateNewFormulaOthers.ftl")}
	${setContextField("includeJsLink", "/hrresources/js/payroll/CreateNewFormulaOther.js")}
	${setContextField("includeJsScript", "component://basehr/webapp/basehr/payroll/script/CreateNewFormulaOtherScript.ftl")}
	${screens.render("component://basehr/widget/PayrollScreens.xml#CreateNewFormula")}
</#if>