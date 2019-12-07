<script type="text/javascript">
	var cellClassjqxPromotion = function (row, columnfield, value) {
		var data = $("#jqxPromotion").jqxGrid("getrowdata", row);
		var returnValue = "";
		if (typeof(data) != "undefined") {
			var now = new Date();
			if (data.thruDate && data.thruDate < now) {
				return "jqx-grid-cell-expired";
			}
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
<#assign dataField = "[
			{ name: 'productPromoId', type: 'string' },
			{ name: 'promoName', type: 'string' },
			{ name: 'createdDate', type: 'date', other: 'Timestamp' },
			{ name: 'fromDate', type: 'date', other: 'Timestamp' },
			{ name: 'thruDate', type: 'date', other: 'Timestamp' },
			{ name: 'statusId', type: 'string' },
			{ name: 'organizationPartyId', type: 'string' }]"/>

<#assign columnlist = "
			{ text: '${uiLabelMap.BSProductPromoId}', dataField: 'productPromoId', width: 180, cellClassName: cellClassjqxPromotion,
				cellsrenderer: function(row, colum, value) {
					return '<span><a href=\"viewPromotionPO?productPromoId=' + value + '${focusMenu?default('')}\">' + value + '</a></span>';
				}
			},
			{ text: '${uiLabelMap.BSPromoName}', dataField: 'promoName', minWidth: 200, cellClassName: cellClassjqxPromotion},
			{ text: '${uiLabelMap.BSCreateDate}', dataField: 'createdDate', width: 180, cellsformat: 'dd/MM/yyyy - HH:mm:ss', cellClassName: cellClassjqxPromotion },
			{ text: '${uiLabelMap.BSFromDate}', dataField: 'fromDate', width: 180, cellsformat: 'dd/MM/yyyy - HH:mm:ss', cellClassName: cellClassjqxPromotion },
			{ text: '${uiLabelMap.BSThruDate}', dataField: 'thruDate', width: 180, cellsformat: 'dd/MM/yyyy - HH:mm:ss', cellClassName: cellClassjqxPromotion },
			{ text: '${uiLabelMap.BSStatus}', dataField: 'statusId', width: 150, filtertype: 'checkedlist', cellClassName: cellClassjqxPromotion,
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
	   			}
			},
			{ text: '${uiLabelMap.BSOrganization}', dataField: 'organizationPartyId', width: 100, cellClassName: cellClassjqxPromotion }"/>

<#assign tmpCreateUrl = ""/>
<#if hasOlbPermission("MODULE", "PROMOPO", "CREATE")>
	<#assign tmpCreateUrl = "icon-plus open-sans@${uiLabelMap.wgaddnew}@createNewPromotionPO"/>
</#if>
<#if !tmpDelete?exists>
	<#assign tmpDelete = "false"/>
	<#if hasOlbPermission("MODULE", "PROMOPO", "DELETE")>
		<#assign tmpDelete = "true"/>
	</#if>
</#if>
<#if !customLoadFunction?exists>
	<#assign customLoadFunction="false"/>
</#if>

<#assign contextMenuItemId = "ctxmnupromolst">
<@jqGrid id="jqxPromotion" url="jqxGeneralServicer?sname=JQListProductPromoPO&partyId=${parameters.partyId?if_exists}" columnlist=columnlist dataField=dataField
	viewSize="20" showtoolbar="true" filtersimplemode="true" showstatusbar="false" customTitleProperties="BSListProductPromotion" customLoadFunction=customLoadFunction
	removeUrl="jqxGeneralServicer?sname=deleteProductPromoCustomPO&jqaction=C" deleteColumn="productPromoId" deleterow=tmpDelete clearfilteringbutton="true"
	customcontrol1=tmpCreateUrl mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemId}" bindresize="true"/>

<div id="contextMenu_${contextMenuItemId}" style="display:none">
	<ul>
		<li id="${contextMenuItemId}_viewdetailnewtab"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}</li>
		<li id="${contextMenuItemId}_viewdetail"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
		<li id="${contextMenuItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
		<#if tmpDelete?exists && tmpDelete == "true"><li id="${contextMenuItemId}_delete"><i class="fa icon-trash"></i>${StringUtil.wrapString(uiLabelMap.BSDeleteSelectedRow)}</li></#if>
	</ul>
</div>

<#include "script/promotionListScript.ftl"/>
