var actionName = "";
var changeReasonCBB;
$(function(){
	OlbOrderDetailPage.init();
});
var OlbOrderDetailPage = (function(){
	var init = function(){
		initElement();
		initEvent();
		initValidateForm();
	};
	var initElement = function(){
		jOlbUtil.windowPopup.create($("#confirmOrderChangeStatus"), {width: 540, height: 220, cancelButton: $("#alterConfirmCancel")});
		
		var configChangeReason = {
			placeHolder: uiLabelMap.OtherReason,
			key: 'enumId',
			value: 'descriptionSearch',
			width: '98%',
			dropDownHeight: 260,
			dropDownWidth: 500,
			autoDropDownHeight: false,
			displayDetail: true,
			dropDownHorizontalAlignment: 'right',
			autoComplete: true,
			searchMode: 'containsignorecase',
			renderer : null,
			renderSelectedItem : null,
			selectedIndex: 0
		};
		changeReasonCBB = new OlbComboBox($("#wcos_changeReason"), reasonCancel, configChangeReason, []);
	};
	var initEvent = function(){
		$("#confirmOrderChangeStatus").on('open', function(event){
			$("body").css('overflow', 'hidden');
		});
		$("#confirmOrderChangeStatus").on('close', function(event){
			$("body").css('overflow', 'inherit');
		});
		$("#alterConfirmSave").on('click', function(){
			if ("CANCEL" == actionName) {
				if(!$('#confirmOrderChangeStatus').jqxValidator('validate')) return false;
				var selectedIndex = $("#wcos_changeReason").jqxComboBox('getSelectedIndex');
				if (selectedIndex > 0) {
					var resultInput = $("#wcos_changeReason").jqxComboBox('getSelectedItem');
					document.OrderCancel.changeReason.value = "" + resultInput.value;
					document.OrderCancel.submit();
				} else {
					var resultValue = $("#wcos_changeDescription").val();
					document.OrderCancel.changeReason.value = "" + resultValue;
					document.OrderCancel.submit();
				}
			}
		});
		$("#confirmOrderChangeStatus").on('close', function(event){
			$("#wcos_changeReason").jqxComboBox("close");
		});
		$("#wcos_changeReason").on("change", function(event){
			var args = event.args;
		    if (args) {
		    	//var index = args.index;
		    	var item = args.item;
		    	if (item) {
		    		var parentObj = $("#wcos_changeDescription").closest(".row-fluid");
			    	if (parentObj) {
			    		if (!/^\s*$/.test(item.value)) {
					    	$(parentObj).hide();
					    } else {
					    	$(parentObj).show();
					    }
			    	}
		    	}
		    }
		});
	};
	var initValidateForm = function() {
		new OlbValidator($("#confirmOrderChangeStatus"), null, 
			[{input: '#wcos_changeReason', message: uiLabelMap.validFieldRequire, action: 'change', 
				rule: function(input, commit){
					var index = $(input).jqxComboBox('getSelectedIndex');
					if (index > 0) {
						if (OlbElementUtil.isNotEmpty($(input).val())) {
							return true;
						}
						return false;
					} else {
						return true;
					}
				}
			},
			{input: '#wcos_changeDescription', message: uiLabelMap.validFieldRequire, action: 'key-up', 
				rule: function(input, commit){
					var index = $("#wcos_changeReason").jqxComboBox('getSelectedIndex');
					if (index > 0) {
						return true;
					} else {
						if (OlbElementUtil.isNotEmpty($(input).val())) {
							return true;
						}
						return false;
					}
				}
			}]
		);
	};
	return {
		init: init,
	}
}());
var changeOrderStatus = function(action){
	if ("CANCEL" == action) {
		actionName = "CANCEL";
		$("#confirmOrderChangeStatus").jqxWindow('open');
	}
};