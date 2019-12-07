jQuery(document).ready(function($) {
	var price = $(".pricetag");
	for (var x = 0; x < price.length; x++) {
		(function(x) {
			$(price[x]).click(function() {
				if (catId) {
					var url = "?catId=" + catId;
					var low = $(price[x]).attr('data-low');
					var high = $(price[x]).attr('data-high')
					if (low && high) {
						url += "&sb=price&vl=" + low + "-" + high;
					}
					window.location.href = "productCategoryList" + url;
				} else if (catalogId) {
					var url = "?catalogId=" + catalogId;
					var low = $(price[x]).attr('data-low');
					var high = $(price[x]).attr('data-high')
					if (low && high) {
						url += "&sb=price&vl=" + low + "-" + high;
					}
					window.location.href = "productCategoryList" + url;
				}

			});
		})(x);
	}
	
	var brands = $(".brandtag");
	for (var x = 0; x < brands.length; x++) {
		(function(x) {
			$(brands[x]).click(function() {
				if (catId) {
					var url = "?catId=" + catId;
					var brand = $(brands[x]).attr('data-brand');
					if (brand) {
						url += "&brand=" + brand;
					}
					window.location.href = "productCategoryList" + url;
				} else if (catalogId) {
					var url = "?catalogId=" + catalogId;
					var brand = $(brands[x]).attr('data-brand');
					if (brand) {
						url += "&brand=" + brand;
					}
					window.location.href = "productCategoryList" + url;
				}

			});
		})(x);
	}
});
