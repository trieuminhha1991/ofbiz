<div id="HealthCareProvider" class="modal hide fade" tabindex="-1">
	<div class="modal-header no-padding">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			${uiLabelMap.EditHealthCareProvider}
		</div>
	</div>	
<div class="modal-body no-padding">
		<form action="<@ofbizUrl>updateHealthCareProvider</@ofbizUrl>" id="HealthCareProviderForm" name="HealthCareProviderForm" method="post">
			<input type="hidden" name="partyId" id="partyIdEdit">
			<input type="hidden" name="contactMechId" id="contactMechId">
			<div class="row-fluid form-horizontal">
				<div class="control-group">
					<label class="control-label">
						<label for="groupNameEdit" class="asterisk" id="">${uiLabelMap.HealthCareProviderName}</label>  
					</label>
					<div class="controls">
						<input type="text" id="groupNameEdit" name="groupName">							
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">
						<label for="countryGeoIdEdit" class="asterisk" id="">${uiLabelMap.HRCommonNational}</label>  
					</label>
					<div class="controls">
						<select id="countryGeoIdEdit" name="countryGeoId">
							<#list geoList as geo>
								<option value="${geo.geoId}">${geo.geoId}: ${geo.geoName}</option>
							</#list>
						</select>							
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">
						<label for="stateProvinceGeoIdEdit" class="asterisk" id="">${uiLabelMap.CommonState}/${uiLabelMap.CommonCity}</label>  
					</label>
					<div class="controls">
						<select id="stateProvinceGeoIdEdit" name="stateProvinceGeoId">
							
						</select>							
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">
						<label>&nbsp;</label>  
					</label>
					<div class="controls">
						<button class="btn btn-small btn-primary" data-dismiss="modal" id="HealthCareProviderBtn" type="submit">
							<i class="icon-ok"></i>
							${uiLabelMap.CommonSubmit}
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
<script type="text/javascript">	
	jQuery(document).ready(function() {
		jQuery("#countryGeoIdEdit").change(function(e, data) {
		    getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'countryGeoIdEdit', 'stateProvinceGeoIdEdit', 'stateList', 'geoId', 'geoName');
		});
		//getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'countryGeoId', 'stateProvinceGeoId', 'stateList', 'geoId', 'geoName');
		jQuery("#HealthCareProviderBtn").click(function(){
			jQuery("#HealthCareProviderForm").submit();
		});
	});
		
	function editPartyHelthCareProvider(partyId, contactMechId, groupName, countryGeoId, stateProvinceGeoId){
		
		jQuery("#groupNameEdit").val(groupName);
	
		jQuery("#partyIdEdit").val(partyId);
	
		jQuery("#contactMechId").val(contactMechId);
		
		if(countryGeoId){
			jQuery("#countryGeoIdEdit option[value='"+ countryGeoId +"']").attr('selected', 'selected');
		}else{
			jQuery("#countryGeoIdEdit option[value='${defaultCountryGeoId}'").attr('selected', 'selected');
		}
		getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'countryGeoIdEdit', 'stateProvinceGeoIdEdit', 'stateList', 'geoId', 'geoName', stateProvinceGeoId);
		jQuery("#HealthCareProvider").modal('show');
	}
</script>