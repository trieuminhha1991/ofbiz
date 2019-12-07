<script type="text/javascript" id="poChartLine">
	var dataFilterTop = ["5", "10", "15", "20"];
	var dataFilterTop = [
		{'text': '5', 'value': '5'},
		{'text': '10', 'value': '10'},
		{'text': '15', 'value': '15'},
		{'text': '20', 'value': '20'},
	];
	var dataFilterSortQty = [
		{'text': '${StringUtil.wrapString(uiLabelMap.TOPMAX)}', 'value': 'DESC'},
		{'text': '${StringUtil.wrapString(uiLabelMap.TOPMIN)}', 'value': 'ASC'}
	];
	$(function () {
		var config = {
			title: {
				text: "${StringUtil.wrapString(uiLabelMap.POChartPurchaseOrderByTime)}",
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
					return "<b>" + this.x + "</b>" + "<br/>" + "<tspan style='fill:" + this.color + "' x='8' dy='15'></tspan> <b>" + formatnumber(this.y) + "</b>";
				}
			}
		};

		var configPopup = [
				{
					action : "addDropDownList",
					params : [{
						id : "dateType",
						label : "${StringUtil.wrapString(uiLabelMap.CommonPeriod)}",
						data : date_type_source,
						index: 2
					}]
				},
				{
					action : "addDateTimeInput",
					params : [{
						id : "from_date",
						label : "${StringUtil.wrapString(uiLabelMap.olap_fromDate)}",
						value: OLBIUS.dateToString(LocalData.date.lastYear)
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
                	action: 'addDropDownList',
	                params: [{
	                    id: 'filterTop',
	                    label: "${StringUtil.wrapString(uiLabelMap.BSTop)}",
	                    data: dataFilterTop,
	                    index:0,
	                }]
	            },
	            {
	                action: 'addDropDownList',
	                params: [{
	                    id: 'filterSort',
	                    label: "${StringUtil.wrapString(uiLabelMap.olap_filter)}",
	                    data: dataFilterSortQty,
	                    index:0,
	                }]
	            },
				];

		var purcharOrderOLap = OLBIUS.oLapChart("poChartLine", config, configPopup, "purchaseOrderChartLineOlap", true, true, OLBIUS.defaultLineFunc);

		purcharOrderOLap.funcUpdate(function(oLap) {
			oLap.update({
				statusId: oLap.val("statusId"),
				fromDate: oLap.val("from_date"),
				thruDate: oLap.val("thru_date"),
				dateType: oLap.val("dateType"),
				filterTop: oLap.val("filterTop"),
	            filterSort: oLap.val("filterSort"),
				categoryId: oLap.val("categoryId")
			}, oLap.val("dateType"));
		});

		purcharOrderOLap.init(function () {
			purcharOrderOLap.runAjax();
		});

	});
</script>