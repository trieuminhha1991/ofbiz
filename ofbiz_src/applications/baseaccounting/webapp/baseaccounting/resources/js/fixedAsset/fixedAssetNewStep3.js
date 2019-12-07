var fixedAssetNewStep3 = (function(){
	var init = function(){
		initGrid();
		initEvent();
	};
	var initGrid = function(){
		var grid = $("#assetAccompanyGrid");
		var datafields = [
		      			{name: 'componentName', type: 'string'},
		      			{name: 'unit', type: 'string' },
		      			{name: 'quantity', type: 'number' },
		      			{name: 'value', type: 'number' },
		              ];
		var columns = [
					   {text: uiLabelMap.FixedAssetAccompanyName, filterable : false, datafield: 'componentName', width: '40%',
				        	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
				        		editor.jqxInput({width: cellwidth, height: cellheight});
				        	},
				        	initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
				        		if(typeof(cellvaue) != 'undefined'){
				        			editor.val(cellvaue)
				        		}
				        	}
						},
						{text: uiLabelMap.BSCalculateUomId, datafield: 'unit', width: '15%',
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
				        		editor.jqxInput({width: cellwidth, height: cellheight});
				        	},
				        	initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
				        		if(typeof(cellvaue) != 'undefined'){
				        			editor.val(cellvaue)
				        		}
				        	}
						},
						{text: uiLabelMap.BACCQuantity, datafield: 'quantity', columntype: 'numberinput', width: '15%',
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								editor.jqxNumberInput({width: cellwidth, height: cellheight, spinButtons: true, decimalDigits: 0});
							},
							initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
								if(typeof(cellvaue) != 'undefined'){
				        			editor.val(cellvaue)
				        		}
				        	},
				        	cellsrenderer: function(row, colum, value){
						  		if(typeof(value) == 'number'){
						  			return '<span style="text-align: right">' + (value) + '</value>';
						  		}
						  	},
						},                    	                     	 
						{text: uiLabelMap.BSValue, datafield: 'value', columntype: 'numberinput',
							cellsrenderer: function(row, colum, value){
						  		if(typeof(value) == 'number'){
						  			return '<span style="text-align: right">' + formatcurrency(value) + '</value>';
						  		}
						  	},
						  	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
						  		editor.jqxNumberInput({width: cellwidth, height: cellheight, spinButtons: true, decimalDigits: 2, max: 999999999999, digits: 12, inputMode: 'advanced'});
							},
							initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
								if(typeof(cellvaue) != 'undefined'){
				        			editor.val(cellvaue)
				        		}
				        	}
						},                    	                     	 
					];
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "assetAccompanyGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.FixedAssetAccompanyList + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(grid, container, uiLabelMap.accAddNewRow, {type: "direct"});
	        Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, "",
                    "", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
		};		
		var config = {
				url: '',
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '100%',
				virtualmode: false,
				editable: true,
				localization: getLocalization(),
				pageable: true,
				editmode: 'selectedcell',
				selectionmode: 'checkbox',
				source: {
					pagesize: 10,
					localdata: [],
				}
		};
		Grid.initGrid(config, datafields, columns, null, grid);
	};
	var initEvent = function(){
		$("#assetAccompanyGrid").on('deletecompleted', function(){
			$("#assetAccompanyGrid").jqxGrid('clearselection');
		});
	};
	var getData = function(){
		var rows = $("#assetAccompanyGrid").jqxGrid('getrows');
		var data = [];
		for(var i = 0; i < rows.length; i++){
			var rowData = rows[i];
			if(rowData.componentName && rowData.componentName.length > 0){
				var temp = {componentName: rowData.componentName};
				if(rowData.unit){
					temp.unit = rowData.unit;
				}
				if(typeof(rowData.quantity) == 'number'){
					temp.quantity = rowData.quantity;
				}
				if(typeof(rowData.value) == 'number'){
					temp.value = rowData.value;
				}
				data.push(temp);
			}
		}
		return data;
	};
	var resetData = function(){
		var source = $("#assetAccompanyGrid").jqxGrid('source');
		source._source.localdata = [];
		$("#assetAccompanyGrid").jqxGrid('source', source);
	};
	return{
		init: init,
		getData: getData,
		resetData: resetData
	}
}());
$(document).ready(function(){
	fixedAssetNewStep3.init();
});