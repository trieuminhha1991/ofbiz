OLBNewInvItem = function (){
};

OLBNewInvItem.prototype = {
		attr : {
			grid_item : $("#newInvoiceItemGrid"),
			invoiceType : $('#invoiceTypeId'),
			editor : {},
			INDEX : 0,
			SEQ : 1,
			ITEM_DATA : new Array(),
			string : new String()
		},
		getData : function(){
			return $("#newInvoiceItemGrid").jqxGrid('getrows');
		},
		init: function(){
			this.initGrid();
			this.initEvent();
		},
		initGrid:  function(){
	        var grid =  $("#newInvoiceItemGrid");
	        var datafield = [
	     		{name: 'invoiceItemSeqId', type: 'string'},
	            {name: 'invoiceItemTypeId', type: 'string'},
	            {name: 'invoiceItemTypeDesc', type: 'string'},
	            {name: 'productId', type: 'string' },
	            {name: 'productName', type: 'string' },
	            {name: 'quantity', type: 'number' },
	            {name: 'amount', type: 'number' },
	            {name: 'description', type: 'string' },
	            {name: 'currencyUomId', type: 'string' },
            ];
	        var columns = [
	   	                { text: uiLabelMap.BACCInvoiceItemSeqId,filterable : false, datafield: 'invoiceItemSeqId',  width: '7%', editable: false},
						{ text: uiLabelMap.BACCInvoiceItemType, filterable : false, datafield: 'invoiceItemTypeDesc',width: '19%'},
						{ text: uiLabelMap.BACCProduct, datafield: 'productName', width: '22%'},
						{ text: uiLabelMap.BACCQuantity, dataField: 'quantity', cellsformat: 'd', columntype: 'numberinput', width: '8%', filtertype: 'number',},                    	                     	 
						{ text: uiLabelMap.BACCUnitPrice, dataField: 'amount', cellsformat: 'd', columntype: 'numberinput', width: '13%', filtertype  :'number',
						  	cellsrenderer: function(row, colum, value){
						  		if(typeof(value) == 'number'){
						  			var data = $("#newInvoiceItemGrid").jqxGrid('getrowdata', row);
						  			return '<span>' + formatcurrency(value, data.currencyUomId) + '</value>';
						  		}
						  	}
						},
						{text: uiLabelMap.BACCDescription, dataField: 'description'}
			];
	        var rendertoolbar = function(toolbar){
				toolbar.html("");
				var id = "newInvoiceItemGrid";
				var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.BACCInvoiceItemList + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
				toolbar.append(jqxheader);
		     	var container = $('#toolbarButtonContainer' + id);
		        var maincontainer = $("#toolbarcontainer" + id);
		        var customcontrol1 = "icon-plus open-sans@" + uiLabelMap.CommonAddNew + "@javascript:void(0)@addNewIITObj.openWindow()";
		        Grid.createCustomControlButton(grid, container, customcontrol1);
		        Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, "",
	                    "", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
		        
			};
			var config = {
					url: '',
					showtoolbar : true,
					rendertoolbar: rendertoolbar,
					width : '100%',
					virtualmode: false,
					editable: false,
					localization: getLocalization(),
					pageable: true,
					source: {
						localdata: [],
					}
			};
			Grid.initGrid(config, datafield, columns, null, grid);
			//parent.attr.string.padSeq('0000', '' + parent.attr.SEQ++)
		},
		initEvent: function(){
			var parent = this;
			$("#newInvoiceItemGrid").on('createcompleted', function(){
				parent.attr.INDEX++;
				parent.attr.SEQ++;
			});
			$("#newInvoiceItemGrid").on('deletecompleted', function(){
				parent.attr.INDEX--;
				parent.attr.SEQ--;
			});
		},
		getInvoiceItemTypeSeq: function(){
			return this.attr.string.padSeq('0000', '' + this.attr.SEQ);
		},
		validate : function(){
			var parent = this;
			var rows = this.attr.grid_item.jqxGrid('getboundrows');
			var rs = true;
			$.each(rows,function(){
				if(parent.attr.invoiceType.val() == 'PAYROL_INVOICE'){
					if(this.invoiceItemTypeId === undefined || this.amount === undefined || this.quantity === undefined) return false;
				}else if(this.invoiceItemTypeId === undefined || this.productId === undefined || this.quantity === undefined || this.amount === undefined) rs = false;
			})
			return rs;
		}
};

