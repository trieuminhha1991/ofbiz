<script type="text/javascript">
		var setting=jQuery("#AddGeneralStandardRating").validate().settings;
			
			$.validator.addMethod("nospecialcharacter", function(value, element) {
				if(value){
					return this.optional(element) || /^[A-Za-z\u00C0-\u1EF9\s\d,\.]+$/i.test(value);
				} else 
					return true;
			}, "Letters, numbers, and underscores only please");
			
			$.extend(setting,{
				rules:{	
					standardName:{
						nospecialcharacter: true
					}
				}, 
				messages: {
					standardName:{
						nospecialcharacter: "<span style='color:red;'>${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}</span>"
					}
				}
			});
		 
</script>
