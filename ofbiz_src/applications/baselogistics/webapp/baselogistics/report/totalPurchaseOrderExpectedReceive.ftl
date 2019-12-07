<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
<#assign orderHeaders = delegator.findList("OrderHeaderAndOrderRolePO", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", "ORDER_APPROVED", "orderTypeId", "PURCHASE_ORDER", "customerId", company)), null, null, null, true) />
<script>
var listPurchaseAllOrder = [];
var listPurchaseOrderMissing = [];
var listPurchaseOrderTomorrow = [];
<#list orderHeaders as header>
	<#assign supplier = delegator.findOne("PartyNameView", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", header.sellerId?if_exists), true)/>
	<#assign orderItems = delegator.findList("OrderItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", header.orderId?if_exists)), null, null, null, true) />
	var orderId = "${header.orderId}";
	<#list orderItems as item>
		<#if item.shipAfterDate?exists && item.shipAfterDate?exists>
			var item = {
				orderId: orderId,
				estimatedDeliveryDate: "${item.estimatedDeliveryDate?if_exists}",
				shipBeforeDate: "${item.shipBeforeDate?if_exists}",
				shipAfterDate: "${item.shipAfterDate?if_exists}",
				supplierName: "${StringUtil.wrapString(supplier.lastName?if_exists)} " + "${StringUtil.wrapString(supplier.middleName?if_exists)} " + "${StringUtil.wrapString(supplier.firstName?if_exists)} " + "${StringUtil.wrapString(supplier.groupName?if_exists)}",
			};
			
			listPurchaseAllOrder.push(item);
			<#if (item.estimatedDeliveryDate?exists && nowTimestamp.after(item.estimatedDeliveryDate)) || (item.shipBeforeDate?exists && nowTimestamp.after(item.shipBeforeDate))>
				listPurchaseOrderMissing.push(item);
			</#if>
			<#if ((item.estimatedDeliveryDate?exists && nowTimestamp.before(item.estimatedDeliveryDate)) || (item.shipAfterDate?exists && nowTimestamp.before(item.shipAfterDate)) || (item.shipBeforeDate?exists && nowTimestamp.before(item.shipBeforeDate)))>
				var date1 = new Date("${item.estimatedDeliveryDate?if_exists}");
				var date2 = new Date("${item.shipAfterDate?if_exists}");
				var date3 = new Date("${item.shipAfterDate?if_exists}");
				var tomorrow = new Date();
				var x = tomorrow.getDate();
				tomorrow.setDate(x + 1);
				tomorrow.setHours(0);
				tomorrow.setMinutes(0);
				tomorrow.setSeconds(0);
				if ((date1 && date1.getDate() == tomorrow.getDate()) || (date2 && date2.getDate() == tomorrow.getDate()) || (date3 && date3.getDate() == tomorrow.getDate())){
					listSalesOrderTomorrow.push(item);
				}
			</#if>
		</#if>
		<#break>
	</#list>
</#list>
</script>
<div>
	<div class="titleTextReceive">
		<i class="fa-download"></i> <lable style="cursor: auto;">${uiLabelMap.StockIn}</lable>
	</div>
	<div class="valueTotal">
		<div class="form-window-container">
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">	
						<div class="span9" style="text-align: left">
							<label style="cursor: auto;"> ${uiLabelMap.TotalOrderNoReceive}: </label>
						</div>
						<div class="span3" style="text-align: right">
							<a style="text-decoration: underline !important; color: #333;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showOrderReceive('TotalOrderNoReceive')">
								<div id="totalPurchaseOrderNeedReceive"></div>
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="valueTotal">
		<div class="form-window-container">
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">	
						<div class="span9" style="text-align: left">
							<label style="cursor: auto;"> ${uiLabelMap.TotalOrderNotReceiveTime}: </label>
						</div>
						<div class="span3" style="text-align: right">
							<a style="text-decoration: underline !important; color: #333;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showOrderReceive('TotalOrderNotReceiveTime')"> 
								<div id="totalOrderNotReceiveTime"></div>
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="valueTotal">
		<div class="form-window-container">
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">	
						<div class="span9" style="text-align: left">
							<label style="cursor: auto;"> ${uiLabelMap.OrderNeedReceiveTomorrow}: </label>
						</div>
						<div class="span3" style="text-align: right">
							<a style="text-decoration: underline !important; color: #333;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showOrderReceive('OrderNeedReceiveTomorrow')">
								<div id="totalOrderReceiveTomorrow"></div>
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>


