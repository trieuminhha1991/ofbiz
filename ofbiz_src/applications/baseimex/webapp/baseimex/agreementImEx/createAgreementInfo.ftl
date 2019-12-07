<script>
	var customTimePeriodId = '${parameters.customTimePeriodId?if_exists}';
	var productPlanId = '${parameters.productPlanId?if_exists}';
	var partyIdToSelected = null;
	var portSelected = null;
	var orderSelected = null;
	var facilitySelected = null;
	var currencySelected = null;
	var listProductSelected = [];
	<#if parameters.productPlanId?has_content>
		<#assign planHeader = delegator.findOne("ProductPlanHeader", {"productPlanId" : parameters.productPlanId?if_exists}, true) !>
		<#if planHeader?has_content>
			<#if planHeader.supplierPartyId?has_content>
				<#assign sup = delegator.findOne("PartySupplierDetail", {"partyId" : planHeader.supplierPartyId?if_exists}, true) !>
				partyIdToSelected = {
					partyId: "${sup.partyId?if_exists}",
					partyCode: "${sup.partyCode?if_exists}",
					groupName: "${StringUtil.wrapString(sup.groupName?if_exists)}",
				};
			</#if>
		</#if>
	</#if>
	
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var quantityUomData = [];
	<#list uoms as item>
		var row = {};
		<#assign descPackingUom = StringUtil.wrapString(item.get("description", locale)) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${descPackingUom?if_exists}';
		quantityUomData[${item_index}] = row;
	</#list>
	
	<#assign wuoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list wuoms as item>
		var row = {};
		<#assign descWUom = StringUtil.wrapString(item.get("abbreviation", locale)) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${descWUom?if_exists}';
		weightUomData[${item_index}] = row;
	</#list>
	
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
	
	<#assign bankAccounts = delegator.findList("FinAccount", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", "FNACT_ACTIVE", "ownerPartyId", company)), null, null, null, false)!>
	<#if bankAccounts?has_content>
		<#assign listBackIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(bankAccounts, "bankId", true)!>
		var bankData = [];
		<#list listBackIds as bankId>
			<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : bankId?if_exists}, true) !>
			var row = {};
			row['partyId'] = '${partyGroup.partyId?if_exists}';
			row['groupName'] = '${StringUtil.wrapString(partyGroup.groupName?if_exists)}';
			bankData.push(row);
		</#list>
	</#if>
	
	var bankAccountData = [];
	<#list bankAccounts as item>
		var row = {};
		row['finAccountId'] = '${item.finAccountId?if_exists}';
		row['bankId'] = '${item.bankId?if_exists}';
		row['finAccountName'] = '${item.finAccountName?if_exists}';
		row['finAccountCode'] = '${item.finAccountCode?if_exists}';
		row['currencyUomId'] = '${item.currencyUomId?if_exists}';
		bankAccountData.push(row);
	</#list>
	
	function getUomDesc(uomId) {
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
	 
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.POSupplierId = "${StringUtil.wrapString(uiLabelMap.POSupplierId)}";
	uiLabelMap.POSupplierName = "${StringUtil.wrapString(uiLabelMap.POSupplierName)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.PlanQuantity = "${StringUtil.wrapString(uiLabelMap.PlanQuantity)}";
	uiLabelMap.orderedQuantity = "${StringUtil.wrapString(uiLabelMap.orderedQuantity)}";
	uiLabelMap.OrderQuantityEdit = "${StringUtil.wrapString(uiLabelMap.OrderQuantityEdit)}";
	uiLabelMap.unitPrice = "${StringUtil.wrapString(uiLabelMap.unitPrice)}";
	uiLabelMap.DAItemTotal = "${StringUtil.wrapString(uiLabelMap.DAItemTotal)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.OrderItemsSubTotal = "${StringUtil.wrapString(uiLabelMap.OrderItemsSubTotal)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.UpdateError = "${StringUtil.wrapString(uiLabelMap.UpdateError)}";
	uiLabelMap.MOQ = "${StringUtil.wrapString(uiLabelMap.MOQ)}";
	uiLabelMap.DAQuantityMustBeGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.DAQuantityMustBeGreaterThanZero)}";
	uiLabelMap.DmsRestrictQuantityPO = "${StringUtil.wrapString(uiLabelMap.DmsRestrictQuantityPO)}";
	uiLabelMap.PortOfDischarge = "${StringUtil.wrapString(uiLabelMap.PortOfDischarge)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.BIEPortCode = "${StringUtil.wrapString(uiLabelMap.BIEPortCode)}";
	uiLabelMap.BIEPortName = "${StringUtil.wrapString(uiLabelMap.BIEPortName)}";
	uiLabelMap.FacilityId = "${StringUtil.wrapString(uiLabelMap.FacilityId)}";
	uiLabelMap.FacilityName = "${StringUtil.wrapString(uiLabelMap.FacilityName)}";
	uiLabelMap.BIEQuota = "${StringUtil.wrapString(uiLabelMap.BIEQuota)}";
	uiLabelMap.DAOrderId = "${StringUtil.wrapString(uiLabelMap.DAOrderId)}";
	uiLabelMap.DACreateDate = "${StringUtil.wrapString(uiLabelMap.DACreateDate)}";
	uiLabelMap.DAShipAfterDate = "${StringUtil.wrapString(uiLabelMap.DAShipAfterDate)}";
	uiLabelMap.DAShipBeforeDate = "${StringUtil.wrapString(uiLabelMap.DAShipBeforeDate)}";
	uiLabelMap.BIEOrderCreateAgreementFull = "${StringUtil.wrapString(uiLabelMap.BIEOrderCreateAgreementFull)}";
	uiLabelMap.BIEYouNotYetChooseOrder = "${StringUtil.wrapString(uiLabelMap.BIEYouNotYetChooseOrder)}";
	uiLabelMap.WrongFormat= "${StringUtil.wrapString(uiLabelMap.WrongFormat)}";
