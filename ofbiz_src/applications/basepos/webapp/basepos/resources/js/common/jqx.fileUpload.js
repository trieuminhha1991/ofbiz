var jqxFileUploadObject = (function(){
	var initJqxFileUpload = function(divEle, uploadUrl, width, height, config){
		if(typeof(width) == null || typeof(width) == "undefined"){
			width = "300";
		}
		if(typeof(height) == null || typeof(height) == "undefined"){
			height = "auto";
		}
		 $("#"+divEle).jqxFileUpload({
			 theme: 'olbius',
			 width: width,
			 multipleFilesUpload: false,
			 height: height,
			 uploadUrl: uploadUrl, 
			 fileInputName: 'fileInput',
			 localization: {
			        browseButton: 'Browser'
			       
			    }
		  });
		 if(typeof(config) != null && config != null){
			 $("#"+divEle).jqxFileUpload(config);
		}
	};
	return{
		initJqxFileUpload: initJqxFileUpload
	}
}());