<script type="text/javascript" id="DOAReport">
	$(function () {
		var config = {
			title: "${StringUtil.wrapString(uiLabelMap.BCRMDOAReport)}",
			id: "DOAReport",
			olap: "evaluateProductByCallsGridV2",
			sortable: true,
			filterable: true,
			showfilterrow: true,
			columns:
			[
				{ text: "${uiLabelMap.BSNo2}", sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
					datafield: {name: "stt", type: "number"}, width: "3%",
					cellsrenderer: function (row, column, value) {
						return "<div style=margin:4px;>" + (value + 1) + "</div>";
					}
				},  
				{ text: "${StringUtil.wrapString(uiLabelMap.BSProductId)}", datafield: {name:"productId", type: "string"}, width: "17%" },
				{ text: "${StringUtil.wrapString(uiLabelMap.BSProductName)}", datafield: {name:"productName", type: "string"}, width: "30%" },
				{ text: "${StringUtil.wrapString(uiLabelMap.BCRMCalls)}", datafield: {name: "quantity", type: "number"}, width: "15%",
					cellsrenderer: function (row, column, value) {
						return "<div class=\"text-right\">" + value.toLocaleString(locale) + "</div>";
					}
				},
				{ text: "${StringUtil.wrapString(uiLabelMap.BSDescription)}", datafield: {name:"reason", type: "string"}, width: "35%" },
			],
			popup:
			[
				{
					group: "dateTime",
					id: "dateTime"
				}
			],
			apply: function (grid, popup) {
				return $.extend({

				}, popup.group("dateTime").val());
			},
			excel: true,
			exportFileName: "${StringUtil.wrapString(uiLabelMap.BCRMDOAReport)}"
		};

		var grid = OlbiusUtil.grid(config);
	});
</script>