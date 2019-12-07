<#assign dataField = "[
		{name  : 'partyIdFrom', type: 'String'},
		{name : 'fullName', type: 'String'},
		{name : 'fromDate', type : 'date', other : 'Timestamp'},
		{name : 'thruDate', type :'date', other : 'Timestamp'}
	]"/>
<#assign columnlist = "
		{text : '${StringUtil.wrapString(uiLabelMap.DASalesmanId)}', width : '10%', datafield: 'partyIdFrom'},
		{text : '${StringUtil.wrapString(uiLabelMap.DAFullName)}', datafield : 'fullName'},
		{text : '${StringUtil.wrapString(uiLabelMap.DAFromDate)}', width : '25%', datafield : 'fromDate',cellsformat: 'dd/MM/yyyy HH:mm:ss'},
		{text : '${StringUtil.wrapString(uiLabelMap.DAThruDate)}', width : '25%', datafield : 'thruDate',cellsformat: 'dd/MM/yyyy HH:mm:ss'}
"/>
<@jqGrid dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true" showtoolbar="true" filtersimplemode="true"
	url = "jqxGeneralServicer?sname=JQGetSalesmanDetails"
/>