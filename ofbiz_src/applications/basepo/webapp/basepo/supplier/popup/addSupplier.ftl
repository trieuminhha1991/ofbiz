<script type="text/javascript" src="/poresources/js/supplier/addSupplier.js?v=0.0.2"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>

<div id="alterpopupWindow" style="display:none;">	
	<div id="addSupplierPopupTitle">${uiLabelMap.POAddSupplierManager}</div>
	<div style="overflow-x: hidden;">
		<div class="row-fluid form-window-content-custom">
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class="row-fluid">
							<div class="span4"><label class="text-right">${uiLabelMap.POSupplierId}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span8"><input type="text" id="txtSupplierId" tabindex="5"/></div>
						</div>
						
						<div class="row-fluid">
							<div class="span4"><label class="text-right">${uiLabelMap.BACCTaxCode}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span8"><input type="text" id="txtTaxCode" tabindex="7"/></div>
						</div>
						
						<div class="row-fluid">
							<div class="span4"><label class="text-right asterisk">${uiLabelMap.PreferredCurrencyUomId}</label></div>
							<div class="span8"><div id="txtCurrencyUomId" tabindex="9"></div></div>
						</div>
						
						<div class="row-fluid">
							<div class="span4"><label class="text-right">${uiLabelMap.POEmailAddr}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span8"><input type="email" id="txtEmail" tabindex="11"/></div>
						</div>
					</div>
					<div class="span6">
						<div class="row-fluid">
							<div class="span4"><label class="text-right asterisk">${uiLabelMap.POSupplierName}</label></div>
							<div class="span8"><input type="text" id="txtSupplierName" tabindex="6"/></div>
						</div>
						
						<div class="row-fluid">
							<div class="span4"><label class="text-right">${uiLabelMap.BACCTaxAuthPartyId}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span8"><div id="txtTaxAuthPartyId" tabindex="8"></div></div>
						</div>
						
						<div class="row-fluid">
							<div class="span4"><label class="text-right asterisk">${uiLabelMap.DAAddress}</label></div>
							<div class="span8">
								<input type="text" id="txtAddress" tabindex="10" disabled AddressProcessor/>
							</div>
						</div>
						
						<div class="row-fluid">
							<div class="span4"><label class="text-right">${uiLabelMap.POTelecomNumber}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span8"><input type="text" id="txtTelecomNumber" tabindex="12"/></div>
						</div>
					</div>
				</div>
			</div>
			<div class="legend-container" id="finLabel">
				<span>${uiLabelMap.AccountInformation}</span>
				<hr/>
			</div>
			<div class="row-fluid" id="finDiv">
				<div class="span12">
					<div class="span6">
						<div class="row-fluid">
							<div class="span4"><label class="text-right">${uiLabelMap.BACCAccountNumber}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span8"><input type="text" id="finAccountCode" tabindex="13"/></div>
						</div>
						<div class="row-fluid">
							<div class="span4"><label class="text-right">${uiLabelMap.AtTheBank}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span8"><input type="text" id="finAccountName" tabindex="14"/></div>
						</div>
					</div>
					<div class="span6">
						<div class="row-fluid">
							<div class="span4"><label class="text-right">${uiLabelMap.BPOCountry}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span8"><div id="finAccountCountry" tabindex="15"></div></div>
						</div>
						<div class="row-fluid">
							<div class="span4"><label class="text-right">${uiLabelMap.BPOStateProvince}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span8"><div id="finAccountState" tabindex="16"></div></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class="row-fluid">
				<button id="alterCancel" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
				<button id="alterSave" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<#assign taxAuthorities = delegator.findByAnd("TaxAuthorityAndDetail", null, null, false)/>
<#assign currencyUom = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, null, true)/>


<#include "component://basepo/webapp/basepo/common/AddressProcessor.ftl"/>

<script>
	const taxAuthorities = [<#if taxAuthorities?exists><#list taxAuthorities as item>{
		taxAuthPartyId : '${item.taxAuthGeoId?if_exists}' + '|' +  '${item.taxAuthPartyId?if_exists}',
		description : '${item.groupName?if_exists}'  + ' - '  + '[${item.taxAuthPartyId?if_exists}]' 
	},</#list></#if>];
	const currencyUom = [<#if currencyUom?exists><#list currencyUom as item>{
		uomId : '${item.uomId?if_exists}',
		description : '${item.description?if_exists}' 
	},</#list></#if>];
	multiLang = _.extend(multiLang, {
		POAddSupplierManager: "${StringUtil.wrapString(uiLabelMap.POAddSupplierManager)}",
		POUpdateSupplierManager: "${StringUtil.wrapString(uiLabelMap.POUpdateSupplierManager)}",
		DmsAddAddress: "${StringUtil.wrapString(uiLabelMap.DmsAddAddress)}",
		DmsEditAddress: "${StringUtil.wrapString(uiLabelMap.DmsEditAddress)}",
	});
	<#if defaultCountryGeoId?exists>
	var defaultCountryGeoId = "${defaultCountryGeoId}";	
	</#if>
</script>
