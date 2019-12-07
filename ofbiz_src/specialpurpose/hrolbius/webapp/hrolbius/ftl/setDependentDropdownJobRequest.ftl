<script type="text/javascript">
jQuery(document).ready( function() {
	jQuery("${prefix_${formName}_${parentDropDown}").on("lookupIdChange", function(){
		getDependentDropdownValues('${requestName}', '${paramKey}', '${prefix}_${formName}_${parentDropDown}', '${formName}_${applicantIdList}', '${responseName}', '${dependentKeyName}', '${descName}');		
		});
	}
});
</script>