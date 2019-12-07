<script type="text/javascript">
	jQuery("input[name='${lookupName}']").bind("lookupIdChange", function(event){
		var partyId = jQuery("input[name='${lookupName}']").val();
		if(partyId){
			getEmplPositionInfo(partyId);	
		}
	});
	jQuery(function() {
		var emplId = jQuery("input[name='${lookupName}']").val();
		if(emplId){
			getEmplPositionInfo(emplId);
		}
	});
	function getEmplPositionInfo(partyId){
		jQuery.ajax({
			url:"<@ofbizUrl>getEmplPosition</@ofbizUrl>",
			type: "POST",
			data:{partyId: partyId},
			success: function(data){
				if(data._EVENT_MESSAGE_){
					jQuery("#${formName}_${fieldName}_title").parent().siblings("div.controls").html(data.emplPositionTypeDesc);
					jQuery("#${formName}_emplPositionId").val(data.emplPositionId);						
				}else{
					jQuery("#${formName}_${fieldName}_title").parent().siblings("div.controls").html(data._ERROR_MESSAGE_);
				}
			},
			error: function(error){
						
			}
		});
	}
</script>