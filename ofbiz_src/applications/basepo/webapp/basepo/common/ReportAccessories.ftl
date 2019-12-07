<link rel="stylesheet" type="text/css" href="../../poresources/css/reportStyle.css" />

<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/poresources/js/popup.extend.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/initDashboard.js"></script>
<script type="text/javascript" src="/poresources/js/popup_extend_grid.js"></script>

<@jqOlbCoreLib />

<script>
	if (typeof (LocalData) == "undefined") {
		var LocalData = new Object();
		
		LocalData.array = Object.freeze({
			categories : Object.freeze(${StringUtil.wrapString(LocalData.categories!"[]")}),
			facilities : Object.freeze(${StringUtil.wrapString(LocalData.facilities!"[]")}),
			returnReasons : Object.freeze(${StringUtil.wrapString(LocalData.returnReasons!"[]")}),
			statusItems_ORDER_ITEM : Object.freeze(${StringUtil.wrapString(LocalData.statusItems_ORDER_ITEM!"[]")}),
			suppliers : Object.freeze(${StringUtil.wrapString(LocalData.suppliers!"[]")}),
			
			optionLimit: Object.freeze([
				{ text:"${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}", value: "" },
				{ text: "5", value: 5 }, { text: "10", value: 10 }, { text: "15", value: 15 }, { text: "20", value: 20 }, { text: "25", value: 25 }
			]),
		});
		
		LocalData.object = Object.freeze({
			mapSuppliers : Object.freeze(${StringUtil.wrapString(LocalData.mapSuppliers!"{}")}),
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