var addNewIITObj = (function(){
	var _isInitedStep2 = false;
	var init = function(){
		initSimpleInput();
		initDropDown();
		initDropDownIIT();
		initWindow();
		initEvent();
		initValidator();
		//$("#addIITypeLoader").jqxLoader({ width: 100, height: 60, imagePosition: 'top' });
	};
	var initSimpleInput = function(){
		$("#itemSeqId").jqxInput({width: '96%', height: 20, disabled: true});
		$("#descriptionInvoiceItemType").jqxInput({width: '96%', height: 20});
		$("#quantity").jqxNumberInput({width: '100%', height: '25px', spinButtons: true, decimalDigits: 0});
		$("#unitPrice").jqxNumberInput({width: '84%', height: '25px', spinButtons: true, decimalDigits: 2, max: 999999999999, digits: 12});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#quantityUomList"), globalVar.quantityUomList, 
				{valueMember: 'uomId', displayMember: 'description', width: '95%', height: 25, disabled: true, placeHolder: ""});
		
		var configProduct = {
			useUrl: true,
			root: 'results',
			widthButton: '98%',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			datafields: [{name: 'productId', type: 'string'}, {name: 'productName', type: 'string'},{name: 'productCode', type: 'string'}, {name: 'quantityUomId', type: 'string'}],
			columns: [
				{text: uiLabelMap.BACCProductId, datafield: 'productCode', width: '30%'},
				{text: uiLabelMap.BACCProductName, datafield: 'productName'}
			],
			url: 'JqxGetTaxProducts',
			useUtilFunc: true,
			key: 'productId',
			pagesize: 10,
			description: ['productName'],
		};
		accutils.initDropDownButton($("#productDropDownBtn"), $("#productIdGrid"), null, configProduct, []);
	};
	var initDropDownIIT = function(){
		$("#invoiceItemTypeDropDown").jqxDropDownButton({width: '97%', height: 25}); 
		var grid = $("#invoiceItemTypeGrid");
		var datafield = [{name: 'invoiceItemTypeId', type: 'string'},
		                 {name: 'description', type: 'string'},
		                 {name: 'defaultGlAccountId', type: 'string'}
		                 ];
		var columns = [{text: uiLabelMap.CommonId, datafield: 'invoiceItemTypeId', width: '30%', filterable: true},
		               {text: uiLabelMap.CommonDescription, datafield: 'description', width: '50%', filterable: true},
		               {text: uiLabelMap.BACCGlAccountId, datafield: 'defaultGlAccountId', width: '20%', filterable: true},
		               ];
		var config = {
      		width: 600, 
      		virtualmode: true,
      		showfilterrow: false,
      		showtoolbar: false,
      		selectionmode: 'singlerow',
      		pageable: true,
      		sortable: false,
	        filterable: true,
	        editable: false,
	        url: '',
	        source: {
	        	pagesize: 5
	        }
      	};
      	Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#addNewIITypeWindow"), 480, 350);
	};
	var getData = function(){
		var index = $("#productIdGrid").jqxGrid('getselectedrowindex');
		var product = $("#productIdGrid").jqxGrid('getrowdata', index);
		var data = {};
		data.invoiceItemSeqId = $("#itemSeqId").val();
		data.invoiceItemTypeId = $("#invoiceItemTypeDropDown").attr('data-value');
		data.invoiceItemTypeDesc = $("#invoiceItemTypeDropDown").attr('data-label');
		data.productId = product.productId;
		data.productName = product.productName;
		data.quantity = $("#quantity").val();
		data.amount = $("#unitPrice").val();
		data.currencyUomId = $("#currencyUomId").val();
		data.description = $("#descriptionInvoiceItemType").val();
		return data;
	};
	var initEvent = function(){
		$("#addNewIITypeWindow").on('open', function(event){
			initData();
		});
		$("#addNewIITypeWindow").on('close', function(event){
			resetData();
		});
		$("#alterCancel").click(function(){
			$("#addNewIITypeWindow").jqxWindow('close');
		});
		$("#alterSave").click(function(){
			var valid = $("#addNewIITypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var data = getData();
			$("#newInvoiceItemGrid").jqxGrid('addrow', null, data);
			$("#addNewIITypeWindow").jqxWindow('close');
		});
		$("#saveAndContinue").click(function(){
			var valid = $("#addNewIITypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var data = getData();
			$("#newInvoiceItemGrid").jqxGrid('addrow', null, data);
			resetData();
			initData();
		});
		$("#productIdGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#productIdGrid").jqxGrid('getrowdata', boundIndex);
			$("#quantityUomList").val(rowData.quantityUomId);
		});
		$("#invoiceItemTypeGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#invoiceItemTypeGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.description + '</div>';
			$("#invoiceItemTypeDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#invoiceItemTypeDropDown").attr("data-value", rowData.invoiceItemTypeId);
			$("#invoiceItemTypeDropDown").attr("data-label", rowData.description);
			$("#invoiceItemTypeDropDown").jqxDropDownButton('close');
		});
		$("#invoiceItemTypeGrid").on('bindingcomplete', function(event){
			if(!_isInitedStep2){
				_isInitedStep2 = true;
				var rows = $("#invoiceItemTypeGrid").jqxGrid('getrows');
				var invoiceType = $("#invoiceTypeId").jqxDropDownList('getSelectedItem');
				Loading.hide('loadingMacro');
				if(rows.length == 0){
					var message = uiLabelMap.InvoiceItemTypeOfInvoiceType + " <b>\"" + invoiceType.label + "\"</b> " + uiLabelMap.CommonNotSet;
					bootbox.dialog(message,
							[
							 {
								 "label" : uiLabelMap.CommonClose,
								 "class" : "btn-danger btn-small icon-remove open-sans",
								 "callback": function(){
									 $('#fuelux-wizard').wizard('previous');
									 $("#invoiceTypeId").jqxDropDownList('focus');
								 }
							 }
							 ]		
					);
				}
			}
		});
	};
	var initValidator = function(){
		$("#addNewIITypeWindow").jqxValidator({
			rules: [
				{ input: '#invoiceItemTypeDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				},     
				{ input: '#productDropDownBtn', message: uiLabelMap.FieldRequired, action: 'none', 
					rule: function (input, commit) {
						var index = $("#productIdGrid").jqxGrid('getselectedrowindex');
						if(index < 0){
							return false;
						}
						return true;
					}
				},     
				{ input: '#quantity', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'none', 
					rule: function (input, commit) {
						var val = $(input).val();
						if(val < 0){
							return false;
						}
						return true;
					}
				},     
			]
		});
	};
	
	var resetData = function(){
		Grid.clearForm($("#addNewIITypeWindow"));
		//accutils.updateSourceDropdownlist($("#invoiceItemTypeId"), []);
		$("#addNewIITypeWindow").jqxValidator('hide');
		$("#productIdGrid").jqxGrid('clearselection');
		$("#invoiceItemTypeGrid").jqxGrid('clearselection');
		$("#invoiceItemTypeGrid").jqxGrid('clearfilters');
		$("#invoiceItemTypeGrid").jqxGrid('gotopage', 0);
	};
	var initData = function(){
		$("#itemSeqId").val(OLBNewInvItem.prototype.getInvoiceItemTypeSeq());
	};
	
	var prepareData = function(){
		_isInitedStep2 = false;
		Loading.show('loadingMacro');
		var invoiceType = $("#invoiceTypeId").jqxDropDownList('getSelectedItem');
		var source = $("#invoiceItemTypeGrid").jqxGrid('source');
		source._source.url = 'jqxGeneralServicer?sname=JQGetInvoiceItemTypeList&invoiceTypeId=' + invoiceType.value;
		$("#invoiceItemTypeGrid").jqxGrid('source', source);
		
		var currencyUomId = $("#currencyUomId").val();
		var	decimalseparator = ",";
		var thousandsseparator = ".";
		var currencysymbol = "đ";
		if(currencyUomId == "USD"){
	        currencysymbol = "$";
	        decimalseparator = ".";
	        thousandsseparator = ",";
	    }else if(currencyUomId == "EUR"){
	        currencysymbol = "€";
	        decimalseparator = ".";
	        thousandsseparator = ",";
	    }
		$("#unitPrice").jqxNumberInput({symbolPosition: 'right', symbol: ' ' + currencysymbol, groupSeparator: thousandsseparator, 
			decimalSeparator: decimalseparator});
	};
	var openWindow = function(){
		var invoiceTypeId = $("#invoiceTypeId").val();
		if(!invoiceTypeId){
			bootbox.dialog(uiLabelMap.HaveChooseInvoiceTypeBeforeAddItem,
					[{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
			return;
		}
		accutils.openJqxWindow($("#addNewIITypeWindow"));
	};
	var getProductPrice = function(){
		var productId = $("#productDropDownBtn").attr('data-value');
		if(productId != "undefined" && productId.length > 0){
			Loading.show('loadingMacro');
			var data = {productId: productId, businessType: globalVar.businessType, currencyUomId: $("#currencyUomId").val()};
			if(globalVar.businessType == "AP"){
				data.partyId = $("#organizationId").attr('data-value');
			}
			$.ajax({
				url: 'getProductPriceAcc',
				data: data,
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "error"){
						bootbox.dialog(response.errorMessage,
								[{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);
						return;
					}

					$("#unitPrice").val(formatnumber(response.productPrice));
				},
				complete:  function(jqXHR, textStatus){
					Loading.hide('loadingMacro');
				}
			});
		}
	};
	return{
		init: init,
		openWindow: openWindow,
		prepareData: prepareData,
		getProductPrice: getProductPrice
	}
}());

$(document).ready(function(){
	addNewIITObj.init();
});