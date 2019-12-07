$(function(){
	InvForecastObj.init();
});
var InvForecastObj = (function(){
	var validatorVAL;
	
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
		initInventoryGrid();
	};
	var initInputs = function(){
		$("#fromDate").jqxDateTimeInput({width: 250, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false, theme: theme});
		$("#fromDate").jqxDateTimeInput('clear');
		$("#thruDate").jqxDateTimeInput({width: 250, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false, theme: theme});
		$("#thruDate").jqxDateTimeInput('clear');
		$('#facilityId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 250, dropDownHeight: 150, source: facilityData, theme: theme, displayMember: 'description', valueMember: 'facilityId',});
		$("#includeExpiredId").jqxCheckBox({ width: 120, height: 25, checked: true});
	};
	
	var initElementComplex = function(){
		
	};
	
	var initValidateForm = function(){
		var extendRules = [
          ];
   		var mapRules = [
   	            {input: '#fromDate', type: 'validInputNotNull'},
   				{input: '#thruDate', type: 'validInputNotNull'},
               ];
   		validatorVAL = new OlbValidator($('#formInfo'), mapRules, extendRules, {position: 'right'});
	};
	var initEvents = function(){
		$("#okButton").click(function (event) {
			var resultValidate = !validatorVAL.validate();
			if(resultValidate) return false;
			
			var fromTmp = $("#fromDate").jqxDateTimeInput('val', 'date');
			var thruTmp = $("#thruDate").jqxDateTimeInput('val', 'date');
			var include = $("#includeExpiredId").val();
			var checkExpiredDate = "N";
			if (include == true){
				checkExpiredDate = "N";
			} else {
				checkExpiredDate = "Y";
			}
			var facilityId = $('#facilityId').jqxDropDownList('val');
			Loading.show('loadingMacro');
			setTimeout(function(){
//				jQuery.ajax({
//					url: "inventoryForecast",
//					type: "POST",
//					async: false,
//					data: {
//						fromDate: fromTmp.getTime(),
//						thruDate: thruTmp.getTime(),
//						checkExpiredDate: checkExpiredDate,
//						,
//					},
//					success: function(res) {
//						listProducts = res.listProducts;
//			   	  	}
//				}).done(function(){
					var tmpS = $("#jqxgridInventoryForecast").jqxGrid('source');
				    tmpS._source.url = "jqxGeneralServicer?sname=jqGetInventoryForecast&fromDate=" + fromTmp.getTime() + "&thruDate=" + thruTmp.getTime() + "&checkExpiredDate=" + checkExpiredDate + "&facilityId=" + facilityId;
				    $("#jqxgridInventoryForecast").jqxGrid('source', tmpS);
//				});
				Loading.hide('loadingMacro');
        	}, 500);
		});
	};
	
	var getDataField = function(){
		var datafield = [{ name: 'productId', type: 'string' },
						{ name: 'productCode', type: 'string' },
						{ name: 'productName', type: 'string' },
						{ name: 'quantityUomId', type: 'string' },
						{ name: 'openingQuantity', type: 'number' },
						{ name: 'endingQuantity', type: 'number' },
						];
		return datafield;
	};
	
	var getColumns = function(grid){
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 150, editable: false, pinned: true,},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 150, editable:false,},
			{ text: uiLabelMap.Unit, dataField: 'quantityUomId', width: 150, editable:false, filterable:true,filtertype: 'checkedlist',
				cellsrenderer: function(row, column, value){
					var data =  $("#jqxgridInventoryForecast").jqxGrid('getrowdata', row);
					var descriptionUom = value;
					for(var i = 0; i < quantityUomData.length; i++){
						if(value == quantityUomData[i].quantityUomId){
							descriptionUom = quantityUomData[i].description;
					 	}
					}
					return '<span style=\"text-align: right\">' + descriptionUom + '</span>';
				},
				createfilterwidget: function (column, columnElement, widget) {
					var filterDataAdapter = new $.jqx.dataAdapter(quantityUomData, {
						autoBind: true
					});
					var records = filterDataAdapter.records;
					widget.jqxDropDownList({source: records, displayMember: 'quantityUomId', valueMember: 'quantityUomId', dropDownWidth: 'auto', autoDropDownHeight: 'auto',
						renderer: function(index, label, value){
				        	if (quantityUomData.length > 0) {
								for(var i = 0; i < quantityUomData.length; i++){
									if(quantityUomData[i].quantityUomId == value){
										return '<span>' + quantityUomData[i].description + '</span>';
									}
								}
							}
							return value;
						}
					});
					widget.jqxDropDownList('checkAll');
	   			}
			},
			{ text: uiLabelMap.OpeningInventoryQuantity, dataField: 'openingQuantity',width: 200, editable:false, filtertype: 'number', sortable: false,
				cellsrenderer: function(row, column, value){
					return '<span style=\"text-align: right\">' + value.toLocaleString(localeStr) + '</span>';
				},
			},
			{ text: uiLabelMap.EndingInventoryQuantity, dataField: 'endingQuantity', width: 200, editable:false, filtertype: 'number', sortable: false,
				cellsrenderer: function(row, column, value){
					return '<span style=\"text-align: right\">' + value.toLocaleString(localeStr) + '</span>';
				},
			},
        ];
		return columns;
	};
	
	var initInventoryGrid = function(){
		var grid = $("#jqxgridInventoryForecast");
		var datafield = getDataField();
		var columns = getColumns(grid);
		var config = {
		   		width: '100%', 
		   		virtualmode: true,
		   		filterable: false,
		   		showtoolbar: false,
		   		selectionmode: 'singlerow',
		   		pageable: true,
		   		sortable: true,
		        filterable: true,	        
		        editable: false,
		        rowsheight: 26,
		        url: '',                
		        source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var getLocalization = function () {
	    var localizationobj = {};
	    localizationobj.pagergotopagestring = uiLabelMap.wgpagergotopagestring + ":";
	    localizationobj.pagershowrowsstring = uiLabelMap.wgpagershowrowsstring + ":";
	    localizationobj.pagerrangestring = uiLabelMap.wgpagerrangestring;
	    localizationobj.pagernextbuttonstring = uiLabelMap.wgpagernextbuttonstring;
	    localizationobj.pagerpreviousbuttonstring = uiLabelMap.wgpagerpreviousbuttonstring;
	    localizationobj.sortascendingstring = uiLabelMap.wgsortascendingstring;
	    localizationobj.sortdescendingstring = uiLabelMap.wgsortdescendingstring;
	    localizationobj.sortremovestring = uiLabelMap.wgsortremovestring;
	    localizationobj.emptydatastring = uiLabelMap.wgemptydatastring;
	    localizationobj.filterselectstring = uiLabelMap.wgfilterselectstring;
	    localizationobj.filterselectallstring = uiLabelMap.wgfilterselectallstring;
	    localizationobj.filterchoosestring = uiLabelMap.filterchoosestring;
	    localizationobj.groupsheaderstring = uiLabelMap.wgdragDropToGroupColumn;
	    localizationobj.todaystring = uiLabelMap.wgtodaystring;
	    localizationobj.clearstring = uiLabelMap.wgclearstring;
	    return localizationobj;
	};
	return {
		init: init,
	}
}());