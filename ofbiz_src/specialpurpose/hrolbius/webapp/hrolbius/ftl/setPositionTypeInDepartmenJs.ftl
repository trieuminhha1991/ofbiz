<script type="text/javascript">
jQuery(document).ready( function() {	
	jQuery("#${formName}_${parentDropdownField}").change(function(){
		getDependentDropdownValues("getPositionTypeOfDept", "departmentId", "${formName}_${parentDropdownField}", 
									"${formName}_${dependentDropDownField}", "positionTypeList", "emplPositionTypeId", "description", "", "", "", "", "", "");
	});
	getDependentDropdownValues("getPositionTypeOfDept", "departmentId", "${formName}_${parentDropdownField}", 
			"${formName}_${dependentDropDownField}", "positionTypeList", "emplPositionTypeId", "description", "", "", "", "", "", "");		
})
</script>