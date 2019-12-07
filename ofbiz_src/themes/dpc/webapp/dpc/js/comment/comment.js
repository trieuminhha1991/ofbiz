$(document).ready(function() {
	$("#btnAddComment").on('click', function() {
		if (!$("#description").val()) {
			$("#description").focus();
		} else if (!$("#author").val()) {
				$("#author").focus();
			} else {
				Comments.send($("#author").val(), $("#description").val(), $("#email").val());
			}
	});
	$("#gotoForm").on('focus', function() {
		pid = pidOriginal;
		isProduct = "true";
		$("#description").focus();
	});
});

if (typeof (Comments) == "undefined") {
	var Comments = (function() {
		var send = function(author, description, email) {
			DataAccess.execute({
						url: "commentToTopic",
						data: {
							isProduct : isProduct,
							contentId : pid,
							author: author,
							description: description,
							email: email}
						}, Comments.reload);
		};
		var reply = function(contentId) {
			pid = contentId;
			isProduct = "false";
			$("body").scrollTop(1300);
			$("#description").focus();
		};
		var reload = function(res) {
			location.reload();
		};
		return {
			send: send,
			reload: reload,
			reply: reply
		};
	})();
}