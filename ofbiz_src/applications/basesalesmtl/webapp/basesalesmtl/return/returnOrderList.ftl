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
	
	var cellClass = function (row, columnfield, value) {
 		var data = $('#listReturnOrder').jqxGrid('getrowdata', row);
 		var returnValue = "";
 		//if (typeof(data) != 'undefined') {
 		//	if (typeof(data.statusId) != 'undefined' && data.statusId == "Y") {
 		//		returnValue = "background-favor-delivery";
 		//		return returnValue;
 		//	}
 		//}
    }
</script>

<#assign isShowTitleProperty="true"/>
<#assign orderUrl="jqxGeneralServicer?sname=JQListReturnPurchOrder"/>

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
<#--
{ text: '${uiLabelMap.BSSupplierRmaId}', dataField: 'supplierRmaId', width: '14%'},
{ text: '${uiLabelMap.BSFromPartyId}', dataField: 'fromPartyId', width: '14%'},
{ text: '${uiLabelMap.BSToPartyId}', dataField: 'toPartyId', width: '14%'},
{ text: '${uiLabelMap.BSDestinationFacilityId}', dataField: 'destinationFacilityId', width: '12%'},
{ text: '${uiLabelMap.BSCurrencyUomId}', dataField: 'currencyUomId', width: '8%'},
{ text: '${uiLabelMap.BSPaymentMethodId}', dataField: 'paymentMethodId', width: '14%'},
{ text: '${uiLabelMap.BSFinAccountId}', dataField: 'finAccountId', width: '14%'},
{ text: '${uiLabelMap.BSBillingAccountId}', dataField: 'billingAccountId', width: '14%'},
{ text: '${uiLabelMap.BSOriginContactMechId}', dataField: 'originContactMechId', width: '14%'},
-->
<#assign columnlist = "
				{ text: '${uiLabelMap.BSReturnId}', dataField: 'returnId', width: '16%', pinned: true, cellClassName: cellClass, 
					cellsrenderer: function(row, colum, value) {
						var data = $('#listReturnOrder').jqxGrid('getrowdata', row);
						return \"<span><a href='viewReturnOrderSuppl?returnId=\" + data.returnId + \"'>\" + data.returnId + \"</a></span>\";
					}
				},
				{ text: '${uiLabelMap.BSOrderId}', dataField: 'orderId', width: '16%',
					cellsrenderer: function(row, colum, value) {
						return \"<span><a href='viewOrder?orderId=\" + value + \"'>\" + value + \"</a></span>\";
					}
				},
				{ text: '${uiLabelMap.BSNeedsInventoryReceive}', dataField: 'needsInventoryReceive'},
				{ text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', width: '16%', cellClassName: cellClass, filtertype: 'checkedlist', 
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
				{ text: '${uiLabelMap.BSCreatedBy}', dataField: 'createdBy', width: '16%', cellClassName: cellClass},
				{ text: '${uiLabelMap.BSCreateDate}', dataField: 'entryDate', width: '16%', cellClassName: cellClass, cellsformat: 'dd/MM/yyyy', filtertype:'range',
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}
				},
			"/>
	<@jqGrid id="listReturnOrder" url=orderUrl timeout=timeout customLoadFunction=customLoadFunction jqGridMinimumLibEnable=jqGridMinimumLibEnable
		filtersimplemode="true" filterable="true" addType="popup" dataField=dataField columnlist=columnlist usecurrencyfunction="true" clearfilteringbutton="true" 
		mouseRightMenu="true" contextMenuId="contextMenu" customTitleProperties="BSListReturnOrder" autoshowloadelement="false" showdefaultloadelement="false"
		/>
<#--customcontrol1="icon-plus open-sans@${uiLabelMap.wgaddnew}@javascript: void(0);@quickCreateOrder()"-->

<div id='contextMenu' style="display:none">
	<ul>
		<li><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}</li>
		<li><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<script src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script>
<@jqOlbCoreLib />
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#contextMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
		var idGrid = "#listReturnOrder";
        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
        var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
        
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}") {
        	if (rowData) {
				var returnId = rowData.returnId;
				var url = 'viewReturnOrder?returnId=' + returnId;
				var win = window.open(url, '_blank');
				win.focus();
			}
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}") {
        	$(idGrid).jqxGrid('updatebounddata');
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.BSViewDetail)}") {
			if (rowData) {
				var returnId = rowData.returnId;
				var url = 'viewReturnOrder?returnId=' + returnId;
				var win = window.open(url, '_self');
				win.focus();
			}
        }
	});
	var quickCreateOrder = function(){
		if(typeof(CookieLayer) != "undefined" && CookieLayer){
			var party = CookieLayer.getCurrentParty().partyId;
			window.open('newReturnOrder?partyId=' + party,'_blank');
		} else {
			window.open('newReturnOrder','_blank');
		}
	};
</script>