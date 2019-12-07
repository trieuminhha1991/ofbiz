<#assign dataField = "[
		{name : 'productStoreId', type:'String'},
		{name : 'storeName', type:'String'},
		{name : 'defaultCurrencyUomId', type :'String'},
		{name : 'fromDate', type :'date', other:'Timestamp'},
		{name : 'inventoryFacilityId', type : 'String'},
		{name : 'partyId', type : 'String'},
		{name : 'payToPartyId', type :'String'}
	]"/>
<#assign columnlist = "
		{text : '${StringUtil.wrapString(uiLabelMap.DANo)}', width :'2%', cellsrenderer : function(row,column,value){
			var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\", row);
			var uid = data.uid++;
			return '<span>'+ uid +'<span>'
		}},
		{text : '${StringUtil.wrapString(uiLabelMap.DAProductStoreId)}', width : '14%', datafield : 'productStoreId'},
		{text : '${StringUtil.wrapString(uiLabelMap.DAStoreName)}', width : '14%', datafield : 'storeName'},
		{text : '${StringUtil.wrapString(uiLabelMap.DACurrencyUomId)}', width : '14%', datafield : 'defaultCurrencyUomId'},
		{text : '${StringUtil.wrapString(uiLabelMap.DAFromDate)}', width : '14%', datafield : 'fromDate', cellsformat : 'dd/MM/yyyy HH:mm:ss'},
		{text : '${StringUtil.wrapString(uiLabelMap.DAMainFacilityId)}', width : '14%', datafield : 'inventoryFacilityId'},
		{text : '${StringUtil.wrapString(uiLabelMap.DAPartyId)}', width : '14%', datafield : 'partyId'},
		{text : '${StringUtil.wrapString(uiLabelMap.DAPayToPartyId)}', datafield : 'payToPartyId'}
	"/>
<@jqGrid dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true" showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup"
		 url="jqxGeneralServicer?sname=JQGetListProductStoreAndDetail"
	/>