<script type="text/javascript">
jQuery(document).ready( function() {
	$("#${formName}_${employeeId}").change(function(){
		getDependentDropdownValues("getPositionTypeOfEmpl", "employeeId", "${formName}_${employeeId}", 
									"${formName}_${dependenEmplPositionField}", "positionTypeList", "emplPositionTypeId", "description", "", "", "", "", "", "");
	});
	getDependentDropdownValues("getPositionTypeOfEmpl", "employeeId", "${formName}_${employeeId}", 
			"${formName}_${dependenEmplPositionField}", "positionTypeList", "emplPositionTypeId", "description", "", "", "", "", "", "");
	
	$(".chzn-select").chosen({
		search_contains: true
	}); 
	
});
</script>

