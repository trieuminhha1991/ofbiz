var jqxDateTimeInputObject = (function(){
	var initJqxDateTimeInput = function(divEle, width, height, config){
		if(width == null || typeof(width) == "undefined"){
			width = "100%";
		}
		if(height == null ||  typeof(height) == "undefined"){
			height = "25px";
		}
		var formatString = "yyyy-MM-dd HH:mm:ss";
		
		$("#"+divEle).jqxDateTimeInput({ 
				formatString: formatString, 
				width: width, 
				height: height, 
				theme: 'olbius',
				showFooter: true
				
				});
		if(typeof(config) != "undefined" && config != null){
			 $("#"+divEle).jqxDateTimeInput(config);
		 };
	};
	return{
		initJqxDateTimeInput: initJqxDateTimeInput
	}
}());