<#assign isRoleEmployee = hasOlbPermission("MODULE", "PRODPROMOTION_EXT_VIEW", "")>
<#assign isRoleDistributor = hasOlbPermission("MODULE", "DIS_PRODPROMOTION", "")>
<#assign listProductPromoExtType = delegator.findByAnd("ProductPromoExtType", null, null, true)!/>
<script type="text/javascript">
	var promoExtTypeData = [
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
 			if ("PROMO_ACCEPTED" == data.statusId) {
 				if (data.fromDate <= now) {
 					if (data.thruDate == null || (data.thruDate >= now)) return "background-running";
 				} else {
 					return "background-prepare";
 				}
 			} else if ("PROMO_CREATED" == data.statusId) {
 				return "background-waiting";
 			}
 		}
    }
</script>
<#if isRoleEmployee>
	<#assign serviceUrlName = "JQListProductPromoExt"/>
</#if>
<#if promoForDistributor?exists && promoForDistributor && isRoleDistributor>
	<#assign serviceUrlName = "JQListProductPromoExt&isCustomer=Y"/>
	<#assign focusMenu = "&fc=dis"/>
</#if>
<#if promoForRetailOutlet?exists && promoForRetailOutlet && isRoleDistributor>
	<#assign serviceUrlName = "JQListProductPromoExt&isOwner=Y"/>
	<#assign focusMenu = "&fc=reo"/>
</#if>
<#assign dataField = "[
			{name: 'productPromoId', type: 'string'}, 
			{name: 'productPromoTypeId', type: 'string'}, 
			{name: 'promoName', type: 'string'}, 
			{name: 'createdDate', type: 'date', other: 'Timestamp'}, 
			{name: 'fromDate', type: 'date', other: 'Timestamp'}, 
			{name: 'thruDate', type: 'date', other: 'Timestamp'}, 
			{name: 'statusId', type: 'string'}, 
			{name: 'organizationPartyId', type: 'string'}, 
		]"/>
<#assign columnlist = "
			{text: '${uiLabelMap.BSProductPromoId}', dataField: 'productPromoId', width: '12%', cellClassName: cellClassjqxPromotion, 
    			cellsrenderer: function(row, colum, value) {
            		return '<span><a href=\"viewPromotionExt?productPromoId=' + value + '${focusMenu?default('')}\">' + value + '</a></span>';
                }
			}, 
			{text: '${uiLabelMap.BSPromoName}', dataField: 'promoName', cellClassName: cellClassjqxPromotion},
			{text: '${uiLabelMap.BSProgramType}', dataField: 'productPromoTypeId', width: '12%', cellClassName: cellClassjqxPromotion, filtertype: 'checkedlist', 
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
							widget.jqxDropDownList({source: records, displayMember: 'typeId', valueMember: 'typeId',
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
			{text: '${uiLabelMap.BSCreateDate}', dataField: 'createdDate', width: '14%', cellsformat: 'dd/MM/yyyy - HH:mm:ss', cellClassName: cellClassjqxPromotion}, 
			{text: '${uiLabelMap.BSFromDate}', dataField: 'fromDate', width: '14%', cellsformat: 'dd/MM/yyyy - HH:mm:ss', cellClassName: cellClassjqxPromotion}, 
			{text: '${uiLabelMap.BSThruDate}', dataField: 'thruDate', width: '14%', cellsformat: 'dd/MM/yyyy - HH:mm:ss', cellClassName: cellClassjqxPromotion}, 
			{text: '${uiLabelMap.BSOrganization}', dataField: 'organizationPartyId', width: '10%', cellClassName: cellClassjqxPromotion},
			"/>
<#if isRoleEmployee>
<#assign columnlist = columnlist + "
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
  		"/>
</#if>
<#assign tmpCreateUrl = ""/>
<#if hasOlbPermission("MODULE", "PRODPROMOTION_EXT_NEW", "")>
	<#assign tmpCreateUrl = "icon-plus open-sans@${uiLabelMap.wgaddnew}@newPromotionExt"/>
</#if>
<#assign tmpDelete = "false"/>
<#if hasOlbPermission("MODULE", "PRODPROMOTION_EXT_DELETE", "")>
	<#assign tmpDelete = "true"/>
</#if>
<@jqGrid id="jqxPromotion" url="jqxGeneralServicer?sname=${serviceUrlName?if_exists}" columnlist=columnlist dataField=dataField 
		viewSize="20" showtoolbar="true" filtersimplemode="true" showstatusbar="false" 
		removeUrl="jqxGeneralServicer?sname=deleteProductPromoExt&jqaction=C" deleteColumn="productPromoId" deleterow=tmpDelete 
		customcontrol1=tmpCreateUrl mouseRightMenu="true" contextMenuId="contextMenu"/>

<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <#if tmpCreateUrl != ""><li><i class="fa-plus-circle open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSCreateNew)}</li></#if>
	    <#if tmpDelete?exists && tmpDelete == "true"><li><i class="icon-trash open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSDeleteSelectedRow)}</li></#if>
	</ul>
</div>

<#include "script/promotionExtListScript.ftl"/>
