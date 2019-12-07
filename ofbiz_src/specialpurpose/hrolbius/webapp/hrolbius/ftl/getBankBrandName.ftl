<script type="text/javascript">
jQuery(document).ready( function() {	
	jQuery("#${formName}_${parentDropdownField}").change(function(){
		getDependentDropdownValues("getBankBrandName", "bankId", "${formName}_${parentDropdownField}", 
									"${formName}_${dependentDropDownField}", "listBankBrandName", "bankBrandId", "brandName", "", "", "", "", "", "");
	});
	getDependentDropdownValues("getBankBrandName", "bankId", "${formName}_${parentDropdownField}", 
			"${formName}_${dependentDropDownField}", "listBankBrandName", "bankBrandId", "brandName", "", "", "", "", "", "");		
})
</script>