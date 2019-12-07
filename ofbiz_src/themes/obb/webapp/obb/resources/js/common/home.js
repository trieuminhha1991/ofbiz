var HomeScreen = (function($) {
	var menu;
	var mainFlag = false;
	var subFlag = false;
	var exit = false;
	var activateSubmenu = function(row) {
		var $row = $(row);
		var submenuId = $row.data("submenuId");
		var $submenu = $("#" + submenuId);
		var height = menu.outerHeight();
		var width = menu.outerWidth();
		// Show the submenu
		$submenu.css({
			display : "block",
		});
		$row.find("a").addClass("menu-active");
		exit = true;
	};

	var deactivateSubmenu = function(row) {
		// return;
		var $row = $(row);
		var submenuId = $row.data("submenuId");
		var $submenu = $("#" + submenuId);
		// Hide the submenu and remove the row's highlighted look
		$submenu.hide();
		$row.find("a").removeClass("menu-active");
	};
	var exitMenu = function(){
		return exit;
	};
	var initCategoryMenu = function(){
		$(window).resize (function() {
		$('#megamenu-top').height($('.slidermobile').height());
        });
		$('#megamenu-top').height($('.slidermobile').height());
	};
	var initBuyNowMenu = function(){
		$('.buy-now').click(function(){
			var obj = $(this);
			var quantityUomId = obj.data('uom');
			var productId = obj.data('product');
			$('input[name="quantityUomId"]').val(quantityUomId);
			$('input[name="add_product_id"]').val(productId);
			$('#MenuAddToCart').submit();
			$('.buy-now').attr('disabled','disabled');
		});
	};
	var init = function() {
		menu = $("#maincategory");
		var child = $('#maincategory .childcontent');
		menu.menuAim({
			// enter : activateSubmenu,
			exitMenu: exitMenu,
			activate : activateSubmenu,
			deactivate : deactivateSubmenu
		});
		menu.hover(function(){
			mainFlag = true;
		});
		menu.mouseleave(function(){
			mainFlag = false;
			if(!subFlag){
				var cur = $(".menu-active").parent("li");
				deactivateSubmenu(cur);
				exit = true;
			}
		});
		child.hover(function(){
			subFlag = false;
		});
		child.mouseleave(function(){
			subFlag = false;
			setTimeout(function(){
				if(!mainFlag){
					var cur = $(".menu-active").parent("li");
					deactivateSubmenu(cur);
					exit = true;
				}
			}, 300);
		});
		initCategoryMenu();
		initBuyNowMenu();
		// activateSubmenu($($('#maincategory li.mega-top')[0]));
	};
	return {
		init : init
	};
})(jQuery);
jQuery(document).ready(function($) {
	HomeScreen.init();
});
