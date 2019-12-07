<script>
	var locale = "${locale}";
	var contentFontSize = 8;
	var infoFontSize = 10;
	var headerFontSize = 12;
	var fontFamily = 'Tahoma';
	var isPrintBeforePayment = 'Y';
	<#assign productStoreId = Static["org.ofbiz.product.store.ProductStoreWorker"].getProductStoreId(request) !>
	<#if productStoreId?exists && productStoreId?has_content>
		<#assign config = delegator.findOne("ConfigPrintOrderAndStore", Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", productStoreId), false) !>
	</#if>
	<#if config?exists && config?has_content>
		contentFontSize = '${config.contentFontSize?if_exists}';
		infoFontSize = '${config.infoFontSize?if_exists}';
		headerFontSize = '${config.headerFontSize?if_exists}';
		fontFamily = '${config.fontFamily?if_exists}';
		isPrintBeforePayment = '${config.isPrintBeforePayment?if_exists}';
	</#if>
	var BPOSAddress = '${StringUtil.wrapString(uiLabelMap.BPOSAddress)}';
	var BPOSMobile = '${StringUtil.wrapString(uiLabelMap.BPOSMobile)}';
	var BPOSCustomer = '${StringUtil.wrapString(uiLabelMap.BPOSCustomer)}';
	var showPricesWithVatTax = true;
	<#if showPricesWithVatTax == "Y">
		showPricesWithVatTax = false;
	</#if>
</script>
<#assign productStoreId = Static["org.ofbiz.product.store.ProductStoreWorker"].getProductStoreId(request) !>
<#if productStoreId?exists && productStoreId?has_content>
	<#assign config = delegator.findOne("ConfigPrintOrderAndStore", Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", productStoreId), false) !>
	<#assign productStore = delegator.findOne("ProductStore", Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", productStoreId), false) !>
</#if>
<#if config?exists && config?has_content>
	<#assign headerFontSize = config.headerFontSize?if_exists !>
	<#assign contentFontSize = config.contentFontSize?if_exists !>
	<#assign infoFontSize = config.infoFontSize?if_exists !>
	<#assign fontFamily = config.fontFamily?if_exists !>
<#else>	
	<#assign headerFontSize = "12" !>
	<#assign contentFontSize = "8" !>
	<#assign infoFontSize = "10" !>
	<#assign fontFamily = "Tahoma" !>
</#if>
<div id="PrintOrder" style="width:94%;font-family: ${fontFamily}; font-size: 11px;margin: 0px; right: 2px;padding:0;display: none;color:#000">
		<table cellpadding="0" cellspacing="0" style="width:100%;">
			<tbody>
				<tr>
					<td colspan="2" rowspan="3">
						<img alt="${config.storeName?if_exists}" src="${config.logo?if_exists}" style="width: 50px"/>
					</td>
					<td colspan="10" rowspan="1">
						<b style="font-size:${infoFontSize}pt;font-weight:bold;">
						<#if config.storeName?exists>
							${config.storeName?if_exists}
						<#else>
							<#if productStore?exists && productStore?has_content>
								${productStore.storeName?if_exists}
							</#if>
						</#if>
						</b>
					</td>
				</tr>
				<tr>
					<td colspan="10" rowspan="1">
						<span style="padding:0;font-size:${infoFontSize}pt;">
							<#if facility?exists>
								${facility.address1?if_exists} -- ${facility.city?if_exists}
							</#if>
						</span>
					</td>
				</tr>
				<tr>
					<td colspan="10" rowspan="1">
						<span style="padding:0;font-size:${infoFontSize}pt;">
							<#if facility?exists>
								${facility.contactNumber?if_exists}
							</#if>
						</span>
					</td>
				</tr>
				<tr>
					<td colspan="12">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="12"><center><span style="font-size:${headerFontSize}pt;font-weight:bold;text-transform: uppercase;">${uiLabelMap.BPOSRetailBill}</span></center></td>
				</tr>
				<tr>
					<td colspan="12">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="6" rowspan="1"><span style="font-size:${infoFontSize}pt;">${uiLabelMap.BPOSEmployee}:</span></td>
					<#assign partyName = (delegator.findOne("PartyNameView", {"partyId" : userLogin.partyId}, true))!>
					<#if partyName?exists>
						<td colspan="6" rowspan="1"><span id="employee" style="font-size:${infoFontSize}pt;">${partyName.partyId} - ${partyName.lastName}  ${partyName.middleName?if_exists}   ${partyName.firstName}</span></td>	
					<#else>
						<td colspan="6" rowspan="1"><span id="employee" style="font-size:${infoFontSize}pt;">${userLogin.userLoginId}</span></td>
					</#if>
				</tr>
				<tr>
					<td colspan="6" rowspan="1"><span style="font-size:${infoFontSize}pt;">${uiLabelMap.BPOSTime}:</span></td>
					<#assign timeSale = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString().split('\\.')[0]>
					<td colspan="6" rowspan="1"><span id="timeSale" style="font-weight:bold;font-size:${infoFontSize}pt;">${timeSale}</span></td>
				</tr>
				<tr id="customerName">
				
				</tr>
				<tr id="customerAddress">
				
				</tr>
				<tr id="customerPhone">
				
				</tr>
				<tr>
					<td colspan="12"><span id="currencyUom" style="float:right; font-size:${infoFontSize}pt;">ĐVT: VND</span></td>
				</tr>
			</tbody>
		</table>
		<table cellpadding="0" cellspacing="0" style="width:100%;">
			<thead>
				<tr>
					<th style="border:1px solid #CCC;"><center><span style="font-weight:bold;padding:0 2px;font-size:${contentFontSize}pt">#</span></center></th>
					<th style="border:1px solid #CCC;"><center><span style="font-weight:bold;padding:0 2px;font-size:${contentFontSize}pt">${uiLabelMap.BPOSShortProductName}</span></center></th>
					<th style="border:1px solid #CCC;"><center><span style="font-weight:bold;padding:0 2px;font-size:${contentFontSize}pt">${uiLabelMap.BPOSPrice}</span></center></th>
					<th style="border:1px solid #CCC;"><center><span style="font-weight:bold;padding:0 2px;font-size:${contentFontSize}pt">${uiLabelMap.BPOSDiscount}</span></center></th>
					<th style="border:1px solid #CCC;"><center><span style="font-weight:bold;padding:0 2px;font-size:${contentFontSize}pt">${uiLabelMap.BPOSShortQuantity}</span></center></th>
					<th style="border:1px solid #CCC;min-width: 60px;"><center><span style="font-weight:bold;padding:0 2px;font-size:${contentFontSize}pt">${uiLabelMap.BPOSPrintAmount}</span></center></th>
				</tr>
			</thead>
			<tbody id="bodyPrint">
			</tbody>
		</table>
		<p style="text-align:center; margin-top: 20px"><span style="font-size:${infoFontSize}pt;">${uiLabelMap.BPOSGoodBye}</span></p>
	</div>
<style>

@media print {
	#PrintOrder{
		display: block !important;
	}
	#PrintOrder table tr td,#PrintOrder table tr th{
		
	}
}
</style>