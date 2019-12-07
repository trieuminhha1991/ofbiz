<style>
	.order {
		color: #037C07;
		font-weight: bold;
		vertical-align: bottom;
		line-height: 20px;
	}
</style>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<@jqOlbCoreLib hasComboBox=true/>
<script type="text/javascript">
	<#assign orderStatuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, ["ORDER_CREATED", "ORDER_PROCESSING", "ORDER_APPROVED", "ORDER_COMPLETED", "ORDER_CANCELLED"]), null, null, null, true)/>
	var orderStatusData = [<#if orderStatuses?exists><#list orderStatuses as statusItem>{
		statusId: "${statusItem.statusId}",
		description: "${StringUtil.wrapString(statusItem.get("description", locale))}"
	},</#list></#if>];
	<#assign orderTypeList = delegator.findList("OrderType", null, null, null, null, false)! />
	
	var productStoreData = [];
	
	var cellClass = function (row, columnfield, value) {
 		var data = $("#listOrderCustomer").jqxGrid("getrowdata", row);
 		if (typeof(data) != "undefined") {
 			if ("ORDER_CANCELLED" == data.statusId) {
 				return "background-cancel";
 			} else if ("ORDER_CREATED" == data.statusId) {
 				return "background-important-nd";
 			} else if ("ORDER_APPROVED" == data.statusId) {
 				return "background-prepare";
 			}
 		}
    }
    
    var hidePrice = true;
    <#if hasOlbPermission("MODULE", "PO_PRICE", "VIEW")>
    	hidePrice = false;
    </#if>
</script>
<div style="position:relative">
<div id="loader_page_common" style="overflow: hidden; position: fixed; display: none; left: 50%; top: 50%; z-index: 99998;" class="jqx-rc-all jqx-rc-all-olbius">
	<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
		<div style="float: left;">
			<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
			<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DALoading}...</span>
		</div>
	</div>
</div>
</div>
<#assign dataField="[
				{ name: 'orderDate', type: 'date', other: 'Timestamp' },
				{ name: 'orderId', type: 'string' },
				{ name: 'fromOrderSaleId', type: 'string' },
				{ name: 'orderName', type: 'string' },
				{ name: 'shipByDate', type: 'date', other: 'Timestamp' },
				{ name: 'shipAfterDate', type: 'date', other: 'Timestamp' },
				{ name: 'shipBeforeDate', type: 'date', other: 'Timestamp' },
				{ name: 'customerId', type: 'string' },
				{ name: 'productStoreId', type: 'string' },
				{ name: 'remainingSubTotal', type: 'number' },
				{ name: 'grandTotal', type: 'number' },
				{ name: 'statusId', type: 'string' },
				{ name: 'primaryAgreementId', type: 'string' },
				{ name: 'partySupplierCode', type: 'string' },
				{ name: 'createdBy', type: 'string' },
				{ name: 'currencyUom', type: 'string' },
				{ name: 'address1', type: 'string' },
				{ name: 'agreementCode', type: 'string' }]"/>

