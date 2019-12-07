<script type="text/javascript">
$(document).ready(function(){
	$(".chzn-select").chosen({
		placeholder_text_multiple: "${StringUtil.wrapString(uiLabelMap.placeholder_text_multiple)}",
		placeholder_text_single: "${uiLabelMap.placeholder_text_single}",
		no_results_text: "${uiLabelMap.no_results_text}"
	});
})
</script>