</script>

<style>
.cell-green-color {
    color: black !important;
    background-color: #FFCCFF !important;
}
.cell-gray-color {
	color: black !important;
	background-color: #87CEEB !important;
}
#loader{ 
	 position : fixed;
	 left: 654px;
	 top: 266px;
	 visibility: hidden;
}
.green1 {
    color: #black;
    background-color: #deedf5;
}
.green1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
    color: black;
    background-color: #deedf5;
}
</style>
<div style="position:relative">
<div id="loader_page_common" style="overflow: hidden; position: fixed; display: none; left: 50%; top: 50%; z-index: 99998;" class="jqx-rc-all jqx-rc-all-olbius">
	<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
		<div style="float: left;">
			<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
			<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DAAddingOrder}...</span>
		</div>
	</div>
</div>
</div>
<#assign buyer = delegator.findOne("RoleType", {"roleTypeId" : "BUYER"}, true) !>
<#assign supplier = delegator.findOne("RoleType", {"roleTypeId" : "SUPPLIER"}, true) !>
<#assign localeStr = "VI" />
<#if locale = "en">
<#assign localeStr = "EN" />
</#if>

<#assign listCurrencyUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, true) />

<div>
	<div class="span12 no-left-margin boder-all-profile" id="agreementEditor">
	<input type="hidden" id="EditPurchaseAgreement2_agreementId"/>
	<input type="hidden" id="EditPurchaseAgreement2_weekETD" name="weekETD"/>
	
	
	<input type="hidden" id="EditPurchaseAgreement2_agreementTypeId" value="PURCHASE_AGREEMENT" name="agreementTypeId" />
	<input type="hidden" id="EditPurchaseAgreement2_productPlanId" name="productPlanId" value="${productPlanId?if_exists}"/>
	
	<div class="row-fluid">
		<div class="row-fluid span6" >
			<div class="span5 asterisk align-right">${uiLabelMap.SlideA}</div>
			<div class="span6" id=""><div style="display: none;" id="EditPurchaseAgreement2_partyIdFrom" name="partyIdFrom"></div><a id="partyIdFromLabel"></a></div>
		</div>
		<div class="row-fluid span5" >
			<div class="span5 asterisk align-right">${uiLabelMap.SlideB}</div>
			<div class="span6"><div id="partyIdTo" name="partyIdTo">
				<div id="jqxGridPartyIdTo"></div>
			</div></div>
		</div>
	</div>
	
	<div class="row-fluid margin-top5">
		<div class="row-fluid span6" >
			<div class="span5 align-right">${uiLabelMap.Role}</div>
			<div class="span6" id=""><input type="hidden" id="EditPurchaseAgreement2_roleTypeIdFrom" name="roleTypeIdFrom" value="${buyer.roleTypeId}" /><div name="roleTypeIdFromText" id="EditPurchaseAgreement2_roleTypeIdFromText"><a>${buyer.get('description', locale)}</a></div></div>
		</div>
		<div class="row-fluid span5" >
			<div class="span5 align-right">${uiLabelMap.Role}</div>
			<div class="span6" style="margin-top: 5px;"><input type="hidden" name="roleTypeIdTo" id="EditPurchaseAgreement2_roleTypeIdTo" value="${supplier.roleTypeId}" /><div name="roleTypeIdToText" id="EditPurchaseAgreement2_roleTypeIdToText"><a>${supplier.get('description', locale)}</a></div></div>
		</div>
	</div>
	
	<div class="row-fluid margin-top5">
		<div class="row-fluid span6" >
			<div class="span5 asterisk align-right">${uiLabelMap.Address}</div>
			<div class="span6" id=""><div name="addressIdFrom" id="EditPurchaseAgreement2_addressIdFrom"></div></div>
		</div>
		<div class="row-fluid span5" >
			<div class="span5 asterisk align-right">${uiLabelMap.Address}</div>
			<div class="span6"><div name="addressIdTo" id="EditPurchaseAgreement2_addressIdTo"></div></div>
		</div>
	</div>
	
	<div class="row-fluid margin-top5">
		<div class="row-fluid span6" >
			<div class="span5 asterisk align-right">${uiLabelMap.TelephoneNumber}</div>
			<div class="span6" id=""><div name="telephoneIdFrom" id="EditPurchaseAgreement2_telephoneIdFrom"></div></div>
		</div>
		<div class="row-fluid span5" >
			<div class="span5 asterisk align-right">${uiLabelMap.EmailAddress}</div>
			<div class="span6"><div name="emailAddressIdTo" id="EditPurchaseAgreement2_emailAddressIdTo"></div></div>
		</div>
	</div>
	
	<div class="row-fluid margin-top5">
		<div class="row-fluid span6" >
			<div class="span5 asterisk align-right">${uiLabelMap.FaxNumber}</div>
			<div class="span6" id=""><div name="faxNumberIdFrom" id="EditPurchaseAgreement2_faxNumberIdFrom"></div></div>
		</div>
		<#-- <div class="row-fluid span6" >
			<div class="span5 asterisk align-right">${uiLabelMap.EmailAddress}</div>
			<div class="span6"><input type='text' id="" /></div>
		</div> -->
		<div class="row-fluid span5" >
			<div class="span5 asterisk align-right">${uiLabelMap.PaymentCurrency}</div>
			<div class="span6"><div name="currencyUomId" id="currencyUomId"></div></div>
		</div>
	</div>
	
	<div class="row-fluid margin-top5">
		<div class="row-fluid span6" >
			<div class="span5 asterisk align-right">${uiLabelMap.BIEAgreementNumber}</div>
			<div class="span6" id="">
				<input name="agreementCode" id="agreementCode" />
			</div>
		</div>
		<div class="row-fluid span5" >
			<div class="span5 asterisk align-right">${uiLabelMap.AgreementDate}</div>
			<div class="span6" id=""><div name="agreementDate" id="EditPurchaseAgreement2_agreementDate"></div></div>
		</div>
	</div>
	<div class="row-fluid margin-top5">
		
		<div class="row-fluid span6" >
			<div class="span5 asterisk align-right">${uiLabelMap.AgreementName}</div>
			<div class="span6" id="">
				<input name="agreementName" id="EditPurchaseAgreement2_agreementName" />
			</div>
		</div>
		<div class="row-fluid span5" >
			<div class="span5 asterisk align-right">${uiLabelMap.AvailableFromDate}</div>
			<div class="span6"><div name="fromDate" id="EditPurchaseAgreement2_fromDate"></div></div>
		</div>
	</div>
	
	<div class="row-fluid margin-top5">
		<div class="row-fluid span6" >
			<div class="span5 align-right">${uiLabelMap.AccountingBank}</div>
			<div class="span6" id="">
				<div id="listBank">
				</div>
			</div>
		</div>
		<div class="row-fluid span5" >
			<div class="span5 asterisk align-right">${uiLabelMap.AvailableThruDate}</div>
			<div class="span6"><div name="thruDate" id="EditPurchaseAgreement2_thruDate"></div></div>
		</div>
	</div>
	
	<div class="row-fluid margin-top5">
		<div class="row-fluid span6" >
			<div class="span5 align-right">${uiLabelMap.BankAccount}</div>
			<div class="span6" id="">
				<div id="bankAccount">
				</div>
			</div>
		</div>
		
		<div class="row-fluid span5" >
			<div class="span5 align-right">${uiLabelMap.BIEPortExport}</div>
			<div class="span6" id="">
				<input name="exportPort" id="exportPort" />
			</div>
		</div>
	</div>
	<div class="row-fluid margin-top5">
		<div class="row-fluid span6" >
			<div class="span5 align-right">${uiLabelMap.ReceiveToFacility}</div>
			<div class="span6" id="">
				<div id="facility">
					<div id="gridFacility">
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid span5" >
			<div class="span5 align-right">${uiLabelMap.BIEPortImport}</div>
			<div class="span6" id="">
				<div id="portOfDischarge">
					<div id="gridFacilityPort">
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<#-- <div class="row-fluid">
		<div class="row-fluid span6" >
			<div class="span5 asterisk align-right">${uiLabelMap.FacilityAddress}</div>
			<div class="span6" id=""><div name="contactMechId" id="EditPurchaseAgreement2_contactMechId"></div></div>
		</div>
		<div class="row-fluid span6" >
			<div class="span5 asterisk align-right">${uiLabelMap.AllowedTransshipment}</div>
			<div class="span6"><div name="transshipment" id="EditPurchaseAgreement2_transshipment"></div></div>
		</div>
	</div> -->
	
	<#-- <div class="row-fluid">
		<div class="row-fluid span6" >
			<div class="span5 asterisk align-right">${uiLabelMap.Represented}</div>
			<div class="span6" id="">
				<input id="representFrom" value="${userLoginId}"/>
			</div>
		</div>
	</div> -->
	
	</div>
