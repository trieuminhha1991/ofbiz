jQuery(document).ready(function() {
	//popup();
});
var popup = function() {
	var checkLiked = function(response) {
		fbId = response.authResponse.userID;
		jQuery('.fb-login-bt').css('display', 'none');
		/*FB.api("/me/likes", function(response) {
		 if (response && !response.error) {
		 var data = response.data;
		 for (var x in data) {
		 if (data[x].id == pageId) {
		 hidePopup();
		 }
		 }
		 }
		 });*/
	};
	var init = function() {
		jQuery('.fb-back').css('display', 'block');
		FB.getLoginStatus(function(response) {
			// console.log(response.authResponse.userID);
			if (response.status === 'connected') {
				fbId = response.authResponse.userID;
				checkLiked(response);
			}
		});
		FB.Event.subscribe('auth.login', function(response) {
			if (response.status === 'connected') {
				fbId = response.authResponse.userID;
				checkLiked(response);
			}

		});
		FB.Event.subscribe('edge.create', function(response) {
			if (response == location.href) {
				addInteraction('like');
			}
			/*else {
			 hidePopup();
			 }*/
		});
	};
	if (!FB) {
		jQuery("#fb-root").on('fb:init', function(e, obj) {
			FB = obj;
			init();
		});
	} else {
		//init();
	}
};
