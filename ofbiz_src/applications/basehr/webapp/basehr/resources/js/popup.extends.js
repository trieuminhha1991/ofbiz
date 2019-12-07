$(function(window, OLBIUS){
	OLBIUS.olbius().popup.addType('jqxNumberInput', {
		val : function(object, cur){
			var value = object.object.element.val();
			var valueStr = value.toString();
			cur[object.object.id] = {value : valueStr, hide : object.hide};
			return valueStr;
		},
		clear: function(object, cur){
		},
	})
	
	function appendNumberInput(id, label, object){
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
		OLBIUS.appendHtmlUUID(element, text, _id, [id.concat('-span'), id.concat('-text'), id, id.concat('-jqxnumberinput')]);
		var tmp = object.returnObject(id, label, 'jqxNumberInput');
		return tmp;
	};
	
	OLBIUS.olbius().popup.addConfig('addJqxNumberInput', function(config){
		var tmp = appendNumberInput(config['id'], config['label'], this);
		$(tmp.element).jqxNumberInput({
			width : config['width'],
			height : config['height'],
			theme : OLBIUS.getTheme(),
			spinButtons: true,
			min : 0,
			value : config['value'],
			digits : config['digits'],
			decimalDigits : config['decimalDigits'],
			groupSeparator: config['groupSeparator']
		});	
		return tmp;
	});
	
	OLBIUS.olbius().popup.addType('jqxTree', {
		val : function(object, cur){
			var val = object.object.grid.jqxTree('val');
			var value = val.value;
			cur[object.object.id] = {value : value, hide : object.hide};
			return value;
		},
		clear : function(object, cur){
		}
	});
	
	function appendTree(id, label, object){
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
		OLBIUS.appendHtmlUUID(element, text, _id, [id.concat('-span'), id.concat('-text'), id, id.concat('-jqxtree')]);
		var tmp = object.returnObject(id, label, 'jqxTree');
		tmp.grid = $('#' + id.concat('-jqxtree') + '-'+ _id);
		return tmp;
	};
	
	OLBIUS.olbius().popup.addConfig('addJqxTree', function(config){
		var tmp = appendTree(config['id'], config['label'], this);
		var config_tree = {dropDownBtnWidth: config['width'], treeWidth: config['width'], value: config['value']};
		createJqxTreeDropDownBtn($(tmp.grid),$(tmp.element), config['source'], "tree", "treeChild", config_tree);
		$(tmp.grid).on('select', function(event){
			var item = $(tmp.grid).jqxTree('getItem', event.args.element);
			setDropDownContent(item, $(this), $(tmp.element));
		});
		
		return tmp;
	});
	
	function appendMonthYearSelection(id, label, object){
		var text = 
    		'<div id="?" class="span6" style="float:left; margin-bottom: 10px;">' +
	    		'<div id="?" style="margin-top: 5px;float: left;" class="align-right">' +
	        		label +
	        	'</div>' +
	        	'<div id="?" style="float: right">' +
	        		'<div id="?" style="display: inline-block; float: left; margin-right: 5px"></div>' + 
	        		'<div id="?" style="display: inline-block; float: left"></div>'
		        '</div>' +
        	'</div>';
		var element = object.jqxElement();
		var _id = object.id();
		OLBIUS.appendHtmlUUID(element, text, _id, [id.concat('-span'), id.concat('-text'), id, id.concat('-month'), id.concat('-year')]);
		var tmp = object.returnObject(id, label, 'monthYearSelection');
		tmp.monthEle = $('#' + id.concat('-month') + '-'+ _id);
		tmp.yearEle = $('#' + id.concat('-year') + '-'+ _id);
		return tmp;
	};
	
	
	function appendGridMulti(id, label, object){
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
		var tmp = object.returnObject(id, label, 'jqxGridMultiSource');
		tmp.grid = $('#' + id.concat('-jqxgrid') + '-'+ _id);
		return tmp;
	};
	
	OLBIUS.olbius().popup.addConfig('addJqxGridMultiSource', function(config){
		var dataSource = [];
		var dataDescription = [];
		var tmp = appendGridMulti(config['id'], config['label'], this);
		createJqxDropDownGrid($(tmp.grid), $(tmp.element), config);
		$(tmp.grid).on('rowselect', function(event){
			var args = event.args;
			if(typeof(args.rowindex) != 'number'){
				var rowBoundIndex = args.rowindex;
				if(rowBoundIndex.length == 0){
					dataSource = [];
					dataDescription = [];
				}else{
		    		for ( var x = 0; x < rowBoundIndex.length; x++) {
			    		var rowID = $(tmp.grid).jqxGrid('getRowId', x);
	    		        var data = $(tmp.grid).jqxGrid('getrowdatabyid', rowID);
	    		      //  console.log(data);
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
	});
	
	var createJqxDropDownGrid = function(grid_ele, dropdown_ele, config){
		dropdown_ele.jqxDropDownButton({
			width : config['width'],
			height : config['height'],
			theme : OLBIUS.getTheme()
		});
		for(var x in config['source'].datafields){
			if(config['source'].datafields[x].name == config['value']){
				config['source'].datafields[x].name = 'value';
			}else if(config['source'].datafields[x].name == config['text']){
				config['source'].datafields[x].name = 'text';
			}else{
				continue;
			}
		};
		
		for(var x in config['columns']){
			if(config['columns'][x].datafield == config['value']){
				config['columns'][x].datafield = 'value';
			}else if(config['columns'][x].datafield == config['text']){
				config['columns'][x].datafield = 'text';
			}else{
				continue;
			}
		};
		
		var dataAdapter = new $.jqx.dataAdapter(config['source'], {autoBind : true});
		
		var rendergridrows = function(params){
			var data = generatedata(params.startindex, params.endindex, params.data);
            return data;
		};
		
		var generatedata = function(startindex, endindex, data){
			var tmp = {};
			for(var i= startindex; i < endindex ; i++){
				var row = {};
				var keys = Object.keys(data[i - startindex]);
				for (var j = 0; j < keys.length; j++) {
					row[keys[j]] = data[i - startindex][keys[j]];
				}
				tmp[i] = row;
			}
			return tmp;
		};
		grid_ele.jqxGrid({
			width: 500,
			source : dataAdapter,
			pageable: true,
            autoheight: true,
            filterable: true,
            showfilterrow: true,
            columnsresize: true,
            selectionmode: 'checkbox',
            theme: OLBIUS.getTheme(),
            virtualmode : true,
            rendergridrows:function(obj) {
				return obj.data;
			},
            columns : config['columns']
		});
		
	};
	
	function setDropDownContent(element, jqxTree, dropdownBtn){
		var item = jqxTree.jqxTree('getItem', element);
		var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + item.label + '</div>';
		dropdownBtn.jqxDropDownButton('setContent', dropDownContent);
	};
	
	
	var createJqxTreeDropDownBtn = function(jqxTreeEle, dropdownBtnEle, rootPartyArr, suffix, suffixChild, config){
		var dropDownBtnWidth = typeof(config.dropDownBtnWidth) != "undefined"? config.dropDownBtnWidth: 250;
		var dropDownHeight = typeof(config.dropDownBtnHeight) != "undefined"? config.dropDownBtnHeight: 25;
		var treeWidth = typeof(config.treeWidth) != "undefined"? config.treeWidth: 250;
		var async = typeof(config.async) != "undefined"? config.async: true;
		var url = typeof(config.url) != "undefined"? config.url: "getListPartyRelByParent"; 
		dropdownBtnEle.jqxDropDownButton({ width: dropDownBtnWidth, height: 25, theme: 'olbius'});
		var dataTreeGroup = new Array();
		var textKey = typeof(config.textKey) != "undefined"? config.textKey : "partyName";
		var valueKey = typeof(config.textKey) != "undefined"? config.valueKey : "partyId";
		var parentKey = typeof(config.parentKey) != "undefined"? config.parentKey : "partyIdFrom";
		for(var i = 0; i < rootPartyArr.length; i++){
			dataTreeGroup.push({
	   				"id": rootPartyArr[i][valueKey] + "_" + suffix,
	   				"parentid": "-1",
	   				"text": rootPartyArr[i][textKey],
	   				"value": rootPartyArr[i][valueKey]
	   		});
	   		dataTreeGroup.push({
	   				"id": rootPartyArr[i][valueKey] +"_" + suffixChild,
	   				"parentid": rootPartyArr[i][valueKey] + "_" + suffix,
	   				"text": "Loading...",
	   				"value": url
	   		});
		}
		var source =
	    {
	        datatype: "json",
	        datafields: [
	            { name: 'id' },
	            { name: 'parentid' },
	            { name: 'text' },
	            { name: 'value' }
	        ],
	        id: 'id',
	        localdata: dataTreeGroup
	    };	
		
		var dataAdapter = new $.jqx.dataAdapter(source);
		dataAdapter.dataBind();
		var records = dataAdapter.getRecordsHierarchy('id', 'parentid', 'items', [{ name: 'text', map: 'label'}]);
		jqxTreeEle.jqxTree({source: records, width: treeWidth, theme: 'olbius'});
		//jqxTreeEle.jqxTree({source: records});
		createExpandEventJqxTree(jqxTreeEle, config.callbackGetExtData, async, config.expandCompleteFunc, parentKey);
		var element = $('#' + config['value'] + "_" + suffix)[0];
		jqxTreeEle.jqxTree('selectItem', element);
		setDropDownContent(element ,jqxTreeEle, dropdownBtnEle);
	};
	
	function createExpandEventJqxTree(jqxTreeDiv, callbackGetExtData, async, expandCompleteFunc, parentKey){
		if(typeof(async) == 'undefined'){
			async = true;
		}
		jqxTreeDiv.on('expand', function (event) {
			 var item = jqxTreeDiv.jqxTree('getItem', event.args.element);
			 var label = item.label;
			 var value = item.value;
			 var idItem = item.id;
			 var $element = $(event.args.element);
			 var loader = false;
			 var loaderItem = null;
	         var children = $element.find('ul:first').children();
	         $.each(children, function () {
	             var item = jqxTreeDiv.jqxTree('getItem', this);
	             if (item && item.label == 'Loading...') {
	                 loaderItem = item;
	                 loader = true;
	                 return false
	             };
	         });
	         if (loader) {
	        	var suffixIndex = idItem.lastIndexOf("_");
	        	var suffix = idItem.substring(suffixIndex);   
	        	var dataSubmit = {};
	        	dataSubmit[parentKey] = value;
	        	if(typeof(callbackGetExtData) != "undefined"){
	        		var extData = callbackGetExtData();
	        		$.extend(dataSubmit, extData);
	        	}
	            $.ajax({
	                 url: loaderItem.value,
	                 data: dataSubmit,
	                 type: 'POST',
	                 async: async,
	                 success: function (data, status, xhr){ 
	                	var listReturn = data.listReturn;
	                	if(listReturn){
	                		for(var i = 0; i < listReturn.length; i++){
	                			var id = listReturn[i].id;
	                			if(id){
	                				listReturn[i].id = id + suffix;
	                				listReturn[i].parentid = idItem;                			 
	                			}
	                		}
	                		var items = jQuery.parseJSON(JSON.stringify(data.listReturn));
	                		jqxTreeDiv.jqxTree('addTo', items, $element[0]);
	                		jqxTreeDiv.jqxTree('removeItem', loaderItem.element);	                     
	                	}
	                 },
	                 complete: function(jqXHR, textStatus){
	                	 if(typeof(expandCompleteFunc) != 'undefined'){
	                		 expandCompleteFunc();
	                	 }
	                 }
	             });
	         }
		});
	}
	
}(window, OLBIUS));