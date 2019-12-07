
<#if !isPurchaseSelfie?exists><#assign isPurchaseSelfie = false/></#if>

<script type="text/javascript">
	var filterObjData = new Object();
	
	var partyIdInput = '${parameters.partyId?if_exists}';
	<#assign orderStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "ORDER_STATUS"}, null, false)/>
	var orderStatusData = [
	<#if orderStatuses?exists>
		<#list orderStatuses as statusItem>
		{	statusId: '${statusItem.statusId}',
			description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	<#assign priorityList = delegator.findByAnd("Enumeration", {"enumTypeId" : "ORDER_PRIORITY"}, ["sequenceId"], false)/>
	var priorityData = [
	<#if priorityList?exists>
		<#list priorityList as priority>
		{	enumId: "${priority.enumId}",
			description: "${StringUtil.wrapString(priority.description?default(""))}",
		},
		</#list>
	</#if>
	];
	
	var favorDeliveryData = [
		{id: "_NA_", description: "${StringUtil.wrapString(uiLabelMap.BSNotConfirmYet)}"},
		{id: "Y", description: "${StringUtil.wrapString(uiLabelMap.ThroughTHTransfer)}"},
		{id: "N", description: "${StringUtil.wrapString(uiLabelMap.BSInternalDelivery)}"},
	];
	var productStoreData = [];
	<#if !isOwnerDistributor?exists>
		<#assign isOwnerDistributor = false/>
	</#if>
	<#if isPurchaseSelfie>
		<#assign productStoresBySeller = Static['com.olbius.basesales.product.ProductStoreWorker'].getListProductStoreSellByCustomer(delegator, userLogin)!/>
	<#else>
		<#assign productStoresBySeller = Static['com.olbius.basesales.product.ProductStoreWorker'].getListProductStore(delegator, userLogin, isOwnerDistributor)!/>
	</#if>
	<#if productStoresBySeller?exists>
	productStoreData = [
		<#list productStoresBySeller as productStore>
			{	storeName : "${productStore.storeName?default('')}",
				productStoreId : "${productStore.productStoreId}"
			},
		</#list>
	];
	</#if>
	
	var cellClass = function (row, columnfield, value) {
 		var data = $('#listOrderCustomer').jqxGrid('getrowdata', row);
 		if (typeof(data) != 'undefined') {
 			if ("ORDER_CANCELLED" == data.statusId) {
 				return "background-cancel";
 			} else if ("ORDER_CREATED" == data.statusId) {
 				return "background-important-nd";
 			} else if ("ORDER_APPROVED" == data.statusId) {
 				return "background-prepare";
 			}
 			<#-- back favor ... -->
 		}
    }
    <#-- back favor ... 
    <style>
		.background-not-favor-delivery {
			background: #FFFF55 !important; 
		}
	</style>
    if (data.isFavorDelivery != null && data.isFavorDelivery == "Y") {
		return "background-favor-delivery";
	} else if (data.isFavorDelivery == null) {
		return "background-important-nd";
	}
    -->
</script>
<#if !timeout?exists>
<#assign timeout= "0"/>
</#if>
<#if !customLoadFunction?exists>
<#assign customLoadFunction= "false"/>
</#if>
<#if !jqGridMinimumLibEnable?exists>
<#assign jqGridMinimumLibEnable= ""/>
</#if>

<#assign dataField="[
				{ name: 'orderDate', type: 'date', other: 'Timestamp'},
				{ name: 'orderId', type: 'string'},
				{ name: 'orderName', type: 'string'},
				{ name: 'fullDeliveryDate', type: 'date', other: 'Timestamp'},
				{ name: 'estimatedDeliveryDate', type: 'date', other: 'Timestamp'},
				{ name: 'shipBeforeDate', type: 'date', other: 'Timestamp'},
				{ name: 'shipAfterDate', type: 'date', other: 'Timestamp'},
				{ name: 'customerId', type: 'string'},
				{ name: 'customerCode', type: 'string'},
				{ name: 'customerFullName', type: 'string'},
				{ name: 'fullContactNumber', type: 'string'},
				{ name: 'productStoreId', type: 'string'},
				{ name: 'grandTotal', type: 'number'},
				{ name: 'statusId', type: 'string'},
				{ name: 'currencyUom', type: 'string'},
				{ name: 'agreementId', type: 'string'},
				{ name: 'agreementCode', type: 'string'},
				{ name: 'priority', type: 'string'},
				{ name: 'isFavorDelivery', type: 'string'},
				{ name: 'createdBy', type: 'string'},
				{ name: 'sellerCode', type: 'string'},
			]"/>
<#assign viewOrderUrl = "viewOrder"/>
<#if viewOrderUrl2?exists><#assign viewOrderUrl = viewOrderUrl2/></#if>
<#assign columnlist = "
				{ text: '${uiLabelMap.BSOrderId}', dataField: 'orderId', width: 120, pinned: true,  
					cellsrenderer: function(row, colum, value) {
						return \"<span><a href='${viewOrderUrl}?orderId=\" + value + \"'>\" + value + \"</a></span>\";
					}
				},"/>
<#if Static["com.olbius.basesales.util.SalesPartyUtil"].isCallCenterManager(delegator, userLogin.userLoginId) || isOwnerDistributor>
<#assign columnlist = columnlist + "
				{ text: '${uiLabelMap.BSCreatedBy}', dataField: 'createdBy', pinned: true, width: 120},
				"/>
</#if>
<#if viewOrderUrl2?exists && isOwnerDistributor>
<#assign columnlist = columnlist + "
				{ text: '${uiLabelMap.BSPayToParty}', dataField: 'sellerCode', width: 120, cellClassName: cellClass},
				"/>
</#if>
<#if !isPurchaseSelfie>
<#if !(isOwnerDistributor?exists && isOwnerDistributor)>
<#assign columnlist = columnlist + "
				{ text: '${uiLabelMap.BSSalesChannel}', dataField: 'productStoreId', width: 140, cellClassName: cellClass, filtertype: 'checkedlist', 
					cellsrenderer: function(row, column, value){
						if (productStoreData.length > 0) {
							for(var i = 0 ; i < productStoreData.length; i++){
    							if (value == productStoreData[i].productStoreId){
    								return '<span title =\"' + productStoreData[i].storeName +'\">' + productStoreData[i].storeName + '</span>';
    							}
    						}
						}
						return '<span title=' + value +'>' + value + '</span>';
				 	}, 
				 	createfilterwidget: function (column, columnElement, widget) {
				 		if (productStoreData.length > 0) {
				 			var filterDataAdapter = new $.jqx.dataAdapter(productStoreData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'productStoreId', valueMember: 'productStoreId',
								renderer: function(index, label, value){
									if (productStoreData.length > 0) {
										for(var i = 0; i < productStoreData.length; i++){
											if(productStoreData[i].productStoreId == value){
												return '<span>' + productStoreData[i].storeName + '</span>';
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
			">
</#if>
</#if>
<#assign columnlist = columnlist + "
				{ text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', minwidth: 120, cellClassName: cellClass, filtertype: 'checkedlist', 
					cellsrenderer: function(row, column, value){
						if (orderStatusData.length > 0) {
							for(var i = 0 ; i < orderStatusData.length; i++){
    							if (value == orderStatusData[i].statusId){
    								return '<span title =\"' + orderStatusData[i].description +'\">' + orderStatusData[i].description + '</span>';
    							}
    						}
						}
						return '<span title=' + value +'>' + value + '</span>';
				 	}, 
				 	createfilterwidget: function (column, columnElement, widget) {
				 		if (orderStatusData.length > 0) {
							var filterDataAdapter = new $.jqx.dataAdapter(orderStatusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value){
									if (orderStatusData.length > 0) {
										for(var i = 0; i < orderStatusData.length; i++){
											if(orderStatusData[i].statusId == value){
												return '<span>' + orderStatusData[i].description + '</span>';
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
				{ text: '${uiLabelMap.BSCreateDate}', dataField: 'orderDate', width: 140, cellClassName: cellClass, cellsformat: 'dd/MM/yyyy', filtertype:'range',
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}
				},
				{ text: '${uiLabelMap.BSDesiredDeliveryDate}', dataField: 'fullDeliveryDate', width: 200, cellClassName: cellClass, cellsformat: 'dd/MM/yyyy', filtertype:'range',
					cellsrenderer: function(row, colum, value) {
						var data = $('#listOrderCustomer').jqxGrid('getrowdata', row);
						if (typeof(data) != 'undefined') {
							var returnStr = \"<span>\";
							if (data.estimatedDeliveryDate != null) {
								returnStr += jOlbUtil.dateTime.formatFullDate(data.estimatedDeliveryDate)
								if (data.shipAfterDate != null || data.shipBeforeDate != null) {
									returnStr += ' (';
									returnStr += jOlbUtil.dateTime.formatFullDate(data.shipAfterDate) + ' - ' + jOlbUtil.dateTime.formatFullDate(data.shipBeforeDate);
									returnStr += ')';
								}
							} else {
								returnStr += jOlbUtil.dateTime.formatFullDate(data.shipAfterDate) + ' - ' + jOlbUtil.dateTime.formatFullDate(data.shipBeforeDate);
							}
							returnStr += \"</span>\";
							return returnStr;
						}
					}
				},
			">
<#if !isPurchaseSelfie>
<#if hasOlbPermission("MODULE", "CRM_CALLCENTER", "")>
	<#assign columnlist = columnlist + "
				{ text: '${uiLabelMap.BSCustomer}', dataField: 'customerCode', width: 150, cellClassName: cellClass, hidden: '${fromCallCenter?if_exists}'=='Y',
					cellsrenderer: function(row, colum, value) {
						return \"<span><a href='Callcenter?partyId=\" + value + \"' target='_blank'>\" + value + \"</a></span>\";
					}
				},
			">
<#else>
	<#assign columnlist = columnlist + "{ text: '${uiLabelMap.BSCustomer}', dataField: 'customerCode', width: 150, cellClassName: cellClass, hidden: '${fromCallCenter?if_exists}'=='Y'},">
</#if>
<#assign columnlist = columnlist + "
				{ text: '${uiLabelMap.BSCustomerName}', dataField: 'customerFullName', width: 140, cellClassName: cellClass, hidden: '${fromCallCenter?if_exists}'=='Y'},
				{ text: '${uiLabelMap.BSPhoneNumber}', dataField: 'fullContactNumber', width: 120, cellClassName: cellClass, hidden: '${fromCallCenter?if_exists}'=='Y'},
				">
</#if>
<#if !isPurchaseSelfie>
	<#assign uiLabelMapTitleAmount = "${uiLabelMap.CommonAmount}"/>
<#else>
	<#assign uiLabelMapTitleAmount = "${uiLabelMap.BSValue}"/>
</#if>
<#assign columnlist = columnlist + "
				{ text: '${uiLabelMapTitleAmount}', dataField: 'grandTotal', width: 120, filtertype: 'number', cellClassName: cellClass, cellsalign: 'right', cellsformat: 'c', 
				 	cellsrenderer: function(row, column, value) {
				 		var str = '<div class=\"innerGridCellContent align-right\">';
				 		var data = $('#listOrderCustomer').jqxGrid('getrowdata', row);
				 		if (typeof(data) != 'undefined') {
					 		str += formatcurrency(value, data.currencyUom);
				 		} else {
							str += value;
						}
						str += '</div>';
						return str;
				 	}
				},
				{ text: '${uiLabelMap.BSAgreementCode}', dataField: 'agreementCode', width: 160, cellClassName: cellClass, 
					cellsrenderer: function(row, colum, value) {
						var data = $('#listOrderCustomer').jqxGrid('getrowdata', row);
						if (typeof(data) != 'undefined') {
							return \"<span><a href='AgreementDetail?agreementId=\" + data.agreementId + \"' target='_blank'>\" + value + \"</a></span>\";
						}
					}
				},
			">
<#if !isPurchaseSelfie>
<#assign columnlist = columnlist + "
				{ text: '${uiLabelMap.BSPriority}', dataField: 'priority', width: 100, cellClassName: cellClass, filtertype: 'checkedlist', 
					cellsrenderer: function(row, column, value){
						if (priorityData.length > 0) {
							for(var i = 0 ; i < priorityData.length; i++){
    							if (value == priorityData[i].enumId){
    								return '<span title =\"' + priorityData[i].description +'\">' + priorityData[i].description + '</span>';
    							}
    						}
						}
						return '<span title=' + value +'>' + value + '</span>';
				 	}, 
				 	createfilterwidget: function (column, columnElement, widget) {
				 		if (priorityData.length > 0) {
							var filterDataAdapter = new $.jqx.dataAdapter(priorityData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'enumId', valueMember: 'enumId',
								renderer: function(index, label, value){
									if (priorityData.length > 0) {
										for(var i = 0; i < priorityData.length; i++){
											if(priorityData[i].enumId == value){
												return '<span>' + priorityData[i].description + '</span>';
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

<#assign showLoader = "false">
<#assign showtoolbar = "${showtoolbar?default('true')}">
<#assign customcontrol1 = "icon-plus open-sans@${uiLabelMap.BSAddNew}@javascript: void(0);@quickCreateOrder()">
<#if !orderUrl?exists>
	<#assign isShowTitleProperty="true"/>
	<#assign showLoader = "true">
	<#if isOwnerDistributor?exists && isOwnerDistributor>
		<#assign orderUrl="jqxGeneralServicer?sname=JQListSalesOrderExternal&partyId=${parameters.partyId?if_exists}"/>
	<#else>
		
		<#if parameters.isPass?exists && parameters.isPass=='Y'>
			<#assign orderUrl="jqxGeneralServicer?sname=JQListSalesOrderPass&partyId=${parameters.partyId?if_exists}"/>
		<#else>	
			<#assign orderUrl="jqxGeneralServicer?sname=JQListSalesOrder&partyId=${parameters.partyId?if_exists}"/>
		</#if>
	</#if>
<#elseif !isShowTitleProperty?exists>
	<#assign isShowTitleProperty="false"/>
</#if>
<#if productPromoId?has_content>
	<#assign orderUrl=orderUrl+"&productPromoId=${productPromoId}"/>
</#if>

<#assign channelCodeParam = ""/>
<#if parameters.cn?exists>
	<#assign channelCodeParam = "&cn=${parameters.cn}"/>
	<#assign orderUrl = orderUrl + channelCodeParam/>
	<#if "ts" != parameters.cn>
		<#assign customcontrol1 = ""/>
	</#if>
</#if>
<#if !customTitleProperties?exists>
	<#assign customTitleProperties=""/>
</#if>
<#if !timeout?exists>
	<#assign timeout= "0"/>
</#if>
<#if !customLoadFunction?exists>
	<#assign customLoadFunction= "false"/>
</#if>
<#if !jqGridMinimumLibEnable?exists>
	<#assign jqGridMinimumLibEnable= ""/>
</#if>

<#assign customcontrol2 = "fa fa-file-excel-o@@javascript: void(0);@exportExcel()">
<#assign contextMenuItemId = "ctxmnuordlst">
<#if hasOlbEntityPermission("SALESORDER", "CREATE") && !(isOwnerDistributor?exists && isOwnerDistributor)>
	<@jqGrid id="listOrderCustomer" url=orderUrl timeout=timeout customLoadFunction=customLoadFunction jqGridMinimumLibEnable=jqGridMinimumLibEnable
		filtersimplemode="true" filterable="true" addType="popup" dataField=dataField columnlist=columnlist usecurrencyfunction="true" clearfilteringbutton="true" 
		mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemId}" customTitleProperties=customTitleProperties autoshowloadelement="${showLoader}" showdefaultloadelement="${showLoader}"
		customcontrol1=customcontrol1 customcontrol2=customcontrol2 isSaveFormData="true" formData="filterObjData" isShowTitleProperty=isShowTitleProperty showtoolbar=showtoolbar enabletooltips="true"/>
<#else>
	<@jqGrid id="listOrderCustomer" url=orderUrl 
		customLoadFunction=customLoadFunction isShowTitleProperty=isShowTitleProperty
		filtersimplemode="true" filterable="true" addType="popup" dataField=dataField columnlist=columnlist usecurrencyfunction="true" clearfilteringbutton="true" 
		mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemId}" customTitleProperties="BSListOrder" autoshowloadelement="${showLoader}" showdefaultloadelement="${showLoader}" 
		jqGridMinimumLibEnable=jqGridMinimumLibEnable
	/>
</#if>
<#-- otherParams="estimatedDeliveryDate:S-getEstimatedDeliveryDateByOrder(orderId{orderId})<estimatedDeliveryDate>" -->
<div id='contextMenu_${contextMenuItemId}' style="display:none">
	<ul>
		<li id="${contextMenuItemId}_viewdetailnewtab"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}</li>
		<li id="${contextMenuItemId}_viewdetail"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li id="${contextMenuItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<script src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script>
<@jqOlbCoreLib />
<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSProductId = "${uiLabelMap.BSProductId}";
	uiLabelMap.BSProductName = "${uiLabelMap.BSProductName}";
	uiLabelMap.BPSearchProductInOrder = "${uiLabelMap.BPSearchProductInOrder}";
	uiLabelMap.BSSearchOrderByAddress = "${uiLabelMap.BSSearchOrderByAddress}";
	uiLabelMap.BSId = "${uiLabelMap.BSId}";
	uiLabelMap.BSAddress = "${uiLabelMap.BSAddress}";
	uiLabelMap.BSFullName = "${uiLabelMap.BSFullName}";
	
	var exportExcel = function(){
		//window.location.href = "exportProductsExcel";
		
		var form = document.createElement("form");
	    form.setAttribute("method", "POST");
	    form.setAttribute("action", "exportSalesOrderExcel");
	    //form.setAttribute("target", "_blank");
	    
	    if (OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)) {
	    	$.each(filterObjData.data, function(key, value) {
	    		var hiddenField0 = document.createElement("input");
		        hiddenField0.setAttribute("type", "hidden");
		        hiddenField0.setAttribute("name", key);
		        hiddenField0.setAttribute("value", value);
		        form.appendChild(hiddenField0);
	    	});
	    }
        
	    document.body.appendChild(form);
	    form.submit();
	};
	
	$(function(){
		OlbOrderList.init();
	});
	var OlbOrderList = (function(){
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.contextMenu.create($("#contextMenu_${contextMenuItemId}"));
		};
		var initEvent = function(){
			$("#contextMenu_${contextMenuItemId}").on('itemclick', function (event) {
				var args = event.args;
				var tmpId = $(args).attr('id');
				var idGrid = "#listOrderCustomer";
				
		        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
		        var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
		        
		        switch(tmpId) {
		    		case "${contextMenuItemId}_viewdetailnewtab": {
		    			if (rowData) {
							var orderId = rowData.orderId;
							var url = 'viewOrder?orderId=' + orderId;
							var win = window.open(url, '_blank');
							win.focus();
						}
						break;
					};
		    		case "${contextMenuItemId}_viewdetail": { 
		    			if (rowData) {
							var orderId = rowData.orderId;
							var url = 'viewOrder?orderId=' + orderId;
							var win = window.open(url, '_self');
							win.focus();
						}
						break;
					};
		    		case "${contextMenuItemId}_refesh": { 
		    			$(idGrid).jqxGrid('updatebounddata');
		    			break;
		    		};
		    		default: break;
		    	}
		    });
		};
		return {
			init: init
		};
	}());
	var quickCreateOrder = function(){
		if(typeof(CookieLayer) != "undefined" && CookieLayer){
			var party = CookieLayer.getCurrentParty().partyId;

			window.open('newSalesOrder?partyId=' + party,'_blank');
		} else {
			window.open('newSalesOrder','_blank');
		}
	};
</script>