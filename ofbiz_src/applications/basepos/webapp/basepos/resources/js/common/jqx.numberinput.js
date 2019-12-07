var jqxNumberInputObject = (function(){
	var initJqxNumberInput = function(divEle, width, height, config){
		if(typeof(width) == null || typeof(width) == "undefined"){
			width = "100%";
		}
		if(typeof(height) == null ||  typeof(height) == "undefined"){
			height = "28px";
		}
		$("#"+divEle).jqxNumberInput({
			spinButtons: true, 
			theme: "olbius", 
			width: width,
			height:height, 
			decimalDigits: 0, 
			min: 0 });
		if(typeof(config) != null && config != null){
			 $("#"+divEle).jqxNumberInput(config);
		 }
	};
	return{
		initJqxNumberInput: initJqxNumberInput
	}
	
}());