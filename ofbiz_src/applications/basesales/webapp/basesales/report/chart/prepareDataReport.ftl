<script type="text/javascript">
	<#-- organization -->
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)!/>
	
	<#-- filterData -->
	var filterData = [
	   	{'text': "${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}", 'value': "PRODUCT_STORE"}, 
	   	{'text': "${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}", 'value': "CHANNEL"}, 
	   	{'text': "${StringUtil.wrapString(uiLabelMap.BSBranch)}", 'value': "REGION"}, 
	   	{'text': "${StringUtil.wrapString(uiLabelMap.BSRanks)}", 'value': "LEVEL"}
   	];
   	
   	<#-- region -->
   	<#assign salesRegion = delegator.findList("PartyRegion", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", ownerPartyId)), null, null, null, false)!>
   	var salesRegion = [
		<#list salesRegion as salesRegionL>
			{
				partyId : "${salesRegionL.partyId}",
				groupName: "${StringUtil.wrapString(salesRegionL.get("groupName", locale))}"
			},
		</#list>	
	];
	
	var listRegionDataSource = [];
	for(var x in salesRegion){
		var regionDataSource = {
			text: salesRegion[x].groupName,
			value: salesRegion[x].partyId,
		}
	listRegionDataSource.push(regionDataSource);
	}
   	
   	<#-- channel -->
   	<#assign salesChannel = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "SALES_METHOD_CHANNEL"), null, false)!>
	var salesChannel = [
		<#list salesChannel as salesChannelL>
			{
				enumId : "${salesChannelL.enumId}",
				description: "${StringUtil.wrapString(salesChannelL.get("description", locale))}"
			},
		</#list>	
	];
	
	var listChannelDataSource = [];
	for(var x in salesChannel){
		var channelDataSource = {
			text: salesChannel[x].description,
			value: salesChannel[x].enumId,
		}
	listChannelDataSource.push(channelDataSource);
	} 
	
   	<#-- product store -->
   	<#assign productStore = delegator.findList("ProductStore", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("payToPartyId", ownerPartyId)), null, null, null, false)!>
   	
   	var productStoreData = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': ''}];
	<#if productStore?exists>
		<#list productStore as productStoreL >
			productStoreData.push({ 'value': '${productStoreL.productStoreId?if_exists}', 'text': '${StringUtil.wrapString(productStoreL.storeName)?if_exists}'});
		</#list>
	</#if>
	
	var productStoreList = [
		<#if productStore?exists>
			<#list productStore as itemStore>
				{
					productStoreId: "${itemStore.productStoreId?if_exists}",
					storeName: "${StringUtil.wrapString(itemStore.get("storeName", locale)?if_exists)}"
				},
			</#list>
		</#if>
	];
	
	var listStoreDataSource = [];	
	for(var x in productStoreList){
		var storeDataSource = {
			text: productStoreList[x].storeName,
			value: productStoreList[x].productStoreId,
		}
		listStoreDataSource.push(storeDataSource);
	}
	
</script>