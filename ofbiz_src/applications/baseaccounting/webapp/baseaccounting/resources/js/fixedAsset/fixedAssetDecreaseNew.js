var createFixedAssetDecreaseObj = (function(){
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
		$("#voucherNumber").jqxInput({width: '91%', height: 22});
		$("#description").jqxInput({width: '91%', height: 22});
		$("#voucherDate").jqxDateTimeInput({width: '93%', height: 25});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#decreaseReasonTypeId"), globalVar.decreaseReasonTypeArr, {valueMember: 'decreaseReasonTypeId', displayMember: 'description', placeHolder: uiLabelMap.filterchoosestring, width: '93%', height: 25});
	};
	var initGrid = function(){
		var grid = $("#fixedAssetItemGrid");
		var datafields = [{name: 'fixedAssetId', type: 'string'},
		                 {name: 'fixedAssetName', type: 'string'},
		                 {name: 'groupName', type: 'string'},
		                 {name: 'depreciationGlAccount', type: 'string'},
		                 {name: 'costGlAccount', type: 'string'},
		                 {name: 'lossGlAccount', type: 'string'},
		                 {name: 'remainValueGlAccount', type: 'string'},
		                 {name: 'purchaseCost', type: 'number'},
		                 {name: 'depreciationAmount', type: 'number'},
		                 {name: 'accumulatedDepreciation', type: 'number'},
		                 {name: 'remainValue', type: 'number'},
		                 {name: 'uomId', type: 'string'},
		                 ];
		var columns = [{text: uiLabelMap.BACCFixedAssetIdShort, datafield: 'fixedAssetId', width: '12%'},
		               {text: uiLabelMap.BACCFixedAssetName, datafield: 'fixedAssetName', width: '25%'},
		               {text: uiLabelMap.OrganizationUsed, datafield: 'groupName', width: '22%'},
		               {text: uiLabelMap.BACCDepreciationGlAccount, datafield: 'depreciationGlAccount', width: '15%'},
		               {text: uiLabelMap.BACCPurCostAcc, datafield: 'costGlAccount', width: '15%'},
		               {text: uiLabelMap.BACCTKChiPhiThanhLy, datafield: 'lossGlAccount', width: '17%'},
		               {text: uiLabelMap.BACCRemainValueGlAccount, datafield: 'remainValueGlAccount', width: '15%', hidden: true},
		               {text: uiLabelMap.BACCPurchaseCost, datafield: 'purchaseCost', columntype: 'numberinput', filtertype: 'number', width: '17%',
		            	   cellsrenderer: function(row, columns, value){
		            		   var data = grid.jqxGrid('getrowdata',row);
		            		   if(data){
		            			   return '<span style="text-align: right">' + formatcurrency(value, data.uomId) + '</span>';
		            		   }
		            	  	}
		               },
		               {text: uiLabelMap.BACCDepreciation, datafield: 'depreciationAmount', columntype: 'numberinput', filtertype: 'number', width: '17%',
		            	   cellsrenderer: function(row, columns, value){
		            		   var data = grid.jqxGrid('getrowdata',row);
		            		   if(data){
		            			   return '<span style="text-align: right">' + formatcurrency(value, data.uomId) + '</span>';
		            		   }
		            	   }
		               },
		               {text: uiLabelMap.AccumulatedDepreciationValue, datafield: 'accumulatedDepreciation', columntype: 'numberinput', filtertype: 'number', width: '16%',
		            	   cellsrenderer: function(row, columns, value){
		            		   var data = grid.jqxGrid('getrowdata',row);
		            		   if(data){
		            			   return '<span style="text-align: right">' + formatcurrency(value, data.uomId) + '</span>';
		            		   }
		            	   }
		               },
		               {text: uiLabelMap.BACCRemainingValue, datafield: 'remainValue', columntype: 'numberinput', filtertype: 'number', width: '16%',
		            	   cellsrenderer: function(row, columns, value){
		            		   var data = grid.jqxGrid('getrowdata',row);
		            		   if(data){
		            			   return '<span style="text-align: right">' + formatcurrency(value, data.uomId) + '</span>';
		            		   }
		            	   }
		               },
		               ];
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "fixedAssetItemGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.BACCDecreasedFixedAsset + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(grid, container, uiLabelMap.accAddNewRow, {type: "popup", container: $("#addFixedAssetWindow")});
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
					pagesize: 5,
					localdata: [],
					id: 'fixedAssetId'
				}
		};
		Grid.initGrid(config, datafields, columns, null, grid);
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#addNewFADecreaseWindow"), 800, 480);
	};
	var initEvent = function(){
		$("#addNewFADecreaseWindow").on('open', function(){
			$("#notedPosted").hide()
			$("#toolbarfixedAssetItemGrid").show()
			if(!_isEdit){
				var date = new Date();
				$("#voucherDate").val(date);
				$("#addNewFADecreaseWindow").jqxWindow('setTitle', uiLabelMap.BACCAddFixedAssetDecrease);
			}else{
				$("#voucherDate").val(_data.voucherDate);
				$("#voucherNumber").val(_data.voucherNumber);
				$("#decreaseReasonTypeId").val(_data.decreaseReasonTypeId);
				$("#description").val(_data.description);
				getFixedAssetDecreaseItem(_data.fixedAssetDecreaseId);
				$("#addNewFADecreaseWindow").jqxWindow('setTitle', uiLabelMap.BACCEditFixedAssetDecrease);
				if(_data['isPosted']==true){
					$("#notedPosted").show()
					$("#toolbarfixedAssetItemGrid").hide()
				}
			}
			var source = $("#fixedAssetGrid").jqxGrid('source');
			source._source.url = 'jqxGeneralServicer?sname=JqxGetListAssetsNotDecrease&fixedAssetDecreaseId=' + _data.fixedAssetDecreaseId;
			$("#fixedAssetGrid").jqxGrid('source', source);
		});
		$("#addNewFADecreaseWindow").on('close', function(){
			var source = $("#fixedAssetGrid").jqxGrid('source');
			source._source.url = '';
			$("#fixedAssetGrid").jqxGrid('source', source);
			Grid.clearForm($("#generalInfo"));
			updateGridLocalData($("#fixedAssetItemGrid"), []);
			_isEdit = false;
			_data = {};
			$("#addNewFADecreaseWindow").jqxValidator('hide');
		});
		$("#cancelAddFADecrease").click(function(e){
			$("#addNewFADecreaseWindow").jqxWindow('close');
		});
		$("#saveAddFADecrease").click(function(e){
			var valid = $("#addNewFADecreaseWindow").jqxValidator('validate');
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
				editFixedAssetDecrease();
			}else{
				bootbox.dialog(uiLabelMap.CreateFixedAssetDscreaseConfirm,
						[
						 {
							 "label" : uiLabelMap.CommonSubmit,
							 "class" : "btn-primary btn-small icon-ok open-sans",
							 "callback": function() {
								 editFixedAssetDecrease();
							 }
						 },
						 {
							 "label" : uiLabelMap.CommonCancel,
							 "class" : "btn-danger btn-small icon-remove open-sans",
						 }
						 ]		
				);
			}
		});
		
		$("#addDecreaseReasonType").click(function(e){
			fixedAssetDecrReasonTypeObj.openWindow();//fixedAssetDecrReasonTypeObj is defined in fixedAssetDecrReasonType.js
		});
	};
	
	var initValidator = function(){
		$("#addNewFADecreaseWindow").jqxValidator({
			rules: [
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
	
	var getFixedAssetDecreaseItem = function(fixedAssetDecreaseId){
		Loading.show('loadingMacro');
		$.ajax({
			url: 'getFixedAssetDecreaseItem',
			type: "POST",
			data: {fixedAssetDecreaseId: fixedAssetDecreaseId},
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
					  updateGridLocalData($("#fixedAssetItemGrid"), response.fixedAssetDecreaseItemList);
				  }
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	var getData = function(){
		var data = {};
		var voucherDate = $("#voucherDate").jqxDateTimeInput('val', 'date');
		data.voucherDate = voucherDate.getTime();
		if($("#voucherNumber").val()){
			data.voucherNumber = $("#voucherNumber").val(); 
		}
		if($("#decreaseReasonTypeId").val()){
			data.decreaseReasonTypeId = $("#decreaseReasonTypeId").val();
		}
		if($("#description").val()){
			data.description = $("#description").val();
		}
		var fixedAssetItemRows = $("#fixedAssetItemGrid").jqxGrid('getrows');
		var fixedAssetItemData = [];
		fixedAssetItemRows.forEach(function(row){
			var tempData = {};
			tempData.fixedAssetId = row.fixedAssetId;
			if(typeof(row.remainValue) == 'number'){
				tempData.remainValue = row.remainValue;
			}
			if(typeof(row.accumulatedDepreciation) == 'number'){
				tempData.accumulatedDepreciation = row.accumulatedDepreciation;
			}
			if(row.depreciationGlAccount){
				tempData.depreciationGlAccount = row.depreciationGlAccount;
			}
			if(row.remainValueGlAccount){
				tempData.remainValueGlAccount = row.remainValueGlAccount;
			}
			fixedAssetItemData.push(tempData);
		});
		data.fixedAssetItem = JSON.stringify(fixedAssetItemData);
		return data;
	};
	
	var updateGridLocalData = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		grid.jqxGrid('source', source);
	};
	
	var editFixedAssetDecrease = function(){
		Loading.show('loadingMacro');
		var data = getData();
		var url = '';
		if(_isEdit){
			url = 'editFixedAssetDecreaseAndItem';
			data.fixedAssetDecreaseId = _data.fixedAssetDecreaseId;
		}else{
			url = 'createFixedAssetDecreaseAndItem';
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
					  $("#addNewFADecreaseWindow").jqxWindow('close');
					  $("#jqxgrid").jqxGrid('updatebounddata');
					  Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
				  }
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	var openWindow = function(data){
		_isEdit = true;
		_data = data;
		accutils.openJqxWindow($("#addNewFADecreaseWindow"));
	};
	
	return{
		init: init,
		openWindow: openWindow
	}
}());

/**=======================================================================**/

var fixedAssetItemObj = (function(){
	var init = function(){
		initInput();
		initFADropDownGrid();
		initGlAccountGrid();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$('#purchaseCost').jqxNumberInput({digits: 12, max: 999999999999, min: 0, width: '97%', spinButtons: true, disabled: true});
		$('#depreciationAmount').jqxNumberInput({digits: 12, max: 999999999999, min: 0, width: '97%', spinButtons: true, disabled: true});
		$('#accumulatedDepreciationValue').jqxNumberInput({digits: 12, max: 999999999999, min: 0, width: '97%', spinButtons: true, disabled: true });
		$('#remainingValue').jqxNumberInput({digits: 12, max: 999999999999, min: 0, width: '97%', spinButtons: true });
		$("#costGlAccount").jqxInput({width: '95%', height: 22, disabled: true});
		$("#lossGlAccount").jqxInput({width: '95%', height: 22, disabled: true});
	};
	var initFADropDownGrid = function(){
		$("#fixedAssetDropDown").jqxDropDownButton({width: "97%", height: 25});
		var grid = $("#fixedAssetGrid");
		var datafields = [{name: 'fixedAssetId', type: 'string'},
		                  {name: 'fixedAssetName', type: 'string'},
		                  {name: 'fullName', type: 'string'},
		                  {name: 'costGlAccountId', type: 'string'},
		                  {name: 'lossGlAccountId', type: 'string'},
		                  {name: 'accDepGlAccountId', type: 'string'},
		                  {name: 'purchaseCost', type: 'number'},
		                  {name: 'accumulatedDep', type: 'number'},
		                  {name: 'uomId', type: 'string'},
		                  ];
		
		var columns = [{text: uiLabelMap.BACCFixedAssetIdShort, datafield: 'fixedAssetId', width: '14%'},
		               {text: uiLabelMap.BACCFixedAssetName, datafield: 'fixedAssetName', width: '27%'},
		               {text: uiLabelMap.OrganizationUsed, datafield: 'fullName', width: '18%'},
		               {text: uiLabelMap.BACCPurCostAcc, datafield: 'costGlAccountId', width: '16%'},
		               {text: uiLabelMap.BACCPurchaseCost, dataField: 'purchaseCost', columntype: 'numberinput', filtertype: 'number',
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
	var initGlAccountGrid = function(){
		$("#depreciationAccDropDown").jqxDropDownButton({width: '98%', height: 25});
		//$("#remainValueAccDropDown").jqxDropDownButton({width: '98%', height: 25});
		
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
		Grid.initGrid(configGrid1, datafields, columns, null, $("#depreciationAccGrid"));
		//Grid.initGrid(configGrid2, datafields, columns, null, $("#remainValueAccGrid"));
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#addFixedAssetWindow"), 500, 430);
	};
	var initEvent = function(){
		$("#depreciationAccGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#depreciationAccGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountId + '</div>';
			$("#depreciationAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#depreciationAccDropDown").attr("data-value", rowData.glAccountId);
			$("#depreciationAccDropDown").jqxDropDownButton('close');
		});
		
		/*$("#remainValueAccGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#remainValueAccGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountId + '</div>';
			$("#remainValueAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#remainValueAccDropDown").attr("data-value", rowData.glAccountId);
			$("#remainValueAccDropDown").jqxDropDownButton('close');
		});*/

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
			$("#costGlAccount").val(rowData.costGlAccountId);
			$("#lossGlAccount").val(rowData.lossGlAccountId);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.accDepGlAccountId + '</div>';
			$("#depreciationAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#purchaseCost").val(rowData.purchaseCost);
			$("#depreciationAmount").val(rowData.purchaseCost);
			$("#accumulatedDepreciationValue").val(rowData.accumulatedDep);
			$("#remainingValue").val(rowData.purchaseCost - rowData.accumulatedDep);
		});
		$("#accumulatedDepreciationValue").on('valueChanged', function(event){
			var value = event.args.value;
			var remainingValue = $("#purchaseCost").val() - value;
			if(remainingValue > 0){
				$("#remainingValue").val(remainingValue);
			}
		});
		$("#addFixedAssetWindow").on('close', function(event){
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
			]
		});
	};
	var resetData = function(){
		$("#fixedAssetGrid").jqxGrid('clearselection');
		$("#depreciationAccGrid").jqxGrid('clearselection');
		//$("#remainValueAccGrid").jqxGrid('clearselection');
		
		$('#fixedAssetGrid').jqxGrid('clearfilters');
		$('#depreciationAccGrid').jqxGrid('clearfilters');
		//$('#remainValueAccGrid').jqxGrid('clearfilters');
		
		$('#fixedAssetGrid').jqxGrid('gotopage', 0);
		$('#depreciationAccGrid').jqxGrid('gotopage', 0);
		//$('#remainValueAccGrid').jqxGrid('gotopage', 0);
		
		$("#fixedAssetDropDown").jqxDropDownButton('setContent', "");
		$("#depreciationAccDropDown").jqxDropDownButton('setContent', "");
		//$("#remainValueAccDropDown").jqxDropDownButton('setContent', "");
		
		$("#costGlAccount").val("");
		$("#lossGlAccount").val("");
		$("#purchaseCost").val(0);
		$("#depreciationAmount").val(0);
		$("#accumulatedDepreciationValue").val(0);
		$("#remainingValue").val(0);
	};
	
	var addFixedAsset = function(){
		var data = {};
		data.fixedAssetId = $("#fixedAssetDropDown").attr('data-value');
		data.depreciationGlAccount = $("#depreciationAccDropDown").val();
		//data.remainValueGlAccount = $("#remainValueAccDropDown").attr('data-value');
		var fixedAssetIndex = $("#fixedAssetGrid").jqxGrid('getselectedrowindex');
		var fixedAssetData = $("#fixedAssetGrid").jqxGrid('getrowdata', fixedAssetIndex);
		data.groupName = fixedAssetData.fullName;
		data.fixedAssetName = fixedAssetData.fixedAssetName;
		data.purchaseCost = fixedAssetData.purchaseCost;
		data.depreciationAmount = fixedAssetData.purchaseCost;
		data.uomId = fixedAssetData.uomId;
		data.accumulatedDepreciation = $("#accumulatedDepreciationValue").val();
		data.remainValue = $("#remainingValue").val();
		data.costGlAccount = $("#costGlAccount").val();
		data.lossGlAccount = $("#lossGlAccount").val();
		var checkRowExists = $("#fixedAssetItemGrid").jqxGrid('getrowboundindexbyid', data.fixedAssetId);
		if(checkRowExists > -1){
			$('#fixedAssetItemGrid').jqxGrid('updaterow', data.fixedAssetId, data);
		}else{
			$("#fixedAssetItemGrid").jqxGrid('addrow', null, data, 'first');
		}
	};
	
	return{
		init: init
	}
}());

$(document).ready(function(){
	createFixedAssetDecreaseObj.init();
	fixedAssetItemObj.init();
});