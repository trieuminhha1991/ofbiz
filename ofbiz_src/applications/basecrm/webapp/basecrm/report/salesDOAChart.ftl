<script type="text/javascript" id="salesDOAChart">
	$(function () {
		var config = {
				chart: {
					type: "column"
				},
				title: {
					text: "${StringUtil.wrapString(uiLabelMap.BCRMDOAReport)}",
					x: -20 //center
				},
				xAxis: {
					type: "category",
					labels: {
						rotation: -30,
						style: {
							fontSize: "12px",
							fontFamily: "Verdana, sans-serif"
						}
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
					enabled: true
				},
				tooltip: {
					pointFormat: "{point.y}"
				},
				plotOptions: {
					column: {
						stacking: "normal",
						dataLabels: {
							enabled: true,
							color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || "white",
							style: {
								fontSize: "13px",
								fontWeight: "bold",
								textShadow: "0 0 3px black"
							}
						}
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
					value: OLBIUS.dateToString(LocalData.date.firstDayOfMonth),
					disabled: true
				}],
				before: "thru_date"
			},
			{
				action : "addDateTimeInput",
				params : [{
					id : "thru_date",
					label : "${StringUtil.wrapString(uiLabelMap.olap_thruDate)}",
					value: OLBIUS.dateToString(LocalData.date.currentDate),
					disabled: true
				}],
				after: "from_date"
			},
			{
				action : "addDateTimeInput",
				params : [{
					id : "from_date_1",
					label : "${StringUtil.wrapString(uiLabelMap.olap_fromDate)}",
					value: OLBIUS.dateToString(LocalData.date.firstDayOfMonth),
					hide: true
				}],
				before: "thru_date"
			},
			{
				action : "addDateTimeInput",
				params : [{
					id : "thru_date_1",
					label : "${StringUtil.wrapString(uiLabelMap.olap_thruDate)}",
					value: OLBIUS.dateToString(LocalData.date.currentDate),
					hide: true
				}],
				after: "from_date"
			},
			{
				action : "addDropDownList",
				params : [{
					id : "customTime",
					label : "${StringUtil.wrapString(uiLabelMap.TypeTimeLabel)}",
					data : LocalData.array.timePeriod,
					index: 4
				}],
				event : function(popup) {
					popup.onEvent("customTime", "select", function(event) {
						var args = event.args;
						var item = popup.item("customTime", args.index);
						var filter = item.value;
						popup.clear("from_date");
						popup.clear("thru_date");
						if (filter == "oo") {
							popup.show("from_date_1");
							popup.show("thru_date_1");
							popup.hide("from_date");
							popup.hide("thru_date");
						} else {
							popup.show("from_date");
							popup.show("thru_date");
							popup.hide("from_date_1");
							popup.hide("thru_date_1");
						}
						popup.resize();
					});
				}
			},
			{
				action : "addDropDownList",
				params : [{
					id : "limit",
					label : "${StringUtil.wrapString(uiLabelMap.olap_limit)}",
					data : [{text: "${StringUtil.wrapString(uiLabelMap.olap_unlimit)}", value: "0"}, "2", "5", "10", "20"],
					index: 3
				}]
			},
			{
				action : "addDropDownList",
				params : [{
					id : "sort",
					label : "${StringUtil.wrapString(uiLabelMap.olap_sort)}",
					data : [{text: "${StringUtil.wrapString(uiLabelMap.CRMASC)}", value: false}, {text: "${StringUtil.wrapString(uiLabelMap.CRMDESC)}", value: true}],
					index: 0
				}]
			}
		];

		var salesDOAOLap = OLBIUS.oLapChart("salesDOAChart", config, configPopup, "evaluateProductByCallsChart", true, true,  function(data, chart, datetype, removeSeries, flagFunc, olap){
			var tmp = {
				labels: {
					enabled: true
				},
				categories: data.xAxis
			};

			chart.xAxis[0].update(tmp, false);

			if (removeSeries) {
				while (chart.series.length > 0) {
					chart.series[0].remove(false);
				}
			}
			var color = 2;
			for (var i in data.yAxis) {
				chart.addSeries({
					name: i,
					data: data.yAxis[i],
					color: Highcharts.getOptions().colors[color++],
				}, false);
			}

			chart.redraw();

			if (data.xAxis != undefined && data.xAxis && data.xAxis.length == 0) {
				flagFunc();
			}
		});

		salesDOAOLap.funcUpdate(function (oLap) {
			oLap.update({
				fromDate: oLap.val("from_date_1"),
				thruDate: oLap.val("thru_date_1"),
				limit: oLap.val("limit"),
				sort: oLap.val("sort"),
				customTime: oLap.val("customTime")
			});
		});
		salesDOAOLap.init(function () {
			salesDOAOLap.runAjax();
		});
	});
</script>