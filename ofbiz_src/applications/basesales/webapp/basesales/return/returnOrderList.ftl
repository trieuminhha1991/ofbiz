<#assign jqxGridId = "listReturnOrder"/>
<script type="text/javascript">
	<#assign orderStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "ORDER_RETURN_STTS"}, null, false)/>
	var orderStatusData = [
	<#if orderStatuses?exists>
		<#list orderStatuses as statusItem>
		{	statusId: '${statusItem.statusId}',
			description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	
	var cellClassReturnList = function (row, columnfield, value) {
 		var data = $('#${jqxGridId}').jqxGrid('getrowdata', row);
 		if (typeof(data) != 'undefined') {
 			if ("RETURN_CANCELLED" == data.statusId) {
 				return "background-cancel";
 			} else if ("RETURN_REQUESTED" == data.statusId) {
 				return "background-important-nd";
 			} else if ("RETURN_ACCEPTED" == data.statusId) {
 				return "background-prepare";
 			}
 		}
    };
	var checkPermissionSalesman = false;
	var checkPemissionDistributor = false;
	<#if hasOlbPermission("ENTITY", "SALESMAN_RETURNORDER", "CREATE")>
		checkPermissionSalesman = true;
	</#if>
	<#if hasOlbPermission("ENTITY", "DIS_RETURNORDER", "CREATE")>
		checkPemissionDistributor = true;
	</#if>
</script>

<#assign isShowTitleProperty="true"/>
<#assign orderUrl="jqxGeneralServicer?sname=JQListReturnSalesOrder&partyId=${parameters.partyId?if_exists}"/>

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
				{ name: 'returnId', type: 'string'},
				{ name: 'orderId', type: 'string'},
				{ name: 'statusId', type: 'string'},
				{ name: 'createdBy', type: 'string'},
				{ name: 'fromPartyId', type: 'string'},
				{ name: 'toPartyId', type: 'string'},
				{ name: 'paymentMethodId', type: 'string'},
				{ name: 'finAccountId', type: 'string'},
				{ name: 'billingAccountId', type: 'string'},
				{ name: 'entryDate', type: 'date', other: 'Timestamp'},
				{ name: 'originContactMechId', type: 'string'},
				{ name: 'destinationFacilityId', type: 'string'},
				{ name: 'needsInventoryReceive', type: 'string'},
				{ name: 'currencyUomId', type: 'string'},
				{ name: 'supplierRmaId', type: 'string'},
			]"/>
<#--{ text: '${uiLabelMap.BSSupplierRmaId}', dataField: 'supplierRmaId', width: '14%'},-->
<#assign columnlist = "
				{ text: '${uiLabelMap.BSReturnId}', dataField: 'returnId', width: 140, cellClassName: cellClassReturnList, 
					cellsrenderer: function(row, colum, value) {
						return \"<span><a href='viewReturnOrder?returnId=\" + value + \"'>\" + value + \"</a></span>\";
					}
				},
				{ text: '${uiLabelMap.BSOrderId}', dataField: 'orderId', width: 140, cellClassName: cellClassReturnList,
					cellsrenderer: function(row, colum, value) {
						return \"<span><a href='viewOrder?orderId=\" + value + \"'>\" + value + \"</a></span>\";
					}
				},
				{ text: '${uiLabelMap.BSCustomerId}', dataField: 'fromPartyId', width: 150, cellClassName: cellClassReturnList},
				{ text: '${uiLabelMap.BSAbbNeedsInventoryReceive}', dataField: 'needsInventoryReceive', width: 100, cellClassName: cellClassReturnList},
				{ text: '${uiLabelMap.BSCurrencyUomId}', dataField: 'currencyUomId', width: 100, cellClassName: cellClassReturnList},
				{ text: '${uiLabelMap.BSCreatedBy}', dataField: 'createdBy', width: 150, cellClassName: cellClassReturnList},
				{ text: '${uiLabelMap.BSCreateDate}', dataField: 'entryDate', width: 150, cellClassName: cellClassReturnList, cellsformat: 'dd/MM/yyyy', filtertype:'range',
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}
				},
				{ text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', minwidth: 120, cellClassName: cellClassReturnList, filtertype: 'checkedlist', 
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
			"/>
	<#assign contextMenuItemId = "ctxmnurol"/>
	<@jqGrid id="${jqxGridId}" url=orderUrl timeout=timeout customLoadFunction=customLoadFunction jqGridMinimumLibEnable=jqGridMinimumLibEnable
		filtersimplemode="true" filterable="true" addType="popup" dataField=dataField columnlist=columnlist usecurrencyfunction="true" clearfilteringbutton="true" 
		customTitleProperties="BSListReturnOrder" autoshowloadelement="false" showdefaultloadelement="false" 
		customcontrol1="icon-plus open-sans@${uiLabelMap.wgaddnew}@javascript: void(0);@OlbReturnOrderList.quickCreateOrder()" 
		mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemId}"/>
<#--
				{ text: '${uiLabelMap.BSToPartyId}', dataField: 'toPartyId', width: '14%'},
				{ text: '${uiLabelMap.BSDestinationFacilityId}', dataField: 'destinationFacilityId', width: '12%'},
				{ text: '${uiLabelMap.BSPaymentMethodId}', dataField: 'paymentMethodId', width: '14%'},
				{ text: '${uiLabelMap.BSFinAccountId}', dataField: 'finAccountId', width: '14%'},
				{ text: '${uiLabelMap.BSBillingAccountId}', dataField: 'billingAccountId', width: '14%'},
				{ text: '${uiLabelMap.BSOriginContactMechId}', dataField: 'originContactMechId', width: '14%'},
-->
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
	$(function(){
		OlbReturnOrderList.init();
	});
	var OlbReturnOrderList = (function(){
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
		        var idGrid = "#${jqxGridId}";
				
		        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
		        var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
	        	
	        	switch(tmpId) {
	        		case "${contextMenuItemId}_viewdetailnewtab": {
	        			if (rowData) {
							var returnId = rowData.returnId;
							var url = 'viewReturnOrder?returnId=' + returnId;
							var win = window.open(url, '_blank');
							win.focus();
						}
						break;
					};
	        		case "${contextMenuItemId}_viewdetail": { 
	        			if (rowData) {
							var returnId = rowData.returnId;
							var url = 'viewReturnOrder?returnId=' + returnId;
							var win = window.open(url, '_self');
							win.focus();
						}
						break;
					};
	        		case "${contextMenuItemId}_refesh": { 
	        			$(idGrid).jqxGrid('updateBoundData');
	        			break;
	        		};
	        		default: break;
	        	}
			});
		};
		var quickCreateOrder = function(){
			if(typeof(CookieLayer) != "undefined" && CookieLayer){
				var party = CookieLayer.getCurrentParty().partyId;
				if(checkPermissionSalesman && !checkPemissionDistributor){
                    window.open('newReturnOrderBySalesman?partyId=' + party,'_blank');
                    return;
				}
				window.open('newReturnOrder?partyId=' + party,'_blank');
			} else {
                if(checkPermissionSalesman && !checkPemissionDistributor){
                    window.open('newReturnOrderBySalesman','_blank');
                    return;
                }
				window.open('newReturnOrder','_blank');
			}
		};
		return {
			init: init,
			quickCreateOrder: quickCreateOrder,
		};
	}());
</script>