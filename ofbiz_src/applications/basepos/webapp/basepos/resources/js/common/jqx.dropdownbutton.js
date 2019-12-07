var jqxDropDownButtonObject = (function(){
	var initJqxDropDownButton = function(divEle, width, height, content, config){
		$("#"+divEle).jqxDropDownButton({ 
			width: width,
			height: height
			
		});
		$("#"+divEle).jqxDropDownButton('setContent', content);
		if(typeof(config) != "undefined" && config != null){
			 $("#"+divEle).jqxDropDownButton(config);
		 }
	};
	return{
		initJqxDropDownButton: initJqxDropDownButton
	}
	
}());