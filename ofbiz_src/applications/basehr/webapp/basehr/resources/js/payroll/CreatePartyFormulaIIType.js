var createPartyFormulaIITypeObject = (function(){
	var init = function(){
			initBtnEvent();
			initJqxNotification();
			initJqxDropDownList();
			initJqxDateTime();
			initJqxWindow();
			initJqxValidator();
			initJqxTreeDropDownBtn();
	};
	var initBtnEvent = function(){
		$("#addNewBtn").click(function(event){
			$("#popupAddRow").jqxWindow('open');
		});
		$("#cancelBtn").click(function(){
			$("#popupAddRow").jqxWindow('close');
		});
		$("#alterSave").click(function () {
			if(!$('#popupAddRow').jqxValidator('validate')){
				return;
			}
			bootbox.dialog(uiLabelMap.AddNewRowConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
		    		    "class" : "btn-primary btn-small icon-ok open-sans",
		    		    "callback": function() {
		    		    	createPartyPayrollFormulaInvoiceItemType();
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
		
		$("#deleteBtn").click(function(event){
			var selectionArr = $("#jqxDataTable").jqxDataTable('getSelection');
			if(selectionArr.length > 0){
				bootbox.dialog(uiLabelMap.NotifyDelete,
					[{
						"label" : uiLabelMap.CommonSubmit,
		    		    "class" : "btn-primary btn-small icon-ok open-sans",
		    		    "callback": function() {
		    		 		deletePartyFormulaInvoiceItemType(selectionArr[0]);   	
		    		    }	
					},
					{
		    		    "label" : uiLabelMap.CommonClose,
		    		    "class" : "btn-danger btn-small icon-remove open-sans",
		    		    "callback": function() {
		    		    	
		    		    }
		    		}]		
				);
				
			}
		});
	};

	var createPartyPayrollFormulaInvoiceItemType = function(){
		var fromDate = $('#fromDateJQ').jqxDateTimeInput('val', 'date').getTime();
		var i = $('#invoiceItemTypeIdJQ').jqxDropDownList('getSelectedItem');
		var j = $('#codeadd').jqxDropDownList('getSelectedItem');
		var invoiceItemTypeId = i ? i.value : "";
		var code = j.value;
		var partyIdChoose = $("#jqxTree").jqxTree('getSelectedItem');
		var partyIdSubmitArr = new Array();
		partyIdSubmitArr.push({partyId: partyIdChoose.value});
		
		var row = { 
	   		invoiceItemTypeId: invoiceItemTypeId,
	   		partyListId: JSON.stringify(partyIdSubmitArr),
	   		fromDate: fromDate,
	   		code: code,
	   	  };
		$("#jqxDataTable").jqxDataTable('addRow', null, row, "first");
	    // select the first row and clear the selection.
	    $("#jqxDataTable").jqxDataTable('clearSelection');                        
	    //$("#jqxDataTable").jqxDataTable('selectRow', 0);  
	    $("#popupAddRow").jqxWindow('close');
	};

	var deletePartyFormulaInvoiceItemType = function (rowData){
		var rowId = rowData.uid;
		var dataSubmit = {};
		var fromDate = rowData.fromDate.getTime();
		dataSubmit.partyId = rowData.partyId;
		dataSubmit.code = rowData.code;
		dataSubmit.invoiceItemTypeId = rowData.invoiceItemTypeId;
		dataSubmit.fromDate = fromDate;
		$.ajax({
			url: 'deletePartyFormulaInvoiceItemTypeJQ',
			data: dataSubmit,
			type: 'POST',
			async: false,
			success: function(response){
				$("#jqxNtf").jqxNotification('closeLast');            			
				if(response.responseMessage == 'success'){            				
					$("#jqxNtfContent").text(response.successMessage);
					$("#jqxNtf").jqxNotification({template: 'info'});
					$("#jqxNtf").jqxNotification("open");
					$("#jqxDataTable").jqxDataTable('updateBoundData');            			
				}else{
					$("#jqxNtfContent").text(response.errorMessage);
					$("#jqxNtf").jqxNotification({template: 'error'});
					$("#jqxNtf").jqxNotification("open");
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				commit(false);
			},
			complete: function(jqXHR, textStatus){
				$('#jqxDataTable').jqxDataTable({disabled:false});            			
				//$('#jqxDataTable').jqxDataTable('updateBoundData');
			}
		});
	};

	var initJqxNotification = function(){
		$("#jqxNtf").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#notifyContainer"});
	};

	var initJqxValidator = function(){
		$("#popupAddRow").jqxValidator({
		   	rules: [
		   	   {
	               input: "#codeadd", 
	               message: uiLabelMap.FieldRequired, 
	               action: 'blur', 
	               rule: function (input, commit) {
	                   var index = $("#codeadd").jqxDropDownList('getSelectedIndex');
	                   return index != -1;
	               }
	           }, 
	           {
	               input: "#invoiceItemTypeIdJQ", 
	               message: uiLabelMap.FieldRequired, 
	               action: 'blur', 
	               rule: function (input, commit) {
	                   var index = $("#invoiceItemTypeIdJQ").jqxDropDownList('getSelectedIndex');
	                   return index != -1;
	               }
	           },
	           {
	        	   input: "#fromDateJQ", 
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
	               input: "#jqxDropDownButton", 
	               message: uiLabelMap.FieldRequired, 
	               action: 'blur', 
	               rule: function (input, commit) {
	                   var val = $("#jqxTree").jqxTree('getSelectedItem');
	                   if(!val){
	                   	return false;
	                   }
	                   return true; 
	               }
	           }
	           ]
		 });
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(invoiceItems, $('#invoiceItemTypeIdJQ'), "invoiceItemTypeId", "description", 25, '98%');
		createJqxDropDownList(codes, $('#codeadd'), "code", "name", 25, '98%');
	};
	
	var initJqxDateTime = function(){
		$("#fromDateJQ").jqxDateTimeInput({height: '24px', width: '98%', theme: 'olbius'});
	};
	
	var initJqxWindow = function(){
		$("#popupAddRow").jqxWindow({
	        width: 470, height: 260, resizable: true, isModal: true, autoOpen: false, theme: 'olbius'         
	    });
		$("#popupAddRow").on('close', function (event) { 
			$("#popupAddRow").jqxValidator('hide');
			clearDropDownContent($("#jqxTree"), $("#jqxDropDownButton"));
			Grid.clearForm($(this));
	    }); 
	};
	
	var initJqxTreeDropDownBtn = function(){
		var config = {dropDownBtnWidth: 250, treeWidth: 250};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#jqxDropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
		 
		$('#jqxTree').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTree').jqxTree('getItem', event.args.element);
		  	setDropdownContent(event.args.element, $("#jqxTree"), $("#jqxDropDownButton"));	        	        
		});
	};
	return{
		init: init
	}
}());

$(document).ready(function(){
	createPartyFormulaIITypeObject.init();
});	
		
