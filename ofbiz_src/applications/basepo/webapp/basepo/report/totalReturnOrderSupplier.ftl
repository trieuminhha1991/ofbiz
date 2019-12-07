<div>
	<div class="titleTextReturnSupplier">
		<i class="fa-history"></i> <lable style="cursor: auto;">${uiLabelMap.ReturnSupplierSummary}</lable>
	</div>
	<div class="valueTotal">
		<div class="form-window-container">
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">	
						<div class="span11" style="text-align: left">
							<label style="cursor: auto;"> ${uiLabelMap.TotalOrderReturn}: </label>
						</div>
						<div class="span1" style="text-align: right">
							<a style="text-decoration: underline !important; color: #333;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showOrderReturnSupplier('TotalOrderReturn')">
								<div id="totalReturnOrderSupplier"></div>
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
		<script type="text/javascript">
			$( document ).ready(function() {
				$.ajax({
					url: "getTotalReturnOrderSupplier",
					type: "post",
					data: {checkTime: "TotalOrderReturn"},
					success: function(data) {
						var orderReturnCount = data.orderReturnCount;
						$("#totalReturnOrderSupplier").html(orderReturnCount.toLocaleString(locale));
					}
				});
			});
		</script>
	</div>
	
	<div class="valueTotal">
		<div class="form-window-container">
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">	
						<div class="span11" style="text-align: left">
							<label style="cursor: auto;"> ${uiLabelMap.OrderReturnThisMonth}: </label>
						</div>
						<div class="span1" style="text-align: right">
							<a style="text-decoration: underline !important; color: #333;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showOrderReturnSupplier('OrderReturnThisMonth')"> 
								<div id="totalOrderReturnThisMonth"></div> 
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
		<script type="text/javascript">
			$( document ).ready(function() {
				$.ajax({url: "getTotalReturnOrderSupplier",
					type: "post",
					data: {checkTime: "OrderReturnThisMonth"},
					success: function(data) {
						var orderReturnCount = data.orderReturnCount;
						$("#totalOrderReturnThisMonth").html(orderReturnCount.toLocaleString(locale));
					}
				});
			});
		</script>
	</div>
	
	<div class="valueTotal">
		<div class="form-window-container">
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">
						<div class="span11" style="text-align: left">
							<label style="cursor: auto;"> ${uiLabelMap.OrderReturnToday}: </label>
						</div>
						<div class="span1" style="text-align: right">
							<a style="text-decoration: underline !important; color: #333;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showOrderReturnSupplier('OrderReturnToday')"> 
								<div id="totalOrderReturnToday"></div>
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
		<script type="text/javascript">
			$( document ).ready(function() {
				$.ajax({url: "getTotalReturnOrderSupplier",
					type: "post",
					data: {checkTime: "OrderReturnToday"},
					success: function(data) {
						var orderReturnCount = data.orderReturnCount;
						$("#totalOrderReturnToday").html(orderReturnCount.toLocaleString(locale));
					}
				});
			});
		</script>
	</div>
</div>


<div id="alterpopupWindowReturnSupplier" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.ListOrderNeedReceiveToday}
	</div>
	<div class="form-window-container">
		<div class="row-fluid">
			<div class="span12">
				<div id="jqxgridReturnSupplier"></div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="btnCancelReturnSupplier" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>
</div>

<script>
	$(document).ready(function() {
		$("#alterpopupWindowReturnSupplier").jqxWindow($.extend( {cancelButton: $("#btnCancelReturnSupplier")}, LocalData.config.jqxwindow ));
	});
	
	function showOrderReturnSupplier(valueReturn) {
		if (valueReturn == "TotalOrderReturn") {
			$("#alterpopupWindowReturnSupplier").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.ListOrderReturnSupplier)}");
		}
		if (valueReturn == "OrderReturnToday") {
			$("#alterpopupWindowReturnSupplier").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.ListOrderReturnSupplierToday)}");
		}
		if (valueReturn == "OrderReturnThisMonth") {
			$("#alterpopupWindowReturnSupplier").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.OrderReturnThisMonth)}");
		}
		viewListReturnOrderSupplier(valueReturn);
	}
	
	function viewListReturnOrderSupplier(valueReturn) {
		$.ajax({
			url: "loadListReturnOrderSupplier",
			type: "POST",
			data: { valueCheck: valueReturn },
			dataType: "json"
		}).done(function(data) {
			var listReturnSupplier = data["listReturn"];
			for (var i in listReturnSupplier) {
				var entryDate = listReturnSupplier[i].entryDate;
				if (entryDate) {
					listReturnSupplier[i].entryDate = entryDate.time;
				}
			}
			bindingDataOrderReceiveView(listReturnSupplier);
		});
	}
	
	function bindingDataOrderReceiveView(listReturnSupplier) {
		var source =
		{
			datafields:
			[
				{ name: "returnId", type: "string" },
				{ name: "entryDate", type: "date" },
				{ name: "detail", type: "string" }
			],
			localdata: listReturnSupplier,
			datatype: "array"
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#jqxgridReturnSupplier").jqxGrid($.extend({
			source: dataAdapter,
			columns:
			[
				{ text: "${uiLabelMap.POReturnId}", datafield: "returnId", width: 200 },
				{ text: "${uiLabelMap.POReturnDate}", dataField: "entryDate", cellsformat: "dd/MM/yyyy", filtertype:"range" },
				{ text: "${uiLabelMap.POShowViewDetail}", datafield: "detail", width: "150", filterable: false, sortable: false,
					cellsrenderer: function(row, columns, value, defaulthtml, columnproperties, rowdata) {
						return "<span><a target=\"_blank\" href=\"" + "viewGeneralReturnSupplier?returnId=" + rowdata.returnId + "\"><i class=\"fa fa-eye\"></i>" + "${uiLabelMap.POShowViewDetail}" + "</a></span>";
					}
				}
			]
		}, LocalData.config.jqxgrid));
		var wtmp = window;
		var tmpwidth = $("#alterpopupWindowReturnSupplier").jqxWindow("width");
		$("#alterpopupWindowReturnSupplier").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
		$("#alterpopupWindowReturnSupplier").jqxWindow("open");
	}
</script>