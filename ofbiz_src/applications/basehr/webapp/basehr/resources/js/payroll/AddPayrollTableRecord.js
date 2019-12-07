var addPayrollTableRecordObj = (function(){
	var _updateData = false;
	var init = function(){
		initGrid();
		initSimpleInput();
		initJqxWindow();
		initEvent();
		initJqxValidator();
		create_spinner($("#spinnerCreateNew"));
	};
	var initGrid = function(){
		var datafield = [{name: 'isSelected', type: 'bool'},
		                 {name: 'timekeepingSummaryId', type: 'string'},
		                 {name: 'timekeepingSummaryName', type: 'string'}
		                 ];
		var columns = [{datafield: 'timekeepingSummaryId', hidden: true},
		               { text: '', datafield: 'isSelected', columntype: 'checkbox', width: 50, editable: true,
							cellbeginedit: function (row, datafield, columntype) {
								var rows = $("#timekeepingSummaryGrid").jqxGrid('getrows');
								var length = rows.length;
								for(var i = 0; i < length; i++){
									$("#timekeepingSummaryGrid").jqxGrid('setcellvalue', i, "isSelected", false);
								}
							}
		               },
		               {text: uiLabelMap.TimekeepingSummaryName, datafield: 'timekeepingSummaryName', align: 'center', editable: false}
		               ];
		var grid = $("#timekeepingSummaryGrid");
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "timekeepingSummaryGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h6>" + uiLabelMap.CalcByTimekeepingSum + "</h6><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
		};
		
		var config = {
				url: '',
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '99%',
				virtualmode: false,
				editable: true,
				selectionmode: 'singlecell',
				localization: getLocalization(),
				pageable: false,
				theme: 'energyblue',
				source: {
					localdata: []
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initSimpleInput = function(){
		var nowDate = new Date();
    	$("#payrollTableNameAdd").jqxInput({width: '96%', height: 19, theme: 'olbius'});
		$("#monthNew").jqxNumberInput({ width: "80%", height: '25px', spinButtons: true, decimalDigits: 0, inputMode: 'simple', min: 1, max: 12});
		$("#yearNew").jqxNumberInput({ width: "100%", height: '25px', spinButtons: true, decimalDigits: 0, inputMode: 'simple', min: 1});
		$("#monthNew").val(nowDate.getMonth() + 1);
		$("#yearNew").val(nowDate.getFullYear());
		$("#isCalsBaseOnTimekeeping").jqxCheckBox({width: '98%', height: 25});
    };
    var updateTimekeepingSummaryGrid = function(month, year){
    	if(_updateData){
    		$("#timekeepingSummaryGrid").jqxGrid('showloadelement');
    		$("#timekeepingSummaryGrid").jqxGrid({disabled: true});
    		var localdata = [];
    		$.ajax({
    			url: 'getTimekeepingSummaryByMonthYear',
    			data: {month: month - 1, year: year},
    			type: 'POST',
    			success: function(response){
    				if(response.responseMessage == "success"){
    					localdata = response.listReturn;
    				}
    			},
    			complete: function(jqXHR, textStatus){
    				$("#timekeepingSummaryGrid").jqxGrid('hideloadelement');
    				$("#timekeepingSummaryGrid").jqxGrid({disabled: false});
    				updateTimekeepingSummaryGridLocalData(localdata);
    			}
    		});
    	}
	};
	var updateTimekeepingSummaryGridLocalData = function(localdata){
		$("#timekeepingSummaryGrid").jqxGrid('clearSelection');
		var source = $("#timekeepingSummaryGrid").jqxGrid('source');
		source._source.localdata = localdata;
		$("#timekeepingSummaryGrid").jqxGrid('source', source);
	};
	var updatePayrollTableName = function(month, year){
		if(typeof(month) != 'undefined' && typeof(year) != 'undefined'){
			var text = uiLabelMap.PayrollTable + " " + uiLabelMap.HRCommonMonthLowercase + " " + month + "/" + year; 
			$("#payrollTableNameAdd").val(text);
		}
	};
	var initJqxWindow = function(){
		createJqxWindow($("#createPayrollTableRecord"), 550, 380);
	};
	var initEvent = function(){
		$("#monthNew").on('valueChanged', function(event){
			var month = event.args.value;
			var year = $("#yearNew").val();
			updatePayrollTableName(month, year);
			updateTimekeepingSummaryGrid(month, year);
		});
		$("#yearNew").on('valueChanged', function(event){
			var month = $("#monthNew").val();
			var year = event.args.value;
			updatePayrollTableName(month, year);
			updateTimekeepingSummaryGrid(month, year);
		});
		$("#createPayrollTableRecord").on('open', function(event){
			_updateData = false;
			var nowDate = new Date();
			var month = nowDate.getMonth();
			var year = nowDate.getFullYear();
			$("#monthNew").val(month + 1);
			$("#yearNew").val(year);
			_updateData = true;
			updateTimekeepingSummaryGrid(month + 1, year);
			updatePayrollTableName(month + 1, year);
			$("#isCalsBaseOnTimekeeping").jqxCheckBox({checked: true});
		});
		$("#createPayrollTableRecord").on('close', function(event){
			_updateData = false;
		});
		$("#alterCancel").click(function(event){
			$("#createPayrollTableRecord").jqxWindow('close');
		});
		$("#alterSave").click(function(event){
			var valid = $("#createPayrollTableRecord").jqxValidator('validate');
			if(!valid){
				return;
			}
			var rows = $("#timekeepingSummaryGrid").jqxGrid('getrows');
			if(rows.length == 0){
				var month = $("#monthNew").val();
				var year = $("#yearNew").val();
				var message = uiLabelMap.TimekeepingSummary + " " + uiLabelMap.HRCommonInLowercase 
								+ " " +  uiLabelMap.HRCommonMonthLowercase + " " + month + "/" + year + " " + uiLabelMap.HRCommonNotCreated;
				bootbox.dialog(message,
						[
						{
			    		    "label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",
			    		}]		
					);
				return;
			}
			var timekeepingSummaryId = "";
			for(var i = 0; i < rows.length; i++){
				var selected = rows[i].isSelected;
				if(selected){
					timekeepingSummaryId = rows[i].timekeepingSummaryId;
					break;
				}
			}
			if(timekeepingSummaryId.length == 0){
				bootbox.dialog(uiLabelMap.TimekeepingSummaryIsNotSelected,
						[
						{
			    		    "label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",
			    		}]		
					);
				return;
			}
			bootbox.dialog(uiLabelMap.ConfirmCreatePayrollTable,
				[{
					"label" : uiLabelMap.CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok open-sans",
	    		    "callback": function() {
	    		 		createNewPayrollTableRecord(timekeepingSummaryId);   	
	    		    }	
				},
				{
	    		    "label" : uiLabelMap.CommonClose,
	    		    "class" : "btn-danger btn-small icon-remove open-sans",
	    		    "callback": function() {
	    		    	
	    		    }
	    		}]		
			);
		});
	};
	var createNewPayrollTableRecord = function(timekeepingSummaryId){
		var data = {};
		data.timekeepingSummaryId = timekeepingSummaryId;
		$("#loadingCreateNew").show();
		$("#alterCancel").attr("disabled", "disabled");
		$("#alterSave").attr("disabled", "disabled");
		data.columnValues = $("#payrollTableNameAdd").val() + "#;" + ($("#monthNew").val() - 1) + "#;" + $("#yearNew").val() + "#;" + timekeepingSummaryId;
		data.columnList = "payrollTableName;month(java.lang.Long);year(java.lang.Long);timekeepingSummaryId";
		$.ajax({
    		url: "jqxGeneralServicer?jqaction=C&sname=createPayrollTableRecord",
    		data: data,
    		type: 'POST',
    		success: function(response){
    			if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.results.successMessage, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
    				$("#createPayrollTableRecord").jqxWindow('close');
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
    			$("#loadingCreateNew").hide();
    			$("#alterCancel").removeAttr("disabled");
    			$("#alterSave").removeAttr("disabled");
    		}
    	});
	};
	var initJqxValidator = function(){
		$("#createPayrollTableRecord").jqxValidator({
			rules: [
		    {
				input: '#payrollTableNameAdd',
				message: uiLabelMap.FieldRequired,
				action: 'blur',
				rule: function (input, commit) {
					if(!input.val()){
						return false;
					}
					return true;
				}
			},
		    {
		    	input: '#monthNew',
		    	message: uiLabelMap.FieldRequired,
		    	action: 'blur',
		    	rule: function (input, commit) {
		    		if(!input.val()){
		    			return false;
		    		}
		    		return true;
		    	}
		    },
		    {
		    	input: '#yearNew',
		    	message: uiLabelMap.FieldRequired,
		    	action: 'blur',
		    	rule: function (input, commit) {
		    		if(!input.val()){
		    			return false;
		    		}
		    		return true;
		    	}
		    },
		    ],
		 });
	};
	return{
		init: init
	};
}());

$(document).ready(function() {
	addPayrollTableRecordObj.init();
});