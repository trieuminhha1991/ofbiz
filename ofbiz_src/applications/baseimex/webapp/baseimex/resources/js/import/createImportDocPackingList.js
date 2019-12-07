$(function(){
	PackingListObj.init();
});
var PackingListObj = (function() {
	
	var validatorPKL = null;
	var containerSelected = null;
	var gridAgreement = $('#jqxGridAgreement');
	var gridProduct = $('#jqxgridPackingListDetail');
	
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	
	var initInputs = function (){
		
		$("#orderNumberSupp").jqxInput({
			height : 25,
			width : 295,
			minLength : 1,
			theme : theme
		});
		$("#invoiceNumber").jqxInput({
			height : 25,
			width : 295,
			minLength : 1,
			theme : theme
		});
		$("#packingListNumber").jqxInput({
			height : 25,
			width : 295,
			minLength : 1,
			theme : theme
		});

		$("#totalNetWeight").jqxNumberInput({
			width : 300,
			height : '25px',
			spinButtons : true,
			theme : theme
		});
		$("#totalGrossWeight").jqxNumberInput({
			width : 300,
			height : '25px',
			spinButtons : true,
			theme : theme
		});

		$("#packingListDate").jqxDateTimeInput({
			width : 300,
			theme : theme
		});
		$("#invoiceDate").jqxDateTimeInput({
			width : 300,
			theme : theme
		});
		$('#packingListDate').jqxDateTimeInput('clear');
		$('#invoiceDate').jqxDateTimeInput('clear');

		$("#agreement").jqxDropDownButton({width: 300, theme: theme}); 
		$('#agreement').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
	}
	
	var initGridProduct = function (grid) {
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
	        	  groupable: false, draggable: false, resizable: false,
	        	  datafield: '', columntype: 'number', width: 50,
	        	  cellsrenderer: function (row, column, value) {
	        		  return '<div style="cursor: pointer;">' + (value + 1) + '</div>';
	        	  }
	      	},
			{ text: uiLabelMap.ProductId, datafield: 'productCode', editable: false, width: 120,
				cellsrenderer: function(row, colum, value){
				},
			},
			{ text: uiLabelMap.ProductName, datafield: 'productName', editable: false, minwidth: 150,
				cellsrenderer: function(row, colum, value){
				},
			},
			{ text: uiLabelMap.originOrderUnit, datafield: 'originOrderUnit', width: 120, editable: false, cellsalign: 'right', 
				cellsrenderer: function(row, colum, value){
		     		   return '<div class="align-right">' +formatnumber(value)+ '</div>';
				}
			},
			{ text: uiLabelMap.packingUnits, datafield: 'packingUnit', width: 120, editable: true, columntype:'numberinput', cellsalign: 'right', cellclassname: productGridCellclass,
				cellsrenderer: function(row, colum, value){
	     		   return '<div class="align-right">' +formatnumber(value)+ '</div>';
				}
			},
			{ text: uiLabelMap.orderUnits, datafield: 'orderUnit', width: 120, editable: true, columntype:'numberinput', cellsalign: 'right', cellclassname: productGridCellclass,
				cellsrenderer: function(row, colum, value){
		     		   return '<div class="align-right">' +formatnumber(value)+ '</div>';
				}
			},
			{ text: uiLabelMap.dateOfManufacture, datafield: 'datetimeManufactured', width: 120, editable: true, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy',  cellclassname: productGridCellclass,
				initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
	        		var uid = gridProduct.jqxGrid('getrowid', row);
	        		editor.on('change', function(event){
	        			var jsDate = event.args.date;
	        		});
				}
			},
			{ text: uiLabelMap.ProductExpireDate, datafield: 'expireDate', width: 120, editable: true, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy',  cellclassname: productGridCellclass,
				initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
	        		var uid = gridProduct.jqxGrid('getrowid', row);
	        		var datemanu = gridProduct.jqxGrid('getcellvaluebyid', uid, 'datetimeManufactured');
	        		if(datemanu && datemanu != null && datemanu != ''){
	        			editor.jqxDateTimeInput('setMinDate', datemanu);
	        		}else{
	        			
	        		}
				},
				validation: function (cell, value) {
					var uid = gridProduct.jqxGrid('getrowid', cell.row);
	        		var datemanu = gridProduct.jqxGrid('getcellvaluebyid', uid, 'datetimeManufactured');
			        if ((!value || value == null || value == '') && (datemanu && datemanu != null && datemanu != '')) {
			        	return { result: false};
			        }
			        return true;
			    }
			},
			{ text: uiLabelMap.batchNumber, datafield: 'batchNumber', width: 120, editable: true , cellclassname: productGridCellclass,},
        ];
		
		var datafield = [
		 	{ name: 'productId', type: 'string'},
		 	{ name: 'productCode', type: 'string'},
		 	{ name: 'productName', type: 'string'},
			{ name: 'packingListSeqId', type: 'string'},
			{ name: 'orderId', type: 'string'},
			{ name: 'orderItemSeqId', type: 'string'},
			{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
			{ name: 'expireDate', type: 'date', other: 'Timestamp'},
			{ name: 'batchNumber', type: 'string'},
			{ name: 'globalTradeItemNumber', type: 'string'},
			{ name: 'packingUnit', type: 'number'},
			{ name: 'packingUomId', type: 'string'},
			{ name: 'orderUnit', type: 'number'},
			{ name: 'originOrderUnit', type: 'number'},
			{ name: 'orderUomId', type: 'string'},
			{ name: 'quantity', type: 'number'},
			{ name: 'itemDescription', type: 'string'}
			]
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "Container";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.Product + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
		}; 
		
		var config = {
			width: '100%', 
	   		virtualmode: false,
	   		showtoolbar: true,
	   		rendertoolbar: rendertoolbar,
	   		selectionmode: 'singlerow',
	   		editmode: 'click',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: true,
	        rowsheight: 26,
	        rowdetails: false,
	        useUrl: false,
	        url: "",                
	        source: {pagesize: 10}
	  	};
	  	Grid.initGrid(config, datafield, columns, null, grid);
	}
	
	var productGridCellclass = function (row, column, value, data) {
		return 'background-prepare';
	}
	
	var initElementComplex = function() {
		initGridProduct(gridProduct);
		initGridAgreement(gridAgreement);
	}
	
	var getDataProductFromAgreement = function (agreementId){
		$.ajax({
	        url: "getAgreementProductAppls",
	        type: "POST",
	        async: false,
	        data: {agreementId: agreementId},
	        dataType: 'json',
	        success: function(res){
	        	if (res.listProducts){
	        		listProductSelected = res.listProducts;
	        		if (listProductSelected && listProductSelected.length > 0){
	        			for (var i in listProductSelected){
		        			listProductSelected[i]["originOrderUnit"] = listProductSelected[i].quantity;
		        			listProductSelected[i]["orderUnit"] = listProductSelected[i].quantity;
		        		}
	        			updateProductGridData(listProductSelected);
	        		}
	        	}
	        }
	    });
	}
	
	var updateProductGridData = function(data){
		var tmpS = gridProduct.jqxGrid("source");
		tmpS._source.localdata = data;
		gridProduct.jqxGrid("source", tmpS);
		gridProduct.jqxGrid("updatebounddata");
	}
	
	var initEvents = function (){
		gridAgreement.on('rowclick', function (event) {
	        var args = event.args;
	        var rowBoundIndex = args.rowindex;
	        var rowData = gridAgreement.jqxGrid('getrowdata', rowBoundIndex);
	        if (rowData){
	        	agreementSelected = {};
		        agreementSelected = $.extend({}, rowData);
		        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ rowData.agreementCode +'</div>';
		        $('#agreement').jqxDropDownButton('setContent', dropDownContent);
		        $("#agreement").jqxDropDownButton('close');
		        
	        	getDataProductFromAgreement(agreementSelected.agreementId);
	        }
		});
		
		gridAgreement.on('bindingcomplete', function (event) {
			if (agreementSelected != null){
				var rows = gridAgreement.jqxGrid('getrows');
				if (rows && rows.length > 0){
					for (var i in rows){
						if (rows[i].agreementId == agreementSelected.agreementId){
							var index = gridAgreement.jqxGrid('getrowboundindexbyid', rows[i].uid);
							agreementSelected.jqxGrid('selectrow', index);
							break;
						}
					}
				}
			}
		});
		
		gridProduct.on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				$.each(listProductSelected, function(i){
	   				var olb = listProductSelected[i];
	   				if (olb.productId == rowData.productId){
	   					listProductSelected.splice(i,1);
	   					return false;
	   				}
	   			});
				if ((dataField == 'orderUnit' && value > 0) || (dataField != 'orderUnit' && rowData.orderUnit > 0) ){
					var item = $.extend({}, rowData);
					item[dataField] = value;
					listProductSelected.push(item);
				}
			}
		});
	}
	
	var initGridAgreement = function(grid){
		var datafield =  [
			{ name: 'agreementId', type: 'string'},
			{ name: 'agreementCode', type: 'string'},
			{ name: 'attrValue', type: 'string'},
			{ name: 'statusId', type: 'string'},
		                  ];
		var columnlist = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.AgreementId, dataField: 'agreementCode', width: 120, editable: false, pinned: true,
				
			},
			{ text: uiLabelMap.AgreementName, dataField: 'attrValue', minwidth: 100, editable:false,},
			{ text: uiLabelMap.Status, dataField: 'statusId', width: 130, editable:false, filtertype: 'checkedlist',
				cellsrenderer: function(row, column, value) {
					return '<span>' + getStatusDesc(value) +'</span>';
				}, 
				createfilterwidget: function (column, columnElement, widget) {
					var filterDataAdapter = new $.jqx.dataAdapter(agreeStatusData, {
						autoBind: true
					});
					var records = filterDataAdapter.records;
					widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
						renderer: function(index, label, value){
							return '<span>' + getStatusDesc (value) + '</span>';
						}
					});
					widget.jqxDropDownList('checkAll');
	   			}
			},
			];
		var config = {
				width: 500, 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'singlerow',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: false,
				rowsheight: 26,
				rowdetails: false,
				useUrl: true,
				url: 'JQGetListPurchaseAgreements&statusId=AGREEMENT_COMPLETED&statusId=AGREEMENT_PROCESSING',                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initValidateForm = function (){
		var extendRules = [
			{
				input: '#agreement', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					if (agreement === null){
						return false;
					}
					return true;
				}
			},
		];
   		var mapRules = [
			{input: '#packingListNumber', type: 'validInputNotNull'},
			{input: '#invoiceNumber', type: 'validInputNotNull'},
			{input: '#orderNumberSupp', type: 'validInputNotNull'},
			{input: '#packingListDate', type: 'validInputNotNull'},
			{input: '#invoiceDate', type: 'validInputNotNull'},
			{input: '#totalNetWeight', type: 'validInputNotNull'},
			{input: '#totalGrossWeight', type: 'validInputNotNull'},
        ];
   		validatorPKL = new OlbValidator($("#PackingListForm"), mapRules, extendRules, {position: 'right'});
	}
	
	var getValidate = function (){
		if (validatorPKL){
			return validatorPKL.validate();
		}
		return true;
	}
	return {
		init: init,
		getValidate: getValidate,
	}
}());