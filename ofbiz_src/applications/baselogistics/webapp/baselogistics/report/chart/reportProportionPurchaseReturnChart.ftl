<script type="text/javascript" id="PurchaseReturn">
	$(function () {
		var config = {
				chart: {
					plotBackgroundColor: null,
					plotBorderWidth: null,
					plotShadow: false
				},
				title: {
					text: "${StringUtil.wrapString(uiLabelMap.BLChartProportionPurchaseReturn)}" 
				},
				tooltip: {
					formatter: function () { 
						if (this.point.name) {
							if (this.point.name == "PURCHASE_ORDER") {
								return "<b>${StringUtil.wrapString(uiLabelMap.BLReceivePurchase)}</b>: " + (this.point.percentage).toFixed(2) + "%: " + formatnumber(this.point.y); 
							} else {
								return "<b>${StringUtil.wrapString(uiLabelMap.Return)}</b>: " + (this.point.percentage).toFixed(2) + "%: " + formatnumber(this.point.y); 
							}
						}
					},
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
							formatter: function () { 
								if (this.point.name) {
									if (this.point.name == "PURCHASE_ORDER") {
										return "<b>${StringUtil.wrapString(uiLabelMap.BLReceivePurchase)}</b>: " + (this.point.percentage).toFixed(2) + "%"; 
									} else {
										return "<b>${StringUtil.wrapString(uiLabelMap.Return)}</b>: " + (this.point.percentage).toFixed(2) + "%"; 
									}
								}
							},
							style: {
								color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || "black"
							}
						}
					}
				}
		};

		var configPopup = [
			{
				action : "addDateTimeInput",
				params : [{
					id : "from_date",
					label : "${StringUtil.wrapString(uiLabelMap.olap_fromDate)}",
					value: OLBIUS.dateToString(new Date(new Date().getFullYear(), 0, 1)),
				}],
				before : "thru_date"
			},
			{
				action : "addDateTimeInput",
				params : [{
					id : "thru_date",
					label : "${StringUtil.wrapString(uiLabelMap.olap_thruDate)}",
					value: OLBIUS.dateToString(new Date()),
				}],
				after : "from_date"
			}
		];

		var returnOLap = OLBIUS.oLapChart("PurchaseReturn", config, configPopup, "olapChartPieProportionPurchaseReturn", true, true, OLBIUS.defaultPieFunc);

		returnOLap.funcUpdate(function(oLap) {
			var filter = oLap.val("filter");
			oLap.update({
				fromDate: oLap.val("from_date"),
				thruDate: oLap.val("thru_date")
			});
		});

		returnOLap.init(function () {
			returnOLap.runAjax();
		});
	});
</script>
