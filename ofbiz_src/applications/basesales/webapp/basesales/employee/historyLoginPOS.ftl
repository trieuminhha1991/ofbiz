<#assign dataField = "[
			{ name: 'partyId', type: 'string' },
			{ name: 'partyCode', type: 'string' },
			{ name: 'partyName', type: 'string' },
			{ name: 'userLoginId', type: 'string' },
			{ name: 'productStoreId', type: 'string' },
			{ name: 'storeName', type: 'string' },
			{ name: 'posTerminalId', type: 'string' },
			{ name: 'terminalName', type: 'string' },
			{ name: 'fromDate', type: 'date', other: 'Timestamp' },
			{ name: 'thruDate', type: 'date', other: 'Timestamp' }]"/>

<#assign columnlist = "
			{ text: '${uiLabelMap.BSEmployeeId}', dataField: 'partyCode', width: 150 },
			{ text: '${uiLabelMap.BSEmployeeName}', dataField: 'partyName', minWidth: 150 },
			{ text: '${uiLabelMap.BSUserLogin}', dataField: 'userLoginId', width: 150 }, 
			{ text: '${uiLabelMap.BSPosTerminalId}', dataField: 'posTerminalId', width: 150 },
			{ text: '${uiLabelMap.BSPosTerminalName}', dataField: 'terminalName', width: 200 },
			{ text: '${uiLabelMap.BSStoreName}', dataField: 'storeName', width: 200, sortable: false, filterable: false, hidden: true },
			{ text: '${uiLabelMap.SettingStartTime}', dataField: 'fromDate', width: '14%', cellsformat: 'dd/MM/yyyy HH:mm:ss', filtertype: 'range' },
			{ text: '${uiLabelMap.SettingFinishTime}', dataField: 'thruDate', width: '14%', cellsformat: 'dd/MM/yyyy HH:mm:ss', filtertype: 'range' }"/>
		
<@jqGrid id="jqxHistoryLoginPOS" url="jqxGeneralServicer?sname=JQListHistoryLoginPOS" columnlist=columnlist dataField=dataField 
	viewSize="20" showtoolbar="true" filtersimplemode="true" showstatusbar="false" bindresize="true" clearfilteringbutton="true"/>