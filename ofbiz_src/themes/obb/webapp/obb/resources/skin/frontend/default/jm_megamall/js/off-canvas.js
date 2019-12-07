!function($){
	var isShowMenu = false;
	function getIEVersion() {
        var rv = -1; // Return value assumes failure.
        if (navigator.appName == 'Microsoft Internet Explorer') {
            var ua = navigator.userAgent;
            var re  = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})");
            if (re.test(ua) != null)
                rv = parseFloat( RegExp.$1 );
        }
        return rv;
    }

	var ver = getIEVersion();
	var getMenu = function(){
		var menu = Array();
		var mainMenu = $('#jm-mnutop .megamenu li.mega-normal');
		var catMenu = $('#megamenu-top .megamenu .mega-top');
		for(var y = 0; y < catMenu.length; y++){
			var obj = $(catMenu[y]);
			var link = obj.find('a.mega-top-link');
			var icon = link.find('i');
			var text = link.text();
			var child = Array();
			var childMenu = obj.find('.childcontent ul.level1 li.mega-level1');
			for(var z = 0 ; z < childMenu.length; z++){
				var tmp = $(childMenu[z]);
				var l = tmp.find('a');
				child.push({
					name: l.text(),
					link: l.attr('href'),
				});
			}
			var item = {
				name: text,
				link: link.attr('href'),
				icon: icon.attr('class'),
				child: child
			};
			menu.push(item);
		}
		for(var x = 0; x < mainMenu.length; x++){
			var obj = $(mainMenu[x]);
			var text = obj.text();
			var link = obj.find('a');
			var icon = obj.find('i');
			menu.push({
				name: text,
				link: link.attr('href'),
				icon: icon.attr('class')
			});
		}
		return menu;
	};
	var stipName = function(name){
		var x = name.replace(/(\r\n|\n|\r| {2,})/gm,"");
		return x;
	};
	var buildMenu = function(menu){
		var str = "<ul class='mobile-menu'>";
		for(var x in menu){
			var par = menu[x];
			var child = par.child;
			str += "<li class='mobile-menu-item'>"
				+ "<a href='" + par.link + "'>"
				+ "<div class='mobile-menu-content'>"
				+ "<i class='" + par.icon + "'>&nbsp;</i>&nbsp;"
				+ stipName(par.name)
				+ "</div></a>";
			if(child && child.length){
				str += "<div class='mobile-menu-child'>"
					+ "<ul class='mobile-menu mobile-child-menu'>";
				for(var y in child){
					var obj = child[y];
					str += "<li class='mobile-menu-item'>"
						+ "<a href='" + obj.link + "'>"
						+ "<div class='mobile-menu-content'>";
					if(obj.icon){
						str += "<i class='" + obj.icon + "'>&nbsp;</i>";
					}
					str	+= stipName(obj.name) + "</div></a></li>";
				}
				str += "</ul></div>";
			}
			str	+= "</li>";
		}
		str += "</ul>";
		$('#off-canvas-nav .jm-mainnav').append(str);
	};
	if (ver<1 || ver >= 9) {
		$(document).ready(function(){
             jQuery(window).resize (function() {
				hideNav();
             });

			var $btn = $('#jm-mainnav .btn-toggle'),
				$nav = null,
				$fixeditems = null;
			if (!$btn.length) return;

			$nav = $("#off-canvas-nav");
			$container = $("#off-canvas-container");
            $searchbarHtml = $("#search_mini_form").parent().html();
            $searchbarHtml = $searchbarHtml.replace('search_mini_form', 'search_mini_form_offcanvas');
            $searchbarHtml = $searchbarHtml.replace('id="search"','id="search_offcanvas"').replace('search_autocomplete','search_autocomplete_offcanvas');
            $nav.append($searchbarHtml);
            // buildMenu(getMenu());
			// $("#jm-megamenu ul.level0").clone().appendTo($nav);

			$('html').addClass ('off-canvas');
			$container.click(function(){
				hideNav();
			});
			$btn.click (function(e){
				if ($(this).data('off-canvas') == 'show') {
					hideNav();
				} else {
					showNav();
				}
				return false;
			});

			showNav = function (side) {
				setTimeout (function(){
					isShowMenu = true;
					$btn.data('off-canvas', 'show');
					$('html').addClass ('off-canvas-enabled');
					if(typeof($zopim) != 'undefined' && typeof($zopim.livechat) != 'undefined'){
						$zopim.livechat.button.setPosition('br');
					}
				}, 50);
			};
			hideNav = function (side) {
				isShowMenu = false;
                if(jQuery("#jmoffcanvasdim").length > 0){
                   jQuery("#jmoffcanvasdim").remove();
                }
				$('html').removeClass ('off-canvas-enabled');
				$btn.data('off-canvas', 'hide');
					setTimeout (function(){
				}, 1000);

			};
		});
	}
}(jQuery);