</div>

<div id="alterpopupWindowAddSupplierProduct" style="display:none;">
	<div style="font-size:18px!important;">${uiLabelMap.POAddNewProduct}</div>
	<div style="overflow: hidden;">
		<div id="contentNotificationSupplierProductExits" class="popup-notification">
		</div>
		
		<div class="row-fluid" style="margin-top:10px;">
			<div class="span4">
				<label class="asterisk" style="margin-top: 3px; float: right;">${uiLabelMap.POProduct}:</label>
			</div>
			<div class="span8">
				<div id="productId" style="width: 100%" class="">
					<div id="jqxgridProduct">
		            </div>
				</div>
			</div>
		</div>
		
		<div class="row-fluid" style="margin-top:10px;">
			<div class="span4 div-inline-block">
				<label class="asterisk"  style="margin-top: 3px; float: right;">${uiLabelMap.Quantity}:</label>
			</div>
			<div class="span8 div-inline-block">
				<div id="quantityProductId"></div>
			</div>
		</div>
		
		<div class="row-fluid" style="margin-top:10px;">
			<div class="span4 div-inline-block">
				<label class="asterisk"  style="margin-top: 3px; float: right;">${uiLabelMap.unitPrice}:</label>
			</div>
			<div class="span8 div-inline-block">
				<div id="lastPrice"></div>
			</div>
		</div>
		
		<hr style="margin: 10px; border-top: 1px solid grey;opacity: 0.4;">
		<div class="row-fluid">
        	<div class="span12">
        		<button id='alterCancel' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
        		<button id='createAndContinue' class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
        		<button id='alterSave' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
    		</div>
		</div>
	</div>
