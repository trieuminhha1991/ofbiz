<script type="text/javascript" src="/salesresources/js/setting/createRetailStore_Confirm.js?v=0.0.2"></script>
<style>
.form-window-content-custom.content-description-left .row-fluid > label {
	width: 210px;
}
.form-window-content-custom.content-description-left .row-fluid > div:last-child {
	width: calc(100% - 215px);
}
.margin-25 {
	margin: 25px 0px;
}
.form-window-content-custom.content-description-left .row-fluid {
    border-bottom: 1px solid #ddd;
}
.legend-container hr {
    border-top: 1px solid #ccc;
}
</style>
<div class="legend-container margin-25">
    <span>${uiLabelMap.BasicInfo}</span>
    <hr/>
</div>
<div class="row-fluid" style="margin-top:-12px">
	<div class="form-window-content-custom content-description-left">
		
	<div class="span6">
		<div class="row-fluid">
			<label>${uiLabelMap.BSPSChannelId}</label>
			<div>
				<span id="wn_productStoreId_label"></span>
			</div>
		</div>
		<div class="row-fluid">
			<label>${uiLabelMap.BSPSChannelName}</label>
			<div>
				<span id="wn_storeName_label"></span>
			</div>
		</div>
		<div class="row-fluid">
			<label>${uiLabelMap.BSIncludeCustomerOtherSalesChannel}</label>
			<div>
				<span id="wn_includeOtherCustomer_label"></span>
			</div>
		</div>
		<div class="row-fluid">
			<label>${uiLabelMap.BSPayToParty}</label>
			<div>
				<span id="wn_payToPartyId_label"></span>
			</div>
		</div>
		<div class="row-fluid">
			<label>${uiLabelMap.BSSalesChannelType}</label>
			<div>
				<span id="wn_salesMethodChannelEnumId_label"></span>
			</div>
		</div>
		<div class="row-fluid">
			<label>${uiLabelMap.BSSalesChannelEnumId}</label>
			<div>
				<span id="wn_defaultSalesChannelEnumId_label"></span>
			</div>
		</div>
	</div><!-- .span6 -->
	<div class="span6">
		<div class="row-fluid">
			<label>${uiLabelMap.BSVatTaxAuthParty}</label>
			<div>
				<span id="wn_vatTaxAuthPartyId_label"></span>
			</div>
		</div>
		<div class="row-fluid">
			<label>${uiLabelMap.BSVatTaxAuthGeo}</label>
			<div>
				<span id="wn_vatTaxAuthGeo_label"></span>
			</div>
		</div>
		<div class="row-fluid">
			<label>${uiLabelMap.BSDefaultCurrencyUomId}</label>
			<div>
				<span id="wn_defaultCurrencyUomId_label"></span>
			</div>
		</div>
		<div class="row-fluid">
            <label>${uiLabelMap.BSReserveOrderEnum}</label>
            <div>
                <span id="wn_reserveOrderEnumId_label"></span>
            </div>
        </div>
        <div class="row-fluid">
			<label>${uiLabelMap.BSProdCatalogId}</label>
			<div>
				<span id="wn_pscata_prodCatalogId_label"></span>
			</div>
		</div>
	</div><!-- .span6 -->
	
	</div>
</div>
<div class="legend-container margin-25">
	<span>${uiLabelMap.BLRoles}</span>
	<hr/>
</div>
<div class="row-fluid" style="margin-top:-12px">
	<div class="form-window-content-custom content-description-left">
	
		<div class="span6">
			<div class="row-fluid">
				<label>${uiLabelMap.CommonManage}</label>
				<div>
					<span id="wn_Manager_label"></span>
				</div>
			</div>
		</div>
		<#--<div class="span6">
			<div class="row-fluid">
				<label>${uiLabelMap.BSSalesExecutive}</label>
				<div>
					<span id="wn_Salesman_label"></span>
				</div>
			</div>
		</div>-->
	
	</div>
</div>
<div class="legend-container margin-25">
	<span>${uiLabelMap.Address}</span>
	<hr/>
</div>
<div class="row-fluid" style="margin-top:-12px">
	<div class="form-window-content-custom content-description-left">
	
		<div class="span6">
			<div class="row-fluid">	
				<label>${uiLabelMap.Country}</label>
				<div>	
					<span id="countryGeoId_label"></span>
				</div>
			</div>
			<div class="row-fluid">	
				<label>${uiLabelMap.Provinces}</label>
				<div>	
					<div id="provinceGeoId_label"></div>
				</div>
			</div>
			<div class="row-fluid">	
				<label>${uiLabelMap.County}</label>
				<div>	
					<span id="districtGeoId_label"></span>
				</div>
			</div>
			<div class="row-fluid">	
				<label>${uiLabelMap.Ward}</label>
				<div>	
					<span id="wardGeoId_label"></span>
				</div>
			</div>
		</div>
		<div class="span6">
			<div class="row-fluid">	
				<label>${uiLabelMap.PhoneNumber}</label>
				<div>	
					<span id="phoneNumber_label"></span>
				</div>
			</div>
			<div class="row-fluid">	
				<label>${uiLabelMap.Address}</label>
				<div>
					<span id="address_label"></span>
				</div>
			</div>
		</div>
	
	</div>
</div>