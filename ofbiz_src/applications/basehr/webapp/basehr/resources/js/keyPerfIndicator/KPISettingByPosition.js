var emplPositionTypeKPIListObj = (function(){
	var isLoadData = true;
	var gridListKPIHeader = "gridListKPIHeader"; 
	var gridListChooseKPIHeader = "gridListChooseKPIHeader";
	var gridListKPIHeaderContent = "";
	var init = function(){
		initJqxGrid();
		initJqxGridEvent();
	};
	
	var loadData = function(){
		$("#gridListKPI" + globalVar.setKPIByPosWindow).jqxGrid('clearselection');
		if(isLoadData){
			var emplPositionTypeId = $("#emplPositionType" + globalVar.setKPIByPosWindow).val();
			if(typeof(emplPositionTypeId) != 'undefined' && emplPositionTypeId.length > 0){
				isLoadData = false;
				var grid = $("#gridListKPI" + globalVar.setKPIByPosWindow);
				refreshBeforeReloadGrid(grid);
				var tmpS = grid.jqxGrid('source');
				var param = "&statusId=KPI_ACTIVE&emplPositionTypeId=" + emplPositionTypeId;
				tmpS._source.url = 'jqxGeneralServicer?sname=GetListEmplPosCriType' + param;
				grid.jqxGrid('source', tmpS);
				var gridKpiChooseS = $("#gridKPIChoose" + globalVar.setKPIByPosWindow).jqxGrid('source');
				gridKpiChooseS._source.localdata = [];
				$("#gridKPIChoose" + globalVar.setKPIByPosWindow).jqxGrid('source', gridKpiChooseS);
			}
		}
	}; 
	
	var resetData = function(){
		refreshBeforeReloadGrid($("#gridListKPI" + globalVar.setKPIByPosWindow));
		var gridKpiChooseS = $("#gridKPIChoose" + globalVar.setKPIByPosWindow).jqxGrid('source');
		gridKpiChooseS._source.localdata = [];
		$("#gridKPIChoose" + globalVar.setKPIByPosWindow).jqxGrid('source', gridKpiChooseS);
		if($("#selectAllKPICheckbox").length > 0){
			$("#selectAllKPICheckbox").jqxCheckBox({checked: false});
		}
	};
	
	var initJqxGrid = function(){
		var datafield = [
       		{name : 'emplPositionTypeId', type : 'string'},
       		{name : 'criteriaId', type : 'string'},
       		{name : 'criteriaName', type : 'string'},
       		{name : 'perfCriteriaTypeId', type : 'string'},
       		{name : 'periodTypeId', type : 'string'},
    		{name : 'target', type : 'number'},
    		{name : 'uomId', type : 'string'},
    		{name : 'statusId', type : 'string'},
    		{name : 'weight', type : 'number'},
    	];
		
		var columns = [
		               {datafield: 'emplPositionTypeId', hidden: true, editable: false},	
		               {text : uiLabelMap.HRCommonKPIName, datafield : 'criteriaName', width : '20%', editable: false},
		               {text : uiLabelMap.HRCommonFields, width : '20%', dataField : 'perfCriteriaTypeId', columntype: 'dropdownlist',
		            	   editable: false,
		            	   cellsrenderer: function (row, column, value) {
			       				for(var i = 0; i < globalVar.perfCriteriaTypeArr.length; i++){
			       					if(value == globalVar.perfCriteriaTypeArr[i].perfCriteriaTypeId){
			       						return '<span>' + globalVar.perfCriteriaTypeArr[i].description + '</span>'; 
			       					}
			       				}
			       				return '<span>' + value + '</span>';
			       			},
			       			createEditor: function (row, cellvalue, editor, cellText, width, height) {
	                		   createJqxDropDownList(globalVar.perfCriteriaTypeArr, editor, "perfCriteriaTypeId", "description", height, width);
							},
							initEditor: function (row, cellvalue, editor, celltext, width, height) {
								editor.val(cellvalue);
							},
			       		},
			       		{text: uiLabelMap.KPIWeigth, datafield: 'weight', width: '12%', cellsalign: 'right', 
		            	   columntype: 'numberinput', filtertype: 'number', editable: true,
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   if(typeof(value) == 'number'){
								   return '<span class="align-right">' + value * 100 + '%<span>'; 
							   }
							   return '<span class="align-right">' + value + '<span>';
						   },
						   createEditor: function (row, cellvalue, editor, cellText, width, height) {
								editor.jqxNumberInput({width: width, height: height,  spinButtons: true, min: 0, max: 1, decimalDigits: 0, 
									digits: 3, symbol: '%', symbolPosition: 'right'});
							},
							initEditor: function (row, cellvalue, editor, celltext, width, height) {
								if(typeof(cellvalue) == 'number'){
									editor.val(cellvalue * 100);
								}
							},
							geteditorvalue: function (row, cellvalue, editor) {
								return editor.val() / 100;
							}
		                },
			       		{text : uiLabelMap.HRFrequency, width : '18%', datafield : 'periodTypeId', columntype: 'dropdownlist',
			       			cellsrenderer: function (row, column, value) {
			    				for(var i = 0; i < globalVar.periodTypeArr.length; i++){
			    					if(value == globalVar.periodTypeArr[i].periodTypeId){
			    						return '<span>' + globalVar.periodTypeArr[i].description + '</span>'; 
			    					}
			    				}
			    				return '<span>' + value + '</span>';
			    			},
			    			createEditor: function (row, cellvalue, editor, cellText, width, height) {
	                		   createJqxDropDownList(globalVar.periodTypeArr, editor, "periodTypeId", "description", height, width);
							},
							initEditor: function (row, cellvalue, editor, celltext, width, height) {
								editor.val(cellvalue);
							},
			    			
			    		},
			    		{text : uiLabelMap.HRTarget, width : '18%', datafield : 'target',  columntype: 'numberinput', cellsalign: 'right',
			    		    cellsrenderer: function (row, column, value) {
			    				var retVal = value;
			    				if(value){
			    					retVal = formatNumber(value, 2,  " ");
			    				}
			    				return '<span style="text-align: right">' + retVal + '<span>';
			    			},
			    			createEditor: function (row, cellvalue, editor, cellText, width, height) {
								editor.jqxNumberInput({width: width, height: height,  spinButtons: true, min: 0, decimalDigits: 2, 
									digits: 12, max: 999999999999});
							},
			    		},
			    		{text : uiLabelMap.HRCommonUnit , datafield : 'uomId', columntype: 'dropdownlist', 
			    		    cellsrenderer: function (row, column, value) {
			    				for(var i = 0; i < globalVar.uomArr.length; i++){
			    					if(value == globalVar.uomArr[i].uomId){
			    						return '<span>' + globalVar.uomArr[i].abbreviation + '</span>'; 
			    					}
			    				}
			    				return '<span>' + value + '</span>';
			    			},
			    			createEditor: function (row, cellvalue, editor, cellText, width, height) {
	                		   createJqxDropDownList(globalVar.uomArr, editor, "uomId", "abbreviation", height, width);
							},
							initEditor: function (row, cellvalue, editor, celltext, width, height) {
								editor.val(cellvalue);
							},
			    		},
			    		{datafield: 'statusId', hidden: true}
		];
		var grid = $("#gridListKPI" + globalVar.setKPIByPosWindow);
		
		var customControlAdvance = "<div id='selectAllKPICheckbox' class='grid-action-button pull-right' style='margin-top: 8px'><b style='font-size: 14px;'>" + uiLabelMap.filterselectallstring + "</b></div>";
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "gridListKPI" + globalVar.setKPIByPosWindow;
			var me = this;
	        var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5 id='" + gridListKPIHeader + "'>" + gridListKPIHeaderContent + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
	     	toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        grid.on('loadCustomControlAdvance', function(event){
	        	initJqxCheckBox();
	        });
	        Grid.triggerToolbarEvent(grid, container, customControlAdvance);
		};
		var config = {
		   		width: '100%', 
		   		height: 220,
		   		rowsheight: 25,
		   		autoheight: false,
		   		virtualmode: true,
		   		showfilterrow: true,
		   		rendertoolbar: rendertoolbar,
		   		selectionmode: 'singlerow',
		   		pageable: true,
		   		sortable: false,
		        filterable: false,
		        editable: false,
		        url: '',    
	   			showtoolbar: true,
	        	source: {pagesize: 5, id: 'criteriaId'}
		 };
		Grid.initGrid(config, datafield, columns, null, grid);
		
		config.virtualmode = false;
		config.editable = true;
		config.editmode = 'dblclick';
		grid = $("#gridKPIChoose" + globalVar.setKPIByPosWindow);
		rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "gridKPIChoose" + globalVar.setKPIByPosWindow;
			var me = this;
	        var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5 id='" + gridListChooseKPIHeader + "'>" + uiLabelMap.KPIChoosen + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
	     	toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        var str = '<button style="margin-left: 20px;" id="deleterowbutton'+id+'"><i class="icon-trash open-sans"></i><span>'+ uiLabelMap.wgdelete +'</span></button>';
	        container.append(str);
            var obj = $("#deleterowbutton" + id);
            obj.jqxButton();
            obj.on('click', function () { 
            	 var selectedrowindexes = grid.jqxGrid('getselectedrowindexes');
            	 var rows = [];
            	 for(x in selectedrowindexes){
					var selectedrowindex = selectedrowindexes[x];
					var rowid = grid.jqxGrid('getrowid', selectedrowindex);
					rows.push(rowid);
            	 }
            	 grid.jqxGrid('deleterow', rows);
            });
		};
		config.rendertoolbar = rendertoolbar;
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var validate = function(){
		var records = $("#gridListKPI" + globalVar.setKPIByPosWindow).jqxGrid('getrows');
		if(records.length <= 0){
			var emplPosTypeSelect = $("#emplPositionType" + globalVar.setKPIByPosWindow).jqxDropDownList('getSelectedItem');
			bootbox.dialog(uiLabelMap.HrCommonPosition + " " + emplPosTypeSelect.label + " " + uiLabelMap.PositionNotSettingKPI,
					[{
		    		    "label" : uiLabelMap.CommonClose,
		    		    "class" : "btn-danger btn-small icon-remove open-sans",
		    		}]	
			);
			return false;
		}
		var checked = $("#selectAllKPICheckbox").jqxCheckBox('checked');
		if(!checked){
			var records = $("#gridKPIChoose" + globalVar.setKPIByPosWindow).jqxGrid('getrows');
			if(records.length <= 0){
				bootbox.dialog(uiLabelMap.KPINotSelect,
						[{
			    		    "label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",
			    		}]	
				);
				return false;
			}
		}
		return true;
	};
	
	var initJqxCheckBox = function(){
		$("#selectAllKPICheckbox").jqxCheckBox({width: 90, height: 25});
		$("#selectAllKPICheckbox").on('change', function (event){
			var checked = event.args.checked;
			$("#gridListKPI" + globalVar.setKPIByPosWindow).jqxGrid({disabled: checked});
			$("#gridKPIChoose" + globalVar.setKPIByPosWindow).jqxGrid({disabled: checked});
		});
	};
	
	var setLoadData = function(value){
		isLoadData = value
	};
	
	var setHeaderGrid = function(emplPositionTypeDesc){
		gridListKPIHeaderContent = uiLabelMap.EmplPositionTypeListKPI + " " + emplPositionTypeDesc + " <i>(" + uiLabelMap.DoubleClickToChoose + ")</i>";
		if($("#" + gridListKPIHeader).length > 0){
			$("#" + gridListKPIHeader).html(gridListKPIHeaderContent);
		}
	};
	
	var initJqxGridEvent = function(){
		var gridListKPI = $("#gridListKPI" + globalVar.setKPIByPosWindow);
		var gridKPIChoose = $("#gridKPIChoose" + globalVar.setKPIByPosWindow);
		gridListKPI.on('rowdoubleclick', function(event){
			var args = event.args;
		    var boundIndex = args.rowindex;
		    var data = gridListKPI.jqxGrid('getrowdata', boundIndex);
		    var checkData = gridKPIChoose.jqxGrid('getrowdatabyid', data.criteriaId);
		    if(!checkData){
		    	var newData = $.extend(true, {}, data);
		    	gridKPIChoose.jqxGrid('addrow', null, newData, 'first');
		    }
		});
	};
	
	var getData = function(){
		var retData = {};
		var selectAll = $("#selectAllKPICheckbox").jqxCheckBox('checked');
		if(!selectAll){
			retData.selectAllKPI = "N";
			var records = $("#gridKPIChoose" + globalVar.setKPIByPosWindow).jqxGrid('getrows'); 
			var kpiSelectedArr = [];
			for(var i = 0; i < records.length; i++){
				var dataRecord = records[i];
				kpiSelectedArr.push({criteriaId: dataRecord.criteriaId, weight: dataRecord.weight, periodTypeId: dataRecord.periodTypeId, target: dataRecord.target, uomId: dataRecord.uomId}); 
			}
			retData.listKPISelected = JSON.stringify(kpiSelectedArr);
		}else{
			retData.selectAllKPI = "Y";
		}
		return retData;
	};
	
	return{
		init: init,
		setLoadData: setLoadData,
		loadData: loadData,
		setHeaderGrid: setHeaderGrid,
		getData: getData,
		resetData: resetData,
		validate: validate
	}
}());

