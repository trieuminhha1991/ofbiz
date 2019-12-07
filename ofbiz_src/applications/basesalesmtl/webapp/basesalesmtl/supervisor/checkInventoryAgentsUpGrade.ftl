<#assign dataField="[
						{name: 'partyId', type: 'string'},
						{name: 'partyCode', type: 'string'},
						{name: 'partyName', type: 'string'},
						{name: 'orderId', type: 'string'},
						{name: 'productId', type: 'string'},
						{name: 'productCode', type: 'string'},
						{name: 'productName', type: 'string'},
						{name: 'createBy', type: 'string'},
						{name: 'createdByName', type: 'string'},
						{name: 'createdByCode', type: 'string'},
						{name: 'qtyInInventory', type: 'number'},
						{name: 'fromDate', type: 'date', other: 'Timestamp'}
						
					]"/>
<#assign columnlist = "{text: '${uiLabelMap.BPOSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (row + 1) + '</div>';
						    }
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', datafield: 'partyCode', width: 120 },
						{ text: '${StringUtil.wrapString(uiLabelMap.BSCustomer)}', datafield: 'partyName', width: 200},
						{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productCode', width: 150 },
						{ text: '${StringUtil.wrapString(uiLabelMap.BSProduct)}', datafield: 'productName', width: 200  },
						{ text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: 'qtyInInventory', filtertype: 'number', width: 100,
							cellsrenderer: function (row, column, value) {
						        return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						    }
						},	
						{ text: '${StringUtil.wrapString(uiLabelMap.InventoryDate)}', datafield: 'fromDate', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSSalesman)}', datafield: 'createdByName', width: 200,
							cellsrenderer: function (row, columns, value) {
								var rowData = $('#jqxGridInventoryAgents').jqxGrid('getrowdata',row);
						        return '<div style=margin:4px;>' + value + ' (' + rowData.createdByCode + ')' + '</div>';
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSOrderId)}', datafield: 'orderId'}
					"/>
<#if urlServices?exists>
    <#assign urlServices = "jqxGeneralServicer?sname=${urlServices}"/>
<#else>
    <#assign urlServices = "jqxGeneralServicer?sname=JQGetListCheckInventoryAgentsUpGrade"/>
</#if>
<@jqGrid id ="jqxGridInventoryAgents" url="${urlServices}" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true" sortable="true"
		 showtoolbar="true" filtersimplemode="true"/>
