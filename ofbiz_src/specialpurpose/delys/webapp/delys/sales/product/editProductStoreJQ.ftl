<#assign listProductStoreGroup = delegator.findByAnd("ProductStoreGroup", {"productStoreGroupTypeId": "PSGT_AREA_CHANNEL"}, ["productStoreGroupName"], false)!/>
<#assign listFacility = delegator.findByAnd("Facility", null, ["facilityName"], false)!/>
<#assign currencies = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, ["description"], false)!/>
<#assign salesMethodChannels = delegator.findByAnd("Enumeration", {"enumTypeId" : "SALES_METHOD_CHANNEL"}, null, false)/>
<script type="text/javascript">
	<#if listProductStoreGroup?exists>
		var productStoreGroupData = [
		<#list listProductStoreGroup as productStoreGroupItem>
			{
				productStoreGroupId: ${productStoreGroupItem.productStoreGroupId},
				productStoreGroupName: ${productStoreGroupItem.productStoreGroupName?default("")}
			}, 
		</#list>
		];
	<#else>
		var productStoreGroupData = [];
	</#if>
	
	<#if currencies?exists>
		var localDataCurrency = [
			<#list currencies as dataItem>
				<#assign description = StringUtil.wrapString(dataItem.get("description", locale)) />
				{uomId: "${dataItem.uomId}",
				abbreviation: "${dataItem.abbreviation?default(dataItem.uomId)}",
				description: "${description}"},
			</#list>
		];
	<#else>
		var localDataCurrency = [];
	</#if>
	<#if salesMethodChannels?exists>
		var salesMethodChannelData = [
		<#list salesMethodChannels as item>
		{
			channelId: "${item.enumId}",
			description: "${StringUtil.wrapString(item.get("description", locale))}"
		},
		</#list>
		];
	<#else>
		var salesMethodChannelData = [];
	</#if>
</script>

