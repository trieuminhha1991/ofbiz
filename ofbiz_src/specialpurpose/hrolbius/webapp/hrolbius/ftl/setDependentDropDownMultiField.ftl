<script type="text/javascript">
jQuery(document).ready(function() {
    if (jQuery('#${dependentForm}').length) {
    	var temp = {};
    	<#list paramMap.entrySet() as entry>
    		temp["${entry.key}"] = "${entry.value}";
    	</#list>
    	
    	jQuery("#${dependentForm}_${mainId}").change(function(e, data) {
          getDependentDropdownValuesCustom('${requestName}', temp, '${dependentForm}_${dependentId}', '${responseName}', '${dependentKeyName}', '${descName}');
      });
      getDependentDropdownValuesCustom('${requestName}', temp, '${dependentForm}_${dependentId}', '${responseName}', '${dependentKeyName}', '${descName}', '${selectedDependentOption}');
    }
})
</script>