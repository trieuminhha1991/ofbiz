<script type="text/javascript">
jQuery(document).ready(function() {
    if (jQuery('#${dependentForm}').length) {
      jQuery("#${PermtRes_mainId}").change(function(e, data) {
          getDependentDropdownValues('${requestName}', '${paramKey}', '${PermtRes_mainId}', '${PermtRes_stateProvinceGeoId}', '${responseName}', '${dependentKeyName}', '${descName}', '${permanentResidenceState?if_exists}');
      });
      <#if postalAddressPermanent?exists>
      	<#assign district = postalAddressPermanent.districtGeoId?if_exists>
      	<#assign ward = postalAddressPermanent.wardGeoId?if_exists>
      </#if>
      jQuery("#${PermtRes_stateProvinceGeoId}").change(function(e, data) {
          getDependentDropdownValues('${requestName_district}', '${paramKey_district}', '${PermtRes_stateProvinceGeoId}', '${PermtRes_distGeoId}', '${responseName_district}', '${dependentKeyName}', '${descName}', '${district?if_exists}');
      });
	 	     
      jQuery("#${PermtRes_distGeoId}").change(function(e, data) {
          getDependentDropdownValues('${requestName_ward}', '${paramKey_ward}', '${PermtRes_distGeoId}', '${PermtRes_wardGeoId}', '${responseName_ward}', '${dependentKeyName}', '${descName}', '${ward?if_exists}');
      });
      
      
      jQuery("#${currRes_mainId}").change(function(e, data) {
          getDependentDropdownValues('${requestName}', '${paramKey}', '${currRes_mainId}', '${currRes_stateProvinceGeoId}', '${responseName}', '${dependentKeyName}', '${descName}', '${currentResidenceState?if_exists}');
      });
      
      <#if currentResidence?exists>
      	<#assign district2 = currentResidence.districtGeoId?if_exists>
    	<#assign ward2 = currentResidence.wardGeoId?if_exists>
      </#if>
      
      jQuery("#${currRes_stateProvinceGeoId}").change(function(e, data) {
          getDependentDropdownValues('${requestName_district}', '${paramKey_district}', '${currRes_stateProvinceGeoId}', '${currRes_distGeoId}', '${responseName_district}', '${dependentKeyName}', '${descName}', '${district2?if_exists}');
      });
      
      jQuery("#${currRes_distGeoId}").change(function(e, data) {
          getDependentDropdownValues('${requestName_ward}', '${paramKey_ward}', '${currRes_distGeoId}', '${currRes_wardGeoId}', '${responseName_ward}', '${dependentKeyName}', '${descName}', '${ward2?if_exists}');
      });
      <#if permanentResidenceState?has_content>
      	<#assign selectedDependentOption = permanentResidenceState> 
      </#if>
      
      getDependentDropdownValues('${requestName}', '${paramKey}', '${PermtRes_mainId}', '${PermtRes_stateProvinceGeoId}', '${responseName}', '${dependentKeyName}', '${descName}', '${selectedDependentOption}');
    
      <#if currentResidenceState?has_content>
  		<#assign selectedDependentOption = currentResidenceState> 
      </#if>     
      getDependentDropdownValues('${requestName}', '${paramKey}', '${currRes_mainId}', '${currRes_stateProvinceGeoId}', '${responseName}', '${dependentKeyName}', '${descName}', '${selectedDependentOption}');
    }
})
</script>