<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
<#assign orderHeaders = delegator.findList("OrderHeaderAndOrderRoleFromTo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", "ORDER_APPROVED", "orderTypeId", "SALES_ORDER", "sellerId", company)), null, null, null, true) />
<script>
var listOrderExpToday = [];
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
			var date1 = new Date("${item.estimatedDeliveryDate?if_exists}");
			var date2 = new Date("${item.shipAfterDate?if_exists}");
			var date3 = new Date("${item.estimatedDeliveryDate?if_exists}");
			var now = new Date();
			if ((date1 && date1.getDate() == now.getDate()) || (date2 && date2.getDate() == now.getDate()) || (date3 && date3.getDate() == now.getDate())){
				listOrderExpToday.push(item);
			}
		</#if>
		<#break>
	</#list>
</#list>
</script>

<div class="totalValueStype">
	<i class="fa-truck"></i> <lable>${uiLabelMap.TotalOrderNeedExportToday}</lable>
	<a href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showOrderExportToday()" >
		<div id="orderExportToday"></div>
	</a>
</div> 

<div id="alterpopupWindowExportToday" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.ListOrderNeedExportToday}
	</div>
	<div class="form-window-container">
		<div class="row-fluid">
			<div class="span12">
				<div id="jqxgridOrderExportToday"></div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="btnCancelExportToday" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>
</div>

<script>
	$(document).ready(function() {
		$("#orderExportToday").html(formatnumber(listOrderExpToday.length));
		$("#alterpopupWindowExportToday").jqxWindow($.extend( {cancelButton: $("#btnCancelExportToday")}, LocalData.config.jqxwindow ));
		$("#alterpopupWindowExportToday").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.ListOrderNeedExportToday)}");
		
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
				localdata: listOrderExpToday,
				datatype: "array"
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#jqxgridOrderExportToday").jqxGrid($.extend({
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
				{text: "${uiLabelMap.BSOrderId}", datafield: "orderId", width: 120,
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
								returnStr += jOlbUtil.dateTime.formatFullDate(rowdata.estimatedDeliveryDate)
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
	});

	function showOrderExportToday(valueExport) {
		var wtmp = window;
		var tmpwidth = $("#alterpopupWindowExportToday").jqxWindow("width");
		$("#alterpopupWindowExportToday").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
		$("#alterpopupWindowExportToday").jqxWindow("open");
	}
</script>