<script type="text/javascript" id="facilityPieIIOLap">
	$(function () {
		var config = {
				chart: {
					plotBackgroundColor: null,
					plotBorderWidth: null,
					plotShadow: false
				},
				title: {
					text: "${StringUtil.wrapString(uiLabelMap.LOGfacilityChartPie)}" 
				},
				tooltip: {
					pointFormat: "<b>{point.percentage:.1f}%: {point.y}</b>"
				},
				series: [{
					type: "pie"
				}],
				plotOptions: {
					pie: {
						allowPointSelect: true,
						cursor: "pointer",
						dataLabels: {
							enabled: true,
							format: "<b>{point.name}</b>: {point.percentage:.1f} %",
							style: {
								color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || "black"
							}
						}
					}
				}
		};

		var _olapTypeSource = [{text: "${StringUtil.wrapString(uiLabelMap.facility_receive)}", value: "RECEIVE"}, {text: "${StringUtil.wrapString(uiLabelMap.facility_export)}", value: "EXPORT"},
			{text:"${StringUtil.wrapString(uiLabelMap.facility_inventory)}", value: "INVENTORY"}];

		var configPopup = [
			{
				action : "addDropDownList",
				params : [{
					id : "olapType",
					label : "${StringUtil.wrapString(uiLabelMap.facility_olapType)}",
					data : _olapTypeSource,
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
				before : "thru_date"
			},
			{
				action : "addDateTimeInput",
				params : [{
					id : "thru_date",
					label : "${StringUtil.wrapString(uiLabelMap.olap_thruDate)}",
					value: OLBIUS.dateToString(LocalData.date.currentDate)
				}],
				after : "from_date"
			}
		];

		var facilityOLap = OLBIUS.oLapChart("facilityPieIIOLap", config, configPopup, "facilityReportPieOlap", true, true, OLBIUS.defaultPieFunc);

		facilityOLap.funcUpdate(function(oLap) {
			var filter = oLap.val("filter");
			oLap.update({
				olapType: oLap.val("olapType"),
				fromDate: oLap.val("from_date"),
				thruDate: oLap.val("thru_date")
			});
		});

		facilityOLap.init(function () {
			facilityOLap.runAjax();
		});
	});
</script>
