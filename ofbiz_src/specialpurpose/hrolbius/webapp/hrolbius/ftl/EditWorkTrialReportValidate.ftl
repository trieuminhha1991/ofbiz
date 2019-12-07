<script type="text/javascript">
		var setting=jQuery("#EditWorkTrialReport").validate().settings;
		$.validator.addMethod("nospecialcharacter", function(value, element) {
			if(value){
				return this.optional(element) || /^[A-Za-z\u00C0-\u1EF9\s\d,\.]+$/i.test(value);
			} else 
				return true;
		}, "Letters, numbers, and underscores only please");
			
			$.extend(setting,{
				rules:{	
					eduProcess:{
						nospecialcharacter: true
					},
					workResult:{
						nospecialcharacter: true
					},
					advAndDis:
					{
						nospecialcharacter: true
					},
					workOrientation:{
						nospecialcharacter: true
					},
					eduProposal:{
						nospecialcharacter: true
					},
					policyProposal:{
						nospecialcharacter: true
					},
					workProposal:{
						nospecialcharacter: true
					}
				}, 
				messages: {
					eduProcess:{
						nospecialcharacter: "${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}"
					},
					workResult:{
						nospecialcharacter: "${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}"
					},
					advAndDis:
					{
						nospecialcharacter: "${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}"
					},
					workOrientation:{
						nospecialcharacter: "${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}"
					},
					eduProposal:{
						nospecialcharacter: "${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}"
					},
					policyProposal:{
						nospecialcharacter: "${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}"
					},
					workProposal:{
						nospecialcharacter: "${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}"
					}
				}
			});
		 
</script>
