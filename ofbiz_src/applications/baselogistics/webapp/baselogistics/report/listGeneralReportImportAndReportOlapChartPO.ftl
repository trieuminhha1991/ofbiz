<script type="text/javascript" id="facilityOLap">
	$(function () {
		var config = {
			title: {
				text: "${StringUtil.wrapString(uiLabelMap.facility_title)}",
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
			}
		};

		var configPopup = [
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
		},
		{
			action : "addDropDownList",
			params : [{
				id : "facilityId",
				label : "${StringUtil.wrapString(uiLabelMap.LogWarehouse)}",
				data : LocalData.array.facilities,
				index: 0
			}]
		}];

		var facilityOLap = OLBIUS.oLapChart("facilityOLap", config, configPopup, "generalImportReciveChart", true, true, OLBIUS.defaultLineFunc);

		facilityOLap.funcUpdate(function(oLap) {
			var title = "${StringUtil.wrapString(uiLabelMap.ChartLineReceiveExportInventory)}";
			var filter = oLap.val("productId");
			oLap.update({
				dateType: oLap.val("dateType"),
				facilityId: oLap.val("facilityId"),
				fromDate: oLap.val("from_date"),
				thruDate: oLap.val("thru_date")
			}, oLap.val("dateType"));
		});

		facilityOLap.init(function () {
			facilityOLap.runAjax();
		});
	});
</script>