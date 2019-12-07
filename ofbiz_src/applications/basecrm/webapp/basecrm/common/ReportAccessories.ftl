
<script>
	if (typeof (LocalData) == "undefined") {
		var LocalData = new Object();
		
		LocalData.array = Object.freeze({
			marketingCampaigns : Object.freeze(${StringUtil.wrapString(LocalData.marketingCampaigns!"[]")}),
			employeeCallCenter : Object.freeze(${StringUtil.wrapString(LocalData.employeeCallCenter!"[]")}),
			
			optionLimit: Object.freeze([
				{ text:"${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}", value: "" },
				{ text: "5", value: 5 }, { text: "10", value: 10 }, { text: "15", value: 15 }, { text: "20", value: 20 }, { text: "25", value: 25 }
			]),
			
			timePeriod: Object.freeze([
				{ text: '${StringUtil.wrapString(uiLabelMap.DayLabel)}', value: 'dd' },
				{ text: '${StringUtil.wrapString(uiLabelMap.WeekLabel)}', value: 'ww' },
				{ text: '${StringUtil.wrapString(uiLabelMap.MonthLabel)}', value: 'mm' },
				{ text: '${StringUtil.wrapString(uiLabelMap.QuarterLabel)}', value: 'qq' },
				{ text: '${StringUtil.wrapString(uiLabelMap.YearLabel)}', value: 'yy' },
				{ text: '${StringUtil.wrapString(uiLabelMap.OtherLabel)}', value: 'oo' }
			]),
		});
		
		LocalData.object = Object.freeze({
			mapProducsWithCode : Object.freeze(${StringUtil.wrapString(LocalData.mapProducsWithCode!"{}")}),
			
		});
		
		LocalData.date = Object.freeze({
			currentDate: Object.freeze(new Date()),
			currentYear: Object.freeze(new Date().getFullYear()),
			firstDayOfMonth: Object.freeze(new Date(new Date().getFullYear(), new Date().getMonth(), 1)),
			lastYear: Object.freeze(new Date(new Date().setFullYear(new Date().getFullYear() - 1))),
		});
		
		LocalData.config = Object.freeze({
			jqxwindow: Object.freeze({
				theme: theme,
				width: 700,
				height: 450,
				resizable: true,
				isModal: true,
				autoOpen: false,
				modalOpacity: 0.7
			}),
			jqxgrid: Object.freeze({
				theme: theme,
				localization: getLocalization(),
				filterable: true,
				showfilterrow: true,
				pageable: true,
				sortable: true,
				pagesize: 10,
				width: "100%",
				height: 340
			}),
		});
	}
</script>