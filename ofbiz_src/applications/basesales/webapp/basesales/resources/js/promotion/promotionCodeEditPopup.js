$(function(){
	OlbPromoCodeEdit.init();
});
var OlbPromoCodeEdit = (function(){
	var init = function(){
		initElement();
		initEvent();
	};
	var initElement = function(){
		jOlbUtil.windowPopup.create($("#alterpopupWindowEditPromoCode"), {maxWidth: 960, width: 960, height: 480, cancelButton: $("#we_alterCancel")});
	};
	var initComplexElement = function(){
		
	};
	var initEvent = function(){
		$("#alterpopupWindowEditPromoCode").on("close", function(){
			//window.location.reload();
			$("#jqxPromotionCode").jqxGrid("updatebounddata");
		});
	};
	var openWindowEdit = function() {
		$("#alterpopupWindowEditPromoCode").jqxWindow("open");
	};
	var loadPromoCodeDetail = function(productPromoCodeId){
		if (productPromoCodeId != null && productPromoCodeId != undefined) {
			openWindowEdit();
			$.ajax({
				type: 'POST',
				url: 'viewPromotionCodeAjax',
				data: {
					productPromoCodeId: productPromoCodeId
				},
				beforeSend: function(){
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
		} else {
			jOlbUtil.alert.info(uiLabelMap.BSVoucherCodeIsEmpty);
		}
	}
	return {
		init: init,
		openWindowEdit: openWindowEdit,
		loadPromoCodeDetail: loadPromoCodeDetail,
	};
}());