var jqxEditorObject = (function(){
	var initJqxEditor = function(divEle, width, height, config){
		if(typeof(width) == 'undefined' || width == null){
			width = 'auto';
		}
		if(typeof(height) == 'undefined' || height == null){
			height = 'auto';
		}
	
		if(typeof(config) == 'undefined' || config == null){
			config = {};
		}
		$("#"+divEle).jqxEditor({
            width: width,
            height: height,
            theme: 'olbiuseditor'
        });
		
		$("#"+divEle).jqxEditor(config);
	};
	return {
		initJqxEditor: initJqxEditor
	}
}());