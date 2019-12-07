<script type="text/javascript">
	$(function(){
		$("#view-more-note-ship-info").on("click", function() {
			if ($("#ship-info-container").hasClass("active")) {
				$("#view-more-note-ship-info i").removeClass("fa-compress");
				$("#view-more-note-ship-info i").addClass("fa-expand");
				$("#ship-info-container").removeClass("active");
				$("#ship-info-container").height('155');
			} else {
				$("#view-more-note-ship-info i").removeClass("fa-expand");
				$("#view-more-note-ship-info i").addClass("fa-compress");
				$("#ship-info-container").addClass("active");
				$("#ship-info-container").height($("#ship-info-container table").height());
			}
		});
		$("td.item-comment-td").on("dblclick", function(){
			var parent = $(this).closest(".item-comment-td");
			if (typeof(parent) != "undefined") {
				var editBox = $(parent).find(".item-comment-e");
				if (typeof(editBox) != "undefined") {
					$(editBox).show();
					$(editBox).find("input").focus();
				}
			}
		});
	});
	function cancelComment(index){
		var viewBox = $("#item-comment-c-" + index + " .item-comment-r");
		if (typeof(viewBox) != "undefined") $(viewBox).show();
		var editBox = $("#item-comment-c-" + index + " .item-comment-e");
		if (typeof(editBox) != "undefined") $(editBox).hide();
	}
	function updateComment(index, cartLine){
		var commentVal = $("#comments_" + index).val();
		$.ajax({
			type: 'POST',
			url: 'updateCartItemComment',
			data: {
				itemComment: commentVal,
				cartLine: cartLine
			},
			beforeSend: function(){
				$("#loader_page_common").show();
			},
			success: function(data){
				processResultUpdateComment(index, data);
			},
			error: function(data){
				alert("Send request is error");
			},
			complete: function(data){
				$("#loader_page_common").hide();
			},
		});
	};
	var processResultUpdateComment = function(index, data){
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
	        	var newComment = data.newComment;
	        	$("#comments_" + index).val(newComment);
	        	$("#item-comment-c-" + index + " .item-comment-r").text(newComment);
				cancelComment(index);
	        	
	        	$('#container').empty();
	        	$('#jqxNotification').jqxNotification({ template: 'info'});
	        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
	        	$("#jqxNotification").jqxNotification("open");
	        }
	        return false;
    	} else {
    		$("#step2").html(data);
    		$('#container').empty();
        	$('#jqxNotification').jqxNotification({ template: 'info'});
        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
        	$("#jqxNotification").jqxNotification("open");
    		return true;
    	}
	};
	function updateDesireAlternateGwpProductCart(alternateGwpProductId, alternateGwpLine) {
		$.ajax({
			type: 'POST',
			url: 'setDesiredAlternateGwpProductIdUpdateAjax',
			data: {
				alternateGwpProductId: alternateGwpProductId,
				alternateGwpLine: alternateGwpLine
			},
			beforeSend: function(){
				$("#loader_page_common").show();
			},
			success: function(data){
				processResultUpdateAfterDesireGwp(data);
			},
			error: function(data){
				alert("Send request is error");
			},
			complete: function(data){
				$("#loader_page_common").hide();
			},
		});
	};
	var processResultUpdateAfterDesireGwp = function(data){
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
    		$("#${containerRefreshId?if_exists}").html(data);
    		$('#container').empty();
        	$('#jqxNotification').jqxNotification({ template: 'info'});
        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
        	$("#jqxNotification").jqxNotification("open");
    		return true;
    	}
	};
</script>