if (typeof (SlidePreview) == "undefined") {
	var SlidePreview = (function() {
		var render = function(data) {
			var indicators = "";
			var carousel = "";
			for ( var x in data) {
				if (x == 0) {
					indicators += "<li class='active' data-slide-to='" + x + "' data-target='#myCarousel'></li>";
					carousel += "<div class='item active'><img width='100%' height='100%' title='" + data[x].description + "' alt='" + data[x].description + "' src='" + data[x].originalImageUrl + "'></div>";
				} else {
					indicators += "<li data-slide-to='" + x + "' data-target='#myCarousel'></li>";
					carousel += "<div class='item'><img width='600' height='400' title='" + data[x].description + "' alt='" + data[x].description + "' src='" + data[x].originalImageUrl + "'></div>";
				}
			}
			$("#indicatorsPreview").html(indicators);
			$("#carouselPreview").html(carousel);
		}
		return {
			render: render
		};
	})();
}