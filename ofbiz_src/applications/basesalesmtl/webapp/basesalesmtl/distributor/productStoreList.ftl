<#assign dataField = "[
		{name: 'productStoreId', type:'String'},
		{name: 'storeName', type:'String'},
		{name: 'defaultCurrencyUomId', type:'String'},
		{name: 'fromDate', type:'date', other:'Timestamp'},
		{name: 'inventoryFacilityId', type: 'String'},
		{name: 'partyId', type: 'String'},
		{name: 'payToPartyId', type:'String'}]"/>
<#assign columnlist = "
		{text: '${uiLabelMap.DmsSequenceId}', datafield: '', filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
		    cellsrenderer: function (row, column, value) {
		        return '<div style=margin:4px;>' + (row + 1) + '</div>';
		    }
		},
		{text: '${StringUtil.wrapString(uiLabelMap.DAProductStoreId)}', width: '10%', datafield: 'productStoreId',
			cellsrenderer: function(row, colum, value) {
		    	return \"<span><a href='showProductStore?productStoreId=\" + value + \"'>\" + value + \"</a></span>\";
		    }
		},
		{text: '${StringUtil.wrapString(uiLabelMap.DAStoreName)}', width: '30%', datafield: 'storeName'},
		{text: '${StringUtil.wrapString(uiLabelMap.DACurrencyUomId)}', width: '10%', datafield: 'defaultCurrencyUomId'},
		{text: '${StringUtil.wrapString(uiLabelMap.DAFromDate)}', width: 200, datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
		{text: '${StringUtil.wrapString(uiLabelMap.DAMainFacilityId)}', width: '14%', datafield: 'inventoryFacilityId'},
		{text: '${StringUtil.wrapString(uiLabelMap.DAPartyId)}', width: '14%', datafield: 'partyId'},
		{text: '${StringUtil.wrapString(uiLabelMap.DAPayToPartyId)}', datafield: 'payToPartyId'}"/>
<@jqGrid dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true" showtoolbar="true"
	alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup"
	 url="jqxGeneralServicer?sname=JQGetListProductStoreAndDetail"
	/>