<#assign isRoleEmployee = hasOlbPermission("MODULE", "PRODPROMOTION_VIEW", "")>
<#assign isRoleDistributor = hasOlbPermission("MODULE", "DIS_PRODPROMOTION", "")>
<#assign promotionStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "PROMO_STATUS"}, null, false)!/>
<#assign promotionStates = delegator.findByAnd("StatusItem", {"statusTypeId" : "STATE_STATUS"}, null, false)!/>
<script type="text/javascript">
	var promotionStatusData = [
	<#if promotionStatuses?exists>
		<#list promotionStatuses as statusItem>
		{	statusId: '${statusItem.statusId}',
			description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	var promotionStateData = [
	<#if promotionStates?exists>
		<#list promotionStates as statusItem>
		{	statusId: '${statusItem.statusId}',
			description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
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
 					if (data.thruDate == null || (data.thruDate >= now)) {
 						if (data.stateId == null || data.stateId != "STATE_FINISH") {
 							return "background-running";
 						}
					}
 				} else {
 					if (data.thruDate == null || (data.thruDate >= now)) return "background-prepare";
 				}
 			} else if ("PROMO_CREATED" == data.statusId) {
 				if (data.thruDate == null || (data.thruDate >= now)) return "background-waiting";
 			}
 		}
    }
</script>
<#assign dataField = "[
			{name: 'productPromoId', type: 'string'}, 
			{name: 'promoName', type: 'string'}, 
			{name: 'createdDate', type: 'date', other: 'Timestamp'}, 
			{name: 'fromDate', type: 'date', other: 'Timestamp'}, 
			{name: 'thruDate', type: 'date', other: 'Timestamp'}, 
			{name: 'statusId', type: 'string'}, 
			{name: 'stateId', type: 'string'}, 
			{name: 'organizationPartyId', type: 'string'}, 
			{name: 'storeNames', type: 'string'}, 
		]"/>
<#assign columnlist = "
			{text: '${uiLabelMap.BSProductPromoId}', dataField: 'productPromoId', width: 140, cellClassName: cellClassjqxPromotion, 
    			cellsrenderer: function(row, colum, value) {
            		return '<span><a href=\"viewPromotion?productPromoId=' + value + '${focusMenu?default('')}\">' + value + '</a></span>';
                }
			}, 
			{text: '${uiLabelMap.BSPromoName}', dataField: 'promoName', minwidth: 120, cellClassName: cellClassjqxPromotion},
			{text: '${uiLabelMap.BSPSSalesChannel}', dataField: 'storeNames', width: 120, cellClassName: cellClassjqxPromotion},
		"/>
<#if !(promoForDistributor?exists && promoForDistributor && isRoleDistributor)>
<#assign columnlist = columnlist + "
			{text: '${uiLabelMap.BSOrganization}', dataField: 'organizationPartyId', width: 80, cellClassName: cellClassjqxPromotion},
		"/>
</#if>
<#assign columnlist = columnlist + "
			{text: '${uiLabelMap.BSCreateDate}', dataField: 'createdDate', width: 100, cellsformat: 'dd/MM/yyyy', cellClassName: cellClassjqxPromotion, filtertype:'range'}, 
			{text: '${uiLabelMap.BSFromDate}', dataField: 'fromDate', width: 140, cellsformat: 'dd/MM/yyyy', cellClassName: cellClassjqxPromotion, filtertype:'range', 
				cellsrenderer: function(row, colum, value) {
					return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
				}
			}, 
			{text: '${uiLabelMap.BSThruDate}', dataField: 'thruDate', width: 140, cellsformat: 'dd/MM/yyyy', cellClassName: cellClassjqxPromotion, filtertype:'range', 
				cellsrenderer: function(row, colum, value) {
					return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
				}
			}, 
		"/>
<#if isRoleEmployee>
<#assign columnlist = columnlist + "
			{text: '${uiLabelMap.BSStatus}', dataField: 'statusId', width: 100, filtertype: 'checkedlist', cellClassName: cellClassjqxPromotion, 
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
			{text: '${uiLabelMap.BSState}', dataField: 'stateId', width: 80, filtertype: 'checkedlist', cellClassName: cellClassjqxPromotion, 
				cellsrenderer: function(row, column, value){
					if (promotionStateData.length > 0) {
						for(var i = 0 ; i < promotionStateData.length; i++){
							if (value == promotionStateData[i].statusId){
								return '<span title =\"' + promotionStateData[i].description +'\">' + promotionStateData[i].description + '</span>';
							}
						}
					}
					return '<span title=' + value +'>' + value + '</span>';
			 	}, 
			 	createfilterwidget: function (column, columnElement, widget) {
					if (promotionStateData.length > 0) {
						var records;
						var filterDataAdapter = new $.jqx.dataAdapter(promotionStateData, {
							autoBind: true
						});
						records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
							renderer: function(index, label, value){
								if (promotionStateData.length > 0) {
									for(var i = 0; i < promotionStateData.length; i++){
										if(promotionStateData[i].statusId == value){
											return '<span>' + promotionStateData[i].description + '</span>';
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
<#if isRoleEmployee>
	<#assign serviceUrlName = "JQListProductPromoDisplay"/>
</#if>
<#if promoForDistributor?exists && promoForDistributor && isRoleDistributor>
	<#assign serviceUrlName = "JQListProductPromoDisplay&isCustomer=Y"/>
	<#--<#assign focusMenu = "&fc=dis"/>-->
</#if>
<#if promoForRetailOutlet?exists && promoForRetailOutlet && isRoleDistributor>
	<#if isDistributor?exists && isDistributor>
		<#assign serviceUrlName = "JQListProductPromoDisplay&isOwner=Y"/>
	<#else>
		<#assign serviceUrlName = "JQListProductPromoDisplay&isSeller=Y&checkActive=Y"/>
	</#if>
	<#--<#assign focusMenu = "&fc=reo"/>-->
</#if>
<#assign tmpCreateUrl = ""/>
<#if hasOlbPermission("MODULE", "PRODPROMOTION_NEW", "")>
	<#assign tmpCreateUrl = "icon-plus open-sans@${uiLabelMap.wgaddnew}@newPromotion"/>
</#if>
<#assign tmpDelete = "false"/>
<#--
<#if hasOlbPermission("MODULE", "PRODPROMOTION_DELETE", "")>
	<#assign tmpDelete = "true"/>
</#if>
-->
<#assign contextMenuItemId = "ctxmnupromolst">
<@jqGrid id="jqxPromotion" url="jqxGeneralServicer?sname=${serviceUrlName?if_exists}" columnlist=columnlist dataField=dataField 
		viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false" 
		removeUrl="jqxGeneralServicer?sname=deleteProductPromoCustom&jqaction=C" deleteColumn="productPromoId" deleterow=tmpDelete 
		customcontrol1=tmpCreateUrl mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemId}" bindresize="true"/>

<div id='contextMenu_${contextMenuItemId}' style="display:none">
	<ul>
		<li id="${contextMenuItemId}_viewdetailnewtab"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}</li>
	    <li id="${contextMenuItemId}_viewdetail"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li id="${contextMenuItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <#if tmpCreateUrl != ""><li id="${contextMenuItemId}_createnew"><i class="fa-plus-circle open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSCreateNew)}</li></#if>
	    <#if tmpDelete?exists && tmpDelete == "true"><li id="${contextMenuItemId}_delete"><i class="fa-trash open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSDeleteSelectedRow)}</li></#if>
	</ul>
</div>

<#include "script/promotionListScript.ftl"/>
