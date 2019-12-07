<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
<#assign orderHeaders = delegator.findList("OrderHeaderAndOrderRolePO", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", "ORDER_APPROVED", "orderTypeId", "PURCHASE_ORDER", "customerId", company)), null, null, null, true) />
<script>
var listOrderImpToday = [];
<#list orderHeaders as header>
	<#assign supplier = delegator.findOne("PartyNameView", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", header.sellerId?if_exists), true)/>
	<#assign orderItems = delegator.findList("OrderItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", header.orderId?if_exists)), null, null, null, true) />
	var orderId = "${header.orderId}";
	<#list orderItems as item>
		<#if item.shipAfterDate?exists || item.estimatedDeliveryDate?exists>
			var item = {
				orderId: orderId,
				estimatedDeliveryDate: "${item.estimatedDeliveryDate?if_exists}",
				shipBeforeDate: "${item.shipBeforeDate?if_exists}",
				shipAfterDate: "${item.shipAfterDate?if_exists}",
				supplierName: "${StringUtil.wrapString(supplier.lastName?if_exists)} " + "${StringUtil.wrapString(supplier.middleName?if_exists)} " + "${StringUtil.wrapString(supplier.firstName?if_exists)} " + "${StringUtil.wrapString(supplier.groupName?if_exists)}",
			};
			var date1 = new Date("${item.shipAfterDate?if_exists}");
			var date2 = new Date("${item.estimatedDeliveryDate?if_exists}");
			var today = new Date();
			if ((date1 && date1.getDate() == today.getDate()) || (date2 && date2.getDate() == today.getDate())){
				listOrderImpToday.push(item);
			}
		</#if>
		<#break>
	</#list>
</#list>
</script>
<div class="totalValueReceiveStyle">
	<i class="fa-cart-plus"></i> <lable>${uiLabelMap.TotalOrderNeedReceiveToday}</lable>
	<a href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showOrderReceiveToday()" >
		<div id="orderReceiveToday"></div>
	</a>
</div> 

<div id="alterpopupWindowReceiveToday" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.ListOrderNeedReceiveToday}
	</div>
	<div class="form-window-container">
		<div class="row-fluid">
			<div class="span12">
				<div id="jqxgridOrderReceiveToday"></div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="btnCancelReceiveToday" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>
</div>

<script>
	$(document).ready(function() {
		$("#orderReceiveToday").html(formatnumber(listOrderImpToday.length.toLocaleString(locale)));
		$("#alterpopupWindowReceiveToday").jqxWindow($.extend( {cancelButton: $("#btnCancelReceiveToday")}, LocalData.config.jqxwindow ));
		
		var source =
		{
			datafields:
			[
				{name: "orderId", type: "string"},
				{name: "estimatedDeliveryDate", type: "date"},
				{name: "shipBeforeDate", type: "date"},
				{name: "shipAfterDate", type: "date"}
			],
			localdata: listOrderImpToday,
			datatype: "array"
		}; 
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#jqxgridOrderReceiveToday").jqxGrid($.extend({
			source: dataAdapter,
			columns: [
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
				{ text: "${uiLabelMap.Supplier}", datafield: "supplierName", minwidth: 200,
					cellsrenderer: function(row, colum, value) {
						return "<span>" + value.trim() + "</span>";
					}
				},
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
				}]
			}, LocalData.config.jqxgrid));
	});


	function showOrderReceiveToday(){   
		var wtmp = window;
		var tmpwidth = $("#alterpopupWindowReceiveToday").jqxWindow("width");
		$("#alterpopupWindowReceiveToday").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
		$("#alterpopupWindowReceiveToday").jqxWindow("open");
	}
</script>