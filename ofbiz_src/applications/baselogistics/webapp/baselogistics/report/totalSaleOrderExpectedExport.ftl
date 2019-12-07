<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
<#assign orderHeaders = delegator.findList("OrderHeaderAndOrderRoleFromTo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", "ORDER_APPROVED", "orderTypeId", "SALES_ORDER", "sellerId", company)), null, null, null, true) />
<script>
var listSalesAllOrder = [];
var listSalesOrderMissing = [];
var listSalesOrderTomorrow = [];
<#list orderHeaders as header>
	<#assign customer = delegator.findOne("PartyNameView", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", header.customerId?if_exists), true)/>
	<#assign orderItems = delegator.findList("OrderItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", header.orderId?if_exists)), null, null, null, true) />
	var orderId = "${header.orderId}";
	<#list orderItems as item>
		<#if item.estimatedDeliveryDate?exists || (item.shipBeforeDate?exists && item.shipAfterDate?exists)>
			var item = {
				orderId: orderId,
				estimatedDeliveryDate: "${item.estimatedDeliveryDate?if_exists}",
				shipBeforeDate: "${item.shipBeforeDate?if_exists}",
				shipAfterDate: "${item.shipAfterDate?if_exists}",
				customerName: "${StringUtil.wrapString(customer.lastName?if_exists)} " + "${StringUtil.wrapString(customer.middleName?if_exists)} " + "${StringUtil.wrapString(customer.firstName?if_exists)} " + "${StringUtil.wrapString(customer.groupName?if_exists)}",
			};
			listSalesAllOrder.push(item);
			<#if (item.estimatedDeliveryDate?exists && nowTimestamp.after(item.estimatedDeliveryDate)) || (item.shipBeforeDate?exists && nowTimestamp.after(item.shipBeforeDate))>
				listSalesOrderMissing.push(item);
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
	<div class="titleText">
		<i class="fa-upload"></i> <lable>${uiLabelMap.StockOut}</lable>
	</div>
	<div class="valueTotal">
		<div class="form-window-container">
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">	
						<div class="span9" style="text-align: left;">
							<label style="cursor: auto;"> ${uiLabelMap.TotalOrderNoExport}: </label>
						</div>
						<div class="span3" style="text-align: right">
							<a style="text-decoration: underline !important; color: #333;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showOrderExport('TotalOrderNoExport')">
								<div id="mostImportedProduct"></div>
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
							<label style="cursor: auto;"> ${uiLabelMap.TotalOrderNotExportTime}: </label>
						</div>
						<div class="span3" style="text-align: right">
							<a style="text-decoration: underline !important; color: #333;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showOrderExport('TotalOrderNotExportTime')" >
								<div id="totalOrderNotExportTime"></div>
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
							<label style="cursor: auto;"> ${uiLabelMap.OrderNeedExportTomorrow}: </label>
						</div>
						<div class="span3" style="text-align: right">
							<a style="text-decoration: underline !important; color: #333;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showOrderExport('OrderNeedExportTomorrow')" >
								<div id="totalOrderExportTomorrow"></div>
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="alterpopupWindowExport" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.ListOrderNeedExportToday}
	</div>
	<div class="form-window-container">
		<div class="row-fluid">
			<div class="span12">
				<div id="jqxgridOrderExport"></div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="btnCancelExport" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>
</div>

<script>
	$(document).ready(function() {
		$("#totalOrderExportTomorrow").html(listSalesOrderTomorrow.length.toLocaleString(locale));
		$("#mostImportedProduct").html(listSalesAllOrder.length.toLocaleString(locale));
		$("#totalOrderNotExportTime").html(listSalesOrderMissing.length.toLocaleString(locale));
		
		$("#alterpopupWindowExport").jqxWindow($.extend( {cancelButton: $("#btnCancelExport")}, LocalData.config.jqxwindow ));
	});

	function showOrderExport(valueExport){
		if (valueExport == "TotalOrderNoExport") {
			$("#alterpopupWindowExport").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.ListOrderNeedExport)}");
			bindingDataOrderExportView(listSalesAllOrder);
		} else if(valueExport == "TotalOrderNotExportTime") {
			$("#alterpopupWindowExport").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.ListOrderNotExportReq)}");
			bindingDataOrderExportView(listSalesOrderMissing);
		} else if (valueExport == "OrderNeedExportTomorrow") {
			$("#alterpopupWindowExport").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.ListOrderExportTomorrow)}");
			bindingDataOrderExportView(listSalesOrderTomorrow);
		}
	}
	
	function bindingDataOrderExportView(listOrderExport) {
		var source =
		{
			datafields:
			[
				{ name: "orderId", type: "string" },
				{ name: "customerName", type: "string" },
				{ name: "estimatedDeliveryDate", type: "date" },
				{ name: "shipBeforeDate", type: "date" },
				{ name: "shipAfterDate", type: "date" }
			],
			localdata: listOrderExport,
			datatype: "array"
		}; 
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#jqxgridOrderExport").jqxGrid($.extend({
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
						return "<span><a target=\"_blank\" href=\"" + "viewOrder?orderId=" + value + "&activeTab=deliveries-tab\">" + value + " <i class=\"fa fa-upload\"></i></a></span>";
					}
				},
				{ text: "${uiLabelMap.Customer}", datafield: "customerName", minwidth: 200 },
				{ text: "${uiLabelMap.BSDesiredDeliveryDate}", dataField: "estimatedDeliveryDate", cellsformat: "dd/MM/yyyy", filtertype:"range", width: 300,
					cellsrenderer: function(row, columns, value, defaulthtml, columnproperties, rowdata) {
						if (rowdata) {
							var returnStr = "<span>";
							if (rowdata.estimatedDeliveryDate != null) {
								returnStr += jOlbUtil.dateTime.formatFullDate(rowdata.estimatedDeliveryDate);
								if (rowdata.shipAfterDate != null && rowdata.shipBeforeDate != null && rowdata.shipAfterDate != "" && rowdata.shipBeforeDate != "") {
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
		var tmpwidth = $("#alterpopupWindowExport").jqxWindow("width");
		$("#alterpopupWindowExport").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
		$("#alterpopupWindowExport").jqxWindow("open");
	}
</script>