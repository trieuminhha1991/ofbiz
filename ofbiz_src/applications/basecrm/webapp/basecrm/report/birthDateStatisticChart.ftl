<script type="text/javascript" id="birthDateOlap">
	$(function () {
		var regions = [];
		var resources = [];
		var getRegions = function() {
			if (!regions.length) {
				$.ajax({
					url: "autoCompleteGeoAjax?geoTypeId=SUBREGION&geoId=VNM",
					async: false,
					success: function(res) {
						if (res.listGeo) {
							regions = [];
							for(var x in res.listGeo) {
								regions.push({
									value: res.listGeo[x].geoId,
									text: res.listGeo[x].geoName
								});
							}
						}
					}
				});
			}
			return regions;
		};
		var getResource = function() {
			if (!resources.length) {
				$.ajax({
					url: "getDataResources",
					async: false,
					success: function(res) {
						if (res.result) {
							resources = [];
							for (var x in res.result) {
								resources.push({
									value: res.result[x].dataSourceId,
									text: res.result[x].dataSourceId
								});
							}
						}
					}
				});
			}
			return resources;
		};
		var config = {
			chart: {
				plotBackgroundColor: null,
				plotBorderWidth: null,
				plotShadow: false
			},
			title: {
				text: "${StringUtil.wrapString(uiLabelMap.BirthDateStatistic)}"
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
			}
		};

		var configPopup =
		[
			{
				action : "addDropDownList",
				params : [{
					id : "dataResourceId",
					label : "${StringUtil.wrapString(uiLabelMap.ChooseResource)}",
					data : [{"text": "${StringUtil.wrapString(uiLabelMap.olap_all)}", "value": ""}].concat(getResource()),
					index: 0
				}]
			},
			{
				action : "addDropDownList",
				params : [{
					id : "geoId",
					label : "${StringUtil.wrapString(uiLabelMap.ChooseRegion)}",
					data : [{"text": "${StringUtil.wrapString(uiLabelMap.olap_all)}", "value": ""}].concat(getRegions()),
					index: 0
				}]
			}
		];
		var DOBReport = OLBIUS.oLapChart("birthDateOlap", config, configPopup, "getDataResourceBirthDate", true, true, OLBIUS.defaultPieFunc, 0.75);

		DOBReport.funcUpdate(function(oLap) {
			oLap.update({
				dataSourceId: oLap.val("dataResourceId"),
				geoId: oLap.val("geoId")
			});
		});

		DOBReport.init(function () {
			DOBReport.runAjax();
		});
	});
</script>