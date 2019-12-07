<script type="text/javascript">
jQuery(document).ready( function() {
	jQuery("a#ReportRecruitment").click(function(event){
		event.preventDefault();
		jQuery.ajax({
			url:"<@ofbizUrl>ReportRecruitmentSalesmanPG</@ofbizUrl>",
			success:function(data){
				if(data._ERROR_MESSAGE_){
					bootbox.dialog({
						message: "${uiLabelMap.ErrorWhenSentReport}",
						title: "${uiLabelMap.NotifyResults}",
						buttons:{
							main: {
								label: "OK!",
								className: "btn-small btn-danger"
							}
						}
					});
					return;
				}else if(data._EVENT_MESSAGE_){
					bootbox.dialog({
						message: data._EVENT_MESSAGE_,
						title: "${uiLabelMap.NotifyResults}",
						buttons:{
							main: {
								label: "OK!",
								className: "btn-small btn-danger"
							}
						}
					});
					return;
				}				
			}
		});
	});
});
</script>