var viewTempPayrollTableListObject = (function(){
	var init = function(){
		initJqxTree();
		initJqxNotify();
		initContextMenu();
		initJqxDropDownList();
		initJqxWindow();
		initJqxRadioButton();
		initBtnEvent();
		initJqxValidator();
		initJqxInput();
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#updatePaySalItemWindow"), 340, 175);
		$("#updatePaySalItemWindow").on('open', function(event){
			$("#overrideData").jqxRadioButton({checked:true});
		});
		createJqxWindow($("#configPayrollFormulaWindow"), 415, 480);
		createJqxWindow($("#popupAddRow"), 475, 260);
		$("#popupAddRow").on('close', function (event) { 
	    	$("#popupAddRow").jqxValidator('hide');
	    	clearDropDownContent($("#jqxTree"), $("#dropDownButton"));
	    	Grid.clearForm($(this));
	    }); 
		
	};
	
	var initJqxRadioButton = function(){
		$("#overrideData").jqxRadioButton({ width: 250, height: 25, theme: 'olbius'});
		$("#notOverride").jqxRadioButton({ width: 250, height: 25, theme: 'olbius'});
	};
	
	var initJqxTree = function(){
		var config = {dropDownBtnWidth: 240, treeWidth: 240};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#dropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
		 
		$('#jqxTree').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTree').jqxTree('getItem', event.args.element);
		  	setDropdownContent(event.args.element, $("#jqxTree"), $("#dropDownButton"));	        	        
		});
		
		var dataFormula = globalObject.getDataFormula();
		var checkedFormula = globalObject.getCheckedFormula();
		var expandItemList = globalObject.getExpandItemList();
	    createJqxTree($('#treeFormula'), dataFormula, 380, 370, true, true, 'energyblue');
	    
	    for(var i = 0; i < checkedFormula.length; i++){
			$("#treeFormula").jqxTree('checkItem', $("#"+ checkedFormula[i])[0], true);	
	    }
	    for(var i = 0; i < expandItemList.length; i++){
	    	$("#treeFormula").jqxTree('expandItem', $("#" + expandItemList[i])[0]);
	    }	    	
	};
	
	var initJqxNotify = function(){
		$("#jqxNotify").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, 
			autoClose: true, template: "info", appendContainer: "#jqxNotifyContainer"});
	};
	
	var initContextMenu = function(){
		var liElement2 = $("#contextMenu2>ul>li").length;
		var contextMenuHeight2 = 30 * liElement2; 
		$("#contextMenu2").jqxMenu({ width: 250, height: contextMenuHeight2, autoOpenPopup: false, mode: 'popup' , theme:'olbius'});
		$("#contextMenu2").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var payrollTableId = dataRecord.payrollTableId;
            if($(args).attr("action") == 'calculatePayrollTable'){
            	submitForm(rowindex);
            }else if($(args).attr("action") == 'updateToPayrollSalaryItem'){
            	updateToPayrollSalaryItem(rowindex);
            }
		});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(yearCustomTimePeriod, $('#yearCustomTime'), "customTimePeriodId", "periodName", 25, "100%");
		createJqxDropDownList([], $('#monthCustomTime'), "customTimePeriodId", "periodName", 25, "100%");
		initJqxDropdownlistEvent();
		if(typeof(globalVar.selectYearCustomTimePeriodId) != "undefined"){
			$("#yearCustomTime").jqxDropDownList('selectItem', globalVar.selectYearCustomTimePeriodId);
		}else{
			$("#yearCustomTime").jqxDropDownList('selectIndex', 0 );
		}
		
	};
	
	var createJqxTree = function (treeEle, data, width, height, hasThreeStates, checkboxes, theme){
		var source =
        {
            datatype: "json",
            datafields: [
                { name: 'id' },
                { name: 'parentId' },
                { name: 'text' },
                { name: 'value' }
            ],
            id: 'id',
            localdata: data
        };
		var dataAdapter = new $.jqx.dataAdapter(source);
		dataAdapter.dataBind();
		var records = dataAdapter.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'text', map: 'label'}]);
		treeEle.jqxTree({ source: records, width: width,height: height, hasThreeStates: hasThreeStates, checkboxes: checkboxes, theme: theme});
	};
	
	var sendAjaxRequest = function(url, data, notifyEle, ntfContent, gridEle){
		gridEle.jqxGrid({'disabled': true});
		gridEle.jqxGrid('showloadelement');
		notifyEle.jqxNotification('closeLast');
		$.ajax({
   			url: url,
   			data: data,
   			type: 'POST',
   			success: function(data){
   				if(data._EVENT_MESSAGE_){
   					ntfContent.html(data._EVENT_MESSAGE_);
    				notifyEle.jqxNotification({template: 'info'})
    				notifyEle.jqxNotification("open");
   				}else{
   					ntfContent.html(data._ERROR_MESSAGE_);
    				notifyEle.jqxNotification({template: 'error'})
    				notifyEle.jqxNotification("open");
   				}
   			},
   			complete: function(){
   				gridEle.jqxGrid({'disabled': false});	
   				gridEle.jqxGrid('hideloadelement');
   			}
   		});
	};
	
	var initJqxDropdownlistEvent = function(){
		$("#yearCustomTime").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var value = item.value;
				$.ajax({
					url: "getCustomTimePeriodByParent",
					data: {parentPeriodId: value},
					type: 'POST',
					success: function(data){
						if(data.listCustomTimePeriod){
							var listCustomTimePeriod = data.listCustomTimePeriod;
							var selectItem = listCustomTimePeriod.filter(function(item, index, array){
								var nowTimestamp = globalVar.startDate;
								if(item.fromDate <= nowTimestamp && item.thruDate >= nowTimestamp){
									return item;
								}
							});
							updateSourceDropdownlist($("#monthCustomTime"), listCustomTimePeriod);
							if(selectItem.length > 0){
								$("#monthCustomTime").jqxDropDownList('selectItem', selectItem[0].customTimePeriodId);
							}else{
								$("#monthCustomTime").jqxDropDownList({selectedIndex: 0 });
							}
						}
					},
					complete: function(jqXHR, textStatus){
						
					}
				});
			}
		});
	};
	var initBtnEvent = function (){
		$("#updatePaySalItemCancel").click(function(event){
			$("#updatePaySalItemWindow").jqxWindow('close');
		});
		
		$("#alterCancel").click(function(){
			$("#popupAddRow").jqxWindow('close');
		});
		
		$("#updatePaySalItemSave").click(function(event){
			$("#jqxgrid").jqxGrid('showloadelement');
			$("#jqxgrid").jqxGrid({disabled: true});
			var rowIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var data = $("#jqxgrid").jqxGrid('getrowdata', rowIndex);
			var overrideData = "";
			if($("#overrideData").jqxRadioButton('checked')){
				overrideData = "Y";
			}
			if($("#notOverride").jqxRadioButton('checked')){
				overrideData = "N";
			}
			$.ajax({
				url: 'updatePaySalaryItemFromPayrollTable',
				data: {payrollTableId: data.payrollTableId, overrideData: overrideData},
				type: 'POST',
				success: function(response){
					$("#jqxNotify").jqxNotification('closeLast');
					if(response._EVENT_MESSAGE_){
						$("#ntfContent").html(response._EVENT_MESSAGE_);
						$("#jqxNotify").jqxNotification({template: 'info'});
	    				$("#jqxNotify").jqxNotification("open");
					}else{
						$("#ntfContent").html(response._ERROR_MESSAGE_);
						$("#jqxNotify").jqxNotification({template: 'error'});
	    				$("#jqxNotify").jqxNotification("open");
					}
				},
				complete: function(jqXHR, textStatus){
		    		$("#jqxgrid").jqxGrid('hideloadelement');
		    		$("#jqxgrid").jqxGrid({ disabled: false});
		    	}
			});
			$("#updatePaySalItemWindow").jqxWindow('close');
		});
		
		$("#updateCalcFormula").click(function(){
	    	$("#updateCalcFormula").attr('disabled','disabled');
	    	var items = $('#treeFormula').jqxTree('getCheckedItems');
	    	var codeSelected = new Array();
	    	for(var i = 0; i < items.length; i++){
	    		codeSelected.push({"code": items[i].value});
	    	}
	    	$.ajax({
	    		url: "updateFormulaIncludedPayrollTable",
	    		type: "POST",
	    		data: {codeSelected: JSON.stringify(codeSelected)},
	    		success: function(data){
	    			
	    		},
	    		complete: function(jqXHR, status){
	    			$("#configPayrollFormulaWindow").jqxWindow('close');
	    			$("#updateCalcFormula").removeAttr('disabled');
	    		}
	    	});
	    });
		
		$("#configPayrollFormula").click(function(){
	    	openJqxWindow($("#configPayrollFormulaWindow"));
	    });
		
		$("#alterSave").click(function () {
			if(!$('#popupAddRow').jqxValidator('validate')){
				return;
			}
			bootbox.dialog(uiLabelMap.ConfirmCreatePayrollTable,
				[{
					"label" : uiLabelMap.CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok open-sans",
	    		    "callback": function() {
	    		 		createNewPayrollTableRecord();   	
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
	
	var initJqxValidator = function(){
		$("#popupAddRow").jqxValidator({
			rules: [
		    {
				input: "#dropDownButton", message: uiLabelMap.FieldRequired, 
				action: 'blur',
			    rule: function (input, commit) {
					var items = $("#jqxTree").jqxTree('getSelectedItem');
					if(!items){
						return false;
					}
					return true;
			   }
		    },	        
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
		    	input: '#monthCustomTime',
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
		    	input: '#yearCustomTime',
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
	
	var submitForm = function(row){
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		var statusId = data.statusId;
		var payrollTableId = data.payrollTableId;
		if(statusId == 'PYRLL_TABLE_CREATED'){
			calculatePayrollRecord(payrollTableId);
			$('#jqxgrid').jqxGrid({ disabled: true});
		}else if(statusId == 'PYRLL_TABLE_CALC'){
			bootbox.dialog(uiLabelMap.PayrollCalculated_Recalculated,
				[
					{
					    "label" : uiLabelMap.CommonSubmit,
					    "class" : "icon-ok btn btn-small btn-primary",
					    "callback": function() {
					    	calculatePayrollRecord(payrollTableId);	
					    }
					},
					{
						  "label" : uiLabelMap.CommonCancel,
			    		   "class" : "btn-danger icon-remove btn-small",
			    		   "callback": function() {
			    		   
			    		   }
					}
				]		
			);
		}
	};
	
	var updateToPayrollSalaryItem = function(rowindex){
		var data = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
		var statusId = data.statusId;
		if(statusId == 'PYRLL_TABLE_CREATED'){
			bootbox.dialog(uiLabelMap.PayrollTableNotCalculated,
				[					
					{
						  "label" : uiLabelMap.CommonClose,
			    		   "class" : "btn-danger icon-remove btn-small",
			    		   "callback": function() {
			    		   
			    		   }
					}
				]		
			);
		}else{
			openJqxWindow($("#updatePaySalItemWindow"));
		}
	};
	
	var calculatePayrollRecord = function(payrollTableId){
		$('#jqxgrid').jqxGrid({ disabled: true});
		$('#jqxgrid').jqxGrid('showloadelement');
		$.ajax({
			url: 'calcPayroll',
			data: {payrollTableId: payrollTableId},
			type: 'POST',
			success: function(data){
				if(data._EVENT_MESSAGE_){
					window.location.href = 'viewPayrollTable?payrollTableId=' + payrollTableId;
				}else{
					$("#jqxNotify").jqxNotification('closeLast');
					$("#ntfContent").html(data._ERROR_MESSAGE_);
					$("#jqxNotify").jqxNotification({template: 'error'});
    				$("#jqxNotify").jqxNotification("open");
				}
			},
			complete: function(jqXHR, textStatus){
				$('#jqxgrid').jqxGrid({ disabled: false});
				$('#jqxgrid').jqxGrid('hideloadelement');
			}
		});
	};
	
	var createNewPayrollTableRecord = function (){
		var departmentSelected = $("#jqxTree").jqxTree('val');		
		var departmentId = departmentSelected.value
    	var row = { 
    		payrollTableName: $("input[name='payrollTableNameAdd']").val(),
    		partyId: departmentId,
    		customTimePeriodId: $("#monthCustomTime").val()
    	};
	    $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#popupAddRow").jqxWindow('close');	
	};
	
	var initJqxInput = function(){
    	$("#payrollTableNameAdd").jqxInput({width: 236, height: 19, theme: 'olbius'});
    };
	
	return{
		init: init
	}
}()); 

$(document).ready(function() {
	viewTempPayrollTableListObject.init();
});
	
