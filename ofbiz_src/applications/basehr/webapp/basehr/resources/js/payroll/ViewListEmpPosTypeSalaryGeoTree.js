var elementCheckInJqxTreeGeo = [];

var globalObjectCreateTree = (function(){
	var setDropDownContent = function(jqxTree, dropdownBtn){
		var items = jqxTree.jqxTree('getCheckedItems');
		var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">';	
		for(var i = 0; i < items.length; i++){
			dropDownContent += items[i].label;
			if(i < items.length - 1){
				dropDownContent += ", ";
			}
		}
		dropDownContent += '</div>';	
		dropdownBtn.jqxDropDownButton('setContent', dropDownContent);
	};
	
	var checkSelectedValid = function(idSelected, parentIdSelected){
		for(var i = 0; i < parentIdSelected.length; i++){
			if(idSelected.indexOf(parentIdSelected[i]) > -1){
				return false;
			}
		}
		return true;
	};
	
	var substringBySeparator = function(str, separator){
		if(str.indexOf(separator) > -1){
			var separatorLastIndexOf = str.lastIndexOf(separator);
			return str.substring(0, separatorLastIndexOf);		
		}else{
			return str;		
		}
	};
	
	var createJqxTree = function(data, treeEle){
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
	        localdata: data
	    };	
		 // create data adapter.
	    var dataAdapter = new $.jqx.dataAdapter(source);
	    // perform Data Binding.
	    dataAdapter.dataBind();
	    var records = dataAdapter.getRecordsHierarchy('id', 'parentid', 'items', [{ name: 'text', map: 'label'}]);
	    treeEle.jqxTree({ source: records, width: '100%', height: "200px", theme: 'energyblue', checkboxes: true});
	}

	var addEventToTreeEle = function(tree, btnDropDown){
		tree.on('expand', function (event) {
			 var item = tree.jqxTree('getItem', event.args.element);
			 var label = item.label;
			 var value = item.value;
			 var idItem = item.id;
			 var $element = $(event.args.element);
			 var loader = false;
			 var loaderItem = null;
	         var children = $element.find('ul:first').children();
	         $.each(children, function () {
	             var item = tree.jqxTree('getItem', this);
	             if (item && item.label == 'Loading...') {
	                 loaderItem = item;
	                 loader = true;
	                 return false
	             };
	         });
	         if (loader) {
	        	var suffixIndex = idItem.lastIndexOf("_");
	        	var suffix = idItem.substring(suffixIndex);        	        	
	            $.ajax({
	                 url: loaderItem.value,
	                 data: {geoId: value},
	                 async: true,
	                 success: function (data, status, xhr) { 
	                	 var listGeoAssoc = data.listGeoAssoc;
	                	 for(var i = 0; i < listGeoAssoc.length; i++){
	                		 var id = listGeoAssoc[i].id;
	                		 if(id){
	                			 listGeoAssoc[i].id = id + suffix;
	                    		 listGeoAssoc[i].parentid = idItem;                			 
	                		 }
	                	 }
	                     var items = jQuery.parseJSON(JSON.stringify(data.listGeoAssoc));
	                     tree.jqxTree('addTo', items, $element[0]);
	                     tree.jqxTree('removeItem', loaderItem.element);
	                     checkItemIfExists(tree);
	                 },
	                 complete: function(){
	                 }
	             });
	         }else{
	        	 checkItemIfExists(tree);
	         }
		});
		
		tree.on('checkChange', function(event){
			setDropDownContent(tree, btnDropDown);
		});
	};
	
	function checkItemIfExists(treeCheckEle){
		for(var i = 0; i < elementCheckInJqxTreeGeo.length; i++){
			var element = $("#" + elementCheckInJqxTreeGeo[i])[0];
			var item = treeCheckEle.jqxTree('getItem', element);
			if(item){
				treeCheckEle.jqxTree("checkItem", element, true);
			}
		}
	}
	
	return{
		setDropDownContent: setDropDownContent,
		checkSelectedValid: checkSelectedValid,
		substringBySeparator: substringBySeparator,
		addEventToTreeEle: addEventToTreeEle,
		createJqxTree: createJqxTree
	}
}());

