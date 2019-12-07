var listPageId = ['1460174284271795', '720790364659960', '501903769877785', '388430071207963'];
var index_post = 0;
jQuery(document).ready(function($) {
	jQuery.noConflict();
	jQuery('.flexslider').flexslider({
		controlNav : false
	});
	$('#sidebarScroll').perfectScrollbar({
		suppressScrollX : true
	});
	$('#list-supplier').perfectScrollbar({
		suppressScrollX : true
	});
	$('.fb-post').perfectScrollbar({
		suppressScrollX : true
	});
	$(".readmore").click(function(){
		if($(this).attr('data-status') == "false"){
			$(this).text("Thu gọn");
			$(this).attr('data-status', "true");
			$(this).prev().html($(this).attr('data-msg'));

		}else{
			$(this).text("Xem thêm");
			$(this).attr('data-status', "false");
			$(this).prev().html($(this).attr('data-abs'));
		}
	});
	/*if (!FB) {
	 $('#fb-root').on('fb:init', function(e, obj) {
	 FB = obj;
	 getPost();
	 getPageInfo(listPageId);
	 intervalGetPost();
	 });
	 } else {
	 getPost();
	 getPageInfo(listPageId);
	 intervalGetPost();
	 }*/
	/*$('iframe').load(function() {
	 $('iframe').contents().find("head").append($("<style type='text/css'>  ._5v3q ._4-eo._9_q, ._9_q{background:none;}  </style>"));
	 });*/
});

function intervalGetPost() {
	var timer = 5 * 60000;
	setInterval(function() {
		getPageInfo(listPageId);
	}, timer);
}

//get top 5 post in olbius page
function getPost() {
	getNewFeed(pageId, jQuery('.flexslider'), 5);
	jQuery('.flexslider').on('fb:getpost', function(e, response) {
		var str = "";
		if (response.data) {
			for (var x = 0; x < response.data.length; x++) {
				if (response.data[x].link) {
					str += "<li>";
					str += "<div class='fb-post  data-width='510' data-height='600'";
					str += "data-href='" + response.data[x].link + "' data-width='500'>";
					str += "<div class='fb-xfbml-parse-ignore'></div>";
					str += "</div></li>";
				}
			}
		}
		jQuery(this).find('ul').html(str);
		setTimeout(function() {
			jQuery('.flexslider').flexslider({
				controlNav : false
			});
			FB.XFBML.parse();
		}, 500);
	});
};
//get first one post in each page
function getPageInfo(list) {
	var str = "";
	shuffle(list);
	for (var x = 0; x < list.length; x++) {
		(function(index_post) {
			str = "<li class='sideslider-item sideslider-item" + index_post + "'>";
			str += "<div class='sideslider-item-title'><div class='sideslider-item-img sideslider-item-img" + index_post + "'><img src=''/></div>";
			str += "<a class='blue' href='http://facebook.com/" + list[x] + "'>";
			str += "<div class='sideslider-item-name sideslider-item-name" + index_post + " marginleft-10'></div></a></div>";
			str += "<div class='sideslider-item-post sideslider-item-post" + index_post + "'></div>";
			str += "</li>";
			jQuery('#sideslider').prepend(str);
			var cur = jQuery('.sideslider-item' + index_post);
			cur.hide();
			var img = jQuery('.sideslider-item-img' + index_post);
			var name = jQuery('.sideslider-item-name' + index_post);
			var post = jQuery('.sideslider-item-post' + index_post);
			renderAvatar(list[x], img);
			renderName(list[x], name);
			renderPost(list[x], post, cur);
		})(index_post);
		index_post++;
	}
}

function renderAvatar(id, img) {
	var i = sessionStorage.getItem('pageAvatar' + id);
	if (i) {
		img.find('img').attr('src', i);
		return;
	}
	getPageAvatar(id, img);
	img.on('fb:pageavatar', function(e, response) {
		if (response.data) {
			img.find('img').attr('src', response.data.url);
			sessionStorage.setItem('pageAvatar' + id, response.data.url);
		}
	});
}

function renderName(id, name) {
	var na = sessionStorage.getItem('pageName' + id);
	if (na) {
		name.text(na);
		return;
	}
	getPageProfile(id, name);
	name.on('fb:pageprofile', function(e, response) {
		if (!response.error) {
			name.text(response.name);
			sessionStorage.setItem('pageName' + id, response.name);
		}
	});
}

function renderPost(id, post, par) {
	getNewFeed(id, post, 1);
	post.on('fb:getpost', function(e, response) {
		if (response.data) {
			var data = response.data[0];
			var po = checkPostExist(data.id);
			if (!po) {
				if (!par.is(":visible")) {
					par.show();
				}
				post.attr('data-post', data.id);
				if (data.message) {
					post.append("<div>" + splitTextByWords(data.message, 30) + "</div>");
				}
				if (data.link) {
					post.append("<div class='sideslide-readmore'><a href='" + data.link + "'>Đọc tiếp</a></div>");
				} else {
					post.append("<div class='sideslide-readmore'><a href='http://facebook.com/" + data.id + "'>Đọc tiếp</a></div>");
				}
			} else {
				par.remove();
			}
		} else {
			par.remove();
		}
	});
}

function checkPostExist(id) {
	var list = jQuery('.sideslider-item-post');
	for (var x = 0; x < list.length; x++) {
		if (id == jQuery(list[x]).attr('data-post')) {
			return true;
		}
	}
	return false;
}

function splitTextByWords(str, words) {
	var arr = str.split(" ", words + 1);
	arr = arr.slice(0, words);
	return arr.join(' ');
}