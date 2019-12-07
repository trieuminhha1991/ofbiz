<div id="addStockEventItem" style="display:none;" data-update=false data-eventId=null>
	<div id="addStockEventItem-title">${uiLabelMap.CommonAdd}</div>
	<div class="form-window-content-custom">
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.UPC}</label></div>
			<div class="span8">
				<div id="txtIdValueASEI">
					<div style="border-color: transparent;" id="jqxgridIdValueASEI"></div>
				</div>
			</div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.DmsSoLuongKiem}</label></div>
			<div class="span8"><div id="txtQuantityASEI"></div></div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.Location}</label></div>
			<div class="span8"><div id="txtLocationASEI"></div></div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span12">
				<button id="btnCancelStockEventItem" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="btnSaveStockEventItem" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<script>
$(document).ready(function() {
	AddStockEventItem.init();
});
if (typeof (AddStockEventItem) == "undefined") {
	var AddStockEventItem = (function() {
		var jqxwindow, mainGrid, productDDB;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({ theme : theme, width : 450, height : 230, resizable : false, isModal : true,
				autoOpen : false, cancelButton : $("#btnCancelStockEventItem"), modalOpacity : 0.7 });
			
			var initProductDrDGrid = function(dropdown, grid, width){
				var datafields =
				[
					{ name: "productCode", type: "string" },
					{ name: "productName", type: "string" },
					{ name: "idValue", type: "string" }
				];
				var columns =
				[
					{ text: "${StringUtil.wrapString(uiLabelMap.BSProductId)}", datafield: "productCode", width: 200 },
					{ text: "${StringUtil.wrapString(uiLabelMap.BSProductName)}", datafield: "productName", width: 200 },
					{ text: "${StringUtil.wrapString(uiLabelMap.UPC)}", datafield: "idValue" }
				];
				var configProduct = {
					useUrl: true,
					widthButton: 218,
					showdefaultloadelement: false,
					autoshowloadelement: false,
					datafields: datafields,
					columns: columns,
					url: "JQGetListGoodIdentificationAndProduct",
					useUtilFunc: true,
					key: "idValue",
					keyCode: "productCode",
					description: ["productName"],
					autoCloseDropDown: true,
					filterable: true
				};
				productDDB = new OlbDropDownButton(dropdown, grid, null, configProduct, []);
			};
			initProductDrDGrid($("#txtIdValueASEI"), $("#jqxgridIdValueASEI"), 600);
			
			$("#txtQuantityASEI").jqxNumberInput({ theme : theme, inputMode : "simple", width : 218, height: 30, decimalDigits : 2, min : 1, spinButtons : true });
			$("#txtLocationASEI").jqxComboBox({ source: [], displayMember: "text", valueMember: "value", width: 218, height: 27, theme: theme });
		};
		var handleEvents = function() {
			$("#btnSaveStockEventItem").click(function() {
				if (jqxwindow.jqxValidator("validate")) {
					var row = {};
					row = {
						eventId: eventId,
						idValue : productDDB.getValue(),
						quantity: $("#txtQuantityASEI").jqxNumberInput("getDecimal"),
						location : $("#txtLocationASEI").jqxComboBox("val"),
						editable: "Y"
					};
					mainGrid.jqxGrid("addRow", null, row, "first");
					jqxwindow.jqxWindow("close");
				}
			});
			jqxwindow.on("open", function() {
				reloadLocation();
			});
			jqxwindow.on("close", function() {
				jqxwindow.jqxValidator("hide");
				productDDB.clearAll();
				$("#txtQuantityASEI").jqxNumberInput("setDecimal", 0);
				$("#txtLocationASEI").jqxComboBox("clearSelection");
				$("#jqxgridIdValueASEI").jqxGrid("clearselection");
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator(
					{
						rules :
						[
							{ input: "#txtIdValueASEI", message: multiLang.fieldRequired, action: "close", 
								rule: function (input, commit) {
									var value = productDDB.getValue();
									if (value.trim()) {
										return true;
									}
									return false;
								}
							},
							{ input: "#txtQuantityASEI", message: multiLang.DmsQuantityNotValid, action: "change",
								rule: function (input, commit) {
									return input.jqxNumberInput("getDecimal") > 0;
								}
							},
							{ input: "#txtLocationASEI", message: multiLang.fieldRequired, action: "change",
								rule: function (input, commit) {
									return !!$("#txtLocationASEI").jqxComboBox("val");
								}
							}
						],
						scroll : false
					});
		};
		var reloadLocation = function () {
			var source =
			{
				datatype: "json",
				datafields: [
						{ name: "text" },
						{ name: "value" },
						{ name: "statusId" }
				],
				url: "loadLocationInEvent?eventId=${stockEvent.eventId}",
				async: false
			};
			var dataAdapter = new $.jqx.dataAdapter(source);
			$("#txtLocationASEI").jqxComboBox({ source: dataAdapter });
		};
		return {
			init : function() {
				jqxwindow = $("#addStockEventItem");
				mainGrid = $("#jqxGridPhieukk");
				initJqxElements();
				handleEvents();
				initValidator();
			}
		};
	})();
}
</script>