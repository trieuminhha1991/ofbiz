$(function(){
	OlbQuotationInfo.init();
});
var OlbQuotationInfo = (function(){
	var validatorVAL;
	
	var init = function(){
		initElement();
		initEvent();
		initValidateForm();
	};
	var initElement = function(){
		//$('#description').jqxTextArea({height: 40, width: '100%', minLength: 1});
		
		jOlbUtil.dateTimeInput.create("#requiredByDate", {width: '100%', allowNullDate: true, value: null, disabled: true});
		jOlbUtil.dateTimeInput.create("#requirementStartDate", {width: '100%', allowNullDate: true, value: null});
	
		if (typeof(requirementSelected.requiredByDate) != "undefined"){
			$('#requiredByDate').jqxDateTimeInput('setDate', requirementSelected.fromDate);
		} else {
			$('#requiredByDate').jqxDateTimeInput('setDate', new Date());
		}
		if (typeof(requirementSelected.requirementStartDate) != "undefined"){
			$('#requirementStartDate').jqxDateTimeInput('setDate', requirementSelected.thruDate);
		}
	};
	var processDataRowSelect = function(rowBoundIndex) {
		var data = $("#jqxgridOrder").jqxGrid("getrowdata", rowBoundIndex);
    	if (data) {
    		var idStr = data.orderId;
    		if (typeof(productPricesMap[idStr]) != "undefined") {
    			var itemValue = productPricesMap[idStr];
    			itemValue.selected = true;
    			productPricesMap[idStr] = itemValue;
    		} else {
				var itemValue = {};
    			itemValue.orderId = data.orderId;
    			itemValue.totalWeight = data.totalWeight;
    			itemValue.customerCode = data.customerCode;
    			itemValue.estimatedDeliveryDate = data.estimatedDeliveryDate;
    			itemValue.shipBeforeDate = data.shipBeforeDate;
    			itemValue.shipAfterDate = data.shipAfterDate;
    			itemValue.orderDate = data.orderDate;
    			itemValue.selected = true;
    			productPricesMap[idStr] = itemValue;
    		}
    	}
	};
	var selectableCheckBox = true;
	var initEvent = function(){
		$("#jqxgridOrder").on("bindingcomplete", function (event) {
			var dataRow = $("#jqxgridOrder").jqxGrid("getboundrows");
			if (typeof(dataRow) != 'undefined') {
				var icount = 0;
				selectableCheckBox = false;
				$.each(dataRow, function(key, value){
					if (value) {
						var isSelected = false;
						var idStr = value.orderId;
						if (typeof(productPricesMap[idStr]) != "undefined") {
							var itemValue = productPricesMap[idStr];
							if (itemValue.selected) {
								$('#jqxgridOrder').jqxGrid('selectrow', icount);
								isSelected = true;
							}
						}
						if (OlbElementUtil.isNotEmpty(value.orderId) && !isSelected) {
							$('#jqxgridOrder').jqxGrid('unselectrow', icount);
						}
					}
					icount++;
				});
				selectableCheckBox = true;
			}
		});
		$('#jqxgridOrder').on('rowselect', function (event) {
			if (selectableCheckBox) {
				var args = event.args;
			    var rowBoundIndex = args.rowindex;
			    if (Object.prototype.toString.call(rowBoundIndex) === '[object Array]') {
			    	for (var i = 0; i < rowBoundIndex.length; i++) {
			    		processDataRowSelect(rowBoundIndex[i]);
			    	}
			    } else {
			    	processDataRowSelect(rowBoundIndex);
			    }
			}
		});
		$('#jqxgridOrder').on('rowunselect', function (event) {
			if (selectableCheckBox) {
			    var args = event.args;
			    var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridOrder").jqxGrid("getrowdata", rowBoundIndex);
		    	if (typeof(data) != 'undefined') {
		    		var idStr = data.orderId;
		    		if (typeof(productPricesMap[idStr]) != "undefined") {
		    			var itemValue = productPricesMap[idStr];
		    			itemValue.selected = false;
		    			productPricesMap[idStr] = itemValue;
		    		}
		    	}
			}
		});
	};
	var initValidateForm = function(){
		var mapRules = [
                {input: '#requiredByDate', type: 'validDateTimeInputNotNull'},
				{input: '#requirementStartDate', type: 'validDateTimeInputNotNull'},
				{input: '#requirementStartDate', type: 'validDateCompareToday'},
				{input: '#requiredByDate, #requirementStartDate', type: 'validCompareTwoDate', paramId1 : "requiredByDate", paramId2 : "requirementStartDate"},
			];
		validatorVAL = new OlbValidator($('#initRequirementEntry'), mapRules, null, {scroll: true});
	};
	var getValidator = function(){
		return validatorVAL;
	}
	return {
		init: init,
		getValidator: getValidator,
	};
}());