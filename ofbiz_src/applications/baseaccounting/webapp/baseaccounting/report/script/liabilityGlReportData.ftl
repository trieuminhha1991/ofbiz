<script type="text/javascript">
	//data for facility
	var facilityData = [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': undefined}];
	<#if facilityList?exists>
		<#list facilityList as itemFa >
			facilityData.push({ 'value': '${itemFa.facilityId?if_exists}', 'text': '${StringUtil.wrapString(itemFa.facilityName)?if_exists}'});
		</#list>
	</#if>
	//data for product store
	var productStoreData = [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': undefined}];
	<#if productStoreList?exists>
		<#list productStoreList as itemPs >
			productStoreData.push({ 'value': '${itemPs.productStoreId?if_exists}', 'text': '${StringUtil.wrapString(itemPs.storeName)?if_exists}'});
		</#list>
	</#if>
	//data for product category
	var cateData = [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': undefined}];
	<#if listProductCategory?exists>
		<#list listProductCategory as itemCate >
			cateData.push({ 'value': '${itemCate.productCategoryId?if_exists}', 'text': '${(itemCate.categoryName)?if_exists}'});
		</#list>
	</#if>
	//data for product
	var productData = [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': undefined}];
	<#if productList?exists>
		<#list productList as itemPro >
			productData.push({ 'value': '${itemPro.productId?if_exists}', 'text': '${(itemPro.internalName)?if_exists}'});
		</#list>
	</#if>
	//data for customer
	var customerData = [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': undefined}];
	customerData.push({ 'value': '_NA_', 'text': '_NA_'});
	<#if customerList?exists>
		<#list customerList as itemCus >
			customerData.push({ 'value': '${itemCus.partyId?if_exists}', 'text': '${(itemCus.lastName)?if_exists} ${(itemCus.firstName)?if_exists}'});
		</#list>
	</#if>
	//data for supplier
	var supplierData = [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': undefined}];
	<#if supplierList?exists>
		<#list supplierList as itemSup >
			supplierData.push({ 'value': '${itemSup.partyGroupId?if_exists}', 'text': '${(itemSup.groupName)?if_exists}'});
		</#list>
	</#if>
	//data for employee
	var employeeData = [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': undefined}];
	<#if employeeList?exists>
		<#list employeeList as itemEm >
			employeeData.push({ 'value': '${itemEm.partyId?if_exists}', 'text': '${(itemEm.lastName)?if_exists} ${(itemEm.firstName)?if_exists}'});
		</#list>
	</#if>
	//data for gl account 
	var glAccountData = [{text: '[131]-Phải thu của khách hàng', value: '131'}, {text: '[138]-Phải thu khác', value: '138'},
            {text:'[1381]-Tài sản thiếu chờ xử lý', value: '1381'}, {text: '[1388]-Phải thu khác', value: '1388'},
            {text: '[141]-Tạm ứng', value: '141'}, {text: '[331]-Phải trả cho người bán', value: '331'}, {text: '[3411]-Các khoản đi vay', value: '3411'},
            {text: '[335]-Chi phí phải trả', value: '335'}, {text: '[3388]-Phải trả, phải nộp khác', value: '3388'}];
	//data for date type
	var date_type_source = [{text: '${StringUtil.wrapString(uiLabelMap.olap_day)}', value: 'DAY'}, {text: '${StringUtil.wrapString(uiLabelMap.olap_week)}', value: 'WEEK'},
            {text:'${StringUtil.wrapString(uiLabelMap.olap_month)}', value: 'MONTH'}, {text: '${StringUtil.wrapString(uiLabelMap.olap_quarter)}', value: 'QUARTER'},
            {text: '${StringUtil.wrapString(uiLabelMap.olap_year)}', value: 'YEAR'}];
	var cur_date = new Date();
	var past_date = new Date(cur_date);
	past_date.setMonth(past_date.getMonth()-11);
	past_date.setDate(1);
	
	function formatDateTime(date){
		var check = date.split("-");
		var size = check.length;
		var stringDate = date;
		if (size == 3) {
			stringDate = check[2] + '-' + check[1] + '-' + check[0];
		} else if (size == 2){
			stringDate = check[1] + '-' + check[0];
		} else if (size == 1) {
			stringDate = check[0];
		}
		return stringDate;
	}
	
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
</script>