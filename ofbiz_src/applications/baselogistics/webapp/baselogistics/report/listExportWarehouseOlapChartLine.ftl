<script type="text/javascript" id="reportExportWarehouseChartLine">
	$(function () {
		var config =
		{
			service: "facilityInventory",
			title: {
				text: "${StringUtil.wrapString(uiLabelMap.LogChartWarehousing)}",
				x: -20 //center
			},
			xAxis: {
				labels: {
					enabled: false
				},
				title : {
					text: null
				}
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
			legend: {
				layout: "vertical",
				align: "right",
				verticalAlign: "middle",
				borderWidth: 0
			},
			tooltip: {
				formatter: function () {
					return "<b>" + this.x + "</b>" + "<br/>" + "<tspan style=\"fill:" + this.color + "\" x=\"8\" dy=\"15\"></tspan></br> <b>${StringUtil.wrapString(uiLabelMap.QuantitySum)}: " + this.y.toLocaleString(locale) + "</b>";
				}
			}
		};

		var configPopup =
		[
			{
				action : "addDropDownList",
				params : [{
					id : "dateType",
					label : "${StringUtil.wrapString(uiLabelMap.CommonPeriod)}",
					data : date_type_source,
					index: 0
				}]
			},
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
					value: []
				}]
			}
		];
		
		var reportExportOLap = OLBIUS.oLapChart("reportExportWarehouseChartLine", config, configPopup, "logReportExportWarehouseChartLine", true, true, OLBIUS.defaultLineFunc);

		reportExportOLap.funcUpdate(function(oLap) {
			oLap.update({
				dateType: oLap.val("dateType"),
				fromDate: oLap.val("from_date"),
				thruDate: oLap.val("thru_date"),
				facilityId: oLap.val("facilityId"),
				categoryId: oLap.val("categoryId")
			}, oLap.val("dateType"));
		});

		reportExportOLap.init(function () {
			reportExportOLap.runAjax();
		});
	});
</script>