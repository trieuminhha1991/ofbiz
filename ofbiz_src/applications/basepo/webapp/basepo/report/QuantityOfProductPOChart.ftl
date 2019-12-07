<script type="text/javascript" id="pucharOrderChart">
	$(function () {
		var optionFilterData =
		[{
			text: "${StringUtil.wrapString(uiLabelMap.POProductsAreBoughtAtMost)}",
			value: "FILTER_MAX"
		},
		{
			text: "${StringUtil.wrapString(uiLabelMap.POProductArePurchasedAtLeast)}",
			value: "FILTER_MIN" 
		}];
		var config = {
				chart: {
					type: "column"
				},
				title: {
					text: "${StringUtil.wrapString(uiLabelMap.POChartPurchaseOrder)}",
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
					enabled: false
				},
				tooltip: {
					formatter: function () {
						return "<b>" + formatnumber(this.y) + "</b>";
					}
				},
				plotOptions: {
		            series: {
		                maxPointWidth: 30
		            }
		        },
		};

		var configPopup = [
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
					id : "categoryId",
					title1: "${StringUtil.wrapString(uiLabelMap.POCategoryId)}",
					title2: "${StringUtil.wrapString(uiLabelMap.POCategoryName)}",
					label : "${StringUtil.wrapString(uiLabelMap.DmsProductCatalogs)}",
					data : LocalData.array.categories,
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
					id : "filterTypeId",
					label : "${StringUtil.wrapString(uiLabelMap.POTypeFilter)}",
					data : optionFilterData,
					index: 1
				}]
			}];

		var purchaseOrderChartOLap = OLBIUS.oLapChart("pucharOrderChart", config, configPopup, "purchaseOrderChart", true, true, OLBIUS.defaultColumnFunc);

		purchaseOrderChartOLap.funcUpdate(function (oLap) {
			oLap.update({
				fromDate: oLap.val("from_date"),
				thruDate: oLap.val("thru_date"),
				categoryId: oLap.val("categoryId"),
				limitId: oLap.val("limitId"),
				filterTypeId: oLap.val("filterTypeId"),
				typeChart: "quantity"
			});
		});
		purchaseOrderChartOLap.init(function () {
			purchaseOrderChartOLap.runAjax();
		});
	});
</script>