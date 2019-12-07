<script type="text/javascript" id="totalOrderNeedApproval">
	$(function() {
		var textView = OLBIUS.textView({
			id :"totalOrderNeedApproval",
			url: "getTotalOrderNeedApprovalToday",
			icon: "fa fa-check",
			data: {checkTime: "OrderNotYetApproved"},
			renderTitle: function(data) {
				return "${StringUtil.wrapString(uiLabelMap.OrderNotYetApproved)}"
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
			showListOrderNotYetApproved("OrderNotYetApproved");
		});
	});
</script>

<div id="alterpopupWindowOrderNeedApproval" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.ListOrdersNotYetApproved}
	</div>
	<div class="form-window-container">
		<div class="row-fluid">
			<div class="span12">
				<div id="jqxgridOrderApproval"></div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="btnCancelOrderNeedApproval" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>
</div>

<script>
	$(document).ready(function() {
		$("#alterpopupWindowOrderNeedApproval").jqxWindow($.extend( {cancelButton: $("#btnCancelOrderNeedApproval")}, LocalData.config.jqxwindow ));
	});
	
	function showListOrderNotYetApproved(valueOrderNotApproved){
		$("#alterpopupWindowOrderNeedApproval").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.ListOrdersNotYetApproved)}");
		viewListOrderNotApproved(valueOrderNotApproved);
	}
	
	function viewListOrderNotApproved(valueOrderNotApproved){
		$.ajax({
			url: "loadListPurchaseOrder",
			type: "POST",
			data: {valueCheck: valueOrderNotApproved},
			dataType: "json"
		}).done(function(data) { 
			var listOrderNotApproved = data["listOrder"];
			for(var i in listOrderNotApproved){
				var estimatedDeliveryDate = listOrderNotApproved[i].estimatedDeliveryDate;
				var shipBeforeDate = listOrderNotApproved[i].shipBeforeDate;
				var shipAfterDate = listOrderNotApproved[i].shipAfterDate;
				if(estimatedDeliveryDate){
					listOrderNotApproved[i].estimatedDeliveryDate = estimatedDeliveryDate.time;
				}
				if(shipBeforeDate){
					listOrderNotApproved[i].shipBeforeDate = shipBeforeDate.time;
				}
				if(shipAfterDate){
					listOrderNotApproved[i].shipAfterDate = shipAfterDate.time;
				}
			}
			bindingDataOrderNotApprovedToday(listOrderNotApproved);
		});
	}
	
	function bindingDataOrderNotApprovedToday(listOrderNotApproved) {
		var source =
		{
				datafields:
				[
					{ name: "orderId", type: "string" },
					{ name: "shipBeforeDate", type: "date" },
					{ name: "shipAfterDate", type: "date" },
					{ name: "detail", type: "string" }
				],
				localdata: listOrderNotApproved,
				datatype: "array"
		}; 
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#jqxgridOrderApproval").jqxGrid($.extend({
			source: dataAdapter,
			columns: [
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
				}]
		}, LocalData.config.jqxgrid));
		
		var wtmp = window;
		var tmpwidth = $("#alterpopupWindowOrderNeedApproval").jqxWindow("width");
		$("#alterpopupWindowOrderNeedApproval").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
		$("#alterpopupWindowOrderNeedApproval").jqxWindow("open");
	}
</script>
