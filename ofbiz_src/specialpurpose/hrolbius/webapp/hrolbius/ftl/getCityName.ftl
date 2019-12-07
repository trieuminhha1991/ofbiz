<script type="text/javascript">
jQuery(document).ready( function() {	
	jQuery("#${formName}_${parentCountry}").change(function(){
		getDependentDropdownValues("getCityName", "countryGeoId", "${formName}_${parentCountry}", 
									"${formName}_${cityName}", "listCityName", "geoIdTo", "geoName", "", "", "", "", "", "");
	});
	getDependentDropdownValues("getCityName", "countryGeoId", "${formName}_${parentCountry}", 
			"${formName}_${cityName}", "listCityName", "geoIdTo", "geoName", "", "", "", "", "", "");		
})
</script>