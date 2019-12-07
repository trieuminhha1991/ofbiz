<#assign hasCreateSupplierAddress = false/>
<form class="form-horizontal form-window-content-custom" id="initPurchaseOrderEntry" name="initPurchaseOrderEntry" method="post" 
	action="<@ofbizUrl>initOrderEntryPurchase</@ofbizUrl>">
	<div class="row-fluid" style="margin-top: 10px;">
		<div class="span12">
			<div class="span5">
				<div class="row-fluid">
					<div class="span5">
						<label class="required">${uiLabelMap.BPOSupplier}</label>
					</div>
					<div class="span7">
						<div id="supplier" style="width: 100%" class="green-label">
							<div id="jqxgridSupplier"></div>
							<input id="supplierId" type="hidden"></input>
						</div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span5">
						<label class="required">${uiLabelMap.ReceiveToFacility}</label>
					</div>
					<div class="span7">
						<div id="originFacilityId" class="hide"></div>
						<div id="facility" class="green-label">
							<div id="jqxgridFacility"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="span7">
				<div class="row-fluid">
					<div class="span5 margin-top10">
						<label class="required">${uiLabelMap.BLShippingDate}</label>
					</div>
					<div class="span7">
						<div id="shipAfterDate"></div>
						<div id="shipBeforeDate"  class="margin-top10"></div>
					</div>
				</div>
				<div class="row-fluid ">
					<div class="span5">
						<label class="required">${uiLabelMap.BPOCurrencyUomId}</label>
					</div>
					<div class="span7">
						<div id="currencyUomId" style="width:100%"></div>
					</div>
				</div>
				<div class="row-fluid hide">
					<div class="span5">
						<label class="required">${uiLabelMap.ShippingAddress}</label>
					</div>
					<div class="span7">
						<div id="shippingContactMechId" style="width: 100%;">
							<div id="shippingContactMechGrid"></div>
						</div>
						<input type="hidden" id="contactMechIdHidden" />
						<#if hasCreateSupplierAddress><a href="javascript:void(0);" id="addNewShippingAddress" class="add-quickly hide"><i class="icon-plus open-sans"></i></a></#if>
					</div>
				</div>
			</div>
		</div>
	</div>
