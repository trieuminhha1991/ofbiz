var jqxSwitchButtonObject = (function(){
	var initJqxSwitchButton = function(divEle, width, heigh, checked, config){
		if(typeof(width) == null || typeof(width) == "undefined"){
			width = "100%";
		}
		if(typeof(height) == null ||  typeof(height) == "undefined"){
			height = "25px";
		}
		 $("#"+divEle).jqxSwitchButton({
			 width: width,
			 height: height,
			 checked: checked
			 
		 });
		 if(typeof(config) != null && config != null){
			 $("#"+divEle).jqxSwitchButton(conifg);
		 };
	};
	return{
		initJqxSwitchButton: initJqxSwitchButton
	}
}());