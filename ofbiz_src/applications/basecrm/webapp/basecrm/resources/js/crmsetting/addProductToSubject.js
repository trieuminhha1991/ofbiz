if (typeof (AddProduct) == "undefined") {
	var AddProduct = (function() {
		var jqxwindow, productGrid, dropdown;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme : "olbius",
				width : 550,
				maxWidth : 1845,
				minHeight : 210,
				height : 240,
				resizable : false,
				isModal : true,
				autoOpen : false,
				cancelButton : $("#cancelAddProductToSubject"),
				modalOpacity : 0.7
			});

			var initProductDrDGrid = function(dropdown, grid, width) {
				var datafields = [ {
					name : "productId",
					type : "string"
				}, {
					name : "productCode",
					type : "string"
				}, {
					name : "productName",
					type : "string"
				} ];
				var columns = [ {
					text : multiLang.DmsPartyId,
					datafield : "productCode",
					width : 200
				}, {
					text : multiLang.DmsPartyLastName,
					datafield : "productName",
					minwidth : 200
				} ];
				GridUtils.initDropDownButton({
					url : "",
					autorowheight : false,
					filterable : true,
					showfilterrow : true,
					width : width ? width : 600,
					source : {
						id : "productId",
						pagesize : 5
					},
					handlekeyboardnavigation : function(event) {
						var key = event.charCode ? event.charCode
								: event.keyCode ? event.keyCode : 0;
						if (key == 70 && event.ctrlKey) {
							productGrid.jqxGrid("clearfilters");
							return true;
						}
					},
					dropdown : {
						width : 220
					}
				}, datafields, columns, null, grid, dropdown, "productId",
						"productName");
			};
			initProductDrDGrid(dropdown, productGrid, 600);

			$("#txtSequenceNum").jqxNumberInput({
				inputMode : "simple",
				spinButtons : true,
				theme : "olbius",
				width : 220,
				decimalDigits : 0,
				min : 1
			});
		};
		var handleEvents = function() {
			jqxwindow.on("open", function() {
				productGrid.jqxGrid("clearSelection");
				Grid.cleanDropDownValue(dropdown);
				$("#txtSequenceNum").jqxNumberInput("val", 1);
			});
			jqxwindow.on("close", function() {
				jqxwindow.jqxValidator("hide");
			});

			$("#saveAddProductToSubject").click(
					function() {
						if (jqxwindow.jqxValidator("validate")) {
							var row = {};
							row.enumId = $("#lblProductSubjectId").text();
							row.productId = Grid.getDropDownValue(dropdown)
									.trim();
							row.sequenceNum = $("#txtSequenceNum")
									.jqxNumberInput("val");
							Products._add(row);
							jqxwindow.jqxWindow("close");
						}
					});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator({
				rules : [ {
					input : "#txtProductBeAdd",
					message : multiLang.fieldRequired,
					action : "close",
					rule : function(input, commit) {
						var value = input.jqxDropDownButton("val").trim();
						if (value) {
							return true;
						}
						return false;
					}
				}, {
					input : "#txtSequenceNum",
					message : multiLang.DmsQuantityNotValid,
					action : "valueChanged",
					rule : function(input, commit) {
						var value = input.jqxNumberInput("val");
						if (value > 0) {
							return true;
						}
						return false;
					}
				} ],
				scroll : false
			});
		};
		var open = function() {
			var rowindex = mainGrid.jqxGrid("getselectedrowindex");
			var enumId = mainGrid.jqxGrid("getcellvalue", rowindex, "enumId");
			$("#lblProductSubjectId").text(enumId);
			var adapter = productGrid.jqxGrid("source");
			if (adapter) {
				adapter.url = "jqxGeneralServicer?sname=JQGetListProductNotInSubject&enumId="
						+ enumId;
				adapter._source.url = "jqxGeneralServicer?sname=JQGetListProductNotInSubject&enumId="
						+ enumId;
				productGrid.jqxGrid("source", adapter);
			}

			var wtmp = window;
			var tmpwidth = jqxwindow.jqxWindow("width");
			jqxwindow.jqxWindow({
				position : {
					x : (wtmp.outerWidth - tmpwidth) / 2,
					y : pageYOffset + 70
				}
			});
			jqxwindow.jqxWindow("open");
		};
		return {
			init : function() {
				jqxwindow = $("#jqxwindowAddProductToSubject");
				productGrid = $("#jqxgridProductBeAdd");
				dropdown = $("#txtProductBeAdd");
				initJqxElements();
				handleEvents();
				initValidator();
			},
			open : open
		};
	})();
}