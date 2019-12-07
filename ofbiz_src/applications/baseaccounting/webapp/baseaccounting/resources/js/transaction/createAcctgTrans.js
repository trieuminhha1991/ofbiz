var createAcctgTransObj = (function(){
	var init = function(){
		initInput();
		initDropDown();
		initDropDownGrid();
		initWindow();
		initValidator();
		initEvent();
	};
	var initInput = function(){
		$("#addAcctgTransId").jqxInput({width: '100%', height: 20, disabled: true});
		$("#addPostedDate").jqxDateTimeInput({width: '95%', height: 25, formatString: 'dd/MM/yyyy HH:mm:ss'});
		$("#transPosted").jqxRadioButton({width: '100%', height: 25, groupName: 'acctgTrans'});
		$("#transNotPosted").jqxRadioButton({width: '100%', height: 25, groupName: 'acctgTrans'});
		$("#isAutoIncrement").jqxCheckBox({width: '100%', height: 25, checked: true});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#addAcctgTransType"), acctgTransTypesData, 
				{width: '95%', height: 25, valueMember: 'acctgTransTypeId', displayMember: 'description', filterable: true});
		accutils.createJqxDropDownList($("#addGlJournalId"), glJournalArr, 
				{width: '95%', height: 25, valueMember: 'glJournalId', displayMember: 'glJournalName'});
		accutils.createJqxDropDownList($("#enumPartyTypeId"), globalVar.enumPartyTypeArr, 
				{valueMember: 'enumId', displayMember: 'description', width: '95%', height: 25});
	};
	var initDropDownGrid = function(){
		initDropDownInvoice();
		initDropDownPayment();
		initDropDownShipment();
		initDropDownParty();
	};
	var initDropDownInvoice = function(){
		$("#invoiceDropDownBtn").jqxDropDownButton({width: '95%', height: 25, dropDownHorizontalAlignment: 'right'});
		var datafield = [{ name: 'invoiceId', type: 'string' },
						 { name: 'invoiceTypeId', type: 'string'},
						 { name: 'invoiceDate', type: 'date', other:'Timestamp'},
						 { name: 'statusId', type: 'string'},
						 { name: 'description', type: 'string'},
						 { name: 'partyIdFrom', type: 'string'},
						 { name: 'partyId', type: 'string'},
						 { name: 'fullNameTo', type: 'string'},
						 { name: 'fullNameFrom', type: 'string'}];
		
		var columns = [{ text: uiLabelMap.BACCInvoiceId, dataField: 'invoiceId', width: '15%', pinned: true},
		               {text: uiLabelMap.BACCInvoiceTypeId, datafield: 'invoiceTypeId', width: '20%', columntype: 'dropdownlist', filtertype: 'checkedlist',
							cellsrenderer: function(row, column, value){
								for(var i = 0; i < globalVar.invoiceTypeArr.length; i++){
									if(value == globalVar.invoiceTypeArr[i].invoiceTypeId){
										return '<span>' + globalVar.invoiceTypeArr[i].description + '</span>';
									}
								}
							},
							createfilterwidget: function (column, columnElement, widget) {
								accutils.createJqxDropDownList(widget, globalVar.invoiceTypeArr, {valueMember: 'invoiceTypeId', displayMember: 'description'});
							}
		               },
		               {text: uiLabelMap.BACCInvoiceFromParty, dataField: 'fullNameFrom', width: '23%',},
		               { text: uiLabelMap.BACCInvoiceToParty, width: '23%', dataField:'fullNameTo'},
		               { text: uiLabelMap.BACCInvoiceDate, dataField: 'invoiceDate', width: '22%', cellsformat: 'dd/MM/yyyy HH:mm:ss', filtertype: 'range'},
		               { text: uiLabelMap.CommonStatus, dataField: 'statusId', width: '16%', filtertype: 'checkedlist', columntype: 'dropdownlist',
		                	  cellsrenderer: function(row, column, value){
		                		  for(var i = 0; i < globalVar.invoiceStatusTypeArr.length; i++){
										if(value == globalVar.invoiceStatusTypeArr[i].statusId){
											return '<span title=' + value + '>' + globalVar.invoiceStatusTypeArr[i].description + '</span>';
										}
									}
									return '<span>' + value + '</span>';
		                	  },
		                	  createfilterwidget: function (column, columnElement, widget) {
					   			accutils.createJqxDropDownList(widget, globalVar.invoiceStatusTypeArr, {valueMember: 'statusId', displayMember: 'description'});
	   						},
		                  },
		               ];
		var config = {
				url: 'JQGetAllListInvoice',
				showtoolbar : false,
				width : 700,
				filterable: true,
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				source:{
					pagesize: 10
				}
		};
		Grid.initGrid(config, datafield, columns, null, $("#addInvoiceGrid"));
		initDropDownEvent($("#invoiceDropDownBtn"), $("#addInvoiceGrid"), "invoiceId");
	};
	
	var initDropDownPayment = function(){
		$("#paymentDropDownBtn").jqxDropDownButton({width: '95%', height: 25, dropDownHorizontalAlignment: 'right'});
		var datafield = [{ name: 'paymentId', type: 'string' },
						 { name: 'paymentTypeId', type: 'string'},
						 { name: 'statusId', type: 'string'},
						 { name: 'partyIdFrom', type: 'string'},
						 { name: 'partyIdTo', type: 'string'},
						 { name: 'effectiveDate', type: 'date', other:'Timestamp'},
						 { name: 'fullNameTo', type: 'string'},
						 { name: 'fullNameFrom', type: 'string'},	
						];
		var columns = [{text: uiLabelMap.BACCPaymentId, dataField: 'paymentId', width: '15%', pinned: true,},
		               {text: uiLabelMap.BACCPaymentFromParty, dataField: 'fullNameFrom', width: '23%',},
				  	   {text: uiLabelMap.BACCPaymentToParty, width: '23%', dataField: 'fullNameTo'},
					   { text: uiLabelMap.BACCPaymentTypeId, dataField: 'paymentTypeId', width: '18%', filtertype: 'checkedlist', columntype: 'dropdownlist',
				  		   	cellsrenderer: function(row, column, value){
								for(var i = 0; i < globalVar.paymentTypeArr.length; i++){
									if(value == globalVar.paymentTypeArr[i].paymentTypeId){
										return '<span title=' + value + '>' + globalVar.paymentTypeArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function (column, columnElement, widget) {
					   			accutils.createJqxDropDownList(widget, globalVar.paymentTypeArr, 
					   					{valueMember: 'paymentTypeId', displayMember: 'description'});
			   			  	},
	      		 	   },
      		 		   {text: uiLabelMap.BACCStatusId, width: '18%', dataField:'statusId', filtertype: 'checkedlist', columntype: 'dropdownlist',
	      		 		   cellsrenderer: function(row, column, value){
	      		 			   for(var i = 0; i < globalVar.paymentStatusTypeArr.length; i++){
	      		 				   if(value == globalVar.paymentStatusTypeArr[i].statusId){
	      		 					   return '<span title=' + value + '>' + globalVar.paymentStatusTypeArr[i].description + '</span>';
	      		 				   }
	      		 			   }
	      		 			   return '<span>' + value + '</span>';
	      		 		   },
	      		 		   createfilterwidget: function (column, columnElement, widget) {
	      		 			   accutils.createJqxDropDownList(widget, globalVar.paymentStatusTypeArr, {valueMember: 'statusId', displayMember: 'description'});			   				
	      		 		   },
      		 		  },
      		 		  { text: uiLabelMap.BACCEffectiveDate, dataField: 'effectiveDate', width: '20%', cellsformat:'dd/MM/yyyy', filtertype: 'range'},
		              ];
		
		var config = {
				url: 'JQGetAllListPayment',
				showtoolbar : false,
				width : 700,
				virtualmode: true,
				editable: false,
				filterable: true,
				localization: getLocalization(),
				pageable: true,
				source:{
					pagesize: 10
				}
		};
		Grid.initGrid(config, datafield, columns, null, $("#addPaymentGrid"));
		initDropDownEvent($("#paymentDropDownBtn"), $("#addPaymentGrid"), "paymentId");
	};
	
	var initDropDownShipment = function(){
		$("#shipmentDropDownBtn").jqxDropDownButton({width: '95%', height: 25, dropDownHorizontalAlignment: 'right'});
		var datafield = [{name: 'shipmentId', type: 'string'},
		                 {name: 'shipmentTypeId', type: 'string'},
		                 {name: 'statusId', type: 'string'},
		                 {name: 'primaryOrderId', type: 'string'},
		                 {name: 'primaryReturnId', type: 'string'},
		                 {name: 'estimatedShipDate', type: 'date'},
		                 {name: 'estimatedArrivalDate', type: 'date'}
		                 ];
		var columns = [{text: uiLabelMap.ShipmentId, datafield: 'shipmentId', width: '17%'},
		               {text: uiLabelMap.ShipmentType, datafield: 'shipmentTypeId', width: '22%', filtertype: 'checkedlist', columntype: 'dropdownlist',
							 cellsrenderer: function(row, column, value){
					 			   for(var i = 0; i < globalVar.shipmentTypeArr.length; i++){
					 				   if(value == globalVar.shipmentTypeArr[i].shipmentTypeId){
					 					   return '<span title=' + value + '>' + globalVar.shipmentTypeArr[i].description + '</span>';
					 				   }
					 			   }
					 			   return '<span>' + value + '</span>';
					 		   },
					 		   createfilterwidget: function (column, columnElement, widget) {
					 			   accutils.createJqxDropDownList(widget, globalVar.shipmentTypeArr, {valueMember: 'shipmentTypeId', displayMember: 'description'});			   				
					 		   },
		               },
		               {text: uiLabelMap.OrderOrderId, datafield: 'primaryOrderId', width: '16%'},
		               {text: uiLabelMap.BSReturnOrder, datafield: 'primaryReturnId', width: '16%'},
		               {text: uiLabelMap.EstimatedShipDate, dataField: 'estimatedShipDate', width: '22%', cellsformat: 'dd/MM/yyyy HH:mm:ss', filtertype: 'range'},
		               {text: uiLabelMap.EstimatedArrivalDate, dataField: 'estimatedArrivalDate', width: '22%', cellsformat: 'dd/MM/yyyy HH:mm:ss', filtertype: 'range'},
		               ];
		
		var config = {
				url: 'JQGetListShipmentACC',
				showtoolbar : false,
				width : 700,
				virtualmode: true,
				editable: false,
				filterable: true,
				localization: getLocalization(),
				pageable: true,
				source:{
					pagesize: 10
				}
		};
		Grid.initGrid(config, datafield, columns, null, $("#addShipmentGrid"));
		initDropDownEvent($("#shipmentDropDownBtn"), $("#addShipmentGrid"), "shipmentId");
	};
	
	var initDropDownParty = function(){
		$("#partyDropDownBtn").jqxDropDownButton({width: '95%', height: 25, dropDownHorizontalAlignment: 'right'});
		var datafield = [
		                 {name: 'partyId', type: 'string'}, 
		                 {name: 'partyCode', type: 'string'}, 
		                 {name: 'fullName', type: 'string'}
		                 ];
		var columns = [
						{text: uiLabelMap.BACCOrganizationId, datafield: 'partyCode', width: '30%'},
						{text: uiLabelMap.BACCFullName, datafield: 'fullName'}
					];
		
		var config = {
		   		width: 500, 
		   		virtualmode: true,
		   		showfilterrow: true,
		   		pageable: true,
		   		sortable: true,
		        filterable: true,
		        editable: false,
		        url: '', 
		        showtoolbar: false,
	        	source: {
	        		pagesize: 10,
	        	}
	   	};
	   	Grid.initGrid(config, datafield, columns, null, $("#addPartyGrid"));
	   	initDropDownEvent($("#partyDropDownBtn"), $("#addPartyGrid"), "fullName", "partyCode");
	};
	
	var initDropDownEvent = function(dropDownEle, gridEle, displayField, displayAddField){
		gridEle.on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var data = gridEle.jqxGrid('getrowdata', boundIndex);
			var desc = data[displayField];
			if(typeof(displayAddField) != "undefined"){
				desc += " [" + data[displayAddField] + "]";
			}
			var dropDownContent = '<div class="innerDropdownContent">' + desc + '</div>';
			dropDownEle.jqxDropDownButton('setContent', dropDownContent);
			dropDownEle.jqxDropDownButton('close');
		});
	};
	
	var initWindow = function(){
		accutils.createJqxWindow($("#CreateAcctgTransWindow"), 850, 580);
	};
	
	var initEvent = function(){
		$("#isAutoIncrement").on('change', function (event) {
			var checked = event.args.checked;
			$("#addAcctgTransId").jqxInput({disabled: checked});
		});
		$("#enumPartyTypeId").on('select', function(event){
			var args = event.args;
		    if (args) {
		    	var item = args.item;
		    	var value = item.value;
		    	var grid = $("#addPartyGrid");
		    	var source = grid.jqxGrid('source');
		    	source._source.url = 'jqxGeneralServicer?sname=JqxGetParties&enumId=' + value;
		    	grid.jqxGrid('source', source);
		    }
		});
		$("#CreateAcctgTransWindow").on('open', function(event){
			initOpen();
		});
		$("#CreateAcctgTransWindow").on('close', function(event){
			resetData();
		});
		$("#cancelAddTransaction").click(function(){
			$("#CreateAcctgTransWindow").jqxWindow('close');
		});
		$("#saveAndContinueAddTransaction").click(function(){
			var valid = $("#CreateAcctgTransWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var result = acctgTranEntryObj.validate();
			if(!result.isValid){
				bootbox.dialog(result.message,
						[
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);	
				return;
			}
			bootbox.dialog(uiLabelMap.CreateAcctgTransConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createAcctgTrans(false);
						}
					},
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		});
		$("#saveAddTransaction").click(function(){
			var valid = $("#CreateAcctgTransWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var result = acctgTranEntryObj.validate();
			if(!result.isValid){
				bootbox.dialog(result.message,
						[
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);	
				return;
			}
			bootbox.dialog(uiLabelMap.CreateAcctgTransConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createAcctgTrans(true);
						}
					},
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		});
	};
	
	var createAcctgTrans = function(isCloseWindow){
		Loading.show('loadingMacro');
		var data = getData();
		$.ajax({
			url: "createAcctgTransAndEntriesOlbius",
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgridTrans', response.successMessage, {template : 'success', appendContainer : '#containerjqxgridTrans'});
					if(isCloseWindow){
						$("#CreateAcctgTransWindow").jqxWindow('close');
					}else{
						resetData();
						initOpen();
					}
					$("#jqxgridTrans").jqxGrid('updatebounddata');
				}else{
					bootbox.dialog(response.errorMessage,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);			
				}
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	
	var getData = function(){
		var data = {};
		var autoIncrement = $("#isAutoIncrement").jqxCheckBox('checked');
		if(!autoIncrement){
			data.acctgTransId = $("#addAcctgTransId").val();
		}
		data.description = $("#description").val();
		data.acctgTransTypeId = $("#addAcctgTransType").val();
		data.transactionDate = $("#addPostedDate").jqxDateTimeInput('val', 'date').getTime();
		if($("#addGlJournalId").val().length > 0){
			data.glJournalId = $("#addGlJournalId").val();
		}
		var transPosted = $("#transPosted").jqxRadioButton('checked');
		var transNotPosted = $("#transNotPosted").jqxRadioButton('checked');
		if(transPosted){
			data.isPosted = "Y";
		}else if(transNotPosted){
			data.isPosted = "N";
		}
		var partyIndex = $("#addPartyGrid").jqxGrid('getselectedrowindex');
		if(partyIndex > -1){
			var party = $("#addPartyGrid").jqxGrid('getrowdata', partyIndex);
			data.partyId = party.partyId;
		}
		var invoiceIndex = $("#addInvoiceGrid").jqxGrid('getselectedrowindex');
		if(invoiceIndex > -1){
			var invoice = $("#addInvoiceGrid").jqxGrid('getrowdata', invoiceIndex);
			data.invoiceId = invoice.invoiceId;
		}
		var paymentIndex = $("#addPaymentGrid").jqxGrid('getselectedrowindex');
		if(paymentIndex > -1){
			var payment = $("#addPaymentGrid").jqxGrid('getrowdata', paymentIndex);
			data.paymentId = payment.paymentId;
		}
		var shipmentIndex = $("#addShipmentGrid").jqxGrid('getselectedrowindex');
		if(shipmentIndex > -1){
			var shipment = $("#addShipmentGrid").jqxGrid('getrowdata', shipmentIndex);
			data.shipmentId = shipment.shipmentId;
		}
		var acctgTransEntryItem = $("#acctgTransEntryGrid").jqxGrid('getrows');
		if(acctgTransEntryItem.length > 0){
			data.acctgTransEntry = JSON.stringify(acctgTransEntryItem); 
		}
		return data;
	};
	
	var initValidator = function(){
		$("#CreateAcctgTransWindow").jqxValidator({
			rules: [
				{ input: '#addAcctgTransId', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						var isAutoIncrement = $("#isAutoIncrement").jqxCheckBox('checked'); 
						if(!isAutoIncrement){
							if(!$(input).val()){
								return false;
							}
						}
						return true;
					}
				},
				{ input: '#addAcctgTransType', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				},
				{ input: '#addPostedDate', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				}
			]
		});
	};
	
	var initOpen = function(){
		$("#isAutoIncrement").jqxCheckBox({checked: true});
		$("#transPosted").jqxRadioButton({checked: true});
		var date = new Date();
		$("#addPostedDate").val(date);
		$('#description').val("");
	};
	var resetData = function(){
		Grid.clearForm($(".form-legend"));
		$("#addAcctgTransType").jqxDropDownList('clearSelection');
		resetGrid($("#addPartyGrid"));
		resetGrid($("#addInvoiceGrid"));
		resetGrid($("#addShipmentGrid"));
		$("#CreateAcctgTransWindow").jqxValidator('hide');
		var source = $("#acctgTransEntryGrid").jqxGrid('source');
		source._source.localdata = []; 
		$("#acctgTransEntryGrid").jqxGrid('source', source);
	};
	
	var resetGrid = function(gridEle){
		gridEle.jqxGrid('clearselection');
		gridEle.jqxGrid('gotopage', 0);
	};
	return{
		init: init
	}
}());

var acctgTranEntryObj = (function(){
	var init = function(){
		initGrid();
	};
	var grid = $("#acctgTransEntryGrid");
	var initGrid = function(){
		var datafield = [{name: 'description', type: 'string'},
		                 {name: 'glAccountId', type: 'string'},
		                 {name: 'glAccountCodeDebit', type: 'string'},
						 {name: 'glAccountCodeCredit', type: 'string'},
						 {name: 'debitCreditFlag', type: 'string'},
						 {name: 'amount', type: 'number'},
						 {name: 'currencyUomId', type: 'string'},
						 {name: 'partyId', type: 'string'},
						 {name: 'fullName', type: 'string'},
						 {name: 'productId', type: 'string'},
						 {name: 'productName', type: 'string'},
						 {name: 'reciprocalSeqId', type: 'string'}
		                 ];
		
		var columns = [
		               {text: uiLabelMap.BACCDebitAccount, datafield: 'glAccountCodeDebit', width: '12%', editable: false},
		               {text: uiLabelMap.BACCCreditAccount, datafield: 'glAccountCodeCredit', width: '12%', editable: false},
		               {text: uiLabelMap.BACCAmount, datafield: 'amount', columntype: 'numberinput', width: '15%', editable: false,
		            	   cellsrenderer: function(row, column, value){
		            		   if(typeof(value) == 'number'){
		            			   return "<span style='text-align: right'>" + formatcurrency(value) + "</span>";
		            		   }
		            	   }
		               },
		               {text: uiLabelMap.DAParty, datafield: 'fullName', width: '18%', editable: false},
		               {text: uiLabelMap.BACCProduct, datafield: 'productName', width: '22%', editable: false},
		               {text: uiLabelMap.AccountingComments, datafield: 'description', width: '22%', editable: false}
		               ];
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "acctgTransEntryGrid";
			var me = this;
	        var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.BACCTransactionDetail + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
	     	toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        var customcontrol1 = "icon-trash open-sans@" + uiLabelMap.wgdelete + "@javascript:void(0)@acctgTranEntryObj.deleteRow()";
	        Grid.createAddRowButton(grid, container, uiLabelMap.accAddNewRow, {type: "popup", container: $("#addNewAcctgTranEntryWindow")});
	        Grid.createCustomControlButton(grid, container, customcontrol1);
    		/*Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, "",
                    "", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);*/
    	};
		var config = {
		   		width: '100%', 
		   		virtualmode: false,
		   		showfilterrow: true,
		   		pageable: true,
		   		sortable: true,
		        filterable: false,
		        editable: true,
		        url: '', 
		        rendertoolbar: rendertoolbar,
		        showtoolbar: true,
	        	source: {
	        		pagesize: 5,
	        	}
	   	};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var validate = function(){
		var acctgTransEntryItems = $("#acctgTransEntryGrid").jqxGrid('getrows');
		var result = {isValid: true};
		if(acctgTransEntryItems.length == 0){
			result.isValid = false;
			result.message = uiLabelMap.AcctgTransEntriesIsEmpty;
			return result;
		}
		var debitAmount = 0;
		var creditAmount = 0;
		var reciprocalSeqObj = {};
		for(var i = 0; i < acctgTransEntryItems.length; i++){
			var acctgTransEntry = acctgTransEntryItems[i];
			var reciprocalSeqId = acctgTransEntry.reciprocalSeqId;
			var debitCreditFlag = acctgTransEntry.debitCreditFlag;
			var amount = acctgTransEntry.amount;
			if(!reciprocalSeqObj.hasOwnProperty(reciprocalSeqId)){
				reciprocalSeqObj[reciprocalSeqId] = {};
				reciprocalSeqObj[reciprocalSeqId]["creditAmount"] = 0;
				reciprocalSeqObj[reciprocalSeqId]["debitAmount"] = 0;
			}
			if("C" == debitCreditFlag){
				creditAmount += amount;
				reciprocalSeqObj[reciprocalSeqId]["creditAmount"] += amount;
			}else if("D" == debitCreditFlag){
				debitAmount += amount;
				reciprocalSeqObj[reciprocalSeqId]["debitAmount"] += amount;
			}
		}
		if(debitAmount != creditAmount){
			result.isValid = false;
			result.message = uiLabelMap.CreditAndDebitIsNotEquals;
			return result;
		}
		var keys = Object.keys(reciprocalSeqObj);
		for(var i = 0; i < keys.length; i++){
			var key = keys[i];
			var reciprocalSeq = reciprocalSeqObj[key];
			if(reciprocalSeq.debitAmount != reciprocalSeq.creditAmount){
				result.isValid = false;
				result.message = uiLabelMap.GlReconciliationId + ' "' + key + '" ' + uiLabelMap.IsNotValid;
				return result;
			}
		}
		return result;
	};
	
	var deleteRow = function(){
		var selectedRowIndex = $("#acctgTransEntryGrid").jqxGrid('getselectedrowindex');
		if(selectedRowIndex > -1){
			var rowData = $("#acctgTransEntryGrid").jqxGrid('getrowdata', selectedRowIndex);
			var reciprocalSeqId = rowData.reciprocalSeqId;
			var rows = $("#acctgTransEntryGrid").jqxGrid('getrows');
			var secondDeleteRowId = -1;
			for(var i = 0; i < rows.length; i++){
				if(i != selectedRowIndex){
					var temRow = $("#acctgTransEntryGrid").jqxGrid('getrowdata', i);
					var tempReciprocalSeqId = temRow.reciprocalSeqId;
					if(tempReciprocalSeqId == reciprocalSeqId){
						secondDeleteRowId = $("#acctgTransEntryGrid").jqxGrid('getrowid', i);
						break;
					}
				}
			}
			$('#acctgTransEntryGrid').jqxGrid('deleterow', rowData.uid);
			$('#acctgTransEntryGrid').jqxGrid('deleterow', secondDeleteRowId);
		}
	};
	
	return{
		init: init,
		validate: validate,
		deleteRow: deleteRow
	}
}());

var createAcctgTransEntryObj = (function(){
	var _reciprocalSeqId = 0;
	var _totalNumberReciprocalSeqId = 5;
	var init = function(){
		initInput();
		initDropDown();
		initDropDownGrid();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#acctgTransEntryDebitDesc").jqxInput({width: '95%', height: 20});
		$("#acctgTransEntryCreditDesc").jqxInput({width: '95%', height: 20});
		$("#transEntryAmount").jqxNumberInput({width: '97%', height: 25, max: 999999999999999, min: -999999999999999, digits: 15,
			symbolPosition: 'right', symbol: ' đ', spinButtons: true, decimalDigits: 2});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#transEntryEnumPartyTypeId"), globalVar.enumPartyTypeArr, 
				{valueMember: 'enumId', displayMember: 'description', width: '97%', height: 25});
	};
	var initDropDownGrid = function(){
		initDropDownProduct();
		initDropDownParty();
		initDropDownGlAccount();
	};
	var initDropDownProduct = function(){
		$("#prodTransEntryDropDownBtn").jqxDropDownButton({width: '97%', height: 25});
		var datafield = [{name: 'productId', type: 'string'}, 
		                 {name: 'productName', type: 'string'},
		                 {name: 'productCode', type: 'string'}, 
		                 {name: 'quantityUomId', type: 'string'}];
		
		var columns = [{text: uiLabelMap.BACCProductId, datafield: 'productCode', width: '30%'},
						{text: uiLabelMap.BACCProductName, datafield: 'productName'}
					   ];
		
		var config = {
		   		width: 500, 
		   		virtualmode: true,
		   		showfilterrow: true,
		   		pageable: true,
		   		sortable: true,
		        filterable: true,
		        editable: false,
		        url: 'JqxGetProducts', 
		        showtoolbar: false,
	        	source: {
	        		pagesize: 10,
	        	}
	   	};
	   	Grid.initGrid(config, datafield, columns, null, $("#addProdTransEntryGrid"));
	   	initDropDownEvent($("#prodTransEntryDropDownBtn"), $("#addProdTransEntryGrid"), "productName", "productCode");
	};
	var initDropDownParty = function(){
		$("#partyTransEntryDropDownBtn").jqxDropDownButton({width: '97%', height: 25});
		var datafield = [
		                 {name: 'partyId', type: 'string'}, 
		                 {name: 'partyCode', type: 'string'}, 
		                 {name: 'fullName', type: 'string'}
		                 ];
		var columns = [
						{text: uiLabelMap.BACCOrganizationId, datafield: 'partyCode', width: '30%'},
						{text: uiLabelMap.BACCFullName, datafield: 'fullName'}
					];
		
		var config = {
		   		width: 500, 
		   		virtualmode: true,
		   		showfilterrow: true,
		   		pageable: true,
		   		sortable: true,
		        filterable: true,
		        editable: false,
		        url: '', 
		        showtoolbar: false,
	        	source: {
	        		pagesize: 10,
	        	}
	   	};
	   	Grid.initGrid(config, datafield, columns, null, $("#addPartyTransEntryGrid"));
	   	initDropDownEvent($("#partyTransEntryDropDownBtn"), $("#addPartyTransEntryGrid"), "fullName", "partyCode");
	};
	var initDropDownGlAccount = function(){
		$("#transEntryDebitGlAccDropDown").jqxDropDownButton({width: '97%', height: 25});
		$("#transEntryCreditGlAccDropDown").jqxDropDownButton({width: '97%', height: 25});
		var datafield = [
		                 {name: 'glAccountId', type: 'string'}, 
		                 {name: 'glAccountTypeId', type: 'string'}, 
		                 {name: 'accountCode', type: 'string'},
		                 {name: 'accountName', type: 'string'}
		                 ];
		var columns = [
						{text: uiLabelMap.BACCAccountCode, datafield: 'accountCode', width: '20%'},
						{text: uiLabelMap.BACCAccountName, datafield: 'accountName', width: '40%'},
						{text: uiLabelMap.BACCGlAccountTypeId, datafield: 'glAccountTypeId', width: '40%',
							filtertype: 'checkedlist', columntype: 'dropdownlist',
							 cellsrenderer: function(row, column, value){
					 			   for(var i = 0; i < globalVar.glAccountTypeArr.length; i++){
					 				   if(value == globalVar.glAccountTypeArr[i].glAccountTypeId){
					 					   return '<span title=' + value + '>' + globalVar.glAccountTypeArr[i].description + '</span>';
					 				   }
					 			   }
					 			   return '<span>' + value + '</span>';
					 		   },
					 		   createfilterwidget: function (column, columnElement, widget) {
					 			   accutils.createJqxDropDownList(widget, globalVar.glAccountTypeArr, {valueMember: 'glAccountTypeId', displayMember: 'description'});			   				
					 		   },
						},
					];
		
		var config = {
				url: 'JQGetListChartOfAccountOriginationTrans',
				showtoolbar : false,
				width : 600,
				virtualmode: true,
				editable: false,
				filterable: true,
				localization: getLocalization(),
				pageable: true,
				source:{
					pagesize: 10
				}
		};
		Grid.initGrid(config, datafield, columns, null, $("#transEntryDebitGlAccGrid"));
		Grid.initGrid(config, datafield, columns, null, $("#transEntryCreditGlAccGrid"));
		initDropDownEvent($("#transEntryDebitGlAccDropDown"), $("#transEntryDebitGlAccGrid"), "accountName", "accountCode");
		initDropDownEvent($("#transEntryCreditGlAccDropDown"), $("#transEntryCreditGlAccGrid"), "accountName", "accountCode");
	};
	var initEvent = function(){
		$("#transEntryEnumPartyTypeId").on('select', function(event){
			var args = event.args;
		    if (args) {
		    	var item = args.item;
		    	var value = item.value;
		    	var grid = $("#addPartyTransEntryGrid");
		    	var source = grid.jqxGrid('source');
		    	source._source.url = 'jqxGeneralServicer?sname=JqxGetParties&enumId=' + value;
		    	grid.jqxGrid('source', source);
		    }
		});
		$("#addNewAcctgTranEntryWindow").on('close', function(event){
			resetData();
		});
		$("#addNewAcctgTranEntryWindow").on('open', function(event){
			initOpen();
		});
		$("#cancelAddTransEntry").click(function(){
			$("#addNewAcctgTranEntryWindow").jqxWindow('close');
		});
		$("#saveAndContinueAddTransEntry").click(function(){
			createAcctgTransEntry(false);
		});
		$("#saveAddTransEntry").click(function(){
			createAcctgTransEntry(true);
		});
	};
	var createAcctgTransEntry = function(isCloseWindow){
		var valid = $("#addNewAcctgTranEntryWindow").jqxValidator('validate');
		if(!valid){
			return;
		}
		var rowData = {};
		rowData.amount = $("#transEntryAmount").val();
		var glAccountDebitIndex = $("#transEntryDebitGlAccGrid").jqxGrid('getselectedrowindex');
		var glAccountDebit = $("#transEntryDebitGlAccGrid").jqxGrid('getrowdata', glAccountDebitIndex);
		var glAccountCreditIndex = $("#transEntryCreditGlAccGrid").jqxGrid('getselectedrowindex');
		var glAccountCredit = $("#transEntryCreditGlAccGrid").jqxGrid('getrowdata', glAccountCreditIndex);
		rowData.description = $("#acctgTransEntryDebitDesc").val();
		var partyIndex = $("#addPartyTransEntryGrid").jqxGrid('getselectedrowindex');
		if(partyIndex > -1){
			var party = $("#addPartyTransEntryGrid").jqxGrid('getrowdata', partyIndex);
			rowData.partyId = party.partyId;
			rowData.fullName = party.fullName;
		}
		var productIndex = $("#addProdTransEntryGrid").jqxGrid('getselectedrowindex');
		if(productIndex > -1){
			var product = $("#addProdTransEntryGrid").jqxGrid('getrowdata', productIndex);
			rowData.productId = product.productId;
			rowData.productName = product.productName;
		}
		rowData.reciprocalSeqId = getReciprocalSeqId();
		
		var rowDataDebit = $.extend({}, rowData);
		var rowDataCredit = $.extend({}, rowData);
		rowDataDebit.glAccountCodeDebit = glAccountDebit.accountCode;
		rowDataDebit.debitCreditFlag = "D";
		rowDataDebit.glAccountId = glAccountDebit.glAccountId;
		
		rowDataCredit.glAccountCodeCredit = glAccountCredit.accountCode;
		rowDataCredit.debitCreditFlag = "C";
		rowDataCredit.glAccountId = glAccountCredit.glAccountId;
		$("#acctgTransEntryGrid").jqxGrid('addrow', null, rowDataDebit, "first");
		$("#acctgTransEntryGrid").jqxGrid('addrow', null, rowDataCredit, "first");
		if(isCloseWindow){
			$("#addNewAcctgTranEntryWindow").jqxWindow('close');
		}else{
			resetData();
			initOpen();
		}
	};
	
	var getReciprocalSeqId = function(){
		_reciprocalSeqId++;
		var reciprocalSeqId = _reciprocalSeqId.toString();
		if(reciprocalSeqId.length < _totalNumberReciprocalSeqId){
			var temp = "";
			for(var i = reciprocalSeqId.length; i < _totalNumberReciprocalSeqId; i++){
				temp += "0";
			}
			reciprocalSeqId = temp + reciprocalSeqId;
		}
		return reciprocalSeqId;
	};
	
	var initWindow = function(){
		accutils.createJqxWindow($("#addNewAcctgTranEntryWindow"), 450, 380);
	};
	
	var initValidator = function(){
		$("#addNewAcctgTranEntryWindow").jqxValidator({
			rules: [
				{ input: '#transEntryDebitGlAccDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						var selectIndex = $("#transEntryDebitGlAccGrid").jqxGrid('getselectedrowindex');
						if(selectIndex < 0){
							return false;
						}
						return true;
					}
				},
				{ input: '#transEntryCreditGlAccDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						var selectIndex = $("#transEntryCreditGlAccGrid").jqxGrid('getselectedrowindex');
						if(selectIndex < 0){
							return false;
						}
						return true;
					}
				},
			]
		});
	};
	
	var initOpen = function(){
		
	};
	
	var resetData = function(){
		Grid.clearForm($("#addNewAcctgTranEntryWindow"));
		resetGrid($("#transEntryDebitGlAccGrid"));
		resetGrid($("#transEntryCreditGlAccGrid"));
		resetGrid($("#addPartyTransEntryGrid"));
		resetGrid($("#addProdTransEntryGrid"));
	};
	
	var resetGrid = function(gridEle){
		gridEle.jqxGrid('clearselection');
		gridEle.jqxGrid('gotopage', 0);
	};
	
	var initDropDownEvent = function(dropDownEle, gridEle, displayField, displayAddField){
		gridEle.on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var data = gridEle.jqxGrid('getrowdata', boundIndex);
			var desc = data[displayField];
			if(typeof(displayAddField) != "undefined"){
				desc += " [" + data[displayAddField] + "]";
			}
			var dropDownContent = '<div class="innerDropdownContent">' + desc + '</div>';
			dropDownEle.jqxDropDownButton('setContent', dropDownContent);
			dropDownEle.jqxDropDownButton('close');
		});
	};
	return{
		init: init,
	}
}());

$(document).ready(function () {
	createAcctgTransObj.init();
	acctgTranEntryObj.init();
	createAcctgTransEntryObj.init();
});