$(function(){
	OlbPosTerminalBank.init();
})

var OlbPosTerminalBank = (function(){
	var validatorVAL;
	var init = function(){
		initjqxWindow();
		initInputs();
		initDropDownButton();
		initValidator();
		bindEvent();
	}
	
	var initInputs = function(){
		$("#fromDate").jqxDateTimeInput({formatString: "dd/MM/yyyy HH:mm:ss", height: '25px', width: '97%'});
		$("#thruDate").jqxDateTimeInput({formatString: "dd/MM/yyyy HH:mm:ss", height: '25px', width: '97%'});
		$("#thruDate").val(null);
	};
	
	var initDropDownButton = function(){
		$("#posTerminalId").jqxDropDownButton({width: '97%', height: 25});
		var datafield =  [
				{name: 'posTerminalId', type: 'string'},
				{name: 'terminalName', type: 'string'},
				{name: 'productStoreId', type: 'string'},
				{name: 'storeName', type: 'string'}
    	];
		var columns = [{ text: uiLabelMap.BPOSTerminalId, dataField: 'posTerminalId', width: 120, pinned: true },
		   			   { text: uiLabelMap.BPOSTerminalName, dataField: 'terminalName', width: 300 },
					   { text: uiLabelMap.BSProductStoreId, dataField: 'productStoreId', width: 100 },
					   { text: uiLabelMap.BSStoreName, dataField: 'storeName', width: 250 }
		               ];
		var config = {
  			width: 700, 
	   		virtualmode: true,
	   		showtoolbar: false,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: false,
	        url: 'JQGetListProductStorePOS',                
	        source: {pagesize: 5}
      	};
      	Grid.initGrid(config, datafield, columns, null, $("#jqxgridPosTerminal"));
      	
      	$("#bankId").jqxDropDownButton({width: '97%', height: 25});
		var datafield =  [
				{ name: 'partyId', type: 'string' },
				{ name: 'partyCode', type: 'string'},
				{ name: 'groupName', type: 'string' }
    	];
		var columns = [{ text: uiLabelMap.POSupplierId, datafield: 'partyCode', width: 150, pinned: true },
					   { text: uiLabelMap.POSupplierName, datafield: 'groupName', minwidth: 250 }
		               ];
		var config = {
  			width: 500, 
	   		virtualmode: true,
	   		showtoolbar: false,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: false,
	        url: 'jqGetListPartySupplier',                
	        source: {pagesize: 5}
      	};
      	Grid.initGrid(config, datafield, columns, null, $("#jqxgridBank"));
	}
	
	var clear = function(){
		$('#posTerminalId').jqxDropDownButton('val','');
		$('#bankId').jqxDropDownButton('val','');
		$("#thruDate").val(null);
		$("#inputBankId").val("");
		$("#inputPosTerminalId").val("");
		$("#jqxgridPosTerminal").jqxGrid('clearSelection');
		$("#jqxgridBank").jqxGrid('clearSelection');
		$("#jqxgridPosTerminal").jqxGrid('clearfilters');
		$("#jqxgridBank").jqxGrid('clearfilters');
	}
	
	var initjqxWindow = function(){
		$("#alterpopupWindow").jqxWindow({
	        width: 500, height : 260, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
	    });
	}
	
	var save = function(){
		var row;
        row = {
    		posTerminalId: $('#inputPosTerminalId').val(),
    		partyId: $('#inputBankId').val(),
    		fromDate: $('#fromDate').jqxDateTimeInput('getDate'),
    		thruDate: $('#thruDate').jqxDateTimeInput('getDate')
        };
	    $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	}
	
	var bindEvent = function(){
	    $('#alterpopupWindow').on('close', function (event) {
	    	$('#formAdd').jqxValidator('hide');
	    });
	    
	    $('#alterpopupWindow').on('open', function (event) {
	    	$("#fromDate").jqxDateTimeInput('setDate', new Date());
	    });
	    
	    $("#jqxgridPosTerminal").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var data = $("#jqxgridPosTerminal").jqxGrid('getrowdata', boundIndex);
			var label = data.terminalName + ' [' + data.posTerminalId + ']';
			var dropDownContent = '<div class="innerDropdownContent">' + label + '</div>';
	        $("#posTerminalId").jqxDropDownButton('setContent', dropDownContent);
	        $("#posTerminalId").jqxDropDownButton('close');
	        $("#inputPosTerminalId").val(data.posTerminalId);
		});
	    
	    $("#jqxgridBank").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var data = $("#jqxgridBank").jqxGrid('getrowdata', boundIndex);
			var label = data.groupName + ' [' + data.partyCode + ']';
			var dropDownContent = '<div class="innerDropdownContent">' + label + '</div>';
	        $("#bankId").jqxDropDownButton('setContent', dropDownContent);
	        $("#bankId").jqxDropDownButton('close');
	        $("#inputBankId").val(data.partyId);
		});
	    
	    $("#save").click(function () {
	    	if(validatorVAL.validate()){
	    		save();
	    		$("#alterpopupWindow").jqxWindow('close');
	    	} else {
	    		return;
	    	}
	    });
	    
	    $('#saveAndContinue').click(function(){
	    	if (validatorVAL.validate()) {
	    		save();
	    		clear();
	    	} else {
	    		return;
	    	}
	    });
	    
	    $('#alterpopupWindow').on('close',function(){
	    	clear();
	    	$('#formAdd').jqxValidator('hide');
	    })
	}
	
	var initValidator = function(){
		var mapRules = [
		                {input: '#posTerminalId', type: 'validInputNotNull', action : 'change,close'},
		                {input: '#bankId', type: 'validInputNotNull', action : 'change,close'},
		                {input: '#fromDate', type: 'validInputNotNull', action : 'change,close'}		             
		               ];
		var extendsRules = [{input: '#thruDate', message: uiLabelMap.BACCThruDateValidate, action: 'blur', position: 'bottom', rule: 
								function (input, commit) {
									if (input.jqxDateTimeInput('getDate')) {
										var thruDate = $(input).jqxDateTimeInput('getDate').getTime();
										var fromDate = $("#fromDate").jqxDateTimeInput('getDate').getTime();
										if(thruDate > fromDate){
											return true;
										}
										return false;
									}
									return true;
								}
							}];
		validatorVAL = new OlbValidator($('#formAdd'), mapRules, extendsRules, {position : 'bottom'});
	}
	
	return {
		init : init
	}
}())