var jqxSplitterObject = (function(){
	var initJqxSplitter = function(divEle, width, height, config){
		if(typeof(config) == "undefined" && config == null){
			config = function(){};
		}
		$("#"+divEle).jqxSplitter({
			width: width,
			height: height,
			
		});
		$("#"+divEle).jqxSplitter(config);
	};
	return{
		initJqxSplitter: initJqxSplitter
	}
}());