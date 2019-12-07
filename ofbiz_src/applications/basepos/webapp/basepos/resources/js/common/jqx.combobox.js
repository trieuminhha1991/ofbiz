var jqxComboboxObject = (function(){
	var createJqxCombobox = function(divCombobox, source,valueMember, displayMember, width, height, config){
		 var dataAdapter = new $.jqx.dataAdapter(source);
		 $("#"+divCombobox).jqxComboBox({
			 source: source, 
			 selectedIndex: 0,
			 displayMember: displayMember,
			 valueMember: valueMember,
			 width: width,
			 height: height
			 
		 });
		 if(typeof(config) != null && config != null){
			 $("#"+divCombobox).jqxComboBox(config);
		 }
	};
	
	var creatingWithRemoteSearch = function(divCombox, url,valueMember, displayMember, width, height, config,datafield, renderer, renderSelectedItem,placeHolder, orderMode){
		//set default config
		
		if(typeof width == "undefined" || width == null){
			width = 'auto';
		}
		if(typeof height == "undefined" || height == null){
			height = 'auto';
		}
		if(typeof config == "undefined" || config == null){
			config = {};
		}
		if(typeof placeHolder == "undefined" || placeHolder == null){
			placeHolder = "";
		}
		// prepare the data
        var source =
        {
            datatype: "json",
            datafields: datafield,
            url: url,
            data: {
            	 featureClass: "P",
                 style: "full",
                 maxRows: 12,
                 username: "jqwidgets"
            },
            type: "POST",
	        root: "resultList"
	      
        };
        var dataAdapter = new $.jqx.dataAdapter(source,
         		 {
   		    	downloadComplete: function (data, status, xhr) {
   		     		if(data.resultList.length < 2){
   		     			$("#"+divCombox).jqxComboBox({autoOpen: false});
   		     		}else{
   		     			$("#"+divCombox).jqxComboBox({autoOpen: true}); 
   		     		}
   		         },
   	        	formatData: function (data) {
   	                if ($("#"+divCombox).jqxComboBox('searchString') != undefined) {
   	                    data.keywordSearch = $("#"+divCombox).jqxComboBox('searchString');
   	                    data.orderMode = orderMode;
   	                    return data;
   	                }
   	            }
   	    });
        $("#"+divCombox).jqxComboBox(
        {
           width: width,
           
           height: 30,
           placeHolder: placeHolder,
           source: dataAdapter,
           //autoDropDownHeight: true,
           remoteAutoComplete: true,
           selectedIndex: 0,
           displayMember: displayMember,
           valueMember: valueMember,
	       renderer: function (index, label, value) {
	        	return renderer(index, label, value, dataAdapter);
	        },
	        renderSelectedItem: function(index, item)
	        {
	        	return renderSelectedItem(index, item, dataAdapter);
	         
	        },
	        search: function (searchString) {
	            dataAdapter.dataBind();
	        }
	            
	        });
         $("#"+divCombox).jqxComboBox(config);
        
	};
	return{
		createJqxCombobox: createJqxCombobox,
		creatingWithRemoteSearch: creatingWithRemoteSearch
	}

}());