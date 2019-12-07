$(function(){
	if (typeof(OlbPromoCodeEdit2) == "undefined") {
		var OlbPromoCodeEdit2 = (function(){
			var userEnteredDDL;
			var requireEmailOrPartyDDL;
			
			var init = function(){
				initElement();
				initComplexElement();
				initEvent();
			};
			var initElement = function(){
				jOlbUtil.input.create("#we_productPromoId", {width: '99%', disabled: true});
				jOlbUtil.input.create("#we_productPromoCodeId", {width: '99%', disabled: true});
				jOlbUtil.numberInput.create("#we_useLimitPerCode", {width: '98%', spinButtons:true, decimalDigits: 0, min: 0});
				jOlbUtil.numberInput.create("#we_useLimitPerCustomer", {width: '98%', spinButtons:true, decimalDigits: 0, min: 0});
				
				jOlbUtil.dateTimeInput.create("#we_fromDate", {width: '98%', allowNullDate: true, value: null, showFooter: true});
				jOlbUtil.dateTimeInput.create("#we_thruDate", {width: '98%', allowNullDate: true, value: null, showFooter: true});
				
				if (promotionCode.useLimitPerCode != null) jOlbUtil.numberInput.val("#we_useLimitPerCode", promotionCode.useLimitPerCode);
				else jOlbUtil.numberInput.clear("#we_useLimitPerCode");
				if (promotionCode.useLimitPerCustomer != null) jOlbUtil.numberInput.val("#we_useLimitPerCustomer", promotionCode.useLimitPerCustomer);
				else jOlbUtil.numberInput.clear("#we_useLimitPerCustomer");
				
				if (promotionCode.fromDate != null) $("#we_fromDate").jqxDateTimeInput('setDate', promotionCode.fromDate);
				if (promotionCode.thruDate != null) $("#we_thruDate").jqxDateTimeInput('setDate', promotionCode.thruDate);
			};
			var initComplexElement = function(){
				var configUserEntered = {
					width: "100%",
		    		key: "id",
		    		value: "description",
		    		autoDropDownHeight: true,
		    		displayDetail: false,
		    		placeHolder: uiLabelMap.BSClickToChoose,
				}
				userEnteredDDL = new OlbDropDownList($('#we_userEntered'), dataYesNoChoose, configUserEntered, [promotionCode.userEntered]);
				
				var configRequireEmailOrParty = {
					width: "100%",
		    		key: "id",
		    		value: "description",
		    		autoDropDownHeight: true,
		    		displayDetail: false,
		    		placeHolder: uiLabelMap.BSClickToChoose,
				}
				requireEmailOrPartyDDL = new OlbDropDownList($('#we_requireEmailOrParty'), dataYesNoChoose, configRequireEmailOrParty, [promotionCode.requireEmailOrParty]);
			};
			var initEvent = function(){
				$("#we_alterSavePromoCode").on("click", function(){
					var dataMap = {
						productPromoId: $("#we_productPromoId").val(),
						productPromoCodeId: $("#we_productPromoCodeId").val(),
						userEntered: userEnteredDDL.getValue(),
						requireEmailOrParty: requireEmailOrPartyDDL.getValue(),
						useLimitPerCode: $("#we_useLimitPerCode").val(),
						useLimitPerCustomer: $("#we_useLimitPerCustomer").val(),
						fromDate: $('#we_fromDate').jqxDateTimeInput('getDate') != null ? $('#we_fromDate').jqxDateTimeInput('getDate').getTime() : '',
						thruDate: $('#we_thruDate').jqxDateTimeInput('getDate') != null ? $('#we_thruDate').jqxDateTimeInput('getDate').getTime() : '',
					};
					$.ajax({
						type: 'POST',
						url: 'updateProductPromoCodeAjax',
						data: dataMap,
						beforeSend: function(){;
							$("#loader_page_common").show();
						},
						success: function(data){
							jOlbUtil.processResultDataAjax(data, "default", "default", function(data){
					    		$("#windowEditPromoCodeContainer").html(data);
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
			return {
				init: init,
			};
		}());
	}
	
	OlbPromoCodeEdit2.init();
	
	setTimeout(function(){
		$("#content-messages").click();
	}, 5000);
});
