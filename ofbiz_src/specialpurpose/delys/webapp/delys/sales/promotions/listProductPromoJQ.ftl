<#assign resultService = dispatcher.runSync("getProductPromoTypesByChannel", Static["org.ofbiz.base.util.UtilMisc"].toMap("salesMethodChannel","SALES_GT_CHANNEL"))/>
<#if resultService?exists && resultService.listProductPromoType?exists>
	<#assign listPromoType = resultService.listProductPromoType/>
</#if>
<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "PROMO_STATUS"}, null, false) />
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
	
	<#if statusList?exists>
		var statusData = [
			<#list statusList as statusItem>
				<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />
				{
					statusId : '${statusItem.statusId}',
					description : "${description}"
				},
			</#list>
		];
	<#else>
		var statusData = [];
	</#if>
</script>
<#assign dataField="[{name: 'productPromoId', type: 'string'}, 
						{name: 'promoName', type: 'string'}, 
						{name: 'productPromoTypeId', type: 'string'}, 
						{name: 'budgetTotalId', type: 'string'}, 
						{name: 'revenueMiniId', type: 'string'}, 
						{name: 'productPromoStatusId', type: 'string'}, 
						{name: 'createdDate', type: 'date', other: 'Timestamp'},
						{name: 'fromDate', type: 'date', other: 'Timestamp'},
						{name: 'thruDate', type: 'date', other: 'Timestamp'}
						]"/>
<#--{text: '${uiLabelMap.DelysBudgetTotal}', dataField: 'budgetTotalId', width: '100px', cellsalign: 'center'}, 
{text: '${uiLabelMap.DelysMiniRevenue}', dataField: 'revenueMiniId', width: '100px', cellsalign: 'center'}, -->
<#assign columnlist="{text: '${uiLabelMap.DelysPromoProductPromoId}', dataField: 'productPromoId', width: '180px', 
		        			cellsrenderer: function(row, colum, value) {
	                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	                        	if (data != undefined) {
	                        		return \"<span><a href='/delys/control/viewProductPromo?productPromoId=\" + data.productPromoId + \"'>\" + data.productPromoId + \"</a></span>\";
	                        	}
	                        	return value;
	                        }
						}, 
						{text: '${uiLabelMap.DelysPromoPromotionName}', dataField: 'promoName'},
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
						{text: '${uiLabelMap.DACreateDate}', dataField: 'createdDate', width: '160px', cellsformat: 'dd/MM/yyyy - hh:mm:ss'}, 
						{text: '${uiLabelMap.DAFromDate}', dataField: 'fromDate', width: '160px', cellsformat: 'dd/MM/yyyy - hh:mm:ss'}, 
						{text: '${uiLabelMap.DAThruDate}', dataField: 'thruDate', width: '160px', cellsformat: 'dd/MM/yyyy - hh:mm:ss'}, 
						{text: '${uiLabelMap.DelysProductPromoStatusId}', dataField: 'productPromoStatusId', width: '160px', cellsalign: 'center', filtertype: 'checkedlist', 
							cellsrenderer: function(row, column, value){
						 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	    						for(var i = 0 ; i < statusData.length; i++){
	    							if (value == statusData[i].statusId){
	    								return '<span title = ' + statusData[i].description +'>' + statusData[i].description + '</span>';
	    							}
	    						}
	    						return '<span title=' + value +'>' + value + '</span>';
						 	}, 
						 	createfilterwidget: function (column, columnElement, widget) {
								var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
									renderer: function(index, label, value){
										for(var i = 0; i < statusData.length; i++){
											if(statusData[i].statusId == value){
												return '<span>' + statusData[i].description + '</span>';
											}
										}
										return value;
									}
								});
								widget.jqxDropDownList('checkAll');
				   			}
						}, 
              		"/>
              		
<#assign tmpCreateUrl = ""/>
<#if security.hasPermission("DELYS_PROMOS_CREATE", session)>
	<#assign tmpCreateUrl = "icon-plus open-sans@${uiLabelMap.DelysPromoCreateNewPromotion}@editProductPromotion"/>
</#if>
<#assign tmpDelete = "false"/>
<#if security.hasPermission("DELYS_PROMOS_DELETE", session)>
	<#assign tmpDelete = "true"/>
</#if>
<@jqGrid id="jqxgrid" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="25" showtoolbar="true" defaultSortColumn="createdDate" sortdirection="desc" filtersimplemode="true" showstatusbar="false" 
		customcontrol1=tmpCreateUrl 
		removeUrl="jqxGeneralServicer?sname=deleteProductPromoDelys&jqaction=C" deleteColumn="productPromoId" deleterow=tmpDelete 
		url="jqxGeneralServicer?sname=JQGetListProductPromo" mouseRightMenu="true" contextMenuId="contextMenu"/>

<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetail)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}</li>
	    <#if tmpDelete?exists && tmpDelete == "true"><li><i class="icon-trash open-sans"></i>${StringUtil.wrapString(uiLabelMap.accDeleteSelectedRow)}</li></#if>
	    <#if tmpCreateUrl != ""><li><i class="fa-plus-circle open-sans"></i>${StringUtil.wrapString(uiLabelMap.DelysPromoCreateNewPromotion)}</li></#if>
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
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetail)}") {
        	var data = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
			if (data != undefined && data != null) {
				var productPromoId = data.productPromoId;
				var url = 'viewProductPromo?productPromoId=' + productPromoId;
				var win = window.open(url, '_self');
				win.focus();
			}
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}") {
        	var data = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
			if (data != undefined && data != null) {
				var productPromoId = data.productPromoId;
				var url = 'viewProductPromo?productPromoId=' + productPromoId;
				var win = window.open(url, '_blank');
				win.focus();
			}
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.accDeleteSelectedRow)}") {
        	$("#deleterowbuttonjqxgrid").click();
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DelysPromoCreateNewPromotion)}") {
        	var win = window.open("editProductPromotion", "_self");
        	window.focus();
        }
	});
</script>