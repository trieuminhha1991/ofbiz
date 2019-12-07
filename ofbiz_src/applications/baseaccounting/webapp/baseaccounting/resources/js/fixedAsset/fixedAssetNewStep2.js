var fixedAssetNewStep2 = (function(){
	var init = function(){
		initInput();
		initDropDown();
		initGridDropDown();
		initValidator();
		initEvent();
	};
	var initInput = function(){
		$('#lifeDepAmount').jqxNumberInput({digits: 12, max: 100000000000000, min: 0, width: '97%', spinButtons: true });
		$('#usedQuantity').jqxNumberInput({digits: 12, min: 0, width: '65%', spinButtons: true, decimalDigits: 0});
		$('#yearlyDepRate').jqxNumberInput({digits: 12, disabled: true, min: 0,  inputMode: 'simple', width: '97%', spinButtons: true });
		$('#monthlyDepRate').jqxNumberInput({digits: 12, inputMode: 'simple', min: 0,  width: '97%', spinButtons: true });
		$('#annualDepAmount').jqxNumberInput({digits: 12, disabled: true, max: 100000000000000, min: 0, width: '97%', spinButtons: true });
		$('#monthlyDepAmount').jqxNumberInput({digits: 12, min: 0, max: 100000000000000, width: '97%', spinButtons: true });
		$('#accumulatedDep').jqxNumberInput({digits: 12, min: 0, max: 100000000000000, width: '97%', spinButtons: true });
		$('#remainingValue').jqxNumberInput({digits: 12, disabled: true, min: 0, max: 100000000000000, width: '97%',spinButtons: true });
		$('#purchaseCost').jqxNumberInput({digits: 12, max: 100000000000000, min: 0, width: '97%', spinButtons: true });
		$("#datePurchase").jqxDateTimeInput({ height: '25px', width: '97%'});
		$("#dateAcquired").jqxDateTimeInput({height: '25px', width: '97%'});
		$("#dateOfIncrease").jqxDateTimeInput({height: '25px', width: '97%'});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#usedPeriod"), globalVar.periodData, { width: '30%', height: '25px', valueMember: 'periodId', displayMember: 'description', selectedIndex: 0});
	};
	var initGridDropDown = function(){
		$("#costGlAccountId").jqxDropDownButton({width: '97%', height: 25, dropDownHorizontalAlignment: 'right'});
		$("#depGlAccountId").jqxDropDownButton({width: '97%', height: 25, dropDownHorizontalAlignment: 'right'});
		$("#allocGlAccountId").jqxDropDownButton({width: '97%', height: 25, dropDownHorizontalAlignment: 'right'});
		
		var datafields = [{name: 'glAccountId', type: 'string'}, {name: 'accountName', type: 'string'}];
		var columns = [
						{text: uiLabelMap.BACCGlAccountId, datafield: 'glAccountId', width: '30%'},
						{text: uiLabelMap.BACCAccountName, datafield: 'accountName'}
					];
		
		var configGrid1 = {
				url: 'JqxGetListGlAccountByClass&glAccountClassId=LONGTERM_ASSET',
				filterable: true,
				showtoolbar : false,
				width : '100%',
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
				width : '100%',
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				source:{
					pagesize: 5
				}
		};
		var configGrid3 = {
				url: 'JqxGetListGlAccountByClass&glAccountClassId=SGA_EXPENSE',
				filterable: true,
				showtoolbar : false,
				width : '100%',
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				source:{
					pagesize: 5
				}
		};		
		Grid.initGrid(configGrid1, datafields, columns, null, $("#costGlAccountGrid"));
		Grid.initGrid(configGrid2, datafields, columns, null, $("#depGlAccountGrid"));
		Grid.initGrid(configGrid3, datafields, columns, null, $("#allocGlAccountGrid"));
	};
	var initEvent = function(){
		$('#usedQuantity').on('valueChanged', function (event) {
			var value = event.args.value;
			var depreciation = $('#lifeDepAmount').val();
			calculateDepreciation(depreciation, value, $("#usedPeriod").val());
		});
		$('#usedPeriod').on('change', function (event) {
			var args = event.args;
			if (args) {
				var item = args.item;
				var value = $('#usedQuantity').val();
				var depreciation = $('#lifeDepAmount').val();
				calculateDepreciation(depreciation, value, item.value);
			} 
		});
		$('#lifeDepAmount').on('valueChanged', function (event) {
			var value = $('#usedQuantity').val();
			var depreciation = $('#lifeDepAmount').val();
			calculateDepreciation(depreciation, value, $("#usedPeriod").val());
		});
		$('#purchaseCost').on('valueChanged', function (event) {
			var value = event.args.value;
			$('#lifeDepAmount').jqxNumberInput('setDecimal', value);
		});
		
		$("#costGlAccountGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#costGlAccountGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountId + '</div>';
			$("#costGlAccountId").jqxDropDownButton('setContent', dropDownContent);
			$("#costGlAccountId").attr("data-value", rowData.glAccountId);
			$("#costGlAccountId").jqxDropDownButton('close');
		});
		
		$("#depGlAccountGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#depGlAccountGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountId + '</div>';
			$("#depGlAccountId").jqxDropDownButton('setContent', dropDownContent);
			$("#depGlAccountId").attr("data-value", rowData.glAccountId);
			$("#depGlAccountId").jqxDropDownButton('close');
		});
		
		$("#allocGlAccountGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#allocGlAccountGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountId + '</div>';
			$("#allocGlAccountId").jqxDropDownButton('setContent', dropDownContent);
			$("#allocGlAccountId").attr("data-value", rowData.glAccountId);
			$("#allocGlAccountId").jqxDropDownButton('close');
		});		
		
		$('#datePurchase').on('change', function (event) {
		 	var args = event.args;
		    if (args) {
			    var item = args.item;
			    datePurchase = ($('#datePurchase').jqxDateTimeInput('getDate'));
			    $('#dateAcquired').val(datePurchase);
		    }
		});	
	};
	
	var calculateDepreciation = function(depreciation, usedQuantity, period){
		$('#remainingValue').jqxNumberInput('setDecimal', depreciation);
		if(usedQuantity == 0){
			$('#monthlyDepRate').val(0);
			$('#yearlyDepRate').val(0);
			$('#annualDepAmount').val(0);
			$('#monthlyDepAmount').val(0);
			return;
		}
		if(period == 'MONTH'){
			$('#monthlyDepRate').jqxNumberInput('setDecimal', (100/usedQuantity).toFixed(2));
			$('#yearlyDepRate').jqxNumberInput('setDecimal', (100/usedQuantity*12).toFixed(2));
			$('#annualDepAmount').jqxNumberInput('setDecimal', (depreciation/usedQuantity*12).toFixed(2));
			$('#monthlyDepAmount').jqxNumberInput('setDecimal', (depreciation/usedQuantity).toFixed(2));
		}else if(period == 'YEAR'){
			$('#monthlyDepRate').jqxNumberInput('setDecimal', (100/usedQuantity/12).toFixed(2));
			$('#yearlyDepRate').jqxNumberInput('setDecimal', (100/usedQuantity).toFixed(2));
			$('#annualDepAmount').jqxNumberInput('setDecimal', (depreciation/usedQuantity).toFixed(2));
			$('#monthlyDepAmount').jqxNumberInput('setDecimal', (depreciation/usedQuantity/12).toFixed(2));
		}
	};
	
	var getData = function(){
		var submitedData = {};
		submitedData.lifeDepAmount = $('#lifeDepAmount').val();
		submitedData.remainingValue = $('#remainingValue').val();
		var usedPeriod = $('#usedPeriod').val();
		if(usedPeriod == "MONTH"){
			submitedData.usefulLives = $('#usedQuantity').val();
		}else{
			submitedData.usefulLives = ($('#usedQuantity').val() * 12);
		}
		submitedData.monthlyDepRate = $('#monthlyDepRate').val();
		submitedData.yearlyDepRate = $('#yearlyDepRate').val();
		
		submitedData.monthlyDepAmount = $('#monthlyDepAmount').val();
		submitedData.yearlyDepAmount = $('#annualDepAmount').val();
		submitedData.accumulatedDep = $('#accumulatedDep').val();
		submitedData.purchaseCost = $('#purchaseCost').val();
		var datePurchase = $('#datePurchase').jqxDateTimeInput('val', 'date');
		submitedData.datePurchase = datePurchase.getTime();
		var dateAcquired = $('#dateAcquired').jqxDateTimeInput('val', 'date');
		submitedData.dateAcquired = dateAcquired.getTime();
		var dateOfIncrease = $('#dateOfIncrease').jqxDateTimeInput('val', 'date');
		if(dateOfIncrease){
			submitedData.dateOfIncrease = dateOfIncrease.getTime();
		}
		submitedData.depGlAccountId = $('#allocGlAccountId').attr('data-value');
		submitedData.costGlAccountId = $('#costGlAccountId').attr('data-value');
		submitedData.accDepGlAccountId = $('#depGlAccountId').attr('data-value');
		
		$('#depGlAccountId').attr('data-value');
		return submitedData;
	};
	var initValidator = function(){
		$('#newDep').jqxValidator({
			position: 'bottom',
	        rules: [
	       			{ input: '#usedPeriod', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
	       				rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{ input: '#usedQuantity', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change', 
	    				rule: function (input, commit) {
	                       if(input.val() <= 0){
	                    	   return false;
	                       }
	                       return true;
	    				}
	    			},
	    			{ input: '#purchaseCost', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change', 
	    				rule: function (input, commit) {
	                       if(input.val() <= 0){
	                    	   return false;
	                       }
	                       return true;
	    				}
	    			},
	    			{input: '#dateAcquired', message: uiLabelMap.BACCDateFixedAssetFieldRequired, action: 'keyup, change', 
						rule: function (input, commit) {
		                    if(input.jqxDateTimeInput('getDate') < $("#datePurchase").jqxDateTimeInput('getDate') && $("#datePurchase").jqxDateTimeInput('getDate')){
		                    	return false;
		                    }else{
		                    	return true;
		                    }
		                    	
		            	}
					},
					{ input: '#costGlAccountId', message: uiLabelMap.FieldRequired, action: 'change', 
	    				rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{ input: '#depGlAccountId', message: uiLabelMap.FieldRequired, action: 'change', 
	    				rule: function (input, commit) {
	    					if(input.val()){
	    						return true;
	    					}else{
	    						return false;
	    					}
	    				}
	    			},
	    			{ input: '#allocGlAccountId', message: uiLabelMap.FieldRequired, action: 'change', 
	    				rule: function (input, commit) {
	    					if(input.val()){
	    						return true;
	    					}else{
	    						return false;
	    					}
	    				}
	    			},	    			
               ]
	    });
	};
	var validate = function(){
		return $('#newDep').jqxValidator('validate');
	};
	var hideValidate = function(){
		$('#newDep').jqxValidator('hide');
	};
	var windownOpenInit = function(){
		var date = new Date();
		$("#datePurchase").val(date);
		$("#dateAcquired").val(date);
		$("#dateOfIncrease").val(null);
	};
	var resetData = function(){
		Grid.clearForm($('#newDep'));
	};
	return{
		init: init,
		getData: getData,
		validate: validate,
		resetData: resetData,
		hideValidate: hideValidate,
		windownOpenInit: windownOpenInit
	}
}());
$(document).ready(function(){
	fixedAssetNewStep2.init()
});