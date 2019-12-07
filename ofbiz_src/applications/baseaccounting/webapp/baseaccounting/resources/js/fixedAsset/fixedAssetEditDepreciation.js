var fixedAssetEditDepreciationObj = (function(){
	var _isWindowOpened = false;
	var _fixedAssetDepId = "";
	var init = function(){
		initInput();
		initDropDown();
		initGridDropDown();
		initWindow();
		initValidator();
		initEvent();
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#editFixedAssetDepreciationWindow"), 830, 470);
	};
	var initInput = function(){
		$('#lifeDepAmount').jqxNumberInput({digits: 12, max: 100000000000000, min: 0, width: '97%', spinButtons: true });
		$('#usedQuantity').jqxNumberInput({digits: 12, min: 0, width: '65%', spinButtons: true, decimalDigits: 0 });
		$('#yearlyDepRate').jqxNumberInput({digits: 12, disabled: true, min: 0,  inputMode: 'simple', width: '97%', spinButtons: true });
		$('#monthlyDepRate').jqxNumberInput({digits: 12, inputMode: 'simple', min: 0,  width: '97%', spinButtons: true });
		$('#annualDepAmount').jqxNumberInput({digits: 12, disabled: true, max: 100000000000000, min: 0, width: '97%', spinButtons: true });
		$('#monthlyDepAmount').jqxNumberInput({digits: 12, min: 0, max: 100000000000000, width: '97%', spinButtons: true });
		$('#accumulatedDep').jqxNumberInput({digits: 12, min: 0, max: 100000000000000, width: '97%', spinButtons: true });
		$('#remainingValue').jqxNumberInput({digits: 12, disabled: true, min: 0, max: 100000000000000, width: '97%',spinButtons: true });
		$('#purchaseCost').jqxNumberInput({digits: 12, max: 100000000000000, min: 0,width: '97%', spinButtons: true });
		$("#datePurchase").jqxDateTimeInput({ height: '25px', width: '97%', value: null});
		$("#dateAcquired").jqxDateTimeInput({height: '25px', width: '97%', value: null});
		$("#dateOfIncrease").jqxDateTimeInput({height: '25px', width: '97%', value: null});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#usedPeriod"), globalVar.periodData, { width: '30%', height: '25px', valueMember: 'periodId', displayMember: 'description', selectedIndex: 0});
	};
	var initGridDropDown = function(){
		$("#costGlAccountId").jqxDropDownButton({width: '97%', height: 25, dropDownHorizontalAlignment: 'right'});
		$("#depGlAccountId").jqxDropDownButton({width: '97%', height: 25, dropDownHorizontalAlignment: 'right'});
		$("#accDepGlAccountId").jqxDropDownButton({width: '97%', height: 25, dropDownHorizontalAlignment: 'right'});
		
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
		Grid.initGrid(configGrid1, datafields, columns, null, $("#costGlAccountGrid"));
		Grid.initGrid(configGrid2, datafields, columns, null, $("#depGlAccountGrid"));
		Grid.initGrid(configGrid3, datafields, columns, null, $("#accDepGlAccountGrid"));
	};
	var initEvent = function(){
		$('#usedQuantity').on('valueChanged', function (event) {
			if(_isWindowOpened){
				var value = event.args.value;
				var depreciation = $('#lifeDepAmount').val();
				calculateDepreciation(depreciation, value, $("#usedPeriod").val());
			}
		});
		$('#usedPeriod').on('change', function (event) {
			if(_isWindowOpened){
				var args = event.args;
				if (args) {
					var item = args.item;
					var value = $('#usedQuantity').val();
					var depreciation = $('#lifeDepAmount').val();
					calculateDepreciation(depreciation, value, item.value);
				} 
			}
		});
		$('#lifeDepAmount').on('valueChanged', function (event) {
			if(_isWindowOpened){
				var value = $('#usedQuantity').val();
				var depreciation = $('#lifeDepAmount').val();
				calculateDepreciation(depreciation, value, $("#usedPeriod").val());
			}
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
		
		$("#accDepGlAccountGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#accDepGlAccountGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountId + '</div>';
			$("#accDepGlAccountId").jqxDropDownButton('setContent', dropDownContent);
			$("#accDepGlAccountId").attr("data-value", rowData.glAccountId);
			$("#accDepGlAccountId").jqxDropDownButton('close');
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
		
		$('#datePurchase').on('change', function (event) {
		 	var args = event.args;
		    if (args) {
			    var item = args.item;
			    datePurchase = ($('#datePurchase').jqxDateTimeInput('getDate'));
			    $('#dateAcquired').val(datePurchase);
		    }
		});	
		$("#editDepreciationBtn").click(function(e){
			accutils.openJqxWindow($("#editFixedAssetDepreciationWindow"));
		});
		$("#editFixedAssetDepreciationWindow").on('open', function(e){
			Loading.show('loadingMacro');
			$.ajax({
				url: 'getFixedAssetGeneralInfo',
				type: "POST",
				data: {fixedAssetId: globalVar.fixedAssetId},
				success: function(response) {
					  if(response.responseMessage == "error"){
						  bootbox.dialog(response.errorMessage,
									[
									{
										"label" : uiLabelMap.CommonClose,
										"class" : "btn-danger btn-small icon-remove open-sans",
									}]		
							);
						  return;
					  }
					  setFixedAssetData(response.fixedAsset);
				  },
				complete:  function(jqXHR, textStatus){
					Loading.hide('loadingMacro');
					_isWindowOpened = true;
				}
			});
		});
		$("#editFixedAssetDepreciationWindow").on('close', function(e){
			_isWindowOpened = false;
			Grid.clearForm($("#editFixedAssetDepreciationWindow"));
		});
		$("#cancelEditFixedAssetDepre").click(function(e){
			$("#editFixedAssetDepreciationWindow").jqxWindow('close');
		});
		$("#saveEditFixedAssetDepre").click(function(e){
			var valid = $("#editFixedAssetDepreciationWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			Loading.show('loadingMacro');
			var data = getData();
			data.fixedAssetId = globalVar.fixedAssetId;
			data.fixedAssetDepId = _fixedAssetDepId;
			$.ajax({
				url: 'updateFixedAssetDepreciation',
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
						  return;
					  }
					  Grid.renderMessage('jqxgrid', uiLabelMap.wgupdatesuccess, {template : 'success', appendContainer : '#containerjqxgrid'});
					  updateFixedAssetView(data);
					  $("#editFixedAssetDepreciationWindow").jqxWindow('close');
				  },
				complete:  function(jqXHR, textStatus){
					Loading.hide('loadingMacro');	
				}
			});
		});
	};
	
	var updateFixedAssetView = function(data){
		var datePurchase = $('#datePurchase').jqxDateTimeInput('val', 'date'); 
		var dateAcquired = $('#dateAcquired').jqxDateTimeInput('val', 'date');
		$("#fixedAssetDatePurchaseView").html(getDateDesc(datePurchase));
		$("#fixedAssetCostGlAccView").html(data.costGlAccountId);
		$("#fixedAssetPurchaseCostView").html(formatcurrency(data.purchaseCost));
		$("#fixedAssetLifeDepreView").html(formatcurrency(data.lifeDepAmount));
		$("#fixedAssetUsefulLiveView").html(data.usefulLives + " " + uiLabelMap.BSMonthLowercase);
		$("#fixedAssetYearlyDepRateView").html(formatcurrency(data.yearlyDepRate));
		$("#fixedAssetMonthlyDepRateView").html(formatcurrency(data.monthlyDepRate));
		$("#fixedAssetDateAcquiredView").html(getDateDesc(dateAcquired));
		$("#fixedAssetDepreGlAccView").html(data.depGlAccountId);
		$("#fixedAssetYearlyDepAmountView").html(formatcurrency(data.yearlyDepAmount));
		$("#fixedAssetMonthlyDepAmountView").html(formatcurrency(data.monthlyDepAmount));
		$("#fixedAssetAccumulatedDepView").html(formatcurrency(data.accumulatedDep));
		$("#fixedAssetRemainValueView").html(formatcurrency(data.remainingValue));
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
		
		var expectedEndOfLife = new Date(new Date(dateAcquired).setMonth(dateAcquired.getMonth() + parseInt($('#usedQuantity').val())));
		submitedData.expectedEndOfLife = accutils.getTimestamp(expectedEndOfLife);
		
		submitedData.depGlAccountId = $('#depGlAccountId').attr('data-value');
		submitedData.accDepGlAccountId = $('#accDepGlAccountId').attr('data-value');
		submitedData.costGlAccountId = $('#costGlAccountId').attr('data-value');
		return submitedData;
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
	
	var initValidator = function(){
		$('#editFixedAssetDepreciationWindow').jqxValidator({
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
	    			{ input: '#accDepGlAccountId', message: uiLabelMap.FieldRequired, action: 'change', 
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
	var setFixedAssetData = function(data){
		_fixedAssetDepId = data.fixedAssetDepId;
		$("#datePurchase").val(new Date(data.datePurchase));
		if(typeof(data.dateOfIncrease) != 'undefined'){
			$("#dateOfIncrease").val(new Date(data.dateOfIncrease));
		}
		$("#dateAcquired").val(new Date(data.dateAcquired));
		if(data.costGlAccountId){
			var dropDownContent = '<div class="innerDropdownContent">' + data.costGlAccountId + '</div>';
			$("#costGlAccountId").jqxDropDownButton('setContent', dropDownContent);
	        accutils.setAttrDataValue('costGlAccountId', data.costGlAccountId);
		}
		if(data.depGlAccountId){
			var dropDownContent = '<div class="innerDropdownContent">' + data.depGlAccountId + '</div>';
			$("#depGlAccountId").jqxDropDownButton('setContent', dropDownContent);
	        accutils.setAttrDataValue('depGlAccountId', data.depGlAccountId);
		}
		if(data.accDepGlAccountId){
			var dropDownContent = '<div class="innerDropdownContent">' + data.accDepGlAccountId + '</div>';
			$("#accDepGlAccountId").jqxDropDownButton('setContent', dropDownContent);
	        accutils.setAttrDataValue('accDepGlAccountId', data.accDepGlAccountId);
		}		
		$("#purchaseCost").val(data.purchaseCost);
		if(typeof(data.lifeDepAmount) != 'undefined'){
			$("#lifeDepAmount").val(data.lifeDepAmount);
		}
		$("#usedPeriod").val("MONTH");
		$("#usedQuantity").val(data.usefulLives);
		$("#yearlyDepRate").val(data.yearlyDepRate);
		$("#monthlyDepRate").val(data.monthlyDepRate);
		$("#annualDepAmount").val(data.yearlyDepAmount);
		$("#monthlyDepAmount").val(data.monthlyDepAmount);
		$("#accumulatedDep").val(data.accumulatedDep);
		$("#remainingValue").val(data.remainingValue);
	};
	var getDateDesc = function(date){
		var str = "";
		str += (date.getDate() > 9? date.getDate() : ("0" + date.getDate()));
		str += "/" + (date.getMonth() >= 9? (date.getMonth() + 1) : ("0" + (date.getMonth() + 1)));
		str += "/" + date.getFullYear();
		return str;
	};
	return{
		init: init
	}
}());
$(document).ready(function(){
	fixedAssetEditDepreciationObj.init();
});