<script type="text/javascript" src="/accresources/js/report/extend.popup.js?v=20170110"></script>
<script>
	//Prepare for product data
	<#assign categories = Static["com.olbius.acc.utils.accounts.AccountUtils"].getProductCategoryByType(delegator, "CATALOG_CATEGORY", false)>
	var categoryData = [
		<#list categories as item>
			{
				<#assign description = StringUtil.wrapString(item.categoryName?if_exists)?replace("'", "\"") />
				categoryId : '${item.productCategoryId}',
				description : '${description}',
			},
		</#list>
	]
	
	//Prepare for group data
	<#assign groups = delegator.findList("PartyClassificationGroup", null, null, null, null, false)>
	var groupData = [
		<#list groups as item>
			{
				<#assign description = StringUtil.wrapString(item.description?if_exists)?replace("'", "\"") />
				groupId : '${item.partyClassificationGroupId}',
				description : '${description}',
			},
		</#list>
	]
	
	var listCategoriesDataSource = new Array();
	for(var i = 0; i < categoryData.length; i++){
		var categoryDataSource = {
			text: categoryData[i].description,
			value: categoryData[i].categoryId,
		}
		listCategoriesDataSource.push(categoryDataSource);
	}
	
	var listGroupDataSource = new Array();
	for(var i = 0; i < groupData.length; i++){
		var groupDataSource = {
			text: groupData[i].description,
			value: groupData[i].groupId,
		}
		listGroupDataSource.push(groupDataSource);
	}
</script>