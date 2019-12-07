var blCtrFlag = false; // On when ctrl is press 
var blShiftFlag = false; // On when shift is press 
var blFreOpened = false; // On when PreferMenu is opened
var blRemoveLast = true; // On when item drop on the first time
var blEditMode = false; // On when Prefer menu is edditable
$(window).keydown(function (e){
    if (e.ctrlKey){
    	blCtrFlag = true;
    }
    if (e.shiftKey){
    	blShiftFlag = true;
    }
    // catch ` key
    if(e.which == 192){
    	if(blCtrFlag){
    		changePreferMenuState();
    	}
    }
    if(e.which == 112){ // catch F1 key
    	if(blCtrFlag){
    		$(".icon-double-angle-left").trigger("click");
    	}
    }
    // catch e key
    if(e.which == 69){
    	//e.preventDefault();
    	if(blEditMode){
    		$("#preferOverlay").css('display', 'block');
    		html.css('overflow', 'hidden');
    		$(".ipreremove").attr('style', 'display: none !important');
    		blEditMode = false;
    		$("#freEditmode").css('display', 'none');
    		return;
    	}
    	if(blFreOpened){
    		blEditMode = true;
    		$("#preferOverlay").css('display', 'none');
    		$("#freEditmode").css('display', 'block');
    		$(".ipreremove").attr('style', 'display: block !important');
    		html.css('overflow-y', 'scroll');
    		$("#preferMenu").css('left', windowWidth - 310);
	    	var varMenuLink = $('.nav.nav-list').find('a').not('.focusPP');
	    	for(i = 0; i < varMenuLink.length;i++){
	    		if(varMenuLink[i].href != '#'){
	    			$(varMenuLink[i]).jqxDragDrop({ dropAction: 'none', dropTarget: $('#preferMenu'), revert: true, opacity: 0.9, dragZIndex: 100000, appendTo: 'body',
	    				onTargetDrop: function () {
	    					rebindTabkey();
	    					var tabbables = $('.focusPP');
	    					tabbables.get(0).focus();
	    				},
		    			onDropTargetLeave: function () {
		    				if(blRemoveLast){
		    					$(".prefmenuright li:last").remove();
		    				}else{
		    					blRemoveLast = true;
		    				}
		    			},
		    			onDropTargetEnter: function (item) {
		    				var tmpCheck = $("a.focusPP[href='" + $(this)[0].element.pathname + "']");
		    				if(tmpCheck.length > 0){
		    					blRemoveLast = false;
		    					return;
		    				}
		    				var varMenuLink = $('.nav.nav-list.prefmenuright').find('a.focusPP');
		    				$(".prefmenuright").append('<li id="mnli' + varMenuLink.length + '"><a class="focusPP" href="' + $(this)[0].element.pathname + '"><span>' + $(this)[0].element.innerText + '</span><i class="fa-times ipreremove" onclick="removeElementFromLeftMenu(this);return false;" style="display:block !important;"></i></a></li>');
	    				}
	    			});
	    			$(varMenuLink[i]).bind('dragEnd', function (event) {
	    			});
	    		}
	    	}
	    	$("#preferMenu").children().blur();
    	}
    }
});
var html = jQuery('html');
var $window = $(this);
var windowWidth = $window.width();
function changePreferMenuState(){
	// disable scroll
	var scrollPosition = [
	                      self.pageXOffset || document.documentElement.scrollLeft || document.body.scrollLeft,
	                      self.pageYOffset || document.documentElement.scrollTop  || document.body.scrollTop
	                    ];
    html.data('scroll-position', scrollPosition);
    html.data('previous-overflow', html.css('overflow'));
    html.css('overflow', 'hidden');
	if($("#preferMenu").css('display') == "block"){
		$("#preferMenu").animate({"left":windowWidth}, "slow", function(){
			$("#preferMenu").css('display', 'none');
			html.css('overflow-y', 'scroll'); // re-enable scroll
		});
		$("#preferOverlay").css('display', 'none');
		$(".ipreremove").attr('style', 'display: none !important');
		blFreOpened = false;
	}else{
		$("#preferMenu").css('display', 'block');
		$("#freEditmode").css('display', 'none');
		blFreOpened = true;
		$("#preferMenu").css('left', windowWidth);
		var tmpWidth = $(window).scrollTop();
		if(tmpWidth > 45){
			$("#preferMenu").css('top', 0);
		}else{
			$("#preferMenu").css('top', 45 - tmpWidth);
		}
		$("#preferMenu").animate({"left": windowWidth - 300}, "slow", function(){
			
		});
		$("#preferOverlay").css('display', 'block');
		$("#preferOverlay").attr('class', 'preferOverlay');
		if($('.focusPP').size() > 0){
			$('.focusPP').get(0).focus();
		}
		//e.preventDefault();
	}
}
$(window).keyup(function (e){
    if (e.ctrlKey){
    	blCtrFlag = false;
    }
});
$(window).keyup(function (e){
	if (e.shiftKey){
		blShiftFlag = false;
	}
});
function rebindTabkey(){
	$(".focusPP").on('keydown', function(event) {
		if (event.keyCode !== $.ui.keyCode.TAB) {
            return;
        }
		var tabbables = $('.focusPP');
        if (event.currentTarget.toString() === tabbables.first()[0].href && event.shiftKey) {
        	tabbables.get(tabbables.size() - 1).focus();
            return false;
        } else if (event.currentTarget.toString() === tabbables.last()[0].href && event.shiftKey) {
        	tabbables.get(tabbables.size() - 2).focus();
            return false;
		} else if (event.currentTarget.toString() === tabbables.last()[0].href && !event.shiftKey) {
			tabbables.get(0).focus();
			return false;
		}
	});
}
$(function() {
	rebindTabkey();
});
