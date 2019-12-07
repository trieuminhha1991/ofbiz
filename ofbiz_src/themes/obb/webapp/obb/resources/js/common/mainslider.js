var MainSlider = (function($){
	var init = function(){
		$('#MainSlider').flexslider({
	        animation: "fade",
	        randomize: true,
	        start: function(slider){
	        }
      });
	};
	return {
		init: init
	};
})(jQuery);
jQuery(document).ready(function($){
	MainSlider.init();
});