<#assign columnlist = "
				{ text: '${uiLabelMap.DAOrderId}', dataField: 'orderId', pinned: true, width:'12%', cellClassName: cellClass,
					cellsrenderer: function(row, colum, value) {
						var data = $('#listOrderCustomer').jqxGrid('getrowdata', row);
						return \"<span><a href='viewDetailPO?orderId=\" + data.orderId + \"'>\" + data.orderId + \"</a></span>\";
					}
				},
				{ text: '${uiLabelMap.POSOId}', dataField: 'fromOrderSaleId', pinned: true, editable: false, width:'12%', hidden: true,
					cellsrenderer: function(row, colum, value) {
						return \"<span><a href='viewOrder?orderId=\" + value + \"'>\" + value + \"</a></span>\";
					}
				},
				{ text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', minwidth: '120', filtertype: 'checkedlist', cellClassName: cellClass,
					cellsrenderer: function(row, column, value){
						if (orderStatusData.length > 0) {
							for(var i = 0 ; i < orderStatusData.length; i++){
    							if (value == orderStatusData[i].statusId){
    								return '<span title =\"' + orderStatusData[i].description +'\">' + orderStatusData[i].description + '</span>';
    							}
    						}
						}
						return '<span title=' + value +'>' + value + '</span>';
					}, createfilterwidget: function (column, columnElement, widget) {
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
					}
				},
				{ text: '${uiLabelMap.DACreateDate}', dataField: 'orderDate', width: '15%', cellsformat: 'dd/MM/yyyy', filtertype:'range', cellClassName: cellClass,
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}
				},
				{ text: '${uiLabelMap.Supplier}', dataField: 'partySupplierCode', width: '15%', cellClassName: cellClass,
					cellsrenderer: function(row, colum, value) {
					}
				},
				{ text: '${uiLabelMap.Agreement}', hidden: 'true', dataField: 'agreementCode', editable: false, width:'12%', cellClassName: cellClass,
					cellsrenderer: function(row, colum, value) {
						var data = $(\"#listOrderCustomer\").jqxGrid(\"getrowdata\", row);
						return \"<span><a href='detailPurchaseAgreement?agreementId=\" + data.primaryAgreementId + \"'>\" + value + \"</a></span>\";
					}
				},
				{ text: '${uiLabelMap.DAShipAfterDate}', columngroup: 'shipGroupDate', columntype: 'datetimeinput', editable: false, dataField: 'shipAfterDate', width: '15%', cellsformat: 'dd/MM/yyyy', filtertype:'range', cellClassName: cellClass,
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}, cellbeginedit: function (row, datafield, columntype, value) {
						var rowData = $('#listOrderCustomer').jqxGrid('getrowdata', row);
						if(rowData.statusId == \"ORDER_COMPLETED\" || rowData.statusId == \"ORDER_CANCELLED\") return false;
					}
				},
				{ text: '${uiLabelMap.DAShipBeforeDate}', columngroup: 'shipGroupDate', columntype: 'datetimeinput', dataField: 'shipBeforeDate', width: '15%', cellsformat: 'dd/MM/yyyy', filtertype:'range', cellClassName: cellClass,
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}, cellbeginedit: function (row, datafield, columntype, value) {
						var rowData = $('#listOrderCustomer').jqxGrid('getrowdata', row);
						if(rowData.statusId == \"ORDER_COMPLETED\" || rowData.statusId == \"ORDER_CANCELLED\") return false;
					}, createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
						editor.jqxDateTimeInput({formatString: 'dd/MM/yyyy HH:mm:ss' });
					}
				},
				{ text: '${uiLabelMap.remainingTotal}', hidden: hidePrice, dataField: 'remainingSubTotal', width: '15%', filtertype: 'number', cellClassName: cellClass,
					cellsrenderer: function(row, column, value) {
						var data = $(\"#listOrderCustomer\").jqxGrid(\"getrowdata\", row);
						var str = \"<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>\";
							str += formatcurrency(value,data.currencyUom);
							str += \"</div>\";
						return str;
					}
				},
				{ text: '${uiLabelMap.totalAfterTax}', hidden: hidePrice, dataField: 'grandTotal', width: '15%', filtertype: 'number', cellClassName: cellClass,
					cellsrenderer: function(row, column, value) {
						var data = $(\"#listOrderCustomer\").jqxGrid(\"getrowdata\", row);
						var str = \"<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>\";
							str += formatcurrency(value,data.currencyUom);
							str += \"</div>\";
						return str;
					}
				},
				{ text: '${uiLabelMap.CreatedBy}', dataField: 'createdBy', width: '15%', cellClassName: cellClass,
					cellsrenderer: function(row, column, value) {
					}
				},
			"/>
			<#assign columngrouplist="
				{ text: '${uiLabelMap.BPOShipByDate}', align: 'center', name: 'shipGroupDate' }
			"/>

<#if (hasOlbPermission("MODULE", "PURCHASEORDER_NEW", "CREATE"))>
	<@jqGrid id="listOrderCustomer" url="jqxGeneralServicer?sname=JQListPOOrder" columngrouplist=columngrouplist
		filtersimplemode="true" filterable="true" addType="popup" dataField=dataField columnlist=columnlist usecurrencyfunction="true" clearfilteringbutton="true" 
		mouseRightMenu="true" customTitleProperties="DAListOrder" autoshowloadelement="false" viewSize="20" showdefaultloadelement="false" selectionmode="checkbox"
		customcontrol1="fa fa-plus@${uiLabelMap.DmsCreateNew}@newPurchaseOrder" contextMenuId="menuListPO"
		customcontrol2="fa fa-file-excel-o@${uiLabelMap.POExportExcel}@javascript:exportExcelMutilPOByOrderList()"
	/>
<#else>
	<@jqGrid id="listOrderCustomer" url="jqxGeneralServicer?sname=JQListPOOrder"
		filtersimplemode="true" filterable="true" addType="popup" dataField=dataField columnlist=columnlist usecurrencyfunction="true" clearfilteringbutton="true" 
		mouseRightMenu="true" customTitleProperties="DAListOrder" autoshowloadelement="false" viewSize="20" showdefaultloadelement="false" contextMenuId="menuListPO"
	/>
</#if>

<div id="menuListPO" style="display:none;">
	<ul>
		<li><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.ViewDetailInNewPage)}</li>
		<li><i class="fa fa-eye"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
		<li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>


