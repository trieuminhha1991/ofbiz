var fixedAssetIncreaseNewStep2 = (function(){
	var init = function(){
		initGrid();
	};
	var initGrid = function(){
		var grid = $("#fixedAssetItemGrid");
		var datafields = [{name: 'fixedAssetId', type: 'string'},
		                 {name: 'fixedAssetName', type: 'string'},
		                 {name: 'groupName', type: 'string'},
		                 {name: 'debitGlAccount', type: 'string'},
		                 {name: 'creditGlAccount', type: 'string'},
		                 {name: 'uomId', type: 'string'},
		                 {name: 'purchaseCost', type: 'number'},
		                 ];
		var columns = [{text: uiLabelMap.BACCFixedAssetIdShort, datafield: 'fixedAssetId', width: '14%'},
		               {text: uiLabelMap.BACCFixedAssetName, datafield: 'fixedAssetName', width: '27%'},
		               /*{text: uiLabelMap.DebitAccount, datafield: 'debitGlAccount', width: '15%'},
		               {text: uiLabelMap.CreditAccount, datafield: 'creditGlAccount', width: '15%'}, */
		               {text: uiLabelMap.BACCPurchaseCost, datafield: 'purchaseCost', columntype: 'numberinput', filtertype: 'number',
		            	   cellsrenderer: function(row, columns, value){
		            		   var data = grid.jqxGrid('getrowdata',row);
		            		   return '<span>' + formatcurrency(value, data.uomId) + '</span>';
		            	  	}
		               }
		               ];
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "fixedAssetItemGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.FixedAssetPurchased + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(grid, container, uiLabelMap.accAddNewRow, {type: "popup", container: $("#newFixedAssetWindow")});
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
					pagesize: 10,
					localdata: [],
					id: 'fixedAssetId'
				}
		};
		Grid.initGrid(config, datafields, columns, null, grid);
	};
	var resetData = function(){
		var source = $("#fixedAssetItemGrid").jqxGrid('source');
		source._source.localdata = [];
		$("#fixedAssetItemGrid").jqxGrid('source', source);
		
		var _gridObj = $('#fixedAssetGrid');
		var tmpSource = _gridObj.jqxGrid('source');
		tmpSource._source.url = "jqxGeneralServicer?sname=JqxGetListAssetsNotIncrease";
		_gridObj.jqxGrid('clearselection');
		_gridObj.jqxGrid('source', tmpSource);


	};
	var validate = function(){
		var rows = $("#fixedAssetItemGrid").jqxGrid('getrows');
		if(rows.length > 0){
			return true;
		}
		bootbox.dialog(uiLabelMap.FixedAssetIsNotSelected,
				[
				 {
					 "label" : uiLabelMap.CommonClose,
					 "class" : "btn-danger btn-small icon-remove open-sans",
				 }
				 ]		
		);
		return false;
	};
	var getData = function(){
		var rows = $("#fixedAssetItemGrid").jqxGrid('getrows');
		return rows;
	};
	var windownOpenInit = function(isEdit, data){
		$("#notedPosted").hide();
		$("#toolbarfixedAssetItemGrid").show()
		if(isEdit){
			Loading.show('loadingMacro');
			$.ajax({
				url: 'getFixedAssetIncreaseItem',
				type: "POST",
				data: {fixedAssetIncreaseId: data.fixedAssetIncreaseId},
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
						  updateGridLocalData($("#fixedAssetItemGrid"), response.fixedAssetIncreaseItemList);
						  if(data['isPosted'] == true){
							  $("#toolbarfixedAssetItemGrid").hide();
							  $("#notedPosted").show();
						  }
					  }
				  },
				complete:  function(jqXHR, textStatus){
					Loading.hide('loadingMacro');	
				}
			});
		}
	};
	var updateGridLocalData = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		grid.jqxGrid('source', source);
	};
	return{
		init: init,
		resetData: resetData,
		validate: validate,
		getData: getData,
		windownOpenInit: windownOpenInit
	}
}());

/**==============================================**/