var createGeoTreeForCreateNewObject = (function(){
	var init = function(){
		initJqxButtonTree();
	};
	
	var initJqxButtonTree = function(){
		$("#jqxBtnIncludeGeo").jqxDropDownButton({ width: '98%', height: 25, theme: 'olbius'});
		$("#jqxBtnExcludeGeo").jqxDropDownButton({ width: '98%', height: 25, theme: 'olbius'});
		
		var dataIncludeGeo = prepareDataTreeGeoObject.getDataIncludeGeo();
		var dataExcludeGeo = prepareDataTreeGeoObject.getDataExcludeGeo();
		
		globalObjectCreateTree.createJqxTree(dataIncludeGeo, $("#jqxTreeIncludeGeo"));
		globalObjectCreateTree.createJqxTree(dataExcludeGeo, $("#jqxTreeExcludeGeo"));
		globalObjectCreateTree.addEventToTreeEle($("#jqxTreeIncludeGeo"), $("#jqxBtnIncludeGeo"));
		globalObjectCreateTree.addEventToTreeEle($("#jqxTreeExcludeGeo"), $("#jqxBtnExcludeGeo"));
	};

	
	var getSelectedGeoInclude = function(){
		var includeSelectedGeoArr = new Array();
		var includeSelectedGeoItems = $("#jqxTreeIncludeGeo").jqxTree('getCheckedItems');
		for(var i = 0; i < includeSelectedGeoItems.length; i++){
			includeSelectedGeoArr.push({"includeGeoId": includeSelectedGeoItems[i].value});
		}
		return includeSelectedGeoArr;
	};

	var getSelectedGeoExclude = function(){
		var excludeSelectedGeoArr = new Array();
		var excludeSelectedGeoItems = $("#jqxTreeExcludeGeo").jqxTree('getCheckedItems');
		for(var i = 0; i < excludeSelectedGeoItems.length; i++){
			excludeSelectedGeoArr.push({"excludeGeoId": excludeSelectedGeoItems[i].value});
		}
		return excludeSelectedGeoArr;
	};
	
	var addValidator = function(){
		rules = [{input: "#jqxBtnIncludeGeo", message: uiLabelMap.NotChooseOrChooseInvalid, action: 'blur',
			rule: function(input, commit){
					var parentIdSelected = new Array();
					var idSelected = new Array();
					var items = $("#jqxTreeIncludeGeo").jqxTree('getCheckedItems');
					if(items.length == 0){
						return false;
					}
					for(var i = 0; i < items.length; i++){
						idSelected.push(substringBySeparator(items[i].id, "_"));
						if(items[i].parentId && items[i].parentId != "-1"){
							if(parentIdSelected.indexOf(items[i].parentId)  == -1){
								parentIdSelected.push(substringBySeparator(items[i].parentId, "_"))	
							}
						}
					}
					var check = checkSelectedValid(idSelected, parentIdSelected);
					return check;
				}				
			},
			{input: '#jqxBtnExcludeGeo', message: uiLabelMap.ChooseInvalid, action: 'blur',
				rule: function(input, commit){
					var parentIdSelected = new Array();
					var idSelected = new Array();
					var items = $("#jqxTreeExcludeGeo").jqxTree('getCheckedItems');
					if(items.length == 0){
						return true;
					}
					var itemsInclude = $("#jqxTreeIncludeGeo").jqxTree('getCheckedItems');
					var itemsIncludeArr = new Array();
					for(var i = 0; i < itemsInclude.length; i++){
						itemsIncludeArr.push(substringBySeparator(itemsInclude[i].id, "_"));
					}
					
					for(var i = 0; i < items.length; i++){
						idSelected.push(substringBySeparator(items[i].id, "_"));
						if(items[i].parentId && items[i].parentId != "-1"){
							if(parentIdSelected.indexOf(items[i].parentId)  == -1){
								parentIdSelected.push(substringBySeparator(items[i].parentId, "_"))	
							}
						}else{
							parentIdSelected.push("-1");
						}
					}
					//if select of excludeGeo not child of includeGeo return false
					for(var i = 0; i < parentIdSelected.length; i++){
						if(itemsIncludeArr.indexOf(parentIdSelected[i]) == -1){
							return false;
						}
					}
					var check = checkSelectedValid(idSelected, parentIdSelected);
					return check;
				}
			}
		];
		return rules;
	};
	
	
	var uncheckAll = function(){
		$("#jqxTreeExcludeGeo, #jqxTreeIncludeGeo").jqxTree('uncheckAll');
		$("#jqxTreeExcludeGeo, #jqxTreeIncludeGeo").jqxTree('collapseAll');
	};
	return{
		init: init,
		addValidator: addValidator,
		getSelectedGeoExclude: getSelectedGeoExclude,
		getSelectedGeoInclude: getSelectedGeoInclude,
		uncheckAll: uncheckAll
	}
}());

