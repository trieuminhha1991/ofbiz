<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.grid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.dropdownbutton.js"></script>

<@jqOlbCoreLib />
<script>
	listInvChanged = [];
	var facilityId = '${parameters.facilityId?if_exists}';
	function reponsiveRowDetails(grid, parentElement) {
	    $(window).bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	    $('#sidebar').bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	}
	<#assign uomList = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
	var uomData = 
	[
		<#list uomList as uom>
		{
			uomId: "${uom.uomId}",
			description: "${StringUtil.wrapString(uom.get('description', locale)?if_exists)}"
		},
		</#list>
	];
	<#assign wuomList = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false) />
	var weightUomData = 
	[
		<#list wuomList as uom>
		{
			uomId: "${uom.uomId}",
			description: "${StringUtil.wrapString(uom.get('abbreviation', locale)?if_exists)}"
		},
		</#list>
	];
	
	var allUomData = [];
	allUomData.concat(uomData);
	allUomData.concat(weightUomData);
	
	function getDescriptionByUomId(uomId) {
		for ( var x in uomData) {
			if (uomId == uomData[x].uomId) {
				return uomData[x].description;
			}
		}
		for ( var x in weightUomData) {
			if (uomId == weightUomData[x].uomId) {
				return weightUomData[x].description;
			}
		}
	}
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "INV_NON_SER_STTS"), null, null, null, false)/>
	var statusData = [];
	<#list statuses as item>
		var row = {};
		<#assign descStatus = StringUtil.wrapString(item.get('description', locale))>
		row['statusId'] = "${item.statusId}";
		row['description'] = "${descStatus?if_exists}";
		statusData[${item_index}] = row;
	</#list>
	<#assign salesChannels = delegator.findList("Enumeration", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumTypeId", "SALES_METHOD_CHANNEL"), null, null, null, false)/>
	var salesChannel = [];
	<#list salesChannels as item>
		var row = {};
		<#assign descChannel = StringUtil.wrapString(item.get('description', locale))>
		row['enumId'] = "${item.enumId}";
		row['description'] = "${descChannel?if_exists}";
		salesChannel.push(row);
	</#list>
	var listViewMethod01 = [];
	var listViewMethod02 = [];
	var mt1 = {};
	mt1["methodId"] = "viewByInventoryItem";
	mt1["description"] = "${uiLabelMap.DetailItem}";
	var mt2 = {};
	mt2["methodId"] = "viewByProduct";
	mt2["description"] = "${uiLabelMap.GroupByProduct}";
	listViewMethod01.push(mt2);
	listViewMethod01.push(mt1);
	listViewMethod02.push(mt2);
	listViewMethod02.push(mt1);
	
	var invLabel = [];
	<#assign invLabels = delegator.findList("InventoryItemLabel", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("inventoryItemLabelTypeId", "USING_PURPOSE"), null, null, null, false)/>
	<#list invLabels as item>
		var row = {};
		<#assign descLabel = StringUtil.wrapString(item.get('description', locale))>
		row['inventoryItemLabelId'] = "${item.inventoryItemLabelId}";
		row['description'] = "${descLabel?if_exists}";
		invLabel.push(row);
	</#list>
	<#assign localeStr = "VI" />
	var localeStr = "VI";
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	
	var deposit = null;
	<#if parameters.deposit?has_content && parameters.deposit == 'Y'>
		deposit = 'Y';
	</#if>
	
	<#assign isDistributor = Static["com.olbius.basesales.util.SalesPartyUtil"].isDistributor(delegator, userLogin.getString("partyId"))!/> 
	
	var listFacilitySelected = [];
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
	
	<#if hasOlbPermission("MODULE", "DISTRIBUTOR", "ADMIN")>
		<#assign company = userLogin.partyId?if_exists>
	</#if>
	
	var company = "${company?if_exists}";
	if (uiLabelMap === undefined) var uiLabelMap = {};
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.BLLocationCode = "${StringUtil.wrapString(uiLabelMap.BLLocationCode)}";
	uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.FacilityId = "${StringUtil.wrapString(uiLabelMap.FacilityId)}";
	uiLabelMap.FacilityName = "${StringUtil.wrapString(uiLabelMap.FacilityName)}";
	uiLabelMap.Facility = "${StringUtil.wrapString(uiLabelMap.Facility)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.BLPrimaryUPC = "${StringUtil.wrapString(uiLabelMap.BLPrimaryUPC)}";
	
	var daysAgo =
	[
		{value: "all", text: "${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}"},
		{value: "3d", text: "${StringUtil.wrapString(uiLabelMap.SGCOver3days)}"},
		{value: "1w", text: "${StringUtil.wrapString(uiLabelMap.SGCOver1week)}"},
		{value: "1m", text: "${StringUtil.wrapString(uiLabelMap.SGCOver1month)}"}
	];
    <#assign json = ""/>
    var listFacility;
    <#if facilityJson?exists>
        <#assign json = StringUtil.wrapString(facilityJson)/>
        listFacility = '${facilities}';
    </#if>
</script>
<script type="text/javascript" src="/logresources/js/inventory/inventoryChange.js?v=1.0.5"></script>
<script type="text/javascript" src="/logresources/js/inventory/listInventory.js?v=1.0.5"></script>