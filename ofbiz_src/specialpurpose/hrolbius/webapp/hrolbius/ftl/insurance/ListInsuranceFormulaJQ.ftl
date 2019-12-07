<#assign dataField="[{ name: 'code', type: 'string' },
					 { name: 'function', type: 'string' },
					 { name: 'description', type: 'string' },
					 { name: 'edit', type: 'string'}]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.PayrollFormulaCode}', datafield: 'code', width: 200, editable: false},
					 { text: '${uiLabelMap.formulaFunction}', datafield: 'function', width: 450, editable: false},
                     { text: '${uiLabelMap.HRolbiusRecruitmentTypeDescription}', datafield: 'description'},
                     { text: '', width: 80, datafield: 'edit', editable: false, filterable: false,
                     	cellsrenderer: function(row, column, value){
                     		var val = $(\"#jqxgrid\").jqxGrid(\"getrowdata\", row);
                     		var str = \"<a href='EditInsuranceFormula?code=\" + val.code + \"'>\" + \"${uiLabelMap.CommonUpdate}\" + \"</a>\";
                     		return str; 
                     	}
                     }"/>
					 
<@jqGrid url="jqxGeneralServicer?sname=JQGetInsuranceFormula" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true"
	editable="true" 
	editrefresh ="true"
	editmode="click"
	autorowheight="true"
	showtoolbar = "true" deleterow="true"
	removeUrl="jqxGeneralServicer?sname=deleteInsuranceFormula&jqaction=D" deleteColumn="code"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateInsuranceFormula"  editColumns="code;description;function"
/>
