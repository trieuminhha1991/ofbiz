var editAcctgTransEntryObj = (function(){
	var _data = {};
	var init = function(){
		initGrid();
		initWindow();
		initEvent();
	};
	var initGrid = function(){
		var grid = $("#editTransEntryGrid");
		var cellclassname = function (row, column, value, data) {
			var reciprocalSeqId = parseInt(data.reciprocalSeqId);
			if (reciprocalSeqId % 2 == 0) {
		        return 'background-running';
		    }else{
		    	return 'background-important-nd';
		    }
		};
		
		var initDropDownListEditor = function(editor, cellValue, width, height){
			var source = {
			    datatype: "json",
			    datafields: [
			        {name: 'glAccountId', type: 'string'},
			        {name: 'accountCode', type: 'string'},
			        {name: 'accountName', type: 'string'},
			    ],
			    type: "POST",
			    root: "listReturn",
			    url: "getGlAccountByAccountCode",
			    async: true
			};
			
		   	var dataAdapter = new $.jqx.dataAdapter(source, {
		   		beforeSend: function (xhr) {
		   			editor.jqxDropDownList({disabled: true});
		   		},
		    	loadComplete: function (records) {
		    		editor.jqxDropDownList({disabled: false});
		     		if(cellValue){
		     			editor.val(cellValue);
		     		}
		        },
			});
			editor.jqxDropDownList({
				width: width,
		    	dropDownWidth: 450,
		    	height: height,
		    	source: dataAdapter,
		        //selectedIndex: 0,
		        displayMember: "accountCode",
		        valueMember: "accountCode",
		        filterable: true,
		        renderer: function (index, label, value) {
		        	var item = dataAdapter.records[index];
		        	if (item != null) {
		        		var accountName = item.accountName;
		            	if (accountName && accountName.length > 65){
		            		accountName = accountName.substring(0, 65);
		            		accountName = accountName + '...';
		            	}
		        		var tableItem = '<div class="row-fluid"><div class="span12" style="margin-left: 0; width: 400px; height: 30px">'
			            	   + '<div class="span3" style="margin-left: 10px">' + item.accountCode + '</div>'
			            	   + '<div class="span7" style="margin-left: 10px">' + accountName + '</div>'
			            	   + '</div></div>';
			            return tableItem;
		        	}
		        	return "";
		        },
			});
			
		};
		
		var datafield = [
		                 {name: 'acctgTransEntrySeqId', type: 'string'},
		                 {name: 'glAccountId', type: 'string'},
		                 {name: 'debitAccountCode', type: 'string'},
						 {name: 'creditAccountCode', type: 'string'},
						 {name: 'debitCreditFlag', type: 'string'},
						 {name: 'amount', type: 'number'},
						 {name: 'currencyUomId', type: 'string'},
						 {name: 'reciprocalSeqId', type: 'string'},
						 {name: 'description', type: 'string'},
		                 ];
		                 
		var columns = [{text: uiLabelMap.BACCDebitAccount, datafield: 'debitAccountCode', width: '18%', cellclassname: cellclassname, columntype: 'dropdownlist', editable: false,
							createeditor: function (row, cellvalue, editor, cellText, width, height) {
								initDropDownListEditor(editor, cellvalue, width, height);
							},
							initeditor: function (row, cellvalue, editor, celltext, pressedkey) {
								editor.jqxDropDownList('clearFilter');
								editor.jqxDropDownList('clearSelection');
								if(cellvalue){
									editor.val(cellvalue);
								}
							},
							geteditorvalue: function (row, cellvalue, editor) {
								if(cellvalue){
									return editor.val();
								}
							},
							cellbeginedit: function (row, datafield, columntype) {
								var data = grid.jqxGrid('getrowdata', row);
								var debitCreditFlag = data.debitCreditFlag;
								if(debitCreditFlag == "C"){
									return false;
								}
							}
					   },
		               {text: uiLabelMap.BACCCreditAccount, datafield: 'creditAccountCode', width: '18%', cellclassname: cellclassname, columntype: 'dropdownlist', editable: false,
							createeditor: function (row, cellvalue, editor, cellText, width, height) {
								initDropDownListEditor(editor, cellvalue, width, height);
							},
							initeditor: function (row, cellvalue, editor, celltext, pressedkey) {
								editor.jqxDropDownList('clearFilter');
								editor.jqxDropDownList('clearSelection');
								if(cellvalue){
									editor.val(cellvalue);
								}
							},
							geteditorvalue: function (row, cellvalue, editor) {
								if(cellvalue){
									return editor.val();
								}
							},
							cellbeginedit: function (row, datafield, columntype) {
								var data = grid.jqxGrid('getrowdata', row);
								var debitCreditFlag = data.debitCreditFlag;
								if(debitCreditFlag == "D"){
									return false;
								}
							}
		               },
		               {text: uiLabelMap.BACCAmount, datafield: 'amount', columntype: 'numberinput', width: '22%', cellclassname: cellclassname,
		            	   columntype: 'numberinput',
		            	   cellsrenderer: function(row, column, value){
		            		   if(typeof(value) == 'number'){
		            			   return "<span style='text-align: right'>" + formatcurrency(value) + "</span>";
		            		   }
		            	   },
		            	   createeditor: function (row, cellvalue, editor, cellText, width, height) {
		            		   editor.jqxNumberInput({width: width, height: height, spinButtons: true, decimalDigits: 2, max: 999999999999, digits: 12, inputMode: 'advanced'});
		            	   },
		            	   initeditor: function (row, cellvalue, editor, celltext, pressedkey) {
		            		   if(typeof(cellvalue) == 'number'){
		            			   editor.val(cellvalue);
		            		   }
		            	   },
		            	   cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
		            		   updateAmountSameReciprocalSeqId(row, newvalue);
		            	   }
		               },
		               {text: uiLabelMap.AccountingComments, datafield: 'description', cellclassname: cellclassname},
		               ];	
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "editTransEntryGrid";
			var me = this;
	        var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.BACCTransactionDetail + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
	     	toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
    		
    	};
		var config = {
		   		width: '100%', 
		   		virtualmode: false,
		   		showfilterrow: true,
		   		pageable: true,
		   		sortable: true,
		        filterable: false,
		        editable: true,
		        rendertoolbar: rendertoolbar,
		        showtoolbar: true,
		        editmode: 'selectedcell',
		        selectionmode: 'singlecell',
		        pagesizeoptions: [6, 8, 16, 30, 40],
	        	source: {
	        		pagesize: 8,
	        		localdata: []
	        	}
	   	};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#editAcctgTransEntryWindow"), 750, 510);
	};
	
	var updateAmountSameReciprocalSeqId = function(row, value){
		var total = {};
		total.D = 0;
		total.C = 0;
		var entryEditRowArr = []; 
		var data = $("#editTransEntryGrid").jqxGrid('getrowdata', row);
		var reciprocalSeqId = data.reciprocalSeqId;
		var debitCreditFlag = data.debitCreditFlag;
		total[debitCreditFlag] = value;
		var rows = $("#editTransEntryGrid").jqxGrid('getrows');
		for(var i = 0; i < rows.length; i++){
			if(i != row){
				var tempRowData = $("#editTransEntryGrid").jqxGrid('getrowdata', i);
				if(tempRowData.reciprocalSeqId == reciprocalSeqId){
					var tempDebitCreditFlag = tempRowData.debitCreditFlag;
					total[tempDebitCreditFlag] += tempRowData.amount;
					if(tempDebitCreditFlag != debitCreditFlag){
						entryEditRowArr.push(i);
					}
				}
			}
		}
		var totalDiff = total.C - total.D;
		if(debitCreditFlag == "D"){
			totalDiff = -totalDiff;
		}
		var diff = totalDiff/entryEditRowArr.length;
		entryEditRowArr.forEach(function(index){
			var tempRowData = $("#editTransEntryGrid").jqxGrid('getrowdata', index);
			var amount = tempRowData.amount + diff;
			$("#editTransEntryGrid").jqxGrid('setcellvalue', index, "amount", amount);
		});
	};
	var getData = function(){
		var data = {};
		data.acctgTransId = _data.acctgTransId;
		var rows = $("#editTransEntryGrid").jqxGrid('getrows');
		rows = rows.filter(function(n){ return n });
		data.acctgTransEntries = JSON.stringify(rows);
		return data;
	};
	var initEvent = function(){
		$("#editAcctgTransEntryWindow").on('open', function(){
			Loading.show('loadingMacro');
			$("#acctgTransIdEdit").html(_data.acctgTransId);
			var acctgTransTypeId = "";
			for(i = 0; i < acctgTransTypesData.length; i++){
				if(acctgTransTypesData[i].acctgTransTypeId == _data.acctgTransTypeId){
					acctgTransTypeId = acctgTransTypesData[i].description;
					break;
				}
			}
			$("#acctgTransTypeIdEdit").html(acctgTransTypeId);
			var transactionDate = new Date(_data.transactionDate);
			var transactionDateDesc = (transactionDate.getDate() > 9? "" + transactionDate.getDate() : "0" + transactionDate.getDate()) + "/"
									 + (transactionDate.getMonth() >= 9? "" + (transactionDate.getMonth() + 1) : "0" + (transactionDate.getMonth() + 1)) + "/"
									 + transactionDate.getFullYear() + " "
									 + (transactionDate.getHours() > 9? "" + transactionDate.getHours() : "0" + transactionDate.getHours()) + ":"
									 + (transactionDate.getMinutes() > 9? "" + transactionDate.getMinutes() : "0" + transactionDate.getMinutes()) + ":"
									 + (transactionDate.getSeconds() > 9? "" + transactionDate.getSeconds() : "0" + transactionDate.getSeconds());
									 
			$("#transactionDateEdit").html(transactionDateDesc);
			var isPosted = "";
			for(i = 0; i < isPostedData.length; i++){
				if(isPostedData[i].isPosted == _data.isPosted){
					isPosted = isPostedData[i].description;
					break;
				}
			}
			$("#isPostedEdit").html(isPosted);
			$.when(
					$.ajax({
						url: 'getAcctgTransEntry',
						data: {acctgTransId: _data.acctgTransId},
						type: 'POST',
						success: function(response){
							if(response.responseMessage == "success"){
								updateGridData(response.listReturn);
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
						},
					}),
					$.ajax({
						url: 'getAcctgTransHistoryLast',
						data: {acctgTransId: _data.acctgTransId},
						type: 'POST',
						success: function(response){
							if(response.responseMessage == "error"){
								bootbox.dialog(response.errorMessage,
										[
										 {
											 "label" : uiLabelMap.CommonClose,
											 "class" : "btn-danger btn-small icon-remove open-sans",
										 }]		
								);		
							}else{
								if(response.changeDate && response.fullName){
									$("#changeDateLstUpdate").html(response.changeDate);
									$("#changeDateByUser").html(response.fullName);
									$("#lastUpdateByUser").show();
								}
							}
						},
					})
			).done(function(){
				Loading.hide('loadingMacro');
			});
		});
		$("#editAcctgTransEntryWindow").on('close', function(){
			resetData();
		});
		$("#cancelEditTransEntry").click(function(){
			$("#editAcctgTransEntryWindow").jqxWindow('close');
		});
		$("#saveEditTransEntry").click(function(){
			Loading.show('loadingMacro');
			var data = getData();
			$.ajax({
				url: 'updateAcctgTransEntryOlb',
				data: data,
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						$("#editAcctgTransEntryWindow").jqxWindow('close');
						$("#jqxgridTrans").jqxGrid('updatebounddata');
						Grid.renderMessage('jqxgridTrans', response.successMessage, {template : 'success', appendContainer : '#containerjqxgridTrans'});
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
				},
			});
		});
		$("#viewAcctgTransHis").click(function(){
			viewAcctgTransHistoryObj.openWindow(_data);
		});
	};
	
	var updateGridData = function(data){
		var source = $("#editTransEntryGrid").jqxGrid('source');
		source._source.localdata = data;
		$("#editTransEntryGrid").jqxGrid('source', source);
	};
	var resetData = function(){
		$("#acctgTransIdEdit").html("");
		$("#acctgTransTypeIdEdit").html("");
		$("#transactionDateEdit").html("");
		$("#isPostedEdit").html("");
		updateGridData([]);
		_data = {};
		$("#changeDateLstUpdate").html("");
		$("#changeDateByUser").html("");
		$("#lastUpdateByUser").hide();
	};
	var openWindow = function(data){
		_data = data;
		accutils.openJqxWindow($("#editAcctgTransEntryWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).on('ready', function(){
	editAcctgTransEntryObj.init();
});