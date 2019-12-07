<script type="text/javascript">
jQuery(document).ready( function() {
	$.validator.addMethod('validateToday',function(value,element){
		if(value){
			var today= new Date();
			today.setHours(0,0,0,0);
			return Date.parseExact(value,"dd/MM/yyyy") >= today;
		}else{
			return true;
		}
		
	},'Great than today');
	
	$('#EditSackingProposal').validate({
	
		errorElement: 'span',
		errorClass: 'help-inline red-color',
		focusInvalid: false,
		errorPlacement: function(error, element) {
			element.addClass("border-error");
    		if (element.parent() != null ){   
				element.parent().find("button").addClass("button-border");     			
    			error.appendTo(element.parent());
			}
    	  },
    	unhighlight: function(element, errorClass) {
    		$(element).removeClass("border-error");
    		$(element).parent().find("button").removeClass("button-border");
    	},
		rules:{
			dateTermination_i18n:{
				validateToday:true
			}
		},
		messages:{
			dateTermination_i18n:{
				validateToday:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}"
			}
		}
		
	});
});
	
	
</script>