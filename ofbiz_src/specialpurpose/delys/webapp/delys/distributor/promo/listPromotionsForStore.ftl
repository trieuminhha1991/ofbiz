<#assign dataField = "[
		{name : 'productPromoId', type : 'String'},
		{name : 'promoName', type : 'String'},
		{name : 'productPromoTypeId', type : 'String'},
		{name : 'budgetIdDis', type : 'String'},
		{name : 'budgetIdDisRev', type : 'String'},
		{name : 'productPromoStatusId', type : 'String'}
	]"/>
<#assign columnlist = "
		{text : '${StringUtil.wrapString(uiLabelMap.DANo)}', width :'2%', cellsrenderer : function(row,column,value){
			var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\", row);
			var uid = data.uid++;
			return '<span>'+ uid +'</span>';
		}},
		{text : '${StringUtil.wrapString(uiLabelMap.DelysPromoProductPromoId)}', datafield : 'productPromoId', width : '8%', cellsrenderer : function(row,column,value){
			var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\", row);
			return \"<span><a href= '/delys/control/viewProductPromo?productPromoId=\" + data.productPromoId +\"'>\" + data.productPromoId + \"</a></span>\";
		}},
		{text : '${StringUtil.wrapString(uiLabelMap.DelysPromoPromotionName)}', datafield : 'promoName', width : '26%'},
		{text : '${StringUtil.wrapString(uiLabelMap.DelysPromotionType)}', datafield : 'productPromoTypeId', width : '16%'},
		{text : '${StringUtil.wrapString(uiLabelMap.DelysBudgetTotal)}', datafield : 'budgetIdDis', width: '16%'},
		{text : '${StringUtil.wrapString(uiLabelMap.DelysMiniRevenue)}', datafield : 'budgetIdDisRev', width : '16%'},
		{text : '${StringUtil.wrapString(uiLabelMap.DelysProductPromoStatusId)}', datafield : 'productPromoStatusId'}
	"/>
<@jqGrid dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true" showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup"
	  	 url="jqxGeneralServicer?sname=JQGetListPromotionsForStore"
	/>