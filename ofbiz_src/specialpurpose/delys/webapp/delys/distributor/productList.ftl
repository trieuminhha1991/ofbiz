<#assign dataField = "[
			{name : 'primaryProductCategoryId', type : 'String'},
			{name : 'productId', type : 'String'},
			{name : 'internalName', type : 'String'},
			{name : 'productName', type : 'String'}
		]"/>
<#assign columnlist = "
		{text : '${StringUtil.wrapString(uiLabelMap.DANo)}', width : '4%',cellsrenderer : function(row,column,value){
			var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\", row);
			var uid = data.uid++;
			return '<span>'+ uid +'</span>';
		}},
		{text : '${StringUtil.wrapString(uiLabelMap.DAPrimaryCategoryId)}', datafield :'primaryProductCategoryId', width :'24%'},
		{text : '${StringUtil.wrapString(uiLabelMap.DAProductId)}', datafield : 'productId', width : '24%'},
		{text : '${StringUtil.wrapString(uiLabelMap.DAInternalName)}',datafield : 'internalName', width : '24%'},
		{text : '${StringUtil.wrapString(uiLabelMap.DAProductName)}', datafield : 'productName'}
	"/>

<@jqGrid  dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true" showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup"
		  url="jqxGeneralServicer?sname=JQGetListProduct"
	/>
		
		
		