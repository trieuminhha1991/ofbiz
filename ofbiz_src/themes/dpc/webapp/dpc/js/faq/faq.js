$(document).ready(function() {
	$("#add-comment").on('click', function() {
		if (!$("#description").val()) {
			$("#description").focus();
		} else if (!$("#author").val()) {
				$("#author").focus();
			} else {
				FAQ.send($("#author").val(), $("#description").val(), $("#email").val());
			}
	});
	$("#gotoForm").on('focus', function() {
		$("#contentId").val(pid);
		$("#description").focus();
	});
});

if (typeof (FAQ) == "undefined") {
	var FAQ = (function() {
		var send = function(author, description, email) {
			DataAccess.execute({
						url: "sendFAQ",
						data: {
							contentTypeId : $("#contentType").val(),
							author: author,
							description: description,
							email: email}
						}, FAQ.reload);
		};
		var reload = function() {
			location.reload();
		};
		return {
			send: send,
			reload: reload
		}
	})();
}