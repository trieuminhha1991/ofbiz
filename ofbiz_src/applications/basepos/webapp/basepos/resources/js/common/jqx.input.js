var jqxInputObject = (function(){
	var initJqxInput = function(divEle, width, height, config){
		if(width == null || typeof(width) == "undefined"){
			width = "98%";
		}
		if(height == null ||  typeof(height) == "undefined"){
			height = "25px";
		}
		if(typeof(config) != "undefined" && config != null){
			 $("#"+divEle).jqxInput(config);
		 };
		 $("#"+divEle).jqxInput({
			 theme: "olbius",
			 width: width,
			 height: height
			 
		 });
		 
	};
	return{
		initJqxInput: initJqxInput
	}
}());