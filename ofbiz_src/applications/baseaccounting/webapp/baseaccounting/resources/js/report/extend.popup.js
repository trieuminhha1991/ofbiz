$(function(window, OLBIUS){
	OLBIUS.olbius().popup.addType('filterDropdown', {
    	val: function (object, cur) {
        		var item = object.object.element.jqxDropDownList('getSelectedItem');
        		valueData = item.value;
        		cur[object.object.id] = {index: item.index, hide: object.hide};
        	return valueData;
    	},
    	clear: function (object, cur) {
    		object.object.element.jqxDropDownList('selectIndex', cur[object.object.id].index);
    	}
	});
	
	function appendGridMultilWithFilter(id, label, object) {
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
    	var tmp = object.returnObject(id, label, 'jqxGridMultilWithFilter');
    	tmp.grid = $('#'+id.concat('-jqxgrid') + '-'+ _id);
    	return tmp;
	}
	
	/*OLBIUS.olbius().popup.addType('jqxGridMultil', {
	     val: function (object, cur) {
	      var item = object.object.element.jqxDropDownButton('val');
	      return item;
	     },
	     clear: function (object, cur) {
	      object.object.grid.jqxGrid('clearSelection');
	      object.object.element.val('');
	     }
	 });*/
	
	
	OLBIUS.olbius().popup.addConfig('addJqxGridMultilWithFilter', function (config) {
			var dataSource = [];
			var dataDescription = [];
	    	var tmp = appendGridMultilWithFilter(config['id'], config['label'], this);
	    	$(tmp.element).jqxDropDownButton({
	    		 width: config['width'],
	             height: config['height'],
	             theme: OLBIUS.getTheme()
	    	});
	    	var configGrid = $.extend({
								localization : getLocalization(),
								pagesizeoptions : [ '5', '10' ],
								source:{
									id: config.id,
									pagesize: config.pagesize != 'undefined'? config.pagesize: 5
								}
							}, 
							_.omit(config, 'source', 'datafields', 'id', 'columns', 'pagesize'));
	    	
	    	configGrid.width = config.gridWidth != 'undefined'? config.gridWidth : 500;
	    	var datafields = config.datafields;
	    	var columns = config.columns;
	    	if(!datafields){
	    		datafields = [{ name: 'value', type: 'string', other: 'olap' },
	    		              { name: 'text', type: 'string', other: 'olap' }];
	    	}
	    	if(!columns){
	    		columns = [{ text: config['title1'], datafield: 'value',  width: 200},
	    		           { text: config['title2'], datafield: 'text' },];
	    	}
	    	//var source = {};
	    	var displayField = config.displayField;
			if(typeof(displayField) == undefined){
				displayField = config.id;
			}
			var displayAdditionField = config.displayAdditionField;
	    	if(config.url){
	    		Grid.initGrid(configGrid, datafields, columns, 'olap', $(tmp.grid));
	    	}else{
	    		Grid.initGrid({width: 500, virtualmode: false, filterable: true, source:{localdata:config['data'],cache : false}}, config.datafields, config.columns , null, $(tmp.grid));
	    	}
	    	tmp.span.css('min-height', config['height'] + 'px');
	    	$(tmp.grid).on('rowselect', function (event) {
	    		var args = event.args;
				var rowBoundIndex = args.rowindex;
				var data = $(tmp.grid).jqxGrid('getrowdata', rowBoundIndex);
				if(data){
					var content = data[displayField];
					if(typeof(displayAdditionField) != undefined){
						content += ' [' + data[displayAdditionField] + ']';
					}
					if(!content){
						content = data.value;
					}
				}
				$(tmp.element).jqxDropDownButton('setContent', '<div class="innerDropdownContent">' + content + '</div>');
				$(tmp.element).jqxDropDownButton('close');
			});
	    	return tmp;
	
	}, function (config) {
	    	return {index: config.index, hide: config.hide};
	});
	
	OLBIUS.olbius().popup.addType('jqxGridMultilWithFilter', {
		val: function (object, cur) {
    		var rowindex = object.object.grid.jqxGrid('getselectedrowindex');
    		var value = object.object.id;
    		cur[object.object.id] = {index: rowindex, hide: object.hide};
    		if(rowindex > -1){
    			var data = object.object.grid.jqxGrid('getrowdata', rowindex);
    			return data[value];
    		}
    	},
    	clear: function (object, cur) {
    		if(cur[object.object.id].index > -1){
    			object.object.grid.jqxGrid('selectrow', cur[object.object.id].index);
    		}else{
    			object.object.grid.jqxGrid('clearselection');
    		}
    	}
	});
	
	function appendFilterDropDownList(id, label, object) {
    	var text = '<div id="?" class="span6" style="float:left; margin-bottom: 10px;"><div id="?" style="margin-top: 5px;float: left;" class="align-right">' +
        	label +
        	'</div><div id="?" style="float: right"></div></div>';
    	var element = object.jqxElement();
    	var _id = object.id();
    	OLBIUS.appendHtmlUUID(element, text, _id, [id.concat('-span'), id.concat('-text'), id]);
    	return object.returnObject(id, label, 'filterDropdown');
	}
	
	OLBIUS.olbius().popup.addConfig('addFilterDropDownList', function (config) {

	    	var tmp = appendFilterDropDownList(config['id'], config['label'], this);
	    	$(tmp.element).jqxDropDownList({
        		source: config['data'],
        		selectedIndex: config['index'],
        		theme: OLBIUS.getTheme(),
        		width: config['width'],
        		height: config['height'],
        		displayMember: 'text',
        		valueMember: 'value'
	    	});
	    	tmp.span.css('min-height', config['height'] + 'px');
	    	
	    	$(tmp.element).jqxDropDownList({filterable: true});
	    	
	    	var $id = $(tmp.element);
	    	return tmp;
	
	}, function (config) {
	    	return {index: config.index, hide: config.hide};
	});
	
}(window, OLBIUS));