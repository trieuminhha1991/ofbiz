jQuery(document).ready(function($) {
	var list = $('#list-supplier li');
	for ( x = 0; x < list.length; x++) {
		(function(x) {
			getAvatar($(list[x]).attr("data-id"), $(list[x]));
			getMutual($(list[x]).attr("data-id"), $(list[x]));
			$(list[x]).on('fb:mutual', function(e, res) {
				if (res.context) {
					var str = "<span>(" + res.context.mutual_friends.summary.total_count + " Bạn chung)</span>";
					$(list[x]).find('.fb-friend-name').append(str);
				}
			});
			$(list[x]).on('fb:avatar', function(e, res) {
				if (res) {
					var str = "<img src='" + res + "' />";
					$(list[x]).find('.fb-friend-avatar').html(str);
				}
			});
		})(x);
	}
});
