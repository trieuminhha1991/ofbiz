if (typeof (AddProduct) == "undefined") {
	var AddProduct = (function() {
		var initJqxElements = function() {
			$("#jqxwindowAddProductToCategory").jqxWindow({
				theme: "olbius", width: 550, maxWidth: 1845, minHeight: 210, height: 240, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#cancelAddProductToCategory"), modalOpacity: 0.7
			});
			
			var initSalesmanDrDGrid = function(dropdown, grid, width){
				var datafields = [{ name: "productId", type: "string" },
				                  { name: "productCode", type: "string" },
				                  { name: "productName", type: "string" }];
				var columns = [{text: multiLang.DmsPartyId, datafield: "productCode", width: 200},
				               {text: multiLang.DmsPartyLastName, datafield: "productName", minwidth: 200}];
				GridUtils.initDropDownButton({
					url: "", autorowheight: false, filterable: true, showfilterrow: true,
					width: width ? width : 600, source: {id: "productId", pagesize: 5},
							handlekeyboardnavigation: function (event) {
								var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
								if (key == 70 && event.ctrlKey) {
									$("#jqxgridProductId").jqxGrid("clearfilters");
									return true;
								}
							}, dropdown: {width: 220}
				}, datafields, columns, null, grid, dropdown, "productId", "productName");
			};
			initSalesmanDrDGrid($("#txtProductId"),$("#jqxgridProductId"), 600);

			$("#txtSequenceNum").jqxNumberInput({ inputMode: "simple", spinButtons: true, theme: "olbius", width: 220, decimalDigits: 0, min: 0 });
		};
		var handleEvents = function() {
			$("#jqxwindowAddProductToCategory").on("open", function () {
				$("#jqxgridProductId").jqxGrid("clearSelection");
				Grid.cleanDropDownValue($("#txtProductId"));
				$("#txtSequenceNum").jqxNumberInput("val", 0);
			});
			$("#jqxwindowAddProductToCategory").on("close", function () {
				$("#jqxwindowAddProductToCategory").jqxValidator("hide");
			});

			$("#saveAddProductToCategory").click(function () {
				if ($("#jqxwindowAddProductToCategory").jqxValidator("validate")) {
					var row = {};
					row.productCategoryId = $("#lblProductCategoryId").text();
					row.productId = Grid.getDropDownValue($("#txtProductId")).trim();
					row.sequenceNum = $("#txtSequenceNum").jqxNumberInput('val');
					DataAccess.execute({
						url: "addProductToCategoryAjax",
						data: row},
						Products.notify);
					$("#jqxwindowAddProductToCategory").jqxWindow("close");
					Products.refresh();
				}
			});
		};
		var initValidator = function() {
			$("#jqxwindowAddProductToCategory").jqxValidator({
			    rules: [{ input: "#txtProductId", message: multiLang.fieldRequired, action: "close",
							rule: function (input, commit) {
								var value = input.jqxDropDownButton("val").trim();
								if (value) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtSequenceNum", message: multiLang.DmsQuantityNotValid, action: "valueChanged",
							rule: function (input, commit) {
								var value = input.jqxNumberInput('val');
								if (value > 0) {
									return true;
										}
								return false;
							}
		                }]
			});
		};
		var open = function() {
			var rowIndexEditing = $("#treeGrid").jqxTreeGrid("getSelection");

			var adapter = $("#jqxgridProductId").jqxGrid('source');
			if(adapter){
				adapter.url = "jqxGeneralServicer?sname=JQGetListProductNotInCategory&productCategoryId=" + rowIndexEditing[0].productCategoryId;
				adapter._source.url = "jqxGeneralServicer?sname=JQGetListProductNotInCategory&productCategoryId=" + rowIndexEditing[0].productCategoryId;
				$("#jqxgridProductId").jqxGrid('source', adapter);
			}
			
			var wtmp = window;
			var tmpwidth = $('#jqxwindowAddProductToCategory').jqxWindow('width');
	        $("#jqxwindowAddProductToCategory").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
	        $("#jqxwindowAddProductToCategory").jqxWindow('open');
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				initValidator();
			},
			open: open,
		};
	})();
}