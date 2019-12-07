if (typeof (GridDetailReview) == "undefined") {
	var GridDetailReview = (function() {
		var reviewGrid;
		var initJqxElements = function() {
			reviewGrid.jqxGrid({
				localization : getLocalization(),
				width : "100%",
				height : 380,
				theme : theme,
				source : [],
				sortable : true,
				pagesize : 15,
				pageable : true,
				columnsresize : true,
				showfilterrow : true,
				filterable : true,
				rowdetails : true,
				rowdetailstemplate :
				{
					rowdetails : "<div id=\"grid\" style=\"margin: 10px;\"></div>",
					rowdetailsheight : 220,
					rowdetailshidden : true
				},
				initrowdetails : initrowdetails,
				selectionmode : "singlerow",
				columns :
				[
					{ text : uiLabelMap.DmsSequenceId, datafield : "", sortable : false, filterable : false, pinned : true, groupable : false, draggable : false, resizable : false, width : 40,
						cellsrenderer : function(row, column, value) {
							return "<div style=margin:4px;>" + (row + 1) + "</div>";
						}
					},
					{ text: uiLabelMap.SettingProductID, datafield: "productId", pinned: true, width: 150 },
					{ text: uiLabelMap.SettingProductName, datafield: "productName", pinned: true, width: 250 },
					{ text: uiLabelMap.Quantity, datafield: "quantity", width: 120,
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							return "<div style=\"margin:4px;text-align: right;\">" + value.toLocaleString(locale) + "</div>";
						}
					},
					{ text: uiLabelMap.UnitPrice, datafield: "lastPrice", columntype: "numberinput", width: 150,
						cellsrenderer: function (row, column, value) {
							var data = mainGrid.jqxGrid("getrowdata", row);
							if (data && data.lastPrice) {
								return "<div style=\"margin:4px;text-align: right;\">" + commonObject.formatcurrency(data.lastPrice, data.currencyUomId) + "</div>";
							} else {
								return "<div style=\"margin:4px;text-align: right;\">" + commonObject.formatcurrency(0, data.currencyUomId) + "</div>";
							}
						}
					},
					{ text: uiLabelMap.BACCTotal, datafield: "grandTotal", width: 150,
						cellsrenderer: function (row, column, value) {
							var data = mainGrid.jqxGrid("getrowdata", row);
							if (data && data.grandTotal) {
								return "<div style=\"margin:4px;text-align: right;\">" + commonObject.formatcurrency(data.grandTotal, data.currencyUomId) + "</div>";
							} else {
								return "<div style=\"margin:4px;text-align: right;\">" + commonObject.formatcurrency(0, data.currencyUomId) + "</div>";
							}
						}
					},
					{ text: uiLabelMap.SettingNotes, datafield: "comments", minWidth: 200 }
				],
				handlekeyboardnavigation : function(event) {
					var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
					if (key == 70 && event.ctrlKey) {
						reviewGrid.jqxGrid("clearfilters");
						return true;
					}
				}
			});
		};
		var initrowdetails = function(index, parentElement, gridElement, datarecord) {
			var gridDetails = $($(parentElement).children()[0]);
			$(gridDetails).attr("id", "jqxgridReviewDetail" + index);
			
			var source =
			{
				localdata: datarecord.rowDetails,
				datatype: "array"
			};
			var dataAdapter = new $.jqx.dataAdapter(source);
			
			gridDetails.jqxGrid({
				localization : getLocalization(),
				width : "95%",
				height : 208,
				theme : theme,
				source : dataAdapter,
				sortable : true,
				pagesize : 5,
				pageable : true,
				columnsresize : true,
				showfilterrow : true,
				filterable : true,
				selectionmode : "singlerow",
				columns :
				[
					{ text : uiLabelMap.DmsSequenceId, datafield : "", sortable : false, filterable : false, pinned : true, groupable : false, draggable : false, resizable : false, width : 40,
						cellsrenderer : function(row, column, value) {
							return "<div style=margin:4px;>" + (row + 1) + "</div>";
						}
					},
					{ text : uiLabelMap.SettingFacilityId, datafield : "facilityId", width : 100 },
					{ text : uiLabelMap.SettingFacilityName, datafield : "facilityName", minwidth : 180 },
					{ text : uiLabelMap.SettingSummaryQOH, datafield : "qoh", width : 100,
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							return "<div style=\"margin:4px;text-align: right;\">" + value.toLocaleString(locale) + "</div>";
						}
					},
					{ text : uiLabelMap.SettingQOO, datafield : "qoo", width : 100,
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							return "<div style=\"margin:4px;text-align: right;\">" + value.toLocaleString(locale) + "</div>";
						}
					},
					{ text : uiLabelMap.SettingQty, datafield : "quantity", columntype : "numberinput", width : 100,
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							return "<div style=\"margin:4px;text-align: right;\">" + value.toLocaleString(locale) + "</div>";
						}
					},
					{ text : uiLabelMap.SettingQPDL, datafield : "qpdL", width : 100,
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							return "<div style=\"margin:4px;text-align: right;\">" + value.toLocaleString(locale) + "</div>";
						}
					},
					{ text : uiLabelMap.SettingQPDS, datafield : "qpdS", width : 100,
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							return "<div style=\"margin:4px;text-align: right;\">" + value.toLocaleString(locale) + "</div>";
						}
					},
					{ text : uiLabelMap.SettingLIDL, datafield : "lidL", width : 100,
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							return "<div style=\"margin:4px;text-align: right;\">" + value.toLocaleString(locale) + "</div>";
						}
					},
					{ text : uiLabelMap.SettingLIDS, datafield : "lidS", width : 100,
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							return "<div style=\"margin:4px;text-align: right;\">" + value.toLocaleString(locale) + "</div>";
						}
					},
					{ text : uiLabelMap.SettingLastsold, datafield : "lastSold", editable : false, cellsformat : "dd/MM/yyyy", width : 100 },
					{ text : uiLabelMap.SettingLastReceived, datafield : "lastReceived", editable : false, cellsformat : "dd/MM/yyyy", width : 100 },
					{ text : uiLabelMap.SettingStatus, datafield : "status", width : 120 }
				],
				handlekeyboardnavigation : function(event) {
					var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
					if (key == 70 && event.ctrlKey) {
						gridDetails.jqxGrid("clearfilters");
						return true;
					}
				}
			});
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				var source =
				{
					localdata: data,
					datatype: "array"
				};
				var dataAdapter = new $.jqx.dataAdapter(source);
				
				reviewGrid.jqxGrid({ source : dataAdapter });
			}
		};
		return {
			init: function() {
				reviewGrid = $("#jqxGridProductListReview");
				initJqxElements();
			},
			setValue: setValue
		};
	})();
}