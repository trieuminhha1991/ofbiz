<script type="text/javascript">
	var sourceStatus =
	[
		{ text: "${StringUtil.wrapString(uiLabelMap.BCRMContactCall)}", value: "C" },
		{ text: "${StringUtil.wrapString(uiLabelMap.BCRMUncontactCall)}", value: "U" }
	];
</script>
<script type="text/javascript" id="TCColumnChart">
$(function(){
	var config = {
		chart: {
			type: "column"
		},
		title: {
			text: "${StringUtil.wrapString(uiLabelMap.BCRMTCColumn)}",
			x: -20 //center
		},
		xAxis: {
			type: "category",
			labels: {
				rotation: 0,
				style: {
					fontSize: "13px",
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
				color: "#000000"
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
			pointFormat: "<span style=\"color:{series.color}\">{series.name}</span>: <b>{point.y}</b><br/>",
		},
		plotOptions: {
			column: {
				stacking: "normal",
				dataLabels: {
					rotation: 0,
					enabled: true,
					y: 0,
					color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || "white",
					style: {
						fontSize: "14px",
						textShadow: "0 0 3px white"
					}
				}
			}
		},
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
				id : "statusCall",
				label : "${StringUtil.wrapString(uiLabelMap.BSStatus)}",
				data : sourceStatus,
				index: 0
			}]
		}
	];

	var columnChart = OLBIUS.oLapChart("TCColumnChart", config, configPopup, "evaluateTopCaller", true, true, function(data, chart, datetype, removeSeries, flagFunc, olap){
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
		var color = 7;
		for (var i in data.yAxis) {
			chart.addSeries({
				name: i,
				data: data.yAxis[i],
				color: Highcharts.getOptions().colors[color++]
			}, false);
		}

		chart.redraw();

		if (data.xAxis != undefined && data.xAxis && data.xAxis.length == 0) {
			flagFunc();
		}
	});

	columnChart.funcUpdate(function (oLap) {
		oLap.update({
			fromDate: oLap.val("from_date_1"),
			thruDate: oLap.val("thru_date_1"),
			statusCall: oLap.val("statusCall"),
			customTime: oLap.val("customTime")
		});
	});

	columnChart.init(function () {
		columnChart.runAjax();
	});
});
</script>