<div id="alterpopupWindowReceive" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.ListOrderNeedReceiveToday}
	</div>
	<div class="form-window-container">
		<div class="row-fluid">
			<div class="span12">
				<div id="jqxgridOrderReceive"></div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="btnCancelReceive" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>
</div>

<script>
	$(document).ready(function() {
		$("#totalPurchaseOrderNeedReceive").html(listPurchaseAllOrder.length.toLocaleString(locale));
		$("#totalOrderNotReceiveTime").html(listPurchaseOrderMissing.length.toLocaleString(locale));
		$("#totalOrderReceiveTomorrow").html(listPurchaseOrderTomorrow.length.toLocaleString(locale));
		$("#alterpopupWindowReceive").jqxWindow($.extend( {cancelButton: $("#btnCancelReceive")}, LocalData.config.jqxwindow ));
	});
	function showOrderReceive(valueReceive){
		if (valueReceive == "TotalOrderNoReceive") { 
			$("#alterpopupWindowReceive").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.ListOrderNeedReceive)}");
			bindingDataOrderReceiveView(listPurchaseAllOrder);
		} else if (valueReceive == "TotalOrderNotReceiveTime") {
			$("#alterpopupWindowReceive").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.ListOrderNotReceiveReq)}");
			bindingDataOrderReceiveView(listPurchaseOrderMissing);
		} else if (valueReceive == "OrderNeedReceiveTomorrow") {
			$("#alterpopupWindowReceive").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.ListOrderReceiveTomorrow)}");
			bindingDataOrderReceiveView(listPurchaseOrderTomorrow);
		}
	}

	function bindingDataOrderReceiveView(listOrderReceive){
		var source =
		{
			datafields:
			[
				{ name: "orderId", type: "string" },
				{ name: "supplierName", type: "string" },
				{ name: "estimatedDeliveryDate", type: "date" },
				{ name: "shipBeforeDate", type: "date" },
				{ name: "shipAfterDate", type: "date" },
			],
			localdata: listOrderReceive,
			datatype: "array"
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#jqxgridOrderReceive").jqxGrid($.extend({
			source: dataAdapter,
			columns:
			[
				{ text: "${uiLabelMap.SequenceId}", sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false,
					datafield: "", columntype: "number", width: 50,
					cellsrenderer: function (row, column, value) {
						return "<div style=margin:4px;>" + (value + 1) + "</div>";
					}
				},
				{ text: "${uiLabelMap.BSOrderId}", datafield: "orderId", width: 120,
					cellsrenderer: function(row, colum, value) {
						return "<span><a target=\"_blank\" href=\"" + "viewDetailPO?orderId=" + value + "&activeTab=deliveries-tab\">" + value + " <i class=\"fa fa-download\"></i></a></span>";
					}
				},
				{ text: "${uiLabelMap.Supplier}", datafield: "supplierName", minwidth: 200 },
				{ text: "${uiLabelMap.BSDesiredDeliveryDate}", dataField: "estimatedDeliveryDate", cellsformat: "dd/MM/yyyy", filtertype:"range", width: 300,
					cellsrenderer: function(row, columns, value, defaulthtml, columnproperties, rowdata) {
						if (rowdata) {
							var returnStr = "<span>";
							if (rowdata.estimatedDeliveryDate != null) {
								returnStr += jOlbUtil.dateTime.formatFullDate(rowdata.estimatedDeliveryDate)
								if (rowdata.shipAfterDate != null || rowdata.shipBeforeDate != null) {
									returnStr += " (";
									returnStr += jOlbUtil.dateTime.formatFullDate(rowdata.shipAfterDate) + " - " + jOlbUtil.dateTime.formatFullDate(rowdata.shipBeforeDate);
									returnStr += ")";
								}
							} else {
								returnStr += jOlbUtil.dateTime.formatFullDate(rowdata.shipAfterDate) + " - " + jOlbUtil.dateTime.formatFullDate(rowdata.shipBeforeDate);
							}
							returnStr += "</span>";
							return returnStr;
						}
					}
				}
			] 
		}, LocalData.config.jqxgrid));
		var wtmp = window;
		var tmpwidth = $("#alterpopupWindowReceive").jqxWindow("width");
		$("#alterpopupWindowReceive").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
		$("#alterpopupWindowReceive").jqxWindow("open");
	}
</script>