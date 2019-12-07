<script>
	<#assign orderStatuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, ["ORDER_CREATED", "ORDER_PROCESSING", "ORDER_APPROVED", "ORDER_COMPLETED", "ORDER_CANCELLED"]), null, null, null, true)/>
	var orderStatusData = [<#if orderStatuses?exists><#list orderStatuses as statusItem>{
		statusId: "${statusItem.statusId}",
		description: "${StringUtil.wrapString(statusItem.get("description", locale))}"
	},</#list></#if>];
</script>
<div id="order-tab" class="tab-pane<#if activeTab?exists && activeTab == "order-tab"> active</#if>">
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
				{ name: 'currencyUom', type: 'string' },
				{ name: 'address1', type: 'string' },
				{ name: 'agreementCode', type: 'string' }]"/>

<#assign columnlist = "
	{ text: '${uiLabelMap.DAOrderId}', dataField: 'orderId', pinned: true, width:'12%',
		cellsrenderer: function(row, colum, value) {
			var data = $('#listOrders').jqxGrid('getrowdata', row);
			return \"<span><a href='viewDetailPO?orderId=\" + data.orderId + \"'>\" + data.orderId + \"</a></span>\";
		}
	},
	{ text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', width: '12%', filtertype: 'checkedlist', 
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
	{ text: '${uiLabelMap.DACreateDate}', dataField: 'orderDate', width: '15%', cellsformat: 'dd/MM/yyyy', filtertype:'range', 
		cellsrenderer: function(row, colum, value) {
			return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
		}
	},
	{ text: '${uiLabelMap.DAShipAfterDate}', columngroup: 'shipGroupDate', columntype: 'datetimeinput', editable: false, dataField: 'shipAfterDate', width: '15%', cellsformat: 'dd/MM/yyyy', filtertype:'range', 
		cellsrenderer: function(row, colum, value) {
			return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
		}, cellbeginedit: function (row, datafield, columntype, value) {
			var rowData = $('#listOrders').jqxGrid('getrowdata', row);
			if(rowData.statusId == \"ORDER_COMPLETED\" || rowData.statusId == \"ORDER_CANCELLED\") return false;
		}
	},
	{ text: '${uiLabelMap.DAShipBeforeDate}', columngroup: 'shipGroupDate', columntype: 'datetimeinput', dataField: 'shipBeforeDate', width: '15%', cellsformat: 'dd/MM/yyyy', filtertype:'range', 
		cellsrenderer: function(row, colum, value) {
			return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
		}, cellbeginedit: function (row, datafield, columntype, value) {
			var rowData = $('#listOrders').jqxGrid('getrowdata', row);
			if(rowData.statusId == \"ORDER_COMPLETED\" || rowData.statusId == \"ORDER_CANCELLED\") return false;
		}, createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
			editor.jqxDateTimeInput({formatString: 'dd/MM/yyyy HH:mm:ss' });
		}
	},
	{ text: '${uiLabelMap.remainingTotal}', dataField: 'remainingSubTotal', width: '15%', filtertype: 'number', 
		cellsrenderer: function(row, column, value) {
			var data = $(\"#listOrders\").jqxGrid(\"getrowdata\", row);
			var str = \"<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>\";
				str += formatcurrency(value,data.currencyUom);
				str += \"</div>\";
			return str;
		}
	},
	{ text: '${uiLabelMap.totalAfterTax}', dataField: 'grandTotal', width: '15%', filtertype: 'number',
		cellsrenderer: function(row, column, value) {
			var data = $(\"#listOrders\").jqxGrid(\"getrowdata\", row);
			var str = \"<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>\";
				str += formatcurrency(value,data.currencyUom);
				str += \"</div>\";
			return str;
		}
	},
"/>
<#assign columngrouplist="
	{ text: '${uiLabelMap.BPOShipByDate}', align: 'center', name: 'shipGroupDate' }
"/>
<@jqGrid id="listOrders" url="jqxGeneralServicer?sname=JQListPOOrder&agreementId=${parameters.agreementId?if_exists}" columngrouplist=columngrouplist
	filtersimplemode="true" filterable="true" addType="popup" dataField=dataField columnlist=columnlist usecurrencyfunction="true" clearfilteringbutton="true" 
	mouseRightMenu="true" customTitleProperties="DAListOrder" autoshowloadelement="false" viewSize="20" showdefaultloadelement="false"
/>
		 
 </div>