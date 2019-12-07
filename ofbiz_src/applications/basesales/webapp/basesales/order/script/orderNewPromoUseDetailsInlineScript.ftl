<script type="text/javascript">
	$(function(){
		$('#recalculatePromotion').on('click', function(){
			var dataStr = $('#formRecalculatePromotion').serialize();
			if (dataStr != null) {
				$.ajax({
					type: 'POST',
					url: '${recalculateOrderPromoUrl?if_exists}',
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
		        	$('#container').empty();
		        	$('#jqxNotification').jqxNotification({ template: 'error'});
		        	$("#jqxNotification").html(errorMessage);
		        	$("#jqxNotification").jqxNotification("open");
		        } else {
		        	$('#container').empty();
		        	$('#jqxNotification').jqxNotification({ template: 'info'});
		        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
		        	$("#jqxNotification").jqxNotification("open");
		        }
		        return false;
        	} else {
        		$("#${containerRefreshId}").html(data);
        		$('#container').empty();
	        	$('#jqxNotification').jqxNotification({ template: 'info'});
	        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
	        	$("#jqxNotification").jqxNotification("open");
        		return true;
        	}
		};
		
		$("#formAddPromoCode").jqxValidator({
			rules: [
		        {
					input: "#productPromoCodeId",
					message: "${StringUtil.wrapString(uiLabelMap.BSIdIsEmpty)}",
					rule: 'required',
					action: 'none'
				},
			]
		});
		
		$('#productPromoCodeId').keypress(function(e) {
			if (e.keyCode == '13') {
				e.preventDefault();
				addPromoCodeToCart();
			}
        });
		$('#btnAddPromoCode').on('click', function(){
			addPromoCodeToCart();
		});
	});
	
	function addPromoCodeToCart() {
		if (!$("#formAddPromoCode").jqxValidator("validate")){
			return false;
		}
		var dataStr = $('#formAddPromoCode').serialize();
		if (dataStr != null) {
			$.ajax({
				type: 'POST',
				url: '${addPromoCodeUrl?if_exists}',
				data: dataStr,
				beforeSend: function(){
					$("#loader_page_common").show();
				},
				success: function(data){
					jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
								$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'error'});
					        	$("#jqxNotification").html(errorMessage);
					        	$("#jqxNotification").jqxNotification("open");
					        	return false;
							}, function(){
								$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
					        	$("#jqxNotification").jqxNotification("open");
							}, function(){
								$("#${containerRefreshId}").html(data);
				        		$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
					        	$("#jqxNotification").jqxNotification("open");
				        		return true;
							}
					);
				},
				error: function(data){
					alert("Send request is error");
				},
				complete: function(data){
					$("#loader_page_common").hide();
				},
			});
		}
	};
	
	var removePromoCode = function(promoCodeId){
		//var dataStr = "promoCode=" + promoCodeId;
		if (promoCodeId) {
			$.ajax({
				type: 'POST',
				url: 'removePromoCode',
				data: {"productPromoCodeId": promoCodeId},
				beforeSend: function(){
					$("#loader_page_common").show();
				},
				success: function(data){
					jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
								$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'error'});
					        	$("#jqxNotification").html(errorMessage);
					        	$("#jqxNotification").jqxNotification("open");
					        	return false;
							}, function(){
								$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
					        	$("#jqxNotification").jqxNotification("open");
							}, function(){
								$("#${containerRefreshId}").html(data);
				        		$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
					        	$("#jqxNotification").jqxNotification("open");
				        		return true;
							}
					);
				},
				error: function(data){
					alert("Send request is error");
				},
				complete: function(data){
					$("#loader_page_common").hide();
				},
			});
		}
	}
</script>