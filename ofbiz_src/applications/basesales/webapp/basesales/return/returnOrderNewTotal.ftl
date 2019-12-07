<#assign isEmployee = Static["com.olbius.basesales.util.SalesPartyUtil"].isEmployee(delegator, userLogin.partyId)!>
<#if !isEmployee?exists><#assign isEmployee = false/></#if>
<script type="text/javascript">
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
	<#assign orderTypeList = delegator.findList("OrderType", null, null, null, null, false)! />
	var orderTypeData = [
	<#if orderTypeList?exists>
		<#list orderTypeList as orderType>
		{	orderTypeId : "${orderType.orderTypeId}",
			description : "${StringUtil.wrapString(orderType.get("description", locale))}"
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
	<#assign productStoresBySeller = Static['com.olbius.basesales.product.ProductStoreWorker'].getListProductStoreSell(delegator, userLogin)!/>
	<#if productStoresBySeller?exists>
	productStoreData = [
		<#list productStoresBySeller as productStore>
			{	storeName : "${productStore.storeName?default('')}",
				productStoreId : "${productStore.productStoreId}"
			},
		</#list>
	];
	</#if>
</script>
<#assign dataField="[
				{ name: 'orderDate', type: 'date', other: 'Timestamp'},
				{ name: 'orderId', type: 'string'},
				{ name: 'orderName', type: 'string'},
				{ name: 'customerId', type: 'string'},
				{ name: 'customerFullName', type: 'string'},
				{ name: 'productStoreId', type: 'string'},
				{ name: 'grandTotal', type: 'string'},
				{ name: 'statusId', type: 'string'},
				{ name: 'currencyUom', type: 'string'},
				{ name: 'agreementId', type: 'string'},
				{ name: 'priority', type: 'string'},
				{ name: 'isFavorDelivery', type: 'string'},
				{ name: 'createdBy', type: 'string'},
			]"/>
<#assign columnlist = "
				{ text: '${uiLabelMap.BSOrderId}', dataField: 'orderId', width: 120},"/>
<#if Static["com.olbius.basesales.util.SalesPartyUtil"].isCallCenterManager(delegator, userLogin.userLoginId)>
<#assign columnlist = columnlist + "
				{ text: '${uiLabelMap.BSCreatedBy}', dataField: 'createdBy', width: 120},
				"/>
</#if>
<#if isEmployee><#--!(parameters.iro?exists && parameters.iro == "Y")-->
<#assign columnlist = columnlist + "
				{ text: '${uiLabelMap.BSSalesChannel}', dataField: 'productStoreId', width: 150, filtertype: 'checkedlist', 
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
			"/>
</#if>
<#assign columnlist = columnlist + "
				{ text: '${uiLabelMap.BSCustomer}', dataField: 'customerId', width: 120},
				{ text: '${uiLabelMap.BSCustomerName}', dataField: 'customerFullName', minwidth: 140},
				{ text: '${uiLabelMap.BSCreateDate}', dataField: 'orderDate', width: 140, cellsformat: 'dd/MM/yyyy', filtertype:'range',
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}
				},
				{ text: '${uiLabelMap.CommonAmount}', dataField: 'grandTotal', width: 120, cellsalign: 'right', cellsformat: 'c', 
				 	cellsrenderer: function(row, column, value) {
				 		var str = '<div class=\"innerGridCellContent align-right\">';
				 		var data = $('#orderGrid').jqxGrid('getrowdata', row);
				 		if (typeof(data) != 'undefined') {
					 		str += formatcurrency(value, data.currencyUom);
				 		} else {
							str += value;
						}
						str += '</div>';
						return str;
				 	}
				},
			"/>
<#--
				{ text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', width: 100, 
					cellsrenderer: function(row, column, value){
						if (orderStatusData.length > 0) {
							for(var i = 0 ; i < orderStatusData.length; i++){
    							if (value == orderStatusData[i].statusId){
    								return '<span title =\"' + orderStatusData[i].description +'\">' + orderStatusData[i].description + '</span>';
    							}
    						}
						}
						return '<span title=' + value +'>' + value + '</span>';
				 	}
				}, 
-->
<#if isEmployee><#--<#if !(parameters.iro?exists && parameters.iro == "Y")>-->
<#assign columnlist = columnlist + "
				{ text: '${uiLabelMap.BSIsFavorDelivery}', dataField: 'isFavorDelivery', width: 120, filtertype: 'checkedlist', 
					cellsrenderer: function(row, column, value){
						if (favorDeliveryData.length > 0) {
							var data = $('#orderGrid').jqxGrid('getrowdata', row);
							var orderId = data.orderId;
			 					for(var i = 0 ; i < favorDeliveryData.length; i++){
	    							if (value == favorDeliveryData[i].id){
	    								return '<span title =\"' + favorDeliveryData[i].description +'\">' + favorDeliveryData[i].description + '</span>';
	    							}
	    						}
			 				}
						return '<span title=' + value +'>' + value + '</span>';
				 	}, 
				 	createfilterwidget: function (column, columnElement, widget) {
				 		if (favorDeliveryData.length > 0) {
							var filterDataAdapter = new $.jqx.dataAdapter(favorDeliveryData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'id', valueMember: 'id',
								renderer: function(index, label, value){
									if (favorDeliveryData.length > 0) {
										for(var i = 0; i < favorDeliveryData.length; i++){
											if(favorDeliveryData[i].id == value){
												return '<span>' + favorDeliveryData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
						}
		   			},
		   		}, 
				{ text: '${uiLabelMap.BSPriority}', dataField: 'priority', width: 100, filtertype: 'checkedlist', 
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

<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap[titleProperty]}</h4>
		<span class="widget-toolbar none-content">
			<#--<div class="row-fluid form-horizontal form-window-content-custom">-->
				<div style="width:550px" class="form-window-content-custom">
					<div class="row-fluid" style="margin-bottom:0">
						<div class="span3">
							<label>${uiLabelMap.BSOrderId}</label>
						</div>
						<div class="span9">
							<div class="div-inline-block" style="vertical-align:middle">
								<div id="orderId">
									<div id="orderGrid"></div>
								</div>
							</div>
							<div class="div-inline-block" style="vertical-align:top">
								<button type="button" id="loadReturnOrder" class="btn btn-mini btn-primary width90px" style="font-size:13px"><i class="fa fa-repeat"></i> ${uiLabelMap.BSLoad}</button>
							</div>
						</div>
					</div>
				</div>
		</span>
	</div>
	<div class="widget-body">
		<div id="containerNewRO" style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
		</div>
		<div id="jqxNotificationNewRO" style="margin-bottom:5px">
		    <div id="notificationNewRO"></div>
		</div>
		
		<div class="row-fluid">
			<div class="span12">
				<div id="loadOrderItems">
					${uiLabelMap.BSHaveNoOrderSelected}
				</div>
			</div>
		</div>
	</div><!--.widget-body-->
</div><!--.widget-box-->


<div class="container_loader">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>

<script type="text/javascript">
	$(function(){
		OlbReturnOrderTotal.init();
	});
	
	var OlbReturnOrderTotal = (function(){
		var orderListDDB;
		
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.notification.create("#containerNewRO", "#jqxNotificationNewRO");
			
			var urlSearchOrder = <#if isEmployee>"JQListSalesOrderReturnable"<#else>"JQListSalesOrderExternalReturnable"</#if>;<#--parameters.iro?exists && parameters.iro == "Y"-->
			var configOrder = {
				width: 900,
				datafields: ${dataField},
				columns: [${columnlist}],
				useUrl: true,
				root: 'results',
				url: urlSearchOrder,
				useUtilFunc: true,
				filterable: true,
				displayDetail: false,
				
				key: 'orderId', 
				description: ['orderId'],
				autoCloseDropDown: false,
				showdefaultloadelement: true,
				autoshowloadelement: true,
				dropDownHorizontalAlignment: "right",
			};
			orderListDDB = new OlbDropDownButton($("#orderId"), $("#orderGrid"), null, configOrder, []);
		};
		var initEvent = function(){
			<#if isEmployee && parameters.orderId?exists && parameters.orderId?has_content>
			orderListDDB.getGrid().bindingCompleteListener(function(){
				orderListDDB.selectItem(["${parameters.orderId}"]);
				
				<#--$("#loadReturnOrder").click();-->
			}, true);
			
			loadOrderContent("${parameters.orderId}");
			</#if>
			
			$("#loadReturnOrder").on('click', function(){
				var orderId = orderListDDB.getValue();
				if (!orderId) {
					jOlbUtil.alert.error("${StringUtil.wrapString(uiLabelMap.BSYouNotYetChooseRow)}!");
					return false;
				}
				loadOrderContent(orderId);
			});
		};
		var loadOrderContent = function(orderId){
			var dataStr = "orderId=" + orderId;
			$.ajax({
				type: 'POST',
				url: 'loadReturnItemsAjax',
				data: dataStr,
				beforeSend: function(){
					$("#loader_page_common").show();
				},
				success: function(data){
					jOlbUtil.processResultDataAjax(data, "default", "default", function(){
			    		$("#loadOrderItems").html(data);
					});
				},
				error: function(data){
					alert("Send request is error");
				},
				complete: function(data){
					$("#loader_page_common").hide();
				},
			});
		};
		return {
			init: init
		};
	}());
</script>