$(function(){
	OlbQuotationTotal.init();
});
var OlbQuotationTotal = (function(){
	var init = function(){
		initElement();
		initEvent();
	};
	var initElement = function(){
		jOlbUtil.notification.create($("#containerPage"), $("#jqxNotificationPage"));
	};
	var initEvent = function(){
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(info.step == 1 && (info.direction == "next")) {
				// check form valid
				if(!OlbQuotationInfo.getValidator().validate()) return false;
				
				var isCheckValid = checkValidManual(productPricesMap);
				if (!isCheckValid) return isCheckValid;
				
				transferDataToConfirm();
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.BSAreYouSureYouWantToCreate, function() {
            	finishCreateQuotation();
            });
		}).on('stepclick', function(e){
			//prevent clicking on steps
		});
	};
	var checkValidManual = function(productPricesMapParam){
		if (typeof(productPricesMapParam) == "undefined") var productPricesMapParam = {};
		var isNotChooseProductYet = false;
		dataSelected = [];
		var count = 0;
		
		var message = uiLabelMap.BSExistProductHaveNotPriceIs + ": ";
		var hasMessage = false;
		var isFirst = true;
		$.each(productPricesMapParam, function(key, value){
			var itemMap = value;
			if (itemMap.selected) {
				count++;
				if (itemMap.orderId == undefined) {
					hasMessage = true;
					if (!isFirst) message += ", ";
					message += itemMap.orderId;
					isFirst = false;
				} else {
					dataSelected.push(itemMap);
				}
			}
		});
		message += "</span>";
		
		if (count <= 0) {
			jOlbUtil.alert.error(uiLabelMap.BSYouNotYetChooseRecord);
			return false;
		}
		
		if (hasMessage) {
			jOlbUtil.alert.error(message);
			return false;
		}
		return true;
	};
	var transferDataToConfirm = function(){
		$("#strDescription").text($("#description").val());
		$("#strRequiredByDate").text($("#requiredByDate").val());
		$("#strRequirementStartDate").text($("#requirementStartDate").val());
		
        var tmpSource = $("#jqxgridOrderConfirm").jqxGrid('source');
		if(typeof(tmpSource) != 'undefined'){
			tmpSource._source.localdata = dataSelected;
			$("#jqxgridOrderConfirm").jqxGrid('source', tmpSource);
		}
	};
	var finishCreateQuotation = function(){
		//var requiredByDate = $("#requiredByDate").jqxDateTimeInput('getDate') != null ? $("#requiredByDate").jqxDateTimeInput('getDate').getTime() : "";
		var requirementStartDate = $("#requirementStartDate").jqxDateTimeInput('getDate') != null ? $("#requirementStartDate").jqxDateTimeInput('getDate').getTime() : "";
		var description = $("#description").val();
		
		var dataSelectedFinal = [];
		if (dataSelected) {
			$.each(dataSelected, function(key, item){
				if (OlbCore.isNotEmpty(item.orderId)) dataSelectedFinal.push(item.orderId);
			});
		}
		
		var dataMap = {
			//requiredByDate: requiredByDate,
			requirementStartDate: requirementStartDate,
			description: description,
		};
		dataMap.orderList = JSON.stringify(dataSelectedFinal);
		
		$.ajax({
			type: 'POST',
			url: urlCreateUpdateQuotation,
			data: dataMap,
			beforeSend: function(){
				$("#btnPrevWizard").addClass("disabled");
				$("#btnNextWizard").addClass("disabled");
				$("#loader_page_common").show();
			},
			success: function(data){
				jOlbUtil.processResultDataAjax(data, "default", function(data){
		        	if (OlbCore.isNotEmpty(data.requirementId)) {
		        		window.location.href = "viewReqDeliveryOrder?requirementId=" + data.requirementId;
		        	}
				});
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
	return {
		init : init,
	}
}());