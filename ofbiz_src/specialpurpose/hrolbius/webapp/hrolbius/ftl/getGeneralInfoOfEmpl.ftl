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
	        	//console.log(data);
	        	jQuery(".${field_1}").html(data['emplPosition']);
	        	jQuery(".${field_2}").html(data['departmentName']);
	        	jQuery(".${field_3}").html(data['emplRemindCount']);
	        	jQuery(".${field_4}").html(data['emplPunishmentLevel']);
	        }
		});
	});	
});
</script>