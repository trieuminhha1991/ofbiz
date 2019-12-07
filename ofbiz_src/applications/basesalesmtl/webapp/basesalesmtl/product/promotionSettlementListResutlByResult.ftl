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
			{name: 'statusId', type: 'string'}, 
			
			{name: 'productCode', type: 'string'}, 
			{name: 'quantityTotal', type: 'string'}, 
			{name: 'quantityRemain', type: 'string'}, 
			{name: 'promoSettlementResultId', type: 'string'}, 
			{name: 'quantityAccept', type: 'string'}, 
			{name: 'partyId', type: 'string'}, 
			{name: 'comment', type: 'string'}, 
		]"/>
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
			
			{text: '${uiLabelMap.BSResultId}', dataField: 'promoSettlementResultId', width: '10%', cellClassName: cellClassjqxPromotion},
			{text: '${uiLabelMap.BSPartyId}', dataField: 'partyId', width: '10%', cellClassName: cellClassjqxPromotion},
			{text: '${uiLabelMap.BSProductId}', dataField: 'productCode', width: '10%', cellClassName: cellClassjqxPromotion},
			{text: '${uiLabelMap.BSQuantity}', dataField: 'quantityAccept', width: '10%', cellClassName: cellClassjqxPromotion},
			{text: '${uiLabelMap.BSPaidQty}', dataField: 'quantityTotal', width: '10%', cellClassName: cellClassjqxPromotion},
			{text: '${uiLabelMap.BSRemainQty}', dataField: 'quantityRemain', width: '10%', cellClassName: cellClassjqxPromotion},
			{text: '${uiLabelMap.BSComment}', dataField: 'comment', width: '10%', cellClassName: cellClassjqxPromotion},
  		"/>
<#if hasPermissionView>
	<#assign serviceUrlName = "JQListProductPromoSettlementOrderCommit"/>
</#if>
<@jqGrid id="jqxPromotion" url="jqxGeneralServicer?sname=${serviceUrlName?if_exists}" columnlist=columnlist dataField=dataField 
		viewSize="20" showtoolbar="true" filtersimplemode="true" showstatusbar="false" 
		customcontrol1="" mouseRightMenu="true" contextMenuId="contextMenu" jqGridMinimumLibEnable="true"/>

<div id='contextMenu' style="display:none">
	<ul>
	    <#--
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}</li>
	    -->
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>

<#assign promotionStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "PROMO_SETTLE_STATUS"}, null, false)!/>
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
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSRefresh = '${StringUtil.wrapString(uiLabelMap.BSRefresh)}';
	uiLabelMap.BSViewDetail = '${StringUtil.wrapString(uiLabelMap.BSViewDetail)}';
	uiLabelMap.BSViewDetailInNewTab = '${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}';
	uiLabelMap.BSDeleteSelectedRow = '${StringUtil.wrapString(uiLabelMap.BSDeleteSelectedRow)}';
	uiLabelMap.BSCreateNew = '${StringUtil.wrapString(uiLabelMap.BSCreateNew)}';
	
	$(function(){
		OlbQuotationList.init();
	});
	var OlbQuotationList = (function(){
		var init = function(){
			initQuickMenu();
		};
		var initQuickMenu = function(){
			jOlbUtil.contextMenu.create($("#contextMenu"));
			$("#contextMenu").on('itemclick', function (event) {
				var args = event.args;
		        var rowindex = $("#jqxPromotion").jqxGrid('getselectedrowindex');
		        var tmpKey = $.trim($(args).text());
		        if (tmpKey == uiLabelMap.BSRefresh) {
		        	$("#jqxPromotion").jqxGrid('updatebounddata');
		        } else if (tmpKey == uiLabelMap.BSViewDetail) {
		        	var data = $("#jqxPromotion").jqxGrid("getrowdata", rowindex);
					if (data != undefined && data != null) {
						var productPromoId = data.productPromoId;
						var url = 'viewPromotionExt?productPromoId=' + productPromoId;
						var win = window.open(url, '_self');
						win.focus();
					}
		        } else if (tmpKey == uiLabelMap.BSViewDetailInNewTab) {
		        	var data = $("#jqxPromotion").jqxGrid("getrowdata", rowindex);
					if (data != undefined && data != null) {
						var productPromoId = data.productPromoId;
						var url = 'viewPromotionExt?productPromoId=' + productPromoId;
						var win = window.open(url, '_blank');
						win.focus();
					}
		        } else if (tmpKey == uiLabelMap.BSDeleteSelectedRow) {
		        	$("#deleterowbuttonjqxPromotion").click();
		        } else if (tmpKey == uiLabelMap.BSCreateNew) {
		        	var win = window.open("newPromotionExt", "_self");
		        	window.focus();
		        }
			});
		};
		return {
			init: init
		};
	}());
</script>
