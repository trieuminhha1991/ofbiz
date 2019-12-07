<#assign isFavorDelivery = false/>
<#if shoppingCart?exists>
	<#if shoppingCart.getAttribute("isFavorDelivery")?exists && "Y" == shoppingCart.getAttribute("isFavorDelivery")>
		<#assign isFavorDelivery = true/>
	</#if>
	<#if shoppingCart.getAttribute("favorSupplierPartyId")?exists>
		<#assign favorSupplierPartyId = shoppingCart.getAttribute("favorSupplierPartyId")/>
	</#if>
	<#if shoppingCart.getAttribute("shipGroupFacilityId")?exists>
		<#assign shipGroupFacilityId = shoppingCart.getAttribute("shipGroupFacilityId")/>
	</#if>
</#if>
<#if !isFavorDelivery>
<style type="text/css">
	.favor-delivery-container {display:none}
	#menuWrappercontextMenushippingContactMechGrid {z-index:99999 !important}
</style>
</#if>
<style type="text/css">
	#dropDownButtonContentshipGroupFacilityId, #dropDownButtonContentfavorSupplierPartyId, 
	#dropDownButtonContentfavorDistributorPartyId, #dropDownButtonContentfacilityConsignId {
		width:90% !important;
	}
</style>

<form class="form-horizontal form-window-content-custom" id="checkoutOrderEntry" name="checkoutOrderEntry" method="post" action="<@ofbizUrl>initOrderEntrySales</@ofbizUrl>">
	<div class="row-fluid">
		<div class="span12">
			<div class='row-fluid'>
				<div class='span5'>
					<label>${uiLabelMap.BSPartyReceive}</label>
				</div>
				<div class="span7">
					<div id="shipToCustomerPartyId">
						<div id="shipToCustomerPartyGrid"></div>
					</div>
				</div>
			</div>
			<#-- 1. them dia chi moi
				 2. splitship OrderSplitIntoMultipleShipments -->
			<div class="row-fluid">
				<div class='span5'>
					<label class="required">${uiLabelMap.BSShippingAddress}</label>
				</div>
				<div class="span7">
					<div class="container-add-plus">
						<div id="shippingContactMechId">
							<div id="shippingContactMechGrid"></div><#-- name="shipping_contact_mech_id" -->
						</div>
						<a href="javascript:void(0);" id="addNewShippingAddress" class="add-quickly"><i class="icon-plus open-sans"></i></a>
					</div>
		   		</div>
			</div>
			
			<div class="row-fluid">
				<div class='span5'>
					<label class="required">${uiLabelMap.BSShippingMethod}</label>
				</div>
				<div class="span7">
					<div id="shippingMethodTypeId"></div><#-- name="shipping_method" -->
		   		</div>
			</div>
			
			<div class="row-fluid">
				<div class='span5'>
					<label class="required">${uiLabelMap.BSPaymentMethod}</label>
				</div>
				<div class="span7">
					<div class="container-add-plus">
						<div id="checkOutPaymentId"></div><#-- name="checkOutPaymentId" -->
						<#if enableCheckLiability?exists && enableCheckLiability>
						<a href="javascript:void(0);" id="checkLiability" class="add-quickly"><i class="icon-ok open-sans"></i></a>
						</#if>
					</div>
		   		</div>
			</div>
			
			<#-- 3. check may split in shoppingCart -->
			
			<!--shipping_instructions-->
			<div class="row-fluid">
				<div class='span5'>
					<label>${uiLabelMap.BSNoteShipping}</label>
				</div>
				<div class="span7">
					<input class="span12" type="text" id="shippingInstructions" name="shippingInstructions" value=""/><#-- name="shipping_instructions" -->
		   		</div>
			</div>
			
			<#--
			<#assign giftEnable = "N">
			<#if productStore.showCheckoutGiftOptions?if_exists != "N" && giftEnable?if_exists != "N">
				<div class="row-fluid">
					<div class='span5'>
						<label>${uiLabelMap.BSIsThisGift}</label>
					</div>
					<div class="span7">
						<div id="isGift"></div>name="is_gift"
			   		</div>
				</div>
			</#if>
			-->
			
			<!--gift_message-->
			
			<!-- other -->
			<#if enableSalesExecutive?exists && enableSalesExecutive>
			<div class='row-fluid'>
				<div class='span5'>
					<label class="required">${uiLabelMap.BSSalesExecutive}</label>
				</div>
				<div class="span7">
					<div id="salesExecutiveId">
						<div id="salesExecutiveGrid"></div>
					</div>
				</div>
			</div>
			</#if>
			<#if enableDropShip?exists && enableDropShip>
			${screens.render("component://basesales/widget/OrderScreens.xml#NewSalesOrderDropShip")}
			</#if>
			<#if enableFacilityConsign?exists && enableFacilityConsign>
			${screens.render("component://basesales/widget/OrderScreens.xml#NewSalesOrderFacilityConsign")}
			</#if>
		</div><!-- .span12 -->
	</div><!-- .row-fluid -->
</form><!--.form-horizontal-->
${screens.render("component://basesales/widget/PartyScreens.xml#PopupNewContactMechShippingAddress")}
<div id='contextMenushippingContactMechGrid' style="display:none">
	<ul>
		<li><i class="fa fa-pencil"></i>${StringUtil.wrapString(uiLabelMap.BSEdit)}</li>
		<li><i class="fa fa-trash"></i>${StringUtil.wrapString(uiLabelMap.BSDelete)}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<#include 'script/orderNewCheckoutOptionScript.ftl'/>

<#--
var configShipToParty = {
	placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
	key: 'partyId',
	value: 'fullName',
	autoDropDownHeight: true,
	selectedIndex: 0,
	renderer: function (index, label, value) {
        var datarecord = localDataShipToParty[index];
        var table = '<table width="100%">';
        table += '<tr>';
        table += '<td width="50%">' + datarecord.partyId + '</td>';
        table += '<td width="50%">' + datarecord.fullName + '</td>';
        table += '</tr>';
        table += '</table>';
        return table;
    },
};
new OlbDropDownList($("#shipToCustomerPartyId"), localDataShipToParty, configShipToParty, []);
-->
<#--
var configShippingAddress = {
	placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
	key: 'productStoreId',
	value: 'storeName',
	autoDropDownHeight: true,
	selectedIndex: 0
};
new OlbDropDownList($("#shippingContactMechId"), localDataShippingAddress, configShippingAddress, []);
-->
<#--
<#if shoppingCart.getShippingContactMechId()?exists>
    <#assign shippingEst = shippingEstWpr.getShippingEstimate(carrierShipmentMethod)?default(-1)>
</#if>
<#if carrierShipmentMethod.partyId != "_NA_">${carrierShipmentMethod.partyId?if_exists}&nbsp;</#if>${carrierShipmentMethod.description?if_exists}
<#if shippingEst?has_content> - <#if (shippingEst > -1)><@ofbizCurrency amount=shippingEst isoCode=shoppingCart.getCurrency()/><#else>${uiLabelMap.OrderCalculatedOffline}</#if></#if>
-->
