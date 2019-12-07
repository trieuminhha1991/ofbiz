<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>

<#--===================================Prepare Data=====================================================-->
<script>
	//Prepare for customTimePeriod
	
	listCustomTimePeriodsMonthDataSource = [
		{
			text: "${uiLabelMap.BACCJanuary}",
			value: 1
		},
		{
			text: "${uiLabelMap.BACCFebruary}",
			value: 2
		},
		{
			text: "${uiLabelMap.BACCMarch}",
			value: 3
		},
		{
			text: "${uiLabelMap.BACCApril}",
			value: 4
		},
		{
			text: "${uiLabelMap.BACCMay}",
			value: 5
		},
		{
			text: "${uiLabelMap.BACCJune}",
			value: 6
		},
		{
			text: "${uiLabelMap.BACCJuly}",
			value: 7
		},
		{
			text: "${uiLabelMap.BACCAugust}",
			value: 8
		},
		{
			text: "${uiLabelMap.BACCSeptember}",
			value: 9
		},
		{
			text: "${uiLabelMap.BACCOctober}",
			value: 10
		},
		{
			text: "${uiLabelMap.BACCNovember}",
			value: 11
		},
		{
			text: "${uiLabelMap.BACCDecember}",
			value: 12
		}
	];
	
	listCustomTimePeriodsQuaterDataSource = [
		{
			text: "${uiLabelMap.BACCFirstQuarter}",
			value: 1
		},
		{
			text: "${uiLabelMap.BACCSecondQuarter}",
			value: 2
		},
		{
			text: "${uiLabelMap.BACCThirdQuarter}",
			value: 3
		},
		{
			text: "${uiLabelMap.BACCFourthQuarter}",
			value: 4
		}
	];
	listCustomTimePeriodsYearDataSource = new Array();
	for (var int = LocalData.date.currentYear; int > (LocalData.date.currentYear - 3); int--) {
		listCustomTimePeriodsYearDataSource.push({ text: int, value: int });
	}
	rankData = [
		{
			text: "${uiLabelMap.TOPMAX}",
			value: "TOPMAX"
		},
		{
			text: "${uiLabelMap.TOPMIN}",
			value: "TOPMIN"
		}
	];
</script>
<#--===================================/Prepare Data=====================================================-->

