<@jqGridMinimumLib />
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	<#assign company = Static['com.olbius.basehr.util.MultiOrganizationUtil'].getCurrentOrganization(delegator, userLogin.get('userLoginId'))! />;
	if (transferTypeData === undefined) {
		var transferTypeData = new Array();
		<#assign transferTypes = delegator.findList("TransferType", null, null, null, null, false) />
		var transferTypeData = new Array();
		<#list transferTypes as item>
			<#assign listChilds = delegator.findList("TransferType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", item.transferTypeId?if_exists), null, null, null, false) />
			<#if !(listChilds[0]?has_content && !item.parentTypeId?has_content)>
				var row = {};
				row['transferTypeId'] = "${item.transferTypeId?if_exists}";
				row['description'] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}";
				transferTypeData.push(row);
			</#if>
		</#list>
	}
	
	<#assign productStores = delegator.findList("ProductStore", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("payToPartyId", company)), null, null, null, false) />
	var shipmentMethodData = [];
	<#assign listIds = []>
	<#if productStores?has_content>
		<#list productStores as store>
			<#assign productStoreShipmentMethods = delegator.findList("ProductStoreShipmentMeth", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", store.productStoreId?if_exists)), null, null, null, false) />
			<#if productStoreShipmentMethods[0]?has_content>
	    		<#list productStoreShipmentMethods as meth>
		    		<#assign idNew = meth.shipmentMethodTypeId>
		        	<#assign check = true>
		        	<#list listIds as idTmp>
		        		<#if idNew == idTmp>
		        			<#assign check = false>
		        			<#break>
		        		<#else>
		        			<#assign check = true>
		        		</#if>
		        	</#list>
		        	<#if check == true>
		        		<#assign listIds = listIds + [idNew]>
		        	</#if>
	    		</#list>
	    	</#if>
		</#list>
	</#if>
	<#if listIds?has_content>
		<#list listIds as methId>
			var item = {};
			<#assign shipmentMethodType = delegator.findOne("ShipmentMethodType", {"shipmentMethodTypeId" : methId?if_exists}, false)/>
			<#assign descMeth = StringUtil.wrapString(shipmentMethodType.get('description', locale)?if_exists) />
			item['shipmentMethodTypeId'] = "${shipmentMethodType.shipmentMethodTypeId?if_exists}";
	    	item['description'] = "${descMeth?if_exists}";
	    	shipmentMethodData.push(item);
		</#list>
	</#if>
	
	<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false) />
	var quantityUomData = new Array();
	<#list quantityUoms as item>
		var row = {};
		row['uomId'] = "${item.uomId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}";
		quantityUomData.push(row);
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
	var weightUomData = new Array();
	<#list weightUoms as item>
		var row = {};
		row['uomId'] = "${item.uomId}";
		row['description'] = "${StringUtil.wrapString(item.get('abbreviation', locale)?if_exists)}";
		weightUomData.push(row);
	</#list>
	
 	function getUomDescription(uomId) {
	 	for (x in quantityUomData) {
	 		if (quantityUomData[x].uomId == uomId) {
	 			return quantityUomData[x].description;
	 		}
	 	}
	 	for (x in weightUomData) {
	 		if (weightUomData[x].uomId == uomId) {
	 			return weightUomData[x].description;
	 		}
	 	}
	 }
	
	var yesNoData = [];
	var itemYes = {
			value: "Y",
			description: "${StringUtil.wrapString(uiLabelMap.LogYes)}",
	}
	var itemNo = {
			value: "N",
			description: "${StringUtil.wrapString(uiLabelMap.LogNO)}",
	}
	yesNoData.push(itemYes);
	yesNoData.push(itemNo);
	
	<#assign transferStatuss = delegator.findList("TransferStatus", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("transferId", transfer.transferId?if_exists, "statusId", "TRANSFER_DELIVERED")), null, null, null, false) />
	<#if transferStatuss?has_content>
		<#assign statusDatetime = transferStatuss.get(0).get('statusDatetime')?if_exists />
	</#if>
	var statusDatetime = '${statusDatetime?if_exists}';
	
	var transferDate = null;
	var shipBeforeDate = null;
	var shipAfterDate = null;
	var shipBeforeDateDt = null;
	var shipAfterDateDt = null;
	<#if transfer.transferDate?exists>
		transferDate = '${transfer.transferDate?if_exists}';
	</#if>
	
	<#if transfer.shipAfterDate?exists>
		shipAfterDate = '${transfer.shipAfterDate}';
		shipAfterDateDt = '${transfer.shipAfterDate}';
	</#if>
	<#if transfer.shipBeforeDate?exists>
		shipBeforeDate = '${transfer.shipBeforeDate}';
		shipBeforeDateDt = '${transfer.shipBeforeDate}';
	</#if>

	var noteData = [];
	<#assign transferNotes = delegator.findList("TransferHeaderNote", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("transferId", transfer.transferId?if_exists)), null, null, null, false) !/>
	<#if transferNotes?has_content>
		<#list transferNotes as noteTr>
			<#assign note = delegator.findOne("NoteData", {"noteId" : noteTr.noteId?if_exists}, false)!/>
			var note = {};
			note["noteId"] = "${note.noteId}";
			note["noteInfo"] = "${StringUtil.wrapString(note.noteInfo?if_exists)}";
			note["noteName"] = "${StringUtil.wrapString(note.noteName?if_exists)}";
			note["noteDateTime"] = new Date("${note.noteDateTime?if_exists}");
			noteData.push(note);
		</#list>
	</#if>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.AreYouSureSave = "${uiLabelMap.AreYouSureSave}";
	uiLabelMap.AreYouSureCreate = "${uiLabelMap.AreYouSureCreate}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.CannotBeforeNow = "${uiLabelMap.CannotBeforeNow}";
	uiLabelMap.CanNotAfterShipBeforeDate = "${uiLabelMap.CanNotAfterShipBeforeDate}";
	uiLabelMap.CanNotBeforeShipAfterDate = "${uiLabelMap.CanNotBeforeShipAfterDate}";
	uiLabelMap.PleaseChooseTransferDateOrShipBeforeAndAfter = "${uiLabelMap.PleaseChooseTransferDateOrShipBeforeAndAfter}";
	uiLabelMap.AreYouSureApprove = "${uiLabelMap.AreYouSureApprove}";
</script>
<script type="text/javascript" src="/logresources/js/util/StringUtil.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/logresources/js/transfer/transferDetailTransfer.js"></script>