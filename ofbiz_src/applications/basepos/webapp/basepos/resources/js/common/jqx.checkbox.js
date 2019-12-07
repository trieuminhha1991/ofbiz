var jqxCheckboxObject = (function(){
	var initJqxCheckbox = function(divEle, width, height, checked, config){
		if(typeof(width) == null || typeof(width) == "undefined"){
			width = "100%";
		}
		if(typeof(height) == null ||  typeof(height) == "undefined"){
			height = "25px";
		}
		 $("#"+divEle).jqxCheckBox({ 
			 width: width, 
			 height: height, 
			 checked: checked
		 });
		 if(typeof(config) != null && config != null){
			 $("#"+divEle).jqxCheckBox(conifg);
		 };
	};
	return{
		initJqxCheckbox: initJqxCheckbox
	}
}());