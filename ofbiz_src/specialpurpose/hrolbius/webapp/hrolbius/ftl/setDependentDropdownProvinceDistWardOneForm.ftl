<script type="text/javascript">
jQuery(document).ready(function() {
    if (jQuery('#${dependentForm}').length) {
      jQuery("#${mainId}").change(function(e, data) {
          getDependentDropdownValues('${requestName}', '${paramKey}', '${mainId}', '${stateProvinceGeoId}', '${responseName}', '${dependentKeyName}', '${descName}');
      });
      
      jQuery("#${stateProvinceGeoId}").change(function(e, data) {
          getDependentDropdownValues('${requestName_district}', '${paramKey_district}', '${stateProvinceGeoId}', '${distGeoId}', '${responseName_district}', '${dependentKeyName}', '${descName}');
      });
      
      jQuery("#${distGeoId}").change(function(e, data) {
          getDependentDropdownValues('${requestName_ward}', '${paramKey_ward}', '${distGeoId}', '${wardGeoId}', '${responseName_ward}', '${dependentKeyName}', '${descName}');
      });
      
      getDependentDropdownValues('${requestName}', '${paramKey}', '${mainId}', '${stateProvinceGeoId}', '${responseName}', '${dependentKeyName}', '${descName}', '${selectedDependentOption}');
    }
})
</script>