<script type="text/javascript">
jQuery(document).ready( function() {
	var setting=jQuery("#${formName}").validate().settings;
	jQuery.extend(setting,{
		rules:{				
			${minMaxValueField}:
			{
				number: true,
				<#if minValue?exists>
					min:${minValue},
				</#if>
				<#if maxValue?exists>
					max:${maxValue},
				</#if>
				
			}
		}, 
		messages: {
			${minMaxValueField}:
			{
				number: "${uiLabelMap.OnlyInputNumber}",	
				<#if minValue?exists>
					min:"${uiLabelMap.ValueGreateThan}: ${minValue}",
				</#if>
				<#if maxValue?exists>
					max: "${uiLabelMap.ValueLessThan}: ${maxValue}",
				</#if>
			},			
		}
	});	
});
</script>