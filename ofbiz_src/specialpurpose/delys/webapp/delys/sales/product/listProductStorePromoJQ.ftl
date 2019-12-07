<#assign resultService = dispatcher.runSync("getProductPromoTypesByChannel", Static["org.ofbiz.base.util.UtilMisc"].toMap("salesMethodChannel","SALES_GT_CHANNEL"))/>
<#if resultService?exists && resultService.listProductPromoType?exists>
	<#assign listPromoType = resultService.listProductPromoType/>
</#if>
<script type="text/javascript">
	<#if listPromoType?exists>
		var promoTypeData = [
			<#list listPromoType as promoTypeItem>
				{
					typeId : "${promoTypeItem.productPromoTypeId}",
					description : "${StringUtil.wrapString(promoTypeItem.get("description", locale))}"
				},
			</#list>
		];
	<#else>
		var promoTypeData = [];
	</#if>
</script>

<#assign dataField = "[{name: 'productStoreId', type: 'string'}, 
						{name: 'productPromoId', type: 'string'}, 
						{name: 'promoName', type: 'string'}, 
						{name: 'fromDate', type: 'date', other: 'Timestamp'}, 
						{name: 'thruDate', type: 'date', other: 'Timestamp'},
						{name: 'productPromoTypeId', type: 'string'}
						]"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.DAProductPromoId)}', dataField: 'productPromoId', width: '16%',
							cellsrenderer: function(row, colum, value) {
	                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	                        	if (data != undefined) {
	                        		return \"<span><a href='/delys/control/viewProductPromo?productPromoId=\" + data.productPromoId + \"'>\" + data.productPromoId + \"</a></span>\";
	                        	}
	                        	return value;
	                        }
						}, 
						{text: '${StringUtil.wrapString(uiLabelMap.DAPromoName)}', dataField: 'promoName'},
						{text: '${StringUtil.wrapString(uiLabelMap.DAFromDate)}', dataField: 'fromDate', width: '14%', cellsformat: 'dd/MM/yyyy - HH:mm:ss'}, 
						{text: '${StringUtil.wrapString(uiLabelMap.DAThruDate)}', dataField: 'thruDate', width: '14%', cellsformat: 'dd/MM/yyyy - HH:mm:ss'}, 
						{text: '${uiLabelMap.DelysPromotionType}', dataField: 'productPromoTypeId', width: '180px', filtertype: 'checkedlist', 
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	    						for(var i = 0 ; i < promoTypeData.length; i++){
	    							if (value == promoTypeData[i].typeId){
	    								return '<span title = ' + promoTypeData[i].description +'>' + promoTypeData[i].description + '</span>';
	    							}
	    						}
	    						return '<span title=' + value +'>' + value + '</span>';
							}, 
							createfilterwidget: function (column, columnElement, widget) {
				   				var filterBoxAdapter2 = new $.jqx.dataAdapter(promoTypeData,
				                {
				                    autoBind: true
				                });
				                var uniqueRecords2 = filterBoxAdapter2.records;
				   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'typeId', valueMember : 'typeId',  
				   					renderer: function (index, label, value) {
										for (i = 0; i < promoTypeData.length; i++){
											if(promoTypeData[i].typeId == value){
												return promoTypeData[i].description;
											}
										}
									    return value;
									}
								});
								//widget.jqxDropDownList('checkAll');
				   			}
				   		}, 
						{text: '${StringUtil.wrapString(uiLabelMap.DAStatus)}', dataField: 'status', width: '8%',
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								if (data != null && data.thruDate != null && data.thruDate != undefined) {
									var thruDate = new Date(data.thruDate);
									var nowDate = new Date('${nowTimestamp}');
									if (thruDate < nowDate) {
										return '<span title=\"${uiLabelMap.DAExpired}\">${uiLabelMap.DAExpired}</span>';
									}
								}
	    						return '<span></span>';
							}, 
						}, 
						"/>
<@jqGrid id="jqxgrid" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="10" showtoolbar="true" filtersimplemode="true" showstatusbar="false" 
		url="jqxGeneralServicer?sname=JQGetListProductStorePromo&productStoreId=${productStore.productStoreId?if_exists}" mouseRightMenu="true" contextMenuId="contextMenu"/>

<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	</ul>
</div>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
        	$("#jqxgrid").jqxGrid('updatebounddata');
        }
	});
</script>
