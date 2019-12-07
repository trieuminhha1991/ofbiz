<script type="text/javascript" src="/logresources/js/logisticsCommon.js"></script>
<#assign useCss = "N"/>
<#include "component://baselogistics/webapp/baselogistics/facility/script/facilityNewFacilityAddressScript.ftl"/>


<script type="text/javascript" src="/salesresources/js/setting/createRetailStore_Information.js?v=0.0.2"></script>
<div class="legend-container">
<span>${uiLabelMap.BasicInfo}</span>
<hr/>
</div>
<div class="row-fluid">
<div class="span6 form-window-content-custom">
	<div class="row-fluid">
		<div class="span5">
			<label class="required">${uiLabelMap.BSPSChannelId}</label>
		</div>
		<div class="span7">
			<input type="text" id="wn_productStoreId" class="span12" maxlength="20" value=""/>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span5">
			<label class="required">${uiLabelMap.BSPSChannelName}</label>
		</div>
		<div class="span7">
			<input type="text" id="wn_storeName" class="span12" maxlength="100" value=""/>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span5">
			<label>${uiLabelMap.BSIncludeCustomerOtherSalesChannel}</label>
		</div>
		<div class="span7">
			<div id="wn_includeOtherCustomer"></div>
		</div>
	</div>
	<div class='row-fluid'>
		<div class='span5'>
			<label class="required">${uiLabelMap.BSPayToParty}</label>
		</div>
		<div class='span7'>
			<div id="wn_payToPartyId">
				<div id="wn_payToPartyGrid"></div>
			</div>
   		</div>
	</div>
	<div class='row-fluid'>
		<div class='span5'>
			<label class="required">${uiLabelMap.BSSalesChannelType}</label>
		</div>
		<div class='span7'>
			<div id="wn_salesMethodChannelEnumId"></div>
   		</div>
	</div>
	<div class='row-fluid'>
		<div class='span5'>
			<label class="required">${uiLabelMap.BSSalesChannelEnumId}</label>
		</div>
		<div class='span7'>
			<div id="wn_defaultSalesChannelEnumId"></div>
   		</div>
	</div>
	
</div><!-- .span6 -->
<div class="span6 form-window-content-custom">
	<div class="row-fluid">
		<div class="span5">
			<label>${uiLabelMap.BSVatTaxAuthParty}</label>
		</div>
		<div class="span7">
			<div id="wn_vatTaxAuthPartyId">
				<div id="wn_vatTaxAuthPartyGrid"></div>
			</div>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span5">
			<label>${uiLabelMap.BSVatTaxAuthGeo}</label>
		</div>
		<div class="span7">
			<div id="wn_vatTaxAuthGeoId">
				<div id="wn_vatTaxAuthGeoGrid"></div>
			</div>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span5">
			<label class="required">${uiLabelMap.BSDefaultCurrencyUomId}</label>
		</div>
		<div class="span7">
			<div id="wn_defaultCurrencyUomId"></div>
		</div>
	</div>
	<div class="row-fluid">
        <div class="span5">
            <label class="required">${uiLabelMap.BSReserveOrderEnum}</label>
        </div>
        <div class="span7">
            <div id="wn_reserveOrderEnumId"></div>
        </div>
    </div>
    <div class="row-fluid">
		<div class="span5">
			<label class="required">${uiLabelMap.BSProdCatalogId}</label>
		</div>
		<div class="span7">
			<div id="wn_pscata_prodCatalogId"></div>
		</div>
	</div>
</div><!-- .span6 -->
</div><!-- .row-fluid -->
<div class="legend-container">
	<span>${uiLabelMap.BLRoles}</span>
	<hr/>
</div>

