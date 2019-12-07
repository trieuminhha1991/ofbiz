var jqxWindowObject = (function(){
	var openJqxWindow = function(divEle){
		//var ratio = headerObject.getRatio();
		var wtmp = window;
		var tmpwidth = $("#"+divEle).outerWidth();
		var tmpHeight = $("#" + divEle).outerHeight();
		var sub = wtmp.outerWidth  - tmpwidth;
		sub = sub < 0 ? 0 : sub;
		var yOffset = wtmp.outerHeight * 0.1;
		
		$("#"+divEle).jqxWindow({position: { x: sub/2, y: yOffset } });
		$("#"+divEle).jqxWindow('open');
	};

	var createJqxWindow= function(divEle, width, height, initContent, config){
		//var ratio = headerObject.getRatio();
		if(typeof(initContent) == "undefined"){
			initContent = function(){};
		}
		if(typeof height == "undefined" || height == null){
			height = "auto";
		}
		var obj = $("#"+divEle);
		obj.jqxWindow({
			theme:'olbius',
			width: width,
			height: height,
			showCollapseButton: false,
	        autoOpen: false, 
	        isModal: true, 
	        maxWidth: 1000, 
	        initContent: initContent
		});
		/*var ratio = getRatio();
		var tmpWidth = obj.outerWidth();
		if(tmpWidth){
			tmpWidth = tmpWidth / ratio;
			obj.jqxWindow({width: tmpWidth})
		}
		*/
		/*var tmpHeight = obj.outerHeight();
		
		if(tmpHeight){
			tmpHeight = tmpHeight / ratio;
			obj.jqxWindow({height: tmpHeight})
		}*/
		if(typeof(config) != "undefined" && config != null){
			$("#"+divEle).jqxWindow(config);
		}
		
	};
	var getRatio = function(){
		var defaultWidth = 1366;
		var windowWidth = window.outerWidth;
		var ratio = defaultWidth / windowWidth;
		return ratio;
	};

	return {
		createJqxWindow: createJqxWindow,
		openJqxWindow: openJqxWindow
		
	}
}());