<form class="form-horizontal form-table-block" id="EditProductStore" name="EditProductStore" method="post" action="<@ofbizUrl>updateProductStore</@ofbizUrl>">
	<input type="hidden" name="productStoreId" value="${productStore.productStoreId}"/>
	<input type="hidden" name="oldStyleSheet" value="${productStore.oldStyleSheet?if_exists}"/>
	<input type="hidden" name="oldHeaderLogo" value="${productStore.oldHeaderLogo?if_exists}"/>
	<input type="hidden" name="oldHeaderMiddleBackground" value="${productStore.oldHeaderMiddleBackground?if_exists}"/>
	<input type="hidden" name="oldHeaderRightBackground" value="${productStore.oldHeaderRightBackground?if_exists}"/>
	<div class="row-fluid">
		<div class="span6">
			<div class="control-group">
				<label class="control-label" for="productStoreId">${uiLabelMap.DAProductStoreId}</label>
				<div class="controls">
					<input id="productStoreIdHide" maxlength="20" value="${productStore.productStoreId}"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="primaryStoreGroupId">${uiLabelMap.DAPrimaryStoreGroup}</label>
				<div class="controls">
					<select name="primaryStoreGroupId">
						<#if listProductStoreGroup?exists>
							<#list listProductStoreGroup as productStoreGroupItem>
								<option value="${productStoreGroupItem.productStoreGroupId}" <#if productStore.productStoreGroupId?if_exists && productStore.productStoreGroupId == productStoreGroupItem.productStoreGroupId>selected="selected"</#if>>
									${productStoreGroupItem.productStoreName?if_exists} [${productStoreGroupItem.productStoreGroupId}]
								</option>
							</#list>
						</#if>
					</select>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="storeName">${uiLabelMap.DAStoreName}</label>
				<div class="controls">
					<input id="storeName" name="storeName" maxlength="60" value="${productStore.storeName?if_exists}"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="title">${uiLabelMap.ProductTitle}</label>
				<div class="controls">
					<input id="title" name="title" maxlength="60" value="${productStore.title?if_exists}"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="title">${uiLabelMap.ProductSubTitle}</label>
				<div class="controls">
					<input id="subtitle" name="subtitle" maxlength="250" value="${productStore.subtitle?if_exists}"/>
				</div>
			</div>
		</div><!--.span6-->
		<div class="span6">
			<div class="control-group">
				<label class="control-label" for="companyName">${uiLabelMap.DACompanyName}</label>
				<div class="controls">
					<input id="companyName" name="companyName" maxlength="100" value="${productStore.companyName?if_exists}"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="payToPartyId">${uiLabelMap.DAPayToParty}</label>
				<div class="controls">
					<@htmlTemplate.lookupField name="payToPartyId" id="payToPartyId" value='${productStore.payToPartyId?if_exists}' width="1150"
						formName="EditProductStore" fieldFormName="LookupPartyName" title="${uiLabelMap.DALookupPartyName}"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="inventoryFacilityId">${uiLabelMap.DAPrimaryInventoryFacility}</label>
				<div class="controls">
					<select name="inventoryFacilityId">
						<#if listFacility?exists>
							<#list listFacility as facilityItem>
								<option value="${facilityItem.facilityId}" <#if productStore.inventoryFacilityId?exists && productStore.inventoryFacilityId == facilityItem.facilityId>selected="selected"</#if>>
									${facilityItem.facilityName?if_exists} [${facilityItem.facilityId}]
								</option>
							</#list>
						</#if>
					</select>
					<a href="<@ofbizUrl>inventoryItemList?facilityId=<#if productStore.inventoryFacilityId?exists>${productStore.inventoryFacilityId}</#if></@ofbizUrl>" 
						 data-rel="tooltip" title="${uiLabelMap.CommonView} ${uiLabelMap.ProductFacility} ${productStore.inventoryFacilityId?if_exists}" data-placement="bottom">
						<i class="open-sans icon-edit"></i>
					</a>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="currencyUomId">${uiLabelMap.DADefaultCurrencyUom}</label>
				<div class="controls">
					<div id="currencyUomId" name="currencyUomId"></div>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="salesMethodChannelEnumId">${uiLabelMap.DAChannel}</label>
				<div class="controls">
					<div id="salesMethodChannelEnumId" name="salesMethodChannelEnumId"></div>
				</div>
			</div>
		</div><!--.span12-->
	</div><!--.row-fluid-->
	<div class="row-fluid">
		<div class="span12">
			<button type="submit" class="btn btn-mini btn-primary pull-right"><i class="icon-ok open-sans"></i>${uiLabelMap.CommonUpdate}</button>
		</div>
	</div>
</form>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/delys/images/js/sales/salesCommon.js"></script>
<script type="text/javascript">
	jQuery(function(){
		$('[data-rel=tooltip]').tooltip();
		
		$("#productStoreIdHide").jqxInput({width: 212, height: 26, maxLength:20, disabled:true});
		$("#storeName").jqxInput({width: 212, height: 26, maxLength:60});
		$("#title").jqxInput({width: 212, height: 26, maxLength:60});
		$("#subtitle").jqxInput({width: 212, height: 26, maxLength:250});
		$("#companyName").jqxInput({width: 212, height: 26, maxLength:100});
		
    	var configCurrency = {
    		width: "218px",
    		key: "uomId",
    		value: "description",
    		dropDownWidth: "280px",
    		displayDetail:true,
    	};
    	initComboBoxEnum($("#currencyUomId"), ["${defaultOrganizationPartyCurrencyUomId?default("VND")}"], localDataCurrency, "${StringUtil.wrapString(uiLabelMap.DAChooseACurrency)}", configCurrency);
    	
    	var config = {
    		width: "218px",
    		key: "channelId",
    		value: "description",
    		autoDropDownHeight: true,
    		displayDetail: false,
    		disabled: true,
    	};
    	initComboBoxEnum($("#salesMethodChannelEnumId"), [<#if productStore.salesMethodChannelEnumId?exists>"${productStore.salesMethodChannelEnumId}"</#if>], salesMethodChannelData, "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}", config);
	});
</script>