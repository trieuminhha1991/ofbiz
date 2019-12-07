<script type="text/javascript" src="/obbresources/asset/js/jquery.barrating.min.js"></script>



<select id="rate${categoryId}${productId}" data-original="${averageRating?if_exists}">
	<option value=""></option>
	<option value="10">10</option>
	<option value="20">20</option>
	<option value="30">30</option>
	<option value="40">40</option>
	<option value="50">50</option>
</select>

<script>
	(function($) {
		$('#rate${categoryId}${productId}').barrating({
		    theme: 'fontawesome-stars',
		    readonly: true
	    });
		if ("${averageRating?if_exists}") {
			var averageRating = "${averageRating?if_exists}".replace(',', '.');
			var averageRating = (Math.round(averageRating/10))*10;
			$('#rate${categoryId}${productId}').barrating('set', averageRating);
		}
	})(jQuery);
	
</script>