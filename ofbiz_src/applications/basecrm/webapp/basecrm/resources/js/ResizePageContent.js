$(document).ready(function() {
	PageContentHeight.resize();
});
if (typeof (PageContentHeight) == "undefined") {
	var PageContentHeight = (function($) {
		var resizePageContentHeight = function(){
			var paddT = $('#page-content').innerHeight() - $('#page-content').height();
			var height = $(window).height() - $('#nav').height() - $('.breadcrumbs').height() - paddT;
			$('#page-content').height(height);
		};
		var resize = function() {
			resizePageContentHeight();
			$(window).resize(function(){
				resizePageContentHeight();
			});
		};
		return {
			resize: resize
		}
	})(jQuery);
}