</div>

<@jqGridMinimumLib />
<#assign organizations = organizations !>

<script type="text/javascript">
	
	var listCurentcy = [
				<#list listCurrencyUom as list>
				{
					uomId: '${list.uomId}'
				},
				</#list>
	                    
	                  ];
	
	var listPartyA = [
		<#list organizations as list>
			{
				partyId: '${list.partyId}',
				groupName: '${StringUtil.wrapString(list.groupName)}'
			},
		</#list>
	];
	var listPartyB = [
	          		<#list suppliers as list>
	          			{
	          				partyId: '${list.partyId}',
	          				partyName: '${StringUtil.wrapString(list.groupName?if_exists)}'
	          			},
	          		</#list>
	          	];
	var listFacilityPort = [
		          		<#list listFacility as list>
		          			{
		          				facilityId: '${list.facilityId}',
		          				facilityName: '${StringUtil.wrapString(list.facilityName)}'
		          			},
		          		</#list>
		          	];
	var transshipment = ["Y", "N"];
	var partialShipment = ["Y", "N"];
	$('#EditPurchaseAgreement2_fromDate').jqxDateTimeInput({ width: 300, theme: theme});
	$('#EditPurchaseAgreement2_fromDate').jqxDateTimeInput('clear');
	$('#EditPurchaseAgreement2_agreementDate').jqxDateTimeInput({ width: 300, theme: theme});
	$('#EditPurchaseAgreement2_agreementDate').jqxDateTimeInput('clear');
	$('#EditPurchaseAgreement2_thruDate').jqxDateTimeInput({ width: 300, theme: theme});
	$('#EditPurchaseAgreement2_thruDate').jqxDateTimeInput('clear');
	$('#EditPurchaseAgreement2_partyIdFrom').jqxDropDownList({ source: listPartyA, width: 300, theme: theme,
		displayMember: 'groupName',
		valueMember: 'partyId'
	});
	$('#EditPurchaseAgreement2_addressIdFrom').jqxDropDownList({ source: "", selectedIndex: 0, width: 300, theme: theme, placeHolder : '${uiLabelMap.PleaseSelectTitle}'});
	$('#EditPurchaseAgreement2_addressIdTo').jqxDropDownList({ source: "", selectedIndex: 0, width: 300, theme: theme, placeHolder : '${uiLabelMap.PleaseSelectTitle}'});