var createGeoTreeForEditObject = (function(){
	var init = function(){
		initJqxButtonTree();
	};
	
	var initJqxButtonTree = function(){
		$("#jqxBtnIncludeGeoEdit").jqxDropDownButton({ width: '98%', height: 25, theme: 'olbius'});
		$("#jqxBtnExcludeGeoEdit").jqxDropDownButton({ width: '98%', height: 25, theme: 'olbius'});
		var dataIncludeGeoEdit = prepareDataTreeGeoObject.getDataIncludeGeoEdit()
		var dataExcludeGeoEdit = prepareDataTreeGeoObject.getDataIncludeGeoEdit();
		globalObjectCreateTree.createJqxTree(dataIncludeGeoEdit, $("#jqxTreeIncludeGeoEdit"));
		globalObjectCreateTree.createJqxTree(dataExcludeGeoEdit, $("#jqxTreeExcludeGeoEdit"));
		globalObjectCreateTree.addEventToTreeEle($("#jqxTreeIncludeGeoEdit"), $("#jqxBtnIncludeGeoEdit"));
		globalObjectCreateTree.addEventToTreeEle($("#jqxTreeExcludeGeoEdit"), $("#jqxBtnExcludeGeoEdit"));
	};
	
	var initDataJqxTreeInWindow = function(data){
		elementCheckInJqxTreeGeo = [];
		$.ajax({
			url: 'getEmplPositionTypeRateGeoAppl',
			data: {emplPositionTypeRateId: data.emplPositionTypeRateId},
			type: 'POST',
			success: function(data){	
				if(data.checkIncludeGeoList){
					var checkIncludeGeoList = data.checkIncludeGeoList; 
					for(var i = 0; i < checkIncludeGeoList.length; i++){
						var element = $("#" + checkIncludeGeoList[i] + "_includeEdit")[0];
						var item = $('#jqxTreeIncludeGeoEdit').jqxTree('getItem', element);
						if(item){
							$("#jqxTreeIncludeGeoEdit").jqxTree("checkItem", item, true);							
						}else{
							elementCheckInJqxTreeGeo.push(checkIncludeGeoList[i] + "_includeEdit");	
						}
					}
					
				}
				if(data.checkExcludeGeoList){
					var checkExcludeGeoList = data.checkExcludeGeoList;
					for(var i = 0; i < checkExcludeGeoList.length; i++){
						var element = $("#" + checkExcludeGeoList[i] + "_excludeEdit")[0];
						var item = $('#jqxTreeExcludeGeoEdit').jqxTree('getItem', element);
						if(item){
							$("#jqxTreeExcludeGeoEdit").jqxTree("checkItem", item, true);							
						}else{
							elementCheckInJqxTreeGeo.push(checkExcludeGeoList[i] + "_excludeEdit");	
						}
					}
				}
				if(data.expandIncludeGeoList){
					var expandIncludeGeoList = data.expandIncludeGeoList; 
					for(var i = 0; i < expandIncludeGeoList.length; i++){
						$("#jqxTreeIncludeGeoEdit").jqxTree("expandItem", $("#" + expandIncludeGeoList[i] + "_includeEdit")[0]);
					}
				}
				if(data.expandExcludeGeoList){
					var expandExcludeGeoList = data.expandExcludeGeoList; 
					for(var i = 0; i < expandExcludeGeoList.length; i++){
						$("#jqxTreeExcludeGeoEdit").jqxTree("expandItem", $("#" +expandExcludeGeoList[i] + "_excludeEdit")[0]);
					}
				}
			}
		});
	};
	
	var getSelectedGeoInclude = function(){
		var includeSelectedGeoArr = new Array();
		var includeSelectedGeoItems = $("#jqxTreeIncludeGeoEdit").jqxTree('getCheckedItems');
		for(var i = 0; i < includeSelectedGeoItems.length; i++){
			includeSelectedGeoArr.push({"includeGeoId": includeSelectedGeoItems[i].value});
		}
		return includeSelectedGeoArr;
	};

	var getSelectedGeoExclude = function(){
		var excludeSelectedGeoArr = new Array();
		var excludeSelectedGeoItems = $("#jqxTreeExcludeGeoEdit").jqxTree('getCheckedItems');
		for(var i = 0; i < excludeSelectedGeoItems.length; i++){
			excludeSelectedGeoArr.push({"excludeGeoId": excludeSelectedGeoItems[i].value});
		}
		return excludeSelectedGeoArr;
	};
	
	var addValidator = function(){
		rules = [
			{input: "#jqxBtnIncludeGeoEdit", message: uiLabelMap.NotChooseOrChooseInvalid, action: 'blur',
				rule: function(input, commit){
					var parentIdSelected = new Array();
					var idSelected = new Array();
					var items = $("#jqxTreeIncludeGeoEdit").jqxTree('getCheckedItems');
					if(items.length == 0){
						return false;
					}
					for(var i = 0; i < items.length; i++){
						idSelected.push(substringBySeparator(items[i].id, "_"));
						if(items[i].parentId && items[i].parentId != "-1"){
							if(parentIdSelected.indexOf(items[i].parentId)  == -1){
								parentIdSelected.push(substringBySeparator(items[i].parentId, "_"))	
							}
						}
					}
					var check = checkSelectedValid(idSelected, parentIdSelected);
					return check;
				}				
			},
			{input: '#jqxBtnExcludeGeoEdit', message: uiLabelMap.ChooseInvalid, action: 'blur',
				rule: function(input, commit){
					var parentIdSelected = new Array();
					var idSelected = new Array();
					var items = $("#jqxTreeExcludeGeoEdit").jqxTree('getCheckedItems');
					if(items.length == 0){
						return true;
					}
					var itemsInclude = $("#jqxTreeIncludeGeoEdit").jqxTree('getCheckedItems');
					var itemsIncludeArr = new Array();
					for(var i = 0; i < itemsInclude.length; i++){
						itemsIncludeArr.push(substringBySeparator(itemsInclude[i].id, "_"));
					}
					
					for(var i = 0; i < items.length; i++){
						idSelected.push(substringBySeparator(items[i].id, "_"));
						if(items[i].parentId && items[i].parentId != "-1"){
							if(parentIdSelected.indexOf(items[i].parentId)  == -1){
								parentIdSelected.push(substringBySeparator(items[i].parentId, "_"))	
							}
						}else{
							parentIdSelected.push("-1");
						}
					}
					//if select of excludeGeo not child of includeGeo return false
					for(var i = 0; i < parentIdSelected.length; i++){
						if(itemsIncludeArr.indexOf(parentIdSelected[i]) == -1){
							return false;
						}
					}
					var check = checkSelectedValid(idSelected, parentIdSelected);
					return check;
				}
			}	          
		];
		return rules;
	};
	
	var uncheckAll = function(){
		$("#jqxTreeExcludeGeoEdit, #jqxTreeIncludeGeoEdit").jqxTree('uncheckAll');
		$("#jqxTreeExcludeGeoEdit, #jqxTreeIncludeGeoEdit").jqxTree('collapseAll');
	};
	
	return{
		init: init,
		addValidator: addValidator,
		getSelectedGeoExclude: getSelectedGeoExclude,
		getSelectedGeoInclude: getSelectedGeoInclude,
		initDataJqxTreeInWindow: initDataJqxTreeInWindow,
		uncheckAll: uncheckAll
	}
}());

$(document).ready(function () {
	createGeoTreeForCreateNewObject.init();
	createGeoTreeForEditObject.init();
});

