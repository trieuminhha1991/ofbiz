var addEmplSalObj = (function(){
	var _windowWidth = 450;
	var _partyChooseData = {};
	var init = function(){
		initJqxGridSearchEmpl();
		initSimpleInput();
		initDropDown();
		initDropDownGrid();
		initJqxWindow();
		initEvent();
		initJqxValidator();
		create_spinner($("#spinnerAdd"));
	};
	var initSimpleInput = function(){
		$("#fromDateDaily").jqxDateTimeInput({width: '98%', height: 25});
		$("#thruDateDaily").jqxDateTimeInput({width: '98%', height: 25, showFooter: true});
		$("#yearFromMonthly").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
		$("#yearThruMonthly").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
		$("#yearFromYearly").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
		$("#yearThruYearly").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
		$("#amountSalAdd").jqxNumberInput({ width: '98%', height: 25, spinButtons: true, decimalDigits: 0});
	};
	var initDropDown = function(){
		createJqxDropDownList(globalVar.periodTypeArr, $("#periodTypeSalAdd"), "periodTypeId", "description", 25, "98%");
		var monthData = [];
		for(var i = 0; i < 12; i++){
			monthData.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
		}
		createJqxDropDownList(monthData, $("#monthFromMonthly"), "month", "description", 25, 90);
		monthData.unshift({month: -1, description: '--------'});
		createJqxDropDownList(monthData, $("#monthThruMonthly"), "month", "description", 25, 90);
	};
	var initDropDownGrid = function(){
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'partyCode', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'emplPositionType', type: 'string'}];
		var columns = [{text: uiLabelMap.EmployeeId, datafield : 'partyCode', width : '23%', editable: false},
		               {text: uiLabelMap.EmployeeName, datafield: 'fullName', width: '30%', editable: false},
		               {text: uiLabelMap.HrCommonPosition, datafield: 'emplPositionType', width: '47%', editable: false}];
		var grid = $("#jqxGridGroupEmpl");
   		var rendertoolbar = function (toolbar){
   			toolbar.html("");
   			var id = "jqxGridGroupEmpl";
   			var me = this;
   			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.EmployeeListSelected + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
   			toolbar.append(jqxheader);
   	     	var container = $('#toolbarButtonContainer' + id);
   	        var maincontainer = $("#toolbarcontainer" + id);
	   	    var str = '<button style="margin-left: 20px;" id="deleterowbutton'+id+'"><i class="icon-trash open-sans"></i><span>'+ uiLabelMap.wgdelete + '</span></button>';
	        container.append(str);
	        var obj = $("#deleterowbutton" + id);
	        obj.jqxButton();
	        obj.click(function(){
	        	var selectedrowindexes = grid.jqxGrid('getselectedrowindexes');
	        	var rowIDs = [];
	        	for(var i = 0; i < selectedrowindexes.length; i++){
	        		var rowid = grid.jqxGrid('getrowid', selectedrowindexes[i]);
	        		rowIDs.push(rowid);
	        	}
	        	grid.jqxGrid('deleterow', rowIDs);
	        	var source = $("#jqxGridGroupEmpl").jqxGrid('source');
	    		var records = source.records;
	    		source._source.localdata = records;
	    		setContentDropDownBtn(records.length + " " + uiLabelMap.EmployeeSelected);
	    		grid.jqxGrid('clearselection');
	        });
   		};               
		var config = {
		   		width: 500, 
		   		rowsheight: 25,
		   		autoheight: true,
		   		virtualmode: false,
		   		showfilterrow: false,
		   		selectionmode: 'multiplerows',
		   		pageable: true,
		   		sortable: false,
		        filterable: false,
		        editable: false,
	   			showtoolbar: true,
	   			rendertoolbar : rendertoolbar,
	        	source: {pagesize: 5, id: 'partyId', localdata: []}
		 };
		Grid.initGrid(config, datafield, columns, null, grid);
		$("#dropDownButtonGroupEmpl").jqxDropDownButton({width: _windowWidth * 9/16, height: 25});
	};
	var initJqxSplitter = function(){
		$("#splitterEmplList").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
	};
	var initJqxWindow = function(){
		createJqxWindow($("#CreateSalaryBaseFlatWindow"), _windowWidth, 300);
		createJqxWindow($('#popupWindowEmplList'), 900, 560, initJqxSplitter);
	};
	var initEvent = function(){
		$("#periodTypeSalAdd").on('select', function(event){
			var args = event.args;
			if(args){
				var periodTypeId = args.item.value;
				if(periodTypeId == "DAILY"){
					$("#CreateSalaryBaseFlatWindow .periodTypeDaily").show();
					$("#CreateSalaryBaseFlatWindow .periodTypeMonthly").hide();
					$("#CreateSalaryBaseFlatWindow .periodTypeYearly").hide();
				}else if(periodTypeId == "MONTHLY"){
					$("#CreateSalaryBaseFlatWindow .periodTypeDaily").hide();
					$("#CreateSalaryBaseFlatWindow .periodTypeMonthly").show();
					$("#CreateSalaryBaseFlatWindow .periodTypeYearly").hide();
				}else if(periodTypeId == "YEARLY"){
					$("#CreateSalaryBaseFlatWindow .periodTypeDaily").hide();
					$("#CreateSalaryBaseFlatWindow .periodTypeMonthly").hide();
					$("#CreateSalaryBaseFlatWindow .periodTypeYearly").show();
				}
			}
		});
		$("#monthThruMonthly").on('select', function(event){
			var args = event.args;
			if(args){
		    	var item = args.item;
		    	var month = item.value;
		    	if(month < 0){
		    		$("#yearThruMonthly").jqxNumberInput({disabled: true});
		    	}else{
		    		$("#yearThruMonthly").jqxNumberInput({disabled: false});
		    	}
		    }
		});
		$("#CreateSalaryBaseFlatWindow").on('open', function(event){
			initData();
		});
		$("#CreateSalaryBaseFlatWindow").on('close', function(event){
			clearData();
		});
		$("#chooseEmplBtn").click(function(event){
			openJqxWindow($('#popupWindowEmplList'));
		});
		$('#popupWindowEmplList').on('open', function(event){
			$("#CreateSalaryBaseFlatWindow").jqxValidator('hide');
			if(typeof(globalVar.expandTreeId) != 'undefined'){
				$("#jqxTreeEmplList").jqxTree('expandItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
				$('#jqxTreeEmplList').jqxTree('selectItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
			}
		});
		$('#popupWindowEmplList').on('close', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
			_partyChooseData = {};
		});
		$("#cancelAdd").click(function(event){
			$("#CreateSalaryBaseFlatWindow").jqxWindow('close');
		});
		$("#saveAdd").click(function(event){
			var valid = $("#CreateSalaryBaseFlatWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateSalaryForEmplConfirm,
				[
				 {
					 "label" : uiLabelMap.CommonSubmit,
					 "class" : "btn-primary btn-small icon-ok open-sans",
					 "callback": function() {
						 createEmplSal(true);	    		
					 }
				 },
				 {
					 "label" : uiLabelMap.CommonCancel,
					 "class" : "btn-danger icon-remove btn-small open-sans",
				 }
				 ]
			);
			
		});
		$("#saveAndContinueAdd").click(function(event){
			var valid = $("#CreateSalaryBaseFlatWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateSalaryForEmplConfirm,
					[
					 {
						 "label" : uiLabelMap.CommonSubmit,
						 "class" : "btn-primary btn-small icon-ok open-sans",
						 "callback": function() {
							 createEmplSal(false);	    		
						 }
					 },
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger icon-remove btn-small open-sans",
					 }
					 ]
			);
			
		});
		$("#saveChooseEmpl").click(function(event){
			var localdata = [];
			for(var partyId in _partyChooseData){
				if(partyId){
					localdata.push(_partyChooseData[partyId]);
				}
			}
			addPartyToEmplGroup(localdata);
			$('#popupWindowEmplList').jqxWindow('close');
		});
		$("#cancelChooseEmpl").click(function(event){
			$('#popupWindowEmplList').jqxWindow('close');
		});
		$("#EmplListInOrg").on('rowselect', function (event){
			var args = event.args;
			var rowData = args.row;
			if(rowData){
				_partyChooseData[rowData.partyId] = rowData;
			}else{
				var datainformation = $('#EmplListInOrg').jqxGrid('getdatainformation');
				var paginginformation = datainformation.paginginformation;
				var pagenum = paginginformation.pagenum;
				var pagesize = paginginformation.pagesize;
				var start = pagenum * pagesize;
				var end = start + pagesize;
				for(var rowIndex = start; rowIndex < end; rowIndex++){
					var data = $('#EmplListInOrg').jqxGrid('getrowdata', rowIndex);
					if(data){
						_partyChooseData[data.partyId] = data;
					}
				}
			}
		});
		$("#EmplListInOrg").on('rowunselect', function (event){
			var args = event.args;
			var rowData = args.row;
			if(rowData){
				delete _partyChooseData[rowData.partyId];
			}else{
				var datainformation = $('#EmplListInOrg').jqxGrid('getdatainformation');
				var paginginformation = datainformation.paginginformation;
				var pagenum = paginginformation.pagenum;
				var pagesize = paginginformation.pagesize;
				var start = pagenum * pagesize;
				var end = start + pagesize;
				for(var rowIndex = start; rowIndex < end; rowIndex++){
					var data = $('#EmplListInOrg').jqxGrid('getrowdata', rowIndex);
					if(data){
						delete _partyChooseData[data.partyId];
					}
				}
			}
		});
		$("#EmplListInOrg").on("bindingcomplete", function (event) {
			var datainformation = $('#EmplListInOrg').jqxGrid('getdatainformation');
			var paginginformation = datainformation.paginginformation;
			var pagenum = paginginformation.pagenum;
			var pagesize = paginginformation.pagesize;
			var start = pagenum * pagesize;
			var end = start + pagesize;
			for(var rowIndex = start; rowIndex < end; rowIndex++){
				var data = $('#EmplListInOrg').jqxGrid('getrowdata', rowIndex);
				if(data){
					var partyId = data.partyId;
					if(partyId && _partyChooseData.hasOwnProperty(partyId)){
						$("#EmplListInOrg").jqxGrid('selectrow', rowIndex);
					}else{
						$("#EmplListInOrg").jqxGrid('unselectrow', rowIndex);
					}
				}
			}
		}); 
	};
	var addPartyToEmplGroup = function(partyArr){
		var source = $("#jqxGridGroupEmpl").jqxGrid('source');
		var localdata = source._source.localdata;
		for(var i = 0; i < partyArr.length; i++){
			var partyId = partyArr[i].partyId;
			var partyIdExists = $("#jqxGridGroupEmpl").jqxGrid('getrowdatabyid', partyId);
			if(!partyIdExists){
				localdata.push(partyArr[i]);
			}
		}
		source._source.localdata = localdata;
		$("#jqxGridGroupEmpl").jqxGrid('source', source);
		setContentDropDownBtn(localdata.length + " " + uiLabelMap.EmployeeSelected);
	};
	var createEmplSal = function(isCloseWindow){
		var data = getData();
		$("#loadingAdd").show();
		$("#cancelAdd").attr("disabled", 'disabled');
		$("#saveAndContinueAdd").attr("disabled", 'disabled');
		$("#saveAdd").attr("disabled", 'disabled');
		$.ajax({
			url: 'createPartySalaryBase',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose : true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					var periodTypeId = $("#periodTypeSalAdd").val();
					$("#jqxgrid").jqxGrid('updatebounddata');
					if(isCloseWindow){
						$("#CreateSalaryBaseFlatWindow").jqxWindow('close');
					}else{
						clearData();
						initData();
					}
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
			complete: function(jqXHR, textStatus){
				$("#loadingAdd").hide();
				$("#cancelAdd").removeAttr("disabled");
				$("#saveAndContinueAdd").removeAttr("disabled");
				$("#saveAdd").removeAttr("disabled");
			}
		});
	};
	var getData = function(){
		var data = {};
		var partyIdArr = [];
		var rowsParty = $("#jqxGridGroupEmpl").jqxGrid('getrows');
		for(var i = 0; i < rowsParty.length; i++){
			partyIdArr.push(rowsParty[i].partyId);
		}
		data.partyIds = JSON.stringify(partyIdArr);
		data.amount = $("#amountSalAdd").val();
		var periodTypeId = $("#periodTypeSalAdd").val();
		data.periodTypeId = periodTypeId;
		var fromDate = null, thruDate = null;
		if(periodTypeId == "DAILY"){
			fromDate = $("#fromDateDaily").jqxDateTimeInput('val', 'date');
			thruDate = $("#thruDateDaily").jqxDateTimeInput('val', 'date');
		}else if(periodTypeId == "MONTHLY"){
			var monthFrom = $("#monthFromMonthly").val();
			var yearFrom = $("#yearFromMonthly").val();
			fromDate = new Date(yearFrom, monthFrom, 1);
			var monthTo = $("#monthThruMonthly").val();
			if(monthTo > -1){
				var yearTo = $("#yearThruMonthly").val();
				thruDate = new Date(yearTo, parseInt(monthTo) + 1, 0);
			}
		}else if(periodTypeId == "YEARLY"){
			var yearFrom = $("#yearFromYearly").val();
			var yearThru = $("#yearThruYearly").val();
			fromDate = new Date(yearFrom, 0, 1);
			thruDate = new Date(yearTo, 11, 31);
		}
		data.fromDate = fromDate.getTime();
		if(thruDate){
			data.thruDate = thruDate.getTime();
		}
		return data;
	};
	var initJqxGridSearchEmpl = function(){
		createJqxGridSearchEmpl($("#EmplListInOrg"), {uiLabelMap: uiLabelMap, selectionmode: 'checkbox', sourceId: "partyId"});
	};
	var initJqxValidator = function(){
		$("#CreateSalaryBaseFlatWindow").jqxValidator({
			rules: [
				{ input: '#chooseEmplBtn', message: uiLabelMap.NoPartyChoose, action: 'none',
					rule : function(input, commit){
		        		var records = $("#jqxGridGroupEmpl").jqxGrid('source').records;
		        		if(records.length <= 0){
		        			return false;
		        		}
		        		return true;
		        	}
				},    
				{ input: '#fromDateDaily', message: uiLabelMap.FieldRequired, action: 'none',
					rule: function (input, commit){
						var periodTypeId = $("#periodTypeSalAdd").val();
						if(periodTypeId == "DAILY" && !input.val()){
							return false; 
						}
						return true;
					}
				},  
				{ input: '#monthFromMonthly', message: uiLabelMap.FieldRequired, action: 'none',
					rule: function (input, commit){
						var periodTypeId = $("#periodTypeSalAdd").val();
						if(periodTypeId == "MONTHLY" && !input.val()){
							return false; 
						}
						return true;
					}
				},  
				{ input: '#yearFromYearly', message: uiLabelMap.FieldRequired, action: 'none',
					rule: function (input, commit){
						var periodTypeId = $("#periodTypeSalAdd").val();
						if(periodTypeId == "YEARLY" && !input.val()){
							return false; 
						}
						return true;
					}
				},  
				
				{ input: '#thruDateDaily' , message: uiLabelMap.ThruDateMustBeAfterFromDate, action: 'none',
					rule: function (input, commit){
						var fromDate = $('#fromDateDaily').jqxDateTimeInput('val', 'date');
						var thruDate = $(input).jqxDateTimeInput('val', 'date');
						var periodTypeId = $("#periodTypeSalAdd").val();
						if(periodTypeId == "DAILY" && fromDate && thruDate && thruDate < fromDate){
							return false;
						}
						return true;
					}
				},  
				{ input: '#yearThruMonthly' , message: uiLabelMap.ThruDateMustBeAfterFromDate, action: 'none',
					rule: function (input, commit){
						var periodTypeId = $("#periodTypeSalAdd").val();
						if(periodTypeId == "MONTHLY"){
							var monthTo = $('#monthThruMonthly').val();
							if(monthTo > -1){
			        			var monthFrom = $('#monthFromMonthly').val();
			        			var yearFrom = $('#yearFromMonthly').val();
			        			var yearTo = $('#yearThruMonthly').val();
			        			var dateFrom = new Date();
			        			var dateTo = new Date();
			        			dateFrom.setDate(1);
			        			dateFrom.setMonth(monthFrom);
			        			dateFrom.setFullYear(yearFrom);
			        			dateTo.setDate(2);
			        			dateTo.setMonth(monthTo);
			        			dateTo.setFullYear(yearTo);
			        			if(dateFrom > dateTo){
			        				return false;
			        			}
			        		}
						}
						return true;
					}
				}, 
				{ input: '#yearThruYearly' , message: uiLabelMap.ThruDateMustBeAfterFromDate, action: 'none',
					rule: function (input, commit){
						var periodTypeId = $("#periodTypeSalAdd").val();
						if(periodTypeId == "YEARLY"){
							var yearFrom = $("#yearFromYearly").val();
							var yearTo = $("#yearThruYearly").val();
							if(yearFrom > yearTo){
								return false;
							}
						}
						return true;
					}
				},
			]
		});
	};
	var clearData = function(){
		Grid.clearForm($("#CreateSalaryBaseFlatWindow"));
		$("#CreateSalaryBaseFlatWindow .periodTypeDaily").hide();
		$("#CreateSalaryBaseFlatWindow .periodTypeMonthly").hide();
		$("#CreateSalaryBaseFlatWindow .periodTypeYearly").hide();
		var source = $("#jqxGridGroupEmpl").jqxGrid("source");
		source._source.localdata = [];
		$("#jqxGridGroupEmpl").jqxGrid("source", source);
		//Grid.clearForm($(this));
		_partyChooseData = {};
		$("#periodTypeSalAdd").jqxDropDownList('clearSelection');
	};
	var initData = function(){
		var date = new Date();
		var monthStart = new Date(date.getFullYear(), date.getMonth(), 1);
		var monthEnd = new Date(date.getFullYear(), parseInt(date.getMonth()) + 1, 0);
		$("#monthThruMonthly").jqxDropDownList({selectedIndex: 0});
		$("#monthFromMonthly").jqxDropDownList({selectedIndex: date.getMonth()});
		$("#yearFromMonthly").val(date.getFullYear());
		$("#yearFromYearly").val(date.getFullYear());
		$("#yearThruMonthly").val(date.getFullYear());
		$("#yearThruYearly").val(date.getFullYear());
		$("#fromDateDaily").val(monthStart);
		$("#thruDateDaily").val(null);
		$("#periodTypeSalAdd").val("MONTHLY");
	};
	var setContentDropDownBtn = function(content){
		var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + content + '</div>';
		$("#dropDownButtonGroupEmpl").jqxDropDownButton("setContent", dropDownContent);
	};
	return{
		init: init
	}
}());
$(document).ready(function(){
	addEmplSalObj.init();
});
