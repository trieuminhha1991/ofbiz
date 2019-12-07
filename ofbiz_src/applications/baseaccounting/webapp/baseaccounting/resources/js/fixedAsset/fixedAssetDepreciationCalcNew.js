var fixedAssetDepreciationCalcNewObj = (function(){
	var _isEdit = false;
	var _data = {};
	var init = function(){
		initInput();
		initDropDown();
		initGrid();
		initWindow();
		initEvent();
		initValidator();		
	};
	var initInput = function(){
		$("#year").jqxNumberInput({ width: '93%', height: 25,  spinButtons: true, inputMode: 'simple', decimalDigits: 0, digits: 4});
		$("#voucherDate").jqxDateTimeInput({width: '98%', height: 25})
		$("#voucherNumber").jqxInput({width: '96%', height: 22});
		$("#description").jqxInput({width: '96%', height: 22});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#month"), globalVar.monthArr, {valueMember: 'month', displayMember: 'description', width: '100%', height: 25, dropDownWidth: 100});
	};
	var initGrid = function(){
		var grid = $("#fixedAssetItemGrid");
		var datafields = [{name: 'fixedAssetId', type: 'string'},
			                 {name: 'fixedAssetName', type: 'string'},
			                 {name: 'dateAcquired', type: 'date', other:'Timestamp'},
			                 {name: 'dateOfIncrease', type: 'date', other:'Timestamp'},
			                 {name: 'expectedEndOfLife', type: 'date', other:'Timestamp'},				                 
			                 {name: 'fullName', type: 'string'},
			                 {name: 'debitGlAccountId', type: 'string'},
			                 {name: 'creditGlAccountId', type: 'string'},
			                 {name: 'purchaseCost', type: 'number'},
			                 {name: 'monthlyDepRate', type: 'number'},
			                 {name: 'depreciationAmount', type: 'number'},
			                 {name: 'uomId', type: 'string'}
			            ];
		
		var columns = [{text: uiLabelMap.BACCFixedAssetIdShort, datafield: 'fixedAssetId', width: '12%'},
		               {text: uiLabelMap.BACCFixedAssetName, datafield: 'fixedAssetName', width: '25%'},
		               {text: uiLabelMap.BACCDateAcquired, dataField: 'dateAcquired', width: '15%', filtertype: 'range', cellsformat:'dd/MM/yyyy' },
		               {text: uiLabelMap.DateOfIncrease, dataField: 'dateOfIncrease', width: '15%', filtertype: 'range', cellsformat:'dd/MM/yyyy' },		               
		               {text: uiLabelMap.BACCExpectedEndOfLife, dataField: 'expectedEndOfLife', width: '15%', filtertype: 'range', cellsformat:'dd/MM/yyyy'},		               
		               {text: uiLabelMap.OrganizationUsed, datafield: 'fullName', width: '22%'},
		               {text: uiLabelMap.DebitAccount, datafield: 'debitGlAccountId', width: '15%'},
		               {text: uiLabelMap.CreditAccount, datafield: 'creditGlAccountId', width: '15%'},
		               {text: uiLabelMap.BACCPurchaseCost, datafield: 'purchaseCost', columntype: 'numberinput', filtertype: 'number', width: '25%',
		            	   cellsrenderer: function(row, columns, value){
		            		   var data = grid.jqxGrid('getrowdata',row);
		            		   if(data){
		            			   return '<span style="text-align: right">' + formatcurrency(value, data.uomId) + '</span>';
		            		   }
		            	  	}, aggregates: ['sum'],
		            	  	aggregatesrenderer: function (aggregates, column, element) {
		            	  		var renderstring = "<div class='jqx-widget-content jqx-widget-content-" + theme + "' style='float: left; width: 100%; height: 100%; '>";
		            	  		$.each(aggregates, function (key, value) {
		            	  			var color = 'red';
		            	  			renderstring += '<div style="color: ' + color + '; position: relative; margin-top: 10px; margin-right: 2px; text-align: right; overflow: hidden;"><b>' + uiLabelMap.BACCTotal + ': ' + formatcurrency(value) + '</b></div>';
 								});
 								renderstring += "</div>";
 								return renderstring;
 							}
		               },
		               {text: uiLabelMap.BACCMonthlyDepRate, datafield: 'monthlyDepRate', columntype: 'numberinput', filtertype: 'number', width: '17%',
		            	   cellsrenderer: function(row, columns, value){
		            		   if(typeof(value) == 'number'){
		            			   return '<span style="text-align: right">' + value + '%</span>';
		            		   }
		            	   }
		               },
		               {text: uiLabelMap.BACCDepreciationAmount, datafield: 'depreciationAmount', columntype: 'numberinput', filtertype: 'number', width: '20%',
		            	   cellsrenderer: function(row, columns, value){
		            		   var data = grid.jqxGrid('getrowdata',row);
		            		   if(data){
		            			   return '<span style="text-align: right">' + formatcurrency(value, data.uomId) + '</span>';
		            		   }
		            	  	}, aggregates: ['sum'],
		            	  	aggregatesrenderer: function (aggregates, column, element) {
		            	  		var renderstring = "<div class='jqx-widget-content jqx-widget-content-" + theme + "' style='float: left; width: 100%; height: 100%; '>";
		            	  		$.each(aggregates, function (key, value) {
		            	  			var color = 'red';
		            	  			renderstring += '<div style="color: ' + color + '; position: relative; margin-top: 10px; margin-right: 2px; text-align: right; overflow: hidden;"><b>' + uiLabelMap.BACCTotal + ': ' + formatcurrency(value) + '</b></div>';
 								});
 								renderstring += "</div>";
 								return renderstring;
 							}
		               }
		          ];
		
		var config = {
			showdefaultloadelement: false,
			autoshowloadelement: false,
			dropDownHorizontalAlignment: 'right',
			datafields: datafields,
			columns: columns,
			useUrl: false,
			pagesize: 5,
			width: '100%',
			bindresize: true,
			localization: getLocalization(),
			showtoolbar: true,
			virtualmode: false,
			showfilterrow: true,
			filterable: true,
			sortable: true,
			editable: false,
			pageable: true,
			showstatusbar: true,
			statusbarheight: 40,
			showaggregates: true,
			rendertoolbarconfig: {
				titleProperty: uiLabelMap.FixedAssetDepreciationCalc,
				customcontrol1: customcontrol1,
				customcontrol2: customcontrol2
			}
		};
		
		new OlbGrid(grid, null, config, []);
	};
	
	var removeItemFromGrid = function(){
		var rowindexes = $("#fixedAssetItemGrid").jqxGrid("getselectedrowindexes");
		if (typeof(rowindexes) == "undefined" || rowindexes.length < 1) {
			jOlbUtil.alert.error(uiLabelMap.BACCYouNotYetChooseFA);
			return false;
		}
		for (var i = 0; i < rowindexes.length; i++) {
			var dataItem = $("#fixedAssetItemGrid").jqxGrid("getrowdata", rowindexes[i]);
			if (dataItem) {
				$("#fixedAssetItemGrid").jqxGrid('deleterow', dataItem.uid);
			}
		}
	};
	
	var openPopupAdd = function(){
		$("#addFixedAssetWindow").jqxWindow("open");
	};
	
	var initWindow = function(){
		accutils.createJqxWindow($("#addNewFADepreciationCalcWindow"), 900, 520);
	};
	
	var initEvent = function(){
		$("#addNewFADepreciationCalcWindow").on('open', function(e){
			$("#notedPostDepreciation").hide()
			$("#toolbarfixedAssetItemGrid").show()
			if (_isEdit) {
				if(_data['isPosted']==true){
					$("#notedPostDepreciation").show();
					$("#toolbarfixedAssetItemGrid").hide();
				}
				$("#year").val(_data.year);
				$("#voucherDate").val(_data.voucherDate);
				$("#voucherNumber").val(_data.voucherNumber);
				$("#description").val(_data.description);
				$("#month").val(_data.month);
				getFixedAssetDepreCalcItem(_data.depreciationCalcId);
				
				var _gridObj = $('#fixedAssetGrid');
				var tmpSource = _gridObj.jqxGrid('source');
				tmpSource._source.url = "jqxGeneralServicer?sname=JqxGetListFixedAssetsDepreciation&depreciationCalcId=" + _data.depreciationCalcId;
				_gridObj.jqxGrid('clearselection');
				_gridObj.jqxGrid('source', tmpSource);
			} else {
				var date = new Date();
				$("#year").val(date.getFullYear());
				$("#month").val(date.getMonth());
			}
		});
		
		$("#addNewFADepreciationCalcWindow").on('close', function(e){
			Grid.clearForm($("#generalInfo"));
			updateGridLocalData($("#fixedAssetItemGrid"), []);
			$("#addNewFADepreciationCalcWindow").jqxValidator('hide');
			_isEdit = false;
			_data = {};
		});
		
		var updateVoucherDate = function(month, year){
			if(typeof(month) == 'number' && year > 0){
				var date = new Date(year, month + 1, 0);
				$("#voucherDate").val(date);
			}
		};
		
		$("#month").on('select', function(event){
			var args = event.args;
		    if(args){
		    	var item = args.item;
		    	var month = item.value;
		    	updateVoucherDate(month, $("#year").val());
				
		    	var _gridObj = $('#fixedAssetGrid');
				var tmpSource = _gridObj.jqxGrid('source');
				var date = new Date($("#year").val(), parseInt($("#month").val()) + 1, 0);
				var month = $("#month").val();
				var year = $("#year").val();
				if (date && month && year) {
					if (!_isEdit) {
						tmpSource._source.url = "jqxGeneralServicer?sname=JqxGetListAssetsNotDepreciation&year=" + year + '&month=' + month + '&date=' + date.getTime();
						_gridObj.jqxGrid('clearselection');
						_gridObj.jqxGrid('source', tmpSource);
						
						getListFixedAssetItemGrid(date.getTime(), month, year);
					}
				}
		    }
		});
		
		$("#year").on('valueChanged', function (event){
			var year = event.args.value;
			var monthItem = $("#month").jqxDropDownList('getSelectedItem');
			if (monthItem) {
				updateVoucherDate(monthItem.value, year);
			}
			
	    	var _gridObj = $('#fixedAssetGrid');
			var tmpSource = _gridObj.jqxGrid('source');
			var date = new Date($("#year").val(), parseInt($("#month").val()) + 1, 0);
			var month = $("#month").val();
			var year = $("#year").val();
			if (date && month && year) {
				if (!_isEdit) {
					tmpSource._source.url = "jqxGeneralServicer?sname=JqxGetListAssetsNotDepreciation&year=" + year + '&month=' + month + '&date=' + date.getTime();
					_gridObj.jqxGrid('clearselection');
					_gridObj.jqxGrid('source', tmpSource);	
					
					getListFixedAssetItemGrid(date.getTime(), month, year);
				}
			}
		});
		
		$("#cancelAddFADepreciation").click(function(e){
			$("#addNewFADepreciationCalcWindow").jqxWindow('close');
		});
		
		$("#saveAddFADepreciation").click(function(e){
			var valid = $("#addNewFADepreciationCalcWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var rows = $("#fixedAssetItemGrid").jqxGrid('getrows');
			if(rows.length < 1){
				bootbox.dialog(uiLabelMap.FixedAssetIsNotSelected,
						[
						 {
							 "label" : uiLabelMap.CommonClose,
							 "class" : "btn-danger btn-small icon-remove open-sans",
						 }
						 ]		
				);
				return false;
			}
			if(_isEdit){
				editFixedAssetDepreciationCalc();
			}else{
				bootbox.dialog(uiLabelMap.CreateFixedAssetDepreciationCalcConfirm,
						[{
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								editFixedAssetDepreciationCalc();
							}
						},
						{
							"label" : uiLabelMap.CommonCancel,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);
			}
		});
	};
	
	var getListFixedAssetItemGrid = function(date, month, year) {
		Loading.show('loadingMacro');
 		$.ajax({
 			url: 'getListFixedAssetItemGrid',
 			type: "POST",
 			data: {
 				date: date,
 				month: month,
 				year: year
 			},
 			success: function(response) {
 				updateGridLocalData($("#fixedAssetItemGrid"), response.listReturn);
 			},
 			complete: function(jqXHR, textStatus){
 				Loading.hide('loadingMacro');	
 			}
 		});
	};
	
	var initValidator = function(){
		$("#addNewFADepreciationCalcWindow").jqxValidator({
			rules: [
					{ input: '#month', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
						rule: function (input, commit) {
							if(input.val()){
								return true;
							}
							return false;
						}
					},
					{ input: '#voucherDate', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
						rule: function (input, commit) {
							if(input.val()){
								return true;
							}
							return false;
						}
					}
				]
		});
	};
	
	var getData = function(){
		var data = {};
		data.month = $("#month").val();
		data.year = $("#year").val();
		var voucherDate = $("#voucherDate").jqxDateTimeInput('val', 'date');
		data.voucherDate = voucherDate.getTime();
		data.voucherNumber = $("#voucherNumber").val();
		if($("#description").val()){
			data.description = $("#description").val();
		}
		var rows = $("#fixedAssetItemGrid").jqxGrid('getrows');
		data.fixedAssetItem = JSON.stringify(rows);
		return data;
	};
	var editFixedAssetDepreciationCalc = function(){
		Loading.show('loadingMacro');
		var data = getData();
		var url = '';
		if(_isEdit){
			url = 'editFixedAssetDepreciationCalcAndItem';
			data.depreciationCalcId = _data.depreciationCalcId;
		}else{
			url = 'createFixedAssetDepreciationCalcAndItem';
		}
		$.ajax({
			url: url,
			type: "POST",
			data: data,
			success: function(response) {
				  if(response.responseMessage == "error"){
					  bootbox.dialog(response.errorMessage,
								[
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);		
				  }else{
					  $("#addNewFADepreciationCalcWindow").jqxWindow('close');
					  $("#jqxgrid").jqxGrid('updatebounddata');
					  Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
				  }
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	var getFixedAssetDepreCalcItem = function(depreciationCalcId){
		Loading.show('loadingMacro');
		$.ajax({
			url: 'getFixedAssetDepreCalcItem',
			type: "POST",
			data: {depreciationCalcId: depreciationCalcId},
			success: function(response) {
				  if(response.responseMessage == "error"){
					  bootbox.dialog(response.errorMessage,
								[
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);		
				  }else{
					  updateGridLocalData($("#fixedAssetItemGrid"), response.fixedAssetDepreCalcItemList);
				  }
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	var updateGridLocalData = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		source._source.id = 'fixedAssetId';
		grid.jqxGrid('source', source);
	};
	
	var openWindow = function(data){
		_data = data;
		_isEdit = true;
		accutils.openJqxWindow($("#addNewFADepreciationCalcWindow"));
	};
	return{
		init: init,
		openWindow: openWindow,
		removeItemFromGrid: removeItemFromGrid,
		openPopupAdd: openPopupAdd
	}
}());

/**=======================================================================**/

var fixedAssetItemObj = (function(){
	var init = function(){
		initFADropDownGrid();
		initGlAccountGrid();
		initInput();
		initWindow();
		initEvent();
		initValidator();
	};
	
	var initGlAccountGrid = function(){
		$("#debitAccDropDown").jqxDropDownButton({width: '98%', height: 25});
		$("#creditAccDropDown").jqxDropDownButton({width: '98%', height: 25});
		
		var datafields = [{name: 'glAccountId', type: 'string'}, 
		                  {name: 'accountCode', type: 'string'},
						  {name: 'accountName', type: 'string'}
						  ];
		var columns = [
						{text: uiLabelMap.BACCGlAccountId, datafield: 'accountCode', width: '30%'},
						{text: uiLabelMap.BACCAccountName, datafield: 'accountName'}
					];
		
		var configGrid1 = {
				url: 'JqxGetListGlAccounts',
				filterable: true,
				showtoolbar : false,
				width : 450,
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				source:{
					pagesize: 5
				}
		};
		var configGrid2 = {
				url: 'JqxGetListGlAccountByClass&glAccountClassId=AMORTIZATION',
				filterable: true,
				showtoolbar : false,
				width : 450,
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				source:{
					pagesize: 5
				}
		};
		Grid.initGrid(configGrid1, datafields, columns, null, $("#debitAccGrid"));
		Grid.initGrid(configGrid2, datafields, columns, null, $("#creditAccGrid"));
	};
	
	var initFADropDownGrid = function(){
		$("#fixedAssetDropDown").jqxDropDownButton({width: "97%", height: 25});
		var grid = $("#fixedAssetGrid");
		var datafields = [{name: 'fixedAssetId', type: 'string'},
		                  {name: 'fixedAssetName', type: 'string'},
		                  {name: 'dateAcquired', type: 'date', other:'Timestamp'},
		                  {name: 'dateOfIncrease', type: 'date', other:'Timestamp'},
		                  {name: 'expectedEndOfLife', type: 'date', other:'Timestamp'},		                  
		                  {name: 'fullName', type: 'string'},
		                  {name: 'costGlAccountId', type: 'string'},
		                  {name: 'depGlAccountId', type: 'string'},
		                  {name: 'accDepGlAccountId', type: 'string'},
		                  {name: 'monthlyDepRate', type: 'number'},
		                  {name: 'monthlyDepAmount', type: 'number'},
		                  {name: 'purchaseCost', type: 'number'},
		                  {name: 'uomId', type: 'string'},
		                  ];
		
		var columns = [{text: uiLabelMap.BACCFixedAssetIdShort, datafield: 'fixedAssetId', width: '17%'},
		               {text: uiLabelMap.BACCFixedAssetName, datafield: 'fixedAssetName', width: '27%'},
		               {text: uiLabelMap.BACCDateAcquired, dataField: 'dateAcquired', width: '22%', filtertype: 'range', cellsformat:'dd/MM/yyyy' },
		               {text: uiLabelMap.DateOfIncrease, dataField: 'dateOfIncrease', width: '16%', filtertype: 'range', cellsformat:'dd/MM/yyyy' },		               
		               {text: uiLabelMap.BACCExpectedEndOfLife, dataField: 'expectedEndOfLife', width: '16%', filtertype: 'range', cellsformat:'dd/MM/yyyy' },
		               {text: uiLabelMap.OrganizationUsed, datafield: 'fullName', width: '22%'},
		               {text: uiLabelMap.BACCAllocGlAccoutId, datafield: 'depGlAccountId', width: '14%'},
		               {text: uiLabelMap.BACCdepGlAccountId, datafield: 'accDepGlAccountId', width: '20%'},
		               {text: uiLabelMap.BACCMonthlyDepRate, datafield: 'monthlyDepRate', columntype: 'numberinput', filtertype: 'number', width: '22%',
		            	   cellsrenderer: function(row, columns, value){
		            		   if(typeof(value) == 'number'){
		            			   return '<span style="text-align: right">' + value + '%</span>';
		            		   }
		            	   }
		               },
		               {text: uiLabelMap.BACCMonthlyDepAmount, dataField: 'monthlyDepAmount', columntype: 'numberinput', filtertype: 'number', width: '25%',
							cellsrenderer: function(row, columns, value){
		                		var data = grid.jqxGrid('getrowdata',row);
		                		if(data){
		                			return '<span style="text-align: right">'+ formatcurrency(value, data.uomId) + '</span>';
		                		}
		                	}
					   },
		               ];		
		var config = {
      		width: 600, 
      		virtualmode: true,
      		showfilterrow: true,
      		showtoolbar: false,
      		selectionmode: 'singlerow',
      		pageable: true,
      		sortable: true,
	        filterable: true,
	        editable: false,
	        url: '',
	        source: {
	        	pagesize: 5
	        }
      	};
      	Grid.initGrid(config, datafields, columns, null, grid);
	};
	
	var initInput = function(){
		var decimalSeparator = '.';
		var groupSeparator = ',';
		$('#purchaseCost').jqxNumberInput({digits: 12, max: 999999999999, min: 0, decimalSeparator: decimalSeparator, groupSeparator: groupSeparator, width: '97%', spinButtons: true, disabled: true});
		$('#monthlyDepRate').jqxNumberInput({digits: 3, max: 100, min: 0, width: '97%', decimalSeparator: decimalSeparator, groupSeparator: groupSeparator, spinButtons: true, disabled: true, symbolPosition: 'right', symbol: '%'});
		$('#depreciationAmount').jqxNumberInput({digits: 12, max: 999999999999, min: 0, decimalSeparator: decimalSeparator, groupSeparator: groupSeparator, width: '97%', spinButtons: true});
	};
	
	var initWindow = function(){
		accutils.createJqxWindow($("#addFixedAssetWindow"), 480, 360);
	};
	
	var initEvent = function(){
		$("#debitAccGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#debitAccGrid").jqxGrid('getrowdata', boundIndex);			
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountId + '</div>';
			$("#debitAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#debitAccDropDown").attr("data-value", rowData.glAccountId);
			$("#debitAccDropDown").jqxDropDownButton('close');
		});
		
		$("#creditAccGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#creditAccGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountId + '</div>';
			$("#creditAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#creditAccDropDown").attr("data-value", rowData.glAccountId);
			$("#creditAccDropDown").jqxDropDownButton('close');
		});

		$("#fixedAssetGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#fixedAssetGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.fixedAssetName + ' [' + rowData.fixedAssetId + ']</div>';
			$("#fixedAssetDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#fixedAssetDropDown").attr("data-value", rowData.fixedAssetId);
			$("#fixedAssetDropDown").jqxDropDownButton('close');
		});
		$("#fixedAssetGrid").on('rowselect', function(event){
			var rowData = args.row;
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.accDepGlAccountId + '</div>';
			$("#creditAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#creditAccDropDown").attr("data-value", rowData.accDepGlAccountId);

			var dropDownContent = '<div class="innerDropdownContent">' + rowData.depGlAccountId + '</div>';
			$("#debitAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#debitAccDropDown").attr("data-value", rowData.depGlAccountId);
			
			$('#purchaseCost').val(rowData.purchaseCost);
			$('#monthlyDepRate').val(rowData.monthlyDepRate);
			$('#depreciationAmount').val(rowData.monthlyDepAmount);
		});
		
		$("#addFixedAssetWindow").on('close', function(e){
			resetData();
		});
		
		$("#cancelAddFixedAsset").click(function(e){
			$("#addFixedAssetWindow").jqxWindow('close');
		});
		$("#saveAndContinueAddFixedAsset").click(function(e){
			var valid = $("#addFixedAssetWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addFixedAsset();
			resetData();
		});
		$("#saveAddFixedAsset").click(function(e){
			var valid = $("#addFixedAssetWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addFixedAsset();
			$("#addFixedAssetWindow").jqxWindow('close');
		});
	};
	
	var initValidator = function(){
		$("#addFixedAssetWindow").jqxValidator({
			rules: [
				{ input: '#fixedAssetDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(input.val()){
							return true;
						}
						return false;
					}
				},
				{ input: '#creditAccDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(input.val()){
							return true;
						}
						return false;
					}
				},
				{ input: '#debitAccDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(input.val()){
							return true;
						}
						return false;
					}
				},
			]
		});
	};
	
	var addFixedAsset = function(){
		var data = {};
		data.fixedAssetId = $("#fixedAssetDropDown").attr('data-value');
		data.creditGlAccountId = $("#creditAccDropDown").attr('data-value');
		data.debitGlAccountId = $("#debitAccDropDown").attr('data-value');
		var fixedAssetIndex = $("#fixedAssetGrid").jqxGrid('getselectedrowindex');
		var fixedAssetData = $("#fixedAssetGrid").jqxGrid('getrowdata', fixedAssetIndex);
        data.dateAcquired = fixedAssetData.dateAcquired;
        data.dateOfIncrease = fixedAssetData.dateOfIncrease;
        data.expectedEndOfLife = fixedAssetData.expectedEndOfLife;
		data.fullName = fixedAssetData.fullName;
		data.fixedAssetName = fixedAssetData.fixedAssetName;
		data.purchaseCost = fixedAssetData.purchaseCost;
		data.depreciationAmount = $("#depreciationAmount").val();
		data.uomId = fixedAssetData.uomId;
		data.monthlyDepRate = fixedAssetData.monthlyDepRate;
		var checkRowExists = $("#fixedAssetItemGrid").jqxGrid('getrowboundindexbyid', data.fixedAssetId);
		if(checkRowExists > -1){
			$('#fixedAssetItemGrid').jqxGrid('updaterow', data.fixedAssetId, data);
		}else{
			$("#fixedAssetItemGrid").jqxGrid('addrow', null, data, 'first');
		}
	};
	
	var resetData = function(){
		$("#fixedAssetGrid").jqxGrid('clearselection');
		$("#creditAccGrid").jqxGrid('clearselection');
		$("#debitAccGrid").jqxGrid('clearselection');
		
		$('#fixedAssetGrid').jqxGrid('clearfilters');
		$('#creditAccGrid').jqxGrid('clearfilters');
		$('#debitAccGrid').jqxGrid('clearfilters');
		
		$('#fixedAssetGrid').jqxGrid('gotopage', 0);
		$('#creditAccGrid').jqxGrid('gotopage', 0);
		$('#debitAccGrid').jqxGrid('gotopage', 0);
		
		$("#fixedAssetDropDown").jqxDropDownButton('setContent', "");
		$("#creditAccDropDown").jqxDropDownButton('setContent', "");
		$("#debitAccDropDown").jqxDropDownButton('setContent', "");
		
		$("#purchaseCost").val(0);
		$("#monthlyDepRate").val(0);
		$("#depreciationAmount").val(0);
	};
	
	return{
		init: init
	}
}());

$(document).ready(function(){
	fixedAssetDepreciationCalcNewObj.init();
	fixedAssetItemObj.init();
});