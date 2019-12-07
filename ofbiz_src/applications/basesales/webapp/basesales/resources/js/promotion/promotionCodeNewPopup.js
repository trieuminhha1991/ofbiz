$(function(){
	OlbPromoCodeNew.init();
});
var OlbPromoCodeNew = (function(){
	var windowInited = false;
	var userEnteredDDL;
	var requireEmailOrPartyDDL;
	var promoCodeLayoutDDL;
	var validatorVAL;
	
	var init = function(){
		initElement();
		initComplexElement();
		initEvent();
		initValidateForm();
		windowInited = true;
	};
	var initElement = function(){
		jOlbUtil.numberInput.create("#wn_quantity", {width: '100%', spinButtons:true, decimalDigits: 0, min: 0});
		jOlbUtil.numberInput.create("#wn_codeLength", {width: '100%', spinButtons:true, decimalDigits: 0, min: 0});
		jOlbUtil.numberInput.create("#wn_useLimitPerCode", {width: '98%', spinButtons:true, decimalDigits: 0, min: 0});
		jOlbUtil.numberInput.create("#wn_useLimitPerCustomer", {width: '98%', spinButtons:true, decimalDigits: 0, min: 0});
		
		jOlbUtil.windowPopup.create($("#alterpopupWindowNewPromoCode"), {maxWidth: 960, width: 960, height: 320, cancelButton: $("#wn_alterCancel")});
		
		jOlbUtil.numberInput.clear("#wn_useLimitPerCode");
		jOlbUtil.numberInput.clear("#wn_useLimitPerCustomer");
	};
	var initComplexElement = function(){
		var configUserEntered = {
			width: "98%",
    		key: "id",
    		value: "description",
    		autoDropDownHeight: true,
    		displayDetail: false,
    		placeHolder: uiLabelMap.BSClickToChoose,
		}
		userEnteredDDL = new OlbDropDownList($('#wn_userEntered'), dataYesNoChoose, configUserEntered, ["Y"]);
		
		var configRequireEmailOrParty = {
			width: "98%",
    		key: "id",
    		value: "description",
    		autoDropDownHeight: true,
    		displayDetail: false,
    		placeHolder: uiLabelMap.BSClickToChoose,
		}
		requireEmailOrPartyDDL = new OlbDropDownList($('#wn_requireEmailOrParty'), dataYesNoChoose, configRequireEmailOrParty, ["N"]);
		
		var configPromoCodeLayout = {
				width: "100%",
				key: "id",
				value: "description",
				autoDropDownHeight: true,
				displayDetail: false,
				placeHolder: uiLabelMap.BSClickToChoose,
				selectedIndex: 0,
		}
		promoCodeLayoutDDL = new OlbDropDownList($('#wn_promoCodeLayout'), dataPromoCodeLayout, configPromoCodeLayout, []);
	};
	var initEvent = function(){
		$("#wn_alterSave").on("click", function(){
			if (!validatorVAL.validate()) return false;
			var dataMap = {
				productPromoId: $("#wn_productPromoId").val(),
				quantity: $("#wn_quantity").val(),
				codeLength: $("#wn_codeLength").val(),
				promoCodeLayout: promoCodeLayoutDDL.getValue(),
				userEntered: userEnteredDDL.getValue(),
				requireEmailOrParty: requireEmailOrPartyDDL.getValue(),
				useLimitPerCode: $("#wn_useLimitPerCode").val(),
				useLimitPerCustomer: $("#wn_useLimitPerCustomer").val(),
			};
			$.ajax({
				type: 'POST',
				url: 'createProductPromoCodeSetAjax',
				data: dataMap,
				beforeSend: function(){
					$("#loader_page_common").show();
				},
				success: function(data){
					jOlbUtil.processResultDataAjax(data, "default", function(){
						$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
			        	$("#jqxNotification").jqxNotification("open");
			        	
			        	$("#alterpopupWindowNewPromoCode").jqxWindow("close");
			        	$("#jqxPromotionCode").jqxGrid("updatebounddata");
					});
				},
				error: function(data){
					alert("Send request is error");
				},
				complete: function(data){
					$("#loader_page_common").hide();
				},
			});
		});
	};
	var initValidateForm = function(){
		var mapRules = [];
		var extendRules = [
			{input: '#wn_quantity', message: uiLabelMap.BSQuantityMustBeGreaterThanZero, action: 'keyup', 
				rule: function(input, commit){
					var value = $(input).val();
					if(value > 0){
						return true;
					}
					return false;
				}
			},
			{input: '#wn_codeLength', message: uiLabelMap.BSQuantityMustBeGreaterThanZero, action: 'keyup', 
				rule: function(input, commit){
					var value = $(input).val();
					if(value > 0){
						return true;
					}
					return false;
				}
			}
		];
		validatorVAL = new OlbValidator($("#alterpopupWindowNewPromoCode"), mapRules, extendRules);
	};
	var openWindowNew = function() {
		if (!windowInited) {
			init();
		}
		$("#alterpopupWindowNewPromoCode").jqxWindow("open");
	};
	var clearNumberInput = function(element){
		jOlbUtil.numberInput.clear(element);
	};
	return {
		init: init,
		openWindowNew: openWindowNew,
		clearNumberInput: clearNumberInput,
	};
}());