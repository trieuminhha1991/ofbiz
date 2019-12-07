<div>
	<div class="titleTextInventory">
		<i class="fa-archive"></i> <lable>${uiLabelMap.Inventory}</lable>
	</div>
	<div class="valueTotal">
		<div class="form-window-container">
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">
						<div class="span9" style="text-align: left; word-wrap: break-word;">
							<label style="cursor: auto;"> ${uiLabelMap.BLListInventoryExpired}: </label>
						</div>
						<div class="span3" style="text-align: right; word-wrap: break-word;" >
							<a style="text-decoration: underline !important; color: #333;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showListProductInventory('Inventory')">
								<div id="productExpire"></div>
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
		<script type="text/javascript">
			$( document ).ready(function() {
				$.ajax({
					url: "getTotalProductInventoryExpire",
					type: "post"
				}).done(function(data) {
					var productCount = data.productCount;
					$("#productExpire").html(productCount.toLocaleString(locale));
				});
			});
		</script>
	</div>
	
	<div class="valueTotal">
		<div class="form-window-container">
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">
						<div class="span9" style="text-align: left; word-wrap: break-word;">
							<label style="cursor: auto;"> ${uiLabelMap.BLListInventoryNearExpiry}: </label>
						</div>
						<div class="span3" style="text-align: right; word-wrap: break-word;" >
							<a style="text-decoration: underline !important; color: #333;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick="showListProductInventory('BLListInventoryNearExpiry')">
								<div id="productNearExpire"></div>
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
		<script type="text/javascript">
			$( document ).ready(function() {
				$.ajax({
					url: "getTotalProductInventoryNearExpire",
					type: "post"
				}).done(function(data) {
					var productCount = data.productCount;
					$("#productNearExpire").html(productCount.toLocaleString(locale));
				});
			});
		</script>
	</div>
</div>


<div id="alterpopupWindowProductInventory" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.ListTranferRequireToday}
	</div>
	<div class="form-window-container">
		<div class="row-fluid">
			<div class="span12">
				<div id="jqxgridProductInventory"></div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="btnCancelProductInventory" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>
</div>

<script>
	$(document).ready(function() {
		$("#alterpopupWindowProductInventory").jqxWindow($.extend( {cancelButton: $("#btnCancelProductInventory")}, LocalData.config.jqxwindow ));
	});
	
	function showListProductInventory(valueProInv) {
		if (valueProInv == "Inventory") {
			$("#alterpopupWindowProductInventory").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.BLListInventoryExpired)}");
		}
		if (valueProInv == "BLListInventoryNearExpiry") {
			$("#alterpopupWindowProductInventory").jqxWindow("setTitle", "${StringUtil.wrapString(uiLabelMap.BLListInventoryNearExpiry)}");
		}
		viewListProductInventory(valueProInv);
	}
	
	function viewListProductInventory(valueProInv) {
		$.ajax({
			url: "loadListProductInventoryItem",
			type: "POST",
			data: { valueCheck: valueProInv },
			dataType: "json"
		}).done(function(data) {
			var listProductInventory = data["listProductInv"];
			bindingDataProductInventory(listProductInventory);
		});
	}
	
	function bindingDataProductInventory(listProductInventory) {
		var source =
		{
			datafields:
			[
				{ name: "productId", type: "string" },
				{ name: "productCode", type: "string" },
				{ name: "productName", type: "string" },
				{ name: "productCount", type: "number" },
				{ name: "quantityUomId", type: "string" },
				{ name: "thresholdsDate", type: "number" }
			],
			localdata: listProductInventory,
			datatype: "array"
		}; 
		
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#jqxgridProductInventory").jqxGrid($.extend({
			source: dataAdapter,
			columns:
			[
				{ text: "${uiLabelMap.ProductId}", datafield: "productCode", width: "150",
					cellsrenderer: function(row, columns, value, defaulthtml, columnproperties, rowdata) {
						var productId = rowdata.productId;
						var checkThresholdsDate = rowdata.thresholdsDate;
						if (checkThresholdsDate == undefined) {
							return "<span><a target=\"_blank\" href=\"" + "getListInventoryExpiryDate?productId=" + productId + "\"><i class=\"fa fa-eye\"></i>" + value + "</a></span>";
						} else{
							return "<span><a target=\"_blank\" href=\"" + "getListInventoryNearExpiryDate?productId=" + productId + "\"><i class=\"fa fa-eye\"></i>" + value + "</a></span>";
						}
					}
				},
				{ text: "${uiLabelMap.ProductName}", datafield: "productName", minwidth: 200 },
				{ text: "${uiLabelMap.Quantity}", datafield: "productCount", width: 100,
					cellsrenderer: function(row, column, value) {
						return "<div class=\"text-right\">" + value.toLocaleString(locale) + "</div>";
					}
				},
				{ text: "${uiLabelMap.Unit}", datafield: "quantityUomId", width: 100, filtertype: "checkedlist",
					cellsrenderer: function(row, column, value) {
						value = value?LocalData.object.mapUoms_PRODUCT_PACKING[value]:value;
						return "<div style=margin:4px;>" + value + "</div>";
					},
					createfilterwidget: function (column, columnElement, widget) {
						widget.jqxDropDownList({ source: LocalData.array.uoms_PRODUCT_PACKING, displayMember: "text", valueMember: "value" });
					}
				}
			]
		}, LocalData.config.jqxgrid));
		var wtmp = window;
		var tmpwidth = $("#alterpopupWindowProductInventory").jqxWindow("width");
		$("#alterpopupWindowProductInventory").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
		$("#alterpopupWindowProductInventory").jqxWindow("open");
	}
</script>