<script type="text/javascript" id="top5SupColChart">
	$( document ).ready(function() {
		//Begin chart config
		var config = {
			chart: {
				type: "column"
			},
			title: {
				text: "${StringUtil.wrapString(uiLabelMap.POTop5Supplier)}",
				x: -20 //center
			},
			xAxis: {
				type: "category",
				labels: {
					rotation: -30,
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
				}
			},
			legend: {
				enabled: true,
				labelFormatter: function () {
            		return "<span title=\"clickToHide\">${StringUtil.wrapString(uiLabelMap.Quantity)}</span>";
	            }
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
		
		//Begin popup config 
		var dateTypeCheck = "DAY";
		var customTimePeriodYear = null;
		var configPopup = [
			{
				action : "addDropDownList",
				params : [{
					id : "dateType",
					label : "${StringUtil.wrapString(uiLabelMap.CommonPeriod)}",
					data : date_type_source,
					index: 0
				}],
				event : function(popup) {
					popup.onEvent("dateType", "select", function(event) {
						var args = event.args;
						var item = popup.item("dateType", args.index);
						var filter = item.value;
						dateTypeCheck = filter;
						if (filter != "DAY") {
							popup.hide("from_date");
							popup.hide("thru_date");
							popup.show("customTimePeriodId");
						}
						if (filter == "DAY") {
							popup.show("from_date");
							popup.show("thru_date");
							popup.hide("customTimePeriodId");
							popup.hide("dateTypePeriodMonth");
							popup.hide("dateTypePeriodQuater");
						} else if (filter == "YEAR") {
							popup.hide("dateTypePeriodMonth");
							popup.hide("dateTypePeriodQuater");
						} else if (filter == "MONTH") {
							popup.show("dateTypePeriodMonth");
							popup.hide("dateTypePeriodQuater");
						} else if (filter == "QUARTER") {
							popup.show("dateTypePeriodQuater");
							popup.hide("dateTypePeriodMonth");
						} else if (filter == "WEEK") {
							popup.show("from_date");
							popup.show("thru_date");
							popup.hide("customTimePeriodId");
							popup.hide("dateTypePeriodMonth");
							popup.hide("dateTypePeriodQuater");
						}
						popup.resize();
					});
				}
			},
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
				action : "addDropDownList",
				params : [{
					id : "topType",
					label : "${StringUtil.wrapString(uiLabelMap.POTopType)}",
					data : [{text:"${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}", value: undefined}].concat(rankData),
					index: 0,
				}]
			},
			{
				action : "addDropDownList",
				params : [{
					id : "customTimePeriodId",
					label : "${StringUtil.wrapString(uiLabelMap.Year)}",
					data : [{text:"${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}", value: undefined}].concat(listCustomTimePeriodsYearDataSource),
					index: 0,
					hide: true
				}],
				event : function(popup) {
					popup.onEvent("customTimePeriodId", "select", function(event) {
						var args = event.args;
						var item = popup.item("customTimePeriodId", args.index);
						var filter = item.value;
						customTimePeriodYear = filter;
						if (dateTypeCheck != "YEAR") {
						}
					});
				}
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
				action : "addDropDownList",
				params : [{
					id : "dateTypePeriodMonth",
					label : "${StringUtil.wrapString(uiLabelMap.KTime)}",
					data : [{text:"${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}", value: undefined}].concat(listCustomTimePeriodsMonthDataSource),
					index: 0,
					hide: true
				}]
			},
			{
				action : "addDropDownList",
				params : [{
					id : "dateTypePeriodQuater",
					label : "${StringUtil.wrapString(uiLabelMap.KTime)}",
					data : [{text:"${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}", value: undefined}].concat(listCustomTimePeriodsQuaterDataSource),
					index: 0,
					hide: true
				}]
			}];

		var columnChart = OLBIUS.oLapChart("top5SupColChart", config, configPopup, "evaluateTop5SupColumnChart", true, true, OLBIUS.defaultColumnFunc);
		columnChart.funcUpdate(function (oLap) {
			if (dateTypeCheck == "DAY") { 
				var thruDate = oLap.val("thru_date");
				var fromDate = oLap.val("from_date");
			} else if (dateTypeCheck == "MONTH") {
				var yearPeriod = oLap.val("customTimePeriodId");
				var monthPeriod = oLap.val("dateTypePeriodMonth");
				if (yearPeriod != null) {
					if (monthPeriod != null) {
						var thruDate = accutils.getThruDate(yearPeriod, monthPeriod, null);
						var fromDate = accutils.getFromDate(yearPeriod, monthPeriod, null);
					} else {
						bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectMonthByFilter)}", [{
							"label" : "${uiLabelMap.POCommonOK}",
							"class" : "btn btn-primary standard-bootbox-bt",
							"icon" : "fa fa-check",
						}]
						);
						return false;
					}
				} else {
					bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectYearByFilter)}", [{
						"label" : "${uiLabelMap.POCommonOK}",
						"class" : "btn btn-primary standard-bootbox-bt",
						"icon" : "fa fa-check",
					}]
					);
					return false;
				}
			} else if (dateTypeCheck == "QUARTER") {
				var yearPeriod = oLap.val("customTimePeriodId");
				var quaterPeriod = oLap.val("dateTypePeriodQuater");
				if (yearPeriod != null) {
					if (quaterPeriod != null) {
						var thruDate = accutils.getThruDate(yearPeriod, null, quaterPeriod);
						var fromDate = accutils.getFromDate(yearPeriod, null, quaterPeriod);
					} else {
						bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectQuaterByFilter)}", [{
							"label" : "${uiLabelMap.POCommonOK}",
							"class" : "btn btn-primary standard-bootbox-bt",
							"icon" : "fa fa-check",
						}]
						);
						return false;
					}
				} else {
					bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectYearByFilter)}", [{
						"label" : "${uiLabelMap.POCommonOK}",
						"class" : "btn btn-primary standard-bootbox-bt",
						"icon" : "fa fa-check",
					}]
					);
					return false;
				}
			} else if (dateTypeCheck == "WEEK") {
				var thruDate = oLap.val("thru_date");
				var fromDate = oLap.val("from_date");
			} else if (dateTypeCheck == "YEAR") {
				var yearPeriod = oLap.val("customTimePeriodId");
				if (yearPeriod != null) {
					var thruDate = accutils.getThruDate(yearPeriod, null, null);
					var fromDate = accutils.getFromDate(yearPeriod, null, null);
				} else {
					bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectYearByFilter)}", [{
						"label" : "${uiLabelMap.POCommonOK}",
						"class" : "btn btn-primary standard-bootbox-bt",
						"icon" : "fa fa-check",
					}]
					);
					return false;
				}
			}
			oLap.resetTitle("${StringUtil.wrapString(uiLabelMap.POTop5Supplier)}");
			oLap.update({
				dateType: oLap.val("dateType"),
				productId: oLap.val("productId"),
				topType: oLap.val("topType"),
				fromDate: fromDate,
				thruDate: thruDate
			}, oLap.val("dateType"));
		});
		columnChart.init(function () {
			columnChart.runAjax();
		});
	});
</script>