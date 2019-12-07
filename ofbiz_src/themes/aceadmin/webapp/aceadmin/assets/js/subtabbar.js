jQuery(function(){
//	displaySidebar();
	$('#myTab > li.dropdown').on("click", function(){
		$(".breadcrumb-container #subtab-advance").toggleClass("display");
		a=$(".breadcrumb-container #subtab-advance").hasClass("display");
		if(a){
			$(this).addClass("open");
		} else {
			$(this).removeClass("open");
		}
	})
	var b = $('.breadcrumb-container #subtab-advance li').hasClass("active");
	if(b){
		$("#myTab > li.dropdown").addClass("open");
		$('.breadcrumb-container #subtab-advance').addClass("display");
	} else {
		$("#myTab > li.dropdown").removeClass("open");
		$('.breadcrumb-container #subtab-advance').removeClass("display");
	}
	
	var t=$(".inner-advance-button").find(".dropdown-toggle");
	var n=function(t){
		$("html").on("click.dropdown.data-api", function(e){
			var target = $(e.target);
		    if (target.hasClass("dropdown-toggle") && target.parents(".inner-advance-button").length){
		        target.parent().toggleClass('open');
		        if (target.parent().hasClass("open")) {
		    		target.parent().find('div[class^="margin-bottom-menu"]').removeClass("menu-class-display-none");
		    	} else {
		    		target.parent().find('div[class^="margin-bottom-menu"]').addClass("menu-class-display-none");
		    	}
		    } else if (target.hasClass("dropdown-toggle")) {
		    	target.parent().toggleClass('open');
		    } else {
		    	if ($(".inner-advance-button").find(".dropdown-toggle").length != 0) {
		    		$(document).off("click.dropdown.data-api");
		    	}
		    }
		});
	};
	n();
	if (t != undefined) {
		n(t);
	};
	if ($(".container-table-scroll-advance") != undefined) {
		initTableContentScroll();
	}
});

function displaySidebar() {
	var view = getCookie("expandSidebar");
	if (view == 'expand') {
		$("#sidebar").removeClass("menu-min");
		$("#main-content").removeClass("main-expand");
		$("#sidebar-collapse i").removeClass("icon-double-angle-right");
		$("#sidebar-collapse-bottom i").removeClass("icon-double-angle-right");
		setCookie('expandSidebar', 'expand');
	} else {
		$("#sidebar").addClass("menu-min", { duration: 700, queue: false }, "easeInQuart");
		$("#main-content").addClass("main-expand", { duration: 700, queue: false }, "easeInQuart");
		$("#sidebar-collapse i").addClass("icon-double-angle-right");
		$("#sidebar-collapse-bottom i").addClass("icon-double-angle-right");
		setCookie('expandSidebar', 'min');
	}
}

function setCookie(key, value) {  
   var expires = new Date();  
   expires.setTime(expires.getTime() + 604800000);
   document.cookie = key + '=' + value + ';expires=' + expires.toUTCString();  
}  
  
function getCookie(key) {  
   var keyValue = document.cookie.match('(^|;) ?' + key + '=([^;]*)(;|$)');  
   return keyValue ? keyValue[2] : null;  
}

function handle_side_menu(){
	$("#menu-toggler").on("click",function(){
		$("#sidebar").toggleClass("display");
		$(this).toggleClass("display");
		return false
	});
	var a=false;
	$("#sidebar-collapse").on("click",function(){
		$("#sidebar").toggleClass("menu-min");
		$(this.firstChild).toggleClass("icon-double-angle-right");
		$("#sidebar-collapse-bottom i").toggleClass("icon-double-angle-right");
//		a=$("#sidebar").hasClass("menu-min");
//		if(a){
//			$(".open > .submenu").removeClass("open");
//			setCookie('expandSidebar', 'min');
//		} else {
//			$("#main-content").removeClass("main-expand");
//			setCookie('expandSidebar', 'expand');
//		}
	});
	$("#sidebar-collapse-bottom").on("click",function(){
		$("#sidebar").toggleClass("menu-min");
		$(this.firstChild).toggleClass("icon-double-angle-right");
		$("#sidebar-collapse i").toggleClass("icon-double-angle-right");
//		a=$("#sidebar").hasClass("menu-min");
//		if(a){
//			$(".open > .submenu").removeClass("open");
//			setCookie('expandSidebar', 'min');
//		} else {
//			$("#main-content").removeClass("main-expand");
//			setCookie('expandSidebar', 'expand');
//		}
	});
	$(".nav-list .dropdown-toggle").each(function(){
		var b=$(this).next().get(0);
		$(this).on("click",function(){
			if(a){return false}
			$(".open > .submenu").each(function(){
				if(this!=b&&!$(this.parentNode).hasClass("active")){
					$(this).slideUp(200).parent().removeClass("open")
				}
			});
			$(b).slideToggle(200).parent().toggleClass("open");return false
		})
	})
};
var initTableContentScroll = function (){
	$(".container-table-scroll-advance table.table-scroll-advance").each(function(index, table) {
		var firstRow = $($(table).find('tr')[0]);
		var offset = 0;
	    var stickies = firstRow.find('.sticky');
	
	    stickies.each(function(index, td) {
	      	var column = $(table).find('tr .sticky:nth-of-type('+(index+1)+')');
	      	column.css({left: offset+'px'});
	      	column.addClass('stuck');
	      	offset += $(td).width() + 10;
	    });
	
	    $(table).parent().css({"margin-left": offset+'px'});
	    $(table).parent().scroll(function(e) {
	      	var top = e.currentTarget.scrollTop - 1;
	      	$(table).find('thead tr th div').css({top:top+'px'});
	    });
	});
};