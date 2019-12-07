<script type="text/javascript">
jQuery(document).ready( function() {
	jQuery("#${formName}_${parentDropdownField}").change(function(){
		//alert("1");
		jQuery.ajax({
			url: "<@ofbizUrl>getGeneralInfoOfEmpl</@ofbizUrl>",
			async: false,
	        type: 'POST',
	        data:[{
	        	name: 'partyId',
	        	value: jQuery("#${formName}_${parentDropdownField}").val()
	        }],
	        success: function(data){
	        	jQuery(".${field_1}").html(data['emplRemindCount']);
	        	jQuery(".${field_2}").html(data['emplPunishmentLevel']);
	        }
		});
	});	
});
</script>