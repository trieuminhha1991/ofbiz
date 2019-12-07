var Report = (function() {
	var bindTabClick = function() {
		$("#AssignResourceTab").on("shown.bs.tab", function(e) {
			// form.jqxValidator("hide");
			var href = $(e.target).attr("href");
			switch (href) {
			case "#report":
				getContactCampaignReport();
				break;
			}
		});
	};

	var init = function() {
		bindTabClick();
	};
	return {
		init : init
	};
})();
$(document).ready(function() {

});
