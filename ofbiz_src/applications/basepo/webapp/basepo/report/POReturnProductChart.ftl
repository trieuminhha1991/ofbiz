<script type="text/javascript" id="returnProduct">
	$(function () {
		var optionFilterData = [
			{
				text: "${StringUtil.wrapString(uiLabelMap.POProductReturnAtMost)}",
				value: "FILTER_MAX"
			}, 
			{
				text: "${StringUtil.wrapString(uiLabelMap.POProductReturnAtLeast)}",
				value: "FILTER_MIN" 
			}
		];
		var config = {  
				chart: {
					type: "column"
				},
				title: {
					text: "${StringUtil.wrapString(uiLabelMap.POPresentProductReturns)}",
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
						return "<i>" + LocalData.object.mapProducsWithCode[this.x] + "</i>" + "<br/>" + "<b>" + this.y.toLocaleString(locale) + "</b>";
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
				action : "addDropDownList",
				params : [{
					id : "limitId",
					label : "${StringUtil.wrapString(uiLabelMap.POTopProduct)}",
					data : LocalData.array.optionLimit,
					index: 1
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

		var returnProductCharOlap = OLBIUS.oLapChart("returnProduct", config, configPopup, "returnProductCharOlapPO", true, true, OLBIUS.defaultColumnFunc);

		returnProductCharOlap.funcUpdate(function (oLap) {
			oLap.update({
				fromDate: oLap.val("from_date"),
				thruDate: oLap.val("thru_date"),
				statusId: oLap.val("statusId"),
				limitId: oLap.val("limitId"),
				filterTypeId: oLap.val("filterTypeId"),
				facilityId: oLap.val("facilityId"),
				categoryId: oLap.val("categoryId"),
				returnReasonId: oLap.val("returnReasonId"),
				typeChart: "quantity"
			});
		});
		returnProductCharOlap.init(function () {
			returnProductCharOlap.runAjax();
		});
	});
</script>