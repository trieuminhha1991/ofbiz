var invoiceListObj = (function(){
	var _data = {};
	var init = function(){
		initContextMenu();
		initGrid();
		initWindow();
		initEvent();
		initColorbox();
	};
	var initContextMenu = function(){
		accutils.createJqxMenu("contextMenu", 30, 250);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#jqxgridInvoice").jqxGrid('getselectedrowindex');
			var data = $("#jqxgridInvoice").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("action");
			if(action == "viewVoucher"){
				_data = data;
				accutils.openJqxWindow($("#voucherListWindow"));
			}
		});
	};
	var initGrid = function(){
		if(globalVar.businessType == 'AP'){
			var voucherCreatedDateAPTitle = uiLabelMap.ReceivingVoucherDate;
		}else{
			var voucherCreatedDateAPTitle = uiLabelMap.PublicationVoucherDate;
		}
		var grid = $("#voucherListGrid");
		var datafield = [{name: 'voucherId', type: 'string'},
						  {name: 'invoiceId', type: 'string'},	
						  {name: 'voucherForm', type: 'string'},	
						  {name: 'voucherSerial', type: 'string'},
						  {name: 'voucherNumber', type: 'string'},
						  {name: 'issuedDate', type: 'date'},
						  {name: 'voucherCreatedDate', type: 'date'},
						  {name: 'taxProductCategoryId', type: 'string'},							  
						  {name: 'dataResourceId', type: 'string'},
						  {name: 'dataResourceName', type: 'string'},
						  {name: 'objectInfo', type: 'string'},
						  {name: 'amount', type: 'number'},
						  {name: 'taxAmount', type: 'number'},	
						  {name: 'totalAmount', type: 'number'},	
						  {name: 'currencyUomId', type: 'string'},	
		                 ];
		var columns = [{text: uiLabelMap.VoucherForm, datafield: 'voucherForm', width: '12%', editable: false},
		               {text: uiLabelMap.VoucherSerial, datafield: 'voucherSerial', width: '11%', editable: false},
					   {text: uiLabelMap.VoucherNumber, datafield: 'voucherNumber', width: '11%', editable: false},
					   {text: uiLabelMap.BACCIssueDate, datafield: 'issuedDate', width: '13%', cellsformat: 'dd/MM/yyyy', 
					   		columntype: 'datetimeinput', filtertype: 'range', editable: false},
					   {text: voucherCreatedDateAPTitle, datafield: 'voucherCreatedDate', width: '13%', cellsformat: 'dd/MM/yyyy', 
					   		columntype: 'datetimeinput', filtertype: 'range', editable: false},
					   {text: uiLabelMap.BACCInvoiceTypeId, datafield: 'taxProductCategoryId', width: '11%', editable: false},					   		
					   {text: uiLabelMap.HRCommonAttactFile, datafield: 'dataResourceName', width: '18%', editable: false,
					   		cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
						   		return '<span><a href="javascript:void(0)" onclick="javascript: invoiceListObj.changeLinkImg(' + row + ')">' + value + '</a></span>';
					   		}
					   },
					   {text: uiLabelMap.AmountNotIncludeTax, datafield: 'amount', columntype: 'numberinput',
						   filtertype: 'number', width: '14%',
						  	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(typeof(value) == 'number'){
									var data = grid.jqxGrid('getrowdata', row);
									return '<span style=\"text-align: right\">' + formatcurrency(value, data.currencyUomId) + '</span>';
								}
							},
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								var	decimalseparator = ',';
								var thousandsseparator = '.';
								if(globalVar.currencyUomId == 'USD'){
							        decimalseparator = '.';
							        thousandsseparator = ',';
							    }else if(globalVar.currencyUomId == 'EUR'){
							        decimalseparator = '.';
							        thousandsseparator = ',';
							    }
								editor.jqxNumberInput({width: cellwidth, height: cellheight, spinButtons: true, decimalDigits: 2, 
									groupSeparator: thousandsseparator, decimalSeparator: decimalseparator, max: 999999999999, digits: 12, inputMode: 'advanced'});
							},
							cellclassname: function (row, columnfield, value) {
								return 'background-prepare';
							}
					   },
					   {text: uiLabelMap.CommonTax, datafield: 'taxAmount', columntype: 'numberinput',
						   filtertype: 'number', width: '13%',
						  	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(typeof(value) == 'number'){
									var data = grid.jqxGrid('getrowdata', row);
									return '<span style=\"text-align: right\">' + formatcurrency(value, data.currencyUomId) + '</span>';
								}
							},
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								var	decimalseparator = ',';
								var thousandsseparator = '.';
								if(globalVar.currencyUomId == 'USD'){
							        decimalseparator = '.';
							        thousandsseparator = ',';
							    }else if(globalVar.currencyUomId == 'EUR'){
							        decimalseparator = '.';
							        thousandsseparator = ',';
							    }
								editor.jqxNumberInput({width: cellwidth, height: cellheight, spinButtons: true, decimalDigits: 2, 
									groupSeparator: thousandsseparator, decimalSeparator: decimalseparator, max: 999999999999, digits: 12, inputMode: 'advanced'});
							},  
							cellclassname: function (row, columnfield, value) {
								return 'background-prepare';
							} 
					   },
					   {text: uiLabelMap.CommonTotal, datafield: 'totalAmount', columntype: 'numberinput',
						  filtertype: 'number', width: '14%', editable: false,
						  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(typeof(value) == 'number'){
									var data = grid.jqxGrid('getrowdata', row);
									return '<span style=\"text-align: right\">' + formatcurrency(value, data.currencyUomId) + '</span>';
								}
							}   
					   },
		               ];
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "voucherListGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5 id='listInvoiceTitle'>" + uiLabelMap.ListVoucherOfInvoice + ' ' + _data.invoiceId + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
		};
		var config = {
				url: '',
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '100%',
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				sortable: true,
				source: {
					pagesize: 10,
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
		
	};
	var initEvent = function(){
		$("#closeViewVocherList").click(function(){
			$("#voucherListWindow").jqxWindow('close');
		});
		$("#voucherListWindow").on('close', function(event){
			var source = $("#voucherListGrid").jqxGrid('source');
			source._source.url = '';
			$("#voucherListGrid").jqxGrid('source', source);
			$("#listInvoiceTitle").html('');
			_data = {};
		});
		$("#voucherListWindow").on('open', function(event){
			setupVoucherData(_data);
		});
	};
	var setupVoucherData = function(data){
		$("#listInvoiceTitle").html(uiLabelMap.ListVoucherOfInvoice + ' ' + data.invoiceId);
		var source = $("#voucherListGrid").jqxGrid('source');
		source._source.url = 'jqxGeneralServicer?sname=JQGetListVoucherInvoice&invoiceId=' + data.invoiceId;
		$("#voucherListGrid").jqxGrid('source', source);
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#voucherListWindow"), 920, 500);
	};
	var initColorbox = function(){
		var colorbox_params = {
				reposition:true,
				scalePhotos:true,
				scrolling:false,
				previous:'<i class="icon-arrow-left"></i>',
				next:'<i class="icon-arrow-right"></i>',
				close:'&times;',
				current:'{current} of {total}',
				maxWidth:'100%',
				maxHeight:'86%',
				className: 'zIndex999999',
				onOpen:function(){
					document.body.style.overflow = 'hidden';
				},
				onClosed:function(){
					document.body.style.overflow = 'auto';
				},
				onComplete:function(){
					$.colorbox.resize();
				}
			};

			$('[data-rel="colorbox"]').colorbox(colorbox_params);
			$("#cboxLoadingGraphic").append("<i class='icon-spinner orange'></i>");//let's add a custom loading icon
	};
	var changeLinkImg = function(row){
		var data = $('#voucherListGrid').jqxGrid('getrowdata', row);
		$("#viewImgVoucher").prop("href", data.objectInfo);
		$("#viewImgVoucher").trigger('click');
	};
	return{
		init: init,
		changeLinkImg: changeLinkImg
	}
}());

$(document).on('ready', function(){
	invoiceListObj.init();
});