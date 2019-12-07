<script type="text/javascript">
	$(function(){
		$("#recalculatePromotion").on("click", function(){
			var dataStr = $("#formRecalculatePromotion").serialize();
			if (dataStr != null) {
				$.ajax({
					type: "POST",
					url: "${recalculateOrderPromoUrl?if_exists}",
					data: dataStr,
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						processResultInitOrder2(data);
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
					},
				});
			}
		});
		var processResultInitOrder2 = function(data){
			if (data.thisRequestUri == "json") {
				var errorMessage = "";
				if (data._ERROR_MESSAGE_LIST_ != null) {
					for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
						errorMessage += "<p><b>${uiLabelMap.BSErrorUper}</b>: " + data._ERROR_MESSAGE_LIST_[i] + "</p>";
					}
				}
				if (data._ERROR_MESSAGE_ != null) {
					errorMessage += "<p><b>${uiLabelMap.BSErrorUper}</b>: " + data._ERROR_MESSAGE_ + "</p>";
				}
				if (errorMessage != "") {
					$("#container").empty();
					$("#jqxNotification").jqxNotification({ template: "error"});
					$("#jqxNotification").html(errorMessage);
					$("#jqxNotification").jqxNotification("open");
				} else {
					$("#container").empty();
					$("#jqxNotification").jqxNotification({ template: "info"});
					$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
					$("#jqxNotification").jqxNotification("open");
				}
				return false;
			} else {
				$("#${containerRefreshId}").html(data);
				$("#container").empty();
				$("#jqxNotification").jqxNotification({ template: "info"});
				$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
				$("#jqxNotification").jqxNotification("open");
				return true;
			}
		};
	});
</script>