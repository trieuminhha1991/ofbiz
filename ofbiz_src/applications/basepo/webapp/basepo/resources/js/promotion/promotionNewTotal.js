$(function() {
	OlbPromoNewTotal.init();
});
var OlbPromoNewTotal = (function() {
	var init = function() {
		initElement();
		initEvent();
	};
	var initElement = function() {
		jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
	};
	var initEvent = function() {
		$("#fuelux-wizard").ace_wizard().on("change", function(e, info) {
			if (info.step == 1 && (info.direction == "next")) {
				// check form valid
				if (!OlbPromoNewInfo.getValidator().validate())
					return false;

				var otherParam = "";
				var supplierIds = OlbPromoNewInfo.getSupplierIds();
				if (OlbCore.isNotEmpty(supplierIds)) {
					for (var i = 0; i < supplierIds.length; i++) {
						var item = supplierIds[i];
						if (item != null) {
							otherParam += "&supplierIds=" + item;
						}
					}
				}
				
				/*$('[id^="productIdListCond"]').each(function(i, obj) {
					OlbComboBoxUtil.updateSource($(obj), "jqxGeneralServicer?sname=JQGetListProductBuyAll&pagesize=0" + otherParam);
				});*/
				/*$('[id^="productIdListAction"]').each(function(i, obj) {
					OlbComboBoxUtil.updateSource($(obj), "jqxGeneralServicer?sname=JQGetListProductBuyAll&pagesize=0" + otherParam);
				});*/
				
				OlbGridUtil.updateSource($("#jqxgridProduct"), "jqxGeneralServicer?sname=JQGetListProductBuyAll" + otherParam);
			}
		}).on("finished", function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.BSAreYouSureYouWantToCreate, function() {
				finishCreatePromo();
			});
		}).on("stepclick", function(e) {
			// return false; //prevent clicking on steps
		});
	};
	var finishCreatePromo = function() {
		$("#btnPrevWizard").addClass("disabled");
		$("#btnNextWizard").addClass("disabled");

		var dataStr = prepareDataToProccess();

		// check has condition PPIP_ORDER_TOTAL and PPIP_PRODUCT_TOTAL
		var isHasCondSpecial = false;
		var isContinue = false;
		if (dataStr.indexOf("PPIP_ORDER_TOTAL") > 0
				|| dataStr.indexOf("PPIP_PRODUCT_TOTAL") > 0
				|| dataStr.indexOf("PPIP_PROD_QUANT_TOTA") > 0) {
			isHasCondSpecial = true;
		}

		if (isHasCondSpecial) {
			jOlbUtil.confirm.dialog(uiLabelMap.BSThisIsSpecialPromotionSetLimitEqualOne, function() {
				dataStr += "&useLimitPerOrder=1";
				isContinue = true;

				createPromo(dataStr);
			});
		} else {
			var infoData = OlbPromoNewInfo.getValue();
			if (OlbElementUtil.isNotEmpty(infoData.useLimitPerOrder)) {
				dataStr += "&useLimitPerOrder=";
				dataStr += "" + infoData.useLimitPerOrder;
			}
			isContinue = true;
		}
		if (isContinue) {
			createPromo(dataStr);
		} else {
			$("#btnPrevWizard").removeClass("disabled");
			$("#btnNextWizard").removeClass("disabled");
		}
	};
	var createPromo = function(dataStr) {
		$.ajax({
			type : "POST",
			url : urlCreateUpdatePromotion,
			data : dataStr,
			beforeSend : function() {
				$("#loader_page_common").show();
			},
			success : function(data) {
				jOlbUtil.processResultDataAjax(data, "default", function(data) {
					var productPromoId = data.productPromoId;
					if (OlbElementUtil.isNotEmpty(productPromoId)) {
						window.location.href = "viewPromotionPO?productPromoId=" + productPromoId;
					}
				});
			},
			error : function(data) {
				alert("Send request is error");
			},
			complete : function(data) {
				$("#loader_page_common").hide();
				$("#btnPrevWizard").removeClass("disabled");
				$("#btnNextWizard").removeClass("disabled");
			},
		});
	};
	var prepareDataToProccess = function() {
		var infoData = OlbPromoNewInfo.getValue();
		
		var dataStr = new StringBuilder();
		dataStr.append("productPromoId=");
		dataStr.append(infoData.productPromoId);
		dataStr.append("&promoName=");
		dataStr.append(encodeURIComponent(infoData.promoName));
		
		var fromDate = infoData.fromDate;
		var thruDate = infoData.thruDate;
		if (OlbElementUtil.isNotEmpty(fromDate)) {
			dataStr.append("&fromDate=");
			dataStr.append(fromDate);
		}
		if (OlbElementUtil.isNotEmpty(thruDate)) {
			dataStr.append("&thruDate=");
			dataStr.append(thruDate);
		}

		var supplierIds = OlbPromoNewInfo.getSupplierIds();
		if (OlbElementUtil.isNotEmpty(supplierIds)) {
			for (var i = 0; i < supplierIds.length; i++) {
				var item = supplierIds[i];
				if (item != null) {
					dataStr.append("&supplierIds=");
					dataStr.append(item);
				}
			}
		}

		dataStr.append("&promoText=");
		dataStr.append(encodeURIComponent(infoData.promoText));
		dataStr.append("&showToCustomer=Y");
		dataStr.append("&requireCode=N");
		
		var useLimitPerCustomer = infoData.useLimitPerCustomer;
		if (OlbElementUtil.isNotEmpty(useLimitPerCustomer)) {
			dataStr.append("&useLimitPerCustomer=");
			dataStr.append(useLimitPerCustomer);
		}
		var useLimitPerPromotion = infoData.useLimitPerPromotion;
		if (OlbElementUtil.isNotEmpty(useLimitPerPromotion)) {
			dataStr.append("&useLimitPerPromotion=");
			dataStr.append(useLimitPerPromotion);
		}
		dataStr.append("&");
		dataStr.append($("#editProductPromoRules").serialize());

		$('[id^="productIdListCond"]').each(function(i, obj) {
			var productIdListCond = $(obj).jqxComboBox("getSelectedItems");
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
			var productCatIdListCond = $(obj).jqxComboBox("getSelectedItems");
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
			var productIdListAction = $(obj).jqxComboBox("getSelectedItems");
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
			var productCatIdListAction = $(obj).jqxComboBox("getSelectedItems");
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
		init : init
	};
}());