<script type="text/javascript">
	totalValue =0;
	var remainingSubTotal = 0;
	$("#menuListPO").jqxMenu({ width: 220, autoOpenPopup: false, mode: "popup", theme: theme });
	$("#menuListPO").on("itemclick", function (event) {
		var data = $("#listOrderCustomer").jqxGrid("getRowData", _.last($("#listOrderCustomer").jqxGrid("selectedrowindexes")));
		var tmpStr = $.trim($(args).text());
		if (tmpStr == "${StringUtil.wrapString(uiLabelMap.ViewDetailInNewPage)}") {
			window.open("viewDetailPO?orderId=" + data.orderId, "_blank");
		} else if (tmpStr == "${StringUtil.wrapString(uiLabelMap.BSViewDetail)}") {
			window.location.href = "viewDetailPO?orderId=" + data.orderId;
		} else if (tmpStr == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}") {
			$("#listOrderCustomer").jqxGrid("updatebounddata");
		}
	});
	
	$("#printExcel").on("click", function(){
		var orderId = $("#orderId").text();
		window.location.href = "exportPurchaseOrderToExcel?orderId=" + orderId;
	});
	
	var orderIdDataExportExcel = [];
	$("#listOrderCustomer").on("rowselect", function (event) {
		var args = event.args;
		if(typeof event.args.rowindex != "number"){
			var rowBoundIndex = args.rowindex;
			if (rowBoundIndex.length == 0) {
				orderIdDataExportExcel = [];
			} else {
				for ( var x in rowBoundIndex) {
					var rowID = $("#listOrderCustomer").jqxGrid("getRowId", rowBoundIndex[x]);
					var data = $("#listOrderCustomer").jqxGrid("getrowdatabyid", rowID);
					var orderId  = data.orderId;
					orderIdDataExportExcel.push(orderId);
				}
			}
		} else {
			var tmpArray = event.args.rowindex;
			var rowID = $("#listOrderCustomer").jqxGrid("getRowId", tmpArray);
			var data = $("#listOrderCustomer").jqxGrid("getrowdatabyid", rowID);
			var orderId  = data.orderId;
			orderIdDataExportExcel.push(orderId);
		}
	});
	
	$("#listOrderCustomer").on("rowunselect", function (event){
		var args = event.args;
		if (typeof event.args.rowindex != "number") {
			var rowBoundIndex = args.rowindex;
			for ( var x in rowBoundIndex) {
				var rowID = $("#listOrderCustomer").jqxGrid("getRowId", rowBoundIndex[x]);
				var data = $("#listOrderCustomer").jqxGrid("getrowdatabyid", rowID);
				var ii = orderIdDataExportExcel.indexOf(data);
				orderIdDataExportExcel.splice(ii, 1);
			}
		} else {
			var tmpArray = event.args.rowindex;
			var rowID = $("#listOrderCustomer").jqxGrid("getRowId", tmpArray);
			var data = $("#listOrderCustomer").jqxGrid("getrowdatabyid", rowID);
			var ii = orderIdDataExportExcel.indexOf(data);
			orderIdDataExportExcel.splice(ii, 1);
		}
	});
	
	function exportExcelMutilPOByOrderList() {
		if (orderIdDataExportExcel.length == 0) {
			bootbox.dialog("${uiLabelMap.POSelectOrderExportExcel}", [{
				"label" : "${uiLabelMap.POCommonOK}",
				"class" : "btn btn-primary standard-bootbox-bt",
				"icon" : "fa fa-check",
			}]
			);
		} else {
			var orderIdIfy = JSON.stringify(orderIdDataExportExcel);
			window.location.href = "exportExcelMutilPOByOrderList?orderIdData=" + orderIdIfy;
			$("#listOrderCustomer").jqxGrid("clearSelection");
			orderIdDataExportExcel = [];
		}
	}
</script>
