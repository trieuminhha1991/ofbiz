<div id="alterpopupWindowContactMechNew" style="display:none">
	<div>${uiLabelMap.BSCreateNewShippingAddress}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<input type="hidden" id="wn_partyId" value="">
	        <input type="hidden" id="wn_contactMechTypeId" value="POSTAL_ADDRESS">
	        <input type="hidden" id="wn_contactMechPurposeTypeId" value="SHIPPING_LOCATION">
			<div id="containerContactMech" style="background-color: transparent; overflow: auto;"></div>
		    <div id="jqxNotificationContactMech" style="margin-bottom:5px">
		        <div id="notificationContentContactMech">
		        </div>
		    </div>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_toName">${uiLabelMap.BSPartyReceive}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_toName" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_attnName">${uiLabelMap.BSOtherInfo}</label><#--DAOtherName-->
						</div>
						<div class='span7'>
							<input type="text" id="wn_attnName" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_countryGeoId" class="required">${uiLabelMap.BSCountry}</label>
						</div>
						<div class='span7'>
							<div id="wn_countryGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSStateProvince}</label>
						</div>
						<div class='span7'>
							<div id="wn_stateProvinceGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_countyGeoId" class="required">${uiLabelMap.BSCounty}</label>
						</div>
						<div class='span7'>
							<div id="wn_countyGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_wardGeoId" class="required">${uiLabelMap.BSWard}</label>
						</div>
						<div class='span7'>
							<div id="wn_wardGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required" for="wn_address1">${uiLabelMap.BSAddress}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_address1" class="span12" maxlength="255" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_postalCode">${uiLabelMap.BSZipCode}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_postalCode" class="span12" maxlength="60" value=""/>
				   		</div>
					</div>
					<div class='row-fluid' style="display:none">
						<div class='span5'>
							<label for="wn_allowSolicitation">${uiLabelMap.BSContactAllowSolicitation}?</label>
						</div>
						<div class='span7'>
							<div id="wn_allowSolicitation"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required" for="wn_isPrimaryLocation">${uiLabelMap.BSUseToPrimaryLocation} ?</label>
						</div>
						<div class='span7'>
							<div id="wn_isPrimaryLocation"></div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<div style="position:relative">
	<div id="loader_page_common_nctm" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<#include '../party/contactMechEditPopup.ftl'/>
<#include "script/contactNewShippingAddressPopupScript.ftl"/>
