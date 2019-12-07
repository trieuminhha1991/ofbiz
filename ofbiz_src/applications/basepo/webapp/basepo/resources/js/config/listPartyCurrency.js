$(document).ready(function() {
	ObjParCur.init();
});
var ObjParCur = (function() {
	var grid = $("#jqxGridPartyCurrency");
	var gridParty = $("#jqxGridListParty"); 
	var popupAdd = $("#AddPartyCurrency"); 
	var validatorVAL = null;
	var init = function() { 
		initInput();
		initElementComplex(); 
		initEvents();
		initValidateForm();

	};
	
	var initInput = function() { 
		$("#fromDate").jqxDateTimeInput({width: '300px', height: '25px', theme: theme});
		$("#thruDate").jqxDateTimeInput({width: '300px', height: '25px', theme: theme});
		$("#fromDate").jqxDateTimeInput("clear");
		$("#thruDate").jqxDateTimeInput("clear");
		
		$("#contextMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
		popupAdd.jqxWindow({
			maxWidth: 800, minWidth: 300, width: 540, height: 320, minHeight: 100, maxHeight: 656, resizable: false, isModal: true, modalZIndex: 10000, zIndex: 10000, autoOpen: false, cancelButton: $("#addCancel"), modalOpacity: 0.7, theme:theme           
		});
		$("#party").jqxDropDownButton({width: 300, theme: theme}); 
		$('#party').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		var config = {filterable: true, checkboxes: true, displayMember: 'description', valueMember: 'currencyUomId', 
				width: 300, height: 25, searchMode: 'contains', theme: theme, placeHolder: uiLabelMap.PleaseSelectTitle};
		createJqxDropDownListExt($("#currencyUomId"), currencyUomData, config);
		
	}
	
	var initElementComplex = function() {
		initGridPartyCurrency(grid);
		initGridParty(gridParty);
	}
	
	var initGridParty = function(grid){
		var datafield =  [
		                  { name: 'partyId', type: 'string'},
		                  { name: 'partyCode', type: 'string'},
		                  { name: 'groupName', type: 'string'},
		                  ];
		var columnlist = [
		                  { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
		                	  groupable: false, draggable: false, resizable: false,
		                	  datafield: '', columntype: 'number', width: 50,
		                	  cellsrenderer: function (row, column, value) {
		                		  return '<div style="cursor: pointer;">' + (value + 1) + '</div>';
		                	  }
		                  },
		                  { text: uiLabelMap.CommonPartyId, datafield: 'partyCode', align: 'left', width: 200, pinned: true, editable: false,
		                	  cellsrenderer: function (row, column, value) {
		                		  var data = grid.jqxGrid('getrowdata', row);
		                		  if (!value){
		                			  value = data.partyId
		                			  return '<div style="cursor: pointer;">' + value + '</div>';
		                		  }
		                		  return '<div style="cursor: pointer;">' + value + '</div>';
		                	  }
		                  },
		                  { text: uiLabelMap.CommonPartyName, datafield: 'groupName', align: 'left', minwidth: 150, pinned: true, editable: false,
		                	  cellsrenderer: function (row, column, value) {
		                		  return '<div style="cursor: pointer;">' + value + '</div>';
		                	  }
		                  },
		                  ];
		var config = {
				width: '100%', 
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
				url: 'jqGetListPartySupplier',                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initGridPartyCurrency = function (grid) {
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.CommonPartyId, dataField: 'partyCode', width: '18%', editable: false, pinned: true,
			},
			{ text: uiLabelMap.CommonPartyName, dataField: 'groupName', minwidth: 100, editable:false,},
			{ text: uiLabelMap.CurrencyUomId, dataField: 'currencyUomId', width: '10%', editable:false,},
			{ text: uiLabelMap.BPFromDate, dataField: 'fromDate', editable: false, align: 'left', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			},
			{ text: uiLabelMap.BPThruDate, dataField: 'thruDate', editable: false, align: 'left', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			},
        ];
		
		var datafield = [
         	{ name: 'partyId', type: 'string'},
         	{ name: 'partyCode', type: 'string'},
         	{ name: 'currencyUomId', type: 'string'},
         	{ name: 'description', type: 'string'},
			{ name: 'groupName', type: 'string'},
			{ name: 'fromDate', type: 'date', other: 'Timestamp'},
		 	{ name: 'thruDate', type: 'date', other: 'Timestamp'},
		 	]
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "PartyCurrency";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.PartyCurrency + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        var customcontrol1 = "icon-plus open-sans@" + uiLabelMap.AddNew + "@javascript:void(0)@ObjParCur.openPopupAdd()";
	        Grid.createCustomControlButton(grid, container, customcontrol1);
		}; 
		
		var config = {
			width: '100%', 
	   		virtualmode: true,
	   		showtoolbar: true,
	   		rendertoolbar: rendertoolbar,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: false,
	        rowsheight: 26,
	        rowdetails: false,
	        useUrl: true,
	        url: 'jqGetPartyCurrencyConfig',                
	        source: {pagesize: 15}
	  	};
	  	Grid.initGrid(config, datafield, columns, null, grid);
		Grid.createContextMenu(grid, $("#contextMenu"), false);
	}
	var initEvents = function() {
		
		$("#contextMenu").on('itemclick', function (event) {
			var data = grid.jqxGrid('getRowData', grid.jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			facilitySelectedId = data.facilityId;
			if(tmpStr == uiLabelMap.AddNew){
				openPopupAdd(data.partyId, data.currencyUomId);
			}
			if(tmpStr == uiLabelMap.Edit){
				openPopupEdit(data.partyId, data.currencyUomId);
			}
			if(tmpStr == uiLabelMap.BPThruDateNow){
				thruDateRelation(data.partyId, data.currencyUomId);
			}
			if(tmpStr == uiLabelMap.BSRefresh){
				grid.jqxGrid('updatebounddata');
			}
		});
		
		 $("#currencyUomId").on('checkChange', function (event) {
			 listCurrencySelected = [];
             if (event.args) {
                 var item = event.args.item;
                 if (item) {
                     var items = $("#currencyUomId").jqxDropDownList('getCheckedItems');
                     $.each(items, function (index) {
                    	 var item = this;
                    	 listCurrencySelected.push({
                    		 currencyUomId: this.value,
                    		 description: this.label
                    	 })
                     });
                 }
             }
         });
		 popupAdd.on('close', function (event) {
			 listCurrencySelected = [];
			 listPartySelected = [];
			 gridParty.jqxGrid('clearSelection');
			 $("#currencyUomId").jqxDropDownList('clearSelection'); 
			 $("#currencyUomId").jqxDropDownList('uncheckAll'); 
			 $('#party').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
			 validatorVAL.hide();
			 $('#thruDate').jqxDateTimeInput('clear');
			 $('#fromDate').jqxDateTimeInput('clear');
		 });
		 
		 gridParty.on('rowclick', function (event) {
		        var args = event.args;
		        var rowBoundIndex = args.rowindex;
		        var rowData = gridParty.jqxGrid('getrowdata', rowBoundIndex);
		        listPartySelected = [];
		        listPartySelected.push(rowData);
		        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ rowData.partyCode + ' - ' + rowData.groupName +'</div>';
		        $('#party').jqxDropDownButton('setContent', dropDownContent);
		        gridParty.jqxGrid('selectrow', rowBoundIndex);
		        $("#party").jqxDropDownButton('close');
		    });
		 
		 $("#addSave").on('click', function (event) {
			var resultValidate = validatorVAL.validate();
			if(!resultValidate) return false;
			bootbox.dialog(uiLabelMap.AreYouSureCreate, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				    "callback": function() {bootbox.hideAll();}
				}, 
				{"label": uiLabelMap.OK,
				    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
				    "callback": function() {
				    	Loading.show('loadingMacro');
				    	setTimeout(function(){
		    				var listParties = [];
		    				for (var i = 0; i < listPartySelected.length; i ++){
				    			var row = {
				    					partyId: listPartySelected[i].partyId,
				    			}
				    			listParties.push(row);
				    		}
				    		var listCurrencyUoms = [];
				    		for (var i = 0; i < listCurrencySelected.length; i ++){
				    			var row = {
			    					currencyUomId: listCurrencySelected[i].currencyUomId,
				    			}
				    			listCurrencyUoms.push(row);
				    		}
				    		listCurrencyUoms = JSON.stringify(listCurrencyUoms);
				    		listParties = JSON.stringify(listParties);
				    		var thruDate = $('#thruDate').jqxDateTimeInput('getDate');
							var fromDate = $('#fromDate').jqxDateTimeInput('getDate');
							var data = {
				    			listParties: listParties,
				    			listCurrencyUoms: listCurrencyUoms,
				    			fromDate: fromDate.getTime(),
				    		};
							if (thruDate){
								data.thruDate = thruDate.getTime();
							}
							$.ajax({
					    		url: "createMultiPartyCurrency",
					    		type: "POST",
					    		async: false,
					    		data: data,
					    		success: function (res){
					    			$("#AddPartyCurrency").jqxWindow('close');
					    			grid.jqxGrid('updatebounddata');
					    		}
					    	});
						Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);
		 });
	}
	
	var thruDateRelation = function (partyId, currencyUomId){
		var d = new Date();
		var data = {
				partyId: partyId,
				currencyUomId: currencyUomId,
    			thruDate: d.getTime(),
		};
		bootbox.dialog(uiLabelMap.AreYouSureSave, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		    "callback": function() {bootbox.hideAll();}
		}, 
		{"label": uiLabelMap.OK,
		    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		    "callback": function() {
		    	Loading.show('loadingMacro');
		    	setTimeout(function(){
					$.ajax({
			    		url: "updatePartyCurrency",
			    		type: "POST",
			    		async: false,
			    		data: data,
			    		success: function (res){
			    			grid.jqxGrid('updatebounddata');
			    		}
			    	});
					Loading.hide('loadingMacro');
		    	}, 500);
		    }
		}]);
	}
	
	var initValidateForm = function(){
		var extendRules = [
			{
				input: '#party', 
			    message: uiLabelMap.FieldRequired, 
			    action: 'blur', 
			    position: 'right',
			    rule: function (input) {
			    	if (listPartySelected.length <= 0){
			    		return false;
			    	}
				   	return true;
			    }
			},
			{
				input: '#currencyUomId', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					if (listCurrencySelected.length <= 0){
						return false;
					}
					return true;
				}
			},
			{
				input: '#thruDate', 
				message: uiLabelMap.CannotBeforeNow, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					var thruDate = $('#thruDate').jqxDateTimeInput('getDate');
	     		   	var nowDate = new Date();
				   	if ((typeof(thruDate) != 'undefined' && thruDate != null && !(/^\s*$/.test(thruDate)))) {
			 		    if (thruDate < nowDate) {
			 		    	return false;
			 		    }
				   	}
	     		   	return true;
				}
			},
			{
				input: '#thruDate', 
				message: uiLabelMap.MustAfterEffectiveDate, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					var thruDate = $('#thruDate').jqxDateTimeInput('getDate');
					var fromDate = $('#fromDate').jqxDateTimeInput('getDate');
					if ((typeof(thruDate) != 'undefined' && thruDate != null && !(/^\s*$/.test(thruDate)))) {
						if (thruDate < fromDate) {
							return false;
						}
					}
					return true;
				}
			},
		];
   		var mapRules = [
			{input: '#fromDate', type: 'validInputNotNull'},
        ];
   		validatorVAL = new OlbValidator($("#AddPartyCurrency"), mapRules, extendRules, {position: 'right'});
	};
	
	var getValidator = function(){
    	return validatorVAL;
    };
    
    function createJqxDropDownListExt(elemenDiv, sourceArr, config){
    	var source = {
    			localdata: sourceArr,
    	        datatype: "array"	
    	};
    	var dataAdapter = new $.jqx.dataAdapter(source);
    	
    	config.source = dataAdapter;
    	elemenDiv.jqxDropDownList(config);
    	if(typeof renderer != "undefined"){
    		elemenDiv.jqxDropDownList({renderer: renderer});
    	};
    	
    	if(sourceArr.length < 8){
    		elemenDiv.jqxDropDownList({autoDropDownHeight: true});
    	}
    }
    
    var openPopupAdd = function (){
    	popupAdd.jqxWindow('open');
    }
    
	return {
		init : init,
		openPopupAdd: openPopupAdd,
	}
}());