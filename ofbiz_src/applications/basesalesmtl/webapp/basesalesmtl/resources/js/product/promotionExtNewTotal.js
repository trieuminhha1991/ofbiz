$(function(){
	OlbPromoNewTotal.init();
});
var OlbPromoNewTotal = (function(){
	var init = function(){
		initElement();
		initEvent();
	};
	var initElement = function(){
        jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
	};
	var initEvent = function(){
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(info.step == 1 && (info.direction == "next")) {
				// check form valid
				if(!OlbPromoNewInfo.getValidator().validate()) return false;
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.BSAreYouSureYouWantToCreate, 
				function() {
            		finishCreatePromo();
            	}
			);
		}).on('stepclick', function(e){
			//return false; //prevent clicking on steps
		});
	};
	var finishCreatePromo = function(){
		$("#btnPrevWizard").addClass("disabled");
		$("#btnNextWizard").addClass("disabled");
		
		var dataStr = prepareDataToProccess();
		$.ajax({
            type: "POST", 
            url: urlCreateUpdatePromotion,
            data: dataStr,
            beforeSend: function () {
				$("#loader_page_common").show();
			}, 
            success: function (data) {
            	jOlbUtil.processResultDataAjax(data, 'default', 
    				function(data){
    					var productPromoId = data.productPromoId;
    		        	if (OlbElementUtil.isNotEmpty(productPromoId)) {
    		        		window.location.href = "viewPromotionExt?productPromoId=" + productPromoId;
    		        	}
    				}
        		);
            },
            error: function(data){
				alert("Send request is error");
			},
			complete: function(data){
				$("#loader_page_common").hide();
				$("#btnPrevWizard").removeClass("disabled");
				$("#btnNextWizard").removeClass("disabled");
			},
        });
	};
	var prepareDataToProccess = function(){
		var dataStr = new StringBuilder();
		dataStr.append("productPromoId=");
		dataStr.append($("#productPromoId").val());
		dataStr.append("&productPromoTypeId=");
		dataStr.append($("#productPromoTypeId").val());
		dataStr.append("&promoName=");
		dataStr.append(encodeURIComponent($("#promoName").val()));
		dataStr.append("&salesMethodChannelEnumId=");
		dataStr.append($("#salesMethodChannelEnumId").val());
		var fromDate = $("#fromDate").jqxDateTimeInput('getDate') != null ? $("#fromDate").jqxDateTimeInput('getDate').getTime() : "";
		var thruDate = $("#thruDate").jqxDateTimeInput('getDate') != null ? $("#thruDate").jqxDateTimeInput('getDate').getTime() : "";
		if (OlbElementUtil.isNotEmpty(fromDate)) {
			dataStr.append("&fromDate=");
			dataStr.append(fromDate);
		}
		if (OlbElementUtil.isNotEmpty(thruDate)) {
			dataStr.append("&thruDate=");
			dataStr.append(thruDate);
		}
		
		var roleTypeIds = $("#roleTypeIds").jqxComboBox('getSelectedItems');
		if (OlbElementUtil.isNotEmpty(roleTypeIds)) {
			for (var i = 0; i < roleTypeIds.length; i++) {
				var item = roleTypeIds[i];
			 	if (item != null) {
			 		dataStr.append("&roleTypeIds=");
			 		dataStr.append(item.value);
			 	}
			}
		}
		
		var productStoreIds = $("#productStoreIds").jqxComboBox('getSelectedItems');
		if (OlbElementUtil.isNotEmpty(productStoreIds)) {
			for (var i = 0; i < productStoreIds.length; i++) {
				var item = productStoreIds[i];
			 	if (item != null) {
			 		dataStr.append("&productStoreIds=");
			 		dataStr.append(item.value);
			 	}
			}
		}
		
		dataStr.append("&promoText=");
		dataStr.append(encodeURIComponent($("#promoText").val()));
		dataStr.append("&showToCustomer=");
		dataStr.append($("#showToCustomer").val());
		dataStr.append("&requireCode=");
		dataStr.append($("#requireCode").val());
		if (OlbElementUtil.isNotEmpty($("#useLimitPerOrder").val())) {
			dataStr.append("&useLimitPerOrder=");
			dataStr.append($("#useLimitPerOrder").val());
		}
		if (OlbElementUtil.isNotEmpty($("#useLimitPerCustomer").val())) {
			dataStr.append("&useLimitPerCustomer=");
			dataStr.append($("#useLimitPerCustomer").val());
		}
		if (OlbElementUtil.isNotEmpty($("#useLimitPerPromotion").val())) {
			dataStr.append("&useLimitPerPromotion=");
			dataStr.append($("#useLimitPerPromotion").val());
		}
		dataStr.append("&");
		dataStr.append($("#editProductPromoRules").serialize());
		
		$('[id^="productIdListCond"]').each(function(i, obj) {
		    var productIdListCond = $(obj).jqxComboBox('getSelectedItems');
		    var id = $(obj).attr("id");
			if (OlbElementUtil.isNotEmpty(productIdListCond)) {
				for (var i = 0; i < productIdListCond.length; i++) {
					var item = productIdListCond[i];
				 	if (item != null) {
				 		dataStr.append("&");
				 		dataStr.append(id);
				 		dataStr.append("=");
				 		dataStr.append(item.value);
				 	}
				}
			}
		});
		$('[id^="productCatIdListCond"]').each(function(i, obj) {
		    var productCatIdListCond = $(obj).jqxComboBox('getSelectedItems');
		    var id = $(obj).attr("id");
			if (OlbElementUtil.isNotEmpty(productCatIdListCond)) {
				for (var i = 0; i < productCatIdListCond.length; i++) {
					var item = productCatIdListCond[i];
				 	if (item != null) {
				 		dataStr.append("&");
				 		dataStr.append(id);
				 		dataStr.append("=");
				 		dataStr.append(item.value);
				 	}
				}
			}
		});
		$('[id^="productIdListAction"]').each(function(i, obj) {
		    var productIdListAction = $(obj).jqxComboBox('getSelectedItems');
		    var id = $(obj).attr("id");
			if (OlbElementUtil.isNotEmpty(productIdListAction)) {
				for (var i = 0; i < productIdListAction.length; i++) {
					var item = productIdListAction[i];
				 	if (item != null) {
				 		dataStr.append("&");
				 		dataStr.append(id);
				 		dataStr.append("=");
				 		dataStr.append(item.value);
				 	}
				}
			}
		});
		$('[id^="productCatIdListAction"]').each(function(i, obj) {
		    var productCatIdListAction = $(obj).jqxComboBox('getSelectedItems');
		    var id = $(obj).attr("id");
			if (OlbElementUtil.isNotEmpty(productCatIdListAction)) {
				for (var i = 0; i < productCatIdListAction.length; i++) {
					var item = productCatIdListAction[i];
				 	if (item != null) {
				 		dataStr.append("&");
				 		dataStr.append(id);
				 		dataStr.append("=");
				 		dataStr.append(item.value);
				 	}
				}
			}
		});
		return dataStr.toString();
	};
	return {
		init: init
	};
}());