<div class="row-fluid">
	<div class="span6 form-window-content-custom">
		<div class="row-fluid">
			<div class="span5">
				<label class="required">${uiLabelMap.CommonManageSADM}</label>
			</div>
			<div class="span7">
				<div class="container-add-plus">
					<div id="wn_Manager">
						<div id="wn_ManagerGrid"></div>
					</div>
					<a id="quickAddNewManager" tabindex="-1" class="add-value pointer"><i class="fa fa-plus"></i></a>
				</div>
			</div>
		</div>
		
		<div id="ManagerNew" style="display: none;">
			<div class="row-fluid">
				<div class="span5">
					<label class="required">${uiLabelMap.EmployeeIdShort}</label>
				</div>
				<div class="span7">
					<input type="text" id="wn_EmployeeId" class="span12" maxlength="30" />
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label class="required">${uiLabelMap.EmployeeName}</label>
				</div>
				<div class="span7">
					<input type="text" id="wn_EmployeeName" class="span12" maxlength="30" />
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label class="required">${uiLabelMap.userLoginId}</label>
				</div>
				<div class="span7">
					<input type="text" id="wn_userLoginId" class="span12" maxlength="30" />
				</div>
			</div>
		</div>
	</div>
	<#--<div class="span6 form-window-content-custom">
		<#--<div class="row-fluid">
			<div class="span5">
				<label class="required">${uiLabelMap.BSSalesExecutive}</label>
			</div>
			<div class="span7">
				<div class="container-add-plus">
					<div id="wn_Salesman">
						<div id="wn_SalesmanGrid"></div>
					</div>
					<a id="quickAddNewSalesman" tabindex="-1" class="add-value pointer"><i class="fa fa-plus"></i></a>
				</div>
			</div>
		</div>
		<#--<div id="SalesmanNew" style="display: none;">
			<div class="row-fluid">
				<div class="span5">
					<label class="required">${uiLabelMap.EmployeeIdShort}</label>
				</div>
				<div class="span7">
					<input type="text" id="wn_EmployeeId_Salesman" class="span12" maxlength="30" />
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label class="required">${uiLabelMap.EmployeeName}</label>
				</div>
				<div class="span7">
					<input type="text" id="wn_EmployeeName_Salesman" class="span12" maxlength="30" />
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label class="required">${uiLabelMap.userLoginId}</label>
				</div>
				<div class="span7">
					<input type="text" id="wn_userLoginId_Salesman" class="span12" maxlength="30" />
				</div>
			</div>
		</div>
	</div>-->
</div>

<div class="legend-container">
	<span>${uiLabelMap.Address}</span>
	<hr/>
</div>


<div class="row-fluid margin-top10" id="initFacilityAddress">
	<div class="span6 form-window-content-custom">
		<div class="row-fluid">	
			<div class="span5">
				<label class="required">${uiLabelMap.Country}</label>
			</div>
			<div class="span7">	
				<div id="countryGeoId"></div>
			</div>
		</div>
		<div class="row-fluid">	
			<div class="span5">
				<label class="required">${uiLabelMap.Provinces}</label>
			</div>
			<div class="span7">	
				<div id="provinceGeoId"></div>
			</div>
		</div>
		<div class="row-fluid">	
			<div class="span5">
				<label class="required">${uiLabelMap.County}</label>
			</div>
			<div class="span7">	
				<div id="districtGeoId"></div>
			</div>
		</div>
		<div class="row-fluid">	
			<div class="span5">
				<label class="required">${uiLabelMap.Ward}</label>
			</div>
			<div class="span7">	
				<div id="wardGeoId"></div>
			</div>
		</div>
	</div>
	<div class="span6 form-window-content-custom">
		<div class="row-fluid">	
			<div class="span5">
				<label class="required">${uiLabelMap.PhoneNumber}</label>
			</div>
			<div class="span7">	
				<input id="phoneNumber"></input>
			</div>
		</div>
		<div class="row-fluid">	
			<div class="span5">
				<label class="required">${uiLabelMap.Address}</label>
			</div>
			<div class="span7" style="text-align: left">
				<textarea id="address" name="address" data-maxlength="250" rows="4" style="resize: vertical; margin-top:0px" class="span12"></textarea>
			</div>
		</div>
	</div>
</div>