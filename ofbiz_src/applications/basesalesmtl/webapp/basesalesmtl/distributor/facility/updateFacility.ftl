<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script src="/salesmtlresources/js/distributor/updateFacility.js"></script>
<style>
	.text-header {
		color: black !important;
	}
	.form-window-content-custom label {
	    margin-top: 0;
	}
	#description {
	    height: 60px;
    	resize: none;
	}
</style>

<div id="containerSlide"></div>
<div id="jqxNotificationNestedSlide">
	<div id="notificationContentNestedSlide"></div>
</div>
<div id="updateFacility" class="form-horizontal form-window-content-custom label-text-left content-description">
	<div id="newDistributor">
		<div class="row-fluid">
			<div class="span12 no-left-margin boder-all-profile">
				<span class="text-header">${uiLabelMap.DmsGeneralInformation}</span>
				<div class="row-fluid">
					<div class="span6">
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right">${uiLabelMap.DAFacilityId}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span7"><label class="green bold" id="FacilityId">${(facilitySelected.facilityId)?if_exists}</label></div>
						</div>
						<div class="row-fluid margin-top10">
							<div class="span5"><label class="text-right">${uiLabelMap.DADescription}&nbsp;&nbsp;&nbsp;</label></div>
							<div class="span7"><textarea id="description" tabindex="4">${(facilitySelected.description)?if_exists}</textarea></div>
						</div>
					</div>
					<div class="span6">
						<div class="row-fluid margin-top10">
				 			<div class="span5"><label class="text-right asterisk">${uiLabelMap.DAFacilityName}</label></div>
				 			<div class="span7"><input type="text" id="facilityName" tabindex="1" value="${(facilitySelected.facilityName)?if_exists}"/></div>
						</div>
						<div class="row-fluid margin-top10">
				 			<div class="span5"><label class="text-right">${uiLabelMap.DAGeoPointId}&nbsp;&nbsp;&nbsp;</label></div>
				 			<div class="span7"><div id="geoPointId" tabindex="1"></div></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span12 no-left-margin boder-all-profile">
			<span class="text-header">${uiLabelMap.BSAddress}</span>
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.DmsCountry}&nbsp;&nbsp;&nbsp;</label></div>
						<div class="span7"><div id="txtCountry" tabindex="5"></div></div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.DmsCounty}&nbsp;&nbsp;&nbsp;</label></div>
						<div class="span7"><div id="txtCounty" tabindex="7"></div></div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsAddress1}</label></div>
						<div class="span7"><input type="text" id="tarAddress" tabindex="9" value="${(postalAddress.address1)?if_exists}"/></div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.DmsProvince}&nbsp;&nbsp;&nbsp;</label></div>
						<div class="span7"><div id="txtProvince" tabindex="6"></div></div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.DmsWard}&nbsp;&nbsp;&nbsp;</label></div>
						<div class="span7"><div id="txtWard" tabindex="8"></div></div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5"><label class="text-right">${uiLabelMap.PhoneNumber}&nbsp;&nbsp;&nbsp;</label></div>
						<div class="span7">
							<input type="number" id="txtPhoneNumber" value="${(telecomNumber.contactNumber)?if_exists}"/>
							<input type="hidden" id="txtPhoneNumberId" value="${(telecomNumber.contactMechId)?if_exists}"/>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span12 margin-top10">
			<button id="btnSave" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script>
	var facilityIdParam = "${parameters.facilityId?if_exists}"
	var addressData = {
			contactMechId: "${(postalAddress.contactMechId)?if_exists}",
			countryGeoId: "${(postalAddress.countryGeoId)?if_exists}",
			stateProvinceGeoId: "${(postalAddress.stateProvinceGeoId)?if_exists}",
			districtGeoId: "${(postalAddress.districtGeoId)?if_exists}",
			wardGeoId: "${(postalAddress.wardGeoId)?if_exists}",
			geoPointId: "${(facilitySelected.geoPointId)?if_exists}"
	};
</script>