// $('#EditPurchaseAgreement2_finAccountIdFroms').jqxDropDownList({ source: "",
// selectedIndex: 0, width: 300, theme: theme});
// $('#EditPurchaseAgreement2_finAccountIdTos').jqxDropDownList({ source: "",
// selectedIndex: 0, width: 300, theme: theme});
	$('#EditPurchaseAgreement2_telephoneIdFrom').jqxDropDownList({ source: "", selectedIndex: 0, width: 300, theme: theme, placeHolder : '${uiLabelMap.PleaseSelectTitle}'});
	$('#EditPurchaseAgreement2_emailAddressIdTo').jqxDropDownList({ source: "", selectedIndex: 0, width: 300, theme: theme, placeHolder : '${uiLabelMap.PleaseSelectTitle}'});
	$('#EditPurchaseAgreement2_faxNumberIdFrom').jqxDropDownList({ source: "", selectedIndex: 0, width: 300, theme: theme, placeHolder : '${uiLabelMap.PleaseSelectTitle}'});
// $('#EditPurchaseAgreement2_representPartyIdFrom').jqxDropDownList({ source:
// "", selectedIndex: 0, width: 300, theme: theme});
// $('#EditPurchaseAgreement2_portOfDischargeId').jqxDropDownList({ source:
// listFacilityPort, width: 300, theme: theme,
// displayMember: 'facilityName',
// valueMember: 'facilityId'
// });
	$("#lastPrice").jqxNumberInput({width: 300,inputMode: 'simple', spinButtons: true, theme: "olbius", decimalDigits: 2, min: 0});
	$("#quantityProductId").jqxNumberInput({width: 300,inputMode: 'simple', spinButtons: true, theme: "olbius", min: 0, decimalDigits: 0});
	$("#productId").jqxDropDownButton({width: 300, theme: theme});
	$('#productId').jqxDropDownButton('setContent', '${uiLabelMap.PleaseSelectTitle}'); 
	$('#currencyUomId').jqxDropDownList({ source: [], selectedIndex: 0, width: 300,theme: theme, valueMember: 'uomId', displayMember: 'description', placeHolder : '${uiLabelMap.PleaseSelectTitle}'})
// $('#EditPurchaseAgreement2_contactMechId').jqxDropDownList({ source: "",
// selectedIndex: 0, width: 300, theme: theme})
// $('#EditPurchaseAgreement2_transshipment').jqxDropDownList({ source:
// transshipment, width: 300, theme: theme})
// $('#EditPurchaseAgreement2_partialShipment').jqxDropDownList({ source:
// partialShipment, width: 300, theme: theme})
	$('#EditPurchaseAgreement2_agreementName').jqxInput({width: 300, height: '25', theme: theme});
	$('#agreementCode').jqxInput({width: 300, height: '25', theme: theme});
	$('#exportPort').jqxInput({width: 300, height: '25', theme: theme});
// $('#EditPurchaseAgreement2_description').jqxInput({width: 300, height:
// '25', theme: theme});
//	$('#EditPurchaseAgreement2_portOfDischargeId').jqxInput({width: 300, height: '25', theme: theme});
// $('#representFrom').jqxInput({width: 300, height: '25', theme: theme});
	
	$("#alterpopupWindowAddSupplierProduct").jqxWindow({ theme:'olbius',
        width: 500, height: 225, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
    });
	
	$('#EditPurchaseAgreement2_partyIdFrom').on('select', function (event){
		var args = event.args;
	    if (args) {
	    var index = args.index;
	    var item = args.item;
	    var label = item.label;
	    var value = item.value;
	    update({
			partyId: value,
			contactMechPurposeTypeId: "PRIMARY_LOCATION",
			}, 'getPartyContactMechs' , 'listPartyContactMechs', 'contactMechId', 'address1', 'EditPurchaseAgreement2_addressIdFrom');
		
// update({
// partyId: value,
// finAccountTypeId: "BANK_ACCOUNT",
// }, 'getPartyFinAccounts' , 'listPartyFinAccounts', 'finAccountId',
// 'finAccountCode', 'EditPurchaseAgreement2_finAccountIdFroms');
		
		update({
			partyId: value,
			contactMechPurposeTypeId: "PRIMARY_PHONE",
			}, 'getPartyTelecomNumbers' , 'listPartyTelecomNumbers', 'contactMechId', 'contactNumber', 'EditPurchaseAgreement2_telephoneIdFrom');
		
		update({
			partyId: value,
			contactMechPurposeTypeId: "FAX_NUMBER",
			}, 'getPartyTelecomNumbers' , 'listPartyTelecomNumbers', 'contactMechId', 'contactNumber', 'EditPurchaseAgreement2_faxNumberIdFrom');
		
// updateWithMultiValue({
// partyId: value,
// partyRelationshipTypeId: "REPRESENT_LEGAL",
// roleTypeIdFrom: "INTERNAL_ORGANIZATIO",
// roleTypeIdTo: "REPRESENT_LEGAL",
// }, 'getPartyRepresents' , 'listPartyRepresents', 'partyId', 'firstName',
// 'middleName', 'lastName', 'EditPurchaseAgreement2_representPartyIdFrom');
	  
	    }
	});
	
		    