</form>
<script type="text/javascript">
	var locale = '${locale}';
	var defaultShipAfterDate = <#if defaultShipAfterDate?exists>"${defaultShipAfterDate}"<#else>null</#if>;
	var defaultShipBeforeDate = <#if defaultShipBeforeDate?exists>"${defaultShipBeforeDate}"<#else>null</#if>;
	var defaultFacilityId = <#if defaultFacilityId?exists>"${defaultFacilityId}"<#else>null</#if>;
	var defaultSupplierId = <#if defaultSupplierId?exists>"${defaultSupplierId}"<#else>null</#if>;
	var defaultContactMechId = <#if defaultContactMechId?exists>"${defaultContactMechId}"<#else>null</#if>;
	var defaultSupplierName = <#if defaultSupplierName?exists>"${defaultSupplierName}"<#else>null</#if>;
	var defaultCurrencyUomId = <#if defaultCurrencyUomId?exists>"${defaultCurrencyUomId}"<#else>null</#if>;
	<#if originFacility?exists>
		var facilityIdTmp = "${originFacility.facilityId?if_exists}";
		var facilityCodeTmp = "${originFacility.facilityCode?if_exists}";
		var facilityNameTmp = "${originFacility.facilityName?if_exists}";
		facilitySelected = {};
		facilitySelected.facilityId = facilityIdTmp;
		facilitySelected.facilityCode = facilityCodeTmp;
		facilitySelected.facilityName = facilityNameTmp;
	</#if>
	var locale = '${locale}';
	<#if listOrderItemEdits?has_content && listOrderItemEdits?length &gt; 0>
		listOrderItemInit = [];
		listorderItemSeqIds = [];
		listProductIds = [];
		<#list listOrderItemEdits as oi>
			var itemMap = {};
			var product = {};
			<#if oi.selectedAmount &gt; 0 && oi.requireAmount?has_content && oi.requireAmount == 'Y'>
				itemMap.quantityPurchase = '${oi.selectedAmount?if_exists}';
				var qty = '${oi.selectedAmount?if_exists}';
				var price = '${oi.unitPrice?if_exists/oi.selectedAmount}';
				<#if locale == 'vi'>
					if (typeof price == 'string') {
						price = price.replace(',', '.');
					}
					if (typeof qty == 'string') {
						qty = qty.replace(',', '.');
					}
				</#if>
				itemMap.quantity = parseFloat(qty, null, 3);
				itemMap.quantityPurchase = parseFloat(qty, null, 3);
				itemMap.lastPrice = parseFloat(price, null, 3);
			<#else>
				itemMap.quantityPurchase = '${oi.alternativeQuantity?if_exists}';
				itemMap.quantity = '${oi.quantity?if_exists}';
				var price = '${oi.alternativeUnitPrice?if_exists}';
				<#if locale == 'vi'>
					if (typeof price == 'string') {
						price = price.replace('.', '');
						price = price.replace(',', '.');
					}
				</#if>
			    itemMap.lastPrice = parseFloat(price, null, 3);
			</#if>
			itemMap.productId = '${oi.productId?if_exists}';
			itemMap.quantityReceived = parseInt('${oi.quantityReceived?if_exists}');
			itemMap.supplierProductId = defaultSupplierId;
			itemMap.weightUomId = '${oi.weightUomId?if_exists}';
			itemMap.quantityUomId = '${oi.quantityUomId?if_exists}';
			itemMap.itemComment = StringCommonObj.unescapeHTML('${StringUtil.wrapString(oi.comments?if_exists?html)}');
			itemMap.orderItemSeqId = '${oi.orderItemSeqId?if_exists}';
			itemMap.requireAmount = '${oi.requireAmount?if_exists}';
			var tmp = $.extend({}, itemMap);
			productOrderMap['${oi.productId?if_exists}'] = tmp;
			listOrderItemInit.push(itemMap);
			product.productId = '${oi.productId?if_exists}';
			listProductIds.push(product);
			listorderItemSeqIds.push('${oi.orderItemSeqId?if_exists}');
		</#list>
	</#if>
	
	<#if planHeader?has_content>
		var listProducts = [];
		$.ajax({
			url : "getProductFromImportPlanToCreatePO",
			type : "POST",
			data : {
				productPlanId : "${planHeader.productPlanId?if_exists}",
				customTimePeriodId : "${parameters.customTimePeriodId?if_exists}",
			},
			dataType : "json",
			success : function(data) {
				
			}
		}).done(function(data) {
			if (data.listProducts) {
				listProducts = data.listProducts;
				if (listProducts.length > 0) {
					for (var x in listProducts) {
						var it = listProducts[x];
						it.quantityUomId = listProducts[x].purchaseUomId;
						productOrderMap[it.productId] = it;
					}
				} else {
					window.location.href = "newPurchaseOrder";
				}
			}
		});
		
	</#if>
	
	<#if copyOrderId?exists>
		<#assign orderRoles = delegator.findList("OrderRole", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", copyOrderId?if_exists, "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false) />
		<#if orderRoles?has_content && orderRoles?length &gt; 0>
			<#list orderRoles as rl>
				<#assign partyIdTmp = rl.partyId?if_exists>
					<#assign sup = delegator.findOne("PartyGroup", {"partyId" : partyIdTmp?if_exists}, false)/>
					<#assign prty = delegator.findOne("Party", {"partyId" : partyIdTmp?if_exists}, false)/>
					defaultSupplierId = "${partyIdTmp?if_exists}";
					defaultSupplierName = "${sup.groupName?if_exists}";
					
					supplierSelected = {};
					supplierSelected.partyId = "${prty.partyId?if_exists}";
					supplierSelected.partyCode = "${prty.partyCode?if_exists}";
					supplierSelected.groupName = "${sup.groupName?if_exists}";
				<#break>
			</#list>
		</#if>
		<#assign a = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, "ITEM_CANCELLED")/>
		<#assign b = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("orderId", copyOrderId?if_exists)/>
		
		<#assign c = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("isPromo", "N")/>
		<#assign d = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("isPromo", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, null)/>
		
		<#assign orCond = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(c, d), Static["org.ofbiz.entity.condition.EntityOperator"].OR)>
		
		<#assign orderItemTmps = delegator.findList("OrderItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(a, b, orCond), null, null, null, false) />
		
		<#assign oh = delegator.findOne("OrderHeader", {"orderId" : copyOrderId?if_exists}, false)/>
		<#assign originFacility = delegator.findOne("Facility", {"facilityId" : oh.originFacilityId?if_exists}, false)/>
		<#if originFacility?exists>
			var facilityIdTmp = "${originFacility.facilityId?if_exists}";
			var facilityCodeTmp = "${originFacility.facilityCode?if_exists}";
			var facilityNameTmp = "${originFacility.facilityName?if_exists}";
			facilitySelected = {};
			facilitySelected.facilityId = facilityIdTmp;
			facilitySelected.facilityCode = facilityCodeTmp;
			facilitySelected.facilityName = facilityNameTmp;
		</#if>
		
		<#if orderItemTmps?has_content && orderItemTmps?length &gt; 0>
			productOrderMap = [];
			<#list orderItemTmps as oi>
				var itemMap = {};
				var product = {};
				<#assign pr = delegator.findOne("Product", {"productId" : oi.productId?if_exists}, false)/>
				<#if oi.selectedAmount &gt; 0 && pr.requireAmount?has_content && pr.requireAmount == 'Y'>
					var qty = '${oi.selectedAmount?if_exists}';
					var price = '${oi.unitPrice?if_exists/oi.selectedAmount}';
					<#if locale == 'vi'>
						if (typeof price == 'string') {
							price = price.replace(',', '.');
						}
						if (typeof qty == 'string') {
							qty = qty.replace(',', '.');
						}
					</#if>
					itemMap.quantity = parseFloat(qty, null, 3);
					itemMap.quantityPurchase = parseFloat(qty, null, 3);
					itemMap.lastPrice = parseFloat(price, null, 3);
				<#else>
					itemMap.quantityPurchase = '${oi.alternativeQuantity?if_exists}';
					itemMap.quantity = '${oi.quantity?if_exists}';
					var price = '${oi.alternativeUnitPrice?if_exists}';
					<#if locale == 'vi'>
						if (typeof price == 'string') {
							price = price.replace(',', '.');
						}
					</#if>
				    itemMap.lastPrice = parseFloat(price, null, 3);
				</#if>
				itemMap.productId = '${oi.productId?if_exists}';
				itemMap.supplierProductId = defaultSupplierId;
				itemMap.weightUomId = '${oi.weightUomId?if_exists}';
				itemMap.quantityUomId = '${oi.quantityUomId?if_exists}';
				itemMap.itemComment = '${StringUtil.wrapString(oi.comments?if_exists)}';
				itemMap.orderItemSeqId = '${oi.orderItemSeqId?if_exists}';
				itemMap.requireAmount = '${pr.requireAmount?if_exists}';
				var tmp = $.extend({}, itemMap);
				productOrderMap['${oi.productId?if_exists}'] = tmp;
			</#list>
		</#if>
	</#if>
</script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>

<@jqOlbCoreLib hasComboBox=true/>
<script type="text/javascript" src="/poresources/js/order/orderNewInfoPurchase.js?v=1.1.3"></script>
<#if hasCreateSupplierAddress><#include "orderNewPurchaseContactMech.ftl"/></#if>