<@jqGridMinimumLib/>
<#include "component://widget/templates/jqwLocalization.ftl" />
<!--<script type="text/javascript" src="/highcharts/assets/js/highcharts.js"></script>
<script type="text/javascript" src="/highcharts/assets/js/highcharts-3d.js"></script>
<script type="text/javascript" src="/highcharts/assets/js/highcharts-more.js"></script>
<script type="text/javascript" src="/highcharts/assets/js/exporting.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/images/bi-x/olbius.config.js"></script>
<script type="text/javascript" src="/images/bi-x/olbius.popup.js"></script>
<script type="text/javascript" src="/images/bi-x/olbius.new.js"></script>
<script type="text/javascript" src="/images/bi-x/olbius.grid.js"></script>
-->
<script type="text/javascript">
	//data for facility
	var facilityData = [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}];
	<#if facilityList?exists>
		<#list facilityList as itemFa >
			facilityData.push({ 'value': '${itemFa.facilityId?if_exists}', 'text': '${StringUtil.wrapString(itemFa.facilityName)?if_exists}'});
		</#list>
	</#if>
	//data for product store
	var productStoreData = [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': 'all'}];
	<#if productStoreList?exists>
		<#list productStoreList as itemPs >
			productStoreData.push({ 'value': '${itemPs.productStoreId?if_exists}', 'text': '${StringUtil.wrapString(itemPs.storeName)?if_exists}'});
		</#list>
	</#if>
	//data for product category
	var cateData = [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}];
	<#if listProductCategory?exists>
		<#list listProductCategory as itemCate >
			cateData.push({ 'value': '${itemCate.productCategoryId?if_exists}', 'text': '${(itemCate.categoryName)?if_exists}'});
		</#list>
	</#if>
	//data for product
	var productData = [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}];
	<#if productList?exists>
		<#list productList as itemPro >
			productData.push({ 'value': '${itemPro.productId?if_exists}', 'text': '${(itemPro.productName)?if_exists}'});
		</#list>
	</#if>
	//data for customer
	var customerData = [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}];
	customerData.push({ 'value': '_NA_', 'text': '_NA_'});
	<#if customerList?exists>
		<#list customerList as itemCus >
			customerData.push({ 'value': '${itemCus.partyId?if_exists}', 'text': '${(itemCus.lastName)?if_exists} ${(itemCus.firstName)?if_exists}'});
		</#list>
	</#if>
	//data for employee
	var employeeData = [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}];
	<#if employeeList?exists>
		<#list employeeList as itemEm >
			employeeData.push({ 'value': '${itemEm.partyId?if_exists}', 'text': '${(itemEm.lastName)?if_exists} ${(itemEm.firstName)?if_exists}'});
		</#list>
	</#if>
	//data for date type
	var date_type_source = [{text: '${StringUtil.wrapString(uiLabelMap.olap_day)}', value: 'DAY'}, {text: '${StringUtil.wrapString(uiLabelMap.olap_week)}', value: 'WEEK'},
            {text:'${StringUtil.wrapString(uiLabelMap.olap_month)}', value: 'MONTH'}, {text: '${StringUtil.wrapString(uiLabelMap.olap_quarter)}', value: 'QUARTER'},
            {text: '${StringUtil.wrapString(uiLabelMap.olap_year)}', value: 'YEAR'}];
	var cur_date = new Date();
	var past_date = new Date(cur_date);
	past_date.setMonth(past_date.getMonth()-11);
	past_date.setDate(1);
	$(function(){
        Highcharts.setOptions({lang: {loading: '${StringUtil.wrapString(uiLabelMap.BPOSDataNotFound)}'}});
		OLBIUS.setTickInterval(parseInt('${tickInterval}'));
        OLBIUS.setDateTimeFormat('${StringUtil.wrapString(uiLabelMap.dateTimeFormat)}');
        OLBIUS.setDateTimeFullFormat('${StringUtil.wrapString(uiLabelMap.dateTimeFullFormat)}');
        OLBIUS.setMonthFormat('${StringUtil.wrapString(uiLabelMap.monthFormat)}');
        OLBIUS.setCompany('${company}');
        OLBIUS.setConfiguration('${StringUtil.wrapString(uiLabelMap.olap_configuration)}');
        OLBIUS.setOKText('${StringUtil.wrapString(uiLabelMap.olap_ok)}');
        OLBIUS.setTheme('olbius');
        OLBIUS.setOkGridLable('${StringUtil.wrapString(uiLabelMap.olap_ok_grid)}')
        OLBIUS.setCancelGridLable('${StringUtil.wrapString(uiLabelMap.olap_cancel_grid)}');
        OLBIUS.setWarnGridLable('${StringUtil.wrapString(uiLabelMap.ReportWarningRunOlapServices)}');
        OLBIUS.setLastupdatedGridLable('${StringUtil.wrapString(uiLabelMap.olap_lastupdated_grid)}');
        OLBIUS.setWeekLable('${StringUtil.wrapString(uiLabelMap.ReportWeekLabel)}');
        OLBIUS.setQuarterLable('${StringUtil.wrapString(uiLabelMap.ReportQuarterLabel)}');
	});
	
	function getSupplierName(partyId){
		var partyName = '';
		for(var i = 0; i < supplierData.length; i++){
			if (supplierData[i].value == partyId){
				partyName = supplierData[i].text;
			} 
		}
		return partyName;
	}
	
	function getCustomerName(partyId){
		var partyName = '';
		for(var i = 0; i < customerData.length; i++){
			if (customerData[i].value == partyId){
				partyName = customerData[i].text;
			} 
		}
		return partyName;
	}
	
	var customDate = [
		{'text': '${StringUtil.wrapString(uiLabelMap.DayLabel)}', 'value': 'dd'},
		{'text': '${StringUtil.wrapString(uiLabelMap.WeekLabel)}', 'value': 'ww'},
		{'text': '${StringUtil.wrapString(uiLabelMap.MonthLabel)}', 'value': 'mm'},
		{'text': '${StringUtil.wrapString(uiLabelMap.QuarterLabel)}', 'value': 'qq'},
		{'text': '${StringUtil.wrapString(uiLabelMap.YearLabel)}', 'value': 'yy'},
		{'text': '${StringUtil.wrapString(uiLabelMap.OtherLabel)}', 'value': 'oo'}
	];
</script>