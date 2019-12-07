<script type="text/javascript" id="totalOrderNeedApprovalToday">
	$(function() {
		var textView = OLBIUS.textView({
			id :"totalOrderNeedApprovalToday",
			url: "getTotalOrderNeedApprovalToday",
			icon: "fa fa-cart-plus",
			data: {checkTime: "OrderCanApprovedToday"},
			renderTitle: function(data) {
				return "${StringUtil.wrapString(uiLabelMap.OrderNeedToApprovedToday)}"
			},
			renderValue: function(data) {
				var orderCount = data.orderCount;
				if(orderCount){
					return orderCount.toLocaleString(locale);
				} else {
					return "0";
				}
			}
		}).init();
		
		textView.click(function() {
			showListOrderNotYetApprovedToday("OrderCanApprovedToday");
		});
	});
</script>

<div id="alterpopupWindowOrderNeedApprovalToday" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.ListOrdersNotYetApproved}
	</div>
	<div class="form-window-container">
		<div class="row-fluid">
			<div class="span12">
				<div id="jqxgridOrderApprovalToday">
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="btnCancelOrderNeedApprovalToday" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>
</div>

<@jqOlbCoreLib />

<script>
	$(document).ready(function() {
		$("#alterpopupWindowOrderNeedApprovalToday").jqxWindow($.extend( {cancelButton: $("#btnCancelOrderNeedApprovalToday")}, LocalData.config.jqxwindow ));
	});
	
	function showListOrderNotYetApprovedToday(valueOrderNotApprovedToday) {
		$("#alterpopupWindowOrderNeedApprovalToday").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.ListOrdersNotYetApproved)}");
		viewListOrderNotApprovedToday(valueOrderNotApprovedToday);
	}
	
	function viewListOrderNotApprovedToday(valueOrderNotApprovedToday) {
		$.ajax({
			url: "loadListPurchaseOrder",
			type: "POST",
			data: { valueCheck: valueOrderNotApprovedToday },
			dataType: "json"
		}).done(function(data) {
			var listOrderNotApprovedToday = data["listOrder"];
			for(var i in listOrderNotApprovedToday){
				var estimatedDeliveryDate = listOrderNotApprovedToday[i].estimatedDeliveryDate;
				var shipBeforeDate = listOrderNotApprovedToday[i].shipBeforeDate;
				var shipAfterDate = listOrderNotApprovedToday[i].shipAfterDate;
				if(estimatedDeliveryDate){
					listOrderNotApprovedToday[i].estimatedDeliveryDate = estimatedDeliveryDate.time;
				}
				if(shipBeforeDate){
					listOrderNotApprovedToday[i].shipBeforeDate = shipBeforeDate.time;
				}
				if(shipAfterDate){
					listOrderNotApprovedToday[i].shipAfterDate = shipAfterDate.time;
				}
			}
			bindingDataOrderNotApprovedToday(listOrderNotApprovedToday);
		});
	}
	
	function bindingDataOrderNotApprovedToday(listOrderNotApprovedToday) {
		var source =
		{
				datafields:
				[
					{ name: "orderId", type: "string" },
					{ name: "shipBeforeDate", type: "date" },
					{ name: "shipAfterDate", type: "date" },
					{ name: "detail", type: "string" }
				],
				localdata: listOrderNotApprovedToday,
				datatype: "array"
		}; 
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#jqxgridOrderApprovalToday").jqxGrid($.extend({
			source: dataAdapter,
			columns:
			[
				{ text: "${uiLabelMap.POOrderId}", datafield: "orderId", width: 150 },
				{ text: "${uiLabelMap.ShippingTime}", dataField: "shipBeforeDate", cellsformat: "dd/MM/yyyy", filtertype:"range",
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
				},
				{ text: "${uiLabelMap.OrderDetail}", datafield: "detail", width: 150, filterable: false, sortable: false,
					cellsrenderer: function(row, columns, value, defaulthtml, columnproperties, rowdata) {
						return "<span><a target=\"_blank\" href=\"" + "viewDetailPO?orderId=" + rowdata.orderId + "\"><i class=\"fa fa-eye\"></i>" + "${uiLabelMap.OrderDetail}" + "</a></span>";
					}
				}
			] 
		}, LocalData.config.jqxgrid));
		
		var wtmp = window;
		var tmpwidth = $("#alterpopupWindowOrderNeedApprovalToday").jqxWindow("width");
		$("#alterpopupWindowOrderNeedApprovalToday").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
		$("#alterpopupWindowOrderNeedApprovalToday").jqxWindow("open");
	}
</script>
