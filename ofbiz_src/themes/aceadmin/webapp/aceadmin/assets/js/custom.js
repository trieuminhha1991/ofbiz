jQuery(function(){
	displaySidebar();
	$('#myTab > li.dropdown').on("click", function(){
		$(".breadcrumb-container #subtab-advance").toggleClass("display");
		a=$(".breadcrumb-container #subtab-advance").hasClass("display");
		if(a){
			$(this).removeClass("open");
		} else {
			$(this).addClass("open");
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
		a=$("#sidebar").hasClass("menu-min");
		if(a){
			$(".open > .submenu").removeClass("open");
			setCookie('expandSidebar', 'min');
		} else {
			$("#main-content").removeClass("main-expand");
			setCookie('expandSidebar', 'expand');
		}
	});
	$("#sidebar-collapse-bottom").on("click",function(){
		$("#sidebar").toggleClass("menu-min");
		$(this.firstChild).toggleClass("icon-double-angle-right");
		$("#sidebar-collapse i").toggleClass("icon-double-angle-right");
		a=$("#sidebar").hasClass("menu-min");
		if(a){
			$(".open > .submenu").removeClass("open");
			setCookie('expandSidebar', 'min');
		} else {
			$("#main-content").removeClass("main-expand");
			setCookie('expandSidebar', 'expand');
		}
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
}