<script type="text/javascript" id="communicationCampaignOlap">
	$(function () {
		var config = {
			chart: {
				plotBackgroundColor: null,
				plotBorderWidth: null,
				plotShadow: false
			},
			title: {
				text: "${StringUtil.wrapString(uiLabelMap.CallResultCampaignReport)}"
			},
			tooltip: {
				pointFormat: "{series.name}: <b>{point.percentage:.1f}%</b>"
			},
			series: [{
				type: "pie"
			}],
			plotOptions: {
				pie: {
					allowPointSelect: true,
					cursor: "pointer",
					dataLabels: {
						enabled: false,
						format: "<b>{point.name}</b>: {point.percentage:.1f} %",
						style: {
							color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || "black"
						}
					},
					showInLegend: true
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
				before: "thru_date_1"
			},
			{
				action : "addDateTimeInput",
				params : [{
					id : "thru_date_1",
					label : "${StringUtil.wrapString(uiLabelMap.olap_thruDate)}",
					value: OLBIUS.dateToString(LocalData.date.currentDate),
					hide: true
				}],
				after: "from_date_1"
			},
			{
				action : "addDropDownList",
				params : [{
					id : "customTime",
					label : "${StringUtil.wrapString(uiLabelMap.TypeTimeLabel)}",
					data : LocalData.array.timePeriod,
					index: 2
				}],
				event : function(popup) {
					popup.onEvent("customTime", "select", function(event) {
						var args = event.args;
						var item = popup.item("customTime", args.index);
						var filter = item.value;
						popup.clear("from_date");
						popup.clear("thru_date");
						if(filter == "oo") {
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
					id : "marketingCampaignId",
					label : "${StringUtil.wrapString(uiLabelMap.Campaign)}",
					data : LocalData.array.marketingCampaigns,
					index: 0
				}]
			}
		];

		var communicationCampaignChart = OLBIUS.oLapChart("communicationCampaignOlap", config, configPopup, "getCommunicationCampaignChart", true, true, OLBIUS.defaultPieFunc, 0.62);

		communicationCampaignChart.funcUpdate(function(oLap) {
			oLap.update({
				fromDate: oLap.val("from_date_1"),
				thruDate: oLap.val("thru_date_1"),
				marketingCampaignId: oLap.val("marketingCampaignId"),
				isChart: true,
				customTime: oLap.val("customTime")
			});
		});

		communicationCampaignChart.init(function () {
			communicationCampaignChart.runAjax();
		});
	});
</script>