<script type="text/javascript" id="returnProduct">
	$(function () {
		var optionFilterData =
		[
			{
				text: "${StringUtil.wrapString(uiLabelMap.LogProductReturnAtMost)}",
				value: "FILTER_MAX"
			}, 
			{
				text: "${StringUtil.wrapString(uiLabelMap.LogProductReturnAtLeast)}",
				value: "FILTER_MIN" 
			}
		];
		var config =
		{
			chart: {
				type: "column"
			},
			title: {
				text: "${StringUtil.wrapString(uiLabelMap.LogPresentProductReturns)}",
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
				pointFormat: "{point.y}"
			},
		 	plotOptions: {
	            series: {
	                maxPointWidth: 30
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
					id : "returnReasonId",
					title1: "${StringUtil.wrapString(uiLabelMap.LogReturnReasonId)}",
					title2: "${StringUtil.wrapString(uiLabelMap.LogRejectReasonReturnProduct)}",
					label : "${StringUtil.wrapString(uiLabelMap.LogRejectReasonReturnProduct)}",
					data : LocalData.array.returnReasons,
					value: []
				}]
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
			},
			{
				action : "addJqxGridMultil",
				params : [{
					id : "enumId",
					title1: "${StringUtil.wrapString(uiLabelMap.LogCodeChannel)}",
					title2: "${StringUtil.wrapString(uiLabelMap.LogPurchaseChannels)}",
					label : "${StringUtil.wrapString(uiLabelMap.LogPurchaseChannels)}",
					data : LocalData.array.enumerations_SALES_METHOD_CHANNEL,
					value: []
				}]
			},
			
			{
				action : "addDropDownList",
				params : [{
					id : "limitId",
					label : "${StringUtil.wrapString(uiLabelMap.POTopProduct)}",
					data : LocalData.array.optionLimit,
					index: 0
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
		var returnProductCharOlap = OLBIUS.oLapChart("returnProduct", config, configPopup, "returnProductCharOlap", true, true, OLBIUS.defaultColumnFunc);

		returnProductCharOlap.funcUpdate(function (oLap) {
			oLap.update({
				fromDate: oLap.val("from_date"),
				thruDate: oLap.val("thru_date"),
				statusId: oLap.val("statusId"),
				limitId: oLap.val("limitId"),
				filterTypeId: oLap.val("filterTypeId"),
				facilityId: oLap.val("facilityId"),
				categoryId: oLap.val("categoryId"),
				enumId: oLap.val("enumId"),
				returnReasonId: oLap.val("returnReasonId"),
				typeChart: "quantity"
			});
		});
		returnProductCharOlap.init(function () {
			returnProductCharOlap.runAjax();
		});
	});
</script>