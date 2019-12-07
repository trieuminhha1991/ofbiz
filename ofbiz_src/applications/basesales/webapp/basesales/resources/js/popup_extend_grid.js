$(function(window, OLBIUS){
	OLBIUS.olbius().popup.addType('jqxGridMultil', {
    	val: function (object, cur) {
    		var rowindexes = object.object.grid.jqxGrid('getselectedrowindexes');
    		var indexDatas = [];
    		var valueData = [];
    		for(var x in rowindexes){
    			var data = object.object.grid.jqxGrid('getrowdata', rowindexes[x]);
    			indexDatas.push(rowindexes[x]);
    			valueData.push(data.value);
    		}
    		cur[object.object.id] = {index: indexDatas, hide: object.hide};
    		return valueData;
    	},
    	clear: function (object, cur) {
    		
    		var rowindexes = object.object.grid.jqxGrid('getselectedrowindexes');
    		
    		var tmp = JSON.parse(JSON.stringify(rowindexes));
    		
    		for(var i = 0; i < tmp.length; i++) {
    			object.object.grid.jqxGrid('unselectrow', tmp[i]);
    		}
    		
    		for(var i in cur[object.object.id].index) {
    			object.object.grid.jqxGrid('selectrow', cur[object.object.id].index[i]);
    		}
    		
    	}
	});
	
	function appendGridMultil(id, label, object) {
    	var text = 
    		'<div id="?" class="span6" style="float:left; margin-bottom: 10px;">' +
	    		'<div id="?" style="margin-top: 5px;float: left;" class="align-right">' +
	        		label +
	        	'</div>' +
	        	'<div id="?" style="float: right">' +
	        		'<div id="?" style="border-color: transparent;">' +
	        		'</div>' +
		        '</div>' +
        	'</div>';
    	var element = object.jqxElement();
    	var _id = object.id();
    	OLBIUS.appendHtmlUUID(element, text, _id, [id.concat('-span'), id.concat('-text'), id, id.concat('-jqxgrid')]);
    	var tmp = object.returnObject(id, label, 'jqxGridMultil');
    	tmp.grid = $('#'+id.concat('-jqxgrid') + '-'+ _id);
    	return tmp;
	}
	
	OLBIUS.olbius().popup.addConfig('addJqxGridMultil', function (config) {
			var dataSource = [];
			var dataDescription = [];
	    	var tmp = appendGridMultil(config['id'], config['label'], this);
	    	$(tmp.element).jqxDropDownButton({
	    		 width: config['width'],
	             height: config['height'],
	             theme: OLBIUS.getTheme()
	    	});
	    	
	    	var source =
            {
                localdata: config['data'],
                datafields:
                [
                    { name: 'value', type: 'string' },
                    { name: 'text', type: 'string' },
                ],
                datatype: "array",
                updaterow: function (rowid, rowdata) {
                }
            };	    	
	    	var dataAdapter = new $.jqx.dataAdapter(source);
	    	
	    	var _config = {
	                 width: 500,
	                 localization: getLocalization(),
	                 source: dataAdapter,
	                 pageable: true,
	                 autoheight: true,
	                 filterable: true,
	                 showfilterrow: true,
	                 columnsresize: true,
	                 pagesize: 5,
	                 pagesizeoptions: ['5', '10'],
	                 selectionmode: 'checkbox',
	                 theme: OLBIUS.getTheme(),
	                 columns: [
	                   { text: config['title1'], columntype: 'textbox', datafield: 'value',  width:200},
	                   { text: config['title2'], columntype: 'textbox', datafield: 'text' },
	                 ]
	             };
	    	
	    	$(tmp.grid).jqxGrid(_config);
	    	
	    	
	    	tmp.span.css('min-height', config['height'] + 'px');
	    	
	    	$(tmp.grid).on('rowselect', function (event) 
			{
				var args = event.args;
				if(typeof event.args.rowindex != 'number'){
		            var rowBoundIndex = args.rowindex;
			    	if(rowBoundIndex.length == 0){
			    		dataSource = [];
			    		dataDescription = [];
			    	}else{
			    		for ( var x = 0; x < rowBoundIndex.length; x++) {
				    		var rowID = $(tmp.grid).jqxGrid('getRowId', x);
		    		        var data = $(tmp.grid).jqxGrid('getrowdatabyid', rowID);
		    		        dataSource.push(data.value);
		    		        dataDescription.push(data.text);
						}
			    	}
		        }else{
		        	var tmpArray = event.args.rowindex;
			        var rowID = $(tmp.grid).jqxGrid('getRowId', tmpArray);
			        var data = $(tmp.grid).jqxGrid('getrowdatabyid', rowID);
			        dataSource.push(data.value);
    		        dataDescription.push(data.text);
		        }
				
				if(dataDescription.length == 0 || dataDescription.length == 1){
					$(tmp.element).jqxDropDownButton('setContent', dataDescription);
				}else{
					$(tmp.element).jqxDropDownButton('setContent', dataDescription + " ,");
				}
		    });
			
			$(tmp.grid).on('rowunselect', function (event) 
			{
				var args = event.args;
		    	var tmpArray = event.args.rowindex;
		        var rowID = $(tmp.grid).jqxGrid('getRowId', tmpArray);
		        var data = $(tmp.grid).jqxGrid('getrowdatabyid', rowID);
		        var ii = dataSource.indexOf(data.value);
				dataSource.splice(ii, 1);
				var iii = dataDescription.indexOf(data.text);
				dataDescription.splice(iii, 1);
		        if(dataDescription.length == 0 || dataDescription.length == 1){
		        	$(tmp.element).jqxDropDownButton('setContent', dataDescription);
		        }else{
		        	$(tmp.element).jqxDropDownButton('setContent', dataDescription + " ,");
		        }
		    });
	    	
			if(config.index) {
				for(var i in config.index) {
					tmp.grid.jqxGrid('selectrow', config.index[i]);
	    		}
	    	}
			
	    	return tmp;
	
	}, function (config) {
		var tmp = [];
		for(var i in config.index) {
			tmp.push(config.index[i]);
		}
	    return {index: tmp, hide: config.hide};
	});
	
}(window, OLBIUS));

