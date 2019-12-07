$(document).ready(function() {
	ScrollDetector.init();
});
if (typeof (ScrollDetector) == "undefined") {
	var ScrollDetector = (function($) {
		var handleEvents = function() {
			$(window).scroll(function() {
				$("#product-nav").removeClass("freeze");
					if ($(window).scrollTop() >= $("#product-nav").offset().top && $("#product-content").height() > ($(window).scrollTop() - $(window).height())) {
						$("#product-nav").addClass("freeze");
				    }
			});
		};
		return {
			init: function() {
				handleEvents();
			}
		}
	})(jQuery);
}