// $("#EditPurchaseAgreement2_portOfDischargeId").on('select', function(event){
// var args = event.args;
// if (args) {
// var index = args.index;
// var item = args.item;
// var label = item.label;
// var value = item.value;
// update({
// facilityId: value,
// contactMechPurposeTypeId: "SHIPPING_LOCATION",
// }, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId',
// 'address1', 'EditPurchaseAgreement2_contactMechId');
// }
// });
	
	function updateWithMultiValue(jsonObject, url, data, key, value1, value2, value3, id) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	var json = res[data];
	        	renderHtmlMultiValue(json, key, value1, value2, value3, id);
	        }
	    });
	}
	function renderHtmlMultiValue(data, key, value1, value2, value3, id){
		var y = "";
		var dataSource = [];
		for (var x in data){
			var row = {};
			row[key] = data[x][key];
			row["displayValue"] = "" +data[x][value1]+ " "+data[x][value2]+" "+data[x][value3];
			dataSource.push(row);
		}
		$('#'+id).jqxDropDownList({ source: dataSource, selectedIndex: 0,
			displayMember: "displayValue",
			valueMember: key
		});
	}
	
	$(document).ready(function(){
		$('#EditPurchaseAgreement2_partyIdFrom').jqxDropDownList('selectItem', '${organizationPartyId}');
		
		var uomCurency = 'VND';
		for(var i = 0; i < listPartyA.length; i++){
			if(listPartyA[i].partyId == '${organizationPartyId}'){
				$('#partyIdFromLabel').text(listPartyA[i].groupName);
			}
		}
	});
	
	function renderToolbar(toolbar){
	 	var container = $("<div style='overflow: hidden;'></div>");
       	var myToolBar = '<div class="row-fluid">';
	 	myToolBar +='<div class="span9"></div>';
       	myToolBar += '<div class="span3">';
       	myToolBar += '<div class="span12"><a id="addProduct" style="float: right; margin-top: 10px; margin-left: 0px; margin-right: 10px;cursor: pointer;" class="icon-plus-sign open-sans" title="${uiLabelMap.addNewProduct}">${uiLabelMap.CommonAdd}</a></div>';
       	myToolBar += '</div></div>';
       	container.append(myToolBar);
       	toolbar.append(container);
       	
       	$('#addProduct').on('click', function(){
    		$("#alterpopupWindowAddSupplierProduct").jqxWindow('open');
    		loadProduct();
    	});
   	}
	
	function loadProduct(){
		listProductSoureData = [];
    	$.ajax({
			url: "loadProductPO",
			type: "POST",
			data: {},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			listProductSoureData = data["listProduct"];
			bindingDataToJqxGirdProductList(listProductSoureData);
		});
    }
	var productId="";
	var productName="";
	var productCode="";
	
	$("#alterSave").on('click', function(){
		var validate = $('#alterpopupWindowAddSupplierProduct').jqxValidator('validate');;
		if(validate == true){
			var rows=[];
			var quantityTotal = parseFloat($('#lastPrice').val()) * parseFloat($('#quantityProductId').val());
			rows.push({productId: productId, productCode: productCode, productName: productName, lastPrice: $('#lastPrice').val(), quantity: $('#quantityProductId').val(), valueTotal: quantityTotal});
			$("#listProduct").jqxGrid('addrow', null, rows, "first");
			$('#alterpopupWindowAddSupplierProduct').jqxWindow('close');
			$('#lastPrice').val("");
			$('#quantityProductId').val("");
			$('#productId').jqxDropDownButton('setContent', '${uiLabelMap.POPleaseSelect}');
		}
	});
	
	$("#createAndContinue").on('click', function(){
		var validate = $('#alterpopupWindowAddSupplierProduct').jqxValidator('validate');;
		if(validate == true){
			var rows=[];
			var quantityTotal = parseFloat($('#lastPrice').val()) * parseFloat($('#quantityProductId').val());
			rows.push({productId: productId, productCode: productCode, productName: productName, lastPrice: $('#lastPrice').val(), quantity: $('#quantityProductId').val(), valueTotal: quantityTotal});
			$("#listProduct").jqxGrid('addrow', null, rows, "first");
			$('#lastPrice').val("");
			$('#quantityProductId').val("");
			$('#productId').jqxDropDownButton('setContent', '${uiLabelMap.POPleaseSelect}');
		}
	});
	
	$('#alterpopupWindowAddSupplierProduct').on('close', function(){
		$('#alterpopupWindowAddSupplierProduct').jqxValidator('hide');
		
	});
	
	$('#agreementEditor').jqxValidator({
	    rules: [
	    			{
			       		input: '#agreementCode', 
			       		message: uiLabelMap.WrongFormat + "0-9, a-z, A-Z, _, -", 
			       		action: 'keyup, blur', 
			       		position: 'right',
			       		rule: function (input) {
			       			if (input.length > 0 ){
			       				var patt = /[^0-9a-zA-Z\_\-]/gm;
				       			var result = input.val().match(patt);
			       				if (result) return false
			       				else return true;
			       			}
			       		return true;
			       	}
			       	},
	    			{ input: '#agreementCode', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired)}', action: 'keyup', rule: 'required', position: 'right'},
					{ input: '#EditPurchaseAgreement2_agreementName', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired)}', action: 'keyup', rule: 'required', position: 'right'},
					{ input: '#EditPurchaseAgreement2_addressIdFrom', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired)}', action: 'valueChanged', rule: 'required', position: 'right',
						rule: function () {
				 		    var addressIdFrom = $('#EditPurchaseAgreement2_addressIdFrom').val();
				     	    if(addressIdFrom == ""){
				     	    	return false; 
				     	    }else{
				     	    	return true; 
				     	    }
				 	    }
					},
					{ input: '#EditPurchaseAgreement2_telephoneIdFrom', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired)}', action: 'valueChanged', rule: 'required', position: 'right',
						rule: function () {
				 		    var telephoneIdFrom = $('#EditPurchaseAgreement2_telephoneIdFrom').val();
				     	    if(telephoneIdFrom == ""){
				     	    	return false; 
				     	    }else{
				     	    	return true; 
				     	    }
				 	    }
					},
					{ input: '#EditPurchaseAgreement2_faxNumberIdFrom', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired)}', action: 'valueChanged', rule: 'required', position: 'right',
						rule: function () {
				 		    var faxNumberIdFrom = $('#EditPurchaseAgreement2_faxNumberIdFrom').val();
				     	    if(faxNumberIdFrom == ""){
				     	    	return false; 
				     	    }else{
				     	    	return true; 
				     	    }
				 	    }
					},
					{ input: '#EditPurchaseAgreement2_addressIdTo', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired)}', action: 'valueChanged', rule: 'required', position: 'right',
						rule: function () {
			     	    	var x = $('#EditPurchaseAgreement2_addressIdTo').jqxDropDownList('getSelectedItem');
		         		   	if(!x){
		             		   	return false; 
		         		   	}
		         		   	return true;
				 	    }
					},
					{ input: '#currencyUomId', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired)}', action: 'valueChanged', rule: 'required', position: 'right',
						rule: function () {
							if (currencySelected == null) return false;
							return true;
				 	    }
					},
					{ input: '#EditPurchaseAgreement2_fromDate', message: '${StringUtil.wrapString(uiLabelMap.POShipByDateInvalid)}', action: 'valueChanged', position: 'right',
			         	   rule: function () {
			         		   if($('#EditPurchaseAgreement2_fromDate').jqxDateTimeInput('getDate') != null && $('#EditPurchaseAgreement2_thruDate').jqxDateTimeInput('getDate') != null){
				         		   var shipBeforeDate = $('#EditPurchaseAgreement2_fromDate').jqxDateTimeInput('getDate').getTime(); 
				         		   var shipAfterDate = $('#EditPurchaseAgreement2_thruDate').jqxDateTimeInput('getDate').getTime();
				             	   if(shipBeforeDate < shipAfterDate){
				             		   return true; 
				             	   }else{
				             		   return false; 
				             	   }
			         		   }
			         	    }
					},
					{ input: '#EditPurchaseAgreement2_thruDate', message: '${StringUtil.wrapString(uiLabelMap.POShipByDateInvalid)}', action: 'valueChanged', position: 'right',
			         	   rule: function () {
			         		   if($('#EditPurchaseAgreement2_fromDate').jqxDateTimeInput('getDate') != null && $('#EditPurchaseAgreement2_thruDate').jqxDateTimeInput('getDate') != null){
				         		   var shipBeforeDate = $('#EditPurchaseAgreement2_fromDate').jqxDateTimeInput('getDate').getTime(); 
				         		   var shipAfterDate = $('#EditPurchaseAgreement2_thruDate').jqxDateTimeInput('getDate').getTime();
				             	   if(shipBeforeDate < shipAfterDate){
				             		   return true; 
				             	   }else{
				             		   return false; 
				             	   }
			         		   }
			         	    }
					},
					{ input: '#EditPurchaseAgreement2_agreementDate', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired)}', action: 'valueChanged', position: 'right',
			         	   rule: function () {
			         		   if(!$('#EditPurchaseAgreement2_agreementDate').jqxDateTimeInput('getDate')){
			             		   return false; 
			         		   }
			         		   return true;
			         	    }
					},
					{ input: '#EditPurchaseAgreement2_agreementDate', message: '${StringUtil.wrapString(uiLabelMap.BIEAgreementDateMustBeforeAgreementEffectiveDate)}', action: 'valueChanged', position: 'right',
			         	   rule: function () {
			         		   if($('#EditPurchaseAgreement2_fromDate').jqxDateTimeInput('getDate') != null && $('#EditPurchaseAgreement2_agreementDate').jqxDateTimeInput('getDate') != null){
				         		   var agreDate = $('#EditPurchaseAgreement2_agreementDate').jqxDateTimeInput('getDate').getTime(); 
				         		   var fromDate = $('#EditPurchaseAgreement2_fromDate').jqxDateTimeInput('getDate').getTime();
				             	   if (agreDate <= fromDate){
				             		   return true; 
				             	   } else{
				             		   return false; 
				             	   }
			         		   }
			         		   return true;
			         	    }
					},
					{ input: '#EditPurchaseAgreement2_fromDate', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired)}', action: 'valueChanged', position: 'right',
			         	   rule: function () {
			         		   if(!$('#EditPurchaseAgreement2_fromDate').jqxDateTimeInput('getDate')){
			             		   return false; 
			         		   }
			         		   return true;
			         	    }
					},
					{ input: '#EditPurchaseAgreement2_thruDate', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired)}', action: 'valueChanged', position: 'right',
			         	   rule: function () {
			         		   if(!$('#EditPurchaseAgreement2_thruDate').jqxDateTimeInput('getDate')){
			             		   return false; 
			         		   }
			         		   return true;
			         	    }
					},
					{ input: '#EditPurchaseAgreement2_emailAddressIdTo', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired)}', action: 'valueChanged', position: 'right',
			         	   rule: function () {
		         	   			var x = $('#EditPurchaseAgreement2_emailAddressIdTo').jqxDropDownList('getSelectedItem');
			         		   	if(!x){
			             		   	return false; 
			         		   	}
			         		   	return true;
			         	    }
					},
					{ input: '#partyIdTo', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired)}', action: 'valueChanged', position: 'right',
			         	   rule: function () {
			         		   if(partyIdToSelected == null){
			             		   return false; 
			         		   }
			         		   return true;
			         	    }
					} 
					
			]
	});
	
	$('#alterpopupWindowAddSupplierProduct').jqxValidator({
	    rules: [
					{ input: '#quantityProductId', message: '${StringUtil.wrapString(uiLabelMap.POCheckGreaterThan)}', action: 'valueChanged', position: 'right',
						rule: function (input, commit) {
							var value = $("#quantityProductId").val();
							if (value > 0) {
								return true;
							}
							return false;
						}
					},
					{ input: '#lastPrice', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired)}', action: 'valueChanged', position: 'right',
	                	rule: function (input, commit) {
	                		var value = $("#lastPrice").val();
	                		if (value >= 0) {
	                			return true;
	                		}
	                		return false;
	                	}
	                },
					{ input: '#productId', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired)}', action: 'valueChanged', position: 'right',
	                	rule: function (input, commit) {
	                		var value = $("#productId").val();
	                		var invalid = '${StringUtil.wrapString(uiLabelMap.POPleaseSelect)}';
	                		if (value != invalid) {
	                			return true;
	                		}
	                		return false;
	                	}
	                },
			]
	});
	
	function update(jsonObject, url, data, key, value, id) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	var json = res[data];
	            renderHtml(json, key, value, id);
	        }
	    });
	}
	function renderHtml(json, key, value, id){
		$('#'+id).jqxDropDownList({ source: json, selectedIndex: 0,
			displayMember: value,
			valueMember: key
		});
	}
</script>