<#assign hasPermissionView = hasOlbPermission("MODULE", "PROMOSETTLEMENT_VIEW", "VIEW")>
<#assign hasPermissionCreate = hasOlbPermission("MODULE", "PROMOSETTLEMENT_NEW", "CREATE")>
<#assign listProductPromoExtType = delegator.findByAnd("ProductPromoExtType", null, null, true)!/>
<script type="text/javascript">
	var promoExtTypeData = [
		{typeId: 'ORDER_PROMO', description: '${StringUtil.wrapString(uiLabelMap.BSOrderPromotion)}'}, 
	<#if listProductPromoExtType?exists>
		<#list listProductPromoExtType as typeItem>
		{	typeId: '${typeItem.productPromoTypeId}',
			description: '${StringUtil.wrapString(typeItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	var cellClassjqxPromotion = function (row, columnfield, value) {
 		var data = $('#jqxPromotion').jqxGrid('getrowdata', row);
 		var returnValue = "";
 		if (typeof(data) != 'undefined') {
 			var now = new Date();
 			if ("PSETTLE_APPROVED" == data.statusId) {
 				if (data.fromDate <= now) {
 					if (data.thruDate == null || (data.thruDate >= now)) return "background-running";
 				} else {
 					return "background-prepare";
 				}
 			} else if ("PSETTLE_CREATED" == data.statusId) {
 				return "background-waiting";
 			}
 		}
    }
</script>
<#assign dataField = "[
			{name: 'promoSettlementId', type: 'string'},
			{name: 'promoSettlementName', type: 'string'},
			{name: 'productPromoId', type: 'string'},
			{name: 'productPromoExtId', type: 'string'},
			{name: 'createdDate', type: 'date', other: 'Timestamp'},
			{name: 'fromDate', type: 'date', other: 'Timestamp'},
			{name: 'thruDate', type: 'date', other: 'Timestamp'},
			{name: 'statusId', type: 'string'},
			{name: 'createdBy', type: 'string'}]"/>
<#assign columnlist = "
			{text: '${uiLabelMap.BSPromoSettlementId}', dataField: 'promoSettlementId', width: '12%', cellClassName: cellClassjqxPromotion, 
    			cellsrenderer: function(row, colum, value) {
            		return '<span><a href=\"viewPromoSettle?promoSettlementId=' + value + '\">' + value + '</a></span>';
                }
			},
			{text: '${uiLabelMap.BSPromoSettlementName}', dataField: 'promoSettlementName', cellClassName: cellClassjqxPromotion, width: '15%'},
			{text: '${uiLabelMap.BSProductPromoId}', dataField: 'productPromoId', cellClassName: cellClassjqxPromotion, width: '12%'},
			{text: '${uiLabelMap.BSProductPromoExtId}', dataField: 'productPromoExtId', cellClassName: cellClassjqxPromotion, width: '12%'},
			{text: '${uiLabelMap.BSCreateDate}', dataField: 'createdDate', width: '14%', cellsformat: 'dd/MM/yyyy - HH:mm:ss', cellClassName: cellClassjqxPromotion}, 
			{text: '${uiLabelMap.BSFromDate}', dataField: 'fromDate', width: '14%', cellsformat: 'dd/MM/yyyy - HH:mm:ss', cellClassName: cellClassjqxPromotion}, 
			{text: '${uiLabelMap.BSThruDate}', dataField: 'thruDate', width: '14%', cellsformat: 'dd/MM/yyyy - HH:mm:ss', cellClassName: cellClassjqxPromotion}, 
			{text: '${uiLabelMap.BSStatus}', dataField: 'statusId', width: '10%', filtertype: 'checkedlist', cellClassName: cellClassjqxPromotion, 
				cellsrenderer: function(row, column, value){
					if (promotionStatusData.length > 0) {
						for(var i = 0 ; i < promotionStatusData.length; i++){
							if (value == promotionStatusData[i].statusId){
								return '<span title =\"' + promotionStatusData[i].description +'\">' + promotionStatusData[i].description + '</span>';
							}
						}
					}
					return '<span title=' + value +'>' + value + '</span>';
			 	},
			 	createfilterwidget: function (column, columnElement, widget) {
			 		if (promotionStatusData.length > 0) {
						var filterDataAdapter = new $.jqx.dataAdapter(promotionStatusData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
							renderer: function(index, label, value){
								if (promotionStatusData.length > 0) {
									for(var i = 0; i < promotionStatusData.length; i++){
										if(promotionStatusData[i].statusId == value){
											return '<span>' + promotionStatusData[i].description + '</span>';
										}
									}
								}
								return value;
							}
						});
						widget.jqxDropDownList('checkAll');
					}
	   			}
			},
			{text: '${uiLabelMap.BSCreatedBy}', dataField: 'createdBy', width: '10%', cellClassName: cellClassjqxPromotion}"/>
<#if hasPermissionView>
	<#assign serviceUrlName = "JQListProductPromoSettlement"/>
</#if>
<#assign tmpCreateUrl = ""/>
<#if hasPermissionCreate>
	<#assign tmpCreateUrl = "icon-plus open-sans@${uiLabelMap.wgaddnew}@javascript:OlbPromoSettlementNew.openNewPopup();"/>
</#if>
<@jqGrid id="jqxPromotion" url="jqxGeneralServicer?sname=${serviceUrlName?if_exists}" columnlist=columnlist dataField=dataField 
		viewSize="20" showtoolbar="true" filtersimplemode="true" showstatusbar="false" 
		customcontrol1=tmpCreateUrl mouseRightMenu="true" contextMenuId="contextMenu" jqGridMinimumLibEnable="true"/>

<#include "promotionSettlementNewPopup.ftl">

<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>

<#include "script/promotionSettlementListScript.ftl"/>

<#--
{text: '${uiLabelMap.BSPromoSettlementTypeId}', dataField: 'promoSettlementTypeId', width: '12%', cellClassName: cellClassjqxPromotion, filtertype: 'checkedlist', 
					cellsrenderer: function(row, column, value){
						if (promoExtTypeData.length > 0) {
							for(var i = 0 ; i < promoExtTypeData.length; i++){
    							if (value == promoExtTypeData[i].typeId){
    								return '<span title =\"' + promoExtTypeData[i].description +'\">' + promoExtTypeData[i].description + '</span>';
    							}
    						}
						}
						return '<span title=' + value +'>' + value + '</span>';
				 	}, 
				 	createfilterwidget: function (column, columnElement, widget) {
				 		if (promoExtTypeData.length > 0) {
							var filterDataAdapter = new $.jqx.dataAdapter(promoExtTypeData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
								renderer: function(index, label, value){
									if (promoExtTypeData.length > 0) {
										for(var i = 0; i < promoExtTypeData.length; i++){
											if(promoExtTypeData[i].typeId == value){
												return '<span>' + promoExtTypeData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
						}
		   			}
				
			},
-->