var newFixedAssetItemObj = (function(){
	var init = function(){
		initFixedAssetDropDown();
		/* initAccountDropDown(); */
		initWindow();
		initEvent();
		initValidator();
	};
	var initFixedAssetDropDown = function(){
		$("#fixedAssetDropDown").jqxDropDownButton({width: "98%", height: 25});
		var grid = $("#fixedAssetGrid");
		var datafields = [{name: 'fixedAssetId', type: 'string'},
		                  {name: 'fixedAssetName', type: 'string'},
		                  {name: 'fullName', type: 'string'},
		                  {name: 'costGlAccountId', type: 'string'},
		                  {name: 'purchaseCost', type: 'number'},
		                  {name: 'uomId', type: 'string'},
		                  ];
		
		var columns = [{text: uiLabelMap.BACCFixedAssetIdShort, datafield: 'fixedAssetId', width: '14%'},
		               {text: uiLabelMap.BACCFixedAssetName, datafield: 'fixedAssetName', width: '27%'},
		               {text: uiLabelMap.OrganizationUsed, datafield: 'fullName', width: '18%'},
		              /* {text: uiLabelMap.BACCPurCostAcc, datafield: 'costGlAccountId', width: '16%'}, */
		               {text: uiLabelMap.BACCPurchaseCost, dataField: 'purchaseCost', columntype: 'numberinput', filtertype: 'number',
							cellsrenderer: function(row, columns, value){
		                		var data = grid.jqxGrid('getrowdata',row);
		                		return '<span>'+ formatcurrency(value, data.uomId) + '</span>';
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
	        url: 'JqxGetListAssetsNotIncrease',
	        source: {
	        	pagesize: 5
	        }
      	};
      	Grid.initGrid(config, datafields, columns, null, grid);
	};
	
	var initAccountDropDown = function(){
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
		Grid.initGrid(configGrid1, datafields, columns, null, $("#debitAccGrid"));
		Grid.initGrid(configGrid2, datafields, columns, null, $("#creditAccGrid"));
	};
	
	var initWindow = function(){
		accutils.createJqxWindow($("#newFixedAssetWindow"), 450, 150);
	};
	var initEvent = function(){
/*		$("#creditAccGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#creditAccGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountId + '</div>';
			$("#creditAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#creditAccDropDown").attr("data-value", rowData.glAccountId);
			$("#creditAccDropDown").jqxDropDownButton('close');
		}); */
		
/*		$("#debitAccGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#debitAccGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountId + '</div>';
			$("#debitAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#debitAccDropDown").attr("data-value", rowData.glAccountId);
			$("#debitAccDropDown").jqxDropDownButton('close');
		});   */

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
			/* var rowData = args.row;
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.costGlAccountId + '</div>';
			$("#debitAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#debitAccDropDown").attr("data-value", rowData.costGlAccountId); */
		});
		
		$("#newFixedAssetWindow").on('close', function(event){
			resetData();
		});
		$("#cancelAddFixedAsset").click(function(){
			$("#newFixedAssetWindow").jqxWindow('close');
		});
		$("#saveAddFixedAsset").click(function(){
			var valid = $("#newFixedAssetWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addFixedAsset();
			$("#newFixedAssetWindow").jqxWindow('close');
		});
		$("#saveAndContinueAddFixedAsset").click(function(){
			var valid = $("#newFixedAssetWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addFixedAsset();
			resetData();
		});
	};
	
	var initValidator = function(){
		$("#newFixedAssetWindow").jqxValidator({
			rules: [
			/*	{ input: '#debitAccDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
				       if(input.val()){
				    	   return true;
				       }
				       return false;
					}
				},  */
				{ input: '#fixedAssetDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(input.val()){
							return true;
						}
						return false;
					}
				},
				/*	{ input: '#creditAccDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(input.val()){
							return true;
						}
						return false;
					}
				},  */
			]
		});
	};
	
	var addFixedAsset = function(){
		var data = {};
		data.fixedAssetId = $("#fixedAssetDropDown").attr('data-value');
/*		data.debitGlAccount = $("#debitAccDropDown").attr('data-value');
		data.creditGlAccount = $("#creditAccDropDown").attr('data-value'); */
		var fixedAssetIndex = $("#fixedAssetGrid").jqxGrid('getselectedrowindex');
		var fixedAssetData = $("#fixedAssetGrid").jqxGrid('getrowdata', fixedAssetIndex);
		data.groupName = fixedAssetData.fullName;
		data.fixedAssetName = fixedAssetData.fixedAssetName;
		data.purchaseCost = fixedAssetData.purchaseCost;
		data.uomId = fixedAssetData.uomId;
		var checkRowExists = $("#fixedAssetItemGrid").jqxGrid('getrowboundindexbyid', data.fixedAssetId);
		if(checkRowExists > -1){
			$('#fixedAssetItemGrid').jqxGrid('updaterow', data.fixedAssetId, data);
		}else{
			$("#fixedAssetItemGrid").jqxGrid('addrow', null, data, 'first');
		}
	};
	
	var resetData = function(){
		$("#fixedAssetGrid").jqxGrid('clearselection');
/*		$("#debitAccGrid").jqxGrid('clearselection');
		$("#creditAccGrid").jqxGrid('clearselection');  */
		
		$('#fixedAssetGrid').jqxGrid('clearfilters');
/*		$('#debitAccGrid').jqxGrid('clearfilters');
		$('#creditAccGrid').jqxGrid('clearfilters'); */
		
		$('#fixedAssetGrid').jqxGrid('gotopage', 0);
/*		$('#debitAccGrid').jqxGrid('gotopage', 0);
		$('#creditAccGrid').jqxGrid('gotopage', 0); */
		
		$("#fixedAssetDropDown").jqxDropDownButton('setContent', "");
/*		$("#debitAccDropDown").jqxDropDownButton('setContent', "");
		$("#creditAccDropDown").jqxDropDownButton('setContent', ""); */
	};
	return{
		init: init,
	}
}());

$(document).ready(function(){
	fixedAssetIncreaseNewStep2.init();
	newFixedAssetItemObj.init();
});