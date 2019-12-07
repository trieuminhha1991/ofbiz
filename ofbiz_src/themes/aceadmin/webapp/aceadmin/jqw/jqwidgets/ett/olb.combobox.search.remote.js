/**
 * Create object jqxComboBox quick search (F1)
 */
var OlbComboBoxSearchRemote = function(p_comboBoxObj, p_config){
	var _comboBoxObj;
	var _config;
	var _selectedIndex_def; // value default
	var _selectArr_def;
	var _seft = this;
	var _datafields;
	var _columns;
	var _multiSelect = false;
	var _type = "comboBoxSearchRemote";
	var _root = "root";
	var _handlerSelectedItem;
	
	/**
     * Initialize Combo box
     */
	function init(comboBoxObj, config){
		if (typeof(config) == undefined) config = [];
		_comboBoxObj = comboBoxObj;
		_config = config;
		_root = _config.root;
		
		// prepare data default
		if (typeof _config.displayMember == "undefined") _config.displayMember = "id";
		if (typeof _config.placeHolder == "undefined") _config.placeHolder = " Enter to search (F1)";
		if (typeof _config.messageItemNotFound == "undefined") _config.messageItemNotFound = "Item not found!";
		if (typeof _config.handlerSelectedItem == "function") _handlerSelectedItem = _config.handlerSelectedItem;
		if (typeof _handlerSelectedItem == "undefined") _handlerSelectedItem = function(item){};
		
		// prepare parameter
		var formatData = _config.formatData;
		var formatDataFuncItem = _config.formatDataFuncItem;
		if (typeof _config.formatData == "undefined") {
			formatData = function (data) {
				var searchStringValue = $(_comboBoxObj).jqxComboBox('val'); //$(_comboBoxObj).jqxComboBox('searchString');
	        	if (searchStringValue != undefined) {
	        		searchStringValue = searchStringValue.trim();
		            if (typeof formatDataFuncItem == "function") {
		            	return formatDataFuncItem(data, searchStringValue);
		            }
		            var keySearchString = typeof _config.keySearchString == "undefined" ? _config.keySearchString : "searchString";
		            data[keySearchString] = searchStringValue;
		            return data;
	            }
	        };
		}
		var renderer = _config.renderer;
		if (typeof renderer == "undefined") {
			renderer = function (index, label, value) {
	        	var item = dataAdapter.records[index];
	            if (item != null) {
	            	return _config.rendererFuncItem(item);
            	}
            	return "";
			}
		}
		var renderSelectedItem = _config.renderSelectedItem;
		if (typeof renderSelectedItem == "undefined") {
			renderSelectedItem = function(index, item) {
	        	var item = dataAdapter.records[index];
	            if (item != null) {
	           		var label = item[_config.displayMember];
	                return label;
	            }
	            return "";
	        }
		}
		
		var sourceProduct = {
		    datatype: "json",
		    datafields: _config.datafields,
		    type: "POST",
		    root: _root,
		    contentType: 'application/x-www-form-urlencoded',
		    url: _config.url
		};
		
		var theme = typeof(_config.theme) != "undefined" ? _config.theme : OlbCore.theme;
		var dataAdapter = new $.jqx.dataAdapter(sourceProduct, {
	    	downloadComplete: function (data, status, xhr) {
	     		if (OlbCore.isNotEmpty(data[_root]) && data[_root].length < 2) {
	     			$(_comboBoxObj).jqxComboBox({autoShowListBox: false});
	     		} else {
	     			$(_comboBoxObj).jqxComboBox({autoShowListBox: true}); 
	     		}
	     		//disableKeyOnInput();
	        },
	    	formatData: formatData
		});
		$(_comboBoxObj).keypress(function(e) {
		    if (e.which == 13) {
		    	if (typeof _config.beforeSearchHandler == 'function') {
		    		var resultHandler = _config.beforeSearchHandler();
		    		if (!resultHandler) return resultHandler;
		    	}
		    	dataAdapter.dataBind();
		    }
		});
		
		// initialization comboBox
	    _comboBoxObj.jqxComboBox({
	    	theme: theme,
	    	width: 290,
	    	dropDownWidth: 600,
	        placeHolder: _config.placeHolder,
	        showArrow: false,
	        height: 30,
	        source: dataAdapter,
	        remoteAutoComplete: true,
	        minLength: 0,
	        selectedIndex: 0,
	        displayMember: _config.displayMember,
	        valueMember: typeof _config.valueMember != "undefined" ? _config.valueMember : "id",
	        scrollBarSize: 15,
	        autoComplete: true,
	        renderer: renderer,
	        renderSelectedItem: renderSelectedItem,
	        search: function (searchString) {
       	 		//dataAdapter.dataBind();
	        }
	    });
	    
	    // initialization event
	    initEvent();
	}
	
	function initEvent() {
		$('body').keydown(function(e) {
			//112 F1
			$(window).keydown(function(e){
				var code = (e.keyCode ? e.keyCode : e.which);
				if(code == 112){e.preventDefault();}
			});
			var code = (e.keyCode ? e.keyCode : e.which);
			if (code == 112) {
				//if (flagPopup){
				_seft.focusSearch();
				//}
				e.preventDefault();
				return false;
			}
		});
	    $(_comboBoxObj).on('bindingComplete', function (event) {
			var items = $(_comboBoxObj).jqxComboBox('getItems');
		    $(_comboBoxObj).jqxComboBox({ autoShowListBox: false });
	        if (items && items.length == 1){
        		var firstItem = items[0];
        		if (firstItem) {
        			firstItem = firstItem.originalItem;
        			if (firstItem) {
        				_handlerSelectedItem(firstItem);
        				_seft.focusSearch();
        			}
                }
	        } else if (items && items.length == 0) {
	        	bootbox.alert(_config.messageItemNotFound, function() {
					$(_comboBoxObj).jqxComboBox("focus");
				});
	        }
	        disableKeyOnInput();
		});
	    
		$(_comboBoxObj).on('close', function (event) {
    		var item = $(_comboBoxObj).jqxComboBox('getSelectedItem'); 
			if(item != undefined){
	    		item = item.originalItem;
	    		if(item){
	    			_handlerSelectedItem(item);
	        		$(_comboBoxObj).jqxComboBox({ disabled: false }); 
	        		$(_comboBoxObj).jqxComboBox('clearSelection');
	    		}
	    	}
	    });
	}
	
	function disableKeyOnInput(){
		$(_comboBoxObj).on('keydown', function (event) {
	    	if(event.keyCode === 38 || event.keyCode === 40) { //up or down
	            // focus to other element
	        	var e = $.Event('keydown');
	            e.keyCode = event.keyCode; 
	            $('body').trigger(e);
	            return false;
	        }
	    	if (event.keyCode === 13){ // enter
	    		var item = $(_comboBoxObj).jqxComboBox('getSelectedItem'); 
				if(item != undefined){
		    		item = item.originalItem;
		    		if(item){
		    			_handlerSelectedItem(item);
		        		$(_comboBoxObj).jqxComboBox({ disabled: false }); 
		        		$(_comboBoxObj).jqxComboBox('clearSelection');
		    		}
		    	}
	    	}
	        if(event.keyCode === 9){ // space
				event.preventDefault();
				return false;
	        }
	    });
	}
	
	this.focusSearch = function() {
		$(_comboBoxObj).jqxComboBox('clearSelection');
		$(_comboBoxObj).jqxComboBox('close');
		$(_comboBoxObj).jqxComboBox('focus');
		return false;
	}
	
	return init(p_comboBoxObj, p_config);
}
