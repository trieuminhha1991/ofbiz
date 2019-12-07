<#assign dataField = "[
		{name : 'promoName', type : 'string'},
		{name : 'productPromoId', type : 'string'},
		{name : 'productPromoTypeId', type : 'string'},
		{name : 'productPromoStatusId', type : 'string'},
		{name : 'budgetTotal', type : 'string'},
		{name : 'revenueMini', type : 'string'}
	]"/>
<#assign columnlist = "
		{text : '${StringUtil.wrapString(uiLabelMap.DAPromoName)}', dataField : 'promoName',cellsrenderer : function(row, colum, value){
			var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			return \"<span><a href='/delys/control/editProductPromotion?productPromoId=\" + data.productPromoId + \"'>\" + data.promoName + \"[\" +data.productPromoId + \"]\" + \"</a></span>\";
		}},
		{text : '${StringUtil.wrapString(uiLabelMap.DACommissionDiscountType)}', width : '20%', dataField : 'productPromoTypeId'},
		{text : '${StringUtil.wrapString(uiLabelMap.DABudgetTotal)}', width : '15%', dataField : 'budgetTotal'},
		{text : '${StringUtil.wrapString(uiLabelMap.DelysMiniRevenue)}', width : '15%', dataField : 'revenueMini'},
		{text : '${StringUtil.wrapString(uiLabelMap.DACommissionDiscountStatus)}', width : '20%', dataField : 'productPromoStatusId'}
	"/>
<@jqGrid contextMenuId="contextMenu" mouseRightMenu="true" filtersimplemode="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" 
	 showtoolbar="true" filterable="true" editable="false"
	 url="jqxGeneralServicer?sname=jqGetListCommissionDiscount"
/>
<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	    <li><i class="icon-edit open-sans"></i>${StringUtil.wrapString(uiLabelMap.DAEditStatus)}</li>
	</ul>
</div>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#contextMenu").jqxMenu({width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function(event){
		var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if(tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}"){
        	$("#jqxgrid").jqxGrid('updatebounddata');
        }else if(tmpKey == "${StringUtil.wrapString(uiLabelMap.DAEditStatus)}"){
        	var data = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
        	var productPromoId = data.productPromoId;
        	var url = 'productPromoOverview?productPromoId=' + productPromoId;
        	var win = window.open(url, '_blank');
        	win.focus();
        }
	})
</script>