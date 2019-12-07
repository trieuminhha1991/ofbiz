<script type="text/javascript" id="receiveWarehouse">
	$(function () {
		var checkFirst = false;
		var olapTypeDataSoure =
		[
			{
				text: "${uiLabelMap.LogEXPORT}",
				value: "EXPORT"
			},
			{
				text: "${uiLabelMap.LogRECEIVE}",
				value: "RECEIVE"
			},
			{ 
				text: "${uiLabelMap.LogINVENTORY}",
				value: "INVENTORY"
			}
			];
		var optionFilterDataInventory =
		[
			{
				text: "${StringUtil.wrapString(uiLabelMap.LogProductInventoryAtMost)}",
				value: "FILTER_MAX"
			},
			{
				text: "${StringUtil.wrapString(uiLabelMap.LogProductInventoryAtLeast)}",
				value: "FILTER_MIN" 
			}
		];
		var optionFilterDataReceive =
		[
			{
				text: "${StringUtil.wrapString(uiLabelMap.LogProductReceiveAtMost)}",
				value: "FILTER_MAX"
			},
			{
				text: "${StringUtil.wrapString(uiLabelMap.LogProductReceiveAtLeast)}",
				value: "FILTER_MIN" 
			}
		];
		var optionFilterDataExport =
		[
			{
				text: "${StringUtil.wrapString(uiLabelMap.LogProductExportAtMost)}",
				value: "FILTER_MAX"
			},
			{
				text: "${StringUtil.wrapString(uiLabelMap.LogProductExportAtLeast)}",
				value: "FILTER_MIN" 
			}
		];
		var optionFilterDataAvaible =
		[
			{
				text: "${StringUtil.wrapString(uiLabelMap.LogProductOrderMost)}",
				value: "FILTER_MAX"
			},
			{
				text: "${StringUtil.wrapString(uiLabelMap.LogProductOrderLeast)}",
				value: "FILTER_MIN" 
			}
		];
		var config = {
				chart: {
					type: "column"
				},
				title: {
					text: "${StringUtil.wrapString(uiLabelMap.LogPresentChartEXPORTrECEIVEiNVENTORY)}",
					x: -20 //center
				},
				xAxis: {
					type: "category",
					labels: {
						rotation: -45,
						style: {
							fontSize: "13px",
							fontFamily: "Verdana, sans-serif"
						}
					},
					title : {
						text: null
					},
				},
				yAxis: {
					plotLines: [{
						value: 0,
						width: 1,
						color: "#808080"
					}],
					title : {
						text: null
					},
					min: 0
				},
				plotOptions: {
					series: {
						maxPointWidth: 30
					}
				},
				legend: {
					enabled: false
				},
				tooltip: {
					formatter: function () {
						return "<i>" + LocalData.object.mapProducsWithCode[this.x] + "</i>" + "<br/>" + "<b>" + this.y.toLocaleString(locale) + "</b>";
					}
				}
		};
		var configPopup =
		[
			{
				action : "addDateTimeInput",
				params : [{
					id : "from_date",
					label : "${StringUtil.wrapString(uiLabelMap.olap_fromDate)}",
					value: OLBIUS.dateToString(LocalData.date.firstDayOfMonth)
				}],
				before: "thru_date"
			},
			{
				action : "addDateTimeInput",
				params : [{
					id : "thru_date",
					label : "${StringUtil.wrapString(uiLabelMap.olap_thruDate)}",
					value: OLBIUS.dateToString(LocalData.date.currentDate)
				}],
				after: "from_date"
			},
			{
				action : "addDropDownList",
				params : [{
					id : "olapType",
					label : "${StringUtil.wrapString(uiLabelMap.LogReport)}",
					data : olapTypeDataSoure,
					index: 0
				}],
				event : function(popup) {
					popup.onEvent("olapType", "select", function(event) {
						var args = event.args;
						var item = popup.item("olapType", args.index);
						var olapType = item.value;
						if (olapType == "INVENTORY") {
							popup.show("facilityId");
							popup.hide("categoryId");
							popup.hide("enumId");
							popup.hide("filterTypeIdReceive");
							popup.hide("filterTypeIdExport");
							popup.show("filterTypeIdInventory");
						}
						if (olapType != "INVENTORY" && olapType != "TYPE_BOOK") {
							if (olapType == "RECEIVE") {
								popup.show("filterTypeIdReceive");
								popup.hide("filterTypeIdInventory");
								popup.hide("filterTypeIdExport");
								popup.hide("enumId");
							} else if (olapType == "EXPORT") {
								popup.show("filterTypeIdExport");
								popup.hide("filterTypeIdReceive");
								popup.hide("filterTypeIdInventory");
								popup.show("enumId");
							}
							popup.show("facilityId");
							popup.show("categoryId");
						}
						popup.resize();
					});
				}
			},
			{
				action : "addJqxGridMultil",
				params : [{
					id : "facilityId",
					title1: "${StringUtil.wrapString(uiLabelMap.FacilityId)}",
					title2: "${StringUtil.wrapString(uiLabelMap.LogFacilityName)}",
					label : "${StringUtil.wrapString(uiLabelMap.LogWarehouse)}",
					data : LocalData.array.facilities,
					value: []
				}]
			},
			{
				action : "addJqxGridMultil",
				params : [{
					id : "categoryId",
					title1: "${StringUtil.wrapString(uiLabelMap.productCategoryId)}",
					title2: "${StringUtil.wrapString(uiLabelMap.CategoryName)}",
					label : "${StringUtil.wrapString(uiLabelMap.DmsProductCatalogs)}",
					data : LocalData.array.categories,
					value: [],
					index: [0],
				}],
			},
			{
				action : "addJqxGridMultil",
				params : [{
					id : "enumId",
					title1: "${StringUtil.wrapString(uiLabelMap.LogCodeChannel)}",
					title2: "${StringUtil.wrapString(uiLabelMap.LogPurchaseChannels)}",
					label : "${StringUtil.wrapString(uiLabelMap.LogPurchaseChannels)}",
					data : LocalData.array.enumerations_SALES_METHOD_CHANNEL,
					value: []
				}]
			},
			{
				action : "addDropDownList",
				params : [{
					id : "limitId",
					label : "${StringUtil.wrapString(uiLabelMap.POTopProduct)}",
					data : LocalData.array.optionLimit,
					index: 4
				}]
			},
			{
				action : "addDropDownList",
				params : [{
					id : "filterTypeIdInventory",
					label : "${StringUtil.wrapString(uiLabelMap.POTypeFilter)}",
					data : optionFilterDataInventory,
					index: 1,
					hide: true
				}]
			},
			{
				action : "addDropDownList",
				params : [{
					id : "filterTypeIdReceive",  
					label : "${StringUtil.wrapString(uiLabelMap.POTypeFilter)}",
					data : optionFilterDataReceive,
					index: 1,
					hide: true
				}]
			},
			{
				action : "addDropDownList",
				params : [{
					id : "filterTypeIdExport",
					label : "${StringUtil.wrapString(uiLabelMap.POTypeFilter)}",
					data : optionFilterDataExport,
					index: 1
				}]
			}];

		var receiveWarehouseCharOlap = OLBIUS.oLapChart("receiveWarehouse", config, configPopup, "receiveWarehouseChartOlap", true, true, OLBIUS.defaultColumnFunc);

		receiveWarehouseCharOlap.funcUpdate(function (oLap) {
			var tmp = oLap.val("categoryId");
			if (tmp != undefined && tmp != null && tmp.length <= 0 && checkFirst == false){
				tmp.push(LocalData.array.categories[0].value);
			}
			oLap.update({
				fromDate: oLap.val("from_date"),
				thruDate: oLap.val("thru_date"),
				statusId: oLap.val("statusId"),
				limitId: oLap.val("limitId"),
				filterTypeIdInventory: oLap.val("filterTypeIdInventory"),
				filterTypeIdReceive: oLap.val("filterTypeIdReceive"),
				filterTypeIdExport: oLap.val("filterTypeIdExport"),
				facilityId: oLap.val("facilityId"),
				categoryId: tmp,
				enumId: oLap.val("enumId"),
				olapType: oLap.val("olapType"),
				typeChart: "quantity"
			});
			checkFirst = true;
		});
		receiveWarehouseCharOlap.init(function () {
			receiveWarehouseCharOlap.runAjax();
		});
	});
</script>