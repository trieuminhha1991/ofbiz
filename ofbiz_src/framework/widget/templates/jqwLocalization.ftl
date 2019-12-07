<script>
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
         localizationobj.notificationfiltershortkey = "${StringUtil.wrapString(uiLabelMap.filterDropDownGridDescription)}"
		 localizationobj.filterstringcomparisonoperators = ['${StringUtil.wrapString(uiLabelMap.Empty)}', 
		 													'${StringUtil.wrapString(uiLabelMap.NotEmpty)}', 
	 														'${StringUtil.wrapString(uiLabelMap.Contains)}', 
	 														'${StringUtil.wrapString(uiLabelMap.ContainsMatchCase)}',
	        												'${StringUtil.wrapString(uiLabelMap.DoesNotContain)}', 
        													'${StringUtil.wrapString(uiLabelMap.DoesNotContainMatchCase)}', 
        													'${StringUtil.wrapString(uiLabelMap.StartWith)}', 
        													'${StringUtil.wrapString(uiLabelMap.StartWithMatchCase)}',
		        'ends with', 'ends with(match case)', 'equal', 'equal(match case)', 'null', 'not null'],
		 localizationobj.filternumericcomparisonoperators = ['${StringUtil.wrapString(uiLabelMap.Equal)}', 
		 													'${StringUtil.wrapString(uiLabelMap.NotEqual)}', 
 															'${StringUtil.wrapString(uiLabelMap.LessThan)}', 
 															'${StringUtil.wrapString(uiLabelMap.LessThanOrEqual)}', 
 															'${StringUtil.wrapString(uiLabelMap.GreaterThan)}', 
 															'${StringUtil.wrapString(uiLabelMap.GreaterThanOrEqual)}', 
 															'${StringUtil.wrapString(uiLabelMap.Null)}', 
 															'${StringUtil.wrapString(uiLabelMap.NotNull)}'],
		 localizationobj.filterdatecomparisonoperators = ['${StringUtil.wrapString(uiLabelMap.Equal)}', 
	 													'${StringUtil.wrapString(uiLabelMap.NotEqual)}', 
	 													'${StringUtil.wrapString(uiLabelMap.LessThan)}', 
	 													'${StringUtil.wrapString(uiLabelMap.LessThanOrEqual)}', 
	 													'${StringUtil.wrapString(uiLabelMap.GreaterThan)}', 
	 													'${StringUtil.wrapString(uiLabelMap.GreaterThanOrEqual)}', 
	 													'${StringUtil.wrapString(uiLabelMap.Null)}', 
	 													'${StringUtil.wrapString(uiLabelMap.NotNull)}'],
		 localizationobj.filterbooleancomparisonoperators = ['${StringUtil.wrapString(uiLabelMap.Equal)}', '${StringUtil.wrapString(uiLabelMap.NotEqual)}'],
         localizationobj.firstDay = 1;
         localizationobj.percentsymbol = "%";
         localizationobj.currencysymbol = "đ";
         localizationobj.decimalseparator = ",";
         localizationobj.thousandsseparator = ".";
         <#if defaultOrganizationPartyCurrencyUomId?has_content>
            <#if defaultOrganizationPartyCurrencyUomId == "USD">
                localizationobj.currencysymbol = "$";
                localizationobj.decimalseparator = ".";
                localizationobj.thousandsseparator = ",";
            <#elseif defaultOrganizationPartyCurrencyUomId == "EUR">
                localizationobj.currencysymbol = "€";
                localizationobj.decimalseparator = ".";
                localizationobj.thousandsseparator = ",";
            </#if>
         </#if>
         localizationobj.currencysymbolposition = "after";
         
         var days = {
             // full day names
             names: ["${StringUtil.wrapString(uiLabelMap.wgmonday)}", "${StringUtil.wrapString(uiLabelMap.wgtuesday)}", "${StringUtil.wrapString(uiLabelMap.wgwednesday)}", "${StringUtil.wrapString(uiLabelMap.wgthursday)}", "${StringUtil.wrapString(uiLabelMap.wgfriday)}", "${StringUtil.wrapString(uiLabelMap.wgsaturday)}", "${StringUtil.wrapString(uiLabelMap.wgsunday)}"],
             // abbreviated day names
             namesAbbr: ["${StringUtil.wrapString(uiLabelMap.wgamonday)}", "${StringUtil.wrapString(uiLabelMap.wgatuesday)}", "${StringUtil.wrapString(uiLabelMap.wgawednesday)}", "${StringUtil.wrapString(uiLabelMap.wgathursday)}", "${StringUtil.wrapString(uiLabelMap.wgafriday)}", "${StringUtil.wrapString(uiLabelMap.wgasaturday)}", "${StringUtil.wrapString(uiLabelMap.wgasunday)}"],
             // shortest day names
             namesShort: ["${StringUtil.wrapString(uiLabelMap.wgsmonday)}", "${StringUtil.wrapString(uiLabelMap.wgstuesday)}", "${StringUtil.wrapString(uiLabelMap.wgswednesday)}", "${StringUtil.wrapString(uiLabelMap.wgsthursday)}", "${StringUtil.wrapString(uiLabelMap.wgsfriday)}", "${StringUtil.wrapString(uiLabelMap.wgssaturday)}", "${StringUtil.wrapString(uiLabelMap.wgssunday)}"],
         };
         localizationobj.days = days;
         var months = {
             // full month names (13 months for lunar calendards -- 13th month should be "" if not lunar)
             names: ["${StringUtil.wrapString(uiLabelMap.wgjanuary)}", "${StringUtil.wrapString(uiLabelMap.wgfebruary)}", "${StringUtil.wrapString(uiLabelMap.wgmarch)}", "${StringUtil.wrapString(uiLabelMap.wgapril)}", "${StringUtil.wrapString(uiLabelMap.wgmay)}", "${StringUtil.wrapString(uiLabelMap.wgjune)}", "${StringUtil.wrapString(uiLabelMap.wgjuly)}", "${StringUtil.wrapString(uiLabelMap.wgaugust)}", "${StringUtil.wrapString(uiLabelMap.wgseptember)}", "${StringUtil.wrapString(uiLabelMap.wgoctober)}", "${StringUtil.wrapString(uiLabelMap.wgnovember)}", "${StringUtil.wrapString(uiLabelMap.wgdecember)}", ""],
             // abbreviated month names
             namesAbbr: ["${StringUtil.wrapString(uiLabelMap.wgajanuary)}", "${StringUtil.wrapString(uiLabelMap.wgafebruary)}", "${StringUtil.wrapString(uiLabelMap.wgamarch)}", "${StringUtil.wrapString(uiLabelMap.wgaapril)}", "${StringUtil.wrapString(uiLabelMap.wgamay)}", "${StringUtil.wrapString(uiLabelMap.wgajune)}", "${StringUtil.wrapString(uiLabelMap.wgajuly)}", "${StringUtil.wrapString(uiLabelMap.wgaaugust)}", "${StringUtil.wrapString(uiLabelMap.wgaseptember)}", "${StringUtil.wrapString(uiLabelMap.wgaoctober)}", "${StringUtil.wrapString(uiLabelMap.wganovember)}", "${StringUtil.wrapString(uiLabelMap.wgadecember)}", ""],
         };
         var patterns = {
            d: "dd/MM/yyyy",
            D: "dd MMMM yyyy",
            f: "dd MMMM yyyy h:mm tt",
            F: "dd MMMM yyyy h:mm:ss tt",
            M: "dd MMMM",
            Y: "MMMM yyyy"
         };
         localizationobj.patterns = patterns;
         localizationobj.months = months;
         localizationobj.todaystring = "${StringUtil.wrapString(uiLabelMap.wgtodaystring)}";
         localizationobj.clearstring = "${StringUtil.wrapString(uiLabelMap.wgclearstring)}";
         return localizationobj;
    };
</script>