var partyIdListEmplPosTypeObj = (function(){
	var isLoadData = true;
	var gridPartyListContent = "";
	var gridPartyListHeader = "gridPartyListHeader";
	var init = function(){
		initJqxGrid();
		initBtnEvent();
		initJqxListBox();
	};
	
	var initJqxListBox = function(){
		var source = {
				localdata: [],
				datatype: "array"
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#listBoxParty" + globalVar.setKPIByPosWindow).jqxListBox({
			source: dataAdapter, 
			displayMember: "partyName", 
			valueMember: "partyId",  
			height: 400, 
			width: '100%', 
			theme: 'olbius', 
			multiple: true,
			itemHeight:'auto',
			renderer: function (index, label, value) {
				return label;
			}
		});
	};
	
	var initBtnEvent = function(){
		var grid = $("#gridPartyList" + globalVar.setKPIByPosWindow);
		var listBox = $("#listBoxParty" + globalVar.setKPIByPosWindow);
		$("#addParty" + globalVar.setKPIByPosWindow).click(function(event){
			var rowindexes = grid.jqxGrid('getselectedrowindexes');
			if(rowindexes.length > 0){
				for(var i = 0; i < rowindexes.length; i++){
					var dataRecord = grid.jqxGrid('getrowdata', rowindexes[i]);
					var item = listBox.jqxListBox('getItemByValue', dataRecord.partyId);
					if(!item){
						listBox.jqxListBox('addItem', {label: dataRecord.partyName, value: dataRecord.partyId});	
					}
				}
			}
		});
		$("#removeParty" + globalVar.setKPIByPosWindow).click(function(event){
			var items = listBox.jqxListBox('getSelectedItems');
			if(items.length > 0){
				for(var i = 0; i < items.length; i++){
					listBox.jqxListBox('removeItem', items[i]);	
				}
			}
			listBox.jqxListBox('clearSelection');
		});
	};
	
	var loadData = function(){
		$("#gridPartyList" + globalVar.setKPIByPosWindow).jqxGrid('clearselection');
		if(isLoadData){
			var data = settingKPIPosObj.getData();
			if(typeof(data.emplPositionTypeId) != 'undefined' && data.emplPositionTypeId.length > 0){
				isLoadData = false;
				var grid = $("#gridPartyList" + globalVar.setKPIByPosWindow);
				refreshBeforeReloadGrid(grid);
				var tmpS = grid.jqxGrid('source');
				var param = "&emplPositionTypeId=" + data.emplPositionTypeId + "&fromDate=" + data.fromDate;
				if(data.hasOwnProperty("thruDate")){
					param += "&thruDate=" + data.thruDate;
				}
				tmpS._source.url = 'jqxGeneralServicer?hasrequest=Y&sname=JQGetEmplListByPosType' + param;
				grid.jqxGrid('source', tmpS);
			}
			var source = {
					localdata: [],
					datatype: "array"
			};
			var dataAdapter = new $.jqx.dataAdapter(source);
			$("#listBoxParty" + globalVar.setKPIByPosWindow).jqxListBox({source: dataAdapter});
		}
	};
	
	var resetData = function(){
		refreshBeforeReloadGrid($("#gridPartyList" + globalVar.setKPIByPosWindow));
		var source = {
				localdata: [],
				datatype: "array"
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#listBoxParty" + globalVar.setKPIByPosWindow).jqxListBox({source: dataAdapter});
		if($("#selectAllPartyCheckbox").length > 0){
			$("#selectAllPartyCheckbox").jqxCheckBox({checked: false});
		}
	};
	
	var initJqxGrid = function(){
		var datafield = [
		       {name: 'partyId', type: 'string'},
		       {name: 'partyCode', type: 'string'},
		       {name: 'partyName', type: 'string'},
		       {name: 'department', type: 'string'}
		];
		
		var columns = [
		      {datafield: 'partyId', hidden: true},         
		      {text : uiLabelMap.EmployeeId, datafield : 'partyCode', width : '22%', editable: false}, 
		      {text : uiLabelMap.EmployeeName, datafield : 'partyName', width : '33%', editable: false}, 
		      {text : uiLabelMap.CommonDepartment, datafield : 'department', width : '45%', editable: false}, 
        ];
		var grid = $("#gridPartyList" + globalVar.setKPIByPosWindow);
		var customControlAdvance = "<div id='selectAllPartyCheckbox' class='grid-action-button pull-right' style='margin-top: 8px'><b style='font-size: 14px;'>" + uiLabelMap.filterselectallstring + "</b></div>";
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "gridPartyList" + globalVar.setKPIByPosWindow;
			var me = this;
	        var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5 id='" + gridPartyListHeader +"'>" + gridPartyListContent + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
	     	toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        grid.on('loadCustomControlAdvance', function(event){
	        	initJqxCheckBox();
	        });
	        Grid.triggerToolbarEvent(grid, container, customControlAdvance);
		};
		var config = {
		   		width: '100%', 
		   		height: 445,
		   		autoheight: false,
		   		virtualmode: true,
		   		showfilterrow: true,
		   		rendertoolbar: rendertoolbar,
		   		selectionmode: 'multiplerows',
		   		pageable: true,
		   		sortable: false,
		        filterable: false,
		        editable: false,
		        url: '',    
	   			showtoolbar: true,
	   			pagesizeoptions: [14, 20, 30, 50, 100],
	        	source: {pagesize: 14}
		 };
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var setHeaderGrid = function(emplPositionTypeDesc){
		gridPartyListContent = uiLabelMap.CommonList + " " + emplPositionTypeDesc;
		if($("#" + gridPartyListHeader).length > 0){
			$("#" + gridPartyListHeader).html(gridPartyListContent);
		}
	};
	
	var initJqxCheckBox = function(){
		$("#selectAllPartyCheckbox").jqxCheckBox({width: 90, height: 25});
		$("#selectAllPartyCheckbox").on('change', function (event){
			var checked = event.args.checked;
			$("#gridPartyList" + globalVar.setKPIByPosWindow).jqxGrid({disabled: checked});
			$("#listBoxParty" + globalVar.setKPIByPosWindow).jqxListBox({disabled: checked});
		});
	};
	
	var setLoadData = function(value){
		isLoadData = value;
	};
	
	var validate = function(){
		var records = $("#gridPartyList" + globalVar.setKPIByPosWindow).jqxGrid('getrows');
		if(records.length <= 0){
			var emplPosTypeSelect = $("#emplPositionType" + globalVar.setKPIByPosWindow).jqxDropDownList('getSelectedItem');
			var errorMsg = uiLabelMap.NotManageEmployeeHaveEmplPositionType + ": <b>" + emplPosTypeSelect.label + "</b> ";
			
			bootbox.dialog(errorMsg,
					[{
		    		    "label" : uiLabelMap.CommonClose,
		    		    "class" : "btn-danger btn-small icon-remove open-sans",
		    		}]	
			);
			return false;
		}
		var checked = $("#selectAllPartyCheckbox").jqxCheckBox('checked');
		if(!checked){
			records = $("#listBoxParty" + globalVar.setKPIByPosWindow).jqxListBox('getItems');
			if(records.length <= 0){
				bootbox.dialog(uiLabelMap.NoPartyChoose,
						[{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]	
				);
				return false;
			}
		}
		return true;
	};
	
	var getData = function(){
		var data = {};
		var selectAll = $("#selectAllPartyCheckbox").jqxCheckBox('checked');
		if(!selectAll){
			data.selectAllParty = "N";
			var partySelected = $("#listBoxParty" + globalVar.setKPIByPosWindow).jqxListBox('getItems');
			var partyArr = [];
			for(var i = 0; i < partySelected.length; i++){
				partyArr.push(partySelected[i].value);
			}
			data.partyIdSelected = JSON.stringify(partyArr);
		}else{
			data.selectAllParty = "Y";
		}
		return data;
	};
	
	return{
		init: init,
		setLoadData: setLoadData,
		loadData: loadData,
		setHeaderGrid: setHeaderGrid,
		getData: getData,
		resetData: resetData,
		validate: validate
	}
}());

var settingKPIPosObj = (function(){
	var init = function(){
		initJqxWindow();
	};
	var initWindowContent = function(){
		initJqxDateTimeInput();
		initJqxDropDownList();
		initJqxValidator();
		partyIdListEmplPosTypeObj.init();
		emplPositionTypeKPIListObj.init();
		wizardObj.init();
		create_spinner($("#spinnerAjax" + globalVar.setKPIByPosWindow));
	};
	
	var initJqxValidator = function(){
		$("#generalInfo" + globalVar.setKPIByPosWindow).jqxValidator({
			rules: [
				{input : '#emplPositionType' + globalVar.setKPIByPosWindow, message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#fromDate' + globalVar.setKPIByPosWindow, message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
                {input : '#thruDate' + globalVar.setKPIByPosWindow, message : uiLabelMap.FieldRequired, action: 'blur',
                    rule : function(input, commit){
                        if(!input.val()){
                            return false;
                        }
                        return true;
                    }
                },
				{input : '#thruDate' + globalVar.setKPIByPosWindow, message : uiLabelMap.ExpireDateMustGreaterOrEqualThanEffectiveDate, action: 'blur', 
					rule : function(input, commit){
						var thruDate = input.jqxDateTimeInput('val', 'date');
						if(!thruDate){
							return true;
						}
						var fromDate = $("#fromDate" + globalVar.setKPIByPosWindow).jqxDateTimeInput('val', 'date');
						if(thruDate < fromDate){
							return false;
						}
						return true;
					}
				},
			]
		});
	};
	
	var validate = function(){
		return $("#generalInfo" + globalVar.setKPIByPosWindow).jqxValidator('validate');
	};
	
	var initJqxDateTimeInput = function(){
		$("#fromDate" + globalVar.setKPIByPosWindow).jqxDateTimeInput({width: '98%', height: 25});
		$("#thruDate" + globalVar.setKPIByPosWindow).jqxDateTimeInput({width: '98%', height: 25, showFooter:true});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.emplPositionTypeArr, $("#emplPositionType" + globalVar.setKPIByPosWindow), "emplPositionTypeId", "description", 25, '98%');
		$("#emplPositionType" + globalVar.setKPIByPosWindow).on('select', function(event){
			partyIdListEmplPosTypeObj.setLoadData(true);
			emplPositionTypeKPIListObj.setLoadData(true);
		});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#" + globalVar.setKPIByPosWindow), 800, 600, initWindowContent);
		$("#" + globalVar.setKPIByPosWindow).on('open', function(event){
			$("#thruDate" + globalVar.setKPIByPosWindow).val(null);
			$("#fromDate" + globalVar.setKPIByPosWindow).val(new Date(globalVar.monthStart));
		});
		
		$("#" + globalVar.setKPIByPosWindow).on('close', function(event){
			partyIdListEmplPosTypeObj.setLoadData(true);
			emplPositionTypeKPIListObj.setLoadData(true);
			resetData();
			partyIdListEmplPosTypeObj.resetData();
			emplPositionTypeKPIListObj.resetData();
			wizardObj.resetStep();
		});
	};
	
	var getData = function(){
		var data = {};
		data.emplPositionTypeId = $("#emplPositionType" + globalVar.setKPIByPosWindow).val();
		data.fromDate = $("#fromDate" + globalVar.setKPIByPosWindow).jqxDateTimeInput('val', 'date').getTime();
		var thruDate = $("#thruDate" + globalVar.setKPIByPosWindow).jqxDateTimeInput('val', 'date')
		if(thruDate){
			data.thruDate = thruDate.getTime();
		}
		return data;
	};
	
	var resetData = function(){
		Grid.clearForm($("#generalInfo" + globalVar.setKPIByPosWindow));
	};
	
	var openWindow = function(){
		openJqxWindow($("#" + globalVar.setKPIByPosWindow));
	};
	var closeWindow = function(){
		$("#" + globalVar.setKPIByPosWindow).jqxWindow('close');
	};
	return{
		init: init,
		openWindow: openWindow,
		getData: getData,
		closeWindow: closeWindow,
		resetData: resetData,
		validate: validate
	}
}());

var wizardObj = (function(){
	var init = function(){
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			var emplPositionSelect = $("#emplPositionType" + globalVar.setKPIByPosWindow).jqxDropDownList('getSelectedItem');
	        if(info.step == 1 && (info.direction == "next")) {
	        	var valid = settingKPIPosObj.validate();
	        	if(!valid){
	        		return false;
	        	}
	        	emplPositionTypeKPIListObj.loadData();
	        	emplPositionTypeKPIListObj.setHeaderGrid(emplPositionSelect.label);
	        }else if(info.step == 2 && info.direction == 'next'){
	        	partyIdListEmplPosTypeObj.loadData();
	        	partyIdListEmplPosTypeObj.setHeaderGrid(emplPositionSelect.label);
	        	return emplPositionTypeKPIListObj.validate();
	        }
	    }).on('finished', function(e) {
	    	var valid = partyIdListEmplPosTypeObj.validate();
	    	if(!valid){
	    		return false;
	    	}
	    	var generalInfoData = settingKPIPosObj.getData();
	    	var partySelectData = partyIdListEmplPosTypeObj.getData();
	    	var kpiSelectedData = emplPositionTypeKPIListObj.getData();
	    	var dataSubmit = $.extend({}, generalInfoData, partySelectData, kpiSelectedData);
	    	var fromDate = new Date(generalInfoData.fromDate);
	    	var confirmMes = uiLabelMap.ConfirmSettingKPIByPositionFirst + " " + uiLabelMap.HrCommonFromLowercase + " " + getDate(fromDate) + "/" + getMonth(fromDate) + "/" + fromDate.getFullYear();
	    	if(generalInfoData.hasOwnProperty("thruDate")){
	    		var thruDate = new Date(generalInfoData.thruDate);
	    		confirmMes += " " + uiLabelMap.HRCommonToLowercase + " " +  getDate(thruDate) + "/" + getMonth(thruDate) + "/" + thruDate.getFullYear();
	    	}
	    	confirmMes += " " + uiLabelMap.ConfirmSettingKPIByPositionSecond;
	    	bootbox.dialog(confirmMes,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							settingKPIByPosType(dataSubmit);
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
	    	
	    }).on('stepclick', function(e){
	    	
	    });
	};
	
	var resetStep = function(){
		$('#fuelux-wizard').wizard('previous');
		$('#fuelux-wizard').wizard('previous');
	};
	
	var settingKPIByPosType = function(data){
		$("#ajaxLoading" + globalVar.setKPIByPosWindow).show();
    	$("#btnNext").attr("disabled", "disabled");
    	$("#btnPrev").attr("disabled", "disabled");
    	$.ajax({
    		url: 'settingKPIForEmplByPosType',
    		data: data,
    		type: 'POST',
    		success: function(response){
    			if(response.responseMessage == 'success'){
    				Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
						template : 'info', appendContainer : "#containerjqxgrid", opacity : 0.9});
    				settingKPIPosObj.closeWindow();
    				kpiAssignEmplObj.updateGrid();
    			}else{
    				bootbox.dialog(response.errorMessage,
						[{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
					);
    			}
    		},
    		complete: function(jqXHR, textStatus){
    			$("#ajaxLoading" + globalVar.setKPIByPosWindow).hide();
    	    	$("#btnNext").removeAttr("disabled");
    	    	$("#btnPrev").removeAttr("disabled");
    		}
    	});
	};
	
	return{
		init: init,
		resetStep: resetStep
	}
}());

$(document).ready(function(){
	settingKPIPosObj.init();
});