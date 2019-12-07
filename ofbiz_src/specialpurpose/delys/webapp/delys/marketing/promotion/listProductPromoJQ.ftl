<#assign resultService = dispatcher.runSync("getProductPromoTypesByChannel", Static["org.ofbiz.base.util.UtilMisc"].toMap("salesMethodChannel","SALES_GT_CHANNEL"))/>
<#if resultService?exists && resultService.listProductPromoType?exists>
	<#assign listPromoType = resultService.listProductPromoType/>
</#if>
<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "PROMO_STATUS"}, null, false) />
<script type="text/javascript">
	var promoTypeData = new Array();
	<#list listPromoType as promoTypeItem>
		var row = {};
		row['typeId'] = "${promoTypeItem.productPromoTypeId}";
		row['description'] = "${StringUtil.wrapString(promoTypeItem.get("description", locale))}";
		promoTypeData[${promoTypeItem_index}] = row;
	</#list>
	
	var statusData = new Array();
	<#list statusList as statusItem>
		<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />
		var row = {};
		row['statusId'] = '${statusItem.statusId}';
		row['description'] = "${description}";
		statusData[${statusItem_index}] = row;
	</#list>
</script>
<#assign dataField="[{name: 'productPromoId', type: 'string'}, 
						{name: 'promoName', type: 'string'}, 
						{name: 'productPromoTypeId', type: 'string'}, 
						{name: 'budgetTotalId', type: 'string'}, 
						{name: 'revenueMiniId', type: 'string'}, 
						{name: 'productPromoStatusId', type: 'string'}, 
						{name: 'createdDate', type: 'date', other: 'Timestamp'}
						]"/>
<#assign columnlist="{text: '${uiLabelMap.DelysPromoProductPromoId}', dataField: 'productPromoId', width: '180px', align: 'center', 
		        			cellsrenderer: function(row, colum, value) {
	                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	                        	return \"<span><a href='/delys/control/ViewMarketingPromos?productPromoId=\" + data.productPromoId + \"'>\" + data.productPromoId + \"</a></span>\";
	                        }
						}, 
						{text: '${uiLabelMap.DelysPromoPromotionName}', dataField: 'promoName', align: 'center'},
						{text: '${uiLabelMap.DelysPromotionType}', dataField: 'productPromoTypeId', width: '180px', align: 'center', filtertype: 'checkedlist', 
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
						{text: '${uiLabelMap.DACreateDate}', dataField: 'createdDate', width: '160px', align: 'center', cellsformat: 'dd/MM/yyyy - hh:mm:ss'}, 
						{text: '${uiLabelMap.DelysBudgetTotal}', dataField: 'budgetTotalId', width: '100px', cellsalign: 'center', align: 'center'}, 
						{text: '${uiLabelMap.DelysMiniRevenue}', dataField: 'revenueMiniId', width: '100px', cellsalign: 'center', align: 'center'}, 
						{text: '${uiLabelMap.DelysProductPromoStatusId}', dataField: 'productPromoStatusId', width: '160px', cellsalign: 'center', align: 'center', filtertype: 'checkedlist', 
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
		url="jqxGeneralServicer?sname=JQGetListProductPromo"/>
