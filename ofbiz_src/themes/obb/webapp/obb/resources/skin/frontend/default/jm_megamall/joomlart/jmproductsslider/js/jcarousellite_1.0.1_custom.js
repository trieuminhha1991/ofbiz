(function($) {                                          // Compliant with jquery.noConflict()
$.fn.jCarouselLite = function(o) {
    o = $.extend({
        btnPrev: null,
        btnNext: null,
        btnGo: null,
        mouseWheel: false,
        auto: null,

        speed: 200,
        easing: null,

        vertical: false,
        circular: true,
        visible: 3,
        start: 0,
        scroll: 1,

        beforeStart: null,
        afterEnd: null
    }, o || {});

    return this.each(function() {                           // Returns the element collection. Chainable.
        var running = false, animCss=o.vertical?"top":"left", sizeCss=o.vertical?"height":"width";
        var div = $(this), ul = $("ul", div), tLi = $("li", ul), tl = tLi.size(), v = o.visible,parentdiv = $(this).parent();

        if(o.circular) {
            ul.prepend(tLi.slice(tl-v-1+1).clone())
              .append(tLi.slice(0,v).clone());
            o.start += v;
        }
        if(ul.children().length < 1){
		return;
        }
        var li = $("li", ul), itemLength = li.size(), curr = o.start;
      
        div.css("visibility", "visible");

        li.css({overflow: "hidden", float: o.vertical ? "none" : "left"});
        ul.css({margin: "0", padding: "0", position: "relative", "list-style-type": "none", "z-index": "1"});
        div.css({overflow: "hidden", position: "relative", "z-index": "2", left: "0px"});
        var wdiv = width(div);
        var liheight = height(li)
        var liSize = o.vertical ? liheight : wdiv;   // Full li size(incl margin)-Used for animation
        var ulSize = liSize * itemLength;                   // size of full ul(total length, not just for the visible items)
        var divSize = liSize * v;                           // size of entire div(total length for just the visible items)
        li.css({width: wdiv, height: liheight});
        ul.css(sizeCss, ulSize+"px").css(animCss, -(curr*liSize));
        realvisible = Math.round(parentdiv.width()/liSize);
        div.css(sizeCss, divSize+"px");                     // Width of the DIV. length of visible images

        if($(".jm-tab-content").length){
            contentindex = $(".jm-tab-content").index(div.parents(".jm-tab-content"));
            if($(".jm-tabs-title")){
                $(".jm-tabs-title li").eq(contentindex).find("span.tab").bind("tabclick",function(){

                       liindex = $(".jm-tabs-title li").index($(this).parents("li"));
                       parentdiv = $(".jm-tab-content").eq(liindex);
                       div = parentdiv.find("ul").parent();

                       ul = $("ul", div), li = $("li", ul);
                       liSize = o.vertical ? height(li) : width(li);   // Full li size(incl margin)-Used for animation
                       if(liSize){

                            li.css({"width":"","height":""});
                            ulSize = liSize * itemLength;                   // size of full ul(total length, not just for the visible items)
                            divSize = liSize * v;                           // size of entire div(total length for just the visible items)

                            li.css({width: li.width(), height: li.height()});
                            ul.css(sizeCss, ulSize+"px").css(animCss, -(curr*liSize));
                            div.css(sizeCss, divSize+"px");
                       }
                });
            }
        }

        jQuery(window).resize (function() {
        	li.css({"width":"","height":""});
        	wdiv = width(div);
            liheight = height(li)
            liSize = o.vertical ? liheight : wdiv;   // Full li size(incl margin)-Used for animation
//            liSize = o.vertical ? height(li) : width(li);   // Full li size(incl margin)-Used for animation
            ulSize = liSize * itemLength;                   // size of full ul(total length, not just for the visible items)
            divSize = liSize * v;                           // size of entire div(total length for just the visible items)

            li.css({width: wdiv, height: liheight});
            ul.css(sizeCss, ulSize+"px").css(animCss, -(curr*liSize));

            div.css(sizeCss, divSize+"px");
        });
        addclasstolis();

        if(o.btnPrev)
            $(o.btnPrev).click(function() {
                return go(curr-o.scroll);
            });

        if(o.btnNext)
            $(o.btnNext).click(function() {
                return go(curr+o.scroll);
            });

        if(o.btnGo)
            $.each(o.btnGo, function(i, val) {
                $(val).click(function() {
                    return go(o.circular ? o.visible+i : i);
                });
            });

        if(o.mouseWheel && div.mousewheel)
            div.mousewheel(function(e, d) {
                return d>0 ? go(curr-o.scroll) : go(curr+o.scroll);
            });

        if(o.auto)
            setInterval(function() {
                go(curr+o.scroll);
            }, o.auto+o.speed);

        function vis() {
            return li.slice(curr).slice(0,v);
        };

         function addclasstolis(){
             li.removeClass("firstitem beforefirstitem lastitem afterlastitem");
             li.eq(curr).addClass("firstitem");
             li.eq(curr-1).addClass("beforefirstitem");
             li.eq(curr+realvisible-1).addClass("lastitem");
             li.eq(curr+realvisible).addClass("afterlastitem");

        }
        function go(to) {
            if(!running) {

                if(o.beforeStart)
                    o.beforeStart.call(this, vis());

                if(o.circular) {            // If circular we are in first or last, then goto the other end
                    if(to<=o.start-v-1) {           // If first, then goto last
                        ul.css(animCss, -((itemLength-(v*2))*liSize)+"px");
                        // If "scroll" > 1, then the "to" might not be equal to the condition; it can be lesser depending on the number of elements.
                        curr = to==o.start-v-1 ? itemLength-(v*2)-1 : itemLength-(v*2)-o.scroll;
                    } else if(to>=itemLength-v+1) { // If last, then goto first
                        ul.css(animCss, -( (v) * liSize ) + "px" );
                        // If "scroll" > 1, then the "to" might not be equal to the condition; it can be greater depending on the number of elements.
                        curr = to==itemLength-v+1 ? v+1 : v+o.scroll;
                    } else curr = to;
                } else {                    // If non-circular and to points to first or last, we just return.
                    if(to<0 || to>itemLength-v) return;
                    else curr = to;
                }                           // If neither overrides it, the curr will still be "to" and we can proceed.

                running = true;

                ul.animate(
                    animCss == "left" ? { left: -(curr*liSize) } : { top: -(curr*liSize) } , o.speed, o.easing,
                    function() {
                        addclasstolis();
                        if(o.afterEnd)
                            o.afterEnd.call(this, vis());
                        running = false;
                    }
                );
                // Disable buttons when the carousel reaches the last/first, and enable when not
                if(!o.circular) {
                    $(o.btnPrev + "," + o.btnNext).removeClass("disabled");
                    $( (curr-o.scroll<0 && o.btnPrev)
                        ||
                       (curr+o.scroll > itemLength-v && o.btnNext)
                        ||
                       []
                     ).addClass("disabled");
                }

            }
            return false;
        };
    });
};

function css(el, prop) {
    return parseInt($.css(el[0], prop)) || 0;
};
function width(el) {
    return  el[0].offsetWidth + css(el, 'marginLeft') + css(el, 'marginRight');
};
function height(el) {
    return el[0].offsetHeight + css(el, 'marginTop') + css(el, 'marginBottom');
};

})(jQuery);