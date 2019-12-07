<#assign columnlist="{ text: '${uiLabelMap.finAccountAuthId}', dataField: 'finAccountAuthId', editable: false},
					 { text: '${uiLabelMap.finAccountId}', dataField: 'finAccountId', editable: false},
					 { text: '${uiLabelMap.amount}', dataField: 'amount', editable: false},
					 { text: '${uiLabelMap.currencyUomId}', dataField: 'currencyUomId', editable: false},
					 { text: '${uiLabelMap.authorizationDate}', dataField: 'authorizationDate', editable: false, cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.fromDate}', dataField: 'fromDate', editable: false, cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.thruDate}', dataField: 'thruDate', editable: false, cellsformat: 'dd/MM/yyyy'}
					 "/>

<#assign dataField="[{ name: 'finAccountAuthId', type: 'string' },
                 	{ name: 'finAccountId', type: 'string' },
                 	{ name: 'amount', type: 'number' },
                 	{ name: 'currencyUomId', type: 'string' },
                 	{ name: 'authorizationDate', type: 'date' },
                 	{ name: 'fromDate', type: 'date' },
                 	{ name: 'thruDate', type: 'date' }
                 	]
		 		  	"/>	
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=JQListFinAccountAuth&finAccountId=${parameters.finAccountId}" createUrl="jqxGeneralServicer?jqaction=C&sname=createFinAccountAuth"
		 addColumns="finAccountId[${parameters.finAccountId}];amount(java.math.BigDecimal);currencyUomId;authorizationDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);fromDate(java.sql.Timestamp);" addrefresh="true"
		 removeUrl="jqxGeneralServicer?jqaction=D&sname=expireFinAccountAuth&finAccountId=${parameters.finAccountId}" deleteColumn="finAccountAuthId"
		/>

<div id="alterpopupWindow">
	<div>${uiLabelMap.accCreateNew}</div>
	<div style="overflow: hidden;">
    	<table>
	 		<tr>	
	 			<td align="right">${uiLabelMap.amount}:</td>
 				<td align="left"><input id="amount"></input></td>
 			</tr>
 			<tr>	
 				<td align="right">${uiLabelMap.currencyUomId}:</td>
 				<td align="left"><input id="currencyUomId"></input></td>
			</tr>
			<tr>	
				<td align="right">${uiLabelMap.authorizationDate}:</td>
				<td align="left"><div id="authorizationDate"></div></td>
			</tr>
			<tr>	
				<td align="right">${uiLabelMap.fromDate}:</td>
				<td align="left"><div id="fromDate"></div></td>
			</tr>
			<tr>	
				<td align="right">${uiLabelMap.thruDate}:</td>
				<td align="left"><div id="thruDate"></div></td>
			</tr>
	 		<tr>
	 			<td align="right"></td>
	 			<td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
	 		</tr>
	 	</table>
	 </div>
</div>
<script type="text/javascript">
	//Create theme
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	// Create Amount
	$('#amount').jqxInput({width: '195px'});
	
	//Create currencyUomId
	$('#currencyUomId').jqxInput({width: '195px'});
	
	//Create authorizationDate
	$('#authorizationDate').jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy'});
	
	//Create fromDate
	$('#fromDate').jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy'});
	
	//Create thruDate
	$('#thruDate').jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy'});
	
	$("#alterpopupWindow").jqxWindow({
		width: 580, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});

	$("#alterCancel").jqxButton();
	$("#alterSave").jqxButton();

	// update the edited row when the user clicks the 'Save' button.
	$("#alterSave").click(function () {
		var row;
		row = { 
				amount:$('#amount').val(),
				currencyUomId:$('#currencyUomId').val(),
				authorizationDate:$('#authorizationDate').val(),
				fromDate:$('#fromDate').val(),
				thruDate:$('#thruDate').val()
    	  	};
	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
    // select the first row and clear the selection.
    $("#jqxgrid").jqxGrid('clearSelection');                        
    $("#jqxgrid").jqxGrid('selectRow', 0);  
    $("#alterpopupWindow").jqxWindow('close');
	});
</script>