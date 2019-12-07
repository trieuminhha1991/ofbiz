<script src="/crmresources/js/miscUtil.js"></script>
<script src="/crmresources/js/Underscore1.8.3.js"></script>
<script src="/crmresources/js/progressing.js"></script>
<script src="/crmresources/js/bootbox.min.js"></script>
<script src="/crmresources/js/notify.js"></script>
<script src="/crmresources/js/DataAccess.js"></script>

<script src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>

<script>

	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var multiLang = {
			Employee: "${StringUtil.wrapString(uiLabelMap.Employee)}",
	};
	
	var getLocalization = function () {
        var localizationobj = {};
        localizationobj.pagergotopagestring = "${StringUtil.wrapString(uiLabelMap.wgpagergotopagestring)}:";
        localizationobj.pagershowrowsstring = "${StringUtil.wrapString(uiLabelMap.wgpagershowrowsstring)}:";
        localizationobj.pagerrangestring = " ${StringUtil.wrapString(uiLabelMap.wgpagerrangestring)} ";
        localizationobj.pagernextbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagernextbuttonstring)}";
        localizationobj.pagerpreviousbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagerpreviousbuttonstring)}";
        localizationobj.sortascendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortascendingstring)}";
        localizationobj.sortdescendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortdescendingstring)}";
        localizationobj.sortremovestring = "${StringUtil.wrapString(uiLabelMap.wgsortremovestring)}";
        localizationobj.emptydatastring = "${StringUtil.wrapString(uiLabelMap.wgemptydatastring)}";
        localizationobj.filterselectstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}";
        localizationobj.filterselectallstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}";
        localizationobj.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
        localizationobj.groupsheaderstring = "${StringUtil.wrapString(uiLabelMap.wgdragDropToGroupColumn)}";
        localizationobj.todaystring = "${StringUtil.wrapString(uiLabelMap.wgtodaystring)}";
        localizationobj.clearstring = "${StringUtil.wrapString(uiLabelMap.wgclearstring)}";
        return localizationobj;
	};
	function fixSelectAll(dataList) {
    	var sourceST = {
		        localdata: dataList,
		        datatype: "array"
	    };
		var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
        var uniqueRecords2 = filterBoxAdapter2.records;
		return uniqueRecords2;
	}
</script>